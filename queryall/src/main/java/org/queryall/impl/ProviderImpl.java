package org.queryall.impl;

import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;

import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.sail.memory.model.MemValueFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.queryall.helpers.Constants;
import org.queryall.helpers.Settings;
import org.queryall.helpers.StringUtils;
import org.queryall.helpers.RdfUtils;
import org.queryall.*;

import org.apache.log4j.Logger;

public class ProviderImpl extends Provider
{
    private static final Logger log = Logger.getLogger(Provider.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.getSettings().getNamespaceForProvider();
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key = null;
    
    private String title = "";
    private URI curationStatus = ProjectImpl.getProjectNotCuratedUri();
    private Collection<String> endpointUrls = new HashSet<String>();
    // See Provider.providerHttpPostSparql.stringValue(), Provider.providerHttpGetUrl.stringValue() and Provider.providerNoCommunication.stringValue()
    private URI endpointMethod = ProviderImpl.getProviderNoCommunication();
    private Collection<URI> namespaces = new HashSet<URI>();
    private Collection<URI> includedInQueryTypes = new HashSet<URI>();
    private Collection<URI> rdfNormalisationsNeeded = new HashSet<URI>();
    private boolean useSparqlGraph = false;
    private String sparqlGraphUri = "";
    private URI redirectOrProxy = ProviderImpl.getProviderRedirect();
    private boolean isDefaultSourceVar = false;
    private URI profileIncludeExcludeOrder = ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri();
    
    // Use these to include information based on whether or not the provider was actually used to provide information for particular user queries
//    public Collection<String> providerQueryInclusions = new HashSet<String>();
//    public boolean onlyIncludeProviderQueryIfInformationReturned = true;
    
    private String acceptHeaderString = "";
    
    private static URI providerTypeUri;
    private static URI providerTitle;
    private static URI providerResolutionStrategy;
    private static URI providerHandledNamespace;
    private static URI providerResolutionMethod;
    private static URI providerEndpointUrl;
    private static URI providerRequiresSparqlGraphURI;
    private static URI providerGraphUri;
    private static URI providerIncludedInQuery;
    private static URI providerIsDefaultSource;
    private static URI providerNeedsRdfNormalisation;
    private static URI providerRedirect;
    private static URI providerProxy;
    private static URI providerHttpPostSparql;
    private static URI providerHttpGetUrl;
    private static URI providerNoCommunication;
    private static URI providerHttpPostUrl;
    private static URI providerAcceptHeader;
    
    public static String providerNamespace;
//    public static String profileNamespace;
    
    static
    {
        ValueFactory f = new MemValueFactory();
        
        providerNamespace = Settings.getSettings().getOntologyTermUriPrefix()
                            +Settings.getSettings().getNamespaceForProvider()
                            +Settings.getSettings().getOntologyTermUriSuffix();
                            
//        profileNamespace = Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX
//                           +Settings.DEFAULT_RDF_PROFILE_NAMESPACE
//                           +Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX;
                           
        setProviderTypeUri(f.createURI(providerNamespace+"Provider"));
        // NOTE: This was deprecated after API version 1 in favour of dc elements title
        setProviderTitle(f.createURI(providerNamespace+"Title"));
        setProviderResolutionStrategy(f.createURI(providerNamespace+"resolutionStrategy"));
        setProviderHandledNamespace(f.createURI(providerNamespace+"handlesNamespace"));
        setProviderResolutionMethod(f.createURI(providerNamespace+"resolutionMethod"));
        setProviderEndpointUrl(f.createURI(providerNamespace+"endpointUrl"));
        setProviderRequiresSparqlGraphURI(f.createURI(providerNamespace+"requiresGraphUri"));
        setProviderGraphUri(f.createURI(providerNamespace+"graphUri"));
        setProviderIncludedInQuery(f.createURI(providerNamespace+"includedInQuery"));
        setProviderIsDefaultSource(f.createURI(providerNamespace+"isDefaultSource"));
        setProviderNeedsRdfNormalisation(f.createURI(providerNamespace+"needsRdfNormalisation"));
        setProviderAcceptHeader(f.createURI(providerNamespace+"acceptHeader"));
        setProviderRedirect(f.createURI(providerNamespace+"redirect"));
        setProviderProxy(f.createURI(providerNamespace+"proxy"));
        setProviderHttpPostSparql(f.createURI(providerNamespace+"httppostsparql"));
        setProviderHttpGetUrl(f.createURI(providerNamespace+"httpgeturl"));
        setProviderNoCommunication(f.createURI(providerNamespace+"nocommunication"));
        setProviderHttpPostUrl(f.createURI(providerNamespace+"httpposturl"));
    }
    
    public static boolean schemaToRdf(Repository myRepository, String keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            URI contextKeyUri = f.createURI(keyToUse);
            con.setAutoCommit(false);
            
            con.add(getProviderTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            if(modelVersion == 1)
            {
                con.add(getProviderTitle(), RDFS.SUBPROPERTYOF, f.createURI(Constants.DC_NAMESPACE+"title"), contextKeyUri);
            }
            con.add(getProviderResolutionStrategy(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProviderHandledNamespace(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProviderResolutionMethod(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProviderEndpointUrl(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getProviderRequiresSparqlGraphURI(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getProviderGraphUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getProviderIncludedInQuery(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProviderIsDefaultSource(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getProviderAcceptHeader(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getProviderNeedsRdfNormalisation(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProviderRedirect(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getProviderProxy(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch (RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
                con.rollback();
                
            log.error("RepositoryException: "+re.getMessage());
        }
        finally
        {
            if(con != null)
                con.close();
        }
        
        return false;
    }
    
    public void setAcceptHeaderString(String acceptHeaderString)
    {
        this.acceptHeaderString = acceptHeaderString;
    }
    
    public String getAcceptHeaderString()
    {
        if(acceptHeaderString != null && !acceptHeaderString.trim().equals(""))
        {
            return acceptHeaderString;
        }
        else
        {
            return Settings.getSettings().getStringPropertyFromConfig("defaultAcceptHeader");
        }
    }
    
    // public static URI getInstanceUri(ValueFactory f, String keyToUse)
    // {
    // URI providerInstanceUri = null;
    //
    // if(_TRACE)
    // {
    // log.trace("Provider.getInstanceUri: keyToUse="+keyToUse);
    // }
    //
    // providerInstanceUri = f.createURI(keyToUse);
    //
    // return providerInstanceUri;
    // }
    
    public static Provider fromRdf(Collection<Statement> inputStatements, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        // TODO: change this line back to "Provider" when refactoring is almost complete
        Provider result = new ProviderImpl();
        
        boolean resultIsValid = false;
        
        Collection<String> tempEndpointUrls = new HashSet<String>();
        Collection<URI> tempNamespaces = new HashSet<URI>();
        Collection<URI> tempIncludedInCustomQueries = new HashSet<URI>();
        Collection<URI> tempRdfNormalisationsNeeded = new HashSet<URI>();
        
        
        ValueFactory f = new MemValueFactory();
        
        for(Statement nextStatement : inputStatements)
        {
            if(_TRACE)
            {
                log.trace("Provider: nextStatement: "+nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE) && nextStatement.getObject().equals(getProviderTypeUri()))
            {
                if(_TRACE)
                {
                    log.trace("Provider: found valid type predicate for URI: "+keyToUse);
                }
                
                resultIsValid = true;
                result.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.getProjectCurationStatusUri()))
            {
                result.setCurationStatus((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProviderTitle()) || nextStatement.getPredicate().equals(f.createURI(Constants.DC_NAMESPACE+"title")))
            {
                result.setTitle(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getProviderAcceptHeader()))
            {
                result.setAcceptHeaderString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getProviderResolutionStrategy()))
            {
                result.setRedirectOrProxy((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProviderHandledNamespace()))
            {
                tempNamespaces.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProviderResolutionMethod()))
            {
                result.setEndpointMethod((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProviderEndpointUrl()))
            {
                tempEndpointUrls.add(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getProviderRequiresSparqlGraphURI()))
            {
                result.setUseSparqlGraph(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getProviderGraphUri()))
            {
                result.setSparqlGraphUri(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getProviderIncludedInQuery()))
            {
                tempIncludedInCustomQueries.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProviderIsDefaultSource()))
            {
                result.setIsDefaultSource(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getProviderNeedsRdfNormalisation()))
            {
                tempRdfNormalisationsNeeded.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileIncludeExcludeOrderUri()))
            {
                result.setProfileIncludeExcludeOrder((URI)nextStatement.getObject());
            }
            else
            {
                result.addUnrecognisedStatement(nextStatement);
            }
        }
        
        result.setNamespaces(tempNamespaces);
        result.setEndpointUrls(tempEndpointUrls);
        result.setNormalisationUris(tempRdfNormalisationsNeeded);
        result.setIncludedInQueryTypes(tempIncludedInCustomQueries);
        
        if(_DEBUG)
        {
            log.debug("Provider.fromRdf: would have returned... keyToUse="+keyToUse+ " result="+result.toString());
        }
        
        if(resultIsValid)
        {
            return result;
        }
        else
        {
            throw new RuntimeException("Provider.fromRdf: result was not valid");
        }
    }
    
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            if(_TRACE)
            {
                log.trace("Provider.toRdf: keyToUse="+keyToUse);
            }

            // create some resources and literals to make statements out of
            URI providerInstanceUri = keyToUse;
            
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
            Literal useSparqlGraphLiteral = f.createLiteral(getUseSparqlGraph());
            Literal sparqlGraphUriLiteral = f.createLiteral(getSparqlGraphUri());
            Literal isDefaultSourceLiteral = f.createLiteral(getIsDefaultSourceVar());
            
            // backwards compatibility check, and use default if nothing was previously specified
            // NOTE: we assume empty accept header is non-intentional as it doesn't have a non-trivial purpose
            Literal acceptHeaderLiteral = null;
            
            if(getAcceptHeaderString() == null || getAcceptHeaderString().trim().equals(""))
            {
                acceptHeaderLiteral = f.createLiteral(Settings.getSettings().getStringPropertyFromConfig("defaultAcceptHeader"));
            }
            else
            {
                acceptHeaderLiteral = f.createLiteral(getAcceptHeaderString());
            }
            
            URI curationStatusLiteral = null;
            
            if(getCurationStatus() == null)
                curationStatusLiteral = ProjectImpl.getProjectNotCuratedUri();
            else
                curationStatusLiteral = getCurationStatus();
                
                
            URI profileIncludeExcludeOrderLiteral = getProfileIncludeExcludeOrder();
            
            con.setAutoCommit(false);
            
            con.add(providerInstanceUri, RDF.TYPE, getProviderTypeUri(), providerInstanceUri);
            
            con.add(providerInstanceUri, ProjectImpl.getProjectCurationStatusUri(), curationStatusLiteral, providerInstanceUri);
            
            con.add(providerInstanceUri, Constants.DC_TITLE, titleLiteral, providerInstanceUri);
            
            con.add(providerInstanceUri, getProviderResolutionStrategy(), redirectOrProxyLiteral, providerInstanceUri);
            
            con.add(providerInstanceUri, getProviderResolutionMethod(), endpointMethodLiteral, providerInstanceUri);
            
            con.add(providerInstanceUri, getProviderRequiresSparqlGraphURI(), useSparqlGraphLiteral, providerInstanceUri);
            
            con.add(providerInstanceUri, getProviderGraphUri(), sparqlGraphUriLiteral, providerInstanceUri);
            
            con.add(providerInstanceUri, getProviderIsDefaultSource(), isDefaultSourceLiteral, providerInstanceUri);
            
            con.add(providerInstanceUri, getProviderAcceptHeader(), acceptHeaderLiteral, providerInstanceUri);
            
            con.add(providerInstanceUri, ProfileImpl.getProfileIncludeExcludeOrderUri(), profileIncludeExcludeOrderLiteral, providerInstanceUri);
            
            if(getEndpointUrls() != null)
            {
                for(String nextEndpointUrl : getEndpointUrls())
                {
                    if(nextEndpointUrl != null)
                    {
                        con.add(providerInstanceUri, getProviderEndpointUrl(), f.createLiteral(nextEndpointUrl), providerInstanceUri);
                    }
                }
            }
            
            if(getNamespaces() != null)
            {
                for(URI nextNamespace : getNamespaces())
                {
                    if(nextNamespace != null)
                    {
                        con.add(providerInstanceUri, getProviderHandledNamespace(), nextNamespace, providerInstanceUri);
                    }
                }
            }
            
            if(getIncludedInQueryTypes() != null)
            {
                for(URI nextIncludedInCustomQuery : getIncludedInQueryTypes())
                {
                    if(nextIncludedInCustomQuery != null)
                    {
                        con.add(providerInstanceUri, getProviderIncludedInQuery(), nextIncludedInCustomQuery, providerInstanceUri);
                    }
                }
            }
            
            if(getRdfNormalisationsNeeded() != null)
            {
                for(URI nextRdfNormalisationNeeded : getRdfNormalisationsNeeded())
                {
                    if(nextRdfNormalisationNeeded != null)
                    {
                        con.add(providerInstanceUri, getProviderNeedsRdfNormalisation(), nextRdfNormalisationNeeded, providerInstanceUri);
                    }
                }
            }
            
            if(unrecognisedStatements != null)
            {
                for(Statement nextUnrecognisedStatement : unrecognisedStatements)
                {
                    if(nextUnrecognisedStatement != null)
                    {
                        con.add(nextUnrecognisedStatement);
                    }
                }
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch (RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            log.error("RepositoryException: "+re.getMessage());
        }
        catch(Exception ex)
        {
            log.error("Provider: Exception.. keyToUse="+keyToUse, ex);
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
    
    public boolean equals(Provider otherProvider)
    {
        // For simplicity, we do this based on the key which we presume people set differently for different providers!
        // TODO: enable an exact distinction by normalising the order of each property set and checking if they exactly match
        return this.getKey().equals(otherProvider.getKey());
    }
    
    public int compareTo(Provider otherProvider)
    {
        @SuppressWarnings("unused")
        final int BEFORE = -1;
        final int EQUAL = 0;
        @SuppressWarnings("unused")
        final int AFTER = 1;
    
        if ( this == otherProvider ) 
            return EQUAL;

        return this.getKey().stringValue().compareTo(otherProvider.getKey().stringValue());
    }

    public boolean containsNamespaceUri(URI newNamespaceUri)
    {
        if(this.getNamespaces() != null)
        {
            return this.getNamespaces().contains(newNamespaceUri);
            // for(String nextNamespace : this.namespaces)
            // {
                // if(nextNamespace.equals(newNamespaceUri))
                // {
                    // return true;
                // }
            // }
        }
        
        return false;
    }
    
    public boolean containsNormalisationUri(URI normalisationKey)
    {
        return this.getNormalisationUris().contains(normalisationKey);
    }
    
    public boolean isHttpPostSparql()
    {
        return this.getEndpointMethod().equals(ProviderImpl.getProviderHttpPostSparql());
    }
    
    public boolean isHttpGetUrl()
    {
        return this.getEndpointMethod().equals(ProviderImpl.getProviderHttpGetUrl());
    }
    
    public boolean hasEndpointUrl()
    {
        return (this.getEndpointUrls() != null && this.getEndpointUrls().size() > 0);
    }
    
    public boolean needsRedirect()
    {
        return getRedirectOrProxy().equals(ProviderImpl.getProviderRedirect().stringValue());
    }
    
    public boolean needsProxy()
    {
        return getRedirectOrProxy().equals(ProviderImpl.getProviderProxy().stringValue());
    }
    
    public String toString()
    {
        String result = "\n";
        
        result += "key="+getKey()+"\n";
        result += "endpointUrls="+getEndpointUrls() + "\n";
        result += "endpointMethod="+getEndpointMethod() + "\n";
        result += "namespaces="+getNamespaces() + "\n";
        result += "includedInCustomQueries="+getIncludedInQueryTypes() + "\n";
        result += "useSparqlGraph="+getUseSparqlGraph() + "\n";
        result += "sparqlGraphUri="+getSparqlGraphUri() + "\n";
        result += "redirectOrProxy="+getRedirectOrProxy() + "\n";
        result += "isDefaultSource="+getIsDefaultSourceVar() + "\n";
        result += "needsUriNormalisation="+getRdfNormalisationsNeeded() + "\n";
        result += "profileIncludeExcludeOrder="+getProfileIncludeExcludeOrder() + "\n";
        result += "acceptHeaderString="+getAcceptHeaderString() + "\n";
        
        return result;
    }
    

    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        String prefix = "provider_";
        
        return sb.toString();
    }
    

    public String toHtml()
    {
        String result = "";
        
        if(getEndpointUrls() != null)
        {
            result += "<div class=\"endpointurl\">Endpoint URL's: "+StringUtils.xmlEncodeString(getEndpointUrls().toString()) + "</div>\n";
        }
        else
        {
            result += "<div class=\"endpointurl\">Endpoint URL's: <span class=\"error\">None specified!</span></div>\n";
        }
        
        result += "<div class=\"endpointmethod\">Retrieval Method: "+StringUtils.xmlEncodeString(getEndpointMethod().stringValue()) + "</div>\n";
        
        if(getNamespaces() != null)
        {
            result += "<div class=\"namespaces\">Namespaces: "+StringUtils.xmlEncodeString(getNamespaces().toString()) + "</div>\n";
        }
        else
        {
            result += "<div class=\"endpointurl\">Endpoint URL's: <span class=\"error\">None specified!</span></div>\n";
        }
        
        if(getIncludedInQueryTypes() != null)
        {
            result += "<div class=\"includedInCustomQueries\">Use this provider for the following queries: "+StringUtils.xmlEncodeString(getIncludedInQueryTypes().toString()) + "</div>\n";
        }
        else
        {
            result += "<div class=\"includedInCustomQueries\"><span class=\"error\">This provider is not going to be used in any queries!</span></div>\n";
        }
        
        if(getUseSparqlGraph())
        {
            result += "<div class=\"useSparqlGraph\">Uses a SPARQL graph URI with URI: "+StringUtils.xmlEncodeString(getSparqlGraphUri()) + "</div>\n";
        }
        else
        {
            result += "<div class=\"useSparqlGraph\">This provider does not use SPARQL graphs!</div>\n";
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
            result += "<div class=\"redirectOrProxy\">This provider will not be used on all namespaces for the configured queries</div>\n";
        }
        
        if(getRdfNormalisationsNeeded() != null)
        {
            result += "<div class=\"includedInCustomQueries\">This provider requires the following normalisations to match the normalised URI formats: "+StringUtils.xmlEncodeString(getRdfNormalisationsNeeded().toString()) + "</div>\n";
        }
        else
        {
            result += "<div class=\"includedInCustomQueries\">No RDF normalisations needed for this provider!</div>\n";
        }
        
        return result;
    }
    
    /**
     * @return the key
     */

    public URI getKey()
    {
        return key;
    }

    /**
     * @param key the key to set
     */

    public void setKey(String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }

    public void setKey(URI nextKey)
    {
        this.key = nextKey;
    }    
    /**
     * @return the namespace used to represent objects of this type by default
     */

    public String getDefaultNamespace()
    {
        return defaultNamespace;
    }
    
    /**
     * @return the URI used for the rdf Type of these elements
     */

    public String getElementType()
    {
        return getProviderTypeUri().stringValue();
    }
    
    public boolean containsQueryTypeUri(URI queryKey)
    {
        if(this.getIncludedInQueryTypes() != null)
        {
            return this.getIncludedInQueryTypes().contains(queryKey);
        }

        log.warn("Provider.handlesQueryExplicitly: provider did not have any included custom queries! this.getKey()="+this.getKey());
        
        return false;
    }
    
    public static URI getProviderHttpPostSparqlUri()
    {
        return getProviderHttpPostSparql();
    }
    
    public static URI getProviderHttpPostUrlUri()
    {
        return getProviderHttpPostUrl();
    }
    
    
    public static URI getProviderHttpGetUrlUri()
    {
        return getProviderHttpGetUrl();
    }
    
    public boolean getIsDefaultSource()
    {
        return getIsDefaultSourceVar();
    }

    public void setIsDefaultSource(boolean isDefaultSourceVar)
    {
        this.setIsDefaultSourceVar(isDefaultSourceVar);
    }

    public boolean getUseSparqlGraph()
    {
        return useSparqlGraph;
    }

    public void setUseSparqlGraph(boolean useSparqlGraph)
    {
        this.useSparqlGraph = useSparqlGraph;
    }

    public String getSparqlGraphUri()
    {
        return sparqlGraphUri;
    }

    public void setSparqlGraphUri(String sparqlGraphUri)
    {
        this.sparqlGraphUri = sparqlGraphUri;
    }
    
    public Collection<URI> getNormalisationUris()
    {
        return getRdfNormalisationsNeeded();
    }
    
    public void setNormalisationUris(Collection<URI> rdfNormalisationsNeeded)
    {
        this.setRdfNormalisationsNeeded(rdfNormalisationsNeeded);
    }

    public Collection<URI> getIncludedInQueryTypes()
    {
        return includedInQueryTypes;
    }
    
    public void setIncludedInQueryTypes(Collection<URI> includedInCustomQueries)
    {
        this.includedInQueryTypes = includedInCustomQueries;
    }

    public Collection<String> getEndpointUrls()
    {
        return endpointUrls;
    }
    
    public void setEndpointUrls(Collection<String> endpointUrls)
    {
        this.endpointUrls = endpointUrls;
    }

    public URI getEndpointMethod()
    {
        return endpointMethod;
    }

    public void setEndpointMethod(URI endpointMethod)
    {
        this.endpointMethod = endpointMethod;
    }
    
    public URI getRedirectOrProxy()
    {
        return redirectOrProxy;
    }

    public void setRedirectOrProxy(URI redirectOrProxy)
    {
        this.redirectOrProxy = redirectOrProxy;
    }
    
    public URI getProfileIncludeExcludeOrder()
    {
        return profileIncludeExcludeOrder;
    }

    public void setProfileIncludeExcludeOrder(URI profileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = profileIncludeExcludeOrder;
    }

    
    public Collection<URI> getNamespaces()
    {
        return namespaces;
    }

    public void setNamespaces(Collection<URI> namespaces)
    {
        this.namespaces = namespaces;
    }

    public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    public URI getCurationStatus()
    {
        return curationStatus;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        unrecognisedStatements.add(unrecognisedStatement);
    }

    public Collection<Statement> getUnrecognisedStatements()
    {
        return unrecognisedStatements;
    }

	/**
	 * @param providerNoCommunication the providerNoCommunication to set
	 */
	public static void setProviderNoCommunication(
			URI providerNoCommunication) {
		ProviderImpl.providerNoCommunication = providerNoCommunication;
	}

	/**
	 * @return the providerNoCommunication
	 */
	public static URI getProviderNoCommunication() {
		return providerNoCommunication;
	}

	/**
	 * @param rdfNormalisationsNeeded the rdfNormalisationsNeeded to set
	 */
	public void setRdfNormalisationsNeeded(Collection<URI> rdfNormalisationsNeeded) {
		this.rdfNormalisationsNeeded = rdfNormalisationsNeeded;
	}

	/**
	 * @return the rdfNormalisationsNeeded
	 */
	public Collection<URI> getRdfNormalisationsNeeded() {
		return rdfNormalisationsNeeded;
	}

	/**
	 * @param providerRedirect the providerRedirect to set
	 */
	public static void setProviderRedirect(URI providerRedirect) {
		ProviderImpl.providerRedirect = providerRedirect;
	}

	/**
	 * @return the providerRedirect
	 */
	public static URI getProviderRedirect() {
		return providerRedirect;
	}

	/**
	 * @param isDefaultSourceVar the isDefaultSourceVar to set
	 */
	public void setIsDefaultSourceVar(boolean isDefaultSourceVar) {
		this.isDefaultSourceVar = isDefaultSourceVar;
	}

	/**
	 * @return the isDefaultSourceVar
	 */
	public boolean getIsDefaultSourceVar() {
		return isDefaultSourceVar;
	}

	/**
	 * @param providerTypeUri the providerTypeUri to set
	 */
	public static void setProviderTypeUri(URI providerTypeUri) {
		ProviderImpl.providerTypeUri = providerTypeUri;
	}

	/**
	 * @return the providerTypeUri
	 */
	public static URI getProviderTypeUri() {
		return providerTypeUri;
	}

	/**
	 * @param providerTitle the providerTitle to set
	 */
	public static void setProviderTitle(URI providerTitle) {
		ProviderImpl.providerTitle = providerTitle;
	}

	/**
	 * @return the providerTitle
	 */
	public static URI getProviderTitle() {
		return providerTitle;
	}

	/**
	 * @param providerResolutionStrategy the providerResolutionStrategy to set
	 */
	public static void setProviderResolutionStrategy(
			URI providerResolutionStrategy) {
		ProviderImpl.providerResolutionStrategy = providerResolutionStrategy;
	}

	/**
	 * @return the providerResolutionStrategy
	 */
	public static URI getProviderResolutionStrategy() {
		return providerResolutionStrategy;
	}

	/**
	 * @param providerHandledNamespace the providerHandledNamespace to set
	 */
	public static void setProviderHandledNamespace(
			URI providerHandledNamespace) {
		ProviderImpl.providerHandledNamespace = providerHandledNamespace;
	}

	/**
	 * @return the providerHandledNamespace
	 */
	public static URI getProviderHandledNamespace() {
		return providerHandledNamespace;
	}

	/**
	 * @param providerResolutionMethod the providerResolutionMethod to set
	 */
	public static void setProviderResolutionMethod(
			URI providerResolutionMethod) {
		ProviderImpl.providerResolutionMethod = providerResolutionMethod;
	}

	/**
	 * @return the providerResolutionMethod
	 */
	public static URI getProviderResolutionMethod() {
		return providerResolutionMethod;
	}

	/**
	 * @param providerEndpointUrl the providerEndpointUrl to set
	 */
	public static void setProviderEndpointUrl(URI providerEndpointUrl) {
		ProviderImpl.providerEndpointUrl = providerEndpointUrl;
	}

	/**
	 * @return the providerEndpointUrl
	 */
	public static URI getProviderEndpointUrl() {
		return providerEndpointUrl;
	}

	/**
	 * @param providerRequiresSparqlGraphURI the providerRequiresSparqlGraphURI to set
	 */
	public static void setProviderRequiresSparqlGraphURI(
			URI providerRequiresSparqlGraphURI) {
		ProviderImpl.providerRequiresSparqlGraphURI = providerRequiresSparqlGraphURI;
	}

	/**
	 * @return the providerRequiresSparqlGraphURI
	 */
	public static URI getProviderRequiresSparqlGraphURI() {
		return providerRequiresSparqlGraphURI;
	}

	/**
	 * @param providerGraphUri the providerGraphUri to set
	 */
	public static void setProviderGraphUri(URI providerGraphUri) {
		ProviderImpl.providerGraphUri = providerGraphUri;
	}

	/**
	 * @return the providerGraphUri
	 */
	public static URI getProviderGraphUri() {
		return providerGraphUri;
	}

	/**
	 * @param providerIncludedInQuery the providerIncludedInQuery to set
	 */
	public static void setProviderIncludedInQuery(
			URI providerIncludedInQuery) {
		ProviderImpl.providerIncludedInQuery = providerIncludedInQuery;
	}

	/**
	 * @return the providerIncludedInQuery
	 */
	public static URI getProviderIncludedInQuery() {
		return providerIncludedInQuery;
	}

	/**
	 * @param providerIsDefaultSource the providerIsDefaultSource to set
	 */
	public static void setProviderIsDefaultSource(
			URI providerIsDefaultSource) {
		ProviderImpl.providerIsDefaultSource = providerIsDefaultSource;
	}

	/**
	 * @return the providerIsDefaultSource
	 */
	public static URI getProviderIsDefaultSource() {
		return providerIsDefaultSource;
	}

	/**
	 * @param providerNeedsRdfNormalisation the providerNeedsRdfNormalisation to set
	 */
	public static void setProviderNeedsRdfNormalisation(
			URI providerNeedsRdfNormalisation) {
		ProviderImpl.providerNeedsRdfNormalisation = providerNeedsRdfNormalisation;
	}

	/**
	 * @return the providerNeedsRdfNormalisation
	 */
	public static URI getProviderNeedsRdfNormalisation() {
		return providerNeedsRdfNormalisation;
	}

	/**
	 * @param providerProxy the providerProxy to set
	 */
	public static void setProviderProxy(URI providerProxy) {
		ProviderImpl.providerProxy = providerProxy;
	}

	/**
	 * @return the providerProxy
	 */
	public static URI getProviderProxy() {
		return providerProxy;
	}

	/**
	 * @param providerHttpPostSparql the providerHttpPostSparql to set
	 */
	public static void setProviderHttpPostSparql(URI providerHttpPostSparql) {
		ProviderImpl.providerHttpPostSparql = providerHttpPostSparql;
	}

	/**
	 * @return the providerHttpPostSparql
	 */
	public static URI getProviderHttpPostSparql() {
		return providerHttpPostSparql;
	}

	/**
	 * @param providerHttpGetUrl the providerHttpGetUrl to set
	 */
	public static void setProviderHttpGetUrl(URI providerHttpGetUrl) {
		ProviderImpl.providerHttpGetUrl = providerHttpGetUrl;
	}

	/**
	 * @return the providerHttpGetUrl
	 */
	public static URI getProviderHttpGetUrl() {
		return providerHttpGetUrl;
	}

	/**
	 * @param providerHttpPostUrl the providerHttpPostUrl to set
	 */
	public static void setProviderHttpPostUrl(URI providerHttpPostUrl) {
		ProviderImpl.providerHttpPostUrl = providerHttpPostUrl;
	}

	/**
	 * @return the providerHttpPostUrl
	 */
	public static URI getProviderHttpPostUrl() {
		return providerHttpPostUrl;
	}

	/**
	 * @param providerAcceptHeader the providerAcceptHeader to set
	 */
	public static void setProviderAcceptHeader(URI providerAcceptHeader) {
		ProviderImpl.providerAcceptHeader = providerAcceptHeader;
	}

	/**
	 * @return the providerAcceptHeader
	 */
	public static URI getProviderAcceptHeader() {
		return providerAcceptHeader;
	}

    public void addNormalisationUri(URI rdfNormalisationNeeded)
    {
        if(this.rdfNormalisationsNeeded == null)
        {
            this.rdfNormalisationsNeeded = new LinkedList<URI>();
        }
        
        this.rdfNormalisationsNeeded.add(rdfNormalisationNeeded);
    }

    public void addIncludedInQueryType(URI includedInQueryType)
    {
        if(this.includedInQueryTypes == null)
        {
            this.includedInQueryTypes = new LinkedList<URI>();
        }
        
        this.includedInQueryTypes.add(includedInQueryType);
    }

    public void addNamespace(URI namespace)
    {
        if(this.namespaces == null)
        {
            this.namespaces = new LinkedList<URI>();
        }
        
        this.namespaces.add(namespace);
    }

}
