package springboot.bg.harisauto.invoice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot.bg.harisauto.invoice.model.Invoice;

/**
 * InvoiceRepository.java - Repository for Invoice model.
 *
 * @author AI Engine
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

  /** Finds an invoice by its booking ID. */
  Optional<Invoice> findByBookingId(UUID bookingId);

  /** Finds all invoices for a given user. */
  List<Invoice> findByUserId(UUID userId);
}
