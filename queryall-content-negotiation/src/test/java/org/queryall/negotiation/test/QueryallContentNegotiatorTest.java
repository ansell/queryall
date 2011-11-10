/**
 * 
 */
package org.queryall.negotiation.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.queryall.negotiation.QueryallContentNegotiator;

import de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public class QueryallContentNegotiatorTest
{
    private ContentTypeNegotiator testContentNegotiator;
    
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
        testContentNegotiator = null;
    }
    
    /**
     * Test method for {@link org.queryall.negotiation.QueryallContentNegotiator#getContentNegotiator(java.lang.String)}.
     */
    @Test
    public void testGetContentNegotiatorPreferredRDFXML()
    {
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/rdf+xml");
        
        // RDF/XML as the default
        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("*/*").getMediaType());
        
        // RDF/XML specifically
        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml").getMediaType());
        
        // IMPORTANT: XML best match should be NON-EXISTENT to avoid showing RDF/XML to browsers who really can't process it very well without XSLT
        Assert.assertNull(testContentNegotiator.getBestMatch("application/xml"));
        Assert.assertNull(testContentNegotiator.getBestMatch("text/xml"));

        // Test two most used RDF formats against each other
        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,text/rdf+n3").getMediaType());
    }
        
    /**
     * Test method for {@link org.queryall.negotiation.QueryallContentNegotiator#getContentNegotiator(java.lang.String)}.
     */
    @Test
    public void testGetContentNegotiatorPreferredN3()
    {
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/rdf+n3");
        
        // RDF/N3 as the default
        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("*/*").getMediaType());
        
        // RDF/N3 specifically
        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3").getMediaType());
        
        // Test two most used RDF formats against each other to make sure that application/rdf+xml is not higher than N3 if it is not preferred
        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("application/rdf+xml,text/rdf+n3").getMediaType());

        // Test against all of the defined alternate mime types for text/rdf+n3 when it is preferred
        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/n3").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("application/rdf+n3").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("application/n3").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/n3,application/n3,application/rdf+n3").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3,text/n3,application/n3,application/rdf+n3").getMediaType());
    }
        
    /**
     * Test method for {@link org.queryall.negotiation.QueryallContentNegotiator#getContentNegotiator(java.lang.String)}.
     */
    @Test
    public void testGetContentNegotiatorN3WithPreferredRDFXML()
    {
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/rdf+xml");
        
        // Test against all of the defined alternate mime types for text/rdf+n3 when it is not preferred
        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/n3").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("application/rdf+n3").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("application/n3").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/n3,application/n3,application/rdf+n3").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3,text/n3,application/n3,application/rdf+n3").getMediaType());

        // Test N3 against other possibilities while it is not preferred to make sure it is second in line
        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3,text/turtle").getMediaType());
    
        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3,application/turtle").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3,application/x-turtle").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3,text/html").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3,application/html").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3,application/xhtml+xml").getMediaType());
        
        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3,application/json").getMediaType());
        
        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3,application/rdf+json").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3,application/ld+json").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3,text/x-nquads").getMediaType());
    
        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3,text/nquads").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3,application/x-trig").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3,application/trig").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3,application/trix").getMediaType());
        
        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("text/rdf+n3,text/plain").getMediaType());
        
    }
    
    /**
     * Test method for {@link org.queryall.negotiation.QueryallContentNegotiator#getContentNegotiator(java.lang.String)}.
     * 
     * Test that RDF/XML is second in line when text/rdf+n3 is the default, except for text/n3 which is acceptable as an alias
     */
    @Test
    public void testGetContentNegotiatorRDFXMLWithPreferredN3()
    {
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/rdf+n3");
        
        // Test against the only standardised format so far, RDF/XML to verify that it is still available when RDF/N3 is preferred
        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml").getMediaType());

        // Verify the strategy for selecting RDF/N3 if RDF/XML is also in the list, to discourage use of the application/*n3 mime types
        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("application/rdf+xml,text/rdf+n3").getMediaType());

        Assert.assertEquals("text/rdf+n3", testContentNegotiator.getBestMatch("application/rdf+xml,text/n3").getMediaType());

        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,application/rdf+n3").getMediaType());

        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,application/n3").getMediaType());

        // Test RDFXML against other possibilities while it is not preferred to make sure it is second in line after N3
        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,text/turtle").getMediaType());
    
        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,application/turtle").getMediaType());

        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,application/x-turtle").getMediaType());

        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,text/html").getMediaType());

        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,application/html").getMediaType());

        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,application/xhtml+xml").getMediaType());
        
        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,application/json").getMediaType());
        
        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,application/rdf+json").getMediaType());

        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,application/ld+json").getMediaType());

        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,text/x-nquads").getMediaType());
    
        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,text/nquads").getMediaType());

        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,application/x-trig").getMediaType());

        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,application/trig").getMediaType());

        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,application/trix").getMediaType());
        
        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml,text/plain").getMediaType());
        
    }
    
    /**
     * Test method for {@link org.queryall.negotiation.QueryallContentNegotiator#getContentNegotiator(java.lang.String)}.
     * 
     * We prefer anything else over text/plain, due to the inadequacy of logic behind its naming as the MIME type for NTriples
     * 
     */
    @Test
    public void testGetContentNegotiatorAnythingElseWithPreferredTextPlain()
    {
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/plain");
        
        // Test against the only standardised format so far, RDF/XML to verify that it is still available when NTriples is preferred
        Assert.assertEquals("application/rdf+xml", testContentNegotiator.getBestMatch("application/rdf+xml").getMediaType());

        // Verify the strategy for selecting NTriples if RDF/XML is also in the list, to discourage use of the text/plain mime type, while still making it useful if they specifically choose it
        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("application/rdf+xml,text/plain").getMediaType());

        // Test NTriples against other possibilities while it is not preferred to make sure it is above all of the others as well
        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,text/rdf+n3").getMediaType());

        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,text/n3").getMediaType());

        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,application/rdf+n3").getMediaType());

        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,application/n3").getMediaType());

        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,text/turtle").getMediaType());
    
        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,application/turtle").getMediaType());

        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,application/x-turtle").getMediaType());

        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,text/html").getMediaType());

        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,application/html").getMediaType());

        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,application/xhtml+xml").getMediaType());
        
        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,application/json").getMediaType());
        
        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,application/rdf+json").getMediaType());

        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,application/ld+json").getMediaType());

        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,text/x-nquads").getMediaType());
    
        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,text/nquads").getMediaType());

        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,application/x-trig").getMediaType());

        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,application/trig").getMediaType());

        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain,application/trix").getMediaType());
        
        Assert.assertEquals("text/plain", testContentNegotiator.getBestMatch("text/plain").getMediaType());
        
    }
    
    /**
     * Test method for {@link org.queryall.negotiation.QueryallContentNegotiator#getResponseContentType(java.lang.String, java.lang.String, de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator, java.lang.String)}.
     * 
     * Test that * / * accept headers always return the defined preferred content type
     */
    @Test
    public void testGetResponseContentTypeStarSlashStar()
    {
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/rdf+xml");
        
        Assert.assertEquals("application/rdf+xml", QueryallContentNegotiator.getResponseContentType("*/*", "dummy-agent/1.0", testContentNegotiator, "text/fake"));

        // If they mistakenly put in application/xml, the otherwise default, application/rdf+xml comes through, but this is not by design
        // NOTE: This is ONLY a sanity check to make sure that something rational happens and the unuseful fallback text/fake isn't used if they specify */*
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/xml");
        
        Assert.assertEquals("application/rdf+xml", QueryallContentNegotiator.getResponseContentType("*/*", "dummy-agent/1.0", testContentNegotiator, "text/fake"));

        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/rdf+n3");

        Assert.assertEquals("text/rdf+n3", QueryallContentNegotiator.getResponseContentType("*/*", "dummy-agent/1.0", testContentNegotiator, "text/fake"));
    
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/turtle");

        Assert.assertEquals("text/turtle", QueryallContentNegotiator.getResponseContentType("*/*", "dummy-agent/1.0", testContentNegotiator, "text/fake"));
    
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/html");

        Assert.assertEquals("text/html", QueryallContentNegotiator.getResponseContentType("*/*", "dummy-agent/1.0", testContentNegotiator, "text/fake"));
    
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/json");

        Assert.assertEquals("application/json", QueryallContentNegotiator.getResponseContentType("*/*", "dummy-agent/1.0", testContentNegotiator, "text/fake"));
    
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/rdf+json");

        Assert.assertEquals("application/json", QueryallContentNegotiator.getResponseContentType("*/*", "dummy-agent/1.0", testContentNegotiator, "text/fake"));
    
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/ld+json");

        Assert.assertEquals("application/ld+json", QueryallContentNegotiator.getResponseContentType("*/*", "dummy-agent/1.0", testContentNegotiator, "text/fake"));    

        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/x-nquads");

        Assert.assertEquals("text/x-nquads", QueryallContentNegotiator.getResponseContentType("*/*", "dummy-agent/1.0", testContentNegotiator, "text/fake"));    
    
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/x-trig");

        Assert.assertEquals("application/x-trig", QueryallContentNegotiator.getResponseContentType("*/*", "dummy-agent/1.0", testContentNegotiator, "text/fake"));    
    
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/trix");

        Assert.assertEquals("application/trix", QueryallContentNegotiator.getResponseContentType("*/*", "dummy-agent/1.0", testContentNegotiator, "text/fake"));    
    
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/plain");

        Assert.assertEquals("text/plain", QueryallContentNegotiator.getResponseContentType("*/*", "dummy-agent/1.0", testContentNegotiator, "text/fake"));    
    
    }
    
    /**
     * Test method for {@link org.queryall.negotiation.QueryallContentNegotiator#getResponseContentType(java.lang.String, java.lang.String, de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator, java.lang.String)}.
     */
    @Test
    public void testGetResponseContentTypeApplicationJson()
    {
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/json");
        
        Assert.assertEquals("application/json", QueryallContentNegotiator.getResponseContentType("application/json", "dummy-agent/1.0", testContentNegotiator, "text/fake"));
    
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/rdf+json");

        Assert.assertEquals("application/json", QueryallContentNegotiator.getResponseContentType("application/json", "dummy-agent/1.0", testContentNegotiator, "text/fake"));
    
        // make sure that JSON-LD is able to take over application/json if it is specifically preferred
        // however, the returned type for JSON-LD is still application/ld+json even if application/json is requested to match the vast majority of other cases
        testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/ld+json");

        Assert.assertEquals("application/ld+json", QueryallContentNegotiator.getResponseContentType("application/json", "dummy-agent/1.0", testContentNegotiator, "text/fake"));
    
    }
}
