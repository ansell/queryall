/**
 * 
 */
package org.queryall.api.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.queryall.api.profile.Profile;
import org.queryall.api.profile.ProfileSchema;
import org.queryall.api.project.ProjectSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class DummyProfile implements Profile
{
    
    private URI curationStatus = ProjectSchema.getProjectNotCuratedUri();
    private String description = "";
    private URI key;
    private String title = "";
    private Collection<Statement> unrecognisedStatements = new ArrayList<Statement>();
    private boolean allowImplicitProviderInclusions = true;
    private boolean allowImplicitQueryTypeInclusions = true;
    private boolean allowImplicitRdfRuleInclusions = true;
    private URI defaultProfileIncludeExcludeOrder = ProfileSchema.getProfileIncludeExcludeOrderUndefinedUri();;
    private Collection<URI> excludeProviders = new ArrayList<URI>();
    private Collection<URI> excludeQueryTypes = new ArrayList<URI>();
    private Collection<URI> excludeRdfRules = new ArrayList<URI>();
    private Collection<URI> includeProviders = new ArrayList<URI>();
    private Collection<URI> includeQueryTypes = new ArrayList<URI>();
    private Collection<URI> includeRdfRules = new ArrayList<URI>();
    private int order = 100;
    private Collection<URI> profileAdministrators = new ArrayList<URI>();
    
    /**
     * 
     */
    public DummyProfile()
    {
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#addExcludeProvider(org.openrdf.model.URI)
     */
    @Override
    public void addExcludeProvider(final URI excludeProvider)
    {
        this.excludeProviders.add(excludeProvider);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#addExcludeQueryType(org.openrdf.model.URI)
     */
    @Override
    public void addExcludeQueryType(final URI excludeQuery)
    {
        this.excludeQueryTypes.add(excludeQuery);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#addExcludeRdfRule(org.openrdf.model.URI)
     */
    @Override
    public void addExcludeRdfRule(final URI excludeRdfRule)
    {
        this.excludeRdfRules.add(excludeRdfRule);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#addIncludeProvider(org.openrdf.model.URI)
     */
    @Override
    public void addIncludeProvider(final URI includeProvider)
    {
        this.includeProviders.add(includeProvider);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#addIncludeQueryType(org.openrdf.model.URI)
     */
    @Override
    public void addIncludeQueryType(final URI includeQuery)
    {
        this.includeQueryTypes.add(includeQuery);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#addIncludeRdfRule(org.openrdf.model.URI)
     */
    @Override
    public void addIncludeRdfRule(final URI includeRdfRule)
    {
        this.includeRdfRules.add(includeRdfRule);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#addProfileAdministrator(org.openrdf.model.URI)
     */
    @Override
    public void addProfileAdministrator(final URI profileAdministrator)
    {
        this.profileAdministrators.add(profileAdministrator);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.queryall.api.base.BaseQueryAllInterface#addUnrecognisedStatement(org.openrdf.model.Statement
     * )
     */
    @Override
    public void addUnrecognisedStatement(final Statement unrecognisedStatement)
    {
        this.unrecognisedStatements.add(unrecognisedStatement);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Profile otherProfile)
    {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        
        if(this == otherProfile)
        {
            return EQUAL;
        }
        
        if(this.getOrder() < otherProfile.getOrder())
        {
            return BEFORE;
        }
        
        if(this.getOrder() > otherProfile.getOrder())
        {
            return AFTER;
        }
        
        if(this.getKey() == null)
        {
            if(otherProfile.getKey() == null)
            {
                return EQUAL;
            }
            else
            {
                return BEFORE;
            }
        }
        
        return this.getKey().stringValue().compareTo(otherProfile.getKey().stringValue());
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#getAllowImplicitProviderInclusions()
     */
    @Override
    public boolean getAllowImplicitProviderInclusions()
    {
        return this.allowImplicitProviderInclusions;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#getAllowImplicitQueryTypeInclusions()
     */
    @Override
    public boolean getAllowImplicitQueryTypeInclusions()
    {
        return this.allowImplicitQueryTypeInclusions;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#getAllowImplicitRdfRuleInclusions()
     */
    @Override
    public boolean getAllowImplicitRdfRuleInclusions()
    {
        return this.allowImplicitRdfRuleInclusions;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#getCurationStatus()
     */
    @Override
    public URI getCurationStatus()
    {
        return this.curationStatus;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#getDefaultNamespace()
     */
    @Override
    public QueryAllNamespaces getDefaultNamespace()
    {
        return QueryAllNamespaces.PROFILE;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#getDefaultProfileIncludeExcludeOrder()
     */
    @Override
    public URI getDefaultProfileIncludeExcludeOrder()
    {
        return this.defaultProfileIncludeExcludeOrder;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#getDescription()
     */
    @Override
    public String getDescription()
    {
        return this.description;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#getElementTypes()
     */
    @Override
    public Set<URI> getElementTypes()
    {
        final Set<URI> result = new HashSet<URI>();
        
        result.add(ProfileSchema.getProfileTypeUri());
        
        return result;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#getExcludeProviders()
     */
    @Override
    public Collection<URI> getExcludeProviders()
    {
        return this.excludeProviders;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#getExcludeQueryTypes()
     */
    @Override
    public Collection<URI> getExcludeQueryTypes()
    {
        return this.excludeQueryTypes;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#getExcludeRdfRules()
     */
    @Override
    public Collection<URI> getExcludeRdfRules()
    {
        return this.excludeRdfRules;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#getIncludeProviders()
     */
    @Override
    public Collection<URI> getIncludeProviders()
    {
        return this.includeProviders;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#getIncludeQueryTypes()
     */
    @Override
    public Collection<URI> getIncludeQueryTypes()
    {
        return this.includeQueryTypes;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#getIncludeRdfRules()
     */
    @Override
    public Collection<URI> getIncludeRdfRules()
    {
        return this.includeRdfRules;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#getKey()
     */
    @Override
    public URI getKey()
    {
        return this.key;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#getOrder()
     */
    @Override
    public int getOrder()
    {
        return this.order;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#getProfileAdministrators()
     */
    @Override
    public Collection<URI> getProfileAdministrators()
    {
        return this.profileAdministrators;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#getTitle()
     */
    @Override
    public String getTitle()
    {
        return this.title;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#getUnrecognisedStatements()
     */
    @Override
    public Collection<Statement> getUnrecognisedStatements()
    {
        return Collections.unmodifiableCollection(this.unrecognisedStatements);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#resetExcludedProviders()
     */
    @Override
    public boolean resetExcludedProviders()
    {
        this.excludeProviders = new ArrayList<URI>();
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#resetExcludedQueryTypes()
     */
    @Override
    public boolean resetExcludedQueryTypes()
    {
        this.excludeQueryTypes = new ArrayList<URI>();
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#resetExcludedRdfRules()
     */
    @Override
    public boolean resetExcludedRdfRules()
    {
        this.excludeRdfRules = new ArrayList<URI>();
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#resetIncludedProviders()
     */
    @Override
    public boolean resetIncludedProviders()
    {
        this.includeProviders = new ArrayList<URI>();
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#resetIncludedQueryTypes()
     */
    @Override
    public boolean resetIncludedQueryTypes()
    {
        this.includeQueryTypes = new ArrayList<URI>();
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#resetIncludedRdfRules()
     */
    @Override
    public boolean resetIncludedRdfRules()
    {
        this.includeRdfRules = new ArrayList<URI>();
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#resetProfileAdministrators()
     */
    @Override
    public boolean resetProfileAdministrators()
    {
        this.profileAdministrators = new ArrayList<URI>();
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#resetUnrecognisedStatements()
     */
    @Override
    public Collection<Statement> resetUnrecognisedStatements()
    {
        final Collection<Statement> results = this.unrecognisedStatements;
        
        this.unrecognisedStatements = new ArrayList<Statement>();
        
        return results;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#setAllowImplicitProviderInclusions(boolean)
     */
    @Override
    public void setAllowImplicitProviderInclusions(final boolean nextAllowImplicitProviderInclusions)
    {
        this.allowImplicitProviderInclusions = nextAllowImplicitProviderInclusions;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#setAllowImplicitQueryTypeInclusions(boolean)
     */
    @Override
    public void setAllowImplicitQueryTypeInclusions(final boolean allowImplicitQueryInclusions)
    {
        this.allowImplicitQueryTypeInclusions = allowImplicitQueryInclusions;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#setAllowImplicitRdfRuleInclusions(boolean)
     */
    @Override
    public void setAllowImplicitRdfRuleInclusions(final boolean nextAllowImplicitRdfRuleInclusions)
    {
        this.allowImplicitRdfRuleInclusions = nextAllowImplicitRdfRuleInclusions;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#setCurationStatus(org.openrdf.model.URI)
     */
    @Override
    public void setCurationStatus(final URI nextCurationStatus)
    {
        this.curationStatus = nextCurationStatus;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.queryall.api.profile.Profile#setDefaultProfileIncludeExcludeOrder(org.openrdf.model.URI)
     */
    @Override
    public void setDefaultProfileIncludeExcludeOrder(final URI nextDefaultProfileIncludeExcludeOrder)
    {
        this.defaultProfileIncludeExcludeOrder = nextDefaultProfileIncludeExcludeOrder;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(final String nextDescription)
    {
        this.description = nextDescription;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#setKey(java.lang.String)
     */
    @Override
    public void setKey(final String nextKey) throws IllegalArgumentException
    {
        this.key = Constants.VALUE_FACTORY.createURI(nextKey);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#setKey(org.openrdf.model.URI)
     */
    @Override
    public void setKey(final URI nextKey)
    {
        this.key = nextKey;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.profile.Profile#setOrder(int)
     */
    @Override
    public void setOrder(final int nextOrder)
    {
        this.order = nextOrder;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#setTitle(java.lang.String)
     */
    @Override
    public void setTitle(final String nextTitle)
    {
        this.title = nextTitle;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#toRdf(org.openrdf.repository.Repository,
     * int, org.openrdf.model.URI[])
     */
    @Override
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... contextUris)
        throws OpenRDFException
    {
        // TODO Auto-generated method stub
        return false;
    }
    
}
