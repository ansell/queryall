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
import org.openrdf.sail.memory.model.MemValueFactory;

import java.util.HashSet;
import java.util.Collection;

import org.queryall.Project;
import org.queryall.helpers.*;

import org.apache.log4j.Logger;

public class ProjectImpl extends Project
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
        ValueFactory f = new MemValueFactory();
        
        projectNamespace = Settings.getSettings().getOntologyTermUriPrefix() + Settings.getSettings().getNamespaceForProject() + Settings.getSettings().getOntologyTermUriSuffix();
        
        
        setProjectTypeUri(f.createURI(projectNamespace+"Project"));
        setProjectAuthority(f.createURI(projectNamespace+"authority"));
        setProjectTitle(f.createURI(projectNamespace+"title"));
        setProjectDescription(f.createURI(projectNamespace+"description"));
        
        setProjectCurationStatusUri(f.createURI(projectNamespace+"hasCurationStatus"));
        setProjectAdminCuratedUri(f.createURI(projectNamespace+"adminCurated"));
        setProjectUserCuratedUri(f.createURI(projectNamespace+"userCurated"));
        setProjectNotCuratedUri(f.createURI(projectNamespace+"notCurated"));
        
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
    
    public static boolean schemaToRdf(Repository myRepository, String keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            URI contextKeyUri = f.createURI(keyToUse);
            con.setAutoCommit(false);
            
            con.add(getProjectTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            con.add(getProjectTitle(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getProjectTitle(), RDFS.SUBPROPERTYOF, f.createURI(Constants.DC_NAMESPACE+"title"), contextKeyUri);
            con.add(getProjectTitle(), RDFS.SUBPROPERTYOF, f.createURI("http://www.w3.org/2000/01/rdf-schema#label"), contextKeyUri);
            con.add(getProjectAuthority(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProjectDescription(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getProjectDescription(), RDFS.SUBPROPERTYOF, f.createURI("http://www.w3.org/2000/01/rdf-schema#comment"), contextKeyUri);
            con.add(getProjectCurationStatusUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProjectAdminCuratedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProjectUserCuratedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProjectNotCuratedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
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
    

    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            URI projectInstanceUri;
            
            if(_DEBUG)
            {
                log.debug("Project.toRdf: about create instance URI");
                log.debug("Project.toRdf: keyToUse="+keyToUse);
            }
            
            projectInstanceUri = keyToUse;
            
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
            
            con.add(projectInstanceUri, RDF.TYPE, getProjectTypeUri(), projectInstanceUri);
            con.add(projectInstanceUri, getProjectAuthority(), authorityLiteral, projectInstanceUri);
            if(modelVersion == 1)
            {
                con.add(projectInstanceUri, getProjectTitle(), titleLiteral, projectInstanceUri);
            }
            else
            {
                con.add(projectInstanceUri, Constants.DC_TITLE, titleLiteral, projectInstanceUri);
            }
            con.add(projectInstanceUri, getProjectDescription(), descriptionLiteral, projectInstanceUri);
            
            if(unrecognisedStatements != null)
            {
            
                for(Statement nextUnrecognisedStatement : unrecognisedStatements)
                {
                    con.add(nextUnrecognisedStatement);
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
    

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append("key=" + key + "\n");
        sb.append("authority=" + getAuthority() + "\n");
        sb.append("title=" + getTitle() + "\n");
        sb.append("description=" + getDescription() + "\n");
        
        return sb.toString();
    }
    

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
    

    public String toHtml()
    {
        return "";
    }

    
    /**
     * @return the key
     */

    public URI getKey()
    {
        return key;
    }
    
    /**
     * @param key the key to set
     */

    public void setKey(String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }

    public void setKey(URI nextKey)
    {
        this.key = nextKey;
    }    
    /**
     * @return the namespace used to represent objects of this type by default
     */

    public String getDefaultNamespace()
    {
        return defaultNamespace;
    }
    
    /**
     * @return the URI used for the rdf Type of these elements
     */

    public String getElementType()
    {
        return getProjectTypeUri().stringValue();
    }
    
    public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    public URI getCurationStatus()
    {
        return curationStatus;
    }

    public void setAuthority(URI authority)
    {
        this.authority = authority;
    }
    
    public URI getAuthority()
    {
        return authority;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getDescription()
    {
        return description;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getTitle()
    {
        return title;
    }

    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        unrecognisedStatements.add(unrecognisedStatement);
    }

    public Collection<Statement> getUnrecognisedStatements()
    {
        return unrecognisedStatements;
    }

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
