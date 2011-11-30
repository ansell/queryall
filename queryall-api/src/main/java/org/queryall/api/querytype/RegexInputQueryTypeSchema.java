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
    private static final Logger log = LoggerFactory.getLogger(RegexInputQueryTypeSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = RegexInputQueryTypeSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = RegexInputQueryTypeSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RegexInputQueryTypeSchema.log.isInfoEnabled();
    
    private static URI regexQueryTypeUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.QUERY.getBaseURI();
        
        RegexInputQueryTypeSchema.setRegexQueryTypeUri(f.createURI(baseUri, "RegexInputQuery"));
        RegexInputQueryTypeSchema.setQueryInputRegex(f.createURI(baseUri, "inputRegex"));
        
    }
    
    static URI queryInputRegex;
    
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
     * @param queryInputRegex
     *            the queryInputRegex to set
     */
    public static void setQueryInputRegex(final URI queryInputRegex)
    {
        RegexInputQueryTypeSchema.queryInputRegex = queryInputRegex;
    }
    
    private static void setRegexQueryTypeUri(final URI regexQueryTypeUri)
    {
        RegexInputQueryTypeSchema.regexQueryTypeUri = regexQueryTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name
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
    public boolean schemaToRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            final URI contextKeyUri = keyToUse;
            con.setAutoCommit(false);
            
            // TODO: add label for this type
            con.add(RegexInputQueryTypeSchema.getRegexInputQueryTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            con.add(RegexInputQueryTypeSchema.getRegexInputQueryTypeUri(), RDFS.SUBCLASSOF, QueryTypeSchema.getQueryTypeUri(), contextKeyUri);
            
            con.add(RegexInputQueryTypeSchema.getQueryInputRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(RegexInputQueryTypeSchema.getQueryInputRegex(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(RegexInputQueryTypeSchema.getQueryInputRegex(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contextKeyUri);
            con.add(RegexInputQueryTypeSchema.getQueryInputRegex(),
                    RDFS.LABEL,
                    f.createLiteral("This input regex contains matching groups that correlate with the input_NN tags, where NN is the index of the matching group."),
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
            
            RegexInputQueryTypeSchema.log.error("RepositoryException: " + re.getMessage());
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
