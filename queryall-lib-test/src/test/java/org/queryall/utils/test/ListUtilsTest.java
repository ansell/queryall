/**
 * 
 */
package org.queryall.utils.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.queryall.utils.ListUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public class ListUtilsTest
{
    
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
     * Test method for {@link org.queryall.utils.ListUtils#chooseRandomItemFromArray(T[])}.
     */
    @Test
    public final void testChooseRandomItemFromArrayBasicObject()
    {
        Object[] objectArray = new Object[3000];
        
        for(int i = 0; i < objectArray.length; i++)
        {
            objectArray[i] = new Object();
        }
        
        Assert.assertNotNull(ListUtils.chooseRandomItemFromArray(objectArray));
    }
    
    /**
     * Test method for {@link org.queryall.utils.ListUtils#chooseRandomItemFromArray(T[])}.
     */
    @Test
    public final void testChooseRandomItemFromArrayString()
    {
        String[] objectArray = new String[3000];
        
        for(int i = 0; i < objectArray.length; i++)
        {
            objectArray[i] = "test"+String.valueOf(i);
        }
        
        String testResult = ListUtils.chooseRandomItemFromArray(objectArray);
        
        Assert.assertNotNull(testResult);
        Assert.assertTrue(testResult.startsWith("test"));
    }
    
    /**
     * Test method for {@link org.queryall.utils.ListUtils#chooseRandomItemFromCollection(java.util.Collection)}.
     */
    @Test
    public final void testChooseRandomItemFromCollectionCollectionOfObject()
    {
        Collection<Object> objectCollection = new HashSet<Object>();
        
        for(int i = 0; i < 3000; i++)
        {
            objectCollection.add(new Object());
        }
        
        Assert.assertNotNull(ListUtils.chooseRandomItemFromCollection(objectCollection));
    }
    
    /**
     * Test method for {@link org.queryall.utils.ListUtils#chooseRandomItemFromCollection(java.util.Collection)}.
     */
    @Test
    public final void testChooseRandomItemFromCollectionCollectionOfString()
    {
        Collection<String> stringCollection = new HashSet<String>();
        
        for(int i = 0; i < 3000; i++)
        {
            stringCollection.add("test"+String.valueOf(i));
        }
        
        String testResult = ListUtils.chooseRandomItemFromCollection(stringCollection);
        
        Assert.assertNotNull(testResult);
        Assert.assertTrue(testResult.startsWith("test"));
    }
    
    /**
     * Test method for {@link org.queryall.utils.ListUtils#chooseRandomItemFromList(java.util.List)}.
     */
    @Test
    public final void testChooseRandomItemFromCollectionListOfObject()
    {
        List<Object> objectList = new ArrayList<Object>(3000);
        
        for(int i = 0; i < 3000; i++)
        {
            objectList.add(new Object());
        }
        
        Assert.assertNotNull(ListUtils.chooseRandomItemFromList(objectList));
    }
    
    /**
     * Test method for {@link org.queryall.utils.ListUtils#chooseRandomItemFromList(java.util.List)}.
     */
    @Test
    public final void testChooseRandomItemFromCollectionListOfString()
    {
        List<String> stringList = new ArrayList<String>(3000);
        
        for(int i = 0; i < 3000; i++)
        {
            stringList.add("test"+String.valueOf(i));
        }
        
        String testResult = ListUtils.chooseRandomItemFromList(stringList);
        
        Assert.assertNotNull(testResult);
        Assert.assertTrue(testResult.startsWith("test"));
    }
    
    /**
     * Test method for {@link org.queryall.utils.ListUtils#collectionContainsStringIgnoreCase(java.util.Collection, java.lang.String)}.
     */
    @Test
    public final void testCollectionContainsStringIgnoreCase()
    {
        List<String> stringList = new ArrayList<String>(3000);
        
        for(int i = 0; i < 3000; i++)
        {
            stringList.add("test"+String.valueOf(i));
        }
        
        Assert.assertTrue(ListUtils.collectionContainsStringIgnoreCase(stringList, "test0"));
        
        Assert.assertTrue(ListUtils.collectionContainsStringIgnoreCase(stringList, "test2000"));
        
        Assert.assertFalse(ListUtils.collectionContainsStringIgnoreCase(stringList, "test3000"));

        Assert.assertTrue(ListUtils.collectionContainsStringIgnoreCase(stringList, "TeSt0"));
        
        Assert.assertTrue(ListUtils.collectionContainsStringIgnoreCase(stringList, "tEsT2000"));
        
        Assert.assertFalse(ListUtils.collectionContainsStringIgnoreCase(stringList, "TesT3000"));
    }
    
    /**
     * Test method for {@link org.queryall.utils.ListUtils#collectionContainsStringIgnoreCase(java.util.Collection, java.lang.String)}.
     */
    @Test
    public final void testCollectionContainsStringIgnoreCaseEmpty()
    {
        List<String> stringList = new ArrayList<String>(3000);
        
        Assert.assertFalse(ListUtils.collectionContainsStringIgnoreCase(stringList, "test0"));
        
        Assert.assertFalse(ListUtils.collectionContainsStringIgnoreCase(stringList, "test2000"));
        
        Assert.assertFalse(ListUtils.collectionContainsStringIgnoreCase(stringList, "test3000"));

        Assert.assertFalse(ListUtils.collectionContainsStringIgnoreCase(stringList, "TeSt0"));
        
        Assert.assertFalse(ListUtils.collectionContainsStringIgnoreCase(stringList, "tEsT2000"));
        
        Assert.assertFalse(ListUtils.collectionContainsStringIgnoreCase(stringList, "TesT3000"));
    }
    
    /**
     * Test method for {@link org.queryall.utils.ListUtils#getIntArrayFromArrayInteger(java.lang.Integer[])}.
     */
    @Ignore
    @Test
    public final void testGetIntArrayFromArrayInteger()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.utils.ListUtils#listFromStringArray(java.lang.String[])}.
     */
    @Ignore
    @Test
    public final void testListFromStringArray()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.utils.ListUtils#randomiseCollectionLayout(java.util.Collection)}.
     */
    @Test
    public final void testRandomiseCollectionLayoutHashSet()
    {
        Collection<String> testCollection = new HashSet<String>();
        List<String> referenceCollection = new ArrayList<String>();
        
        for(int i = 0; i < 3000; i++)
        {
            referenceCollection.add("test"+String.valueOf(i));
        }
        
        for(String nextString : referenceCollection)
        {
            testCollection.add(nextString);
        }
        
        List<String> result = ListUtils.randomiseCollectionLayout(testCollection);
        
        Assert.assertNotNull(result);
        Assert.assertEquals(3000, result.size());
        
        int matchingCount = 0;
        
        for(int i = 0; i < 3000; i++)
        {
            if(result.get(i).equals(referenceCollection.get(i)))
            {
                matchingCount++;
            }
        }
        
        Assert.assertTrue("More than 1000 of the 3000 test strings were not moved by the randomisation", matchingCount < 1000);
    }
    
    /**
     * Test method for {@link org.queryall.utils.ListUtils#randomiseCollectionLayout(java.util.Collection)}.
     */
    @Test
    public final void testRandomiseCollectionLayoutLinkedHashSet()
    {
        Collection<String> testCollection = new LinkedHashSet<String>();
        List<String> referenceCollection = new ArrayList<String>();
        
        for(int i = 0; i < 3000; i++)
        {
            referenceCollection.add("test"+String.valueOf(i));
        }
        
        for(String nextString : referenceCollection)
        {
            testCollection.add(nextString);
        }
        
        List<String> result = ListUtils.randomiseCollectionLayout(testCollection);
        
        Assert.assertNotNull(result);
        Assert.assertEquals(3000, result.size());
        
        int matchingCount = 0;
        
        for(int i = 0; i < 3000; i++)
        {
            if(result.get(i).equals(referenceCollection.get(i)))
            {
                matchingCount++;
            }
        }
        
        Assert.assertTrue("More than 1000 of the 3000 test strings were not moved by the randomisation", matchingCount < 1000);
    }
    
    /**
     * Test method for {@link org.queryall.utils.ListUtils#randomiseListLayout(java.util.List)}.
     */
    @Test
    public final void testRandomiseListLayoutLinkedList()
    {
        List<String> testCollection = new LinkedList<String>();
        List<String> referenceCollection = new ArrayList<String>();
        
        for(int i = 0; i < 3000; i++)
        {
            referenceCollection.add("test"+String.valueOf(i));
        }
        
        for(String nextString : referenceCollection)
        {
            testCollection.add(nextString);
        }
        
        List<String> result = ListUtils.randomiseListLayout(testCollection);
        
        Assert.assertNotNull(result);
        Assert.assertEquals(3000, result.size());
        
        int matchingCount = 0;
        
        for(int i = 0; i < 3000; i++)
        {
            if(result.get(i).equals(referenceCollection.get(i)))
            {
                matchingCount++;
            }
        }
        
        Assert.assertTrue("More than 1000 of the 3000 test strings were not moved by the randomisation", matchingCount < 1000);
    }
    
    /**
     * Test method for {@link org.queryall.utils.ListUtils#randomiseListLayout(java.util.List)}.
     */
    @Test
    public final void testRandomiseListLayoutArrayList()
    {
        List<String> testCollection = new ArrayList<String>();
        List<String> referenceCollection = new ArrayList<String>();
        
        for(int i = 0; i < 3000; i++)
        {
            referenceCollection.add("test"+String.valueOf(i));
        }
        
        for(String nextString : referenceCollection)
        {
            testCollection.add(nextString);
        }
        
        List<String> result = ListUtils.randomiseListLayout(testCollection);
        
        Assert.assertNotNull(result);
        Assert.assertEquals(3000, result.size());
        
        int matchingCount = 0;
        
        for(int i = 0; i < 3000; i++)
        {
            if(result.get(i).equals(referenceCollection.get(i)))
            {
                matchingCount++;
            }
        }
        
        Assert.assertTrue("More than 1000 of the 3000 test strings were not moved by the randomisation", matchingCount < 1000);
    }
    
}
