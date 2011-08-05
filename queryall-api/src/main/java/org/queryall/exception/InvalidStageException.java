package org.queryall.exception;

/**
 * This exception is thrown if a stage is added to a normalisation rule 
 * where the stage is not in the list of valid stages for the rule
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class InvalidStageException extends IllegalArgumentException 
{
	private static final long serialVersionUID = 2391457551752313315L;

	public InvalidStageException() 
	{
		super();
	}
	
	public InvalidStageException(String string) 
	{
		super(string);
	}

	public InvalidStageException(Throwable cause) 
	{
		super(cause);
	}

	public InvalidStageException(String string, Throwable cause) 
	{
		super(string, cause);
	}
}
