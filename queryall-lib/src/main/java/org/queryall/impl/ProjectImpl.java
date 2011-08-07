package org.queryall.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.Project;
import org.queryall.enumerations.Constants;
import org.queryall.query.Settings;
import org.queryall.utils.StringUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProjectImpl implements Project
{
    private static final Logger log = Logger.getLogger(Project.class.getName());
    private static final boolean _TRACE = ProjectImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = ProjectImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = ProjectImpl.log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.getSettings().getNamespaceForProject();
    
    /**
     * @return the projectAdminCuratedUri
     */
    public static URI getProjectAdminCuratedUri()
    {
        return ProjectImpl.projectAdminCuratedUri;
    }
    
    /**
     * @return the projectAuthority
     */
    public static URI getProjectAuthority()
    {
        return ProjectImpl.projectAuthority;
    }
    
    /**
     * @return the projectCurationStatusUri
     */
    public static URI getProjectCurationStatusUri()
    {
        return ProjectImpl.projectCurationStatusUri;
    }
    
    /**
     * @return the projectDescription
     */
    public static URI getProjectDescription()
    {
        return ProjectImpl.projectDescription;
    }
    
    /**
     * @return the projectNotCuratedUri
     */
    public static URI getProjectNotCuratedUri()
    {
        return ProjectImpl.projectNotCuratedUri;
    }
    
    /**
     * @return the projectTitle
     */
    public static URI getProjectTitle()
    {
        return ProjectImpl.projectTitle;
    }
    
    /**
     * @return the projectTypeUri
     */
    public static URI getProjectTypeUri()
    {
        return ProjectImpl.projectTypeUri;
    }
    
    /**
     * @return the projectUserCuratedUri
     */
    public static URI getProjectUserCuratedUri()
    {
        return ProjectImpl.projectUserCuratedUri;
    }
    
    /**
     * @param projectAdminCuratedUri
     *            the projectAdminCuratedUri to set
     */
    public static void setProjectAdminCuratedUri(final URI projectAdminCuratedUri)
    {
        ProjectImpl.projectAdminCuratedUri = projectAdminCuratedUri;
    }
    
    /**
     * @param projectAuthority
     *            the projectAuthority to set
     */
    public static void setProjectAuthority(final URI projectAuthority)
    {
        ProjectImpl.projectAuthority = projectAuthority;
    }
    
    /**
     * @param projectCurationStatusUri
     *            the projectCurationStatusUri to set
     */
    public static void setProjectCurationStatusUri(final URI projectCurationStatusUri)
    {
        ProjectImpl.projectCurationStatusUri = projectCurationStatusUri;
    }
    
    /**
     * @param projectDescription
     *            the projectDescription to set
     */
    public static void setProjectDescription(final URI projectDescription)
    {
        ProjectImpl.projectDescription = projectDescription;
    }
    
    /**
     * @param projectNotCuratedUri
     *            the projectNotCuratedUri to set
     */
    public static void setProjectNotCuratedUri(final URI projectNotCuratedUri)
    {
        ProjectImpl.projectNotCuratedUri = projectNotCuratedUri;
    }
    
    /**
     * @param projectTitle
     *            the projectTitle to set
     */
    public static void setProjectTitle(final URI projectTitle)
    {
        ProjectImpl.projectTitle = projectTitle;
    }
    
    /**
     * @param projectTypeUri
     *            the projectTypeUri to set
     */
    public static void setProjectTypeUri(final URI projectTypeUri)
    {
        ProjectImpl.projectTypeUri = projectTypeUri;
    }
    
    /**
     * @param projectUserCuratedUri
     *            the projectUserCuratedUri to set
     */
    public static void setProjectUserCuratedUri(final URI projectUserCuratedUri)
    {
        ProjectImpl.projectUserCuratedUri = projectUserCuratedUri;
    }
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key = null;
    
    private URI authority = null;
    
    private String title = "";
    
    private String description = "";
    
    private URI curationStatus = null;
    
    private static URI projectTypeUri;
    
    private static URI projectAuthority;
    
    private static URI projectTitle;
    
    private static URI projectDescription;
    
    private static URI projectCurationStatusUri;
    
    private static URI projectAdminCuratedUri;
    
    private static URI projectUserCuratedUri;
    
    private static URI projectNotCuratedUri;
    
    public static String projectNamespace;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        ProjectImpl.projectNamespace =
                Settings.getSettings().getOntologyTermUriPrefix() + Settings.getSettings().getNamespaceForProject()
                        + Settings.getSettings().getOntologyTermUriSuffix();
        
        ProjectImpl.setProjectTypeUri(f.createURI(ProjectImpl.projectNamespace, "Project"));
        ProjectImpl.setProjectAuthority(f.createURI(ProjectImpl.projectNamespace, "authority"));
        ProjectImpl.setProjectTitle(f.createURI(ProjectImpl.projectNamespace, "title"));
        ProjectImpl.setProjectDescription(f.createURI(ProjectImpl.projectNamespace, "description"));
        
        ProjectImpl.setProjectCurationStatusUri(f.createURI(ProjectImpl.projectNamespace, "hasCurationStatus"));
        ProjectImpl.setProjectAdminCuratedUri(f.createURI(ProjectImpl.projectNamespace, "adminCurated"));
        ProjectImpl.setProjectUserCuratedUri(f.createURI(ProjectImpl.projectNamespace, "userCurated"));
        ProjectImpl.setProjectNotCuratedUri(f.createURI(ProjectImpl.projectNamespace, "notCurated"));
        
    }
    
    // keyToUse is the URI of the next instance that can be found in myRepository
    // returns null if the URI is not in the repository or the information is not enough to create a
    // minimal provider configuration
    public static Project fromRdf(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        final Project result = new ProjectImpl();
        
        boolean resultIsValid = false;
        
        for(final Statement nextStatement : inputStatements)
        {
            if(ProjectImpl._DEBUG)
            {
                ProjectImpl.log.debug("Project: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(ProjectImpl.getProjectTypeUri()))
            {
                if(ProjectImpl._TRACE)
                {
                    ProjectImpl.log.trace("Project: found valid type predicate for URI: " + keyToUse);
                }
                
                resultIsValid = true;
                result.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.getProjectAuthority()))
            {
                result.setAuthority((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.getProjectTitle())
                    || nextStatement.getPredicate().equals(Constants.DC_TITLE))
            {
                if(result.getTitle().equals(""))
                {
                    result.setTitle(nextStatement.getObject().stringValue());
                }
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.getProjectDescription()))
            {
                result.setDescription(nextStatement.getObject().stringValue());
            }
            else
            {
                result.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(ProjectImpl._TRACE)
        {
            ProjectImpl.log.trace("Project.fromRdf: would have returned... result=" + result.toString());
        }
        
        if(resultIsValid)
        {
            return result;
        }
        else
        {
            throw new RuntimeException("Project.fromRdf: result was not valid");
        }
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            final URI contextKeyUri = keyToUse;
            con.setAutoCommit(false);
            
            con.add(ProjectImpl.getProjectTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            
            // TODO: Add description
            con.add(ProjectImpl.getProjectTitle(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contextKeyUri);
            con.add(ProjectImpl.getProjectTitle(), RDFS.SUBPROPERTYOF, Constants.DC_TITLE, contextKeyUri);
            con.add(ProjectImpl.getProjectTitle(), RDFS.SUBPROPERTYOF, RDFS.LABEL, contextKeyUri);
            con.add(ProjectImpl.getProjectTitle(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(ProjectImpl.getProjectTitle(), RDFS.DOMAIN, ProjectImpl.getProjectTypeUri(), contextKeyUri);
            con.add(ProjectImpl.getProjectTitle(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(ProjectImpl.getProjectAuthority(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(ProjectImpl.getProjectAuthority(), RDFS.RANGE, RDFS.RESOURCE, contextKeyUri);
            con.add(ProjectImpl.getProjectAuthority(), RDFS.DOMAIN, ProjectImpl.getProjectTypeUri(), contextKeyUri);
            con.add(ProjectImpl.getProjectAuthority(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(ProjectImpl.getProjectDescription(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contextKeyUri);
            con.add(ProjectImpl.getProjectDescription(), RDFS.SUBPROPERTYOF, RDFS.COMMENT, contextKeyUri);
            con.add(ProjectImpl.getProjectDescription(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(ProjectImpl.getProjectDescription(), RDFS.DOMAIN, ProjectImpl.getProjectTypeUri(), contextKeyUri);
            con.add(ProjectImpl.getProjectDescription(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(ProjectImpl.getProjectCurationStatusUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(ProjectImpl.getProjectCurationStatusUri(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(ProjectImpl.getProjectAdminCuratedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(ProjectImpl.getProjectAdminCuratedUri(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(ProjectImpl.getProjectUserCuratedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(ProjectImpl.getProjectUserCuratedUri(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(ProjectImpl.getProjectNotCuratedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(ProjectImpl.getProjectNotCuratedUri(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
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
    public String getDefaultNamespace()
    {
        return ProjectImpl.defaultNamespace;
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
    public Collection<URI> getElementTypes()
    {
        final Collection<URI> results = new ArrayList<URI>(1);
        
        results.add(ProjectImpl.getProjectTypeUri());
        
        return results;
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
    public boolean toRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
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
            
            if(this.getAuthority() == null)
            {
                authorityLiteral = f.createURI(Settings.getSettings().getDefaultHostAddress());
            }
            else
            {
                authorityLiteral = this.getAuthority();
            }
            
            final Literal descriptionLiteral = f.createLiteral(this.getDescription());
            
            if(ProjectImpl._TRACE)
            {
                ProjectImpl.log.trace("Project.toRdf: about to add URI's to connection");
            }
            
            con.setAutoCommit(false);
            
            con.add(projectInstanceUri, RDF.TYPE, ProjectImpl.getProjectTypeUri(), keyToUse);
            con.add(projectInstanceUri, ProjectImpl.getProjectAuthority(), authorityLiteral, keyToUse);
            if(modelVersion == 1)
            {
                con.add(projectInstanceUri, ProjectImpl.getProjectTitle(), titleLiteral, keyToUse);
            }
            else
            {
                con.add(projectInstanceUri, Constants.DC_TITLE, titleLiteral, keyToUse);
            }
            con.add(projectInstanceUri, ProjectImpl.getProjectDescription(), descriptionLiteral, keyToUse);
            
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
