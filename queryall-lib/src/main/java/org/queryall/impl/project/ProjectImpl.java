package org.queryall.impl.project;

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
import org.queryall.api.base.HtmlExport;
import org.queryall.api.project.Project;
import org.queryall.api.project.ProjectSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProjectImpl implements Project, HtmlExport
{
    private static final Logger log = LoggerFactory.getLogger(ProjectImpl.class);
    private static final boolean _TRACE = ProjectImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = ProjectImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = ProjectImpl.log.isInfoEnabled();
    
    public static Set<URI> myTypes()
    {
        final Set<URI> results = new HashSet<URI>(1);
        
        results.add(ProjectSchema.getProjectTypeUri());
        
        return results;
    }
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key = null;
    
    private URI authority = null;
    
    private String title = "";
    
    private String description = "";
    
    private URI curationStatus = null;
    
    public ProjectImpl(final Collection<Statement> rdfStatements, final URI subjectKey, final int modelVersion)
        throws OpenRDFException
    {
        for(final Statement nextStatement : rdfStatements)
        {
            if(ProjectImpl._DEBUG)
            {
                ProjectImpl.log.debug("Project: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(ProjectSchema.getProjectTypeUri()))
            {
                if(ProjectImpl._TRACE)
                {
                    ProjectImpl.log.trace("Project: found valid type predicate for URI: " + subjectKey);
                }
                
                this.setKey(subjectKey);
            }
            else if(nextStatement.getPredicate().equals(ProjectSchema.getProjectAuthority()))
            {
                this.setAuthority((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProjectSchema.getProjectTitle())
                    || nextStatement.getPredicate().equals(Constants.DC_TITLE))
            {
                if(this.getTitle().equals(""))
                {
                    this.setTitle(nextStatement.getObject().stringValue());
                }
            }
            else if(nextStatement.getPredicate().equals(ProjectSchema.getProjectDescription()))
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
    public void addUnrecognisedStatement(final Statement unrecognisedStatement)
    {
        this.unrecognisedStatements.add(unrecognisedStatement);
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
    
    @Override
    public URI getAuthority()
    {
        return this.authority;
    }
    
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
        return QueryAllNamespaces.PROJECT;
    }
    
    @Override
    public String getDescription()
    {
        return this.description;
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
    public void setAuthority(final URI authority)
    {
        this.authority = authority;
    }
    
    @Override
    public void setCurationStatus(final URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
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
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... keyToUse)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            final URI projectInstanceUri = this.getKey();
            
            if(ProjectImpl._DEBUG)
            {
                ProjectImpl.log.debug("Project.toRdf: keyToUse=" + keyToUse);
            }
            
            final Literal titleLiteral = f.createLiteral(this.getTitle());
            URI authorityLiteral = null;
            
            if(this.getAuthority() != null)
            {
                authorityLiteral = this.getAuthority();
            }
            
            final Literal descriptionLiteral = f.createLiteral(this.getDescription());
            
            if(ProjectImpl._TRACE)
            {
                ProjectImpl.log.trace("Project.toRdf: about to add URI's to connection");
            }
            
            con.setAutoCommit(false);
            
            for(final URI nextElementType : this.getElementTypes())
            {
                con.add(projectInstanceUri, RDF.TYPE, nextElementType, keyToUse);
            }
            
            if(authorityLiteral != null)
            {
                con.add(projectInstanceUri, ProjectSchema.getProjectAuthority(), authorityLiteral, keyToUse);
            }
            
            if(modelVersion == 1)
            {
                con.add(projectInstanceUri, ProjectSchema.getProjectTitle(), titleLiteral, keyToUse);
            }
            else
            {
                con.add(projectInstanceUri, Constants.DC_TITLE, titleLiteral, keyToUse);
            }
            con.add(projectInstanceUri, ProjectSchema.getProjectDescription(), descriptionLiteral, keyToUse);
            
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
            
            if(con != null)
            {
                con.rollback();
            }
            
            ProjectImpl.log.error("RepositoryException: " + re.getMessage());
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
        
        sb.append("key=" + this.key + "\n");
        sb.append("authority=" + this.getAuthority() + "\n");
        sb.append("title=" + this.getTitle() + "\n");
        sb.append("description=" + this.getDescription() + "\n");
        
        return sb.toString();
    }
    
}
