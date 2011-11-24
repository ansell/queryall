/**
 * 
 */
package org.queryall.api.namespace;

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
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllSchema.class)
public class RegexValidatingNamespaceEntrySchema extends QueryAllSchema
{
    private static final Logger log = LoggerFactory.getLogger(RegexValidatingNamespaceEntrySchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = RegexValidatingNamespaceEntrySchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = RegexValidatingNamespaceEntrySchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RegexValidatingNamespaceEntrySchema.log.isInfoEnabled();
    
    private static URI regexValidatingNamespaceTypeUri;
    private static URI namespaceIdentifierRegex;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        RegexValidatingNamespaceEntrySchema.setRegexValidatingNamespaceTypeUri(f.createURI(
                QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(), "RegexValidatingNamespace"));
        RegexValidatingNamespaceEntrySchema.setNamespaceIdentifierRegex(f.createURI(
                QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(), "identifierRegex"));
    }
    
    public static final QueryAllSchema REGEX_VALIDATING_NAMESPACE_ENTRY_SCHEMA =
            new RegexValidatingNamespaceEntrySchema();
    
    /**
     * @return the namespaceIdentifierRegex
     */
    public static URI getNamespaceIdentifierRegex()
    {
        return RegexValidatingNamespaceEntrySchema.namespaceIdentifierRegex;
    }
    
    /**
     * @return the regexValidatingNamespaceTypeUri
     */
    public static URI getRegexValidatingNamespaceTypeUri()
    {
        return RegexValidatingNamespaceEntrySchema.regexValidatingNamespaceTypeUri;
    }
    
    /**
     * @param namespaceIdentifierRegex
     *            the namespaceIdentifierRegex to set
     */
    public static void setNamespaceIdentifierRegex(final URI namespaceIdentifierRegex)
    {
        RegexValidatingNamespaceEntrySchema.namespaceIdentifierRegex = namespaceIdentifierRegex;
    }
    
    /**
     * @param regexValidatingNamespaceTypeUri
     *            the regexValidatingNamespaceTypeUri to set
     */
    public static void setRegexValidatingNamespaceTypeUri(final URI namespaceTypeUri)
    {
        RegexValidatingNamespaceEntrySchema.regexValidatingNamespaceTypeUri = namespaceTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name
     */
    public RegexValidatingNamespaceEntrySchema()
    {
        this(RegexValidatingNamespaceEntrySchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public RegexValidatingNamespaceEntrySchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            final URI contextKeyUri = contextUri;
            con.setAutoCommit(false);
            
            con.add(RegexValidatingNamespaceEntrySchema.getRegexValidatingNamespaceTypeUri(), RDF.TYPE, OWL.CLASS,
                    contextKeyUri);
            
            con.add(RegexValidatingNamespaceEntrySchema.getNamespaceIdentifierRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextKeyUri);
            con.add(RegexValidatingNamespaceEntrySchema.getNamespaceIdentifierRegex(), RDFS.RANGE, RDFS.LITERAL,
                    contextKeyUri);
            con.add(RegexValidatingNamespaceEntrySchema.getNamespaceIdentifierRegex(), RDFS.DOMAIN,
                    RegexValidatingNamespaceEntrySchema.getRegexValidatingNamespaceTypeUri(), contextKeyUri);
            con.add(RegexValidatingNamespaceEntrySchema.getNamespaceIdentifierRegex(),
                    RDFS.LABEL,
                    f.createLiteral("This namespace contains valid identifiers that match this regex. It may be used to identify before querying, whether this namespace and identifier combination is actually relevant."),
                    contextKeyUri);
            
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
            
            RegexValidatingNamespaceEntrySchema.log.error("RepositoryException: " + re.getMessage());
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
