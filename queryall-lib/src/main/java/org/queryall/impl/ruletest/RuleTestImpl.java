package org.queryall.impl.ruletest;

import java.util.ArrayList;
import java.util.Collection;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.base.HtmlExport;
import org.queryall.api.project.ProjectSchema;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.ruletest.RuleTestSchema;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.impl.base.BaseQueryAllImpl;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the RuleTest class
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class RuleTestImpl extends BaseQueryAllImpl implements RuleTest, HtmlExport
{
    private static final Logger log = LoggerFactory.getLogger(RuleTestImpl.class);
    private static final boolean TRACE = RuleTestImpl.log.isTraceEnabled();
    private static final boolean DEBUG = RuleTestImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = RuleTestImpl.log.isInfoEnabled();
    
    private Collection<URI> rdfRuleUris = new ArrayList<URI>();
    
    private Collection<URI> stages = new ArrayList<URI>();
    
    public RuleTestImpl()
    {
        // TODO Auto-generated constructor stub
    }
    
    public RuleTestImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = this.resetUnrecognisedStatements();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(RuleTestImpl.TRACE)
            {
                RuleTestImpl.log.trace("RuleTest: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(RuleTestSchema.getRuletestTypeUri()))
            {
                if(RuleTestImpl.TRACE)
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
                this.addRuleUri((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(RuleTestSchema.getRuletestTestsStage()))
            {
                this.addStage((URI)nextStatement.getObject());
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(RuleTestImpl.TRACE)
        {
            RuleTestImpl.log.trace("RuleTest.fromRdf: would have returned... result=" + this.toString());
        }
    }
    
    /**
     * @param rdfRuleUri
     *            the rdfRuleUris to set
     */
    @Override
    public void addRuleUri(final URI rdfRuleUri)
    {
        this.rdfRuleUris.add(rdfRuleUri);
    }
    
    /**
     * @param stage
     *            the stages to set
     */
    @Override
    public void addStage(final URI stage)
    {
        this.stages.add(stage);
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
        if(!super.equals(obj))
        {
            return false;
        }
        if(!(obj instanceof RuleTest))
        {
            return false;
        }
        final RuleTest other = (RuleTest)obj;
        if(this.getRuleUris() == null)
        {
            if(other.getRuleUris() != null)
            {
                return false;
            }
        }
        else if(!this.getRuleUris().equals(other.getRuleUris()))
        {
            return false;
        }
        if(this.getStages() == null)
        {
            if(other.getStages() != null)
            {
                return false;
            }
        }
        else if(!this.getStages().equals(other.getStages()))
        {
            return false;
        }
        return true;
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
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.rdfRuleUris == null) ? 0 : this.rdfRuleUris.hashCode());
        result = prime * result + ((this.stages == null) ? 0 : this.stages.hashCode());
        return result;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.ruletest.RuleTest#resetRuleUris()
     */
    @Override
    public boolean resetRuleUris()
    {
        try
        {
            this.rdfRuleUris.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            RuleTestImpl.log.debug("Could not clear collection");
        }
        
        this.rdfRuleUris = new ArrayList<URI>();
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.ruletest.RuleTest#resetStages()
     */
    @Override
    public boolean resetStages()
    {
        try
        {
            this.stages.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            RuleTestImpl.log.debug("Could not clear collection");
        }
        
        this.stages = new ArrayList<URI>();
        
        return true;
    }
    
    @Override
    public String toHtml()
    {
        final StringBuilder sb = new StringBuilder();
        
        final String prefix = "ruletest_";
        
        sb.append("<div class=\"" + prefix + "rulekey\">Rule Key: "
                + StringUtils.xmlEncodeString(this.getKey().stringValue()) + "</div>\n");
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
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... contextKey)
        throws OpenRDFException
    {
        super.toRdf(myRepository, modelVersion, contextKey);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        try
        {
            final URI keyUri = this.getKey();
            
            con.setAutoCommit(false);
            
            for(final URI nextElementType : this.getElementTypes())
            {
                con.add(keyUri, RDF.TYPE, nextElementType, contextKey);
            }
            
            if(this.rdfRuleUris != null)
            {
                for(final URI nextRdfRuleUri : this.rdfRuleUris)
                {
                    con.add(keyUri, RuleTestSchema.getRuletestHasRuleUri(), nextRdfRuleUri, contextKey);
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
        
        result += "key=" + this.getKey() + "\n";
        result += "rdfRuleUris=" + this.rdfRuleUris + "\n";
        
        return result;
    }
    
}
