package com.rebobank.payment.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Unknown certification exception
 */
public class UnknownCertificateException extends UsernameNotFoundException
{
    private static final long serialVersionUID = -7793640794418384290L;

    public UnknownCertificateException(final String message)
    {
        super(message);
    }
}
