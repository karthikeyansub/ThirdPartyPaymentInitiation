package com.rabobank.payment.exception;

/**
 * Generic exception
 */
public class GenericException extends RuntimeException
{

    private static final long serialVersionUID = 3133004334768619750L;

    public GenericException(final String message)
    {
        super(message);
    }
}
