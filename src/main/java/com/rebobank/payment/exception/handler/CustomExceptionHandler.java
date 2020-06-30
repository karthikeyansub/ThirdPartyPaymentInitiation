
package com.rebobank.payment.exception.handler;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

import com.rebobank.payment.exception.InvalidSignatureException;
import com.rebobank.payment.exception.LimitExceededException;
import com.rebobank.payment.exception.UnknownCertificateException;
import com.rebobank.payment.model.PaymentRejectedResponse;
import com.rebobank.payment.util.ErrorReasonCode;
import com.rebobank.payment.util.TransactionStatus;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler
 */
@ControllerAdvice
public class CustomExceptionHandler
{

    private PaymentRejectedResponse response;

    /**
     * Method argument not valid exception handler
     * 
     * @param exception
     * @return ResponseEntity<PaymentRejectedResponse> the rejected response
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<PaymentRejectedResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception)
    {
        // Extract the default error message from the exception
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) ->
        {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        response = new PaymentRejectedResponse(TransactionStatus.Rejected, errors.toString(), ErrorReasonCode.INVALID_REQUEST);
        return new ResponseEntity<PaymentRejectedResponse>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Limit exceeded exception handler
     * 
     * @param exception
     * @return ResponseEntity<PaymentRejectedResponse> the rejected response
     */
    @ExceptionHandler(value = LimitExceededException.class)
    public ResponseEntity<PaymentRejectedResponse> handleLimitExceededException(final LimitExceededException exception)
    {
        response = new PaymentRejectedResponse(TransactionStatus.Rejected, exception.getMessage(), ErrorReasonCode.LIMIT_EXCEEDED);
        return new ResponseEntity<PaymentRejectedResponse>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Unknown certificate exception handler
     * 
     * @param exception
     * @return ResponseEntity<PaymentRejectedResponse> the rejected response
     */
    @ExceptionHandler(value = UnknownCertificateException.class)
    public ResponseEntity<PaymentRejectedResponse> handleUnknownCertificateException(final UnknownCertificateException exception)
    {
        response = new PaymentRejectedResponse(TransactionStatus.Rejected, exception.getMessage(), ErrorReasonCode.UNKNOWN_CERTIFICATE);
        return new ResponseEntity<PaymentRejectedResponse>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Invalid signature exception handler
     * 
     * @param exception
     * @return ResponseEntity<PaymentRejectedResponse> the rejected response
     */
    @ExceptionHandler(value = InvalidSignatureException.class)
    public ResponseEntity<PaymentRejectedResponse> handleInvalidSignatureException(final InvalidSignatureException exception)
    {
        response = new PaymentRejectedResponse(TransactionStatus.Rejected, exception.getMessage(), ErrorReasonCode.INVALID_SIGNATURE);
        return new ResponseEntity<PaymentRejectedResponse>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * General exception handler
     * 
     * @param exception
     * @return ResponseEntity<PaymentRejectedResponse> the rejected response
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<PaymentRejectedResponse> handleGenericException(final Exception exception)
    {
        response = new PaymentRejectedResponse(TransactionStatus.Rejected, exception.getMessage(), ErrorReasonCode.GENERAL_ERROR);

        return new ResponseEntity<PaymentRejectedResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<PaymentRejectedResponse> handleAccessDeniedException(AccessDeniedException exception)
    {
        response = new PaymentRejectedResponse(TransactionStatus.Rejected, exception.getMessage(), ErrorReasonCode.UNKNOWN_CERTIFICATE);

        return new ResponseEntity<PaymentRejectedResponse>(response, HttpStatus.BAD_REQUEST);
    }
}
