/**
 * 
 */
package org.queryall.api.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.queryall.api.profile.Profile;
import org.queryall.api.profile.ProfileSchema;

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
     * Needs to be overriden in concrete test cases to provide a new object implementing the Profile
     * interface for each invocation.
     * 
     * @return a new Profile instance
     */
    public abstract Profile getNewTestProfile();
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        final ValueFactory valueFactory = new MemValueFactory();
        
        this.testProfile1 = this.getNewTestProfile();
        this.testProfile2 = this.getNewTestProfile();
        
        this.testProfileAdministratorUri = valueFactory.createURI("http://example.org/test/profile/administrator/1");
        
        this.testIncludeProvider1 = valueFactory.createURI("http://example.org/test/provider/include/1");
        this.testExcludeProvider1 = valueFactory.createURI("http://example.org/test/provider/exclude/1");
        
        this.testIncludeQueryType1 = valueFactory.createURI("http://example.org/test/querytype/include/1");
        this.testExcludeQueryType1 = valueFactory.createURI("http://example.org/test/querytype/exclude/1");
        
        this.testIncludeRdfRule1 = valueFactory.createURI("http://example.org/test/rdfrule/include/1");
        this.testExcludeRdfRule1 = valueFactory.createURI("http://example.org/test/rdfrule/exclude/1");
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.testProfile1 = null;
        this.testProfile2 = null;
        
        this.testProfileAdministratorUri = null;
        
        this.testIncludeProvider1 = null;
        this.testExcludeProvider1 = null;
        
        this.testIncludeQueryType1 = null;
        this.testExcludeQueryType1 = null;
        
        this.testIncludeRdfRule1 = null;
        this.testExcludeRdfRule1 = null;
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.profile.Profile#addExcludeProvider(org.openrdf.model.URI)} .
     */
    @Test
    public final void testAddExcludeProvider()
    {
        this.testProfile1.addExcludeProvider(this.testExcludeProvider1);
        
        Assert.assertFalse(this.testProfile1.getExcludeProviders().contains(this.testIncludeProvider1));
        Assert.assertTrue(this.testProfile1.getExcludeProviders().contains(this.testExcludeProvider1));
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.profile.Profile#addExcludeQueryType(org.openrdf.model.URI)} .
     */
    @Test
    public final void testAddExcludeQueryType()
    {
        this.testProfile1.addExcludeQueryType(this.testExcludeQueryType1);
        
        Assert.assertFalse(this.testProfile1.getExcludeQueryTypes().contains(this.testIncludeQueryType1));
        Assert.assertTrue(this.testProfile1.getExcludeQueryTypes().contains(this.testExcludeQueryType1));
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.profile.Profile#addExcludeRdfRule(org.openrdf.model.URI)} .
     */
    @Test
    public final void testAddExcludeRdfRule()
    {
        this.testProfile1.addExcludeRdfRule(this.testExcludeRdfRule1);
        
        Assert.assertFalse(this.testProfile1.getExcludeRdfRules().contains(this.testIncludeRdfRule1));
        Assert.assertTrue(this.testProfile1.getExcludeRdfRules().contains(this.testExcludeRdfRule1));
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.profile.Profile#addIncludeProvider(org.openrdf.model.URI)} .
     */
    @Test
    public final void testAddIncludeProvider()
    {
        this.testProfile1.addIncludeProvider(this.testIncludeProvider1);
        
        Assert.assertTrue(this.testProfile1.getIncludeProviders().contains(this.testIncludeProvider1));
        Assert.assertFalse(this.testProfile1.getIncludeProviders().contains(this.testExcludeProvider1));
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.profile.Profile#addIncludeQueryType(org.openrdf.model.URI)} .
     */
    @Test
    public final void testAddIncludeQueryType()
    {
        this.testProfile1.addIncludeQueryType(this.testIncludeQueryType1);
        
        Assert.assertTrue(this.testProfile1.getIncludeQueryTypes().contains(this.testIncludeQueryType1));
        Assert.assertFalse(this.testProfile1.getIncludeQueryTypes().contains(this.testExcludeQueryType1));
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.profile.Profile#addIncludeRdfRule(org.openrdf.model.URI)} .
     */
    @Test
    public final void testAddIncludeRdfRule()
    {
        this.testProfile1.addIncludeRdfRule(this.testIncludeRdfRule1);
        
        Assert.assertTrue(this.testProfile1.getIncludeRdfRules().contains(this.testIncludeRdfRule1));
        Assert.assertFalse(this.testProfile1.getIncludeRdfRules().contains(this.testExcludeRdfRule1));
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.profile.Profile#addProfileAdministrators(org.openrdf.model.URI)} .
     */
    @Test
    public final void testAddProfileAdministrator()
    {
        Assert.assertEquals(0, this.testProfile1.getProfileAdministrators().size());
        
        this.testProfile1.addProfileAdministrator(this.testProfileAdministratorUri);
        
        Assert.assertEquals(1, this.testProfile1.getProfileAdministrators().size());
        
        Assert.assertEquals(this.testProfileAdministratorUri, this.testProfile1.getProfileAdministrators().toArray()[0]);
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.profile.Profile#compareTo(org.queryall.api.profile.Profile)} .
     */
    @Test
    public final void testCompareTo()
    {
        // two empty profiles should be equal
        Assert.assertEquals(0, this.testProfile1.compareTo(this.testProfile2));
    }
    
    /**
     * Test method for {@link org.queryall.api.profile.Profile#getAllowImplicitProviderInclusions()}
     * .
     */
    @Test
    public final void testGetAllowImplicitProviderInclusions()
    {
        Assert.assertFalse(this.testProfile1.getAllowImplicitProviderInclusions());
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.profile.Profile#getAllowImplicitQueryTypeInclusions()} .
     */
    @Test
    public final void testGetAllowImplicitQueryTypeInclusions()
    {
        Assert.assertFalse(this.testProfile1.getAllowImplicitQueryTypeInclusions());
    }
    
    /**
     * Test method for {@link org.queryall.api.profile.Profile#getAllowImplicitRdfRuleInclusions()}
     * .
     */
    @Test
    public final void testGetAllowImplicitRdfRuleInclusions()
    {
        Assert.assertFalse(this.testProfile1.getAllowImplicitRdfRuleInclusions());
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.profile.Profile#getDefaultProfileIncludeExcludeOrder()} .
     */
    @Test
    public final void testGetDefaultProfileIncludeExcludeOrder()
    {
        Assert.assertEquals(ProfileSchema.getProfileIncludeExcludeOrderUndefinedUri(),
                this.testProfile1.getDefaultProfileIncludeExcludeOrder());
    }
    
    /**
     * Test method for {@link org.queryall.api.profile.Profile#getExcludeProviders()}.
     */
    @Test
    public final void testGetExcludeProviders()
    {
        this.testProfile1.addExcludeProvider(this.testExcludeProvider1);
        
        Assert.assertFalse(this.testProfile1.getExcludeProviders().contains(this.testIncludeProvider1));
        Assert.assertTrue(this.testProfile1.getExcludeProviders().contains(this.testExcludeProvider1));
    }
    
    /**
     * Test method for {@link org.queryall.api.profile.Profile#getExcludeQueryTypes()}.
     */
    @Test
    public final void testGetExcludeQueryTypes()
    {
        this.testProfile1.addExcludeQueryType(this.testExcludeQueryType1);
        
        Assert.assertFalse(this.testProfile1.getExcludeQueryTypes().contains(this.testIncludeQueryType1));
        Assert.assertTrue(this.testProfile1.getExcludeQueryTypes().contains(this.testExcludeQueryType1));
    }
    
    /**
     * Test method for {@link org.queryall.api.profile.Profile#getExcludeRdfRules()}.
     */
    @Test
    public final void testGetExcludeRdfRules()
    {
        this.testProfile1.addExcludeRdfRule(this.testExcludeRdfRule1);
        
        Assert.assertFalse(this.testProfile1.getExcludeRdfRules().contains(this.testIncludeRdfRule1));
        Assert.assertTrue(this.testProfile1.getExcludeRdfRules().contains(this.testExcludeRdfRule1));
    }
    
    /**
     * Test method for {@link org.queryall.api.profile.Profile#getIncludeProviders()}.
     */
    @Test
    public final void testGetIncludeProviders()
    {
        this.testProfile1.addIncludeProvider(this.testIncludeProvider1);
        
        Assert.assertTrue(this.testProfile1.getIncludeProviders().contains(this.testIncludeProvider1));
        Assert.assertFalse(this.testProfile1.getIncludeProviders().contains(this.testExcludeProvider1));
    }
    
    /**
     * Test method for {@link org.queryall.api.profile.Profile#getIncludeQueryTypes()}.
     */
    @Test
    public final void testGetIncludeQueryTypes()
    {
        this.testProfile1.addIncludeQueryType(this.testIncludeQueryType1);
        
        Assert.assertTrue(this.testProfile1.getIncludeQueryTypes().contains(this.testIncludeQueryType1));
        Assert.assertFalse(this.testProfile1.getIncludeQueryTypes().contains(this.testExcludeQueryType1));
    }
    
    /**
     * Test method for {@link org.queryall.api.profile.Profile#getIncludeRdfRules()}.
     */
    @Test
    public final void testGetIncludeRdfRules()
    {
        this.testProfile1.addIncludeRdfRule(this.testIncludeRdfRule1);
        
        Assert.assertTrue(this.testProfile1.getIncludeRdfRules().contains(this.testIncludeRdfRule1));
        Assert.assertFalse(this.testProfile1.getIncludeRdfRules().contains(this.testExcludeRdfRule1));
    }
    
    /**
     * Test method for {@link org.queryall.api.profile.Profile#getOrder()}.
     */
    @Test
    public final void testGetOrder()
    {
        this.testProfile1.setOrder(100);
        this.testProfile2.setOrder(200);
        
        Assert.assertEquals(100, this.testProfile1.getOrder());
        Assert.assertEquals(200, this.testProfile2.getOrder());
    }
    
    /**
     * Test method for {@link org.queryall.api.profile.Profile#getProfileAdministrators()}.
     */
    @Test
    public final void testGetProfileAdministrators()
    {
        Assert.assertEquals(0, this.testProfile1.getProfileAdministrators().size());
        
        this.testProfile1.addProfileAdministrator(this.testProfileAdministratorUri);
        
        Assert.assertEquals(1, this.testProfile1.getProfileAdministrators().size());
        
        Assert.assertEquals(this.testProfileAdministratorUri, this.testProfile1.getProfileAdministrators().toArray()[0]);
    }
    
    @Test
    public void testResetExcludeProviders()
    {
        final Profile testProfile = this.getNewTestProfile();
        
        Assert.assertEquals(0, testProfile.getExcludeProviders().size());
        
        testProfile.addExcludeProvider(this.testExcludeProvider1);
        
        Assert.assertEquals(1, testProfile.getExcludeProviders().size());
        
        Assert.assertTrue(testProfile.resetExcludedProviders());
        
        Assert.assertEquals(0, testProfile.getExcludeProviders().size());
    }
    
    @Test
    public void testResetExcludeQueryTypes()
    {
        final Profile testProfile = this.getNewTestProfile();
        
        Assert.assertEquals(0, testProfile.getExcludeQueryTypes().size());
        
        testProfile.addExcludeQueryType(this.testExcludeQueryType1);
        
        Assert.assertEquals(1, testProfile.getExcludeQueryTypes().size());
        
        Assert.assertTrue(testProfile.resetExcludedQueryTypes());
        
        Assert.assertEquals(0, testProfile.getExcludeQueryTypes().size());
    }
    
    @Test
    public void testResetExcludeRdfRules()
    {
        final Profile testProfile = this.getNewTestProfile();
        
        Assert.assertEquals(0, testProfile.getExcludeRdfRules().size());
        
        testProfile.addExcludeRdfRule(this.testExcludeRdfRule1);
        
        Assert.assertEquals(1, testProfile.getExcludeRdfRules().size());
        
        Assert.assertTrue(testProfile.resetExcludedRdfRules());
        
        Assert.assertEquals(0, testProfile.getExcludeRdfRules().size());
    }
    
    @Test
    public void testResetIncludeProviders()
    {
        final Profile testProfile = this.getNewTestProfile();
        
        Assert.assertEquals(0, testProfile.getIncludeProviders().size());
        
        testProfile.addIncludeProvider(this.testIncludeProvider1);
        
        Assert.assertEquals(1, testProfile.getIncludeProviders().size());
        
        Assert.assertTrue(testProfile.resetIncludedProviders());
        
        Assert.assertEquals(0, testProfile.getIncludeProviders().size());
    }
    
    @Test
    public void testResetIncludeQueryTypes()
    {
        final Profile testProfile = this.getNewTestProfile();
        
        Assert.assertEquals(0, testProfile.getIncludeQueryTypes().size());
        
        testProfile.addIncludeQueryType(this.testIncludeQueryType1);
        
        Assert.assertEquals(1, testProfile.getIncludeQueryTypes().size());
        
        Assert.assertTrue(testProfile.resetIncludedQueryTypes());
        
        Assert.assertEquals(0, testProfile.getIncludeQueryTypes().size());
    }
    
    @Test
    public void testResetIncludeRdfRules()
    {
        final Profile testProfile = this.getNewTestProfile();
        
        Assert.assertEquals(0, testProfile.getIncludeRdfRules().size());
        
        testProfile.addIncludeRdfRule(this.testIncludeRdfRule1);
        
        Assert.assertEquals(1, testProfile.getIncludeRdfRules().size());
        
        Assert.assertTrue(testProfile.resetIncludedRdfRules());
        
        Assert.assertEquals(0, testProfile.getIncludeRdfRules().size());
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.profile.Profile#setAllowImplicitProviderInclusions(boolean)} .
     */
    @Test
    public final void testSetAllowImplicitProviderInclusions()
    {
        Assert.assertFalse(this.testProfile1.getAllowImplicitProviderInclusions());
        
        this.testProfile1.setAllowImplicitProviderInclusions(true);
        
        Assert.assertTrue(this.testProfile1.getAllowImplicitProviderInclusions());
        
        Assert.assertFalse(this.testProfile2.getAllowImplicitProviderInclusions());
        
        this.testProfile2.setAllowImplicitProviderInclusions(false);
        
        Assert.assertFalse(this.testProfile2.getAllowImplicitProviderInclusions());
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.profile.Profile#setAllowImplicitQueryTypeInclusions(boolean)} .
     */
    @Test
    public final void testSetAllowImplicitQueryTypeInclusions()
    {
        Assert.assertFalse(this.testProfile1.getAllowImplicitQueryTypeInclusions());
        
        this.testProfile1.setAllowImplicitQueryTypeInclusions(true);
        
        Assert.assertTrue(this.testProfile1.getAllowImplicitQueryTypeInclusions());
        
        Assert.assertFalse(this.testProfile2.getAllowImplicitQueryTypeInclusions());
        
        this.testProfile2.setAllowImplicitQueryTypeInclusions(false);
        
        Assert.assertFalse(this.testProfile2.getAllowImplicitQueryTypeInclusions());
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.profile.Profile#setAllowImplicitRdfRuleInclusions(boolean)} .
     */
    @Test
    public final void testSetAllowImplicitRdfRuleInclusions()
    {
        Assert.assertFalse(this.testProfile1.getAllowImplicitRdfRuleInclusions());
        
        this.testProfile1.setAllowImplicitRdfRuleInclusions(true);
        
        Assert.assertTrue(this.testProfile1.getAllowImplicitRdfRuleInclusions());
        
        Assert.assertFalse(this.testProfile2.getAllowImplicitRdfRuleInclusions());
        
        this.testProfile2.setAllowImplicitRdfRuleInclusions(false);
        
        Assert.assertFalse(this.testProfile2.getAllowImplicitRdfRuleInclusions());
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.profile.Profile#setDefaultProfileIncludeExcludeOrder(org.openrdf.model.URI)}
     * .
     */
    @Test
    public final void testSetDefaultProfileIncludeExcludeOrder()
    {
        Assert.assertEquals(ProfileSchema.getProfileIncludeExcludeOrderUndefinedUri(),
                this.testProfile1.getDefaultProfileIncludeExcludeOrder());
        
        this.testProfile1.setDefaultProfileIncludeExcludeOrder(ProfileSchema.getProfileExcludeThenIncludeUri());
        
        Assert.assertEquals(ProfileSchema.getProfileExcludeThenIncludeUri(),
                this.testProfile1.getDefaultProfileIncludeExcludeOrder());
        
        this.testProfile2.setDefaultProfileIncludeExcludeOrder(ProfileSchema.getProfileIncludeThenExcludeUri());
        
        Assert.assertEquals(ProfileSchema.getProfileIncludeThenExcludeUri(),
                this.testProfile2.getDefaultProfileIncludeExcludeOrder());
    }
    
    /**
     * Test method for {@link org.queryall.api.profile.Profile#setOrder(int)}.
     */
    @Test
    public final void testSetOrder()
    {
        this.testProfile1.setOrder(100);
        this.testProfile2.setOrder(200);
        
        Assert.assertEquals(100, this.testProfile1.getOrder());
        Assert.assertEquals(200, this.testProfile2.getOrder());
    }
}
