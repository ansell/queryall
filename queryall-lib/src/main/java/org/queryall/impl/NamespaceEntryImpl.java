package org.queryall.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.NamespaceEntry;
import org.queryall.api.NamespaceEntrySchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class NamespaceEntryImpl implements NamespaceEntry
{
    private static final Logger log = LoggerFactory.getLogger(NamespaceEntryImpl.class);
    private static final boolean _TRACE = NamespaceEntryImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = NamespaceEntryImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = NamespaceEntryImpl.log.isInfoEnabled();
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key;
    
    private URI authority;
    
    private URI curationStatus = ProjectImpl.getProjectNotCuratedUri();
    
    private String preferredPrefix = "";
    
    private Collection<String> alternativePrefixes = new HashSet<String>();
    
    private String description = "";
    
    private String identifierRegex = "";
    
    private String uriTemplate = "";
    
    private String separator = "";
    
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
                    && nextStatement.getObject().equals(NamespaceEntrySchema.getNamespaceTypeUri()))
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
            else if(nextStatement.getPredicate().equals(NamespaceEntrySchema.getNamespaceAuthority()))
            {
                this.setAuthority((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(NamespaceEntrySchema.getNamespacePreferredPrefix())
                    || nextStatement.getPredicate().equals(NamespaceEntrySchema.getOldNamespaceTitle()))
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
            else if(nextStatement.getPredicate().equals(NamespaceEntrySchema.getNamespaceAlternativePrefix()))
            {
                tempAlternativePrefixes.add(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(NamespaceEntrySchema.getNamespaceDescription())
                    || nextStatement.getPredicate().equals(RDFS.COMMENT))
            {
                this.setDescription(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(NamespaceEntrySchema.getNamespaceIdentifierRegex()))
            {
                this.setIdentifierRegex(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(NamespaceEntrySchema.getNamespaceUriTemplate()))
            {
                this.setUriTemplate(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(NamespaceEntrySchema.getNamespaceSeparator()))
            {
                this.setSeparator(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(
                    NamespaceEntrySchema.getNamespaceConvertQueriesToPreferredPrefix()))
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
    public URI getAuthority()
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
    public QueryAllNamespaces getDefaultNamespace()
    {
        return QueryAllNamespaces.NAMESPACEENTRY;
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
        results.add(NamespaceEntrySchema.getNamespaceTypeUri());
        
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
    public void setAuthority(final URI authority)
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
            
            if(this.getAuthority() != null && !this.getAuthority().stringValue().trim().equals(""))
            {
                authorityLiteral = this.getAuthority();
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
            
            con.add(namespaceInstanceUri, RDF.TYPE, NamespaceEntrySchema.getNamespaceTypeUri(), keyToUse);

            if(authorityLiteral != null)
            {
                con.add(namespaceInstanceUri, NamespaceEntrySchema.getNamespaceAuthority(), authorityLiteral, keyToUse);
            }
            
            con.add(namespaceInstanceUri, NamespaceEntrySchema.getNamespacePreferredPrefix(), preferredPrefixLiteral,
                    keyToUse);
            con.add(namespaceInstanceUri, NamespaceEntrySchema.getNamespaceConvertQueriesToPreferredPrefix(),
                    convertQueriesToPreferredPrefixLiteral, keyToUse);
            con.add(namespaceInstanceUri, NamespaceEntrySchema.getNamespaceIdentifierRegex(), identifierRegexLiteral,
                    keyToUse);
            con.add(namespaceInstanceUri, NamespaceEntrySchema.getNamespaceSeparator(), separatorLiteral, keyToUse);
            con.add(namespaceInstanceUri, NamespaceEntrySchema.getNamespaceUriTemplate(), uriTemplateLiteral, keyToUse);
            con.add(namespaceInstanceUri, ProjectImpl.getProjectCurationStatusUri(), curationStatusLiteral, keyToUse);
            if(modelVersion == 1)
            {
                con.add(namespaceInstanceUri, NamespaceEntrySchema.getNamespaceDescription(), descriptionLiteral,
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
                    con.add(namespaceInstanceUri, NamespaceEntrySchema.getNamespaceAlternativePrefix(),
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
        sb.append("preferredPrefix=" + this.getPreferredPrefix() + "\n");
        sb.append("description=" + this.getDescription() + "\n");
        
        return sb.toString();
    }
    
}
