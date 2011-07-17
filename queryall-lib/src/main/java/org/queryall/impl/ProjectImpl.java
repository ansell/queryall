package org.queryall.impl;

import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;

import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryConnection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collection;

import org.queryall.api.Project;
import org.queryall.helpers.*;

import org.apache.log4j.Logger;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProjectImpl implements Project
{
    private static final Logger log = Logger.getLogger(Project.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.getSettings().getNamespaceForProject();
    
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
        
        projectNamespace = Settings.getSettings().getOntologyTermUriPrefix() + Settings.getSettings().getNamespaceForProject() + Settings.getSettings().getOntologyTermUriSuffix();
        
        
        setProjectTypeUri(f.createURI(projectNamespace,"Project"));
        setProjectAuthority(f.createURI(projectNamespace,"authority"));
        setProjectTitle(f.createURI(projectNamespace,"title"));
        setProjectDescription(f.createURI(projectNamespace,"description"));
        
        setProjectCurationStatusUri(f.createURI(projectNamespace,"hasCurationStatus"));
        setProjectAdminCuratedUri(f.createURI(projectNamespace,"adminCurated"));
        setProjectUserCuratedUri(f.createURI(projectNamespace,"userCurated"));
        setProjectNotCuratedUri(f.createURI(projectNamespace,"notCurated"));
        
    }
    
    // keyToUse is the URI of the next instance that can be found in myRepository
    // returns null if the URI is not in the repository or the information is not enough to create a minimal provider configuration
    public static Project fromRdf(Collection<Statement> inputStatements, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        Project result = new ProjectImpl();
        
        boolean resultIsValid = false;
        
        for(Statement nextStatement : inputStatements)
        {
            if(_DEBUG)
            {
                log.debug("Project: nextStatement: "+nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE) && nextStatement.getObject().equals(getProjectTypeUri()))
            {
                if(_TRACE)
                {
                    log.trace("Project: found valid type predicate for URI: "+keyToUse);
                }
                
                resultIsValid = true;
                result.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(getProjectAuthority()))
            {
                result.setAuthority((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProjectTitle()) || nextStatement.getPredicate().equals(Constants.DC_TITLE))
            {
                if(result.getTitle().equals(""))
                    result.setTitle(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getProjectDescription()))
            {
                result.setDescription(nextStatement.getObject().stringValue());
            }
            else
            {
                result.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(_TRACE)
        {
            log.trace("Project.fromRdf: would have returned... result="+result.toString());
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
    
    public static boolean schemaToRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            URI contextKeyUri = keyToUse;
            con.setAutoCommit(false);
            
            con.add(getProjectTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);

            // TODO: Add description
            con.add(getProjectTitle(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contextKeyUri);
            con.add(getProjectTitle(), RDFS.SUBPROPERTYOF, Constants.DC_TITLE, contextKeyUri);
            con.add(getProjectTitle(), RDFS.SUBPROPERTYOF, RDFS.LABEL, contextKeyUri);
            con.add(getProjectTitle(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getProjectTitle(), RDFS.DOMAIN, getProjectTypeUri(), contextKeyUri);
            con.add(getProjectTitle(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(getProjectAuthority(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProjectAuthority(), RDFS.RANGE, RDFS.RESOURCE, contextKeyUri);
            con.add(getProjectAuthority(), RDFS.DOMAIN, getProjectTypeUri(), contextKeyUri);
            con.add(getProjectAuthority(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);

            // TODO: Add description
            con.add(getProjectDescription(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contextKeyUri);
            con.add(getProjectDescription(), RDFS.SUBPROPERTYOF, RDFS.COMMENT, contextKeyUri);
            con.add(getProjectDescription(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getProjectDescription(), RDFS.DOMAIN, getProjectTypeUri(), contextKeyUri);
            con.add(getProjectDescription(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);

            // TODO: Add description
            con.add(getProjectCurationStatusUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProjectCurationStatusUri(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(getProjectAdminCuratedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProjectAdminCuratedUri(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(getProjectUserCuratedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProjectUserCuratedUri(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);

            // TODO: Add description
            con.add(getProjectNotCuratedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProjectNotCuratedUri(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch (RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
                con.rollback();
                
            log.error("RepositoryException: "+re.getMessage());
        }
        finally
        {
            if(con != null)
                con.close();
        }
        
        return false;
    }
    
    @Override
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            URI projectInstanceUri = this.getKey();
            
            if(_DEBUG)
            {
                log.debug("Project.toRdf: keyToUse="+keyToUse);
            }
            
            Literal titleLiteral = f.createLiteral(getTitle());
            URI authorityLiteral = null;
            
            if(getAuthority() == null)
                authorityLiteral = f.createURI(Settings.getSettings().getDefaultHostAddress());
            else
                authorityLiteral = getAuthority();
                
            Literal descriptionLiteral = f.createLiteral(getDescription());
            
            if(_TRACE)
            {
                log.trace("Project.toRdf: about to add URI's to connection");
            }
            
            con.setAutoCommit(false);
            
            con.add(projectInstanceUri, RDF.TYPE, getProjectTypeUri(), keyToUse);
            con.add(projectInstanceUri, getProjectAuthority(), authorityLiteral, keyToUse);
            if(modelVersion == 1)
            {
                con.add(projectInstanceUri, getProjectTitle(), titleLiteral, keyToUse);
            }
            else
            {
                con.add(projectInstanceUri, Constants.DC_TITLE, titleLiteral, keyToUse);
            }
            con.add(projectInstanceUri, getProjectDescription(), descriptionLiteral, keyToUse);
            
            if(unrecognisedStatements != null)
            {
            
                for(Statement nextUnrecognisedStatement : unrecognisedStatements)
                {
                    con.add(nextUnrecognisedStatement, keyToUse);
                }
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch (RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
                con.rollback();
                
            log.error("RepositoryException: "+re.getMessage());
        }
        finally
        {
            if(con != null)
                con.close();
        }
        
        return false;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append("key=" + key + "\n");
        sb.append("authority=" + getAuthority() + "\n");
        sb.append("title=" + getTitle() + "\n");
        sb.append("description=" + getDescription() + "\n");
        
        return sb.toString();
    }
    
    @Override
    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        String prefix = "project_";
        
        // sb.append("<div class=\""+prefix+"preferredPrefix_div\"><span class=\""+prefix+"preferredPrefix_span\">Prefix:</span><input type=\"text\" name=\""+prefix+"preferredPrefix\" value=\""+RdfUtils.xmlEncodeString(preferredPrefix)+"\" /></div>\n");
        // sb.append("<div class=\""+prefix+"description_div\"><span class=\""+prefix+"description_span\">Description:</span><input type=\"text\" name=\""+prefix+"description\" value=\""+RdfUtils.xmlEncodeString(description)+"\" /></div>\n");
        // sb.append("<div class=\""+prefix+"identifierRegex_div\"><span class=\""+prefix+"identifierRegex_span\">Namespace identifier regular expression:</span><input type=\"text\" name=\""+prefix+"identifierRegex\" value=\""+RdfUtils.xmlEncodeString(identifierRegex)+"\" /></div>\n");
        
        return sb.toString();
    }
    
    @Override
    public String toHtml()
    {
        return "";
    }

    
    /**
     * @return the key
     */
    @Override
    public URI getKey()
    {
        return key;
    }
    
    /**
     * @param key the key to set
     */
    @Override
    public void setKey(String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }

    @Override
    public void setKey(URI nextKey)
    {
        this.key = nextKey;
    }    

    /**
     * @return the namespace used to represent objects of this type by default
     */
    @Override
    public String getDefaultNamespace()
    {
        return defaultNamespace;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class, including abstract implementations
     */
    @Override
    public Collection<URI> getElementTypes()
    {
        Collection<URI> results = new ArrayList<URI>(1);
    	
    	results.add(getProjectTypeUri());
    	
    	return results;
    }
    
    @Override
    public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    @Override
    public URI getCurationStatus()
    {
        return curationStatus;
    }

    @Override
    public void setAuthority(URI authority)
    {
        this.authority = authority;
    }
    
    @Override
    public URI getAuthority()
    {
        return authority;
    }

    @Override
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    @Override
    public String getTitle()
    {
        return title;
    }

    @Override
    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        unrecognisedStatements.add(unrecognisedStatement);
    }

    @Override
    public Collection<Statement> getUnrecognisedStatements()
    {
        return unrecognisedStatements;
    }

    @Override
    public int compareTo(Project otherProject)
    {
        @SuppressWarnings("unused")
        final int BEFORE = -1;
        final int EQUAL = 0;
        @SuppressWarnings("unused")
        final int AFTER = 1;
    
        if ( this == otherProject ) 
            return EQUAL;

        return this.getKey().stringValue().compareTo(otherProject.getKey().stringValue());
    }

	/**
	 * @param projectNotCuratedUri the projectNotCuratedUri to set
	 */
	public static void setProjectNotCuratedUri(URI projectNotCuratedUri) {
		ProjectImpl.projectNotCuratedUri = projectNotCuratedUri;
	}

	/**
	 * @return the projectNotCuratedUri
	 */
	public static URI getProjectNotCuratedUri() {
		return projectNotCuratedUri;
	}

	/**
	 * @param projectTypeUri the projectTypeUri to set
	 */
	public static void setProjectTypeUri(URI projectTypeUri) {
		ProjectImpl.projectTypeUri = projectTypeUri;
	}

	/**
	 * @return the projectTypeUri
	 */
	public static URI getProjectTypeUri() {
		return projectTypeUri;
	}

	/**
	 * @param projectAuthority the projectAuthority to set
	 */
	public static void setProjectAuthority(URI projectAuthority) {
		ProjectImpl.projectAuthority = projectAuthority;
	}

	/**
	 * @return the projectAuthority
	 */
	public static URI getProjectAuthority() {
		return projectAuthority;
	}

	/**
	 * @param projectTitle the projectTitle to set
	 */
	public static void setProjectTitle(URI projectTitle) {
		ProjectImpl.projectTitle = projectTitle;
	}

	/**
	 * @return the projectTitle
	 */
	public static URI getProjectTitle() {
		return projectTitle;
	}

	/**
	 * @param projectDescription the projectDescription to set
	 */
	public static void setProjectDescription(URI projectDescription) {
		ProjectImpl.projectDescription = projectDescription;
	}

	/**
	 * @return the projectDescription
	 */
	public static URI getProjectDescription() {
		return projectDescription;
	}

	/**
	 * @param projectCurationStatusUri the projectCurationStatusUri to set
	 */
	public static void setProjectCurationStatusUri(
			URI projectCurationStatusUri) {
		ProjectImpl.projectCurationStatusUri = projectCurationStatusUri;
	}

	/**
	 * @return the projectCurationStatusUri
	 */
	public static URI getProjectCurationStatusUri() {
		return projectCurationStatusUri;
	}

	/**
	 * @param projectAdminCuratedUri the projectAdminCuratedUri to set
	 */
	public static void setProjectAdminCuratedUri(URI projectAdminCuratedUri) {
		ProjectImpl.projectAdminCuratedUri = projectAdminCuratedUri;
	}

	/**
	 * @return the projectAdminCuratedUri
	 */
	public static URI getProjectAdminCuratedUri() {
		return projectAdminCuratedUri;
	}

	/**
	 * @param projectUserCuratedUri the projectUserCuratedUri to set
	 */
	public static void setProjectUserCuratedUri(URI projectUserCuratedUri) {
		ProjectImpl.projectUserCuratedUri = projectUserCuratedUri;
	}

	/**
	 * @return the projectUserCuratedUri
	 */
	public static URI getProjectUserCuratedUri() {
		return projectUserCuratedUri;
	}
    
}
