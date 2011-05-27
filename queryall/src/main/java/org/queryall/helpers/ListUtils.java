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
    private static final Logger log = Logger.getLogger(StringUtils.class
            .getName());
    @SuppressWarnings("unused")
    private static final boolean _TRACE = log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = log.isInfoEnabled();
    
    /**
     * A prng that can be used in this class to generate random numbers
     */
    public static final Random prng = new Random();

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
        
        for(T nextItem : newList)
        {
            resultList.add(nextItem);
        }
        
        return randomiseListLayout(resultList);
    }

    /**
     * @param nextArray
     * @return
     */
    public static Collection<String> listFromStringArray(String[] nextArray)
    {
        final Collection<String> results = new ArrayList<String>(nextArray.length);
        
        for(final String nextString : nextArray)
        {
            results.add(nextString);
        }
        
        return results;
    }

    /**
     * @param nextInputIntegers
     * @return
     */
    public static int[] getIntArrayFromArrayInteger(Integer[] nextInputIntegers)
    {
        final int[] result = new int[nextInputIntegers.length];
        
        for(int i = 0; i < nextInputIntegers.length; i++)
        {
            result[i] = nextInputIntegers[i];
        }
        
        return result;
    }

    /**
     * @param <T>
     * @param newList
     * @return
     */
    public static <T> T chooseRandomItemFromCollection(List<T> newList)
    {
        // T result = null;
        
        int listSize = newList.size();
        
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
     * @param <T>
     * @param newList
     * @return
     */
    public static <T> T chooseRandomItemFromCollection(Collection<T> newList)
    {
        // T result = null;
        int listSize = newList.size();
        
        if(listSize > 0)
        {
            // then randomly select one from the list to use...
            int index = (listSize == 1 ? 0 : ListUtils.prng.nextInt(listSize));
            
            int tempPosition = 0;
            
            for(T nextThing : newList)
            {
                if(index == tempPosition)
                    return nextThing;
                
                tempPosition++;
            }
        }
        
        return null;
    }

    /**
     * @param <T>
     * @param newArray
     * @return
     */
    public static <T> T chooseRandomItemFromArray(T[] newArray)
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
     * 
     * @param stringCollection
     * @param searchString
     * @return
     */
    public static boolean collectionContainsStringIgnoreCase(Collection<String> stringCollection, String searchString)
    {
    	searchString = searchString.toLowerCase(Locale.ENGLISH);

    	if(stringCollection == null)
    	{
    		return false;
    	}
    	
    	for(String nextString : stringCollection)
    	{
    		if(nextString.toLowerCase(Locale.ENGLISH).contains(searchString))
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }
}
