package org.queryall.impl;

import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;

import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.Collection;
import java.util.HashSet;

import org.queryall.RuleTest;
import org.queryall.helpers.*;

import org.apache.log4j.Logger;

/**
 * An implementation of the RuleTest class
 * 
 * @author peter
 *
 */
public class RuleTestImpl extends RuleTest
{
    private static final Logger log = Logger.getLogger( RuleTest.class.getName() );
    private static final boolean _TRACE = log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.getSettings().getNamespaceForRuleTest();
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key = null;
    private Collection<URI> rdfRuleUris = new HashSet<URI>();
    private Collection<URI> stages = new HashSet<URI>();
    private String testInputString = "";
    private String testOutputString = "";
    private URI curationStatus = ProjectImpl.getProjectNotCuratedUri();
    private URI profileIncludeExcludeOrder = ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri();
    
    
    private static URI ruletestTypeUri;
    private static URI ruletestHasRuleUri;
    private static URI ruletestTestsStage;
    private static URI ruletestInputTestString;
    private static URI ruletestOutputTestString;
    
    private static String ruletestNamespace;
    
    static
    {
        ruletestNamespace = Settings.getSettings().getOntologyTermUriPrefix()
                            +Settings.getSettings().getNamespaceForRuleTest()
                            +Settings.getSettings().getOntologyTermUriSuffix();
                            
        try
        {
            Repository myStaticRepository = new SailRepository( new MemoryStore() );
            myStaticRepository.initialize();
            ValueFactory f = myStaticRepository.getValueFactory();
            
            ruletestTypeUri= f.createURI( ruletestNamespace+"RuleTest" );
            ruletestHasRuleUri = f.createURI( ruletestNamespace+"testsRules" );
            ruletestTestsStage = f.createURI( ruletestNamespace+"testsStages" );
            
            ruletestInputTestString = f.createURI( ruletestNamespace+"inputTestString" );
            ruletestOutputTestString = f.createURI( ruletestNamespace+"outputTestString" );
        }
        catch ( RepositoryException re )
        {
            log.error( re.getMessage() );
        }
    }
    
    public static boolean schemaToRdf(Repository myRepository, String keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            URI contextKeyUri = f.createURI( keyToUse );
            con.setAutoCommit( false );
            
            con.add( ruletestTypeUri, RDF.TYPE, OWL.CLASS, contextKeyUri );
            
            con.add( ruletestHasRuleUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri );
            
            con.add( ruletestTestsStage, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri );
            
            con.add( ruletestInputTestString, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri );
            
            con.add( ruletestOutputTestString, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri );
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch ( RepositoryException re )
        {
            // Something went wrong during the transaction, so we roll it back
            if( con != null )
                con.rollback();
                
            log.error( "RepositoryException: "+re.getMessage() );
        }
        finally
        {
            if( con != null )
                con.close();
        }
        
        return false;
    }
    
    // keyToUse is the URI of the next instance that can be found in myRepository
    public static RuleTest fromRdf( Collection<Statement> inputStatements, URI keyToUse , int modelVersion )  throws OpenRDFException
    {
        RuleTest result = new RuleTestImpl();
        
        boolean resultIsValid = false;
        
        Collection<URI> tempTestUris = new HashSet<URI>();
        Collection<URI> tempStages = new HashSet<URI>();
        
        for( Statement nextStatement : inputStatements )
        {
            log.debug( "RuleTest: nextStatement: "+nextStatement.toString() );
            
            if( nextStatement.getPredicate().equals( RDF.TYPE )  && nextStatement.getObject().equals( ruletestTypeUri ) )
            {
                log.trace( "RuleTest: found valid type predicate for URI: "+keyToUse );
                resultIsValid = true;
                result.setKey(keyToUse);
            }
            else if( nextStatement.getPredicate().equals( ProjectImpl.getProjectCurationStatusUri() ) )
            {
                result.setCurationStatus((URI)nextStatement.getObject());
            }
            else if( nextStatement.getPredicate().equals( ruletestHasRuleUri ) )
            {
                tempTestUris.add( (URI)nextStatement.getObject() );
            }
            else if( nextStatement.getPredicate().equals( ruletestTestsStage ) )
            {
                tempStages.add( (URI)nextStatement.getObject() );
            }           
            else if( nextStatement.getPredicate().equals( ruletestInputTestString ) )
            {
                result.setTestInputString(nextStatement.getObject().stringValue());
            }
            else if( nextStatement.getPredicate().equals( ruletestOutputTestString ) )
            {
                result.setTestOutputString(nextStatement.getObject().stringValue());
            }
            else
            {
                result.addUnrecognisedStatement( nextStatement );
            }
        }
        
        result.setRuleUris(tempTestUris);
        result.setStages(tempStages);
        
        if( _TRACE )
        {
            log.trace( "RuleTest.fromRdf: would have returned... result="+result.toString() );
        }
        
        if( resultIsValid )
        {
            return result;
        }
        else
        {
            throw new RuntimeException( "RuleTest.fromRdf: result was not valid" );
        }
    }
    

    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            URI keyUri = keyToUse;
            Literal testInputStringLiteral = f.createLiteral( testInputString );
            Literal testOutputStringLiteral = f.createLiteral( testOutputString );
            
