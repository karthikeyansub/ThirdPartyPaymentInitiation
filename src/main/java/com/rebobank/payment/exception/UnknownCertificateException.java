package com.rebobank.payment.exception;

public class UnknownCertificateException extends RuntimeException
{
    private static final long serialVersionUID = -7793640794418384290L;

    public UnknownCertificateException(String message)
    {
        super(message);
    }
}
