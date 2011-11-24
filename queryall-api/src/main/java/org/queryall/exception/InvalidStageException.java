package org.queryall.exception;

import org.openrdf.model.URI;
import org.queryall.api.rdfrule.NormalisationRule;

/**
 * This exception is thrown if a stage is attempted to be used or added to a rule in an invalid
 * manner.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class InvalidStageException extends QueryAllException
{
    private static final long serialVersionUID = 2391457551752313315L;
    private NormalisationRule ruleCause;
    private URI invalidStageCause;
    
    public InvalidStageException()
    {
        super();
    }
    
    public InvalidStageException(final String message)
    {
        super(message);
    }
    
    public InvalidStageException(final String message, final NormalisationRule nextRule, final URI invalidStage)
    {
        super(message);
        this.setRuleCause(nextRule);
        this.setInvalidStageCause(invalidStage);
    }
    
    public InvalidStageException(final String message, final NormalisationRule nextRule, final URI invalidStage,
            final Throwable cause)
    {
        super(message, cause);
        this.setRuleCause(nextRule);
        this.setInvalidStageCause(invalidStage);
    }
    
    public InvalidStageException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    public InvalidStageException(final Throwable cause)
    {
        super(cause);
    }
    
    /**
     * @return the invalidStageCause
     */
    public URI getInvalidStageCause()
    {
        return this.invalidStageCause;
    }
    
    /**
     * @return the ruleCause
     */
    public NormalisationRule getRuleCause()
    {
        return this.ruleCause;
    }
    
    /**
     * @param invalidStageCause
     *            the invalidStageCause to set
     */
    public void setInvalidStageCause(final URI invalidStageCause)
    {
        this.invalidStageCause = invalidStageCause;
    }
    
    /**
     * @param ruleCause
     *            the ruleCause to set
     */
    public void setRuleCause(final NormalisationRule ruleCause)
    {
        this.ruleCause = ruleCause;
    }
}
