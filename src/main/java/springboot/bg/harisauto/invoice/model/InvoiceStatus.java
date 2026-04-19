package springboot.bg.harisauto.invoice.model;

/**
 * InvoiceStatus.java - Lifecycle states of an invoice.
 *
 * @author Kristian Popov
 */
public enum InvoiceStatus {
  PAID,
  PENDING,
  OVERDUE,
  CANCELLED
}
