/**
 * 
 */
package org.queryall.utils.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.NamespaceEntry;
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.api.QueryType;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.impl.ProfileImpl;
import org.queryall.impl.QueryTypeImpl;
import org.queryall.utils.ProviderUtils;
import org.queryall.utils.RdfUtils;

/**
 * @author karina
 *
 */
public class ProviderUtilsTest
{
    
    private Repository testRepository;
    private RepositoryConnection testRepositoryConnection;
    private ValueFactoryImpl testValueFactory;

    private QueryType testDefaultProvidersQueryTypeTrue1;
    private QueryType testDefaultProvidersQueryTypeFalse1;
    private List<Profile> emptyProfileList;
    private List<Profile> singleImplicitAllowAllProfileList;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.testRepository = new SailRepository(new MemoryStore());
        this.testRepository.initialize();
        this.testRepositoryConnection = this.testRepository.getConnection();
        this.testValueFactory = new ValueFactoryImpl();
        
        this.testDefaultProvidersQueryTypeTrue1 = new QueryTypeImpl();
        this.testDefaultProvidersQueryTypeTrue1.setKey("http://example.org/query:defaultprovidersquerytypetest-true");
        this.testDefaultProvidersQueryTypeTrue1.setIncludeDefaults(true);

        this.testDefaultProvidersQueryTypeFalse1 = new QueryTypeImpl();
        this.testDefaultProvidersQueryTypeFalse1.setKey("http://example.org/query:defaultprovidersquerytypetest-false");
        this.testDefaultProvidersQueryTypeFalse1.setIncludeDefaults(false);
        
        emptyProfileList = Collections.emptyList();
        
        singleImplicitAllowAllProfileList = new LinkedList<Profile>();
        Profile testImplicitAllowAllProfile = new ProfileImpl();
        
        testImplicitAllowAllProfile.setAllowImplicitProviderInclusions(true);
        testImplicitAllowAllProfile.setAllowImplicitQueryTypeInclusions(true);
        testImplicitAllowAllProfile.setAllowImplicitRdfRuleInclusions(true);
        testImplicitAllowAllProfile.setDefaultProfileIncludeExcludeOrder(ProfileImpl.getProfileExcludeThenIncludeUri());
        
        singleImplicitAllowAllProfileList.add(testImplicitAllowAllProfile);
        
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        if(this.testRepositoryConnection != null)
        {
            try
            {
                this.testRepositoryConnection.close();
            }
            catch(final RepositoryException ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                this.testRepositoryConnection = null;
            }
        }
        
        this.testRepository = null;
        this.testValueFactory = null;
        
        this.testDefaultProvidersQueryTypeTrue1 = null;
        this.testDefaultProvidersQueryTypeFalse1 = null;
        
