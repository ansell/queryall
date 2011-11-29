/**
 * 
 */
package org.queryall.api.querytype;

/**
 * An OutputQueryType that provides RDF statements.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface RdfOutputQueryType extends OutputQueryType
{
    String getOutputRdfFormat();
    
    void setOutputRdfFormat(String rdfFormat);
}
