/**
 * 
 */
package org.queryall.api.querytype;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.base.QueryAllSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfOutputQueryTypeSchema implements QueryAllSchema
{
    private static final Logger log = LoggerFactory.getLogger(RdfOutputQueryTypeSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = RdfOutputQueryTypeSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = RdfOutputQueryTypeSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RdfOutputQueryTypeSchema.log.isInfoEnabled();
    
    private static URI rdfOutputQueryTypeUri;
    private static URI queryOLDOutputRdfXmlString;
    private static URI queryOutputRdfString;
    private static URI queryOutputRdfFormat;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.QUERY.getBaseURI();
        
        RdfOutputQueryTypeSchema.setRdfOutputQueryTypeUri(f.createURI(baseUri, "RdfOutputQuery"));
        RdfOutputQueryTypeSchema.setOLDQueryOutputRdfXmlString(f.createURI(baseUri, "outputRdfXmlString"));
        RdfOutputQueryTypeSchema.setQueryOutputRdfString(f.createURI(baseUri, "outputRdfString"));
        RdfOutputQueryTypeSchema.setQueryOutputRdfFormat(f.createURI(baseUri, "outputRdfFormat"));
    }
    
    /**
     * @return the queryOLDOutputRdfXmlString
     */
    public static URI getOLDQueryOutputRdfXmlString()
    {
        return RdfOutputQueryTypeSchema.queryOLDOutputRdfXmlString;
    }
    
    public static URI getQueryOutputRdfFormat()
    {
        return RdfOutputQueryTypeSchema.queryOutputRdfFormat;
    }
    
    public static URI getQueryOutputRdfString()
    {
        return RdfOutputQueryTypeSchema.queryOutputRdfString;
    }
    
    /**
     * @return the queryTypeUri
     */
    public static URI getRdfOutputQueryTypeUri()
    {
        return RdfOutputQueryTypeSchema.rdfOutputQueryTypeUri;
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            final URI contextKeyUri = keyToUse;
            con.setAutoCommit(false);
            
            con.add(RdfOutputQueryTypeSchema.getRdfOutputQueryTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            
            con.add(RdfOutputQueryTypeSchema.getOLDQueryOutputRdfXmlString(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextKeyUri);
            con.add(RdfOutputQueryTypeSchema.getOLDQueryOutputRdfXmlString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(RdfOutputQueryTypeSchema.getOLDQueryOutputRdfXmlString(), RDFS.DOMAIN,
                    QueryTypeSchema.getQueryTypeUri(), contextKeyUri);
            con.add(RdfOutputQueryTypeSchema.getOLDQueryOutputRdfXmlString(), RDFS.LABEL,
                    f.createLiteral("DEPRECATED: Use rdfOutputString instead."), contextKeyUri);
            
            con.add(RdfOutputQueryTypeSchema.getQueryOutputRdfString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(RdfOutputQueryTypeSchema.getQueryOutputRdfString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(RdfOutputQueryTypeSchema.getQueryOutputRdfString(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contextKeyUri);
            con.add(RdfOutputQueryTypeSchema.getQueryOutputRdfString(),
                    RDFS.LABEL,
                    f.createLiteral("Property for denoting an RDF template that can be used to generate static additional statements for this query type."),
                    contextKeyUri);
            
            con.add(RdfOutputQueryTypeSchema.getQueryOutputRdfFormat(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(RdfOutputQueryTypeSchema.getQueryOutputRdfFormat(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(RdfOutputQueryTypeSchema.getQueryOutputRdfFormat(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contextKeyUri);
            con.add(RdfOutputQueryTypeSchema.getQueryOutputRdfFormat(),
                    RDFS.LABEL,
                    f.createLiteral("The RDF format used to design the rdfOutputString. This property defaults to application/rdf+xml if not defined."),
                    contextKeyUri);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
            {
                con.rollback();
            }
            
            RdfOutputQueryTypeSchema.log.error("RepositoryException: " + re.getMessage());
        }
        finally
        {
            if(con != null)
            {
                con.close();
            }
        }
        
        return false;
    }
    
    /**
     * @param queryOLDOutputRdfXmlString
     *            the queryOLDOutputRdfXmlString to set
     */
    public static void setOLDQueryOutputRdfXmlString(final URI queryOutputRdfXmlString)
    {
        RdfOutputQueryTypeSchema.queryOLDOutputRdfXmlString = queryOutputRdfXmlString;
    }
    
    public static void setQueryOutputRdfFormat(final URI outputRdfFormat)
    {
        RdfOutputQueryTypeSchema.queryOutputRdfFormat = outputRdfFormat;
    }
    
    public static void setQueryOutputRdfString(final URI outputRdfString)
    {
        RdfOutputQueryTypeSchema.queryOutputRdfString = outputRdfString;
    }
    
    /**
     * @param queryTypeUri
     *            the queryTypeUri to set
     */
    public static void setRdfOutputQueryTypeUri(final URI queryTypeUri)
    {
        RdfOutputQueryTypeSchema.rdfOutputQueryTypeUri = queryTypeUri;
    }
    
}
