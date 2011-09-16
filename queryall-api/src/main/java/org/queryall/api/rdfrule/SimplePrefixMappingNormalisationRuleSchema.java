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
public class SimplePrefixMappingNormalisationRuleSchema
{
    private static final Logger log = LoggerFactory.getLogger(SimplePrefixMappingNormalisationRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = SimplePrefixMappingNormalisationRuleSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = SimplePrefixMappingNormalisationRuleSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SimplePrefixMappingNormalisationRuleSchema.log.isInfoEnabled();
    
    private static URI simplePrefixMappingTypeUri;
    private static URI subjectMappingPredicateUri;
    private static URI outputPrefixUri;
    private static URI inputPrefixUri;
    private static URI objectMappingPredicateUri;
    private static URI predicateMappingPredicateUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        SimplePrefixMappingNormalisationRuleSchema.setSimplePrefixMappingTypeUri(f.createURI(baseUri, "SimplePrefixMappingNormalisationRule"));
        SimplePrefixMappingNormalisationRuleSchema.setInputPrefixUri(f.createURI(baseUri, "inputPrefix"));
        SimplePrefixMappingNormalisationRuleSchema.setOutputPrefixUri(f.createURI(baseUri, "outputPrefix"));
        SimplePrefixMappingNormalisationRuleSchema.setSubjectMappingPredicateUri(f.createURI(baseUri, "subjectMappingPredicate"));
        SimplePrefixMappingNormalisationRuleSchema.setPredicateMappingPredicateUri(f.createURI(baseUri, "predicateMappingPredicate"));
        SimplePrefixMappingNormalisationRuleSchema.setObjectMappingPredicateUri(f.createURI(baseUri, "objectMappingPredicate"));
    }
    
    /**
     * @return the simplePrefixMappingTypeUri
     */
    public static URI getSimplePrefixMappingTypeUri()
    {
        return SimplePrefixMappingNormalisationRuleSchema.simplePrefixMappingTypeUri;
    }
    
    private static void setObjectMappingPredicateUri(URI nextObjectMappingPredicateUri)
    {
        objectMappingPredicateUri = nextObjectMappingPredicateUri;
    }

    private static void setPredicateMappingPredicateUri(URI nextPredicateMappingPredicateUri)
    {
        predicateMappingPredicateUri = nextPredicateMappingPredicateUri;
    }

    public static void setInputPrefixUri(URI nextInputPrefixUri)
    {
        SimplePrefixMappingNormalisationRuleSchema.inputPrefixUri = nextInputPrefixUri;
    }

    public static void setOutputPrefixUri(URI nextOutputPrefixUri)
    {
        SimplePrefixMappingNormalisationRuleSchema.outputPrefixUri = nextOutputPrefixUri;
    }

    public static void setSubjectMappingPredicateUri(URI nextMappingPredicateUri)
    {
        SimplePrefixMappingNormalisationRuleSchema.subjectMappingPredicateUri = nextMappingPredicateUri;
    }

    /**
     * @return the inputPrefixUri
     */
    public static URI getInputPrefixUri()
    {
        return SimplePrefixMappingNormalisationRuleSchema.inputPrefixUri;
    }
    
    /**
     * @return the outputPrefixUri
     */
    public static URI getOutputPrefixUri()
    {
        return SimplePrefixMappingNormalisationRuleSchema.outputPrefixUri;
    }
    
    /**
     * @return the outputPrefixUri
     */
    public static URI getSubjectMappingPredicateUri()
    {
        return SimplePrefixMappingNormalisationRuleSchema.subjectMappingPredicateUri;
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        NormalisationRuleSchema.schemaToRdf(myRepository, contextUri, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(SimplePrefixMappingNormalisationRuleSchema.getSimplePrefixMappingTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(SimplePrefixMappingNormalisationRuleSchema.getSimplePrefixMappingTypeUri(), RDFS.SUBCLASSOF,
                    NormalisationRuleSchema.getNormalisationRuleTypeUri(), contextUri);
            con.add(SimplePrefixMappingNormalisationRuleSchema.getSimplePrefixMappingTypeUri(),
                    RDFS.LABEL,
                    f.createLiteral("A simple mapping rule for translating URI prefixes between two schemes. Internally it uses both Regular Expressions and Sparql rules."),
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
            
            SimplePrefixMappingNormalisationRuleSchema.log.error("RepositoryException: " + re.getMessage());
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
     * @param simplePrefixMappingTypeUri
     *            the simplePrefixMappingTypeUri to set
     */
    public static void setSimplePrefixMappingTypeUri(final URI simplePrefixMappingTypeUri)
    {
        SimplePrefixMappingNormalisationRuleSchema.simplePrefixMappingTypeUri = simplePrefixMappingTypeUri;
    }

    public static URI getObjectMappingPredicateUri()
    {
        return objectMappingPredicateUri;
    }

    public static URI getPredicateMappingPredicateUri()
    {
        return predicateMappingPredicateUri;
    }
    
}
