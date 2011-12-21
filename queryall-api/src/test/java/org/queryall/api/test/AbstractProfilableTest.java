package org.queryall.api.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.queryall.api.base.ProfilableInterface;
import org.queryall.api.profile.Profile;
import org.queryall.api.profile.ProfileSchema;

/**
 * Abstract unit test for Profilable API
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractProfilableTest
{
    protected URI testTrueQueryTypeUri;
    protected URI testFalseQueryTypeUri;
    protected URI testTrueRuleUri;
    protected URI testFalseRuleUri;
    protected URI testTrueProviderUri;
    protected URI testFalseProviderUri;
    
    private ProfilableInterface providerNonDefault;
    private ProfilableInterface providerSpecificDefault;
    private ProfilableInterface providerIncludeImplicitly;
    private ProfilableInterface providerExcludeImplicitly;
    private ProfilableInterface providerIncludeExcludeOrderUndefined;
    private ProfilableInterface providerTrueUri;
    private ProfilableInterface providerFalseUri;
    
    private Profile profileIncludeAllImplicitly;
    private Profile profileExcludeImplicitly;
    private Profile profileExcludeImplicitlyAndByDefault;
    private Profile profileIncludeAllImplicitlyExcludeByDefault;
    private Profile profileIncludeTrueOnly;
    private Profile profileExcludeFalseOnly;
    
    private List<Profile> profileListEmpty;
    private List<Profile> profileListSingleIncludeAllImplicitly;
    private List<Profile> profileListSingleExcludeImplicitly;
    private List<Profile> profileListSingleExcludeExplicitlyAndByDefault;
    private List<Profile> profileListSingleIncludeAllImplicitlyExcludeByDefault;
    private List<Profile> profileListSingleIncludeTrue;
    private List<Profile> profileListSingleExcludeFalse;
    private List<Profile> profileListMultipleIncludeTrueThenExcludeFalse;
    private List<Profile> profileListMultipleExcludeFalseThenIncludeTrue;
    private List<Profile> profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly;
    private List<Profile> profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly;
    
    /**
     * This method must be overridden to return a new instance of the implemented
     * ProfilableInterface for each successive invocation
     */
    public abstract ProfilableInterface getNewTestProfilable();
    
    /**
     * This method must be overridden to return a new instance of the implemented Profile class for
     * each successive invocation
     */
    public abstract Profile getNewTestProfile();
    
    /**
     * This method is necessary to ensure that the profile exclude instruction matches the type of
     * the object being checked
     * 
     * @param profilable
     * @param uriToExclude
     */
    public abstract void includeFalseUri(Profile profilable, URI uriToExclude);
    
    /**
     * This method is necessary to ensure that the profile include instruction matches the type of
     * the object being checked
     * 
     * @param profilable
     * @param uriToInclude
     */
    public abstract void includeTrueUri(Profile profilable, URI uriToInclude);
    
    /**
     * This method performs the following actions: - Creates new Providers for the Provider type
     * fields using multiple calls to getNewTestProvider - Create org.openrdf.model.URI instances
     * for the test URIs - Add testTrue*'s using the relevant methods from the API
     */
    @Before
    public void setUp() throws Exception
    {
        final ValueFactory f = new MemValueFactory();
        
        this.testTrueQueryTypeUri = f.createURI("http://example.org/test/includedQueryType");
        this.testFalseQueryTypeUri = f.createURI("http://example.org/test/excludedQueryType");
        this.testTrueRuleUri = f.createURI("http://example.org/test/includedRule");
        this.testFalseRuleUri = f.createURI("http://example.org/test/excludedRule");
        this.testTrueProviderUri = f.createURI("http://example.org/test/includedProvider");
        this.testFalseProviderUri = f.createURI("http://example.org/test/excludedProvider");
        
        this.providerNonDefault = this.getNewTestProfilable();
        this.providerNonDefault
                .setProfileIncludeExcludeOrder(ProfileSchema.getProfileIncludeExcludeOrderUndefinedUri());
        
        this.providerSpecificDefault = this.getNewTestProfilable();
        this.providerSpecificDefault.setProfileIncludeExcludeOrder(ProfileSchema
                .getProfileIncludeExcludeOrderUndefinedUri());
        
        this.providerTrueUri = this.getNewTestProfilable();
        this.providerTrueUri.setProfileIncludeExcludeOrder(ProfileSchema.getProfileIncludeExcludeOrderUndefinedUri());
        this.providerTrueUri.setKey(this.testTrueProviderUri);
        
        this.providerFalseUri = this.getNewTestProfilable();
        this.providerFalseUri.setProfileIncludeExcludeOrder(ProfileSchema.getProfileIncludeExcludeOrderUndefinedUri());
        this.providerFalseUri.setKey(this.testFalseProviderUri);
        
        this.providerIncludeImplicitly = this.getNewTestProfilable();
        this.providerIncludeImplicitly.setProfileIncludeExcludeOrder(ProfileSchema.getProfileExcludeThenIncludeUri());
        
        this.providerExcludeImplicitly = this.getNewTestProfilable();
        this.providerExcludeImplicitly.setProfileIncludeExcludeOrder(ProfileSchema.getProfileIncludeThenExcludeUri());
        
        this.providerIncludeExcludeOrderUndefined = this.getNewTestProfilable();
        this.providerIncludeExcludeOrderUndefined.setProfileIncludeExcludeOrder(ProfileSchema
                .getProfileIncludeExcludeOrderUndefinedUri());
        
        this.profileIncludeAllImplicitly = this.getNewTestProfile();
        this.profileIncludeAllImplicitly.setAllowImplicitProviderInclusions(true);
        this.profileIncludeAllImplicitly.setAllowImplicitQueryTypeInclusions(true);
        this.profileIncludeAllImplicitly.setAllowImplicitRdfRuleInclusions(true);
        this.profileIncludeAllImplicitly.setDefaultProfileIncludeExcludeOrder(ProfileSchema
                .getProfileExcludeThenIncludeUri());
        
        this.profileIncludeAllImplicitlyExcludeByDefault = this.getNewTestProfile();
        this.profileIncludeAllImplicitlyExcludeByDefault.setAllowImplicitProviderInclusions(true);
        this.profileIncludeAllImplicitlyExcludeByDefault.setAllowImplicitQueryTypeInclusions(true);
        this.profileIncludeAllImplicitlyExcludeByDefault.setAllowImplicitRdfRuleInclusions(true);
        this.profileIncludeAllImplicitlyExcludeByDefault.setDefaultProfileIncludeExcludeOrder(ProfileSchema
                .getProfileIncludeThenExcludeUri());
        
        this.profileExcludeImplicitly = this.getNewTestProfile();
        this.profileExcludeImplicitly.setAllowImplicitProviderInclusions(false);
        this.profileExcludeImplicitly.setAllowImplicitQueryTypeInclusions(false);
        this.profileExcludeImplicitly.setAllowImplicitRdfRuleInclusions(false);
        this.profileExcludeImplicitly.setDefaultProfileIncludeExcludeOrder(ProfileSchema
                .getProfileExcludeThenIncludeUri());
        
        this.profileExcludeImplicitlyAndByDefault = this.getNewTestProfile();
        this.profileExcludeImplicitlyAndByDefault.setAllowImplicitProviderInclusions(false);
        this.profileExcludeImplicitlyAndByDefault.setAllowImplicitQueryTypeInclusions(false);
        this.profileExcludeImplicitlyAndByDefault.setAllowImplicitRdfRuleInclusions(false);
        this.profileExcludeImplicitlyAndByDefault.setDefaultProfileIncludeExcludeOrder(ProfileSchema
                .getProfileIncludeThenExcludeUri());
        
        this.profileIncludeTrueOnly = this.getNewTestProfile();
        this.includeTrueUri(this.profileIncludeTrueOnly, this.testTrueProviderUri);
        this.profileIncludeTrueOnly.setAllowImplicitProviderInclusions(false);
        this.profileIncludeTrueOnly.setAllowImplicitQueryTypeInclusions(false);
        this.profileIncludeTrueOnly.setAllowImplicitRdfRuleInclusions(false);
        this.profileIncludeTrueOnly.setDefaultProfileIncludeExcludeOrder(ProfileSchema
                .getProfileIncludeThenExcludeUri());
        
        this.profileExcludeFalseOnly = this.getNewTestProfile();
        this.includeFalseUri(this.profileExcludeFalseOnly, this.testFalseProviderUri);
        this.profileExcludeFalseOnly.setAllowImplicitProviderInclusions(false);
        this.profileExcludeFalseOnly.setAllowImplicitQueryTypeInclusions(false);
        this.profileExcludeFalseOnly.setAllowImplicitRdfRuleInclusions(false);
        this.profileExcludeFalseOnly.setDefaultProfileIncludeExcludeOrder(ProfileSchema
                .getProfileIncludeThenExcludeUri());
        
        this.profileListEmpty = new ArrayList<Profile>(0);
        
        this.profileListSingleIncludeAllImplicitly = new ArrayList<Profile>(1);
        this.profileListSingleIncludeAllImplicitly.add(this.profileIncludeAllImplicitly);
        
        this.profileListSingleIncludeAllImplicitlyExcludeByDefault = new ArrayList<Profile>(1);
        this.profileListSingleIncludeAllImplicitlyExcludeByDefault
                .add(this.profileIncludeAllImplicitlyExcludeByDefault);
        
        this.profileListSingleExcludeImplicitly = new ArrayList<Profile>(1);
        this.profileListSingleExcludeImplicitly.add(this.profileExcludeImplicitly);
        
        this.profileListSingleExcludeExplicitlyAndByDefault = new ArrayList<Profile>(1);
        this.profileListSingleExcludeExplicitlyAndByDefault.add(this.profileExcludeImplicitlyAndByDefault);
        
        this.profileListSingleIncludeTrue = new ArrayList<Profile>(1);
        this.profileListSingleIncludeTrue.add(this.profileIncludeTrueOnly);
        
        this.profileListSingleExcludeFalse = new ArrayList<Profile>(1);
        this.profileListSingleExcludeFalse.add(this.profileExcludeFalseOnly);
        
        this.profileListMultipleIncludeTrueThenExcludeFalse = new ArrayList<Profile>(2);
        this.profileListMultipleIncludeTrueThenExcludeFalse.add(this.profileIncludeTrueOnly);
        this.profileListMultipleIncludeTrueThenExcludeFalse.add(this.profileExcludeFalseOnly);
        
        this.profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly = new ArrayList<Profile>(3);
        this.profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly.add(this.profileIncludeTrueOnly);
        this.profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly.add(this.profileExcludeFalseOnly);
        this.profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly
                .add(this.profileIncludeAllImplicitly);
        
        this.profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly = new ArrayList<Profile>(3);
        this.profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly.add(this.profileIncludeTrueOnly);
        this.profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly.add(this.profileExcludeFalseOnly);
        this.profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly
                .add(this.profileExcludeImplicitlyAndByDefault);
        
        this.profileListMultipleExcludeFalseThenIncludeTrue = new ArrayList<Profile>(2);
        this.profileListMultipleExcludeFalseThenIncludeTrue.add(this.profileExcludeFalseOnly);
        this.profileListMultipleExcludeFalseThenIncludeTrue.add(this.profileIncludeTrueOnly);
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.testTrueQueryTypeUri = null;
        this.testFalseQueryTypeUri = null;
        this.testTrueRuleUri = null;
        this.testFalseRuleUri = null;
        this.testTrueProviderUri = null;
        this.testFalseProviderUri = null;
        
        this.providerNonDefault = null;
        this.providerSpecificDefault = null;
        this.providerIncludeImplicitly = null;
        this.providerExcludeImplicitly = null;
        this.providerIncludeExcludeOrderUndefined = null;
        this.providerTrueUri = null;
        this.providerFalseUri = null;
        
        this.profileIncludeAllImplicitly = null;
        this.profileExcludeImplicitly = null;
        this.profileExcludeImplicitlyAndByDefault = null;
        this.profileIncludeAllImplicitlyExcludeByDefault = null;
        this.profileIncludeTrueOnly = null;
        this.profileExcludeFalseOnly = null;
        
        this.profileListEmpty = null;
        this.profileListSingleIncludeAllImplicitly = null;
        this.profileListSingleExcludeImplicitly = null;
        this.profileListSingleExcludeExplicitlyAndByDefault = null;
        this.profileListSingleIncludeAllImplicitlyExcludeByDefault = null;
        this.profileListSingleIncludeTrue = null;
        this.profileListSingleExcludeFalse = null;
        this.profileListMultipleIncludeTrueThenExcludeFalse = null;
        this.profileListMultipleExcludeFalseThenIncludeTrue = null;
        this.profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly = null;
        this.profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly = null;
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.provider.Provider#isUsedWithProfileList(org.openrdf.model.URI, boolean, boolean)}
     * .
     * 
     * This method contains a matrix of single and multiple profile configurations that are designed
     * to test the isUsedWithProfileList method for differing provider configurations
     * 
     * In cases where the providers do not match explicitly, the final two parameters of the
     * isUsedWithProfileList method are used to determine whether the provider is useful
     * 
     * The second parameter determines whether implicit inclusions are recognised. These are
     * relevant if the providers include exclude order is computed to be excludeThenInclude. This
     * may occur in cases where either the providers default include exclude order was undefined and
     * any profile had a default include exclude order of excludeThenInclude; or when the providers
     * default include exclude order was excludeThenInclude.
     */
    @Test
    public void testIsUsedWithProfileListEmpty()
    {
        Assert.assertNotNull("Did you call super.setUp()?", this.providerIncludeImplicitly);
        
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerIncludeImplicitly, this.profileListEmpty, true, true,
                false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerExcludeImplicitly, this.profileListEmpty, false,
                false, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerIncludeExcludeOrderUndefined, this.profileListEmpty,
                true, true, false, false);
    }
    
    @Test
    public void testIsUsedWithProfileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly()
    {
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerIncludeImplicitly,
                this.profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, true, true, false, false);
        
        ProfilableTestUtil
                .testIsUsedWithProfileList(this.providerExcludeImplicitly,
                        this.profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, false, false,
                        false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerIncludeExcludeOrderUndefined,
                this.profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, true, true, false, false);
    }
    
    @Test
    public void testIsUsedWithProfileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly()
    {
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerIncludeImplicitly,
                this.profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, true, true, true, false);
        
        ProfilableTestUtil
                .testIsUsedWithProfileList(this.providerExcludeImplicitly,
                        this.profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, false, false,
                        false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerIncludeExcludeOrderUndefined,
                this.profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, true, true, true, false);
    }
    
    @Test
    public void testIsUsedWithProfileListSingleExcludeExplicitlyAndByDefault()
    {
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerIncludeImplicitly,
                this.profileListSingleExcludeExplicitlyAndByDefault, true, true, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerExcludeImplicitly,
                this.profileListSingleExcludeExplicitlyAndByDefault, false, false, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerIncludeExcludeOrderUndefined,
                this.profileListSingleExcludeExplicitlyAndByDefault, true, true, false, false);
    }
    
    @Test
    public void testIsUsedWithProfileListSingleExcludeFalse()
    {
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerTrueUri, this.profileListSingleExcludeFalse, true,
                true, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerFalseUri, this.profileListSingleExcludeFalse, false,
                false, false, false);
    }
    
    @Test
    public void testIsUsedWithProfileListSingleExcludeImplicitly()
    {
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerIncludeImplicitly,
                this.profileListSingleExcludeImplicitly, true, true, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerExcludeImplicitly,
                this.profileListSingleExcludeImplicitly, false, false, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerIncludeExcludeOrderUndefined,
                this.profileListSingleExcludeImplicitly, true, true, false, false);
    }
    
    @Test
    public void testIsUsedWithProfileListSingleIncludeAllImplicitly()
    {
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerIncludeImplicitly,
                this.profileListSingleIncludeAllImplicitly, true, true, true, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerExcludeImplicitly,
                this.profileListSingleIncludeAllImplicitly, false, false, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerIncludeExcludeOrderUndefined,
                this.profileListSingleIncludeAllImplicitly, true, true, true, false);
        
    }
    
    @Test
    public void testIsUsedWithProfileListSingleIncludeAllImplicitlyExcludeByDefault()
    {
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerIncludeImplicitly,
                this.profileListSingleIncludeAllImplicitlyExcludeByDefault, true, true, true, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerExcludeImplicitly,
                this.profileListSingleIncludeAllImplicitlyExcludeByDefault, false, false, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerIncludeExcludeOrderUndefined,
                this.profileListSingleIncludeAllImplicitlyExcludeByDefault, true, true, false, false);
    }
    
    @Test
    public void testIsUsedWithProfileListSingleIncludeTrue()
    {
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerTrueUri, this.profileListSingleIncludeTrue, true,
                true, true, true);
        
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerFalseUri, this.profileListSingleIncludeTrue, true,
                true, false, false);
    }
    
    @Test
    public void testIsUsedWithProviderProfileListMultipleExcludeFalseThenIncludeTrue()
    {
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerTrueUri,
                this.profileListMultipleExcludeFalseThenIncludeTrue, true, true, true, true);
        
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerFalseUri,
                this.profileListMultipleExcludeFalseThenIncludeTrue, false, false, false, false);
    }
    
    @Test
    public void testIsUsedWithProviderProfileListMultipleIncludeTrueThenExcludeFalse()
    {
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerTrueUri,
                this.profileListMultipleIncludeTrueThenExcludeFalse, true, true, true, true);
        
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerFalseUri,
                this.profileListMultipleIncludeTrueThenExcludeFalse, false, false, false, false);
    }
    
    @Test
    public void testIsUsedWithProviderProfileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly()
    {
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerTrueUri,
                this.profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, true, true, true, true);
        
        ProfilableTestUtil
                .testIsUsedWithProfileList(this.providerFalseUri,
                        this.profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, false, false,
                        false, false);
    }
    
    @Test
    public void testIsUsedWithProviderProfileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly()
    {
        ProfilableTestUtil.testIsUsedWithProfileList(this.providerTrueUri,
                this.profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, true, true, true, true);
        
        ProfilableTestUtil
                .testIsUsedWithProfileList(this.providerFalseUri,
                        this.profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, false, false,
                        false, false);
    }
}
