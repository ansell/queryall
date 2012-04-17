/**
 * 
 */
package org.queryall.api.querytype;

/**
 * A Query Type that provides output in the form of a string.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface OutputQueryType extends QueryType
{
    /**
     * 
     * @return The static output for this query in the form of a String
     */
    String getOutputString();
    
    /**
     * 
     * @param outputString
     *            Sets the output string for this query in the form of a String
     */
    void setOutputString(String outputString);
}
