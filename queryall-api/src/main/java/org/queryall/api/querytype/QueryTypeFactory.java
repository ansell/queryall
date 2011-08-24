/**
 * 
 */
package org.queryall.api.querytype;


/**
 *
 */
public interface QueryTypeFactory {

	QueryTypeEnum getQueryType();

	QueryTypeParser getParser();
	
}
