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
public class RdfsNormalisationRuleSchema extends QueryAllSchema
{
    private static final Logger log = LoggerFactory.getLogger(RdfsNormalisationRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = RdfsNormalisationRuleSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = RdfsNormalisationRuleSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RdfsNormalisationRuleSchema.log.isInfoEnabled();
    
    private static URI rdfsruleTypeUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        RdfsNormalisationRuleSchema.setRdfsRuleTypeUri(f.createURI(baseUri, "RdfsValidatingRule"));
    }
    
    public static final QueryAllSchema RDFS_NORMALISATION_RULE_SCHEMA = new RdfsNormalisationRuleSchema();
    
    /**
     * @return the rdfsruleTypeUri
     */
    public static URI getRdfsRuleTypeUri()
    {
        return RdfsNormalisationRuleSchema.rdfsruleTypeUri;
    }
    
    /**
     * @param rdfsruleTypeUri
     *            the rdfsruleTypeUri to set
     */
    public static void setRdfsRuleTypeUri(final URI rdfsruleTypeUri)
    {
        RdfsNormalisationRuleSchema.rdfsruleTypeUri = rdfsruleTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name
     */
    public RdfsNormalisationRuleSchema()
    {
        this(RdfsNormalisationRuleSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public RdfsNormalisationRuleSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contextUri)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(RdfsNormalisationRuleSchema.getRdfsRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(RdfsNormalisationRuleSchema.getRdfsRuleTypeUri(), RDFS.SUBCLASSOF,
                    ValidatingRuleSchema.getValidatingRuleTypeUri(), contextUri);
            con.add(RdfsNormalisationRuleSchema.getRdfsRuleTypeUri(),
                    RDFS.LABEL,
                    f.createLiteral("An RDFS normalisation rule intended to validate triples based on an RDFS ontology."),
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
            
            RdfsNormalisationRuleSchema.log.error("RepositoryException: " + re.getMessage());
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
