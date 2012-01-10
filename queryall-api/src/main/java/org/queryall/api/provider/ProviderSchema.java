/**
 * 
 */
package org.queryall.api.provider;

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
import org.queryall.api.namespace.NamespaceEntrySchema;
import org.queryall.api.querytype.QueryTypeSchema;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllSchema.class)
public class ProviderSchema extends QueryAllSchema
{
    private static final Logger log = LoggerFactory.getLogger(ProviderSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = ProviderSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = ProviderSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = ProviderSchema.log.isInfoEnabled();
    
    private static URI providerNoCommunication;
    
    private static URI providerTypeUri;
    
    private static URI providerTitle;
    
    private static URI providerResolutionStrategy;
    
    private static URI providerHandlesNamespace;
    
    private static URI providerResolutionMethod;
    
    private static URI providerIncludedInQuery;
    
    private static URI providerIsDefaultSource;
    
    private static URI providerNeedsRdfNormalisation;
    
    private static URI providerRedirect;
    
    private static URI providerProxy;
    
    private static URI providerAssumedContentType;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.PROVIDER.getBaseURI();
        
        ProviderSchema.setProviderTypeUri(f.createURI(baseUri, "Provider"));
        
        ProviderSchema.setProviderResolutionStrategy(f.createURI(baseUri, "resolutionStrategy"));
        ProviderSchema.setProviderHandlesNamespace(f.createURI(baseUri, "handlesNamespace"));
        ProviderSchema.setProviderResolutionMethod(f.createURI(baseUri, "resolutionMethod"));
        ProviderSchema.setProviderIncludedInQuery(f.createURI(baseUri, "includedInQuery"));
        ProviderSchema.setProviderIsDefaultSource(f.createURI(baseUri, "isDefaultSource"));
        ProviderSchema.setProviderNeedsRdfNormalisation(f.createURI(baseUri, "needsRdfNormalisation"));
        ProviderSchema.setProviderRedirect(f.createURI(baseUri, "redirect"));
        ProviderSchema.setProviderProxy(f.createURI(baseUri, "proxy"));
        ProviderSchema.setProviderNoCommunication(f.createURI(baseUri, "nocommunication"));
        ProviderSchema.setProviderAssumedContentType(f.createURI(baseUri, "assumedContentType"));
        
        // NOTE: This was deprecated after API version 1 in favour of dc
        // elements title
        ProviderSchema.setProviderTitle(f.createURI(baseUri, "Title"));
    }
    
    public static final QueryAllSchema PROVIDER_SCHEMA = new ProviderSchema();
    
    /**
     * @return the providerAssumedContentType
     */
    public static URI getProviderAssumedContentType()
    {
        return ProviderSchema.providerAssumedContentType;
    }
    
    /**
     * @return the providerHandledNamespace
     */
    public static URI getProviderHandlesNamespace()
    {
        return ProviderSchema.providerHandlesNamespace;
    }
    
    /**
     * @return the providerIncludedInQuery
     */
    public static URI getProviderIncludedInQuery()
    {
        return ProviderSchema.providerIncludedInQuery;
    }
    
    /**
     * @return the providerIsDefaultSource
     */
    public static URI getProviderIsDefaultSource()
    {
        return ProviderSchema.providerIsDefaultSource;
    }
    
    /**
     * @return the providerNeedsRdfNormalisation
     */
    public static URI getProviderNeedsRdfNormalisation()
    {
        return ProviderSchema.providerNeedsRdfNormalisation;
    }
    
    /**
     * @return the providerNoCommunication
     */
    public static URI getProviderNoCommunication()
    {
        return ProviderSchema.providerNoCommunication;
    }
    
    /**
     * @return the providerProxy
     */
    public static URI getProviderProxy()
    {
        return ProviderSchema.providerProxy;
    }
    
    /**
     * @return the providerRedirect
     */
    public static URI getProviderRedirect()
    {
        return ProviderSchema.providerRedirect;
    }
    
    /**
     * @return the providerResolutionMethod
     */
    public static URI getProviderResolutionMethod()
    {
        return ProviderSchema.providerResolutionMethod;
    }
    
    /**
     * @return the providerResolutionStrategy
     */
    public static URI getProviderResolutionStrategy()
    {
        return ProviderSchema.providerResolutionStrategy;
    }
    
    /**
     * @return the providerTitle
     */
    public static URI getProviderTitle()
    {
        return ProviderSchema.providerTitle;
    }
    
