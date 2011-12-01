/**
 * 
 */
package org.queryall.api.querytype;

/**
 * An OutputQueryType that provides RDF statements which are statically created on the server
 * without queries.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface RdfOutputQueryType extends OutputQueryType
{
    /**
     * NOTE: This setting does not affect the expected RDF format for Providers attached to this
     * query type.
     * 
     * @return The expected RDF format for the static RDF triples provided by this query type
     */
    String getOutputRdfFormat();
    
    /**
     * 
     * @param rdfFormat
     *            The expected RDF format for the static RDF triples provided by this query type.
     */
    void setOutputRdfFormat(String rdfFormat);
}
