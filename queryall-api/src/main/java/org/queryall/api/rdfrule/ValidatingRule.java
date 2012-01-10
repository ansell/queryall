/**
 * 
 */
package org.queryall.api.rdfrule;

import org.openrdf.model.URI;
import org.queryall.exception.InvalidStageException;
import org.queryall.exception.QueryAllException;
import org.queryall.exception.ValidationFailedException;

/**
 * Represents a rule that returns true or false depending on whether the validation conditions pass.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface ValidatingRule extends NormalisationRule
{
    /**
     * Performs validation for the given stage by validating the input object using the criteria
     * defined for this rule.
     * 
     * @param stage
     *            A URI denoting the stage to use. This stage must be a valid stage for this type of
     *            rule based on the result of validForStage(stage)
     * @param input
     *            The input object to be validated by this rule.
     * @return True if the validation succeeded, or false otherwise.
     * @throws InvalidStageException
     *             If the given stage was not valid.
     * @throws ValidationFailedException
     *             If the validation failed.
     * @throws QueryAllException
     *             If the validation process did not complete.
     */
    boolean normaliseByStage(URI stage, Object input) throws InvalidStageException, ValidationFailedException,
        QueryAllException;
    
    /**
     * Validates the given input object in the stage after query creation, but before query parsing.
     * 
     * @param input
     *            The input object to be validated.
     * @return True if the input object was valid and false otherwise.
     * @throws ValidationFailedException
     *             If the validation failed.
     */
    boolean stageAfterQueryCreation(Object input) throws ValidationFailedException;
    
    /**
     * Validates the given input object in the stage after query parsing, but before query
     * submission to the provider.
     * 
     * @param input
     *            The input object to be validated.
     * @return True if the input object was valid and false otherwise.
     * @throws ValidationFailedException
     *             If the validation failed.
     */
    boolean stageAfterQueryParsing(Object input) throws ValidationFailedException;
    
    /**
     * Validates the given input object in the stage after the RDF results have been imported from
     * the results for a provider, but before they have been merged into a pool with results from
     * other providers.
     * 
     * @param input
     *            The input object to be validated.
     * @return True if the input object was valid and false otherwise.
     * @throws ValidationFailedException
     *             If the validation failed.
     */
    boolean stageAfterResultsImport(Object input) throws ValidationFailedException;
    
    /**
     * Validates the given input object in the stage after the combined RDF statements in the pool
     * have been serialised to the results document.
     * 
     * @param input
     *            The input object to be validated.
     * @return True if the input object was valid and false otherwise.
     * @throws ValidationFailedException
     *             If the validation failed.
     */
    boolean stageAfterResultsToDocument(Object input) throws ValidationFailedException;
    
    /**
     * Validates the given input object in the stage after the RDF results have been merged into a
     * pool of RDF statements, but before they have been serialised to the results document.
     * 
     * @param input
     *            The input object to be validated.
     * @return True if the input object was valid and false otherwise.
     * @throws ValidationFailedException
     *             If the validation failed.
     */
    boolean stageAfterResultsToPool(Object input) throws ValidationFailedException;
    
    /**
     * Validates the given input object in the stage after results have been returned from a
     * provider, but before they have been parsed into RDF statements.
     * 
     * @param input
     *            The input object to be validated.
     * @return True if the input object was valid and false otherwise.
     * @throws ValidationFailedException
     *             If the validation failed.
     */
    boolean stageBeforeResultsImport(Object input) throws ValidationFailedException;
    
    /**
     * Validates the given input object in the stage where query variables are being normalised
     * based on the context of the provider.
     * 
     * @param input
     *            The input object to be validated.
     * @return True if the input object was valid and false otherwise.
     * @throws ValidationFailedException
     *             If the validation failed.
     */
    boolean stageQueryVariables(Object input) throws ValidationFailedException;
}
