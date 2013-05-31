/**
 * 
 */
package org.queryall.api.rdfrule;

import org.kohsuke.MetaInfServices;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.base.QueryAllSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllSchema.class)
public class SparqlConstructRuleSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(SparqlConstructRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = SparqlConstructRuleSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = SparqlConstructRuleSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = SparqlConstructRuleSchema.LOG.isInfoEnabled();
    
    private static URI sparqlruleTypeUri;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        SparqlConstructRuleSchema.setSparqlConstructRuleTypeUri(f.createURI(baseUri, "SparqlConstructRule"));
        SparqlConstructRuleSchema.setOLDSparqlRuleSparqlConstructQuery(f.createURI(baseUri, "sparqlConstructQuery"));
        SparqlConstructRuleSchema.setSparqlRuleSparqlConstructQueryTarget(f.createURI(baseUri,
                "sparqlConstructQueryTarget"));
        SparqlConstructRuleSchema.setSparqlRuleMode(f.createURI(baseUri, "sparqlMode"));
        SparqlConstructRuleSchema.setSparqlRuleModeOnlyDeleteMatches(f.createURI(baseUri, "onlyDeleteMatchingTriples"));
        SparqlConstructRuleSchema.setOLDsparqlruleModeOnlyDeleteMatches(f.createURI(baseUri, "onlyDeleteMatches"));
        SparqlConstructRuleSchema.setSparqlRuleModeOnlyIncludeMatches(f
                .createURI(baseUri, "onlyIncludeMatchingTriples"));
        SparqlConstructRuleSchema.setSparqlRuleModeAddAllMatchingTriples(f.createURI(baseUri, "addAllMatchingTriples"));
    }
    
    static URI sparqlruleSparqlConstructQueryTarget;
    
    static URI sparqlruleMode;
    
    static URI sparqlruleModeOnlyIncludeMatches;
    
    static URI sparqlruleModeOnlyDeleteMatches;
    
    static URI sparqlruleModeAddAllMatchingTriples;
    
    static URI sparqlruleSparqlConstructQuery;
    
    static URI oldSparqlruleModeOnlyDeleteMatches;
    
    /**
     * A pre-instantiated schema object for SparqlConstructRuleSchema.
     */
    public static final QueryAllSchema SPARQL_CONSTRUCT_RULE_SCHEMA = new SparqlConstructRuleSchema();
    
    /**
     * @return the oLDsparqlruleModeOnlyDeleteMatches
     */
    public static URI getOLDsparqlruleModeOnlyDeleteMatches()
    {
        return SparqlConstructRuleSchema.oldSparqlruleModeOnlyDeleteMatches;
    }
    
    public static URI getOLDSparqlRuleSparqlConstructQuery()
    {
        return SparqlConstructRuleSchema.sparqlruleSparqlConstructQuery;
    }
    
    /**
     * @return the sparqlruleTypeUri
     */
    public static URI getSparqlConstructRuleTypeUri()
    {
        return SparqlConstructRuleSchema.sparqlruleTypeUri;
    }
    
    /**
     * @return the sparqlruleMode
     */
    public static URI getSparqlRuleMode()
    {
        return SparqlConstructRuleSchema.sparqlruleMode;
    }
    
    /**
     * @return the sparqlruleModeAddAllMatchingTriples
     */
    public static URI getSparqlRuleModeAddAllMatchingTriples()
    {
        return SparqlConstructRuleSchema.sparqlruleModeAddAllMatchingTriples;
    }
    
    /**
     * @return the sparqlruleModeOnlyDeleteMatches
     */
    public static URI getSparqlRuleModeOnlyDeleteMatches()
    {
        return SparqlConstructRuleSchema.sparqlruleModeOnlyDeleteMatches;
    }
    
    /**
     * @return the sparqlruleModeOnlyIncludeMatches
     */
    public static URI getSparqlRuleModeOnlyIncludeMatches()
    {
        return SparqlConstructRuleSchema.sparqlruleModeOnlyIncludeMatches;
    }
    
    /**
     * @return the sparqlruleSparqlConstructQueryTarget
     */
    public static URI getSparqlRuleSparqlConstructQueryTarget()
    {
        return SparqlConstructRuleSchema.sparqlruleSparqlConstructQueryTarget;
    }
    
    /**
     * @param oLDsparqlruleModeOnlyDeleteMatches
     *            the oLDsparqlruleModeOnlyDeleteMatches to set
     */
    public static void setOLDsparqlruleModeOnlyDeleteMatches(final URI oLDsparqlruleModeOnlyDeleteMatches)
    {
        SparqlConstructRuleSchema.oldSparqlruleModeOnlyDeleteMatches = oLDsparqlruleModeOnlyDeleteMatches;
    }
    
    private static void setOLDSparqlRuleSparqlConstructQuery(final URI nextSparqlruleSparqlConstructQuery)
    {
        SparqlConstructRuleSchema.sparqlruleSparqlConstructQuery = nextSparqlruleSparqlConstructQuery;
        
    }
    
    /**
     * @param sparqlConstructRuleTypeUri
     *            the sparqlruleTypeUri to set
     */
    public static void setSparqlConstructRuleTypeUri(final URI sparqlConstructRuleTypeUri)
    {
        SparqlConstructRuleSchema.sparqlruleTypeUri = sparqlConstructRuleTypeUri;
    }
    
    /**
     * @param nextSparqlruleMode
     *            the sparqlruleMode to set
     */
    public static void setSparqlRuleMode(final URI nextSparqlruleMode)
    {
        SparqlConstructRuleSchema.sparqlruleMode = nextSparqlruleMode;
    }
    
    /**
     * @param nextSparqlruleModeAddAllMatchingTriples
     *            the sparqlruleModeAddAllMatchingTriples to set
     */
    public static void setSparqlRuleModeAddAllMatchingTriples(final URI nextSparqlruleModeAddAllMatchingTriples)
    {
        SparqlConstructRuleSchema.sparqlruleModeAddAllMatchingTriples = nextSparqlruleModeAddAllMatchingTriples;
    }
    
    /**
     * @param nextSparqlruleModeOnlyDeleteMatches
     *            the sparqlruleModeOnlyDeleteMatches to set
     */
    public static void setSparqlRuleModeOnlyDeleteMatches(final URI nextSparqlruleModeOnlyDeleteMatches)
    {
        SparqlConstructRuleSchema.sparqlruleModeOnlyDeleteMatches = nextSparqlruleModeOnlyDeleteMatches;
    }
    
    /**
     * @param nextSparqlruleModeOnlyIncludeMatches
     *            the sparqlruleModeOnlyIncludeMatches to set
     */
    public static void setSparqlRuleModeOnlyIncludeMatches(final URI nextSparqlruleModeOnlyIncludeMatches)
    {
        SparqlConstructRuleSchema.sparqlruleModeOnlyIncludeMatches = nextSparqlruleModeOnlyIncludeMatches;
    }
    
    /**
     * @param sparqlruleSparqlConstructQueryTarget
     *            the sparqlruleSparqlConstructQueryTarget to set
     */
    public static void setSparqlRuleSparqlConstructQueryTarget(final URI nextSparqlruleSparqlConstructQuery)
    {
        SparqlConstructRuleSchema.sparqlruleSparqlConstructQueryTarget = nextSparqlruleSparqlConstructQuery;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public SparqlConstructRuleSchema()
    {
        this(SparqlConstructRuleSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public SparqlConstructRuleSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contextUri)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            con.begin();
            
            con.add(SparqlConstructRuleSchema.getSparqlConstructRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(SparqlConstructRuleSchema.getSparqlConstructRuleTypeUri(), RDFS.LABEL,
                    f.createLiteral("A SPARQL based normalisation rule intended to normalise in-memory RDF triples."),
                    contextUri);
            con.add(SparqlConstructRuleSchema.getSparqlConstructRuleTypeUri(), RDFS.SUBCLASSOF,
                    TransformingRuleSchema.getTransformingRuleTypeUri(), contextUri);
            con.add(SparqlConstructRuleSchema.getSparqlConstructRuleTypeUri(), RDFS.SUBCLASSOF,
                    SparqlNormalisationRuleSchema.getSparqlRuleTypeUri(), contextUri);
            
            // TODO: update schema
            con.add(SparqlConstructRuleSchema.getSparqlRuleSparqlConstructQueryTarget(), RDF.TYPE,
                    OWL.DATATYPEPROPERTY, contextUri);
            con.add(SparqlConstructRuleSchema.getSparqlRuleSparqlConstructQueryTarget(), RDFS.RANGE, RDFS.LITERAL,
                    contextUri);
            con.add(SparqlConstructRuleSchema.getSparqlRuleSparqlConstructQueryTarget(), RDFS.DOMAIN,
                    SparqlConstructRuleSchema.getSparqlConstructRuleTypeUri(), contextUri);
            con.add(SparqlConstructRuleSchema.getSparqlRuleSparqlConstructQueryTarget(),
                    RDFS.LABEL,
                    f.createLiteral("The CONSTRUCT { ... } part of the query that will be used to match against RDF triples in memory at the assigned stages, in the form of a basic graph pattern."),
                    contextUri);
            
            con.add(SparqlConstructRuleSchema.getSparqlRuleMode(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(SparqlConstructRuleSchema.getSparqlRuleMode(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(SparqlConstructRuleSchema.getSparqlRuleMode(), RDFS.DOMAIN,
                    SparqlConstructRuleSchema.getSparqlConstructRuleTypeUri(), contextUri);
            con.add(SparqlConstructRuleSchema.getSparqlRuleMode(),
                    RDFS.LABEL,
                    f.createLiteral("The mode that this normalisation rule will be used in. In the absence of SPARQL Update language support, this enables deletions and filtering based on the matched triples."),
                    contextUri);
            
            con.add(SparqlConstructRuleSchema.getSparqlRuleModeOnlyIncludeMatches(),
                    RDFS.LABEL,
                    f.createLiteral("Specifies that the SPARQL rule will be applied, and only the matches from the rule will remain in the relevant RDF triple store after the application. If the stage is after Results Import, only the results from the current provider and query will be affected."),
                    contextUri);
            
            con.add(SparqlConstructRuleSchema.getSparqlRuleModeOnlyDeleteMatches(),
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
            
            SparqlConstructRuleSchema.LOG.error("RepositoryException: " + re.getMessage());
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
    
}
