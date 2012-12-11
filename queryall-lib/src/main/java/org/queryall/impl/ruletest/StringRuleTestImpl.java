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
import org.queryall.api.ruletest.StringRuleTest;
import org.queryall.api.ruletest.StringRuleTestSchema;
import org.queryall.api.utils.Constants;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class StringRuleTestImpl extends RuleTestImpl implements StringRuleTest
{
    private static final Set<URI> STRING_RULE_TEST_IMPL_TYPES = new HashSet<URI>();
    
    static
    {
        StringRuleTestImpl.STRING_RULE_TEST_IMPL_TYPES.add(RuleTestSchema.getRuletestTypeUri());
        StringRuleTestImpl.STRING_RULE_TEST_IMPL_TYPES.add(StringRuleTestSchema.getStringRuleTestTypeUri());
    }
    
    public static Set<URI> myTypes()
    {
        return StringRuleTestImpl.STRING_RULE_TEST_IMPL_TYPES;
    }
    
    private String testInputString = "";
    
    private String testOutputString = "";
    
    public StringRuleTestImpl()
    {
        super();
    }
    
    public StringRuleTestImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = this.resetUnrecognisedStatements();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(this.log.isTraceEnabled())
            {
                this.log.trace("StringRuleTestImpl: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(StringRuleTestSchema.getStringRuleTestTypeUri()))
            {
                if(this.log.isTraceEnabled())
                {
                    this.log.trace("StringRuleTestImpl: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(StringRuleTestSchema.getRuletestInputTestString()))
            {
                this.setTestInputString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(StringRuleTestSchema.getRuletestOutputTestString()))
            {
                this.setTestOutputString(nextStatement.getObject().stringValue());
            }
            else
            {
                if(this.log.isDebugEnabled())
                {
                    this.log.debug("StringRuleTestImpl: found unexpected Statement nextStatement: "
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
        return StringRuleTestImpl.myTypes();
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
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... keyToUse)
        throws OpenRDFException
    {
        super.toRdf(myRepository, modelVersion, keyToUse);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            final URI keyUri = this.getKey();
            final Literal testInputStringLiteral = f.createLiteral(this.testInputString);
            final Literal testOutputStringLiteral = f.createLiteral(this.testOutputString);
            
            con.setAutoCommit(false);
            
            con.add(keyUri, RDF.TYPE, StringRuleTestSchema.getStringRuleTestTypeUri(), keyToUse);
            con.add(keyUri, StringRuleTestSchema.getRuletestInputTestString(), testInputStringLiteral, keyToUse);
            con.add(keyUri, StringRuleTestSchema.getRuletestOutputTestString(), testOutputStringLiteral, keyToUse);
            
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
