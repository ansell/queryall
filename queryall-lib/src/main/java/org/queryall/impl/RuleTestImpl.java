package org.queryall.impl;

import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;

import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryConnection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.queryall.api.RuleTest;
import org.queryall.helpers.*;

import org.apache.log4j.Logger;

/**
 * An implementation of the RuleTest class
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RuleTestImpl implements RuleTest
{
    private static final Logger log = Logger.getLogger( RuleTest.class.getName() );
    private static final boolean _TRACE = log.isTraceEnabled();
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
    private String title;
    
    
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
                            
        final ValueFactory f = Constants.valueFactory;
        
        setRuletestTypeUri(f.createURI( ruletestNamespace,"RuleTest" ));
        setRuletestHasRuleUri(f.createURI( ruletestNamespace,"testsRules" ));
        setRuletestTestsStage(f.createURI( ruletestNamespace,"testsStages" ));
        
        setRuletestInputTestString(f.createURI( ruletestNamespace,"inputTestString" ));
        setRuletestOutputTestString(f.createURI( ruletestNamespace,"outputTestString" ));
    }
    
    public RuleTestImpl(Collection<Statement> inputStatements, URI keyToUse , int modelVersion )  throws OpenRDFException
    {
        Collection<URI> tempTestUris = new HashSet<URI>();
        Collection<URI> tempStages = new HashSet<URI>();
        
        for( Statement nextStatement : inputStatements )
        {
        	if(_DEBUG)
        	{
        		log.debug( "RuleTest: nextStatement: "+nextStatement.toString() );
        	}
        	
            if( nextStatement.getPredicate().equals( RDF.TYPE )  && nextStatement.getObject().equals( getRuletestTypeUri() ) )
            {
            	if(_TRACE)
            	{
            		log.trace( "RuleTest: found valid type predicate for URI: "+keyToUse );
            	}
            	
                this.setKey(keyToUse);
            }
            else if( nextStatement.getPredicate().equals( ProjectImpl.getProjectCurationStatusUri() ) )
            {
                this.setCurationStatus((URI)nextStatement.getObject());
            }
            else if( nextStatement.getPredicate().equals( getRuletestHasRuleUri() ) )
            {
                tempTestUris.add( (URI)nextStatement.getObject() );
            }
            else if( nextStatement.getPredicate().equals( getRuletestTestsStage() ) )
            {
                tempStages.add( (URI)nextStatement.getObject() );
            }           
            else if( nextStatement.getPredicate().equals( getRuletestInputTestString() ) )
            {
                this.setTestInputString(nextStatement.getObject().stringValue());
            }
            else if( nextStatement.getPredicate().equals( getRuletestOutputTestString() ) )
            {
                this.setTestOutputString(nextStatement.getObject().stringValue());
            }
            else
            {
                this.addUnrecognisedStatement( nextStatement );
            }
        }
        
        this.setRuleUris(tempTestUris);
        this.setStages(tempStages);
        
        if( _TRACE )
        {
            log.trace( "RuleTest.fromRdf: would have returned... result="+this.toString() );
        }
	}

	public static boolean schemaToRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            URI contextKeyUri = keyToUse;
            con.setAutoCommit(false);
            
            con.add(getRuletestTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            con.add(getRuletestTypeUri(), RDFS.LABEL, f.createLiteral("A test case for normalisation rules."), contextKeyUri);
            
            con.add(getRuletestHasRuleUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getRuletestHasRuleUri(), RDFS.RANGE, NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextKeyUri);
            con.add(getRuletestHasRuleUri(), RDFS.DOMAIN, getRuletestTypeUri(), contextKeyUri);
            con.add(getRuletestHasRuleUri(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);

            con.add(getRuletestTestsStage(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getRuletestTestsStage(), RDFS.RANGE, RDFS.RESOURCE, contextKeyUri);
            con.add(getRuletestTestsStage(), RDFS.DOMAIN, getRuletestTypeUri(), contextKeyUri);
            con.add(getRuletestTestsStage(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(getRuletestInputTestString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getRuletestInputTestString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getRuletestInputTestString(), RDFS.DOMAIN, getRuletestTypeUri(), contextKeyUri);
            con.add(getRuletestInputTestString(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(getRuletestOutputTestString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getRuletestOutputTestString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getRuletestOutputTestString(), RDFS.DOMAIN, getRuletestTypeUri(), contextKeyUri);
            con.add(getRuletestOutputTestString(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
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
    
    @Override
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            URI keyUri = this.getKey();
            Literal testInputStringLiteral = f.createLiteral( testInputString );
            Literal testOutputStringLiteral = f.createLiteral( testOutputString );
            
            URI curationStatusLiteral = null;
            
            if( curationStatus == null )
                curationStatusLiteral = ProjectImpl.getProjectNotCuratedUri();
            else
                curationStatusLiteral = curationStatus;
                
            con.setAutoCommit( false );
            
            con.add( keyUri, RDF.TYPE, getRuletestTypeUri(), keyToUse );
            con.add( keyUri, ProjectImpl.getProjectCurationStatusUri(), curationStatusLiteral, keyToUse );
            con.add( keyUri, getRuletestInputTestString(), testInputStringLiteral, keyToUse );
            con.add( keyUri, getRuletestOutputTestString(), testOutputStringLiteral, keyToUse );
            
            if( rdfRuleUris != null )
            {
                for( URI nextRdfRuleUri : rdfRuleUris )
                {
                    con.add( keyUri, getRuletestHasRuleUri(), nextRdfRuleUri , keyToUse );
                }
            }
            
            if( unrecognisedStatements != null )
            {
                for( Statement nextUnrecognisedStatement : unrecognisedStatements )
                {
                    con.add( nextUnrecognisedStatement , keyToUse);
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
    
    @Override
    public String toString()
    {
        String result = "\n";
        
        result += "key="+key+"\n";
        result += "testInputString="+testInputString + "\n";
        result += "testOutputString="+testOutputString + "\n";
        result += "rdfRuleUris="+rdfRuleUris + "\n";
        
        return result;
    }
    
    @Override
    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        String prefix = "ruletest_";
        
        return sb.toString();
    }
    
    @Override
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

    @Override
    public void setTitle(String title)
    {
        this.title = title;
    }

    @Override
    public String getTitle()
    {
        return this.title;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
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
    @Override
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
    @Override
    public URI getKey()
    {
        return key;
    }

    /**
     * @param key the key to set
     */
    @Override
    public void setKey(String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }

    @Override
    public void setKey(URI nextKey)
    {
        this.key = nextKey;
    }
    /**
     * @return the namespace used to represent objects of this type by default
     */
    @Override
    public String getDefaultNamespace()
    {
        return defaultNamespace;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class, including abstract implementations
     */
    @Override
    public Collection<URI> getElementTypes()
    {
        Collection<URI> results = new ArrayList<URI>(1);
    	
    	results.add(getRuletestTypeUri());
    	
    	return results;
    }
    
    /**
     * @return the rdfRuleUris
     */
    @Override
    public Collection<URI> getRuleUris()
    {
        return rdfRuleUris;
    }

    /**
     * @param rdfRuleUris the rdfRuleUris to set
     */
    @Override
    public void setRuleUris(Collection<URI> rdfRuleUris)
    {
        this.rdfRuleUris = rdfRuleUris;
    }

    /**
     * @return the stages
     */
    @Override
    public Collection<URI> getStages()
    {
        return stages;
    }

    /**
     * @param stages the stages to set
     */
    @Override
    public void setStages(Collection<URI> stages)
    {
        this.stages = stages;
    }

    /**
     * @return the testInputString
     */
    @Override
    public String getTestInputString()
    {
        return testInputString;
    }

    /**
     * @param testInputString the testInputString to set
     */
    @Override
    public void setTestInputString(String testInputString)
    {
        this.testInputString = testInputString;
    }

    /**
     * @return the testOutputString
     */
    @Override
    public String getTestOutputString()
    {
        return testOutputString;
    }

    /**
     * @param testOutputString the testOutputString to set
     */
    @Override
    public void setTestOutputString(String testOutputString)
    {
        this.testOutputString = testOutputString;
    }

    /**
     * @return the curationStatus
     */
    @Override
    public URI getCurationStatus()
    {
        return curationStatus;
    }

    /**
     * @param curationStatus the curationStatus to set
     */
    @Override
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

    @Override
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

    @Override
    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        unrecognisedStatements.add(unrecognisedStatement);
    }

    @Override
    public Collection<Statement> getUnrecognisedStatements()
    {
        return unrecognisedStatements;
    }

    /**
     * @param ruletestTestsStage the ruletestTestsStage to set
     */
    public static void setRuletestTestsStage(URI ruletestTestsStage)
    {
        RuleTestImpl.ruletestTestsStage = ruletestTestsStage;
    }

    /**
     * @return the ruletestTestsStage
     */
    public static URI getRuletestTestsStage()
    {
        return ruletestTestsStage;
    }

    /**
     * @param ruletestHasRuleUri the ruletestHasRuleUri to set
     */
    public static void setRuletestHasRuleUri(URI ruletestHasRuleUri)
    {
        RuleTestImpl.ruletestHasRuleUri = ruletestHasRuleUri;
    }

    /**
     * @param ruletestTypeUri the ruletestTypeUri to set
     */
    public static void setRuletestTypeUri(URI ruletestTypeUri)
    {
        RuleTestImpl.ruletestTypeUri = ruletestTypeUri;
    }

    /**
     * @param ruletestInputTestString the ruletestInputTestString to set
     */
    public static void setRuletestInputTestString(
            URI ruletestInputTestString)
    {
        RuleTestImpl.ruletestInputTestString = ruletestInputTestString;
    }

    /**
     * @param ruletestOutputTestString the ruletestOutputTestString to set
     */
    public static void setRuletestOutputTestString(
            URI ruletestOutputTestString)
    {
        RuleTestImpl.ruletestOutputTestString = ruletestOutputTestString;
    }

    
}
