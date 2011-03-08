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
    private Provider providerNonDefault;
    private Provider providerSpecificDefault;
    private Provider providerNoNamespacesDefault;
    
    protected URI testTrueQueryTypeUri;
    protected URI testFalseQueryTypeUri;
    protected URI testTrueRuleUri;
    protected URI testFalseRuleUri;
    protected URI testTrueNamespaceUri;
    protected URI testFalseNamespaceUri;
    
    private Provider providerIncludeImplicitly;
    
    private List<Profile> profileListEmpty;
    private LinkedList<Profile> profileListSingleIncludeAllImplicitly;
    private LinkedList<Profile> profileListSingleExcludeImplicitly;
    private LinkedList<Profile> profileListSingleExcludeExplicitlyAndByDefault;
    
    private Profile profileIncludeAllImplicitly;
    private Provider providerExcludeImplicitly;
    private Profile profileExcludeImplicitly;
    private Profile profileExcludeImplicitlyAndByDefault;
    private Provider providerIncludeExcludeOrderUndefined;
    private Profile profileIncludeAllImplicitlyExcludeByDefault;
    private LinkedList<Profile> profileListSingleIncludeAllImplicitlyExcludeByDefault;
    
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

        providerNonDefault = getNewTestProvider();
        providerNonDefault.setIsDefaultSource(false);
        providerNonDefault.addIncludedInQueryType(testTrueQueryTypeUri);
        providerNonDefault.addNormalisationUri(testTrueRuleUri);
        providerNonDefault.addNamespace(testTrueNamespaceUri);
        
        providerSpecificDefault = getNewTestProvider();
        providerSpecificDefault.setIsDefaultSource(true);
        providerSpecificDefault.addIncludedInQueryType(testTrueQueryTypeUri);
        providerSpecificDefault.addNormalisationUri(testTrueRuleUri);
        providerSpecificDefault.addNamespace(testTrueNamespaceUri);
        
        providerNoNamespacesDefault = getNewTestProvider();
        providerNoNamespacesDefault.setIsDefaultSource(true);
        providerNoNamespacesDefault.addIncludedInQueryType(testTrueQueryTypeUri);
        providerNoNamespacesDefault.addNormalisationUri(testTrueRuleUri);
        
        providerIncludeImplicitly = getNewTestProvider();
        providerIncludeImplicitly.setProfileIncludeExcludeOrder(getProfileExcludeThenIncludeURI());
        
        providerExcludeImplicitly = getNewTestProvider();
        providerExcludeImplicitly.setProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());
        
        providerIncludeExcludeOrderUndefined = getNewTestProvider();
        providerIncludeExcludeOrderUndefined.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        
        profileListEmpty = new LinkedList<Profile>();

        profileIncludeAllImplicitly = getNewTestProfile();
        profileIncludeAllImplicitly.setAllowImplicitProviderInclusions(true);
        profileIncludeAllImplicitly.setDefaultProfileIncludeExcludeOrder(getProfileExcludeThenIncludeURI());
        
        profileIncludeAllImplicitlyExcludeByDefault = getNewTestProfile();
        profileIncludeAllImplicitlyExcludeByDefault.setAllowImplicitProviderInclusions(true);
        profileIncludeAllImplicitlyExcludeByDefault.setDefaultProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());
        
        profileExcludeImplicitly = getNewTestProfile();
        profileExcludeImplicitly.setAllowImplicitProviderInclusions(false);
        profileExcludeImplicitly.setDefaultProfileIncludeExcludeOrder(getProfileExcludeThenIncludeURI());
        
        profileExcludeImplicitlyAndByDefault = getNewTestProfile();
        profileExcludeImplicitlyAndByDefault.setAllowImplicitProviderInclusions(false);
        profileExcludeImplicitlyAndByDefault.setDefaultProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());
        
        profileListSingleIncludeAllImplicitly = new LinkedList<Profile>();
        profileListSingleIncludeAllImplicitly.add(profileIncludeAllImplicitly);
        
        profileListSingleIncludeAllImplicitlyExcludeByDefault = new LinkedList<Profile>();
        profileListSingleIncludeAllImplicitlyExcludeByDefault.add(profileIncludeAllImplicitlyExcludeByDefault);
        
        profileListSingleExcludeImplicitly = new LinkedList<Profile>();
        profileListSingleExcludeImplicitly.add(profileExcludeImplicitly);
        
        profileListSingleExcludeExplicitlyAndByDefault = new LinkedList<Profile>();
        profileListSingleExcludeExplicitlyAndByDefault.add(profileExcludeImplicitlyAndByDefault);
    }
    
    public abstract URI getProfileIncludeExcludeOrderUndefinedUri();

    public abstract URI getProfileExcludeThenIncludeURI();
    
    public abstract URI getProfileIncludeThenExcludeURI();
    
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
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        providerSpecificDefault = null;
        providerNonDefault = null;
        providerNoNamespacesDefault = null;
        testTrueQueryTypeUri = null;
        testFalseQueryTypeUri = null;
        testTrueRuleUri = null;
        testFalseRuleUri = null;
        testTrueNamespaceUri = null;
        testFalseNamespaceUri = null;
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
     * Test method for {@link org.queryall.Provider#containsNamespaceOrDefault(org.openrdf.model.URI)}.
     */
    @Test
    public void testIsProviderUsedWithProfileList()
    {
        assertTrue(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListEmpty, true, true));
        assertTrue(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListEmpty, false, true));
        assertFalse(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListEmpty, true, false));
        assertFalse(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListEmpty, false, false));
        
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListEmpty, true, true));
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListEmpty, false, true));
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListEmpty, true, false));
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListEmpty, false, false));
        
        assertTrue(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListEmpty, true, true));
        assertTrue(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListEmpty, false, true));
        assertFalse(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListEmpty, true, false));
        assertFalse(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListEmpty, false, false));
        
        
        
        assertTrue(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitly, true, true));
        assertTrue(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitly, false, true));
        assertTrue(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitly, true, false));
        assertFalse(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitly, false, false));
        
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitly, true, true));
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitly, false, true));
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitly, true, false));
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitly, false, false));
        
        assertTrue(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitly, true, true));
        assertTrue(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitly, false, true));
        assertTrue(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitly, true, false));
        assertFalse(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitly, false, false));
        
        
        
        assertTrue(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, true, true));
        assertTrue(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, false, true));
        assertTrue(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, true, false));
        assertFalse(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, false, false));
        
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, true, true));
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, false, true));
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, true, false));
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, false, false));
        
        assertTrue(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, true, true));
        assertTrue(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, false, true));
        assertFalse(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, true, false));
        assertFalse(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListSingleIncludeAllImplicitlyExcludeByDefault, false, false));
        
        
        
        assertTrue(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListSingleExcludeImplicitly, true, true));
        assertTrue(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListSingleExcludeImplicitly, false, true));
        assertFalse(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListSingleExcludeImplicitly, true, false));
        assertFalse(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListSingleExcludeImplicitly, false, false));
        
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListSingleExcludeImplicitly, true, true));
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListSingleExcludeImplicitly, false, true));
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListSingleExcludeImplicitly, true, false));
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListSingleExcludeImplicitly, false, false));
        
        assertTrue(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListSingleExcludeImplicitly, true, true));
        assertTrue(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListSingleExcludeImplicitly, false, true));
        assertFalse(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListSingleExcludeImplicitly, true, false));
        assertFalse(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListSingleExcludeImplicitly, false, false));
        
        
        
        assertTrue(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, true, true));
        assertTrue(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, false, true));
        assertFalse(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, true, false));
        assertFalse(providerIncludeImplicitly.isProviderUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, false, false));
        
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, true, true));
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, false, true));
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, true, false));
        assertFalse(providerExcludeImplicitly.isProviderUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, false, false));
        
        assertTrue(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, true, true));
        assertTrue(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, false, true));
        assertFalse(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, true, false));
        assertFalse(providerIncludeExcludeOrderUndefined.isProviderUsedWithProfileList(profileListSingleExcludeExplicitlyAndByDefault, false, false));
        
    }
}
