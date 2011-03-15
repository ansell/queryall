package org.queryall;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;

/**
 * Abstract unit test for Provider API
 * 
 * Implementations must implement the abstract setUp method
 */

@SuppressWarnings("unused")
public abstract class AbstractProviderTest 
    extends TestCase
{
    protected URI testTrueQueryTypeUri;
    protected URI testFalseQueryTypeUri;
    protected URI testTrueRuleUri;
    protected URI testFalseRuleUri;
    protected URI testTrueNamespaceUri;
    protected URI testFalseNamespaceUri;
    protected URI testTrueProviderUri;
    protected URI testFalseProviderUri;
    
    private Provider providerNonDefault;
    private Provider providerSpecificDefault;
    private Provider providerNoNamespacesDefault;
    private Provider providerIncludeImplicitly;
    private Provider providerExcludeImplicitly;
    private Provider providerIncludeExcludeOrderUndefined;
    private Provider providerTrueUri;
    private Provider providerFalseUri;

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
        testTrueNamespaceUri = f.createURI("http://example.org/test/includedNamespace");
        testFalseNamespaceUri = f.createURI("http://example.org/test/excludedNamespace");
        testTrueProviderUri = f.createURI("http://example.org/test/includedProvider");
        testFalseProviderUri = f.createURI("http://example.org/test/excludedProvider");

        
        
        providerNonDefault = getNewTestProvider();
        providerNonDefault.setIsDefaultSource(false);
        providerNonDefault.addIncludedInQueryType(testTrueQueryTypeUri);
        providerNonDefault.addNormalisationUri(testTrueRuleUri);
        providerNonDefault.addNamespace(testTrueNamespaceUri);
        providerNonDefault.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        
        providerSpecificDefault = getNewTestProvider();
        providerSpecificDefault.setIsDefaultSource(true);
        providerSpecificDefault.addIncludedInQueryType(testTrueQueryTypeUri);
        providerSpecificDefault.addNormalisationUri(testTrueRuleUri);
        providerSpecificDefault.addNamespace(testTrueNamespaceUri);
        providerSpecificDefault.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        
        providerNoNamespacesDefault = getNewTestProvider();
        providerNoNamespacesDefault.setIsDefaultSource(true);
        providerNoNamespacesDefault.addIncludedInQueryType(testTrueQueryTypeUri);
        providerNoNamespacesDefault.addNormalisationUri(testTrueRuleUri);
        providerSpecificDefault.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        
        providerTrueUri = getNewTestProvider();
        providerTrueUri.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        providerTrueUri.setKey(testTrueProviderUri);
        
        providerFalseUri = getNewTestProvider();
        providerFalseUri.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        providerFalseUri.setKey(testFalseProviderUri);

        providerIncludeImplicitly = getNewTestProvider();
        providerIncludeImplicitly.setProfileIncludeExcludeOrder(getProfileExcludeThenIncludeURI());
        
        providerExcludeImplicitly = getNewTestProvider();
        providerExcludeImplicitly.setProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());
        
        providerIncludeExcludeOrderUndefined = getNewTestProvider();
        providerIncludeExcludeOrderUndefined.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        
        
        
        profileIncludeAllImplicitly = getNewTestProfile();
        profileIncludeAllImplicitly.setAllowImplicitProviderInclusions(true);
        profileIncludeAllImplicitly.setAllowImplicitQueryInclusions(true);
        profileIncludeAllImplicitly.setAllowImplicitRdfRuleInclusions(true);
        profileIncludeAllImplicitly.setDefaultProfileIncludeExcludeOrder(getProfileExcludeThenIncludeURI());
        
        profileIncludeAllImplicitlyExcludeByDefault = getNewTestProfile();
        profileIncludeAllImplicitlyExcludeByDefault.setAllowImplicitProviderInclusions(true);
        profileIncludeAllImplicitlyExcludeByDefault.setAllowImplicitQueryInclusions(true);
        profileIncludeAllImplicitlyExcludeByDefault.setAllowImplicitRdfRuleInclusions(true);
        profileIncludeAllImplicitlyExcludeByDefault.setDefaultProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());
        
        profileExcludeImplicitly = getNewTestProfile();
        profileExcludeImplicitly.setAllowImplicitProviderInclusions(false);
        profileExcludeImplicitly.setAllowImplicitQueryInclusions(false);
        profileExcludeImplicitly.setAllowImplicitRdfRuleInclusions(false);
        profileExcludeImplicitly.setDefaultProfileIncludeExcludeOrder(getProfileExcludeThenIncludeURI());
        
        profileExcludeImplicitlyAndByDefault = getNewTestProfile();
        profileExcludeImplicitlyAndByDefault.setAllowImplicitProviderInclusions(false);
        profileExcludeImplicitlyAndByDefault.setAllowImplicitQueryInclusions(false);
        profileExcludeImplicitlyAndByDefault.setAllowImplicitRdfRuleInclusions(false);
        profileExcludeImplicitlyAndByDefault.setDefaultProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());
        
        profileIncludeTrueOnly = getNewTestProfile();
        profileIncludeTrueOnly.addIncludeProvider(testTrueProviderUri);
        profileIncludeTrueOnly.setAllowImplicitProviderInclusions(false);
        profileIncludeTrueOnly.setAllowImplicitQueryInclusions(false);
        profileIncludeTrueOnly.setAllowImplicitRdfRuleInclusions(false);
        profileIncludeTrueOnly.setDefaultProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());
        
        profileExcludeFalseOnly = getNewTestProfile();
        profileExcludeFalseOnly.addExcludeProvider(testFalseProviderUri);
        profileExcludeFalseOnly.setAllowImplicitProviderInclusions(false);
        profileExcludeFalseOnly.setAllowImplicitQueryInclusions(false);
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
    public abstract Provider getNewTestProvider();
    
    /**
     * This method must be overridden to return a new instance of 
     * the implemented Profile class for each successive invocation
     */
    public abstract Profile getNewTestProfile();
    

    public abstract URI getProfileIncludeExcludeOrderUndefinedUri();

    public abstract URI getProfileExcludeThenIncludeURI();
    
    public abstract URI getProfileIncludeThenExcludeURI();

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
        testTrueNamespaceUri = null;
        testFalseNamespaceUri = null;
        testTrueProviderUri = null;
        testFalseProviderUri = null;
        
        providerNonDefault = null;
        providerSpecificDefault = null;
        providerNoNamespacesDefault = null;
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
     * Test method for {@link org.queryall.Provider#containsQueryTypeUri(org.openrdf.model.URI)}.
     */
    @Test
    public void testHandlesQueryTypes()
    {
        assertTrue(providerSpecificDefault.containsQueryTypeUri(testTrueQueryTypeUri));
        assertFalse(providerSpecificDefault.containsQueryTypeUri(testFalseQueryTypeUri));
        assertTrue(providerNonDefault.containsQueryTypeUri(testTrueQueryTypeUri));
        assertFalse(providerNonDefault.containsQueryTypeUri(testFalseQueryTypeUri));
        assertTrue(providerNoNamespacesDefault.containsQueryTypeUri(testTrueQueryTypeUri));
        assertFalse(providerNoNamespacesDefault.containsQueryTypeUri(testFalseQueryTypeUri));
    }

    /**
     * Test method for {@link org.queryall.Provider#containsNamespaceUri(org.openrdf.model.URI)}.
     */
    @Test
    public void testContainsNamespaceUri()
    {
        assertTrue(providerSpecificDefault.containsNamespaceUri(testTrueNamespaceUri));
        assertFalse(providerSpecificDefault.containsNamespaceUri(testFalseNamespaceUri));
        assertTrue(providerNonDefault.containsNamespaceUri(testTrueNamespaceUri));
        assertFalse(providerNonDefault.containsNamespaceUri(testFalseNamespaceUri));
        assertFalse(providerNoNamespacesDefault.containsNamespaceUri(testTrueNamespaceUri));
        assertFalse(providerNoNamespacesDefault.containsNamespaceUri(testFalseNamespaceUri));
    }
    
    /**
     * Test method for {@link org.queryall.Provider#containsNamespaceOrDefault(org.openrdf.model.URI)}.
     */
    @Test
    public void testContainsNamespaceOrDefault()
    {
        assertTrue(providerSpecificDefault.containsNamespaceOrDefault(testTrueNamespaceUri));
        assertTrue(providerSpecificDefault.containsNamespaceOrDefault(testFalseNamespaceUri));
        assertTrue(providerNonDefault.containsNamespaceOrDefault(testTrueNamespaceUri));
        assertFalse(providerNonDefault.containsNamespaceOrDefault(testFalseNamespaceUri));
        assertTrue(providerNoNamespacesDefault.containsNamespaceOrDefault(testTrueNamespaceUri));
        assertTrue(providerNoNamespacesDefault.containsNamespaceOrDefault(testFalseNamespaceUri));

    }    

    /**
     * Test method for {@link org.queryall.Provider#isUsedWithProfileList(org.openrdf.model.URI, boolean, boolean)}.
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
    public void testisUsedWithProfileList()
    {
        assertTrue(providerIncludeImplicitly.isUsedWithProfileList(profileListEmpty, true, true));
        assertTrue(providerIncludeImplicitly.isUsedWithProfileList(profileListEmpty, false, true));
        assertFalse(providerIncludeImplicitly.isUsedWithProfileList(profileListEmpty, true, false));
        assertFalse(providerIncludeImplicitly.isUsedWithProfileList(profileListEmpty, false, false));
        
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListEmpty, true, true));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListEmpty, false, true));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListEmpty, true, false));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListEmpty, false, false));
        
        assertTrue(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListEmpty, true, true));
        assertTrue(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListEmpty, false, true));
        assertFalse(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListEmpty, true, false));
        assertFalse(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListEmpty, false, false));
        
        
        
        assertTrue(providerIncludeImplicitly.isUsedWithProfileList(profileListSingleIncludeAllImplicitly, true, true));
        assertTrue(providerIncludeImplicitly.isUsedWithProfileList(profileListSingleIncludeAllImplicitly, false, true));
        assertTrue(providerIncludeImplicitly.isUsedWithProfileList(profileListSingleIncludeAllImplicitly, true, false));
        assertFalse(providerIncludeImplicitly.isUsedWithProfileList(profileListSingleIncludeAllImplicitly, false, false));
        
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListSingleIncludeAllImplicitly, true, true));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListSingleIncludeAllImplicitly, false, true));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListSingleIncludeAllImplicitly, true, false));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListSingleIncludeAllImplicitly, false, false));
        
        assertTrue(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListSingleIncludeAllImplicitly, true, true));
        assertTrue(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListSingleIncludeAllImplicitly, false, true));
        assertTrue(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListSingleIncludeAllImplicitly, true, false));
        assertFalse(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListSingleIncludeAllImplicitly, false, false));
        
        
        
        assertTrue(providerIncludeImplicitly.isUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, true, true));
        assertTrue(providerIncludeImplicitly.isUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, false, true));
        assertTrue(providerIncludeImplicitly.isUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, true, false));
        assertFalse(providerIncludeImplicitly.isUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, false, false));
        
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, true, true));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, false, true));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, true, false));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, false, false));
        
        assertTrue(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, true, true));
        assertTrue(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, false, true));
        assertFalse(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, true, false));
        assertFalse(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, false, false));
        
        
        
        assertTrue(providerIncludeImplicitly.isUsedWithProfileList(profileListSingleExcludeImplicitly, true, true));
        assertTrue(providerIncludeImplicitly.isUsedWithProfileList(profileListSingleExcludeImplicitly, false, true));
        assertFalse(providerIncludeImplicitly.isUsedWithProfileList(profileListSingleExcludeImplicitly, true, false));
        assertFalse(providerIncludeImplicitly.isUsedWithProfileList(profileListSingleExcludeImplicitly, false, false));
        
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListSingleExcludeImplicitly, true, true));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListSingleExcludeImplicitly, false, true));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListSingleExcludeImplicitly, true, false));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListSingleExcludeImplicitly, false, false));
        
        assertTrue(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListSingleExcludeImplicitly, true, true));
        assertTrue(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListSingleExcludeImplicitly, false, true));
        assertFalse(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListSingleExcludeImplicitly, true, false));
        assertFalse(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListSingleExcludeImplicitly, false, false));
        
        
        
        assertTrue(providerIncludeImplicitly.isUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, true, true));
        assertTrue(providerIncludeImplicitly.isUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, false, true));
        assertFalse(providerIncludeImplicitly.isUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, true, false));
        assertFalse(providerIncludeImplicitly.isUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, false, false));
        
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, true, true));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, false, true));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, true, false));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, false, false));
        
        assertTrue(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, true, true));
        assertTrue(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, false, true));
        assertFalse(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, true, false));
        assertFalse(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, false, false));
        
        
        
        assertTrue(providerIncludeImplicitly.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, true, true));
        assertTrue(providerIncludeImplicitly.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, false, true));
        assertTrue(providerIncludeImplicitly.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, true, false));
        assertFalse(providerIncludeImplicitly.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, false, false));
        
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, true, true));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, false, true));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, true, false));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, false, false));
        
        assertTrue(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, true, true));
        assertTrue(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, false, true));
        assertTrue(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, true, false));
        assertFalse(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, false, false));
        

        
        assertTrue(providerIncludeImplicitly.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, true, true));
        assertTrue(providerIncludeImplicitly.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, false, true));
        assertFalse(providerIncludeImplicitly.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, true, false));
        assertFalse(providerIncludeImplicitly.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, false, false));
        
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, true, true));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, false, true));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, true, false));
        assertFalse(providerExcludeImplicitly.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, false, false));
        
        assertTrue(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, true, true));
        assertTrue(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, false, true));
        assertFalse(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, true, false));
        assertFalse(providerIncludeExcludeOrderUndefined.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, false, false));
        
        
        
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListSingleIncludeTrue, true, true));
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListSingleIncludeTrue, false, true));
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListSingleIncludeTrue, true, false));
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListSingleIncludeTrue, false, false));
        
        assertTrue(providerFalseUri.isUsedWithProfileList(profileListSingleIncludeTrue, true, true));
        assertTrue(providerFalseUri.isUsedWithProfileList(profileListSingleIncludeTrue, false, true));
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListSingleIncludeTrue, true, false));
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListSingleIncludeTrue, false, false));
        
        
        
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListSingleExcludeFalse, true, true));
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListSingleExcludeFalse, false, true));
        assertFalse(providerTrueUri.isUsedWithProfileList(profileListSingleExcludeFalse, true, false));
        assertFalse(providerTrueUri.isUsedWithProfileList(profileListSingleExcludeFalse, false, false));
        
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListSingleExcludeFalse, true, true));
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListSingleExcludeFalse, false, true));
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListSingleExcludeFalse, true, false));
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListSingleExcludeFalse, false, false));
        
        
        
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, true, true));
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, false, true));
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, true, false));
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, false, false));
        
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, true, true));
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, false, true));
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, true, false));
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, false, false));
        
        
        
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, true, true));
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, false, true));
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, true, false));
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, false, false));
        
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, true, true));
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, false, true));
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, true, false));
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, false, false));
        
        
        
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalse, true, true));
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalse, false, true));
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalse, true, false));
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalse, false, false));
        
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalse, true, true));
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalse, false, true));
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalse, true, false));
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListMultipleIncludeTrueThenExcludeFalse, false, false));
        
        
        
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListMultipleExcludeFalseThenIncludeTrue, true, true));
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListMultipleExcludeFalseThenIncludeTrue, false, true));
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListMultipleExcludeFalseThenIncludeTrue, true, false));
        assertTrue(providerTrueUri.isUsedWithProfileList(profileListMultipleExcludeFalseThenIncludeTrue, false, false));
        
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListMultipleExcludeFalseThenIncludeTrue, true, true));
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListMultipleExcludeFalseThenIncludeTrue, false, true));
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListMultipleExcludeFalseThenIncludeTrue, true, false));
        assertFalse(providerFalseUri.isUsedWithProfileList(profileListMultipleExcludeFalseThenIncludeTrue, false, false));
        
        
        
    }
}
