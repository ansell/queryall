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
import org.queryall.api.provider.SparqlProviderSchema;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class HttpOnlyProviderImpl extends HttpProviderImpl
{
    private static final Set<URI> HTTP_ONLY_PROVIDER_IMPL_TYPES = new HashSet<URI>();

    static
    {
        HTTP_ONLY_PROVIDER_IMPL_TYPES.add(ProviderSchema.getProviderTypeUri());
        HTTP_ONLY_PROVIDER_IMPL_TYPES.add(HttpProviderSchema.getProviderHttpTypeUri());
    }
    
    public static Set<URI> httpOnlyTypes()
    {
        return HTTP_ONLY_PROVIDER_IMPL_TYPES;
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
