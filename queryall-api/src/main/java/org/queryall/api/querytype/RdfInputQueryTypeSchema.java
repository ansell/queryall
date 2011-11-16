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
    private static final Logger log = LoggerFactory.getLogger(RdfInputQueryTypeSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = RdfInputQueryTypeSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = RdfInputQueryTypeSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RdfInputQueryTypeSchema.log.isInfoEnabled();
    
    private static URI rdfInputQueryTypeUri;
    private static URI querySparqlInputSelect;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.QUERY.getBaseURI();
        
        RdfInputQueryTypeSchema.setRdfInputQueryTypeUri(f.createURI(baseUri, "RdfInputQuery"));
        RdfInputQueryTypeSchema.setQuerySparqlInputSelect(f.createURI(baseUri, "sparqlInputSelect"));
    }
    
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
     * Default constructor, uses the name of this class as the name
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
    public boolean schemaToRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            final URI contextKeyUri = keyToUse;
            con.setAutoCommit(false);
            
            con.add(RdfInputQueryTypeSchema.getRdfInputQueryTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            
            con.add(RdfInputQueryTypeSchema.getQuerySparqlInputSelect(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(RdfInputQueryTypeSchema.getQuerySparqlInputSelect(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(RdfInputQueryTypeSchema.getQuerySparqlInputSelect(), RDFS.DOMAIN,
                    QueryTypeSchema.getQueryTypeUri(), contextKeyUri);
            con.add(RdfInputQueryTypeSchema.getQuerySparqlInputSelect(),
                    RDFS.LABEL,
                    f.createLiteral("The SPARQL input select statement that will convert the input RDF document into named parameters for use in templates."),
                    contextKeyUri);
            con.add(RdfInputQueryTypeSchema.getQuerySparqlInputSelect(),
                    RDFS.COMMENT,
                    f.createLiteral("For compatibility with the RegexInput style, the parameters will need to stick to using input_NN where NN is the index of the parameter in the equivalent HTTP REST Regex match pattern. Any parameters of the same name that are defined in the HTTP GET query parameters will override these bindings. Any bindings that represent public identifiers or namespace identifiers must be defined in the relevant publicIdentifierTag etc., lists."),
                    contextKeyUri);
            
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
            
            RdfInputQueryTypeSchema.log.error("RepositoryException: " + re.getMessage());
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
