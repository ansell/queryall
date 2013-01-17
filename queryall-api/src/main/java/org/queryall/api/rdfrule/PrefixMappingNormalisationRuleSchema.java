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
public class PrefixMappingNormalisationRuleSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(PrefixMappingNormalisationRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = PrefixMappingNormalisationRuleSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = PrefixMappingNormalisationRuleSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = PrefixMappingNormalisationRuleSchema.LOG.isInfoEnabled();
    
    private static URI simplePrefixMappingTypeUri;
    private static URI subjectMappingPredicateUri;
    private static URI outputPrefixUri;
    private static URI inputPrefixUri;
    private static URI objectMappingPredicateUri;
    private static URI predicateMappingPredicateUri;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        PrefixMappingNormalisationRuleSchema.setSimplePrefixMappingTypeUri(f.createURI(baseUri,
                "PrefixMappingNormalisationRule"));
        PrefixMappingNormalisationRuleSchema.setInputPrefixUri(f.createURI(baseUri, "inputPrefix"));
        PrefixMappingNormalisationRuleSchema.setOutputPrefixUri(f.createURI(baseUri, "outputPrefix"));
        PrefixMappingNormalisationRuleSchema.setSubjectMappingPredicateUri(f.createURI(baseUri,
                "subjectMappingPredicate"));
        PrefixMappingNormalisationRuleSchema.setPredicateMappingPredicateUri(f.createURI(baseUri,
                "predicateMappingPredicate"));
        PrefixMappingNormalisationRuleSchema.setObjectMappingPredicateUri(f
                .createURI(baseUri, "objectMappingPredicate"));
    }
    
    /**
     * The pre-instantiated schema object for PrefixMappingNormalisationRuleSchema.
     */
    public static final QueryAllSchema PREFIX_MAPPING_NORMALISATION_RULE_SCHEMA =
            new PrefixMappingNormalisationRuleSchema();
    
    /**
     * @return the inputPrefixUri
     */
    public static URI getInputPrefixUri()
    {
        return PrefixMappingNormalisationRuleSchema.inputPrefixUri;
    }
    
    public static URI getObjectMappingPredicateUri()
    {
        return PrefixMappingNormalisationRuleSchema.objectMappingPredicateUri;
    }
    
    /**
     * @return the outputPrefixUri
     */
    public static URI getOutputPrefixUri()
    {
        return PrefixMappingNormalisationRuleSchema.outputPrefixUri;
    }
    
    public static URI getPredicateMappingPredicateUri()
    {
        return PrefixMappingNormalisationRuleSchema.predicateMappingPredicateUri;
    }
    
    /**
     * @return the simplePrefixMappingTypeUri
     */
    public static URI getSimplePrefixMappingTypeUri()
    {
        return PrefixMappingNormalisationRuleSchema.simplePrefixMappingTypeUri;
    }
    
    /**
     * @return the outputPrefixUri
     */
    public static URI getSubjectMappingPredicateUri()
    {
        return PrefixMappingNormalisationRuleSchema.subjectMappingPredicateUri;
    }
    
    public static void setInputPrefixUri(final URI nextInputPrefixUri)
    {
        PrefixMappingNormalisationRuleSchema.inputPrefixUri = nextInputPrefixUri;
    }
    
    private static void setObjectMappingPredicateUri(final URI nextObjectMappingPredicateUri)
    {
        PrefixMappingNormalisationRuleSchema.objectMappingPredicateUri = nextObjectMappingPredicateUri;
    }
    
    public static void setOutputPrefixUri(final URI nextOutputPrefixUri)
    {
        PrefixMappingNormalisationRuleSchema.outputPrefixUri = nextOutputPrefixUri;
    }
    
    private static void setPredicateMappingPredicateUri(final URI nextPredicateMappingPredicateUri)
    {
        PrefixMappingNormalisationRuleSchema.predicateMappingPredicateUri = nextPredicateMappingPredicateUri;
    }
    
    /**
     * @param nextSimplePrefixMappingTypeUri
     *            the simplePrefixMappingTypeUri to set
     */
    public static void setSimplePrefixMappingTypeUri(final URI nextSimplePrefixMappingTypeUri)
    {
        PrefixMappingNormalisationRuleSchema.simplePrefixMappingTypeUri = nextSimplePrefixMappingTypeUri;
    }
    
    public static void setSubjectMappingPredicateUri(final URI nextMappingPredicateUri)
    {
        PrefixMappingNormalisationRuleSchema.subjectMappingPredicateUri = nextMappingPredicateUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public PrefixMappingNormalisationRuleSchema()
    {
        this(PrefixMappingNormalisationRuleSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public PrefixMappingNormalisationRuleSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contextUri)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            con.begin();
            
            con.add(PrefixMappingNormalisationRuleSchema.getSimplePrefixMappingTypeUri(), RDF.TYPE, OWL.CLASS,
                    contextUri);
            con.add(PrefixMappingNormalisationRuleSchema.getSimplePrefixMappingTypeUri(), RDFS.SUBCLASSOF,
                    TransformingRuleSchema.getTransformingRuleTypeUri(), contextUri);
            con.add(PrefixMappingNormalisationRuleSchema.getSimplePrefixMappingTypeUri(),
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
            
            PrefixMappingNormalisationRuleSchema.LOG.error("RepositoryException: " + re.getMessage());
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
