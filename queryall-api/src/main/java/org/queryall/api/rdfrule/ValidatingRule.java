/**
 * 
 */
package org.queryall.api.rdfrule;

import org.openrdf.model.URI;
import org.queryall.exception.InvalidStageException;
import org.queryall.exception.ValidationFailedException;

/**
 * Represents a rule that returns true or false depending on whether the validation conditions pass
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface ValidatingRule extends NormalisationRule
{
    boolean normaliseByStage(URI stage, Object input) throws InvalidStageException, ValidationFailedException;
    
    boolean stageAfterQueryCreation(Object input) throws ValidationFailedException;
    
    boolean stageAfterQueryParsing(Object input) throws ValidationFailedException;
    
    boolean stageAfterResultsImport(Object input) throws ValidationFailedException;
    
    boolean stageAfterResultsToDocument(Object input) throws ValidationFailedException;
    
    boolean stageAfterResultsToPool(Object input) throws ValidationFailedException;
    
    boolean stageBeforeResultsImport(Object input) throws ValidationFailedException;
    
    boolean stageQueryVariables(Object input) throws ValidationFailedException;
}
