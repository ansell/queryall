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

import java.util.Collection;
import java.util.HashSet;

import org.queryall.NamespaceEntry;
import org.queryall.helpers.Utilities;
import org.queryall.helpers.Settings;

import org.apache.log4j.Logger;

public class NamespaceEntryImpl extends NamespaceEntry
{
    private static final Logger log = Logger.getLogger(NamespaceEntry.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    private static final boolean _INFO = log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.DEFAULT_RDF_NAMESPACEENTRY_NAMESPACE;
    
    public Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key;
    public String authority = "";
    private URI curationStatus = ProjectImpl.projectNotCuratedUri;
    
    public String preferredPrefix = "";
    public Collection<String> alternativePrefixes = new HashSet<String>();
    public String description = "";
    public String identifierRegex = "";
    public String uriTemplate = "";
    public String separator = Settings.getStringPropertyFromConfig("separator");
    
    // This setting determines whether input namespace prefixes in the alternatives list should be converted to the preferred prefix
    // It also determines whether owl:sameAs will be used to relate the preferred prefix to each of the alternative prefixes
    public boolean convertQueriesToPreferredPrefix = true;
    
    public static URI namespaceTypeUri;
    public static URI namespaceAuthority;
    public static URI namespaceIdentifierRegex;
    public static URI oldNamespaceTitle;
    public static URI namespacePreferredPrefix;
    public static URI namespaceAlternativePrefix;
    public static URI namespaceDescription;
    public static URI namespaceConvertQueriesToPreferredPrefix;
    public static URI namespaceUriTemplate;
    public static URI namespaceSeparator;
    
    public static String namespaceNamespace;
    
    static
    {
        ValueFactory f = new MemValueFactory();
        
        namespaceNamespace = Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX
                             +Settings.DEFAULT_RDF_NAMESPACEENTRY_NAMESPACE
                             +Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX;
                             
        namespaceTypeUri = f.createURI(namespaceNamespace+"Namespace");
        namespaceAuthority = f.createURI(namespaceNamespace+"authority");
        namespaceIdentifierRegex = f.createURI(namespaceNamespace+"identifierRegex");
        namespacePreferredPrefix = f.createURI(namespaceNamespace+"preferredPrefix");
        namespaceAlternativePrefix = f.createURI(namespaceNamespace+"alternativePrefix");
        namespaceConvertQueriesToPreferredPrefix = f.createURI(namespaceNamespace+"convertToPreferred");
        namespaceDescription = f.createURI(namespaceNamespace+"description");
        namespaceUriTemplate = f.createURI(namespaceNamespace+"uriTemplate");
        namespaceSeparator = f.createURI(namespaceNamespace+"separator");
        oldNamespaceTitle = f.createURI(namespaceNamespace+"title");
    }
    
    // keyToUse is the URI of the next instance that can be found in myRepository
    // returns null if the URI is not in the repository or the information is not enough to create a minimal provider configuration
    public static NamespaceEntry fromRdf(Collection<Statement> inputStatements, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        NamespaceEntry result = new NamespaceEntryImpl();
        
        boolean resultIsValid = false;
        
        Collection<String> tempAlternativePrefixes = new HashSet<String>();
        
        ValueFactory f = new MemValueFactory();
        
        URI namespaceInstanceUri = keyToUse;
        
        for(Statement nextStatement : inputStatements)
        {
            if(_DEBUG)
            {
                log.debug("NamespaceEntry: nextStatement: "+nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE) && nextStatement.getObject().equals(namespaceTypeUri))
            {
                if(_TRACE)
                {
                    log.trace("NamespaceEntry: found valid type predicate for URI: "+keyToUse);
                }
                
                resultIsValid = true;
                result.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.projectCurationStatusUri))
            {
                result.setCurationStatus((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(namespaceAuthority))
            {
                result.setAuthority(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(namespacePreferredPrefix) || nextStatement.getPredicate().equals(oldNamespaceTitle))
            {
                if(result.getPreferredPrefix().trim().equals(""))
                {
                    result.setPreferredPrefix(nextStatement.getObject().stringValue());
                }
                else
                {
                    log.error("NamespaceEntry.fromRdf: found two preferred prefixes keyToUse="+keyToUse+" .... chosen="+result.getPreferredPrefix()+" other="+nextStatement.getObject().stringValue());
                }
            }
            else if(nextStatement.getPredicate().equals(namespaceAlternativePrefix))
            {
                tempAlternativePrefixes.add(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(namespaceDescription) || nextStatement.getPredicate().equals(RDFS.COMMENT))
            {
                result.setDescription(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(namespaceIdentifierRegex))
            {
                result.setIdentifierRegex(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(namespaceUriTemplate))
            {
                result.setUriTemplate(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(namespaceSeparator))
            {
                result.setSeparator(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(namespaceConvertQueriesToPreferredPrefix))
            {
                result.setConvertQueriesToPreferredPrefix(Utilities.getBooleanFromValue(nextStatement.getObject()));
            }
            else
            {
                result.addUnrecognisedStatement(nextStatement);
            }
        }
        
        result.setAlternativePrefixes(tempAlternativePrefixes);
        
        if(_TRACE)
        {
            log.trace("NamespaceEntry.fromRdf: would have returned... result="+result.toString());
        }
        
        if(resultIsValid)
        {
            return result;
        }
        else
        {
            throw new RuntimeException("NamespaceEntry.fromRdf: result was not valid");
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
            
            con.add(namespaceTypeUri, RDF.TYPE, OWL.CLASS, contextKeyUri);
            con.add(namespacePreferredPrefix, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(namespacePreferredPrefix, RDFS.SUBPROPERTYOF, f.createURI(Settings.DC_NAMESPACE+"title"), contextKeyUri);
            con.add(namespacePreferredPrefix, RDFS.SUBPROPERTYOF, f.createURI("http://www.w3.org/2000/01/rdf-schema#label"), contextKeyUri);
            con.add(namespaceAlternativePrefix, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(namespaceAuthority, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(namespaceIdentifierRegex, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(namespaceConvertQueriesToPreferredPrefix, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(namespaceUriTemplate, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(namespaceSeparator, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            if(modelVersion == 1)
            {
                con.add(namespaceDescription, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
                con.add(namespaceDescription, RDFS.SUBPROPERTYOF, RDFS.COMMENT, contextKeyUri);
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
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            URI namespaceInstanceUri;
            
            if(_DEBUG)
            {
                log.debug("NamespaceEntry.toRdf: about create instance URI");
                log.debug("NamespaceEntry.toRdf: keyToUse="+keyToUse);
                log.debug("NamespaceEntry.toRdf: preferredPrefix="+preferredPrefix);
            }
            
            namespaceInstanceUri = keyToUse;
            
            Literal preferredPrefixLiteral = f.createLiteral(preferredPrefix);
            
            URI authorityLiteral = null;
            
            if(authority == null || authority.trim().equals(""))
                authorityLiteral = f.createURI(Settings.getDefaultHostAddress());
            else
                authorityLiteral = f.createURI(authority);
                
            URI curationStatusLiteral = null;
            
            if(curationStatus == null)
                curationStatusLiteral = ProjectImpl.projectNotCuratedUri;
            else
                curationStatusLiteral = curationStatus;
                
            con.add(namespaceInstanceUri, ProjectImpl.projectCurationStatusUri, curationStatusLiteral, namespaceInstanceUri);
            
            Literal descriptionLiteral = f.createLiteral(description);
            
            Literal identifierRegexLiteral = f.createLiteral(identifierRegex);
            
            Literal convertQueriesToPreferredPrefixLiteral = f.createLiteral(convertQueriesToPreferredPrefix);
            
            Literal uriTemplateLiteral = f.createLiteral(uriTemplate);
            Literal separatorLiteral = f.createLiteral(separator);

            if(_TRACE)
            {
                log.trace("NamespaceEntry.toRdf: about to add URI's to connection");
            }
            
            con.setAutoCommit(false);
            
            con.add(namespaceInstanceUri, RDF.TYPE, namespaceTypeUri, namespaceInstanceUri);
            con.add(namespaceInstanceUri, namespaceAuthority, authorityLiteral, namespaceInstanceUri);
            con.add(namespaceInstanceUri, namespacePreferredPrefix, preferredPrefixLiteral, namespaceInstanceUri);
            con.add(namespaceInstanceUri, namespaceConvertQueriesToPreferredPrefix, convertQueriesToPreferredPrefixLiteral, namespaceInstanceUri);
            con.add(namespaceInstanceUri, namespaceIdentifierRegex, identifierRegexLiteral, namespaceInstanceUri);
            con.add(namespaceInstanceUri, namespaceSeparator, separatorLiteral, namespaceInstanceUri);
            con.add(namespaceInstanceUri, namespaceUriTemplate, uriTemplateLiteral, namespaceInstanceUri);
            if(modelVersion == 1)
            {
                con.add(namespaceInstanceUri, namespaceDescription, descriptionLiteral, namespaceInstanceUri);
            }
            else
            {
                con.add(namespaceInstanceUri, RDFS.COMMENT, descriptionLiteral, namespaceInstanceUri);
            }
            
            if(alternativePrefixes != null)
            {
            
                for(String nextAlternativePrefix : alternativePrefixes)
                {
                    con.add(namespaceInstanceUri, namespaceAlternativePrefix, f.createLiteral(nextAlternativePrefix), namespaceInstanceUri);
                }
            }
            
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
        sb.append("preferredPrefix=" + preferredPrefix + "\n");
        sb.append("description=" + description + "\n");
        
        return sb.toString();
    }
    
    @Override
    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        String prefix = "namespace_";
        
        sb.append("<div class=\""+prefix+"preferredPrefix_div\"><span class=\""+prefix+"preferredPrefix_span\">Prefix:</span><input type=\"text\" name=\""+prefix+"preferredPrefix\" value=\""+Utilities.xmlEncodeString(preferredPrefix)+"\" /></div>\n");
        sb.append("<div class=\""+prefix+"description_div\"><span class=\""+prefix+"description_span\">Description:</span><input type=\"text\" name=\""+prefix+"description\" value=\""+Utilities.xmlEncodeString(description)+"\" /></div>\n");
        sb.append("<div class=\""+prefix+"identifierRegex_div\"><span class=\""+prefix+"identifierRegex_span\">Namespace identifier regular expression:</span><input type=\"text\" name=\""+prefix+"identifierRegex\" value=\""+Utilities.xmlEncodeString(identifierRegex)+"\" /></div>\n");
        
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
        return namespaceTypeUri.stringValue();
    }
    
    public String getPreferredPrefix()
    {
        return preferredPrefix;
    }
    
    public void setPreferredPrefix(String preferredPrefix)
    {
        this.preferredPrefix = preferredPrefix;
    }
    
    public Collection<String> getAlternativePrefixes()
    {
        return alternativePrefixes;
    }
    
    public void setAlternativePrefixes(Collection<String> alternativePrefixes)
    {
        this.alternativePrefixes = alternativePrefixes;
    }
    
    public int compareTo(NamespaceEntry otherNamespace)
    {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
    
        if ( this == otherNamespace ) 
            return EQUAL;
        
        return this.getPreferredPrefix().compareTo(otherNamespace.getPreferredPrefix());
    }

    public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    public URI getCurationStatus()
    {
        return curationStatus;
    }
    
    public String getAuthority()
    {
        return authority;
    }
    
    public void setAuthority(String authority)
    {
        this.authority = authority;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }    
    
    public String getIdentifierRegex()
    {
        return identifierRegex;
    }
    
    public void setIdentifierRegex(String identifierRegex)
    {
        this.identifierRegex = identifierRegex;
    }
    
    public String getUriTemplate()
    {
        return uriTemplate;
    }
    
    public void setUriTemplate(String uriTemplate)
    {
        this.uriTemplate = uriTemplate;
    }

    public String getSeparator()
    {
        return separator;
    }
    
    public void setSeparator(String separator)
    {
        this.separator = separator;
    }    
    
    public boolean getConvertQueriesToPreferredPrefix()
    {
        return convertQueriesToPreferredPrefix;
    }
    
    public void setConvertQueriesToPreferredPrefix(boolean convertQueriesToPreferredPrefix)
    {
        this.convertQueriesToPreferredPrefix = convertQueriesToPreferredPrefix;
    }    
    
    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        unrecognisedStatements.add(unrecognisedStatement);
    }

    public Collection<Statement> getUnrecognisedStatements()
    {
        return unrecognisedStatements;
    }

    
}
