/**
 * 
 */
package org.queryall.impl.querytype;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.querytype.NoInputQueryType;
import org.queryall.api.querytype.NoInputQueryTypeSchema;
import org.queryall.api.querytype.QueryTypeSchema;
import org.queryall.api.querytype.RdfOutputQueryTypeSchema;

/**
 * Matches all inputs, but processes none of them. It always returns true from
 * matchesQueryParameters and always returns an empty Map from matchesForQueryParameters.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class NoInputQueryTypeImpl extends QueryTypeImpl implements NoInputQueryType
{
    private static final Set<URI> NO_INPUT_QUERY_TYPE_IMPL_TYPES = new HashSet<URI>();
    
    static
    {
        NoInputQueryTypeImpl.NO_INPUT_QUERY_TYPE_IMPL_TYPES.add(QueryTypeSchema.getQueryTypeUri());
        NoInputQueryTypeImpl.NO_INPUT_QUERY_TYPE_IMPL_TYPES.add(NoInputQueryTypeSchema.getNoInputQueryTypeUri());
        NoInputQueryTypeImpl.NO_INPUT_QUERY_TYPE_IMPL_TYPES.add(RdfOutputQueryTypeSchema.getRdfOutputQueryTypeUri());
    }
    
    public static Set<URI> myTypes()
    {
        return NoInputQueryTypeImpl.NO_INPUT_QUERY_TYPE_IMPL_TYPES;
    }
    
    /**
     * 
     */
    public NoInputQueryTypeImpl()
    {
        super();
    }
    
    /**
     * @param inputStatements
     * @param keyToUse
     * @param modelVersion
     * @throws OpenRDFException
     */
    public NoInputQueryTypeImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
    }
    
    @Override
    public Set<URI> getElementTypes()
    {
        return NoInputQueryTypeImpl.NO_INPUT_QUERY_TYPE_IMPL_TYPES;
    }
    
    /**
     * Returns an empty map to indicate that there are no specific matches
     */
    @Override
    public Map<String, List<String>> matchesForQueryParameters(final Map<String, String> queryParameters)
    {
        return Collections.emptyMap();
    }
    
    /**
     * Always returns true to match all query parameters
     */
    @Override
    public boolean matchesQueryParameters(final Map<String, String> queryString)
    {
        return true;
    }
    
    @Override
    public Map<String, Object> parseInputs(final Map<String, Object> inputParameterMap)
    {
        return inputParameterMap;
    }
    
}
