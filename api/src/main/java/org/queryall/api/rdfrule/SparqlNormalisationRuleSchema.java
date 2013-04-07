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
public class SparqlNormalisationRuleSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(SparqlNormalisationRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = SparqlNormalisationRuleSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = SparqlNormalisationRuleSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = SparqlNormalisationRuleSchema.LOG.isInfoEnabled();
    
    private static URI sparqlruleTypeUri;
    
    static URI sparqlruleSparqlWherePattern;
    
    static URI sparqlruleSparqlPrefixes;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        SparqlNormalisationRuleSchema.setSparqlRuleTypeUri(f.createURI(baseUri, "SparqlNormalisationRule"));
        SparqlNormalisationRuleSchema.setSparqlRuleSparqlWherePattern(f.createURI(baseUri, "sparqlWherePattern"));
        SparqlNormalisationRuleSchema.setSparqlRuleSparqlPrefixes(f.createURI(baseUri, "sparqlPrefixes"));
    }
    
    /**
     * A pre-instantiated schema object for SparqlNormalisationRuleSchema.
     */
    public static final QueryAllSchema SPARQL_NORMALISATION_RULE_SCHEMA = new SparqlNormalisationRuleSchema();
    
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
    
    public static void setSparqlRuleSparqlPrefixes(final URI nextSparqlruleSparqlPrefixes)
    {
        SparqlNormalisationRuleSchema.sparqlruleSparqlPrefixes = nextSparqlruleSparqlPrefixes;
    }
    
    public static void setSparqlRuleSparqlWherePattern(final URI nextSparqlruleSparqlWherePattern)
    {
        SparqlNormalisationRuleSchema.sparqlruleSparqlWherePattern = nextSparqlruleSparqlWherePattern;
    }
    
    /**
     * @param nextSparqlruleTypeUri
     *            the sparqlruleTypeUri to set
     */
    public static void setSparqlRuleTypeUri(final URI nextSparqlruleTypeUri)
    {
        SparqlNormalisationRuleSchema.sparqlruleTypeUri = nextSparqlruleTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public SparqlNormalisationRuleSchema()
    {
        this(SparqlNormalisationRuleSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public SparqlNormalisationRuleSchema(final String nextName)
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
            
            con.add(SparqlNormalisationRuleSchema.getSparqlRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(SparqlNormalisationRuleSchema.getSparqlRuleTypeUri(), RDFS.LABEL,
                    f.createLiteral("A SPARQL based normalisation rule intended to normalise in-memory RDF triples."),
                    contextUri);
            con.add(SparqlNormalisationRuleSchema.getSparqlRuleTypeUri(), RDFS.SUBCLASSOF,
                    TransformingRuleSchema.getTransformingRuleTypeUri(), contextUri);
            
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
            
            SparqlNormalisationRuleSchema.LOG.error("RepositoryException: " + re.getMessage());
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
