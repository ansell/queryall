/**
 * 
 */
package org.queryall.api.project;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.base.QueryAllSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProjectSchema implements QueryAllSchema
{
    private static final Logger log = LoggerFactory.getLogger(ProjectSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = ProjectSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = ProjectSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = ProjectSchema.log.isInfoEnabled();
    
    private static URI projectTypeUri;
    
    private static URI projectAuthority;
    
    private static URI projectTitle;
    
    private static URI projectDescription;
    
    private static URI projectCurationStatusUri;
    
    private static URI projectAdminCuratedUri;
    
    private static URI projectUserCuratedUri;
    
    private static URI projectNotCuratedUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.PROJECT.getBaseURI();
        
        ProjectSchema.setProjectTypeUri(f.createURI(baseUri, "Project"));
        ProjectSchema.setProjectAuthority(f.createURI(baseUri, "authority"));
        ProjectSchema.setProjectTitle(f.createURI(baseUri, "title"));
        ProjectSchema.setProjectDescription(f.createURI(baseUri, "description"));
        
        ProjectSchema.setProjectCurationStatusUri(f.createURI(baseUri, "hasCurationStatus"));
        ProjectSchema.setProjectAdminCuratedUri(f.createURI(baseUri, "adminCurated"));
        ProjectSchema.setProjectUserCuratedUri(f.createURI(baseUri, "userCurated"));
        ProjectSchema.setProjectNotCuratedUri(f.createURI(baseUri, "notCurated"));
        
    }
    
    /**
     * @return the projectAdminCuratedUri
     */
    public static URI getProjectAdminCuratedUri()
    {
        return ProjectSchema.projectAdminCuratedUri;
    }
    
    /**
     * @return the projectAuthority
     */
    public static URI getProjectAuthority()
    {
        return ProjectSchema.projectAuthority;
    }
    
    /**
     * @return the projectCurationStatusUri
     */
    public static URI getProjectCurationStatusUri()
    {
        return ProjectSchema.projectCurationStatusUri;
    }
    
    /**
     * @return the projectDescription
     */
    public static URI getProjectDescription()
    {
        return ProjectSchema.projectDescription;
    }
    
    /**
     * @return the projectNotCuratedUri
     */
    public static URI getProjectNotCuratedUri()
    {
        return ProjectSchema.projectNotCuratedUri;
    }
    
    /**
     * @return the projectTitle
     */
    public static URI getProjectTitle()
    {
        return ProjectSchema.projectTitle;
    }
    
    /**
     * @return the projectTypeUri
     */
    public static URI getProjectTypeUri()
    {
        return ProjectSchema.projectTypeUri;
    }
    
    /**
     * @return the projectUserCuratedUri
     */
    public static URI getProjectUserCuratedUri()
    {
        return ProjectSchema.projectUserCuratedUri;
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
            
            con.add(ProjectSchema.getProjectTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            
            // TODO: Add description
            con.add(ProjectSchema.getProjectTitle(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contextKeyUri);
            con.add(ProjectSchema.getProjectTitle(), RDFS.SUBPROPERTYOF, Constants.DC_TITLE, contextKeyUri);
            con.add(ProjectSchema.getProjectTitle(), RDFS.SUBPROPERTYOF, RDFS.LABEL, contextKeyUri);
            con.add(ProjectSchema.getProjectTitle(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(ProjectSchema.getProjectTitle(), RDFS.DOMAIN, ProjectSchema.getProjectTypeUri(), contextKeyUri);
            con.add(ProjectSchema.getProjectTitle(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(ProjectSchema.getProjectAuthority(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(ProjectSchema.getProjectAuthority(), RDFS.RANGE, RDFS.RESOURCE, contextKeyUri);
            con.add(ProjectSchema.getProjectAuthority(), RDFS.DOMAIN, ProjectSchema.getProjectTypeUri(), contextKeyUri);
            con.add(ProjectSchema.getProjectAuthority(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(ProjectSchema.getProjectDescription(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contextKeyUri);
            con.add(ProjectSchema.getProjectDescription(), RDFS.SUBPROPERTYOF, RDFS.COMMENT, contextKeyUri);
            con.add(ProjectSchema.getProjectDescription(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(ProjectSchema.getProjectDescription(), RDFS.DOMAIN, ProjectSchema.getProjectTypeUri(),
                    contextKeyUri);
            con.add(ProjectSchema.getProjectDescription(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(ProjectSchema.getProjectCurationStatusUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(ProjectSchema.getProjectCurationStatusUri(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(ProjectSchema.getProjectAdminCuratedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(ProjectSchema.getProjectAdminCuratedUri(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(ProjectSchema.getProjectUserCuratedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(ProjectSchema.getProjectUserCuratedUri(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(ProjectSchema.getProjectNotCuratedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(ProjectSchema.getProjectNotCuratedUri(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
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
            
            ProjectSchema.log.error("RepositoryException: " + re.getMessage());
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
    
    /**
     * @param projectAdminCuratedUri
     *            the projectAdminCuratedUri to set
     */
    public static void setProjectAdminCuratedUri(final URI projectAdminCuratedUri)
    {
        ProjectSchema.projectAdminCuratedUri = projectAdminCuratedUri;
    }
    
    /**
     * @param projectAuthority
     *            the projectAuthority to set
     */
    public static void setProjectAuthority(final URI projectAuthority)
    {
        ProjectSchema.projectAuthority = projectAuthority;
    }
    
    /**
     * @param projectCurationStatusUri
     *            the projectCurationStatusUri to set
     */
    public static void setProjectCurationStatusUri(final URI projectCurationStatusUri)
    {
        ProjectSchema.projectCurationStatusUri = projectCurationStatusUri;
    }
    
    /**
     * @param projectDescription
     *            the projectDescription to set
     */
    public static void setProjectDescription(final URI projectDescription)
    {
        ProjectSchema.projectDescription = projectDescription;
    }
    
    /**
     * @param projectNotCuratedUri
     *            the projectNotCuratedUri to set
     */
    public static void setProjectNotCuratedUri(final URI projectNotCuratedUri)
    {
        ProjectSchema.projectNotCuratedUri = projectNotCuratedUri;
    }
    
    /**
     * @param projectTitle
     *            the projectTitle to set
     */
    public static void setProjectTitle(final URI projectTitle)
    {
        ProjectSchema.projectTitle = projectTitle;
    }
    
    /**
     * @param projectTypeUri
     *            the projectTypeUri to set
     */
    public static void setProjectTypeUri(final URI projectTypeUri)
    {
        ProjectSchema.projectTypeUri = projectTypeUri;
    }
    
    /**
     * @param projectUserCuratedUri
     *            the projectUserCuratedUri to set
     */
    public static void setProjectUserCuratedUri(final URI projectUserCuratedUri)
    {
        ProjectSchema.projectUserCuratedUri = projectUserCuratedUri;
    }
}
