/**
 * 
 */
package org.queryall.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.querytype.QueryTypeSchema;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.impl.profile.ProfileImpl;
import org.queryall.utils.RdfUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfUtilsTest
{
    private Repository testRepository;
    private RepositoryConnection testRepositoryConnection;
    private ValueFactory testValueFactory;
    
    private String applicationRdfXml;
    private String textRdfN3;
    private String textPlain;
    private String bogusContentType1;
    private String bogusContentType2;
    
    private String trueString;
    private String falseString;
    private String booleanDataType;
    private URI booleanDataTypeUri;
    private Value trueBooleanTypedLiteral;
    private Value falseBooleanTypedLiteral;
    private Value trueBooleanNativeLiteral;
    private Value falseBooleanNativeLiteral;
    private Value trueBooleanStringLiteral;
    private Value falseBooleanStringLiteral;
    
    private String dateTimeDataType;
    private Date testDate;
    private String testDateString;
    private Value testDateTypedLiteral;
    private Value testDateNativeLiteral;
    private Value testDateStringLiteral;
    
    private Literal testFloatTypedLiteral;
    private Literal testFloatNativeLiteral;
    private Literal testFloatStringLiteral;
    private float testFloat;
    private String floatDataType;
    
    private Value testIntTypedLiteral;
    private Value testIntNativeLiteral;
    private Value testIntStringLiteral;
    private int testInt;
    private String intDataType;
    
    private Value testLongTypedLiteral;
    private Value testLongNativeLiteral;
    private Value testLongStringLiteral;
    private long testLong;
    private String longDataType;
    
    private URI testProfileUri1;
    private URI testProfileUri2;
    
    private URI testProviderUri1;
    private URI testRuleTestUri1;
    private URI testQueryTypeUri1;
    
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
        
        this.applicationRdfXml = "application/rdf+xml";
        this.textRdfN3 = "text/rdf+n3";
        this.textPlain = "text/plain";
        this.bogusContentType1 = "bogus1";
        this.bogusContentType2 = "bogus2";
        
        this.trueString = "true";
        this.falseString = "false";
        this.booleanDataType = "http://www.w3.org/2001/XMLSchema#boolean";
        this.booleanDataTypeUri = this.testValueFactory.createURI(this.booleanDataType);
        this.trueBooleanTypedLiteral = this.testValueFactory.createLiteral(this.trueString, this.booleanDataTypeUri);
        this.falseBooleanTypedLiteral = this.testValueFactory.createLiteral(this.falseString, this.booleanDataTypeUri);
        this.trueBooleanNativeLiteral = this.testValueFactory.createLiteral(true);
        this.falseBooleanNativeLiteral = this.testValueFactory.createLiteral(false);
        this.trueBooleanStringLiteral = this.testValueFactory.createLiteral(this.trueString);
        this.falseBooleanStringLiteral = this.testValueFactory.createLiteral(this.falseString);
        
        this.dateTimeDataType = "http://www.w3.org/2001/XMLSchema#dateTime";
        final Calendar testDateCalendar = Constants.ISO8601UTC().getCalendar();
        testDateCalendar.set(2010, 01, 02, 03, 04, 05);
        this.testDate = testDateCalendar.getTime();
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(this.testDate.getTime());
        DatatypeFactory df;
        try
        {
            df = DatatypeFactory.newInstance();
        }
        catch(final DatatypeConfigurationException dce)
        {
            throw new IllegalStateException("Exception while obtaining DatatypeFactory instance", dce);
        }
        final XMLGregorianCalendar xmlDate = df.newXMLGregorianCalendar(gc);
        this.testDateString = Constants.ISO8601UTC().format(this.testDate);
        this.testDateTypedLiteral = this.testValueFactory.createLiteral(this.testDateString, this.dateTimeDataType);
        this.testDateNativeLiteral = this.testValueFactory.createLiteral(xmlDate);
        this.testDateStringLiteral = this.testValueFactory.createLiteral(this.testDateString);
        
        this.floatDataType = "http://www.w3.org/2001/XMLSchema#float";
        this.testFloat = 0.278134f;
        this.testFloatTypedLiteral =
                this.testValueFactory.createLiteral(Float.toString(this.testFloat), this.floatDataType);
        this.testFloatNativeLiteral = this.testValueFactory.createLiteral(this.testFloat);
        this.testFloatStringLiteral = this.testValueFactory.createLiteral(Float.toString(this.testFloat));
        
        this.intDataType = "http://www.w3.org/2001/XMLSchema#int";
        this.testInt = 278134;
        this.testIntTypedLiteral =
                this.testValueFactory.createLiteral(Integer.toString(this.testInt), this.intDataType);
        this.testIntNativeLiteral = this.testValueFactory.createLiteral(this.testInt);
        this.testIntStringLiteral = this.testValueFactory.createLiteral(Integer.toString(this.testInt));
        
        this.longDataType = "http://www.w3.org/2001/XMLSchema#long";
        this.testLong = 278134965145L;
        this.testLongTypedLiteral =
                this.testValueFactory.createLiteral(Long.toString(this.testLong), this.longDataType);
        this.testLongNativeLiteral = this.testValueFactory.createLiteral(this.testLong);
        this.testLongStringLiteral = this.testValueFactory.createLiteral(Long.toString(this.testLong));
        
        this.testProfileUri1 = this.testValueFactory.createURI("http://example.org/profile:test-1");
        this.testProfileUri2 = this.testValueFactory.createURI("http://example.org/profile:test-2");
        
        this.testProviderUri1 = this.testValueFactory.createURI("http://example.org/provider:test-1");
        
        this.testRuleTestUri1 = this.testValueFactory.createURI("http://example.org/ruletest:test-1");
        
        this.testQueryTypeUri1 = this.testValueFactory.createURI("http://example.org/query:test-1");
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
        
        this.applicationRdfXml = null;
        this.textRdfN3 = null;
        this.textPlain = null;
        this.bogusContentType1 = null;
        this.bogusContentType2 = null;
        
        this.trueString = null;
        this.falseString = null;
        this.booleanDataType = null;
        this.booleanDataTypeUri = null;
        
        this.trueBooleanTypedLiteral = null;
        this.falseBooleanTypedLiteral = null;
        
        this.trueBooleanNativeLiteral = null;
        this.falseBooleanNativeLiteral = null;
        
        this.trueBooleanStringLiteral = null;
        this.falseBooleanStringLiteral = null;
        
        this.testProfileUri1 = null;
        this.testProfileUri2 = null;
        
        this.testProviderUri1 = null;
        
        this.testRuleTestUri1 = null;
        
        this.testQueryTypeUri1 = null;
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#findBestContentType(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testFindBestContentTypeAgainst1BogusType()
    {
        Assert.assertEquals(this.applicationRdfXml,
                RdfUtils.findBestContentType(this.applicationRdfXml, this.bogusContentType1, this.textRdfN3));
        Assert.assertEquals(this.applicationRdfXml,
                RdfUtils.findBestContentType(this.bogusContentType1, this.applicationRdfXml, this.textRdfN3));
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.bogusContentType1, this.textRdfN3, this.applicationRdfXml));
        
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.textRdfN3, this.bogusContentType1, this.applicationRdfXml));
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.bogusContentType1, this.textRdfN3, this.applicationRdfXml));
        Assert.assertEquals(this.applicationRdfXml,
                RdfUtils.findBestContentType(this.bogusContentType1, this.applicationRdfXml, this.textRdfN3));
        
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.textPlain, this.bogusContentType1, this.applicationRdfXml));
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.bogusContentType1, this.textPlain, this.applicationRdfXml));
        Assert.assertEquals(this.applicationRdfXml,
                RdfUtils.findBestContentType(this.bogusContentType1, this.applicationRdfXml, this.textPlain));
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#findBestContentType(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testFindBestContentTypeAgainst2BogusTypes()
    {
        Assert.assertEquals(this.applicationRdfXml,
                RdfUtils.findBestContentType(this.applicationRdfXml, this.bogusContentType1, this.bogusContentType2));
        Assert.assertEquals(this.applicationRdfXml,
                RdfUtils.findBestContentType(this.bogusContentType1, this.applicationRdfXml, this.bogusContentType2));
        Assert.assertEquals(this.applicationRdfXml,
                RdfUtils.findBestContentType(this.bogusContentType1, this.bogusContentType2, this.applicationRdfXml));
        
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.textRdfN3, this.bogusContentType1, this.bogusContentType2));
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.bogusContentType1, this.textRdfN3, this.bogusContentType2));
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.bogusContentType1, this.bogusContentType2, this.textRdfN3));
        
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.textPlain, this.bogusContentType1, this.bogusContentType2));
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.bogusContentType1, this.textPlain, this.bogusContentType2));
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.bogusContentType1, this.bogusContentType2, this.textPlain));
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#findBestContentType(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testFindBestContentTypeWith3RealTypes()
    {
        Assert.assertEquals(this.applicationRdfXml,
                RdfUtils.findBestContentType(this.applicationRdfXml, this.textPlain, this.textRdfN3));
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.textPlain, this.applicationRdfXml, this.textRdfN3));
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.textPlain, this.textRdfN3, this.applicationRdfXml));
        
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.textRdfN3, this.textPlain, this.applicationRdfXml));
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.textPlain, this.textRdfN3, this.applicationRdfXml));
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.textPlain, this.applicationRdfXml, this.textRdfN3));
        
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.textPlain, this.textRdfN3, this.applicationRdfXml));
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.textRdfN3, this.textPlain, this.applicationRdfXml));
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.textRdfN3, this.applicationRdfXml, this.textPlain));
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getDateTimeFromValue(org.openrdf.model.Value)}.
     * 
     * 
     * TODO: make this work
     */
    @Test
    @Ignore
    public void testGetDateTimeFromValue()
    {
        try
        {
            Assert.assertEquals(this.testDate.getTime(), RdfUtils.getDateTimeFromValue(this.testDateTypedLiteral)
                    .getTime());
            Assert.assertEquals(this.testDate.getTime(), RdfUtils.getDateTimeFromValue(this.testDateNativeLiteral)
                    .getTime());
            Assert.assertEquals(this.testDate.getTime(), RdfUtils.getDateTimeFromValue(this.testDateStringLiteral)
                    .getTime());
        }
        catch(final ParseException e)
        {
            Assert.fail("Found unexpected ParseException e=" + e.getMessage());
        }
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getFloatFromValue(org.openrdf.model.Value)}.
     */
    @Test
    public void testGetFloatFromValue()
    {
        Assert.assertEquals(this.testFloat, RdfUtils.getFloatFromValue(this.testFloatTypedLiteral), 0.0000001);
        Assert.assertEquals(this.testFloat, RdfUtils.getFloatFromValue(this.testFloatNativeLiteral), 0.0000001);
        Assert.assertEquals(this.testFloat, RdfUtils.getFloatFromValue(this.testFloatStringLiteral), 0.0000001);
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getIntegerFromValue(org.openrdf.model.Value)}.
     */
    @Test
    public void testGetIntegerFromValue()
    {
        Assert.assertEquals(this.testInt, RdfUtils.getIntegerFromValue(this.testIntTypedLiteral));
        Assert.assertEquals(this.testInt, RdfUtils.getIntegerFromValue(this.testIntNativeLiteral));
        Assert.assertEquals(this.testInt, RdfUtils.getIntegerFromValue(this.testIntStringLiteral));
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getLongFromValue(org.openrdf.model.Value)}
     * .
     */
    @Test
    public void testGetLongFromValue()
    {
        Assert.assertEquals(this.testLong, RdfUtils.getLongFromValue(this.testLongTypedLiteral));
        Assert.assertEquals(this.testLong, RdfUtils.getLongFromValue(this.testLongNativeLiteral));
        Assert.assertEquals(this.testLong, RdfUtils.getLongFromValue(this.testLongStringLiteral));
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getNamespaceEntries(org.openrdf.repository.Repository)}.
     */
    @Test
    public void testGetNamespaceEntries()
    {
        final InputStream nextInputStream = this.getClass().getResourceAsStream("/testconfigs/namespaceentry-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            final Map<URI, NamespaceEntry> results = RdfUtils.getNamespaceEntries(this.testRepository);
            
            Assert.assertEquals(1, results.size());
            
            for(final URI nextNamespaceEntryUri : results.keySet())
            {
                Assert.assertEquals("Results did not contain correct namespace entry URI",
                        this.testValueFactory.createURI("http://example.org/ns:abc"), nextNamespaceEntryUri);
                
                final NamespaceEntry nextNamespaceEntry = results.get(nextNamespaceEntryUri);
                
                Assert.assertNotNull("Namespace entry was null", nextNamespaceEntry);
                
                Assert.assertEquals("Namespace entry key was not the same as its map URI", nextNamespaceEntryUri,
                        nextNamespaceEntry.getKey());
                
                Assert.assertEquals("Authority was not parsed correctly",
                        this.testValueFactory.createURI("http://example.org/"), nextNamespaceEntry.getAuthority());
                
                Assert.assertEquals("URI template was not parsed correctly",
                        "${authority}${namespace}${separator}${identifier}", nextNamespaceEntry.getUriTemplate());
                
                Assert.assertEquals("Separator was not parsed correctly", ":", nextNamespaceEntry.getSeparator());
                
                Assert.assertEquals("Preferred prefix was not parsed correctly", "abc",
                        nextNamespaceEntry.getPreferredPrefix());
                
                Assert.assertTrue("Convert queries to preferred prefix setting was not parsed correctly",
                        nextNamespaceEntry.getConvertQueriesToPreferredPrefix());
                
                Assert.assertEquals("Identifier regex was not parsed correctly", "[zyx][qrs][tuv]",
                        nextNamespaceEntry.getIdentifierRegex());
                
                Assert.assertEquals("Description was not parsed correctly", "ABC Example Database",
                        nextNamespaceEntry.getDescription());
                
                Assert.assertEquals("QueryAllNamespace was not implemented correctly for this object",
                        QueryAllNamespaces.NAMESPACEENTRY, nextNamespaceEntry.getDefaultNamespace());
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
     * {@link org.queryall.utils.RdfUtils#getBooleanFromValue(org.openrdf.model.Value)}.
     */
    @Test
    public void testGetNativeBooleanFromValue()
    {
        Assert.assertTrue(RdfUtils.getBooleanFromValue(this.trueBooleanNativeLiteral));
        Assert.assertFalse(RdfUtils.getBooleanFromValue(this.falseBooleanNativeLiteral));
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getNormalisationRules(org.openrdf.repository.Repository)}.
     * 
     * TODO Test specific subclasses of NormalisationRule
     */
    @Test
    public void testGetNormalisationRules()
    {
        final InputStream nextInputStream = this.getClass().getResourceAsStream("/testconfigs/normalisationrule-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            final Map<URI, NormalisationRule> results = RdfUtils.getNormalisationRules(this.testRepository);
            
            Assert.assertEquals("RdfUtils did not create the expected number of normalisation rules.", 1, results.size());
            
            for(final URI nextNormalisationRuleUri : results.keySet())
            {
                Assert.assertEquals("Results did not contain correct normalisation rule URI",
                        this.testValueFactory.createURI("http://example.org/rdfrule:abc_issn"),
                        nextNormalisationRuleUri);
                
                final NormalisationRule nextNormalisationRule = results.get(nextNormalisationRuleUri);
                
                Assert.assertNotNull("Normalisation rule was null", nextNormalisationRule);
                
                Assert.assertEquals("Normalisation rule key was not the same as its map URI", nextNormalisationRuleUri,
                        nextNormalisationRule.getKey());
                
                Assert.assertTrue(
                        "Could not find expected stage",
                        nextNormalisationRule.getStages().contains(
                                NormalisationRuleSchema.getRdfruleStageQueryVariables()));
                Assert.assertTrue(
                        "Could not find expected stage",
                        nextNormalisationRule.getStages().contains(
                                NormalisationRuleSchema.getRdfruleStageBeforeResultsImport()));
                
                Assert.assertEquals("Description was not parsed correctly",
                        "Converts between the URIs used by the ABC ISSN's and the Example organisation ISSN namespace",
                        nextNormalisationRule.getDescription());
                Assert.assertEquals("Order was not parsed correctly", 110, nextNormalisationRule.getOrder());
                Assert.assertEquals("Include exclude order was not parsed correctly",
                        ProfileImpl.getProfileIncludeThenExcludeUri(),
                        nextNormalisationRule.getProfileIncludeExcludeOrder());
                
                Assert.assertTrue("Related namespace was not parsed correctly", nextNormalisationRule
                        .getRelatedNamespaces().contains(this.testValueFactory.createURI("http://example.org/ns:issn")));
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
     * {@link org.queryall.utils.RdfUtils#getProfiles(org.openrdf.repository.Repository)}.
     */
    @Test
    public void testGetProfiles()
    {
        final InputStream nextInputStream = this.getClass().getResourceAsStream("/testconfigs/profile-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            final Map<URI, Profile> results = RdfUtils.getProfiles(this.testRepository);
            
            Assert.assertEquals(2, results.size());
            
            for(final URI nextProfileUri : results.keySet())
            {
                final Profile nextProfile = results.get(nextProfileUri);
                
                Assert.assertNotNull("Profile was null", nextProfile);
                
                Assert.assertEquals("Profile key was not the same as its map URI", nextProfileUri, nextProfile.getKey());
                
                if(nextProfileUri.equals(this.testProfileUri1))
                {
                    Assert.assertEquals("Results did not contain correct profile URI", this.testProfileUri1,
                            nextProfileUri);
                    
                    Assert.assertEquals(
                            "Title was not parsed correctly",
                            "Test profile for RDF Utilities test class with all implicit not allowed and exclude by default",
                            nextProfile.getTitle());
                    Assert.assertEquals("Order was not parsed correctly", 120, nextProfile.getOrder());
                    
                    Assert.assertEquals("Default profile include exclude order was not parsed correctly",
                            ProfileImpl.getProfileIncludeThenExcludeUri(),
                            nextProfile.getDefaultProfileIncludeExcludeOrder());
                    
                    Assert.assertFalse("Allow implicit provider inclusions was not parsed correctly",
                            nextProfile.getAllowImplicitProviderInclusions());
                    Assert.assertFalse("Allow implicit query inclusions was not parsed correctly",
                            nextProfile.getAllowImplicitQueryTypeInclusions());
                    Assert.assertFalse("Allow implicit rdf rule inclusions was not parsed correctly",
                            nextProfile.getAllowImplicitRdfRuleInclusions());
                    
                    Assert.assertEquals("Did not find the expected number of included providers", 1, nextProfile
                            .getIncludeProviders().size());
                    Assert.assertEquals("Did not find the expected number of excluded providers", 1, nextProfile
                            .getExcludeProviders().size());
                    
                    Assert.assertEquals("Did not find the expected number of included query types", 1, nextProfile
                            .getIncludeQueryTypes().size());
                    Assert.assertEquals("Did not find the expected number of excluded query types", 1, nextProfile
                            .getExcludeQueryTypes().size());
                    
                    Assert.assertEquals("Did not find the expected number of included rdf rules", 1, nextProfile
                            .getIncludeRdfRules().size());
                    Assert.assertEquals("Did not find the expected number of excluded rdf rules", 1, nextProfile
                            .getExcludeRdfRules().size());
                }
                else if(nextProfileUri.equals(this.testProfileUri2))
                {
                    Assert.assertEquals("Results did not contain correct profile URI", this.testProfileUri2,
                            nextProfileUri);
                    
                    Assert.assertEquals(
                            "Title was not parsed correctly",
                            "Test profile 2 for RDF Utilities test class with all implicit allowed, and allowed by default",
                            nextProfile.getTitle());
                    Assert.assertEquals("Order was not parsed correctly", 230, nextProfile.getOrder());
                    
                    Assert.assertEquals("Default profile include exclude order was not parsed correctly",
                            ProfileImpl.getProfileExcludeThenIncludeUri(),
                            nextProfile.getDefaultProfileIncludeExcludeOrder());
                    
                    Assert.assertTrue("Allow implicit provider inclusions was not parsed correctly",
                            nextProfile.getAllowImplicitProviderInclusions());
                    Assert.assertTrue("Allow implicit query inclusions was not parsed correctly",
                            nextProfile.getAllowImplicitQueryTypeInclusions());
                    Assert.assertTrue("Allow implicit rdf rule inclusions was not parsed correctly",
                            nextProfile.getAllowImplicitRdfRuleInclusions());
                    
                    Assert.assertEquals("Did not find the expected number of included providers", 0, nextProfile
                            .getIncludeProviders().size());
                    Assert.assertEquals("Did not find the expected number of excluded providers", 0, nextProfile
                            .getExcludeProviders().size());
                    
                    Assert.assertEquals("Did not find the expected number of included query types", 0, nextProfile
                            .getIncludeQueryTypes().size());
                    Assert.assertEquals("Did not find the expected number of excluded query types", 0, nextProfile
                            .getExcludeQueryTypes().size());
                    
                    Assert.assertEquals("Did not find the expected number of included rdf rules", 0, nextProfile
                            .getIncludeRdfRules().size());
                    Assert.assertEquals("Did not find the expected number of excluded rdf rules", 0, nextProfile
                            .getExcludeRdfRules().size());
                    
                }
                else
                {
                    Assert.fail("Found unexpected profile URI=" + nextProfileUri);
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
     * {@link org.queryall.utils.RdfUtils#getProviders(org.openrdf.repository.Repository)}.
     */
    @Test
    public void testGetProviders()
    {
        final InputStream nextInputStream = this.getClass().getResourceAsStream("/testconfigs/provider-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            final Map<URI, Provider> results = RdfUtils.getProviders(this.testRepository);
            
            Assert.assertEquals(1, results.size());
            
            for(final URI nextProviderUri : results.keySet())
            {
                final Provider nextProvider = results.get(nextProviderUri);
                
                Assert.assertNotNull("Provider was null", nextProvider);
                
                Assert.assertEquals("Provider key was not the same as its map URI", nextProviderUri,
                        nextProvider.getKey());
                
                if(nextProviderUri.equals(this.testProviderUri1))
                {
                    Assert.assertEquals("Results did not contain correct profile URI", this.testProviderUri1,
                            nextProviderUri);
                    
                    Assert.assertEquals("Title was not parsed correctly", "Test provider 1", nextProvider.getTitle());
                    
                    Assert.assertEquals("Resolution strategy was not parsed correctly",
                            ProviderSchema.getProviderProxy(), nextProvider.getRedirectOrProxy());
                    Assert.assertEquals("Resolution method was not parsed correctly",
                            ProviderSchema.getProviderNoCommunication(), nextProvider.getEndpointMethod());
                    Assert.assertFalse("Default provider status was not parsed correctly",
                            nextProvider.getIsDefaultSource());
                    
                    Assert.assertEquals("Profile include exclude order was not parsed correctly",
                            ProfileImpl.getProfileExcludeThenIncludeUri(), nextProvider.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Namespaces were not parsed correctly", 1, nextProvider.getNamespaces().size());
                    Assert.assertEquals("Query Types were not parsed correctly", 1, nextProvider
                            .getIncludedInQueryTypes().size());
                    Assert.assertEquals("Normalisation rules were not parsed correctly", 1, nextProvider
                            .getNormalisationUris().size());
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
     * {@link org.queryall.utils.RdfUtils#getQueryTypes(org.openrdf.repository.Repository)}.
     */
    @Test
    public void testGetQueryTypes()
    {
        final InputStream nextInputStream = this.getClass().getResourceAsStream("/testconfigs/querytype-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            final Map<URI, QueryType> results = RdfUtils.getQueryTypes(this.testRepository);
            
            Assert.assertEquals("RdfUtils did not create the expected number of query types.", 1, results.size());
            
            for(final URI nextQueryTypeUri : results.keySet())
            {
                final QueryType nextQueryType = results.get(nextQueryTypeUri);
                
                Assert.assertNotNull("QueryType was null", nextQueryType);
                
                Assert.assertEquals("QueryType key was not the same as its map URI", nextQueryTypeUri,
                        nextQueryType.getKey());
                
                if(nextQueryTypeUri.equals(this.testQueryTypeUri1))
                {
                    Assert.assertEquals("Results did not contain correct query type URI", this.testQueryTypeUri1,
                            nextQueryTypeUri);
                    
                    Assert.assertTrue("Query type is dummy query type was not parsed correctly",
                            nextQueryType.getIsDummyQueryType());
                    
                    Assert.assertTrue("Query type is pageable was not parsed correctly", nextQueryType.getIsPageable());
                    
                    Assert.assertEquals("Query type title was not parsed correctly", "Test 1 query type",
                            nextQueryType.getTitle());
                    
                    Assert.assertFalse("Query type handle all namespaces was not parsed correctly",
                            nextQueryType.getHandleAllNamespaces());
                    
                    Assert.assertTrue("Query type is namespace specific was not parsed correctly",
                            nextQueryType.getIsNamespaceSpecific());
                    
                    Assert.assertEquals("Query type namespace match method was not parsed correctly",
                            QueryTypeSchema.getNamespaceMatchAllUri(), nextQueryType.getNamespaceMatchMethod());
                    
                    Assert.assertTrue("Query type include defaults was not parsed correctly",
                            nextQueryType.getIncludeDefaults());
                    
                    Assert.assertEquals("Query type input regex was not parsed correctly", "^([\\w-]+):(.+)",
                            nextQueryType.getInputRegex());
                    
                    Assert.assertEquals("Query type template string was not parsed correctly",
                            "CONSTRUCT { ${normalisedStandardUri} ?p ?o . } WHERE { ${endpointSpecificUri} ?p ?o . }",
                            nextQueryType.getTemplateString());
                    
                    Assert.assertEquals("Query type query uri template string was not parsed correctly",
                            "${defaultHostAddress}${input_1}${defaultSeparator}${input_2}",
                            nextQueryType.getQueryUriTemplateString());
                    
                    Assert.assertEquals("Query type standard uri template string was not parsed correctly",
                            "${defaultHostAddress}${input_1}${defaultSeparator}${input_2}",
                            nextQueryType.getStandardUriTemplateString());
                    
                    Assert.assertEquals(
                            "Query type output rdf xml string was not parsed correctly",
                            "<rdf:Description rdf:about=\"${xmlEncoded_inputUrlEncoded_privateuppercase_normalisedStandardUri}\"><ns0pred:xmlUrl xmlns:ns0pred=\"${defaultHostAddress}bio2rdf_resource:\">${xmlEncoded_inputUrlEncoded_privateuppercase_normalisedQueryUri}</ns0pred:xmlUrl></rdf:Description>",
                            nextQueryType.getOutputRdfXmlString());
                    
                    Assert.assertTrue("Query type in robots txt was not parsed correctly",
                            nextQueryType.getInRobotsTxt());
                    
                    Assert.assertEquals("Query type profile include exclude order was not parsed correctly",
                            ProfileImpl.getProfileExcludeThenIncludeUri(),
                            nextQueryType.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Query type public identifiers size was not parsed correctly", 1,
                            nextQueryType.getPublicIdentifierIndexes().length);
                    
                    Assert.assertEquals("Query type public identifiers were not parsed correctly", 1,
                            nextQueryType.getPublicIdentifierIndexes()[0]);
                    
                    Assert.assertEquals("Query type namespace input indexes size was not parsed correctly", 1,
                            nextQueryType.getNamespaceInputIndexes().length);
                    
                    Assert.assertEquals("Query type namespace input indexes were not parsed correctly", 1,
                            nextQueryType.getNamespaceInputIndexes()[0]);
                    
                    Assert.assertEquals("Query type semantically linked query types were not parsed correctly", 1,
                            nextQueryType.getSemanticallyLinkedQueryTypes().size());
                    
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
     * {@link org.queryall.utils.RdfUtils#getRuleTests(org.openrdf.repository.Repository)}.
     */
    @Test
    public void testGetRuleTests()
    {
        final InputStream nextInputStream = this.getClass().getResourceAsStream("/testconfigs/ruletest-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            final Map<URI, RuleTest> results = RdfUtils.getRuleTests(this.testRepository);
            
            Assert.assertEquals(1, results.size());
            
            for(final URI nextRuleTestUri : results.keySet())
            {
                final RuleTest nextRuleTest = results.get(nextRuleTestUri);
                
                Assert.assertNotNull("RuleTest was null", nextRuleTest);
                
                Assert.assertEquals("RuleTest key was not the same as its map URI", nextRuleTestUri,
                        nextRuleTest.getKey());
                
                if(nextRuleTestUri.equals(this.testRuleTestUri1))
                {
                    Assert.assertEquals("Results did not contain correct rule test URI", this.testRuleTestUri1,
                            nextRuleTestUri);
                    
                    Assert.assertEquals("RuleTest stages were not parsed correctly", 2, nextRuleTest.getStages().size());
                    
                    Assert.assertEquals("RuleTest rules were not parsed correctly", 1, nextRuleTest.getRuleUris()
                            .size());
                    
                    Assert.assertEquals("RuleTest input string was not parsed correctly", "http://example.org/",
                            nextRuleTest.getTestInputString());
                    
                    Assert.assertEquals("RuleTest output string was not parsed correctly", "http://otherexample.net/",
                            nextRuleTest.getTestOutputString());
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
     * {@link org.queryall.utils.RdfUtils#getBooleanFromValue(org.openrdf.model.Value)}.
     */
    @Test
    public void testGetStringBooleanFromValue()
    {
        Assert.assertTrue(RdfUtils.getBooleanFromValue(this.trueBooleanStringLiteral));
        Assert.assertFalse(RdfUtils.getBooleanFromValue(this.falseBooleanStringLiteral));
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getBooleanFromValue(org.openrdf.model.Value)}.
     */
    @Test
    public void testGetTypedBooleanFromValue()
    {
        Assert.assertTrue(RdfUtils.getBooleanFromValue(this.trueBooleanTypedLiteral));
        Assert.assertFalse(RdfUtils.getBooleanFromValue(this.falseBooleanTypedLiteral));
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getUTF8StringValueFromSesameValue(org.openrdf.model.Value)}
     * .
     */
    @Test
    @Ignore
    public void testGetUTF8StringValueFromSesameValue()
    {
        Assert.fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getWriterFormat(java.lang.String)}.
     */
    @Test
    public void testGetWriterFormat()
    {
        Assert.assertEquals("Could not find RDF XML writer format", RDFFormat.RDFXML,
                RdfUtils.getWriterFormat("application/rdf+xml"));
        Assert.assertEquals("Could not find N3 writer format", RDFFormat.N3, RdfUtils.getWriterFormat("text/rdf+n3"));
        Assert.assertNull("Did not properly respond with null for HTML format", RdfUtils.getWriterFormat("text/html"));
    }
    
}
