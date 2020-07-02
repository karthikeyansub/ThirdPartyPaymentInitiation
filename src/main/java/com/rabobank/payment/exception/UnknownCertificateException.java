package com.rabobank.payment.exception;

/**
 * Unknown certification exception
 */
public class UnknownCertificateException extends RuntimeException
{
    private static final long serialVersionUID = -7793640794418384290L;

    public UnknownCertificateException(final String message)
    {
        super(message);
    }
}
