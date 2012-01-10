package org.queryall.impl.profile;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.base.HtmlExport;
import org.queryall.api.profile.Profile;
import org.queryall.api.profile.ProfileSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.impl.base.BaseQueryAllImpl;
import org.queryall.utils.RdfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfileImpl extends BaseQueryAllImpl implements Profile, Comparable<Profile>, HtmlExport
{
    private static final Logger log = LoggerFactory.getLogger(ProfileImpl.class);
    private static final boolean TRACE = ProfileImpl.log.isTraceEnabled();
    private static final boolean DEBUG = ProfileImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = ProfileImpl.log.isInfoEnabled();
    
    private static final Set<URI> PROFILE_IMPL_TYPES = new HashSet<URI>();
    
    static
    {
        ProfileImpl.PROFILE_IMPL_TYPES.add(ProfileSchema.getProfileTypeUri());
    }
    
    public static Set<URI> myTypes()
    {
        return ProfileImpl.PROFILE_IMPL_TYPES;
    }
    
    private int order = 100;
    
    private boolean allowImplicitQueryInclusions = true;
    
    private boolean allowImplicitProviderInclusions = true;
    
    private boolean allowImplicitRdfRuleInclusions = true;
    
    private URI defaultProfileIncludeExcludeOrder = ProfileSchema.getProfileIncludeExcludeOrderUndefinedUri();
    
    private Collection<URI> profileAdministrators = new HashSet<URI>();
    
    private Collection<URI> includeProviders = new HashSet<URI>();
    
    private Collection<URI> excludeProviders = new HashSet<URI>();
    
    private Collection<URI> includeQueries = new HashSet<URI>();
    
    private Collection<URI> excludeQueries = new HashSet<URI>();
    
    private Collection<URI> includeRdfRules = new HashSet<URI>();
    
    private Collection<URI> excludeRdfRules = new HashSet<URI>();
    
    public ProfileImpl()
    {
    }
    
    public ProfileImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = this.resetUnrecognisedStatements();
        
        boolean defaultProfileIncludeExcludeOrderValidationFailed = true;
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(ProfileImpl.DEBUG)
            {
                ProfileImpl.log.debug("Profile.fromRdf: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(ProfileSchema.getProfileTypeUri()))
            {
                if(ProfileImpl.TRACE)
                {
                    ProfileImpl.log.trace("Profile.fromRdf: found valid type predicate for URI: " + keyToUse);
                }
                
                // resultIsValid = true;
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProfileSchema.getProfileOrderUri()))
            {
                this.setOrder(RdfUtils.getIntegerFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(ProfileSchema.getProfileAdministratorUri()))
            {
                this.addProfileAdministrator((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileSchema.getProfileDefaultIncludeExcludeOrderUri()))
            {
                this.setDefaultProfileIncludeExcludeOrder((URI)nextStatement.getObject());
                
                if(this.getDefaultProfileIncludeExcludeOrder().equals(ProfileSchema.getProfileIncludeThenExcludeUri())
                        || this.getDefaultProfileIncludeExcludeOrder().equals(
                                ProfileSchema.getProfileExcludeThenIncludeUri()))
                {
                    defaultProfileIncludeExcludeOrderValidationFailed = false;
                }
            }
            else if(nextStatement.getPredicate().equals(ProfileSchema.getProfileAllowImplicitQueryInclusionsUri()))
            {
                this.setAllowImplicitQueryTypeInclusions(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(ProfileSchema.getProfileAllowImplicitProviderInclusionsUri()))
            {
                this.setAllowImplicitProviderInclusions(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(ProfileSchema.getProfileAllowImplicitRdfRuleInclusionsUri()))
            {
                this.setAllowImplicitRdfRuleInclusions(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(ProfileSchema.getProfileIncludeProviderInProfile()))
            {
                this.addIncludeProvider((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileSchema.getProfileExcludeProviderFromProfile()))
            {
                this.addExcludeProvider((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileSchema.getProfileIncludeQueryInProfile()))
            {
                this.addIncludeQueryType((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileSchema.getProfileExcludeQueryFromProfile()))
            {
                this.addExcludeQueryType((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileSchema.getProfileIncludeRdfRuleInProfile()))
            {
                this.addIncludeRdfRule((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileSchema.getProfileExcludeRdfRuleFromProfile()))
            {
                this.addExcludeRdfRule((URI)nextStatement.getObject());
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(defaultProfileIncludeExcludeOrderValidationFailed)
        {
            ProfileImpl.log
                    .warn("The default profile include exclude order for a profile was not valid. This may cause errors if any profilable objects do not explicitly define their order. profile.getKey()="
                            + this.getKey() + " " + this.getDefaultProfileIncludeExcludeOrder().stringValue());
        }
        
        if(ProfileImpl.TRACE)
        {
            ProfileImpl.log.trace("Profile.fromRdf: would have returned... result=" + this.toString());
        }
    }
    
    @Override
    public void addExcludeProvider(final URI excludeProvider)
    {
        this.excludeProviders.add(excludeProvider);
    }
    
    @Override
    public void addExcludeQueryType(final URI excludeQuery)
    {
        this.excludeQueries.add(excludeQuery);
    }
    
    @Override
    public void addExcludeRdfRule(final URI excludeRdfRule)
    {
        this.excludeRdfRules.add(excludeRdfRule);
    }
    
    @Override
    public void addIncludeProvider(final URI includeProvider)
    {
        this.includeProviders.add(includeProvider);
    }
    
    @Override
    public void addIncludeQueryType(final URI includeQuery)
    {
        this.includeQueries.add(includeQuery);
    }
    
    @Override
    public void addIncludeRdfRule(final URI includeRdfRule)
    {
        this.includeRdfRules.add(includeRdfRule);
    }
    
    @Override
    public void addProfileAdministrator(final URI profileAdministrator)
    {
        this.profileAdministrators.add(profileAdministrator);
    }
    
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
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(!super.equals(obj))
        {
            return false;
        }
        if(!(obj instanceof Profile))
        {
            return false;
        }
        final Profile other = (Profile)obj;
        
        if(this.getAllowImplicitProviderInclusions() != other.getAllowImplicitProviderInclusions())
        {
            return false;
        }
        if(this.getAllowImplicitQueryTypeInclusions() != other.getAllowImplicitQueryTypeInclusions())
        {
            return false;
        }
        if(this.getAllowImplicitRdfRuleInclusions() != other.getAllowImplicitRdfRuleInclusions())
        {
            return false;
        }
        
        if(this.getDefaultProfileIncludeExcludeOrder() == null)
        {
            if(other.getDefaultProfileIncludeExcludeOrder() != null)
            {
                return false;
            }
        }
        else if(!this.getDefaultProfileIncludeExcludeOrder().equals(other.getDefaultProfileIncludeExcludeOrder()))
        {
            return false;
        }
        if(this.getExcludeProviders() == null)
        {
            if(other.getExcludeProviders() != null)
            {
                return false;
            }
        }
        else if(!this.getExcludeProviders().equals(other.getExcludeProviders()))
        {
            return false;
        }
        if(this.getExcludeQueryTypes() == null)
        {
            if(other.getExcludeQueryTypes() != null)
            {
                return false;
            }
        }
        else if(!this.getExcludeQueryTypes().equals(other.getExcludeQueryTypes()))
        {
            return false;
        }
        if(this.getExcludeRdfRules() == null)
        {
            if(other.getExcludeRdfRules() != null)
            {
                return false;
            }
        }
        else if(!this.getExcludeRdfRules().equals(other.getExcludeRdfRules()))
        {
            return false;
        }
        if(this.getIncludeProviders() == null)
        {
            if(other.getIncludeProviders() != null)
            {
                return false;
            }
        }
        else if(!this.getIncludeProviders().equals(other.getIncludeProviders()))
        {
            return false;
        }
        if(this.getIncludeQueryTypes() == null)
        {
            if(other.getIncludeQueryTypes() != null)
            {
                return false;
            }
        }
        else if(!this.getIncludeQueryTypes().equals(other.getIncludeQueryTypes()))
        {
            return false;
        }
        if(this.getIncludeRdfRules() == null)
        {
            if(other.getIncludeRdfRules() != null)
            {
                return false;
            }
        }
        else if(!this.getIncludeRdfRules().equals(other.getIncludeRdfRules()))
        {
            return false;
        }
        
        if(this.getOrder() != other.getOrder())
        {
            return false;
        }
        if(this.getProfileAdministrators() == null)
        {
            if(other.getProfileAdministrators() != null)
            {
                return false;
            }
        }
        else if(!this.getProfileAdministrators().equals(other.getProfileAdministrators()))
        {
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean getAllowImplicitProviderInclusions()
    {
        return this.allowImplicitProviderInclusions;
    }
    
    @Override
    public boolean getAllowImplicitQueryTypeInclusions()
    {
        return this.allowImplicitQueryInclusions;
    }
    
    @Override
    public boolean getAllowImplicitRdfRuleInclusions()
    {
        return this.allowImplicitRdfRuleInclusions;
    }
    
    /**
     * @return the namespace used to represent objects of this type by default
     */
    @Override
    public QueryAllNamespaces getDefaultNamespace()
    {
        return QueryAllNamespaces.PROFILE;
    }
    
    @Override
    public URI getDefaultProfileIncludeExcludeOrder()
    {
        return this.defaultProfileIncludeExcludeOrder;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Set<URI> getElementTypes()
    {
        return ProfileImpl.myTypes();
    }
    
    @Override
    public Collection<URI> getExcludeProviders()
    {
        return this.excludeProviders;
    }
    
    @Override
    public Collection<URI> getExcludeQueryTypes()
    {
        return this.excludeQueries;
    }
    
    @Override
    public Collection<URI> getExcludeRdfRules()
    {
        return this.excludeRdfRules;
    }
    
    @Override
    public Collection<URI> getIncludeProviders()
    {
        return this.includeProviders;
    }
    
    @Override
    public Collection<URI> getIncludeQueryTypes()
    {
        return this.includeQueries;
    }
    
    @Override
    public Collection<URI> getIncludeRdfRules()
    {
        return this.includeRdfRules;
    }
    
    @Override
    public int getOrder()
    {
        return this.order;
    }
    
    @Override
    public Collection<URI> getProfileAdministrators()
    {
        return this.profileAdministrators;
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
        int result = super.hashCode();
        result = prime * result + (this.allowImplicitProviderInclusions ? 1231 : 1237);
        result = prime * result + (this.allowImplicitQueryInclusions ? 1231 : 1237);
        result = prime * result + (this.allowImplicitRdfRuleInclusions ? 1231 : 1237);
        result =
                prime
                        * result
                        + ((this.defaultProfileIncludeExcludeOrder == null) ? 0
                                : this.defaultProfileIncludeExcludeOrder.hashCode());
        result = prime * result + ((this.excludeProviders == null) ? 0 : this.excludeProviders.hashCode());
        result = prime * result + ((this.excludeQueries == null) ? 0 : this.excludeQueries.hashCode());
        result = prime * result + ((this.excludeRdfRules == null) ? 0 : this.excludeRdfRules.hashCode());
        result = prime * result + ((this.includeProviders == null) ? 0 : this.includeProviders.hashCode());
        result = prime * result + ((this.includeQueries == null) ? 0 : this.includeQueries.hashCode());
        result = prime * result + ((this.includeRdfRules == null) ? 0 : this.includeRdfRules.hashCode());
        result = prime * result + this.order;
        result = prime * result + ((this.profileAdministrators == null) ? 0 : this.profileAdministrators.hashCode());
        return result;
    }
    
    @Override
    public boolean resetExcludedProviders()
    {
        try
        {
            this.excludeProviders.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            ProfileImpl.log.debug("Could not clear collection");
        }
        
        this.excludeProviders = new HashSet<URI>();
        
        return true;
    }
    
    @Override
    public boolean resetExcludedQueryTypes()
    {
        try
        {
            this.excludeQueries.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            ProfileImpl.log.debug("Could not clear collection");
        }
        
        this.excludeQueries = new HashSet<URI>();
        
        return true;
    }
    
    @Override
    public boolean resetExcludedRdfRules()
    {
        try
        {
            this.excludeRdfRules.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            ProfileImpl.log.debug("Could not clear collection");
        }
        
        this.excludeRdfRules = new HashSet<URI>();
        
        return true;
    }
    
    @Override
    public boolean resetIncludedProviders()
    {
        try
        {
            this.includeProviders.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            ProfileImpl.log.debug("Could not clear collection");
        }
        
        this.includeProviders = new HashSet<URI>();
        
        return true;
    }
    
    @Override
    public boolean resetIncludedQueryTypes()
    {
        try
        {
            this.includeQueries.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            ProfileImpl.log.debug("Could not clear collection");
        }
        
        this.includeQueries = new HashSet<URI>();
        
        return true;
    }
    
    @Override
    public boolean resetIncludedRdfRules()
    {
        try
        {
            this.includeRdfRules.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            ProfileImpl.log.debug("Could not clear collection");
        }
        
        this.includeRdfRules = new HashSet<URI>();
        
        return true;
    }
    
    @Override
    public boolean resetProfileAdministrators()
    {
        try
        {
            this.profileAdministrators.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            ProfileImpl.log.debug("Could not clear collection");
        }
        
        this.profileAdministrators = new HashSet<URI>();
        
        return true;
    }
    
    @Override
    public void setAllowImplicitProviderInclusions(final boolean allowImplicitProviderInclusions)
    {
        this.allowImplicitProviderInclusions = allowImplicitProviderInclusions;
    }
    
    @Override
    public void setAllowImplicitQueryTypeInclusions(final boolean allowImplicitQueryInclusions)
    {
        this.allowImplicitQueryInclusions = allowImplicitQueryInclusions;
    }
    
    @Override
    public void setAllowImplicitRdfRuleInclusions(final boolean allowImplicitRdfRuleInclusions)
    {
        this.allowImplicitRdfRuleInclusions = allowImplicitRdfRuleInclusions;
    }
    
    @Override
    public void setDefaultProfileIncludeExcludeOrder(final URI defaultProfileIncludeExcludeOrder)
    {
        this.defaultProfileIncludeExcludeOrder = defaultProfileIncludeExcludeOrder;
    }
    
    @Override
    public void setOrder(final int order)
    {
        this.order = order;
    }
    
    @Override
    public String toHtml()
    {
        final StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        final String prefix = "profile_";
        
        return sb.toString();
    }
    
    @Override
    public String toHtmlFormBody()
    {
        final StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        final String prefix = "profile_";
        
        return sb.toString();
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... contextKey)
        throws OpenRDFException
    {
        super.toRdf(myRepository, modelVersion, contextKey);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            final URI profileInstanceUri = this.getKey();
            
            Literal titleLiteral;
            
            if(this.getTitle() == null)
            {
                titleLiteral = f.createLiteral("");
            }
            else
            {
                titleLiteral = f.createLiteral(this.getTitle());
            }
            
            final Literal orderLiteral = f.createLiteral(this.order);
            final Literal allowImplicitQueryInclusionsLiteral = f.createLiteral(this.allowImplicitQueryInclusions);
            final Literal allowImplicitProviderInclusionsLiteral =
                    f.createLiteral(this.allowImplicitProviderInclusions);
            final Literal allowImplicitRdfRuleInclusionsLiteral = f.createLiteral(this.allowImplicitRdfRuleInclusions);
            final URI defaultProfileIncludeExcludeOrderLiteral = this.defaultProfileIncludeExcludeOrder;
            
            // log.info("About to add to the repository");
            
            con.setAutoCommit(false);
            
            for(final URI nextElementType : this.getElementTypes())
            {
                con.add(profileInstanceUri, RDF.TYPE, nextElementType, contextKey);
            }
            
            // log.info("About to add to the repository 2");
            if(modelVersion == 1)
            {
                con.add(profileInstanceUri, ProfileSchema.getProfileTitle(), titleLiteral, contextKey);
            }
            
            // log.info("About to add to the repository 3");
            con.add(profileInstanceUri, ProfileSchema.getProfileOrderUri(), orderLiteral, contextKey);
            
            // log.info("About to add to the repository 4");
            con.add(profileInstanceUri, ProfileSchema.getProfileAllowImplicitQueryInclusionsUri(),
                    allowImplicitQueryInclusionsLiteral, contextKey);
            
            // log.info("About to add to the repository 5");
            con.add(profileInstanceUri, ProfileSchema.getProfileAllowImplicitProviderInclusionsUri(),
                    allowImplicitProviderInclusionsLiteral, contextKey);
            
            // log.info("About to add to the repository 6");
            con.add(profileInstanceUri, ProfileSchema.getProfileAllowImplicitRdfRuleInclusionsUri(),
                    allowImplicitRdfRuleInclusionsLiteral, contextKey);
            
            // log.info("About to add to the repository 7");
            con.add(profileInstanceUri, ProfileSchema.getProfileDefaultIncludeExcludeOrderUri(),
                    defaultProfileIncludeExcludeOrderLiteral, contextKey);
            
            // log.info("About to add array based information");
            
            if(this.includeProviders != null)
            {
                
                for(final URI nextIncludeProviders : this.includeProviders)
                {
                    con.add(profileInstanceUri, ProfileSchema.getProfileIncludeProviderInProfile(),
                            nextIncludeProviders, contextKey);
                }
            }
            
            if(this.excludeProviders != null)
            {
                
                for(final URI nextExcludeProviders : this.excludeProviders)
                {
                    con.add(profileInstanceUri, ProfileSchema.getProfileExcludeProviderFromProfile(),
                            nextExcludeProviders, contextKey);
                }
            }
            
            if(this.profileAdministrators != null)
            {
                
                for(final URI nextProfileAdministrator : this.profileAdministrators)
                {
                    con.add(profileInstanceUri, ProfileSchema.getProfileAdministratorUri(), nextProfileAdministrator,
                            contextKey);
                }
            }
            
            if(this.includeQueries != null)
            {
                
                for(final URI nextIncludeQuery : this.includeQueries)
                {
                    if(ProfileImpl.TRACE)
                    {
                        ProfileImpl.log.trace("Profile.toRdf: nextIncludeQuery=" + nextIncludeQuery);
                    }
                    
                    con.add(profileInstanceUri, ProfileSchema.getProfileIncludeQueryInProfile(), nextIncludeQuery,
                            contextKey);
                }
            }
            
            if(this.excludeQueries != null)
            {
                
                for(final URI nextExcludeQuery : this.excludeQueries)
                {
                    if(ProfileImpl.TRACE)
                    {
                        ProfileImpl.log.trace("Profile.toRdf: nextExcludeQuery=" + nextExcludeQuery);
                    }
                    
                    con.add(profileInstanceUri, ProfileSchema.getProfileExcludeQueryFromProfile(), nextExcludeQuery,
                            contextKey);
                }
            }
            
            if(this.includeRdfRules != null)
            {
                
                for(final URI nextIncludeRdfRules : this.includeRdfRules)
                {
                    con.add(profileInstanceUri, ProfileSchema.getProfileIncludeRdfRuleInProfile(), nextIncludeRdfRules,
                            contextKey);
                }
            }
            
            if(this.excludeRdfRules != null)
            {
                
                for(final URI nextExcludeRdfRules : this.excludeRdfRules)
                {
                    con.add(profileInstanceUri, ProfileSchema.getProfileExcludeRdfRuleFromProfile(),
                            nextExcludeRdfRules, contextKey);
                }
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            ProfileImpl.log.error("RepositoryException: " + re.getMessage());
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
    
    @Override
    public String toString()
    {
        final StringBuilder result = new StringBuilder();
        
        result.append("key=").append(this.getKey().stringValue());
        result.append("order=").append(this.getOrder());
        
        return result.toString();
    }
}
