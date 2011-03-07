package org.queryall.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collection;

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
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import org.queryall.NormalisationRule;
import org.queryall.Profile;

import org.queryall.helpers.Settings;
import org.queryall.helpers.StringUtils;
import org.queryall.helpers.RdfUtils;

public abstract class NormalisationRuleImpl extends NormalisationRule
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
    
    private URI profileIncludeExcludeOrder;
    private Collection<URI> relatedNamespaces;
    
    protected Collection<URI> stages = new ArrayList<URI>(2);
    
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
        try
        {
            final Repository myStaticRepository = new SailRepository(
                    new MemoryStore());
            myStaticRepository.initialize();
            final ValueFactory f = myStaticRepository.getValueFactory();
            
            NormalisationRuleImpl.setNormalisationRuleTypeUri(f.createURI(NormalisationRuleImpl.rdfruleNamespace + "NormalisationRule"));
                    
            NormalisationRuleImpl.version2NormalisationRuleTypeUri = f.createURI(NormalisationRuleImpl.rdfruleNamespace+"RdfRule");
            
            NormalisationRuleImpl.setRdfruleDescription(f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace
                            + "description"));
            NormalisationRuleImpl.setRdfruleOrder(f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "order"));
            NormalisationRuleImpl.setRdfruleStage(f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "stage"));
            NormalisationRuleImpl.setRdfruleHasRelatedNamespace(f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace
                            + "hasRelatedNamespace"));
                    
            NormalisationRuleImpl.setRdfruleStageQueryVariables(f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "queryVariables"));

            NormalisationRuleImpl.setRdfruleStageAfterQueryCreation(f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "afterQueryCreation"));

            NormalisationRuleImpl.setRdfruleStageAfterQueryParsing(f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "afterQueryParsing"));

            NormalisationRuleImpl.setRdfruleStageBeforeResultsImport(f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "beforeResultsImport"));
            
            NormalisationRuleImpl.setRdfruleStageAfterResultsImport(f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "afterResultsImport"));
            
            NormalisationRuleImpl.setRdfruleStageAfterResultsToPool(f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "afterResultsToPool"));

            NormalisationRuleImpl.setRdfruleStageAfterResultsToDocument(f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "afterResultsToDocument"));

            NormalisationRuleImpl.setRdfruleTypeValidForStage(f.createURI(NormalisationRuleImpl.rdfruleNamespace + "typeValidForStage"));
        }
        catch (final RepositoryException re)
        {
            log.error(re.getMessage());
        }
        
    }
    
    // keyToUse is the URI of the next instance that can be found in
    // myRepository
    public NormalisationRuleImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion)
            throws OpenRDFException
    {
        @SuppressWarnings("unused")
        boolean isValid = false;
        
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
                
                isValid = true;
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
            // else
            // {
                // tempUnrecognisedStatements.add(nextStatement);
            // }
        }
        
        this.relatedNamespaces = tempRelatedNamespaces;
        this.stages = tempStages;
        
        if(_DEBUG)
        {
            log
                    .debug("NormalisationRuleImpl.fromRdf: would have returned... result="
                            + this.toString());
        }
        
        // if(!isValid)
        // {
            // throw new RuntimeException(
                    // "NormalisationRuleImpl.fromRdf: result was not valid");
        // }
        
    }

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
            if(_INFO)
            {
                log.info("NormalisationRuleImpl.normaliseByStage : found an inapplicable stage for this type of rule key="+getKey().stringValue()+" stage="+stage);
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

    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            if(_DEBUG)
            {
                log
                        .debug("NormalisationRuleImpl.toRdf: keyToUse="
                                + keyToUse);
            }
            
            final URI keyUri = keyToUse;
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
                con.add(keyUri, RDF.TYPE, NormalisationRuleImpl.version2NormalisationRuleTypeUri, keyUri);
            }
            else
            {
                con.add(keyUri, RDF.TYPE, NormalisationRuleImpl.getNormalisationRuleTypeUri(),
                    keyUri);
            }
            
            con.add(keyUri, ProjectImpl.getProjectCurationStatusUri(),
                    curationStatusLiteral, keyUri);

            if(modelVersion == 1)
            {
                con.add(keyUri, NormalisationRuleImpl.getRdfruleDescription(),
                    descriptionLiteral, keyUri);
            }
            else
            {
                con.add(keyUri, RDFS.COMMENT,
                    descriptionLiteral, keyUri);
            }
            con.add(keyUri, NormalisationRuleImpl.getRdfruleOrder(), orderLiteral,
                    keyUri);
            con.add(keyUri, ProfileImpl.getProfileIncludeExcludeOrderUri(),
                    profileIncludeExcludeOrderLiteral, keyUri);
            
            if(this.relatedNamespaces != null)
            {
                for(final URI nextRelatedNamespace : this.relatedNamespaces)
                {
                    con.add(keyUri, NormalisationRuleImpl.getRdfruleHasRelatedNamespace(), nextRelatedNamespace);
                }
            }

            if(this.stages != null)
            {
                for(final URI nextStage : this.stages)
                {
                    con.add(keyUri, NormalisationRuleImpl.getRdfruleStage(), nextStage);
                }
            }

            if(this.unrecognisedStatements != null)
            {
                for(final Statement nextUnrecognisedStatement : this.unrecognisedStatements)
                {
                    con.add(nextUnrecognisedStatement);
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

    public static boolean schemaToRdf(Repository myRepository, String keyToUse, int modelVersion) throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            final URI contextKeyUri = f.createURI(keyToUse);
            con.setAutoCommit(false);
            
            con.add(NormalisationRuleImpl.getNormalisationRuleTypeUri(), RDF.TYPE, OWL.CLASS,
                    contextKeyUri);
            
            con.add(NormalisationRuleImpl.getNormalisationRuleTypeUri(), RDFS.LABEL, 
                f.createLiteral("A normalisation rule intended to denormalise parts of queries to match endpoints, and renormalise the output of the query to match the normalised form."),
                    contextKeyUri);

            if(modelVersion == 1)
            {
                con.add(NormalisationRuleImpl.getRdfruleDescription(),
                    RDFS.SUBPROPERTYOF, RDFS.COMMENT, contextKeyUri);

                con.add(NormalisationRuleImpl.getRdfruleDescription(), RDFS.RANGE,
                    RDFS.LITERAL, contextKeyUri);

                con.add(NormalisationRuleImpl.getRdfruleDescription(), RDFS.DOMAIN,
                    NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextKeyUri);
                
                con.add(NormalisationRuleImpl.getRdfruleDescription(), RDFS.LABEL, 
                    f.createLiteral("The description of a normalisation rule."),
                    contextKeyUri);

            }
            
            con.add(NormalisationRuleImpl.getRdfruleOrder(), RDF.TYPE,
                    OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(NormalisationRuleImpl.getRdfruleOrder(), RDFS.RANGE,
                    RDFS.LITERAL, contextKeyUri);

            con.add(NormalisationRuleImpl.getRdfruleOrder(), RDFS.DOMAIN,
                    NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextKeyUri);

            con.add(NormalisationRuleImpl.getRdfruleOrder(), RDFS.LABEL, 
                f.createLiteral("The ordering variable that is used to identify what order the normalisation rules are designed to be applied in."),
                    contextKeyUri);

            
            con.add(NormalisationRuleImpl.getRdfruleHasRelatedNamespace(), RDF.TYPE,
                    OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(NormalisationRuleImpl.getRdfruleHasRelatedNamespace(), RDFS.RANGE,
                    NamespaceEntryImpl.getNamespaceTypeUri(), contextKeyUri);

            con.add(NormalisationRuleImpl.getRdfruleHasRelatedNamespace(), RDFS.DOMAIN,
                    NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextKeyUri);

            con.add(NormalisationRuleImpl.getRdfruleHasRelatedNamespace(), RDFS.LABEL, 
                f.createLiteral("An informative property indicating that the target namespace is somehow related to this rule."),
                    contextKeyUri);
            
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
    public URI getKey()
    {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }

    public void setKey(URI nextKey)
    {
        this.key = nextKey;
    }    

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return this.title;
    }

    
    /**
     * @return the validStages
     */
    public Collection<URI> getValidStages()
    {
        return validStages;
    }

    /**
     * @param validStages the validStages to set
     */
    protected void setValidStages(Collection<URI> nextValidStages)
    {
        this.validStages = nextValidStages;
    }
    
    
    /**
     * @return the namespace used to represent objects of this type by default
     */
    public String getDefaultNamespace()
    {
        return defaultNamespace;
    }
    
    /**
     * @return the URI used for the rdf Type of these elements
     */
    public String getElementType()
    {
        return getNormalisationRuleTypeUri().stringValue();
    }
    
    public int getOrder()
    {
        return order;
    }
    
    public void setOrder(int order)
    {
        this.order = order;
    }

    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }

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

    public URI getProfileIncludeExcludeOrder()
    {
        return profileIncludeExcludeOrder;
    }

    public void setProfileIncludeExcludeOrder(URI profileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = profileIncludeExcludeOrder;
    }

    
    
    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        unrecognisedStatements.add(unrecognisedStatement);
    }

    public Collection<Statement> getUnrecognisedStatements()
    {
        return unrecognisedStatements;
    }

    public URI getCurationStatus()
    {
        return curationStatus;
    }
    
    public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }

	public boolean isRdfRuleUsedWithProfileList(Collection<Profile> nextSortedProfileList, boolean recogniseImplicitRdfRuleInclusions, boolean includeNonProfileMatchedRdfRules)
    {
        for(final Profile nextProfile : nextSortedProfileList)
        {
            final int trueResult = nextProfile.usedWithRdfRule(this.getKey(), this.profileIncludeExcludeOrder);
            if(trueResult == ProfileImpl.IMPLICIT_INCLUDE)
            {
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.isRdfRuleUsedWithProfileList: found implicit include for rdfRuleUri="
                                    + this.getKey().stringValue()
                                    + " profile="
                                    + nextProfile.getKey().stringValue());
                    // log.debug("Settings: this.getBooleanPropertyFromConfig("recogniseImplicitRdfRuleInclusions")="+this.getBooleanPropertyFromConfig("recogniseImplicitRdfRuleInclusions"));
                }
                if(recogniseImplicitRdfRuleInclusions)
                {
                    if(Settings._TRACE)
                    {
                        Settings.log
                                .trace("Settings.isRdfRuleUsedWithProfileList: returning implicit include true for rdfRuleUri="
                                        + this.getKey().stringValue()
                                        + " profile="
                                        + nextProfile.getKey().stringValue());
                    }
                    return true;
                }
                else if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.isRdfRuleUsedWithProfileList: implicit include not recognised for rdfRuleUri="
                                    + this.getKey().stringValue()
                                    + " profile="
                                    + nextProfile.getKey().stringValue());
                }
            }
            else if(trueResult == ProfileImpl.SPECIFIC_INCLUDE)
            {
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.isRdfRuleUsedWithProfileList: returning specific true for rdfRuleUri="
                                    + this.getKey().stringValue()
                                    + " profile="
                                    + nextProfile.getKey().stringValue());
                }
                return true;
            }
            else if(trueResult == ProfileImpl.SPECIFIC_EXCLUDE)
            {
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.isRdfRuleUsedWithProfileList: returning specific false for rdfRuleUri="
                                    + this.getKey().stringValue()
                                    + " profile="
                                    + nextProfile.getKey().stringValue());
                }
                return false;
            }
        }
        
        boolean returnValue = (this.profileIncludeExcludeOrder.equals(ProfileImpl.getExcludeThenIncludeUri()) && includeNonProfileMatchedRdfRules);
        
        if(Settings._DEBUG)
        {
            // log.debug("Settings: this.getBooleanPropertyFromConfig("includeNonProfileMatchedRdfRules")="+this.getBooleanPropertyFromConfig("includeNonProfileMatchedRdfRules"));
            Settings.log
                    .debug("Settings.isRdfRuleUsedWithProfileList: returning no specific or implicit matches found returnValue="
                            + returnValue
                            + " for rdfRuleUri=" + this.getKey().stringValue());
        }
        
        return returnValue;
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
