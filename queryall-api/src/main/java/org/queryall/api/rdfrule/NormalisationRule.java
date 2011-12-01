package org.queryall.api.rdfrule;

import java.util.Collection;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.base.BaseQueryAllInterface;
import org.queryall.api.base.ProfilableInterface;
import org.queryall.exception.InvalidStageException;

/**
 * Normalisation Rules modify queries to match the expected data for providers that they are linked
 * to, and normalise the results of the query for the provider they are attached to.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface NormalisationRule extends BaseQueryAllInterface, Comparable<NormalisationRule>, ProfilableInterface
{
    /**
     * Adds an informative link to namespaces that are known to be related to this rule.
     * 
     * These namespaces are useful for browsing rules to determine relevant rules for datasets, but
     * they have no semantic effect on the rules operation.
     * 
     * @param nextRelatedNamespace
     *            The URI of a namespace that is related to this rule.
     */
    void addRelatedNamespace(URI nextRelatedNamespace);
    
    /**
     * 
     * @return True if the related namespaces collection was reset and false otherwise
     */
    boolean resetRelatedNamespaces();
    
    /**
     * Selects a stage in the QueryAll model as relevant to this rule.
     * <p/>
     * 
     * The stages are:
     * <ol>
     * <li>Query Variables (defaults to the URI: http://purl.org/queryall/rdfrule:queryVariables )</li>
     * <li>After Query Creation (defaults to the URI:
     * http://purl.org/queryall/rdfrule:afterQueryCreation )</li>
     * <li>After Query Parsing (defaults to the URI:
     * http://purl.org/queryall/rdfrule:afterQueryParsing )</li>
     * <li>Before Results Import (defaults to the URI:
     * http://purl.org/queryall/rdfrule:beforeResultsImport )</li>
     * <li>After Results Import (defaults to the URI:
     * http://purl.org/queryall/rdfrule:afterResultsImport )</li>
     * <li>After Results To Pool (defaults to the URI:
     * http://purl.org/queryall/rdfrule:afterResultsToPool )</li>
     * <li>After Results To Document (defaults to the URI:
     * http://purl.org/queryall/rdfrule:afterResultsToDocument )</li>
     * </ol>
     * 
     * @param nextStage
     *            A URI that identifies a stage that is to be used by this rule.
     * @throws InvalidStageException
     *             If the URI is not recognised as a valid stage for this type of rule.
     */
    void addStage(URI nextStage) throws InvalidStageException;
    
    /**
     * 
     * @return True if the related namespaces collection was reset and false otherwise
     */
    boolean resetStages();
    
    /**
     * Gets the order that this rule will be processed in. If rules have the same integer for their
     * order, they can be processed in any order.
     * 
     * @return The integer denoting the order in which this rule will be processed.
     */
    int getOrder();
    
    /**
     * 
     * @return The collection of URIs that denote namespaces which are informatively linked to this
     *         rule.
     */
    Collection<URI> getRelatedNamespaces();
    
    /**
     * 
     * @return A set of URIs denoting the stages that this rule will be applied to.
     */
    Set<URI> getStages();
    
    /**
     * 
     * @return Returns the list of valid stages for this type of rule. These values are setup
     *         internally for each rule and cannot be modified externally by design.
     */
    Set<URI> getValidStages();
    
    /**
     * The order goes from lowest to highest for the first three stages, and then highest to lowest
     * for the last three stages. This behaviour matches the general contract for rules which
     * denormalise results in the first three stages and then normalise the results in the last four
     * stages.
     * 
     * @param order
     *            An integer denoting the order in which this rule will be applied.
     */
    void setOrder(int order);
    
    /**
     * 
     * @param stage
     *            A URI denoting a stage
     * @return True if the stage is valid and it is used in this rule, and false otherwise.
     * @throws InvalidStageException
     *             if the given stage is not one of the stages defined in
     *             NormalisationRuleSchema.getAllStages
     */
    boolean usedInStage(URI stage) throws InvalidStageException;
    
    /**
     * 
     * @param stage
     *            A URI denoting a stage
     * @return True if the stage is valid for this rule, and false otherwise
     * @throws InvalidStageException
     *             if the given stage is not one of the stages defined in
     *             NormalisationRuleSchema.getAllStages
     */
    boolean validInStage(URI stage) throws InvalidStageException;
    
}
