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
    private static final Logger LOG = LoggerFactory.getLogger(XsltNormalisationRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = XsltNormalisationRuleSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = XsltNormalisationRuleSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = XsltNormalisationRuleSchema.LOG.isInfoEnabled();
    
    private static URI xsltRuleTypeUri;
    
    private static URI xsltRuleStylesheetUri;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        XsltNormalisationRuleSchema.setXsltRuleTypeUri(f.createURI(baseUri, "XsltNormalisationRule"));
        XsltNormalisationRuleSchema.setXsltRuleStylesheetUri(f.createURI(baseUri, "xsltStylesheet"));
        
    }
    
    /**
     * A pre-instantiated schema object for XsltNormalisationRuleSchema.
     */
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
     * @param nextXsltRuleStylesheetUri
     *            the xsltRuleStylesheetUri to set
     */
    public static void setXsltRuleStylesheetUri(final URI nextXsltRuleStylesheetUri)
    {
        XsltNormalisationRuleSchema.xsltRuleStylesheetUri = nextXsltRuleStylesheetUri;
    }
    
    /**
     * @param nextXsltRuleTypeUri
     *            the xsltRuleTypeUri to set
     */
    public static void setXsltRuleTypeUri(final URI nextXsltRuleTypeUri)
    {
        XsltNormalisationRuleSchema.xsltRuleTypeUri = nextXsltRuleTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
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
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contextUri)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            con.begin();
            
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
            
            XsltNormalisationRuleSchema.LOG.error("RepositoryException: " + re.getMessage());
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
