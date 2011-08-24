/**
 * 
 */
package org.queryall.impl.querytype;

import java.util.Collection;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.querytype.QueryTypeParser;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryTypeImplParser implements QueryTypeParser
{
    /**
     * 
     */
    public QueryTypeImplParser()
    {

    }
    
    @Override
    public QueryType createObject(Collection<Statement> rdfStatements, URI subjectKey, int modelVersion)
        throws IllegalArgumentException
    {
        try
        {
            return new QueryTypeImpl(rdfStatements, subjectKey, modelVersion);
        }
        catch(OpenRDFException ex)
        {
            throw new IllegalArgumentException("Could not parse the given RDF statements into a query type", ex);
        }
    }
    
}
