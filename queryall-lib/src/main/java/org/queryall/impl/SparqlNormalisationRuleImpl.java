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
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.enumerations.Constants;
import org.queryall.utils.StringUtils;

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
    
    private static URI getOLDSparqlRuleSparqlConstructQuery()
    {
        return SparqlNormalisationRuleImpl.sparqlruleSparqlConstructQuery;
    }
    
    // public String key;
    // public String description = "";
    // public String curationStatus = ProjectImpl.projectNotCuratedUri.stringValue();
    
    /**
     * @return the sparqlruleMode
     */
    public static URI getSparqlRuleMode()
    {
        return SparqlNormalisationRuleImpl.sparqlruleMode;
    }
    
    /**
     * @return the sparqlruleModeAddAllMatchingTriples
     */
    public static URI getSparqlRuleModeAddAllMatchingTriples()
    {
        return SparqlNormalisationRuleImpl.sparqlruleModeAddAllMatchingTriples;
    }
    
    /**
     * @return the sparqlruleModeOnlyDeleteMatches
     */
    public static URI getSparqlRuleModeOnlyDeleteMatches()
    {
        return SparqlNormalisationRuleImpl.sparqlruleModeOnlyDeleteMatches;
    }
    
    /**
     * @return the sparqlruleModeOnlyIncludeMatches
     */
    public static URI getSparqlRuleModeOnlyIncludeMatches()
    {
        return SparqlNormalisationRuleImpl.sparqlruleModeOnlyIncludeMatches;
    }
    
    /**
     * @return the sparqlruleSparqlConstructQueryTarget
     */
    public static URI getSparqlRuleSparqlConstructQueryTarget()
    {
        return SparqlNormalisationRuleImpl.sparqlruleSparqlConstructQueryTarget;
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
     * @return the sparqlruleTypeUri
     */
    public static URI getSparqlRuleTypeUri()
    {
        return SparqlNormalisationRuleImpl.sparqlruleTypeUri;
    }
    
    private static void setOLDSparqlRuleSparqlConstructQuery(final URI sparqlruleSparqlConstructQuery)
    {
        SparqlNormalisationRuleImpl.sparqlruleSparqlConstructQuery = sparqlruleSparqlConstructQuery;
        
    }
    
    /**
     * @param sparqlruleMode
     *            the sparqlruleMode to set
     */
    public static void setSparqlRuleMode(final URI sparqlruleMode)
    {
        SparqlNormalisationRuleImpl.sparqlruleMode = sparqlruleMode;
    }
    
    /**
     * @param sparqlruleModeAddAllMatchingTriples
     *            the sparqlruleModeAddAllMatchingTriples to set
     */
    public static void setSparqlRuleModeAddAllMatchingTriples(final URI sparqlruleModeAddAllMatchingTriples)
    {
        SparqlNormalisationRuleImpl.sparqlruleModeAddAllMatchingTriples = sparqlruleModeAddAllMatchingTriples;
    }
    
    /**
     * @param sparqlruleModeOnlyDeleteMatches
     *            the sparqlruleModeOnlyDeleteMatches to set
     */
    public static void setSparqlRuleModeOnlyDeleteMatches(final URI sparqlruleModeOnlyDeleteMatches)
    {
        SparqlNormalisationRuleImpl.sparqlruleModeOnlyDeleteMatches = sparqlruleModeOnlyDeleteMatches;
    }
    
    /**
     * @param sparqlruleModeOnlyIncludeMatches
     *            the sparqlruleModeOnlyIncludeMatches to set
     */
    public static void setSparqlRuleModeOnlyIncludeMatches(final URI sparqlruleModeOnlyIncludeMatches)
    {
        SparqlNormalisationRuleImpl.sparqlruleModeOnlyIncludeMatches = sparqlruleModeOnlyIncludeMatches;
    }
    
    /**
     * @param sparqlruleSparqlConstructQueryTarget
     *            the sparqlruleSparqlConstructQueryTarget to set
     */
    public static void setSparqlRuleSparqlConstructQueryTarget(final URI sparqlruleSparqlConstructQuery)
    {
        SparqlNormalisationRuleImpl.sparqlruleSparqlConstructQueryTarget = sparqlruleSparqlConstructQuery;
    }
    
    // public static String rdfruleNamespace;
    
    public static void setSparqlRuleSparqlPrefixes(final URI sparqlruleSparqlPrefixes)
    {
        SparqlNormalisationRuleImpl.sparqlruleSparqlPrefixes = sparqlruleSparqlPrefixes;
    }
    
    public static void setSparqlRuleSparqlWherePattern(final URI sparqlruleSparqlWherePattern)
    {
        SparqlNormalisationRuleImpl.sparqlruleSparqlWherePattern = sparqlruleSparqlWherePattern;
    }
    
    /**
     * @param sparqlruleTypeUri
     *            the sparqlruleTypeUri to set
     */
    public static void setSparqlRuleTypeUri(final URI sparqlruleTypeUri)
    {
        SparqlNormalisationRuleImpl.sparqlruleTypeUri = sparqlruleTypeUri;
    }
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
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
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        SparqlNormalisationRuleImpl.setSparqlRuleTypeUri(f.createURI(baseUri,
                "SparqlNormalisationRule"));
        SparqlNormalisationRuleImpl.setOLDSparqlRuleSparqlConstructQuery(f.createURI(
                baseUri, "sparqlConstructQuery"));
        SparqlNormalisationRuleImpl.setSparqlRuleSparqlConstructQueryTarget(f.createURI(
                baseUri, "sparqlConstructQueryTarget"));
        SparqlNormalisationRuleImpl.setSparqlRuleSparqlWherePattern(f.createURI(baseUri,
                "sparqlWherePatterns"));
        SparqlNormalisationRuleImpl.setSparqlRuleSparqlPrefixes(f.createURI(baseUri,
                "sparqlPrefixes"));
        SparqlNormalisationRuleImpl.setSparqlRuleMode(f.createURI(baseUri, "mode"));
        SparqlNormalisationRuleImpl.setSparqlRuleModeOnlyDeleteMatches(f.createURI(
                baseUri, "onlyDeleteMatchingTriples"));
        SparqlNormalisationRuleImpl.OLDsparqlruleModeOnlyDeleteMatches =
                f.createURI(baseUri, "onlyDeleteMatches");
        SparqlNormalisationRuleImpl.setSparqlRuleModeOnlyIncludeMatches(f.createURI(
                baseUri, "onlyIncludeMatchingTriples"));
        SparqlNormalisationRuleImpl.setSparqlRuleModeAddAllMatchingTriples(f.createURI(
                baseUri, "addAllMatchingTriples"));
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
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
    
    public SparqlNormalisationRuleImpl()
    {
        super();
        
        this.addValidStage(NormalisationRuleImpl.getRdfruleStageAfterResultsImport());
        this.addValidStage(NormalisationRuleImpl.getRdfruleStageAfterResultsToPool());
    }
    
    // keyToUse is the URI of the next instance that can be found in
    // myRepository
    public SparqlNormalisationRuleImpl(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        this.addValidStage(NormalisationRuleImpl.getRdfruleStageAfterResultsImport());
        this.addValidStage(NormalisationRuleImpl.getRdfruleStageAfterResultsToPool());
        
        final Collection<Statement> currentUnrecognisedStatements = new HashSet<Statement>();
        
        currentUnrecognisedStatements.addAll(this.getUnrecognisedStatements());
        
        this.unrecognisedStatements = new HashSet<Statement>();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
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
    
    @Override
    public void addSparqlWherePattern(final String sparqlWherePattern)
    {
        this.sparqlWherePatterns.add(sparqlWherePattern);
    }
    
    private Repository chooseStatementsFromRepository(final Repository myRepository, final boolean addToMyRepository)
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
                                + this.getSparqlConstructQuery());
            }
            
            final RepositoryConnection selectConnection = myRepository.getConnection();
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
                for(final String nextConstructQuery : this.getSparqlConstructQueries())
                {
                    try
                    {
                        final GraphQueryResult graphResult =
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
                    catch(final Exception ex)
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
        catch(final org.openrdf.repository.RepositoryException rex)
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
    
    private Repository doWorkBasedOnMode(final Repository input)
    {
        if(this.getMode().equals(SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyDeleteMatches()))
        {
            return this.removeStatementsFromRepository(input);
        }
        else if(this.getMode().equals(SparqlNormalisationRuleImpl.getSparqlRuleModeOnlyIncludeMatches()))
        {
            return this.chooseStatementsFromRepository(input, false);
        }
        else if(this.getMode().equals(SparqlNormalisationRuleImpl.getSparqlRuleModeAddAllMatchingTriples()))
        {
            return this.chooseStatementsFromRepository(input, true);
        }
        
        return input;
    }
    
    /**
     * @param wherePattern
     * @return
     */
    private String getConstructQueryUsingWherePattern(final String wherePattern)
    {
        return new StringBuilder(this.getSparqlPrefixes()).append(" CONSTRUCT { ")
                .append(this.getSparqlConstructQueryTarget()).append(" } WHERE { ").append(wherePattern).append(" }")
                .toString();
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Collection<URI> getElementTypes()
    {
        final Collection<URI> results = super.getElementTypes();
        
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
        return this.mode;
    }
    
    @Override
    public List<String> getSparqlConstructQueries()
    {
        final List<String> results = new ArrayList<String>(this.getSparqlWherePatterns().size());
        for(final String wherePattern : this.getSparqlWherePatterns())
        {
            final String result = this.getConstructQueryUsingWherePattern(wherePattern);
            
            results.add(result.toString());
        }
        
        return results;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.SparqlNormalisationRule#getSparqlConstructQuery()
     */
    public String getSparqlConstructQuery()
    {
        final List<String> results = this.getSparqlConstructQueries();
        
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
    public String getSparqlConstructQueryTarget()
    {
        return this.sparqlConstructQueryTarget;
    }
    
    @Override
    public String getSparqlPrefixes()
    {
        return this.sparqlPrefixes;
    }
    
    @Override
    public List<String> getSparqlWherePatterns()
    {
        return this.sparqlWherePatterns;
    }
    
    private Repository removeStatementsFromRepository(final Repository myRepository)
    {
        try
        {
            if(SparqlNormalisationRuleImpl._DEBUG)
            {
                SparqlNormalisationRuleImpl.log
                        .debug("SparqlNormalisationRuleImpl: removing statements according to sparqlConstructQueryTarget="
                                + this.getSparqlConstructQuery());
            }
            
            final RepositoryConnection removeConnection = myRepository.getConnection();
            
            try
            {
                for(final String nextConstructQuery : this.getSparqlConstructQueries())
                {
                    try
                    {
                        final GraphQueryResult graphResult =
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
                    catch(final Exception ex)
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
        catch(final org.openrdf.repository.RepositoryException rex)
        {
            SparqlNormalisationRuleImpl.log.error(
                    "SparqlNormalisationRuleImpl: RepositoryException exception adding statements", rex);
        }
        
        return myRepository;
    }
    
    public boolean runTests(final Collection<RuleTest> myRules)
    {
        // TODO: implement me or delete me!
        final boolean allPassed = true;
        
        // final Collection<RuleTest> myRules =
        // Settings.getRuleTestsForNormalisationRuleUri(this.getKey());
        
        // for(final RuleTest nextRuleTest : myRules)
        // {
        //
        // }
        
        return allPassed;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.SparqlNormalisationRule#setMode(org.openrdf.model.URI)
     */
    @Override
    public void setMode(final URI mode)
    {
        this.mode = mode;
    }
    
    @Override
    public void setSparqlConstructQueryTarget(final String sparqlConstructQueryTarget)
    {
        this.sparqlConstructQueryTarget = sparqlConstructQueryTarget;
    }
    
    @Override
    public void setSparqlPrefixes(final String sparqlPrefixes)
    {
        this.sparqlPrefixes = sparqlPrefixes;
    }
    
    @Override
    public Object stageAfterQueryCreation(final Object input)
    {
        return input;
    }
    
    @Override
    public Object stageAfterQueryParsing(final Object input)
    {
        return input;
    }
    
    @Override
    public Object stageAfterResultsImport(final Object input)
    {
        return this.doWorkBasedOnMode((Repository)input);
    }
    
    @Override
    public Object stageAfterResultsToDocument(final Object input)
    {
        return input;
    }
    
    @Override
    public Object stageAfterResultsToPool(final Object input)
    {
        return this.doWorkBasedOnMode((Repository)input);
    }
    
    @Override
    public Object stageBeforeResultsImport(final Object input)
    {
        return input;
    }
    
    @Override
    public Object stageQueryVariables(final Object input)
    {
        return input;
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
        final StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        final String prefix = "rdfrule_";
        
        return sb.toString();
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
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
            final URI modeUri = this.getMode();
            
            con.setAutoCommit(false);
            
            con.add(keyUri, RDF.TYPE, SparqlNormalisationRuleImpl.getSparqlRuleTypeUri(), keyToUse);
            
            if(modelVersion >= 5)
            {
                con.add(keyUri, SparqlNormalisationRuleImpl.getSparqlRuleSparqlPrefixes(), sparqlPrefixesLiteral,
                        keyToUse);
                con.add(keyUri, SparqlNormalisationRuleImpl.getSparqlRuleSparqlConstructQueryTarget(),
                        sparqlConstructQueryTargetLiteral, keyToUse);
                
                for(final String nextWherePattern : this.getSparqlWherePatterns())
                {
                    final Literal sparqlWherePatternLiteral = f.createLiteral(nextWherePattern);
                    con.add(keyUri, SparqlNormalisationRuleImpl.getSparqlRuleSparqlWherePattern(),
                            sparqlWherePatternLiteral, keyToUse);
                }
            }
            else
            {
                con.add(keyUri, SparqlNormalisationRuleImpl.getOLDSparqlRuleSparqlConstructQuery(),
                        f.createLiteral(this.getSparqlConstructQuery()), keyToUse);
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
}
