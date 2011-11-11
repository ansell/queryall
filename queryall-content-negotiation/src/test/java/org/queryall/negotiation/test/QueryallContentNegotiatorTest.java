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
        this.testContentNegotiator = null;
    }
    
    /**
     * Test method for
     * {@link org.queryall.negotiation.QueryallContentNegotiator#getContentNegotiator(java.lang.String)}
     * .
     * 
     * We prefer anything else over text/plain, due to the inadequacy of logic behind its naming as
     * the MIME type for NTriples
     * 
     */
    @Test
    public void testGetContentNegotiatorAnythingElseWithPreferredTextPlain()
    {
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/plain");
        
        // Test against the only standardised format so far, RDF/XML to verify that it is still
        // available when NTriples is preferred
        Assert.assertEquals("application/rdf+xml", this.testContentNegotiator.getBestMatch("application/rdf+xml")
                .getMediaType());
        
        // Verify the strategy for selecting NTriples if RDF/XML is also in the list, to discourage
        // use of the text/plain mime type, while still making it useful if they specifically choose
        // it
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("application/rdf+xml,text/plain")
                .getMediaType());
        
        // Test NTriples against other possibilities while it is not preferred to make sure it is
        // above all of the others as well
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,text/rdf+n3")
                .getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,text/n3").getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,application/rdf+n3")
                .getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,application/n3")
                .getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,text/turtle")
                .getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,application/turtle")
                .getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,application/x-turtle")
                .getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,text/html")
                .getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,application/html")
                .getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,application/xhtml+xml")
                .getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,application/json")
                .getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,application/rdf+json")
                .getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,application/ld+json")
                .getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,text/x-nquads")
                .getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,text/nquads")
                .getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,application/x-trig")
                .getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,application/trig")
                .getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain,application/trix")
                .getMediaType());
        
        Assert.assertEquals("text/plain", this.testContentNegotiator.getBestMatch("text/plain").getMediaType());
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.negotiation.QueryallContentNegotiator#getContentNegotiator(java.lang.String)}
     * .
     */
    @Test
    public void testGetContentNegotiatorN3WithPreferredRDFXML()
    {
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/rdf+xml");
        
        // Test against all of the defined alternate mime types for text/rdf+n3 when it is not
        // preferred
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3").getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/n3").getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("application/rdf+n3").getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("application/n3").getMediaType());
        
        Assert.assertEquals("text/rdf+n3",
                this.testContentNegotiator.getBestMatch("text/n3,application/n3,application/rdf+n3").getMediaType());
        
        Assert.assertEquals("text/rdf+n3",
                this.testContentNegotiator.getBestMatch("text/rdf+n3,text/n3,application/n3,application/rdf+n3")
                        .getMediaType());
        
        // Test N3 against other possibilities while it is not preferred to make sure it is second
        // in line
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3,text/turtle")
                .getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3,application/turtle")
                .getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3,application/x-turtle")
                .getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3,text/html")
                .getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3,application/html")
                .getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3,application/xhtml+xml")
                .getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3,application/json")
                .getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3,application/rdf+json")
                .getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3,application/ld+json")
                .getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3,text/x-nquads")
                .getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3,text/nquads")
                .getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3,application/x-trig")
                .getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3,application/trig")
                .getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3,application/trix")
                .getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3,text/plain")
                .getMediaType());
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.negotiation.QueryallContentNegotiator#getContentNegotiator(java.lang.String)}
     * .
     */
    @Test
    public void testGetContentNegotiatorPreferredN3()
    {
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/rdf+n3");
        
        // RDF/N3 as the default
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("*/*").getMediaType());
        
        // RDF/N3 specifically
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3").getMediaType());
        
        // Test two most used RDF formats against each other to make sure that application/rdf+xml
        // is not higher than N3 if it is not preferred
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("application/rdf+xml,text/rdf+n3")
                .getMediaType());
        
        // Test against all of the defined alternate mime types for text/rdf+n3 when it is preferred
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/rdf+n3").getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("text/n3").getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("application/rdf+n3").getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("application/n3").getMediaType());
        
        Assert.assertEquals("text/rdf+n3",
                this.testContentNegotiator.getBestMatch("text/n3,application/n3,application/rdf+n3").getMediaType());
        
        Assert.assertEquals("text/rdf+n3",
                this.testContentNegotiator.getBestMatch("text/rdf+n3,text/n3,application/n3,application/rdf+n3")
                        .getMediaType());
    }
    
    /**
     * Test method for
     * {@link org.queryall.negotiation.QueryallContentNegotiator#getContentNegotiator(java.lang.String)}
     * .
     */
    @Test
    public void testGetContentNegotiatorPreferredRDFXML()
    {
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/rdf+xml");
        
        // RDF/XML as the default
        Assert.assertEquals("application/rdf+xml", this.testContentNegotiator.getBestMatch("*/*").getMediaType());
        
        // RDF/XML specifically
        Assert.assertEquals("application/rdf+xml", this.testContentNegotiator.getBestMatch("application/rdf+xml")
                .getMediaType());
        
        // IMPORTANT: XML best match should be NON-EXISTENT to avoid showing RDF/XML to browsers who
        // really can't process it very well without XSLT
        Assert.assertNull(this.testContentNegotiator.getBestMatch("application/xml"));
        Assert.assertNull(this.testContentNegotiator.getBestMatch("text/xml"));
        
        // Test two most used RDF formats against each other
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,text/rdf+n3").getMediaType());
    }
    
    /**
     * Test method for
     * {@link org.queryall.negotiation.QueryallContentNegotiator#getContentNegotiator(java.lang.String)}
     * .
     * 
     * Test that RDF/XML is second in line when text/rdf+n3 is the default, except for text/n3 which
     * is acceptable as an alias
     */
    @Test
    public void testGetContentNegotiatorRDFXMLWithPreferredN3()
    {
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/rdf+n3");
        
        // Test against the only standardised format so far, RDF/XML to verify that it is still
        // available when RDF/N3 is preferred
        Assert.assertEquals("application/rdf+xml", this.testContentNegotiator.getBestMatch("application/rdf+xml")
                .getMediaType());
        
        // Verify the strategy for selecting RDF/N3 if RDF/XML is also in the list, to discourage
        // use of the application/*n3 mime types
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("application/rdf+xml,text/rdf+n3")
                .getMediaType());
        
        Assert.assertEquals("text/rdf+n3", this.testContentNegotiator.getBestMatch("application/rdf+xml,text/n3")
                .getMediaType());
        
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,application/rdf+n3").getMediaType());
        
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,application/n3").getMediaType());
        
        // Test RDFXML against other possibilities while it is not preferred to make sure it is
        // second in line after N3
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,text/turtle").getMediaType());
        
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,application/turtle").getMediaType());
        
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,application/x-turtle").getMediaType());
        
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,text/html").getMediaType());
        
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,application/html").getMediaType());
        
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,application/xhtml+xml").getMediaType());
        
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,application/json").getMediaType());
        
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,application/rdf+json").getMediaType());
        
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,application/ld+json").getMediaType());
        
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,text/x-nquads").getMediaType());
        
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,text/nquads").getMediaType());
        
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,application/x-trig").getMediaType());
        
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,application/trig").getMediaType());
        
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,application/trix").getMediaType());
        
        Assert.assertEquals("application/rdf+xml",
                this.testContentNegotiator.getBestMatch("application/rdf+xml,text/plain").getMediaType());
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.negotiation.QueryallContentNegotiator#getResponseContentType(java.lang.String, java.lang.String, de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator, java.lang.String)}
     * .
     */
    @Test
    public void testGetResponseContentTypeApplicationJson()
    {
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/json");
        
        Assert.assertEquals("application/json", QueryallContentNegotiator.getResponseContentType("application/json",
                "dummy-agent/1.0", this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/rdf+json");
        
        Assert.assertEquals("application/json", QueryallContentNegotiator.getResponseContentType("application/json",
                "dummy-agent/1.0", this.testContentNegotiator, "text/fake"));
        
        // make sure that JSON-LD is able to take over application/json if it is specifically
        // preferred
        // however, the returned type for JSON-LD is still application/ld+json even if
        // application/json is requested to match the vast majority of other cases
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/ld+json");
        
        Assert.assertEquals("application/ld+json", QueryallContentNegotiator.getResponseContentType("application/json",
                "dummy-agent/1.0", this.testContentNegotiator, "text/fake"));
        
    }
    
    /**
     * Tests the behaviour of the QueryallContentNegotiator against a Chrome default accept header:
     * 
     * http://my.opera.com/karlcow/blog/2011/03/03/wrong-to-be-right-with-xhtml
     * 
     * https://developer.mozilla.org/en/HTTP/Content_negotiation
     * 
     * http://www.useragentstring.com/pages/Chrome/
     * 
     */
    @Test
    public void testGetResponseContentTypeChromeDefault()
    {
        final String defaultChromeDesktopAccept =
                "application/xml,application/xhtml+xml,text/html;q=0.9, text/plain;q=0.8,image/png,*/*;q=0.5";
        
        this.testHtmlBrowserDefaults("Safari",
                "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.6 (KHTML, like Gecko) Chrome/16.0.897.0 Safari/535.6",
                defaultChromeDesktopAccept);
    }
    
    /**
     * Tests the behaviour of the QueryallContentNegotiator against the current Firefox default
     * accept header:
     * 
     * http://my.opera.com/karlcow/blog/2011/03/03/wrong-to-be-right-with-xhtml
     * 
     * text/html,application/xhtml+xml,application/xml;q=0.9, * / *;q=0.8
     */
    @Test
    public void testGetResponseContentTypeFirefoxDefault()
    {
        final String defaultFirefoxAccept = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
        
        this.testHtmlBrowserDefaults("Firefox", "Mozilla/5.0 (X11; Linux i686; rv:7.0.1) Gecko/20100101 Firefox/7.0.1",
                defaultFirefoxAccept);
    }
    
    /**
     * Tests the behaviour of the QueryallContentNegotiator against an IE default accept header:
     * 
     * http://my.opera.com/karlcow/blog/2011/03/03/wrong-to-be-right-with-xhtml
     * 
     * https://developer.mozilla.org/en/HTTP/Content_negotiation
     * 
     * http://www.useragentstring.com/pages/Internet%20Explorer/
     * 
     */
    @Test
    public void testGetResponseContentTypeIEDefault()
    {
        final String defaultIEDesktopAccept =
                "image/jpeg, application/x-ms-application, image/gif, application/xaml+xml, image/pjpeg, application/x-ms-xbap, application/x-shockwave-flash, application/msword, */*";
        
        this.testHtmlBrowserDefaults(
                "Internet Explorer",
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; Zune 4.0; InfoPath.3; MS-RTC LM 8; .NET4.0C; .NET4.0E)",
                defaultIEDesktopAccept);
    }
    
    /**
     * Test the behaviour of the QueryallContentNegotiator against a Jena default accept header
     * 
     * http://richard.cyganiak.de/blog/2008/03/what-is-your-rdf-browsers-accept-header/
     * 
     */
    @Test
    public void testGetResponseContentTypeJenaDefault()
    {
        final String defaultJenaAccept =
                "application/rdf+xml, application/xml; q=0.8, text/xml; q=0.7, application/rss+xml; q=0.3, */*; q=0.2";
        
        this.testRdfBrowserDefaults("Jena", "Java/6", defaultJenaAccept);
    }
    
    /**
     * Test the behaviour of the QueryallContentNegotiator against a OpenLink RDF Browser default
     * accept header
     * 
     * http://richard.cyganiak.de/blog/2008/03/what-is-your-rdf-browsers-accept-header/
     * 
     */
    @Test
    public void testGetResponseContentTypeOpenLinkDefault()
    {
        final String defaultOpenLinkAccept =
                "application/rdf+xml, text/rdf+n3, application/rdf+turtle, application/x-turtle, application/turtle, application/xml, */*";
        
        this.testRdfBrowserDefaults("OpenLink", "OpenLink/6", defaultOpenLinkAccept);
    }
    
    /**
     * Tests the behaviour of the QueryallContentNegotiator against the current Opera default accept
     * header:
     * 
     * http://my.opera.com/karlcow/blog/2011/03/03/wrong-to-be-right-with-xhtml
     * 
     * http://dev.opera.com/articles/view/opera-ua-string-changes/
     * 
     */
    @Test
    public void testGetResponseContentTypeOperaDesktopDefault()
    {
        final String defaultOperaDesktopAccept =
                "text/html, application/xml;q=0.9, application/xhtml+xml, image/png, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1";
        
        this.testHtmlBrowserDefaults("Opera",
                "Opera/9.80 (Macintosh; Intel Mac OS X; U; en) Presto/2.2.15 Version/10.00", defaultOperaDesktopAccept);
    }
    
    /**
     * Tests the behaviour of the QueryallContentNegotiator against a Safari default accept header:
     * 
     * http://my.opera.com/karlcow/blog/2011/03/03/wrong-to-be-right-with-xhtml
     * 
     * http://www.useragentstring.com/pages/Safari/
     * 
     */
    @Test
    public void testGetResponseContentTypeSafariDefault()
    {
        final String defaultSafariDesktopAccept =
                "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5";
        
        this.testHtmlBrowserDefaults(
                "Safari",
                "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_6; en-gb) AppleWebKit/533.20.25 (KHTML, like Gecko) Version/5.0.4 Safari/533.20.27",
                defaultSafariDesktopAccept);
    }
    
    /**
     * Test the behaviour of the QueryallContentNegotiator against a OpenLink RDF Browser default
     * accept header
     * 
     * http://richard.cyganiak.de/blog/2008/03/what-is-your-rdf-browsers-accept-header/
     * 
     */
    @Test
    public void testGetResponseContentTypeSindiceDefault()
    {
        final String defaultSindiceAccept = "application/rdf+xml, application/xml;q=0.6, text/xml;q=0.6";
        
        this.testRdfBrowserDefaults("Sindice", "SindiceBot/1.0", defaultSindiceAccept);
    }
    
    /**
     * Test method for
     * {@link org.queryall.negotiation.QueryallContentNegotiator#getResponseContentType(java.lang.String, java.lang.String, de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator, java.lang.String)}
     * .
     * 
     * Test that * / * accept headers always return the defined preferred content type
     */
    @Test
    public void testGetResponseContentTypeStarSlashStar()
    {
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/rdf+xml");
        
        Assert.assertEquals("application/rdf+xml", QueryallContentNegotiator.getResponseContentType("*/*",
                "dummy-agent/1.0", this.testContentNegotiator, "text/fake"));
        
        // If they mistakenly put in application/xml, the otherwise default, application/rdf+xml
        // comes through, but this is not by design
        // NOTE: This is ONLY a sanity check to make sure that something rational happens and the
        // unuseful fallback text/fake isn't used if they specify */*
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/xml");
        
        Assert.assertEquals("application/rdf+xml", QueryallContentNegotiator.getResponseContentType("*/*",
                "dummy-agent/1.0", this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/rdf+n3");
        
        Assert.assertEquals("text/rdf+n3", QueryallContentNegotiator.getResponseContentType("*/*", "dummy-agent/1.0",
                this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/turtle");
        
        Assert.assertEquals("text/turtle", QueryallContentNegotiator.getResponseContentType("*/*", "dummy-agent/1.0",
                this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/html");
        
        Assert.assertEquals("text/html", QueryallContentNegotiator.getResponseContentType("*/*", "dummy-agent/1.0",
                this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/json");
        
        Assert.assertEquals("application/json", QueryallContentNegotiator.getResponseContentType("*/*",
                "dummy-agent/1.0", this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/rdf+json");
        
        Assert.assertEquals("application/json", QueryallContentNegotiator.getResponseContentType("*/*",
                "dummy-agent/1.0", this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/ld+json");
        
        Assert.assertEquals("application/ld+json", QueryallContentNegotiator.getResponseContentType("*/*",
                "dummy-agent/1.0", this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/x-nquads");
        
        Assert.assertEquals("text/x-nquads", QueryallContentNegotiator.getResponseContentType("*/*", "dummy-agent/1.0",
                this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/x-trig");
        
        Assert.assertEquals("application/x-trig", QueryallContentNegotiator.getResponseContentType("*/*",
                "dummy-agent/1.0", this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/trix");
        
        Assert.assertEquals("application/trix", QueryallContentNegotiator.getResponseContentType("*/*",
                "dummy-agent/1.0", this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/plain");
        
        Assert.assertEquals("text/plain", QueryallContentNegotiator.getResponseContentType("*/*", "dummy-agent/1.0",
                this.testContentNegotiator, "text/fake"));
        
    }
    
    /**
     * Test the behaviour of the QueryallContentNegotiator against a Tabulator default accept header
     * 
     * http://richard.cyganiak.de/blog/2008/03/what-is-your-rdf-browsers-accept-header/
     * 
     */
    @Test
    public void testGetResponseContentTypeTabulatorDefault()
    {
        final String defaultTabulatorAccept =
                "application/rdf+xml, application/xhtml+xml;q=0.3, text/xml;q=0.2, application/xml;q=0.2, text/html;q=0.3, text/plain;q=0.1, text/n3, text/rdf+n3;q=0.5, application/x-turtle;q=0.2, text/turtle;q=1";
        
        this.testRdfBrowserDefaults("Tabulator", "Mozilla/5.0 (compatible)", defaultTabulatorAccept);
    }
    
    /**
     * Test to verify that our strategy returns text/html to any common HTML browser.
     * 
     * Also tests other possibilities, including application/xml which is troublesome for
     * application/rdf+xml versus application/xhtml+xml
     */
    private void testHtmlBrowserDefaults(final String browserName, final String typicalUserAgent,
            final String defaultAcceptHeader)
    {
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/html");
        
        Assert.assertEquals(browserName
                + " : failed to return text/html when it was the preferred display content type", "text/html",
                QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/fake"));
        
        // Test that requests for XHTML+XML go back as text/html for best compatibility with
        // different browsers,
        // even though the content type is possibly XHTML+RDFa
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/xhtml+xml");
        
        Assert.assertEquals(browserName
                + " : failed to return text/html when application/xhtml+xml was the preferred display content type",
                "text/html", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/fake"));
        
        // If they setup the preferred content type as application/xml, then we follow through on
        // their preferences by assuming they mean RDF/XML in preference to XHTML+RDFa
        // NOTE: the test for XHTML+RDFa if they explicitly select it is above
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/xml");
        
        Assert.assertEquals(
                browserName
                        + " : failed to return application/rdf+xml when application/xml was the preferred display content type",
                "application/rdf+xml", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader,
                        typicalUserAgent, this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/rdf+xml");
        
        Assert.assertEquals(
                browserName
                        + " : failed to return application/rdf+xml when application/rdf+xml was the preferred display content type",
                "application/rdf+xml", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader,
                        typicalUserAgent, this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/rdf+n3");
        
        Assert.assertEquals(browserName
                + " : failed to return text/rdf+n3 when text/rdf+n3 was the preferred display content type",
                "text/rdf+n3", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/turtle");
        
        Assert.assertEquals(browserName
                + " : failed to return text/turtle when text/turtle was the preferred display content type",
                "text/turtle", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/fake"));
        
        // Test that we return application/json for RDF/JSON but not for JSON-LD
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/json");
        
        Assert.assertEquals(browserName
                + " : failed to return application/json when application/json was the preferred display content type",
                "application/json", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader,
                        typicalUserAgent, this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/rdf+json");
        
        Assert.assertEquals(
                browserName
                        + " : failed to return application/json when application/rdf+json was the preferred display content type",
                "application/json", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader,
                        typicalUserAgent, this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/ld+json");
        
        Assert.assertEquals(
                browserName
                        + " : failed to return application/ld+json when application/ld+json was the preferred display content type",
                "application/ld+json", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader,
                        typicalUserAgent, this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/x-nquads");
        
        Assert.assertEquals(browserName
                + " : failed to return text/x-nquads when text/x-nquads was the preferred display content type",
                "text/x-nquads", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader,
                        typicalUserAgent, this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/x-trig");
        
        Assert.assertEquals(
                browserName
                        + " : failed to return application/x-trig when application/x-trig was the preferred display content type",
                "application/x-trig", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader,
                        typicalUserAgent, this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/trix");
        
        Assert.assertEquals(browserName
                + " : failed to return application/trix when application/trix was the preferred display content type",
                "application/trix", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader,
                        typicalUserAgent, this.testContentNegotiator, "text/fake"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/plain");
        
        Assert.assertEquals(browserName
                + " : failed to return text/plain when text/plain was the preferred display content type",
                "text/plain", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/fake"));
        
    }
    
    /**
     * Test to verify that our strategy returns an RDF format to any common RDF browser instead of
     * text/html, although that would also be useful if they can parse RDFa.
     * 
     * Also tests other possibilities, including application/xml which is troublesome for
     * application/rdf+xml versus application/xhtml+xml
     */
    private void testRdfBrowserDefaults(final String browserName, final String typicalUserAgent,
            final String defaultAcceptHeader)
    {
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/html");
        
        Assert.assertNotSame(
                browserName
                        + " : failed to return an RDF format to an RDF browser when text/html was the preferred display content type",
                "text/html", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/html"));
        
        // Test that requests for XHTML+XML go back as text/html for best compatibility with
        // different browsers,
        // even though the content type is possibly XHTML+RDFa
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/xhtml+xml");
        
        Assert.assertNotSame(
                browserName
                        + " : failed to return an RDF format to an RDF browser when application/xhtml+xml was the preferred display content type",
                "text/html", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/html"));
        
        // If they setup the preferred content type as application/xml, then we follow through on
        // their preferences by assuming they mean RDF/XML in preference to XHTML+RDFa
        // NOTE: the test for XHTML+RDFa if they explicitly select it is above
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/xml");
        
        Assert.assertNotSame(
                browserName
                        + " : failed to return an RDF format to an RDF browser when application/xml was the preferred display content type",
                "text/html", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/html"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/rdf+xml");
        
        Assert.assertNotSame(
                browserName
                        + " : failed to return an RDF format to an RDF browser when application/rdf+xml was the preferred display content type",
                "text/html", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/html"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/rdf+n3");
        
        Assert.assertNotSame(
                browserName
                        + " : failed to return an RDF format to an RDF browser when text/rdf+n3 was the preferred display content type",
                "text/html", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/html"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/turtle");
        
        Assert.assertNotSame(
                browserName
                        + " : failed to return an RDF format to an RDF browser when text/turtle was the preferred display content type",
                "text/html", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/html"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/json");
        
        Assert.assertNotSame(
                browserName
                        + " : failed to return an RDF format to an RDF browser when application/json was the preferred display content type",
                "text/html", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/html"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/rdf+json");
        
        Assert.assertNotSame(
                browserName
                        + " : failed to return an RDF format to an RDF browser when application/rdf+json was the preferred display content type",
                "text/html", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/html"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/ld+json");
        
        Assert.assertNotSame(
                browserName
                        + " : failed to return an RDF format to an RDF browser when application/ld+json was the preferred display content type",
                "text/html", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/html"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/x-nquads");
        
        Assert.assertNotSame(
                browserName
                        + " : failed to return an RDF format to an RDF browser when text/x-nquads was the preferred display content type",
                "text/html", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/html"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/x-trig");
        
        Assert.assertNotSame(
                browserName
                        + " : failed to return an RDF format to an RDF browser when application/x-trig was the preferred display content type",
                "text/html", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/html"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("application/trix");
        
        Assert.assertNotSame(
                browserName
                        + " : failed to return an RDF format to an RDF browser when application/trix was the preferred display content type",
                "text/html", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/html"));
        
        this.testContentNegotiator = QueryallContentNegotiator.getContentNegotiator("text/plain");
        
        Assert.assertNotSame(
                browserName
                        + " : failed to return an RDF format to an RDF browser when text/plain was the preferred display content type",
                "text/html", QueryallContentNegotiator.getResponseContentType(defaultAcceptHeader, typicalUserAgent,
                        this.testContentNegotiator, "text/html"));
        
    }
}
