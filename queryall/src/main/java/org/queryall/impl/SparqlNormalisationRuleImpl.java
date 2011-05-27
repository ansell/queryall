
package org.queryall.impl;

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
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import org.queryall.api.RuleTest;
import org.queryall.api.SparqlNormalisationRule;
import org.queryall.helpers.*;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SparqlNormalisationRuleImpl extends NormalisationRuleImpl implements SparqlNormalisationRule
{
    private static final Logger log = Logger
            .getLogger(SparqlNormalisationRuleImpl.class.getName());
    private static final boolean _TRACE = SparqlNormalisationRuleImpl.log
            .isTraceEnabled();
    private static final boolean _DEBUG = SparqlNormalisationRuleImpl.log
            .isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SparqlNormalisationRuleImpl.log
            .isInfoEnabled();
        
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    // public String key;
    // public String description = "";
    // public String curationStatus = ProjectImpl.projectNotCuratedUri.stringValue();
    
    private String sparqlConstructQuery = "";
    // public String profileIncludeExcludeOrder = Profile.getProfileIncludeExcludeOrder()UndefinedUri
            // .stringValue();
    // public Collection<String> relatedNamespaces = new HashSet<String>();
    
    private URI mode = SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyDeleteMatches();
    
    private static URI sparqlruleTypeUri;
    private static URI sparqlruleSparqlConstructQuery;
    private static URI sparqlruleMode;
    private static URI sparqlruleModeOnlyIncludeMatches;
    private static URI sparqlruleModeOnlyDeleteMatches;
    
    // public static String rdfruleNamespace;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        SparqlNormalisationRuleImpl.setSparqlRuleTypeUri(f
                .createURI(SparqlNormalisationRuleImpl.rdfruleNamespace, "SparqlNormalisationRule"));
        SparqlNormalisationRuleImpl.setSparqlRuleSparqlConstructQuery(f
                .createURI(SparqlNormalisationRuleImpl.rdfruleNamespace, "sparqlConstructQuery"));
        SparqlNormalisationRuleImpl.setSparqlRuleMode(f
                .createURI(SparqlNormalisationRuleImpl.rdfruleNamespace, "mode"));
        SparqlNormalisationRuleImpl.setSparqlRuleModeOnlyDeleteMatches(f
                .createURI(SparqlNormalisationRuleImpl.rdfruleNamespace, "onlyDeleteMatches"));
        SparqlNormalisationRuleImpl.setSparqlRuleModeOnlyIncludeMatches(f
                .createURI(SparqlNormalisationRuleImpl.rdfruleNamespace, "onlyIncludeMatches"));
    }
    
    public SparqlNormalisationRuleImpl()
    {
    	super();
    }
    
    // keyToUse is the URI of the next instance that can be found in
    // myRepository
    public SparqlNormalisationRuleImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion)
            throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
    	Collection<Statement> currentUnrecognisedStatements = new HashSet<Statement>();
    	
    	currentUnrecognisedStatements.addAll(this.getUnrecognisedStatements());
    	
    	this.unrecognisedStatements = new HashSet<Statement>();
    	
        for(Statement nextStatement : currentUnrecognisedStatements)
        {
            if(SparqlNormalisationRuleImpl._DEBUG)
            {
                SparqlNormalisationRuleImpl.log
                        .debug("SparqlNormalisationRuleImpl: nextStatement: "
                                + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(
                            SparqlNormalisationRuleImpl.getSparqlRuleTypeUri()))
            {
                if(SparqlNormalisationRuleImpl._TRACE)
                {
                    SparqlNormalisationRuleImpl.log
                            .trace("SparqlNormalisationRuleImpl: found valid type predicate for URI: "
                                    + keyToUse);
                }
                
//                isValid = true;
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(
                    SparqlNormalisationRuleImpl.getSparqlRuleSparqlConstructQuery()))
            {
                this.setSparqlConstructQuery(nextStatement.getObject()
                        .stringValue());
            }
            else if(nextStatement.getPredicate().equals(
                    SparqlNormalisationRuleImpl.getSparqlRuleMode()) 
                && nextStatement.getObject().equals(
                    SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyIncludeMatches()))
            {
                this.setMode(SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyIncludeMatches());
            }
            else if(nextStatement.getPredicate().equals(
                    SparqlNormalisationRuleImpl.getSparqlRuleMode()) 
                && nextStatement.getObject().equals(
                    SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyDeleteMatches()))
            {
                this.setMode(SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyDeleteMatches());
            }
            else
            {
                if(_TRACE)
                {
                    log.trace("SparqlNormalisationRuleImpl: unrecognisedStatement nextStatement: "+nextStatement.toString());
                }
                this.unrecognisedStatements.add(nextStatement);
            }
        }
        
        // this.relatedNamespaces = tempRelatedNamespaces;
        // this.unrecognisedStatements = tempUnrecognisedStatements;
        
        // stages.add(NormalisationRule.rdfruleStageAfterResultsImport.stringValue());
        
        // mode = sparqlruleModeOnlyIncludeMatches.stringValue();

        this.addValidStage(getRdfruleStageAfterResultsImport());
        this.addValidStage(getRdfruleStageAfterResultsToPool());

        if(SparqlNormalisationRuleImpl._DEBUG)
        {
            SparqlNormalisationRuleImpl.log
                    .debug("SparqlNormalisationRuleImpl constructor: toString()="
                            + this.toString());
        }
    }
    
    public static boolean schemaToRdf(Repository myRepository, String keyToUse, int modelVersion) throws OpenRDFException
    {
        NormalisationRuleImpl.schemaToRdf(myRepository, keyToUse, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            final URI contextKeyUri = f.createURI(keyToUse);
            con.setAutoCommit(false);
            
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleTypeUri(), RDFS.LABEL, f.createLiteral("A SPARQL based normalisation rule intended to normalise in-memory RDF triples."), contextKeyUri);
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleTypeUri(), RDFS.SUBCLASSOF, NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextKeyUri);


            con.add(SparqlNormalisationRuleImpl.getSparqlRuleSparqlConstructQuery(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleSparqlConstructQuery(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleSparqlConstructQuery(), RDFS.DOMAIN, SparqlNormalisationRuleImpl.getSparqlRuleTypeUri(), contextKeyUri);
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleSparqlConstructQuery(), RDFS.LABEL, f.createLiteral("A SPARQL CONSTRUCT pattern that will be used to match against RDF triples in memory at the assigned stages, in the form CONSTRUCT { ... } WHERE { ... }."), contextKeyUri);

            con.add(SparqlNormalisationRuleImpl.getSparqlRuleMode(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleMode(), RDFS.RANGE, RDFS.RESOURCE, contextKeyUri);
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleMode(), RDFS.DOMAIN, SparqlNormalisationRuleImpl.getSparqlRuleTypeUri(), contextKeyUri);
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleMode(), RDFS.LABEL, f.createLiteral("The mode that this normalisation rule will be used in. In the absence of SPARQL Update language support, this enables deletions and filtering based on the matched triples."), contextKeyUri);

            con.add(SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyIncludeMatches(), RDFS.LABEL, f.createLiteral("Specifies that the SPARQL rule will be applied, and only the matches from the rule will remain in the relevant RDF triple store after the application. If the stage is after Results Import, only the results from the current provider and query will be affected."), contextKeyUri);

            con.add(SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyDeleteMatches(), RDFS.LABEL, f.createLiteral("Specifies that the SPARQL rule will be applied, and the matches from the rule will be deleted from the relevant RDF triple store after the application. If the stage is after Results Import, only the results from the current provider and query will be affected."), contextKeyUri);

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
            
            SparqlNormalisationRuleImpl.log.error("RepositoryException: "
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
    
    public Object stageQueryVariables(Object input)
    {
        return input;
    }
        
    public Object stageAfterQueryCreation(Object input)
    {
        return input;
    }

    public Object stageAfterQueryParsing(Object input)
    {
        return input;
    }

    public Object stageBeforeResultsImport(Object input)
    {
        return input;
    }

    public Object stageAfterResultsImport(Object input)
    {
        return doWorkBasedOnMode((Repository) input);
    }

    public Object stageAfterResultsToPool(Object input)
    {
        return doWorkBasedOnMode((Repository) input);
    }
    
    public Object stageAfterResultsToDocument(Object input)
    {
        return input;
    }

    private Repository doWorkBasedOnMode(Repository input)
    {
        if(getMode().equals(getSparqlRuleModeOnlyDeleteMatches()))
        {
            return removeStatementsFromRepository((Repository) input);
        }
        else if(getMode().equals(getSparqlRuleModeOnlyIncludeMatches()))
        {
            return chooseStatementsFromRepository((Repository) input);
        }
        
        return input;
    }

    private Repository removeStatementsFromRepository(Repository myRepository)
    {
        try
        {
            if(_DEBUG)
            {
                log.debug("SparqlNormalisationRuleImpl: removing statements according to sparqlConstructQuery="+getSparqlConstructQuery());
            }        
            
            RepositoryConnection removeConnection = myRepository.getConnection();
            
            try 
            {
                GraphQueryResult graphResult = removeConnection.prepareGraphQuery(
                    QueryLanguage.SPARQL, getSparqlConstructQuery()).evaluate();
                
                int deletedStatements = 0;
                
                while (graphResult.hasNext()) 
                {
                   removeConnection.remove(graphResult.next());
                   deletedStatements++;
                }
                
                removeConnection.commit();
                if(_DEBUG)
                {
                    log.debug("SparqlNormalisationRuleImpl: removed "+deletedStatements+ " results");
                }        
                
            }
            catch(Exception ex)
            {
                log.error("SparqlNormalisationRuleImpl: exception removing statements",ex);
            }
            finally
            {
                removeConnection.close();
            }
        }
        catch(org.openrdf.repository.RepositoryException rex)
        {
            log.error("SparqlNormalisationRuleImpl: RepositoryException exception adding statements", rex);
        }
        
        return myRepository;
    }

    private Repository chooseStatementsFromRepository(Repository myRepository)
    {
        Repository myResultRepository = null;
        
        try
        {
            myResultRepository = new SailRepository(new MemoryStore());
            myResultRepository.initialize();
            
            if(_DEBUG)
            {
                log.debug("SparqlNormalisationRuleImpl: selecting statements according to sparqlConstructQuery="+getSparqlConstructQuery());
            }        
            
            RepositoryConnection selectConnection = myRepository.getConnection();
            RepositoryConnection addConnection = myResultRepository.getConnection();
            
            try 
            {
                GraphQueryResult graphResult = selectConnection.prepareGraphQuery(
                    QueryLanguage.SPARQL, getSparqlConstructQuery()).evaluate();
                
                int selectedStatements = 0;
                
                while (graphResult.hasNext()) 
                {
                   addConnection.add(graphResult.next());
                   selectedStatements++;
                }
                
                if(_DEBUG)
                {
                    log.debug("SparqlNormalisationRuleImpl: slected "+selectedStatements+ " statements for results");
                }        
                
                addConnection.commit();
            }
            catch(Exception ex)
            {
                log.error("SparqlNormalisationRuleImpl: exception adding statements",ex);
            }
            finally
            {
                selectConnection.close();
                addConnection.close();
            }
        }
        catch(org.openrdf.repository.RepositoryException rex)
        {
            log.error("SparqlNormalisationRuleImpl: RepositoryException exception adding statements", rex);
        }
        
        return myResultRepository;
    }

    public boolean runTests(Collection<RuleTest> myRules)
    {
        boolean allPassed = true;
        
        //final Collection<RuleTest> myRules = Settings.getRuleTestsForNormalisationRuleUri(this.getKey());
        
        // for(final RuleTest nextRuleTest : myRules)
        // {
        // 
        // }
        
        return allPassed;
    }
    
    @Override
    public String toHtml()
    {
        String result = "";
        
        result += "<div class=\"rulekey\">Rule Key: "
                + StringUtils.xmlEncodeString(this.getKey().stringValue()) + "</div>\n";
        result += "<div class=\"description\">Description: "
                + StringUtils.xmlEncodeString(this.getDescription()) + "</div>\n";
        result += "<div class=\"order\">Order: "
                + StringUtils.xmlEncodeString(this.getOrder() + "") + "</div>\n";
        result += "<div class=\"inputMatchSparqlPattern\">SPARQL Construct query: "
                + StringUtils.xmlEncodeString(this.getSparqlConstructQuery()) + "</div>\n";
        
        return result;
    }
    
    @Override
    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        String prefix = "rdfrule_";
        
        return sb.toString();
    }
    
    @Override
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        super.toRdf(myRepository, keyToUse, modelVersion);

        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            if(SparqlNormalisationRuleImpl._DEBUG)
            {
                SparqlNormalisationRuleImpl.log
                        .debug("SparqlNormalisationRuleImpl.toRdf: keyToUse="
                                + keyToUse);
            }
            
            final URI keyUri = keyToUse;
            final Literal sparqlConstructQueryLiteral = f
                    .createLiteral(this.getSparqlConstructQuery());
            final URI modeUri = getMode();
            
            con.setAutoCommit(false);
            
            con.add(keyUri, RDF.TYPE, SparqlNormalisationRuleImpl.getSparqlRuleTypeUri(),
                    keyUri);
            
            con.add(keyUri, SparqlNormalisationRuleImpl.getSparqlRuleSparqlConstructQuery(),
                    sparqlConstructQueryLiteral, keyUri);
            con.add(keyUri, SparqlNormalisationRuleImpl.getSparqlRuleMode(),
                    modeUri, keyUri);

            // if(this.unrecognisedStatements != null)
            // {
                // for(final Statement nextUnrecognisedStatement : this.unrecognisedStatements)
                // {
                    // con.add(nextUnrecognisedStatement);
                // }
            // }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch (final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            SparqlNormalisationRuleImpl.log.error("RepositoryException: "
                    + re.getMessage());
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
    
    @Override
    public String toString()
    {
        String result = "\n";
        
        result += "key=" + this.getKey() + "\n";
        result += "order=" + this.getOrder() + "\n";
        result += "sparqlConstructQuery=" + this.getSparqlConstructQuery() + "\n";
        result += "mode=" + this.getMode() + "\n";
        result += "description=" + this.getDescription() + "\n";
        
        return result;
    }
    
    /**
     * @return the URI used for the rdf Type of these elements
     */
    @Override
    public URI getElementType()
    {
        return getSparqlRuleTypeUri();
    }
    
    /* (non-Javadoc)
	 * @see org.queryall.impl.SparqlNormalisationRule#getMode()
	 */
    @Override
	public URI getMode()
    {
        return mode;
    }

	/* (non-Javadoc)
	 * @see org.queryall.impl.SparqlNormalisationRule#setMode(org.openrdf.model.URI)
	 */
	@Override
	public void setMode(URI mode)
	{
		this.mode = mode;
	}

	/* (non-Javadoc)
	 * @see org.queryall.impl.SparqlNormalisationRule#setSparqlConstructQuery(java.lang.String)
	 */
	@Override
	public void setSparqlConstructQuery(String sparqlConstructQuery) {
		this.sparqlConstructQuery = sparqlConstructQuery;
	}

	/* (non-Javadoc)
	 * @see org.queryall.impl.SparqlNormalisationRule#getSparqlConstructQuery()
	 */
	@Override
	public String getSparqlConstructQuery() {
		return sparqlConstructQuery;
	}

	/**
	 * @param sparqlruleModeOnlyDeleteMatches the sparqlruleModeOnlyDeleteMatches to set
	 */
	public static void setSparqlRuleModeOnlyDeleteMatches(
			URI sparqlruleModeOnlyDeleteMatches) {
		SparqlNormalisationRuleImpl.sparqlruleModeOnlyDeleteMatches = sparqlruleModeOnlyDeleteMatches;
	}

	/**
	 * @return the sparqlruleModeOnlyDeleteMatches
	 */
	public static URI getSparqlRuleModeOnlyDeleteMatches() {
		return sparqlruleModeOnlyDeleteMatches;
	}

	/**
	 * @param sparqlruleTypeUri the sparqlruleTypeUri to set
	 */
	public static void setSparqlRuleTypeUri(URI sparqlruleTypeUri) {
		SparqlNormalisationRuleImpl.sparqlruleTypeUri = sparqlruleTypeUri;
	}

	/**
	 * @return the sparqlruleTypeUri
	 */
	public static URI getSparqlRuleTypeUri() {
		return sparqlruleTypeUri;
	}

	/**
	 * @param sparqlruleSparqlConstructQuery the sparqlruleSparqlConstructQuery to set
	 */
	public static void setSparqlRuleSparqlConstructQuery(
			URI sparqlruleSparqlConstructQuery) {
		SparqlNormalisationRuleImpl.sparqlruleSparqlConstructQuery = sparqlruleSparqlConstructQuery;
	}

	/**
	 * @return the sparqlruleSparqlConstructQuery
	 */
	public static URI getSparqlRuleSparqlConstructQuery() {
		return sparqlruleSparqlConstructQuery;
	}

	/**
	 * @param sparqlruleMode the sparqlruleMode to set
	 */
	public static void setSparqlRuleMode(URI sparqlruleMode) {
		SparqlNormalisationRuleImpl.sparqlruleMode = sparqlruleMode;
	}

	/**
	 * @return the sparqlruleMode
	 */
	public static URI getSparqlRuleMode() {
		return sparqlruleMode;
	}

	/**
	 * @param sparqlruleModeOnlyIncludeMatches the sparqlruleModeOnlyIncludeMatches to set
	 */
	public static void setSparqlRuleModeOnlyIncludeMatches(
			URI sparqlruleModeOnlyIncludeMatches) {
		SparqlNormalisationRuleImpl.sparqlruleModeOnlyIncludeMatches = sparqlruleModeOnlyIncludeMatches;
	}

	/**
	 * @return the sparqlruleModeOnlyIncludeMatches
	 */
	public static URI getSparqlRuleModeOnlyIncludeMatches() {
		return sparqlruleModeOnlyIncludeMatches;
	}
}
