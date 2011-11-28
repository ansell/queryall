/**
 * 
 */
package org.queryall.api.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.queryall.api.profile.Profile;
import org.queryall.api.profile.ProfileSchema;
import org.queryall.api.provider.Provider;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.utils.QueryAllNamespaces;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public final class DummyProvider implements Provider
{
    private URI profileIncludeExcludeOrder = ProfileSchema.getProfileIncludeExcludeOrderUndefinedUri();
    private String title = "";
    private URI key = null;
    private String description = "";
    private URI curationStatus = null;
    private Collection<Statement> unrecognisedStatements = new ArrayList<Statement>();
    private Collection<URI> includedInQueryTypes = new ArrayList<URI>();
    private Collection<URI> namespaces = new ArrayList<URI>();
    private boolean isDefault = false;
    private Collection<URI> normalisations = new ArrayList<URI>();
    private Collection<URI> queryTypes = new ArrayList<URI>();
    private String assumedContentType;
    private URI endpointMethod;
    private URI redirectOrProxy;
    
    /**
     * 
     */
    public DummyProvider()
    {

    }
    
    @Override
    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        this.unrecognisedStatements.add(unrecognisedStatement);
    }
    
    @Override
    public URI getCurationStatus()
    {
        return this.curationStatus;
    }
    
    @Override
    public QueryAllNamespaces getDefaultNamespace()
    {
        return QueryAllNamespaces.RDFRULE;
    }
    
    @Override
    public String getDescription()
    {
        return this.description;
    }
    
    @Override
    public Set<URI> getElementTypes()
    {
        Set<URI> types = new HashSet<URI>();
        
        types.add(ProviderSchema.getProviderTypeUri());
        
        return types;
    }
    
    @Override
    public URI getKey()
    {
        return this.key;
    }
    
    @Override
    public String getTitle()
    {
        return this.title;
    }
    
    @Override
    public Collection<Statement> getUnrecognisedStatements()
    {
        return this.unrecognisedStatements;
    }
    
    @Override
    public Collection<Statement> resetUnrecognisedStatements()
    {
        Collection<Statement> unrecognisedStatementsTemp = new ArrayList<Statement>(this.unrecognisedStatements);
        
        this.unrecognisedStatements = new ArrayList<Statement>();
        
        return unrecognisedStatementsTemp;
    }
    
    @Override
    public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    @Override
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    @Override
    public void setKey(String nextKey) throws IllegalArgumentException
    {
        setKey(new ValueFactoryImpl().createURI(nextKey));
    }
    
    @Override
    public void setKey(URI nextKey)
    {
        this.key = nextKey;
    }
    
    @Override
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    /**
     * NOTE: This is a dummy implementation. Always returns true with no sideeffects.
     * @param myRepository
     * @param modelVersion
     * @param contextUris
     * @return
     * @throws OpenRDFException
     */
    @Override
    public boolean toRdf(Repository myRepository, int modelVersion, URI... contextUris) throws OpenRDFException
    {
        return true;
    }
    
    @Override
    public URI getProfileIncludeExcludeOrder()
    {
        return this.profileIncludeExcludeOrder;
    }
    
    /**
     * NOTE: This is a dummy class, always returns true
     * 
     * @param orderedProfileList
     * @param allowImplicitInclusions
     * @param includeNonProfileMatched
     * @return
     */
    @Override
    public boolean isUsedWithProfileList(List<Profile> orderedProfileList, boolean allowImplicitInclusions,
            boolean includeNonProfileMatched)
    {
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.ProfilableInterface#setProfileIncludeExcludeOrder(org.openrdf.model.URI)
     */
    @Override
    public void setProfileIncludeExcludeOrder(URI profileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = profileIncludeExcludeOrder;
    }

    @Override
    public int compareTo(Provider o)
    {
        return this.getKey().stringValue().compareTo(o.getKey().stringValue());
    }

    @Override
    public void addIncludedInQueryType(URI includedInQueryType)
    {
        this.includedInQueryTypes.add(includedInQueryType);
    }

    @Override
    public void addNamespace(URI namespace)
    {
        this.namespaces.add(namespace);
    }

    @Override
    public void addNormalisationUri(URI rdfNormalisationNeeded)
    {
        this.normalisations.add(rdfNormalisationNeeded);
    }

    @Override
    public boolean containsNamespaceOrDefault(URI namespaceKey)
    {
        if(this.isDefault)
        {
            return true;
        }
        else
        {
            return this.namespaces.contains(namespaceKey);
        }
    }

    @Override
    public boolean containsNamespaceUri(URI namespaceKey)
    {
        return this.namespaces.contains(namespaceKey);
    }

    @Override
    public boolean containsNormalisationUri(URI normalisationKey)
    {
        return this.normalisations.contains(normalisationKey);
    }

    @Override
    public boolean containsQueryTypeUri(URI queryKey)
    {
        return this.queryTypes.contains(queryKey);
    }

    @Override
    public String getAssumedContentType()
    {
        return this.assumedContentType;
    }

    @Override
    public URI getEndpointMethod()
    {
        return this.endpointMethod;
    }

    @Override
    public Collection<URI> getIncludedInQueryTypes()
    {
        return this.includedInQueryTypes;
    }

    @Override
    public boolean getIsDefaultSource()
    {
        return this.isDefault;
    }

    @Override
    public Collection<URI> getNamespaces()
    {
        return this.namespaces;
    }

    @Override
    public Collection<URI> getNormalisationUris()
    {
        return this.normalisations;
    }

    @Override
    public URI getRedirectOrProxy()
    {
        return this.redirectOrProxy;
    }

    @Override
    public boolean needsProxy()
    {
        return this.redirectOrProxy.equals(ProviderSchema.getProviderProxy());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DummyProvider [title=");
        builder.append(title);
        builder.append(", key=");
        builder.append(key);
        builder.append(", endpointMethod=");
        builder.append(endpointMethod);
        builder.append(", redirectOrProxy=");
        builder.append(redirectOrProxy);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public boolean needsRedirect()
    {
        return this.redirectOrProxy.equals(ProviderSchema.getProviderRedirect());
    }

    @Override
    public void setAssumedContentType(String assumedContentType)
    {
        this.assumedContentType = assumedContentType;
    }

    @Override
    public void setEndpointMethod(URI endpointMethod)
    {
        this.endpointMethod = endpointMethod;
    }

    @Override
    public void setIsDefaultSource(boolean isDefaultSource)
    {
        this.isDefault = isDefaultSource;
    }

    @Override
    public void setRedirectOrProxy(URI redirectOrProxy)
    {
        this.redirectOrProxy = redirectOrProxy;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((assumedContentType == null) ? 0 : assumedContentType.hashCode());
        result = prime * result + ((curationStatus == null) ? 0 : curationStatus.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((endpointMethod == null) ? 0 : endpointMethod.hashCode());
        result = prime * result + ((includedInQueryTypes == null) ? 0 : includedInQueryTypes.hashCode());
        result = prime * result + (isDefault ? 1231 : 1237);
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((namespaces == null) ? 0 : namespaces.hashCode());
        result = prime * result + ((normalisations == null) ? 0 : normalisations.hashCode());
        result = prime * result + ((profileIncludeExcludeOrder == null) ? 0 : profileIncludeExcludeOrder.hashCode());
        result = prime * result + ((queryTypes == null) ? 0 : queryTypes.hashCode());
        result = prime * result + ((redirectOrProxy == null) ? 0 : redirectOrProxy.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((unrecognisedStatements == null) ? 0 : unrecognisedStatements.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(obj == null)
        {
            return false;
        }
        if(!(obj instanceof DummyProvider))
        {
            return false;
        }
        DummyProvider other = (DummyProvider)obj;
        if(assumedContentType == null)
        {
            if(other.assumedContentType != null)
            {
                return false;
            }
        }
        else if(!assumedContentType.equals(other.assumedContentType))
        {
            return false;
        }
        if(curationStatus == null)
        {
            if(other.curationStatus != null)
            {
                return false;
            }
        }
        else if(!curationStatus.equals(other.curationStatus))
        {
            return false;
        }
        if(description == null)
        {
            if(other.description != null)
            {
                return false;
            }
        }
        else if(!description.equals(other.description))
        {
            return false;
        }
        if(endpointMethod == null)
        {
            if(other.endpointMethod != null)
            {
                return false;
            }
        }
        else if(!endpointMethod.equals(other.endpointMethod))
        {
            return false;
        }
        if(includedInQueryTypes == null)
        {
            if(other.includedInQueryTypes != null)
            {
                return false;
            }
        }
        else if(!includedInQueryTypes.equals(other.includedInQueryTypes))
        {
            return false;
        }
        if(isDefault != other.isDefault)
        {
            return false;
        }
        if(key == null)
        {
            if(other.key != null)
            {
                return false;
            }
        }
        else if(!key.equals(other.key))
        {
            return false;
        }
        if(namespaces == null)
        {
            if(other.namespaces != null)
            {
                return false;
            }
        }
        else if(!namespaces.equals(other.namespaces))
        {
            return false;
        }
        if(normalisations == null)
        {
            if(other.normalisations != null)
            {
                return false;
            }
        }
        else if(!normalisations.equals(other.normalisations))
        {
            return false;
        }
        if(profileIncludeExcludeOrder == null)
        {
            if(other.profileIncludeExcludeOrder != null)
            {
                return false;
            }
        }
        else if(!profileIncludeExcludeOrder.equals(other.profileIncludeExcludeOrder))
        {
            return false;
        }
        if(queryTypes == null)
        {
            if(other.queryTypes != null)
            {
                return false;
            }
        }
        else if(!queryTypes.equals(other.queryTypes))
        {
            return false;
        }
        if(redirectOrProxy == null)
        {
            if(other.redirectOrProxy != null)
            {
                return false;
            }
        }
        else if(!redirectOrProxy.equals(other.redirectOrProxy))
        {
            return false;
        }
        if(title == null)
        {
            if(other.title != null)
            {
                return false;
            }
        }
        else if(!title.equals(other.title))
        {
            return false;
        }
        if(unrecognisedStatements == null)
        {
            if(other.unrecognisedStatements != null)
            {
                return false;
            }
        }
        else if(!unrecognisedStatements.equals(other.unrecognisedStatements))
        {
            return false;
        }
        return true;
    }
    
}
