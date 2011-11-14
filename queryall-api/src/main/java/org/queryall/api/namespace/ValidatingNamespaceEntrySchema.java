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
import org.queryall.api.base.QueryAllSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ValidatingNamespaceEntrySchema implements QueryAllSchema
{
    private static final Logger log = LoggerFactory.getLogger(ValidatingNamespaceEntrySchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = ValidatingNamespaceEntrySchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = ValidatingNamespaceEntrySchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = ValidatingNamespaceEntrySchema.log.isInfoEnabled();
    
    private static URI validatingNamespaceTypeUri;
    private static URI namespaceValidationPossible;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        ValidatingNamespaceEntrySchema.setValidatingNamespaceTypeUri(f.createURI(
                QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(), "ValidatingNamespace"));
        ValidatingNamespaceEntrySchema.setValidationPossibleUri(f.createURI(
                QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(), "validationPossible"));
    }
    
    /**
     * @return the validatingNamespaceTypeUri
     */
    public static URI getValidatingNamespaceTypeUri()
    {
        return ValidatingNamespaceEntrySchema.validatingNamespaceTypeUri;
    }
    
    /**
     * @return the namespaceValidationPossible
     */
    public static URI getValidationPossibleUri()
    {
        return ValidatingNamespaceEntrySchema.namespaceValidationPossible;
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
            
            con.add(ValidatingNamespaceEntrySchema.getValidatingNamespaceTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            
            con.add(ValidatingNamespaceEntrySchema.getValidationPossibleUri(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextKeyUri);
            con.add(ValidatingNamespaceEntrySchema.getValidationPossibleUri(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(ValidatingNamespaceEntrySchema.getValidationPossibleUri(), RDFS.DOMAIN,
                    ValidatingNamespaceEntrySchema.getValidatingNamespaceTypeUri(), contextKeyUri);
            con.add(ValidatingNamespaceEntrySchema.getValidationPossibleUri(),
                    RDFS.LABEL,
                    f.createLiteral("If this property is true, then validation is possible for identifiers in this namespace."),
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
            
            ValidatingNamespaceEntrySchema.log.error("RepositoryException: " + re.getMessage());
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
     * @param validatingNamespaceTypeUri
     *            the validatingNamespaceTypeUri to set
     */
    public static void setValidatingNamespaceTypeUri(final URI namespaceTypeUri)
    {
        ValidatingNamespaceEntrySchema.validatingNamespaceTypeUri = namespaceTypeUri;
    }
    
    /**
     * @param namespaceValidationPossible
     *            the namespaceValidationPossible to set
     */
    public static void setValidationPossibleUri(final URI namespaceIdentifierRegex)
    {
        ValidatingNamespaceEntrySchema.namespaceValidationPossible = namespaceIdentifierRegex;
    }
    
}
