/**
 * 
 */
package org.queryall.api.profile;

import org.kohsuke.MetaInfServices;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.base.QueryAllSchema;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.querytype.QueryTypeSchema;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllSchema.class)
public class ProfileSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(ProfileSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = ProfileSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = ProfileSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = ProfileSchema.LOG.isInfoEnabled();
    
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
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.PROFILE.getBaseURI();
        
        ProfileSchema.setProfileTypeUri(f.createURI(baseUri, "Profile"));
        ProfileSchema.setProfileTitle(f.createURI(baseUri, "Title"));
        ProfileSchema.setProfileOrderUri(f.createURI(baseUri, "order"));
        ProfileSchema.setProfileAdministratorUri(f.createURI(baseUri, "hasProfileAdministrator"));
        
        ProfileSchema.setProfileAllowImplicitQueryInclusionsUri(f.createURI(baseUri, "allowImplicitQueryInclusions"));
        ProfileSchema.setProfileAllowImplicitProviderInclusionsUri(f.createURI(baseUri,
                "allowImplicitProviderInclusions"));
        ProfileSchema.setProfileAllowImplicitRdfRuleInclusionsUri(f
                .createURI(baseUri, "allowImplicitRdfRuleInclusions"));
        ProfileSchema
                .setProfileDefaultIncludeExcludeOrderUri(f.createURI(baseUri, "defaultProfileIncludeExcludeOrder"));
        ProfileSchema.setProfileIncludeExcludeOrderUri(f.createURI(baseUri, "profileIncludeExcludeOrder"));
        ProfileSchema.setProfileIncludeProviderInProfile(f.createURI(baseUri, "includesProvider"));
        ProfileSchema.setProfileExcludeProviderFromProfile(f.createURI(baseUri, "excludesProvider"));
        ProfileSchema.setProfileIncludeQueryInProfile(f.createURI(baseUri, "includesQuery"));
        ProfileSchema.setProfileExcludeQueryFromProfile(f.createURI(baseUri, "excludesQuery"));
        ProfileSchema.setProfileIncludeRdfRuleInProfile(f.createURI(baseUri, "includesRdfRule"));
        ProfileSchema.setProfileExcludeRdfRuleFromProfile(f.createURI(baseUri, "excludesRdfRule"));
        
        ProfileSchema.setProfileExcludeThenIncludeUri(f.createURI(baseUri, "excludeThenInclude"));
        ProfileSchema.setProfileIncludeThenExcludeUri(f.createURI(baseUri, "includeThenExclude"));
        ProfileSchema.setProfileIncludeExcludeOrderUndefinedUri(f.createURI(baseUri, "includeExcludeOrderUndefined"));
    }
    
    /**
     * A pre-instantiated schema object for ProfileSchema.
     */
    public static final QueryAllSchema PROFILE_SCHEMA = new ProfileSchema();
    
    /**
     * @return the profileAdministratorUri
     */
    public static URI getProfileAdministratorUri()
    {
        return ProfileSchema.profileAdministratorUri;
    }
    
    /**
     * @return the profileAllowImplicitProviderInclusionsUri
     */
    public static URI getProfileAllowImplicitProviderInclusionsUri()
    {
        return ProfileSchema.profileAllowImplicitProviderInclusionsUri;
    }
    
    /**
     * @return the profileAllowImplicitQueryInclusionsUri
     */
    public static URI getProfileAllowImplicitQueryInclusionsUri()
    {
        return ProfileSchema.profileAllowImplicitQueryInclusionsUri;
    }
    
    /**
     * @return the profileAllowImplicitRdfRuleInclusionsUri
     */
    public static URI getProfileAllowImplicitRdfRuleInclusionsUri()
    {
        return ProfileSchema.profileAllowImplicitRdfRuleInclusionsUri;
    }
    
    /**
     * @return the profileDefaultIncludeExcludeOrderUri
     */
    public static URI getProfileDefaultIncludeExcludeOrderUri()
    {
        return ProfileSchema.profileDefaultIncludeExcludeOrderUri;
    }
    
    /**
     * @return the profileExcludeProviderFromProfile
     */
    public static URI getProfileExcludeProviderFromProfile()
    {
        return ProfileSchema.profileExcludeProviderFromProfile;
    }
    
    /**
     * @return the profileExcludeQueryFromProfile
     */
    public static URI getProfileExcludeQueryFromProfile()
    {
        return ProfileSchema.profileExcludeQueryFromProfile;
    }
    
    /**
     * @return the profileExcludeRdfRuleFromProfile
     */
    public static URI getProfileExcludeRdfRuleFromProfile()
    {
        return ProfileSchema.profileExcludeRdfRuleFromProfile;
    }
    
    /**
     * @return the profileExcludeThenIncludeUri
     */
    public static URI getProfileExcludeThenIncludeUri()
    {
        return ProfileSchema.profileExcludeThenIncludeUri;
    }
    
    public static URI getProfileIncludeExcludeOrderUndefinedUri()
    {
        return ProfileSchema.profileIncludeExcludeOrderUndefinedUri;
    }
    
    public static URI getProfileIncludeExcludeOrderUri()
    {
        return ProfileSchema.profileIncludeExcludeOrderUri;
    }
    
    /**
     * @return the profileIncludeProviderInProfile
     */
    public static URI getProfileIncludeProviderInProfile()
    {
        return ProfileSchema.profileIncludeProviderInProfile;
    }
    
    /**
     * @return the profileIncludeQueryInProfile
     */
    public static URI getProfileIncludeQueryInProfile()
    {
        return ProfileSchema.profileIncludeQueryInProfile;
    }
    
    /**
     * @return the profileIncludeRdfRuleInProfile
     */
    public static URI getProfileIncludeRdfRuleInProfile()
    {
        return ProfileSchema.profileIncludeRdfRuleInProfile;
    }
    
    /**
     * @return the profileIncludeThenExcludeUri
     */
    public static URI getProfileIncludeThenExcludeUri()
    {
        return ProfileSchema.profileIncludeThenExcludeUri;
    }
    
    /**
     * @return the profileOrderUri
     */
    public static URI getProfileOrderUri()
    {
        return ProfileSchema.profileOrderUri;
    }
    
    /**
     * @return the profileTitle
     */
    public static URI getProfileTitle()
    {
        return ProfileSchema.profileTitle;
    }
    
    /**
     * @return the profileTypeUri
     */
    public static URI getProfileTypeUri()
    {
        return ProfileSchema.profileTypeUri;
    }
    
    /**
     * @param nextProfileAdministratorUri
     *            the profileAdministratorUri to set
     */
    public static void setProfileAdministratorUri(final URI nextProfileAdministratorUri)
    {
        ProfileSchema.profileAdministratorUri = nextProfileAdministratorUri;
    }
    
    /**
     * @param nextProfileAllowImplicitProviderInclusionsUri
     *            the profileAllowImplicitProviderInclusionsUri to set
     */
    public static void setProfileAllowImplicitProviderInclusionsUri(
            final URI nextProfileAllowImplicitProviderInclusionsUri)
    {
        ProfileSchema.profileAllowImplicitProviderInclusionsUri = nextProfileAllowImplicitProviderInclusionsUri;
    }
    
    /**
     * @param nextProfileAllowImplicitQueryInclusionsUri
     *            the profileAllowImplicitQueryInclusionsUri to set
     */
    public static void setProfileAllowImplicitQueryInclusionsUri(final URI nextProfileAllowImplicitQueryInclusionsUri)
    {
        ProfileSchema.profileAllowImplicitQueryInclusionsUri = nextProfileAllowImplicitQueryInclusionsUri;
    }
    
    /**
     * @param nextProfileAllowImplicitRdfRuleInclusionsUri
     *            the profileAllowImplicitRdfRuleInclusionsUri to set
     */
    public static void setProfileAllowImplicitRdfRuleInclusionsUri(
            final URI nextProfileAllowImplicitRdfRuleInclusionsUri)
    {
        ProfileSchema.profileAllowImplicitRdfRuleInclusionsUri = nextProfileAllowImplicitRdfRuleInclusionsUri;
    }
    
    /**
     * @param nextProfileDefaultIncludeExcludeOrderUri
     *            the profileDefaultIncludeExcludeOrderUri to set
     */
    public static void setProfileDefaultIncludeExcludeOrderUri(final URI nextProfileDefaultIncludeExcludeOrderUri)
    {
        ProfileSchema.profileDefaultIncludeExcludeOrderUri = nextProfileDefaultIncludeExcludeOrderUri;
    }
    
    /**
     * @param nextProfileExcludeProviderFromProfile
     *            the profileExcludeProviderFromProfile to set
     */
    public static void setProfileExcludeProviderFromProfile(final URI nextProfileExcludeProviderFromProfile)
    {
        ProfileSchema.profileExcludeProviderFromProfile = nextProfileExcludeProviderFromProfile;
    }
    
    /**
     * @param nextProfileExcludeQueryFromProfile
     *            the profileExcludeQueryFromProfile to set
     */
    public static void setProfileExcludeQueryFromProfile(final URI nextProfileExcludeQueryFromProfile)
    {
        ProfileSchema.profileExcludeQueryFromProfile = nextProfileExcludeQueryFromProfile;
    }
    
    /**
     * @param nextProfileExcludeRdfRuleFromProfile
     *            the profileExcludeRdfRuleFromProfile to set
     */
    public static void setProfileExcludeRdfRuleFromProfile(final URI nextProfileExcludeRdfRuleFromProfile)
    {
        ProfileSchema.profileExcludeRdfRuleFromProfile = nextProfileExcludeRdfRuleFromProfile;
    }
    
    /**
     * @param nextProfileExcludeThenIncludeUri
     *            the profileExcludeThenIncludeUri to set
     */
    public static void setProfileExcludeThenIncludeUri(final URI nextProfileExcludeThenIncludeUri)
    {
        ProfileSchema.profileExcludeThenIncludeUri = nextProfileExcludeThenIncludeUri;
    }
    
    /**
     * @param nextProfileIncludeExcludeOrderUndefinedUri
     *            the profileIncludeExcludeOrderUndefinedUri to set
     */
    public static void setProfileIncludeExcludeOrderUndefinedUri(final URI nextProfileIncludeExcludeOrderUndefinedUri)
    {
        ProfileSchema.profileIncludeExcludeOrderUndefinedUri = nextProfileIncludeExcludeOrderUndefinedUri;
    }
    
    /**
     * @param nextProfileIncludeExcludeOrderUri
     *            the profileIncludeExcludeOrderUri to set
     */
    public static void setProfileIncludeExcludeOrderUri(final URI nextProfileIncludeExcludeOrderUri)
    {
        ProfileSchema.profileIncludeExcludeOrderUri = nextProfileIncludeExcludeOrderUri;
    }
    
    /**
     * @param nextProfileIncludeProviderInProfile
     *            the profileIncludeProviderInProfile to set
     */
    public static void setProfileIncludeProviderInProfile(final URI nextProfileIncludeProviderInProfile)
    {
        ProfileSchema.profileIncludeProviderInProfile = nextProfileIncludeProviderInProfile;
    }
    
    /**
     * @param nextProfileIncludeQueryInProfile
     *            the profileIncludeQueryInProfile to set
     */
    public static void setProfileIncludeQueryInProfile(final URI nextProfileIncludeQueryInProfile)
    {
        ProfileSchema.profileIncludeQueryInProfile = nextProfileIncludeQueryInProfile;
    }
    
    /**
     * @param nextProfileIncludeRdfRuleInProfile
     *            the profileIncludeRdfRuleInProfile to set
     */
    public static void setProfileIncludeRdfRuleInProfile(final URI nextProfileIncludeRdfRuleInProfile)
    {
        ProfileSchema.profileIncludeRdfRuleInProfile = nextProfileIncludeRdfRuleInProfile;
    }
    
    /**
     * @param nextProfileIncludeThenExcludeUri
     *            the profileIncludeThenExcludeUri to set
     */
    public static void setProfileIncludeThenExcludeUri(final URI nextProfileIncludeThenExcludeUri)
    {
        ProfileSchema.profileIncludeThenExcludeUri = nextProfileIncludeThenExcludeUri;
    }
    
    /**
     * @param nextProfileOrderUri
     *            the profileOrderUri to set
     */
    public static void setProfileOrderUri(final URI nextProfileOrderUri)
    {
        ProfileSchema.profileOrderUri = nextProfileOrderUri;
    }
    
    /**
     * @param nextProfileTitle
     *            the profileTitle to set
     */
    public static void setProfileTitle(final URI nextProfileTitle)
    {
        ProfileSchema.profileTitle = nextProfileTitle;
    }
    
    /**
     * @param nextProfileTypeUri
     *            the profileTypeUri to set
     */
    public static void setProfileTypeUri(final URI nextProfileTypeUri)
    {
        ProfileSchema.profileTypeUri = nextProfileTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public ProfileSchema()
    {
        this(ProfileSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public ProfileSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contextUri)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(ProfileSchema.getProfileTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            
            if(modelVersion == 1)
            {
                con.add(ProfileSchema.getProfileTitle(), RDFS.SUBPROPERTYOF,
                        f.createURI(Constants.DC_NAMESPACE + "title"), contextUri);
            }
            
            // TODO: Add description
            con.add(ProfileSchema.getProfileOrderUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(ProfileSchema.getProfileOrderUri(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(ProfileSchema.getProfileOrderUri(), RDFS.DOMAIN, ProfileSchema.getProfileTypeUri(), contextUri);
            con.add(ProfileSchema.getProfileOrderUri(),
                    RDFS.LABEL,
                    f.createLiteral("A number that defines the order that this profile will be applied in. This is optional if the implementation defines another method for aligning profiles."),
                    contextUri);
            
            // TODO: Add description
            con.add(ProfileSchema.getProfileAdministratorUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileSchema.getProfileAdministratorUri(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(ProfileSchema.getProfileAdministratorUri(), RDFS.DOMAIN, ProfileSchema.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileSchema.getProfileAdministratorUri(), RDFS.LABEL,
                    f.createLiteral("The URI of the administrator for this profile."), contextUri);
            
            // TODO: Add description
            con.add(ProfileSchema.getProfileDefaultIncludeExcludeOrderUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileSchema.getProfileDefaultIncludeExcludeOrderUri(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(ProfileSchema.getProfileDefaultIncludeExcludeOrderUri(), RDFS.DOMAIN,
                    ProfileSchema.getProfileTypeUri(), contextUri);
            con.add(ProfileSchema.getProfileDefaultIncludeExcludeOrderUri(),
                    RDFS.LABEL,
                    f.createLiteral("This property defines the default include or exclude order to use if an item does not define it."),
                    contextUri);
            
            con.add(ProfileSchema.getProfileAllowImplicitQueryInclusionsUri(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextUri);
            con.add(ProfileSchema.getProfileAllowImplicitQueryInclusionsUri(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(ProfileSchema.getProfileAllowImplicitQueryInclusionsUri(), RDFS.DOMAIN,
                    ProfileSchema.getProfileTypeUri(), contextUri);
            con.add(ProfileSchema.getProfileAllowImplicitQueryInclusionsUri(),
                    RDFS.LABEL,
                    f.createLiteral("This property defines whether query types that aren't explicitly included or explicitly excluded will included by this profile. If they are not included, then they may be included by other profiles."),
                    contextUri);
            
            con.add(ProfileSchema.getProfileAllowImplicitProviderInclusionsUri(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextUri);
            con.add(ProfileSchema.getProfileAllowImplicitProviderInclusionsUri(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(ProfileSchema.getProfileAllowImplicitProviderInclusionsUri(), RDFS.DOMAIN,
                    ProfileSchema.getProfileTypeUri(), contextUri);
            con.add(ProfileSchema.getProfileAllowImplicitProviderInclusionsUri(),
                    RDFS.LABEL,
                    f.createLiteral("This property defines whether providers that aren't explicitly included or explicitly excluded will included by this profile. If they are not included, then they may be included by other profiles."),
                    contextUri);
            
            con.add(ProfileSchema.getProfileAllowImplicitRdfRuleInclusionsUri(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextUri);
            con.add(ProfileSchema.getProfileAllowImplicitRdfRuleInclusionsUri(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(ProfileSchema.getProfileAllowImplicitRdfRuleInclusionsUri(), RDFS.DOMAIN,
                    ProfileSchema.getProfileTypeUri(), contextUri);
            con.add(ProfileSchema.getProfileAllowImplicitRdfRuleInclusionsUri(),
                    RDFS.LABEL,
                    f.createLiteral("This property defines whether rdf rules that aren't explicitly included or explicitly excluded will included by this profile. If they are not included, then they may be included by other profiles."),
                    contextUri);
            
            con.add(ProfileSchema.getProfileIncludeProviderInProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileSchema.getProfileIncludeProviderInProfile(), RDFS.RANGE,
                    ProviderSchema.getProviderTypeUri(), contextUri);
            con.add(ProfileSchema.getProfileIncludeProviderInProfile(), RDFS.DOMAIN, ProfileSchema.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileSchema.getProfileIncludeProviderInProfile(), RDFS.LABEL,
                    f.createLiteral("This property specifies that the provider is included in this profile."),
                    contextUri);
            
            con.add(ProfileSchema.getProfileExcludeProviderFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileSchema.getProfileExcludeProviderFromProfile(), RDFS.RANGE,
                    ProviderSchema.getProviderTypeUri(), contextUri);
            con.add(ProfileSchema.getProfileExcludeProviderFromProfile(), RDFS.DOMAIN,
                    ProfileSchema.getProfileTypeUri(), contextUri);
            con.add(ProfileSchema.getProfileExcludeProviderFromProfile(),
                    RDFS.LABEL,
                    f.createLiteral("This property specifies that the rdf rule is excluded by this profile. Further profiles will not be processed for this provider."),
                    contextUri);
            
            con.add(ProfileSchema.getProfileIncludeQueryInProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileSchema.getProfileIncludeQueryInProfile(), RDFS.RANGE, QueryTypeSchema.getQueryTypeUri(),
                    contextUri);
            con.add(ProfileSchema.getProfileIncludeQueryInProfile(), RDFS.DOMAIN, ProfileSchema.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileSchema.getProfileIncludeQueryInProfile(), RDFS.LABEL,
                    f.createLiteral("This property specifies that the query type is included in this profile."),
                    contextUri);
            
            con.add(ProfileSchema.getProfileExcludeQueryFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileSchema.getProfileExcludeQueryFromProfile(), RDFS.RANGE, QueryTypeSchema.getQueryTypeUri(),
                    contextUri);
            con.add(ProfileSchema.getProfileExcludeQueryFromProfile(), RDFS.DOMAIN, ProfileSchema.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileSchema.getProfileExcludeQueryFromProfile(),
                    RDFS.LABEL,
                    f.createLiteral("This property specifies that the query type is excluded by this profile. Further profiles will not be processed for this query type."),
                    contextUri);
            
            con.add(ProfileSchema.getProfileIncludeRdfRuleInProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileSchema.getProfileIncludeRdfRuleInProfile(), RDFS.RANGE,
                    NormalisationRuleSchema.getNormalisationRuleTypeUri(), contextUri);
            con.add(ProfileSchema.getProfileIncludeRdfRuleInProfile(), RDFS.DOMAIN, ProfileSchema.getProfileTypeUri(),
                    contextUri);
            con.add(ProfileSchema.getProfileIncludeRdfRuleInProfile(), RDFS.LABEL,
                    f.createLiteral("This property specifies that the rdf rule is included in this profile."),
                    contextUri);
            
            con.add(ProfileSchema.getProfileExcludeRdfRuleFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileSchema.getProfileExcludeRdfRuleFromProfile(), RDFS.RANGE,
                    NormalisationRuleSchema.getNormalisationRuleTypeUri(), contextUri);
            con.add(ProfileSchema.getProfileExcludeRdfRuleFromProfile(), RDFS.DOMAIN,
                    ProfileSchema.getProfileTypeUri(), contextUri);
            con.add(ProfileSchema.getProfileExcludeRdfRuleFromProfile(),
                    RDFS.LABEL,
                    f.createLiteral("This property specifies that the rdf rule is excluded by this profile. Further profiles will not be processed for this rdf rule."),
                    contextUri);
            
            con.add(ProfileSchema.getProfileExcludeThenIncludeUri(), RDF.TYPE, OWL.INDIVIDUAL, contextUri);
            con.add(ProfileSchema.getProfileExcludeThenIncludeUri(),
                    RDFS.LABEL,
                    f.createLiteral("This is a marker that defines that this item will be processed against exclude rules first, but if it doesn't match it will be assumed to match and possibly be included implicitly."),
                    contextUri);
            
            con.add(ProfileSchema.getProfileIncludeThenExcludeUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileSchema.getProfileIncludeThenExcludeUri(),
                    RDFS.LABEL,
                    f.createLiteral("This is a marker that defines that this item will be processed against include rules first, but if it doesn't match it will be excluded by this profile and should be processed by other profiles."),
                    contextUri);
            
            con.add(ProfileSchema.getProfileIncludeExcludeOrderUndefinedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProfileSchema.getProfileIncludeExcludeOrderUndefinedUri(),
                    RDFS.LABEL,
                    f.createLiteral("This is a marker that defines that this item will be processed using the default include or exclude order of each profile that it is defined in."),
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
            
            ProfileSchema.LOG.error("RepositoryException: " + re.getMessage());
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
    
}
