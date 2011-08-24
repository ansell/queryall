/**
 * 
 */
package org.queryall.utils.test;

import java.io.IOException;
import java.io.InputStream;
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
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.api.QueryType;
import org.queryall.impl.ProfileImpl;
import org.queryall.impl.querytype.QueryTypeImpl;
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
    
    private List<Profile> emptyProfileList;
    private List<Profile> singleImplicitAllowAllProfileList;
    private Collection<Collection<URI>> testNamespaces1;
    private Collection<Collection<URI>> testNamespaces12;
    private Collection<Collection<URI>> testNamespaces123;
    private Collection<Collection<URI>> testNamespaces23;
    private URI testQueryUri1;
    private URI testNamespaceUri1;
    private URI testNamespaceUri2;
    private URI testNamespaceUri3;
    private URI testNamespaceUri4;
    private URI testDefaultProvidersQueryTypeTrueUri;
    private URI testDefaultProvidersQueryTypeFalseUri;
    private URI testQueryUri2;
    
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
        
        this.testDefaultProvidersQueryTypeTrueUri =
                this.testValueFactory.createURI("http://example.org/query:defaultprovidersquerytypetest-true");
        this.testDefaultProvidersQueryTypeFalseUri =
                this.testValueFactory.createURI("http://example.org/query:defaultprovidersquerytypetest-false");
        
        this.emptyProfileList = Collections.emptyList();
        
        this.singleImplicitAllowAllProfileList = new LinkedList<Profile>();
        final Profile testImplicitAllowAllProfile = new ProfileImpl();
        
        testImplicitAllowAllProfile.setAllowImplicitProviderInclusions(true);
        testImplicitAllowAllProfile.setAllowImplicitQueryTypeInclusions(true);
        testImplicitAllowAllProfile.setAllowImplicitRdfRuleInclusions(true);
        testImplicitAllowAllProfile.setDefaultProfileIncludeExcludeOrder(ProfileImpl.getProfileExcludeThenIncludeUri());
        
        this.singleImplicitAllowAllProfileList.add(testImplicitAllowAllProfile);
        
        this.testQueryUri1 = this.testValueFactory.createURI("http://example.org/query:test-1");
        this.testQueryUri2 = this.testValueFactory.createURI("http://example.org/query:test-2");
        
        this.testNamespaceUri1 = this.testValueFactory.createURI("http://example.org/ns:test-1");
        this.testNamespaceUri2 = this.testValueFactory.createURI("http://example.org/ns:test-2");
        this.testNamespaceUri3 = this.testValueFactory.createURI("http://example.org/ns:test-3");
        this.testNamespaceUri4 = this.testValueFactory.createURI("http://example.org/ns:test-4");
        
        final Collection<URI> tempTestNamespace1 = new LinkedList<URI>();
        tempTestNamespace1.add(this.testNamespaceUri1);
        
        final Collection<URI> tempTestNamespace2 = new LinkedList<URI>();
        tempTestNamespace2.add(this.testNamespaceUri2);
        
        final Collection<URI> tempTestNamespace3 = new LinkedList<URI>();
        tempTestNamespace3.add(this.testNamespaceUri3);
        
        final Collection<URI> tempTestNamespace4 = new LinkedList<URI>();
        tempTestNamespace4.add(this.testNamespaceUri4);
        
        this.testNamespaces1 = new LinkedList<Collection<URI>>();
        this.testNamespaces12 = new LinkedList<Collection<URI>>();
        this.testNamespaces123 = new LinkedList<Collection<URI>>();
        this.testNamespaces23 = new LinkedList<Collection<URI>>();
        
        this.testNamespaces1.add(tempTestNamespace1);
        
        this.testNamespaces12.add(tempTestNamespace1);
        this.testNamespaces12.add(tempTestNamespace2);
        
        this.testNamespaces123.add(tempTestNamespace1);
        this.testNamespaces123.add(tempTestNamespace2);
        this.testNamespaces123.add(tempTestNamespace3);
        
        this.testNamespaces23.add(tempTestNamespace2);
        this.testNamespaces23.add(tempTestNamespace3);
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
        
        this.testDefaultProvidersQueryTypeTrueUri = null;
        this.testDefaultProvidersQueryTypeFalseUri = null;
        
        this.emptyProfileList = null;
        this.singleImplicitAllowAllProfileList = null;
        
        this.testNamespaces1 = null;
        this.testNamespaces12 = null;
        this.testNamespaces123 = null;
        this.testNamespaces23 = null;
        
        this.testQueryUri1 = null;
        this.testQueryUri2 = null;
        
        this.testNamespaceUri1 = null;
        this.testNamespaceUri2 = null;
        this.testNamespaceUri3 = null;
        this.testNamespaceUri4 = null;
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.ProviderUtils#getDefaultProviders(java.util.Map, org.queryall.api.QueryType, java.util.List, boolean, boolean)}
     * .
     */
    @Test
    public void testGetDefaultProviders()
    {
        final InputStream nextInputStream =
                this.getClass().getResourceAsStream("/testconfigs/defaultProviderTest-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            final Map<URI, Provider> testDefaultProviders = RdfUtils.getProviders(this.testRepository);
            final Map<URI, QueryType> testDefaultQueryTypes = RdfUtils.getQueryTypes(this.testRepository);
            
            Assert.assertEquals(2, testDefaultProviders.keySet().size());
            Assert.assertEquals(2, testDefaultQueryTypes.keySet().size());
            
            for(final URI nextQueryTypeURI : testDefaultQueryTypes.keySet())
            {
                final QueryType nextQueryType = testDefaultQueryTypes.get(nextQueryTypeURI);
                
                if(nextQueryTypeURI.equals(this.testDefaultProvidersQueryTypeTrueUri))
                {
                    Collection<Provider> noProfileTrueResults =
                            ProviderUtils.getDefaultProviders(testDefaultProviders, nextQueryType,
                                    this.emptyProfileList, true, true);
                    
                    Assert.assertEquals(1, noProfileTrueResults.size());
                    
                    noProfileTrueResults =
                            ProviderUtils.getDefaultProviders(testDefaultProviders, nextQueryType,
                                    this.emptyProfileList, true, false);
                    
                    Assert.assertEquals(0, noProfileTrueResults.size());
                    
                    noProfileTrueResults =
                            ProviderUtils.getDefaultProviders(testDefaultProviders, nextQueryType,
                                    this.emptyProfileList, false, true);
                    
                    Assert.assertEquals(1, noProfileTrueResults.size());
                    
                    noProfileTrueResults =
                            ProviderUtils.getDefaultProviders(testDefaultProviders, nextQueryType,
                                    this.emptyProfileList, false, false);
                    
                    Assert.assertEquals(0, noProfileTrueResults.size());
                    
                    Collection<Provider> implicitProfileTrueResults =
                            ProviderUtils.getDefaultProviders(testDefaultProviders, nextQueryType,
                                    this.singleImplicitAllowAllProfileList, true, true);
                    
                    Assert.assertEquals(1, implicitProfileTrueResults.size());
                    
                    implicitProfileTrueResults =
                            ProviderUtils.getDefaultProviders(testDefaultProviders, nextQueryType,
                                    this.singleImplicitAllowAllProfileList, true, false);
                    
                    Assert.assertEquals(1, implicitProfileTrueResults.size());
                    
                    implicitProfileTrueResults =
                            ProviderUtils.getDefaultProviders(testDefaultProviders, nextQueryType,
                                    this.singleImplicitAllowAllProfileList, false, true);
                    
                    Assert.assertEquals(1, implicitProfileTrueResults.size());
                    
                    implicitProfileTrueResults =
                            ProviderUtils.getDefaultProviders(testDefaultProviders, nextQueryType,
                                    this.singleImplicitAllowAllProfileList, false, false);
                    
                    Assert.assertEquals(0, implicitProfileTrueResults.size());
                    
                }
                else if(nextQueryTypeURI.equals(this.testDefaultProvidersQueryTypeFalseUri))
                {
                    Collection<Provider> noProfileFalseResults =
                            ProviderUtils.getDefaultProviders(testDefaultProviders, nextQueryType,
                                    this.emptyProfileList, true, true);
                    
                    Assert.assertEquals(0, noProfileFalseResults.size());
                    
                    noProfileFalseResults =
                            ProviderUtils.getDefaultProviders(testDefaultProviders, nextQueryType,
                                    this.emptyProfileList, true, false);
                    
                    Assert.assertEquals(0, noProfileFalseResults.size());
                    
                    noProfileFalseResults =
                            ProviderUtils.getDefaultProviders(testDefaultProviders, nextQueryType,
                                    this.emptyProfileList, false, true);
                    
                    Assert.assertEquals(0, noProfileFalseResults.size());
                    
                    noProfileFalseResults =
                            ProviderUtils.getDefaultProviders(testDefaultProviders, nextQueryType,
                                    this.emptyProfileList, false, false);
                    
                    Assert.assertEquals(0, noProfileFalseResults.size());
                    
                    Collection<Provider> implicitProfileFalseResults =
                            ProviderUtils.getDefaultProviders(testDefaultProviders, nextQueryType,
                                    this.singleImplicitAllowAllProfileList, true, true);
                    
                    Assert.assertEquals(0, implicitProfileFalseResults.size());
                    
                    implicitProfileFalseResults =
                            ProviderUtils.getDefaultProviders(testDefaultProviders, nextQueryType,
                                    this.singleImplicitAllowAllProfileList, true, false);
                    
                    Assert.assertEquals(0, implicitProfileFalseResults.size());
                    
                    implicitProfileFalseResults =
                            ProviderUtils.getDefaultProviders(testDefaultProviders, nextQueryType,
                                    this.singleImplicitAllowAllProfileList, false, true);
                    
                    Assert.assertEquals(0, implicitProfileFalseResults.size());
                    
                    implicitProfileFalseResults =
                            ProviderUtils.getDefaultProviders(testDefaultProviders, nextQueryType,
                                    this.singleImplicitAllowAllProfileList, false, false);
                    
                    Assert.assertEquals(0, implicitProfileFalseResults.size());
                }
                else
                {
                    Assert.fail("Found unrecognised query type nextQueryTypeURI=" + nextQueryTypeURI.stringValue());
                }
            }
            
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
     * Test method for
     * {@link org.queryall.utils.ProviderUtils#getProvidersForNamespaceUris(java.util.Map, java.util.Collection, org.openrdf.model.URI)}
     * .
     */
    @Test
    public void testGetProvidersForNamespaceUris()
    {
        final InputStream nextInputStream =
                this.getClass().getResourceAsStream("/testconfigs/multipleNamespaceProviderTest-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            final Map<URI, Provider> testProviders = RdfUtils.getProviders(this.testRepository);
            
            Assert.assertEquals(4, testProviders.size());
            
            Map<URI, Provider> allNamespaceMatchProviders =
                    ProviderUtils.getProvidersForNamespaceUris(testProviders, this.testNamespaces1,
                            QueryTypeImpl.getNamespaceMatchAllUri());
            
            Assert.assertEquals(3, allNamespaceMatchProviders.size());
            
            allNamespaceMatchProviders =
                    ProviderUtils.getProvidersForNamespaceUris(testProviders, this.testNamespaces12,
                            QueryTypeImpl.getNamespaceMatchAllUri());
            
            Assert.assertEquals(2, allNamespaceMatchProviders.size());
            
            allNamespaceMatchProviders =
                    ProviderUtils.getProvidersForNamespaceUris(testProviders, this.testNamespaces123,
                            QueryTypeImpl.getNamespaceMatchAllUri());
            
            Assert.assertEquals(1, allNamespaceMatchProviders.size());
            
            allNamespaceMatchProviders =
                    ProviderUtils.getProvidersForNamespaceUris(testProviders, this.testNamespaces23,
                            QueryTypeImpl.getNamespaceMatchAllUri());
            
            Assert.assertEquals(2, allNamespaceMatchProviders.size());
            
            Map<URI, Provider> anyNamespaceMatchProviders =
                    ProviderUtils.getProvidersForNamespaceUris(testProviders, this.testNamespaces1,
                            QueryTypeImpl.getNamespaceMatchAnyUri());
            
            Assert.assertEquals(3, anyNamespaceMatchProviders.size());
            
            anyNamespaceMatchProviders =
                    ProviderUtils.getProvidersForNamespaceUris(testProviders, this.testNamespaces12,
                            QueryTypeImpl.getNamespaceMatchAnyUri());
            
            Assert.assertEquals(4, anyNamespaceMatchProviders.size());
            
            anyNamespaceMatchProviders =
                    ProviderUtils.getProvidersForNamespaceUris(testProviders, this.testNamespaces123,
                            QueryTypeImpl.getNamespaceMatchAnyUri());
            
            Assert.assertEquals(4, anyNamespaceMatchProviders.size());
            
            anyNamespaceMatchProviders =
                    ProviderUtils.getProvidersForNamespaceUris(testProviders, this.testNamespaces23,
                            QueryTypeImpl.getNamespaceMatchAnyUri());
            
            Assert.assertEquals(3, anyNamespaceMatchProviders.size());
            
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
     * Test method for
     * {@link org.queryall.utils.ProviderUtils#getProvidersForQueryNamespaceSpecific(java.util.Map, java.util.List, org.queryall.api.QueryType, java.util.Map, java.lang.String, boolean, boolean)}
     * .
     */
    @Test
    @Ignore
    public void testGetProvidersForQueryNamespaceSpecific()
    {
        Assert.fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.ProviderUtils#getProvidersForQueryNonNamespaceSpecific(java.util.Map, org.openrdf.model.URI, java.util.List, boolean, boolean)}
     * .
     */
    @Test
    public void testGetProvidersForQueryNonNamespaceSpecific()
    {
        final InputStream nextInputStream =
                this.getClass().getResourceAsStream("/testconfigs/multipleNamespaceProviderTest-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            final Map<URI, Provider> testNonNamespaceSpecificProviders = RdfUtils.getProviders(this.testRepository);
            
            Assert.assertEquals(4, testNonNamespaceSpecificProviders.keySet().size());
            
            Collection<Provider> noProfileTrueResults =
                    ProviderUtils.getProvidersForQueryNonNamespaceSpecific(testNonNamespaceSpecificProviders,
                            this.testQueryUri1, this.emptyProfileList, true, true);
            
            Assert.assertEquals(4, noProfileTrueResults.size());
            
            noProfileTrueResults =
                    ProviderUtils.getProvidersForQueryNonNamespaceSpecific(testNonNamespaceSpecificProviders,
                            this.testQueryUri1, this.emptyProfileList, true, false);
            
            Assert.assertEquals(0, noProfileTrueResults.size());
            
            noProfileTrueResults =
                    ProviderUtils.getProvidersForQueryNonNamespaceSpecific(testNonNamespaceSpecificProviders,
                            this.testQueryUri1, this.emptyProfileList, false, true);
            
            Assert.assertEquals(4, noProfileTrueResults.size());
            
            noProfileTrueResults =
                    ProviderUtils.getProvidersForQueryNonNamespaceSpecific(testNonNamespaceSpecificProviders,
                            this.testQueryUri1, this.emptyProfileList, false, false);
            
            Assert.assertEquals(0, noProfileTrueResults.size());
            
            Collection<Provider> implicitProfileTrueResults =
                    ProviderUtils.getProvidersForQueryNonNamespaceSpecific(testNonNamespaceSpecificProviders,
                            this.testQueryUri1, this.singleImplicitAllowAllProfileList, true, true);
            
            Assert.assertEquals(4, implicitProfileTrueResults.size());
            
            implicitProfileTrueResults =
                    ProviderUtils.getProvidersForQueryNonNamespaceSpecific(testNonNamespaceSpecificProviders,
                            this.testQueryUri1, this.singleImplicitAllowAllProfileList, true, false);
            
            Assert.assertEquals(4, implicitProfileTrueResults.size());
            
            implicitProfileTrueResults =
                    ProviderUtils.getProvidersForQueryNonNamespaceSpecific(testNonNamespaceSpecificProviders,
                            this.testQueryUri1, this.singleImplicitAllowAllProfileList, false, true);
            
            Assert.assertEquals(4, implicitProfileTrueResults.size());
            
            implicitProfileTrueResults =
                    ProviderUtils.getProvidersForQueryNonNamespaceSpecific(testNonNamespaceSpecificProviders,
                            this.testQueryUri1, this.singleImplicitAllowAllProfileList, false, false);
            
            Assert.assertEquals(0, implicitProfileTrueResults.size());
            
            Collection<Provider> noProfileFalseResults =
                    ProviderUtils.getProvidersForQueryNonNamespaceSpecific(testNonNamespaceSpecificProviders,
                            this.testQueryUri2, this.emptyProfileList, true, true);
            
            Assert.assertEquals(0, noProfileFalseResults.size());
            
            noProfileFalseResults =
                    ProviderUtils.getProvidersForQueryNonNamespaceSpecific(testNonNamespaceSpecificProviders,
                            this.testQueryUri2, this.emptyProfileList, true, false);
            
            Assert.assertEquals(0, noProfileFalseResults.size());
            
            noProfileFalseResults =
                    ProviderUtils.getProvidersForQueryNonNamespaceSpecific(testNonNamespaceSpecificProviders,
                            this.testQueryUri2, this.emptyProfileList, false, true);
            
            Assert.assertEquals(0, noProfileFalseResults.size());
            
            noProfileFalseResults =
                    ProviderUtils.getProvidersForQueryNonNamespaceSpecific(testNonNamespaceSpecificProviders,
                            this.testQueryUri2, this.emptyProfileList, false, false);
            
            Assert.assertEquals(0, noProfileFalseResults.size());
            
            Collection<Provider> implicitProfileFalseResults =
                    ProviderUtils.getProvidersForQueryNonNamespaceSpecific(testNonNamespaceSpecificProviders,
                            this.testQueryUri2, this.singleImplicitAllowAllProfileList, true, true);
            
            Assert.assertEquals(0, implicitProfileFalseResults.size());
            
            implicitProfileFalseResults =
                    ProviderUtils.getProvidersForQueryNonNamespaceSpecific(testNonNamespaceSpecificProviders,
                            this.testQueryUri2, this.singleImplicitAllowAllProfileList, true, false);
            
            Assert.assertEquals(0, implicitProfileFalseResults.size());
            
            implicitProfileFalseResults =
                    ProviderUtils.getProvidersForQueryNonNamespaceSpecific(testNonNamespaceSpecificProviders,
                            this.testQueryUri2, this.singleImplicitAllowAllProfileList, false, true);
            
            Assert.assertEquals(0, implicitProfileFalseResults.size());
            
            implicitProfileFalseResults =
                    ProviderUtils.getProvidersForQueryNonNamespaceSpecific(testNonNamespaceSpecificProviders,
                            this.testQueryUri2, this.singleImplicitAllowAllProfileList, false, false);
            
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
     * Test method for
     * {@link org.queryall.utils.ProviderUtils#getProvidersForQueryType(java.util.Map, org.openrdf.model.URI)}
     * .
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
            
            final Map<URI, Provider> testProviders = RdfUtils.getProviders(this.testRepository);
            
            Assert.assertEquals(1, testProviders.size());
            
            final Map<URI, Provider> trueResults =
                    ProviderUtils.getProvidersForQueryType(testProviders, this.testQueryUri1);
            
            Assert.assertEquals(1, trueResults.size());
            
            final Map<URI, Provider> falseResults =
                    ProviderUtils.getProvidersForQueryType(testProviders, this.testQueryUri2);
            
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
     * Test method for
     * {@link org.queryall.utils.ProviderUtils#getProvidersForQueryTypeForNamespaceUris(java.util.Map, org.openrdf.model.URI, java.util.Collection, org.openrdf.model.URI)}
     * .
     */
    @Test
    public void testGetProvidersForQueryTypeForNamespaceUris()
    {
        final InputStream nextInputStream =
                this.getClass().getResourceAsStream("/testconfigs/multipleNamespaceProviderTest-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            final Map<URI, Provider> testProviders = RdfUtils.getProviders(this.testRepository);
            
            Assert.assertEquals(4, testProviders.size());
            
            Map<URI, Provider> allNamespaceMatchProviders =
                    ProviderUtils.getProvidersForQueryTypeForNamespaceUris(testProviders, this.testQueryUri1,
                            this.testNamespaces1, QueryTypeImpl.getNamespaceMatchAllUri());
            
            Assert.assertEquals(3, allNamespaceMatchProviders.size());
            
            allNamespaceMatchProviders =
                    ProviderUtils.getProvidersForQueryTypeForNamespaceUris(testProviders, this.testQueryUri1,
                            this.testNamespaces12, QueryTypeImpl.getNamespaceMatchAllUri());
            
            Assert.assertEquals(2, allNamespaceMatchProviders.size());
            
            allNamespaceMatchProviders =
                    ProviderUtils.getProvidersForQueryTypeForNamespaceUris(testProviders, this.testQueryUri1,
                            this.testNamespaces123, QueryTypeImpl.getNamespaceMatchAllUri());
            
            Assert.assertEquals(1, allNamespaceMatchProviders.size());
            
            allNamespaceMatchProviders =
                    ProviderUtils.getProvidersForQueryTypeForNamespaceUris(testProviders, this.testQueryUri1,
                            this.testNamespaces23, QueryTypeImpl.getNamespaceMatchAllUri());
            
            Assert.assertEquals(2, allNamespaceMatchProviders.size());
            
            Map<URI, Provider> anyNamespaceMatchProviders =
                    ProviderUtils.getProvidersForQueryTypeForNamespaceUris(testProviders, this.testQueryUri1,
                            this.testNamespaces1, QueryTypeImpl.getNamespaceMatchAnyUri());
            
            Assert.assertEquals(3, anyNamespaceMatchProviders.size());
            
            anyNamespaceMatchProviders =
                    ProviderUtils.getProvidersForQueryTypeForNamespaceUris(testProviders, this.testQueryUri1,
                            this.testNamespaces12, QueryTypeImpl.getNamespaceMatchAnyUri());
            
            Assert.assertEquals(4, anyNamespaceMatchProviders.size());
            
            anyNamespaceMatchProviders =
                    ProviderUtils.getProvidersForQueryTypeForNamespaceUris(testProviders, this.testQueryUri1,
                            this.testNamespaces123, QueryTypeImpl.getNamespaceMatchAnyUri());
            
            Assert.assertEquals(4, anyNamespaceMatchProviders.size());
            
            anyNamespaceMatchProviders =
                    ProviderUtils.getProvidersForQueryTypeForNamespaceUris(testProviders, this.testQueryUri1,
                            this.testNamespaces23, QueryTypeImpl.getNamespaceMatchAnyUri());
            
            Assert.assertEquals(3, anyNamespaceMatchProviders.size());
            
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
    
}
