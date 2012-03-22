package org.queryall.api.services;

import java.util.Collection;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.base.BaseQueryAllInterface;

/**
 * Parses RDF configurations to create instances of the BaseQueryAllInterface interface.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface QueryAllParser<T extends BaseQueryAllInterface>
{
    
    /**
     * Creates a new instance of a QueryAll object based on a search for relevant statements in the
     * given rdfStatements collection with the subjectKey.
     * 
     * @param rdfStatements
     *            The RDF statements that contain the definition for the object with URI
     *            "subjectKey", specified using the conventions from API version "modelVersion"
     * @param subjectKey
     *            The URI to use to identify the object in the given statements, as there may not be
     *            a simple way to determine which object in the statements is to be created
     *            otherwise.
     * @param modelVersion
     *            The version of the configuration API that was used to describe the object in the
     *            given RDF statements. Provides backwards compatibility when parsing old
     *            configurations.
     * @return A typed java object based on the given statements.
     * @throws IllegalArgumentException
     *             If modelVersion is not supported by this implementation.
     */
    T createObject(Collection<Statement> rdfStatements, URI subjectKey, int modelVersion)
        throws IllegalArgumentException;
    
}