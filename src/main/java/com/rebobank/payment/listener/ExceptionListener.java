package com.rebobank.payment.listener;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.rebobank.payment.exception.InvalidSignatureException;
import com.rebobank.payment.exception.LimitExceededException;
import com.rebobank.payment.exception.UnknownCertificateException;
import com.rebobank.payment.model.PaymentRejectedResponse;
import com.rebobank.payment.util.ErrorReasonCode;
import com.rebobank.payment.util.TransactionStatus;

@ControllerAdvice
public class ExceptionListener
{

    private PaymentRejectedResponse response;

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<PaymentRejectedResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception)
    {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) ->
        {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        response = new PaymentRejectedResponse(TransactionStatus.Rejected, errors.toString(),
                ErrorReasonCode.INVALID_REQUEST);
        return new ResponseEntity<PaymentRejectedResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = LimitExceededException.class)
    public ResponseEntity<PaymentRejectedResponse> handleLimitExceededException(
            LimitExceededException exception)
    {
        response = new PaymentRejectedResponse(TransactionStatus.Rejected, exception.getMessage(),
                ErrorReasonCode.LIMIT_EXCEEDED);
        return new ResponseEntity<PaymentRejectedResponse>(response,
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = {UnknownCertificateException.class, InvalidSignatureException.class})
    public ResponseEntity<PaymentRejectedResponse> handleCustomerException(
            Exception exception)
    {
        if(exception instanceof UnknownCertificateException)
        {
            response = new PaymentRejectedResponse(TransactionStatus.Rejected, exception.getMessage(),
                    ErrorReasonCode.UNKNOWN_CERTIFICATE);
        } else if(exception instanceof InvalidSignatureException)
        {
            response = new PaymentRejectedResponse(TransactionStatus.Rejected, exception.getMessage(),
                    ErrorReasonCode.INVALID_SIGNATURE);
        }
        return new ResponseEntity<PaymentRejectedResponse>(response,
                HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<PaymentRejectedResponse> handleGenericException(
            Exception exception)
    {
        response = new PaymentRejectedResponse(TransactionStatus.Rejected, exception.getMessage(),
                ErrorReasonCode.GENERAL_ERROR);
        
        return new ResponseEntity<PaymentRejectedResponse>(response,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
