package com.hackaton.vente.payment.service.loader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hackaton.vente.payment.repository.SalePaidTotal;
import com.hackaton.vente.payment.repository.SalePaymentRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaidAmountLoaderTest {

    @Mock
    private SalePaymentRepository paymentRepository;
    @InjectMocks
    private PaidAmountLoader loader;

    private SalePaidTotal total(Long saleId, String amount) {
        return new SalePaidTotal() {
            @Override
            public Long getSaleId() {
                return saleId;
            }

            @Override
            public BigDecimal getTotal() {
                return new BigDecimal(amount);
            }
        };
    }

    @Test
    void shouldMapTotalsBySale_whenPaymentsExist() {
        when(paymentRepository.sumAmountBySaleIds(List.of(1L, 2L))).thenReturn(List.of(total(1L, "186000")));

        Map<Long, BigDecimal> paid = loader.bySale(List.of(1L, 2L));

        assertEquals(Map.of(1L, new BigDecimal("186000")), paid);
    }

    @Test
    void shouldNotQuery_whenNoSaleIsGiven() {
        assertTrue(loader.bySale(List.of()).isEmpty());
        verifyNoInteractions(paymentRepository);
    }
}
