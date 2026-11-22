package com.hackaton.stock.invoice.repository;

import com.hackaton.stock.invoice.entity.InvoiceEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceEventRepository extends JpaRepository<InvoiceEvent, Long> {

    List<InvoiceEvent> findByInvoiceIdOrderByIdAsc(Long invoiceId);
}
