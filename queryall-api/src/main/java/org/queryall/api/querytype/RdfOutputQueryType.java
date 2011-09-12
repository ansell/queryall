/**
 * 
 */
package org.queryall.api.querytype;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface RdfOutputQueryType extends OutputQueryType
{
    String getOutputRdfFormat();
    
    void setOutputRdfFormat(String rdfFormat);
}
