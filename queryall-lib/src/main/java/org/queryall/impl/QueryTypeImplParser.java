/**
 * 
 */
package org.queryall.impl;

import java.util.Collection;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.QueryType;
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
    
    /* (non-Javadoc)
     * @see org.queryall.api.utils.QueryTypeParser#createQueryType(java.util.Collection, org.openrdf.model.URI, int)
     */
    @Override
    public QueryType createQueryType(Collection<Statement> rdfStatements, URI subjectKey, int modelVersion)
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
