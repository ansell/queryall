package org.queryall.impl.rdfrule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.SparqlNormalisationRule;
import org.queryall.api.rdfrule.SparqlNormalisationRuleSchema;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SparqlNormalisationRuleImpl extends NormalisationRuleImpl implements SparqlNormalisationRule
{
    private static final Logger log = LoggerFactory.getLogger(SparqlNormalisationRuleImpl.class);
    private static final boolean _TRACE = SparqlNormalisationRuleImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = SparqlNormalisationRuleImpl.log.isDebugEnabled();
    private static final boolean _INFO = SparqlNormalisationRuleImpl.log.isInfoEnabled();
    
    private String sparqlConstructQueryTarget = "";
    
    private List<String> sparqlWherePatterns = new ArrayList<String>(2);
    
    private String sparqlPrefixes = "";
    
    private URI mode = SparqlNormalisationRuleSchema.getSparqlRuleModeOnlyDeleteMatches();
    
    static
    {
        // register this query type implementation with the central register
        // NormalisationRuleEnum.register(SparqlNormalisationRuleImpl.class.getName(),
        // SparqlNormalisationRuleImpl.myTypes());
    }
    
    public static Set<URI> myTypes()
    {
        final Set<URI> results = new HashSet<URI>();
        
        results.add(NormalisationRuleSchema.getNormalisationRuleTypeUri());
        results.add(SparqlNormalisationRuleSchema.getSparqlRuleTypeUri());
        
        return results;
    }
    
    public SparqlNormalisationRuleImpl()
    {
        super();
        
        this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
        this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
    }
    
    // keyToUse is the URI of the next instance that can be found in
    // myRepository
    public SparqlNormalisationRuleImpl(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
        this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
        
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
                    && nextStatement.getObject().equals(SparqlNormalisationRuleSchema.getSparqlRuleTypeUri()))
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
                    SparqlNormalisationRuleSchema.getSparqlRuleSparqlConstructQueryTarget()))
            {
                this.setSparqlConstructQueryTarget(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate()
                    .equals(SparqlNormalisationRuleSchema.getSparqlRuleSparqlWherePattern()))
            {
                this.addSparqlWherePattern(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(SparqlNormalisationRuleSchema.getSparqlRuleSparqlPrefixes()))
            {
                this.setSparqlPrefixes(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(SparqlNormalisationRuleSchema.getSparqlRuleMode())
                    && nextStatement.getObject().equals(
                            SparqlNormalisationRuleSchema.getSparqlRuleModeOnlyIncludeMatches()))
            {
                this.setMode(SparqlNormalisationRuleSchema.getSparqlRuleModeOnlyIncludeMatches());
            }
            else if(nextStatement.getPredicate().equals(SparqlNormalisationRuleSchema.getSparqlRuleMode())
                    && (nextStatement.getObject().equals(
                            SparqlNormalisationRuleSchema.getSparqlRuleModeOnlyDeleteMatches()) || nextStatement
                            .getObject().equals(SparqlNormalisationRuleSchema.getOLDsparqlruleModeOnlyDeleteMatches())))
            {
                this.setMode(SparqlNormalisationRuleSchema.getSparqlRuleModeOnlyDeleteMatches());
            }
            else if(nextStatement.getPredicate().equals(SparqlNormalisationRuleSchema.getSparqlRuleMode())
                    && nextStatement.getObject().equals(
                            SparqlNormalisationRuleSchema.getSparqlRuleModeAddAllMatchingTriples()))
            {
                this.setMode(SparqlNormalisationRuleSchema.getSparqlRuleModeAddAllMatchingTriples());
            }
            else
            {
                if(SparqlNormalisationRuleImpl._INFO)
                {
                    SparqlNormalisationRuleImpl.log
                            .info("SparqlNormalisationRuleImpl: unrecognisedStatement nextStatement: "
                                    + nextStatement.toString());
                }
                this.addUnrecognisedStatement(nextStatement);
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
    
    /**
     * Performs changes to the input repository based on the mode of this rule
     * 
     * @param input A repository containing the current set of RDF statements
     * @return A repository  containing the output set of RDF statements, after normalisation by this rule
     */
    private Repository doWorkBasedOnMode(final Repository input)
    {
        if(this.getMode().equals(SparqlNormalisationRuleSchema.getSparqlRuleModeOnlyDeleteMatches()))
        {
            return this.removeStatementsFromRepository(input);
        }
        else if(this.getMode().equals(SparqlNormalisationRuleSchema.getSparqlRuleModeOnlyIncludeMatches()))
        {
            return this.chooseStatementsFromRepository(input, false);
        }
        else if(this.getMode().equals(SparqlNormalisationRuleSchema.getSparqlRuleModeAddAllMatchingTriples()))
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
    public Set<URI> getElementTypes()
    {
        return SparqlNormalisationRuleImpl.myTypes();
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
            
            con.add(keyUri, RDF.TYPE, SparqlNormalisationRuleSchema.getSparqlRuleTypeUri(), keyToUse);
            
            if(modelVersion >= 5)
            {
                con.add(keyUri, SparqlNormalisationRuleSchema.getSparqlRuleSparqlPrefixes(), sparqlPrefixesLiteral,
                        keyToUse);
                con.add(keyUri, SparqlNormalisationRuleSchema.getSparqlRuleSparqlConstructQueryTarget(),
                        sparqlConstructQueryTargetLiteral, keyToUse);
                
                for(final String nextWherePattern : this.getSparqlWherePatterns())
                {
                    final Literal sparqlWherePatternLiteral = f.createLiteral(nextWherePattern);
                    con.add(keyUri, SparqlNormalisationRuleSchema.getSparqlRuleSparqlWherePattern(),
                            sparqlWherePatternLiteral, keyToUse);
                }
            }
            else
            {
                con.add(keyUri, SparqlNormalisationRuleSchema.getOLDSparqlRuleSparqlConstructQuery(),
                        f.createLiteral(this.getSparqlConstructQuery()), keyToUse);
            }
            
            // the onlyDeleteMatches URI changed between versions 4 and 5, so check here and provide
            // backwards compatibility as necessary
            if(modelVersion < 5 && modeUri.equals(SparqlNormalisationRuleSchema.getSparqlRuleModeOnlyDeleteMatches()))
            {
                con.add(keyUri, SparqlNormalisationRuleSchema.getSparqlRuleMode(),
                        SparqlNormalisationRuleSchema.getOLDsparqlruleModeOnlyDeleteMatches(), keyToUse);
            }
            else
            {
                con.add(keyUri, SparqlNormalisationRuleSchema.getSparqlRuleMode(), modeUri, keyToUse);
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
        result += "mode=" + this.getMode() + "\n";
        result += "description=" + this.getDescription() + "\n";
        
        return result;
    }
}
