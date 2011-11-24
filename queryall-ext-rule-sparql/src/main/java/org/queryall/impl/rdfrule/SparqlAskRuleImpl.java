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
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.base.HtmlExport;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.SparqlAskRule;
import org.queryall.api.rdfrule.SparqlAskRuleSchema;
import org.queryall.api.rdfrule.SparqlNormalisationRuleSchema;
import org.queryall.api.rdfrule.ValidatingRuleSchema;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.exception.QueryAllException;
import org.queryall.exception.ValidationFailedException;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SparqlAskRuleImpl extends BaseValidatingRuleImpl implements SparqlAskRule, HtmlExport
{
    private static final Logger log = LoggerFactory.getLogger(SparqlAskRuleImpl.class);
    private static final boolean _TRACE = SparqlAskRuleImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = SparqlAskRuleImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SparqlAskRuleImpl.log.isInfoEnabled();
    
    private List<String> sparqlWherePatterns = new ArrayList<String>(2);
    
    private String sparqlPrefixes = "";
    
    private static final Set<URI> SPARQL_ASK_RULE_IMPL_TYPES = new HashSet<URI>(6);
    private static final Set<URI> SPARQL_ASK_RULE_VALID_STAGES = new HashSet<URI>(5);
    
    static
    {
        SparqlAskRuleImpl.SPARQL_ASK_RULE_IMPL_TYPES.add(NormalisationRuleSchema.getNormalisationRuleTypeUri());
        SparqlAskRuleImpl.SPARQL_ASK_RULE_IMPL_TYPES.add(ValidatingRuleSchema.getValidatingRuleTypeUri());
        SparqlAskRuleImpl.SPARQL_ASK_RULE_IMPL_TYPES.add(SparqlNormalisationRuleSchema.getSparqlRuleTypeUri());
        SparqlAskRuleImpl.SPARQL_ASK_RULE_IMPL_TYPES.add(SparqlAskRuleSchema.getSparqlAskRuleTypeUri());
        
        SparqlAskRuleImpl.SPARQL_ASK_RULE_VALID_STAGES.add(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
        SparqlAskRuleImpl.SPARQL_ASK_RULE_VALID_STAGES.add(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
    }
    
    public static Set<URI> myTypes()
    {
        return SparqlAskRuleImpl.SPARQL_ASK_RULE_IMPL_TYPES;
    }
    
    public SparqlAskRuleImpl()
    {
        super();
    }
    
    // keyToUse is the URI of the next instance that can be found in
    // myRepository
    public SparqlAskRuleImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
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
                    && (nextStatement.getObject().equals(SparqlAskRuleSchema.getSparqlAskRuleTypeUri()) || nextStatement
                            .getObject().equals(SparqlNormalisationRuleSchema.getSparqlRuleTypeUri())))
            {
                if(SparqlAskRuleImpl._TRACE)
                {
                    SparqlAskRuleImpl.log.trace("SparqlNormalisationRuleImpl: found valid type predicate for URI: "
                            + keyToUse);
                }
                
                // isValid = true;
                this.setKey(keyToUse);
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
            else
            {
                if(SparqlAskRuleImpl._DEBUG)
                {
                    SparqlAskRuleImpl.log.debug("SparqlNormalisationRuleImpl: unrecognisedStatement nextStatement: "
                            + nextStatement.toString());
                }
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(SparqlAskRuleImpl._DEBUG)
        {
            SparqlAskRuleImpl.log.debug("SparqlNormalisationRuleImpl constructor: toString()=" + this.toString());
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
    private String getAskQueryUsingWherePattern(final String wherePattern)
    {
        return new StringBuilder(this.getSparqlPrefixes()).append(" ASK WHERE { ").append(wherePattern).append(" }")
                .toString();
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Set<URI> getElementTypes()
    {
        return SparqlAskRuleImpl.myTypes();
    }
    
    @Override
    public List<String> getSparqlAskQueries()
    {
        final List<String> results = new ArrayList<String>(this.getSparqlWherePatterns().size());
        for(final String wherePattern : this.getSparqlWherePatterns())
        {
            final String result = this.getAskQueryUsingWherePattern(wherePattern);
            
            results.add(result.toString());
        }
        
        return results;
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
    
    @Override
    public void setSparqlPrefixes(final String sparqlPrefixes)
    {
        this.sparqlPrefixes = sparqlPrefixes;
    }
    
    @Override
    protected Set<URI> setupValidStages()
    {
        return SparqlAskRuleImpl.SPARQL_ASK_RULE_VALID_STAGES;
    }
    
    @Override
    public boolean stageAfterQueryCreation(final Object input)
    {
        return true;
    }
    
    @Override
    public boolean stageAfterQueryParsing(final Object input)
    {
        return true;
    }
    
    @Override
    public boolean stageAfterResultsImport(final Object input) throws ValidationFailedException
    {
        try
        {
            return RdfUtils.checkSparqlAskQueries((Repository)input, this.getSparqlAskQueries());
        }
        catch(final QueryAllException e)
        {
            throw new ValidationFailedException(e);
        }
    }
    
    @Override
    public boolean stageAfterResultsToDocument(final Object input)
    {
        return true;
    }
    
    @Override
    public boolean stageAfterResultsToPool(final Object input) throws ValidationFailedException
    {
        try
        {
            return RdfUtils.checkSparqlAskQueries((Repository)input, this.getSparqlAskQueries());
        }
        catch(final QueryAllException e)
        {
            throw new ValidationFailedException(e);
        }
    }
    
    @Override
    public boolean stageBeforeResultsImport(final Object input) throws ValidationFailedException
    {
        try
        {
            return RdfUtils.checkSparqlAskQueries((Repository)input, this.getSparqlAskQueries());
        }
        catch(final QueryAllException e)
        {
            throw new ValidationFailedException(e);
        }
    }
    
    @Override
    public boolean stageQueryVariables(final Object input)
    {
        return true;
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
            if(SparqlAskRuleImpl._DEBUG)
            {
                SparqlAskRuleImpl.log.debug("SparqlNormalisationRuleImpl.toRdf: keyToUse=" + keyToUse);
            }
            
            final URI keyUri = this.getKey();
            final Literal sparqlPrefixesLiteral = f.createLiteral(this.getSparqlPrefixes());
            
            con.setAutoCommit(false);
            
            con.add(keyUri, RDF.TYPE, SparqlNormalisationRuleSchema.getSparqlRuleTypeUri(), keyToUse);
            
            con.add(keyUri, SparqlNormalisationRuleSchema.getSparqlRuleSparqlPrefixes(), sparqlPrefixesLiteral,
                    keyToUse);
            
            for(final String nextWherePattern : this.getSparqlWherePatterns())
            {
                final Literal sparqlWherePatternLiteral = f.createLiteral(nextWherePattern);
                con.add(keyUri, SparqlNormalisationRuleSchema.getSparqlRuleSparqlWherePattern(),
                        sparqlWherePatternLiteral, keyToUse);
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            SparqlAskRuleImpl.log.error("RepositoryException: " + re.getMessage());
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
        result += "description=" + this.getDescription() + "\n";
        
        return result;
    }
}
