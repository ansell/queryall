/**
 * 
 */
package org.queryall.api.rdfrule;

import org.kohsuke.MetaInfServices;
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
@MetaInfServices(QueryAllSchema.class)
public class XsltNormalisationRuleSchema extends QueryAllSchema
{
    private static final Logger log = LoggerFactory.getLogger(XsltNormalisationRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = XsltNormalisationRuleSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = XsltNormalisationRuleSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = XsltNormalisationRuleSchema.log.isInfoEnabled();
    
    private static URI xsltRuleTypeUri;
    
    private static URI xsltRuleStylesheetUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        XsltNormalisationRuleSchema.setXsltRuleTypeUri(f.createURI(baseUri, "XsltNormalisationRule"));
        XsltNormalisationRuleSchema.setXsltRuleStylesheetUri(f.createURI(baseUri, "xsltStylesheet"));
        
    }
    
    public static final QueryAllSchema XSLT_NORMALISATION_RULE_SCHEMA = new XsltNormalisationRuleSchema();
    
    /**
     * @return the xsltRuleStylesheetUri
     */
    public static URI getXsltRuleStylesheetUri()
    {
        return XsltNormalisationRuleSchema.xsltRuleStylesheetUri;
    }
    
    /**
     * @return the xsltRuleTypeUri
     */
    public static URI getXsltRuleTypeUri()
    {
        return XsltNormalisationRuleSchema.xsltRuleTypeUri;
    }
    
    /**
     * @param xsltRuleStylesheetUri
     *            the xsltRuleStylesheetUri to set
     */
    public static void setXsltRuleStylesheetUri(final URI xsltRuleStylesheetUri)
    {
        XsltNormalisationRuleSchema.xsltRuleStylesheetUri = xsltRuleStylesheetUri;
    }
    
    /**
     * @param xsltRuleTypeUri
     *            the xsltRuleTypeUri to set
     */
    public static void setXsltRuleTypeUri(final URI xsltRuleTypeUri)
    {
        XsltNormalisationRuleSchema.xsltRuleTypeUri = xsltRuleTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name
     */
    public XsltNormalisationRuleSchema()
    {
        this(XsltNormalisationRuleSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public XsltNormalisationRuleSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(XsltNormalisationRuleSchema.getXsltRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(XsltNormalisationRuleSchema.getXsltRuleTypeUri(), RDFS.LABEL,
                    f.createLiteral("A XSLT based normalisation rule intended to normalise textual XML documents."),
                    contextUri);
            con.add(XsltNormalisationRuleSchema.getXsltRuleTypeUri(), RDFS.SUBCLASSOF,
                    TransformingRuleSchema.getTransformingRuleTypeUri(), contextUri);
            
            con.add(XsltNormalisationRuleSchema.getXsltRuleStylesheetUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(XsltNormalisationRuleSchema.getXsltRuleStylesheetUri(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(XsltNormalisationRuleSchema.getXsltRuleStylesheetUri(), RDFS.DOMAIN,
                    XsltNormalisationRuleSchema.getXsltRuleTypeUri(), contextUri);
            con.add(XsltNormalisationRuleSchema.getXsltRuleStylesheetUri(),
                    RDFS.LABEL,
                    f.createLiteral("An XSLT stylesheet that will be used to transform textual queries or result documents"),
                    contextUri);
            
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
            
            XsltNormalisationRuleSchema.log.error("RepositoryException: " + re.getMessage());
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
}
