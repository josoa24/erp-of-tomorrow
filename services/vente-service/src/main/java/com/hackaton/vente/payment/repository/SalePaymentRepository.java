package com.hackaton.vente.payment.repository;

import com.hackaton.vente.payment.entity.SalePayment;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalePaymentRepository extends JpaRepository<SalePayment, Long> {

    List<SalePayment> findBySale_IdOrderByPaidAtAscIdAsc(Long saleId);

    boolean existsBySale_Id(Long saleId);

    @Query("select coalesce(sum(p.amount), 0) from SalePayment p where p.sale.id = :saleId")
    BigDecimal sumAmountBySaleId(@Param("saleId") Long saleId);

    @Query("select p.sale.id as saleId, sum(p.amount) as total from SalePayment p "
            + "where p.sale.id in :saleIds group by p.sale.id")
    List<SalePaidTotal> sumAmountBySaleIds(@Param("saleIds") Collection<Long> saleIds);

    @Query("select coalesce(sum(p.amount), 0) from SalePayment p where p.paidAt >= :from and p.paidAt < :to")
    BigDecimal sumAmountPaidBetween(@Param("from") Instant from, @Param("to") Instant to);
}
