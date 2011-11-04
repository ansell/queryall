package org.queryall.api.rdfrule;

import java.util.Set;

import org.openrdf.model.URI;

public interface SpinNormalisationRule extends NormalisationRule
{

    /**
     * Add an import to this rule
     * 
     * @param nextImport
     */
    public abstract void addImport(String nextImport);

    /**
     * @return Set of Strings that indicate which imports are active for this rule
     */
    public abstract Set<String> getImports();

    /**
     * A URI from the list on http://www.w3.org/ns/entailment/ or a profile on http://www.w3.org/TR/owl-profiles/
     * or another supported URI that indicates whether this rule is 
     * governed by the given entailment
     * 
     * @param inferenceURI A URI from http://www.w3.org/ns/entailment/ or one of the profiles on http://www.w3.org/TR/owl-profiles/ that indicates the entailment for this rule
     * @return True if the entailment is enabled or false otherwise
     */
    public abstract boolean isEntailmentEnabled(URI entailmentURI);

    /**
     * @return A set of URIs indicating which entailments are active for this rule
     */
    public abstract Set<URI> getEntailmentUris();
    
    /**
     * Add an entailment from the list on http://www.w3.org/ns/entailment/ or a profile on http://www.w3.org/TR/owl-profiles/ to this rule
     * 
     * @param nextEntailmentURI A URI from http://www.w3.org/ns/entailment/ or a profile on that indicates the rule will be using this entailment
     */
    public abstract void addEntailmentUri(URI nextEntailmentURI);
    
}
