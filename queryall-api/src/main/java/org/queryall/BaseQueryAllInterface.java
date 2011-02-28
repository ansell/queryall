package org.queryall;

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
    
    /**
     * Changes the object into RDF, and inserts the relevant triples into myRepository
     **/
    public abstract boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException;
    
    /**
    * Returns the namespace used for objects of this type by default. For example, it would correspond to namespaceString in the following:
    * http://myhost.org/namespaceString:identifier
     **/
    public abstract String getDefaultNamespace();
    
    
    public abstract String getElementType();
    
    
    /**
     * Returns the URI string key for the object, and this can optionally be used in the toRdf method
     **/
    public abstract URI getKey();
    
    /**
     * Sets the URI for the object. If it is not a valid URI an IllegalArgumentException is thrown.
     **/
    public abstract void setKey(String nextKey) throws IllegalArgumentException;

    /**
     * Sets the URI for the object. If it is not a valid URI an IllegalArgumentException is thrown.
     **/
    public abstract void setKey(URI nextKey);
    
    public abstract URI getCurationStatus();
    
    public abstract void setCurationStatus(URI curationStatus);
    
    /**
     * Returns the input fields in XHTML as a string for use in forms designed to edit this object
     **/
    public abstract String toHtmlFormBody();
    
    /**
     * Returns the representation of the object as an XHTML marked up string for display only
     **/
    public abstract String toHtml();
    
    public abstract void addUnrecognisedStatement(Statement unrecognisedStatement);
    
    public abstract Collection<Statement> getUnrecognisedStatements();
}
