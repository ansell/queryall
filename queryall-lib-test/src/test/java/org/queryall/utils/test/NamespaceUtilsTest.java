/**
 * 
 */
package org.queryall.utils.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.After;
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
        allNamespacesByPrefix = new HashMap<String, Collection<URI>>();
        
        testPrefix1 = "test-prefix-1";
        testPrefix2 = "test-prefix-2";
        testPrefix3 = "test-prefix-3";
        
        testNamespaceUri1a = new URIImpl("http://test.example.org/test/namesapce/1a");
        testNamespaceUri1b = new URIImpl("http://test.example.org/test/namesapce/1b");
        
        testNamespaceUri2a = new URIImpl("http://test.example.org/test/namesapce/2a");
        testNamespaceUri2b = new URIImpl("http://test.example.org/test/namesapce/2b");
        
        testList1 = new ArrayList<URI>(1);
        testList1.add(testNamespaceUri1a);
        
        // one URI for one prefix, with the other designed to be unrecognised
        allNamespacesByPrefix.put(testPrefix1, testList1);
        
        testList2 = new ArrayList<URI>(2);
        testList2.add(testNamespaceUri2a);
        testList2.add(testNamespaceUri2b);
        
        // two URIs for one prefix
        allNamespacesByPrefix.put(testPrefix2, testList2);

        testList3 = Collections.emptyList();
        
        // empty list to verify that testPrefix3 returns no results
        allNamespacesByPrefix.put(testPrefix3, testList3);
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        allNamespacesByPrefix = null;
        
        testPrefix1 = null;
        testPrefix2 = null;
        testPrefix3 = null;
        
        testNamespaceUri1a = null;
        testNamespaceUri1b = null;
        
        testNamespaceUri2a = null;
        testNamespaceUri2b = null;
        
        testList1 = null;
    }
    
    /**
     * Test method for {@link org.queryall.utils.NamespaceUtils#getNamespaceUrisForPrefix(java.util.Map, java.lang.String)}.
     */
    @Test
    public final void testGetNamespaceUrisForPrefixSingle()
    {
        Collection<URI> namespaceUrisForPrefix = NamespaceUtils.getNamespaceUrisForPrefix(allNamespacesByPrefix, testPrefix1);
        
        Assert.assertNotNull(namespaceUrisForPrefix);
        Assert.assertTrue(namespaceUrisForPrefix.size() > 0);
        
        Assert.assertEquals(1, namespaceUrisForPrefix.size());
        
        Assert.assertTrue(namespaceUrisForPrefix.contains(testNamespaceUri1a));
        Assert.assertFalse(namespaceUrisForPrefix.contains(testNamespaceUri1b));

        Assert.assertFalse(namespaceUrisForPrefix.contains(testNamespaceUri2a));
        Assert.assertFalse(namespaceUrisForPrefix.contains(testNamespaceUri2b));
    }
    
    /**
     * Test method for {@link org.queryall.utils.NamespaceUtils#getNamespaceUrisForPrefix(java.util.Map, java.lang.String)}.
     */
    @Test
    public final void testGetNamespaceUrisForPrefixMultiple()
    {
        Collection<URI> namespaceUrisForPrefix = NamespaceUtils.getNamespaceUrisForPrefix(allNamespacesByPrefix, testPrefix2);
        
        Assert.assertNotNull(namespaceUrisForPrefix);
        Assert.assertTrue(namespaceUrisForPrefix.size() > 0);
        
        Assert.assertEquals(2, namespaceUrisForPrefix.size());
        
        Assert.assertFalse(namespaceUrisForPrefix.contains(testNamespaceUri1a));
        Assert.assertFalse(namespaceUrisForPrefix.contains(testNamespaceUri1b));

        Assert.assertTrue(namespaceUrisForPrefix.contains(testNamespaceUri2a));
        Assert.assertTrue(namespaceUrisForPrefix.contains(testNamespaceUri2b));
    }
    
    /**
     * Test method for {@link org.queryall.utils.NamespaceUtils#getNamespaceUrisForPrefix(java.util.Map, java.lang.String)}.
     */
    @Test
    public final void testGetNamespaceUrisForPrefixNone()
    {
        Collection<URI> namespaceUrisForPrefix = NamespaceUtils.getNamespaceUrisForPrefix(allNamespacesByPrefix, testPrefix3);
        
        Assert.assertNotNull(namespaceUrisForPrefix);
        Assert.assertEquals(0, namespaceUrisForPrefix.size());
        
        Assert.assertFalse(namespaceUrisForPrefix.contains(testNamespaceUri1a));
        Assert.assertFalse(namespaceUrisForPrefix.contains(testNamespaceUri1b));

        Assert.assertFalse(namespaceUrisForPrefix.contains(testNamespaceUri2a));
        Assert.assertFalse(namespaceUrisForPrefix.contains(testNamespaceUri2b));
    }
    
}
