package com.rebobank.payment.controller;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebobank.payment.exception.LimitExceededException;
import com.rebobank.payment.model.PaymentAcceptedResponse;
import com.rebobank.payment.model.PaymentInitiationRequest;
import com.rebobank.payment.service.PaymentInitiationService;
import com.rebobank.payment.util.TransactionStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Test for PaymentInitiationController
 */
@SpringBootTest
@AutoConfigureMockMvc
//@WithMockUser
public class PaymentInitiationControllerTest
{

    @MockBean
    private PaymentInitiationService mockPaymentInitiationService;

    @Autowired(required = true)
    private MockMvc mockMvc;

    @Autowired(required = true)
    private ObjectMapper objectMapper;
    
    @BeforeEach
    public void setUp()
    {
    }

    /**
     * Test initiate payment with valid input and expected payment accepted status
     * @throws Exception not expected exception
     */
    @Test
    public void testInitiatePayment_Expect_Payment_Accepted() throws Exception
    {
        final PaymentInitiationRequest request = new PaymentInitiationRequest("NL91ABNA0417164301",
                "NL91ABNA0417164302", "100.00", "EUR", "U1000");
        
        final String paymentId = UUID.randomUUID().toString();
        
        final PaymentAcceptedResponse response = new PaymentAcceptedResponse(paymentId, TransactionStatus.Accepted);
        Mockito.when(mockPaymentInitiationService.initiatePayment(Mockito.any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/initiate-payment")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andExpect(MockMvcResultMatchers.status().isCreated())
                 .andExpect(jsonPath("$.paymentId", is(paymentId)))
                 .andExpect(jsonPath("$.status", is("Accepted")));
    }
    
    /**
     * Test initiate payment, expected payment rejected response due to limit exceeded
     * @throws Exception not expected exception
     */
    @Test
    public void testInitiatePayment_Expect_Payment_Rejected_With_Reason_Limit_Exceeded() throws Exception
    {
        final PaymentInitiationRequest request = new PaymentInitiationRequest("NL91ABNA0417164300",
                "NL91ABNA0417164301", "100.00", "EUR", "U1000");
        
        Mockito.doThrow(LimitExceededException.class).when(mockPaymentInitiationService).initiatePayment(Mockito.any());
        
        mockMvc.perform(MockMvcRequestBuilders.post("/initiate-payment")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                 .andExpect(jsonPath("$.status", is("Rejected")))
                 .andExpect(jsonPath("$.reasonCode", is("LIMIT_EXCEEDED")));
    }
    
    /**
     * Test initiate payment, expected payment rejected response due to invalid Debtor IBAN
     * @throws Exception not expected exception
     */
    @Test
    public void testInitiatePayment_Expect_Payment_Rejected_With_Invalid_DebtorIBAN() throws Exception
    {
        final PaymentInitiationRequest request = new PaymentInitiationRequest("INVALID_IBAN",
                "NL91ABNA0417164301", "100.00", "EUR", "U1000");
        
        mockMvc.perform(MockMvcRequestBuilders.post("/initiate-payment")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andExpect(MockMvcResultMatchers.status().isBadRequest())
                 .andExpect(jsonPath("$.status", is("Rejected")))
                 .andExpect(jsonPath("$.reasonCode", is("INVALID_REQUEST")));
    }
    
    /**
     * Test initiate payment, expected payment rejected response due to Creditor IBAN is null
     * @throws Exception not expected exception
     */
    @Test
    public void testInitiatePayment_Expect_Payment_Rejected_With_Null_CreditorIBAN() throws Exception
    {
        final PaymentInitiationRequest request = new PaymentInitiationRequest("NL91ABNA0417164301",
                null, "100.00", "EUR", "U1000");
        
        mockMvc.perform(MockMvcRequestBuilders.post("/initiate-payment")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andExpect(MockMvcResultMatchers.status().isBadRequest())
                 .andExpect(jsonPath("$.status", is("Rejected")))
                 .andExpect(jsonPath("$.reasonCode", is("INVALID_REQUEST")));
    }
}