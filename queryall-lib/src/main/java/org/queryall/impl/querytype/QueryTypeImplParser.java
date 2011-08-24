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
    @Override
    public QueryType createObject(final Collection<Statement> rdfStatements, final URI subjectKey,
            final int modelVersion) throws IllegalArgumentException
    {
        try
        {
            return new QueryTypeImpl(rdfStatements, subjectKey, modelVersion);
        }
        catch(final OpenRDFException ex)
        {
            throw new IllegalArgumentException("Could not parse the given RDF statements into a query type", ex);
        }
    }
}
