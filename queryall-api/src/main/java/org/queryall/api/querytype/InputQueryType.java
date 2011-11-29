/**
 * 
 */
package org.queryall.api.querytype;

import java.util.Collection;

/**
 * A Query Type that uses named parameters to identify inputs.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface InputQueryType extends QueryType
{
    void addExpectedInputParameter(String expectedInputParameter);
    
    Collection<String> getExpectedInputParameters();
}
