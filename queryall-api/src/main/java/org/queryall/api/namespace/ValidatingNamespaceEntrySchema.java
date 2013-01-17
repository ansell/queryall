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
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllSchema.class)
public class ValidatingNamespaceEntrySchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(ValidatingNamespaceEntrySchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = ValidatingNamespaceEntrySchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = ValidatingNamespaceEntrySchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = ValidatingNamespaceEntrySchema.LOG.isInfoEnabled();
    
    private static URI validatingNamespaceTypeUri;
    private static URI namespaceValidationPossible;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        ValidatingNamespaceEntrySchema.setValidatingNamespaceTypeUri(f.createURI(
                QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(), "ValidatingNamespace"));
        ValidatingNamespaceEntrySchema.setValidationPossibleUri(f.createURI(
                QueryAllNamespaces.NAMESPACEENTRY.getBaseURI(), "validationPossible"));
    }
    
    /**
     * A pre-instantiated schema object for ValidatingNamespaceEntrySchema.
     */
    public static final QueryAllSchema VALIDATING_NAMESPACE_ENTRY_SCHEMA = new ValidatingNamespaceEntrySchema();
    
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
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public ValidatingNamespaceEntrySchema()
    {
        this(ValidatingNamespaceEntrySchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public ValidatingNamespaceEntrySchema(final String nextName)
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
            
            con.add(ValidatingNamespaceEntrySchema.getValidatingNamespaceTypeUri(), RDF.TYPE, OWL.CLASS, contexts);
            
            con.add(ValidatingNamespaceEntrySchema.getValidationPossibleUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(ValidatingNamespaceEntrySchema.getValidationPossibleUri(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(ValidatingNamespaceEntrySchema.getValidationPossibleUri(), RDFS.DOMAIN,
                    ValidatingNamespaceEntrySchema.getValidatingNamespaceTypeUri(), contexts);
            con.add(ValidatingNamespaceEntrySchema.getValidationPossibleUri(),
                    RDFS.LABEL,
                    f.createLiteral("If this property is true, then validation is possible for identifiers in this namespace."),
                    contexts);
            
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
            
            ValidatingNamespaceEntrySchema.LOG.error("RepositoryException: " + re.getMessage());
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
