/**
 * 
 */
package org.queryall.utils.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.queryall.utils.NamespaceUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class NamespaceUtilsTest
{
    private Map<String, Collection<URI>> allNamespacesByPrefix;
    
    private String testPrefix1;
    private String testPrefix2;
    private String testPrefix3;
    
    private URI testNamespaceUri1a;
    private URI testNamespaceUri1b;
    
    private URI testNamespaceUri2a;
    private URI testNamespaceUri2b;
    
    private Collection<URI> testList1;
    
    private Collection<URI> testList2;
    
    private Collection<URI> testList3;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.allNamespacesByPrefix = new HashMap<String, Collection<URI>>();
        
        this.testPrefix1 = "test-prefix-1";
        this.testPrefix2 = "test-prefix-2";
        this.testPrefix3 = "test-prefix-3";
        
        this.testNamespaceUri1a = new URIImpl("http://test.example.org/test/namesapce/1a");
        this.testNamespaceUri1b = new URIImpl("http://test.example.org/test/namesapce/1b");
        
        this.testNamespaceUri2a = new URIImpl("http://test.example.org/test/namesapce/2a");
        this.testNamespaceUri2b = new URIImpl("http://test.example.org/test/namesapce/2b");
        
        this.testList1 = new ArrayList<URI>(1);
        this.testList1.add(this.testNamespaceUri1a);
        
        // one URI for one prefix, with the other designed to be unrecognised
        this.allNamespacesByPrefix.put(this.testPrefix1, this.testList1);
        
        this.testList2 = new ArrayList<URI>(2);
        this.testList2.add(this.testNamespaceUri2a);
        this.testList2.add(this.testNamespaceUri2b);
        
        // two URIs for one prefix
        this.allNamespacesByPrefix.put(this.testPrefix2, this.testList2);
        
        this.testList3 = Collections.emptyList();
        
        // empty list to verify that testPrefix3 returns no results
        this.allNamespacesByPrefix.put(this.testPrefix3, this.testList3);
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.allNamespacesByPrefix = null;
        
        this.testPrefix1 = null;
        this.testPrefix2 = null;
        this.testPrefix3 = null;
        
        this.testNamespaceUri1a = null;
        this.testNamespaceUri1b = null;
        
        this.testNamespaceUri2a = null;
        this.testNamespaceUri2b = null;
        
        this.testList1 = null;
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.NamespaceUtils#getNamespaceUrisForPrefix(java.util.Map, java.lang.String)}
     * .
     */
    @Test
    public final void testGetNamespaceUrisForPrefixMultiple()
    {
        final Collection<URI> namespaceUrisForPrefix =
                NamespaceUtils.getNamespaceUrisForPrefix(this.allNamespacesByPrefix, this.testPrefix2);
        
        Assert.assertNotNull(namespaceUrisForPrefix);
        Assert.assertTrue(namespaceUrisForPrefix.size() > 0);
        
        Assert.assertEquals(2, namespaceUrisForPrefix.size());
        
        Assert.assertFalse(namespaceUrisForPrefix.contains(this.testNamespaceUri1a));
        Assert.assertFalse(namespaceUrisForPrefix.contains(this.testNamespaceUri1b));
        
        Assert.assertTrue(namespaceUrisForPrefix.contains(this.testNamespaceUri2a));
        Assert.assertTrue(namespaceUrisForPrefix.contains(this.testNamespaceUri2b));
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.NamespaceUtils#getNamespaceUrisForPrefix(java.util.Map, java.lang.String)}
     * .
     */
    @Test
    public final void testGetNamespaceUrisForPrefixNone()
    {
        final Collection<URI> namespaceUrisForPrefix =
                NamespaceUtils.getNamespaceUrisForPrefix(this.allNamespacesByPrefix, this.testPrefix3);
        
        Assert.assertNotNull(namespaceUrisForPrefix);
        Assert.assertEquals(0, namespaceUrisForPrefix.size());
        
        Assert.assertFalse(namespaceUrisForPrefix.contains(this.testNamespaceUri1a));
        Assert.assertFalse(namespaceUrisForPrefix.contains(this.testNamespaceUri1b));
        
        Assert.assertFalse(namespaceUrisForPrefix.contains(this.testNamespaceUri2a));
        Assert.assertFalse(namespaceUrisForPrefix.contains(this.testNamespaceUri2b));
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.NamespaceUtils#getNamespaceUrisForPrefix(java.util.Map, java.lang.String)}
     * .
     */
    @Test
    public final void testGetNamespaceUrisForPrefixSingle()
    {
        final Collection<URI> namespaceUrisForPrefix =
                NamespaceUtils.getNamespaceUrisForPrefix(this.allNamespacesByPrefix, this.testPrefix1);
        
        Assert.assertNotNull(namespaceUrisForPrefix);
        Assert.assertTrue(namespaceUrisForPrefix.size() > 0);
        
        Assert.assertEquals(1, namespaceUrisForPrefix.size());
        
        Assert.assertTrue(namespaceUrisForPrefix.contains(this.testNamespaceUri1a));
        Assert.assertFalse(namespaceUrisForPrefix.contains(this.testNamespaceUri1b));
        
        Assert.assertFalse(namespaceUrisForPrefix.contains(this.testNamespaceUri2a));
        Assert.assertFalse(namespaceUrisForPrefix.contains(this.testNamespaceUri2b));
    }
    
}