    /**
     * @return the providerTypeUri
     */
    public static URI getProviderTypeUri()
    {
        return ProviderSchema.providerTypeUri;
    }
    
    // Use these to include information based on whether or not the provider was
    // actually used to
    // provide information for particular user queries
    // public Collection<String> providerQueryInclusions = new
    // HashSet<String>();
    // public boolean onlyIncludeProviderQueryIfInformationReturned = true;
    
    /**
     * @param nextProviderAssumedContentType
     *            the providerAssumedContentType to set
     */
    public static void setProviderAssumedContentType(final URI nextProviderAssumedContentType)
    {
        ProviderSchema.providerAssumedContentType = nextProviderAssumedContentType;
    }
    
    /**
     * @param nextProviderHandledNamespace
     *            the providerHandledNamespace to set
     */
    public static void setProviderHandlesNamespace(final URI nextProviderHandledNamespace)
    {
        ProviderSchema.providerHandlesNamespace = nextProviderHandledNamespace;
    }
    
    /**
     * @param nextProviderIncludedInQuery
     *            the providerIncludedInQuery to set
     */
    public static void setProviderIncludedInQuery(final URI nextProviderIncludedInQuery)
    {
        ProviderSchema.providerIncludedInQuery = nextProviderIncludedInQuery;
    }
    
    /**
     * @param nextProviderIsDefaultSource
     *            the providerIsDefaultSource to set
     */
    public static void setProviderIsDefaultSource(final URI nextProviderIsDefaultSource)
    {
        ProviderSchema.providerIsDefaultSource = nextProviderIsDefaultSource;
    }
    
    /**
     * @param nextProviderNeedsRdfNormalisation
     *            the providerNeedsRdfNormalisation to set
     */
    public static void setProviderNeedsRdfNormalisation(final URI nextProviderNeedsRdfNormalisation)
    {
        ProviderSchema.providerNeedsRdfNormalisation = nextProviderNeedsRdfNormalisation;
    }
    
    /**
     * @param nextProviderNoCommunication
     *            the providerNoCommunication to set
     */
    public static void setProviderNoCommunication(final URI nextProviderNoCommunication)
    {
        ProviderSchema.providerNoCommunication = nextProviderNoCommunication;
    }
    
    /**
     * @param nextProviderProxy
     *            the providerProxy to set
     */
    public static void setProviderProxy(final URI nextProviderProxy)
    {
        ProviderSchema.providerProxy = nextProviderProxy;
    }
    
    /**
     * @param nextProviderRedirect
     *            the providerRedirect to set
     */
    public static void setProviderRedirect(final URI nextProviderRedirect)
    {
        ProviderSchema.providerRedirect = nextProviderRedirect;
    }
    
    /**
     * @param nextProviderResolutionMethod
     *            the providerResolutionMethod to set
     */
    public static void setProviderResolutionMethod(final URI nextProviderResolutionMethod)
    {
        ProviderSchema.providerResolutionMethod = nextProviderResolutionMethod;
    }
    
    /**
     * @param nextProviderResolutionStrategy
     *            the providerResolutionStrategy to set
     */
    public static void setProviderResolutionStrategy(final URI nextProviderResolutionStrategy)
    {
        ProviderSchema.providerResolutionStrategy = nextProviderResolutionStrategy;
    }
    
    /**
     * @param nextProviderTitle
     *            the providerTitle to set
     */
    public static void setProviderTitle(final URI nextProviderTitle)
    {
        ProviderSchema.providerTitle = nextProviderTitle;
    }
    