            URI curationStatusLiteral = null;
            
            if( curationStatus == null )
                curationStatusLiteral = ProjectImpl.getProjectNotCuratedUri();
            else
                curationStatusLiteral = curationStatus;
                
            con.setAutoCommit( false );
            
            con.add( keyUri, RDF.TYPE, ruletestTypeUri, keyUri );
            con.add( keyUri, ProjectImpl.getProjectCurationStatusUri(), curationStatusLiteral, keyUri );
            con.add( keyUri, ruletestInputTestString, testInputStringLiteral, keyUri );
            con.add( keyUri, ruletestOutputTestString, testOutputStringLiteral, keyUri );
            
            if( rdfRuleUris != null )
            {
                for( URI nextRdfRuleUri : rdfRuleUris )
                {
                    con.add( keyUri, ruletestHasRuleUri, nextRdfRuleUri , keyUri );
                }
            }
            
            if( unrecognisedStatements != null )
            {
                for( Statement nextUnrecognisedStatement : unrecognisedStatements )
                {
                    con.add( nextUnrecognisedStatement );
                }
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch ( RepositoryException re )
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            log.error( "RepositoryException: "+re.getMessage() );
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
    

    public String toString()
    {
        String result = "\n";
        
        result += "key="+key+"\n";
        result += "testInputString="+testInputString + "\n";
        result += "testOutputString="+testOutputString + "\n";
        result += "rdfRuleUris="+rdfRuleUris + "\n";
        
        return result;
    }
    

    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        String prefix = "ruletest_";
        
        return sb.toString();
    }
    

