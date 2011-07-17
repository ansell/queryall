package org.queryall.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

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


import org.queryall.api.NormalisationRule;
import org.queryall.api.Profile;
import org.queryall.exception.InvalidStageException;
import org.queryall.helpers.Constants;
import org.queryall.helpers.ProfileUtils;
import org.queryall.helpers.Settings;
import org.queryall.helpers.StringUtils;
import org.queryall.helpers.RdfUtils;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class NormalisationRuleImpl implements NormalisationRule
{

    protected static final Logger log = Logger.getLogger(NormalisationRuleImpl.class.getName());
    protected static final boolean _TRACE = log.isTraceEnabled();
    protected static final boolean _DEBUG = log.isDebugEnabled();
    protected static final boolean _INFO = log.isInfoEnabled();

    private static final String defaultNamespace = Settings.getSettings().getNamespaceForNormalisationRule();
    
    protected Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key;
    private String description;
    private URI curationStatus = ProjectImpl.getProjectNotCuratedUri();
    
    private URI profileIncludeExcludeOrder = ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri();
    private Collection<URI> relatedNamespaces;
    
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
    
    
    
    public static String rdfruleNamespace;
    
    static
    {
        NormalisationRuleImpl.rdfruleNamespace = Settings.getSettings().getOntologyTermUriPrefix()
                + Settings.getSettings().getNamespaceForNormalisationRule()
                + Settings.getSettings().getOntologyTermUriSuffix();
        
        
        final ValueFactory f = Constants.valueFactory;
        
        NormalisationRuleImpl.setNormalisationRuleTypeUri(f.createURI(NormalisationRuleImpl.rdfruleNamespace , "NormalisationRule"));
                
        NormalisationRuleImpl.version2NormalisationRuleTypeUri = f.createURI(NormalisationRuleImpl.rdfruleNamespace,"RdfRule");
        
        NormalisationRuleImpl.setRdfruleDescription(f
                .createURI(NormalisationRuleImpl.rdfruleNamespace
                        , "description"));
        NormalisationRuleImpl.setRdfruleOrder(f
                .createURI(NormalisationRuleImpl.rdfruleNamespace , "order"));
        NormalisationRuleImpl.setRdfruleStage(f
                .createURI(NormalisationRuleImpl.rdfruleNamespace , "stage"));
        NormalisationRuleImpl.setRdfruleHasRelatedNamespace(f
                .createURI(NormalisationRuleImpl.rdfruleNamespace
                        , "hasRelatedNamespace"));
                
        NormalisationRuleImpl.setRdfruleStageQueryVariables(f
                .createURI(NormalisationRuleImpl.rdfruleNamespace , "queryVariables"));

        NormalisationRuleImpl.setRdfruleStageAfterQueryCreation(f
                .createURI(NormalisationRuleImpl.rdfruleNamespace , "afterQueryCreation"));

        NormalisationRuleImpl.setRdfruleStageAfterQueryParsing(f
                .createURI(NormalisationRuleImpl.rdfruleNamespace , "afterQueryParsing"));

        NormalisationRuleImpl.setRdfruleStageBeforeResultsImport(f
                .createURI(NormalisationRuleImpl.rdfruleNamespace , "beforeResultsImport"));
        
        NormalisationRuleImpl.setRdfruleStageAfterResultsImport(f
                .createURI(NormalisationRuleImpl.rdfruleNamespace , "afterResultsImport"));
        
        NormalisationRuleImpl.setRdfruleStageAfterResultsToPool(f
                .createURI(NormalisationRuleImpl.rdfruleNamespace , "afterResultsToPool"));

        NormalisationRuleImpl.setRdfruleStageAfterResultsToDocument(f
                .createURI(NormalisationRuleImpl.rdfruleNamespace , "afterResultsToDocument"));

        NormalisationRuleImpl.setRdfruleTypeValidForStage(f.createURI(NormalisationRuleImpl.rdfruleNamespace , "typeValidForStage"));
    }
    
    protected NormalisationRuleImpl()
    {
    	
    }
    
    // keyToUse is the URI of the next instance that can be found in
    // myRepository
    public NormalisationRuleImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion)
            throws OpenRDFException
    {
        final Collection<URI> tempRelatedNamespaces = new HashSet<URI>();
        final Collection<URI> tempStages = new HashSet<URI>();
        
        for(final Statement nextStatement : inputStatements)
        {
            if(NormalisationRuleImpl._DEBUG)
            {
                log
                        .debug("NormalisationRule: nextStatement: "
                                + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    &&  nextStatement.getObject().equals(
                            NormalisationRuleImpl.getNormalisationRuleTypeUri())
              )
            {
                if(_TRACE)
                {
                    log
                            .trace("NormalisationRule: found valid type predicate for URI: "
                                    + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(
                    ProjectImpl.getProjectCurationStatusUri()))
            {
                this.curationStatus = (URI)nextStatement.getObject();
            }
            else if(nextStatement.getPredicate().equals(
                    NormalisationRuleImpl.getRdfruleOrder()))
            {
                this.order = RdfUtils.getIntegerFromValue(nextStatement
                        .getObject());
            }
            else if(nextStatement.getPredicate().equals(
                    NormalisationRuleImpl.getRdfruleDescription()) || nextStatement.getPredicate().equals(RDFS.COMMENT))
            {
                this.description = nextStatement.getObject().stringValue();
            }
            else if(nextStatement.getPredicate().equals(
                    NormalisationRuleImpl.getRdfruleHasRelatedNamespace()))
            {
                tempRelatedNamespaces.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(
                    NormalisationRuleImpl.getRdfruleStage()))
            {
                tempStages.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(
                    ProfileImpl.getProfileIncludeExcludeOrderUri()))
            {
                this.setProfileIncludeExcludeOrder((URI)nextStatement.getObject());
            }
			else
			{
			    this.addUnrecognisedStatement(nextStatement);
			}
        }
        
        this.relatedNamespaces = tempRelatedNamespaces;
        this.stages = tempStages;
        
        if(_DEBUG)
        {
            log
                    .debug("NormalisationRuleImpl.fromRdf: would have returned... result="
                            + this.toString());
        }
    }

    @Override
	public Object normaliseByStage(URI stage, Object input)
    {
        if(!validStages.contains(stage))
        {
            if(_TRACE)
            {
                log.trace("NormalisationRuleImpl.normaliseByStage : found an invalid stage for this type of rule stage="+stage);
            }
            
            return input;
        }

        if(!stages.contains(stage))
        {
            if(_DEBUG)
            {
                log.debug("NormalisationRuleImpl.normaliseByStage : found an inapplicable stage for this type of rule key="+getKey().stringValue()+" stage="+stage);
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
        
        throw new RuntimeException("Normalisation rule stage unknown : stage="+stage);
    }

    @Override
	public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            if(_DEBUG)
            {
                log.debug("NormalisationRuleImpl.toRdf: keyToUse="+ keyToUse);
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
                con.add(keyUri, RDF.TYPE, NormalisationRuleImpl.getNormalisationRuleTypeUri(),
                		keyToUse);
            }
            
            con.add(keyUri, ProjectImpl.getProjectCurationStatusUri(),
                    curationStatusLiteral, keyToUse);

            if(modelVersion == 1)
            {
                con.add(keyUri, NormalisationRuleImpl.getRdfruleDescription(),
                    descriptionLiteral, keyToUse);
            }
            else
            {
                con.add(keyUri, RDFS.COMMENT,
                    descriptionLiteral, keyToUse);
            }
            con.add(keyUri, NormalisationRuleImpl.getRdfruleOrder(), orderLiteral,
            		keyToUse);
            con.add(keyUri, ProfileImpl.getProfileIncludeExcludeOrderUri(),
                    profileIncludeExcludeOrderLiteral, keyToUse);
            
            if(this.relatedNamespaces != null)
            {
                for(final URI nextRelatedNamespace : this.relatedNamespaces)
                {
                    con.add(keyUri, NormalisationRuleImpl.getRdfruleHasRelatedNamespace(), nextRelatedNamespace, keyToUse);
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
        catch (final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            log.error("RepositoryException: "
                    + re.getMessage());
        }
        finally
        {
            con.close();
        }
        
        return false;
    }

    public static boolean schemaToRdf(Repository myRepository, URI contextUri, int modelVersion) throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(NormalisationRuleImpl.getNormalisationRuleTypeUri(), RDF.TYPE, OWL.CLASS,
                    contextUri);
            
            con.add(NormalisationRuleImpl.getNormalisationRuleTypeUri(), RDFS.LABEL, 
                f.createLiteral("A normalisation rule intended to denormalise parts of queries to match endpoints, and renormalise the output of the query to match the normalised form."),
                    contextUri);

            if(modelVersion == 1)
            {
                con.add(NormalisationRuleImpl.getRdfruleDescription(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contextUri);
                con.add(NormalisationRuleImpl.getRdfruleDescription(),
                    RDFS.SUBPROPERTYOF, RDFS.COMMENT, contextUri);
                con.add(NormalisationRuleImpl.getRdfruleDescription(), RDFS.RANGE,
                    RDFS.LITERAL, contextUri);
                con.add(NormalisationRuleImpl.getRdfruleDescription(), RDFS.DOMAIN,
                    NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextUri);
                con.add(NormalisationRuleImpl.getRdfruleDescription(), RDFS.LABEL, 
                    f.createLiteral("The description of a normalisation rule."),
                    contextUri);

            }
            
            con.add(NormalisationRuleImpl.getRdfruleOrder(), RDF.TYPE,
                    OWL.DATATYPEPROPERTY, contextUri);
            con.add(NormalisationRuleImpl.getRdfruleOrder(), RDFS.RANGE,
                    RDFS.LITERAL, contextUri);
            con.add(NormalisationRuleImpl.getRdfruleOrder(), RDFS.DOMAIN,
                    NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextUri);
            con.add(NormalisationRuleImpl.getRdfruleOrder(), RDFS.LABEL, 
                f.createLiteral("The ordering variable that is used to identify what order the normalisation rules are designed to be applied in."),
                    contextUri);

            
            con.add(NormalisationRuleImpl.getRdfruleHasRelatedNamespace(), RDF.TYPE,
                    OWL.OBJECTPROPERTY, contextUri);
            con.add(NormalisationRuleImpl.getRdfruleHasRelatedNamespace(), RDFS.RANGE,
                    NamespaceEntryImpl.getNamespaceTypeUri(), contextUri);
            con.add(NormalisationRuleImpl.getRdfruleHasRelatedNamespace(), RDFS.DOMAIN,
                    NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextUri);
            con.add(NormalisationRuleImpl.getRdfruleHasRelatedNamespace(), RDFS.LABEL, 
                f.createLiteral("An informative property indicating that the target namespace is somehow related to this rule."),
                    contextUri);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch (final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            if(con != null)
            {
                con.rollback();
            }
            
            log.error("RepositoryException: "
                    + re.getMessage());
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
     * @return the key
     */
    @Override
	public URI getKey()
    {
        return key;
    }

    /**
     * @param key the key to set
     */
    @Override
	public void setKey(String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }

    @Override
	public void setKey(URI nextKey)
    {
        this.key = nextKey;
    }    

    @Override
	public void setTitle(String title)
    {
        this.title = title;
    }

    @Override
	public String getTitle()
    {
        return this.title;
    }

    
    /**
     * @return the validStages
     */
    @Override
	public Collection<URI> getValidStages()
    {
        return Collections.unmodifiableCollection(validStages);
    }

    /**
     * @param validStages the validStages to set
     */
	protected void setValidStages(Collection<URI> nextValidStages)
    {
        this.validStages = nextValidStages;
    }
    
    /**
     * @return the validStages
     */
	protected void addValidStage(URI validStage)
    {
    	if(validStages == null)
    		validStages = new ArrayList<URI>();
    	
    	validStages.add(validStage);
    }
    
    /**
     * @return the Stages
     */
    @Override
	public Collection<URI> getStages()
    {
        return Collections.unmodifiableCollection(stages);
    }

    /**
     * @param Stages the Stages to set
     */
    @Override
	public void setStages(Collection<URI> nextStages) throws InvalidStageException
    {
    	try
    	{
	    	for(URI nextStage : nextStages)
	    	{
	    		addStage(nextStage);
	    	}
    	}
    	catch(InvalidStageException ise)
    	{
    		this.stages = new ArrayList<URI>();
    		throw ise;
    	}
    }
    
    /**
     * @return the Stages
     */
    @Override
	public void addStage(URI stage) throws InvalidStageException
    {
    	if(stages == null)
    		stages = new ArrayList<URI>();
    	
    	if(validInStage(stage))
    		stages.add(stage);
    	else
    		throw new InvalidStageException("Attempted to add a stage that was not in the list of valid stages");
    }
    
    public boolean validInStage(org.openrdf.model.URI stage)
    {
    	return this.validStages.contains(stage);
    }

    public boolean usedInStage(org.openrdf.model.URI stage)
    {
    	return this.stages.contains(stage);
    }

    /**
     * @return the namespace used to represent objects of this type by default
     */
    @Override
	public String getDefaultNamespace()
    {
        return defaultNamespace;
    }
    

    /**
     * @return a collection of the relevant element types that are implemented by this class, including abstract implementations
     */
    @Override
	public Collection<URI> getElementTypes()
    {
    	Collection<URI> ruleTypes = new ArrayList<URI>(2);
        ruleTypes.add(getNormalisationRuleTypeUri());
        
        return ruleTypes;
    }
    
    @Override
	public int getOrder()
    {
        return order;
    }
    
    @Override
	public void setOrder(int order)
    {
        this.order = order;
    }

    @Override
	public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
	public int compareTo(NormalisationRule otherRule)
    {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
    
        if ( this == otherRule ) 
            return EQUAL;
    
        if (this.getOrder() < otherRule.getOrder()) 
            return BEFORE;
        
        if (this.getOrder() > otherRule.getOrder()) 
            return AFTER;

        return this.getKey().stringValue().compareTo(otherRule.getKey().stringValue());
    }

    @Override
	public URI getProfileIncludeExcludeOrder()
    {
        return profileIncludeExcludeOrder;
    }

    @Override
	public void setProfileIncludeExcludeOrder(URI profileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = profileIncludeExcludeOrder;
    }

    
    
    @Override
	public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        unrecognisedStatements.add(unrecognisedStatement);
    }

    @Override
	public Collection<Statement> getUnrecognisedStatements()
    {
        return unrecognisedStatements;
    }

    @Override
	public URI getCurationStatus()
    {
        return curationStatus;
    }
    
    @Override
	public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }

    @Override
	public boolean isUsedWithProfileList(List<Profile> orderedProfileList,
            boolean allowImplicitInclusions, boolean includeNonProfileMatched)
    {
        return ProfileUtils.isUsedWithProfileList(this, orderedProfileList, allowImplicitInclusions, includeNonProfileMatched);
    }    

    /**
	 * @param normalisationRuleTypeUri the normalisationRuleTypeUri to set
	 */
	public static void setNormalisationRuleTypeUri(
			URI normalisationRuleTypeUri) {
		NormalisationRuleImpl.normalisationRuleTypeUri = normalisationRuleTypeUri;
	}

	/**
	 * @return the normalisationRuleTypeUri
	 */
	public static URI getNormalisationRuleTypeUri() {
		return normalisationRuleTypeUri;
	}

	/**
	 * @param rdfruleDescription the rdfruleDescription to set
	 */
	public static void setRdfruleDescription(URI rdfruleDescription) {
		NormalisationRuleImpl.rdfruleDescription = rdfruleDescription;
	}

	/**
	 * @return the rdfruleDescription
	 */
	public static URI getRdfruleDescription() {
		return rdfruleDescription;
	}

	/**
	 * @param rdfruleOrder the rdfruleOrder to set
	 */
	public static void setRdfruleOrder(URI rdfruleOrder) {
		NormalisationRuleImpl.rdfruleOrder = rdfruleOrder;
	}

	/**
	 * @return the rdfruleOrder
	 */
	public static URI getRdfruleOrder() {
		return rdfruleOrder;
	}

	/**
	 * @param rdfruleStage the rdfruleStage to set
	 */
	public static void setRdfruleStage(URI rdfruleStage) {
		NormalisationRuleImpl.rdfruleStage = rdfruleStage;
	}

	/**
	 * @return the rdfruleStage
	 */
	public static URI getRdfruleStage() {
		return rdfruleStage;
	}

	/**
	 * @param rdfruleHasRelatedNamespace the rdfruleHasRelatedNamespace to set
	 */
	public static void setRdfruleHasRelatedNamespace(
			URI rdfruleHasRelatedNamespace) {
		NormalisationRuleImpl.rdfruleHasRelatedNamespace = rdfruleHasRelatedNamespace;
	}

	/**
	 * @return the rdfruleHasRelatedNamespace
	 */
	public static URI getRdfruleHasRelatedNamespace() {
		return rdfruleHasRelatedNamespace;
	}

	/**
	 * @param rdfruleStageQueryVariables the rdfruleStageQueryVariables to set
	 */
	public static void setRdfruleStageQueryVariables(
			URI rdfruleStageQueryVariables) {
		NormalisationRuleImpl.rdfruleStageQueryVariables = rdfruleStageQueryVariables;
	}

	/**
	 * @return the rdfruleStageQueryVariables
	 */
	public static URI getRdfruleStageQueryVariables() {
		return rdfruleStageQueryVariables;
	}

	/**
	 * @param rdfruleStageAfterQueryCreation the rdfruleStageAfterQueryCreation to set
	 */
	public static void setRdfruleStageAfterQueryCreation(
			URI rdfruleStageAfterQueryCreation) {
		NormalisationRuleImpl.rdfruleStageAfterQueryCreation = rdfruleStageAfterQueryCreation;
	}

	/**
	 * @return the rdfruleStageAfterQueryCreation
	 */
	public static URI getRdfruleStageAfterQueryCreation() {
		return rdfruleStageAfterQueryCreation;
	}

	/**
	 * @param rdfruleStageAfterQueryParsing the rdfruleStageAfterQueryParsing to set
	 */
	public static void setRdfruleStageAfterQueryParsing(
			URI rdfruleStageAfterQueryParsing) {
		NormalisationRuleImpl.rdfruleStageAfterQueryParsing = rdfruleStageAfterQueryParsing;
	}

	/**
	 * @return the rdfruleStageAfterQueryParsing
	 */
	public static URI getRdfruleStageAfterQueryParsing() {
		return rdfruleStageAfterQueryParsing;
	}

	/**
	 * @param rdfruleStageBeforeResultsImport the rdfruleStageBeforeResultsImport to set
	 */
	public static void setRdfruleStageBeforeResultsImport(
			URI rdfruleStageBeforeResultsImport) {
		NormalisationRuleImpl.rdfruleStageBeforeResultsImport = rdfruleStageBeforeResultsImport;
	}

	/**
	 * @return the rdfruleStageBeforeResultsImport
	 */
	public static URI getRdfruleStageBeforeResultsImport() {
		return rdfruleStageBeforeResultsImport;
	}

	/**
	 * @param rdfruleStageAfterResultsImport the rdfruleStageAfterResultsImport to set
	 */
	public static void setRdfruleStageAfterResultsImport(
			URI rdfruleStageAfterResultsImport) {
		NormalisationRuleImpl.rdfruleStageAfterResultsImport = rdfruleStageAfterResultsImport;
	}

	/**
	 * @return the rdfruleStageAfterResultsImport
	 */
	public static URI getRdfruleStageAfterResultsImport() {
		return rdfruleStageAfterResultsImport;
	}

	/**
	 * @param rdfruleStageAfterResultsToPool the rdfruleStageAfterResultsToPool to set
	 */
	public static void setRdfruleStageAfterResultsToPool(
			URI rdfruleStageAfterResultsToPool) {
		NormalisationRuleImpl.rdfruleStageAfterResultsToPool = rdfruleStageAfterResultsToPool;
	}

	/**
	 * @return the rdfruleStageAfterResultsToPool
	 */
	public static URI getRdfruleStageAfterResultsToPool() {
		return rdfruleStageAfterResultsToPool;
	}

	/**
	 * @param rdfruleStageAfterResultsToDocument the rdfruleStageAfterResultsToDocument to set
	 */
	public static void setRdfruleStageAfterResultsToDocument(
			URI rdfruleStageAfterResultsToDocument) {
		NormalisationRuleImpl.rdfruleStageAfterResultsToDocument = rdfruleStageAfterResultsToDocument;
	}

	/**
	 * @return the rdfruleStageAfterResultsToDocument
	 */
	public static URI getRdfruleStageAfterResultsToDocument() {
		return rdfruleStageAfterResultsToDocument;
	}

	/**
	 * @param rdfruleTypeValidForStage the rdfruleTypeValidForStage to set
	 */
	public static void setRdfruleTypeValidForStage(
			URI rdfruleTypeValidForStage) {
		NormalisationRuleImpl.rdfruleTypeValidForStage = rdfruleTypeValidForStage;
	}

	/**
	 * @return the rdfruleTypeValidForStage
	 */
	public static URI getRdfruleTypeValidForStage() {
		return rdfruleTypeValidForStage;
	}
}
