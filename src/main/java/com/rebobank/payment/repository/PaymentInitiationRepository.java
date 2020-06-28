package com.rebobank.payment.repository;

import java.util.UUID;

import com.rebobank.payment.model.PaymentInitiationRequest;

public interface PaymentInitiationRepository
{

    /**
     * Create payment
     * 
     * @param payment
     *            the Payment Initiation Request
     * @return String the paymentId
     */
    default String createPayment(final PaymentInitiationRequest payment)
    {
        return UUID.randomUUID().toString();
    }
}
