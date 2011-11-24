package org.queryall.impl.rdfrule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.base.HtmlExport;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.SparqlConstructRule;
import org.queryall.api.rdfrule.SparqlConstructRuleSchema;
import org.queryall.api.rdfrule.SparqlNormalisationRuleSchema;
import org.queryall.api.rdfrule.TransformingRuleSchema;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.exception.InvalidStageException;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SparqlConstructRuleImpl extends BaseTransformingRuleImpl implements SparqlConstructRule, HtmlExport
{
    private static final Logger log = LoggerFactory.getLogger(SparqlConstructRuleImpl.class);
    private static final boolean _TRACE = SparqlConstructRuleImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = SparqlConstructRuleImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SparqlConstructRuleImpl.log.isInfoEnabled();
    
    private String sparqlConstructQueryTarget = "";
    
    private List<String> sparqlWherePatterns = new ArrayList<String>(2);
    
    private String sparqlPrefixes = "";
    
    private URI mode = SparqlConstructRuleSchema.getSparqlRuleModeOnlyDeleteMatches();
    
    private static final Set<URI> SPARQL_CONSTRUCT_RULE_IMPL_TYPES = new HashSet<URI>();
    
    static
    {
        SparqlConstructRuleImpl.SPARQL_CONSTRUCT_RULE_IMPL_TYPES.add(NormalisationRuleSchema
                .getNormalisationRuleTypeUri());
        SparqlConstructRuleImpl.SPARQL_CONSTRUCT_RULE_IMPL_TYPES.add(TransformingRuleSchema
                .getTransformingRuleTypeUri());
        SparqlConstructRuleImpl.SPARQL_CONSTRUCT_RULE_IMPL_TYPES.add(SparqlNormalisationRuleSchema
                .getSparqlRuleTypeUri());
        SparqlConstructRuleImpl.SPARQL_CONSTRUCT_RULE_IMPL_TYPES.add(SparqlConstructRuleSchema
                .getSparqlConstructRuleTypeUri());
    }
    
    public static Set<URI> myTypes()
    {
        return SparqlConstructRuleImpl.SPARQL_CONSTRUCT_RULE_IMPL_TYPES;
    }
    
    public SparqlConstructRuleImpl()
    {
        super();
    }
    
    // keyToUse is the URI of the next instance that can be found in
    // myRepository
    public SparqlConstructRuleImpl(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = this.resetUnrecognisedStatements();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            // if(SparqlNormalisationRuleImpl._DEBUG)
            // {
            // SparqlNormalisationRuleImpl.log.debug("SparqlNormalisationRuleImpl: nextStatement: "
            // + nextStatement.toString());
            // }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && (nextStatement.getObject().equals(SparqlConstructRuleSchema.getSparqlConstructRuleTypeUri()))
                    || nextStatement.getObject().equals(SparqlNormalisationRuleSchema.getSparqlRuleTypeUri()))
            {
                if(SparqlConstructRuleImpl._TRACE)
                {
                    SparqlConstructRuleImpl.log
                            .trace("SparqlNormalisationRuleImpl: found valid type predicate for URI: " + keyToUse);
                }
                
                // isValid = true;
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(
                    SparqlConstructRuleSchema.getSparqlRuleSparqlConstructQueryTarget()))
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
            else if(nextStatement.getPredicate().equals(SparqlConstructRuleSchema.getSparqlRuleMode())
                    && nextStatement.getObject()
                            .equals(SparqlConstructRuleSchema.getSparqlRuleModeOnlyIncludeMatches()))
            {
                this.setMode(SparqlConstructRuleSchema.getSparqlRuleModeOnlyIncludeMatches());
            }
            else if(nextStatement.getPredicate().equals(SparqlConstructRuleSchema.getSparqlRuleMode())
                    && (nextStatement.getObject()
                            .equals(SparqlConstructRuleSchema.getSparqlRuleModeOnlyDeleteMatches()) || nextStatement
                            .getObject().equals(SparqlConstructRuleSchema.getOLDsparqlruleModeOnlyDeleteMatches())))
            {
                this.setMode(SparqlConstructRuleSchema.getSparqlRuleModeOnlyDeleteMatches());
            }
            else if(nextStatement.getPredicate().equals(SparqlConstructRuleSchema.getSparqlRuleMode())
                    && nextStatement.getObject().equals(
                            SparqlConstructRuleSchema.getSparqlRuleModeAddAllMatchingTriples()))
            {
                this.setMode(SparqlConstructRuleSchema.getSparqlRuleModeAddAllMatchingTriples());
            }
            else
            {
                if(SparqlConstructRuleImpl._DEBUG)
                {
                    SparqlConstructRuleImpl.log
                            .debug("SparqlNormalisationRuleImpl: unrecognisedStatement nextStatement: "
                                    + nextStatement.toString());
                }
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        // this.relatedNamespaces = tempRelatedNamespaces;
        // this.unrecognisedStatements = tempUnrecognisedStatements;
        
        // stages.add(NormalisationRule.rdfruleStageAfterResultsImport.stringValue());
        
        // mode = sparqlruleModeOnlyIncludeMatches.stringValue();
        
        if(SparqlConstructRuleImpl._DEBUG)
        {
            SparqlConstructRuleImpl.log.debug("SparqlNormalisationRuleImpl constructor: toString()=" + this.toString());
        }
    }
    
    @Override
    public void addSparqlWherePattern(final String sparqlWherePattern)
    {
        this.sparqlWherePatterns.add(sparqlWherePattern);
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
        return SparqlConstructRuleImpl.myTypes();
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
            SparqlConstructRuleImpl.log.error("Could not find a sparql construct query for key="
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
    
    /**
     * @return the validStages
     */
    @Override
    public Set<URI> getValidStages()
    {
        if(this.validStages.size() == 0)
        {
            try
            {
                this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
                this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
            }
            catch(final InvalidStageException e)
            {
                SparqlConstructRuleImpl.log
                        .error("InvalidStageException found from hardcoded stage URI insertion, bad things may happen now!",
                                e);
                throw new RuntimeException("Found fatal InvalidStageException in hardcoded stage URI insertion", e);
            }
        }
        
        return Collections.unmodifiableSet(this.validStages);
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
        return RdfUtils.doSparqlConstructWorkBasedOnMode((Repository)input, this.getMode(),
                this.getSparqlConstructQueries());
    }
    
    @Override
    public Object stageAfterResultsToDocument(final Object input)
    {
        return input;
    }
    
    @Override
    public Object stageAfterResultsToPool(final Object input)
    {
        return RdfUtils.doSparqlConstructWorkBasedOnMode((Repository)input, this.getMode(),
                this.getSparqlConstructQueries());
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
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... keyToUse)
        throws OpenRDFException
    {
        super.toRdf(myRepository, modelVersion, keyToUse);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            if(SparqlConstructRuleImpl._DEBUG)
            {
                SparqlConstructRuleImpl.log.debug("SparqlNormalisationRuleImpl.toRdf: keyToUse=" + keyToUse);
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
                con.add(keyUri, SparqlConstructRuleSchema.getSparqlRuleSparqlConstructQueryTarget(),
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
                con.add(keyUri, SparqlConstructRuleSchema.getOLDSparqlRuleSparqlConstructQuery(),
                        f.createLiteral(this.getSparqlConstructQuery()), keyToUse);
            }
            
            // the onlyDeleteMatches URI changed between versions 4 and 5, so check here and provide
            // backwards compatibility as necessary
            if(modelVersion < 5 && modeUri.equals(SparqlConstructRuleSchema.getSparqlRuleModeOnlyDeleteMatches()))
            {
                con.add(keyUri, SparqlConstructRuleSchema.getSparqlRuleMode(),
                        SparqlConstructRuleSchema.getOLDsparqlruleModeOnlyDeleteMatches(), keyToUse);
            }
            else
            {
                con.add(keyUri, SparqlConstructRuleSchema.getSparqlRuleMode(), modeUri, keyToUse);
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            SparqlConstructRuleImpl.log.error("RepositoryException: " + re.getMessage());
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
