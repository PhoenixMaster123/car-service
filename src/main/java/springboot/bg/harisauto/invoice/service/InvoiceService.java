package springboot.bg.harisauto.invoice.service;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.bg.harisauto.invoice.model.Invoice;
import springboot.bg.harisauto.invoice.model.InvoiceLineItem;
import springboot.bg.harisauto.invoice.model.InvoiceStatus;
import springboot.bg.harisauto.invoice.repository.InvoiceRepository;
import springboot.bg.harisauto.service.model.CarService;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.vehicle.model.Vehicle;

/**
 * InvoiceService.java - Generation and retrieval of invoices.
 *
 * @author Kristian Popov
 */
@Slf4j
@Service
public class InvoiceService {

  private static final BigDecimal VAT_RATE = new BigDecimal("0.20");
  private static final int DUE_DAYS = 14;

  private final InvoiceRepository repository;
  private final InvoiceNumberGenerator numberGenerator;

  @Autowired
  public InvoiceService(InvoiceRepository repository,
                        InvoiceNumberGenerator numberGenerator) {
    this.repository = repository;
    this.numberGenerator = numberGenerator;
  }

  /**
   * Generate and persist an invoice for a completed booking.
   */
  @Transactional
  public Invoice generate(User user,
                          Vehicle vehicle,
                          List<CarService> services,
                          LocalDateTime serviceDate,
                          String paymentMethod,
                          InvoiceStatus status,
                          UUID bookingId) {

    BigDecimal subtotal = services.stream()
        .map(CarService::getBasePrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);

    BigDecimal taxAmount = subtotal.multiply(VAT_RATE).setScale(2, RoundingMode.HALF_UP);
    BigDecimal total = subtotal.add(taxAmount);

    List<InvoiceLineItem> lineItems = services.stream()
        .map(s -> InvoiceLineItem.builder()
            .name(s.getName())
            .description(s.getDescription())
            .price(s.getBasePrice().setScale(2, RoundingMode.HALF_UP))
            .build())
        .toList();

    Invoice invoice = Invoice.builder()
        .invoiceNumber(numberGenerator.next())
        .userId(user.getId())
        .bookingId(bookingId)
        .customerName(user.getFirstName() + " " + user.getLastName())
        .customerEmail(user.getEmail())
        .customerPhone(user.getPhoneNumber())
        .vehicleDescription(describeVehicle(vehicle))
        .serviceDate(serviceDate)
        .paymentMethod(paymentMethod)
        .subtotal(subtotal)
        .taxRate(VAT_RATE)
        .taxAmount(taxAmount)
        .total(total)
        .status(status)
        .dueDate(serviceDate.plusDays(DUE_DAYS))
        .lineItems(lineItems)
        .build();

    Invoice saved = repository.save(invoice);
    log.info("Generated invoice {} for user {} ({} services, total {})",
        saved.getInvoiceNumber(), user.getId(), services.size(), total);
    return saved;
  }

  /** Returns all invoices for the given user, newest first. */
  public List<Invoice> listForUser(UUID userId) {
    return repository.findAllByUserIdOrderByIssuedAtDesc(userId);
  }

  /**
   * Look up an invoice by id and verify the requester owns it.
   * Throws AccessDeniedException if the invoice belongs to a different user.
   */
  public Invoice getForUser(UUID invoiceId, UUID userId) {
    Invoice invoice = repository.findById(invoiceId)
        .orElseThrow(() -> new EntityNotFoundException("Invoice not found: " + invoiceId));
    if (!invoice.getUserId().equals(userId)) {
      throw new AccessDeniedException("Invoice does not belong to the current user");
    }
    return invoice;
  }

  private static String describeVehicle(Vehicle vehicle) {
    if (vehicle == null) {
      return "—";
    }
    return vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getLicensePlate() + ")";
  }
}
