package springboot.bg.harisauto.web.controller;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import springboot.bg.harisauto.booking.dto.response.BookingResponse;
import springboot.bg.harisauto.booking.service.BookingService;
import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.invoice.model.Invoice;
import springboot.bg.harisauto.invoice.service.InvoiceService;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.service.UserService;

/**
 * InvoiceController.java - Rest Controller for downloading invoices.
 *
 * @author Kristian Popov
 */
@Slf4j
@RestController
@RequestMapping("/users/invoices")
@RequiredArgsConstructor
public class InvoiceController {

  private final InvoiceService invoiceService;
  private final BookingService bookingService;
  private final UserService userService;

  /**
   * Generates and downloads the PDF invoice for a given booking ID.
   * Only works for bookings that have a PAID invoice record in the database.
   */
  @GetMapping("/{bookingId}/download")
  public ResponseEntity<byte[]> downloadInvoice(
      @PathVariable UUID bookingId,
      @AuthenticationPrincipal AuthenticationMetaData metaData) {

    UUID userId = metaData.getUserId();

    // Verify booking belongs to user
    Optional<BookingResponse> bookingOpt = bookingService.getBookingsByUser(userId).stream()
        .filter(b -> b.getId().equals(bookingId))
        .findFirst();

    if (bookingOpt.isEmpty()) {
      log.warn("Booking {} not found for user {}", bookingId, userId);
      return ResponseEntity.notFound().build();
    }

    // Only allow download if an invoice record exists (created at payment time)
    Optional<Invoice> invoiceOpt = invoiceService.getInvoiceByBookingId(bookingId);
    if (invoiceOpt.isEmpty()) {
      log.warn("No invoice found for booking {}", bookingId);
      return ResponseEntity.notFound().build();
    }

    BookingResponse booking = bookingOpt.get();
    Invoice invoice = invoiceOpt.get();
    User user = userService.getById(userId);

    // Build PDF context
    Context context = new Context();
    context.setVariable("customerName", user.getFirstName() + " " + user.getLastName());
    context.setVariable("customerEmail", user.getEmail());
    context.setVariable("customerPhone", booking.getPhoneNumber() != null ? booking.getPhoneNumber() : "N/A");
    context.setVariable("invoiceNumber", invoice.getInvoiceNumber());
    context.setVariable("issueDate", invoice.getIssueDate().toLocalDate().toString());
    context.setVariable("status", invoice.getStatus());
    context.setVariable("vehicleDescription", booking.getVehicleDescription());
    context.setVariable("bookingDate", booking.getBookingDate() != null ? booking.getBookingDate().toLocalDate().toString() : "N/A");
    context.setVariable("totalAmount", invoice.getTotalAmount());
    context.setVariable("services", java.util.List.of(
        springboot.bg.harisauto.service.model.CarService.builder()
            .name(booking.getServiceNames())
            .basePrice(invoice.getTotalAmount())
            .build()
    ));

    byte[] pdfBytes = invoiceService.generatePdf(context);

    if (pdfBytes == null || pdfBytes.length == 0) {
      log.error("Failed to generate PDF for invoice {}", invoice.getInvoiceNumber());
      return ResponseEntity.internalServerError().build();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("attachment", "Invoice-" + invoice.getInvoiceNumber() + ".pdf");
    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

    return ResponseEntity.ok()
        .headers(headers)
        .body(pdfBytes);
  }
}
