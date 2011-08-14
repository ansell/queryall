package org.queryall.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

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
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.enumerations.Constants;
import org.queryall.utils.ProfileUtils;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProviderImpl implements Provider
{
    private static final Logger log = LoggerFactory.getLogger(ProviderImpl.class.getName());
    private static final boolean _TRACE = ProviderImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = ProviderImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = ProviderImpl.log.isInfoEnabled();
    
    /**
     * @return the providerAssumedContentType
     */
    public static URI getProviderAssumedContentType()
    {
        return ProviderImpl.providerAssumedContentType;
    }
    
    /**
     * @return the providerGraphUri
     */
    public static URI getProviderGraphUri()
    {
        return ProviderImpl.providerGraphUri;
    }
    
    /**
     * @return the providerHandledNamespace
     */
    public static URI getProviderHandledNamespace()
    {
        return ProviderImpl.providerHandledNamespace;
    }
    
    /**
     * @return the providerIncludedInQuery
     */
    public static URI getProviderIncludedInQuery()
    {
        return ProviderImpl.providerIncludedInQuery;
    }
    
    /**
     * @return the providerIsDefaultSource
     */
    public static URI getProviderIsDefaultSource()
    {
        return ProviderImpl.providerIsDefaultSource;
    }
    
    /**
     * @return the providerNeedsRdfNormalisation
     */
    public static URI getProviderNeedsRdfNormalisation()
    {
        return ProviderImpl.providerNeedsRdfNormalisation;
    }
    
    /**
     * @return the providerNoCommunication
     */
    public static URI getProviderNoCommunication()
    {
        return ProviderImpl.providerNoCommunication;
    }
    
    /**
     * @return the providerProxy
     */
    public static URI getProviderProxy()
    {
        return ProviderImpl.providerProxy;
    }
    
    /**
     * @return the providerRedirect
     */
    public static URI getProviderRedirect()
    {
        return ProviderImpl.providerRedirect;
    }
    
    /**
     * @return the providerRequiresSparqlGraphURI
     */
    public static URI getProviderRequiresSparqlGraphURI()
    {
        return ProviderImpl.providerRequiresSparqlGraphURI;
    }
    
    /**
     * @return the providerResolutionMethod
     */
    public static URI getProviderResolutionMethod()
    {
        return ProviderImpl.providerResolutionMethod;
    }
    
    /**
     * @return the providerResolutionStrategy
     */
    public static URI getProviderResolutionStrategy()
    {
        return ProviderImpl.providerResolutionStrategy;
    }
    
    /**
     * @return the providerTitle
     */
    public static URI getProviderTitle()
    {
        return ProviderImpl.providerTitle;
    }
    
    // Use these to include information based on whether or not the provider was actually used to
    // provide information for particular user queries
    // public Collection<String> providerQueryInclusions = new HashSet<String>();
    // public boolean onlyIncludeProviderQueryIfInformationReturned = true;
    
    /**
     * @return the providerTypeUri
     */
    public static URI getProviderTypeUri()
    {
        return ProviderImpl.providerTypeUri;
    }
    
    /**
     * @param providerAssumedContentType
     *            the providerAssumedContentType to set
     */
    public static void setProviderAssumedContentType(final URI providerAssumedContentType)
    {
        ProviderImpl.providerAssumedContentType = providerAssumedContentType;
    }
    
    /**
     * @param providerGraphUri
     *            the providerGraphUri to set
     */
    public static void setProviderGraphUri(final URI providerGraphUri)
    {
        ProviderImpl.providerGraphUri = providerGraphUri;
    }
    
    /**
     * @param providerHandledNamespace
     *            the providerHandledNamespace to set
     */
    public static void setProviderHandledNamespace(final URI providerHandledNamespace)
    {
        ProviderImpl.providerHandledNamespace = providerHandledNamespace;
    }
    
    /**
     * @param providerIncludedInQuery
     *            the providerIncludedInQuery to set
     */
    public static void setProviderIncludedInQuery(final URI providerIncludedInQuery)
    {
        ProviderImpl.providerIncludedInQuery = providerIncludedInQuery;
    }
    
    /**
     * @param providerIsDefaultSource
     *            the providerIsDefaultSource to set
     */
    public static void setProviderIsDefaultSource(final URI providerIsDefaultSource)
    {
        ProviderImpl.providerIsDefaultSource = providerIsDefaultSource;
    }
    
    /**
     * @param providerNeedsRdfNormalisation
     *            the providerNeedsRdfNormalisation to set
     */
    public static void setProviderNeedsRdfNormalisation(final URI providerNeedsRdfNormalisation)
    {
        ProviderImpl.providerNeedsRdfNormalisation = providerNeedsRdfNormalisation;
    }
    
    /**
     * @param providerNoCommunication
     *            the providerNoCommunication to set
     */
    public static void setProviderNoCommunication(final URI providerNoCommunication)
    {
        ProviderImpl.providerNoCommunication = providerNoCommunication;
    }
    
    /**
     * @param providerProxy
     *            the providerProxy to set
     */
    public static void setProviderProxy(final URI providerProxy)
    {
        ProviderImpl.providerProxy = providerProxy;
    }
    
    /**
     * @param providerRedirect
     *            the providerRedirect to set
     */
    public static void setProviderRedirect(final URI providerRedirect)
    {
        ProviderImpl.providerRedirect = providerRedirect;
    }
    
    /**
     * @param providerRequiresSparqlGraphURI
     *            the providerRequiresSparqlGraphURI to set
     */
    public static void setProviderRequiresSparqlGraphURI(final URI providerRequiresSparqlGraphURI)
    {
        ProviderImpl.providerRequiresSparqlGraphURI = providerRequiresSparqlGraphURI;
    }
    
    /**
     * @param providerResolutionMethod
     *            the providerResolutionMethod to set
     */
    public static void setProviderResolutionMethod(final URI providerResolutionMethod)
    {
        ProviderImpl.providerResolutionMethod = providerResolutionMethod;
    }
    
    /**
     * @param providerResolutionStrategy
     *            the providerResolutionStrategy to set
     */
    public static void setProviderResolutionStrategy(final URI providerResolutionStrategy)
    {
        ProviderImpl.providerResolutionStrategy = providerResolutionStrategy;
    }
    
    /**
     * @param providerTitle
     *            the providerTitle to set
     */
    public static void setProviderTitle(final URI providerTitle)
    {
        ProviderImpl.providerTitle = providerTitle;
    }
    
    /**
     * @param providerTypeUri
     *            the providerTypeUri to set
     */
    public static void setProviderTypeUri(final URI providerTypeUri)
    {
        ProviderImpl.providerTypeUri = providerTypeUri;
    }
    
    protected Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key = null;
    
    private String title = "";
    
    private URI curationStatus = ProjectImpl.getProjectNotCuratedUri();
    
    private Collection<URI> namespaces = new HashSet<URI>();
    
    private Collection<URI> includedInQueryTypes = new HashSet<URI>();
    
    private Collection<URI> rdfNormalisationsNeeded = new HashSet<URI>();
    
    private URI redirectOrProxy = ProviderImpl.getProviderRedirect();
    
    private boolean isDefaultSourceVar = false;
    
    private URI profileIncludeExcludeOrder = ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri();
    
    // See Provider.providerHttpPostSparql.stringValue(), Provider.providerHttpGetUrl.stringValue()
    // and Provider.providerNoCommunication.stringValue()
    private URI endpointMethod = ProviderImpl.getProviderNoCommunication();
    
    private String assumedContentType = "";
    
    static URI providerNoCommunication;
    
    private static URI providerTypeUri;
    
    private static URI providerTitle;
    
    private static URI providerResolutionStrategy;
    
    private static URI providerHandledNamespace;
    
    private static URI providerResolutionMethod;
    
    private static URI providerRequiresSparqlGraphURI;
    
    private static URI providerGraphUri;
    
    private static URI providerIncludedInQuery;
    
    private static URI providerIsDefaultSource;
    
    private static URI providerNeedsRdfNormalisation;
    
    private static URI providerRedirect;
    
    private static URI providerProxy;
    
    private static URI providerAssumedContentType;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.PROVIDER.getBaseURI();
        
        ProviderImpl.setProviderTypeUri(f.createURI(baseUri, "Provider"));
        
        ProviderImpl.setProviderResolutionStrategy(f.createURI(baseUri, "resolutionStrategy"));
        ProviderImpl.setProviderHandledNamespace(f.createURI(baseUri, "handlesNamespace"));
        ProviderImpl.setProviderResolutionMethod(f.createURI(baseUri, "resolutionMethod"));
        ProviderImpl.setProviderRequiresSparqlGraphURI(f.createURI(baseUri, "requiresGraphUri"));
        ProviderImpl.setProviderGraphUri(f.createURI(baseUri, "graphUri"));
        ProviderImpl.setProviderIncludedInQuery(f.createURI(baseUri, "includedInQuery"));
        ProviderImpl.setProviderIsDefaultSource(f.createURI(baseUri, "isDefaultSource"));
        ProviderImpl.setProviderNeedsRdfNormalisation(f.createURI(baseUri, "needsRdfNormalisation"));
        ProviderImpl.setProviderRedirect(f.createURI(baseUri, "redirect"));
        ProviderImpl.setProviderProxy(f.createURI(baseUri, "proxy"));
        ProviderImpl.setProviderNoCommunication(f.createURI(baseUri, "nocommunication"));
        ProviderImpl.setProviderAssumedContentType(f.createURI(baseUri, "assumedContentType"));
        
        // NOTE: This was deprecated after API version 1 in favour of dc elements title
        ProviderImpl.setProviderTitle(f.createURI(baseUri, "Title"));
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(ProviderImpl.getProviderTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            
            if(modelVersion == 1)
            {
                con.add(ProviderImpl.getProviderTitle(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contextUri);
                con.add(ProviderImpl.getProviderTitle(), RDFS.SUBPROPERTYOF, Constants.DC_TITLE, contextUri);
            }
            
            con.add(ProviderImpl.getProviderResolutionStrategy(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProviderImpl.getProviderResolutionStrategy(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(ProviderImpl.getProviderResolutionStrategy(), RDFS.DOMAIN, ProviderImpl.getProviderTypeUri(),
                    contextUri);
            con.add(ProviderImpl.getProviderResolutionStrategy(),
                    RDFS.LABEL,
                    f.createLiteral("The provider may use a strategy of either proxying the communications with this provider, or it may redirect to it."),
                    contextUri);
            
            con.add(ProviderImpl.getProviderHandledNamespace(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProviderImpl.getProviderHandledNamespace(), RDFS.RANGE, NamespaceEntryImpl.getNamespaceTypeUri(),
                    contextUri);
            con.add(ProviderImpl.getProviderHandledNamespace(), RDFS.DOMAIN, ProviderImpl.getProviderTypeUri(),
                    contextUri);
            con.add(ProviderImpl.getProviderHandledNamespace(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProviderImpl.getProviderIncludedInQuery(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProviderImpl.getProviderIncludedInQuery(), RDFS.RANGE, QueryTypeImpl.getQueryTypeUri(), contextUri);
            con.add(ProviderImpl.getProviderIncludedInQuery(), RDFS.DOMAIN, ProviderImpl.getProviderTypeUri(),
                    contextUri);
            con.add(ProviderImpl.getProviderIncludedInQuery(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProviderImpl.getProviderNeedsRdfNormalisation(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProviderImpl.getProviderNeedsRdfNormalisation(), RDFS.RANGE,
                    NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextUri);
            con.add(ProviderImpl.getProviderNeedsRdfNormalisation(), RDFS.DOMAIN, ProviderImpl.getProviderTypeUri(),
                    contextUri);
            con.add(ProviderImpl.getProviderNeedsRdfNormalisation(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProviderImpl.getProviderResolutionMethod(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProviderImpl.getProviderResolutionMethod(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(ProviderImpl.getProviderResolutionMethod(), RDFS.DOMAIN, ProviderImpl.getProviderTypeUri(),
                    contextUri);
            con.add(ProviderImpl.getProviderResolutionMethod(),
                    RDFS.LABEL,
                    f.createLiteral("The provider may either use no-communication, or one of the supported resolution methods, for example, HTTP GET or HTTP POST."),
                    contextUri);
            
            con.add(ProviderImpl.getProviderRequiresSparqlGraphURI(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(ProviderImpl.getProviderRequiresSparqlGraphURI(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(ProviderImpl.getProviderRequiresSparqlGraphURI(), RDFS.DOMAIN, ProviderImpl.getProviderTypeUri(),
                    contextUri);
            con.add(ProviderImpl.getProviderRequiresSparqlGraphURI(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProviderImpl.getProviderGraphUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(ProviderImpl.getProviderGraphUri(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(ProviderImpl.getProviderGraphUri(), RDFS.DOMAIN, ProviderImpl.getProviderTypeUri(), contextUri);
            con.add(ProviderImpl.getProviderGraphUri(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProviderImpl.getProviderIsDefaultSource(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(ProviderImpl.getProviderIsDefaultSource(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(ProviderImpl.getProviderIsDefaultSource(), RDFS.DOMAIN, ProviderImpl.getProviderTypeUri(),
                    contextUri);
            con.add(ProviderImpl.getProviderIsDefaultSource(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(ProviderImpl.getProviderAssumedContentType(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(ProviderImpl.getProviderAssumedContentType(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(ProviderImpl.getProviderAssumedContentType(), RDFS.DOMAIN, ProviderImpl.getProviderTypeUri(),
                    contextUri);
            con.add(ProviderImpl.getProviderAssumedContentType(),
                    RDFS.LABEL,
                    f.createLiteral("If the provider does not send a recognised RDF format MIME type, the assumed content type will be used, as long as it is a recognised RDF format MIME type."),
                    contextUri);
            
            con.add(ProviderImpl.getProviderRedirect(), RDFS.LABEL,
                    f.createLiteral("The provider will redirect users to one of the endpoints given."), contextUri);
            
            con.add(ProviderImpl.getProviderProxy(),
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
            
            ProviderImpl.log.error("RepositoryException: " + re.getMessage());
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
    
    public ProviderImpl()
    {
        super();
    }
    
    public ProviderImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final ValueFactory f = Constants.valueFactory;
        
        for(final Statement nextStatement : inputStatements)
        {
            if(ProviderImpl._TRACE)
            {
                ProviderImpl.log.trace("Provider: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(ProviderImpl.getProviderTypeUri()))
            {
                if(ProviderImpl._TRACE)
                {
                    ProviderImpl.log.trace("Provider: found valid type predicate for URI: " + keyToUse);
                }
                
                // resultIsValid = true;
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.getProjectCurationStatusUri()))
            {
                this.setCurationStatus((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProviderImpl.getProviderTitle())
                    || nextStatement.getPredicate().equals(f.createURI(Constants.DC_NAMESPACE + "title")))
            {
                this.setTitle(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(ProviderImpl.getProviderResolutionStrategy()))
            {
                this.setRedirectOrProxy((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProviderImpl.getProviderHandledNamespace()))
            {
                this.addNamespace((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProviderImpl.getProviderIncludedInQuery()))
            {
                this.addIncludedInQueryType((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProviderImpl.getProviderIsDefaultSource()))
            {
                this.setIsDefaultSource(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(ProviderImpl.getProviderNeedsRdfNormalisation()))
            {
                this.addNormalisationUri((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileIncludeExcludeOrderUri()))
            {
                this.setProfileIncludeExcludeOrder((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProviderImpl.getProviderResolutionMethod()))
            {
                this.setEndpointMethod((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProviderImpl.getProviderAssumedContentType()))
            {
                this.setAssumedContentType(nextStatement.getObject().stringValue());
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(ProviderImpl._DEBUG)
        {
            ProviderImpl.log.debug("Provider.fromRdf: would have returned... keyToUse=" + keyToUse + " result="
                    + this.toString());
        }
    }
    
    @Override
    public void addIncludedInQueryType(final URI includedInQueryType)
    {
        if(this.includedInQueryTypes == null)
        {
            this.includedInQueryTypes = new LinkedList<URI>();
        }
        
        this.includedInQueryTypes.add(includedInQueryType);
    }
    
    @Override
    public void addNamespace(final URI namespace)
    {
        if(this.namespaces == null)
        {
            this.namespaces = new LinkedList<URI>();
        }
        
        this.namespaces.add(namespace);
    }
    
    @Override
    public void addNormalisationUri(final URI rdfNormalisationNeeded)
    {
        if(this.rdfNormalisationsNeeded == null)
        {
            this.rdfNormalisationsNeeded = new LinkedList<URI>();
        }
        
        this.rdfNormalisationsNeeded.add(rdfNormalisationNeeded);
    }
    
    @Override
    public void addUnrecognisedStatement(final Statement unrecognisedStatement)
    {
        this.unrecognisedStatements.add(unrecognisedStatement);
    }
    
    @Override
    public int compareTo(final Provider otherProvider)
    {
        @SuppressWarnings("unused")
        final int BEFORE = -1;
        final int EQUAL = 0;
        @SuppressWarnings("unused")
        final int AFTER = 1;
        
        if(this == otherProvider)
        {
            return EQUAL;
        }
        
        return this.getKey().stringValue().compareTo(otherProvider.getKey().stringValue());
    }
    
    @Override
    public boolean containsNamespaceOrDefault(final URI namespaceKey)
    {
        return this.containsNamespaceUri(namespaceKey) || this.getIsDefaultSource();
    }
    
    @Override
    public boolean containsNamespaceUri(final URI newNamespaceUri)
    {
        if(this.getNamespaces() != null && newNamespaceUri != null)
        {
            return this.getNamespaces().contains(newNamespaceUri);
        }
        
        return false;
    }
    
    @Override
    public boolean containsNormalisationUri(final URI normalisationKey)
    {
        if(this.getNormalisationUris() != null && normalisationKey != null)
        {
            return this.getNormalisationUris().contains(normalisationKey);
        }
        
        return false;
    }
    
    @Override
    public boolean containsQueryTypeUri(final URI queryKey)
    {
        if(this.getIncludedInQueryTypes() != null && queryKey != null)
        {
            return this.getIncludedInQueryTypes().contains(queryKey);
        }
        
        if(queryKey != null)
        {
            ProviderImpl.log
                    .warn("ProviderImpl.containsQueryTypeUri: provider did not have any included query types! this.getKey()="
                            + this.getKey());
        }
        
        return false;
    }
    
    @Override
    public boolean equals(final Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(obj == null)
        {
            return false;
        }
        if(this.getClass() != obj.getClass())
        {
            return false;
        }
        final ProviderImpl other = (ProviderImpl)obj;
        if(this.key == null)
        {
            if(other.key != null)
            {
                return false;
            }
        }
        else if(!this.key.equals(other.key))
        {
            return false;
        }
        
        if(this.curationStatus == null)
        {
            if(other.curationStatus != null)
            {
                return false;
            }
        }
        else if(!this.curationStatus.equals(other.curationStatus))
        {
            return false;
        }
        if(this.includedInQueryTypes == null)
        {
            if(other.includedInQueryTypes != null)
            {
                return false;
            }
        }
        else if(!this.includedInQueryTypes.equals(other.includedInQueryTypes))
        {
            return false;
        }
        if(this.isDefaultSourceVar != other.isDefaultSourceVar)
        {
            return false;
        }
        if(this.namespaces == null)
        {
            if(other.namespaces != null)
            {
                return false;
            }
        }
        else if(!this.namespaces.equals(other.namespaces))
        {
            return false;
        }
        if(this.profileIncludeExcludeOrder == null)
        {
            if(other.profileIncludeExcludeOrder != null)
            {
                return false;
            }
        }
        else if(!this.profileIncludeExcludeOrder.equals(other.profileIncludeExcludeOrder))
        {
            return false;
        }
        if(this.rdfNormalisationsNeeded == null)
        {
            if(other.rdfNormalisationsNeeded != null)
            {
                return false;
            }
        }
        else if(!this.rdfNormalisationsNeeded.equals(other.rdfNormalisationsNeeded))
        {
            return false;
        }
        if(this.redirectOrProxy == null)
        {
            if(other.redirectOrProxy != null)
            {
                return false;
            }
        }
        else if(!this.redirectOrProxy.equals(other.redirectOrProxy))
        {
            return false;
        }
        if(this.title == null)
        {
            if(other.title != null)
            {
                return false;
            }
        }
        else if(!this.title.equals(other.title))
        {
            return false;
        }
        
        return true;
    }
    
    @Override
    public String getAssumedContentType()
    {
        return this.assumedContentType;
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
        return QueryAllNamespaces.PROVIDER;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Collection<URI> getElementTypes()
    {
        final Collection<URI> results = new ArrayList<URI>(1);
        
        results.add(ProviderImpl.getProviderTypeUri());
        
        return results;
    }
    
    @Override
    public URI getEndpointMethod()
    {
        return this.endpointMethod;
    }
    
    @Override
    public Collection<URI> getIncludedInQueryTypes()
    {
        return this.includedInQueryTypes;
    }
    
    @Override
    public boolean getIsDefaultSource()
    {
        return this.getIsDefaultSourceVar();
    }
    
    /**
     * @return the isDefaultSourceVar
     */
    public boolean getIsDefaultSourceVar()
    {
        return this.isDefaultSourceVar;
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
    public Collection<URI> getNamespaces()
    {
        return this.namespaces;
    }
    
    @Override
    public Collection<URI> getNormalisationUris()
    {
        return this.rdfNormalisationsNeeded;
    }
    
    @Override
    public URI getProfileIncludeExcludeOrder()
    {
        return this.profileIncludeExcludeOrder;
    }
    
    @Override
    public URI getRedirectOrProxy()
    {
        return this.redirectOrProxy;
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
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.curationStatus == null) ? 0 : this.curationStatus.hashCode());
        result = prime * result + ((this.includedInQueryTypes == null) ? 0 : this.includedInQueryTypes.hashCode());
        result = prime * result + (this.isDefaultSourceVar ? 1231 : 1237);
        result = prime * result + ((this.key == null) ? 0 : this.key.hashCode());
        result = prime * result + ((this.namespaces == null) ? 0 : this.namespaces.hashCode());
        result =
                prime * result
                        + ((this.profileIncludeExcludeOrder == null) ? 0 : this.profileIncludeExcludeOrder.hashCode());
        result =
                prime * result + ((this.rdfNormalisationsNeeded == null) ? 0 : this.rdfNormalisationsNeeded.hashCode());
        result = prime * result + ((this.redirectOrProxy == null) ? 0 : this.redirectOrProxy.hashCode());
        result = prime * result + ((this.title == null) ? 0 : this.title.hashCode());
        return result;
    }
    
    @Override
    public boolean isUsedWithProfileList(final List<Profile> orderedProfileList, final boolean allowImplicitInclusions,
            final boolean includeNonProfileMatched)
    {
        return ProfileUtils.isUsedWithProfileList(this, orderedProfileList, allowImplicitInclusions,
                includeNonProfileMatched);
    }
    
    @Override
    public boolean needsProxy()
    {
        return this.getRedirectOrProxy().equals(ProviderImpl.getProviderProxy());
    }
    
    @Override
    public boolean needsRedirect()
    {
        return this.getRedirectOrProxy().equals(ProviderImpl.getProviderRedirect());
    }
    
    @Override
    public void setAssumedContentType(final String assumedContentType)
    {
        this.assumedContentType = assumedContentType;
        
    }
    
    @Override
    public void setCurationStatus(final URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    @Override
    public void setEndpointMethod(final URI endpointMethod)
    {
        this.endpointMethod = endpointMethod;
    }
    
    @Override
    public void setIncludedInQueryTypes(final Collection<URI> includedInCustomQueries)
    {
        this.includedInQueryTypes = includedInCustomQueries;
    }
    
    @Override
    public void setIsDefaultSource(final boolean isDefaultSourceVar)
    {
        this.setIsDefaultSourceVar(isDefaultSourceVar);
    }
    
    /**
     * @param isDefaultSourceVar
     *            the isDefaultSourceVar to set
     */
    public void setIsDefaultSourceVar(final boolean isDefaultSourceVar)
    {
        this.isDefaultSourceVar = isDefaultSourceVar;
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
    public void setNamespaces(final Collection<URI> namespaces)
    {
        this.namespaces = namespaces;
    }
    
    @Override
    public void setProfileIncludeExcludeOrder(final URI profileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = profileIncludeExcludeOrder;
    }
    
    @Override
    public void setRedirectOrProxy(final URI redirectOrProxy)
    {
        this.redirectOrProxy = redirectOrProxy;
    }
    
    @Override
    public void setTitle(final String title)
    {
        this.title = title;
    }
    
    @Override
    public String toHtml()
    {
        String result = "";
        
        // if(getEndpointUrls() != null)
        // {
        // result +=
        // "<div class=\"endpointurl\">Endpoint URL's: "+StringUtils.xmlEncodeString(getEndpointUrls().toString())
        // + "</div>\n";
        // }
        // else
        // {
        // result +=
        // "<div class=\"endpointurl\">Endpoint URL's: <span class=\"error\">None specified!</span></div>\n";
        // }
        //
        // result +=
        // "<div class=\"endpointmethod\">Retrieval Method: "+StringUtils.xmlEncodeString(getEndpointMethod().stringValue())
        // + "</div>\n";
        
        if(this.getNamespaces() != null)
        {
            result +=
                    "<div class=\"namespaces\">Namespaces: "
                            + StringUtils.xmlEncodeString(this.getNamespaces().toString()) + "</div>\n";
        }
        else
        {
            result += "<div class=\"endpointurl\">Endpoint URL's: <span class=\"error\">None specified!</span></div>\n";
        }
        
        if(this.getIncludedInQueryTypes() != null)
        {
            result +=
                    "<div class=\"includedInCustomQueries\">Use this provider for the following queries: "
                            + StringUtils.xmlEncodeString(this.getIncludedInQueryTypes().toString()) + "</div>\n";
        }
        else
        {
            result +=
                    "<div class=\"includedInCustomQueries\"><span class=\"error\">This provider is not going to be used in any queries!</span></div>\n";
        }
        
        if(this.needsRedirect())
        {
            result += "<div class=\"redirectOrProxy\">Redirect to this provider</div>\n";
        }
        else
        {
            result += "<div class=\"redirectOrProxy\">Proxy communications with this provider</div>\n";
        }
        
        if(this.getIsDefaultSource())
        {
            result += "<div class=\"defaultsource\">Use this provider for configured queries on all namespaces</div>\n";
        }
        else
        {
            result +=
                    "<div class=\"redirectOrProxy\">This provider will not be used on all namespaces for the configured queries</div>\n";
        }
        
        return result;
    }
    
    @Override
    public String toHtmlFormBody()
    {
        final StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        final String prefix = "provider_";
        
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
            if(ProviderImpl._TRACE)
            {
                ProviderImpl.log.trace("Provider.toRdf: keyToUse=" + keyToUse);
            }
            
            // create some resources and literals to make statements out of
            final URI providerInstanceUri = this.getKey();
            
            Literal titleLiteral;
            
            if(this.getTitle() == null)
            {
                titleLiteral = f.createLiteral("");
            }
            else
            {
                titleLiteral = f.createLiteral(this.getTitle());
            }
            
            final URI redirectOrProxyLiteral = this.getRedirectOrProxy();
            final URI endpointMethodLiteral = this.getEndpointMethod();
            final Literal isDefaultSourceLiteral = f.createLiteral(this.getIsDefaultSourceVar());
            final Literal assumedContentTypeLiteral = f.createLiteral(this.getAssumedContentType());
            
            URI curationStatusLiteral = null;
            
            if(this.getCurationStatus() == null)
            {
                curationStatusLiteral = ProjectImpl.getProjectNotCuratedUri();
            }
            else
            {
                curationStatusLiteral = this.getCurationStatus();
            }
            
            final URI profileIncludeExcludeOrderLiteral = this.getProfileIncludeExcludeOrder();
            
            con.setAutoCommit(false);
            
            con.add(providerInstanceUri, RDF.TYPE, ProviderImpl.getProviderTypeUri(), keyToUse);
            
            con.add(providerInstanceUri, ProjectImpl.getProjectCurationStatusUri(), curationStatusLiteral, keyToUse);
            
            con.add(providerInstanceUri, Constants.DC_TITLE, titleLiteral, keyToUse);
            
            con.add(providerInstanceUri, ProviderImpl.getProviderResolutionStrategy(), redirectOrProxyLiteral, keyToUse);
            
            con.add(providerInstanceUri, ProviderImpl.getProviderResolutionMethod(), endpointMethodLiteral, keyToUse);
            
            con.add(providerInstanceUri, ProviderImpl.getProviderIsDefaultSource(), isDefaultSourceLiteral, keyToUse);
            
            con.add(providerInstanceUri, ProfileImpl.getProfileIncludeExcludeOrderUri(),
                    profileIncludeExcludeOrderLiteral, keyToUse);
            
            con.add(providerInstanceUri, ProviderImpl.getProviderAssumedContentType(), assumedContentTypeLiteral,
                    keyToUse);
            
            if(this.getNamespaces() != null)
            {
                for(final URI nextNamespace : this.getNamespaces())
                {
                    if(nextNamespace != null)
                    {
                        con.add(providerInstanceUri, ProviderImpl.getProviderHandledNamespace(), nextNamespace,
                                keyToUse);
                    }
                }
            }
            
            if(this.getIncludedInQueryTypes() != null)
            {
                for(final URI nextIncludedInCustomQuery : this.getIncludedInQueryTypes())
                {
                    if(nextIncludedInCustomQuery != null)
                    {
                        con.add(providerInstanceUri, ProviderImpl.getProviderIncludedInQuery(),
                                nextIncludedInCustomQuery, keyToUse);
                    }
                }
            }
            
            if(this.getNormalisationUris() != null)
            {
                for(final URI nextRdfNormalisationNeeded : this.getNormalisationUris())
                {
                    if(nextRdfNormalisationNeeded != null)
                    {
                        con.add(providerInstanceUri, ProviderImpl.getProviderNeedsRdfNormalisation(),
                                nextRdfNormalisationNeeded, keyToUse);
                    }
                }
            }
            
            if(this.unrecognisedStatements != null)
            {
                for(final Statement nextUnrecognisedStatement : this.unrecognisedStatements)
                {
                    if(nextUnrecognisedStatement != null)
                    {
                        con.add(nextUnrecognisedStatement, keyToUse);
                    }
                }
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            ProviderImpl.log.error("RepositoryException: " + re.getMessage());
        }
        catch(final Exception ex)
        {
            ProviderImpl.log.error("Provider: Exception.. keyToUse=" + keyToUse, ex);
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
    
    @Override
    public String toString()
    {
        String result = "\n";
        
        result += "key=" + this.getKey() + "\n";
        // result += "endpointUrls="+getEndpointUrls() + "\n";
        result += "endpointMethod=" + this.getEndpointMethod() + "\n";
        result += "namespaces=" + this.getNamespaces() + "\n";
        result += "includedInCustomQueries=" + this.getIncludedInQueryTypes() + "\n";
        result += "redirectOrProxy=" + this.getRedirectOrProxy() + "\n";
        result += "isDefaultSource=" + this.getIsDefaultSourceVar() + "\n";
        result += "profileIncludeExcludeOrder=" + this.getProfileIncludeExcludeOrder() + "\n";
        // result += "acceptHeaderString="+getAcceptHeaderString() + "\n";
        
        return result;
    }
    
}
