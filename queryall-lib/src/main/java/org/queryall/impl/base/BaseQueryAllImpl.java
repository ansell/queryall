/**
 * 
 */
package org.queryall.impl.base;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.base.BaseQueryAllInterface;
import org.queryall.api.namespace.NamespaceEntrySchema;
import org.queryall.api.profile.ProfileSchema;
import org.queryall.api.project.ProjectSchema;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.querytype.QueryTypeSchema;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.utils.Constants;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public abstract class BaseQueryAllImpl implements BaseQueryAllInterface
{
    private static final Logger log = LoggerFactory.getLogger(BaseQueryAllImpl.class);
    private static final boolean _TRACE = BaseQueryAllImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = BaseQueryAllImpl.log.isDebugEnabled();
    private static final boolean _INFO = BaseQueryAllImpl.log.isInfoEnabled();
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    private URI key = null;
    private String title = "";
    private URI curationStatus = ProjectSchema.getProjectNotCuratedUri();
    private String description = "";
    
    /**
     * 
     */
    protected BaseQueryAllImpl()
    {
        
    }
    
    protected BaseQueryAllImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        for(final Statement nextStatement : inputStatements)
        {
            if(nextStatement.getPredicate().equals(ProjectSchema.getProjectCurationStatusUri()))
            {
                this.setCurationStatus((URI)nextStatement.getObject());
            }
            // Parse these statements for backwards compatibility
            // Preference is now Dublin Core title predicate
            else if(nextStatement.getPredicate().equals(ProfileSchema.getProfileTitle())
                    || nextStatement.getPredicate().equals(QueryTypeSchema.getQueryTitle())
                    || nextStatement.getPredicate().equals(ProviderSchema.getProviderTitle())
                    || nextStatement.getPredicate().equals(ProjectSchema.getProjectTitle())
                    || nextStatement.getPredicate().equals(Constants.DC_TITLE))
            {
                this.setTitle(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(RDFS.COMMENT)
                    || nextStatement.getPredicate().equals(NormalisationRuleSchema.getRdfruleDescription())
                    || nextStatement.getPredicate().equals(NamespaceEntrySchema.getNamespaceDescription())
                    || nextStatement.getPredicate().equals(ProjectSchema.getProjectDescription()))
            {
                this.setDescription(nextStatement.getObject().stringValue());
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
    }
    
    @Override
    public final void addUnrecognisedStatement(final Statement unrecognisedStatement)
    {
        this.unrecognisedStatements.add(unrecognisedStatement);
    }
    
    @Override
    public final URI getCurationStatus()
    {
        return this.curationStatus;
    }
    
    @Override
    public final String getDescription()
    {
        return this.description;
    }
    
    @Override
    public final URI getKey()
    {
        return this.key;
    }
    
    @Override
    public final String getTitle()
    {
        return this.title;
    }
    
    @Override
    public final Collection<Statement> getUnrecognisedStatements()
    {
        return Collections.unmodifiableCollection(this.unrecognisedStatements);
    }
    
    @Override
    public final Collection<Statement> resetUnrecognisedStatements()
    {
        final Collection<Statement> result = new HashSet<Statement>(this.unrecognisedStatements);
        
        this.unrecognisedStatements = new HashSet<Statement>();
        
        return result;
    }
    
    @Override
    public final void setCurationStatus(final URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    @Override
    public final void setDescription(final String nextDescription)
    {
        this.description = nextDescription;
    }
    
    @Override
    public final void setKey(final String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }
    
    @Override
    public final void setKey(final URI nextKey)
    {
        this.key = nextKey;
    }
    
    @Override
    public final void setTitle(final String title)
    {
        this.title = title;
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... contextKey)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            if(BaseQueryAllImpl._DEBUG)
            {
                BaseQueryAllImpl.log.debug("toRdf: contextKey=" + contextKey);
            }
            
            final URI keyUri = this.getKey();
            
            Literal titleLiteral;
            
            if(this.getTitle() == null)
            {
                titleLiteral = f.createLiteral("");
            }
            else
            {
                titleLiteral = f.createLiteral(this.getTitle());
            }
            
            final Literal descriptionLiteral = f.createLiteral(this.getDescription());
            
            URI curationStatusLiteral = null;
            
            if((this.getCurationStatus() == null))
            {
                curationStatusLiteral = ProjectSchema.getProjectNotCuratedUri();
            }
            else
            {
                curationStatusLiteral = this.getCurationStatus();
            }
            
            con.setAutoCommit(false);
            
            con.add(keyUri, ProjectSchema.getProjectCurationStatusUri(), curationStatusLiteral, contextKey);
            
            con.add(keyUri, RDFS.COMMENT, descriptionLiteral, contextKey);
            
            con.add(keyUri, Constants.DC_TITLE, titleLiteral, contextKey);
            
            for(final Statement nextUnrecognisedStatement : this.getUnrecognisedStatements())
            {
                con.add(nextUnrecognisedStatement, contextKey);
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            BaseQueryAllImpl.log.error("RepositoryException: " + re.getMessage());
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
    
}
