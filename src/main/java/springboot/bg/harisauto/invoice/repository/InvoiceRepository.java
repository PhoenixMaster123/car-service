package springboot.bg.harisauto.invoice.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot.bg.harisauto.invoice.model.Invoice;

/**
 * InvoiceRepository.java - JPA repository for invoices.
 *
 * @author Kristian Popov
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

  List<Invoice> findAllByUserIdOrderByIssuedAtDesc(UUID userId);

  long countByUserId(UUID userId);
}
