package org.queryall.api.rdfrule;

import java.util.Collection;

import org.openrdf.model.URI;

/**
 * Prefix Mapping Normalisation Rules encapsulate the necessary actions surrounding effective URI
 * translation.
 * 
 * They map URIs using a common prefix from a normalised scheme into a denormalised scheme to match
 * a provider, before also mapping the URI back to the normalised scheme in results from the
 * provider.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface PrefixMappingNormalisationRule extends TransformingRule
{
    /**
     * Adds a URI to the list of mapping predicates for objects if their URIs are modified by this
     * rule.
     * 
     * <p/>
     * 
     * If this rule is used with OWL, and it is targeted at Individuals, this could be owl:sameAs.
     * 
     * <p/>
     * 
     * If this rule is used with OWL, and it is targeted at Classes, this could be
     * owl:equivalentClass.
     * 
     * @param objectMappingPredicate
     *            A predicate that should be used to map objects if their URIs are modified by this
     *            rule.
     */
    void addObjectMappingPredicate(URI objectMappingPredicate);
    
    /**
     * Adds a URI to the list of mapping predicates for predicates if their URIs are modified by
     * this rule.
     * 
     * <p/>
     * 
     * If this rule is used with OWL, this could be owl:equivalentProperty.
     * 
     * @param predicateMappingPredicate
     *            A predicate that should be used to map predicates if their URIs are modified by
     *            this rule.
     */
    void addPredicateMappingPredicate(URI predicateMappingPredicate);
    
    /**
     * Adds a URI to the list of mapping predicates for subjects if their URIs are modified by this
     * rule.
     * 
     * <p/>
     * 
     * If this rule is used with OWL, and it is targeted at Individuals, this could be owl:sameAs.
     * 
     * <p/>
     * 
     * If this rule is used with OWL, and it is targeted at Classes, this could be
     * owl:equivalentClass.
     * 
     * @param subjectMappingPredicate
     *            A predicate that should be used to map subjects if their URIs are modified by this
     *            rule.
     */
    void addSubjectMappingPredicate(URI subjectMappingPredicate);
    
    /**
     * Returns the URI prefix that will be used to determine whether to map a URI with this rule.
     * 
     * This prefix does not have to be a valid prefix for any RDF file formats. It is simply the
     * base of a URI that needs to be mapped.
     * 
     * Its lexical value is compared with lexical values of URIs using
     * String.startsWith(inputUriPrefix)
     * 
     * @return The URI prefix for this rule to match against the graph.
     */
    String getInputUriPrefix();
    
    /**
     * 
     * @return The collection of mapping predicates used to map objects if their URIs change.
     */
    Collection<URI> getObjectMappingPredicates();
    
    /**
     * 
     * @return The output URI prefix for this rule to replace any matches that matched using the
     *         inputUriPrefix
     */
    String getOutputUriPrefix();
    
    /**
     * 
     * @return The collection of mapping predicates used to map predicates if their URIs change.
     */
    Collection<URI> getPredicateMappingPredicates();
    
    /**
     * 
     * @return The collection of mapping predicates used to map subjects if their URIs change.
     */
    Collection<URI> getSubjectMappingPredicates();
    
    /**
     * 
     * @return True if the object mapping predicates list was reset and false otherwise.
     */
    boolean resetObjectMappingPredicates();
    
    /**
     * 
     * @return True if the predicate mapping predicates list was reset and false otherwise.
     */
    boolean resetPredicateMappingPredicates();
    
    /**
     * 
     * @return True if the object mapping predicates list was reset and false otherwise.
     */
    boolean resetSubjectMappingPredicates();
    
    /**
     * 
     * @param inputUriPrefix
     *            The string denoting the URI prefix that will be used to match with this rule.
     */
    void setInputUriPrefix(String inputUriPrefix);
    
    /**
     * 
     * @param outputUriPrefix
     *            The string denoting the URI prefix to replace any matches with inputUriPrefix
     *            using this rule.
     */
    void setOutputUriPrefix(String outputUriPrefix);
    
}