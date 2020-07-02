package com.rabobank.payment.repository;

import java.util.UUID;

import com.rabobank.payment.model.PaymentInitiationRequest;

/**
 * Payment initiation repository
 */
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
