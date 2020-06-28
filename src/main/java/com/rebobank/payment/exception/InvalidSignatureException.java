package com.rebobank.payment.exception;

public class InvalidSignatureException extends RuntimeException
{
    private static final long serialVersionUID = -4818467074614148420L;

    public InvalidSignatureException(final String message)
    {
        super(message);
    }

}