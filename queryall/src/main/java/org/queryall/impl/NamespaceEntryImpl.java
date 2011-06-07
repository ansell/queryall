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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.queryall.api.NamespaceEntry;
import org.queryall.helpers.Constants;
import org.queryall.helpers.StringUtils;
import org.queryall.helpers.RdfUtils;
import org.queryall.helpers.Settings;

import org.apache.log4j.Logger;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class NamespaceEntryImpl extends NamespaceEntry
{
    private static final Logger log = Logger.getLogger(NamespaceEntry.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = log.isInfoEnabled();
    
    private static URI namespaceTypeUri;
    private static URI namespaceAuthority;
    private static URI namespaceIdentifierRegex;
    public static URI oldNamespaceTitle;
    private static URI namespacePreferredPrefix;
    private static URI namespaceAlternativePrefix;
    private static URI namespaceDescription;
    private static URI namespaceConvertQueriesToPreferredPrefix;
    private static URI namespaceUriTemplate;
    private static URI namespaceSeparator;
    
    public static String namespaceNamespace;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        namespaceNamespace = Settings.getSettings().getOntologyTermUriPrefix()
                             +Settings.getSettings().getNamespaceForNamespaceEntry()
                             +Settings.getSettings().getOntologyTermUriSuffix();
        
        setNamespaceTypeUri(f.createURI(namespaceNamespace,"Namespace"));
        setNamespaceAuthority(f.createURI(namespaceNamespace,"authority"));
        setNamespaceIdentifierRegex(f.createURI(namespaceNamespace,"identifierRegex"));
        setNamespacePreferredPrefix(f.createURI(namespaceNamespace,"preferredPrefix"));
        setNamespaceAlternativePrefix(f.createURI(namespaceNamespace,"alternativePrefix"));
        setNamespaceConvertQueriesToPreferredPrefix(f.createURI(namespaceNamespace,"convertToPreferred"));
        setNamespaceDescription(f.createURI(namespaceNamespace,"description"));
        setNamespaceUriTemplate(f.createURI(namespaceNamespace,"uriTemplate"));
        setNamespaceSeparator(f.createURI(namespaceNamespace,"separator"));
        oldNamespaceTitle = f.createURI(namespaceNamespace,"title");
    }

    private String defaultNamespace = Settings.getSettings().getNamespaceForNamespaceEntry();
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key;
    private String authority = "";
    private URI curationStatus = ProjectImpl.getProjectNotCuratedUri();
    
    private String preferredPrefix = "";
    private Collection<String> alternativePrefixes = new HashSet<String>();
    private String description = "";
    private String identifierRegex = "";
    private String uriTemplate = "";
    private String separator = Settings.getSettings().getStringPropertyFromConfig("separator", ":");
    
    // This setting determines whether input namespace prefixes in the alternatives list should be converted to the preferred prefix
    // It also determines whether owl:sameAs will be used to relate the preferred prefix to each of the alternative prefixes
    private boolean convertQueriesToPreferredPrefix = true;
    private String title;
    
    public NamespaceEntryImpl()
    {
    	
    }

    public NamespaceEntryImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion) throws OpenRDFException
	{
        Collection<String> tempAlternativePrefixes = new HashSet<String>();
        
        for(Statement nextStatement : inputStatements)
        {
            if(_DEBUG)
            {
                log.debug("NamespaceEntry: nextStatement: "+nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE) && nextStatement.getObject().equals(getNamespaceTypeUri()))
            {
                if(_TRACE)
                {
                    log.trace("NamespaceEntry: found valid type predicate for URI: "+keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.getProjectCurationStatusUri()))
            {
                this.setCurationStatus((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getNamespaceAuthority()))
            {
                this.setAuthority(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getNamespacePreferredPrefix()) || nextStatement.getPredicate().equals(oldNamespaceTitle))
            {
                if(this.getPreferredPrefix().trim().equals(""))
                {
                    this.setPreferredPrefix(nextStatement.getObject().stringValue());
                }
                else
                {
                    log.error("NamespaceEntry.fromRdf: found two preferred prefixes keyToUse="+keyToUse+" .... chosen="+this.getPreferredPrefix()+" other="+nextStatement.getObject().stringValue());
                }
            }
            else if(nextStatement.getPredicate().equals(getNamespaceAlternativePrefix()))
            {
                tempAlternativePrefixes.add(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getNamespaceDescription()) || nextStatement.getPredicate().equals(RDFS.COMMENT))
            {
                this.setDescription(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getNamespaceIdentifierRegex()))
            {
                this.setIdentifierRegex(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getNamespaceUriTemplate()))
            {
                this.setUriTemplate(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getNamespaceSeparator()))
            {
                this.setSeparator(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getNamespaceConvertQueriesToPreferredPrefix()))
            {
                this.setConvertQueriesToPreferredPrefix(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        this.setAlternativePrefixes(tempAlternativePrefixes);
        
        if(_TRACE)
        {
            log.trace("NamespaceEntry.fromRdf: would have returned... result="+this.toString());
        }
    }
    
    public static boolean schemaToRdf(Repository myRepository, String keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            URI contextKeyUri = f.createURI(keyToUse);
            con.setAutoCommit(false);
            
            con.add(getNamespaceTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);

            // TODO: Add description
            con.add(getNamespacePreferredPrefix(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getNamespacePreferredPrefix(), RDFS.SUBPROPERTYOF, Constants.DC_TITLE, contextKeyUri);
            con.add(getNamespacePreferredPrefix(), RDFS.SUBPROPERTYOF, RDFS.LABEL, contextKeyUri);
            con.add(getNamespacePreferredPrefix(), RDFS.SUBPROPERTYOF, Constants.SKOS_PREFLABEL, contextKeyUri);
            con.add(getNamespacePreferredPrefix(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getNamespacePreferredPrefix(), RDFS.DOMAIN, getNamespaceTypeUri(), contextKeyUri);
            con.add(getNamespacePreferredPrefix(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);


            // TODO: Add description
            con.add(getNamespaceAlternativePrefix(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getNamespaceAlternativePrefix(), RDFS.SUBPROPERTYOF, Constants.SKOS_ALTLABEL, contextKeyUri);
            con.add(getNamespaceAlternativePrefix(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getNamespaceAlternativePrefix(), RDFS.DOMAIN, getNamespaceTypeUri(), contextKeyUri);
            con.add(getNamespaceAlternativePrefix(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);


            // TODO: Add description
            con.add(getNamespaceAuthority(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getNamespaceAuthority(), RDFS.RANGE, RDFS.RESOURCE, contextKeyUri);
            con.add(getNamespaceAuthority(), RDFS.DOMAIN, getNamespaceTypeUri(), contextKeyUri);
            con.add(getNamespaceAuthority(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);

            
            // TODO: Add description
            con.add(getNamespaceIdentifierRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getNamespaceIdentifierRegex(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getNamespaceIdentifierRegex(), RDFS.DOMAIN, getNamespaceTypeUri(), contextKeyUri);
            con.add(getNamespaceIdentifierRegex(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);


            // TODO: Add description
            con.add(getNamespaceConvertQueriesToPreferredPrefix(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getNamespaceConvertQueriesToPreferredPrefix(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getNamespaceConvertQueriesToPreferredPrefix(), RDFS.DOMAIN, getNamespaceTypeUri(), contextKeyUri);
            con.add(getNamespaceConvertQueriesToPreferredPrefix(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);


            
            // TODO: Add description
            con.add(getNamespaceUriTemplate(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getNamespaceUriTemplate(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getNamespaceUriTemplate(), RDFS.DOMAIN, getNamespaceTypeUri(), contextKeyUri);
            con.add(getNamespaceUriTemplate(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);

            // TODO: Add description
            con.add(getNamespaceSeparator(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getNamespaceSeparator(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getNamespaceSeparator(), RDFS.DOMAIN, getNamespaceTypeUri(), contextKeyUri);
            con.add(getNamespaceSeparator(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);

            
            if(modelVersion == 1)
            {
                con.add(getNamespaceDescription(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
                con.add(getNamespaceDescription(), RDFS.SUBPROPERTYOF, RDFS.COMMENT, contextKeyUri);
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
    
    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        unrecognisedStatements.add(unrecognisedStatement);
    }
    
    public int compareTo(NamespaceEntry otherNamespace)
    {
        @SuppressWarnings("unused")
        final int BEFORE = -1;
        final int EQUAL = 0;
        @SuppressWarnings("unused")
        final int AFTER = 1;
    
        if ( this == otherNamespace ) 
            return EQUAL;
        
        return this.getPreferredPrefix().compareTo(otherNamespace.getPreferredPrefix());
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return this.title;
    }

    public Collection<String> getAlternativePrefixes()
    {
        return alternativePrefixes;
    }
    
    public String getAuthority()
    {
        return authority;
    }
    
    public boolean getConvertQueriesToPreferredPrefix()
    {
        return convertQueriesToPreferredPrefix;
    }

    public URI getCurationStatus()
    {
        return curationStatus;
    }

    /**
     * @return the namespace used to represent objects of this type by default
     */
    public String getDefaultNamespace()
    {
        return defaultNamespace;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    /**
     * @return the URI used for the rdf Type of these elements
     */
    public URI getElementType()
    {
        return getNamespaceTypeUri();
    }
    
    public String getIdentifierRegex()
    {
        return identifierRegex;
    }
    
    /**
     * @return the key
     */
    public URI getKey()
    {
        return key;
    }
    
    public String getPreferredPrefix()
    {
        return preferredPrefix;
    }
    
    public String getSeparator()
    {
        return separator;
    }
    
    public Collection<Statement> getUnrecognisedStatements()
    {
        return unrecognisedStatements;
    }

    public String getUriTemplate()
    {
        return uriTemplate;
    }
    
    public void setAlternativePrefixes(Collection<String> alternativePrefixes)
    {
        this.alternativePrefixes = alternativePrefixes;
    }
    
    public void setAuthority(String authority)
    {
        this.authority = authority;
    }
    
    public void setConvertQueriesToPreferredPrefix(boolean convertQueriesToPreferredPrefix)
    {
        this.convertQueriesToPreferredPrefix = convertQueriesToPreferredPrefix;
    }
    
    public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }    
    
    public void setIdentifierRegex(String identifierRegex)
    {
        this.identifierRegex = identifierRegex;
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
    
    public void setPreferredPrefix(String preferredPrefix)
    {
        this.preferredPrefix = preferredPrefix;
    }

    public void setSeparator(String separator)
    {
        this.separator = separator;
    }
    
    public void setUriTemplate(String uriTemplate)
    {
        this.uriTemplate = uriTemplate;
    }    
    
    public String toHtml()
    {
        return "";
    }
    
    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        String prefix = "namespace_";
        
        sb.append("<div class=\""+prefix+"preferredPrefix_div\"><span class=\""+prefix+"preferredPrefix_span\">Prefix:</span><input type=\"text\" name=\""+prefix+"preferredPrefix\" value=\""+StringUtils.xmlEncodeString(getPreferredPrefix())+"\" /></div>\n");
        sb.append("<div class=\""+prefix+"description_div\"><span class=\""+prefix+"description_span\">Description:</span><input type=\"text\" name=\""+prefix+"description\" value=\""+StringUtils.xmlEncodeString(getDescription())+"\" /></div>\n");
        sb.append("<div class=\""+prefix+"identifierRegex_div\"><span class=\""+prefix+"identifierRegex_span\">Namespace identifier regular expression:</span><input type=\"text\" name=\""+prefix+"identifierRegex\" value=\""+StringUtils.xmlEncodeString(getIdentifierRegex())+"\" /></div>\n");
        
        return sb.toString();
    }    
    
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            URI namespaceInstanceUri;
            
            if(_DEBUG)
            {
                log.debug("NamespaceEntry.toRdf: about create instance URI");
                log.debug("NamespaceEntry.toRdf: keyToUse="+keyToUse);
                log.debug("NamespaceEntry.toRdf: preferredPrefix="+getPreferredPrefix());
            }
            
            namespaceInstanceUri = keyToUse;
            
            Literal preferredPrefixLiteral = f.createLiteral(getPreferredPrefix());
            
            URI authorityLiteral = null;
            
            if(getAuthority() == null || getAuthority().trim().equals(""))
                authorityLiteral = f.createURI(Settings.getSettings().getDefaultHostAddress());
            else
                authorityLiteral = f.createURI(getAuthority());
                
            URI curationStatusLiteral = null;
            
            if(curationStatus == null)
                curationStatusLiteral = ProjectImpl.getProjectNotCuratedUri();
            else
                curationStatusLiteral = curationStatus;
                
            con.add(namespaceInstanceUri, ProjectImpl.getProjectCurationStatusUri(), curationStatusLiteral, namespaceInstanceUri);
            
            Literal descriptionLiteral = f.createLiteral(getDescription());
            
            Literal identifierRegexLiteral = f.createLiteral(getIdentifierRegex());
            
            Literal convertQueriesToPreferredPrefixLiteral = f.createLiteral(getConvertQueriesToPreferredPrefix());
            
            Literal uriTemplateLiteral = f.createLiteral(getUriTemplate());
            Literal separatorLiteral = f.createLiteral(getSeparator());

            if(_TRACE)
            {
                log.trace("NamespaceEntry.toRdf: about to add URI's to connection");
            }
            
            con.setAutoCommit(false);
            
            con.add(namespaceInstanceUri, RDF.TYPE, getNamespaceTypeUri(), namespaceInstanceUri);
            con.add(namespaceInstanceUri, getNamespaceAuthority(), authorityLiteral, namespaceInstanceUri);
            con.add(namespaceInstanceUri, getNamespacePreferredPrefix(), preferredPrefixLiteral, namespaceInstanceUri);
            con.add(namespaceInstanceUri, getNamespaceConvertQueriesToPreferredPrefix(), convertQueriesToPreferredPrefixLiteral, namespaceInstanceUri);
            con.add(namespaceInstanceUri, getNamespaceIdentifierRegex(), identifierRegexLiteral, namespaceInstanceUri);
            con.add(namespaceInstanceUri, getNamespaceSeparator(), separatorLiteral, namespaceInstanceUri);
            con.add(namespaceInstanceUri, getNamespaceUriTemplate(), uriTemplateLiteral, namespaceInstanceUri);
            if(modelVersion == 1)
            {
                con.add(namespaceInstanceUri, getNamespaceDescription(), descriptionLiteral, namespaceInstanceUri);
            }
            else
            {
                con.add(namespaceInstanceUri, RDFS.COMMENT, descriptionLiteral, namespaceInstanceUri);
            }
            
            if(getAlternativePrefixes() != null)
            {
            
                for(String nextAlternativePrefix : getAlternativePrefixes())
                {
                    con.add(namespaceInstanceUri, getNamespaceAlternativePrefix(), f.createLiteral(nextAlternativePrefix), namespaceInstanceUri);
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
        sb.append("preferredPrefix=" + getPreferredPrefix() + "\n");
        sb.append("description=" + getDescription() + "\n");
        
        return sb.toString();
    }

	/**
	 * @param namespaceTypeUri the namespaceTypeUri to set
	 */
	public static void setNamespaceTypeUri(URI namespaceTypeUri) {
		NamespaceEntryImpl.namespaceTypeUri = namespaceTypeUri;
	}

	/**
	 * @return the namespaceTypeUri
	 */
	public static URI getNamespaceTypeUri() {
		return namespaceTypeUri;
	}

	/**
	 * @param namespaceAuthority the namespaceAuthority to set
	 */
	public static void setNamespaceAuthority(URI namespaceAuthority) {
		NamespaceEntryImpl.namespaceAuthority = namespaceAuthority;
	}

	/**
	 * @return the namespaceAuthority
	 */
	public static URI getNamespaceAuthority() {
		return namespaceAuthority;
	}

	/**
	 * @param namespaceIdentifierRegex the namespaceIdentifierRegex to set
	 */
	public static void setNamespaceIdentifierRegex(
			URI namespaceIdentifierRegex) {
		NamespaceEntryImpl.namespaceIdentifierRegex = namespaceIdentifierRegex;
	}

	/**
	 * @return the namespaceIdentifierRegex
	 */
	public static URI getNamespaceIdentifierRegex() {
		return namespaceIdentifierRegex;
	}

	/**
	 * @param namespacePreferredPrefix the namespacePreferredPrefix to set
	 */
	public static void setNamespacePreferredPrefix(
			URI namespacePreferredPrefix) {
		NamespaceEntryImpl.namespacePreferredPrefix = namespacePreferredPrefix;
	}

	/**
	 * @return the namespacePreferredPrefix
	 */
	public static URI getNamespacePreferredPrefix() {
		return namespacePreferredPrefix;
	}

	/**
	 * @param namespaceAlternativePrefix the namespaceAlternativePrefix to set
	 */
	public static void setNamespaceAlternativePrefix(
			URI namespaceAlternativePrefix) {
		NamespaceEntryImpl.namespaceAlternativePrefix = namespaceAlternativePrefix;
	}

	/**
	 * @return the namespaceAlternativePrefix
	 */
	public static URI getNamespaceAlternativePrefix() {
		return namespaceAlternativePrefix;
	}

	/**
	 * @param namespaceDescription the namespaceDescription to set
	 */
	public static void setNamespaceDescription(URI namespaceDescription) {
		NamespaceEntryImpl.namespaceDescription = namespaceDescription;
	}

	/**
	 * @return the namespaceDescription
	 */
	public static URI getNamespaceDescription() {
		return namespaceDescription;
	}

	/**
	 * @param namespaceConvertQueriesToPreferredPrefix the namespaceConvertQueriesToPreferredPrefix to set
	 */
	public static void setNamespaceConvertQueriesToPreferredPrefix(
			URI namespaceConvertQueriesToPreferredPrefix) {
		NamespaceEntryImpl.namespaceConvertQueriesToPreferredPrefix = namespaceConvertQueriesToPreferredPrefix;
	}

	/**
	 * @return the namespaceConvertQueriesToPreferredPrefix
	 */
	public static URI getNamespaceConvertQueriesToPreferredPrefix() {
		return namespaceConvertQueriesToPreferredPrefix;
	}

	/**
	 * @param namespaceUriTemplate the namespaceUriTemplate to set
	 */
	public static void setNamespaceUriTemplate(URI namespaceUriTemplate) {
		NamespaceEntryImpl.namespaceUriTemplate = namespaceUriTemplate;
	}

	/**
	 * @return the namespaceUriTemplate
	 */
	public static URI getNamespaceUriTemplate() {
		return namespaceUriTemplate;
	}

	/**
	 * @param namespaceSeparator the namespaceSeparator to set
	 */
	public static void setNamespaceSeparator(URI namespaceSeparator) {
		NamespaceEntryImpl.namespaceSeparator = namespaceSeparator;
	}

	/**
	 * @return the namespaceSeparator
	 */
	public static URI getNamespaceSeparator() {
		return namespaceSeparator;
	}

    
}
