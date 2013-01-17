/**
 * 
 */
package org.queryall.api.querytype;

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
public class RdfInputQueryTypeSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(RdfInputQueryTypeSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = RdfInputQueryTypeSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = RdfInputQueryTypeSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = RdfInputQueryTypeSchema.LOG.isInfoEnabled();
    
    private static URI rdfInputQueryTypeUri;
    private static URI querySparqlInputSelect;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.QUERY.getBaseURI();
        
        RdfInputQueryTypeSchema.setRdfInputQueryTypeUri(f.createURI(baseUri, "RdfInputQuery"));
        RdfInputQueryTypeSchema.setQuerySparqlInputSelect(f.createURI(baseUri, "sparqlInputSelect"));
    }
    
    /**
     * A pre-instantiated schema object for RdfInputQueryTypeSchema.
     */
    public static final QueryAllSchema RDF_INPUT_QUERY_TYPE_SCHEMA = new RdfInputQueryTypeSchema();
    
    public static URI getQuerySparqlInputSelect()
    {
        return RdfInputQueryTypeSchema.querySparqlInputSelect;
    }
    
    /**
     * @return the queryTypeUri
     */
    public static URI getRdfInputQueryTypeUri()
    {
        return RdfInputQueryTypeSchema.rdfInputQueryTypeUri;
    }
    
    public static void setQuerySparqlInputSelect(final URI sparqlInputSelect)
    {
        RdfInputQueryTypeSchema.querySparqlInputSelect = sparqlInputSelect;
    }
    
    /**
     * @param queryTypeUri
     *            the queryTypeUri to set
     */
    public static void setRdfInputQueryTypeUri(final URI queryTypeUri)
    {
        RdfInputQueryTypeSchema.rdfInputQueryTypeUri = queryTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public RdfInputQueryTypeSchema()
    {
        this(RdfInputQueryTypeSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public RdfInputQueryTypeSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contexts)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            con.begin();
            
            con.add(RdfInputQueryTypeSchema.getRdfInputQueryTypeUri(), RDF.TYPE, OWL.CLASS, contexts);
            con.add(RdfInputQueryTypeSchema.getRdfInputQueryTypeUri(), RDFS.SUBCLASSOF,
                    QueryTypeSchema.getQueryTypeUri(), contexts);
            
            con.add(RdfInputQueryTypeSchema.getQuerySparqlInputSelect(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(RdfInputQueryTypeSchema.getQuerySparqlInputSelect(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(RdfInputQueryTypeSchema.getQuerySparqlInputSelect(), RDFS.DOMAIN,
                    QueryTypeSchema.getQueryTypeUri(), contexts);
            con.add(RdfInputQueryTypeSchema.getQuerySparqlInputSelect(),
                    RDFS.LABEL,
                    f.createLiteral("The SPARQL input select statement that will convert the input RDF document into named parameters for use in templates."),
                    contexts);
            con.add(RdfInputQueryTypeSchema.getQuerySparqlInputSelect(),
                    RDFS.COMMENT,
                    f.createLiteral("For compatibility with the RegexInput style, the parameters will need to stick to using input_NN where NN is the index of the parameter in the equivalent HTTP REST Regex match pattern. Any parameters of the same name that are defined in the HTTP GET query parameters will override these bindings. Any bindings that represent public identifiers or namespace identifiers must be defined in the relevant publicIdentifierTag etc., lists."),
                    contexts);
            
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
            
            RdfInputQueryTypeSchema.LOG.error("RepositoryException: " + re.getMessage());
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
