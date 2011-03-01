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
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.memory.model.MemValueFactory;

import java.util.List;
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
    private static final boolean _INFO = log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.DEFAULT_RDF_PROJECT_NAMESPACE;
    
    public Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key;
    public URI authority = null;
    public String title = "";
    public String description = "";
    public URI curationStatus;
    
    public static URI projectTypeUri;
    public static URI projectAuthority;
    public static URI projectTitle;
    public static URI projectDescription;
    
    public static URI projectCurationStatusUri;
    public static URI projectAdminCuratedUri;
    public static URI projectUserCuratedUri;
    public static URI projectNotCuratedUri;
    public static String projectNamespace;
    
    static
    {
        ValueFactory f = new MemValueFactory();
        
        projectNamespace = Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX + Settings.DEFAULT_RDF_PROJECT_NAMESPACE + Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX;
        
        
        projectTypeUri = f.createURI(projectNamespace+"Project");
        projectAuthority = f.createURI(projectNamespace+"authority");
        projectTitle = f.createURI(projectNamespace+"title");
        projectDescription = f.createURI(projectNamespace+"description");
        
        projectCurationStatusUri = f.createURI(projectNamespace+"hasCurationStatus");
        projectAdminCuratedUri = f.createURI(projectNamespace+"adminCurated");
        projectUserCuratedUri = f.createURI(projectNamespace+"userCurated");
        projectNotCuratedUri = f.createURI(projectNamespace+"notCurated");
        
    }
    
    // keyToUse is the URI of the next instance that can be found in myRepository
    // returns null if the URI is not in the repository or the information is not enough to create a minimal provider configuration
    public static Project fromRdf(Collection<Statement> inputStatements, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        Project result = new ProjectImpl();
        
        boolean resultIsValid = false;
        
        ValueFactory f = new MemValueFactory();
        
        URI projectInstanceUri = keyToUse;
        
        for(Statement nextStatement : inputStatements)
        {
            if(_DEBUG)
            {
                log.debug("Project: nextStatement: "+nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE) && nextStatement.getObject().equals(projectTypeUri))
            {
                if(_TRACE)
                {
                    log.trace("Project: found valid type predicate for URI: "+keyToUse);
                }
                
                resultIsValid = true;
                result.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(projectAuthority))
            {
                result.setAuthority((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(projectTitle) || nextStatement.getPredicate().equals(Settings.DC_TITLE))
            {
                if(result.getTitle().equals(""))
                    result.setTitle(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(projectDescription))
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
            
            con.add(projectTypeUri, RDF.TYPE, OWL.CLASS, contextKeyUri);
            con.add(projectTitle, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(projectTitle, RDFS.SUBPROPERTYOF, f.createURI(Settings.DC_NAMESPACE+"title"), contextKeyUri);
            con.add(projectTitle, RDFS.SUBPROPERTYOF, f.createURI("http://www.w3.org/2000/01/rdf-schema#label"), contextKeyUri);
            con.add(projectAuthority, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(projectDescription, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(projectDescription, RDFS.SUBPROPERTYOF, f.createURI("http://www.w3.org/2000/01/rdf-schema#comment"), contextKeyUri);
            con.add(projectCurationStatusUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(projectAdminCuratedUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(projectUserCuratedUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(projectNotCuratedUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
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
        catch (OpenRDFException ordfe)
        {
            log.error(ordfe);
            
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
                con.rollback();
                
            throw ordfe;
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
            
            Literal titleLiteral = f.createLiteral(title);
            URI authorityLiteral = null;
            
            if(authority == null)
                authorityLiteral = f.createURI(Settings.getDefaultHostAddress());
            else
                authorityLiteral = authority;
                
            Literal descriptionLiteral = f.createLiteral(description);
            
            if(_TRACE)
            {
                log.trace("Project.toRdf: about to add URI's to connection");
            }
            
            con.setAutoCommit(false);
            
            con.add(projectInstanceUri, RDF.TYPE, projectTypeUri, projectInstanceUri);
            con.add(projectInstanceUri, projectAuthority, authorityLiteral, projectInstanceUri);
            if(modelVersion == 1)
            {
                con.add(projectInstanceUri, projectTitle, titleLiteral, projectInstanceUri);
            }
            else
            {
                con.add(projectInstanceUri, Settings.DC_TITLE, titleLiteral, projectInstanceUri);
            }
            con.add(projectInstanceUri, projectDescription, descriptionLiteral, projectInstanceUri);
            
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
        catch (OpenRDFException ordfe)
        {
            log.error(ordfe);
            
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
                con.rollback();
                
            throw ordfe;
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
        sb.append("authority=" + authority + "\n");
        sb.append("title=" + title + "\n");
        sb.append("description=" + description + "\n");
        
        return sb.toString();
    }
    
    @Override
    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        String prefix = "project_";
        
        // sb.append("<div class=\""+prefix+"preferredPrefix_div\"><span class=\""+prefix+"preferredPrefix_span\">Prefix:</span><input type=\"text\" name=\""+prefix+"preferredPrefix\" value=\""+Utilities.xmlEncodeString(preferredPrefix)+"\" /></div>\n");
        // sb.append("<div class=\""+prefix+"description_div\"><span class=\""+prefix+"description_span\">Description:</span><input type=\"text\" name=\""+prefix+"description\" value=\""+Utilities.xmlEncodeString(description)+"\" /></div>\n");
        // sb.append("<div class=\""+prefix+"identifierRegex_div\"><span class=\""+prefix+"identifierRegex_span\">Namespace identifier regular expression:</span><input type=\"text\" name=\""+prefix+"identifierRegex\" value=\""+Utilities.xmlEncodeString(identifierRegex)+"\" /></div>\n");
        
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
        this.setKey(Utilities.createURI(nextKey));
    }

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
     * @return the URI used for the rdf Type of these elements
     */
    @Override
    public String getElementType()
    {
        return projectTypeUri.stringValue();
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
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
    
        if ( this == otherProject ) 
            return EQUAL;

        return this.getKey().stringValue().compareTo(otherProject.getKey().stringValue());
    }
    
}
