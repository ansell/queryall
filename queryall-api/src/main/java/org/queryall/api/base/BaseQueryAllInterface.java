package org.queryall.api.base;

import java.util.Collection;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.queryall.api.utils.QueryAllNamespaces;

/**
 * The base of all QueryAll objects.
 * 
 * Contains the basic elements necessary to identify objects and keep track of RDF statements from
 * the objects definitions that were not recognised, but need to be stored for future serialisation
 * of the object.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface BaseQueryAllInterface
{
    /**
     * Adds an unrecognised statement to the internal list for either parsing by a more specific
     * subclass or for storage to include in the serialised version in future
     * 
     * @param unrecognisedStatement
     */
    void addUnrecognisedStatement(Statement unrecognisedStatement);
    
    /**
     * 
     * @return A URI indicating the current curation status for this item.
     */
    URI getCurationStatus();
    
    /**
     * Returns the namespace used for objects of this type by default. For example, it would
     * correspond to namespaceString in the following: http://myhost.org/namespaceString:identifier
     **/
    QueryAllNamespaces getDefaultNamespace();
    
    /**
     * 
     * @return The description, mapping to the RDF Schema Comment property.
     */
    String getDescription();
    
    /**
     * This list may not be the only list of URIs that a class recognises, but it should map to
     * those URIs which are relevant for the currently stored object
     * 
     * @return The set of URIs that this object recognises for the current object.
     */
    Set<URI> getElementTypes();
    
    /**
     * Returns the org.openrdf.model.URI string key for the object, and this can optionally be used
     * in the toRdf method
     **/
    URI getKey();
    
    /**
     * 
     * @return The title for the object, mapping to the Dublin Core Title property
     */
    String getTitle();
    
    /**
     * Return the collection of statements that are currently unrecognised.
     * 
     * @return An unmodifiable collection of statements representing the currently unrecognised
     *         statements
     */
    Collection<Statement> getUnrecognisedStatements();
    
    /**
     * This method resets the internal unrecognised statements collection to empty, as is necessary
     * before a subclass starts parsing the remaining unrecognised statements
     * 
     * @return The current list of unrecognised statements before the reset
     */
    Collection<Statement> resetUnrecognisedStatements();
    
    void setCurationStatus(URI curationStatus);
    
    void setDescription(String description);
    
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
     * Changes the object into RDF, and inserts the relevant triples into myRepository using the
     * URIs in contextUris as the context URIs.
     * 
     * The Configuration API version to attempt to use for the rdf export is given as modelVersion.
     * 
     * @param myRepository
     * @param modelVersion
     * @param contextUris
     * @return
     * @throws OpenRDFException
     */
    boolean toRdf(Repository myRepository, int modelVersion, URI... contextUris) throws OpenRDFException;
}
