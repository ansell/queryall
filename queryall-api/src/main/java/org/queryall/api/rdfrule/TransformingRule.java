/**
 * 
 */
package org.queryall.api.rdfrule;

import org.openrdf.model.URI;
import org.queryall.exception.InvalidStageException;
import org.queryall.exception.QueryAllException;

/**
 * This interface provides object transformation methods based on the stages defined for
 * normalisation rules.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public interface TransformingRule extends NormalisationRule
{
    Object normaliseByStage(URI stage, Object input) throws InvalidStageException, QueryAllException;
    
    Object stageAfterQueryCreation(Object input) throws QueryAllException;
    
    Object stageAfterQueryParsing(Object input) throws QueryAllException;
    
    Object stageAfterResultsImport(Object input) throws QueryAllException;
    
    Object stageAfterResultsToDocument(Object input) throws QueryAllException;
    
    Object stageAfterResultsToPool(Object input) throws QueryAllException;
    
    Object stageBeforeResultsImport(Object input) throws QueryAllException;
    
    Object stageQueryVariables(Object input) throws QueryAllException;
    
}