        this.emptyProfileList = null;
        this.singleImplicitAllowAllProfileList = null;
    }
    
    /**
     * Test method for {@link org.queryall.utils.ProviderUtils#getDefaultProviders(java.util.Map, org.queryall.api.QueryType, java.util.List, boolean, boolean)}.
     */
    @Test
    public void testGetDefaultProviders()
    {
        final InputStream nextInputStream = this.getClass().getResourceAsStream("/testconfigs/defaultProviderTest-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            
            Map<URI, Provider> testDefaultProviders = RdfUtils.getProviders(testRepository);
            
            Assert.assertEquals(2, testDefaultProviders.size());
            
            
            Collection<Provider> noProfileTrueResults = ProviderUtils.getDefaultProviders(testDefaultProviders, testDefaultProvidersQueryTypeTrue1, emptyProfileList, true, true);
            
            Assert.assertEquals(1, noProfileTrueResults.size());
            
            noProfileTrueResults = ProviderUtils.getDefaultProviders(testDefaultProviders, testDefaultProvidersQueryTypeTrue1, emptyProfileList, true, false);
            
            Assert.assertEquals(0, noProfileTrueResults.size());
            
            noProfileTrueResults = ProviderUtils.getDefaultProviders(testDefaultProviders, testDefaultProvidersQueryTypeTrue1, emptyProfileList, false, true);
            
            Assert.assertEquals(1, noProfileTrueResults.size());
            
            noProfileTrueResults = ProviderUtils.getDefaultProviders(testDefaultProviders, testDefaultProvidersQueryTypeTrue1, emptyProfileList, false, false);
            
            Assert.assertEquals(0, noProfileTrueResults.size());
            
            
            Collection<Provider> noProfileFalseResults = ProviderUtils.getDefaultProviders(testDefaultProviders, testDefaultProvidersQueryTypeFalse1, emptyProfileList, true, true);
            
            Assert.assertEquals(0, noProfileFalseResults.size());
            
            noProfileFalseResults = ProviderUtils.getDefaultProviders(testDefaultProviders, testDefaultProvidersQueryTypeFalse1, emptyProfileList, true, false);
            
            Assert.assertEquals(0, noProfileFalseResults.size());            
            
            noProfileFalseResults = ProviderUtils.getDefaultProviders(testDefaultProviders, testDefaultProvidersQueryTypeFalse1, emptyProfileList, false, true);
            
            Assert.assertEquals(0, noProfileFalseResults.size());            
            
            noProfileFalseResults = ProviderUtils.getDefaultProviders(testDefaultProviders, testDefaultProvidersQueryTypeFalse1, emptyProfileList, false, false);
            
            Assert.assertEquals(0, noProfileFalseResults.size());            
            
            
            Collection<Provider> implicitProfileTrueResults = ProviderUtils.getDefaultProviders(testDefaultProviders, testDefaultProvidersQueryTypeTrue1, this.singleImplicitAllowAllProfileList, true, true);
            
            Assert.assertEquals(1, implicitProfileTrueResults.size());
            
            implicitProfileTrueResults = ProviderUtils.getDefaultProviders(testDefaultProviders, testDefaultProvidersQueryTypeTrue1, singleImplicitAllowAllProfileList, true, false);
            
            Assert.assertEquals(1, implicitProfileTrueResults.size());
            
            implicitProfileTrueResults = ProviderUtils.getDefaultProviders(testDefaultProviders, testDefaultProvidersQueryTypeTrue1, singleImplicitAllowAllProfileList, false, true);
            
            Assert.assertEquals(1, implicitProfileTrueResults.size());
            
            implicitProfileTrueResults = ProviderUtils.getDefaultProviders(testDefaultProviders, testDefaultProvidersQueryTypeTrue1, singleImplicitAllowAllProfileList, false, false);
            
            Assert.assertEquals(0, implicitProfileTrueResults.size());
            
            
            Collection<Provider> implicitProfileFalseResults = ProviderUtils.getDefaultProviders(testDefaultProviders, testDefaultProvidersQueryTypeFalse1, singleImplicitAllowAllProfileList, true, true);
            
            Assert.assertEquals(0, implicitProfileFalseResults.size());
            
            implicitProfileFalseResults = ProviderUtils.getDefaultProviders(testDefaultProviders, testDefaultProvidersQueryTypeFalse1, singleImplicitAllowAllProfileList, true, false);
            
            Assert.assertEquals(0, implicitProfileFalseResults.size());            
            
            implicitProfileFalseResults = ProviderUtils.getDefaultProviders(testDefaultProviders, testDefaultProvidersQueryTypeFalse1, singleImplicitAllowAllProfileList, false, true);
            
            Assert.assertEquals(0, implicitProfileFalseResults.size());            
            
            implicitProfileFalseResults = ProviderUtils.getDefaultProviders(testDefaultProviders, testDefaultProvidersQueryTypeFalse1, singleImplicitAllowAllProfileList, false, false);
            
            Assert.assertEquals(0, implicitProfileFalseResults.size());            
            
            
            
        }
        catch(final RDFParseException ex)
        {
            Assert.fail("Found unexpected RDFParseException : " + ex.getMessage());
        }
        catch(final RepositoryException ex)
        {
            Assert.fail("Found unexpected RepositoryException : " + ex.getMessage());
        }
        catch(final IOException ex)
        {
            Assert.fail("Found unexpected IOException : " + ex.getMessage());
        }
    }
    
    /**
     * Test method for {@link org.queryall.utils.ProviderUtils#getProvidersForQueryType(java.util.Map, org.openrdf.model.URI)}.
     */
    @Test
    public void testGetProvidersForQueryType()
    {
        final InputStream nextInputStream = this.getClass().getResourceAsStream("/testconfigs/provider-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            
            Map<URI, Provider> testProviders = RdfUtils.getProviders(testRepository);
            
            Assert.assertEquals(1, testProviders.size());
            
            
            Map<URI, Provider> trueResults = ProviderUtils.getProvidersForQueryType(testProviders, this.testValueFactory.createURI("http://example.org/query:test-1"));
            
            Assert.assertEquals(1, trueResults.size());
            
            
            Map<URI, Provider> falseResults = ProviderUtils.getProvidersForQueryType(testProviders, this.testValueFactory.createURI("http://example.org/query:test-2"));
            
            Assert.assertEquals(0, falseResults.size());
            
        }
        catch(final RDFParseException ex)
        {
            Assert.fail("Found unexpected RDFParseException : " + ex.getMessage());
        }
        catch(final RepositoryException ex)
        {
            Assert.fail("Found unexpected RepositoryException : " + ex.getMessage());
        }
        catch(final IOException ex)
        {
            Assert.fail("Found unexpected IOException : " + ex.getMessage());
        }
    }
    
    /**
     * Test method for {@link org.queryall.utils.ProviderUtils#getProvidersForNamespaceUris(java.util.Map, java.util.Collection, org.openrdf.model.URI)}.
     */
    @Test
    @Ignore
    public void testGetProvidersForNamespaceUris()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.queryall.utils.ProviderUtils#getProvidersForQueryNamespaceSpecific(java.util.Map, java.util.List, org.queryall.api.QueryType, java.util.Map, java.lang.String, boolean, boolean)}.
     */
    @Test
    @Ignore
    public void testGetProvidersForQueryNamespaceSpecific()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.queryall.utils.ProviderUtils#getProvidersForQueryNonNamespaceSpecific(java.util.Map, org.queryall.api.QueryType, java.util.List, boolean, boolean)}.
     */
    @Test
    @Ignore
    public void testGetProvidersForQueryNonNamespaceSpecific()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.queryall.utils.ProviderUtils#getProvidersForQueryTypeForNamespaceUris(java.util.Map, org.openrdf.model.URI, java.util.Collection, org.openrdf.model.URI)}.
     */
    @Test
    @Ignore
    public void testGetProvidersForQueryTypeForNamespaceUris()
    {
        fail("Not yet implemented");
    }
    
}
