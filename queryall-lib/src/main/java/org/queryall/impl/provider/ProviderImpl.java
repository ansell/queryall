package org.queryall.impl.provider;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

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
import org.queryall.api.profile.Profile;
import org.queryall.api.profile.ProfileSchema;
import org.queryall.api.project.ProjectSchema;
import org.queryall.api.provider.Provider;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.utils.ProfileUtils;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class ProviderImpl implements Provider, HtmlExport
{
    private static final Logger log = LoggerFactory.getLogger(ProviderImpl.class);
    private static final boolean _TRACE = ProviderImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = ProviderImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = ProviderImpl.log.isInfoEnabled();
    
    protected Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key = null;
    
    private String title = "";
    
    private URI curationStatus = ProjectSchema.getProjectNotCuratedUri();
    
    private Collection<URI> namespaces = new HashSet<URI>();
    
    private Collection<URI> includedInQueryTypes = new HashSet<URI>();
    
    private Collection<URI> rdfNormalisationsNeeded = new HashSet<URI>();
    
    private URI redirectOrProxy = ProviderSchema.getProviderRedirect();
    
    private boolean isDefaultSourceVar = false;
    
    private URI profileIncludeExcludeOrder = ProfileSchema.getProfileIncludeExcludeOrderUndefinedUri();
    
    // See Provider.providerHttpPostSparql.stringValue(), Provider.providerHttpGetUrl.stringValue()
    // and Provider.providerNoCommunication.stringValue()
    private URI endpointMethod = ProviderSchema.getProviderNoCommunication();
    
    private String assumedContentType = "";
    
    protected ProviderImpl()
    {
        super();
    }
    
    protected ProviderImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
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
                    && nextStatement.getObject().equals(ProviderSchema.getProviderTypeUri()))
            {
                if(ProviderImpl._TRACE)
                {
                    ProviderImpl.log.trace("Provider: found valid type predicate for URI: " + keyToUse);
                }
                
                // resultIsValid = true;
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectSchema.getProjectCurationStatusUri()))
            {
                this.setCurationStatus((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProviderSchema.getProviderTitle())
                    || nextStatement.getPredicate().equals(f.createURI(Constants.DC_NAMESPACE + "title")))
            {
                this.setTitle(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(ProviderSchema.getProviderResolutionStrategy()))
            {
                this.setRedirectOrProxy((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProviderSchema.getProviderHandlesNamespace()))
            {
                this.addNamespace((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProviderSchema.getProviderIncludedInQuery()))
            {
                this.addIncludedInQueryType((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProviderSchema.getProviderIsDefaultSource()))
            {
                this.setIsDefaultSource(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(ProviderSchema.getProviderNeedsRdfNormalisation()))
            {
                this.addNormalisationUri((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileSchema.getProfileIncludeExcludeOrderUri()))
            {
                this.setProfileIncludeExcludeOrder((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProviderSchema.getProviderResolutionMethod()))
            {
                this.setEndpointMethod((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProviderSchema.getProviderAssumedContentType()))
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
        return this.getRedirectOrProxy().equals(ProviderSchema.getProviderProxy());
    }
    
    @Override
    public boolean needsRedirect()
    {
        return this.getRedirectOrProxy().equals(ProviderSchema.getProviderRedirect());
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
                curationStatusLiteral = ProjectSchema.getProjectNotCuratedUri();
            }
            else
            {
                curationStatusLiteral = this.getCurationStatus();
            }
            
            final URI profileIncludeExcludeOrderLiteral = this.getProfileIncludeExcludeOrder();
            
            con.setAutoCommit(false);
            
            for(URI nextElementType : this.getElementTypes())
            {
                con.add(providerInstanceUri, RDF.TYPE, nextElementType, keyToUse);
            }
            
            con.add(providerInstanceUri, ProjectSchema.getProjectCurationStatusUri(), curationStatusLiteral, keyToUse);
            
            con.add(providerInstanceUri, Constants.DC_TITLE, titleLiteral, keyToUse);
            
            con.add(providerInstanceUri, ProviderSchema.getProviderResolutionStrategy(), redirectOrProxyLiteral,
                    keyToUse);
            
            con.add(providerInstanceUri, ProviderSchema.getProviderResolutionMethod(), endpointMethodLiteral, keyToUse);
            
            con.add(providerInstanceUri, ProviderSchema.getProviderIsDefaultSource(), isDefaultSourceLiteral, keyToUse);
            
            con.add(providerInstanceUri, ProfileSchema.getProfileIncludeExcludeOrderUri(),
                    profileIncludeExcludeOrderLiteral, keyToUse);
            
            con.add(providerInstanceUri, ProviderSchema.getProviderAssumedContentType(), assumedContentTypeLiteral,
                    keyToUse);
            
            if(this.getNamespaces() != null)
            {
                for(final URI nextNamespace : this.getNamespaces())
                {
                    if(nextNamespace != null)
                    {
                        con.add(providerInstanceUri, ProviderSchema.getProviderHandlesNamespace(), nextNamespace,
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
                        con.add(providerInstanceUri, ProviderSchema.getProviderIncludedInQuery(),
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
                        con.add(providerInstanceUri, ProviderSchema.getProviderNeedsRdfNormalisation(),
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
