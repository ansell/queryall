/**
 * 
 */
package org.queryall.api.rdfrule;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SparqlNormalisationRuleSchema
{
    private static final Logger log = LoggerFactory.getLogger(SparqlNormalisationRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = SparqlNormalisationRuleSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = SparqlNormalisationRuleSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SparqlNormalisationRuleSchema.log.isInfoEnabled();
    
    private static URI sparqlruleTypeUri;
    
    private static URI sparqlruleSparqlConstructQueryTarget;
    
    private static URI sparqlruleMode;
    
    private static URI sparqlruleModeOnlyIncludeMatches;
    
    private static URI sparqlruleModeOnlyDeleteMatches;
    
    private static URI sparqlruleModeAddAllMatchingTriples;
    
    private static URI sparqlruleSparqlWherePattern;
    
    private static URI sparqlruleSparqlPrefixes;
    
    private static URI sparqlruleSparqlConstructQuery;
    
    private static URI OLDsparqlruleModeOnlyDeleteMatches;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        SparqlNormalisationRuleSchema.setSparqlRuleTypeUri(f.createURI(baseUri, "SparqlNormalisationRule"));
        SparqlNormalisationRuleSchema
                .setOLDSparqlRuleSparqlConstructQuery(f.createURI(baseUri, "sparqlConstructQuery"));
        SparqlNormalisationRuleSchema.setSparqlRuleSparqlConstructQueryTarget(f.createURI(baseUri,
                "sparqlConstructQueryTarget"));
        SparqlNormalisationRuleSchema.setSparqlRuleSparqlWherePattern(f.createURI(baseUri, "sparqlWherePatterns"));
        SparqlNormalisationRuleSchema.setSparqlRuleSparqlPrefixes(f.createURI(baseUri, "sparqlPrefixes"));
        SparqlNormalisationRuleSchema.setSparqlRuleMode(f.createURI(baseUri, "mode"));
        SparqlNormalisationRuleSchema.setSparqlRuleModeOnlyDeleteMatches(f.createURI(baseUri,
                "onlyDeleteMatchingTriples"));
        SparqlNormalisationRuleSchema.setOLDsparqlruleModeOnlyDeleteMatches(f.createURI(baseUri, "onlyDeleteMatches"));
        SparqlNormalisationRuleSchema.setSparqlRuleModeOnlyIncludeMatches(f.createURI(baseUri,
                "onlyIncludeMatchingTriples"));
        SparqlNormalisationRuleSchema.setSparqlRuleModeAddAllMatchingTriples(f.createURI(baseUri,
                "addAllMatchingTriples"));
    }
    
    /**
     * @return the oLDsparqlruleModeOnlyDeleteMatches
     */
    public static URI getOLDsparqlruleModeOnlyDeleteMatches()
    {
        return SparqlNormalisationRuleSchema.OLDsparqlruleModeOnlyDeleteMatches;
    }
    
    public static URI getOLDSparqlRuleSparqlConstructQuery()
    {
        return SparqlNormalisationRuleSchema.sparqlruleSparqlConstructQuery;
    }
    
    /**
     * @return the sparqlruleMode
     */
    public static URI getSparqlRuleMode()
    {
        return SparqlNormalisationRuleSchema.sparqlruleMode;
    }
    
    /**
     * @return the sparqlruleModeAddAllMatchingTriples
     */
    public static URI getSparqlRuleModeAddAllMatchingTriples()
    {
        return SparqlNormalisationRuleSchema.sparqlruleModeAddAllMatchingTriples;
    }
    
    // public static String rdfruleNamespace;
    
    /**
     * @return the sparqlruleModeOnlyDeleteMatches
     */
    public static URI getSparqlRuleModeOnlyDeleteMatches()
    {
        return SparqlNormalisationRuleSchema.sparqlruleModeOnlyDeleteMatches;
    }
    
    /**
     * @return the sparqlruleModeOnlyIncludeMatches
     */
    public static URI getSparqlRuleModeOnlyIncludeMatches()
    {
        return SparqlNormalisationRuleSchema.sparqlruleModeOnlyIncludeMatches;
    }
    
    /**
     * @return the sparqlruleSparqlConstructQueryTarget
     */
    public static URI getSparqlRuleSparqlConstructQueryTarget()
    {
        return SparqlNormalisationRuleSchema.sparqlruleSparqlConstructQueryTarget;
    }
    
    public static URI getSparqlRuleSparqlPrefixes()
    {
        return SparqlNormalisationRuleSchema.sparqlruleSparqlPrefixes;
    }
    
    public static URI getSparqlRuleSparqlWherePattern()
    {
        return SparqlNormalisationRuleSchema.sparqlruleSparqlWherePattern;
    }
    
    /**
     * @return the sparqlruleTypeUri
     */
    public static URI getSparqlRuleTypeUri()
    {
        return SparqlNormalisationRuleSchema.sparqlruleTypeUri;
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        NormalisationRuleSchema.schemaToRdf(myRepository, contextUri, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(SparqlNormalisationRuleSchema.getSparqlRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(SparqlNormalisationRuleSchema.getSparqlRuleTypeUri(), RDFS.LABEL,
                    f.createLiteral("A SPARQL based normalisation rule intended to normalise in-memory RDF triples."),
                    contextUri);
            con.add(SparqlNormalisationRuleSchema.getSparqlRuleTypeUri(), RDFS.SUBCLASSOF,
                    NormalisationRuleSchema.getNormalisationRuleTypeUri(), contextUri);
            
            // TODO: update schema
            con.add(SparqlNormalisationRuleSchema.getSparqlRuleSparqlConstructQueryTarget(), RDF.TYPE,
                    OWL.DATATYPEPROPERTY, contextUri);
            con.add(SparqlNormalisationRuleSchema.getSparqlRuleSparqlConstructQueryTarget(), RDFS.RANGE, RDFS.LITERAL,
                    contextUri);
            con.add(SparqlNormalisationRuleSchema.getSparqlRuleSparqlConstructQueryTarget(), RDFS.DOMAIN,
                    SparqlNormalisationRuleSchema.getSparqlRuleTypeUri(), contextUri);
            con.add(SparqlNormalisationRuleSchema.getSparqlRuleSparqlConstructQueryTarget(),
                    RDFS.LABEL,
                    f.createLiteral("The CONSTRUCT { ... } part of the query that will be used to match against RDF triples in memory at the assigned stages, in the form of a basic graph pattern."),
                    contextUri);
            
            con.add(SparqlNormalisationRuleSchema.getSparqlRuleMode(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(SparqlNormalisationRuleSchema.getSparqlRuleMode(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(SparqlNormalisationRuleSchema.getSparqlRuleMode(), RDFS.DOMAIN,
                    SparqlNormalisationRuleSchema.getSparqlRuleTypeUri(), contextUri);
            con.add(SparqlNormalisationRuleSchema.getSparqlRuleMode(),
                    RDFS.LABEL,
                    f.createLiteral("The mode that this normalisation rule will be used in. In the absence of SPARQL Update language support, this enables deletions and filtering based on the matched triples."),
                    contextUri);
            
            con.add(SparqlNormalisationRuleSchema.getSparqlRuleModeOnlyIncludeMatches(),
                    RDFS.LABEL,
                    f.createLiteral("Specifies that the SPARQL rule will be applied, and only the matches from the rule will remain in the relevant RDF triple store after the application. If the stage is after Results Import, only the results from the current provider and query will be affected."),
                    contextUri);
            
            con.add(SparqlNormalisationRuleSchema.getSparqlRuleModeOnlyDeleteMatches(),
                    RDFS.LABEL,
                    f.createLiteral("Specifies that the SPARQL rule will be applied, and the matches from the rule will be deleted from the relevant RDF triple store after the application. If the stage is after Results Import, only the results from the current provider and query will be affected."),
                    contextUri);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            if(con != null)
            {
                con.rollback();
            }
            
            SparqlNormalisationRuleSchema.log.error("RepositoryException: " + re.getMessage());
        }
        finally
        {
            if(con != null)
            {
                con.close();
            }
        }
        
        return false;
    }
    
    /**
     * @param oLDsparqlruleModeOnlyDeleteMatches
     *            the oLDsparqlruleModeOnlyDeleteMatches to set
     */
    public static void setOLDsparqlruleModeOnlyDeleteMatches(final URI oLDsparqlruleModeOnlyDeleteMatches)
    {
        SparqlNormalisationRuleSchema.OLDsparqlruleModeOnlyDeleteMatches = oLDsparqlruleModeOnlyDeleteMatches;
    }
    
    private static void setOLDSparqlRuleSparqlConstructQuery(final URI sparqlruleSparqlConstructQuery)
    {
        SparqlNormalisationRuleSchema.sparqlruleSparqlConstructQuery = sparqlruleSparqlConstructQuery;
        
    }
    
    /**
     * @param sparqlruleMode
     *            the sparqlruleMode to set
     */
    public static void setSparqlRuleMode(final URI sparqlruleMode)
    {
        SparqlNormalisationRuleSchema.sparqlruleMode = sparqlruleMode;
    }
    
    /**
     * @param sparqlruleModeAddAllMatchingTriples
     *            the sparqlruleModeAddAllMatchingTriples to set
     */
    public static void setSparqlRuleModeAddAllMatchingTriples(final URI sparqlruleModeAddAllMatchingTriples)
    {
        SparqlNormalisationRuleSchema.sparqlruleModeAddAllMatchingTriples = sparqlruleModeAddAllMatchingTriples;
    }
    
    /**
     * @param sparqlruleModeOnlyDeleteMatches
     *            the sparqlruleModeOnlyDeleteMatches to set
     */
    public static void setSparqlRuleModeOnlyDeleteMatches(final URI sparqlruleModeOnlyDeleteMatches)
    {
        SparqlNormalisationRuleSchema.sparqlruleModeOnlyDeleteMatches = sparqlruleModeOnlyDeleteMatches;
    }
    
    /**
     * @param sparqlruleModeOnlyIncludeMatches
     *            the sparqlruleModeOnlyIncludeMatches to set
     */
    public static void setSparqlRuleModeOnlyIncludeMatches(final URI sparqlruleModeOnlyIncludeMatches)
    {
        SparqlNormalisationRuleSchema.sparqlruleModeOnlyIncludeMatches = sparqlruleModeOnlyIncludeMatches;
    }
    
    /**
     * @param sparqlruleSparqlConstructQueryTarget
     *            the sparqlruleSparqlConstructQueryTarget to set
     */
    public static void setSparqlRuleSparqlConstructQueryTarget(final URI sparqlruleSparqlConstructQuery)
    {
        SparqlNormalisationRuleSchema.sparqlruleSparqlConstructQueryTarget = sparqlruleSparqlConstructQuery;
    }
    
    public static void setSparqlRuleSparqlPrefixes(final URI sparqlruleSparqlPrefixes)
    {
        SparqlNormalisationRuleSchema.sparqlruleSparqlPrefixes = sparqlruleSparqlPrefixes;
    }
    
    public static void setSparqlRuleSparqlWherePattern(final URI sparqlruleSparqlWherePattern)
    {
        SparqlNormalisationRuleSchema.sparqlruleSparqlWherePattern = sparqlruleSparqlWherePattern;
    }
    
    /**
     * @param sparqlruleTypeUri
     *            the sparqlruleTypeUri to set
     */
    public static void setSparqlRuleTypeUri(final URI sparqlruleTypeUri)
    {
        SparqlNormalisationRuleSchema.sparqlruleTypeUri = sparqlruleTypeUri;
    }
    
}
