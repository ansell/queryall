package org.queryall.exception;

import org.openrdf.model.URI;
import org.queryall.api.rdfrule.NormalisationRule;

/**
 * This exception is thrown if a stage is attempted to be used or added to a rule in an invalid manner.
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
    
    public InvalidStageException(final String message, NormalisationRule nextRule, URI invalidStage)
    {
        super(message);
        this.setRuleCause(nextRule);
        this.setInvalidStageCause(invalidStage);
    }
    
    public InvalidStageException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    public InvalidStageException(final String message, NormalisationRule nextRule, URI invalidStage, final Throwable cause)
    {
        super(message, cause);
        this.setRuleCause(nextRule);
        this.setInvalidStageCause(invalidStage);
    }
    
    public InvalidStageException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * @return the ruleCause
     */
    public NormalisationRule getRuleCause()
    {
        return ruleCause;
    }

    /**
     * @param ruleCause the ruleCause to set
     */
    public void setRuleCause(NormalisationRule ruleCause)
    {
        this.ruleCause = ruleCause;
    }

    /**
     * @return the invalidStageCause
     */
    public URI getInvalidStageCause()
    {
        return invalidStageCause;
    }

    /**
     * @param invalidStageCause the invalidStageCause to set
     */
    public void setInvalidStageCause(URI invalidStageCause)
    {
        this.invalidStageCause = invalidStageCause;
    }
}
