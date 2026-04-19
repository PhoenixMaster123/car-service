package springboot.bg.harisauto.invoice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * InvoiceLineItem.java - Embedded line item snapshot stored on an invoice.
 * Denormalized so the invoice stays valid even if the underlying service is renamed or deleted.
 *
 * @author Kristian Popov
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class InvoiceLineItem {

  @Column(nullable = false)
  private String name;

  @Column(length = 500)
  private String description;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal price;
}
