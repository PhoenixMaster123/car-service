package springboot.bg.harisauto.invoice.service;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;
import springboot.bg.harisauto.invoice.repository.InvoiceRepository;

/**
 * InvoiceNumberGenerator.java - Produces human-readable invoice numbers in the form INV-YYYY-NNNNNN.
 * Seeded from the persisted invoice count so numbers stay monotonic across restarts (single-node).
 *
 * @author Kristian Popov
 */
@Component
public class InvoiceNumberGenerator {

  private final AtomicLong counter;

  public InvoiceNumberGenerator(InvoiceRepository repository) {
    this.counter = new AtomicLong(repository.count());
  }

  /** Returns the next invoice number, e.g. INV-2026-000042. */
  public String next() {
    long n = counter.incrementAndGet();
    return String.format("INV-%d-%06d", LocalDate.now().getYear(), n);
  }
}
