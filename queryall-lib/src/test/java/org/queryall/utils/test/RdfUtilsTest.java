/**
 * 
 */
package org.queryall.utils.test;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.queryall.enumerations.Constants;
import org.queryall.utils.RdfUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfUtilsTest
{
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


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        testValueFactory = new ValueFactoryImpl();
        
        applicationRdfXml = "application/rdf+xml";
        textRdfN3 = "text/rdf+n3";
        textPlain = "text/plain";
        bogusContentType1 = "bogus1";
        bogusContentType2 = "bogus2";
        
        trueString = "true";
        falseString = "false";
        booleanDataType = "http://www.w3.org/2001/XMLSchema#boolean";
        booleanDataTypeUri = testValueFactory.createURI(booleanDataType);
        trueBooleanTypedLiteral = testValueFactory.createLiteral(trueString, booleanDataTypeUri);
        falseBooleanTypedLiteral = testValueFactory.createLiteral(falseString, booleanDataTypeUri);
        trueBooleanNativeLiteral = testValueFactory.createLiteral(true);
        falseBooleanNativeLiteral = testValueFactory.createLiteral(false);
        trueBooleanStringLiteral = testValueFactory.createLiteral(trueString);
        falseBooleanStringLiteral = testValueFactory.createLiteral(falseString);
        
        dateTimeDataType = "http://www.w3.org/2001/XMLSchema#dateTime";
        Calendar testDateCalendar = Constants.ISO8601UTC().getCalendar();
        testDateCalendar.set(2010, 01, 02, 03, 04, 05);
        testDate = testDateCalendar.getTime();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(testDate.getTime());
        DatatypeFactory df;
        try {
            df = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException dce) {
            throw new IllegalStateException(
                "Exception while obtaining DatatypeFactory instance", dce);
        }
        XMLGregorianCalendar xmlDate = df.newXMLGregorianCalendar(gc);
        testDateString = Constants.ISO8601UTC().format(testDate);
        testDateTypedLiteral = testValueFactory.createLiteral(testDateString, dateTimeDataType);
        testDateNativeLiteral = testValueFactory.createLiteral(xmlDate);
        testDateStringLiteral = testValueFactory.createLiteral(testDateString);

        floatDataType = "http://www.w3.org/2001/XMLSchema#float";
        testFloat = 0.278134f;
        testFloatTypedLiteral = testValueFactory.createLiteral(Float.toString(testFloat), floatDataType);
        testFloatNativeLiteral = testValueFactory.createLiteral(testFloat);
        testFloatStringLiteral = testValueFactory.createLiteral(Float.toString(testFloat));
        
        intDataType = "http://www.w3.org/2001/XMLSchema#int";
        testInt = 278134;
        testIntTypedLiteral = testValueFactory.createLiteral(Integer.toString(testInt), intDataType);
        testIntNativeLiteral = testValueFactory.createLiteral(testInt);
        testIntStringLiteral = testValueFactory.createLiteral(Integer.toString(testInt));

        longDataType = "http://www.w3.org/2001/XMLSchema#long";
        testLong = 278134965145L;
        testLongTypedLiteral = testValueFactory.createLiteral(Long.toString(testLong), longDataType);
        testLongNativeLiteral = testValueFactory.createLiteral(testLong);
        testLongStringLiteral = testValueFactory.createLiteral(Long.toString(testLong));
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        testValueFactory = null;
        
        applicationRdfXml = null;
        textRdfN3 = null;
        textPlain = null;
        bogusContentType1 = null;
        bogusContentType2 = null;
        
        trueString = null;
        falseString = null;
        booleanDataType = null;
        booleanDataTypeUri = null;

        trueBooleanTypedLiteral = null;
        falseBooleanTypedLiteral = null;
        
        trueBooleanNativeLiteral = null;
        falseBooleanNativeLiteral = null;

        trueBooleanStringLiteral = null;
        falseBooleanStringLiteral = null;
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#findBestContentType(java.lang.String, java.lang.String, java.lang.String)}.
     */
    @Test
    public void testFindBestContentTypeAgainst2BogusTypes()
    {
        assertEquals(applicationRdfXml, RdfUtils.findBestContentType(applicationRdfXml, bogusContentType1, bogusContentType2));
        assertEquals(applicationRdfXml, RdfUtils.findBestContentType(bogusContentType1, applicationRdfXml, bogusContentType2));
        assertEquals(applicationRdfXml, RdfUtils.findBestContentType(bogusContentType1, bogusContentType2, applicationRdfXml));

        assertEquals(textRdfN3, RdfUtils.findBestContentType(textRdfN3, bogusContentType1, bogusContentType2));
        assertEquals(textRdfN3, RdfUtils.findBestContentType(bogusContentType1, textRdfN3, bogusContentType2));
        assertEquals(textRdfN3, RdfUtils.findBestContentType(bogusContentType1, bogusContentType2, textRdfN3));

        assertEquals(textPlain, RdfUtils.findBestContentType(textPlain, bogusContentType1, bogusContentType2));
        assertEquals(textPlain, RdfUtils.findBestContentType(bogusContentType1, textPlain, bogusContentType2));
        assertEquals(textPlain, RdfUtils.findBestContentType(bogusContentType1, bogusContentType2, textPlain));
    
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#findBestContentType(java.lang.String, java.lang.String, java.lang.String)}.
     */
    @Test
    public void testFindBestContentTypeAgainst1BogusType()
    {
        assertEquals(applicationRdfXml, RdfUtils.findBestContentType(applicationRdfXml, bogusContentType1, textRdfN3));
        assertEquals(applicationRdfXml, RdfUtils.findBestContentType(bogusContentType1, applicationRdfXml, textRdfN3));
        assertEquals(textRdfN3, RdfUtils.findBestContentType(bogusContentType1, textRdfN3, applicationRdfXml));

        assertEquals(textRdfN3, RdfUtils.findBestContentType(textRdfN3, bogusContentType1, applicationRdfXml));
        assertEquals(textRdfN3, RdfUtils.findBestContentType(bogusContentType1, textRdfN3, applicationRdfXml));
        assertEquals(applicationRdfXml, RdfUtils.findBestContentType(bogusContentType1, applicationRdfXml, textRdfN3));

        assertEquals(textPlain, RdfUtils.findBestContentType(textPlain, bogusContentType1, applicationRdfXml));
        assertEquals(textPlain, RdfUtils.findBestContentType(bogusContentType1, textPlain, applicationRdfXml));
        assertEquals(applicationRdfXml, RdfUtils.findBestContentType(bogusContentType1, applicationRdfXml, textPlain));
    
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#findBestContentType(java.lang.String, java.lang.String, java.lang.String)}.
     */
    @Test
    public void testFindBestContentTypeWith3RealTypes()
    {
        assertEquals(applicationRdfXml, RdfUtils.findBestContentType(applicationRdfXml, textPlain, textRdfN3));
        assertEquals(textPlain, RdfUtils.findBestContentType(textPlain, applicationRdfXml, textRdfN3));
        assertEquals(textPlain, RdfUtils.findBestContentType(textPlain, textRdfN3, applicationRdfXml));

        assertEquals(textRdfN3, RdfUtils.findBestContentType(textRdfN3, textPlain, applicationRdfXml));
        assertEquals(textPlain, RdfUtils.findBestContentType(textPlain, textRdfN3, applicationRdfXml));
        assertEquals(textPlain, RdfUtils.findBestContentType(textPlain, applicationRdfXml, textRdfN3));

        assertEquals(textPlain, RdfUtils.findBestContentType(textPlain, textRdfN3, applicationRdfXml));
        assertEquals(textRdfN3, RdfUtils.findBestContentType(textRdfN3, textPlain, applicationRdfXml));
        assertEquals(textRdfN3, RdfUtils.findBestContentType(textRdfN3, applicationRdfXml, textPlain));
    
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getBooleanFromValue(org.openrdf.model.Value)}.
     */
    @Test
    public void testGetTypedBooleanFromValue()
    {
        assertTrue(RdfUtils.getBooleanFromValue(trueBooleanTypedLiteral));
        assertFalse(RdfUtils.getBooleanFromValue(falseBooleanTypedLiteral));
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getBooleanFromValue(org.openrdf.model.Value)}.
     */
    @Test
    public void testGetNativeBooleanFromValue()
    {
        assertTrue(RdfUtils.getBooleanFromValue(trueBooleanNativeLiteral));
        assertFalse(RdfUtils.getBooleanFromValue(falseBooleanNativeLiteral));
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getBooleanFromValue(org.openrdf.model.Value)}.
     */
    @Test
    public void testGetStringBooleanFromValue()
    {
        assertTrue(RdfUtils.getBooleanFromValue(trueBooleanStringLiteral));
        assertFalse(RdfUtils.getBooleanFromValue(falseBooleanStringLiteral));
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getDateTimeFromValue(org.openrdf.model.Value)}.
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
            assertEquals(testDate.getTime(), RdfUtils.getDateTimeFromValue(testDateTypedLiteral).getTime());
            assertEquals(testDate.getTime(), RdfUtils.getDateTimeFromValue(testDateNativeLiteral).getTime());
            assertEquals(testDate.getTime(), RdfUtils.getDateTimeFromValue(testDateStringLiteral).getTime());
        }
        catch(ParseException e)
        {
            fail("Found unexpected ParseException e="+e.getMessage());
        }
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getFloatFromValue(org.openrdf.model.Value)}.
     */
    @Test
    public void testGetFloatFromValue()
    {
        assertEquals(testFloat, RdfUtils.getFloatFromValue(testFloatTypedLiteral), 0.0000001);
        assertEquals(testFloat, RdfUtils.getFloatFromValue(testFloatNativeLiteral), 0.0000001);
        assertEquals(testFloat, RdfUtils.getFloatFromValue(testFloatStringLiteral), 0.0000001);
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getIntegerFromValue(org.openrdf.model.Value)}.
     */
    @Test
    public void testGetIntegerFromValue()
    {
        assertEquals(testInt, RdfUtils.getIntegerFromValue(testIntTypedLiteral));
        assertEquals(testInt, RdfUtils.getIntegerFromValue(testIntNativeLiteral));
        assertEquals(testInt, RdfUtils.getIntegerFromValue(testIntStringLiteral));
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getLongFromValue(org.openrdf.model.Value)}.
     */
    @Test
    public void testGetLongFromValue()
    {
        assertEquals(testLong, RdfUtils.getLongFromValue(testLongTypedLiteral));
        assertEquals(testLong, RdfUtils.getLongFromValue(testLongNativeLiteral));
        assertEquals(testLong, RdfUtils.getLongFromValue(testLongStringLiteral));
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getNamespaceEntries(org.openrdf.repository.Repository)}.
     */
    @Test
    @Ignore
    public void testGetNamespaceEntries()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getNormalisationRules(org.openrdf.repository.Repository)}.
     */
    @Test
    @Ignore
    public void testGetNormalisationRules()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getProfiles(org.openrdf.repository.Repository)}.
     */
    @Test
    @Ignore
    public void testGetProfiles()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getProviders(org.openrdf.repository.Repository)}.
     */
    @Test
    @Ignore
    public void testGetProviders()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getQueryTypes(org.openrdf.repository.Repository)}.
     */
    @Test
    @Ignore
    public void testGetQueryTypes()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getRuleTests(org.openrdf.repository.Repository)}.
     */
    @Test
    @Ignore
    public void testGetRuleTests()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getUTF8StringValueFromSesameValue(org.openrdf.model.Value)}.
     */
    @Test
    @Ignore
    public void testGetUTF8StringValueFromSesameValue()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getWriterFormat(java.lang.String)}.
     */
    @Test
    @Ignore
    public void testGetWriterFormat()
    {
        fail("Not yet implemented");
    }
    
}
