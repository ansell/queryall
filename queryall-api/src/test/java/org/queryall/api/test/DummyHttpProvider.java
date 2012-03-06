/**
 * 
 */
package org.queryall.api.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.HttpProvider;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.Provider;
import org.queryall.api.utils.ProfileIncludeExclude;
import org.queryall.api.utils.QueryAllNamespaces;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public class DummyHttpProvider extends DummyProvider implements HttpProvider
{
    
    private final List<String> endpointUrls = new ArrayList<String>();
    private String acceptHeaderString;

    /**
     * 
     */
    public DummyHttpProvider()
    {
        super();
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.provider.HttpProvider#addEndpointUrl(java.lang.String)
     */
    @Override
    public void addEndpointUrl(String endpointUrl)
    {
        this.endpointUrls.add(endpointUrl);
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.provider.HttpProvider#getAcceptHeaderString(java.lang.String)
     */
    @Override
    public String getAcceptHeaderString(String defaultAcceptHeader)
    {
        if(this.acceptHeaderString != null)
        {
            return this.acceptHeaderString;
        }
        else
        {
            return defaultAcceptHeader;
        }
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.provider.HttpProvider#getEndpointUrls()
     */
    @Override
    public Collection<String> getEndpointUrls()
    {
        return Collections.unmodifiableList(this.endpointUrls);
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.provider.HttpProvider#hasEndpointUrl()
     */
    @Override
    public boolean hasEndpointUrl()
    {
        return !this.endpointUrls.isEmpty();
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.provider.HttpProvider#isHttpGetUrl()
     */
    @Override
    public boolean isHttpGetUrl()
    {
        return this.getEndpointMethod().equals(HttpProviderSchema.getProviderHttpGetUrl());
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.provider.HttpProvider#resetEndpointUrls()
     */
    @Override
    public boolean resetEndpointUrls()
    {
        this.endpointUrls.clear();
        
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.provider.HttpProvider#setAcceptHeaderString(java.lang.String)
     */
    @Override
    public void setAcceptHeaderString(String acceptHeaderString)
    {
        this.acceptHeaderString = acceptHeaderString;
    }
    
}
