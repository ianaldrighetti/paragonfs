package org.paragon.paragonfs.exception;

/**
 * The base exception for ParagonFS errors.
 */
public class ParagonFSException extends Exception
{
	public ParagonFSException(final String message)
	{
		super(message);
	}

	public ParagonFSException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
