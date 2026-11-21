package com.hackaton.stock.product.repository;

import com.hackaton.stock.product.entity.Product;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySku(String sku);

    @Query("select p from Product p "
            + "where lower(p.name) like lower(:pattern) or lower(p.sku) like lower(:pattern) "
            + "order by p.name")
    List<Product> search(@Param("pattern") String pattern);

    @Query("select p from Product p where p.quantity <= p.minThreshold order by p.quantity")
    List<Product> findLowStock();

    @Query("select count(p) from Product p where p.quantity <= p.minThreshold")
    long countLowStock();

    @Query("select coalesce(sum(p.quantity * p.unitPrice), 0) from Product p")
    BigDecimal totalStockValue();
}
