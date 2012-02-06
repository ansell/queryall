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
     * The modelVersion is given to provide backwards compatibility when parsing old configurations.
     * 
     * If modelVersion is not supported, an IllegalArgumentException may be thrown.
     * 
     * @param rdfStatements
     * @param subjectKey
     * @param modelVersion
     * @return
     */
    T createObject(Collection<Statement> rdfStatements, URI subjectKey, int modelVersion)
        throws IllegalArgumentException;
    
}