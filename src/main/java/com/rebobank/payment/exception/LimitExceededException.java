package com.rebobank.payment.exception;

public class LimitExceededException extends RuntimeException
{

	private static final long serialVersionUID = 8267472512406684557L;

	public LimitExceededException(String message)
	{
		super(message);
	}
	
}
