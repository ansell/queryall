/**
 * 
 */
package org.queryall.utils.test;

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
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.namespace.RegexValidatingNamespaceEntry;
import org.queryall.api.namespace.ValidatingNamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.profile.ProfileSchema;
import org.queryall.api.provider.HttpProvider;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.HttpSparqlProvider;
import org.queryall.api.provider.Provider;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.provider.RdfProvider;
import org.queryall.api.provider.SparqlProvider;
import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.querytype.ProcessorQueryType;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.querytype.QueryTypeSchema;
import org.queryall.api.querytype.RdfInputQueryType;
import org.queryall.api.querytype.RdfOutputQueryType;
import org.queryall.api.querytype.RegexInputQueryType;
import org.queryall.api.querytype.SparqlProcessorQueryType;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.PrefixMappingNormalisationRule;
import org.queryall.api.rdfrule.RegexNormalisationRule;
import org.queryall.api.rdfrule.SparqlAskRule;
import org.queryall.api.rdfrule.SparqlConstructRule;
import org.queryall.api.rdfrule.SparqlConstructRuleSchema;
import org.queryall.api.rdfrule.SparqlNormalisationRule;
import org.queryall.api.rdfrule.XsltNormalisationRule;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.ruletest.SparqlRuleTest;
import org.queryall.api.ruletest.StringRuleTest;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.utils.RdfUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfUtilsTest
{
    /**
     * There will always be cases where unexpected triples appear in annotated configurations, so
     * this is FALSE by default to match reality If you want to test a new feature is being parsed
     * correctly, you can temporarily turn this on
     */
    private static final boolean FAIL_ON_UNEXPECTED_TRIPLES = true;
    
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
    private URI testRuleTestUri2;
    private URI testQueryTypeUri1;
    private URI testQueryTypeUri2;
    private URI testNormalisationRule1;
    private URI testNormalisationRule2;
    private URI testNormalisationRule3;
    private URI testNormalisationRule4;
    
    private URI testNormalisationRule5;
    
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
        this.testRuleTestUri2 = this.testValueFactory.createURI("http://example.org/ruletest:test-2");
        
        this.testQueryTypeUri1 = this.testValueFactory.createURI("http://example.org/query:test-1");
        this.testQueryTypeUri2 = this.testValueFactory.createURI("http://example.org/query:test-2");
        
        this.testNormalisationRule1 = this.testValueFactory.createURI("http://example.org/rdfrule:abc_issn");
        this.testNormalisationRule2 =
                this.testValueFactory.createURI("http://bio2rdf.org/rdfrule:neurocommonsgeneaddsymboluriconstruct");
        this.testNormalisationRule3 = this.testValueFactory.createURI("http://bio2rdf.org/rdfrule:xsltNlmPubmed");
        this.testNormalisationRule4 = this.testValueFactory.createURI("http://oas.example.org/rdfrule:bio2rdfpo");
        this.testNormalisationRule5 =
                this.testValueFactory.createURI("http://bio2rdf.org/rdfrule:neurocommonsgeneaddsymboluriask");
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
        this.testNormalisationRule1 = null;
        this.testNormalisationRule2 = null;
        this.testNormalisationRule3 = null;
        this.testNormalisationRule4 = null;
        this.testNormalisationRule5 = null;
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
    public void testGetDateTimeFromValue()
    {
        try
        {
            Assert.assertEquals(this.testDate.getTime(), RdfUtils.getDateTimeFromValue(this.testDateTypedLiteral)
                    .getTime(), 1000);
        }
        catch(final ParseException e)
        {
            Assert.fail("Found unexpected ParseException e=" + e.getMessage());
        }
        
        try
        {
            Assert.assertEquals(this.testDate.getTime(), RdfUtils.getDateTimeFromValue(this.testDateStringLiteral)
                    .getTime(), 1000);
        }
        catch(final ParseException e)
        {
            Assert.fail("Found unexpected ParseException e=" + e.getMessage());
        }
        
        try
        {
            Assert.assertEquals(this.testDate.getTime(), RdfUtils.getDateTimeFromValue(this.testDateNativeLiteral)
                    .getTime());
        }
        catch(final ParseException e)
        {
            // TODO: Make this work
            // Assert.fail("Found unexpected ParseException e=" + e.getMessage());
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
                
                Assert.assertEquals("Description was not parsed correctly", "ABC Example Database",
                        nextNamespaceEntry.getDescription());
                
                Assert.assertEquals("QueryAllNamespace was not implemented correctly for this object",
                        QueryAllNamespaces.NAMESPACEENTRY, nextNamespaceEntry.getDefaultNamespace());
                
                Assert.assertTrue("Was not a validating namespace",
                        nextNamespaceEntry instanceof ValidatingNamespaceEntry);
                
                final ValidatingNamespaceEntry nextValidatingNamespaceEntry =
                        (ValidatingNamespaceEntry)nextNamespaceEntry;
                
                Assert.assertTrue("Was not a regex validating namespace",
                        nextNamespaceEntry instanceof ValidatingNamespaceEntry);
                
                Assert.assertTrue("Validation possible field was not parsed correctly",
                        nextValidatingNamespaceEntry.getValidationPossible());
                
                final RegexValidatingNamespaceEntry nextRegexValidatingNamespaceEntry =
                        (RegexValidatingNamespaceEntry)nextValidatingNamespaceEntry;
                
                Assert.assertEquals("Identifier Regex was not parsed correctly", "[zyx][qrs][tuv]",
                        nextRegexValidatingNamespaceEntry.getIdentifierRegex());
                
                // once we have found that the regex was parsed correctly through the
                // RegexValidatingNamespaceEntry interface, go back through ValidatingNamespaceEntry
                // and validate a test identifier
                Assert.assertTrue("Validation failed", nextValidatingNamespaceEntry.validateIdentifier("zrv"));
                
                if(RdfUtilsTest.FAIL_ON_UNEXPECTED_TRIPLES)
                {
                    Assert.assertEquals("There were unexpected triples in the test file. This should not happen. "
                            + nextNamespaceEntry.getUnrecognisedStatements(), 0, nextNamespaceEntry
                            .getUnrecognisedStatements().size());
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
    public void testGetNativeBooleanFromValue()
    {
        Assert.assertTrue(RdfUtils.getBooleanFromValue(this.trueBooleanNativeLiteral));
        Assert.assertFalse(RdfUtils.getBooleanFromValue(this.falseBooleanNativeLiteral));
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getNormalisationRules(org.openrdf.repository.Repository)}.
     * 
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
            
            Assert.assertEquals("RdfUtils did not create the expected number of normalisation rules.", 5,
                    results.size());
            
            for(final URI nextNormalisationRuleUri : results.keySet())
            {
                final NormalisationRule nextNormalisationRule = results.get(nextNormalisationRuleUri);
                
                Assert.assertNotNull("Normalisation rule was null", nextNormalisationRule);
                
                Assert.assertEquals("Normalisation rule key was not the same as its map URI", nextNormalisationRuleUri,
                        nextNormalisationRule.getKey());
                
                if(nextNormalisationRuleUri.equals(this.testNormalisationRule1))
                {
                    Assert.assertEquals("Results did not contain correct normalisation rule URI",
                            this.testNormalisationRule1, nextNormalisationRule.getKey());
                    
                    Assert.assertEquals("Did not find expected number of stages", 2, nextNormalisationRule.getStages()
                            .size());
                    Assert.assertTrue(
                            "Could not find expected stage",
                            nextNormalisationRule.getStages().contains(
                                    NormalisationRuleSchema.getRdfruleStageQueryVariables()));
                    Assert.assertTrue(
                            "Could not find expected stage",
                            nextNormalisationRule.getStages().contains(
                                    NormalisationRuleSchema.getRdfruleStageBeforeResultsImport()));
                    
                    Assert.assertEquals(
                            "Description was not parsed correctly",
                            "Converts between the URIs used by the ABC ISSN's and the Example organisation ISSN namespace",
                            nextNormalisationRule.getDescription());
                    Assert.assertEquals("Order was not parsed correctly", 110, nextNormalisationRule.getOrder());
                    Assert.assertEquals("Include exclude order was not parsed correctly",
                            ProfileSchema.getProfileIncludeThenExcludeUri(),
                            nextNormalisationRule.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Related namespaces were not parsed correctly", 1, nextNormalisationRule
                            .getRelatedNamespaces().size());
                    
                    Assert.assertTrue(
                            "Related namespace was not parsed correctly",
                            nextNormalisationRule.getRelatedNamespaces().contains(
                                    this.testValueFactory.createURI("http://example.org/ns:issn")));
                    
                    final RegexNormalisationRule nextRegexRule = (RegexNormalisationRule)nextNormalisationRule;
                    
                    Assert.assertEquals("Regex rule input match regex was not parsed correctly",
                            "http://example\\.org/issn:", nextRegexRule.getInputMatchRegex());
                    Assert.assertEquals("Regex rule input replace regex was not parsed correctly",
                            "http://id\\.abc\\.org/issn/", nextRegexRule.getInputReplaceRegex());
                    
                    Assert.assertEquals("Regex rule output match regex was not parsed correctly",
                            "http://id\\.abc\\.org/issn/", nextRegexRule.getOutputMatchRegex());
                    Assert.assertEquals("Regex rule output replace regex was not parsed correctly",
                            "http://example\\.org/issn:", nextRegexRule.getOutputReplaceRegex());
                }
                else if(nextNormalisationRuleUri.equals(this.testNormalisationRule2))
                {
                    Assert.assertEquals("Results did not contain correct normalisation rule URI",
                            this.testNormalisationRule2, nextNormalisationRule.getKey());
                    
                    Assert.assertEquals("Did not find expected number of stages", 1, nextNormalisationRule.getStages()
                            .size());
                    Assert.assertTrue(
                            "Could not find expected stage",
                            nextNormalisationRule.getStages().contains(
                                    NormalisationRuleSchema.getRdfruleStageAfterResultsToPool()));
                    
                    Assert.assertEquals("Description was not parsed correctly",
                            "Add symbol URI based on Neurocommons gene symbol literals",
                            nextNormalisationRule.getDescription());
                    Assert.assertEquals("Order was not parsed correctly", 100, nextNormalisationRule.getOrder());
                    Assert.assertEquals("Include exclude order was not parsed correctly",
                            ProfileSchema.getProfileExcludeThenIncludeUri(),
                            nextNormalisationRule.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Related namespaces were not parsed correctly", 1, nextNormalisationRule
                            .getRelatedNamespaces().size());
                    
                    Assert.assertTrue(
                            "Related namespace was not parsed correctly",
                            nextNormalisationRule.getRelatedNamespaces().contains(
                                    this.testValueFactory.createURI("http://example.org/ns:symbol")));
                    
                    Assert.assertTrue(
                            "Normalisation rule was not implemented using the SparqlNormalisationRule interface",
                            nextNormalisationRule instanceof SparqlNormalisationRule);
                    
                    final SparqlNormalisationRule nextSparqlRule = (SparqlNormalisationRule)nextNormalisationRule;
                    
                    Assert.assertEquals("Did not parse the correct number of sparql where patterns", 1, nextSparqlRule
                            .getSparqlWherePatterns().size());
                    
                    Assert.assertEquals(
                            "Sparql construct query where pattern was not parsed correctly",
                            " ?myUri <http://purl.org/science/owl/sciencecommons/ggp_has_primary_symbol> ?primarysymbol . bind(iri(concat(\"http://bio2rdf.org/symbol:\", encode_for_uri(lcase(str(?primarySymbol))))) AS ?symbolUri)",
                            nextSparqlRule.getSparqlWherePatterns().iterator().next());
                    
                    Assert.assertTrue("Normalisation rule was not implemented using the SparqlConstructRule interface",
                            nextNormalisationRule instanceof SparqlConstructRule);
                    
                    final SparqlConstructRule nextSparqlConstructRule = (SparqlConstructRule)nextSparqlRule;
                    
                    Assert.assertEquals("Sparql mode not parsed correctly",
                            SparqlConstructRuleSchema.getSparqlRuleModeAddAllMatchingTriples(),
                            nextSparqlConstructRule.getMode());
                    
                    Assert.assertEquals("Sparql construct query target was not parsed correctly",
                            "?myUri <http://bio2rdf.org/bio2rdf_resource:dbxref> ?symbolUri . ",
                            nextSparqlConstructRule.getSparqlConstructQueryTarget());
                    
                }
                else if(nextNormalisationRuleUri.equals(this.testNormalisationRule3))
                {
                    Assert.assertEquals("Results did not contain correct normalisation rule URI",
                            this.testNormalisationRule3, nextNormalisationRule.getKey());
                    
                    Assert.assertEquals("Did not find expected number of stages", 1, nextNormalisationRule.getStages()
                            .size());
                    Assert.assertTrue(
                            "Could not find expected stage",
                            nextNormalisationRule.getStages().contains(
                                    NormalisationRuleSchema.getRdfruleStageBeforeResultsImport()));
                    
                    Assert.assertEquals("Description was not parsed correctly",
                            "XSLT transformation of a Pubmed XML document into RDF NTriples",
                            nextNormalisationRule.getDescription());
                    Assert.assertEquals("Order was not parsed correctly", 100, nextNormalisationRule.getOrder());
                    Assert.assertEquals("Include exclude order was not parsed correctly",
                            ProfileSchema.getProfileExcludeThenIncludeUri(),
                            nextNormalisationRule.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Related namespaces were not parsed correctly", 1, nextNormalisationRule
                            .getRelatedNamespaces().size());
                    
                    Assert.assertTrue(
                            "Related namespace was not parsed correctly",
                            nextNormalisationRule.getRelatedNamespaces().contains(
                                    this.testValueFactory.createURI("http://example.org/ns:pubmed")));
                    
                    Assert.assertTrue(
                            "Normalisation rule was not implemented using the XsltNormalisationRule interface",
                            nextNormalisationRule instanceof XsltNormalisationRule);
                    
                    final XsltNormalisationRule nextXsltRule = (XsltNormalisationRule)nextNormalisationRule;
                    
                    Assert.assertTrue("Xslt transform was not parsed correctly", nextXsltRule.getXsltStylesheet()
                            .startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"));
                    
                    Assert.assertTrue("Xslt transform was not parsed correctly", nextXsltRule.getXsltStylesheet()
                            .contains("</xsl:stylesheet>"));
                }
                else if(nextNormalisationRuleUri.equals(this.testNormalisationRule4))
                {
                    Assert.assertEquals("Results did not contain correct normalisation rule URI",
                            this.testNormalisationRule4, nextNormalisationRule.getKey());
                    
                    Assert.assertEquals("Did not find expected number of stages", 3, nextNormalisationRule.getStages()
                            .size());
                    Assert.assertTrue("Could not find expected stage: query variables", nextNormalisationRule
                            .getStages().contains(NormalisationRuleSchema.getRdfruleStageQueryVariables()));
                    Assert.assertTrue("Could not find expected stage: before results import", nextNormalisationRule
                            .getStages().contains(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport()));
                    Assert.assertTrue("Could not find expected stage: after results import", nextNormalisationRule
                            .getStages().contains(NormalisationRuleSchema.getRdfruleStageAfterResultsImport()));
                    
                    Assert.assertEquals(
                            "Description was not parsed correctly",
                            "Provides conversion between the deprecated Bio2RDF Plant Ontology namespace and the OAS Plant Ontology namespace using a simple prefix mapping.",
                            nextNormalisationRule.getDescription());
                    Assert.assertEquals("Order was not parsed correctly", 100, nextNormalisationRule.getOrder());
                    Assert.assertEquals("Include exclude order was not parsed correctly",
                            ProfileSchema.getProfileIncludeThenExcludeUri(),
                            nextNormalisationRule.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Related namespaces were not parsed correctly", 1, nextNormalisationRule
                            .getRelatedNamespaces().size());
                    
                    Assert.assertTrue(
                            "Related namespace was not parsed correctly",
                            nextNormalisationRule.getRelatedNamespaces().contains(
                                    this.testValueFactory.createURI("http://oas.example.org/ns:po")));
                    
                    Assert.assertTrue(
                            "Normalisation rule was not implemented using the PrefixMappingNormalisationRule interface",
                            nextNormalisationRule instanceof PrefixMappingNormalisationRule);
                    
                    final PrefixMappingNormalisationRule nextPrefixMappingRule =
                            (PrefixMappingNormalisationRule)nextNormalisationRule;
                    
                    Assert.assertEquals("Input URI prefix was not parsed correctly", "http://bio2rdf.org/po:",
                            nextPrefixMappingRule.getInputUriPrefix());
                    Assert.assertEquals("Output URI prefix was not parsed correctly", "http://oas.example.org/po:",
                            nextPrefixMappingRule.getOutputUriPrefix());
                    
                    Assert.assertEquals("Subject mapping predicates were not parsed correctly", 1,
                            nextPrefixMappingRule.getSubjectMappingPredicates().size());
                    Assert.assertEquals("Predicate mapping predicates were not parsed correctly", 1,
                            nextPrefixMappingRule.getPredicateMappingPredicates().size());
                    Assert.assertEquals("Object mapping predicates were not parsed correctly", 1, nextPrefixMappingRule
                            .getObjectMappingPredicates().size());
                    
                    Assert.assertTrue("Subject mapping predicates were not parsed correctly: owl:sameAs",
                            nextPrefixMappingRule.getSubjectMappingPredicates().contains(OWL.SAMEAS));
                    Assert.assertTrue("Predicate mapping predicates were not parsed correctly: owl:equivalentProperty",
                            nextPrefixMappingRule.getPredicateMappingPredicates().contains(OWL.EQUIVALENTPROPERTY));
                    Assert.assertTrue("Object mapping predicates were not parsed correctly: owl:equivalentClass",
                            nextPrefixMappingRule.getObjectMappingPredicates().contains(OWL.EQUIVALENTCLASS));
                }
                else if(nextNormalisationRuleUri.equals(this.testNormalisationRule5))
                {
                    Assert.assertEquals("Results did not contain correct normalisation rule URI",
                            this.testNormalisationRule5, nextNormalisationRule.getKey());
                    
                    Assert.assertEquals("Did not find expected number of stages", 1, nextNormalisationRule.getStages()
                            .size());
                    Assert.assertTrue(
                            "Could not find expected stage",
                            nextNormalisationRule.getStages().contains(
                                    NormalisationRuleSchema.getRdfruleStageAfterResultsImport()));
                    
                    Assert.assertEquals("Description was not parsed correctly",
                            "Tests for the existence of Neurocommons gene symbol literals",
                            nextNormalisationRule.getDescription());
                    Assert.assertEquals("Order was not parsed correctly", 110, nextNormalisationRule.getOrder());
                    Assert.assertEquals("Include exclude order was not parsed correctly",
                            ProfileSchema.getProfileExcludeThenIncludeUri(),
                            nextNormalisationRule.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Related namespaces were not parsed correctly", 1, nextNormalisationRule
                            .getRelatedNamespaces().size());
                    
                    Assert.assertTrue(
                            "Related namespace was not parsed correctly",
                            nextNormalisationRule.getRelatedNamespaces().contains(
                                    this.testValueFactory.createURI("http://example.org/ns:symbol")));
                    
                    Assert.assertTrue(
                            "Normalisation rule was not implemented using the SparqlNormalisationRule interface",
                            nextNormalisationRule instanceof SparqlNormalisationRule);
                    
                    final SparqlNormalisationRule nextSparqlRule = (SparqlNormalisationRule)nextNormalisationRule;
                    
                    Assert.assertEquals("Did not parse the correct number of sparql where patterns", 1, nextSparqlRule
                            .getSparqlWherePatterns().size());
                    
                    Assert.assertEquals(
                            "Sparql construct query where pattern was not parsed correctly",
                            " ?myUri <http://purl.org/science/owl/sciencecommons/ggp_has_primary_symbol> ?primarysymbol . ",
                            nextSparqlRule.getSparqlWherePatterns().iterator().next());
                    
                    Assert.assertTrue("Normalisation rule was not implemented using the SparqlAskRule interface",
                            nextNormalisationRule instanceof SparqlAskRule);
                    
                    final SparqlAskRule nextSparqlAskRule = (SparqlAskRule)nextSparqlRule;
                    
                    Assert.assertEquals("Did not generate the correct number of ask queries", 1, nextSparqlAskRule
                            .getSparqlAskQueries().size());
                }
                else
                {
                    Assert.fail("Found a rule with a URI that we were not testing for nextNormalisationRuleUri="
                            + nextNormalisationRuleUri);
                }
                
                if(RdfUtilsTest.FAIL_ON_UNEXPECTED_TRIPLES)
                {
                    Assert.assertEquals(
                            "There were unexpected triples in the test file. This should not happen. nextNormalisationRule.class="
                                    + nextNormalisationRule.getClass().getName() + " "
                                    + nextNormalisationRule.getUnrecognisedStatements(), 0, nextNormalisationRule
                                    .getUnrecognisedStatements().size());
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
                            ProfileSchema.getProfileIncludeThenExcludeUri(),
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
                            ProfileSchema.getProfileExcludeThenIncludeUri(),
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
                    Assert.fail("Found a profile with a URI that we were not testing for nextProfileUri="
                            + nextProfileUri);
                }
                
                if(RdfUtilsTest.FAIL_ON_UNEXPECTED_TRIPLES)
                {
                    Assert.assertEquals("There were unexpected triples in the test file. This should not happen.", 0,
                            nextProfile.getUnrecognisedStatements().size());
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
     * {@link org.queryall.utils.RdfUtils#getProjects(org.openrdf.repository.Repository)}.
     */
    @Test
    @Ignore
    public void testGetProjects()
    {
        // TODO: Implement me!
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
                            HttpProviderSchema.getProviderHttpGetUrl(), nextProvider.getEndpointMethod());
                    Assert.assertFalse("Default provider status was not parsed correctly",
                            nextProvider.getIsDefaultSource());
                    
                    Assert.assertEquals("Profile include exclude order was not parsed correctly",
                            ProfileSchema.getProfileExcludeThenIncludeUri(),
                            nextProvider.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Namespaces were not parsed correctly", 1, nextProvider.getNamespaces().size());
                    Assert.assertEquals("Query Types were not parsed correctly", 1, nextProvider
                            .getIncludedInQueryTypes().size());
                    Assert.assertEquals("Normalisation rules were not parsed correctly", 1, nextProvider
                            .getNormalisationUris().size());
                    
                    Assert.assertTrue("Provider was not parsed as an Http Provider",
                            nextProvider instanceof HttpProvider);
                    
                    final HttpProvider nextHttpProvider = (HttpProvider)nextProvider;
                    
                    Assert.assertEquals("Provider Http Endpoint Urls were not parsed correctly", 2, nextHttpProvider
                            .getEndpointUrls().size());
                    
                    // Test to make sure that we didn't parse it as any of the specialised providers
                    Assert.assertFalse("Provider was parsed incorrectly as a Sparql Provider",
                            nextProvider instanceof SparqlProvider);
                    
                    Assert.assertFalse("Provider was parsed incorrectly as an Http Sparql Provider",
                            nextProvider instanceof HttpSparqlProvider);
                    
                    Assert.assertFalse("Provider was parsed incorrectly as an Rdf Provider",
                            nextProvider instanceof RdfProvider);
                }
                else
                {
                    Assert.fail("Found a provider with a URI that we were not testing for nextProviderUri="
                            + nextProviderUri);
                }
                
                if(RdfUtilsTest.FAIL_ON_UNEXPECTED_TRIPLES)
                {
                    Assert.assertEquals("There were unexpected triples in the test file. This should not happen.", 0,
                            nextProvider.getUnrecognisedStatements().size());
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
            
            Assert.assertEquals("RdfUtils did not create the expected number of query types.", 2, results.size());
            
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
                    
                    Assert.assertEquals("Query type query uri template string was not parsed correctly",
                            "${defaultHostAddress}${input_1}${defaultSeparator}${input_2}",
                            nextQueryType.getQueryUriTemplateString());
                    
                    Assert.assertEquals("Query type standard uri template string was not parsed correctly",
                            "${defaultHostAddress}${input_1}${defaultSeparator}${input_2}",
                            nextQueryType.getStandardUriTemplateString());
                    
                    Assert.assertTrue("Query type in robots txt was not parsed correctly",
                            nextQueryType.getInRobotsTxt());
                    
                    Assert.assertEquals("Query type profile include exclude order was not parsed correctly",
                            ProfileSchema.getProfileExcludeThenIncludeUri(),
                            nextQueryType.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Query type public identifiers size was not parsed correctly", 1, nextQueryType
                            .getPublicIdentifierTags().size());
                    
                    Assert.assertTrue("Query type public identifiers were not parsed correctly", nextQueryType
                            .getPublicIdentifierTags().contains("input_1"));
                    
                    Assert.assertEquals("Query type namespace input indexes size was not parsed correctly", 1,
                            nextQueryType.getNamespaceInputTags().size());
                    
                    Assert.assertTrue("Query type namespace input indexes were not parsed correctly", nextQueryType
                            .getNamespaceInputTags().contains("input_1"));
                    
                    Assert.assertEquals("Query type semantically linked query types were not parsed correctly", 1,
                            nextQueryType.getLinkedQueryTypes().size());
                    
                    Assert.assertTrue("Query type was not parsed into a InputQueryType",
                            nextQueryType instanceof InputQueryType);
                    
                    final InputQueryType nextInputQueryType = (InputQueryType)nextQueryType;
                    
                    Assert.assertEquals("Query type expected input parameters were not parsed correctly", 2,
                            nextInputQueryType.getExpectedInputParameters().size());
                    
                    Assert.assertTrue("Query type was not parsed into a RegexInputQueryType",
                            nextQueryType instanceof RegexInputQueryType);
                    
                    final RegexInputQueryType nextRegexQueryType = (RegexInputQueryType)nextQueryType;
                    
                    Assert.assertEquals("Query type input regex was not parsed correctly", "^([\\w-]+):(.+)",
                            nextRegexQueryType.getInputRegex());
                    
                    Assert.assertTrue("Query type was not parsed into a RdfOutputQueryType",
                            nextQueryType instanceof RdfOutputQueryType);
                    
                    final RdfOutputQueryType nextRdfXmlQueryType = (RdfOutputQueryType)nextQueryType;
                    
                    Assert.assertEquals(
                            "Query type output rdf xml string was not parsed correctly",
                            "<rdf:Description rdf:about=\"${xmlEncoded_inputUrlEncoded_privateuppercase_normalisedStandardUri}\"><ns0pred:xmlUrl xmlns:ns0pred=\"${defaultHostAddress}bio2rdf_resource:\">${xmlEncoded_inputUrlEncoded_privateuppercase_normalisedQueryUri}</ns0pred:xmlUrl></rdf:Description>",
                            nextRdfXmlQueryType.getOutputString());
                    
                    Assert.assertTrue("Query type was not parsed into a ProcessorQueryType",
                            nextQueryType instanceof ProcessorQueryType);
                    
                    final ProcessorQueryType nextProcessorQueryType = (ProcessorQueryType)nextQueryType;
                    
                    
                    Assert.assertEquals("Query type template string was not parsed correctly",
                            "CONSTRUCT { ${normalisedStandardUri} ?p ?o . } WHERE { ${endpointSpecificUri} ?p ?o . }",
                            nextProcessorQueryType.getProcessingTemplateString());
                    
                    Assert.assertTrue("Query type was not parsed into a SparqlProcessorQueryType",
                            nextQueryType instanceof SparqlProcessorQueryType);
                    
                    final SparqlProcessorQueryType nextSparqlProcessorQueryType = (SparqlProcessorQueryType)nextQueryType;
                    
                    
                    Assert.assertEquals("Query type template string was not parsed correctly",
                            "CONSTRUCT { ${normalisedStandardUri} ?p ?o . } WHERE { ${endpointSpecificUri} ?p ?o . }",
                            nextSparqlProcessorQueryType.getSparqlTemplateString());
                    
                    Assert.assertEquals(nextProcessorQueryType.getProcessingTemplateString(), nextSparqlProcessorQueryType.getSparqlTemplateString());
                }
                else if(nextQueryTypeUri.equals(this.testQueryTypeUri2))
                {
                    Assert.assertEquals("Results did not contain correct query type URI", this.testQueryTypeUri2,
                            nextQueryTypeUri);
                    
                    Assert.assertFalse("Query type is dummy query type was not parsed correctly",
                            nextQueryType.getIsDummyQueryType());
                    
                    Assert.assertFalse("Query type is pageable was not parsed correctly", nextQueryType.getIsPageable());
                    
                    Assert.assertEquals("Query type title was not parsed correctly", "Test 2 query type",
                            nextQueryType.getTitle());
                    
                    Assert.assertTrue("Query type handle all namespaces was not parsed correctly",
                            nextQueryType.getHandleAllNamespaces());
                    
                    Assert.assertTrue("Query type is namespace specific was not parsed correctly",
                            nextQueryType.getIsNamespaceSpecific());
                    
                    Assert.assertEquals("Query type namespace match method was not parsed correctly",
                            QueryTypeSchema.getNamespaceMatchAnyUri(), nextQueryType.getNamespaceMatchMethod());
                    
                    Assert.assertFalse("Query type include defaults was not parsed correctly",
                            nextQueryType.getIncludeDefaults());
                    
                    Assert.assertEquals("Query type query uri template string was not parsed correctly",
                            "${defaultHostAddress}${input_1}${defaultSeparator}${input_2}",
                            nextQueryType.getQueryUriTemplateString());
                    
                    Assert.assertEquals("Query type standard uri template string was not parsed correctly",
                            "${defaultHostAddress}${input_1}${defaultSeparator}${input_2}",
                            nextQueryType.getStandardUriTemplateString());
                    
                    Assert.assertTrue("Query type in robots txt was not parsed correctly",
                            nextQueryType.getInRobotsTxt());
                    
                    Assert.assertEquals("Query type profile include exclude order was not parsed correctly",
                            ProfileSchema.getProfileExcludeThenIncludeUri(),
                            nextQueryType.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Query type public identifiers size was not parsed correctly", 1, nextQueryType
                            .getPublicIdentifierTags().size());
                    
                    Assert.assertTrue("Query type public identifiers were not parsed correctly", nextQueryType
                            .getPublicIdentifierTags().contains("input_1"));
                    
                    Assert.assertEquals("Query type namespace input indexes size was not parsed correctly", 1,
                            nextQueryType.getNamespaceInputTags().size());
                    
                    Assert.assertTrue("Query type namespace input indexes were not parsed correctly", nextQueryType
                            .getNamespaceInputTags().contains("input_1"));
                    
                    Assert.assertEquals("Query type semantically linked query types were not parsed correctly", 1,
                            nextQueryType.getLinkedQueryTypes().size());
                    
                    Assert.assertTrue("Query type was not parsed into a InputQueryType",
                            nextQueryType instanceof InputQueryType);
                    
                    final InputQueryType nextInputQueryType = (InputQueryType)nextQueryType;
                    
                    Assert.assertEquals("Query type expected input parameters were not parsed correctly", 2,
                            nextInputQueryType.getExpectedInputParameters().size());
                    
                    Assert.assertTrue("Query type was not parsed into a RdfInputQueryType",
                            nextQueryType instanceof RdfInputQueryType);
                    
                    final RdfInputQueryType nextRdfQueryType = (RdfInputQueryType)nextQueryType;
                    
                    Assert.assertEquals(
                            "Query type input sparql select was not parsed correctly",
                            "SELECT ?input_1 ?input_2 WHERE { ?testObjects rdf:type <http://example.org/rdfinputtest:type1> . ?testObjects <http://example.org/rdfinputtest:variable1> ?input_1 . ?testObjects <http://example.org/rdfinputtest:variable2> ?input_2 . }",
                            nextRdfQueryType.getSparqlInputSelect());
                    
                    Assert.assertTrue("Query type was not parsed into a RdfOutputQueryType",
                            nextQueryType instanceof RdfOutputQueryType);
                    
                    final RdfOutputQueryType nextRdfOutputQueryType = (RdfOutputQueryType)nextQueryType;
                    
                    Assert.assertEquals("Query type output format was not parsed correctly", "text/rdf+n3",
                            nextRdfOutputQueryType.getOutputRdfFormat());
                    
                    Assert.assertEquals(
                            "Query type output rdf n3 string was not parsed correctly",
                            "<${ntriplesEncoded_inputUrlEncoded_privatelowercase_normalisedStandardUri}> a <http://purl.org/queryall/query:QueryType>",
                            nextRdfOutputQueryType.getOutputString());
                    
                    
                    final ProcessorQueryType nextProcessorQueryType = (ProcessorQueryType)nextQueryType;
                    
                    
                    Assert.assertEquals("Query type template string was not parsed correctly",
                            "CONSTRUCT { ${normalisedStandardUri} ?p ?o . } WHERE { ${endpointSpecificUri} ?p ?o . }",
                            nextProcessorQueryType.getProcessingTemplateString());
                    
                    Assert.assertTrue("Query type was not parsed into a SparqlProcessorQueryType",
                            nextQueryType instanceof SparqlProcessorQueryType);
                    
                    final SparqlProcessorQueryType nextSparqlProcessorQueryType = (SparqlProcessorQueryType)nextQueryType;
                    
                    
                    Assert.assertEquals("Query type template string was not parsed correctly",
                            "CONSTRUCT { ${normalisedStandardUri} ?p ?o . } WHERE { ${endpointSpecificUri} ?p ?o . }",
                            nextSparqlProcessorQueryType.getSparqlTemplateString());
                    
                    Assert.assertEquals(nextProcessorQueryType.getProcessingTemplateString(), nextSparqlProcessorQueryType.getSparqlTemplateString());
                }
                else
                {
                    Assert.fail("Found a query type with a URI that we were not testing for nextQueryTypeUri="
                            + nextQueryTypeUri);
                }
                
                if(RdfUtilsTest.FAIL_ON_UNEXPECTED_TRIPLES)
                {
                    Assert.assertEquals("There were unexpected triples in the test file. This should not happen. "
                            + nextQueryType.getUnrecognisedStatements().toString(), 0, nextQueryType
                            .getUnrecognisedStatements().size());
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
            
            Assert.assertEquals(2, results.size());
            
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
                    
                    Assert.assertTrue(nextRuleTest instanceof StringRuleTest);
                    
                    final StringRuleTest nextRegexRuleTest = (StringRuleTest)nextRuleTest;
                    
                    Assert.assertEquals("RuleTest input string was not parsed correctly", "http://example.org/",
                            nextRegexRuleTest.getTestInputString());
                    
                    Assert.assertEquals("RuleTest output string was not parsed correctly", "http://otherexample.net/",
                            nextRegexRuleTest.getTestOutputString());
                }
                else if(nextRuleTestUri.equals(this.testRuleTestUri2))
                {
                    Assert.assertEquals("Results did not contain correct rule test URI", this.testRuleTestUri2,
                            nextRuleTestUri);
                    
                    Assert.assertEquals("RuleTest stages were not parsed correctly", 1, nextRuleTest.getStages().size());
                    
                    Assert.assertEquals("RuleTest rules were not parsed correctly", 1, nextRuleTest.getRuleUris()
                            .size());
                    
                    Assert.assertTrue("Sparql Rule test was not parsed into a SparqlRuleTest object",
                            nextRuleTest instanceof SparqlRuleTest);
                    
                    final SparqlRuleTest nextSparqlRuleTest = (SparqlRuleTest)nextRuleTest;
                    
                    Assert.assertTrue("Expected result was not parsed correctly",
                            nextSparqlRuleTest.getExpectedResult());
                    
                    Assert.assertEquals("Sparql Ask test query was not parsed correctly",
                            " ?bio2rdfUri <http://bio2rdf.org/bio2rdf_resource:dbxref> ?symbolUri . ",
                            nextSparqlRuleTest.getTestSparqlAsk());
                    
                    Assert.assertEquals("Test input mime type was not parsed correctly", "text/rdf+n3",
                            nextSparqlRuleTest.getTestInputMimeType());
                    
                    Assert.assertEquals(
                            "Test triple string was not parsed correctly",
                            " <http://bio2rdf.org/geneid:12334> <http://purl.org/science/owl/sciencecommons/ggp_has_primary_symbol> \"Capn2\" . ",
                            nextSparqlRuleTest.getTestInputTriples());
                }
                else
                {
                    Assert.fail("Found a rule test with a URI that we were not testing for nextRuleTestUri="
                            + nextRuleTestUri);
                }
                
                if(RdfUtilsTest.FAIL_ON_UNEXPECTED_TRIPLES)
                {
                    Assert.assertEquals("There were unexpected triples in the test file. This should not happen. "
                            + nextRuleTest.getUnrecognisedStatements(), 0, nextRuleTest.getUnrecognisedStatements()
                            .size());
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
