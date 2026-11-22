package com.hackaton.vente.payment.service.recorder;

import com.hackaton.vente.cancellation.repository.SaleCancellationRepository;
import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.payment.dto.PaymentRequest;
import com.hackaton.vente.payment.dto.PaymentResponse;
import com.hackaton.vente.payment.entity.SalePayment;
import com.hackaton.vente.payment.mapper.PaymentMapper;
import com.hackaton.vente.payment.repository.SalePaymentRepository;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.repository.SaleRepository;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PaymentRecorderService implements PaymentRecorder {

    private final SaleRepository saleRepository;
    private final SalePaymentRepository paymentRepository;
    private final SaleCancellationRepository cancellationRepository;
    private final PaymentRules rules;
    private final PaymentMapper paymentMapper;
    private final Clock clock;

    @Override
    @Transactional
    public PaymentResponse record(Long saleId, PaymentRequest request, Long recordedBy) {
        Sale sale = saleRepository.findWithLockById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vente", saleId));
        rules.check(sale, request.amount(), request.method(),
                paymentRepository.sumAmountBySaleId(saleId), cancellationRepository.existsBySale_Id(saleId));
        String reference = StringUtils.hasText(request.reference()) ? request.reference().trim() : null;
        SalePayment payment = new SalePayment(
                sale, request.amount(), request.method(), Instant.now(clock), reference, recordedBy);
        return paymentMapper.toResponse(paymentRepository.save(payment));
    }
}
