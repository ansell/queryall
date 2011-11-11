/**
 * 
 */
package org.queryall.api.rdfrule;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SparqlAskRuleSchema
{
    private static final Logger log = LoggerFactory.getLogger(SparqlAskRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = SparqlAskRuleSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = SparqlAskRuleSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SparqlAskRuleSchema.log.isInfoEnabled();
    
    private static URI sparqlAskRuleTypeUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        SparqlAskRuleSchema.setSparqlAskRuleTypeUri(f
                .createURI(baseUri, "SparqlAskRule"));
    }
    
    /**
     * @return the sparqlruleTypeUri
     */
    public static URI getSparqlAskRuleTypeUri()
    {
        return SparqlAskRuleSchema.sparqlAskRuleTypeUri;
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        NormalisationRuleSchema.schemaToRdf(myRepository, contextUri, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(SparqlAskRuleSchema.getSparqlAskRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(SparqlAskRuleSchema.getSparqlAskRuleTypeUri(), RDFS.LABEL,
                    f.createLiteral("A SPARQL based normalisation rule intended to validate In-memory RDF triples using ASK queries."),
                    contextUri);
            con.add(SparqlAskRuleSchema.getSparqlAskRuleTypeUri(), RDFS.SUBCLASSOF,
                    ValidatingRuleSchema.getValidatingRuleTypeUri(), contextUri);
            con.add(SparqlAskRuleSchema.getSparqlAskRuleTypeUri(), RDFS.SUBCLASSOF,
                    SparqlNormalisationRuleSchema.getSparqlRuleTypeUri(), contextUri);
            
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
            
            SparqlAskRuleSchema.log.error("RepositoryException: " + re.getMessage());
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
     * @param sparqlConstructRuleTypeUri
     *            the sparqlruleTypeUri to set
     */
    public static void setSparqlAskRuleTypeUri(final URI sparqlConstructRuleTypeUri)
    {
        SparqlAskRuleSchema.sparqlAskRuleTypeUri = sparqlConstructRuleTypeUri;
    }
    
}
