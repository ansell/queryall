/**
 * 
 */
package org.queryall.impl.rdfrule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.util.Locator;
import com.hp.hpl.jena.util.TypedStream;

/**
 * Loads QueryAll Schemas based on their known URIs
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class QueryAllSchemaLocatorClass implements Locator
{
    private static final Logger log = LoggerFactory.getLogger(QueryAllSchemaLocatorClass.class);
    private String internalString = "";
    
    /**
     * Uses the given repository to generate an in memory Serialised RDF document representing the
     * schema
     */
    public QueryAllSchemaLocatorClass(final Repository allSchemas)
    {
        RepositoryConnection schemaConnection = null;
        
        try
        {
            schemaConnection = allSchemas.getConnection();
            
            // List<Statement> completeList = schemaConnection.getStatements(null, null, null,
            // true).asList();
            
            final RdfStringOutputStream outputStream = new RdfStringOutputStream();
            
            schemaConnection.exportStatements(null, null, null, true, new RDFXMLWriter(outputStream));
            
            this.internalString = outputStream.toString();
            
            outputStream.close();
        }
        catch(final RepositoryException e)
        {
            QueryAllSchemaLocatorClass.log.error("Could not get the list of schemas as a list of statements", e);
        }
        catch(final RDFHandlerException e)
        {
            QueryAllSchemaLocatorClass.log.error("Error outputting the statements to RDF", e);
        }
        finally
        {
            if(schemaConnection != null)
            {
                try
                {
                    schemaConnection.close();
                }
                catch(final RepositoryException e)
                {
                    QueryAllSchemaLocatorClass.log.error("Found exception closing connection", e);
                }
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.hp.hpl.jena.util.Locator#getName()
     */
    @Override
    public String getName()
    {
        return "QueryAllSchemaLocatorClass";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.hp.hpl.jena.util.Locator#open(java.lang.String)
     */
    @Override
    public TypedStream open(String filenameOrURI)
    {
        QueryAllSchemaLocatorClass.log.info("filenameOrURI=" + filenameOrURI);
        
        if(filenameOrURI == null)
        {
            throw new IllegalArgumentException("Cannot use a null filenameOrURI");
        }
        
        if(filenameOrURI.startsWith("queryall:"))
        {
            filenameOrURI = QueryAllNamespaces.getPrefix() + filenameOrURI.substring("queryall:".length());
        }
        
        // NOTE: We do this for efficiency
        // If people want to define other URIs for schema URIs, then we should check the allSchemas
        // repository instead of doing this check
        final QueryAllNamespaces match = QueryAllNamespaces.matchBaseUri(filenameOrURI);
        
        if(match == null)
        {
            QueryAllSchemaLocatorClass.log.info("Did not find a queryall match for filenameOrURI=" + filenameOrURI);
            return null;
        }
        
        final InputStream in = new ByteArrayInputStream(this.internalString.getBytes(Charset.forName("UTF-8")));
        
        return new TypedStream(in);
    }
    
}
