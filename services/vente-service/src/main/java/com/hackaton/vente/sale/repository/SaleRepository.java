package com.hackaton.vente.sale.repository;

import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.entity.Sale;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    boolean existsByCustomerId(Long customerId);

    List<Sale> findAllByOrderByCreatedAtDesc();

    List<Sale> findByStatus(DocumentStatus status);
}
