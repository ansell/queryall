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
public class NamespaceEntrySchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(NamespaceEntrySchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = NamespaceEntrySchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = NamespaceEntrySchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = NamespaceEntrySchema.LOG.isInfoEnabled();
    
    private static URI namespaceTypeUri;
    private static URI namespaceAuthority;
    private static URI oldNamespaceTitle;
    private static URI namespacePreferredPrefix;
    private static URI namespaceAlternativePrefix;
    private static URI namespaceDescription;
    private static URI namespaceConvertQueriesToPreferredPrefix;
    private static URI namespaceUriTemplate;
    private static URI namespaceSeparator;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        NamespaceEntrySchema.setNamespaceTypeUri(f.createURI(QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(),
                "Namespace"));
        NamespaceEntrySchema.setNamespaceAuthority(f.createURI(QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(),
                "authority"));
        NamespaceEntrySchema.setNamespacePreferredPrefix(f.createURI(QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(),
                "preferredPrefix"));
        NamespaceEntrySchema.setNamespaceAlternativePrefix(f.createURI(QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(),
                "alternativePrefix"));
        NamespaceEntrySchema.setNamespaceConvertQueriesToPreferredPrefix(f.createURI(
                QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(), "convertToPreferred"));
        NamespaceEntrySchema.setNamespaceDescription(f.createURI(QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(),
                "description"));
        NamespaceEntrySchema.setNamespaceUriTemplate(f.createURI(QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(),
                "uriTemplate"));
        NamespaceEntrySchema.setNamespaceSeparator(f.createURI(QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(),
                "separator"));
        NamespaceEntrySchema.setOldNamespaceTitle(f.createURI(QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(), "title"));
    }
    
    public static final QueryAllSchema NAMESPACE_ENTRY_SCHEMA = new NamespaceEntrySchema();
    
    /**
     * @return the namespaceAlternativePrefix
     */
    public static URI getNamespaceAlternativePrefix()
    {
        return NamespaceEntrySchema.namespaceAlternativePrefix;
    }
    
    /**
     * @return the namespaceAuthority
     */
    public static URI getNamespaceAuthority()
    {
        return NamespaceEntrySchema.namespaceAuthority;
    }
    
    /**
     * @return the namespaceConvertQueriesToPreferredPrefix
     */
    public static URI getNamespaceConvertQueriesToPreferredPrefix()
    {
        return NamespaceEntrySchema.namespaceConvertQueriesToPreferredPrefix;
    }
    
    /**
     * @return the namespaceDescription
     */
    public static URI getNamespaceDescription()
    {
        return NamespaceEntrySchema.namespaceDescription;
    }
    
    /**
     * @return the namespacePreferredPrefix
     */
    public static URI getNamespacePreferredPrefix()
    {
        return NamespaceEntrySchema.namespacePreferredPrefix;
    }
    
    /**
     * @return the namespaceSeparator
     */
    public static URI getNamespaceSeparator()
    {
        return NamespaceEntrySchema.namespaceSeparator;
    }
    
    /**
     * @return the namespaceTypeUri
     */
    public static URI getNamespaceTypeUri()
    {
        return NamespaceEntrySchema.namespaceTypeUri;
    }
    
    /**
     * @return the namespaceUriTemplate
     */
    public static URI getNamespaceUriTemplate()
    {
        return NamespaceEntrySchema.namespaceUriTemplate;
    }
    
    /**
     * @return the oldNamespaceTitle
     */
    public static URI getOldNamespaceTitle()
    {
        return NamespaceEntrySchema.oldNamespaceTitle;
    }
    
    /**
     * @param nextNamespaceAlternativePrefix
     *            the namespaceAlternativePrefix to set
     */
    public static void setNamespaceAlternativePrefix(final URI nextNamespaceAlternativePrefix)
    {
        NamespaceEntrySchema.namespaceAlternativePrefix = nextNamespaceAlternativePrefix;
    }
    
    /**
     * @param nextNamespaceAuthority
     *            the namespaceAuthority to set
     */
    public static void setNamespaceAuthority(final URI nextNamespaceAuthority)
    {
        NamespaceEntrySchema.namespaceAuthority = nextNamespaceAuthority;
    }
    
    /**
     * @param nextNamespaceConvertQueriesToPreferredPrefix
     *            the namespaceConvertQueriesToPreferredPrefix to set
     */
    public static void setNamespaceConvertQueriesToPreferredPrefix(
            final URI nextNamespaceConvertQueriesToPreferredPrefix)
    {
        NamespaceEntrySchema.namespaceConvertQueriesToPreferredPrefix = nextNamespaceConvertQueriesToPreferredPrefix;
    }
    
    /**
     * @param nextNamespaceDescription
     *            the namespaceDescription to set
     */
    public static void setNamespaceDescription(final URI nextNamespaceDescription)
    {
        NamespaceEntrySchema.namespaceDescription = nextNamespaceDescription;
    }
    
    /**
     * @param nextNamespacePreferredPrefix
     *            the namespacePreferredPrefix to set
     */
    public static void setNamespacePreferredPrefix(final URI nextNamespacePreferredPrefix)
    {
        NamespaceEntrySchema.namespacePreferredPrefix = nextNamespacePreferredPrefix;
    }
    
    /**
     * @param nextNamespaceSeparator
     *            the namespaceSeparator to set
     */
    public static void setNamespaceSeparator(final URI nextNamespaceSeparator)
    {
        NamespaceEntrySchema.namespaceSeparator = nextNamespaceSeparator;
    }
    
    /**
     * @param nextNamespaceTypeUri
     *            the namespaceTypeUri to set
     */
    public static void setNamespaceTypeUri(final URI nextNamespaceTypeUri)
    {
        NamespaceEntrySchema.namespaceTypeUri = nextNamespaceTypeUri;
    }
    
    /**
     * @param nextNamespaceUriTemplate
     *            the namespaceUriTemplate to set
     */
    public static void setNamespaceUriTemplate(final URI nextNamespaceUriTemplate)
    {
        NamespaceEntrySchema.namespaceUriTemplate = nextNamespaceUriTemplate;
    }
    
    /**
     * @param nextOldNamespaceTitle
     *            the oldNamespaceTitle to set
     */
    public static void setOldNamespaceTitle(final URI nextOldNamespaceTitle)
    {
        NamespaceEntrySchema.oldNamespaceTitle = nextOldNamespaceTitle;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public NamespaceEntrySchema()
    {
        this(NamespaceEntrySchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public NamespaceEntrySchema(final String nextName)
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
            
            con.add(NamespaceEntrySchema.getNamespaceTypeUri(), RDF.TYPE, OWL.CLASS, contexts);
            con.add(NamespaceEntrySchema.getNamespaceTypeUri(), RDFS.SUBCLASSOF,
                    f.createURI(Constants.COIN_BASE_URI, "URISpace"), contexts);
            
            con.add(NamespaceEntrySchema.getNamespacePreferredPrefix(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(NamespaceEntrySchema.getNamespacePreferredPrefix(), RDFS.SUBPROPERTYOF, Constants.DC_TITLE,
                    contexts);
            con.add(NamespaceEntrySchema.getNamespacePreferredPrefix(), RDFS.SUBPROPERTYOF, RDFS.LABEL, contexts);
            con.add(NamespaceEntrySchema.getNamespacePreferredPrefix(), RDFS.SUBPROPERTYOF, Constants.SKOS_PREFLABEL,
                    contexts);
            con.add(NamespaceEntrySchema.getNamespacePreferredPrefix(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(NamespaceEntrySchema.getNamespacePreferredPrefix(), RDFS.DOMAIN,
                    NamespaceEntrySchema.getNamespaceTypeUri(), contexts);
            con.add(NamespaceEntrySchema.getNamespacePreferredPrefix(), RDFS.LABEL,
                    f.createLiteral("This property defines the preferred prefix, and the label for this namespace."),
                    contexts);
            
            con.add(NamespaceEntrySchema.getNamespaceAlternativePrefix(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(NamespaceEntrySchema.getNamespaceAlternativePrefix(), RDFS.SUBPROPERTYOF, Constants.SKOS_ALTLABEL,
                    contexts);
            con.add(NamespaceEntrySchema.getNamespaceAlternativePrefix(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(NamespaceEntrySchema.getNamespaceAlternativePrefix(), RDFS.DOMAIN,
                    NamespaceEntrySchema.getNamespaceTypeUri(), contexts);
            con.add(NamespaceEntrySchema.getNamespaceAlternativePrefix(), RDFS.LABEL,
                    f.createLiteral("A range of alternative prefixes for this namespace."), contexts);
            
            con.add(NamespaceEntrySchema.getNamespaceAuthority(), RDF.TYPE, OWL.OBJECTPROPERTY, contexts);
            con.add(NamespaceEntrySchema.getNamespaceAuthority(), RDFS.RANGE, RDFS.RESOURCE, contexts);
            con.add(NamespaceEntrySchema.getNamespaceAuthority(), RDFS.DOMAIN,
                    NamespaceEntrySchema.getNamespaceTypeUri(), contexts);
            con.add(NamespaceEntrySchema.getNamespaceAuthority(),
                    RDFS.LABEL,
                    f.createLiteral("This namespace is controlled by this authority, although the authority may represent a community."),
                    contexts);
            
            con.add(NamespaceEntrySchema.getNamespaceConvertQueriesToPreferredPrefix(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contexts);
            con.add(NamespaceEntrySchema.getNamespaceConvertQueriesToPreferredPrefix(), RDFS.RANGE, RDFS.LITERAL,
                    contexts);
            con.add(NamespaceEntrySchema.getNamespaceConvertQueriesToPreferredPrefix(), RDFS.DOMAIN,
                    NamespaceEntrySchema.getNamespaceTypeUri(), contexts);
            con.add(NamespaceEntrySchema.getNamespaceConvertQueriesToPreferredPrefix(),
                    RDFS.LABEL,
                    f.createLiteral("If this property is defined as true, then alternative prefixes that match this namespace should be converted to the preferred prefix."),
                    contexts);
            
            con.add(NamespaceEntrySchema.getNamespaceUriTemplate(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(NamespaceEntrySchema.getNamespaceUriTemplate(), RDFS.SUBPROPERTYOF,
                    f.createURI(Constants.COIN_BASE_URI, "uriTemplate"), contexts);
            con.add(NamespaceEntrySchema.getNamespaceUriTemplate(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(NamespaceEntrySchema.getNamespaceUriTemplate(), RDFS.DOMAIN,
                    NamespaceEntrySchema.getNamespaceTypeUri(), contexts);
            con.add(NamespaceEntrySchema.getNamespaceUriTemplate(),
                    RDFS.LABEL,
                    f.createLiteral("This template combines the authority, prefix, separator and identifier templates to create the desired URI structure for this namespace."),
                    contexts);
            
            con.add(NamespaceEntrySchema.getNamespaceSeparator(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(NamespaceEntrySchema.getNamespaceSeparator(), RDFS.SUBPROPERTYOF,
                    f.createURI(Constants.COIN_BASE_URI, "fragmentSeparator"), contexts);
            con.add(NamespaceEntrySchema.getNamespaceSeparator(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(NamespaceEntrySchema.getNamespaceSeparator(), RDFS.DOMAIN,
                    NamespaceEntrySchema.getNamespaceTypeUri(), contexts);
            con.add(NamespaceEntrySchema.getNamespaceSeparator(),
                    RDFS.LABEL,
                    f.createLiteral("This value is used as the separator between the namespace and the identifier portion of the URI."),
                    contexts);
            
            if(modelVersion == 1)
            {
                con.add(NamespaceEntrySchema.getNamespaceDescription(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
                con.add(NamespaceEntrySchema.getNamespaceDescription(), RDFS.SUBPROPERTYOF, RDFS.COMMENT, contexts);
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
            
            NamespaceEntrySchema.LOG.error("RepositoryException: " + re.getMessage());
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
