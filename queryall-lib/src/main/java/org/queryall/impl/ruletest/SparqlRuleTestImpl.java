/**
 * 
 */
package org.queryall.impl.ruletest;

import java.util.Collection;
import java.util.HashSet;
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
import org.queryall.api.ruletest.RuleTestSchema;
import org.queryall.api.ruletest.SparqlRuleTest;
import org.queryall.api.ruletest.SparqlRuleTestSchema;
import org.queryall.api.utils.Constants;
import org.queryall.utils.RdfUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SparqlRuleTestImpl extends RuleTestImpl implements SparqlRuleTest
{
    private static final Set<URI> SPARQL_RULE_TEST_IMPL_TYPES = new HashSet<URI>();
    
    static
    {
        SparqlRuleTestImpl.SPARQL_RULE_TEST_IMPL_TYPES.add(RuleTestSchema.getRuletestTypeUri());
        SparqlRuleTestImpl.SPARQL_RULE_TEST_IMPL_TYPES.add(SparqlRuleTestSchema.getSparqlRuleTestTypeUri());
    }
    
    public static Set<URI> myTypes()
    {
        return SparqlRuleTestImpl.SPARQL_RULE_TEST_IMPL_TYPES;
    }
    
    private String askQuery = "";
    
    private boolean expectedResult = true;
    private String testInputTriples;
    private String testInputMimeType;
    
    public SparqlRuleTestImpl()
    {
        super();
    }
    
    public SparqlRuleTestImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = this.resetUnrecognisedStatements();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(this.log.isDebugEnabled())
            {
                this.log.debug("SparqlRuleTestImpl: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(SparqlRuleTestSchema.getSparqlRuleTestTypeUri()))
            {
                if(this.log.isTraceEnabled())
                {
                    this.log.trace("SparqlRuleTestImpl: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(SparqlRuleTestSchema.getSparqlRuletestSparqlAskPattern()))
            {
                this.setTestSparqlAsk(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(SparqlRuleTestSchema.getSparqlRuletestExpectedResult()))
            {
                this.setExpectedResult(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(SparqlRuleTestSchema.getSparqlRuletestInputTriples()))
            {
                this.setTestInputTriples(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(SparqlRuleTestSchema.getSparqlRuletestInputMimeType()))
            {
                this.setTestInputMimeType(nextStatement.getObject().stringValue());
            }
            else
            {
                if(this.log.isDebugEnabled())
                {
                    this.log.debug("SparqlRuleTestImpl: found unexpected Statement nextStatement: "
                            + nextStatement.toString());
                }
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(this.log.isTraceEnabled())
        {
            this.log.trace("StringRuleTestImpl.fromRdf: would have returned... result=" + this.toString());
        }
    }
    
    @Override
    public Set<URI> getElementTypes()
    {
        return SparqlRuleTestImpl.myTypes();
    }
    
    @Override
    public boolean getExpectedResult()
    {
        return this.expectedResult;
    }
    
    @Override
    public String getTestInputMimeType()
    {
        return this.testInputMimeType;
    }
    
    @Override
    public String getTestInputTriples()
    {
        return this.testInputTriples;
    }
    
    @Override
    public String getTestSparqlAsk()
    {
        return this.askQuery;
    }
    
    @Override
    public void setExpectedResult(final boolean expectedResult)
    {
        this.expectedResult = expectedResult;
    }
    
    @Override
    public void setTestInputMimeType(final String testInputMimeType)
    {
        this.testInputMimeType = testInputMimeType;
    }
    
    @Override
    public void setTestInputTriples(final String testInputTriples)
    {
        this.testInputTriples = testInputTriples;
        
    }
    
    @Override
    public void setTestSparqlAsk(final String testSparqlAsk)
    {
        this.askQuery = testSparqlAsk;
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... keyToUse)
        throws OpenRDFException
    {
        super.toRdf(myRepository, modelVersion, keyToUse);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            final URI keyUri = this.getKey();
            final Literal testAskQueryLiteral = f.createLiteral(this.askQuery);
            final Literal testExpectedResultLiteral = f.createLiteral(this.expectedResult);
            final Literal testInputTriplesLiteral = f.createLiteral(this.testInputTriples);
            final Literal testInputMimeTypeLiteral = f.createLiteral(this.testInputMimeType);
            
            con.begin();
            
            con.add(keyUri, RDF.TYPE, SparqlRuleTestSchema.getSparqlRuleTestTypeUri(), keyToUse);
            con.add(keyUri, SparqlRuleTestSchema.getSparqlRuletestSparqlAskPattern(), testAskQueryLiteral, keyToUse);
            con.add(keyUri, SparqlRuleTestSchema.getSparqlRuletestExpectedResult(), testExpectedResultLiteral, keyToUse);
            con.add(keyUri, SparqlRuleTestSchema.getSparqlRuletestInputTriples(), testInputTriplesLiteral, keyToUse);
            con.add(keyUri, SparqlRuleTestSchema.getSparqlRuletestInputMimeType(), testInputMimeTypeLiteral, keyToUse);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            this.log.error("RepositoryException: " + re.getMessage());
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
}
