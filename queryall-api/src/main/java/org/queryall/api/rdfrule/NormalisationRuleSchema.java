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
    private static final Logger log = LoggerFactory.getLogger(NormalisationRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = NormalisationRuleSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = NormalisationRuleSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = NormalisationRuleSchema.log.isInfoEnabled();
    
    private static final Set<URI> ALL_STAGES;
    
    private static URI normalisationRuleTypeUri;
    
    public static URI version2NormalisationRuleTypeUri;
    
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
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        NormalisationRuleSchema.setNormalisationRuleTypeUri(f.createURI(baseUri, "NormalisationRule"));
        
        NormalisationRuleSchema.version2NormalisationRuleTypeUri = f.createURI(baseUri, "RdfRule");
        
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
        
        ALL_STAGES = new LinkedHashSet<URI>(7);
        NormalisationRuleSchema.ALL_STAGES.add(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation());
        NormalisationRuleSchema.ALL_STAGES.add(NormalisationRuleSchema.getRdfruleStageAfterQueryParsing());
        NormalisationRuleSchema.ALL_STAGES.add(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
        NormalisationRuleSchema.ALL_STAGES.add(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument());
        NormalisationRuleSchema.ALL_STAGES.add(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
        NormalisationRuleSchema.ALL_STAGES.add(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport());
        NormalisationRuleSchema.ALL_STAGES.add(NormalisationRuleSchema.getRdfruleStageQueryVariables());
        
    }
    
    public static Set<URI> getAllStages()
    {
        return NormalisationRuleSchema.ALL_STAGES;
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
    
    /**
     * @param normalisationRuleTypeUri
     *            the normalisationRuleTypeUri to set
     */
    public static void setNormalisationRuleTypeUri(final URI normalisationRuleTypeUri)
    {
        NormalisationRuleSchema.normalisationRuleTypeUri = normalisationRuleTypeUri;
    }
    
    /**
     * @param rdfruleDescription
     *            the rdfruleDescription to set
     */
    public static void setRdfruleDescription(final URI rdfruleDescription)
    {
        NormalisationRuleSchema.rdfruleDescription = rdfruleDescription;
    }
    
    /**
     * @param rdfruleHasRelatedNamespace
     *            the rdfruleHasRelatedNamespace to set
     */
    public static void setRdfruleHasRelatedNamespace(final URI rdfruleHasRelatedNamespace)
    {
        NormalisationRuleSchema.rdfruleHasRelatedNamespace = rdfruleHasRelatedNamespace;
    }
    
    /**
     * @param rdfruleOrder
     *            the rdfruleOrder to set
     */
    public static void setRdfruleOrder(final URI rdfruleOrder)
    {
        NormalisationRuleSchema.rdfruleOrder = rdfruleOrder;
    }
    
    /**
     * @param rdfruleStage
     *            the rdfruleStage to set
     */
    public static void setRdfruleStage(final URI rdfruleStage)
    {
        NormalisationRuleSchema.rdfruleStage = rdfruleStage;
    }
    
    /**
     * @param rdfruleStageAfterQueryCreation
     *            the rdfruleStageAfterQueryCreation to set
     */
    public static void setRdfruleStageAfterQueryCreation(final URI rdfruleStageAfterQueryCreation)
    {
        NormalisationRuleSchema.rdfruleStageAfterQueryCreation = rdfruleStageAfterQueryCreation;
    }
    
    /**
     * @param rdfruleStageAfterQueryParsing
     *            the rdfruleStageAfterQueryParsing to set
     */
    public static void setRdfruleStageAfterQueryParsing(final URI rdfruleStageAfterQueryParsing)
    {
        NormalisationRuleSchema.rdfruleStageAfterQueryParsing = rdfruleStageAfterQueryParsing;
    }
    
    /**
     * @param rdfruleStageAfterResultsImport
     *            the rdfruleStageAfterResultsImport to set
     */
    public static void setRdfruleStageAfterResultsImport(final URI rdfruleStageAfterResultsImport)
    {
        NormalisationRuleSchema.rdfruleStageAfterResultsImport = rdfruleStageAfterResultsImport;
    }
    
    /**
     * @param rdfruleStageAfterResultsToDocument
     *            the rdfruleStageAfterResultsToDocument to set
     */
    public static void setRdfruleStageAfterResultsToDocument(final URI rdfruleStageAfterResultsToDocument)
    {
        NormalisationRuleSchema.rdfruleStageAfterResultsToDocument = rdfruleStageAfterResultsToDocument;
    }
    
    /**
     * @param rdfruleStageAfterResultsToPool
     *            the rdfruleStageAfterResultsToPool to set
     */
    public static void setRdfruleStageAfterResultsToPool(final URI rdfruleStageAfterResultsToPool)
    {
        NormalisationRuleSchema.rdfruleStageAfterResultsToPool = rdfruleStageAfterResultsToPool;
    }
    
    /**
     * @param rdfruleStageBeforeResultsImport
     *            the rdfruleStageBeforeResultsImport to set
     */
    public static void setRdfruleStageBeforeResultsImport(final URI rdfruleStageBeforeResultsImport)
    {
        NormalisationRuleSchema.rdfruleStageBeforeResultsImport = rdfruleStageBeforeResultsImport;
    }
    
    /**
     * @param rdfruleStageQueryVariables
     *            the rdfruleStageQueryVariables to set
     */
    public static void setRdfruleStageQueryVariables(final URI rdfruleStageQueryVariables)
    {
        NormalisationRuleSchema.rdfruleStageQueryVariables = rdfruleStageQueryVariables;
    }
    
    /**
     * @param rdfruleTypeValidForStage
     *            the rdfruleTypeValidForStage to set
     */
    public static void setRdfruleTypeValidForStage(final URI rdfruleTypeValidForStage)
    {
        NormalisationRuleSchema.rdfruleTypeValidForStage = rdfruleTypeValidForStage;
    }
    
    public static final QueryAllSchema NORMALISATION_RULE_SCHEMA = new NormalisationRuleSchema();
    
    /**
     * Default constructor, uses the name of this class as the name
     */
    public NormalisationRuleSchema()
    {
        this(NormalisationRuleSchema.class.getName());
    }
    
    /**
     * @param nextName The name for this schema object
     */
    public NormalisationRuleSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            con.setAutoCommit(false);
            
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
            
            NormalisationRuleSchema.log.error("RepositoryException: " + re.getMessage());
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
