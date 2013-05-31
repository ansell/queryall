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
public class RegexInputQueryTypeSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(RegexInputQueryTypeSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = RegexInputQueryTypeSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = RegexInputQueryTypeSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = RegexInputQueryTypeSchema.LOG.isInfoEnabled();
    
    private static URI regexQueryTypeUri;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.QUERY.getBaseURI();
        
        RegexInputQueryTypeSchema.setRegexQueryTypeUri(f.createURI(baseUri, "RegexInputQuery"));
        RegexInputQueryTypeSchema.setQueryInputRegex(f.createURI(baseUri, "inputRegex"));
        
    }
    
    static URI queryInputRegex;
    
    /**
     * The pre-instantiated schema object for RegexInputQueryTypeSchema.
     */
    public static final QueryAllSchema REGEX_INPUT_QUERY_TYPE_SCHEMA = new RegexInputQueryTypeSchema();
    
    /**
     * @return the queryInputRegex
     */
    public static URI getQueryInputRegex()
    {
        return RegexInputQueryTypeSchema.queryInputRegex;
    }
    
    public static URI getRegexInputQueryTypeUri()
    {
        return RegexInputQueryTypeSchema.regexQueryTypeUri;
    }
    
    /**
     * @param nextQueryInputRegex
     *            the queryInputRegex to set
     */
    public static void setQueryInputRegex(final URI nextQueryInputRegex)
    {
        RegexInputQueryTypeSchema.queryInputRegex = nextQueryInputRegex;
    }
    
    private static void setRegexQueryTypeUri(final URI nextRegexQueryTypeUri)
    {
        RegexInputQueryTypeSchema.regexQueryTypeUri = nextRegexQueryTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public RegexInputQueryTypeSchema()
    {
        this(RegexInputQueryTypeSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public RegexInputQueryTypeSchema(final String nextName)
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
            
            // TODO: add label for this type
            con.add(RegexInputQueryTypeSchema.getRegexInputQueryTypeUri(), RDF.TYPE, OWL.CLASS, contexts);
            con.add(RegexInputQueryTypeSchema.getRegexInputQueryTypeUri(), RDFS.SUBCLASSOF,
                    QueryTypeSchema.getQueryTypeUri(), contexts);
            
            con.add(RegexInputQueryTypeSchema.getQueryInputRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(RegexInputQueryTypeSchema.getQueryInputRegex(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(RegexInputQueryTypeSchema.getQueryInputRegex(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contexts);
            con.add(RegexInputQueryTypeSchema.getQueryInputRegex(),
                    RDFS.LABEL,
                    f.createLiteral("This input regex contains matching groups that correlate with the input_NN tags, where NN is the index of the matching group."),
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
            
            RegexInputQueryTypeSchema.LOG.error("RepositoryException: " + re.getMessage());
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
