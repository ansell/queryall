/**
 * 
 */
package org.queryall.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ListUtils
{
    private static final Logger log = Logger.getLogger(StringUtils.class.getName());
    @SuppressWarnings("unused")
    private static final boolean _TRACE = ListUtils.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = ListUtils.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = ListUtils.log.isInfoEnabled();
    
    /**
     * A prng that can be used in this class to generate random numbers
     */
    public static final Random prng = new Random();
    
    /**
     * @param <T>
     * @param newArray
     * @return
     */
    public static <T> T chooseRandomItemFromArray(final T[] newArray)
    {
        // T result = null;
        
        if(newArray.length == 1)
        {
            return newArray[0];
        }
        else if(newArray.length > 1)
        {
            return newArray[ListUtils.prng.nextInt(newArray.length)];
        }
        
        return null;
    }
    
    /**
     * @param <T>
     * @param newList
     * @return
     */
    public static <T> T chooseRandomItemFromCollection(final Collection<T> newList)
    {
        // T result = null;
        final int listSize = newList.size();
        
        if(listSize > 0)
        {
            // then randomly select one from the list to use...
            final int index = (listSize == 1 ? 0 : ListUtils.prng.nextInt(listSize));
            
            int tempPosition = 0;
            
            for(final T nextThing : newList)
            {
                if(index == tempPosition)
                {
                    return nextThing;
                }
                
                tempPosition++;
            }
        }
        
        return null;
    }
    
    /**
     * @param <T>
     * @param newList
     * @return
     */
    public static <T> T chooseRandomItemFromCollection(final List<T> newList)
    {
        // T result = null;
        
        final int listSize = newList.size();
        
        if(listSize == 1)
        {
            return newList.get(0);
        }
        else if(listSize > 1)
        {
            // then randomly select one from the list to use...
            return newList.get(ListUtils.prng.nextInt(listSize));
        }
        
        return null;
    }
    
    /**
     * 
     * @param stringCollection
     * @param searchString
     * @return
     */
    public static boolean collectionContainsStringIgnoreCase(final Collection<String> stringCollection,
            String searchString)
    {
        searchString = searchString.toLowerCase(Locale.ENGLISH);
        
        if(stringCollection == null)
        {
            return false;
        }
        
        for(final String nextString : stringCollection)
        {
            if(nextString.toLowerCase(Locale.ENGLISH).contains(searchString))
            {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * @param nextInputIntegers
     * @return
     */
    public static int[] getIntArrayFromArrayInteger(final Integer[] nextInputIntegers)
    {
        final int[] result = new int[nextInputIntegers.length];
        
        for(int i = 0; i < nextInputIntegers.length; i++)
        {
            result[i] = nextInputIntegers[i];
        }
        
        return result;
    }
    
    /**
     * @param nextArray
     * @return
     */
    public static Collection<String> listFromStringArray(final String[] nextArray)
    {
        final Collection<String> results = new ArrayList<String>(nextArray.length);
        
        for(final String nextString : nextArray)
        {
            results.add(nextString);
        }
        
        return results;
    }
    
    /**
     * @param <T>
     * @param newList
     * @return
     */
    public static <T> List<T> randomiseListLayout(final Collection<T> newList)
    {
        if(newList.size() <= 1 && newList instanceof List)
        {
            return (List<T>)newList;
        }
        
        final List<T> resultList = new ArrayList<T>(newList.size());
        
        for(final T nextItem : newList)
        {
            resultList.add(nextItem);
        }
        
        return ListUtils.randomiseListLayout(resultList);
    }
    
    /**
     * @param newList
     * @return
     */
    public static <T> List<T> randomiseListLayout(final List<T> newList)
    {
        if(newList.size() <= 1)
        {
            return newList;
        }
        
        final List<T> resultList = new ArrayList<T>(newList.size());
        
        int nextPosition;
        
        while(newList.size() > 0)
        {
            // then randomly select one from the list to use as the next element
            // in the resultList
            nextPosition = ListUtils.prng.nextInt(newList.size());
            resultList.add(newList.get(nextPosition));
            newList.remove(nextPosition);
        }
        
        return resultList;
    }
}
