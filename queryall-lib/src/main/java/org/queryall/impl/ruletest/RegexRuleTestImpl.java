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
import org.queryall.api.ruletest.RegexRuleTest;
import org.queryall.api.ruletest.RegexRuleTestSchema;
import org.queryall.api.ruletest.RuleTestSchema;
import org.queryall.api.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RegexRuleTestImpl extends RuleTestImpl implements RegexRuleTest
{
    private static final Logger log = LoggerFactory.getLogger(RegexRuleTestImpl.class);
    private static final boolean _TRACE = RegexRuleTestImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = RegexRuleTestImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RegexRuleTestImpl.log.isInfoEnabled();
    
    public static Set<URI> myTypes()
    {
        final Set<URI> results = new HashSet<URI>();
        
        results.add(RuleTestSchema.getRuletestTypeUri());
        results.add(RegexRuleTestSchema.getRegexRuletestTypeUri());
        
        return results;
    }
    
    private String testInputString = "";
    
    private String testOutputString = "";
    
    public RegexRuleTestImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = new HashSet<Statement>();
        
        currentUnrecognisedStatements.addAll(this.getUnrecognisedStatements());
        
        this.unrecognisedStatements = new HashSet<Statement>();
        
        for(final Statement nextStatement : inputStatements)
        {
            if(RegexRuleTestImpl._DEBUG)
            {
                RegexRuleTestImpl.log.debug("RegexRuleTestImpl: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(RegexRuleTestSchema.getRegexRuletestTypeUri()))
            {
                if(RegexRuleTestImpl._TRACE)
                {
                    RegexRuleTestImpl.log.trace("RegexRuleTestImpl: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(RegexRuleTestSchema.getRuletestInputTestString()))
            {
                this.setTestInputString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(RegexRuleTestSchema.getRuletestOutputTestString()))
            {
                this.setTestOutputString(nextStatement.getObject().stringValue());
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(RegexRuleTestImpl._TRACE)
        {
            RegexRuleTestImpl.log.trace("RegexRuleTestImpl.fromRdf: would have returned... result=" + this.toString());
        }
    }
    
    /**
     * @return the testInputString
     */
    @Override
    public String getTestInputString()
    {
        return this.testInputString;
    }
    
    /**
     * @return the testOutputString
     */
    @Override
    public String getTestOutputString()
    {
        return this.testOutputString;
    }
    
    /**
     * @param testInputString
     *            the testInputString to set
     */
    @Override
    public void setTestInputString(final String testInputString)
    {
        this.testInputString = testInputString;
    }
    
    /**
     * @param testOutputString
     *            the testOutputString to set
     */
    @Override
    public void setTestOutputString(final String testOutputString)
    {
        this.testOutputString = testOutputString;
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super.toRdf(myRepository, keyToUse, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            final URI keyUri = this.getKey();
            final Literal testInputStringLiteral = f.createLiteral(this.testInputString);
            final Literal testOutputStringLiteral = f.createLiteral(this.testOutputString);
            
            con.setAutoCommit(false);
            
            con.add(keyUri, RDF.TYPE, RegexRuleTestSchema.getRegexRuletestTypeUri(), keyToUse);
            con.add(keyUri, RegexRuleTestSchema.getRuletestInputTestString(), testInputStringLiteral, keyToUse);
            con.add(keyUri, RegexRuleTestSchema.getRuletestOutputTestString(), testOutputStringLiteral, keyToUse);
            
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
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            RegexRuleTestImpl.log.error("RepositoryException: " + re.getMessage());
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
}
