package org.queryall.impl.namespace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.base.HtmlExport;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.namespace.NamespaceEntrySchema;
import org.queryall.api.namespace.RegexValidatingNamespaceEntry;
import org.queryall.api.namespace.RegexValidatingNamespaceEntrySchema;
import org.queryall.api.namespace.ValidatingNamespaceEntry;
import org.queryall.api.namespace.ValidatingNamespaceEntrySchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.impl.base.BaseQueryAllImpl;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class NamespaceEntryImpl extends BaseQueryAllImpl implements NamespaceEntry, RegexValidatingNamespaceEntry,
        HtmlExport
{
    private static final Logger log = LoggerFactory.getLogger(NamespaceEntryImpl.class);
    private static final boolean TRACE = NamespaceEntryImpl.log.isTraceEnabled();
    private static final boolean DEBUG = NamespaceEntryImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = NamespaceEntryImpl.log.isInfoEnabled();
    
    private static final Set<URI> NAMESPACE_ENTRY_IMPL_TYPES = new HashSet<URI>();
    
    public static Set<URI> myTypes()
    {
        return NamespaceEntryImpl.NAMESPACE_ENTRY_IMPL_TYPES;
    }
    
    private URI authority;
    
    private String preferredPrefix = "";
    
    private Collection<String> alternativePrefixes = new ArrayList<String>();
    
    private String identifierRegex = "";
    
    private String uriTemplate = "";
    
    private String separator = "";
    
    // This setting determines whether input namespace prefixes in the alternatives list should be
    // converted to the preferred prefix
    private boolean convertQueriesToPreferredPrefix = true;
    // TODO:
    // It also determines whether owl:sameAs will be used to relate the preferred prefix to each of
    // the alternative prefixes
    
    private Pattern identifierRegexPattern = null;
    private boolean validationPossible = false;
    
    static
    {
        NamespaceEntryImpl.NAMESPACE_ENTRY_IMPL_TYPES.add(NamespaceEntrySchema.getNamespaceTypeUri());
        NamespaceEntryImpl.NAMESPACE_ENTRY_IMPL_TYPES.add(ValidatingNamespaceEntrySchema
                .getValidatingNamespaceTypeUri());
        NamespaceEntryImpl.NAMESPACE_ENTRY_IMPL_TYPES.add(RegexValidatingNamespaceEntrySchema
                .getRegexValidatingNamespaceTypeUri());
    }
    
    public NamespaceEntryImpl()
    {
        
    }
    
    public NamespaceEntryImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = this.resetUnrecognisedStatements();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(NamespaceEntryImpl.TRACE)
            {
                NamespaceEntryImpl.log.trace("NamespaceEntry: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && (nextStatement.getObject().equals(NamespaceEntrySchema.getNamespaceTypeUri())
                            || nextStatement.getObject().equals(
                                    ValidatingNamespaceEntrySchema.getValidatingNamespaceTypeUri()) || nextStatement
                            .getObject().equals(
                                    RegexValidatingNamespaceEntrySchema.getRegexValidatingNamespaceTypeUri())))
            {
                if(NamespaceEntryImpl.TRACE)
                {
                    NamespaceEntryImpl.log.trace("NamespaceEntry: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
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
                this.addAlternativePrefix(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(
                    RegexValidatingNamespaceEntrySchema.getNamespaceIdentifierRegex()))
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
            else if(nextStatement.getPredicate().equals(ValidatingNamespaceEntrySchema.getValidationPossibleUri()))
            {
                this.setValidationPossible(true);
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(NamespaceEntryImpl.TRACE)
        {
            NamespaceEntryImpl.log.trace("NamespaceEntry.fromRdf: would have returned... result=" + this.toString());
        }
    }
    
    @Override
    public void addAlternativePrefix(final String alternativePrefix)
    {
        this.alternativePrefixes.add(alternativePrefix);
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
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(!super.equals(obj))
        {
            return false;
        }
        if(!(obj instanceof NamespaceEntry))
        {
            return false;
        }
        final NamespaceEntry other = (NamespaceEntry)obj;
        if(this.alternativePrefixes == null)
        {
            if(other.getAlternativePrefixes() != null)
            {
                return false;
            }
        }
        else if(!this.getAlternativePrefixes().equals(other.getAlternativePrefixes()))
        {
            return false;
        }
        if(this.getAuthority() == null)
        {
            if(other.getAuthority() != null)
            {
                return false;
            }
        }
        else if(!this.getAuthority().equals(other.getAuthority()))
        {
            return false;
        }
        if(this.getConvertQueriesToPreferredPrefix() != other.getConvertQueriesToPreferredPrefix())
        {
            return false;
        }
        if(this.getPreferredPrefix() == null)
        {
            if(other.getPreferredPrefix() != null)
            {
                return false;
            }
        }
        else if(!this.getPreferredPrefix().equals(other.getPreferredPrefix()))
        {
            return false;
        }
        if(this.getSeparator() == null)
        {
            if(other.getSeparator() != null)
            {
                return false;
            }
        }
        else if(!this.getSeparator().equals(other.getSeparator()))
        {
            return false;
        }
        if(this.getUriTemplate() == null)
        {
            if(other.getUriTemplate() != null)
            {
                return false;
            }
        }
        else if(!this.getUriTemplate().equals(other.getUriTemplate()))
        {
            return false;
        }
        if(other instanceof ValidatingNamespaceEntry)
        {
            final ValidatingNamespaceEntry otherValidating = (ValidatingNamespaceEntry)other;
            if(this.getValidationPossible() != otherValidating.getValidationPossible())
            {
                return false;
            }
            if(otherValidating instanceof RegexValidatingNamespaceEntry)
            {
                final RegexValidatingNamespaceEntry otherRegexValidating = (RegexValidatingNamespaceEntry)other;
                if(this.getIdentifierRegex() == null)
                {
                    if(otherRegexValidating.getIdentifierRegex() != null)
                    {
                        return false;
                    }
                }
                else if(!this.getIdentifierRegex().equals(otherRegexValidating.getIdentifierRegex()))
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
        
        return true;
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
    
    /**
     * @return the namespace used to represent objects of this type by default
     */
    @Override
    public QueryAllNamespaces getDefaultNamespace()
    {
        return QueryAllNamespaces.NAMESPACEENTRY;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Set<URI> getElementTypes()
    {
        return NamespaceEntryImpl.myTypes();
    }
    
    @Override
    public String getIdentifierRegex()
    {
        return this.identifierRegex;
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
    public String getUriTemplate()
    {
        return this.uriTemplate;
    }
    
    @Override
    public boolean getValidationPossible()
    {
        return this.validationPossible;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.alternativePrefixes == null) ? 0 : this.alternativePrefixes.hashCode());
        result = prime * result + ((this.authority == null) ? 0 : this.authority.hashCode());
        result = prime * result + (this.convertQueriesToPreferredPrefix ? 1231 : 1237);
        result = prime * result + ((this.identifierRegex == null) ? 0 : this.identifierRegex.hashCode());
        result = prime * result + ((this.identifierRegexPattern == null) ? 0 : this.identifierRegexPattern.hashCode());
        result = prime * result + ((this.preferredPrefix == null) ? 0 : this.preferredPrefix.hashCode());
        result = prime * result + ((this.separator == null) ? 0 : this.separator.hashCode());
        result = prime * result + ((this.uriTemplate == null) ? 0 : this.uriTemplate.hashCode());
        result = prime * result + (this.validationPossible ? 1231 : 1237);
        return result;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.namespace.NamespaceEntry#resetAlternativePrefixes()
     */
    @Override
    public boolean resetAlternativePrefixes()
    {
        // TODO Auto-generated method stub
        return false;
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
    public void setIdentifierRegex(final String identifierRegex)
    {
        this.identifierRegex = identifierRegex;
        this.identifierRegexPattern = Pattern.compile(identifierRegex);
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
    public void setUriTemplate(final String uriTemplate)
    {
        this.uriTemplate = uriTemplate;
    }
    
    @Override
    public void setValidationPossible(final boolean validationPossible)
    {
        this.validationPossible = validationPossible;
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
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... contextKey)
        throws OpenRDFException
    {
        super.toRdf(myRepository, modelVersion, contextKey);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            final URI namespaceInstanceUri = this.getKey();
            
            final Literal preferredPrefixLiteral = f.createLiteral(this.getPreferredPrefix());
            
            URI authorityLiteral = null;
            
            if(this.getAuthority() != null && !this.getAuthority().stringValue().trim().equals(""))
            {
                authorityLiteral = this.getAuthority();
            }
            
            final Literal descriptionLiteral = f.createLiteral(this.getDescription());
            final Literal identifierRegexLiteral = f.createLiteral(this.getIdentifierRegex());
            final Literal convertQueriesToPreferredPrefixLiteral =
                    f.createLiteral(this.getConvertQueriesToPreferredPrefix());
            final Literal validationPossibleLiteral = f.createLiteral(this.getValidationPossible());
            final Literal uriTemplateLiteral = f.createLiteral(this.getUriTemplate());
            final Literal separatorLiteral = f.createLiteral(this.getSeparator());
            
            con.setAutoCommit(false);
            
            for(final URI nextElementType : this.getElementTypes())
            {
                con.add(namespaceInstanceUri, RDF.TYPE, nextElementType, contextKey);
            }
            
            if(authorityLiteral != null)
            {
                con.add(namespaceInstanceUri, NamespaceEntrySchema.getNamespaceAuthority(), authorityLiteral,
                        contextKey);
            }
            
            con.add(namespaceInstanceUri, NamespaceEntrySchema.getNamespacePreferredPrefix(), preferredPrefixLiteral,
                    contextKey);
            con.add(namespaceInstanceUri, NamespaceEntrySchema.getNamespaceConvertQueriesToPreferredPrefix(),
                    convertQueriesToPreferredPrefixLiteral, contextKey);
            con.add(namespaceInstanceUri, NamespaceEntrySchema.getNamespaceSeparator(), separatorLiteral, contextKey);
            con.add(namespaceInstanceUri, NamespaceEntrySchema.getNamespaceUriTemplate(), uriTemplateLiteral,
                    contextKey);
            
            con.add(namespaceInstanceUri, ValidatingNamespaceEntrySchema.getValidationPossibleUri(),
                    validationPossibleLiteral, contextKey);
            con.add(namespaceInstanceUri, RegexValidatingNamespaceEntrySchema.getNamespaceIdentifierRegex(),
                    identifierRegexLiteral, contextKey);
            
            if(modelVersion == 1)
            {
                con.add(namespaceInstanceUri, NamespaceEntrySchema.getNamespaceDescription(), descriptionLiteral,
                        contextKey);
            }
            
            if(this.getAlternativePrefixes() != null)
            {
                for(final String nextAlternativePrefix : this.getAlternativePrefixes())
                {
                    con.add(namespaceInstanceUri, NamespaceEntrySchema.getNamespaceAlternativePrefix(),
                            f.createLiteral(nextAlternativePrefix), contextKey);
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
        
        sb.append("key=" + this.getKey() + "\n");
        sb.append("preferredPrefix=" + this.getPreferredPrefix() + "\n");
        sb.append("description=" + this.getDescription() + "\n");
        
        return sb.toString();
    }
    
    @Override
    public boolean validateIdentifier(final String identifier)
    {
        // if we can't validate this namespace, assume that all identifiers are valid
        if(!this.validationPossible)
        {
            return true;
        }
        // if we don't have a regex but we can validate this namespace, assume the identifier is
        // invalid
        else if(this.identifierRegexPattern == null)
        {
            NamespaceEntryImpl.log
                    .warn("Validation was possible, but no regular expression was found for the namespace key="
                            + this.getKey().stringValue() + " identifier=" + identifier);
            
            return false;
        }
        else
        {
            return StringUtils.matchesRegexOnString(this.identifierRegexPattern, this.getIdentifierRegex(), identifier);
        }
    }
    
}
