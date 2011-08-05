package org.queryall.api;

import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.model.URI;
import org.openrdf.model.Statement;
import java.util.Collection;

public interface BaseQueryAllInterface
{
    /**
     * Changes the schema used to represent objects of this class into RDF, and inserts the relevant triples into myRepository
     **/
    //public static abstract boolean schemaToRdf(Repository myRepository, String keyToUse, int modelVersion) throws OpenRDFException;
    
    //<T extends BaseQueryAllInterface> T fromRdf(Collection<Statement> relevantStatements, URI keyToUse, int modelVerson)  throws OpenRDFException;
    
    /**
     * Changes the object into RDF, and inserts the relevant triples into myRepository using the URI keyToUse as the context URI
     **/
    boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException;
    
    /**
    * Returns the namespace used for objects of this type by default. For example, it would correspond to namespaceString in the following:
    * http://myhost.org/namespaceString:identifier
     **/
    String getDefaultNamespace();
    
    
    Collection<URI> getElementTypes();
    
    
    /**
     * Returns the org.openrdf.model.URI string key for the object, and this can optionally be used in the toRdf method
     **/
    URI getKey();
    
    /**
     * Sets the URI for the object. If it is not a valid org.openrdf.model.URI an IllegalArgumentException is thrown.
     **/
    void setKey(String nextKey) throws IllegalArgumentException;

    /**
     * Sets the org.openrdf.model.URI for the object.
     **/
    void setKey(URI nextKey);
    
    URI getCurationStatus();
    
    void setCurationStatus(URI curationStatus);
    
    /**
     * Returns the input fields in XHTML as a string for use in forms designed to edit this object
     **/
    String toHtmlFormBody();
    
    /**
     * Returns the representation of the object as an XHTML marked up string for display only
     **/
    String toHtml();
        
    void addUnrecognisedStatement(Statement unrecognisedStatement);
    
    Collection<Statement> getUnrecognisedStatements();

    void setTitle(String title);

    String getTitle();
}
