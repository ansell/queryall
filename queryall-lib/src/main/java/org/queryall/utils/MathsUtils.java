/**
 * 
 */
package org.queryall.utils;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class MathsUtils
{
    private static final Logger log = LoggerFactory.getLogger(StringUtils.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = MathsUtils.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = MathsUtils.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = MathsUtils.log.isInfoEnabled();
    
    /**
     * @param inputValues
     * @return
     */
    public static double getStandardDeviationFromDoubles(final Collection<Double> inputValues)
    {
        double sumOfSquares = 0.0;
        double sum = 0;
        double mean = 0.0;
        final int count = inputValues.size();
        
        if(count < 2)
        {
            return 0.0;
        }
        
        for(final double nextInputValue : inputValues)
        {
            sum += nextInputValue;
        }
        
        mean = sum / count;
        
        for(final double nextInputValue : inputValues)
        {
            sumOfSquares = sumOfSquares + ((mean - nextInputValue) * (mean - nextInputValue));
        }
        
        return Math.sqrt(sumOfSquares / (count - 1));
    }
    
    /**
     * @param inputValues
     * @return
     */
    public static double getStandardDeviationFromDoubles(final double[] inputValues)
    {
        double sumOfSquares = 0.0;
        double sum = 0;
        double mean = 0.0;
        final int count = inputValues.length;
        
        if(count < 2)
        {
            return 0.0;
        }
        
        for(final double nextInputValue : inputValues)
        {
            sum += nextInputValue;
        }
        
        mean = sum / count;
        
        for(final double nextInputValue : inputValues)
        {
            sumOfSquares = sumOfSquares + ((mean - nextInputValue) * (mean - nextInputValue));
        }
        
        return Math.sqrt(sumOfSquares / (count - 1));
    }
    
    /**
     * @param inputValues
     * @return
     */
    public static double getStandardDeviationFromLongs(final Collection<Long> inputValues)
    {
        double sumOfSquares = 0.0;
        double sum = 0;
        double mean = 0.0;
        final int count = inputValues.size();
        
        if(count < 2)
        {
            return 0.0;
        }
        
        for(final long nextInputValue : inputValues)
        {
            sum += nextInputValue;
        }
        
        mean = sum / count;
        
        for(final double nextInputValue : inputValues)
        {
            sumOfSquares = sumOfSquares + ((mean - nextInputValue) * (mean - nextInputValue));
        }
        
        return Math.sqrt(sumOfSquares / (count - 1));
    }
    
    /**
     * @param inputValues
     * @return
     */
    public static double getStandardDeviationFromLongs(final long[] inputValues)
    {
        double sumOfSquares = 0.0;
        double sum = 0;
        double mean = 0.0;
        final int count = inputValues.length;
        
        if(count < 2)
        {
            return 0.0;
        }
        
        for(final long nextInputValue : inputValues)
        {
            sum += nextInputValue;
        }
        
        mean = sum / count;
        
        for(final double nextInputValue : inputValues)
        {
            sumOfSquares = sumOfSquares + ((mean - nextInputValue) * (mean - nextInputValue));
        }
        
        return Math.sqrt(sumOfSquares / (count - 1));
    }
    
}
