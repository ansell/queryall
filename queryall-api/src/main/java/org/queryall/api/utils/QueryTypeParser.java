/**
 * 
 */
package org.queryall.api.utils;

import java.util.Collection;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.QueryType;

/**
 * Parses RDF configurations to create instances of the QueryType interface 
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface QueryTypeParser 
{
    /**
     * Creates a new instance of QueryType based on a search for relevant QueryType statements in the given rdfStatements collection with the subjectKey.
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
    QueryType createQueryType(Collection<Statement> rdfStatements, URI subjectKey, int modelVersion) throws IllegalArgumentException;
}
