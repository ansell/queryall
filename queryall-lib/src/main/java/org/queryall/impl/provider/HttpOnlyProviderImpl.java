/**
 * 
 */
package org.queryall.impl.provider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.ProviderSchema;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class HttpOnlyProviderImpl extends HttpProviderImpl
{
    
    public static Set<URI> httpOnlyTypes()
    {
        final Set<URI> results = new HashSet<URI>();
        
        results.add(ProviderSchema.getProviderTypeUri());
        results.add(HttpProviderSchema.getProviderHttpTypeUri());
        
        return results;
    }
    
    /**
     * 
     */
    public HttpOnlyProviderImpl()
    {
    }
    
    /**
     * @param inputStatements
     * @param keyToUse
     * @param modelVersion
     * @throws OpenRDFException
     */
    public HttpOnlyProviderImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
    }
    
}
