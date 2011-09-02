/**
 * 
 */
package org.queryall.impl.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.ProviderSchema;

/**
 * @author karina
 * 
 */
public class HttpOnlyProviderImpl extends HttpProviderImpl
{
    
    public static List<URI> httpOnlyTypes()
    {
        final List<URI> results = new ArrayList<URI>(2);
        
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
