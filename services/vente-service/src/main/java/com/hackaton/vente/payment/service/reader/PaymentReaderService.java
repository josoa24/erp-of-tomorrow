package com.hackaton.vente.payment.service.reader;

import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.payment.dto.PaymentResponse;
import com.hackaton.vente.payment.mapper.PaymentMapper;
import com.hackaton.vente.payment.repository.SalePaymentRepository;
import com.hackaton.vente.sale.repository.SaleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentReaderService implements PaymentReader {

    private final SaleRepository saleRepository;
    private final SalePaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public List<PaymentResponse> findBySale(Long saleId) {
        if (!saleRepository.existsById(saleId)) {
            throw new ResourceNotFoundException("Vente", saleId);
        }
        return paymentRepository.findBySale_IdOrderByPaidAtAscIdAsc(saleId).stream()
                .map(paymentMapper::toResponse)
                .toList();
    }
}
