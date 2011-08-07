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
import org.queryall.api.NamespaceEntry;
import org.queryall.enumerations.Constants;
import org.queryall.query.Settings;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.StringUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class NamespaceEntryImpl implements NamespaceEntry
{
    private static final Logger log = Logger.getLogger(NamespaceEntry.class.getName());
    private static final boolean _TRACE = NamespaceEntryImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = NamespaceEntryImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = NamespaceEntryImpl.log.isInfoEnabled();
    
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
        
        NamespaceEntryImpl.namespaceNamespace =
                Settings.getSettings().getOntologyTermUriPrefix()
                        + Settings.getSettings().getNamespaceForNamespaceEntry()
                        + Settings.getSettings().getOntologyTermUriSuffix();
        
        NamespaceEntryImpl.setNamespaceTypeUri(f.createURI(NamespaceEntryImpl.namespaceNamespace, "Namespace"));
        NamespaceEntryImpl.setNamespaceAuthority(f.createURI(NamespaceEntryImpl.namespaceNamespace, "authority"));
        NamespaceEntryImpl.setNamespaceIdentifierRegex(f.createURI(NamespaceEntryImpl.namespaceNamespace,
                "identifierRegex"));
        NamespaceEntryImpl.setNamespacePreferredPrefix(f.createURI(NamespaceEntryImpl.namespaceNamespace,
                "preferredPrefix"));
        NamespaceEntryImpl.setNamespaceAlternativePrefix(f.createURI(NamespaceEntryImpl.namespaceNamespace,
                "alternativePrefix"));
        NamespaceEntryImpl.setNamespaceConvertQueriesToPreferredPrefix(f.createURI(
                NamespaceEntryImpl.namespaceNamespace, "convertToPreferred"));
        NamespaceEntryImpl.setNamespaceDescription(f.createURI(NamespaceEntryImpl.namespaceNamespace, "description"));
        NamespaceEntryImpl.setNamespaceUriTemplate(f.createURI(NamespaceEntryImpl.namespaceNamespace, "uriTemplate"));
        NamespaceEntryImpl.setNamespaceSeparator(f.createURI(NamespaceEntryImpl.namespaceNamespace, "separator"));
        NamespaceEntryImpl.oldNamespaceTitle = f.createURI(NamespaceEntryImpl.namespaceNamespace, "title");
    }
    
    /**
     * @return the namespaceAlternativePrefix
     */
    public static URI getNamespaceAlternativePrefix()
    {
        return NamespaceEntryImpl.namespaceAlternativePrefix;
    }
    
    /**
     * @return the namespaceAuthority
     */
    public static URI getNamespaceAuthority()
    {
        return NamespaceEntryImpl.namespaceAuthority;
    }
    
    /**
     * @return the namespaceConvertQueriesToPreferredPrefix
     */
    public static URI getNamespaceConvertQueriesToPreferredPrefix()
    {
        return NamespaceEntryImpl.namespaceConvertQueriesToPreferredPrefix;
    }
    
    /**
     * @return the namespaceDescription
     */
    public static URI getNamespaceDescription()
    {
        return NamespaceEntryImpl.namespaceDescription;
    }
    
    /**
     * @return the namespaceIdentifierRegex
     */
    public static URI getNamespaceIdentifierRegex()
    {
        return NamespaceEntryImpl.namespaceIdentifierRegex;
    }
    
    /**
     * @return the namespacePreferredPrefix
     */
    public static URI getNamespacePreferredPrefix()
    {
        return NamespaceEntryImpl.namespacePreferredPrefix;
    }
    
    /**
     * @return the namespaceSeparator
     */
    public static URI getNamespaceSeparator()
    {
        return NamespaceEntryImpl.namespaceSeparator;
    }
    
    /**
     * @return the namespaceTypeUri
     */
    public static URI getNamespaceTypeUri()
    {
        return NamespaceEntryImpl.namespaceTypeUri;
    }
    
    /**
     * @return the namespaceUriTemplate
     */
    public static URI getNamespaceUriTemplate()
    {
        return NamespaceEntryImpl.namespaceUriTemplate;
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            final URI contextKeyUri = contextUri;
            con.setAutoCommit(false);
            
            con.add(NamespaceEntryImpl.getNamespaceTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            
            // TODO: Add description
            con.add(NamespaceEntryImpl.getNamespacePreferredPrefix(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespacePreferredPrefix(), RDFS.SUBPROPERTYOF, Constants.DC_TITLE,
                    contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespacePreferredPrefix(), RDFS.SUBPROPERTYOF, RDFS.LABEL, contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespacePreferredPrefix(), RDFS.SUBPROPERTYOF, Constants.SKOS_PREFLABEL,
                    contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespacePreferredPrefix(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespacePreferredPrefix(), RDFS.DOMAIN,
                    NamespaceEntryImpl.getNamespaceTypeUri(), contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespacePreferredPrefix(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(NamespaceEntryImpl.getNamespaceAlternativePrefix(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceAlternativePrefix(), RDFS.SUBPROPERTYOF, Constants.SKOS_ALTLABEL,
                    contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceAlternativePrefix(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceAlternativePrefix(), RDFS.DOMAIN,
                    NamespaceEntryImpl.getNamespaceTypeUri(), contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceAlternativePrefix(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(NamespaceEntryImpl.getNamespaceAuthority(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceAuthority(), RDFS.RANGE, RDFS.RESOURCE, contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceAuthority(), RDFS.DOMAIN, NamespaceEntryImpl.getNamespaceTypeUri(),
                    contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceAuthority(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(NamespaceEntryImpl.getNamespaceIdentifierRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceIdentifierRegex(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceIdentifierRegex(), RDFS.DOMAIN,
                    NamespaceEntryImpl.getNamespaceTypeUri(), contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceIdentifierRegex(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(NamespaceEntryImpl.getNamespaceConvertQueriesToPreferredPrefix(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceConvertQueriesToPreferredPrefix(), RDFS.RANGE, RDFS.LITERAL,
                    contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceConvertQueriesToPreferredPrefix(), RDFS.DOMAIN,
                    NamespaceEntryImpl.getNamespaceTypeUri(), contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceConvertQueriesToPreferredPrefix(), RDFS.LABEL, f.createLiteral("."),
                    contextKeyUri);
            
            // TODO: Add description
            con.add(NamespaceEntryImpl.getNamespaceUriTemplate(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceUriTemplate(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceUriTemplate(), RDFS.DOMAIN,
                    NamespaceEntryImpl.getNamespaceTypeUri(), contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceUriTemplate(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // TODO: Add description
            con.add(NamespaceEntryImpl.getNamespaceSeparator(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceSeparator(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceSeparator(), RDFS.DOMAIN, NamespaceEntryImpl.getNamespaceTypeUri(),
                    contextKeyUri);
            con.add(NamespaceEntryImpl.getNamespaceSeparator(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            if(modelVersion == 1)
            {
                con.add(NamespaceEntryImpl.getNamespaceDescription(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
                con.add(NamespaceEntryImpl.getNamespaceDescription(), RDFS.SUBPROPERTYOF, RDFS.COMMENT, contextKeyUri);
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
            
            NamespaceEntryImpl.log.error("RepositoryException: " + re.getMessage());
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
     * @param namespaceAlternativePrefix
     *            the namespaceAlternativePrefix to set
     */
    public static void setNamespaceAlternativePrefix(final URI namespaceAlternativePrefix)
    {
        NamespaceEntryImpl.namespaceAlternativePrefix = namespaceAlternativePrefix;
    }
    
    /**
     * @param namespaceAuthority
     *            the namespaceAuthority to set
     */
    public static void setNamespaceAuthority(final URI namespaceAuthority)
    {
        NamespaceEntryImpl.namespaceAuthority = namespaceAuthority;
    }
    
    /**
     * @param namespaceConvertQueriesToPreferredPrefix
     *            the namespaceConvertQueriesToPreferredPrefix to set
     */
    public static void setNamespaceConvertQueriesToPreferredPrefix(final URI namespaceConvertQueriesToPreferredPrefix)
    {
        NamespaceEntryImpl.namespaceConvertQueriesToPreferredPrefix = namespaceConvertQueriesToPreferredPrefix;
    }
    
    /**
     * @param namespaceDescription
     *            the namespaceDescription to set
     */
    public static void setNamespaceDescription(final URI namespaceDescription)
    {
        NamespaceEntryImpl.namespaceDescription = namespaceDescription;
    }
    
    /**
     * @param namespaceIdentifierRegex
     *            the namespaceIdentifierRegex to set
     */
    public static void setNamespaceIdentifierRegex(final URI namespaceIdentifierRegex)
    {
        NamespaceEntryImpl.namespaceIdentifierRegex = namespaceIdentifierRegex;
    }
    
    /**
     * @param namespacePreferredPrefix
     *            the namespacePreferredPrefix to set
     */
    public static void setNamespacePreferredPrefix(final URI namespacePreferredPrefix)
    {
        NamespaceEntryImpl.namespacePreferredPrefix = namespacePreferredPrefix;
    }
    
    /**
     * @param namespaceSeparator
     *            the namespaceSeparator to set
     */
    public static void setNamespaceSeparator(final URI namespaceSeparator)
    {
        NamespaceEntryImpl.namespaceSeparator = namespaceSeparator;
    }
    
    /**
     * @param namespaceTypeUri
     *            the namespaceTypeUri to set
     */
    public static void setNamespaceTypeUri(final URI namespaceTypeUri)
    {
        NamespaceEntryImpl.namespaceTypeUri = namespaceTypeUri;
    }
    
    /**
     * @param namespaceUriTemplate
     *            the namespaceUriTemplate to set
     */
    public static void setNamespaceUriTemplate(final URI namespaceUriTemplate)
    {
        NamespaceEntryImpl.namespaceUriTemplate = namespaceUriTemplate;
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
    
    private String separator = Settings.getSettings().getStringProperty("separator", ":");
    
    // This setting determines whether input namespace prefixes in the alternatives list should be
    // converted to the preferred prefix
    // It also determines whether owl:sameAs will be used to relate the preferred prefix to each of
    // the alternative prefixes
    private boolean convertQueriesToPreferredPrefix = true;
    
    private String title;
    
    public NamespaceEntryImpl()
    {
        
    }
    
    public NamespaceEntryImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final Collection<String> tempAlternativePrefixes = new HashSet<String>();
        
        for(final Statement nextStatement : inputStatements)
        {
            if(NamespaceEntryImpl._DEBUG)
            {
                NamespaceEntryImpl.log.debug("NamespaceEntry: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(NamespaceEntryImpl.getNamespaceTypeUri()))
            {
                if(NamespaceEntryImpl._TRACE)
                {
                    NamespaceEntryImpl.log.trace("NamespaceEntry: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.getProjectCurationStatusUri()))
            {
                this.setCurationStatus((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(NamespaceEntryImpl.getNamespaceAuthority()))
            {
                this.setAuthority(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(NamespaceEntryImpl.getNamespacePreferredPrefix())
                    || nextStatement.getPredicate().equals(NamespaceEntryImpl.oldNamespaceTitle))
            {
                if(this.getPreferredPrefix().trim().equals(""))
                {
                    this.setPreferredPrefix(nextStatement.getObject().stringValue());
                }
                else
                {
                    NamespaceEntryImpl.log.error("NamespaceEntry.fromRdf: found two preferred prefixes keyToUse="
                            + keyToUse + " .... chosen=" + this.getPreferredPrefix() + " other="
                            + nextStatement.getObject().stringValue());
                }
            }
            else if(nextStatement.getPredicate().equals(NamespaceEntryImpl.getNamespaceAlternativePrefix()))
            {
                tempAlternativePrefixes.add(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(NamespaceEntryImpl.getNamespaceDescription())
                    || nextStatement.getPredicate().equals(RDFS.COMMENT))
            {
                this.setDescription(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(NamespaceEntryImpl.getNamespaceIdentifierRegex()))
            {
                this.setIdentifierRegex(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(NamespaceEntryImpl.getNamespaceUriTemplate()))
            {
                this.setUriTemplate(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(NamespaceEntryImpl.getNamespaceSeparator()))
            {
                this.setSeparator(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(
                    NamespaceEntryImpl.getNamespaceConvertQueriesToPreferredPrefix()))
            {
                this.setConvertQueriesToPreferredPrefix(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        this.setAlternativePrefixes(tempAlternativePrefixes);
        
        if(NamespaceEntryImpl._TRACE)
        {
            NamespaceEntryImpl.log.trace("NamespaceEntry.fromRdf: would have returned... result=" + this.toString());
        }
    }
    
    @Override
    public void addUnrecognisedStatement(final Statement unrecognisedStatement)
    {
        this.unrecognisedStatements.add(unrecognisedStatement);
    }
    
    @Override
    public int compareTo(final NamespaceEntry otherNamespace)
    {
        @SuppressWarnings("unused")
        final int BEFORE = -1;
        final int EQUAL = 0;
        @SuppressWarnings("unused")
        final int AFTER = 1;
        
        if(this == otherNamespace)
        {
            return EQUAL;
        }
        
        return this.getPreferredPrefix().compareTo(otherNamespace.getPreferredPrefix());
    }
    
    @Override
    public Collection<String> getAlternativePrefixes()
    {
        return this.alternativePrefixes;
    }
    
    @Override
    public String getAuthority()
    {
        return this.authority;
    }
    
    @Override
    public boolean getConvertQueriesToPreferredPrefix()
    {
        return this.convertQueriesToPreferredPrefix;
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
        return this.defaultNamespace;
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
        results.add(NamespaceEntryImpl.getNamespaceTypeUri());
        
        return results;
    }
    
    @Override
    public String getIdentifierRegex()
    {
        return this.identifierRegex;
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
    public String getPreferredPrefix()
    {
        return this.preferredPrefix;
    }
    
    @Override
    public String getSeparator()
    {
        return this.separator;
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
    public String getUriTemplate()
    {
        return this.uriTemplate;
    }
    
    @Override
    public void setAlternativePrefixes(final Collection<String> alternativePrefixes)
    {
        this.alternativePrefixes = alternativePrefixes;
    }
    
    @Override
    public void setAuthority(final String authority)
    {
        this.authority = authority;
    }
    
    @Override
    public void setConvertQueriesToPreferredPrefix(final boolean convertQueriesToPreferredPrefix)
    {
        this.convertQueriesToPreferredPrefix = convertQueriesToPreferredPrefix;
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
    
    @Override
    public void setIdentifierRegex(final String identifierRegex)
    {
        this.identifierRegex = identifierRegex;
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
    public void setPreferredPrefix(final String preferredPrefix)
    {
        this.preferredPrefix = preferredPrefix;
    }
    
    @Override
    public void setSeparator(final String separator)
    {
        this.separator = separator;
    }
    
    @Override
    public void setTitle(final String title)
    {
        this.title = title;
    }
    
    @Override
    public void setUriTemplate(final String uriTemplate)
    {
        this.uriTemplate = uriTemplate;
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
        
        final String prefix = "namespace_";
        
        sb.append("<div class=\"" + prefix + "preferredPrefix_div\"><span class=\"" + prefix
                + "preferredPrefix_span\">Prefix:</span><input type=\"text\" name=\"" + prefix
                + "preferredPrefix\" value=\"" + StringUtils.xmlEncodeString(this.getPreferredPrefix())
                + "\" /></div>\n");
        sb.append("<div class=\"" + prefix + "description_div\"><span class=\"" + prefix
                + "description_span\">Description:</span><input type=\"text\" name=\"" + prefix
                + "description\" value=\"" + StringUtils.xmlEncodeString(this.getDescription()) + "\" /></div>\n");
        sb.append("<div class=\"" + prefix + "identifierRegex_div\"><span class=\"" + prefix
                + "identifierRegex_span\">Namespace identifier regular expression:</span><input type=\"text\" name=\""
                + prefix + "identifierRegex\" value=\"" + StringUtils.xmlEncodeString(this.getIdentifierRegex())
                + "\" /></div>\n");
        
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
            final URI namespaceInstanceUri = this.getKey();
            
            if(NamespaceEntryImpl._DEBUG)
            {
                NamespaceEntryImpl.log.debug("NamespaceEntry.toRdf: about create instance URI");
                // log.debug("NamespaceEntry.toRdf: keyToUse="+keyToUse);
                // log.debug("NamespaceEntry.toRdf: preferredPrefix="+getPreferredPrefix());
            }
            
            final Literal preferredPrefixLiteral = f.createLiteral(this.getPreferredPrefix());
            
            URI authorityLiteral = null;
            
            if(this.getAuthority() == null || this.getAuthority().trim().equals(""))
            {
                authorityLiteral = f.createURI(Settings.getSettings().getDefaultHostAddress());
            }
            else
            {
                authorityLiteral = f.createURI(this.getAuthority());
            }
            
            URI curationStatusLiteral = null;
            
            if(this.curationStatus == null)
            {
                curationStatusLiteral = ProjectImpl.getProjectNotCuratedUri();
            }
            else
            {
                curationStatusLiteral = this.curationStatus;
            }
            
            final Literal descriptionLiteral = f.createLiteral(this.getDescription());
            
            final Literal identifierRegexLiteral = f.createLiteral(this.getIdentifierRegex());
            
            final Literal convertQueriesToPreferredPrefixLiteral =
                    f.createLiteral(this.getConvertQueriesToPreferredPrefix());
            
            final Literal uriTemplateLiteral = f.createLiteral(this.getUriTemplate());
            final Literal separatorLiteral = f.createLiteral(this.getSeparator());
            
            if(NamespaceEntryImpl._TRACE)
            {
                NamespaceEntryImpl.log.trace("NamespaceEntry.toRdf: about to add URI's to connection");
            }
            
            con.setAutoCommit(false);
            
            con.add(namespaceInstanceUri, RDF.TYPE, NamespaceEntryImpl.getNamespaceTypeUri(), keyToUse);
            con.add(namespaceInstanceUri, NamespaceEntryImpl.getNamespaceAuthority(), authorityLiteral, keyToUse);
            con.add(namespaceInstanceUri, NamespaceEntryImpl.getNamespacePreferredPrefix(), preferredPrefixLiteral,
                    keyToUse);
            con.add(namespaceInstanceUri, NamespaceEntryImpl.getNamespaceConvertQueriesToPreferredPrefix(),
                    convertQueriesToPreferredPrefixLiteral, keyToUse);
            con.add(namespaceInstanceUri, NamespaceEntryImpl.getNamespaceIdentifierRegex(), identifierRegexLiteral,
                    keyToUse);
            con.add(namespaceInstanceUri, NamespaceEntryImpl.getNamespaceSeparator(), separatorLiteral, keyToUse);
            con.add(namespaceInstanceUri, NamespaceEntryImpl.getNamespaceUriTemplate(), uriTemplateLiteral, keyToUse);
            con.add(namespaceInstanceUri, ProjectImpl.getProjectCurationStatusUri(), curationStatusLiteral, keyToUse);
            if(modelVersion == 1)
            {
                con.add(namespaceInstanceUri, NamespaceEntryImpl.getNamespaceDescription(), descriptionLiteral,
                        keyToUse);
            }
            else
            {
                con.add(namespaceInstanceUri, RDFS.COMMENT, descriptionLiteral, keyToUse);
            }
            
            if(this.getAlternativePrefixes() != null)
            {
                for(final String nextAlternativePrefix : this.getAlternativePrefixes())
                {
                    con.add(namespaceInstanceUri, NamespaceEntryImpl.getNamespaceAlternativePrefix(),
                            f.createLiteral(nextAlternativePrefix), keyToUse);
                }
            }
            
            if(this.unrecognisedStatements != null)
            {
                
                for(final Statement nextUnrecognisedStatement : this.unrecognisedStatements)
                {
                    con.add(nextUnrecognisedStatement);
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
            
            NamespaceEntryImpl.log.error("RepositoryException: " + re.getMessage());
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
        sb.append("preferredPrefix=" + this.getPreferredPrefix() + "\n");
        sb.append("description=" + this.getDescription() + "\n");
        
        return sb.toString();
    }
    
}
