/**
 * 
 */
package org.queryall.api.querytype;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface OutputQueryType extends QueryType
{
    String getOutputString();
    
    void setOutputString(String outputString);
}