    /**
     * @param nextProviderTypeUri
     *            the providerTypeUri to set
     */
    public static void setProviderTypeUri(final URI nextProviderTypeUri)
    {
        ProviderSchema.providerTypeUri = nextProviderTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public ProviderSchema()
    {
        this(ProviderSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public ProviderSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contextUri)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(ProviderSchema.getProviderTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(ProviderSchema.getProviderTypeUri(), RDFS.LABEL, f.createLiteral("The class of data providers."),
                    contextUri);
            
            if(modelVersion == 1)
            {
                con.add(ProviderSchema.getProviderTitle(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contextUri);
                con.add(ProviderSchema.getProviderTitle(), RDFS.SUBPROPERTYOF, Constants.DC_TITLE, contextUri);
            }
            
            con.add(ProviderSchema.getProviderResolutionStrategy(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProviderSchema.getProviderResolutionStrategy(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(ProviderSchema.getProviderResolutionStrategy(), RDFS.DOMAIN, ProviderSchema.getProviderTypeUri(),
                    contextUri);
            con.add(ProviderSchema.getProviderResolutionStrategy(),
                    RDFS.LABEL,
                    f.createLiteral("The provider may use a strategy of either proxying the communications with this provider, or it may redirect to it."),
                    contextUri);
            
            con.add(ProviderSchema.getProviderHandlesNamespace(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProviderSchema.getProviderHandlesNamespace(), RDFS.RANGE,
                    NamespaceEntrySchema.getNamespaceTypeUri(), contextUri);
            con.add(ProviderSchema.getProviderHandlesNamespace(), RDFS.DOMAIN, ProviderSchema.getProviderTypeUri(),
                    contextUri);
            con.add(ProviderSchema.getProviderHandlesNamespace(),
                    RDFS.LABEL,
                    f.createLiteral("The provider declares it is able to handle this namespace. If it is a default provider, it may leave this list of namespaces empty, as long as all of the relevant query types are compatible with default providers."),
                    contextUri);
            
            con.add(ProviderSchema.getProviderIncludedInQuery(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProviderSchema.getProviderIncludedInQuery(), RDFS.RANGE, QueryTypeSchema.getQueryTypeUri(),
                    contextUri);
            con.add(ProviderSchema.getProviderIncludedInQuery(), RDFS.DOMAIN, ProviderSchema.getProviderTypeUri(),
                    contextUri);
            con.add(ProviderSchema.getProviderIncludedInQuery(),
                    RDFS.LABEL,
                    f.createLiteral("The provider declares that it is able to handle these queries. This list cannot be empty in a useful configuration, as the provider would never be used."),
                    contextUri);
            
            con.add(ProviderSchema.getProviderNeedsRdfNormalisation(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProviderSchema.getProviderNeedsRdfNormalisation(), RDFS.RANGE,
                    NormalisationRuleSchema.getNormalisationRuleTypeUri(), contextUri);
            con.add(ProviderSchema.getProviderNeedsRdfNormalisation(), RDFS.DOMAIN,
                    ProviderSchema.getProviderTypeUri(), contextUri);
            con.add(ProviderSchema.getProviderNeedsRdfNormalisation(),
                    RDFS.LABEL,
                    f.createLiteral("The provider declares that it needs these rules to process input and/or output data."),
                    contextUri);
            
            con.add(ProviderSchema.getProviderResolutionMethod(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProviderSchema.getProviderResolutionMethod(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(ProviderSchema.getProviderResolutionMethod(), RDFS.DOMAIN, ProviderSchema.getProviderTypeUri(),
                    contextUri);
            con.add(ProviderSchema.getProviderResolutionMethod(),
                    RDFS.LABEL,
                    f.createLiteral("The provider may either use no-communication, or one of the supported resolution methods, for example, HTTP GET or HTTP POST."),
                    contextUri);
            
            con.add(ProviderSchema.getProviderIsDefaultSource(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(ProviderSchema.getProviderIsDefaultSource(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(ProviderSchema.getProviderIsDefaultSource(), RDFS.DOMAIN, ProviderSchema.getProviderTypeUri(),
                    contextUri);
            con.add(ProviderSchema.getProviderIsDefaultSource(),
                    RDFS.LABEL,
                    f.createLiteral("If this provider can operate over any of the namespaces supported by its query types, it can declare that it is a default provider and may be used even when the provider namespace list is not declared."),
                    contextUri);
            
            con.add(ProviderSchema.getProviderAssumedContentType(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(ProviderSchema.getProviderAssumedContentType(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(ProviderSchema.getProviderAssumedContentType(), RDFS.DOMAIN, ProviderSchema.getProviderTypeUri(),
                    contextUri);
            con.add(ProviderSchema.getProviderAssumedContentType(),
                    RDFS.LABEL,
                    f.createLiteral("If the provider does not send a recognised RDF format MIME type, the assumed content type will be used, as long as it is a recognised RDF format MIME type."),
                    contextUri);
            
            con.add(ProviderSchema.getProviderRedirect(), RDFS.LABEL,
                    f.createLiteral("The provider will redirect users to one of the endpoints given."), contextUri);
            
            con.add(ProviderSchema.getProviderProxy(),
                    RDFS.LABEL,
                    f.createLiteral("The provider will proxy requests for users and return results in combination with other providers."),
                    contextUri);
            
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
            
            ProviderSchema.log.error("RepositoryException: " + re.getMessage());
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
