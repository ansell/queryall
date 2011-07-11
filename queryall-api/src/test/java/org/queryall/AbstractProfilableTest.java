package org.queryall;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.queryall.api.ProfilableInterface;
import org.queryall.api.Profile;

/**
 * Abstract unit test for Provider API
 * 
 * Implementations must implement the abstract setUp method
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
     * This method performs the following actions:
     * - Creates new Providers for the Provider type fields using multiple calls to getNewTestProvider
     * - Create org.openrdf.model.URI instances for the test URIs
     * - Add testTrue*'s using the relevant methods from the API
     */
    @Before
    public void setUp() throws Exception
    {
        ValueFactory f = new MemValueFactory();

        testTrueQueryTypeUri = f.createURI("http://example.org/test/includedQueryType");
        testFalseQueryTypeUri = f.createURI("http://example.org/test/excludedQueryType");
        testTrueRuleUri = f.createURI("http://example.org/test/includedRule");
        testFalseRuleUri = f.createURI("http://example.org/test/excludedRule");
        testTrueProviderUri = f.createURI("http://example.org/test/includedProvider");
        testFalseProviderUri = f.createURI("http://example.org/test/excludedProvider");

        
        
        providerNonDefault = getNewTestProfilable();
        providerNonDefault.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        
        providerSpecificDefault = getNewTestProfilable();
        providerSpecificDefault.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        
        providerTrueUri = getNewTestProfilable();
        providerTrueUri.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        providerTrueUri.setKey(testTrueProviderUri);
        
        providerFalseUri = getNewTestProfilable();
        providerFalseUri.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        providerFalseUri.setKey(testFalseProviderUri);

        providerIncludeImplicitly = getNewTestProfilable();
        providerIncludeImplicitly.setProfileIncludeExcludeOrder(getProfileExcludeThenIncludeURI());
        
        providerExcludeImplicitly = getNewTestProfilable();
        providerExcludeImplicitly.setProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());
        
        providerIncludeExcludeOrderUndefined = getNewTestProfilable();
        providerIncludeExcludeOrderUndefined.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        
        
        
        profileIncludeAllImplicitly = getNewTestProfile();
        profileIncludeAllImplicitly.setAllowImplicitProviderInclusions(true);
        profileIncludeAllImplicitly.setAllowImplicitQueryTypeInclusions(true);
        profileIncludeAllImplicitly.setAllowImplicitRdfRuleInclusions(true);
        profileIncludeAllImplicitly.setDefaultProfileIncludeExcludeOrder(getProfileExcludeThenIncludeURI());
        
        profileIncludeAllImplicitlyExcludeByDefault = getNewTestProfile();
        profileIncludeAllImplicitlyExcludeByDefault.setAllowImplicitProviderInclusions(true);
        profileIncludeAllImplicitlyExcludeByDefault.setAllowImplicitQueryTypeInclusions(true);
        profileIncludeAllImplicitlyExcludeByDefault.setAllowImplicitRdfRuleInclusions(true);
        profileIncludeAllImplicitlyExcludeByDefault.setDefaultProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());
        
        profileExcludeImplicitly = getNewTestProfile();
        profileExcludeImplicitly.setAllowImplicitProviderInclusions(false);
        profileExcludeImplicitly.setAllowImplicitQueryTypeInclusions(false);
        profileExcludeImplicitly.setAllowImplicitRdfRuleInclusions(false);
        profileExcludeImplicitly.setDefaultProfileIncludeExcludeOrder(getProfileExcludeThenIncludeURI());
        
        profileExcludeImplicitlyAndByDefault = getNewTestProfile();
        profileExcludeImplicitlyAndByDefault.setAllowImplicitProviderInclusions(false);
        profileExcludeImplicitlyAndByDefault.setAllowImplicitQueryTypeInclusions(false);
        profileExcludeImplicitlyAndByDefault.setAllowImplicitRdfRuleInclusions(false);
        profileExcludeImplicitlyAndByDefault.setDefaultProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());
        
        profileIncludeTrueOnly = getNewTestProfile();
        includeTrueUri(profileIncludeTrueOnly, testTrueProviderUri);
        profileIncludeTrueOnly.setAllowImplicitProviderInclusions(false);
        profileIncludeTrueOnly.setAllowImplicitQueryTypeInclusions(false);
        profileIncludeTrueOnly.setAllowImplicitRdfRuleInclusions(false);
        profileIncludeTrueOnly.setDefaultProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());
        
        profileExcludeFalseOnly = getNewTestProfile();
        includeFalseUri(profileExcludeFalseOnly, testFalseProviderUri);
        profileExcludeFalseOnly.setAllowImplicitProviderInclusions(false);
        profileExcludeFalseOnly.setAllowImplicitQueryTypeInclusions(false);
        profileExcludeFalseOnly.setAllowImplicitRdfRuleInclusions(false);
        profileExcludeFalseOnly.setDefaultProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());

        
        
        profileListEmpty = new LinkedList<Profile>();

        profileListSingleIncludeAllImplicitly = new LinkedList<Profile>();
        profileListSingleIncludeAllImplicitly.add(profileIncludeAllImplicitly);
        
        profileListSingleIncludeAllImplicitlyExcludeByDefault = new LinkedList<Profile>();
        profileListSingleIncludeAllImplicitlyExcludeByDefault.add(profileIncludeAllImplicitlyExcludeByDefault);
        
        profileListSingleExcludeImplicitly = new LinkedList<Profile>();
        profileListSingleExcludeImplicitly.add(profileExcludeImplicitly);
        
        profileListSingleExcludeExplicitlyAndByDefault = new LinkedList<Profile>();
        profileListSingleExcludeExplicitlyAndByDefault.add(profileExcludeImplicitlyAndByDefault);
        
        profileListSingleIncludeTrue = new LinkedList<Profile>();
        profileListSingleIncludeTrue.add(profileIncludeTrueOnly);
        
        profileListSingleExcludeFalse = new LinkedList<Profile>();
        profileListSingleExcludeFalse.add(profileExcludeFalseOnly);

        profileListMultipleIncludeTrueThenExcludeFalse = new LinkedList<Profile>();
        profileListMultipleIncludeTrueThenExcludeFalse.add(profileIncludeTrueOnly);
        profileListMultipleIncludeTrueThenExcludeFalse.add(profileExcludeFalseOnly);
        
        profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly = new LinkedList<Profile>();
        profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly.add(profileIncludeTrueOnly);
        profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly.add(profileExcludeFalseOnly);
        profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly.add(profileIncludeAllImplicitly);
        
        profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly = new LinkedList<Profile>();
        profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly.add(profileIncludeTrueOnly);
        profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly.add(profileExcludeFalseOnly);
        profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly.add(profileExcludeImplicitlyAndByDefault);
        
        profileListMultipleExcludeFalseThenIncludeTrue = new LinkedList<Profile>();
        profileListMultipleExcludeFalseThenIncludeTrue.add(profileExcludeFalseOnly);
        profileListMultipleExcludeFalseThenIncludeTrue.add(profileIncludeTrueOnly);
    }
    
    /**
     * This method must be overridden to return a new instance of 
     * the implemented Provider class for each successive invocation
     */
    public abstract ProfilableInterface getNewTestProfilable();
    
    /**
     * This method must be overridden to return a new instance of 
     * the implemented Profile class for each successive invocation
     */
    public abstract Profile getNewTestProfile();
    

    public abstract URI getProfileIncludeExcludeOrderUndefinedUri();

    public abstract URI getProfileExcludeThenIncludeURI();
    
    public abstract URI getProfileIncludeThenExcludeURI();

    /**
     * This method is necessary to ensure that the profile include 
     * instruction matches the type of the object being checked
     * @param profilable
     * @param uriToInclude
     */
    public abstract void includeTrueUri(Profile profilable, URI uriToInclude);
    
    /**
     * This method is necessary to ensure that the profile exclude 
     * instruction matches the type of the object being checked
     * @param profilable
     * @param uriToExclude
     */
    public abstract void includeFalseUri(Profile profilable, URI uriToExclude);

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        testTrueQueryTypeUri = null;
        testFalseQueryTypeUri = null;
        testTrueRuleUri = null;
        testFalseRuleUri = null;
        testTrueProviderUri = null;
        testFalseProviderUri = null;
        
        providerNonDefault = null;
        providerSpecificDefault = null;
        providerIncludeImplicitly = null;
        providerExcludeImplicitly = null;
        providerIncludeExcludeOrderUndefined = null;
        providerTrueUri = null;
        providerFalseUri = null;

        profileIncludeAllImplicitly = null;
        profileExcludeImplicitly = null;
        profileExcludeImplicitlyAndByDefault = null;
        profileIncludeAllImplicitlyExcludeByDefault = null;
        profileIncludeTrueOnly = null;
        profileExcludeFalseOnly = null;

        profileListEmpty = null;
        profileListSingleIncludeAllImplicitly = null;
        profileListSingleExcludeImplicitly = null;
        profileListSingleExcludeExplicitlyAndByDefault = null;
        profileListSingleIncludeAllImplicitlyExcludeByDefault = null;
        profileListSingleIncludeTrue = null;
        profileListSingleExcludeFalse = null;
        profileListMultipleIncludeTrueThenExcludeFalse = null;
        profileListMultipleExcludeFalseThenIncludeTrue = null;
        profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly = null;
        profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly = null;

    }

    /**
     * Test method for {@link org.queryall.api.Provider#isUsedWithProfileList(org.openrdf.model.URI, boolean, boolean)}.
     * 
     * This method contains a matrix of single and multiple profile configurations that are designed to test the isUsedWithProfileList method for differing provider configurations
     * 
     * In cases where the providers do not match explicitly, the final two parameters of the isUsedWithProfileList method are used to determine whether the provider is useful
     * 
     * The second parameter determines whether implicit inclusions are recognised. 
     * These are relevant if the providers include exclude order is computed to be excludeThenInclude. 
     * This may occur in cases where either
     * the providers default include exclude order was undefined and
     * any profile had a default include exclude order of excludeThenInclude; 
     * or when the providers default include exclude order was excludeThenInclude.
     */
    @Test
    public void testIsUsedWithProfileList()
    {
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeImplicitly, profileListEmpty, 
                true, true, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerExcludeImplicitly, profileListEmpty, 
                false, false, false, false);

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeExcludeOrderUndefined, profileListEmpty, 
                true, true, false, false);


        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeImplicitly, profileListSingleIncludeAllImplicitly, 
                true, true, true, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerExcludeImplicitly, profileListSingleIncludeAllImplicitly, 
                false, false, false, false);

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeExcludeOrderUndefined, profileListSingleIncludeAllImplicitly, 
                true, true, true, false);
        
        
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeImplicitly, profileListSingleIncludeAllImplicitlyExcludeByDefault, 
                true, true, true, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerExcludeImplicitly, profileListSingleIncludeAllImplicitlyExcludeByDefault, 
                false, false, false, false);

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeExcludeOrderUndefined, profileListSingleIncludeAllImplicitlyExcludeByDefault, 
                true, true, false, false);
        

        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeImplicitly, profileListSingleExcludeImplicitly, 
                true, true, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerExcludeImplicitly, profileListSingleExcludeImplicitly, 
                false, false, false, false);

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeExcludeOrderUndefined, profileListSingleExcludeImplicitly, 
                true, true, false, false);
        

        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeImplicitly, profileListSingleExcludeExplicitlyAndByDefault, 
                true, true, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerExcludeImplicitly, profileListSingleExcludeExplicitlyAndByDefault, 
                false, false, false, false);

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeExcludeOrderUndefined, profileListSingleExcludeExplicitlyAndByDefault, 
                true, true, false, false);

        

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeImplicitly, profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, 
                true, true, true, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerExcludeImplicitly, profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, 
                false, false, false, false);

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeExcludeOrderUndefined, profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, 
                true, true, true, false);

        

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeImplicitly, profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, 
                true, true, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerExcludeImplicitly, profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, 
                false, false, false, false);

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeExcludeOrderUndefined, profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, 
                true, true, false, false);
        
        
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerTrueUri, profileListSingleIncludeTrue, 
                true, true, true, true);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerFalseUri, profileListSingleIncludeTrue, 
                true, true, false, false);
        

        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerTrueUri, profileListSingleExcludeFalse, 
                true, true, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerFalseUri, profileListSingleExcludeFalse, 
                false, false, false, false);
        
        

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerTrueUri, profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, 
                true, true, true, true);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerFalseUri, profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, 
                false, false, false, false);
        
        
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerTrueUri, profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, 
                true, true, true, true);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerFalseUri, profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, 
                false, false, false, false);
        
        
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerTrueUri, profileListMultipleIncludeTrueThenExcludeFalse, 
                true, true, true, true);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerFalseUri, profileListMultipleIncludeTrueThenExcludeFalse, 
                false, false, false, false);

        
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerTrueUri, profileListMultipleExcludeFalseThenIncludeTrue, 
                true, true, true, true);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerFalseUri, profileListMultipleExcludeFalseThenIncludeTrue, 
                false, false, false, false);
    }
}
