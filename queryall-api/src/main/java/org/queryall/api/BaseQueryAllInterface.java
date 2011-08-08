package org.queryall.api;

import java.util.Collection;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.queryall.api.utils.QueryAllNamespaces;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface BaseQueryAllInterface
{
    /**
     * Changes the schema used to represent objects of this class into RDF, and inserts the relevant
     * triples into myRepository
     **/
    // public static abstract boolean schemaToRdf(Repository myRepository, String keyToUse, int
    // modelVersion) throws OpenRDFException;
    
    // <T extends BaseQueryAllInterface> T fromRdf(Collection<Statement> relevantStatements, URI
    // keyToUse, int modelVerson) throws OpenRDFException;
    
    void addUnrecognisedStatement(Statement unrecognisedStatement);
    
    URI getCurationStatus();
    
    /**
     * Returns the namespace used for objects of this type by default. For example, it would
     * correspond to namespaceString in the following: http://myhost.org/namespaceString:identifier
     **/
    QueryAllNamespaces getDefaultNamespace();
    
    Collection<URI> getElementTypes();
    
    /**
     * Returns the org.openrdf.model.URI string key for the object, and this can optionally be used
     * in the toRdf method
     **/
    URI getKey();
    
    String getTitle();
    
    Collection<Statement> getUnrecognisedStatements();
    
    void setCurationStatus(URI curationStatus);
    
    /**
     * Sets the URI for the object. If it is not a valid org.openrdf.model.URI an
     * IllegalArgumentException is thrown.
     **/
    void setKey(String nextKey) throws IllegalArgumentException;
    
    /**
     * Sets the org.openrdf.model.URI for the object.
     **/
    void setKey(URI nextKey);
    
    void setTitle(String title);
    
    /**
     * Returns the representation of the object as an XHTML marked up string for display only
     **/
    String toHtml();
    
    /**
     * Returns the input fields in XHTML as a string for use in forms designed to edit this object
     **/
    String toHtmlFormBody();
    
    /**
     * Changes the object into RDF, and inserts the relevant triples into myRepository using the URI
     * keyToUse as the context URI
     **/
    boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException;
}
