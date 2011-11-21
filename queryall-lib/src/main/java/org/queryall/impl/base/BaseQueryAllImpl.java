/**
 * 
 */
package org.queryall.impl.base;

import java.util.Collection;
import java.util.HashSet;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDFS;
import org.queryall.api.base.BaseQueryAllInterface;
import org.queryall.api.profile.ProfileSchema;
import org.queryall.api.project.ProjectSchema;
import org.queryall.api.utils.Constants;
import org.queryall.utils.StringUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public abstract class BaseQueryAllImpl implements BaseQueryAllInterface
{
    
    protected Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    private URI key = null;
    private String title = "";
    private URI curationStatus = ProjectSchema.getProjectNotCuratedUri();
    private String description;
    
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
            else if(nextStatement.getPredicate().equals(ProfileSchema.getProfileTitle())
                    || nextStatement.getPredicate().equals(Constants.DC_TITLE))
            {
                this.setTitle(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(nextStatement.getPredicate().equals(RDFS.COMMENT)))
            {
                this.setDescription(nextStatement.getObject().stringValue());
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.queryall.api.base.BaseQueryAllInterface#addUnrecognisedStatement(org.openrdf.model.Statement
     * )
     */
    @Override
    public void addUnrecognisedStatement(final Statement unrecognisedStatement)
    {
        this.unrecognisedStatements.add(unrecognisedStatement);
    }
    
    @Override
    public URI getCurationStatus()
    {
        return this.curationStatus;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#getDescription()
     */
    @Override
    public String getDescription()
    {
        return this.description;
    }
    
    /**
     * @return the key
     */
    @Override
    public URI getKey()
    {
        return this.key;
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
    
    @Override
    public void setCurationStatus(final URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(final String description)
    {
        this.description = description;
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
    
}
