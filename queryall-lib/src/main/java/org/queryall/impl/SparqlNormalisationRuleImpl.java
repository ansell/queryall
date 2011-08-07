package org.queryall.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.RuleTest;
import org.queryall.api.SparqlNormalisationRule;
import org.queryall.helpers.Constants;
import org.queryall.helpers.StringUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SparqlNormalisationRuleImpl extends NormalisationRuleImpl implements SparqlNormalisationRule
{
    private static final Logger log = Logger.getLogger(SparqlNormalisationRuleImpl.class.getName());
    private static final boolean _TRACE = SparqlNormalisationRuleImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = SparqlNormalisationRuleImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SparqlNormalisationRuleImpl.log.isInfoEnabled();
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    // public String key;
    // public String description = "";
    // public String curationStatus = ProjectImpl.projectNotCuratedUri.stringValue();
    
    private String sparqlConstructQueryTarget = "";
    private List<String> sparqlWherePatterns = new ArrayList<String>();
    private String sparqlPrefixes = "";
    // public String profileIncludeExcludeOrder =
    // Profile.getProfileIncludeExcludeOrder()UndefinedUri
    // .stringValue();
    // public Collection<String> relatedNamespaces = new HashSet<String>();
    
    private URI mode = SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyDeleteMatches();
    
    private static URI sparqlruleTypeUri;
    private static URI sparqlruleSparqlConstructQueryTarget;
    private static URI sparqlruleMode;
    private static URI sparqlruleModeOnlyIncludeMatches;
    private static URI sparqlruleModeOnlyDeleteMatches;
    private static URI sparqlruleModeAddAllMatchingTriples;
    private static URI sparqlruleSparqlWherePattern;
    private static URI sparqlruleSparqlPrefixes;
    private static URI sparqlruleSparqlConstructQuery;
    private static URI OLDsparqlruleModeOnlyDeleteMatches;
    
    // public static String rdfruleNamespace;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        SparqlNormalisationRuleImpl.setSparqlRuleTypeUri(f.createURI(NormalisationRuleImpl.rdfruleNamespace,
                "SparqlNormalisationRule"));
        SparqlNormalisationRuleImpl.setOLDSparqlRuleSparqlConstructQuery(f.createURI(
                NormalisationRuleImpl.rdfruleNamespace, "sparqlConstructQuery"));
        SparqlNormalisationRuleImpl.setSparqlRuleSparqlConstructQueryTarget(f.createURI(
                NormalisationRuleImpl.rdfruleNamespace, "sparqlConstructQueryTarget"));
        SparqlNormalisationRuleImpl.setSparqlRuleSparqlWherePattern(f.createURI(NormalisationRuleImpl.rdfruleNamespace,
                "sparqlWherePatterns"));
        SparqlNormalisationRuleImpl.setSparqlRuleSparqlPrefixes(f.createURI(NormalisationRuleImpl.rdfruleNamespace,
                "sparqlPrefixes"));
        SparqlNormalisationRuleImpl.setSparqlRuleMode(f.createURI(NormalisationRuleImpl.rdfruleNamespace, "mode"));
        SparqlNormalisationRuleImpl.setSparqlRuleModeOnlyDeleteMatches(f.createURI(
                NormalisationRuleImpl.rdfruleNamespace, "onlyDeleteMatchingTriples"));
        SparqlNormalisationRuleImpl.OLDsparqlruleModeOnlyDeleteMatches =
                f.createURI(NormalisationRuleImpl.rdfruleNamespace, "onlyDeleteMatches");
        SparqlNormalisationRuleImpl.setSparqlRuleModeOnlyIncludeMatches(f.createURI(
                NormalisationRuleImpl.rdfruleNamespace, "onlyIncludeMatchingTriples"));
        SparqlNormalisationRuleImpl.setSparqlRuleModeAddAllMatchingTriples(f.createURI(
                NormalisationRuleImpl.rdfruleNamespace, "addAllMatchingTriples"));
    }
    
    public SparqlNormalisationRuleImpl()
    {
        super();
        
        this.addValidStage(NormalisationRuleImpl.getRdfruleStageAfterResultsImport());
        this.addValidStage(NormalisationRuleImpl.getRdfruleStageAfterResultsToPool());
    }
    
    // keyToUse is the URI of the next instance that can be found in
    // myRepository
    public SparqlNormalisationRuleImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        this.addValidStage(NormalisationRuleImpl.getRdfruleStageAfterResultsImport());
        this.addValidStage(NormalisationRuleImpl.getRdfruleStageAfterResultsToPool());
        
        Collection<Statement> currentUnrecognisedStatements = new HashSet<Statement>();
        
        currentUnrecognisedStatements.addAll(this.getUnrecognisedStatements());
        
        this.unrecognisedStatements = new HashSet<Statement>();
        
        for(Statement nextStatement : currentUnrecognisedStatements)
        {
            if(SparqlNormalisationRuleImpl._DEBUG)
            {
                SparqlNormalisationRuleImpl.log.debug("SparqlNormalisationRuleImpl: nextStatement: "
                        + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(SparqlNormalisationRuleImpl.getSparqlRuleTypeUri()))
            {
                if(SparqlNormalisationRuleImpl._TRACE)
                {
                    SparqlNormalisationRuleImpl.log
                            .trace("SparqlNormalisationRuleImpl: found valid type predicate for URI: " + keyToUse);
                }
                
                // isValid = true;
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(
                    SparqlNormalisationRuleImpl.getSparqlRuleSparqlConstructQueryTarget()))
            {
                this.setSparqlConstructQueryTarget(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(SparqlNormalisationRuleImpl.getSparqlRuleSparqlWherePattern()))
            {
                this.addSparqlWherePattern(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(SparqlNormalisationRuleImpl.getSparqlRuleSparqlPrefixes()))
            {
                this.setSparqlPrefixes(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(SparqlNormalisationRuleImpl.getSparqlRuleMode())
                    && nextStatement.getObject().equals(
                            SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyIncludeMatches()))
            {
                this.setMode(SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyIncludeMatches());
            }
            else if(nextStatement.getPredicate().equals(SparqlNormalisationRuleImpl.getSparqlRuleMode())
                    && (nextStatement.getObject().equals(
                            SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyDeleteMatches()) || nextStatement
                            .getObject().equals(SparqlNormalisationRuleImpl.OLDsparqlruleModeOnlyDeleteMatches)))
            {
                this.setMode(SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyDeleteMatches());
            }
            else if(nextStatement.getPredicate().equals(SparqlNormalisationRuleImpl.getSparqlRuleMode())
                    && nextStatement.getObject().equals(
                            SparqlNormalisationRuleImpl.getSparqlRuleModeAddAllMatchingTriples()))
            {
                this.setMode(SparqlNormalisationRuleImpl.getSparqlRuleModeAddAllMatchingTriples());
            }
            else
            {
                if(SparqlNormalisationRuleImpl._TRACE)
                {
                    SparqlNormalisationRuleImpl.log
                            .trace("SparqlNormalisationRuleImpl: unrecognisedStatement nextStatement: "
                                    + nextStatement.toString());
                }
                this.unrecognisedStatements.add(nextStatement);
            }
        }
        
        // this.relatedNamespaces = tempRelatedNamespaces;
        // this.unrecognisedStatements = tempUnrecognisedStatements;
        
        // stages.add(NormalisationRule.rdfruleStageAfterResultsImport.stringValue());
        
        // mode = sparqlruleModeOnlyIncludeMatches.stringValue();
        
        if(SparqlNormalisationRuleImpl._DEBUG)
        {
            SparqlNormalisationRuleImpl.log.debug("SparqlNormalisationRuleImpl constructor: toString()="
                    + this.toString());
        }
    }
    
    public static boolean schemaToRdf(Repository myRepository, URI contextUri, int modelVersion)
        throws OpenRDFException
    {
        NormalisationRuleImpl.schemaToRdf(myRepository, contextUri, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleTypeUri(), RDFS.LABEL,
                    f.createLiteral("A SPARQL based normalisation rule intended to normalise in-memory RDF triples."),
                    contextUri);
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleTypeUri(), RDFS.SUBCLASSOF,
                    NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextUri);
            
            // TODO: update schema
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleSparqlConstructQueryTarget(), RDF.TYPE,
                    OWL.DATATYPEPROPERTY, contextUri);
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleSparqlConstructQueryTarget(), RDFS.RANGE, RDFS.LITERAL,
                    contextUri);
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleSparqlConstructQueryTarget(), RDFS.DOMAIN,
                    SparqlNormalisationRuleImpl.getSparqlRuleTypeUri(), contextUri);
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleSparqlConstructQueryTarget(),
                    RDFS.LABEL,
                    f.createLiteral("The CONSTRUCT { ... } part of the query that will be used to match against RDF triples in memory at the assigned stages, in the form of a basic graph pattern."),
                    contextUri);
            
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleMode(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleMode(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleMode(), RDFS.DOMAIN,
                    SparqlNormalisationRuleImpl.getSparqlRuleTypeUri(), contextUri);
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleMode(),
                    RDFS.LABEL,
                    f.createLiteral("The mode that this normalisation rule will be used in. In the absence of SPARQL Update language support, this enables deletions and filtering based on the matched triples."),
                    contextUri);
            
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyIncludeMatches(),
                    RDFS.LABEL,
                    f.createLiteral("Specifies that the SPARQL rule will be applied, and only the matches from the rule will remain in the relevant RDF triple store after the application. If the stage is after Results Import, only the results from the current provider and query will be affected."),
                    contextUri);
            
            con.add(SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyDeleteMatches(),
                    RDFS.LABEL,
                    f.createLiteral("Specifies that the SPARQL rule will be applied, and the matches from the rule will be deleted from the relevant RDF triple store after the application. If the stage is after Results Import, only the results from the current provider and query will be affected."),
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
            
            SparqlNormalisationRuleImpl.log.error("RepositoryException: " + re.getMessage());
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
    
    @Override
    public Object stageQueryVariables(Object input)
    {
        return input;
    }
    
    @Override
    public Object stageAfterQueryCreation(Object input)
    {
        return input;
    }
    
    @Override
    public Object stageAfterQueryParsing(Object input)
    {
        return input;
    }
    
    @Override
    public Object stageBeforeResultsImport(Object input)
    {
        return input;
    }
    
    @Override
    public Object stageAfterResultsImport(Object input)
    {
        return doWorkBasedOnMode((Repository)input);
    }
    
    @Override
    public Object stageAfterResultsToPool(Object input)
    {
        return doWorkBasedOnMode((Repository)input);
    }
    
    @Override
    public Object stageAfterResultsToDocument(Object input)
    {
        return input;
    }
    
    private Repository doWorkBasedOnMode(Repository input)
    {
        if(getMode().equals(SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyDeleteMatches()))
        {
            return removeStatementsFromRepository(input);
        }
        else if(getMode().equals(SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyIncludeMatches()))
        {
            return chooseStatementsFromRepository(input, false);
        }
        else if(getMode().equals(SparqlNormalisationRuleImpl.getSparqlRuleModeAddAllMatchingTriples()))
        {
            return chooseStatementsFromRepository(input, true);
        }
        
        return input;
    }
    
    private Repository removeStatementsFromRepository(Repository myRepository)
    {
        try
        {
            if(SparqlNormalisationRuleImpl._DEBUG)
            {
                SparqlNormalisationRuleImpl.log
                        .debug("SparqlNormalisationRuleImpl: removing statements according to sparqlConstructQueryTarget="
                                + getSparqlConstructQuery());
            }
            
            RepositoryConnection removeConnection = myRepository.getConnection();
            
            try
            {
                for(String nextConstructQuery : getSparqlConstructQueries())
                {
                    try
                    {
                        GraphQueryResult graphResult =
                                removeConnection.prepareGraphQuery(QueryLanguage.SPARQL, nextConstructQuery).evaluate();
                        
                        int deletedStatements = 0;
                        
                        while(graphResult.hasNext())
                        {
                            removeConnection.remove(graphResult.next());
                            deletedStatements++;
                        }
                        
                        removeConnection.commit();
                        if(SparqlNormalisationRuleImpl._DEBUG)
                        {
                            SparqlNormalisationRuleImpl.log.debug("SparqlNormalisationRuleImpl: removed "
                                    + deletedStatements + " results");
                        }
                        
                    }
                    catch(Exception ex)
                    {
                        SparqlNormalisationRuleImpl.log.error(
                                "SparqlNormalisationRuleImpl: exception removing statements", ex);
                    }
                }
            }
            finally
            {
                removeConnection.close();
            }
        }
        catch(org.openrdf.repository.RepositoryException rex)
        {
            SparqlNormalisationRuleImpl.log.error(
                    "SparqlNormalisationRuleImpl: RepositoryException exception adding statements", rex);
        }
        
        return myRepository;
    }
    
    private Repository chooseStatementsFromRepository(Repository myRepository, boolean addToMyRepository)
    {
        Repository resultRepository = null;
        
        try
        {
            if(!addToMyRepository)
            {
                resultRepository = new SailRepository(new MemoryStore());
                resultRepository.initialize();
            }
            
            if(SparqlNormalisationRuleImpl._DEBUG)
            {
                SparqlNormalisationRuleImpl.log
                        .debug("SparqlNormalisationRuleImpl: selecting statements according to sparqlConstructQueryTarget="
                                + getSparqlConstructQuery());
            }
            
            RepositoryConnection selectConnection = myRepository.getConnection();
            RepositoryConnection addConnection = null;
            
            if(addToMyRepository)
            {
                addConnection = myRepository.getConnection();
            }
            else
            {
                addConnection = resultRepository.getConnection();
            }
            
            addConnection.setAutoCommit(false);
            
            try
            {
                for(String nextConstructQuery : getSparqlConstructQueries())
                {
                    try
                    {
                        GraphQueryResult graphResult =
                                selectConnection.prepareGraphQuery(QueryLanguage.SPARQL, nextConstructQuery).evaluate();
                        
                        int selectedStatements = 0;
                        
                        while(graphResult.hasNext())
                        {
                            addConnection.add(graphResult.next());
                            selectedStatements++;
                        }
                        
                        if(SparqlNormalisationRuleImpl._DEBUG)
                        {
                            SparqlNormalisationRuleImpl.log.debug("SparqlNormalisationRuleImpl: slected "
                                    + selectedStatements + " statements for results");
                        }
                        
                        addConnection.commit();
                    }
                    catch(Exception ex)
                    {
                        SparqlNormalisationRuleImpl.log.error(
                                "SparqlNormalisationRuleImpl: exception adding statements", ex);
                    }
                }
            }
            finally
            {
                selectConnection.close();
                addConnection.close();
            }
        }
        catch(org.openrdf.repository.RepositoryException rex)
        {
            SparqlNormalisationRuleImpl.log.error(
                    "SparqlNormalisationRuleImpl: RepositoryException exception adding statements", rex);
        }
        
        if(addToMyRepository)
        {
            return myRepository;
        }
        else
        {
            return resultRepository;
        }
    }
    
    public boolean runTests(Collection<RuleTest> myRules)
    {
        // TODO: implement me or delete me!
        boolean allPassed = true;
        
        // final Collection<RuleTest> myRules =
        // Settings.getRuleTestsForNormalisationRuleUri(this.getKey());
        
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
        
        result +=
                "<div class=\"rulekey\">Rule Key: " + StringUtils.xmlEncodeString(this.getKey().stringValue())
                        + "</div>\n";
        result +=
                "<div class=\"description\">Description: " + StringUtils.xmlEncodeString(this.getDescription())
                        + "</div>\n";
        result += "<div class=\"order\">Order: " + StringUtils.xmlEncodeString(this.getOrder() + "") + "</div>\n";
        result +=
                "<div class=\"inputMatchSparqlPattern\">SPARQL Construct query: "
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
                SparqlNormalisationRuleImpl.log.debug("SparqlNormalisationRuleImpl.toRdf: keyToUse=" + keyToUse);
            }
            
            final URI keyUri = this.getKey();
            final Literal sparqlConstructQueryTargetLiteral = f.createLiteral(this.getSparqlConstructQueryTarget());
            final Literal sparqlPrefixesLiteral = f.createLiteral(this.getSparqlPrefixes());
            final URI modeUri = getMode();
            
            con.setAutoCommit(false);
            
            con.add(keyUri, RDF.TYPE, SparqlNormalisationRuleImpl.getSparqlRuleTypeUri(), keyToUse);
            
            if(modelVersion >= 5)
            {
                con.add(keyUri, SparqlNormalisationRuleImpl.getSparqlRuleSparqlPrefixes(), sparqlPrefixesLiteral,
                        keyToUse);
                con.add(keyUri, SparqlNormalisationRuleImpl.getSparqlRuleSparqlConstructQueryTarget(),
                        sparqlConstructQueryTargetLiteral, keyToUse);
                
                for(String nextWherePattern : this.getSparqlWherePatterns())
                {
                    Literal sparqlWherePatternLiteral = f.createLiteral(nextWherePattern);
                    con.add(keyUri, SparqlNormalisationRuleImpl.getSparqlRuleSparqlWherePattern(),
                            sparqlWherePatternLiteral, keyToUse);
                }
            }
            else
            {
                con.add(keyUri, SparqlNormalisationRuleImpl.getOLDSparqlRuleSparqlConstructQuery(),
                        f.createLiteral(getSparqlConstructQuery()), keyToUse);
            }
            
            // the onlyDeleteMatches URI changed between versions 4 and 5, so check here and provide
            // backwards compatibility as necessary
            if(modelVersion < 5 && modeUri.equals(SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyDeleteMatches()))
            {
                con.add(keyUri, SparqlNormalisationRuleImpl.getSparqlRuleMode(),
                        SparqlNormalisationRuleImpl.OLDsparqlruleModeOnlyDeleteMatches, keyToUse);
            }
            else
            {
                con.add(keyUri, SparqlNormalisationRuleImpl.getSparqlRuleMode(), modeUri, keyToUse);
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            SparqlNormalisationRuleImpl.log.error("RepositoryException: " + re.getMessage());
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
        result += "sparqlConstructQueryTarget=" + this.getSparqlConstructQuery() + "\n";
        result += "mode=" + this.getMode() + "\n";
        result += "description=" + this.getDescription() + "\n";
        
        return result;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Collection<URI> getElementTypes()
    {
        Collection<URI> results = super.getElementTypes();
        
        results.add(SparqlNormalisationRuleImpl.getSparqlRuleTypeUri());
        return results;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.SparqlNormalisationRule#getMode()
     */
    @Override
    public URI getMode()
    {
        return mode;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.SparqlNormalisationRule#setMode(org.openrdf.model.URI)
     */
    @Override
    public void setMode(URI mode)
    {
        this.mode = mode;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.SparqlNormalisationRule#getSparqlConstructQuery()
     */
    public String getSparqlConstructQuery()
    {
        List<String> results = getSparqlConstructQueries();
        
        if(results.size() > 0)
        {
            return results.get(0);
        }
        else
        {
            SparqlNormalisationRuleImpl.log.error("Could not find a sparql construct query for key="
                    + this.getKey().stringValue());
            return "";
        }
    }
    
    @Override
    public List<String> getSparqlConstructQueries()
    {
        List<String> results = new ArrayList<String>(getSparqlWherePatterns().size());
        for(String wherePattern : getSparqlWherePatterns())
        {
            String result = getConstructQueryUsingWherePattern(wherePattern);
            
            results.add(result.toString());
        }
        
        return results;
    }
    
    /**
     * @param wherePattern
     * @return
     */
    private String getConstructQueryUsingWherePattern(String wherePattern)
    {
        return new StringBuilder(getSparqlPrefixes()).append(" CONSTRUCT { ").append(getSparqlConstructQueryTarget())
                .append(" } WHERE { ").append(wherePattern).append(" }").toString();
    }
    
    /**
     * @param sparqlruleModeOnlyDeleteMatches
     *            the sparqlruleModeOnlyDeleteMatches to set
     */
    public static void setSparqlRuleModeOnlyDeleteMatches(URI sparqlruleModeOnlyDeleteMatches)
    {
        SparqlNormalisationRuleImpl.sparqlruleModeOnlyDeleteMatches = sparqlruleModeOnlyDeleteMatches;
    }
    
    /**
     * @return the sparqlruleModeOnlyDeleteMatches
     */
    public static URI getSparqlRuleModeOnlyDeleteMatches()
    {
        return SparqlNormalisationRuleImpl.sparqlruleModeOnlyDeleteMatches;
    }
    
    /**
     * @param sparqlruleTypeUri
     *            the sparqlruleTypeUri to set
     */
    public static void setSparqlRuleTypeUri(URI sparqlruleTypeUri)
    {
        SparqlNormalisationRuleImpl.sparqlruleTypeUri = sparqlruleTypeUri;
    }
    
    /**
     * @return the sparqlruleTypeUri
     */
    public static URI getSparqlRuleTypeUri()
    {
        return SparqlNormalisationRuleImpl.sparqlruleTypeUri;
    }
    
    /**
     * @param sparqlruleSparqlConstructQueryTarget
     *            the sparqlruleSparqlConstructQueryTarget to set
     */
    public static void setSparqlRuleSparqlConstructQueryTarget(URI sparqlruleSparqlConstructQuery)
    {
        SparqlNormalisationRuleImpl.sparqlruleSparqlConstructQueryTarget = sparqlruleSparqlConstructQuery;
    }
    
    /**
     * @return the sparqlruleSparqlConstructQueryTarget
     */
    public static URI getSparqlRuleSparqlConstructQueryTarget()
    {
        return SparqlNormalisationRuleImpl.sparqlruleSparqlConstructQueryTarget;
    }
    
    public static void setSparqlRuleSparqlPrefixes(URI sparqlruleSparqlPrefixes)
    {
        SparqlNormalisationRuleImpl.sparqlruleSparqlPrefixes = sparqlruleSparqlPrefixes;
    }
    
    public static void setSparqlRuleSparqlWherePattern(URI sparqlruleSparqlWherePattern)
    {
        SparqlNormalisationRuleImpl.sparqlruleSparqlWherePattern = sparqlruleSparqlWherePattern;
    }
    
    public static URI getSparqlRuleSparqlPrefixes()
    {
        return SparqlNormalisationRuleImpl.sparqlruleSparqlPrefixes;
    }
    
    public static URI getSparqlRuleSparqlWherePattern()
    {
        return SparqlNormalisationRuleImpl.sparqlruleSparqlWherePattern;
    }
    
    /**
     * @param sparqlruleMode
     *            the sparqlruleMode to set
     */
    public static void setSparqlRuleMode(URI sparqlruleMode)
    {
        SparqlNormalisationRuleImpl.sparqlruleMode = sparqlruleMode;
    }
    
    /**
     * @return the sparqlruleMode
     */
    public static URI getSparqlRuleMode()
    {
        return SparqlNormalisationRuleImpl.sparqlruleMode;
    }
    
    /**
     * @param sparqlruleModeOnlyIncludeMatches
     *            the sparqlruleModeOnlyIncludeMatches to set
     */
    public static void setSparqlRuleModeOnlyIncludeMatches(URI sparqlruleModeOnlyIncludeMatches)
    {
        SparqlNormalisationRuleImpl.sparqlruleModeOnlyIncludeMatches = sparqlruleModeOnlyIncludeMatches;
    }
    
    /**
     * @return the sparqlruleModeOnlyIncludeMatches
     */
    public static URI getSparqlRuleModeOnlyIncludeMatches()
    {
        return SparqlNormalisationRuleImpl.sparqlruleModeOnlyIncludeMatches;
    }
    
    /**
     * @param sparqlruleModeAddAllMatchingTriples
     *            the sparqlruleModeAddAllMatchingTriples to set
     */
    public static void setSparqlRuleModeAddAllMatchingTriples(URI sparqlruleModeAddAllMatchingTriples)
    {
        SparqlNormalisationRuleImpl.sparqlruleModeAddAllMatchingTriples = sparqlruleModeAddAllMatchingTriples;
    }
    
    /**
     * @return the sparqlruleModeAddAllMatchingTriples
     */
    public static URI getSparqlRuleModeAddAllMatchingTriples()
    {
        return SparqlNormalisationRuleImpl.sparqlruleModeAddAllMatchingTriples;
    }
    
    @Override
    public String getSparqlConstructQueryTarget()
    {
        return this.sparqlConstructQueryTarget;
    }
    
    @Override
    public void setSparqlConstructQueryTarget(String sparqlConstructQueryTarget)
    {
        this.sparqlConstructQueryTarget = sparqlConstructQueryTarget;
    }
    
    @Override
    public List<String> getSparqlWherePatterns()
    {
        return this.sparqlWherePatterns;
    }
    
    @Override
    public void addSparqlWherePattern(String sparqlWherePattern)
    {
        this.sparqlWherePatterns.add(sparqlWherePattern);
    }
    
    @Override
    public String getSparqlPrefixes()
    {
        return this.sparqlPrefixes;
    }
    
    @Override
    public void setSparqlPrefixes(String sparqlPrefixes)
    {
        this.sparqlPrefixes = sparqlPrefixes;
    }
    
    private static void setOLDSparqlRuleSparqlConstructQuery(URI sparqlruleSparqlConstructQuery)
    {
        SparqlNormalisationRuleImpl.sparqlruleSparqlConstructQuery = sparqlruleSparqlConstructQuery;
        
    }
    
    private static URI getOLDSparqlRuleSparqlConstructQuery()
    {
        return SparqlNormalisationRuleImpl.sparqlruleSparqlConstructQuery;
    }
}
