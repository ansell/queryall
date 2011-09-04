package org.queryall.api.querytype;

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

public class RegexInputQueryTypeSchema
{
    private static final Logger log = LoggerFactory.getLogger(RegexInputQueryTypeSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = RegexInputQueryTypeSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = RegexInputQueryTypeSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RegexInputQueryTypeSchema.log.isInfoEnabled();
    
    private static URI regexQueryTypeUri;
    

    public static URI getRegexInputQueryTypeUri()
    {
        return regexQueryTypeUri;
    }
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.QUERY.getBaseURI();
        
        RegexInputQueryTypeSchema.setRegexQueryTypeUri(f.createURI(baseUri, "Query"));
        RegexInputQueryTypeSchema.setQueryInputRegex(f.createURI(baseUri, "inputRegex"));

    }

    private static void setRegexQueryTypeUri(URI regexQueryTypeUri)
    {
        RegexInputQueryTypeSchema.regexQueryTypeUri = regexQueryTypeUri;
    }

    /**
     * @param queryInputRegex
     *            the queryInputRegex to set
     */
    public static void setQueryInputRegex(final URI queryInputRegex)
    {
        RegexInputQueryTypeSchema.queryInputRegex = queryInputRegex;
    }

    static URI queryInputRegex;


    /**
     * @return the queryInputRegex
     */
    public static URI getQueryInputRegex()
    {
        return queryInputRegex;
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
            throws OpenRDFException
        {
            QueryTypeSchema.schemaToRdf(myRepository, keyToUse, modelVersion);
        
            final RepositoryConnection con = myRepository.getConnection();
            
            final ValueFactory f = Constants.valueFactory;
            
            try
            {
                final URI contextKeyUri = keyToUse;
                con.setAutoCommit(false);
                
                // TODO: add label for this type
                con.add(RegexInputQueryTypeSchema.getRegexInputQueryTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
                
                con.add(RegexInputQueryTypeSchema.getQueryInputRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
                con.add(RegexInputQueryTypeSchema.getQueryInputRegex(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
                con.add(RegexInputQueryTypeSchema.getQueryInputRegex(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(), contextKeyUri);
                con.add(RegexInputQueryTypeSchema.getQueryInputRegex(), RDFS.LABEL, f.createLiteral("This input regex contains matching groups that correlate with the input_NN tags, where NN is the index of the matching group."), contextKeyUri);
                
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
