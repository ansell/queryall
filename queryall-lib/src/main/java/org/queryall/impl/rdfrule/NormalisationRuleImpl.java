package org.queryall.impl.rdfrule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.Profile;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.enumerations.Constants;
import org.queryall.exception.InvalidStageException;
import org.queryall.impl.NamespaceEntryImpl;
import org.queryall.impl.ProfileImpl;
import org.queryall.impl.ProjectImpl;
import org.queryall.utils.ProfileUtils;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class NormalisationRuleImpl implements NormalisationRule
{
    protected static final Logger log = LoggerFactory.getLogger(NormalisationRuleImpl.class);
    protected static final boolean _TRACE = NormalisationRuleImpl.log.isTraceEnabled();
    protected static final boolean _DEBUG = NormalisationRuleImpl.log.isDebugEnabled();
    protected static final boolean _INFO = NormalisationRuleImpl.log.isInfoEnabled();
    
    /**
     * @return the normalisationRuleTypeUri
     */
    public static URI getNormalisationRuleTypeUri()
    {
        return NormalisationRuleImpl.normalisationRuleTypeUri;
    }
    
    /**
     * @return the rdfruleDescription
     */
    public static URI getRdfruleDescription()
    {
        return NormalisationRuleImpl.rdfruleDescription;
    }
    
    /**
     * @return the rdfruleHasRelatedNamespace
     */
    public static URI getRdfruleHasRelatedNamespace()
    {
        return NormalisationRuleImpl.rdfruleHasRelatedNamespace;
    }
    
    /**
     * @return the rdfruleOrder
     */
    public static URI getRdfruleOrder()
    {
        return NormalisationRuleImpl.rdfruleOrder;
    }
    
    /**
     * @return the rdfruleStage
     */
    public static URI getRdfruleStage()
    {
        return NormalisationRuleImpl.rdfruleStage;
    }
    
    /**
     * @return the rdfruleStageAfterQueryCreation
     */
    public static URI getRdfruleStageAfterQueryCreation()
    {
        return NormalisationRuleImpl.rdfruleStageAfterQueryCreation;
    }
    
    /**
     * @return the rdfruleStageAfterQueryParsing
     */
    public static URI getRdfruleStageAfterQueryParsing()
    {
        return NormalisationRuleImpl.rdfruleStageAfterQueryParsing;
    }
    
    /**
     * @return the rdfruleStageAfterResultsImport
     */
    public static URI getRdfruleStageAfterResultsImport()
    {
        return NormalisationRuleImpl.rdfruleStageAfterResultsImport;
    }
    
    /**
     * @return the rdfruleStageAfterResultsToDocument
     */
    public static URI getRdfruleStageAfterResultsToDocument()
    {
        return NormalisationRuleImpl.rdfruleStageAfterResultsToDocument;
    }
    
    /**
     * @return the rdfruleStageAfterResultsToPool
     */
    public static URI getRdfruleStageAfterResultsToPool()
    {
        return NormalisationRuleImpl.rdfruleStageAfterResultsToPool;
    }
    
    /**
     * @return the rdfruleStageBeforeResultsImport
     */
    public static URI getRdfruleStageBeforeResultsImport()
    {
        return NormalisationRuleImpl.rdfruleStageBeforeResultsImport;
    }
    
    /**
     * @return the rdfruleStageQueryVariables
     */
    public static URI getRdfruleStageQueryVariables()
    {
        return NormalisationRuleImpl.rdfruleStageQueryVariables;
    }
    
    /**
     * @return the rdfruleTypeValidForStage
     */
    public static URI getRdfruleTypeValidForStage()
    {
        return NormalisationRuleImpl.rdfruleTypeValidForStage;
    }
    
    /**
     * @param normalisationRuleTypeUri
     *            the normalisationRuleTypeUri to set
     */
    public static void setNormalisationRuleTypeUri(final URI normalisationRuleTypeUri)
    {
        NormalisationRuleImpl.normalisationRuleTypeUri = normalisationRuleTypeUri;
    }
    
    /**
     * @param rdfruleDescription
     *            the rdfruleDescription to set
     */
    public static void setRdfruleDescription(final URI rdfruleDescription)
    {
        NormalisationRuleImpl.rdfruleDescription = rdfruleDescription;
    }
    
    /**
     * @param rdfruleHasRelatedNamespace
     *            the rdfruleHasRelatedNamespace to set
     */
    public static void setRdfruleHasRelatedNamespace(final URI rdfruleHasRelatedNamespace)
    {
        NormalisationRuleImpl.rdfruleHasRelatedNamespace = rdfruleHasRelatedNamespace;
    }
    
    /**
     * @param rdfruleOrder
     *            the rdfruleOrder to set
     */
    public static void setRdfruleOrder(final URI rdfruleOrder)
    {
        NormalisationRuleImpl.rdfruleOrder = rdfruleOrder;
    }
    
    /**
     * @param rdfruleStage
     *            the rdfruleStage to set
     */
    public static void setRdfruleStage(final URI rdfruleStage)
    {
        NormalisationRuleImpl.rdfruleStage = rdfruleStage;
    }
    
    /**
     * @param rdfruleStageAfterQueryCreation
     *            the rdfruleStageAfterQueryCreation to set
     */
    public static void setRdfruleStageAfterQueryCreation(final URI rdfruleStageAfterQueryCreation)
    {
        NormalisationRuleImpl.rdfruleStageAfterQueryCreation = rdfruleStageAfterQueryCreation;
    }
    
    /**
     * @param rdfruleStageAfterQueryParsing
     *            the rdfruleStageAfterQueryParsing to set
     */
    public static void setRdfruleStageAfterQueryParsing(final URI rdfruleStageAfterQueryParsing)
    {
        NormalisationRuleImpl.rdfruleStageAfterQueryParsing = rdfruleStageAfterQueryParsing;
    }
    
    /**
     * @param rdfruleStageAfterResultsImport
     *            the rdfruleStageAfterResultsImport to set
     */
    public static void setRdfruleStageAfterResultsImport(final URI rdfruleStageAfterResultsImport)
    {
        NormalisationRuleImpl.rdfruleStageAfterResultsImport = rdfruleStageAfterResultsImport;
    }
    
    /**
     * @param rdfruleStageAfterResultsToDocument
     *            the rdfruleStageAfterResultsToDocument to set
     */
    public static void setRdfruleStageAfterResultsToDocument(final URI rdfruleStageAfterResultsToDocument)
    {
        NormalisationRuleImpl.rdfruleStageAfterResultsToDocument = rdfruleStageAfterResultsToDocument;
    }
    
    /**
     * @param rdfruleStageAfterResultsToPool
     *            the rdfruleStageAfterResultsToPool to set
     */
    public static void setRdfruleStageAfterResultsToPool(final URI rdfruleStageAfterResultsToPool)
    {
        NormalisationRuleImpl.rdfruleStageAfterResultsToPool = rdfruleStageAfterResultsToPool;
    }
    
    /**
     * @param rdfruleStageBeforeResultsImport
     *            the rdfruleStageBeforeResultsImport to set
     */
    public static void setRdfruleStageBeforeResultsImport(final URI rdfruleStageBeforeResultsImport)
    {
        NormalisationRuleImpl.rdfruleStageBeforeResultsImport = rdfruleStageBeforeResultsImport;
    }
    
    /**
     * @param rdfruleStageQueryVariables
     *            the rdfruleStageQueryVariables to set
     */
    public static void setRdfruleStageQueryVariables(final URI rdfruleStageQueryVariables)
    {
        NormalisationRuleImpl.rdfruleStageQueryVariables = rdfruleStageQueryVariables;
    }
    
    /**
     * @param rdfruleTypeValidForStage
     *            the rdfruleTypeValidForStage to set
     */
    public static void setRdfruleTypeValidForStage(final URI rdfruleTypeValidForStage)
    {
        NormalisationRuleImpl.rdfruleTypeValidForStage = rdfruleTypeValidForStage;
    }
    
    protected Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key;
    
    private String description;
    
    private URI curationStatus = ProjectImpl.getProjectNotCuratedUri();
    
    private URI profileIncludeExcludeOrder = ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri();
    
    private Collection<URI> relatedNamespaces = new ArrayList<URI>(2);
    
    protected Collection<URI> stages = new ArrayList<URI>(3);
    
    private Collection<URI> validStages = new ArrayList<URI>(7);
    
    private int order = 100;
    
    private String title;
    
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
        
        NormalisationRuleImpl.setNormalisationRuleTypeUri(f.createURI(baseUri, "NormalisationRule"));
        
        NormalisationRuleImpl.version2NormalisationRuleTypeUri = f.createURI(baseUri, "RdfRule");
        
        NormalisationRuleImpl.setRdfruleDescription(f.createURI(baseUri, "description"));
        NormalisationRuleImpl.setRdfruleOrder(f.createURI(baseUri, "order"));
        NormalisationRuleImpl.setRdfruleStage(f.createURI(baseUri, "stage"));
        NormalisationRuleImpl.setRdfruleHasRelatedNamespace(f.createURI(baseUri, "hasRelatedNamespace"));
        
        NormalisationRuleImpl.setRdfruleStageQueryVariables(f.createURI(baseUri, "queryVariables"));
        
        NormalisationRuleImpl.setRdfruleStageAfterQueryCreation(f.createURI(baseUri, "afterQueryCreation"));
        
        NormalisationRuleImpl.setRdfruleStageAfterQueryParsing(f.createURI(baseUri, "afterQueryParsing"));
        
        NormalisationRuleImpl.setRdfruleStageBeforeResultsImport(f.createURI(baseUri, "beforeResultsImport"));
        
        NormalisationRuleImpl.setRdfruleStageAfterResultsImport(f.createURI(baseUri, "afterResultsImport"));
        
        NormalisationRuleImpl.setRdfruleStageAfterResultsToPool(f.createURI(baseUri, "afterResultsToPool"));
        
        NormalisationRuleImpl.setRdfruleStageAfterResultsToDocument(f.createURI(baseUri, "afterResultsToDocument"));
        
        NormalisationRuleImpl.setRdfruleTypeValidForStage(f.createURI(baseUri, "typeValidForStage"));
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(NormalisationRuleImpl.getNormalisationRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            
            con.add(NormalisationRuleImpl.getNormalisationRuleTypeUri(),
                    RDFS.LABEL,
                    f.createLiteral("A normalisation rule intended to denormalise parts of queries to match endpoints, and renormalise the output of the query to match the normalised form."),
                    contextUri);
            
            if(modelVersion == 1)
            {
                con.add(NormalisationRuleImpl.getRdfruleDescription(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contextUri);
                con.add(NormalisationRuleImpl.getRdfruleDescription(), RDFS.SUBPROPERTYOF, RDFS.COMMENT, contextUri);
                con.add(NormalisationRuleImpl.getRdfruleDescription(), RDFS.RANGE, RDFS.LITERAL, contextUri);
                con.add(NormalisationRuleImpl.getRdfruleDescription(), RDFS.DOMAIN,
                        NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextUri);
                con.add(NormalisationRuleImpl.getRdfruleDescription(), RDFS.LABEL,
                        f.createLiteral("The description of a normalisation rule."), contextUri);
                
            }
            
            con.add(NormalisationRuleImpl.getRdfruleOrder(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(NormalisationRuleImpl.getRdfruleOrder(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(NormalisationRuleImpl.getRdfruleOrder(), RDFS.DOMAIN,
                    NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextUri);
            con.add(NormalisationRuleImpl.getRdfruleOrder(),
                    RDFS.LABEL,
                    f.createLiteral("The ordering variable that is used to identify what order the normalisation rules are designed to be applied in."),
                    contextUri);
            
            con.add(NormalisationRuleImpl.getRdfruleHasRelatedNamespace(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(NormalisationRuleImpl.getRdfruleHasRelatedNamespace(), RDFS.RANGE,
                    NamespaceEntryImpl.getNamespaceTypeUri(), contextUri);
            con.add(NormalisationRuleImpl.getRdfruleHasRelatedNamespace(), RDFS.DOMAIN,
                    NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextUri);
            con.add(NormalisationRuleImpl.getRdfruleHasRelatedNamespace(),
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
            
            NormalisationRuleImpl.log.error("RepositoryException: " + re.getMessage());
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
    
    protected NormalisationRuleImpl()
    {
        
    }
    
    // keyToUse is the URI of the next instance that can be found in
    // myRepository
    public NormalisationRuleImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final Collection<URI> tempRelatedNamespaces = new HashSet<URI>();
        final Collection<URI> tempStages = new HashSet<URI>();
        
        for(final Statement nextStatement : inputStatements)
        {
            if(NormalisationRuleImpl._DEBUG)
            {
                NormalisationRuleImpl.log.debug("NormalisationRule: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(NormalisationRuleImpl.getNormalisationRuleTypeUri()))
            {
                if(NormalisationRuleImpl._TRACE)
                {
                    NormalisationRuleImpl.log.trace("NormalisationRule: found valid type predicate for URI: "
                            + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.getProjectCurationStatusUri()))
            {
                this.curationStatus = (URI)nextStatement.getObject();
            }
            else if(nextStatement.getPredicate().equals(NormalisationRuleImpl.getRdfruleOrder()))
            {
                this.order = RdfUtils.getIntegerFromValue(nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(NormalisationRuleImpl.getRdfruleDescription())
                    || nextStatement.getPredicate().equals(RDFS.COMMENT))
            {
                this.description = nextStatement.getObject().stringValue();
            }
            else if(nextStatement.getPredicate().equals(NormalisationRuleImpl.getRdfruleHasRelatedNamespace()))
            {
                this.addRelatedNamespaces((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(NormalisationRuleImpl.getRdfruleStage()))
            {
                tempStages.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileIncludeExcludeOrderUri()))
            {
                this.setProfileIncludeExcludeOrder((URI)nextStatement.getObject());
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        // this.setRelatedNamespaces(tempRelatedNamespaces);
        this.stages = tempStages;
        
        if(NormalisationRuleImpl._DEBUG)
        {
            NormalisationRuleImpl.log.debug("NormalisationRuleImpl.fromRdf: would have returned... result="
                    + this.toString());
        }
    }
    
    /**
     * 
     * @param nextRelatedNamespace
     */
    @Override
    public void addRelatedNamespaces(final URI nextRelatedNamespace)
    {
        this.relatedNamespaces.add(nextRelatedNamespace);
    }
    
    /**
     * @return the Stages
     */
    @Override
    public void addStage(final URI stage) throws InvalidStageException
    {
        if(this.stages == null)
        {
            this.stages = new ArrayList<URI>();
        }
        
        if(this.validInStage(stage))
        {
            this.stages.add(stage);
        }
        else
        {
            throw new InvalidStageException("Attempted to add a stage that was not in the list of valid stages");
        }
    }
    
    @Override
    public void addUnrecognisedStatement(final Statement unrecognisedStatement)
    {
        this.unrecognisedStatements.add(unrecognisedStatement);
    }
    
    /**
     * @return the validStages
     */
    protected void addValidStage(final URI validStage)
    {
        if(this.validStages == null)
        {
            this.validStages = new ArrayList<URI>();
        }
        
        this.validStages.add(validStage);
    }
    
    @Override
    public int compareTo(final NormalisationRule otherRule)
    {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        
        if(this == otherRule)
        {
            return EQUAL;
        }
        
        if(this.getOrder() < otherRule.getOrder())
        {
            return BEFORE;
        }
        
        if(this.getOrder() > otherRule.getOrder())
        {
            return AFTER;
        }
        
        return this.getKey().stringValue().compareTo(otherRule.getKey().stringValue());
    }
    
    @Override
    public URI getCurationStatus()
    {
        return this.curationStatus;
    }
    
    /**
     * @return the namespace used to represent objects of this type by default
     */
    @Override
    public QueryAllNamespaces getDefaultNamespace()
    {
        return QueryAllNamespaces.RDFRULE;
    }
    
    @Override
    public String getDescription()
    {
        return this.description;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Collection<URI> getElementTypes()
    {
        final Collection<URI> ruleTypes = new ArrayList<URI>(2);
        ruleTypes.add(NormalisationRuleImpl.getNormalisationRuleTypeUri());
        
        return ruleTypes;
    }
    
    /**
     * @return the key
     */
    @Override
    public URI getKey()
    {
        return this.key;
    }
    
    @Override
    public int getOrder()
    {
        return this.order;
    }
    
    @Override
    public URI getProfileIncludeExcludeOrder()
    {
        return this.profileIncludeExcludeOrder;
    }
    
    /**
     * @return the relatedNamespaces
     */
    @Override
    public Collection<URI> getRelatedNamespaces()
    {
        return this.relatedNamespaces;
    }
    
    /**
     * @return the Stages
     */
    @Override
    public Collection<URI> getStages()
    {
        return Collections.unmodifiableCollection(this.stages);
    }
    
    @Override
    public String getTitle()
    {
        return this.title;
    }
    
    @Override
    public Collection<Statement> getUnrecognisedStatements()
    {
        return this.unrecognisedStatements;
    }
    
    /**
     * @return the validStages
     */
    @Override
    public Collection<URI> getValidStages()
    {
        return Collections.unmodifiableCollection(this.validStages);
    }
    
    @Override
    public boolean isUsedWithProfileList(final List<Profile> orderedProfileList, final boolean allowImplicitInclusions,
            final boolean includeNonProfileMatched)
    {
        return ProfileUtils.isUsedWithProfileList(this, orderedProfileList, allowImplicitInclusions,
                includeNonProfileMatched);
    }
    
    @Override
    public Object normaliseByStage(final URI stage, final Object input)
    {
        if(!this.validStages.contains(stage))
        {
            if(NormalisationRuleImpl._TRACE)
            {
                NormalisationRuleImpl.log
                        .trace("NormalisationRuleImpl.normaliseByStage : found an invalid stage for this type of rule stage="
                                + stage);
            }
            
            return input;
        }
        
        if(!this.stages.contains(stage))
        {
            if(NormalisationRuleImpl._DEBUG)
            {
                NormalisationRuleImpl.log
                        .debug("NormalisationRuleImpl.normaliseByStage : found an inapplicable stage for this type of rule key="
                                + this.getKey().stringValue() + " stage=" + stage);
            }
            
            return input;
        }
        
        if(stage.equals(NormalisationRuleImpl.getRdfruleStageQueryVariables()))
        {
            return this.stageQueryVariables(input);
        }
        else if(stage.equals(NormalisationRuleImpl.getRdfruleStageAfterQueryCreation()))
        {
            return this.stageAfterQueryCreation(input);
        }
        else if(stage.equals(NormalisationRuleImpl.getRdfruleStageAfterQueryParsing()))
        {
            return this.stageAfterQueryParsing(input);
        }
        else if(stage.equals(NormalisationRuleImpl.getRdfruleStageBeforeResultsImport()))
        {
            return this.stageBeforeResultsImport(input);
        }
        else if(stage.equals(NormalisationRuleImpl.getRdfruleStageAfterResultsImport()))
        {
            return this.stageAfterResultsImport(input);
        }
        else if(stage.equals(NormalisationRuleImpl.getRdfruleStageAfterResultsToPool()))
        {
            return this.stageAfterResultsToPool(input);
        }
        else if(stage.equals(NormalisationRuleImpl.getRdfruleStageAfterResultsToDocument()))
        {
            return this.stageAfterResultsToDocument(input);
        }
        
        throw new RuntimeException("Normalisation rule stage unknown : stage=" + stage);
    }
    
    @Override
    public void setCurationStatus(final URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    public void setDescription(final String description)
    {
        this.description = description;
    }
    
    /**
     * @param key
     *            the key to set
     */
    @Override
    public void setKey(final String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }
    
    @Override
    public void setKey(final URI nextKey)
    {
        this.key = nextKey;
    }
    
    @Override
    public void setOrder(final int order)
    {
        this.order = order;
    }
    
    @Override
    public void setProfileIncludeExcludeOrder(final URI profileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = profileIncludeExcludeOrder;
    }
    
    @Override
    public void setTitle(final String title)
    {
        this.title = title;
    }
    
    /**
     * @param validStages
     *            the validStages to set
     */
    protected void setValidStages(final Collection<URI> nextValidStages)
    {
        this.validStages = nextValidStages;
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            if(NormalisationRuleImpl._DEBUG)
            {
                NormalisationRuleImpl.log.debug("NormalisationRuleImpl.toRdf: keyToUse=" + keyToUse);
            }
            
            final URI keyUri = this.getKey();
            final Literal descriptionLiteral = f.createLiteral(this.getDescription());
            final Literal orderLiteral = f.createLiteral(this.getOrder());
            final URI profileIncludeExcludeOrderLiteral = this.getProfileIncludeExcludeOrder();
            
            URI curationStatusLiteral = null;
            
            if((this.curationStatus == null))
            {
                curationStatusLiteral = ProjectImpl.getProjectNotCuratedUri();
            }
            else
            {
                curationStatusLiteral = this.curationStatus;
            }
            
            con.setAutoCommit(false);
            
            if(modelVersion <= 2)
            {
                con.add(keyUri, RDF.TYPE, NormalisationRuleImpl.version2NormalisationRuleTypeUri, keyToUse);
            }
            else
            {
                con.add(keyUri, RDF.TYPE, NormalisationRuleImpl.getNormalisationRuleTypeUri(), keyToUse);
            }
            
            con.add(keyUri, ProjectImpl.getProjectCurationStatusUri(), curationStatusLiteral, keyToUse);
            
            if(modelVersion == 1)
            {
                con.add(keyUri, NormalisationRuleImpl.getRdfruleDescription(), descriptionLiteral, keyToUse);
            }
            else
            {
                con.add(keyUri, RDFS.COMMENT, descriptionLiteral, keyToUse);
            }
            con.add(keyUri, NormalisationRuleImpl.getRdfruleOrder(), orderLiteral, keyToUse);
            con.add(keyUri, ProfileImpl.getProfileIncludeExcludeOrderUri(), profileIncludeExcludeOrderLiteral, keyToUse);
            
            if(this.getRelatedNamespaces() != null)
            {
                for(final URI nextRelatedNamespace : this.getRelatedNamespaces())
                {
                    con.add(keyUri, NormalisationRuleImpl.getRdfruleHasRelatedNamespace(), nextRelatedNamespace,
                            keyToUse);
                }
            }
            
            if(this.stages != null)
            {
                for(final URI nextStage : this.stages)
                {
                    con.add(keyUri, NormalisationRuleImpl.getRdfruleStage(), nextStage, keyToUse);
                }
            }
            
            if(this.unrecognisedStatements != null)
            {
                for(final Statement nextUnrecognisedStatement : this.unrecognisedStatements)
                {
                    con.add(nextUnrecognisedStatement, keyToUse);
                }
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            NormalisationRuleImpl.log.error("RepositoryException: " + re.getMessage());
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
    
    @Override
    public boolean usedInStage(final org.openrdf.model.URI stage)
    {
        return this.stages.contains(stage);
    }
    
    @Override
    public boolean validInStage(final org.openrdf.model.URI stage)
    {
        return this.validStages.contains(stage);
    }
}
