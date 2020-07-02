package com.rabobank.payment.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.rabobank.payment.exception.LimitExceededException;
import com.rabobank.payment.model.PaymentAcceptedResponse;
import com.rabobank.payment.model.PaymentInitiationRequest;
import com.rabobank.payment.repository.PaymentInitiationRepository;
import com.rabobank.payment.util.TransactionStatus;

import lombok.AllArgsConstructor;

/**
 * Payment initiation service
 */
@Component
@AllArgsConstructor
public class PaymentInitiationService
{

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentInitiationService.class);

    private PaymentInitiationRepository paymentInitiationRepository;

    /**
     * Initiate third party payment
     * 
     * @param request
     *            the Payment Initiation Request
     * @return PaymentAcceptedResponse
     */
    public PaymentAcceptedResponse initiatePayment(final PaymentInitiationRequest request)
    {
        //Check the transaction amount limit
        checkAmountLimit(request);

        // Accept payment request
        String paymentId = paymentInitiationRepository.createPayment(request);
        LOGGER.debug("Payment Id {}", paymentId);

        // Return payment accepted response
        return new PaymentAcceptedResponse(paymentId, TransactionStatus.Accepted);
    }

    private void checkAmountLimit(final PaymentInitiationRequest request)
    {
        final BigDecimal amount = new BigDecimal(request.getAmount());

        // Extract the numbers from the DebtorIBAN
        Long digits = Long.parseLong(request.getDebtorIBAN().replaceAll("\\D+", ""));
        Long sumOfDigits = 0L;

        // Sum of the digits
        while (digits != 0) {
            sumOfDigits = sumOfDigits + digits % 10;
            digits = digits / 10;
        }
        
        if (BigDecimal.ZERO.compareTo(amount) < 0
                && (sumOfDigits % request.getDebtorIBAN().length()) == 0) {
            LOGGER.error("Limit exceeded error");
            throw new LimitExceededException("Amount limit exceeded");
        }
    }
}
