package org.queryall.impl.ruletest;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the RuleTest class
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RuleTestImpl implements RuleTest, HtmlExport
{
    private static final Logger log = LoggerFactory.getLogger(RuleTestImpl.class);
    private static final boolean _TRACE = RuleTestImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = RuleTestImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RuleTestImpl.log.isInfoEnabled();
    
    private static final Set<URI> RULE_TEST_IMPL_TYPES = new HashSet<URI>();
    
    static
    {
        RuleTestImpl.RULE_TEST_IMPL_TYPES.add(RuleTestSchema.getRuletestTypeUri());
    }
    
    public static Set<URI> myTypes()
    {
        return RuleTestImpl.RULE_TEST_IMPL_TYPES;
    }
    
    protected Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key = null;
    
    private Collection<URI> rdfRuleUris = new HashSet<URI>();
    
    private Collection<URI> stages = new HashSet<URI>();
    
    private URI curationStatus = ProjectSchema.getProjectNotCuratedUri();
    
    private String title;
    
    public RuleTestImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
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
        
        if(RuleTestImpl._TRACE)
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
    public Set<URI> getElementTypes()
    {
        return RuleTestImpl.myTypes();
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
        
        try
        {
            final URI keyUri = this.getKey();
            
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
            
            for(URI nextElementType : this.getElementTypes())
            {
                con.add(keyUri, RDF.TYPE, nextElementType, keyToUse);
            }

            con.add(keyUri, ProjectSchema.getProjectCurationStatusUri(), curationStatusLiteral, keyToUse);
            
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
        result += "rdfRuleUris=" + this.rdfRuleUris + "\n";
        
        return result;
    }
    
}
