package com.rebobank.payment.controller;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebobank.payment.exception.LimitExceededException;
import com.rebobank.payment.model.PaymentAcceptedResponse;
import com.rebobank.payment.model.PaymentInitiationRequest;
import com.rebobank.payment.service.PaymentInitiationService;
import com.rebobank.payment.util.TransactionStatus;

/**
 * Test for PaymentInitiationController
 */
@SpringBootTest
@AutoConfigureMockMvc
// @WithMockUser
public class PaymentInitiationControllerTest
{

    @MockBean
    private PaymentInitiationService mockPaymentInitiationService;

    @Autowired(required = true)
    private MockMvc mockMvc;

    @Autowired(required = true)
    private ObjectMapper objectMapper;

    private PaymentInitiationController subject;

    @BeforeEach
    public void setUp()
    {
        subject = new PaymentInitiationController(mockPaymentInitiationService);
    }

    /**
     * Test initiate payment with valid input and expected payment accepted status
     * 
     * @throws Exception
     *             not expected exception
     */
    @Test
    public void testInitiatePayment_Expect_Payment_Accepted() throws Exception // NOSONAR
    {
        final PaymentInitiationRequest request = new PaymentInitiationRequest("NL91ABNA0417164301",
                "NL91ABNA0417164302", "100.00", "EUR", "U1000");

        final String paymentId = UUID.randomUUID().toString();

        final PaymentAcceptedResponse expected = new PaymentAcceptedResponse(paymentId,
                TransactionStatus.Accepted);
        when(mockPaymentInitiationService.initiatePayment(any())).thenReturn(expected);

        /*
         * mockMvc.perform(MockMvcRequestBuilders.post("/initiate-payment")
         * .content(objectMapper.writeValueAsString(request))
         * .contentType(MediaType.APPLICATION_JSON_VALUE)
         * ).andExpect(MockMvcResultMatchers.status().isCreated())
         * .andExpect(jsonPath("$.paymentId", is(paymentId)))
         * .andExpect(jsonPath("$.status", is("Accepted")));
         */

        ResponseEntity<PaymentAcceptedResponse> response = subject.initiatePayment(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    /**
     * Test initiate payment, expected payment rejected response due to limit
     * exceeded
     * 
     * @throws Exception
     *             not expected exception
     */
    // @Test
    public void testInitiatePayment_Expect_Payment_Rejected_With_Reason_Limit_Exceeded() // NOSONAR
            throws Exception // NOSONAR
    {
        final PaymentInitiationRequest request = new PaymentInitiationRequest("NL91ABNA0417164300",
                "NL91ABNA0417164301", "50.00", "INR", "U1001");

        doThrow(LimitExceededException.class).when(mockPaymentInitiationService)
                .initiatePayment(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/initiate-payment")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status", is("Rejected")))
                .andExpect(jsonPath("$.reasonCode", is("LIMIT_EXCEEDED")));
    }

    /**
     * Test initiate payment, expected payment rejected response due to invalid
     * Debtor IBAN
     * 
     * @throws Exception
     *             not expected exception
     */
    // @Test
    public void testInitiatePayment_Expect_Payment_Rejected_With_Invalid_DebtorIBAN() // NOSONAR
            throws Exception
    {
        final PaymentInitiationRequest request = new PaymentInitiationRequest("INVALID_IBAN",
                "NL91ABNA0417164304", "70.00", "GBR", "U1002");

        mockMvc.perform(MockMvcRequestBuilders.post("/initiate-payment")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.status", is("Rejected")))
                .andExpect(jsonPath("$.reasonCode", is("INVALID_REQUEST")));
    }

    /**
     * Test initiate payment, expected payment rejected response due to Creditor
     * IBAN is null
     * 
     * @throws Exception
     *             not expected exception
     */
    // @Test
    public void testInitiatePayment_Expect_Payment_Rejected_With_Null_CreditorIBAN() // NOSONAR
            throws Exception
    {
        final PaymentInitiationRequest request = new PaymentInitiationRequest("NL91ABNA0417164305",
                null, "100.00", "EUR", "U1000");

        mockMvc.perform(MockMvcRequestBuilders.post("/initiate-payment")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.status", is("Rejected")))
                .andExpect(jsonPath("$.reasonCode", is("INVALID_REQUEST")));
    }
}