package com.rebobank.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rebobank.payment.exception.LimitExceededException;
import com.rebobank.payment.model.PaymentAcceptedResponse;
import com.rebobank.payment.model.PaymentInitiationRequest;
import com.rebobank.payment.repository.PaymentInitiationRepository;
import com.rebobank.payment.util.TransactionStatus;

/**
 * Test for PaymentInitiationService
 */
@ExtendWith(MockitoExtension.class)
public class PaymentInitiationServiceTest
{

    @Mock
    private PaymentInitiationRepository mockPaymentInitiationRepository;

    private PaymentInitiationService subject;

    /**
     * Test iniitial set up
     */
    @BeforeEach
    public void setUp()
    {
        subject = new PaymentInitiationService(mockPaymentInitiationRepository);
    }

    /**
     * Test initiate payment with valid request
     */
    @Test
    public void testInitiatePayment_With_Valid_Request() // NOSONAR
    {
        final String paymentId = UUID.randomUUID().toString();

        final PaymentInitiationRequest request = new PaymentInitiationRequest("NL91ABNA0417164301",
                "NL91ABNA0417164302", "500.00", "EUR", "U1000");

        when(mockPaymentInitiationRepository.createPayment(any(PaymentInitiationRequest.class)))
                .thenReturn(paymentId);

        PaymentAcceptedResponse actual = subject.initiatePayment(request);

        assertEquals(TransactionStatus.Accepted, actual.getStatus());
        assertEquals(paymentId, actual.getPaymentId());

    }

    /**
     * Test initiate payment with expected LimitExceededException
     */
    @Test
    public void testInitiatePayment_Expected_Reject_Payment_With_Limit_Exceeded_Exception() // NOSONAR
    {
        final PaymentInitiationRequest request = new PaymentInitiationRequest("NL91ABNA0417164300",
                "NL91ABNA0417164301", "500.00", "EUR", "U1000");

        Exception exception = Assertions.assertThrows(LimitExceededException.class,
                () -> subject.initiatePayment(request));

        assertTrue(exception.getMessage().contains("Amount limit exceeded"));
    }

    /**
     * Test initiate payment with amount zero and expected LimitExceededException
     */
    @Test
    public void testInitiatePayment_With_Amount_Less_Than_Zero_Expected_Accept_The_Payment() // NOSONAR
    {
        final String paymentId = UUID.randomUUID().toString();

        final PaymentInitiationRequest request = new PaymentInitiationRequest("NL91ABNA0417164304",
                "NL91ABNA0417164308", "-100.00", "EUR", "U1005");

        when(mockPaymentInitiationRepository.createPayment(any(PaymentInitiationRequest.class)))
                .thenReturn(paymentId);

        PaymentAcceptedResponse actual = subject.initiatePayment(request);

        assertEquals(TransactionStatus.Accepted, actual.getStatus());
        assertEquals(paymentId, actual.getPaymentId());
    }
}
