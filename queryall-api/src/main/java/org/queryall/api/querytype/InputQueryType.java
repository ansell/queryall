/**
 * 
 */
package org.queryall.api.querytype;

import java.util.Collection;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface InputQueryType extends QueryType
{
    Collection<String> getExpectedInputParameters();

    void addExpectedInputParameter(String expectedInputParameter);
}
