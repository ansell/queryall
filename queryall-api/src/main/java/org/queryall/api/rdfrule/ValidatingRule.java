/**
 * 
 */
package org.queryall.api.rdfrule;

import org.openrdf.model.URI;
import org.queryall.exception.ValidationFailedException;

/**
 * Represents a rule that throws ValidationFailedExceptions if the rule fails to validate the results at any stage, instead of returning results
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface ValidatingRule extends NormalisationRule
{
    Object normaliseByStage(URI stage, Object input) throws ValidationFailedException;
    
    Object stageAfterQueryCreation(Object input) throws ValidationFailedException;
    
    Object stageAfterQueryParsing(Object input) throws ValidationFailedException;
    
    Object stageAfterResultsImport(Object input) throws ValidationFailedException;
    
    Object stageAfterResultsToDocument(Object input) throws ValidationFailedException;
    
    Object stageAfterResultsToPool(Object input) throws ValidationFailedException;
    
    Object stageBeforeResultsImport(Object input) throws ValidationFailedException;
    
    Object stageQueryVariables(Object input) throws ValidationFailedException;
}
