/**
 * 
 */
package org.queryall.api.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.queryall.api.Profile;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractProfileTest
{
    private Profile testProfile1;
    private Profile testProfile2;
    private URI testProfileAdministratorUri;
    private URI testIncludeProvider1;
    private URI testExcludeProvider1;
    private URI testIncludeQueryType1;
    private URI testExcludeQueryType1;
    private URI testIncludeRdfRule1;
    private URI testExcludeRdfRule1;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        ValueFactory valueFactory = new MemValueFactory();
        
        testProfile1 = getNewTestProfile();
        testProfile2 = getNewTestProfile();
        
        testProfileAdministratorUri = valueFactory.createURI("http://example.org/test/profile/administrator/1");
        
        testIncludeProvider1 = valueFactory.createURI("http://example.org/test/provider/include/1");
        testExcludeProvider1 = valueFactory.createURI("http://example.org/test/provider/exclude/1");

        testIncludeQueryType1 = valueFactory.createURI("http://example.org/test/querytype/include/1");
        testExcludeQueryType1 = valueFactory.createURI("http://example.org/test/querytype/exclude/1");

        testIncludeRdfRule1 = valueFactory.createURI("http://example.org/test/rdfrule/include/1");
        testExcludeRdfRule1 = valueFactory.createURI("http://example.org/test/rdfrule/exclude/1");
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        testProfile1 = null;
        testProfile2 = null;
        
        testProfileAdministratorUri = null;

        testIncludeProvider1 = null;
        testExcludeProvider1 = null;
        
        testIncludeQueryType1 = null;
        testExcludeQueryType1 = null;

        testIncludeRdfRule1 = null;
        testExcludeRdfRule1 = null;
    }
    
    /**
     * Needs to be overriden in concrete test cases to provide a new object implementing the Profile
     * interface for each invocation.
     * 
     * @return a new Profile instance
     */
    public abstract Profile getNewTestProfile();
    

    public abstract URI getProfileIncludeExcludeOrderUndefinedUri();

    public abstract URI getProfileExcludeThenIncludeURI();
    
    public abstract URI getProfileIncludeThenExcludeURI();

    /**
     * Test method for {@link org.queryall.api.Profile#compareTo(org.queryall.api.Profile)}.
     */
    @Test
    public final void testCompareTo()
    {
        // two empty profiles should be equal
        assertEquals(0, testProfile1.compareTo(testProfile2));
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#getOrder()}.
     */
    @Test
    public final void testGetOrder()
    {
        testProfile1.setOrder(100);
        testProfile2.setOrder(200);
        
        assertEquals(100, testProfile1.getOrder());
        assertEquals(200, testProfile2.getOrder());
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#setOrder(int)}.
     */
    @Test
    public final void testSetOrder()
    {
        testProfile1.setOrder(100);
        testProfile2.setOrder(200);
        
        assertEquals(100, testProfile1.getOrder());
        assertEquals(200, testProfile2.getOrder());
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#getDefaultProfileIncludeExcludeOrder()}.
     */
    @Test
    public final void testGetDefaultProfileIncludeExcludeOrder()
    {
        assertEquals(getProfileIncludeExcludeOrderUndefinedUri(), testProfile1.getDefaultProfileIncludeExcludeOrder());
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.Profile#setDefaultProfileIncludeExcludeOrder(org.openrdf.model.URI)}.
     */
    @Test
    public final void testSetDefaultProfileIncludeExcludeOrder()
    {
        assertEquals(getProfileIncludeExcludeOrderUndefinedUri(), testProfile1.getDefaultProfileIncludeExcludeOrder());
        
        testProfile1.setDefaultProfileIncludeExcludeOrder(getProfileExcludeThenIncludeURI());
        
        assertEquals(getProfileExcludeThenIncludeURI(), testProfile1.getDefaultProfileIncludeExcludeOrder());

        testProfile2.setDefaultProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());
        
        assertEquals(getProfileIncludeThenExcludeURI(), testProfile2.getDefaultProfileIncludeExcludeOrder());
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#setAllowImplicitQueryTypeInclusions(boolean)}
     * .
     */
    @Test
    public final void testSetAllowImplicitQueryTypeInclusions()
    {
        assertFalse(testProfile1.getAllowImplicitQueryTypeInclusions());
        
        testProfile1.setAllowImplicitQueryTypeInclusions(true);
        
        assertTrue(testProfile1.getAllowImplicitQueryTypeInclusions());
        
        assertFalse(testProfile2.getAllowImplicitQueryTypeInclusions());
        
        testProfile2.setAllowImplicitQueryTypeInclusions(false);

        assertFalse(testProfile2.getAllowImplicitQueryTypeInclusions());
        
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#getAllowImplicitQueryTypeInclusions()}.
     */
    @Test
    public final void testGetAllowImplicitQueryTypeInclusions()
    {
        assertFalse(testProfile1.getAllowImplicitQueryTypeInclusions());
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#setAllowImplicitProviderInclusions(boolean)}
     * .
     */
    @Test
    public final void testSetAllowImplicitProviderInclusions()
    {
        assertFalse(testProfile1.getAllowImplicitProviderInclusions());
        
        testProfile1.setAllowImplicitProviderInclusions(true);
        
        assertTrue(testProfile1.getAllowImplicitProviderInclusions());
        
        assertFalse(testProfile2.getAllowImplicitProviderInclusions());
        
        testProfile2.setAllowImplicitProviderInclusions(false);

        assertFalse(testProfile2.getAllowImplicitProviderInclusions());
        
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#getAllowImplicitProviderInclusions()}.
     */
    @Test
    public final void testGetAllowImplicitProviderInclusions()
    {
        assertFalse(testProfile1.getAllowImplicitProviderInclusions());
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#setAllowImplicitRdfRuleInclusions(boolean)}
     * .
     */
    @Test
    public final void testSetAllowImplicitRdfRuleInclusions()
    {
        assertFalse(testProfile1.getAllowImplicitRdfRuleInclusions());
        
        testProfile1.setAllowImplicitRdfRuleInclusions(true);
        
        assertTrue(testProfile1.getAllowImplicitRdfRuleInclusions());
        
        assertFalse(testProfile2.getAllowImplicitRdfRuleInclusions());
        
        testProfile2.setAllowImplicitRdfRuleInclusions(false);

        assertFalse(testProfile2.getAllowImplicitRdfRuleInclusions());
        
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#getAllowImplicitRdfRuleInclusions()}.
     */
    @Test
    public final void testGetAllowImplicitRdfRuleInclusions()
    {
        assertFalse(testProfile1.getAllowImplicitRdfRuleInclusions());
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.Profile#addProfileAdministrators(org.openrdf.model.URI)}.
     */
    @Test
    public final void testAddProfileAdministrator()
    {
        assertEquals(0, testProfile1.getProfileAdministrators().size());

        testProfile1.addProfileAdministrator(testProfileAdministratorUri);
        
        assertEquals(1, testProfile1.getProfileAdministrators().size());

        assertEquals(testProfileAdministratorUri, testProfile1.getProfileAdministrators().toArray()[0]);
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#getProfileAdministrators()}.
     */
    @Test
    public final void testGetProfileAdministrators()
    {
        assertEquals(0, testProfile1.getProfileAdministrators().size());

        testProfile1.addProfileAdministrator(testProfileAdministratorUri);
        
        assertEquals(1, testProfile1.getProfileAdministrators().size());

        assertEquals(testProfileAdministratorUri, testProfile1.getProfileAdministrators().toArray()[0]);
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#addIncludeProvider(org.openrdf.model.URI)}.
     */
    @Test
    public final void testAddIncludeProvider()
    {
        testProfile1.addIncludeProvider(testIncludeProvider1);
        
        assertTrue(testProfile1.getIncludeProviders().contains(testIncludeProvider1));
        assertFalse(testProfile1.getIncludeProviders().contains(testExcludeProvider1));
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#getIncludeProviders()}.
     */
    @Test
    public final void testGetIncludeProviders()
    {
        testProfile1.addIncludeProvider(testIncludeProvider1);
        
        assertTrue(testProfile1.getIncludeProviders().contains(testIncludeProvider1));
        assertFalse(testProfile1.getIncludeProviders().contains(testExcludeProvider1));
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#addExcludeProvider(org.openrdf.model.URI)}.
     */
    @Test
    public final void testAddExcludeProvider()
    {
        testProfile1.addExcludeProvider(testExcludeProvider1);
        
        assertFalse(testProfile1.getExcludeProviders().contains(testIncludeProvider1));
        assertTrue(testProfile1.getExcludeProviders().contains(testExcludeProvider1));
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#getExcludeProviders()}.
     */
    @Test
    public final void testGetExcludeProviders()
    {
        testProfile1.addExcludeProvider(testExcludeProvider1);
        
        assertFalse(testProfile1.getExcludeProviders().contains(testIncludeProvider1));
        assertTrue(testProfile1.getExcludeProviders().contains(testExcludeProvider1));
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#addIncludeQueryType(org.openrdf.model.URI)}.
     */
    @Test
    public final void testAddIncludeQueryType()
    {
        testProfile1.addIncludeQueryType(testIncludeQueryType1);
        
        assertTrue(testProfile1.getIncludeQueryTypes().contains(testIncludeQueryType1));
        assertFalse(testProfile1.getIncludeQueryTypes().contains(testExcludeQueryType1));
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#getIncludeQueryTypes()}.
     */
    @Test
    public final void testGetIncludeQueryTypes()
    {
        testProfile1.addIncludeQueryType(testIncludeQueryType1);
        
        assertTrue(testProfile1.getIncludeQueryTypes().contains(testIncludeQueryType1));
        assertFalse(testProfile1.getIncludeQueryTypes().contains(testExcludeQueryType1));
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#addExcludeQueryType(org.openrdf.model.URI)}.
     */
    @Test
    public final void testAddExcludeQueryType()
    {
        testProfile1.addExcludeQueryType(testExcludeQueryType1);
        
        assertFalse(testProfile1.getExcludeQueryTypes().contains(testIncludeQueryType1));
        assertTrue(testProfile1.getExcludeQueryTypes().contains(testExcludeQueryType1));
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#getExcludeQueryTypes()}.
     */
    @Test
    public final void testGetExcludeQueryTypes()
    {
        testProfile1.addExcludeQueryType(testExcludeQueryType1);
        
        assertFalse(testProfile1.getExcludeQueryTypes().contains(testIncludeQueryType1));
        assertTrue(testProfile1.getExcludeQueryTypes().contains(testExcludeQueryType1));
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#addIncludeRdfRule(org.openrdf.model.URI)}.
     */
    @Test
    public final void testAddIncludeRdfRule()
    {
        testProfile1.addIncludeRdfRule(testIncludeRdfRule1);
        
        assertTrue(testProfile1.getIncludeRdfRules().contains(testIncludeRdfRule1));
        assertFalse(testProfile1.getIncludeRdfRules().contains(testExcludeRdfRule1));
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#getIncludeRdfRules()}.
     */
    @Test
    public final void testGetIncludeRdfRules()
    {
        testProfile1.addIncludeRdfRule(testIncludeRdfRule1);
        
        assertTrue(testProfile1.getIncludeRdfRules().contains(testIncludeRdfRule1));
        assertFalse(testProfile1.getIncludeRdfRules().contains(testExcludeRdfRule1));
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#addExcludeRdfRule(org.openrdf.model.URI)}.
     */
    @Test
    public final void testAddExcludeRdfRule()
    {
        testProfile1.addExcludeRdfRule(testExcludeRdfRule1);
        
        assertFalse(testProfile1.getExcludeRdfRules().contains(testIncludeRdfRule1));
        assertTrue(testProfile1.getExcludeRdfRules().contains(testExcludeRdfRule1));
    }
    
    /**
     * Test method for {@link org.queryall.api.Profile#getExcludeRdfRules()}.
     */
    @Test
    public final void testGetExcludeRdfRules()
    {
        testProfile1.addExcludeRdfRule(testExcludeRdfRule1);
        
        assertFalse(testProfile1.getExcludeRdfRules().contains(testIncludeRdfRule1));
        assertTrue(testProfile1.getExcludeRdfRules().contains(testExcludeRdfRule1));
    }
    
    
}
