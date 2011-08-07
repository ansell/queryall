package org.queryall.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.Profile;
import org.queryall.helpers.Constants;
import org.queryall.helpers.RdfUtils;
import org.queryall.helpers.Settings;
import org.queryall.helpers.StringUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfileImpl implements Profile, Comparable<Profile>
{
    private static final Logger log = Logger.getLogger(Profile.class.getName());
    private static final boolean _TRACE = ProfileImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = ProfileImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = ProfileImpl.log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.getSettings().getNamespaceForProfile();
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key;
    private String title = "";
    private URI curationStatus = ProjectImpl.getProjectNotCuratedUri();
    
    private int order = 100;
    
    private boolean allowImplicitQueryInclusions = false;
    private boolean allowImplicitProviderInclusions = false;
    private boolean allowImplicitRdfRuleInclusions = false;
    
    private URI defaultProfileIncludeExcludeOrder = ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri();
    
    private Collection<URI> profileAdministrators = new HashSet<URI>();
    
    private Collection<URI> includeProviders = new HashSet<URI>();
    private Collection<URI> excludeProviders = new HashSet<URI>();
    private Collection<URI> includeQueries = new HashSet<URI>();
    private Collection<URI> excludeQueries = new HashSet<URI>();
    private Collection<URI> includeRdfRules = new HashSet<URI>();
    private Collection<URI> excludeRdfRules = new HashSet<URI>();
    
    private static URI profileTypeUri;
    private static URI profileTitle;
    private static URI profileAdministratorUri;
    private static URI profileOrderUri;
    private static URI profileAllowImplicitQueryInclusionsUri;
    private static URI profileAllowImplicitProviderInclusionsUri;
    private static URI profileAllowImplicitRdfRuleInclusionsUri;
    private static URI profileDefaultIncludeExcludeOrderUri;
    private static URI profileIncludeExcludeOrderUri;
    private static URI profileIncludeProviderInProfile;
    private static URI profileExcludeProviderFromProfile;
    private static URI profileIncludeQueryInProfile;
    private static URI profileExcludeQueryFromProfile;
    private static URI profileIncludeRdfRuleInProfile;
    private static URI profileExcludeRdfRuleFromProfile;
    
    private static URI profileExcludeThenIncludeUri;
    private static URI profileIncludeThenExcludeUri;
    private static URI profileIncludeExcludeOrderUndefinedUri;
    
    public static String profileNamespace;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        ProfileImpl.profileNamespace =
                Settings.getSettings().getOntologyTermUriPrefix() + Settings.getSettings().getNamespaceForProfile()
                        + Settings.getSettings().getOntologyTermUriSuffix();
        
        ProfileImpl.setProfileTypeUri(f.createURI(ProfileImpl.profileNamespace, "Profile"));
        ProfileImpl.setProfileTitle(f.createURI(ProfileImpl.profileNamespace, "Title"));
        ProfileImpl.setProfileOrderUri(f.createURI(ProfileImpl.profileNamespace, "order"));
        ProfileImpl.setProfileAdministratorUri(f.createURI(ProfileImpl.profileNamespace, "hasProfileAdministrator"));
        
        ProfileImpl.setProfileAllowImplicitQueryInclusionsUri(f.createURI(ProfileImpl.profileNamespace,
                "allowImplicitQueryInclusions"));
        ProfileImpl.setProfileAllowImplicitProviderInclusionsUri(f.createURI(ProfileImpl.profileNamespace,
                "allowImplicitProviderInclusions"));
        ProfileImpl.setProfileAllowImplicitRdfRuleInclusionsUri(f.createURI(ProfileImpl.profileNamespace,
                "allowImplicitRdfRuleInclusions"));
        ProfileImpl.setProfileDefaultIncludeExcludeOrderUri(f.createURI(ProfileImpl.profileNamespace,
                "defaultProfileIncludeExcludeOrder"));
        ProfileImpl.setProfileIncludeExcludeOrderUri(f.createURI(ProfileImpl.profileNamespace,
                "profileIncludeExcludeOrder"));
        ProfileImpl.setProfileIncludeProviderInProfile(f.createURI(ProfileImpl.profileNamespace, "includesProvider"));
        ProfileImpl.setProfileExcludeProviderFromProfile(f.createURI(ProfileImpl.profileNamespace, "excludesProvider"));
        ProfileImpl.setProfileIncludeQueryInProfile(f.createURI(ProfileImpl.profileNamespace, "includesQuery"));
        ProfileImpl.setProfileExcludeQueryFromProfile(f.createURI(ProfileImpl.profileNamespace, "excludesQuery"));
        ProfileImpl.setProfileIncludeRdfRuleInProfile(f.createURI(ProfileImpl.profileNamespace, "includesRdfRule"));
        ProfileImpl.setProfileExcludeRdfRuleFromProfile(f.createURI(ProfileImpl.profileNamespace, "excludesRdfRule"));
        
        ProfileImpl.setProfileExcludeThenIncludeUri(f.createURI(ProfileImpl.profileNamespace, "excludeThenInclude"));
        ProfileImpl.setProfileIncludeThenExcludeUri(f.createURI(ProfileImpl.profileNamespace, "includeThenExclude"));
        ProfileImpl.setProfileIncludeExcludeOrderUndefinedUri(f.createURI(ProfileImpl.profileNamespace,
                "includeExcludeOrderUndefined"));
    }
    
    public ProfileImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        boolean defaultProfileIncludeExcludeOrderValidationFailed = true;
        
        for(Statement nextStatement : inputStatements)
        {
            if(ProfileImpl._DEBUG)
            {
                ProfileImpl.log.debug("Profile.fromRdf: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(ProfileImpl.getProfileTypeUri()))
            {
                if(ProfileImpl._TRACE)
                {
                    ProfileImpl.log.trace("Profile.fromRdf: found valid type predicate for URI: " + keyToUse);
                }
                
                // resultIsValid = true;
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.getProjectCurationStatusUri()))
            {
                this.setCurationStatus((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileTitle())
                    || nextStatement.getPredicate().equals(Constants.DC_TITLE))
            {
                this.setTitle(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileOrderUri()))
            {
                this.setOrder(RdfUtils.getIntegerFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileAdministratorUri()))
            {
                this.addProfileAdministrator((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileDefaultIncludeExcludeOrderUri()))
            {
                this.setDefaultProfileIncludeExcludeOrder((URI)nextStatement.getObject());
                
                if(this.getDefaultProfileIncludeExcludeOrder().equals(ProfileImpl.getProfileIncludeExcludeOrderUri())
                        || this.getDefaultProfileIncludeExcludeOrder().equals(
                                ProfileImpl.getProfileExcludeThenIncludeUri()))
                {
                    defaultProfileIncludeExcludeOrderValidationFailed = false;
                }
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileAllowImplicitQueryInclusionsUri()))
            {
                this.setAllowImplicitQueryTypeInclusions(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileAllowImplicitProviderInclusionsUri()))
            {
                this.setAllowImplicitProviderInclusions(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileAllowImplicitRdfRuleInclusionsUri()))
            {
                this.setAllowImplicitRdfRuleInclusions(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileIncludeProviderInProfile()))
            {
                this.addIncludeProvider((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileExcludeProviderFromProfile()))
            {
                this.addExcludeProvider((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileIncludeQueryInProfile()))
            {
                this.addIncludeQueryType((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileExcludeQueryFromProfile()))
            {
                this.addExcludeQueryType((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileIncludeRdfRuleInProfile()))
            {
                this.addIncludeRdfRule((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileExcludeRdfRuleFromProfile()))
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
                            + this.getKey());
        }
        
        if(ProfileImpl._TRACE)
        {
            ProfileImpl.log.trace("Profile.fromRdf: would have returned... result=" + this.toString());
        }
    }
    
    public ProfileImpl()
    {
    }
    
    public static boolean schemaToRdf(Repository myRepository, URI contextUri, int modelVersion)
        throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(ProfileImpl.getProfileTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            
            if(modelVersion == 1)
            {
                con.add(ProfileImpl.getProfileTitle(), RDFS.SUBPROPERTYOF,
                        f.createURI(Constants.DC_NAMESPACE + "title"), contextUri);
            }
            
            // TODO: Add description
            con.add(ProfileImpl.getProfileOrderUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileOrderUri(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(ProfileImpl.getProfileOrderUri(), RDFS.DOMAIN, ProfileImpl.getProfileTypeUri(), contextUri);
            con.add(ProfileImpl.getProfileOrderUri(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            // TODO: Add description
            con.add(ProfileImpl.getProfileAdministratorUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileAdministratorUri(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(ProfileImpl.getProfileAdministratorUri(), RDFS.DOMAIN, ProfileImpl.getProfileTypeUri(), contextUri);
            con.add(ProfileImpl.getProfileAdministratorUri(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            // TODO: Add description
            con.add(ProfileImpl.getProfileDefaultIncludeExcludeOrderUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileDefaultIncludeExcludeOrderUri(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(ProfileImpl.getProfileDefaultIncludeExcludeOrderUri(), RDFS.DOMAIN,
                    ProfileImpl.getProfileTypeUri(), contextUri);
            con.add(ProfileImpl.getProfileDefaultIncludeExcludeOrderUri(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProfileImpl.getProfileAllowImplicitQueryInclusionsUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileAllowImplicitQueryInclusionsUri(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(ProfileImpl.getProfileAllowImplicitQueryInclusionsUri(), RDFS.DOMAIN,
                    ProfileImpl.getProfileTypeUri(), contextUri);
            con.add(ProfileImpl.getProfileAllowImplicitQueryInclusionsUri(), RDFS.LABEL, f.createLiteral("."),
                    contextUri);
            
            con.add(ProfileImpl.getProfileAllowImplicitProviderInclusionsUri(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextUri);
            con.add(ProfileImpl.getProfileAllowImplicitProviderInclusionsUri(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(ProfileImpl.getProfileAllowImplicitProviderInclusionsUri(), RDFS.DOMAIN,
                    ProfileImpl.getProfileTypeUri(), contextUri);
            con.add(ProfileImpl.getProfileAllowImplicitProviderInclusionsUri(), RDFS.LABEL, f.createLiteral("."),
                    contextUri);
            
            con.add(ProfileImpl.getProfileAllowImplicitRdfRuleInclusionsUri(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextUri);
            con.add(ProfileImpl.getProfileAllowImplicitRdfRuleInclusionsUri(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(ProfileImpl.getProfileAllowImplicitRdfRuleInclusionsUri(), RDFS.DOMAIN,
                    ProfileImpl.getProfileTypeUri(), contextUri);
            con.add(ProfileImpl.getProfileAllowImplicitRdfRuleInclusionsUri(), RDFS.LABEL, f.createLiteral("."),
                    contextUri);
            
            con.add(ProfileImpl.getProfileIncludeProviderInProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileIncludeProviderInProfile(), RDFS.RANGE, ProviderImpl.getProviderTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileIncludeProviderInProfile(), RDFS.DOMAIN, ProfileImpl.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileIncludeProviderInProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProfileImpl.getProfileExcludeProviderFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileExcludeProviderFromProfile(), RDFS.RANGE, ProviderImpl.getProviderTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileExcludeProviderFromProfile(), RDFS.DOMAIN, ProfileImpl.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileExcludeProviderFromProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProfileImpl.getProfileIncludeQueryInProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileIncludeQueryInProfile(), RDFS.RANGE, QueryTypeImpl.getQueryTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileIncludeQueryInProfile(), RDFS.DOMAIN, ProfileImpl.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileIncludeQueryInProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProfileImpl.getProfileExcludeQueryFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileExcludeQueryFromProfile(), RDFS.RANGE, QueryTypeImpl.getQueryTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileExcludeQueryFromProfile(), RDFS.DOMAIN, ProfileImpl.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileExcludeQueryFromProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProfileImpl.getProfileIncludeRdfRuleInProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileIncludeRdfRuleInProfile(), RDFS.RANGE,
                    NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextUri);
            con.add(ProfileImpl.getProfileIncludeRdfRuleInProfile(), RDFS.DOMAIN, ProfileImpl.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileIncludeRdfRuleInProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProfileImpl.getProfileExcludeRdfRuleFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileExcludeRdfRuleFromProfile(), RDFS.RANGE,
                    NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextUri);
            con.add(ProfileImpl.getProfileExcludeRdfRuleFromProfile(), RDFS.DOMAIN, ProfileImpl.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileExcludeRdfRuleFromProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProfileImpl.getProfileExcludeThenIncludeUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileExcludeThenIncludeUri(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(ProfileImpl.getProfileExcludeThenIncludeUri(), RDFS.DOMAIN, ProfileImpl.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileExcludeThenIncludeUri(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProfileImpl.getProfileIncludeThenExcludeUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileIncludeThenExcludeUri(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(ProfileImpl.getProfileIncludeThenExcludeUri(), RDFS.DOMAIN, ProfileImpl.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileIncludeThenExcludeUri(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri(), RDFS.DOMAIN,
                    ProfileImpl.getProfileTypeUri(), contextUri);
            con.add(ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri(), RDFS.LABEL, f.createLiteral("."),
                    contextUri);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
            {
                con.rollback();
            }
            
            ProfileImpl.log.error("RepositoryException: " + re.getMessage());
        }
        finally
        {
            if(con != null)
            {
                con.close();
            }
        }
        
        return false;
    }
    
    @Override
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            URI profileInstanceUri = this.getKey();
            
            Literal titleLiteral;
            
            if(title == null)
            {
                titleLiteral = f.createLiteral("");
            }
            else
            {
                titleLiteral = f.createLiteral(title);
            }
            
            Literal orderLiteral = f.createLiteral(order);
            Literal allowImplicitQueryInclusionsLiteral = f.createLiteral(allowImplicitQueryInclusions);
            Literal allowImplicitProviderInclusionsLiteral = f.createLiteral(allowImplicitProviderInclusions);
            Literal allowImplicitRdfRuleInclusionsLiteral = f.createLiteral(allowImplicitRdfRuleInclusions);
            URI defaultProfileIncludeExcludeOrderLiteral = defaultProfileIncludeExcludeOrder;
            
            URI curationStatusLiteral = null;
            
            if(curationStatus == null)
            {
                curationStatusLiteral = ProjectImpl.getProjectNotCuratedUri();
            }
            else
            {
                curationStatusLiteral = curationStatus;
            }
            
            // log.info("About to add to the repository");
            
            con.setAutoCommit(false);
            
            con.add(profileInstanceUri, RDF.TYPE, ProfileImpl.getProfileTypeUri(), keyToUse);
            
            con.add(profileInstanceUri, ProjectImpl.getProjectCurationStatusUri(), curationStatusLiteral, keyToUse);
            
            // log.info("About to add to the repository 2");
            if(modelVersion == 1)
            {
                con.add(profileInstanceUri, ProfileImpl.getProfileTitle(), titleLiteral, keyToUse);
            }
            else
            {
                con.add(profileInstanceUri, Constants.DC_TITLE, titleLiteral, keyToUse);
            }
            
            // log.info("About to add to the repository 3");
            con.add(profileInstanceUri, ProfileImpl.getProfileOrderUri(), orderLiteral, keyToUse);
            
            // log.info("About to add to the repository 4");
            con.add(profileInstanceUri, ProfileImpl.getProfileAllowImplicitQueryInclusionsUri(),
                    allowImplicitQueryInclusionsLiteral, keyToUse);
            
            // log.info("About to add to the repository 5");
            con.add(profileInstanceUri, ProfileImpl.getProfileAllowImplicitProviderInclusionsUri(),
                    allowImplicitProviderInclusionsLiteral, keyToUse);
            
            // log.info("About to add to the repository 6");
            con.add(profileInstanceUri, ProfileImpl.getProfileAllowImplicitRdfRuleInclusionsUri(),
                    allowImplicitRdfRuleInclusionsLiteral, keyToUse);
            
            // log.info("About to add to the repository 7");
            con.add(profileInstanceUri, ProfileImpl.getProfileDefaultIncludeExcludeOrderUri(),
                    defaultProfileIncludeExcludeOrderLiteral, keyToUse);
            
            // log.info("About to add array based information");
            
            if(includeProviders != null)
            {
                
                for(URI nextIncludeProviders : includeProviders)
                {
                    con.add(profileInstanceUri, ProfileImpl.getProfileIncludeProviderInProfile(), nextIncludeProviders,
                            keyToUse);
                }
            }
            
            if(excludeProviders != null)
            {
                
                for(URI nextExcludeProviders : excludeProviders)
                {
                    con.add(profileInstanceUri, ProfileImpl.getProfileExcludeProviderFromProfile(),
                            nextExcludeProviders, keyToUse);
                }
            }
            
            if(profileAdministrators != null)
            {
                
                for(URI nextProfileAdministrator : profileAdministrators)
                {
                    con.add(profileInstanceUri, ProfileImpl.getProfileAdministratorUri(), nextProfileAdministrator,
                            keyToUse);
                }
            }
            
            if(includeQueries != null)
            {
                
                for(URI nextIncludeQuery : includeQueries)
                {
                    if(ProfileImpl._TRACE)
                    {
                        ProfileImpl.log.trace("Profile.toRdf: nextIncludeQuery=" + nextIncludeQuery);
                    }
                    
                    con.add(profileInstanceUri, ProfileImpl.getProfileIncludeQueryInProfile(), nextIncludeQuery,
                            keyToUse);
                }
            }
            
            if(excludeQueries != null)
            {
                
                for(URI nextExcludeQuery : excludeQueries)
                {
                    if(ProfileImpl._TRACE)
                    {
                        ProfileImpl.log.trace("Profile.toRdf: nextExcludeQuery=" + nextExcludeQuery);
                    }
                    
                    con.add(profileInstanceUri, ProfileImpl.getProfileExcludeQueryFromProfile(), nextExcludeQuery,
                            keyToUse);
                }
            }
            
            if(includeRdfRules != null)
            {
                
                for(URI nextIncludeRdfRules : includeRdfRules)
                {
                    con.add(profileInstanceUri, ProfileImpl.getProfileIncludeRdfRuleInProfile(), nextIncludeRdfRules,
                            keyToUse);
                }
            }
            
            if(excludeRdfRules != null)
            {
                
                for(URI nextExcludeRdfRules : excludeRdfRules)
                {
                    con.add(profileInstanceUri, ProfileImpl.getProfileExcludeRdfRuleFromProfile(), nextExcludeRdfRules,
                            keyToUse);
                }
            }
            
            if(unrecognisedStatements != null)
            {
                
                for(Statement nextUnrecognisedStatement : unrecognisedStatements)
                {
                    con.add(nextUnrecognisedStatement, keyToUse);
                }
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(RepositoryException re)
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
        StringBuilder result = new StringBuilder();
        
        result.append("key=").append(getKey().stringValue());
        result.append("order=").append(getOrder());
        
        return result.toString();
    }
    
    @Override
    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        String prefix = "profile_";
        
        return sb.toString();
    }
    
    @Override
    public String toHtml()
    {
        StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        String prefix = "profile_";
        
        return sb.toString();
    }
    
    /**
     * @return the key
     */
    @Override
    public URI getKey()
    {
        return key;
    }
    
    /**
     * @param key
     *            the key to set
     */
    @Override
    public void setKey(String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }
    
    @Override
    public void setKey(URI nextKey)
    {
        this.key = nextKey;
    }
    
    /**
     * @return the namespace used to represent objects of this type by default
     */
    @Override
    public String getDefaultNamespace()
    {
        return ProfileImpl.defaultNamespace;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Collection<URI> getElementTypes()
    {
        Collection<URI> results = new ArrayList<URI>(1);
        
        results.add(ProfileImpl.getProfileTypeUri());
        
        return results;
    }
    
    @Override
    public int compareTo(Profile otherProfile)
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
    
    public static URI getExcludeThenIncludeUri()
    {
        return ProfileImpl.getProfileExcludeThenIncludeUri();
    }
    
    public static URI getIncludeThenExcludeUri()
    {
        return ProfileImpl.getProfileIncludeThenExcludeUri();
    }
    
    public static URI getProfileIncludeExcludeOrderUri()
    {
        return ProfileImpl.profileIncludeExcludeOrderUri;
    }
    
    public static URI getProfileIncludeExcludeOrderUndefinedUri()
    {
        return ProfileImpl.profileIncludeExcludeOrderUndefinedUri;
    }
    
    @Override
    public int getOrder()
    {
        return order;
    }
    
    @Override
    public void setOrder(int order)
    {
        this.order = order;
    }
    
    @Override
    public String getTitle()
    {
        return title;
    }
    
    @Override
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    @Override
    public URI getDefaultProfileIncludeExcludeOrder()
    {
        return defaultProfileIncludeExcludeOrder;
    }
    
    @Override
    public void setDefaultProfileIncludeExcludeOrder(URI defaultProfileIncludeExcludeOrder)
    {
        this.defaultProfileIncludeExcludeOrder = defaultProfileIncludeExcludeOrder;
    }
    
    @Override
    public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    @Override
    public URI getCurationStatus()
    {
        return curationStatus;
    }
    
    @Override
    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        unrecognisedStatements.add(unrecognisedStatement);
    }
    
    @Override
    public Collection<Statement> getUnrecognisedStatements()
    {
        return unrecognisedStatements;
    }
    
    @Override
    public boolean getAllowImplicitQueryTypeInclusions()
    {
        return allowImplicitQueryInclusions;
    }
    
    @Override
    public void setAllowImplicitQueryTypeInclusions(boolean allowImplicitQueryInclusions)
    {
        this.allowImplicitQueryInclusions = allowImplicitQueryInclusions;
    }
    
    @Override
    public boolean getAllowImplicitProviderInclusions()
    {
        return allowImplicitProviderInclusions;
    }
    
    @Override
    public void setAllowImplicitProviderInclusions(boolean allowImplicitProviderInclusions)
    {
        this.allowImplicitProviderInclusions = allowImplicitProviderInclusions;
    }
    
    @Override
    public boolean getAllowImplicitRdfRuleInclusions()
    {
        return allowImplicitRdfRuleInclusions;
    }
    
    @Override
    public void setAllowImplicitRdfRuleInclusions(boolean allowImplicitRdfRuleInclusions)
    {
        this.allowImplicitRdfRuleInclusions = allowImplicitRdfRuleInclusions;
    }
    
    @Override
    public void addProfileAdministrator(URI profileAdministrator)
    {
        if(this.profileAdministrators == null)
        {
            this.profileAdministrators = new LinkedList<URI>();
        }
        
        this.profileAdministrators.add(profileAdministrator);
    }
    
    @Override
    public Collection<URI> getProfileAdministrators()
    {
        return profileAdministrators;
    }
    
    @Override
    public Collection<URI> getIncludeProviders()
    {
        return includeProviders;
    }
    
    @Override
    public Collection<URI> getExcludeProviders()
    {
        return excludeProviders;
    }
    
    @Override
    public Collection<URI> getIncludeQueryTypes()
    {
        return includeQueries;
    }
    
    @Override
    public Collection<URI> getExcludeQueryTypes()
    {
        return excludeQueries;
    }
    
    @Override
    public Collection<URI> getIncludeRdfRules()
    {
        return includeRdfRules;
    }
    
    @Override
    public Collection<URI> getExcludeRdfRules()
    {
        return excludeRdfRules;
    }
    
    /**
     * @param profileTypeUri
     *            the profileTypeUri to set
     */
    public static void setProfileTypeUri(URI profileTypeUri)
    {
        ProfileImpl.profileTypeUri = profileTypeUri;
    }
    
    /**
     * @return the profileTypeUri
     */
    public static URI getProfileTypeUri()
    {
        return ProfileImpl.profileTypeUri;
    }
    
    /**
     * @param profileTitle
     *            the profileTitle to set
     */
    public static void setProfileTitle(URI profileTitle)
    {
        ProfileImpl.profileTitle = profileTitle;
    }
    
    /**
     * @return the profileTitle
     */
    public static URI getProfileTitle()
    {
        return ProfileImpl.profileTitle;
    }
    
    /**
     * @param profileAdministratorUri
     *            the profileAdministratorUri to set
     */
    public static void setProfileAdministratorUri(URI profileAdministratorUri)
    {
        ProfileImpl.profileAdministratorUri = profileAdministratorUri;
    }
    
    /**
     * @return the profileAdministratorUri
     */
    public static URI getProfileAdministratorUri()
    {
        return ProfileImpl.profileAdministratorUri;
    }
    
    /**
     * @param profileOrderUri
     *            the profileOrderUri to set
     */
    public static void setProfileOrderUri(URI profileOrderUri)
    {
        ProfileImpl.profileOrderUri = profileOrderUri;
    }
    
    /**
     * @return the profileOrderUri
     */
    public static URI getProfileOrderUri()
    {
        return ProfileImpl.profileOrderUri;
    }
    
    /**
     * @param profileAllowImplicitQueryInclusionsUri
     *            the profileAllowImplicitQueryInclusionsUri to set
     */
    public static void setProfileAllowImplicitQueryInclusionsUri(URI profileAllowImplicitQueryInclusionsUri)
    {
        ProfileImpl.profileAllowImplicitQueryInclusionsUri = profileAllowImplicitQueryInclusionsUri;
    }
    
    /**
     * @return the profileAllowImplicitQueryInclusionsUri
     */
    public static URI getProfileAllowImplicitQueryInclusionsUri()
    {
        return ProfileImpl.profileAllowImplicitQueryInclusionsUri;
    }
    
    /**
     * @param profileAllowImplicitProviderInclusionsUri
     *            the profileAllowImplicitProviderInclusionsUri to set
     */
    public static void setProfileAllowImplicitProviderInclusionsUri(URI profileAllowImplicitProviderInclusionsUri)
    {
        ProfileImpl.profileAllowImplicitProviderInclusionsUri = profileAllowImplicitProviderInclusionsUri;
    }
    
    /**
     * @return the profileAllowImplicitProviderInclusionsUri
     */
    public static URI getProfileAllowImplicitProviderInclusionsUri()
    {
        return ProfileImpl.profileAllowImplicitProviderInclusionsUri;
    }
    
    /**
     * @param profileAllowImplicitRdfRuleInclusionsUri
     *            the profileAllowImplicitRdfRuleInclusionsUri to set
     */
    public static void setProfileAllowImplicitRdfRuleInclusionsUri(URI profileAllowImplicitRdfRuleInclusionsUri)
    {
        ProfileImpl.profileAllowImplicitRdfRuleInclusionsUri = profileAllowImplicitRdfRuleInclusionsUri;
    }
    
    /**
     * @return the profileAllowImplicitRdfRuleInclusionsUri
     */
    public static URI getProfileAllowImplicitRdfRuleInclusionsUri()
    {
        return ProfileImpl.profileAllowImplicitRdfRuleInclusionsUri;
    }
    
    /**
     * @param profileDefaultIncludeExcludeOrderUri
     *            the profileDefaultIncludeExcludeOrderUri to set
     */
    public static void setProfileDefaultIncludeExcludeOrderUri(URI profileDefaultIncludeExcludeOrderUri)
    {
        ProfileImpl.profileDefaultIncludeExcludeOrderUri = profileDefaultIncludeExcludeOrderUri;
    }
    
    /**
     * @return the profileDefaultIncludeExcludeOrderUri
     */
    public static URI getProfileDefaultIncludeExcludeOrderUri()
    {
        return ProfileImpl.profileDefaultIncludeExcludeOrderUri;
    }
    
    /**
     * @param profileIncludeExcludeOrderUri
     *            the profileIncludeExcludeOrderUri to set
     */
    public static void setProfileIncludeExcludeOrderUri(URI profileIncludeExcludeOrderUri)
    {
        ProfileImpl.profileIncludeExcludeOrderUri = profileIncludeExcludeOrderUri;
    }
    
    /**
     * @param profileIncludeProviderInProfile
     *            the profileIncludeProviderInProfile to set
     */
    public static void setProfileIncludeProviderInProfile(URI profileIncludeProviderInProfile)
    {
        ProfileImpl.profileIncludeProviderInProfile = profileIncludeProviderInProfile;
    }
    
    /**
     * @return the profileIncludeProviderInProfile
     */
    public static URI getProfileIncludeProviderInProfile()
    {
        return ProfileImpl.profileIncludeProviderInProfile;
    }
    
    /**
     * @param profileExcludeProviderFromProfile
     *            the profileExcludeProviderFromProfile to set
     */
    public static void setProfileExcludeProviderFromProfile(URI profileExcludeProviderFromProfile)
    {
        ProfileImpl.profileExcludeProviderFromProfile = profileExcludeProviderFromProfile;
    }
    
    /**
     * @return the profileExcludeProviderFromProfile
     */
    public static URI getProfileExcludeProviderFromProfile()
    {
        return ProfileImpl.profileExcludeProviderFromProfile;
    }
    
    /**
     * @param profileIncludeQueryInProfile
     *            the profileIncludeQueryInProfile to set
     */
    public static void setProfileIncludeQueryInProfile(URI profileIncludeQueryInProfile)
    {
        ProfileImpl.profileIncludeQueryInProfile = profileIncludeQueryInProfile;
    }
    
    /**
     * @return the profileIncludeQueryInProfile
     */
    public static URI getProfileIncludeQueryInProfile()
    {
        return ProfileImpl.profileIncludeQueryInProfile;
    }
    
    /**
     * @param profileExcludeQueryFromProfile
     *            the profileExcludeQueryFromProfile to set
     */
    public static void setProfileExcludeQueryFromProfile(URI profileExcludeQueryFromProfile)
    {
        ProfileImpl.profileExcludeQueryFromProfile = profileExcludeQueryFromProfile;
    }
    
    /**
     * @return the profileExcludeQueryFromProfile
     */
    public static URI getProfileExcludeQueryFromProfile()
    {
        return ProfileImpl.profileExcludeQueryFromProfile;
    }
    
    /**
     * @param profileIncludeRdfRuleInProfile
     *            the profileIncludeRdfRuleInProfile to set
     */
    public static void setProfileIncludeRdfRuleInProfile(URI profileIncludeRdfRuleInProfile)
    {
        ProfileImpl.profileIncludeRdfRuleInProfile = profileIncludeRdfRuleInProfile;
    }
    
    /**
     * @return the profileIncludeRdfRuleInProfile
     */
    public static URI getProfileIncludeRdfRuleInProfile()
    {
        return ProfileImpl.profileIncludeRdfRuleInProfile;
    }
    
    /**
     * @param profileExcludeRdfRuleFromProfile
     *            the profileExcludeRdfRuleFromProfile to set
     */
    public static void setProfileExcludeRdfRuleFromProfile(URI profileExcludeRdfRuleFromProfile)
    {
        ProfileImpl.profileExcludeRdfRuleFromProfile = profileExcludeRdfRuleFromProfile;
    }
    
    /**
     * @return the profileExcludeRdfRuleFromProfile
     */
    public static URI getProfileExcludeRdfRuleFromProfile()
    {
        return ProfileImpl.profileExcludeRdfRuleFromProfile;
    }
    
    /**
     * @param profileExcludeThenIncludeUri
     *            the profileExcludeThenIncludeUri to set
     */
    public static void setProfileExcludeThenIncludeUri(URI profileExcludeThenIncludeUri)
    {
        ProfileImpl.profileExcludeThenIncludeUri = profileExcludeThenIncludeUri;
    }
    
    /**
     * @return the profileExcludeThenIncludeUri
     */
    public static URI getProfileExcludeThenIncludeUri()
    {
        return ProfileImpl.profileExcludeThenIncludeUri;
    }
    
    /**
     * @param profileIncludeThenExcludeUri
     *            the profileIncludeThenExcludeUri to set
     */
    public static void setProfileIncludeThenExcludeUri(URI profileIncludeThenExcludeUri)
    {
        ProfileImpl.profileIncludeThenExcludeUri = profileIncludeThenExcludeUri;
    }
    
    /**
     * @return the profileIncludeThenExcludeUri
     */
    public static URI getProfileIncludeThenExcludeUri()
    {
        return ProfileImpl.profileIncludeThenExcludeUri;
    }
    
    /**
     * @param profileIncludeExcludeOrderUndefinedUri
     *            the profileIncludeExcludeOrderUndefinedUri to set
     */
    public static void setProfileIncludeExcludeOrderUndefinedUri(URI profileIncludeExcludeOrderUndefinedUri)
    {
        ProfileImpl.profileIncludeExcludeOrderUndefinedUri = profileIncludeExcludeOrderUndefinedUri;
    }
    
    @Override
    public void addIncludeProvider(URI includeProvider)
    {
        if(this.includeProviders == null)
        {
            this.includeProviders = new LinkedList<URI>();
        }
        
        this.includeProviders.add(includeProvider);
    }
    
    @Override
    public void addExcludeProvider(URI excludeProvider)
    {
        if(this.excludeProviders == null)
        {
            this.excludeProviders = new LinkedList<URI>();
        }
        
        this.excludeProviders.add(excludeProvider);
    }
    
    @Override
    public void addIncludeQueryType(URI includeQuery)
    {
        if(this.includeQueries == null)
        {
            this.includeQueries = new LinkedList<URI>();
        }
        
        this.includeQueries.add(includeQuery);
    }
    
    @Override
    public void addExcludeQueryType(URI excludeQuery)
    {
        if(this.excludeQueries == null)
        {
            this.excludeQueries = new LinkedList<URI>();
        }
        
        this.excludeQueries.add(excludeQuery);
    }
    
    @Override
    public void addIncludeRdfRule(URI includeRdfRule)
    {
        if(this.includeRdfRules == null)
        {
            this.includeRdfRules = new LinkedList<URI>();
        }
        
        this.includeRdfRules.add(includeRdfRule);
    }
    
    @Override
    public void addExcludeRdfRule(URI excludeRdfRule)
    {
        if(this.excludeRdfRules == null)
        {
            this.excludeRdfRules = new LinkedList<URI>();
        }
        
        this.excludeRdfRules.add(excludeRdfRule);
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
        result = prime * result + (allowImplicitProviderInclusions ? 1231 : 1237);
        result = prime * result + (allowImplicitQueryInclusions ? 1231 : 1237);
        result = prime * result + (allowImplicitRdfRuleInclusions ? 1231 : 1237);
        result = prime * result + ((curationStatus == null) ? 0 : curationStatus.hashCode());
        result =
                prime
                        * result
                        + ((defaultProfileIncludeExcludeOrder == null) ? 0 : defaultProfileIncludeExcludeOrder
                                .hashCode());
        result = prime * result + ((excludeProviders == null) ? 0 : excludeProviders.hashCode());
        result = prime * result + ((excludeQueries == null) ? 0 : excludeQueries.hashCode());
        result = prime * result + ((excludeRdfRules == null) ? 0 : excludeRdfRules.hashCode());
        result = prime * result + ((includeProviders == null) ? 0 : includeProviders.hashCode());
        result = prime * result + ((includeQueries == null) ? 0 : includeQueries.hashCode());
        result = prime * result + ((includeRdfRules == null) ? 0 : includeRdfRules.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + order;
        result = prime * result + ((profileAdministrators == null) ? 0 : profileAdministrators.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }
    
    /*
     * (non-Javadoc)
     * 
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
        if(!(obj instanceof ProfileImpl))
        {
            return false;
        }
        ProfileImpl other = (ProfileImpl)obj;
        if(allowImplicitProviderInclusions != other.allowImplicitProviderInclusions)
        {
            return false;
        }
        if(allowImplicitQueryInclusions != other.allowImplicitQueryInclusions)
        {
            return false;
        }
        if(allowImplicitRdfRuleInclusions != other.allowImplicitRdfRuleInclusions)
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
        if(defaultProfileIncludeExcludeOrder == null)
        {
            if(other.defaultProfileIncludeExcludeOrder != null)
            {
                return false;
            }
        }
        else if(!defaultProfileIncludeExcludeOrder.equals(other.defaultProfileIncludeExcludeOrder))
        {
            return false;
        }
        if(excludeProviders == null)
        {
            if(other.excludeProviders != null)
            {
                return false;
            }
        }
        else if(!excludeProviders.equals(other.excludeProviders))
        {
            return false;
        }
        if(excludeQueries == null)
        {
            if(other.excludeQueries != null)
            {
                return false;
            }
        }
        else if(!excludeQueries.equals(other.excludeQueries))
        {
            return false;
        }
        if(excludeRdfRules == null)
        {
            if(other.excludeRdfRules != null)
            {
                return false;
            }
        }
        else if(!excludeRdfRules.equals(other.excludeRdfRules))
        {
            return false;
        }
        if(includeProviders == null)
        {
            if(other.includeProviders != null)
            {
                return false;
            }
        }
        else if(!includeProviders.equals(other.includeProviders))
        {
            return false;
        }
        if(includeQueries == null)
        {
            if(other.includeQueries != null)
            {
                return false;
            }
        }
        else if(!includeQueries.equals(other.includeQueries))
        {
            return false;
        }
        if(includeRdfRules == null)
        {
            if(other.includeRdfRules != null)
            {
                return false;
            }
        }
        else if(!includeRdfRules.equals(other.includeRdfRules))
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
        if(order != other.order)
        {
            return false;
        }
        if(profileAdministrators == null)
        {
            if(other.profileAdministrators != null)
            {
                return false;
            }
        }
        else if(!profileAdministrators.equals(other.profileAdministrators))
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
        return true;
    }
}
