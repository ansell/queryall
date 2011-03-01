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

import org.queryall.helpers.Settings;
import org.queryall.helpers.Utilities;
import org.queryall.*;

import org.apache.log4j.Logger;

public class ProviderImpl extends Provider
{
    private static final Logger log = Logger.getLogger(Provider.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.DEFAULT_RDF_PROVIDER_NAMESPACE;
    
    public Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    public URI key;
    
    public String title = "";
    public URI curationStatus = ProjectImpl.projectNotCuratedUri;
    public Collection<String> endpointUrls = new HashSet<String>();
    // See Provider.providerHttpPostSparql.stringValue(), Provider.providerHttpGetUrl.stringValue() and Provider.providerNoCommunication.stringValue()
    public URI endpointMethod = ProviderImpl.providerNoCommunication;
    public Collection<URI> namespaces = new HashSet<URI>();
    public Collection<URI> includedInCustomQueries = new HashSet<URI>();
    public Collection<URI> rdfNormalisationsNeeded = new HashSet<URI>();
    public boolean useSparqlGraph = false;
    public String sparqlGraphUri = "";
    public URI redirectOrProxy = ProviderImpl.providerRedirect;
    public boolean isDefaultSourceVar = false;
    public URI profileIncludeExcludeOrder = ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri();
    
    // Use these to include information based on whether or not the provider was actually used to provide information for particular user queries
    public Collection<String> providerQueryInclusions = new HashSet<String>();
    public boolean onlyIncludeProviderQueryIfInformationReturned = true;
    
    public String acceptHeaderString = "";
    
    public static URI providerTypeUri;
    public static URI providerTitle;
    public static URI providerResolutionStrategy;
    public static URI providerHandledNamespace;
    public static URI providerResolutionMethod;
    public static URI providerEndpointUrl;
    public static URI providerRequiresSparqlGraphURI;
    public static URI providerGraphUri;
    public static URI providerIncludedInQuery;
    public static URI providerIsDefaultSource;
    public static URI providerNeedsRdfNormalisation;
    public static URI providerRedirect;
    public static URI providerProxy;
    public static URI providerHttpPostSparql;
    public static URI providerHttpGetUrl;
    public static URI providerNoCommunication;
    public static URI providerHttpPostUrl;
    public static URI providerAcceptHeader;
    
    public static String providerNamespace;
    public static String profileNamespace;
    
    static
    {
        ValueFactory f = new MemValueFactory();
        
        providerNamespace = Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX
                            +Settings.DEFAULT_RDF_PROVIDER_NAMESPACE
                            +Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX;
                            
        profileNamespace = Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX
                           +Settings.DEFAULT_RDF_PROFILE_NAMESPACE
                           +Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX;
                           
        providerTypeUri = f.createURI(providerNamespace+"Provider");
        // NOTE: This was deprecated after API version 1 in favour of dc elements title
        providerTitle = f.createURI(providerNamespace+"Title");
        providerResolutionStrategy = f.createURI(providerNamespace+"resolutionStrategy");
        providerHandledNamespace = f.createURI(providerNamespace+"handlesNamespace");
        providerResolutionMethod = f.createURI(providerNamespace+"resolutionMethod");
        providerEndpointUrl = f.createURI(providerNamespace+"endpointUrl");
        providerRequiresSparqlGraphURI = f.createURI(providerNamespace+"requiresGraphUri");
        providerGraphUri = f.createURI(providerNamespace+"graphUri");
        providerIncludedInQuery = f.createURI(providerNamespace+"includedInQuery");
        providerIsDefaultSource = f.createURI(providerNamespace+"isDefaultSource");
        providerNeedsRdfNormalisation = f.createURI(providerNamespace+"needsRdfNormalisation");
        providerAcceptHeader = f.createURI(providerNamespace+"acceptHeader");
        providerRedirect = f.createURI(providerNamespace+"redirect");
        providerProxy = f.createURI(providerNamespace+"proxy");
        providerHttpPostSparql = f.createURI(providerNamespace+"httppostsparql");
        providerHttpGetUrl = f.createURI(providerNamespace+"httpgeturl");
        providerNoCommunication = f.createURI(providerNamespace+"nocommunication");
        providerHttpPostUrl = f.createURI(providerNamespace+"httpposturl");
    }
    
    public static boolean schemaToRdf(Repository myRepository, String keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            URI contextKeyUri = f.createURI(keyToUse);
            con.setAutoCommit(false);
            
            con.add(providerTypeUri, RDF.TYPE, OWL.CLASS, contextKeyUri);
            if(modelVersion == 1)
            {
                con.add(providerTitle, RDFS.SUBPROPERTYOF, f.createURI(Settings.DC_NAMESPACE+"title"), contextKeyUri);
            }
            con.add(providerResolutionStrategy, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(providerHandledNamespace, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(providerResolutionMethod, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(providerEndpointUrl, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(providerRequiresSparqlGraphURI, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(providerGraphUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(providerIncludedInQuery, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(providerIsDefaultSource, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(providerAcceptHeader, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(providerNeedsRdfNormalisation, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(providerRedirect, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(providerProxy, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
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
            return Settings.getStringPropertyFromConfig("defaultAcceptHeader");
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
    
    // keyToUse is the URI of the next instance that can be found in myRepository
    // throws a RuntimeException if the URI is not in the repository or the information is not enough to create a minimal provider configuration
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
            
            if(nextStatement.getPredicate().equals(RDF.TYPE) && nextStatement.getObject().equals(providerTypeUri))
            {
                if(_TRACE)
                {
                    log.trace("Provider: found valid type predicate for URI: "+keyToUse);
                }
                
                resultIsValid = true;
                result.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.projectCurationStatusUri))
            {
                result.setCurationStatus((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(providerTitle) || nextStatement.getPredicate().equals(f.createURI(Settings.DC_NAMESPACE+"title")))
            {
                result.setTitle(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(providerAcceptHeader))
            {
                result.setAcceptHeaderString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(providerResolutionStrategy))
            {
                result.setRedirectOrProxy((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(providerHandledNamespace))
            {
                tempNamespaces.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(providerResolutionMethod))
            {
                result.setEndpointMethod((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(providerEndpointUrl))
            {
                tempEndpointUrls.add(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(providerRequiresSparqlGraphURI))
            {
                result.setUseSparqlGraph(Utilities.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(providerGraphUri))
            {
                result.setSparqlGraphUri(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(providerIncludedInQuery))
            {
                tempIncludedInCustomQueries.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(providerIsDefaultSource))
            {
                result.setIsDefaultSource(Utilities.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(providerNeedsRdfNormalisation))
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
        result.setNormalisationsNeeded(tempRdfNormalisationsNeeded);
        result.setIncludedInCustomQueries(tempIncludedInCustomQueries);
        
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
            
            if(title == null)
            {
                titleLiteral = f.createLiteral("");
            }
            else
            {
                titleLiteral = f.createLiteral(title);
            }
            
            URI redirectOrProxyLiteral = redirectOrProxy;
            URI endpointMethodLiteral = endpointMethod;
            Literal useSparqlGraphLiteral = f.createLiteral(useSparqlGraph);
            Literal sparqlGraphUriLiteral = f.createLiteral(sparqlGraphUri);
            Literal isDefaultSourceLiteral = f.createLiteral(isDefaultSourceVar);
            
            // backwards compatibility check, and use default if nothing was previously specified
            // NOTE: we assume empty accept header is non-intentional as it doesn't have a non-trivial purpose
            Literal acceptHeaderLiteral = null;
            
            if(acceptHeaderString == null || acceptHeaderString.trim().equals(""))
            {
                acceptHeaderLiteral = f.createLiteral(Settings.getStringPropertyFromConfig("defaultAcceptHeader"));
            }
            else
            {
                acceptHeaderLiteral = f.createLiteral(acceptHeaderString);
            }
            
            URI curationStatusLiteral = null;
            
            if(curationStatus == null)
                curationStatusLiteral = ProjectImpl.projectNotCuratedUri;
            else
                curationStatusLiteral = curationStatus;
                
                
            URI profileIncludeExcludeOrderLiteral = profileIncludeExcludeOrder;
            
            con.setAutoCommit(false);
            
            con.add(providerInstanceUri, RDF.TYPE, providerTypeUri, providerInstanceUri);
            
            con.add(providerInstanceUri, ProjectImpl.projectCurationStatusUri, curationStatusLiteral, providerInstanceUri);
            
            con.add(providerInstanceUri, Settings.DC_TITLE, titleLiteral, providerInstanceUri);
            
            con.add(providerInstanceUri, providerResolutionStrategy, redirectOrProxyLiteral, providerInstanceUri);
            
            con.add(providerInstanceUri, providerResolutionMethod, endpointMethodLiteral, providerInstanceUri);
            
            con.add(providerInstanceUri, providerRequiresSparqlGraphURI, useSparqlGraphLiteral, providerInstanceUri);
            
            con.add(providerInstanceUri, providerGraphUri, sparqlGraphUriLiteral, providerInstanceUri);
            
            con.add(providerInstanceUri, providerIsDefaultSource, isDefaultSourceLiteral, providerInstanceUri);
            
            con.add(providerInstanceUri, providerAcceptHeader, acceptHeaderLiteral, providerInstanceUri);
            
            con.add(providerInstanceUri, ProfileImpl.getProfileIncludeExcludeOrderUri(), profileIncludeExcludeOrderLiteral, providerInstanceUri);
            
            if(endpointUrls != null)
            {
                for(String nextEndpointUrl : endpointUrls)
                {
                    if(nextEndpointUrl != null)
                    {
                        con.add(providerInstanceUri, providerEndpointUrl, f.createLiteral(nextEndpointUrl), providerInstanceUri);
                    }
                }
            }
            
            if(namespaces != null)
            {
                for(URI nextNamespace : namespaces)
                {
                    if(nextNamespace != null)
                    {
                        con.add(providerInstanceUri, providerHandledNamespace, nextNamespace, providerInstanceUri);
                    }
                }
            }
            
            if(includedInCustomQueries != null)
            {
                for(URI nextIncludedInCustomQuery : includedInCustomQueries)
                {
                    if(nextIncludedInCustomQuery != null)
                    {
                        con.add(providerInstanceUri, providerIncludedInQuery, nextIncludedInCustomQuery, providerInstanceUri);
                    }
                }
            }
            
            if(rdfNormalisationsNeeded != null)
            {
                for(URI nextRdfNormalisationNeeded : rdfNormalisationsNeeded)
                {
                    if(nextRdfNormalisationNeeded != null)
                    {
                        con.add(providerInstanceUri, providerNeedsRdfNormalisation, nextRdfNormalisationNeeded, providerInstanceUri);
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
        if(this.namespaces != null)
        {
            return this.namespaces.contains(newNamespaceUri);
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
    
    public boolean isHttpPostSparql()
    {
        return this.endpointMethod.equals(ProviderImpl.providerHttpPostSparql);
    }
    
    public boolean isHttpGetUrl()
    {
        return this.endpointMethod.equals(ProviderImpl.providerHttpGetUrl);
    }
    
    public boolean hasEndpointUrl()
    {
        return (this.getEndpointUrls() != null && this.getEndpointUrls().size() > 0);
    }
    
    public boolean needsRedirect()
    {
        return redirectOrProxy.equals(ProviderImpl.providerRedirect.stringValue());
    }
    
    public boolean needsProxy()
    {
        return redirectOrProxy.equals(ProviderImpl.providerProxy.stringValue());
    }
    
    public String toString()
    {
        String result = "\n";
        
        result += "key="+key+"\n";
        result += "endpointUrls="+endpointUrls + "\n";
        result += "endpointMethod="+endpointMethod + "\n";
        result += "namespaces="+namespaces + "\n";
        result += "includedInCustomQueries="+includedInCustomQueries + "\n";
        result += "useSparqlGraph="+useSparqlGraph + "\n";
        result += "sparqlGraphUri="+sparqlGraphUri + "\n";
        result += "redirectOrProxy="+redirectOrProxy + "\n";
        result += "isDefaultSource="+isDefaultSourceVar + "\n";
        result += "needsUriNormalisation="+rdfNormalisationsNeeded + "\n";
        result += "profileIncludeExcludeOrder="+profileIncludeExcludeOrder + "\n";
        result += "acceptHeaderString="+acceptHeaderString + "\n";
        
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
        
        if(endpointUrls != null)
        {
            result += "<div class=\"endpointurl\">Endpoint URL's: "+Utilities.xmlEncodeString(endpointUrls.toString()) + "</div>\n";
        }
        else
        {
            result += "<div class=\"endpointurl\">Endpoint URL's: <span class=\"error\">None specified!</span></div>\n";
        }
        
        result += "<div class=\"endpointmethod\">Retrieval Method: "+Utilities.xmlEncodeString(endpointMethod.stringValue()) + "</div>\n";
        
        if(namespaces != null)
        {
            result += "<div class=\"namespaces\">Namespaces: "+Utilities.xmlEncodeString(namespaces.toString()) + "</div>\n";
        }
        else
        {
            result += "<div class=\"endpointurl\">Endpoint URL's: <span class=\"error\">None specified!</span></div>\n";
        }
        
        if(includedInCustomQueries != null)
        {
            result += "<div class=\"includedInCustomQueries\">Use this provider for the following queries: "+Utilities.xmlEncodeString(includedInCustomQueries.toString()) + "</div>\n";
        }
        else
        {
            result += "<div class=\"includedInCustomQueries\"><span class=\"error\">This provider is not going to be used in any queries!</span></div>\n";
        }
        
        if(getUseSparqlGraph())
        {
            result += "<div class=\"useSparqlGraph\">Uses a SPARQL graph URI with URI: "+Utilities.xmlEncodeString(sparqlGraphUri) + "</div>\n";
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
        
        if(rdfNormalisationsNeeded != null)
        {
            result += "<div class=\"includedInCustomQueries\">This provider requires the following normalisations to match the normalised URI formats: "+Utilities.xmlEncodeString(rdfNormalisationsNeeded.toString()) + "</div>\n";
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
        this.setKey(Utilities.createURI(nextKey));
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
        return providerTypeUri.stringValue();
    }
    
    public boolean handlesQueryExplicitly(URI queryKey)
    {
        if(this.includedInCustomQueries != null)
        {
            return this.includedInCustomQueries.contains(queryKey);
            // for(String nextCustomQueryType : this.includedInCustomQueries)
            // {
                // if(nextCustomQueryType.equals(queryKey))
                // {
                    // return true;
                // }
            // }
        }
        else
        {
            log.warn("Provider.handlesQueryExplicitly: provider did not have any included custom queries! this.getKey()="+this.getKey());
        }
        
        return false;
    }
    
    public static URI getProviderHttpPostSparqlUri()
    {
        return providerHttpPostSparql;
    }
    
    public static URI getProviderHttpPostUrlUri()
    {
        return providerHttpPostUrl;
    }
    
    
    public static URI getProviderHttpGetUrlUri()
    {
        return providerHttpGetUrl;
    }
    
    public boolean getIsDefaultSource()
    {
        return isDefaultSourceVar;
    }

    public void setIsDefaultSource(boolean isDefaultSourceVar)
    {
        this.isDefaultSourceVar = isDefaultSourceVar;
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
    
    public Collection<URI> getNormalisationsNeeded()
    {
        return rdfNormalisationsNeeded;
    }
    
    public void setNormalisationsNeeded(Collection<URI> rdfNormalisationsNeeded)
    {
        this.rdfNormalisationsNeeded = rdfNormalisationsNeeded;
    }

    public Collection<URI> getIncludedInCustomQueries()
    {
        return includedInCustomQueries;
    }
    
    public void setIncludedInCustomQueries(Collection<URI> includedInCustomQueries)
    {
        this.includedInCustomQueries = includedInCustomQueries;
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

}