    public String toHtml()
    {
        StringBuilder sb = new StringBuilder();
        
        String prefix = "ruletest_";
        
        sb.append("<div class=\""+prefix+"rulekey\">Rule Key: "+StringUtils.xmlEncodeString( getKey().stringValue() )  + "</div>\n");
        sb.append("<div class=\""+prefix+"testInputString\">Test Input String: "+StringUtils.xmlEncodeString( testInputString+"" )  + "</div>\n");
        sb.append("<div class=\""+prefix+"testOutputString\">Test Output String: "+StringUtils.xmlEncodeString( testOutputString+"" )  + "</div>\n");
        sb.append("<div class=\""+prefix+"rdfruleuri\">Tests RDF Rules: "+StringUtils.xmlEncodeString( rdfRuleUris.toString() )  + "</div>\n");
        
        return sb.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */

    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((curationStatus == null) ? 0 : curationStatus.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result
                + ((getRuleUris() == null) ? 0 : getRuleUris().hashCode());
        result = prime * result
                + ((testInputString == null) ? 0 : testInputString.hashCode());
        result = prime
                * result
                + ((testOutputString == null) ? 0 : testOutputString.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */

    public boolean equals(Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(obj == null)
        {
            return false;
        }
        if(!(obj instanceof RuleTest))
        {
            return false;
        }
        RuleTest other = (RuleTest) obj;
        if(curationStatus == null)
        {
            if(other.getCurationStatus() != null)
            {
                return false;
            }
        }
        else if(!curationStatus.equals(other.getCurationStatus()))
        {
            return false;
        }
        if(key == null)
        {
            if(other.getKey() != null)
            {
                return false;
            }
        }
        else if(!key.equals(other.getKey()))
        {
            return false;
        }
        if(rdfRuleUris == null)
        {
            if(other.getRuleUris() != null)
            {
                return false;
            }
        }
        else if(!rdfRuleUris.equals(other.getRuleUris()))
        {
            return false;
        }
        if(testInputString == null)
        {
            if(other.getTestInputString() != null)
            {
                return false;
            }
        }
        else if(!testInputString.equals(other.getTestInputString()))
        {
            return false;
        }
        if(testOutputString == null)
        {
            if(other.getTestOutputString() != null)
            {
                return false;
            }
        }
        else if(!testOutputString.equals(other.getTestOutputString()))
        {
            return false;
        }
        return true;
    }

    /**
     * @return the key
     */

    public URI getKey()
    {
        return key;
    }

    /**
     * @param key the key to set
     */

    public void setKey(String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }

    public void setKey(URI nextKey)
    {
        this.key = nextKey;
    }
    /**
     * @return the namespace used to represent objects of this type by default
     */

    public String getDefaultNamespace()
    {
        return defaultNamespace;
    }
    
    /**
     * @return the URI used for the rdf Type of these elements
     */

    public String getElementType()
    {
        return ruletestTypeUri.stringValue();
    }
    
    /**
     * @return the rdfRuleUris
     */
    public Collection<URI> getRuleUris()
    {
        return rdfRuleUris;
    }

    /**
     * @param rdfRuleUris the rdfRuleUris to set
     */
    public void setRuleUris(Collection<URI> rdfRuleUris)
    {
        this.rdfRuleUris = rdfRuleUris;
    }

    /**
     * @return the stages
     */
    public Collection<URI> getStages()
    {
        return stages;
    }

    /**
     * @param stages the stages to set
     */
    public void setStages(Collection<URI> stages)
    {
        this.stages = stages;
    }

    /**
     * @return the testInputString
     */
    public String getTestInputString()
    {
        return testInputString;
    }

    /**
     * @param testInputString the testInputString to set
     */
    public void setTestInputString(String testInputString)
    {
        this.testInputString = testInputString;
    }

    /**
     * @return the testOutputString
     */
    public String getTestOutputString()
    {
        return testOutputString;
    }

    /**
     * @param testOutputString the testOutputString to set
     */
    public void setTestOutputString(String testOutputString)
    {
        this.testOutputString = testOutputString;
    }

    /**
     * @return the curationStatus
     */
    public URI getCurationStatus()
    {
        return curationStatus;
    }

    /**
     * @param curationStatus the curationStatus to set
     */
    public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }

    /**
     * @return the ruletestTypeUri
     */
    public static URI getRuletestTypeUri()
    {
        return ruletestTypeUri;
    }

    /**
     * @return the ruletestHasRuleUri
     */
    public static URI getRuletestHasRuleUri()
    {
        return ruletestHasRuleUri;
    }

    /**
     * @return the ruletestInputTestString
     */
    public static URI getRuletestInputTestString()
    {
        return ruletestInputTestString;
    }

    /**
     * @return the ruletestOutputTestString
     */
    public static URI getRuletestOutputTestString()
    {
        return ruletestOutputTestString;
    }

    /**
     * @return the ruletestNamespace
     */
    public static String getRuletestNamespace()
    {
        return ruletestNamespace;
    }

    public URI getProfileIncludeExcludeOrder()
    {
        return profileIncludeExcludeOrder;
    }

    public void setProfileIncludeExcludeOrder(URI profileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = profileIncludeExcludeOrder;
    }

    public int compareTo(RuleTest otherRuleTest)
    {
        @SuppressWarnings("unused")
        final int BEFORE = -1;
        final int EQUAL = 0;
        @SuppressWarnings("unused")
        final int AFTER = 1;
    
        if ( this == otherRuleTest ) 
            return EQUAL;
    
        return getKey().stringValue().compareTo(otherRuleTest.getKey().stringValue());
    }    

    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        unrecognisedStatements.add(unrecognisedStatement);
    }

    public Collection<Statement> getUnrecognisedStatements()
    {
        return unrecognisedStatements;
    }

    
}
