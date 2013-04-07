package org.queryall.impl.project;

import java.util.Collection;
import java.util.Collections;
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
import org.queryall.api.base.HtmlExport;
import org.queryall.api.project.Project;
import org.queryall.api.project.ProjectSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.impl.base.BaseQueryAllImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProjectImpl extends BaseQueryAllImpl implements Project, HtmlExport
{
    public static Set<URI> myTypes()
    {
        return Collections.singleton(ProjectSchema.getProjectTypeUri());
    }
    
    private URI authority = null;
    
    public ProjectImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = this.resetUnrecognisedStatements();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(this.log.isDebugEnabled())
            {
                this.log.debug("Project: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(ProjectSchema.getProjectTypeUri()))
            {
                if(this.log.isTraceEnabled())
                {
                    this.log.trace("Project: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectSchema.getProjectAuthority()))
            {
                this.setAuthority((URI)nextStatement.getObject());
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
    }
    
    @Override
    public int compareTo(final Project otherProject)
    {
        @SuppressWarnings("unused")
        final int BEFORE = -1;
        final int EQUAL = 0;
        @SuppressWarnings("unused")
        final int AFTER = 1;
        
        if(this == otherProject)
        {
            return EQUAL;
        }
        
        return this.getKey().stringValue().compareTo(otherProject.getKey().stringValue());
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
        if(!(obj instanceof Project))
        {
            return false;
        }
        final Project other = (Project)obj;
        if(this.getAuthority() == null)
        {
            if(other.getAuthority() != null)
            {
                return false;
            }
        }
        else if(!this.getAuthority().equals(other.getAuthority()))
        {
            return false;
        }
        return true;
    }
    
    @Override
    public final URI getAuthority()
    {
        return this.authority;
    }
    
    /**
     * @return the namespace used to represent objects of this type by default
     */
    @Override
    public QueryAllNamespaces getDefaultNamespace()
    {
        return QueryAllNamespaces.PROJECT;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Set<URI> getElementTypes()
    {
        return ProjectImpl.myTypes();
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
        result = prime * result + ((this.authority == null) ? 0 : this.authority.hashCode());
        return result;
    }
    
    @Override
    public final void setAuthority(final URI authority)
    {
        this.authority = authority;
    }
    
    @Override
    public String toHtml()
    {
        return "";
    }
    
    @Override
    public String toHtmlFormBody()
    {
        final StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        final String prefix = "project_";
        
        // sb.append("<div class=\""+prefix+"preferredPrefix_div\"><span class=\""+prefix+"preferredPrefix_span\">Prefix:</span><input type=\"text\" name=\""+prefix+"preferredPrefix\" value=\""+RdfUtils.xmlEncodeString(preferredPrefix)+"\" /></div>\n");
        // sb.append("<div class=\""+prefix+"description_div\"><span class=\""+prefix+"description_span\">Description:</span><input type=\"text\" name=\""+prefix+"description\" value=\""+RdfUtils.xmlEncodeString(description)+"\" /></div>\n");
        // sb.append("<div class=\""+prefix+"identifierRegex_div\"><span class=\""+prefix+"identifierRegex_span\">Namespace identifier regular expression:</span><input type=\"text\" name=\""+prefix+"identifierRegex\" value=\""+RdfUtils.xmlEncodeString(identifierRegex)+"\" /></div>\n");
        
        return sb.toString();
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... contextKey)
        throws OpenRDFException
    {
        super.toRdf(myRepository, modelVersion, contextKey);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            final URI projectInstanceUri = this.getKey();
            
            if(this.log.isDebugEnabled())
            {
                this.log.debug("Project.toRdf: keyToUse=" + contextKey);
            }
            
            Literal titleLiteral;
            
            if(this.getTitle() == null)
            {
                titleLiteral = f.createLiteral("");
            }
            else
            {
                titleLiteral = f.createLiteral(this.getTitle());
            }
            
            URI authorityLiteral = null;
            
            if(this.getAuthority() != null)
            {
                authorityLiteral = this.getAuthority();
            }
            
            final Literal descriptionLiteral = f.createLiteral(this.getDescription());
            
            if(this.log.isTraceEnabled())
            {
                this.log.trace("Project.toRdf: about to add URI's to connection");
            }
            
            con.begin();
            
            for(final URI nextElementType : this.getElementTypes())
            {
                con.add(projectInstanceUri, RDF.TYPE, nextElementType, contextKey);
            }
            
            if(authorityLiteral != null)
            {
                con.add(projectInstanceUri, ProjectSchema.getProjectAuthority(), authorityLiteral, contextKey);
            }
            
            if(modelVersion == 1)
            {
                con.add(projectInstanceUri, ProjectSchema.getProjectTitle(), titleLiteral, contextKey);
                con.add(projectInstanceUri, ProjectSchema.getProjectDescription(), descriptionLiteral, contextKey);
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
            {
                con.rollback();
            }
            
            this.log.error("RepositoryException: " + re.getMessage());
        }
        finally
        {
            if(con != null)
            {
                con.close();
            }
        }
        
        return false;
    }
    
    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        
        sb.append("key=" + this.getKey() + "\n");
        sb.append("authority=" + this.getAuthority() + "\n");
        sb.append("title=" + this.getTitle() + "\n");
        sb.append("description=" + this.getDescription() + "\n");
        
        return sb.toString();
    }
    
}
