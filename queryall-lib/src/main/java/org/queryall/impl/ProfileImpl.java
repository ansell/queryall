package org.queryall.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

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
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.querytype.QueryTypeSchema;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfileImpl implements Profile, Comparable<Profile>
{
    private static final Logger log = LoggerFactory.getLogger(ProfileImpl.class);
    private static final boolean _TRACE = ProfileImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = ProfileImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = ProfileImpl.log.isInfoEnabled();
    
    /**
     * @return the profileAdministratorUri
     */
    public static URI getProfileAdministratorUri()
    {
        return ProfileImpl.profileAdministratorUri;
    }
    
    /**
     * @return the profileAllowImplicitProviderInclusionsUri
     */
    public static URI getProfileAllowImplicitProviderInclusionsUri()
    {
        return ProfileImpl.profileAllowImplicitProviderInclusionsUri;
    }
    
    /**
     * @return the profileAllowImplicitQueryInclusionsUri
     */
    public static URI getProfileAllowImplicitQueryInclusionsUri()
    {
        return ProfileImpl.profileAllowImplicitQueryInclusionsUri;
    }
    
    /**
     * @return the profileAllowImplicitRdfRuleInclusionsUri
     */
    public static URI getProfileAllowImplicitRdfRuleInclusionsUri()
    {
        return ProfileImpl.profileAllowImplicitRdfRuleInclusionsUri;
    }
    
    /**
     * @return the profileDefaultIncludeExcludeOrderUri
     */
    public static URI getProfileDefaultIncludeExcludeOrderUri()
    {
        return ProfileImpl.profileDefaultIncludeExcludeOrderUri;
    }
    
    /**
     * @return the profileExcludeProviderFromProfile
     */
    public static URI getProfileExcludeProviderFromProfile()
    {
        return ProfileImpl.profileExcludeProviderFromProfile;
    }
    
    /**
     * @return the profileExcludeQueryFromProfile
     */
    public static URI getProfileExcludeQueryFromProfile()
    {
        return ProfileImpl.profileExcludeQueryFromProfile;
    }
    
    /**
     * @return the profileExcludeRdfRuleFromProfile
     */
    public static URI getProfileExcludeRdfRuleFromProfile()
    {
        return ProfileImpl.profileExcludeRdfRuleFromProfile;
    }
    
    /**
     * @return the profileExcludeThenIncludeUri
     */
    public static URI getProfileExcludeThenIncludeUri()
    {
        return ProfileImpl.profileExcludeThenIncludeUri;
    }
    
    /**
     * @return the profileIncludeProviderInProfile
     */
    public static URI getProfileIncludeProviderInProfile()
    {
        return ProfileImpl.profileIncludeProviderInProfile;
    }
    
    /**
     * @return the profileIncludeQueryInProfile
     */
    public static URI getProfileIncludeQueryInProfile()
    {
        return ProfileImpl.profileIncludeQueryInProfile;
    }
    
    /**
     * @return the profileIncludeRdfRuleInProfile
     */
    public static URI getProfileIncludeRdfRuleInProfile()
    {
        return ProfileImpl.profileIncludeRdfRuleInProfile;
    }
    
    /**
     * @return the profileIncludeThenExcludeUri
     */
    public static URI getProfileIncludeThenExcludeUri()
    {
        return ProfileImpl.profileIncludeThenExcludeUri;
    }
    
    /**
     * @return the profileOrderUri
     */
    public static URI getProfileOrderUri()
    {
        return ProfileImpl.profileOrderUri;
    }
    
    /**
     * @return the profileTitle
     */
    public static URI getProfileTitle()
    {
        return ProfileImpl.profileTitle;
    }
    
    /**
     * @return the profileTypeUri
     */
    public static URI getProfileTypeUri()
    {
        return ProfileImpl.profileTypeUri;
    }
    
    /**
     * @param profileAdministratorUri
     *            the profileAdministratorUri to set
     */
    public static void setProfileAdministratorUri(final URI profileAdministratorUri)
    {
        ProfileImpl.profileAdministratorUri = profileAdministratorUri;
    }
    
    /**
     * @param profileAllowImplicitProviderInclusionsUri
     *            the profileAllowImplicitProviderInclusionsUri to set
     */
    public static void setProfileAllowImplicitProviderInclusionsUri(final URI profileAllowImplicitProviderInclusionsUri)
    {
        ProfileImpl.profileAllowImplicitProviderInclusionsUri = profileAllowImplicitProviderInclusionsUri;
    }
    
    /**
     * @param profileAllowImplicitQueryInclusionsUri
     *            the profileAllowImplicitQueryInclusionsUri to set
     */
    public static void setProfileAllowImplicitQueryInclusionsUri(final URI profileAllowImplicitQueryInclusionsUri)
    {
        ProfileImpl.profileAllowImplicitQueryInclusionsUri = profileAllowImplicitQueryInclusionsUri;
    }
    
    /**
     * @param profileAllowImplicitRdfRuleInclusionsUri
     *            the profileAllowImplicitRdfRuleInclusionsUri to set
     */
    public static void setProfileAllowImplicitRdfRuleInclusionsUri(final URI profileAllowImplicitRdfRuleInclusionsUri)
    {
        ProfileImpl.profileAllowImplicitRdfRuleInclusionsUri = profileAllowImplicitRdfRuleInclusionsUri;
    }
    
    /**
     * @param profileDefaultIncludeExcludeOrderUri
     *            the profileDefaultIncludeExcludeOrderUri to set
     */
    public static void setProfileDefaultIncludeExcludeOrderUri(final URI profileDefaultIncludeExcludeOrderUri)
    {
        ProfileImpl.profileDefaultIncludeExcludeOrderUri = profileDefaultIncludeExcludeOrderUri;
    }
    
    /**
     * @param profileExcludeProviderFromProfile
     *            the profileExcludeProviderFromProfile to set
     */
    public static void setProfileExcludeProviderFromProfile(final URI profileExcludeProviderFromProfile)
    {
        ProfileImpl.profileExcludeProviderFromProfile = profileExcludeProviderFromProfile;
    }
    
    /**
     * @param profileExcludeQueryFromProfile
     *            the profileExcludeQueryFromProfile to set
     */
    public static void setProfileExcludeQueryFromProfile(final URI profileExcludeQueryFromProfile)
    {
        ProfileImpl.profileExcludeQueryFromProfile = profileExcludeQueryFromProfile;
    }
    
    /**
     * @param profileExcludeRdfRuleFromProfile
     *            the profileExcludeRdfRuleFromProfile to set
     */
    public static void setProfileExcludeRdfRuleFromProfile(final URI profileExcludeRdfRuleFromProfile)
    {
        ProfileImpl.profileExcludeRdfRuleFromProfile = profileExcludeRdfRuleFromProfile;
    }
    
    /**
     * @param profileExcludeThenIncludeUri
     *            the profileExcludeThenIncludeUri to set
     */
    public static void setProfileExcludeThenIncludeUri(final URI profileExcludeThenIncludeUri)
    {
        ProfileImpl.profileExcludeThenIncludeUri = profileExcludeThenIncludeUri;
    }
    
    /**
     * @param profileIncludeExcludeOrderUndefinedUri
     *            the profileIncludeExcludeOrderUndefinedUri to set
     */
    public static void setProfileIncludeExcludeOrderUndefinedUri(final URI profileIncludeExcludeOrderUndefinedUri)
    {
        ProfileImpl.profileIncludeExcludeOrderUndefinedUri = profileIncludeExcludeOrderUndefinedUri;
    }
    
    /**
     * @param profileIncludeExcludeOrderUri
     *            the profileIncludeExcludeOrderUri to set
     */
    public static void setProfileIncludeExcludeOrderUri(final URI profileIncludeExcludeOrderUri)
    {
        ProfileImpl.profileIncludeExcludeOrderUri = profileIncludeExcludeOrderUri;
    }
    
    /**
     * @param profileIncludeProviderInProfile
     *            the profileIncludeProviderInProfile to set
     */
    public static void setProfileIncludeProviderInProfile(final URI profileIncludeProviderInProfile)
    {
        ProfileImpl.profileIncludeProviderInProfile = profileIncludeProviderInProfile;
    }
    
    /**
     * @param profileIncludeQueryInProfile
     *            the profileIncludeQueryInProfile to set
     */
    public static void setProfileIncludeQueryInProfile(final URI profileIncludeQueryInProfile)
    {
        ProfileImpl.profileIncludeQueryInProfile = profileIncludeQueryInProfile;
    }
    
    /**
     * @param profileIncludeRdfRuleInProfile
     *            the profileIncludeRdfRuleInProfile to set
     */
    public static void setProfileIncludeRdfRuleInProfile(final URI profileIncludeRdfRuleInProfile)
    {
        ProfileImpl.profileIncludeRdfRuleInProfile = profileIncludeRdfRuleInProfile;
    }
    
    /**
     * @param profileIncludeThenExcludeUri
     *            the profileIncludeThenExcludeUri to set
     */
    public static void setProfileIncludeThenExcludeUri(final URI profileIncludeThenExcludeUri)
    {
        ProfileImpl.profileIncludeThenExcludeUri = profileIncludeThenExcludeUri;
    }
    
    /**
     * @param profileOrderUri
     *            the profileOrderUri to set
     */
    public static void setProfileOrderUri(final URI profileOrderUri)
    {
        ProfileImpl.profileOrderUri = profileOrderUri;
    }
    
    /**
     * @param profileTitle
     *            the profileTitle to set
     */
    public static void setProfileTitle(final URI profileTitle)
    {
        ProfileImpl.profileTitle = profileTitle;
    }
    
    /**
     * @param profileTypeUri
     *            the profileTypeUri to set
     */
    public static void setProfileTypeUri(final URI profileTypeUri)
    {
        ProfileImpl.profileTypeUri = profileTypeUri;
    }
    
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
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.PROFILE.getBaseURI();
        
        ProfileImpl.setProfileTypeUri(f.createURI(baseUri, "Profile"));
        ProfileImpl.setProfileTitle(f.createURI(baseUri, "Title"));
        ProfileImpl.setProfileOrderUri(f.createURI(baseUri, "order"));
        ProfileImpl.setProfileAdministratorUri(f.createURI(baseUri, "hasProfileAdministrator"));
        
        ProfileImpl.setProfileAllowImplicitQueryInclusionsUri(f.createURI(baseUri, "allowImplicitQueryInclusions"));
        ProfileImpl.setProfileAllowImplicitProviderInclusionsUri(f
                .createURI(baseUri, "allowImplicitProviderInclusions"));
        ProfileImpl.setProfileAllowImplicitRdfRuleInclusionsUri(f.createURI(baseUri, "allowImplicitRdfRuleInclusions"));
        ProfileImpl.setProfileDefaultIncludeExcludeOrderUri(f.createURI(baseUri, "defaultProfileIncludeExcludeOrder"));
        ProfileImpl.setProfileIncludeExcludeOrderUri(f.createURI(baseUri, "profileIncludeExcludeOrder"));
        ProfileImpl.setProfileIncludeProviderInProfile(f.createURI(baseUri, "includesProvider"));
        ProfileImpl.setProfileExcludeProviderFromProfile(f.createURI(baseUri, "excludesProvider"));
        ProfileImpl.setProfileIncludeQueryInProfile(f.createURI(baseUri, "includesQuery"));
        ProfileImpl.setProfileExcludeQueryFromProfile(f.createURI(baseUri, "excludesQuery"));
        ProfileImpl.setProfileIncludeRdfRuleInProfile(f.createURI(baseUri, "includesRdfRule"));
        ProfileImpl.setProfileExcludeRdfRuleFromProfile(f.createURI(baseUri, "excludesRdfRule"));
        
        ProfileImpl.setProfileExcludeThenIncludeUri(f.createURI(baseUri, "excludeThenInclude"));
        ProfileImpl.setProfileIncludeThenExcludeUri(f.createURI(baseUri, "includeThenExclude"));
        ProfileImpl.setProfileIncludeExcludeOrderUndefinedUri(f.createURI(baseUri, "includeExcludeOrderUndefined"));
    }
    
    public static URI getProfileIncludeExcludeOrderUndefinedUri()
    {
        return ProfileImpl.profileIncludeExcludeOrderUndefinedUri;
    }
    
    public static URI getProfileIncludeExcludeOrderUri()
    {
        return ProfileImpl.profileIncludeExcludeOrderUri;
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
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
            con.add(ProfileImpl.getProfileIncludeProviderInProfile(), RDFS.RANGE, ProviderSchema.getProviderTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileIncludeProviderInProfile(), RDFS.DOMAIN, ProfileImpl.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileIncludeProviderInProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProfileImpl.getProfileExcludeProviderFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileExcludeProviderFromProfile(), RDFS.RANGE, ProviderSchema.getProviderTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileExcludeProviderFromProfile(), RDFS.DOMAIN, ProfileImpl.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileExcludeProviderFromProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProfileImpl.getProfileIncludeQueryInProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileIncludeQueryInProfile(), RDFS.RANGE, QueryTypeSchema.getQueryTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileIncludeQueryInProfile(), RDFS.DOMAIN, ProfileImpl.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileIncludeQueryInProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProfileImpl.getProfileExcludeQueryFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileExcludeQueryFromProfile(), RDFS.RANGE, QueryTypeSchema.getQueryTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileExcludeQueryFromProfile(), RDFS.DOMAIN, ProfileImpl.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileExcludeQueryFromProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProfileImpl.getProfileIncludeRdfRuleInProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileIncludeRdfRuleInProfile(), RDFS.RANGE,
                    NormalisationRuleSchema.getNormalisationRuleTypeUri(), contextUri);
            con.add(ProfileImpl.getProfileIncludeRdfRuleInProfile(), RDFS.DOMAIN, ProfileImpl.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileImpl.getProfileIncludeRdfRuleInProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProfileImpl.getProfileExcludeRdfRuleFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileImpl.getProfileExcludeRdfRuleFromProfile(), RDFS.RANGE,
                    NormalisationRuleSchema.getNormalisationRuleTypeUri(), contextUri);
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
        catch(final RepositoryException re)
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
    
    public ProfileImpl()
    {
    }
    
    public ProfileImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        boolean defaultProfileIncludeExcludeOrderValidationFailed = true;
        
        for(final Statement nextStatement : inputStatements)
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
                
                if(this.getDefaultProfileIncludeExcludeOrder().equals(ProfileImpl.getProfileIncludeThenExcludeUri())
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
        if(this.curationStatus == null)
        {
            if(other.curationStatus != null)
            {
                return false;
            }
        }
        else if(!this.curationStatus.equals(other.curationStatus))
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
        if(this.key == null)
        {
            if(other.key != null)
            {
                return false;
            }
        }
        else if(!this.key.equals(other.key))
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
        if(this.title == null)
        {
            if(other.title != null)
            {
                return false;
            }
        }
        else if(!this.title.equals(other.title))
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
    
    @Override
    public URI getCurationStatus()
    {
        return this.curationStatus;
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
    public Collection<URI> getElementTypes()
    {
        final Collection<URI> results = new ArrayList<URI>(1);
        
        results.add(ProfileImpl.getProfileTypeUri());
        
        return results;
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
    
    /**
     * @return the key
     */
    @Override
    public URI getKey()
    {
        return this.key;
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
        result = prime * result + (this.allowImplicitProviderInclusions ? 1231 : 1237);
        result = prime * result + (this.allowImplicitQueryInclusions ? 1231 : 1237);
        result = prime * result + (this.allowImplicitRdfRuleInclusions ? 1231 : 1237);
        result = prime * result + ((this.curationStatus == null) ? 0 : this.curationStatus.hashCode());
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
        result = prime * result + ((this.key == null) ? 0 : this.key.hashCode());
        result = prime * result + this.order;
        result = prime * result + ((this.profileAdministrators == null) ? 0 : this.profileAdministrators.hashCode());
        result = prime * result + ((this.title == null) ? 0 : this.title.hashCode());
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
    public void setCurationStatus(final URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    @Override
    public void setDefaultProfileIncludeExcludeOrder(final URI defaultProfileIncludeExcludeOrder)
    {
        this.defaultProfileIncludeExcludeOrder = defaultProfileIncludeExcludeOrder;
    }
    
    /**
     * @param key
     *            the key to set
     */
    @Override
    public void setKey(final String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }
    
    @Override
    public void setKey(final URI nextKey)
    {
        this.key = nextKey;
    }
    
    @Override
    public void setOrder(final int order)
    {
        this.order = order;
    }
    
    @Override
    public void setTitle(final String title)
    {
        this.title = title;
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
    public boolean toRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            final URI profileInstanceUri = this.getKey();
            
            Literal titleLiteral;
            
            if(this.title == null)
            {
                titleLiteral = f.createLiteral("");
            }
            else
            {
                titleLiteral = f.createLiteral(this.title);
            }
            
            final Literal orderLiteral = f.createLiteral(this.order);
            final Literal allowImplicitQueryInclusionsLiteral = f.createLiteral(this.allowImplicitQueryInclusions);
            final Literal allowImplicitProviderInclusionsLiteral =
                    f.createLiteral(this.allowImplicitProviderInclusions);
            final Literal allowImplicitRdfRuleInclusionsLiteral = f.createLiteral(this.allowImplicitRdfRuleInclusions);
            final URI defaultProfileIncludeExcludeOrderLiteral = this.defaultProfileIncludeExcludeOrder;
            
            URI curationStatusLiteral = null;
            
            if(this.curationStatus == null)
            {
                curationStatusLiteral = ProjectImpl.getProjectNotCuratedUri();
            }
            else
            {
                curationStatusLiteral = this.curationStatus;
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
            
            if(this.includeProviders != null)
            {
                
                for(final URI nextIncludeProviders : this.includeProviders)
                {
                    con.add(profileInstanceUri, ProfileImpl.getProfileIncludeProviderInProfile(), nextIncludeProviders,
                            keyToUse);
                }
            }
            
            if(this.excludeProviders != null)
            {
                
                for(final URI nextExcludeProviders : this.excludeProviders)
                {
                    con.add(profileInstanceUri, ProfileImpl.getProfileExcludeProviderFromProfile(),
                            nextExcludeProviders, keyToUse);
                }
            }
            
            if(this.profileAdministrators != null)
            {
                
                for(final URI nextProfileAdministrator : this.profileAdministrators)
                {
                    con.add(profileInstanceUri, ProfileImpl.getProfileAdministratorUri(), nextProfileAdministrator,
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
                    
                    con.add(profileInstanceUri, ProfileImpl.getProfileIncludeQueryInProfile(), nextIncludeQuery,
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
                    
                    con.add(profileInstanceUri, ProfileImpl.getProfileExcludeQueryFromProfile(), nextExcludeQuery,
                            keyToUse);
                }
            }
            
            if(this.includeRdfRules != null)
            {
                
                for(final URI nextIncludeRdfRules : this.includeRdfRules)
                {
                    con.add(profileInstanceUri, ProfileImpl.getProfileIncludeRdfRuleInProfile(), nextIncludeRdfRules,
                            keyToUse);
                }
            }
            
            if(this.excludeRdfRules != null)
            {
                
                for(final URI nextExcludeRdfRules : this.excludeRdfRules)
                {
                    con.add(profileInstanceUri, ProfileImpl.getProfileExcludeRdfRuleFromProfile(), nextExcludeRdfRules,
                            keyToUse);
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
