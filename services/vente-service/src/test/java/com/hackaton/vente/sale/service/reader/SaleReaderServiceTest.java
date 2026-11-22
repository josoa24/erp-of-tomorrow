package com.hackaton.vente.sale.service.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.common.page.PageResult;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.SaleTestAssembler;
import com.hackaton.vente.sale.dto.PaymentStatus;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.dto.SaleSearchCriteria;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.repository.SaleRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleReaderServiceTest {

    @Mock
    private SaleRepository saleRepository;

    private SaleReaderService service;

    @BeforeEach
    void setUp() {
        Clock clock = SaleTestAssembler.clockAt(SaleTestAssembler.NOW);
        service = new SaleReaderService(
                saleRepository, SaleTestAssembler.withoutFacts(clock), new SaleSearchFilter(clock));
    }

    private Sale draft(Long id) {
        Sale sale = SaleFixtures.draftSale(id, SaleFixtures.line(10L, "Riz", 1, "1000"));
        sale.setCreatedAt(Instant.parse("2026-11-24T06:00:00Z"));
        return sale;
    }

    @Test
    void shouldPageMatchingSalesAndKeepTotal_whenSizeIsGiven() {
        Sale validated = SaleFixtures.validatedSale(3L, "2026-11-24T05:00:00Z", PaymentMethod.CASH,
                SaleFixtures.line(10L, "Riz", 1, "1000"));
        when(saleRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(draft(5L), draft(4L), validated));
        SaleSearchCriteria drafts = new SaleSearchCriteria(
                null, null, null, null, null, List.of(PaymentStatus.DRAFT), null, null);

        PageResult<SaleResponse> page = service.search(drafts, 1, 1);

        assertEquals(2, page.total());
        assertEquals(1, page.items().size());
        assertEquals("V-0004", page.items().get(0).number());
    }

    @Test
    void shouldReturnEverySale_whenNoCriteriaAndNoPaging() {
        when(saleRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(draft(5L), draft(4L)));

        PageResult<SaleResponse> page = service.search(SaleSearchCriteria.between(null, null), null, null);

        assertEquals(2, page.items().size());
    }

    @Test
    void shouldReturnSale_whenItExists() {
        when(saleRepository.findById(5L)).thenReturn(Optional.of(draft(5L)));

        assertEquals("V-0005", service.findById(5L).number());
    }

    @Test
    void shouldThrowNotFound_whenSaleDoesNotExist() {
        when(saleRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(9L));
    }
}
