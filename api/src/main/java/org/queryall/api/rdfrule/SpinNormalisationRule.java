package org.queryall.api.rdfrule;

import java.util.Set;

import org.openrdf.model.URI;

/**
 * The SpinNormalisationRule interface encapsulates the common components for the SpinConstraintRule
 * and SpinInferencingRules, providing a list of rules and spin profiles that are active for this
 * rule.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface SpinNormalisationRule extends NormalisationRule
{
    
    /**
     * Add an entailment from the list on http://www.w3.org/ns/entailment/ or a profile on
     * http://www.w3.org/TR/owl-profiles/ to this rule.
     * 
     * @param nextEntailmentURI
     *            A URI from http://www.w3.org/ns/entailment/ or a profile on that indicates the
     *            rule will be using this entailment
     */
    void addEntailmentUri(URI nextEntailmentURI);
    
    /**
     * Add an import to this rule based on a classpath resource.
     * 
     * @param nextImport
     */
    void addLocalImport(String nextLocalImport);
    
    /**
     * Add an import to this rule based on a URL.
     * 
     * @param nextImport
     */
    void addUrlImport(URI nextURLImport);
    
    /**
     * @return A set of URIs indicating which entailments are active for this rule.
     */
    Set<URI> getEntailmentUris();
    
    /**
     * @return Set of Strings that indicate which local imports are active for this rule.
     */
    Set<String> getLocalImports();
    
    /**
     * @return Set of URLs that indicate which URL imports are active for this rule.
     */
    Set<URI> getURLImports();
    
    /**
     * A URI from the list on http://www.w3.org/ns/entailment/ or a profile on
     * http://www.w3.org/TR/owl-profiles/ or another supported URI that indicates whether this rule
     * is governed by the given entailment.
     * 
     * @param inferenceURI
     *            A URI from http://www.w3.org/ns/entailment/ or one of the profiles on
     *            http://www.w3.org/TR/owl-profiles/ that indicates the entailment for this rule
     * @return True if the entailment is enabled or false otherwise
     */
    boolean isEntailmentEnabled(URI entailmentURI);
    
    /**
     * 
     * @return True if the entailment URIs collection was reset and false otherwise.
     */
    boolean resetEntailmentUris();
    
    /**
     * 
     * @return True if the local imports collection was reset and false otherwise.
     */
    boolean resetLocalImports();
    
    /**
     * 
     * @return True if the local imports collection was reset and false otherwise.
     */
    boolean resetUrlImports();
    
}
