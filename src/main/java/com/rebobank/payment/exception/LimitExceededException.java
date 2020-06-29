package com.rebobank.payment.exception;

/**
 * Limit Exceeded Exception
 */
public class LimitExceededException extends RuntimeException
{

	private static final long serialVersionUID = 8267472512406684557L;

	public LimitExceededException(final String message)
	{
		super(message);
	}
	
}
