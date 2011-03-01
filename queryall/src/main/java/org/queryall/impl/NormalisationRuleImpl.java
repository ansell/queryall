package org.queryall.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collection;
import java.util.regex.PatternSyntaxException;

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
import org.openrdf.sail.memory.model.MemValueFactory;

import org.queryall.NormalisationRule;
import org.queryall.NamespaceEntry;
import org.queryall.Profile;
import org.queryall.Project;
import org.queryall.RuleTest;

import org.queryall.helpers.Settings;
import org.queryall.helpers.Utilities;

public abstract class NormalisationRuleImpl extends NormalisationRule
{

    protected static final Logger log = Logger.getLogger(NormalisationRuleImpl.class.getName());
    protected static final boolean _TRACE = log.isTraceEnabled();
    protected static final boolean _DEBUG = log.isDebugEnabled();
    protected static final boolean _INFO = log.isInfoEnabled();

    private static final String defaultNamespace = Settings.DEFAULT_RDF_RDFRULE_NAMESPACE;
    
    protected Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key;
    private String description;
    private URI curationStatus = ProjectImpl.projectNotCuratedUri;
    
    private URI profileIncludeExcludeOrder;
    private Collection<URI> relatedNamespaces;
    
    protected Collection<URI> stages = new ArrayList<URI>(2);
    
    private Collection<URI> validStages = new ArrayList<URI>(7);
    
    private int order = 100;
    
    public static URI normalisationRuleTypeUri;
    public static URI version2NormalisationRuleTypeUri;
    public static URI rdfruleDescription;
    public static URI rdfruleOrder;
    public static URI rdfruleStage;
    public static URI rdfruleHasRelatedNamespace;
    public static URI rdfruleStageQueryVariables;
    public static URI rdfruleStageAfterQueryCreation;
    public static URI rdfruleStageAfterQueryParsing;
    public static URI rdfruleStageBeforeResultsImport;
    public static URI rdfruleStageAfterResultsImport;
    public static URI rdfruleStageAfterResultsToPool;
    public static URI rdfruleStageAfterResultsToDocument;
    public static URI rdfruleTypeValidForStage;
    
    
    
    public static String rdfruleNamespace;
    
