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
 */
public interface TransformingRule extends NormalisationRule
{
    /**
     * Performs normalisation for the given stage by transforming the input object into an output
     * object.
     * 
     * The types of the input and output objects should be equivalent.
     * 
     * @param stage
     *            A URI denoting the stage to use. This stage must be a valid stage for this type of
     *            rule based on the result of validForStage(stage)
     * @param input
     *            The input object to be normalised by this rule.
     * @return The output object after normalisation by this rule.
     * @throws InvalidStageException
     *             If the given stage was not valid.
     * @throws QueryAllException
     *             If the normalisation fails to complete.
     */
    Object normaliseByStage(URI stage, Object input) throws InvalidStageException, QueryAllException;
    
    /**
     * Transforms the given input object in the stage after query creation, but before query
     * parsing.
     * 
     * @param input
     *            The input object to be transformed.
     * @return The output object after normalisation by this rule.
     * @throws QueryAllException
     *             If the transformation failed.
     */
    Object stageAfterQueryCreation(Object input) throws QueryAllException;
    
    /**
     * Transforms the given input object in the stage after query parsing, but before query
     * submission to the provider.
     * 
     * @param input
     *            The input object to be transformed.
     * @return The output object after normalisation by this rule.
     * @throws QueryAllException
     *             If the transformation failed.
     */
    Object stageAfterQueryParsing(Object input) throws QueryAllException;
    
    /**
     * Transforms the given input object in the stage after the RDF results have been imported from
     * the results for a provider, but before they have been merged into a pool with results from
     * other providers.
     * 
     * @param input
     *            The input object to be transformed.
     * @return The output object after normalisation by this rule.
     * @throws QueryAllException
     *             If the transformation failed.
     */
    Object stageAfterResultsImport(Object input) throws QueryAllException;
    
    /**
     * Transforms the given input object in the stage after the combined RDF statements in the pool
     * have been serialised to the results document.
     * 
     * @param input
     *            The input object to be transformed.
     * @return The output object after normalisation by this rule.
     * @throws QueryAllException
     *             If the transformation failed.
     */
    Object stageAfterResultsToDocument(Object input) throws QueryAllException;
    
    /**
     * Transforms the given input object in the stage after the RDF results have been merged into a
     * pool of RDF statements, but before they have been serialised to the results document.
     * 
     * @param input
     *            The input object to be transformed.
     * @return The output object after normalisation by this rule.
     * @throws QueryAllException
     *             If the transformation failed.
     */
    Object stageAfterResultsToPool(Object input) throws QueryAllException;
    
    /**
     * Transforms the given input object in the stage after results have been returned from a
     * provider, but before they have been parsed into RDF statements.
     * 
     * @param input
     *            The input object to be transformed.
     * @return The output object after normalisation by this rule.
     * @throws QueryAllException
     *             If the transformation failed.
     */
    Object stageBeforeResultsImport(Object input) throws QueryAllException;
    
    /**
     * Transforms the given input object in the stage where query variables are being normalised
     * based on the context of the provider.
     * 
     * @param input
     *            The input object to be transformed.
     * @return The output object after normalisation by this rule.
     * @throws QueryAllException
     *             If the transformation failed.
     */
    Object stageQueryVariables(Object input) throws QueryAllException;
}
