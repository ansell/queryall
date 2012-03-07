/**
 * 
 */
package org.queryall.api.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.queryall.api.base.BaseQueryAllInterface;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.utils.ProfileIncludeExclude;
import org.queryall.api.utils.ProfileMatch;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class DummyProvider implements Provider
{
    private static final Logger LOG = LoggerFactory.getLogger(DummyProvider.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = DummyProvider.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = DummyProvider.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = DummyProvider.LOG.isInfoEnabled();
    
    private ProfileIncludeExclude profileIncludeExcludeOrder = ProfileIncludeExclude.UNDEFINED;
    private String title = "";
    private URI key = null;
    private String description = "";
    private URI curationStatus = null;
    private Set<Statement> unrecognisedStatements = new HashSet<Statement>();
    private Set<URI> namespaces = new HashSet<URI>();
    private boolean isDefault = false;
    private Set<URI> normalisations = new HashSet<URI>();
    private Set<URI> queryTypes = new HashSet<URI>();
    private String assumedContentType;
    private URI endpointMethod = ProviderSchema.getProviderNoCommunication();
    private URI redirectOrProxy;
    
    /**
     * 
     */
    public DummyProvider()
    {
        
    }
    
    @Override
    public void addIncludedInQueryType(final URI includedInQueryType)
    {
        this.queryTypes.add(includedInQueryType);
    }
    
    @Override
    public void addNamespace(final URI namespace)
    {
        this.namespaces.add(namespace);
    }
    
    @Override
    public void addNormalisationUri(final URI rdfNormalisationNeeded)
    {
        this.normalisations.add(rdfNormalisationNeeded);
    }
    
    @Override
    public void addUnrecognisedStatement(final Statement unrecognisedStatement)
    {
        this.unrecognisedStatements.add(unrecognisedStatement);
    }
    
    @Override
    public int compareTo(final Provider o)
    {
        return this.getKey().stringValue().compareTo(o.getKey().stringValue());
    }
    
    @Override
    public boolean containsNamespaceOrDefault(final URI namespaceKey)
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
    public boolean containsNamespaceUri(final URI namespaceKey)
    {
        return this.namespaces.contains(namespaceKey);
    }
    
    @Override
    public boolean containsNormalisationUri(final URI normalisationKey)
    {
        return this.normalisations.contains(normalisationKey);
    }
    
    @Override
    public boolean containsQueryTypeUri(final URI queryKey)
    {
        return this.queryTypes.contains(queryKey);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(obj == null)
        {
            return false;
        }
        if(!(obj instanceof BaseQueryAllInterface))
        {
            return false;
        }
        final BaseQueryAllInterface other = (BaseQueryAllInterface)obj;
        if(this.getCurationStatus() == null)
        {
            if(other.getCurationStatus() != null)
            {
                return false;
            }
        }
        else if(!this.getCurationStatus().equals(other.getCurationStatus()))
        {
            return false;
        }
        if(this.getDescription() == null)
        {
            if(other.getDescription() != null)
            {
                return false;
            }
        }
        else if(!this.getDescription().equals(other.getDescription()))
        {
            return false;
        }
        if(this.getKey() == null)
        {
            if(other.getKey() != null)
            {
                return false;
            }
        }
        else if(!this.getKey().equals(other.getKey()))
        {
            return false;
        }
        if(this.getTitle() == null)
        {
            if(other.getTitle() != null)
            {
                return false;
            }
        }
        else if(!this.getTitle().equals(other.getTitle()))
        {
            return false;
        }
        if(this.getUnrecognisedStatements() == null)
        {
            if(other.getUnrecognisedStatements() != null)
            {
                return false;
            }
        }
        else if(!this.getUnrecognisedStatements().equals(other.getUnrecognisedStatements()))
        {
            return false;
        }
        
        if(!(obj instanceof Provider))
        {
            return false;
        }
        final Provider otherProvider = (Provider)obj;
        if(this.getIncludedInQueryTypes() == null)
        {
            if(otherProvider.getIncludedInQueryTypes() != null)
            {
                return false;
            }
        }
        else if(!this.getIncludedInQueryTypes().equals(otherProvider.getIncludedInQueryTypes()))
        {
            return false;
        }
        if(this.getIsDefaultSource() != otherProvider.getIsDefaultSource())
        {
            return false;
        }
        if(this.getNamespaces() == null)
        {
            if(otherProvider.getNamespaces() != null)
            {
                return false;
            }
        }
        else if(!this.getNamespaces().equals(otherProvider.getNamespaces()))
        {
            return false;
        }
        if(this.getProfileIncludeExcludeOrder() == null)
        {
            if(otherProvider.getProfileIncludeExcludeOrder() != null)
            {
                return false;
            }
        }
        else if(!this.getProfileIncludeExcludeOrder().equals(otherProvider.getProfileIncludeExcludeOrder()))
        {
            return false;
        }
        if(this.getNormalisationUris() == null)
        {
            if(otherProvider.getNormalisationUris() != null)
            {
                return false;
            }
        }
        else if(!this.getNormalisationUris().equals(otherProvider.getNormalisationUris()))
        {
            return false;
        }
        if(this.getRedirectOrProxy() == null)
        {
            if(otherProvider.getRedirectOrProxy() != null)
            {
                return false;
            }
        }
        else if(!this.getRedirectOrProxy().equals(otherProvider.getRedirectOrProxy()))
        {
            return false;
        }
        return true;
    }
    
    @Override
    public String getAssumedContentType()
    {
        return this.assumedContentType;
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
        return Collections.singleton(ProviderSchema.getProviderTypeUri());
    }
    
    @Override
    public URI getEndpointMethod()
    {
        return this.endpointMethod;
    }
    
    @Override
    public Collection<URI> getIncludedInQueryTypes()
    {
        return this.queryTypes;
    }
    
    @Override
    public boolean getIsDefaultSource()
    {
        return this.isDefault;
    }
    
    @Override
    public URI getKey()
    {
        return this.key;
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
    public ProfileIncludeExclude getProfileIncludeExcludeOrder()
    {
        return this.profileIncludeExcludeOrder;
    }
    
    @Override
    public URI getRedirectOrProxy()
    {
        return this.redirectOrProxy;
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
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.assumedContentType == null) ? 0 : this.assumedContentType.hashCode());
        result = prime * result + ((this.curationStatus == null) ? 0 : this.curationStatus.hashCode());
        result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
        result = prime * result + ((this.endpointMethod == null) ? 0 : this.endpointMethod.hashCode());
        result = prime * result + ((this.queryTypes == null) ? 0 : this.queryTypes.hashCode());
        result = prime * result + (this.isDefault ? 1231 : 1237);
        result = prime * result + ((this.key == null) ? 0 : this.key.hashCode());
        result = prime * result + ((this.namespaces == null) ? 0 : this.namespaces.hashCode());
        result = prime * result + ((this.normalisations == null) ? 0 : this.normalisations.hashCode());
        result =
                prime * result
                        + ((this.profileIncludeExcludeOrder == null) ? 0 : this.profileIncludeExcludeOrder.hashCode());
        result = prime * result + ((this.redirectOrProxy == null) ? 0 : this.redirectOrProxy.hashCode());
        result = prime * result + ((this.title == null) ? 0 : this.title.hashCode());
        result = prime * result + ((this.unrecognisedStatements == null) ? 0 : this.unrecognisedStatements.hashCode());
        return result;
    }
    
    /**
     * NOTE: This is a dummy class, always returns true.
     * 
     * @param orderedProfileList
     * @param allowImplicitInclusions
     * @param includeNonProfileMatched
     * @return
     */
    @Override
    public boolean isUsedWithProfileList(final List<Profile> orderedProfileList, final boolean allowImplicitInclusions,
            final boolean includeNonProfileMatched)
    {
        return ProfileMatch.isUsedWithProfileList(this, orderedProfileList, allowImplicitInclusions,
                includeNonProfileMatched);
    }
    
    @Override
    public boolean needsProxy()
    {
        return this.redirectOrProxy.equals(ProviderSchema.getProviderProxy());
    }
    
    @Override
    public boolean needsRedirect()
    {
        return this.redirectOrProxy.equals(ProviderSchema.getProviderRedirect());
    }
    
    @Override
    public boolean resetIncludedInQueryTypes()
    {
        this.queryTypes = new HashSet<URI>();
        
        return true;
    }
    
    @Override
    public boolean resetNamespaces()
    {
        this.namespaces = new HashSet<URI>();
        
        return true;
    }
    
    @Override
    public boolean resetNormalisationUris()
    {
        this.normalisations = new HashSet<URI>();
        
        return true;
    }
    
    @Override
    public Collection<Statement> resetUnrecognisedStatements()
    {
        final Collection<Statement> unrecognisedStatementsTemp = new ArrayList<Statement>(this.unrecognisedStatements);
        
        this.unrecognisedStatements = new HashSet<Statement>();
        
        return unrecognisedStatementsTemp;
    }
    
    @Override
    public void setAssumedContentType(final String nextAssumedContentType)
    {
        this.assumedContentType = nextAssumedContentType;
    }
    
    @Override
    public void setCurationStatus(final URI nextCurationStatus)
    {
        this.curationStatus = nextCurationStatus;
    }
    
    @Override
    public void setDescription(final String nextDescription)
    {
        this.description = nextDescription;
    }
    
    @Override
    public void setEndpointMethod(final URI nextEndpointMethod)
    {
        this.endpointMethod = nextEndpointMethod;
    }
    
    @Override
    public void setIsDefaultSource(final boolean isDefaultSource)
    {
        this.isDefault = isDefaultSource;
    }
    
    @Override
    public void setKey(final String nextKey) throws IllegalArgumentException
    {
        this.setKey(new ValueFactoryImpl().createURI(nextKey));
    }
    
    @Override
    public void setKey(final URI nextKey)
    {
        this.key = nextKey;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.queryall.api.base.ProfilableInterface#setProfileIncludeExcludeOrder(org.openrdf.model
     * .URI)
     */
    @Override
    public void setProfileIncludeExcludeOrder(final ProfileIncludeExclude nextProfileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = nextProfileIncludeExcludeOrder;
    }
    
    @Override
    public void setRedirectOrProxy(final URI nextRedirectOrProxy)
    {
        this.redirectOrProxy = nextRedirectOrProxy;
    }
    
    @Override
    public void setTitle(final String nextTitle)
    {
        this.title = nextTitle;
    }
    
    /**
     * NOTE: This is a dummy implementation. Always returns true with no sideeffects.
     * 
     * @param myRepository
     * @param modelVersion
     * @param contextUris
     * @return
     * @throws OpenRDFException
     */
    @Override
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... contextUris)
        throws OpenRDFException
    {
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("DummyProvider [title=");
        builder.append(this.title);
        builder.append(", key=");
        builder.append(this.key);
        builder.append(", endpointMethod=");
        builder.append(this.endpointMethod);
        builder.append(", redirectOrProxy=");
        builder.append(this.redirectOrProxy);
        builder.append("]");
        return builder.toString();
    }
    
}
