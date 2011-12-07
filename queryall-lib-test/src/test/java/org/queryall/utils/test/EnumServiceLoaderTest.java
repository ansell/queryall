/**
 * 
 */
package org.queryall.utils.test;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.queryall.api.namespace.NamespaceEntrySchema;
import org.queryall.api.profile.ProfileSchema;
import org.queryall.api.project.ProjectSchema;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.querytype.QueryTypeSchema;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.ruletest.RuleTestSchema;
import org.queryall.api.services.EnumServiceLoader;
import org.queryall.api.services.QueryAllEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class EnumServiceLoaderTest
{
    private static final Logger log = LoggerFactory.getLogger(SchemaServiceLoaderTest.class);
    
    /**
     * This field contains the expected number of enums, if and when new enums are added it needs to
     * be updated to match the expected number
     */
    private static final int CURRENT_EXPECTED_ENUM_COUNT = 20;
    
    private Set<URI> getBaseTypes(final Set<URI> nextTypeURIs)
    {
        final Set<URI> baseTypesFound = new HashSet<URI>();
        
        if(nextTypeURIs.contains(ProviderSchema.getProviderTypeUri()))
        {
            baseTypesFound.add(ProviderSchema.getProviderTypeUri());
        }
        
        if(nextTypeURIs.contains(QueryTypeSchema.getQueryTypeUri()))
        {
            baseTypesFound.add(QueryTypeSchema.getQueryTypeUri());
        }
        
        if(nextTypeURIs.contains(RuleTestSchema.getRuletestTypeUri()))
        {
            baseTypesFound.add(RuleTestSchema.getRuletestTypeUri());
        }
        
        if(nextTypeURIs.contains(NormalisationRuleSchema.getNormalisationRuleTypeUri()))
        {
            baseTypesFound.add(NormalisationRuleSchema.getNormalisationRuleTypeUri());
        }
        
        if(nextTypeURIs.contains(ProjectSchema.getProjectTypeUri()))
        {
            baseTypesFound.add(ProjectSchema.getProjectTypeUri());
        }
        
        if(nextTypeURIs.contains(ProfileSchema.getProfileTypeUri()))
        {
            baseTypesFound.add(ProfileSchema.getProfileTypeUri());
        }
        
        if(nextTypeURIs.contains(NamespaceEntrySchema.getNamespaceTypeUri()))
        {
            baseTypesFound.add(NamespaceEntrySchema.getNamespaceTypeUri());
        }
        
        return baseTypesFound;
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }
    
    /**
     * Test method for {@link org.queryall.api.services.EnumServiceLoader#getAll()}.
     */
    @Test
    public void testGetAll()
    {
        Assert.assertEquals(
                "Did not find the expected number of enums. Were there new implementations added recently?",
                EnumServiceLoaderTest.CURRENT_EXPECTED_ENUM_COUNT, EnumServiceLoader.getInstance().getAll().size());
        
        for(final QueryAllEnum nextEnum : EnumServiceLoader.getInstance().getAll())
        {
            EnumServiceLoaderTest.log.info("nextEnum.getName()=" + nextEnum.getName());
        }
    }
    
    @Test
    public void testGetAllTypeUris()
    {
        Assert.assertEquals(EnumServiceLoaderTest.CURRENT_EXPECTED_ENUM_COUNT, EnumServiceLoader.getInstance().getAll()
                .size());
        
        for(final QueryAllEnum nextEnum : EnumServiceLoader.getInstance().getAll())
        {
            Assert.assertNotNull("Enum type URIs set was null", nextEnum.getTypeURIs());
            
            Assert.assertTrue("Enum did not have any type URIs registered", nextEnum.getTypeURIs().size() > 0);
            
            final Set<URI> nextBaseTypes = this.getBaseTypes(nextEnum.getTypeURIs());
            
            Assert.assertTrue(
                    "Could not find any base types registered for this enum nextEnum.getName()=" + nextEnum.getName(),
                    nextBaseTypes.size() > 0);
            
            Assert.assertEquals("Did not find a unique base type registered for this enum nextEnum.getName()="
                    + nextEnum.getName(), 1, nextBaseTypes.size());
        }
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.services.EnumServiceLoader#getKey(org.queryall.api.services.QueryAllEnum)}
     * .
     */
    @Test
    public void testGetKeyQueryAllEnum()
    {
        Assert.assertEquals(EnumServiceLoaderTest.CURRENT_EXPECTED_ENUM_COUNT, EnumServiceLoader.getInstance().getAll()
                .size());
        
        final Set<String> allEnumNames = new HashSet<String>();
        
        for(final QueryAllEnum nextEnum : EnumServiceLoader.getInstance().getAll())
        {
            // test for null schema names
            Assert.assertNotNull("Enum name should not be null", nextEnum.getName());
            
            // test that the schema name doesn't just contain whitespace
            Assert.assertTrue(nextEnum.getName().trim().length() > 0);
            
            Assert.assertFalse(allEnumNames.contains(nextEnum.getName()));
            
            allEnumNames.add(nextEnum.getName());
            
            // verify that the enum fetched using .get(String) is the same as the one we are looking
            // at
            Assert.assertEquals(nextEnum, EnumServiceLoader.getInstance().get(nextEnum.getName()));
        }
        
        // Verify that unique, non-null strings were found for each enum
        // According to the Set contract, this should never fail, as the assertion in the loop,
        // assertFalse(allEnumNames.contains()), should fail first, but checking just to make it
        // obvious
        Assert.assertEquals(EnumServiceLoaderTest.CURRENT_EXPECTED_ENUM_COUNT, allEnumNames.size());
    }
    
}
