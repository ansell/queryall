/**
 * 
 */
package org.queryall.api.rdfrule;

import java.util.LinkedHashSet;
import java.util.Set;

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
import org.queryall.api.namespace.NamespaceEntrySchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllSchema.class)
public class NormalisationRuleSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(NormalisationRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = NormalisationRuleSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = NormalisationRuleSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = NormalisationRuleSchema.LOG.isInfoEnabled();
    
    private static Set<URI> allStages;
    
    private static URI normalisationRuleTypeUri;
    
    private static URI version2NormalisationRuleTypeUri;
    
    private static URI rdfruleDescription;
    
    private static URI rdfruleOrder;
    
    private static URI rdfruleStage;
    
    private static URI rdfruleHasRelatedNamespace;
    
    private static URI rdfruleStageQueryVariables;
    
    private static URI rdfruleStageAfterQueryCreation;
    
    private static URI rdfruleStageAfterQueryParsing;
    
    private static URI rdfruleStageBeforeResultsImport;
    
    private static URI rdfruleStageAfterResultsImport;
    
    private static URI rdfruleStageAfterResultsToPool;
    
    private static URI rdfruleStageAfterResultsToDocument;
    
    private static URI rdfruleTypeValidForStage;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        NormalisationRuleSchema.setNormalisationRuleTypeUri(f.createURI(baseUri, "NormalisationRule"));
        
        NormalisationRuleSchema.setVersion2NormalisationRuleTypeUri(f.createURI(baseUri, "RdfRule"));
        
        NormalisationRuleSchema.setRdfruleDescription(f.createURI(baseUri, "description"));
        NormalisationRuleSchema.setRdfruleOrder(f.createURI(baseUri, "order"));
        NormalisationRuleSchema.setRdfruleStage(f.createURI(baseUri, "stage"));
        NormalisationRuleSchema.setRdfruleHasRelatedNamespace(f.createURI(baseUri, "hasRelatedNamespace"));
        
        NormalisationRuleSchema.setRdfruleStageQueryVariables(f.createURI(baseUri, "queryVariables"));
        
        NormalisationRuleSchema.setRdfruleStageAfterQueryCreation(f.createURI(baseUri, "afterQueryCreation"));
        
        NormalisationRuleSchema.setRdfruleStageAfterQueryParsing(f.createURI(baseUri, "afterQueryParsing"));
        
        NormalisationRuleSchema.setRdfruleStageBeforeResultsImport(f.createURI(baseUri, "beforeResultsImport"));
        
        NormalisationRuleSchema.setRdfruleStageAfterResultsImport(f.createURI(baseUri, "afterResultsImport"));
        
        NormalisationRuleSchema.setRdfruleStageAfterResultsToPool(f.createURI(baseUri, "afterResultsToPool"));
        
        NormalisationRuleSchema.setRdfruleStageAfterResultsToDocument(f.createURI(baseUri, "afterResultsToDocument"));
        
        NormalisationRuleSchema.setRdfruleTypeValidForStage(f.createURI(baseUri, "typeValidForStage"));
        
        NormalisationRuleSchema.allStages = new LinkedHashSet<URI>(7);
        NormalisationRuleSchema.allStages.add(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation());
        NormalisationRuleSchema.allStages.add(NormalisationRuleSchema.getRdfruleStageAfterQueryParsing());
        NormalisationRuleSchema.allStages.add(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
        NormalisationRuleSchema.allStages.add(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument());
        NormalisationRuleSchema.allStages.add(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
        NormalisationRuleSchema.allStages.add(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport());
        NormalisationRuleSchema.allStages.add(NormalisationRuleSchema.getRdfruleStageQueryVariables());
        
    }
    
    /**
     * A pre-instantiated schema object for NormalisationRuleSchema.
     */
    public static final QueryAllSchema NORMALISATION_RULE_SCHEMA = new NormalisationRuleSchema();
    
    public static Set<URI> getAllStages()
    {
        return NormalisationRuleSchema.allStages;
    }
    
    /**
     * @return the normalisationRuleTypeUri
     */
    public static URI getNormalisationRuleTypeUri()
    {
        return NormalisationRuleSchema.normalisationRuleTypeUri;
    }
    
    /**
     * @return the rdfruleDescription
     */
    public static URI getRdfruleDescription()
    {
        return NormalisationRuleSchema.rdfruleDescription;
    }
    
    /**
     * @return the rdfruleHasRelatedNamespace
     */
    public static URI getRdfruleHasRelatedNamespace()
    {
        return NormalisationRuleSchema.rdfruleHasRelatedNamespace;
    }
    
    /**
     * @return the rdfruleOrder
     */
    public static URI getRdfruleOrder()
    {
        return NormalisationRuleSchema.rdfruleOrder;
    }
    
    /**
     * @return the rdfruleStage
     */
    public static URI getRdfruleStage()
    {
        return NormalisationRuleSchema.rdfruleStage;
    }
    
    /**
     * @return the rdfruleStageAfterQueryCreation
     */
    public static URI getRdfruleStageAfterQueryCreation()
    {
        return NormalisationRuleSchema.rdfruleStageAfterQueryCreation;
    }
    
    /**
     * @return the rdfruleStageAfterQueryParsing
     */
    public static URI getRdfruleStageAfterQueryParsing()
    {
        return NormalisationRuleSchema.rdfruleStageAfterQueryParsing;
    }
    
    /**
     * @return the rdfruleStageAfterResultsImport
     */
    public static URI getRdfruleStageAfterResultsImport()
    {
        return NormalisationRuleSchema.rdfruleStageAfterResultsImport;
    }
    
    /**
     * @return the rdfruleStageAfterResultsToDocument
     */
    public static URI getRdfruleStageAfterResultsToDocument()
    {
        return NormalisationRuleSchema.rdfruleStageAfterResultsToDocument;
    }
    
    /**
     * @return the rdfruleStageAfterResultsToPool
     */
    public static URI getRdfruleStageAfterResultsToPool()
    {
        return NormalisationRuleSchema.rdfruleStageAfterResultsToPool;
    }
    
    /**
     * @return the rdfruleStageBeforeResultsImport
     */
    public static URI getRdfruleStageBeforeResultsImport()
    {
        return NormalisationRuleSchema.rdfruleStageBeforeResultsImport;
    }
    
    /**
     * @return the rdfruleStageQueryVariables
     */
    public static URI getRdfruleStageQueryVariables()
    {
        return NormalisationRuleSchema.rdfruleStageQueryVariables;
    }
    
    /**
     * @return the rdfruleTypeValidForStage
     */
    public static URI getRdfruleTypeValidForStage()
    {
        return NormalisationRuleSchema.rdfruleTypeValidForStage;
    }
    
    public static URI getVersion2NormalisationRuleTypeUri()
    {
        return NormalisationRuleSchema.version2NormalisationRuleTypeUri;
    }
    
    /**
     * @param nextNormalisationRuleTypeUri
     *            the normalisationRuleTypeUri to set
     */
    public static void setNormalisationRuleTypeUri(final URI nextNormalisationRuleTypeUri)
    {
        NormalisationRuleSchema.normalisationRuleTypeUri = nextNormalisationRuleTypeUri;
    }
    
    /**
     * @param nextRdfruleDescription
     *            the rdfruleDescription to set
     */
    public static void setRdfruleDescription(final URI nextRdfruleDescription)
    {
        NormalisationRuleSchema.rdfruleDescription = nextRdfruleDescription;
    }
    
    /**
     * @param nextRdfruleHasRelatedNamespace
     *            the rdfruleHasRelatedNamespace to set
     */
    public static void setRdfruleHasRelatedNamespace(final URI nextRdfruleHasRelatedNamespace)
    {
        NormalisationRuleSchema.rdfruleHasRelatedNamespace = nextRdfruleHasRelatedNamespace;
    }
    
    /**
     * @param nextRdfruleOrder
     *            the rdfruleOrder to set
     */
    public static void setRdfruleOrder(final URI nextRdfruleOrder)
    {
        NormalisationRuleSchema.rdfruleOrder = nextRdfruleOrder;
    }
    
    /**
     * @param nextRdfruleStage
     *            the rdfruleStage to set
     */
    public static void setRdfruleStage(final URI nextRdfruleStage)
    {
        NormalisationRuleSchema.rdfruleStage = nextRdfruleStage;
    }
    
    /**
     * @param nextRdfruleStageAfterQueryCreation
     *            the rdfruleStageAfterQueryCreation to set
     */
    public static void setRdfruleStageAfterQueryCreation(final URI nextRdfruleStageAfterQueryCreation)
    {
        NormalisationRuleSchema.rdfruleStageAfterQueryCreation = nextRdfruleStageAfterQueryCreation;
    }
    
    /**
     * @param nextRdfruleStageAfterQueryParsing
     *            the rdfruleStageAfterQueryParsing to set
     */
    public static void setRdfruleStageAfterQueryParsing(final URI nextRdfruleStageAfterQueryParsing)
    {
        NormalisationRuleSchema.rdfruleStageAfterQueryParsing = nextRdfruleStageAfterQueryParsing;
    }
    
    /**
     * @param nextRdfruleStageAfterResultsImport
     *            the rdfruleStageAfterResultsImport to set
     */
    public static void setRdfruleStageAfterResultsImport(final URI nextRdfruleStageAfterResultsImport)
    {
        NormalisationRuleSchema.rdfruleStageAfterResultsImport = nextRdfruleStageAfterResultsImport;
    }
    
    /**
     * @param nextRdfruleStageAfterResultsToDocument
     *            the rdfruleStageAfterResultsToDocument to set
     */
    public static void setRdfruleStageAfterResultsToDocument(final URI nextRdfruleStageAfterResultsToDocument)
    {
        NormalisationRuleSchema.rdfruleStageAfterResultsToDocument = nextRdfruleStageAfterResultsToDocument;
    }
    
    /**
     * @param nextRdfruleStageAfterResultsToPool
     *            the rdfruleStageAfterResultsToPool to set
     */
    public static void setRdfruleStageAfterResultsToPool(final URI nextRdfruleStageAfterResultsToPool)
    {
        NormalisationRuleSchema.rdfruleStageAfterResultsToPool = nextRdfruleStageAfterResultsToPool;
    }
    
    /**
     * @param nextRdfruleStageBeforeResultsImport
     *            the rdfruleStageBeforeResultsImport to set
     */
    public static void setRdfruleStageBeforeResultsImport(final URI nextRdfruleStageBeforeResultsImport)
    {
        NormalisationRuleSchema.rdfruleStageBeforeResultsImport = nextRdfruleStageBeforeResultsImport;
    }
    
    /**
     * @param nextRdfruleStageQueryVariables
     *            the rdfruleStageQueryVariables to set
     */
    public static void setRdfruleStageQueryVariables(final URI nextRdfruleStageQueryVariables)
    {
        NormalisationRuleSchema.rdfruleStageQueryVariables = nextRdfruleStageQueryVariables;
    }
    
    /**
     * @param nextRdfruleTypeValidForStage
     *            the rdfruleTypeValidForStage to set
     */
    public static void setRdfruleTypeValidForStage(final URI nextRdfruleTypeValidForStage)
    {
        NormalisationRuleSchema.rdfruleTypeValidForStage = nextRdfruleTypeValidForStage;
    }
    
    private static void setVersion2NormalisationRuleTypeUri(final URI nextVersion2NormalisationRuleTypeUri)
    {
        NormalisationRuleSchema.version2NormalisationRuleTypeUri = nextVersion2NormalisationRuleTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public NormalisationRuleSchema()
    {
        this(NormalisationRuleSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public NormalisationRuleSchema(final String nextName)
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
            
            con.add(NormalisationRuleSchema.getNormalisationRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            
            con.add(NormalisationRuleSchema.getNormalisationRuleTypeUri(),
                    RDFS.LABEL,
                    f.createLiteral("A normalisation rule intended to denormalise parts of queries to match endpoints, and renormalise the output of the query to match the normalised form."),
                    contextUri);
            
            if(modelVersion == 1)
            {
                con.add(NormalisationRuleSchema.getRdfruleDescription(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contextUri);
                con.add(NormalisationRuleSchema.getRdfruleDescription(), RDFS.SUBPROPERTYOF, RDFS.COMMENT, contextUri);
                con.add(NormalisationRuleSchema.getRdfruleDescription(), RDFS.RANGE, RDFS.LITERAL, contextUri);
                con.add(NormalisationRuleSchema.getRdfruleDescription(), RDFS.DOMAIN,
                        NormalisationRuleSchema.getNormalisationRuleTypeUri(), contextUri);
                con.add(NormalisationRuleSchema.getRdfruleDescription(), RDFS.LABEL,
                        f.createLiteral("The description of a normalisation rule."), contextUri);
                
            }
            
            con.add(NormalisationRuleSchema.getRdfruleOrder(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(NormalisationRuleSchema.getRdfruleOrder(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(NormalisationRuleSchema.getRdfruleOrder(), RDFS.DOMAIN,
                    NormalisationRuleSchema.getNormalisationRuleTypeUri(), contextUri);
            con.add(NormalisationRuleSchema.getRdfruleOrder(),
                    RDFS.LABEL,
                    f.createLiteral("The ordering variable that is used to identify what order the normalisation rules are designed to be applied in."),
                    contextUri);
            
            con.add(NormalisationRuleSchema.getRdfruleHasRelatedNamespace(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(NormalisationRuleSchema.getRdfruleHasRelatedNamespace(), RDFS.RANGE,
                    NamespaceEntrySchema.getNamespaceTypeUri(), contextUri);
            con.add(NormalisationRuleSchema.getRdfruleHasRelatedNamespace(), RDFS.DOMAIN,
                    NormalisationRuleSchema.getNormalisationRuleTypeUri(), contextUri);
            con.add(NormalisationRuleSchema.getRdfruleHasRelatedNamespace(),
                    RDFS.LABEL,
                    f.createLiteral("An informative property indicating that the target namespace is somehow related to this rule."),
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
            
            NormalisationRuleSchema.LOG.error("RepositoryException: " + re.getMessage());
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
