package com.hackaton.vente.sale.repository;

import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    String EFFECTIVE = "select s from Sale s "
            + "where s.status = com.hackaton.vente.common.status.DocumentStatus.VALIDATED "
            + "and not exists (select c.id from SaleCancellation c where c.sale = s)";

    boolean existsByCustomerId(Long customerId);

    List<Sale> findAllByOrderByCreatedAtDesc();

    List<Sale> findByCustomerIdAndStatusOrderByValidatedAtDesc(Long customerId, DocumentStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Sale> findWithLockById(Long id);

    @Query(EFFECTIVE)
    List<Sale> findEffective();

    @Query(EFFECTIVE + " and s.validatedAt >= :since")
    List<Sale> findEffectiveValidatedSince(@Param("since") Instant since);

    @Query(EFFECTIVE + " and s.paymentMethod = :method")
    List<Sale> findEffectiveByPaymentMethod(@Param("method") PaymentMethod method);
}
