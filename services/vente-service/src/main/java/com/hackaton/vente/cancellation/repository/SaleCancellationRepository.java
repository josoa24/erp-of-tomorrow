package com.hackaton.vente.cancellation.repository;

import com.hackaton.vente.cancellation.entity.SaleCancellation;
import java.util.Collection;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SaleCancellationRepository extends JpaRepository<SaleCancellation, Long> {

    boolean existsBySale_Id(Long saleId);

    @Query("select c.sale.id from SaleCancellation c where c.sale.id in :saleIds")
    Set<Long> findCancelledSaleIds(@Param("saleIds") Collection<Long> saleIds);
}