    static
    {
        NormalisationRuleImpl.rdfruleNamespace = Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX
                + Settings.DEFAULT_RDF_RDFRULE_NAMESPACE
                + Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX;
        try
        {
            final Repository myStaticRepository = new SailRepository(
                    new MemoryStore());
            myStaticRepository.initialize();
            final ValueFactory f = myStaticRepository.getValueFactory();
            
            NormalisationRuleImpl.normalisationRuleTypeUri = f.createURI(NormalisationRuleImpl.rdfruleNamespace + "NormalisationRule");
                    
            NormalisationRuleImpl.version2NormalisationRuleTypeUri = f.createURI(NormalisationRuleImpl.rdfruleNamespace+"RdfRule");
            
            NormalisationRuleImpl.rdfruleDescription = f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace
                            + "description");
            NormalisationRuleImpl.rdfruleOrder = f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "order");
            NormalisationRuleImpl.rdfruleStage = f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "stage");
            NormalisationRuleImpl.rdfruleHasRelatedNamespace = f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace
                            + "hasRelatedNamespace");
                    
            NormalisationRuleImpl.rdfruleStageQueryVariables = f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "queryVariables");

            NormalisationRuleImpl.rdfruleStageAfterQueryCreation = f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "afterQueryCreation");

            NormalisationRuleImpl.rdfruleStageAfterQueryParsing = f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "afterQueryParsing");

            NormalisationRuleImpl.rdfruleStageBeforeResultsImport = f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "beforeResultsImport");
            
            NormalisationRuleImpl.rdfruleStageAfterResultsImport = f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "afterResultsImport");
            
            NormalisationRuleImpl.rdfruleStageAfterResultsToPool = f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "afterResultsToPool");

            NormalisationRuleImpl.rdfruleStageAfterResultsToDocument = f
                    .createURI(NormalisationRuleImpl.rdfruleNamespace + "afterResultsToDocument");

            NormalisationRuleImpl.rdfruleTypeValidForStage = f.createURI(NormalisationRuleImpl.rdfruleNamespace + "typeValidForStage");
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
        boolean isValid = false;
        
        final ValueFactory f = new MemValueFactory();
        
        // final URI providerInstanceUri = f.createURI(keyToUse);
        
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
                            NormalisationRuleImpl.normalisationRuleTypeUri)
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
                    ProjectImpl.projectCurationStatusUri))
            {
                this.curationStatus = (URI)nextStatement.getObject();
            }
            else if(nextStatement.getPredicate().equals(
                    NormalisationRuleImpl.rdfruleOrder))
            {
                this.order = Utilities.getIntegerFromValue(nextStatement
                        .getObject());
            }
            else if(nextStatement.getPredicate().equals(
                    NormalisationRuleImpl.rdfruleDescription) || nextStatement.getPredicate().equals(RDFS.COMMENT))
            {
                this.description = nextStatement.getObject().stringValue();
            }
            else if(nextStatement.getPredicate().equals(
                    NormalisationRuleImpl.rdfruleHasRelatedNamespace))
            {
                tempRelatedNamespaces.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(
                    NormalisationRuleImpl.rdfruleStage))
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
        
        if(stage.equals(NormalisationRuleImpl.rdfruleStageQueryVariables))
        {
            return this.stageQueryVariables(input);
        }
        else if(stage.equals(NormalisationRuleImpl.rdfruleStageAfterQueryCreation))
        {
            return this.stageAfterQueryCreation(input);
        }
        else if(stage.equals(NormalisationRuleImpl.rdfruleStageAfterQueryParsing))
        {
            return this.stageAfterQueryParsing(input);
        }
        else if(stage.equals(NormalisationRuleImpl.rdfruleStageBeforeResultsImport))
        {
            return this.stageBeforeResultsImport(input);
        }
        else if(stage.equals(NormalisationRuleImpl.rdfruleStageAfterResultsImport))
        {
            return this.stageAfterResultsImport(input);
        }
        else if(stage.equals(NormalisationRuleImpl.rdfruleStageAfterResultsToPool))
        {
            return this.stageAfterResultsToPool(input);
        }
        else if(stage.equals(NormalisationRuleImpl.rdfruleStageAfterResultsToDocument))
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
                curationStatusLiteral = ProjectImpl.projectNotCuratedUri;
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
                con.add(keyUri, RDF.TYPE, NormalisationRuleImpl.normalisationRuleTypeUri,
                    keyUri);
            }
            
            con.add(keyUri, ProjectImpl.projectCurationStatusUri,
                    curationStatusLiteral, keyUri);

            if(modelVersion == 1)
            {
                con.add(keyUri, NormalisationRuleImpl.rdfruleDescription,
                    descriptionLiteral, keyUri);
            }
            else
            {
                con.add(keyUri, RDFS.COMMENT,
                    descriptionLiteral, keyUri);
            }
            con.add(keyUri, NormalisationRuleImpl.rdfruleOrder, orderLiteral,
                    keyUri);
            con.add(keyUri, ProfileImpl.getProfileIncludeExcludeOrderUri(),
                    profileIncludeExcludeOrderLiteral, keyUri);
            
            if(this.relatedNamespaces != null)
            {
                for(final URI nextRelatedNamespace : this.relatedNamespaces)
                {
                    con.add(keyUri, NormalisationRuleImpl.rdfruleHasRelatedNamespace, nextRelatedNamespace);
                }
            }

            if(this.stages != null)
            {
                for(final URI nextStage : this.stages)
                {
                    con.add(keyUri, NormalisationRuleImpl.rdfruleStage, nextStage);
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
        catch (final OpenRDFException ordfe)
        {
            log.error(ordfe);
            
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            throw ordfe;
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
            
            con.add(NormalisationRuleImpl.normalisationRuleTypeUri, RDF.TYPE, OWL.CLASS,
                    contextKeyUri);
            
            con.add(NormalisationRuleImpl.normalisationRuleTypeUri, RDFS.LABEL, 
                f.createLiteral("A normalisation rule intended to denormalise parts of queries to match endpoints, and renormalise the output of the query to match the normalised form."),
                    contextKeyUri);

            if(modelVersion == 1)
            {
                con.add(NormalisationRuleImpl.rdfruleDescription,
                    RDFS.SUBPROPERTYOF, RDFS.COMMENT, contextKeyUri);

                con.add(NormalisationRuleImpl.rdfruleDescription, RDFS.RANGE,
                    RDFS.LITERAL, contextKeyUri);

                con.add(NormalisationRuleImpl.rdfruleDescription, RDFS.DOMAIN,
                    NormalisationRuleImpl.normalisationRuleTypeUri, contextKeyUri);
                
                con.add(NormalisationRuleImpl.rdfruleDescription, RDFS.LABEL, 
                    f.createLiteral("The description of a normalisation rule."),
                    contextKeyUri);

            }
            
            con.add(NormalisationRuleImpl.rdfruleOrder, RDF.TYPE,
                    OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(NormalisationRuleImpl.rdfruleOrder, RDFS.RANGE,
                    RDFS.LITERAL, contextKeyUri);

            con.add(NormalisationRuleImpl.rdfruleOrder, RDFS.DOMAIN,
                    NormalisationRuleImpl.normalisationRuleTypeUri, contextKeyUri);

            con.add(NormalisationRuleImpl.rdfruleOrder, RDFS.LABEL, 
                f.createLiteral("The ordering variable that is used to identify what order the normalisation rules are designed to be applied in."),
                    contextKeyUri);

            
            con.add(NormalisationRuleImpl.rdfruleHasRelatedNamespace, RDF.TYPE,
                    OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(NormalisationRuleImpl.rdfruleHasRelatedNamespace, RDFS.RANGE,
                    NamespaceEntryImpl.namespaceTypeUri, contextKeyUri);

            con.add(NormalisationRuleImpl.rdfruleHasRelatedNamespace, RDFS.DOMAIN,
                    NormalisationRuleImpl.normalisationRuleTypeUri, contextKeyUri);

            con.add(NormalisationRuleImpl.rdfruleHasRelatedNamespace, RDFS.LABEL, 
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
        catch (final OpenRDFException ordfe)
        {
            log.error(ordfe);
            
            // Something went wrong during the transaction, so we roll it back
            if(con != null)
            {
                con.rollback();
            }
            
            throw ordfe;
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
        this.setKey(Utilities.createURI(nextKey));
    }

    public void setKey(URI nextKey)
    {
        this.key = nextKey;
    }    
    /**
     * @return the validStages
     */
    @Override
    public Collection<URI> getValidStages()
    {
        return validStages;
    }

    /**
     * @param validStages the validStages to set
     */
    @Override
    protected void setValidStages(Collection<URI> nextValidStages)
    {
        this.validStages = nextValidStages;
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
     * @return the URI used for the rdf Type of these elements
     */
    @Override
    public String getElementType()
    {
        return normalisationRuleTypeUri.stringValue();
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
}
