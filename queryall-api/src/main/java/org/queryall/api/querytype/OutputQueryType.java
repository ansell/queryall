/**
 * 
 */
package org.queryall.api.querytype;

/**
 * A Query Type that provides output in the form of a string
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface OutputQueryType extends QueryType
{
    String getOutputString();
    
    void setOutputString(String outputString);
}
