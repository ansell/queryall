package org.queryall.api.base;

import java.util.Collection;
import java.util.Set;

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
    void addUnrecognisedStatement(Statement unrecognisedStatement);
    
    URI getCurationStatus();
    
    /**
     * Returns the namespace used for objects of this type by default. For example, it would
     * correspond to namespaceString in the following: http://myhost.org/namespaceString:identifier
     **/
    QueryAllNamespaces getDefaultNamespace();
    
    Set<URI> getElementTypes();
    
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
     * Changes the object into RDF, and inserts the relevant triples into myRepository using the URI
     * contextUri as the context URI.
     * 
     * The Configuration API version to attempt to use for the rdf export is given as modelVersion.
     **/
    boolean toRdf(Repository myRepository, URI contextUri, int modelVersion) throws OpenRDFException;
}
