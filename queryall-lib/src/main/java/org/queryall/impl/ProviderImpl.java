package org.queryall.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
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
import org.queryall.helpers.Constants;
import org.queryall.helpers.ProfileUtils;
import org.queryall.helpers.RdfUtils;
import org.queryall.helpers.Settings;
import org.queryall.helpers.StringUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProviderImpl implements Provider
{
    private static final Logger log = Logger.getLogger(ProviderImpl.class.getName());
    private static final boolean _TRACE = ProviderImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = ProviderImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = ProviderImpl.log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.getSettings().getNamespaceForProvider();
    
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
    
    // Use these to include information based on whether or not the provider was actually used to
    // provide information for particular user queries
    // public Collection<String> providerQueryInclusions = new HashSet<String>();
    // public boolean onlyIncludeProviderQueryIfInformationReturned = true;
    
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
    public static String providerNamespace;
    // public static String profileNamespace;
    private static URI providerAssumedContentType;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        ProviderImpl.providerNamespace =
                Settings.getSettings().getOntologyTermUriPrefix() + Settings.getSettings().getNamespaceForProvider()
                        + Settings.getSettings().getOntologyTermUriSuffix();
        
        ProviderImpl.setProviderTypeUri(f.createURI(ProviderImpl.providerNamespace, "Provider"));
        
        ProviderImpl.setProviderResolutionStrategy(f.createURI(ProviderImpl.providerNamespace, "resolutionStrategy"));
        ProviderImpl.setProviderHandledNamespace(f.createURI(ProviderImpl.providerNamespace, "handlesNamespace"));
        ProviderImpl.setProviderResolutionMethod(f.createURI(ProviderImpl.providerNamespace, "resolutionMethod"));
        ProviderImpl.setProviderRequiresSparqlGraphURI(f.createURI(ProviderImpl.providerNamespace, "requiresGraphUri"));
        ProviderImpl.setProviderGraphUri(f.createURI(ProviderImpl.providerNamespace, "graphUri"));
        ProviderImpl.setProviderIncludedInQuery(f.createURI(ProviderImpl.providerNamespace, "includedInQuery"));
        ProviderImpl.setProviderIsDefaultSource(f.createURI(ProviderImpl.providerNamespace, "isDefaultSource"));
        ProviderImpl.setProviderNeedsRdfNormalisation(f.createURI(ProviderImpl.providerNamespace,
                "needsRdfNormalisation"));
        ProviderImpl.setProviderRedirect(f.createURI(ProviderImpl.providerNamespace, "redirect"));
        ProviderImpl.setProviderProxy(f.createURI(ProviderImpl.providerNamespace, "proxy"));
        ProviderImpl.setProviderNoCommunication(f.createURI(ProviderImpl.providerNamespace, "nocommunication"));
        ProviderImpl.setProviderAssumedContentType(f.createURI(ProviderImpl.providerNamespace, "assumedContentType"));
        
        // NOTE: This was deprecated after API version 1 in favour of dc elements title
        ProviderImpl.setProviderTitle(f.createURI(ProviderImpl.providerNamespace, "Title"));
    }
    
    public static boolean schemaToRdf(Repository myRepository, URI contextUri, int modelVersion)
        throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
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
        catch(RepositoryException re)
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
    
    @Override
    public URI getEndpointMethod()
    {
        return endpointMethod;
    }
    
    @Override
    public void setEndpointMethod(URI endpointMethod)
    {
        this.endpointMethod = endpointMethod;
    }
    
    @Override
    public boolean containsNamespaceOrDefault(URI namespaceKey)
    {
        return containsNamespaceUri(namespaceKey) || getIsDefaultSource();
    }
    
    public ProviderImpl()
    {
        super();
    }
    
    public ProviderImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        final ValueFactory f = Constants.valueFactory;
        
        for(Statement nextStatement : inputStatements)
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
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            if(ProviderImpl._TRACE)
            {
                ProviderImpl.log.trace("Provider.toRdf: keyToUse=" + keyToUse);
            }
            
            // create some resources and literals to make statements out of
            URI providerInstanceUri = this.getKey();
            
            Literal titleLiteral;
            
            if(getTitle() == null)
            {
                titleLiteral = f.createLiteral("");
            }
            else
            {
                titleLiteral = f.createLiteral(getTitle());
            }
            
            URI redirectOrProxyLiteral = getRedirectOrProxy();
            URI endpointMethodLiteral = getEndpointMethod();
            Literal isDefaultSourceLiteral = f.createLiteral(getIsDefaultSourceVar());
            Literal assumedContentTypeLiteral = f.createLiteral(getAssumedContentType());
            
            URI curationStatusLiteral = null;
            
            if(getCurationStatus() == null)
            {
                curationStatusLiteral = ProjectImpl.getProjectNotCuratedUri();
            }
            else
            {
                curationStatusLiteral = getCurationStatus();
            }
            
            URI profileIncludeExcludeOrderLiteral = getProfileIncludeExcludeOrder();
            
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
            
            if(getNamespaces() != null)
            {
                for(URI nextNamespace : getNamespaces())
                {
                    if(nextNamespace != null)
                    {
                        con.add(providerInstanceUri, ProviderImpl.getProviderHandledNamespace(), nextNamespace,
                                keyToUse);
                    }
                }
            }
            
            if(getIncludedInQueryTypes() != null)
            {
                for(URI nextIncludedInCustomQuery : getIncludedInQueryTypes())
                {
                    if(nextIncludedInCustomQuery != null)
                    {
                        con.add(providerInstanceUri, ProviderImpl.getProviderIncludedInQuery(),
                                nextIncludedInCustomQuery, keyToUse);
                    }
                }
            }
            
            if(getNormalisationUris() != null)
            {
                for(URI nextRdfNormalisationNeeded : getNormalisationUris())
                {
                    if(nextRdfNormalisationNeeded != null)
                    {
                        con.add(providerInstanceUri, ProviderImpl.getProviderNeedsRdfNormalisation(),
                                nextRdfNormalisationNeeded, keyToUse);
                    }
                }
            }
            
            if(unrecognisedStatements != null)
            {
                for(Statement nextUnrecognisedStatement : unrecognisedStatements)
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
        catch(RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            ProviderImpl.log.error("RepositoryException: " + re.getMessage());
        }
        catch(Exception ex)
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
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((curationStatus == null) ? 0 : curationStatus.hashCode());
        result = prime * result + ((includedInQueryTypes == null) ? 0 : includedInQueryTypes.hashCode());
        result = prime * result + (isDefaultSourceVar ? 1231 : 1237);
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((namespaces == null) ? 0 : namespaces.hashCode());
        result = prime * result + ((profileIncludeExcludeOrder == null) ? 0 : profileIncludeExcludeOrder.hashCode());
        result = prime * result + ((rdfNormalisationsNeeded == null) ? 0 : rdfNormalisationsNeeded.hashCode());
        result = prime * result + ((redirectOrProxy == null) ? 0 : redirectOrProxy.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(obj == null)
        {
            return false;
        }
        if(getClass() != obj.getClass())
        {
            return false;
        }
        ProviderImpl other = (ProviderImpl)obj;
        if(key == null)
        {
            if(other.key != null)
            {
                return false;
            }
        }
        else if(!key.equals(other.key))
        {
            return false;
        }
        
        if(curationStatus == null)
        {
            if(other.curationStatus != null)
            {
                return false;
            }
        }
        else if(!curationStatus.equals(other.curationStatus))
        {
            return false;
        }
        if(includedInQueryTypes == null)
        {
            if(other.includedInQueryTypes != null)
            {
                return false;
            }
        }
        else if(!includedInQueryTypes.equals(other.includedInQueryTypes))
        {
            return false;
        }
        if(isDefaultSourceVar != other.isDefaultSourceVar)
        {
            return false;
        }
        if(namespaces == null)
        {
            if(other.namespaces != null)
            {
                return false;
            }
        }
        else if(!namespaces.equals(other.namespaces))
        {
            return false;
        }
        if(profileIncludeExcludeOrder == null)
        {
            if(other.profileIncludeExcludeOrder != null)
            {
                return false;
            }
        }
        else if(!profileIncludeExcludeOrder.equals(other.profileIncludeExcludeOrder))
        {
            return false;
        }
        if(rdfNormalisationsNeeded == null)
        {
            if(other.rdfNormalisationsNeeded != null)
            {
                return false;
            }
        }
        else if(!rdfNormalisationsNeeded.equals(other.rdfNormalisationsNeeded))
        {
            return false;
        }
        if(redirectOrProxy == null)
        {
            if(other.redirectOrProxy != null)
            {
                return false;
            }
        }
        else if(!redirectOrProxy.equals(other.redirectOrProxy))
        {
            return false;
        }
        if(title == null)
        {
            if(other.title != null)
            {
                return false;
            }
        }
        else if(!title.equals(other.title))
        {
            return false;
        }
        
        return true;
    }
    
    @Override
    public int compareTo(Provider otherProvider)
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
    public boolean containsNamespaceUri(URI newNamespaceUri)
    {
        if(this.getNamespaces() != null && newNamespaceUri != null)
        {
            return this.getNamespaces().contains(newNamespaceUri);
        }
        
        return false;
    }
    
    @Override
    public boolean containsNormalisationUri(URI normalisationKey)
    {
        if(this.getNormalisationUris() != null && normalisationKey != null)
        {
            return this.getNormalisationUris().contains(normalisationKey);
        }
        
        return false;
    }
    
    @Override
    public boolean needsRedirect()
    {
        return getRedirectOrProxy().equals(ProviderImpl.getProviderRedirect());
    }
    
    @Override
    public boolean needsProxy()
    {
        return getRedirectOrProxy().equals(ProviderImpl.getProviderProxy());
    }
    
    @Override
    public String toString()
    {
        String result = "\n";
        
        result += "key=" + getKey() + "\n";
        // result += "endpointUrls="+getEndpointUrls() + "\n";
        result += "endpointMethod=" + getEndpointMethod() + "\n";
        result += "namespaces=" + getNamespaces() + "\n";
        result += "includedInCustomQueries=" + getIncludedInQueryTypes() + "\n";
        result += "redirectOrProxy=" + getRedirectOrProxy() + "\n";
        result += "isDefaultSource=" + getIsDefaultSourceVar() + "\n";
        result += "profileIncludeExcludeOrder=" + getProfileIncludeExcludeOrder() + "\n";
        // result += "acceptHeaderString="+getAcceptHeaderString() + "\n";
        
        return result;
    }
    
    @Override
    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        String prefix = "provider_";
        
        return sb.toString();
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
        
        if(getNamespaces() != null)
        {
            result +=
                    "<div class=\"namespaces\">Namespaces: " + StringUtils.xmlEncodeString(getNamespaces().toString())
                            + "</div>\n";
        }
        else
        {
            result += "<div class=\"endpointurl\">Endpoint URL's: <span class=\"error\">None specified!</span></div>\n";
        }
        
        if(getIncludedInQueryTypes() != null)
        {
            result +=
                    "<div class=\"includedInCustomQueries\">Use this provider for the following queries: "
                            + StringUtils.xmlEncodeString(getIncludedInQueryTypes().toString()) + "</div>\n";
        }
        else
        {
            result +=
                    "<div class=\"includedInCustomQueries\"><span class=\"error\">This provider is not going to be used in any queries!</span></div>\n";
        }
        
        if(needsRedirect())
        {
            result += "<div class=\"redirectOrProxy\">Redirect to this provider</div>\n";
        }
        else
        {
            result += "<div class=\"redirectOrProxy\">Proxy communications with this provider</div>\n";
        }
        
        if(getIsDefaultSource())
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
    
    /**
     * @return the key
     */
    @Override
    public URI getKey()
    {
        return key;
    }
    
    /**
     * @param key
     *            the key to set
     */
    @Override
    public void setKey(String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }
    
    @Override
    public void setKey(URI nextKey)
    {
        this.key = nextKey;
    }
    
    /**
     * @return the namespace used to represent objects of this type by default
     */
    @Override
    public String getDefaultNamespace()
    {
        return ProviderImpl.defaultNamespace;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Collection<URI> getElementTypes()
    {
        Collection<URI> results = new ArrayList<URI>(1);
        
        results.add(ProviderImpl.getProviderTypeUri());
        
        return results;
    }
    
    @Override
    public boolean containsQueryTypeUri(URI queryKey)
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
    public boolean getIsDefaultSource()
    {
        return getIsDefaultSourceVar();
    }
    
    @Override
    public void setIsDefaultSource(boolean isDefaultSourceVar)
    {
        this.setIsDefaultSourceVar(isDefaultSourceVar);
    }
    
    @Override
    public Collection<URI> getNormalisationUris()
    {
        return rdfNormalisationsNeeded;
    }
    
    @Override
    public Collection<URI> getIncludedInQueryTypes()
    {
        return includedInQueryTypes;
    }
    
    @Override
    public void setIncludedInQueryTypes(Collection<URI> includedInCustomQueries)
    {
        this.includedInQueryTypes = includedInCustomQueries;
    }
    
    @Override
    public URI getRedirectOrProxy()
    {
        return redirectOrProxy;
    }
    
    @Override
    public void setRedirectOrProxy(URI redirectOrProxy)
    {
        this.redirectOrProxy = redirectOrProxy;
    }
    
    @Override
    public URI getProfileIncludeExcludeOrder()
    {
        return profileIncludeExcludeOrder;
    }
    
    @Override
    public void setProfileIncludeExcludeOrder(URI profileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = profileIncludeExcludeOrder;
    }
    
    @Override
    public Collection<URI> getNamespaces()
    {
        return namespaces;
    }
    
    @Override
    public void setNamespaces(Collection<URI> namespaces)
    {
        this.namespaces = namespaces;
    }
    
    @Override
    public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    @Override
    public URI getCurationStatus()
    {
        return curationStatus;
    }
    
    @Override
    public String getTitle()
    {
        return title;
    }
    
    @Override
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    @Override
    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        unrecognisedStatements.add(unrecognisedStatement);
    }
    
    @Override
    public Collection<Statement> getUnrecognisedStatements()
    {
        return unrecognisedStatements;
    }
    
    /**
     * @param providerRedirect
     *            the providerRedirect to set
     */
    public static void setProviderRedirect(URI providerRedirect)
    {
        ProviderImpl.providerRedirect = providerRedirect;
    }
    
    /**
     * @return the providerRedirect
     */
    public static URI getProviderRedirect()
    {
        return ProviderImpl.providerRedirect;
    }
    
    /**
     * @param isDefaultSourceVar
     *            the isDefaultSourceVar to set
     */
    public void setIsDefaultSourceVar(boolean isDefaultSourceVar)
    {
        this.isDefaultSourceVar = isDefaultSourceVar;
    }
    
    /**
     * @return the isDefaultSourceVar
     */
    public boolean getIsDefaultSourceVar()
    {
        return isDefaultSourceVar;
    }
    
    /**
     * @param providerTypeUri
     *            the providerTypeUri to set
     */
    public static void setProviderTypeUri(URI providerTypeUri)
    {
        ProviderImpl.providerTypeUri = providerTypeUri;
    }
    
    /**
     * @return the providerTypeUri
     */
    public static URI getProviderTypeUri()
    {
        return ProviderImpl.providerTypeUri;
    }
    
    /**
     * @param providerTitle
     *            the providerTitle to set
     */
    public static void setProviderTitle(URI providerTitle)
    {
        ProviderImpl.providerTitle = providerTitle;
    }
    
    /**
     * @return the providerTitle
     */
    public static URI getProviderTitle()
    {
        return ProviderImpl.providerTitle;
    }
    
    /**
     * @param providerResolutionStrategy
     *            the providerResolutionStrategy to set
     */
    public static void setProviderResolutionStrategy(URI providerResolutionStrategy)
    {
        ProviderImpl.providerResolutionStrategy = providerResolutionStrategy;
    }
    
    /**
     * @return the providerResolutionStrategy
     */
    public static URI getProviderResolutionStrategy()
    {
        return ProviderImpl.providerResolutionStrategy;
    }
    
    /**
     * @param providerHandledNamespace
     *            the providerHandledNamespace to set
     */
    public static void setProviderHandledNamespace(URI providerHandledNamespace)
    {
        ProviderImpl.providerHandledNamespace = providerHandledNamespace;
    }
    
    /**
     * @return the providerHandledNamespace
     */
    public static URI getProviderHandledNamespace()
    {
        return ProviderImpl.providerHandledNamespace;
    }
    
    /**
     * @param providerResolutionMethod
     *            the providerResolutionMethod to set
     */
    public static void setProviderResolutionMethod(URI providerResolutionMethod)
    {
        ProviderImpl.providerResolutionMethod = providerResolutionMethod;
    }
    
    /**
     * @return the providerResolutionMethod
     */
    public static URI getProviderResolutionMethod()
    {
        return ProviderImpl.providerResolutionMethod;
    }
    
    /**
     * @param providerRequiresSparqlGraphURI
     *            the providerRequiresSparqlGraphURI to set
     */
    public static void setProviderRequiresSparqlGraphURI(URI providerRequiresSparqlGraphURI)
    {
        ProviderImpl.providerRequiresSparqlGraphURI = providerRequiresSparqlGraphURI;
    }
    
    /**
     * @return the providerRequiresSparqlGraphURI
     */
    public static URI getProviderRequiresSparqlGraphURI()
    {
        return ProviderImpl.providerRequiresSparqlGraphURI;
    }
    
    /**
     * @param providerGraphUri
     *            the providerGraphUri to set
     */
    public static void setProviderGraphUri(URI providerGraphUri)
    {
        ProviderImpl.providerGraphUri = providerGraphUri;
    }
    
    /**
     * @return the providerGraphUri
     */
    public static URI getProviderGraphUri()
    {
        return ProviderImpl.providerGraphUri;
    }
    
    /**
     * @param providerIncludedInQuery
     *            the providerIncludedInQuery to set
     */
    public static void setProviderIncludedInQuery(URI providerIncludedInQuery)
    {
        ProviderImpl.providerIncludedInQuery = providerIncludedInQuery;
    }
    
    /**
     * @return the providerIncludedInQuery
     */
    public static URI getProviderIncludedInQuery()
    {
        return ProviderImpl.providerIncludedInQuery;
    }
    
    /**
     * @param providerIsDefaultSource
     *            the providerIsDefaultSource to set
     */
    public static void setProviderIsDefaultSource(URI providerIsDefaultSource)
    {
        ProviderImpl.providerIsDefaultSource = providerIsDefaultSource;
    }
    
    /**
     * @return the providerIsDefaultSource
     */
    public static URI getProviderIsDefaultSource()
    {
        return ProviderImpl.providerIsDefaultSource;
    }
    
    /**
     * @param providerNeedsRdfNormalisation
     *            the providerNeedsRdfNormalisation to set
     */
    public static void setProviderNeedsRdfNormalisation(URI providerNeedsRdfNormalisation)
    {
        ProviderImpl.providerNeedsRdfNormalisation = providerNeedsRdfNormalisation;
    }
    
    /**
     * @return the providerNeedsRdfNormalisation
     */
    public static URI getProviderNeedsRdfNormalisation()
    {
        return ProviderImpl.providerNeedsRdfNormalisation;
    }
    
    /**
     * @param providerProxy
     *            the providerProxy to set
     */
    public static void setProviderProxy(URI providerProxy)
    {
        ProviderImpl.providerProxy = providerProxy;
    }
    
    /**
     * @return the providerProxy
     */
    public static URI getProviderProxy()
    {
        return ProviderImpl.providerProxy;
    }
    
    @Override
    public void addNormalisationUri(URI rdfNormalisationNeeded)
    {
        if(this.rdfNormalisationsNeeded == null)
        {
            this.rdfNormalisationsNeeded = new LinkedList<URI>();
        }
        
        this.rdfNormalisationsNeeded.add(rdfNormalisationNeeded);
    }
    
    @Override
    public void addIncludedInQueryType(URI includedInQueryType)
    {
        if(this.includedInQueryTypes == null)
        {
            this.includedInQueryTypes = new LinkedList<URI>();
        }
        
        this.includedInQueryTypes.add(includedInQueryType);
    }
    
    @Override
    public void addNamespace(URI namespace)
    {
        if(this.namespaces == null)
        {
            this.namespaces = new LinkedList<URI>();
        }
        
        this.namespaces.add(namespace);
    }
    
    @Override
    public boolean isUsedWithProfileList(List<Profile> orderedProfileList, boolean allowImplicitInclusions,
            boolean includeNonProfileMatched)
    {
        return ProfileUtils.isUsedWithProfileList(this, orderedProfileList, allowImplicitInclusions,
                includeNonProfileMatched);
    }
    
    /**
     * @return the providerNoCommunication
     */
    public static URI getProviderNoCommunication()
    {
        return ProviderImpl.providerNoCommunication;
    }
    
    /**
     * @param providerNoCommunication
     *            the providerNoCommunication to set
     */
    public static void setProviderNoCommunication(URI providerNoCommunication)
    {
        ProviderImpl.providerNoCommunication = providerNoCommunication;
    }
    
    @Override
    public String getAssumedContentType()
    {
        return assumedContentType;
    }
    
    @Override
    public void setAssumedContentType(String assumedContentType)
    {
        this.assumedContentType = assumedContentType;
        
    }
    
    /**
     * @param providerAssumedContentType
     *            the providerAssumedContentType to set
     */
    public static void setProviderAssumedContentType(URI providerAssumedContentType)
    {
        ProviderImpl.providerAssumedContentType = providerAssumedContentType;
    }
    
    /**
     * @return the providerAssumedContentType
     */
    public static URI getProviderAssumedContentType()
    {
        return ProviderImpl.providerAssumedContentType;
    }
    
}
