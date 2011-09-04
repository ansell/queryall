/**
 * 
 */
package org.queryall.api.namespace;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class NamespaceEntrySchema
{
    private static final Logger log = LoggerFactory.getLogger(NamespaceEntrySchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = NamespaceEntrySchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = NamespaceEntrySchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = NamespaceEntrySchema.log.isInfoEnabled();
    
    private static URI namespaceTypeUri;
    private static URI namespaceAuthority;
    private static URI namespaceIdentifierRegex;
    private static URI oldNamespaceTitle;
    private static URI namespacePreferredPrefix;
    private static URI namespaceAlternativePrefix;
    private static URI namespaceDescription;
    private static URI namespaceConvertQueriesToPreferredPrefix;
    private static URI namespaceUriTemplate;
    private static URI namespaceSeparator;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        NamespaceEntrySchema.setNamespaceTypeUri(f.createURI(QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(),
                "Namespace"));
        NamespaceEntrySchema.setNamespaceAuthority(f.createURI(QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(),
                "authority"));
        NamespaceEntrySchema.setNamespaceIdentifierRegex(f.createURI(QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(),
                "identifierRegex"));
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
     * @return the namespaceIdentifierRegex
     */
    public static URI getNamespaceIdentifierRegex()
    {
        return NamespaceEntrySchema.namespaceIdentifierRegex;
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
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            final URI contextKeyUri = contextUri;
            con.setAutoCommit(false);
            
            con.add(NamespaceEntrySchema.getNamespaceTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            
            con.add(NamespaceEntrySchema.getNamespacePreferredPrefix(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespacePreferredPrefix(), RDFS.SUBPROPERTYOF, Constants.DC_TITLE,
                    contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespacePreferredPrefix(), RDFS.SUBPROPERTYOF, RDFS.LABEL, contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespacePreferredPrefix(), RDFS.SUBPROPERTYOF, Constants.SKOS_PREFLABEL,
                    contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespacePreferredPrefix(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespacePreferredPrefix(), RDFS.DOMAIN,
                    NamespaceEntrySchema.getNamespaceTypeUri(), contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespacePreferredPrefix(), RDFS.LABEL,
                    f.createLiteral("This property defines the preferred prefix, and the label for this namespace."),
                    contextKeyUri);
            
            con.add(NamespaceEntrySchema.getNamespaceAlternativePrefix(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceAlternativePrefix(), RDFS.SUBPROPERTYOF, Constants.SKOS_ALTLABEL,
                    contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceAlternativePrefix(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceAlternativePrefix(), RDFS.DOMAIN,
                    NamespaceEntrySchema.getNamespaceTypeUri(), contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceAlternativePrefix(), RDFS.LABEL,
                    f.createLiteral("A range of alternative prefixes for this namespace."), contextKeyUri);
            
            con.add(NamespaceEntrySchema.getNamespaceAuthority(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceAuthority(), RDFS.RANGE, RDFS.RESOURCE, contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceAuthority(), RDFS.DOMAIN,
                    NamespaceEntrySchema.getNamespaceTypeUri(), contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceAuthority(),
                    RDFS.LABEL,
                    f.createLiteral("This namespace is controlled by this authority, although the authority may represent a community."),
                    contextKeyUri);
            
            con.add(NamespaceEntrySchema.getNamespaceIdentifierRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceIdentifierRegex(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceIdentifierRegex(), RDFS.DOMAIN,
                    NamespaceEntrySchema.getNamespaceTypeUri(), contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceIdentifierRegex(),
                    RDFS.LABEL,
                    f.createLiteral("This namespace contains valid identifiers that match this regex. It may be used to identify before querying, whether this namespace and identifier combination is actually relevant."),
                    contextKeyUri);
            
            con.add(NamespaceEntrySchema.getNamespaceConvertQueriesToPreferredPrefix(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceConvertQueriesToPreferredPrefix(), RDFS.RANGE, RDFS.LITERAL,
                    contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceConvertQueriesToPreferredPrefix(), RDFS.DOMAIN,
                    NamespaceEntrySchema.getNamespaceTypeUri(), contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceConvertQueriesToPreferredPrefix(),
                    RDFS.LABEL,
                    f.createLiteral("If this property is defined as true, then alternative prefixes that match this namespace should be converted to the preferred prefix."),
                    contextKeyUri);
            
            con.add(NamespaceEntrySchema.getNamespaceUriTemplate(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceUriTemplate(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceUriTemplate(), RDFS.DOMAIN,
                    NamespaceEntrySchema.getNamespaceTypeUri(), contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceUriTemplate(),
                    RDFS.LABEL,
                    f.createLiteral("This template combines the authority, prefix, separator and identifier templates to create the desired URI structure for this namespace."),
                    contextKeyUri);
            
            con.add(NamespaceEntrySchema.getNamespaceSeparator(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceSeparator(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceSeparator(), RDFS.DOMAIN,
                    NamespaceEntrySchema.getNamespaceTypeUri(), contextKeyUri);
            con.add(NamespaceEntrySchema.getNamespaceSeparator(),
                    RDFS.LABEL,
                    f.createLiteral("This value is used as the separator between the namespace and the identifier portion of the URI."),
                    contextKeyUri);
            
            if(modelVersion == 1)
            {
                con.add(NamespaceEntrySchema.getNamespaceDescription(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
                con.add(NamespaceEntrySchema.getNamespaceDescription(), RDFS.SUBPROPERTYOF, RDFS.COMMENT, contextKeyUri);
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
            
            NamespaceEntrySchema.log.error("RepositoryException: " + re.getMessage());
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
        NamespaceEntrySchema.namespaceAlternativePrefix = namespaceAlternativePrefix;
    }
    
    /**
     * @param namespaceAuthority
     *            the namespaceAuthority to set
     */
    public static void setNamespaceAuthority(final URI namespaceAuthority)
    {
        NamespaceEntrySchema.namespaceAuthority = namespaceAuthority;
    }
    
    /**
     * @param namespaceConvertQueriesToPreferredPrefix
     *            the namespaceConvertQueriesToPreferredPrefix to set
     */
    public static void setNamespaceConvertQueriesToPreferredPrefix(final URI namespaceConvertQueriesToPreferredPrefix)
    {
        NamespaceEntrySchema.namespaceConvertQueriesToPreferredPrefix = namespaceConvertQueriesToPreferredPrefix;
    }
    
    /**
     * @param namespaceDescription
     *            the namespaceDescription to set
     */
    public static void setNamespaceDescription(final URI namespaceDescription)
    {
        NamespaceEntrySchema.namespaceDescription = namespaceDescription;
    }
    
    /**
     * @param namespaceIdentifierRegex
     *            the namespaceIdentifierRegex to set
     */
    public static void setNamespaceIdentifierRegex(final URI namespaceIdentifierRegex)
    {
        NamespaceEntrySchema.namespaceIdentifierRegex = namespaceIdentifierRegex;
    }
    
    /**
     * @param namespacePreferredPrefix
     *            the namespacePreferredPrefix to set
     */
    public static void setNamespacePreferredPrefix(final URI namespacePreferredPrefix)
    {
        NamespaceEntrySchema.namespacePreferredPrefix = namespacePreferredPrefix;
    }
    
    /**
     * @param namespaceSeparator
     *            the namespaceSeparator to set
     */
    public static void setNamespaceSeparator(final URI namespaceSeparator)
    {
        NamespaceEntrySchema.namespaceSeparator = namespaceSeparator;
    }
    
    /**
     * @param namespaceTypeUri
     *            the namespaceTypeUri to set
     */
    public static void setNamespaceTypeUri(final URI namespaceTypeUri)
    {
        NamespaceEntrySchema.namespaceTypeUri = namespaceTypeUri;
    }
    
    /**
     * @param namespaceUriTemplate
     *            the namespaceUriTemplate to set
     */
    public static void setNamespaceUriTemplate(final URI namespaceUriTemplate)
    {
        NamespaceEntrySchema.namespaceUriTemplate = namespaceUriTemplate;
    }
    
    /**
     * @param oldNamespaceTitle
     *            the oldNamespaceTitle to set
     */
    public static void setOldNamespaceTitle(final URI oldNamespaceTitle)
    {
        NamespaceEntrySchema.oldNamespaceTitle = oldNamespaceTitle;
    }
    
}
