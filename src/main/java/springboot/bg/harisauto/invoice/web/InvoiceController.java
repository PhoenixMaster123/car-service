package springboot.bg.harisauto.invoice.web;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.invoice.model.Invoice;
import springboot.bg.harisauto.invoice.service.InvoiceService;

/**
 * InvoiceController.java - Web endpoints for listing, viewing, and downloading invoices.
 *
 * @author Kristian Popov
 */
@Controller
@RequestMapping("/users/invoices")
public class InvoiceController {

  private final InvoiceService invoiceService;

  public InvoiceController(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  /** List all invoices for the current user. */
  @GetMapping
  public ModelAndView list(@AuthenticationPrincipal AuthenticationMetaData metaData) {

    List<Invoice> invoices = invoiceService.listForUser(metaData.getUserId());

    long paid = invoices.stream().filter(i -> i.getStatus().name().equals("PAID")).count();
    long pending = invoices.stream().filter(i -> i.getStatus().name().equals("PENDING")).count();
    long overdue = invoices.stream().filter(i -> i.getStatus().name().equals("OVERDUE")).count();

    ModelAndView mv = new ModelAndView("account/invoices");
    mv.addObject("invoices", invoices);
    mv.addObject("totalCount", invoices.size());
    mv.addObject("paidCount", paid);
    mv.addObject("pendingCount", pending);
    mv.addObject("overdueCount", overdue);
    return mv;
  }

  /** Show invoice detail page. */
  @GetMapping("/{id}")
  public ModelAndView detail(@PathVariable("id") UUID id,
                             @AuthenticationPrincipal AuthenticationMetaData metaData) {

    Invoice invoice = invoiceService.getForUser(id, metaData.getUserId());

    ModelAndView mv = new ModelAndView("account/invoice-detail");
    mv.addObject("invoice", invoice);
    return mv;
  }

  /** Download invoice as PDF. */
  @GetMapping("/{id}/pdf")
  public ResponseEntity<byte[]> downloadPdf(@PathVariable("id") UUID id,
                                            @AuthenticationPrincipal AuthenticationMetaData metaData) {

    Invoice invoice = invoiceService.getForUser(id, metaData.getUserId());
    byte[] pdf = invoiceService.renderPdf(invoice);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + invoice.getInvoiceNumber() + ".pdf\"")
        .body(pdf);
  }
}
