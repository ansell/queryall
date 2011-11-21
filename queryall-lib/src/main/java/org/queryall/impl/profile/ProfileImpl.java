package org.queryall.impl.profile;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.queryall.api.project.ProjectSchema;
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
    private static final boolean _TRACE = ProfileImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = ProfileImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = ProfileImpl.log.isInfoEnabled();
    
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
    
    private boolean allowImplicitQueryInclusions = false;
    
    private boolean allowImplicitProviderInclusions = false;
    
    private boolean allowImplicitRdfRuleInclusions = false;
    
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
        
        final Collection<Statement> currentUnrecognisedStatements = new HashSet<Statement>();
        
        currentUnrecognisedStatements.addAll(this.getUnrecognisedStatements());
        
        this.unrecognisedStatements = new HashSet<Statement>();
        
        boolean defaultProfileIncludeExcludeOrderValidationFailed = true;
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(ProfileImpl._DEBUG)
            {
                ProfileImpl.log.debug("Profile.fromRdf: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(ProfileSchema.getProfileTypeUri()))
            {
                if(ProfileImpl._TRACE)
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
        
        if(ProfileImpl._TRACE)
        {
            ProfileImpl.log.trace("Profile.fromRdf: would have returned... result=" + this.toString());
        }
    }
    
    @Override
    public void addExcludeProvider(final URI excludeProvider)
    {
        if(this.excludeProviders == null)
        {
            this.excludeProviders = new LinkedList<URI>();
        }
        
        this.excludeProviders.add(excludeProvider);
    }
    
    @Override
    public void addExcludeQueryType(final URI excludeQuery)
    {
        if(this.excludeQueries == null)
        {
            this.excludeQueries = new LinkedList<URI>();
        }
        
        this.excludeQueries.add(excludeQuery);
    }
    
    @Override
    public void addExcludeRdfRule(final URI excludeRdfRule)
    {
        if(this.excludeRdfRules == null)
        {
            this.excludeRdfRules = new LinkedList<URI>();
        }
        
        this.excludeRdfRules.add(excludeRdfRule);
    }
    
    @Override
    public void addIncludeProvider(final URI includeProvider)
    {
        if(this.includeProviders == null)
        {
            this.includeProviders = new LinkedList<URI>();
        }
        
        this.includeProviders.add(includeProvider);
    }
    
    @Override
    public void addIncludeQueryType(final URI includeQuery)
    {
        if(this.includeQueries == null)
        {
            this.includeQueries = new LinkedList<URI>();
        }
        
        this.includeQueries.add(includeQuery);
    }
    
    @Override
    public void addIncludeRdfRule(final URI includeRdfRule)
    {
        if(this.includeRdfRules == null)
        {
            this.includeRdfRules = new LinkedList<URI>();
        }
        
        this.includeRdfRules.add(includeRdfRule);
    }
    
    @Override
    public void addProfileAdministrator(final URI profileAdministrator)
    {
        if(this.profileAdministrators == null)
        {
            this.profileAdministrators = new LinkedList<URI>();
        }
        
        this.profileAdministrators.add(profileAdministrator);
    }
    
    @Override
    public void addUnrecognisedStatement(final Statement unrecognisedStatement)
    {
        this.unrecognisedStatements.add(unrecognisedStatement);
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
        if(obj == null)
        {
            return false;
        }
        if(!(obj instanceof ProfileImpl))
        {
            return false;
        }
        final ProfileImpl other = (ProfileImpl)obj;
        if(this.allowImplicitProviderInclusions != other.allowImplicitProviderInclusions)
        {
            return false;
        }
        if(this.allowImplicitQueryInclusions != other.allowImplicitQueryInclusions)
        {
            return false;
        }
        if(this.allowImplicitRdfRuleInclusions != other.allowImplicitRdfRuleInclusions)
        {
            return false;
        }
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
        if(this.defaultProfileIncludeExcludeOrder == null)
        {
            if(other.defaultProfileIncludeExcludeOrder != null)
            {
                return false;
            }
        }
        else if(!this.defaultProfileIncludeExcludeOrder.equals(other.defaultProfileIncludeExcludeOrder))
        {
            return false;
        }
        if(this.excludeProviders == null)
        {
            if(other.excludeProviders != null)
            {
                return false;
            }
        }
        else if(!this.excludeProviders.equals(other.excludeProviders))
        {
            return false;
        }
        if(this.excludeQueries == null)
        {
            if(other.excludeQueries != null)
            {
                return false;
            }
        }
        else if(!this.excludeQueries.equals(other.excludeQueries))
        {
            return false;
        }
        if(this.excludeRdfRules == null)
        {
            if(other.excludeRdfRules != null)
            {
                return false;
            }
        }
        else if(!this.excludeRdfRules.equals(other.excludeRdfRules))
        {
            return false;
        }
        if(this.includeProviders == null)
        {
            if(other.includeProviders != null)
            {
                return false;
            }
        }
        else if(!this.includeProviders.equals(other.includeProviders))
        {
            return false;
        }
        if(this.includeQueries == null)
        {
            if(other.includeQueries != null)
            {
                return false;
            }
        }
        else if(!this.includeQueries.equals(other.includeQueries))
        {
            return false;
        }
        if(this.includeRdfRules == null)
        {
            if(other.includeRdfRules != null)
            {
                return false;
            }
        }
        else if(!this.includeRdfRules.equals(other.includeRdfRules))
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
        if(this.order != other.order)
        {
            return false;
        }
        if(this.profileAdministrators == null)
        {
            if(other.profileAdministrators != null)
            {
                return false;
            }
        }
        else if(!this.profileAdministrators.equals(other.profileAdministrators))
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
        int result = 1;
        result = prime * result + (this.allowImplicitProviderInclusions ? 1231 : 1237);
        result = prime * result + (this.allowImplicitQueryInclusions ? 1231 : 1237);
        result = prime * result + (this.allowImplicitRdfRuleInclusions ? 1231 : 1237);
        result = prime * result + ((this.getCurationStatus() == null) ? 0 : this.getCurationStatus().hashCode());
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
        result = prime * result + ((this.getKey() == null) ? 0 : this.getKey().hashCode());
        result = prime * result + this.order;
        result = prime * result + ((this.profileAdministrators == null) ? 0 : this.profileAdministrators.hashCode());
        result = prime * result + ((this.getTitle() == null) ? 0 : this.getTitle().hashCode());
        return result;
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
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... keyToUse)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
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
            
            URI curationStatusLiteral = null;
            
            if(this.getCurationStatus() == null)
            {
                curationStatusLiteral = ProjectSchema.getProjectNotCuratedUri();
            }
            else
            {
                curationStatusLiteral = this.getCurationStatus();
            }
            
            // log.info("About to add to the repository");
            
            con.setAutoCommit(false);
            
            for(final URI nextElementType : this.getElementTypes())
            {
                con.add(profileInstanceUri, RDF.TYPE, nextElementType, keyToUse);
            }
            
            con.add(profileInstanceUri, ProjectSchema.getProjectCurationStatusUri(), curationStatusLiteral, keyToUse);
            
            // log.info("About to add to the repository 2");
            if(modelVersion == 1)
            {
                con.add(profileInstanceUri, ProfileSchema.getProfileTitle(), titleLiteral, keyToUse);
            }
            else
            {
                con.add(profileInstanceUri, Constants.DC_TITLE, titleLiteral, keyToUse);
            }
            
            // log.info("About to add to the repository 3");
            con.add(profileInstanceUri, ProfileSchema.getProfileOrderUri(), orderLiteral, keyToUse);
            
            // log.info("About to add to the repository 4");
            con.add(profileInstanceUri, ProfileSchema.getProfileAllowImplicitQueryInclusionsUri(),
                    allowImplicitQueryInclusionsLiteral, keyToUse);
            
            // log.info("About to add to the repository 5");
            con.add(profileInstanceUri, ProfileSchema.getProfileAllowImplicitProviderInclusionsUri(),
                    allowImplicitProviderInclusionsLiteral, keyToUse);
            
            // log.info("About to add to the repository 6");
            con.add(profileInstanceUri, ProfileSchema.getProfileAllowImplicitRdfRuleInclusionsUri(),
                    allowImplicitRdfRuleInclusionsLiteral, keyToUse);
            
            // log.info("About to add to the repository 7");
            con.add(profileInstanceUri, ProfileSchema.getProfileDefaultIncludeExcludeOrderUri(),
                    defaultProfileIncludeExcludeOrderLiteral, keyToUse);
            
            // log.info("About to add array based information");
            
            if(this.includeProviders != null)
            {
                
                for(final URI nextIncludeProviders : this.includeProviders)
                {
                    con.add(profileInstanceUri, ProfileSchema.getProfileIncludeProviderInProfile(),
                            nextIncludeProviders, keyToUse);
                }
            }
            
            if(this.excludeProviders != null)
            {
                
                for(final URI nextExcludeProviders : this.excludeProviders)
                {
                    con.add(profileInstanceUri, ProfileSchema.getProfileExcludeProviderFromProfile(),
                            nextExcludeProviders, keyToUse);
                }
            }
            
            if(this.profileAdministrators != null)
            {
                
                for(final URI nextProfileAdministrator : this.profileAdministrators)
                {
                    con.add(profileInstanceUri, ProfileSchema.getProfileAdministratorUri(), nextProfileAdministrator,
                            keyToUse);
                }
            }
            
            if(this.includeQueries != null)
            {
                
                for(final URI nextIncludeQuery : this.includeQueries)
                {
                    if(ProfileImpl._TRACE)
                    {
                        ProfileImpl.log.trace("Profile.toRdf: nextIncludeQuery=" + nextIncludeQuery);
                    }
                    
                    con.add(profileInstanceUri, ProfileSchema.getProfileIncludeQueryInProfile(), nextIncludeQuery,
                            keyToUse);
                }
            }
            
            if(this.excludeQueries != null)
            {
                
                for(final URI nextExcludeQuery : this.excludeQueries)
                {
                    if(ProfileImpl._TRACE)
                    {
                        ProfileImpl.log.trace("Profile.toRdf: nextExcludeQuery=" + nextExcludeQuery);
                    }
                    
                    con.add(profileInstanceUri, ProfileSchema.getProfileExcludeQueryFromProfile(), nextExcludeQuery,
                            keyToUse);
                }
            }
            
            if(this.includeRdfRules != null)
            {
                
                for(final URI nextIncludeRdfRules : this.includeRdfRules)
                {
                    con.add(profileInstanceUri, ProfileSchema.getProfileIncludeRdfRuleInProfile(), nextIncludeRdfRules,
                            keyToUse);
                }
            }
            
            if(this.excludeRdfRules != null)
            {
                
                for(final URI nextExcludeRdfRules : this.excludeRdfRules)
                {
                    con.add(profileInstanceUri, ProfileSchema.getProfileExcludeRdfRuleFromProfile(),
                            nextExcludeRdfRules, keyToUse);
                }
            }
            
            if(this.unrecognisedStatements != null)
            {
                
                for(final Statement nextUnrecognisedStatement : this.unrecognisedStatements)
                {
                    con.add(nextUnrecognisedStatement, keyToUse);
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
