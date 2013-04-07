/**
 * 
 */
package org.queryall.api.project;

import org.kohsuke.MetaInfServices;
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
@MetaInfServices(QueryAllSchema.class)
public class ProjectSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(ProjectSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = ProjectSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = ProjectSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = ProjectSchema.LOG.isInfoEnabled();
    
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
        final ValueFactory f = Constants.VALUE_FACTORY;
        
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
     * A pre-instantiated schema object for ProjectSchema.
     */
    public static final QueryAllSchema PROJECT_SCHEMA = new ProjectSchema();
    
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
    
    /**
     * @param nextProjectAdminCuratedUri
     *            the projectAdminCuratedUri to set
     */
    public static void setProjectAdminCuratedUri(final URI nextProjectAdminCuratedUri)
    {
        ProjectSchema.projectAdminCuratedUri = nextProjectAdminCuratedUri;
    }
    
    /**
     * @param nextProjectAuthority
     *            the projectAuthority to set
     */
    public static void setProjectAuthority(final URI nextProjectAuthority)
    {
        ProjectSchema.projectAuthority = nextProjectAuthority;
    }
    
    /**
     * @param nextProjectCurationStatusUri
     *            the projectCurationStatusUri to set
     */
    public static void setProjectCurationStatusUri(final URI nextProjectCurationStatusUri)
    {
        ProjectSchema.projectCurationStatusUri = nextProjectCurationStatusUri;
    }
    
    /**
     * @param nextProjectDescription
     *            the projectDescription to set
     */
    public static void setProjectDescription(final URI nextProjectDescription)
    {
        ProjectSchema.projectDescription = nextProjectDescription;
    }
    
    /**
     * @param nextProjectNotCuratedUri
     *            the projectNotCuratedUri to set
     */
    public static void setProjectNotCuratedUri(final URI nextProjectNotCuratedUri)
    {
        ProjectSchema.projectNotCuratedUri = nextProjectNotCuratedUri;
    }
    
    /**
     * @param nextProjectTitle
     *            the projectTitle to set
     */
    public static void setProjectTitle(final URI nextProjectTitle)
    {
        ProjectSchema.projectTitle = nextProjectTitle;
    }
    
    /**
     * @param nextProjectTypeUri
     *            the projectTypeUri to set
     */
    public static void setProjectTypeUri(final URI nextProjectTypeUri)
    {
        ProjectSchema.projectTypeUri = nextProjectTypeUri;
    }
    
    /**
     * @param nextProjectUserCuratedUri
     *            the projectUserCuratedUri to set
     */
    public static void setProjectUserCuratedUri(final URI nextProjectUserCuratedUri)
    {
        ProjectSchema.projectUserCuratedUri = nextProjectUserCuratedUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public ProjectSchema()
    {
        this(ProjectSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public ProjectSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contexts)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            con.begin();
            
            con.add(ProjectSchema.getProjectTypeUri(), RDF.TYPE, OWL.CLASS, contexts);
            
            // TODO: Add description
            con.add(ProjectSchema.getProjectTitle(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contexts);
            con.add(ProjectSchema.getProjectTitle(), RDFS.SUBPROPERTYOF, Constants.DC_TITLE, contexts);
            con.add(ProjectSchema.getProjectTitle(), RDFS.SUBPROPERTYOF, RDFS.LABEL, contexts);
            con.add(ProjectSchema.getProjectTitle(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(ProjectSchema.getProjectTitle(), RDFS.DOMAIN, ProjectSchema.getProjectTypeUri(), contexts);
            con.add(ProjectSchema.getProjectTitle(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            // TODO: Add description
            con.add(ProjectSchema.getProjectAuthority(), RDF.TYPE, OWL.OBJECTPROPERTY, contexts);
            con.add(ProjectSchema.getProjectAuthority(), RDFS.RANGE, RDFS.RESOURCE, contexts);
            con.add(ProjectSchema.getProjectAuthority(), RDFS.DOMAIN, ProjectSchema.getProjectTypeUri(), contexts);
            con.add(ProjectSchema.getProjectAuthority(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            // TODO: Add description
            con.add(ProjectSchema.getProjectDescription(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contexts);
            con.add(ProjectSchema.getProjectDescription(), RDFS.SUBPROPERTYOF, RDFS.COMMENT, contexts);
            con.add(ProjectSchema.getProjectDescription(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(ProjectSchema.getProjectDescription(), RDFS.DOMAIN, ProjectSchema.getProjectTypeUri(), contexts);
            con.add(ProjectSchema.getProjectDescription(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            // TODO: Add description
            con.add(ProjectSchema.getProjectCurationStatusUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contexts);
            con.add(ProjectSchema.getProjectCurationStatusUri(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            // TODO: Add description
            con.add(ProjectSchema.getProjectAdminCuratedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contexts);
            con.add(ProjectSchema.getProjectAdminCuratedUri(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            // TODO: Add description
            con.add(ProjectSchema.getProjectUserCuratedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contexts);
            con.add(ProjectSchema.getProjectUserCuratedUri(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            // TODO: Add description
            con.add(ProjectSchema.getProjectNotCuratedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contexts);
            con.add(ProjectSchema.getProjectNotCuratedUri(), RDFS.LABEL, f.createLiteral("."), contexts);
            
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
            
            ProjectSchema.LOG.error("RepositoryException: " + re.getMessage());
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
}
