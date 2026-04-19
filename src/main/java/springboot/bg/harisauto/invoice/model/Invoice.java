package springboot.bg.harisauto.invoice.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Invoice.java - Locally-stored invoice generated when a booking is paid (or scheduled to be paid).
 * All fields are denormalized at creation time so the document survives upstream changes.
 *
 * @author Kristian Popov
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoices")
public class Invoice {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String invoiceNumber;

  @Column(nullable = false)
  private UUID userId;

  private UUID bookingId;

  @Column(nullable = false)
  private String customerName;

  @Column(nullable = false)
  private String customerEmail;

  private String customerPhone;

  private String vehicleDescription;

  @Column(nullable = false)
  private LocalDateTime serviceDate;

  @Column(nullable = false)
  private String paymentMethod;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal subtotal;

  @Column(nullable = false, precision = 5, scale = 4)
  private BigDecimal taxRate;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal taxAmount;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal total;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private InvoiceStatus status;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime issuedAt;

  private LocalDateTime dueDate;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "invoice_line_items", joinColumns = @JoinColumn(name = "invoice_id"))
  @Builder.Default
  private List<InvoiceLineItem> lineItems = new ArrayList<>();
}
