package springboot.bg.harisauto.invoice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Invoice.java - Entity class for Invoice mapping bookings to an invoice number.
 *
 * @author AI Engine
 */
@Entity
@Table(name = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String invoiceNumber;

  @Column(nullable = false)
  private UUID bookingId;

  @Column(nullable = false)
  private UUID userId;

  @Column(nullable = false)
  private LocalDateTime issueDate;

  @Column(nullable = false)
  private BigDecimal totalAmount;

  @Column(nullable = false)
  private String status;
}
