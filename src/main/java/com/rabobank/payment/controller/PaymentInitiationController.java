package com.rabobank.payment.controller;

import javax.validation.Valid;

import com.rabobank.payment.model.PaymentAcceptedResponse;
import com.rabobank.payment.model.PaymentInitiationRequest;
import com.rabobank.payment.service.PaymentInitiationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

/**
 * Payment initiation controller
 */
@RestController
@AllArgsConstructor
public class PaymentInitiationController
{

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentInitiationController.class);

    private final PaymentInitiationService paymentInitiationService;
    
    /**
     * Initiate third party payment
     * 
     * @param request
     *            the Payment Initiation Request
     * @return ResponseEntity<PaymentAcceptedResponse>
     */
    @PostMapping(path = "/initiate-payment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentAcceptedResponse> initiatePayment(
           @Valid @RequestBody final PaymentInitiationRequest request)
    {
        LOGGER.info("Initiate payment controller");
        PaymentAcceptedResponse response = paymentInitiationService.initiatePayment(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
