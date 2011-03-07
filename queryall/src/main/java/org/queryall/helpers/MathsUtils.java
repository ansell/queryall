/**
 * 
 */
package org.queryall.helpers;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.openrdf.model.Value;
import org.openrdf.model.impl.NumericLiteralImpl;

/**
 * @author peter
 *
 */
public class MathsUtils
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
     * @param inputValues
     * @return
     */
    public static double getStandardDeviationFromLongs(long[] inputValues)
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
            sumOfSquares = sumOfSquares
                    + ((mean - nextInputValue) * (mean - nextInputValue));
        }
        
        return Math.sqrt(sumOfSquares / (count - 1));
    }

    /**
     * @param inputValues
     * @return
     */
    public static double getStandardDeviationFromLongs(Collection<Long> inputValues)
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
            sumOfSquares = sumOfSquares
                    + ((mean - nextInputValue) * (mean - nextInputValue));
        }
        
        return Math.sqrt(sumOfSquares / (count - 1));
    }

    /**
     * @param inputValues
     * @return
     */
    public static double getStandardDeviationFromDoubles(Collection<Double> inputValues)
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
            sumOfSquares = sumOfSquares
                    + ((mean - nextInputValue) * (mean - nextInputValue));
        }
        
        return Math.sqrt(sumOfSquares / (count - 1));
    }

    /**
     * @param inputValues
     * @return
     */
    public static double getStandardDeviationFromDoubles(double[] inputValues)
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
            sumOfSquares = sumOfSquares
                    + ((mean - nextInputValue) * (mean - nextInputValue));
        }
        
        return Math.sqrt(sumOfSquares / (count - 1));
    }

    /**
     * @param nextValue
     * @return
     */
    public static float getFloatFromValue(Value nextValue)
    {
        float result = 0.0f;
        
        try
        {
            result = ((NumericLiteralImpl) nextValue).floatValue();
        }
        catch (final ClassCastException cce)
        {
            result = Float.parseFloat(nextValue.toString());
        }
        
        return result;
    }

}
