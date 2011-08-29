package org.queryall.impl.ruletest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.project.ProjectSchema;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.ruletest.RuleTestSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the RuleTest class
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RuleTestImpl implements RuleTest
{
    private static final Logger log = LoggerFactory.getLogger(RuleTestImpl.class);
    private static final boolean _TRACE = RuleTestImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = RuleTestImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RuleTestImpl.log.isInfoEnabled();
    

    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    private URI key = null;
    
    private Collection<URI> rdfRuleUris = new HashSet<URI>();
    
    private Collection<URI> stages = new HashSet<URI>();
    
    private String testInputString = "";
    
    private String testOutputString = "";
    
    private URI curationStatus = ProjectSchema.getProjectNotCuratedUri();
    
    private String title;
    

    public RuleTestImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final Collection<URI> tempTestUris = new HashSet<URI>();
        final Collection<URI> tempStages = new HashSet<URI>();
        
        for(final Statement nextStatement : inputStatements)
        {
            if(RuleTestImpl._DEBUG)
            {
                RuleTestImpl.log.debug("RuleTest: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(RuleTestSchema.getRuletestTypeUri()))
            {
                if(RuleTestImpl._TRACE)
                {
                    RuleTestImpl.log.trace("RuleTest: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectSchema.getProjectCurationStatusUri()))
            {
                this.setCurationStatus((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(RuleTestSchema.getRuletestHasRuleUri()))
            {
                tempTestUris.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(RuleTestSchema.getRuletestTestsStage()))
            {
                tempStages.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(RuleTestSchema.getRuletestInputTestString()))
            {
                this.setTestInputString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(RuleTestSchema.getRuletestOutputTestString()))
            {
                this.setTestOutputString(nextStatement.getObject().stringValue());
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        this.setRuleUris(tempTestUris);
        this.setStages(tempStages);
        
        if(RuleTestImpl._TRACE)
        {
            RuleTestImpl.log.trace("RuleTest.fromRdf: would have returned... result=" + this.toString());
        }
    }
    
    @Override
    public void addUnrecognisedStatement(final Statement unrecognisedStatement)
    {
        this.unrecognisedStatements.add(unrecognisedStatement);
    }
    
    @Override
    public int compareTo(final RuleTest otherRuleTest)
    {
        @SuppressWarnings("unused")
        final int BEFORE = -1;
        final int EQUAL = 0;
        @SuppressWarnings("unused")
        final int AFTER = 1;
        
        if(this == otherRuleTest)
        {
            return EQUAL;
        }
        
        return this.getKey().stringValue().compareTo(otherRuleTest.getKey().stringValue());
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
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
        final RuleTest other = (RuleTest)obj;
        if(this.curationStatus == null)
        {
            if(other.getCurationStatus() != null)
            {
                return false;
            }
        }
        else if(!this.curationStatus.equals(other.getCurationStatus()))
        {
            return false;
        }
        if(this.key == null)
        {
            if(other.getKey() != null)
            {
                return false;
            }
        }
        else if(!this.key.equals(other.getKey()))
        {
            return false;
        }
        if(this.rdfRuleUris == null)
        {
            if(other.getRuleUris() != null)
            {
                return false;
            }
        }
        else if(!this.rdfRuleUris.equals(other.getRuleUris()))
        {
            return false;
        }
        if(this.testInputString == null)
        {
            if(other.getTestInputString() != null)
            {
                return false;
            }
        }
        else if(!this.testInputString.equals(other.getTestInputString()))
        {
            return false;
        }
        if(this.testOutputString == null)
        {
            if(other.getTestOutputString() != null)
            {
                return false;
            }
        }
        else if(!this.testOutputString.equals(other.getTestOutputString()))
        {
            return false;
        }
        return true;
    }
    
    /**
     * @return the curationStatus
     */
    @Override
    public URI getCurationStatus()
    {
        return this.curationStatus;
    }
    
    /**
     * @return the namespace used to represent objects of this type by default
     */
    @Override
    public QueryAllNamespaces getDefaultNamespace()
    {
        return QueryAllNamespaces.RULETEST;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Collection<URI> getElementTypes()
    {
        return myTypes();
    }
    
    /**
     * @return the key
     */
    @Override
    public URI getKey()
    {
        return this.key;
    }
    
    /**
     * @return the rdfRuleUris
     */
    @Override
    public Collection<URI> getRuleUris()
    {
        return this.rdfRuleUris;
    }
    
    /**
     * @return the stages
     */
    @Override
    public Collection<URI> getStages()
    {
        return this.stages;
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
    
    @Override
    public String getTitle()
    {
        return this.title;
    }
    
    @Override
    public Collection<Statement> getUnrecognisedStatements()
    {
        return this.unrecognisedStatements;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.curationStatus == null) ? 0 : this.curationStatus.hashCode());
        result = prime * result + ((this.key == null) ? 0 : this.key.hashCode());
        result = prime * result + ((this.getRuleUris() == null) ? 0 : this.getRuleUris().hashCode());
        result = prime * result + ((this.testInputString == null) ? 0 : this.testInputString.hashCode());
        result = prime * result + ((this.testOutputString == null) ? 0 : this.testOutputString.hashCode());
        return result;
    }
    
    /**
     * @param curationStatus
     *            the curationStatus to set
     */
    @Override
    public void setCurationStatus(final URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    /**
     * @param key
     *            the key to set
     */
    @Override
    public void setKey(final String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }
    
    @Override
    public void setKey(final URI nextKey)
    {
        this.key = nextKey;
    }
    
    /**
     * @param rdfRuleUris
     *            the rdfRuleUris to set
     */
    @Override
    public void setRuleUris(final Collection<URI> rdfRuleUris)
    {
        this.rdfRuleUris = rdfRuleUris;
    }
    
    /**
     * @param stages
     *            the stages to set
     */
    @Override
    public void setStages(final Collection<URI> stages)
    {
        this.stages = stages;
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
    public void setTitle(final String title)
    {
        this.title = title;
    }
    
    @Override
    public String toHtml()
    {
        final StringBuilder sb = new StringBuilder();
        
        final String prefix = "ruletest_";
        
        sb.append("<div class=\"" + prefix + "rulekey\">Rule Key: "
                + StringUtils.xmlEncodeString(this.getKey().stringValue()) + "</div>\n");
        sb.append("<div class=\"" + prefix + "testInputString\">Test Input String: "
                + StringUtils.xmlEncodeString(this.testInputString + "") + "</div>\n");
        sb.append("<div class=\"" + prefix + "testOutputString\">Test Output String: "
                + StringUtils.xmlEncodeString(this.testOutputString + "") + "</div>\n");
        sb.append("<div class=\"" + prefix + "rdfruleuri\">Tests RDF Rules: "
                + StringUtils.xmlEncodeString(this.rdfRuleUris.toString()) + "</div>\n");
        
        return sb.toString();
    }
    
    @Override
    public String toHtmlFormBody()
    {
        final StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        final String prefix = "ruletest_";
        
        return sb.toString();
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            final URI keyUri = this.getKey();
            final Literal testInputStringLiteral = f.createLiteral(this.testInputString);
            final Literal testOutputStringLiteral = f.createLiteral(this.testOutputString);
            
            URI curationStatusLiteral = null;
            
            if(this.curationStatus == null)
            {
                curationStatusLiteral = ProjectSchema.getProjectNotCuratedUri();
            }
            else
            {
                curationStatusLiteral = this.curationStatus;
            }
            
            con.setAutoCommit(false);
            
            con.add(keyUri, RDF.TYPE, RuleTestSchema.getRuletestTypeUri(), keyToUse);
            con.add(keyUri, ProjectSchema.getProjectCurationStatusUri(), curationStatusLiteral, keyToUse);
            con.add(keyUri, RuleTestSchema.getRuletestInputTestString(), testInputStringLiteral, keyToUse);
            con.add(keyUri, RuleTestSchema.getRuletestOutputTestString(), testOutputStringLiteral, keyToUse);
            
            if(this.rdfRuleUris != null)
            {
                for(final URI nextRdfRuleUri : this.rdfRuleUris)
                {
                    con.add(keyUri, RuleTestSchema.getRuletestHasRuleUri(), nextRdfRuleUri, keyToUse);
                }
            }
            
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
            
            RuleTestImpl.log.error("RepositoryException: " + re.getMessage());
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
        
        result += "key=" + this.key + "\n";
        result += "testInputString=" + this.testInputString + "\n";
        result += "testOutputString=" + this.testOutputString + "\n";
        result += "rdfRuleUris=" + this.rdfRuleUris + "\n";
        
        return result;
    }

    public static List<URI> myTypes()
    {
        final List<URI> results = new ArrayList<URI>(1);
        
        results.add(RuleTestSchema.getRuletestTypeUri());
        
        return results;
    }
    
}
