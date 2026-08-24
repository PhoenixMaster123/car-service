package springboot.bg.harisauto.invoice.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import springboot.bg.harisauto.invoice.model.Invoice;
import springboot.bg.harisauto.invoice.repository.InvoiceRepository;

/**
 * InvoiceService.java - Handles PDF generation and Invoice persistence.
 *
 * @author AI Engine
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

  private final InvoiceRepository invoiceRepository;
  private final TemplateEngine templateEngine;
  private final springboot.bg.harisauto.user.service.UserService userService;
  private final springboot.bg.harisauto.vehicle.service.VehicleService vehicleService;
  private final springboot.bg.harisauto.service.service.CatalogService catalogService;
  private final springboot.bg.harisauto.email.EmailService emailService;

  /**
   * Orchestrates the creation, PDF generation, and emailing of an invoice.
   */
  public void processAndSendInvoice(UUID userId, UUID bookingId, List<UUID> serviceIds, UUID vehicleId, BigDecimal totalAmount, String status) {
    // 1. Create Invoice Record
    Invoice invoice = createInvoiceRecord(userId, bookingId, totalAmount, status);

    // 2. Gather Data for PDF
    springboot.bg.harisauto.user.model.User user = userService.getById(userId);
    springboot.bg.harisauto.vehicle.model.Vehicle vehicle = vehicleService.getById(vehicleId);
    List<springboot.bg.harisauto.service.model.CarService> services = serviceIds.stream()
        .map(catalogService::getById)
        .toList();

    Context context = new Context();
    context.setVariable("customerName", user.getFirstName() + " " + user.getLastName());
    context.setVariable("customerEmail", user.getEmail());
    context.setVariable("customerPhone", "N/A"); // Adjust if User has phone
    context.setVariable("invoiceNumber", invoice.getInvoiceNumber());
    context.setVariable("issueDate", invoice.getIssueDate().toLocalDate().toString());
    context.setVariable("status", invoice.getStatus());
    context.setVariable("vehicleDescription", vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getLicensePlate() + ")");
    context.setVariable("bookingDate", LocalDateTime.now().toLocalDate().toString()); // Approximation
    context.setVariable("services", services);
    context.setVariable("totalAmount", invoice.getTotalAmount());

    // 3. Generate PDF
    byte[] pdfBytes = generatePdf(context);

    // 4. Send Email
    emailService.sendInvoiceEmail(user.getEmail(), pdfBytes, invoice.getInvoiceNumber());
  }

  /**
   * Generates a new invoice number based on sequence.
   */
  public Invoice createInvoiceRecord(UUID userId, UUID bookingId, BigDecimal totalAmount, String status) {
    long count = invoiceRepository.count() + 1;
    String invoiceNumber = String.format("INV-%s-%04d", LocalDateTime.now().getYear(), count);

    Invoice invoice = Invoice.builder()
        .invoiceNumber(invoiceNumber)
        .bookingId(bookingId)
        .userId(userId)
        .issueDate(LocalDateTime.now())
        .totalAmount(totalAmount)
        .status(status)
        .build();

    return invoiceRepository.save(invoice);
  }

  /**
   * Generates the PDF document payload.
   */
  public byte[] generatePdf(Context context) {
    try {
      String html = templateEngine.process("email/invoice-template", context);
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PdfRendererBuilder builder = new PdfRendererBuilder();
      builder.useFastMode();
      builder.withHtmlContent(html, "http://localhost:8080/");
      builder.toStream(os);
      builder.run();
      return os.toByteArray();
    } catch (Exception e) {
      log.error("Error generating Invoice PDF: {}", e.getMessage());
      return new byte[0];
    }
  }

  /**
   * Get invoice by booking id.
   */
  public Optional<Invoice> getInvoiceByBookingId(UUID bookingId) {
    return invoiceRepository.findByBookingId(bookingId);
  }

  /**
   * Get invoices by user id.
   */
  public List<Invoice> getInvoicesByUser(UUID userId) {
    return invoiceRepository.findByUserId(userId);
  }
}
