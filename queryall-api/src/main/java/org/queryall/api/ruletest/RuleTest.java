package org.queryall.api.ruletest;

import java.util.Collection;

import org.openrdf.model.URI;
import org.queryall.api.base.BaseQueryAllInterface;

/**
 * A Rule Test provides a way to specify the expected results for a normalisation to ensure that the
 * rule executes properly for the current implementation of QueryAll.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface RuleTest extends BaseQueryAllInterface, Comparable<RuleTest>
{
    /**
     * 
     * @param rdfRuleUri
     *            A URI identifying a rule that needs to be tested by this rule test
     */
    void addRuleUri(URI rdfRuleUri);
    
    /**
     * 
     * @return True if the rule URIs collection was reset and false otherwise.
     */
    boolean resetRuleUris();
    
    /**
     * 
     * @param stage
     *            A URI identifying a stage that is relevant to this rule test
     */
    void addStage(URI stage);
    
    /**
     * 
     * @return True if the stages collection was reset and false otherwise.
     */
    boolean resetStages();
    
    /**
     * 
     * @return A collection of URIs identifying rules that need to be tested by this rule test
     */
    Collection<URI> getRuleUris();
    
    /**
     * 
     * @return A collection of URIs identifying stages that are relevant to this rule test
     */
    Collection<URI> getStages();
    
}
