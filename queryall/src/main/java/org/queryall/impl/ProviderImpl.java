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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.helpers.Constants;
import org.queryall.helpers.Settings;
import org.queryall.helpers.StringUtils;
import org.queryall.helpers.RdfUtils;

import org.apache.log4j.Logger;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProviderImpl extends Provider
{
    private static final Logger log = Logger.getLogger(ProviderImpl.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.getSettings().getNamespaceForProvider();
    
    protected Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key = null;
    
    private String title = "";
    private URI curationStatus = ProjectImpl.getProjectNotCuratedUri();
    private Collection<URI> namespaces = new HashSet<URI>();
    private Collection<URI> includedInQueryTypes = new HashSet<URI>();
    private Collection<URI> rdfNormalisationsNeeded = new HashSet<URI>();
    private boolean useSparqlGraph = false;
    private String sparqlGraphUri = "";
    private URI redirectOrProxy = ProviderImpl.getProviderRedirect();
    private boolean isDefaultSourceVar = false;
    private URI profileIncludeExcludeOrder = ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri();
	// See Provider.providerHttpPostSparql.stringValue(), Provider.providerHttpGetUrl.stringValue() and Provider.providerNoCommunication.stringValue()
	private URI endpointMethod = ProviderImpl.getProviderNoCommunication();
	static URI providerNoCommunication;
    
    // Use these to include information based on whether or not the provider was actually used to provide information for particular user queries
//    public Collection<String> providerQueryInclusions = new HashSet<String>();
//    public boolean onlyIncludeProviderQueryIfInformationReturned = true;
    
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
//    public static String profileNamespace;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        providerNamespace = Settings.getSettings().getOntologyTermUriPrefix()
                            +Settings.getSettings().getNamespaceForProvider()
                            +Settings.getSettings().getOntologyTermUriSuffix();
                            
                           
        setProviderTypeUri(f.createURI(providerNamespace,"Provider"));

        setProviderResolutionStrategy(f.createURI(providerNamespace,"resolutionStrategy"));
        setProviderHandledNamespace(f.createURI(providerNamespace,"handlesNamespace"));
        setProviderResolutionMethod(f.createURI(providerNamespace,"resolutionMethod"));
        setProviderRequiresSparqlGraphURI(f.createURI(providerNamespace,"requiresGraphUri"));
        setProviderGraphUri(f.createURI(providerNamespace,"graphUri"));
        setProviderIncludedInQuery(f.createURI(providerNamespace,"includedInQuery"));
        setProviderIsDefaultSource(f.createURI(providerNamespace,"isDefaultSource"));
        setProviderNeedsRdfNormalisation(f.createURI(providerNamespace,"needsRdfNormalisation"));
        setProviderRedirect(f.createURI(providerNamespace,"redirect"));
        setProviderProxy(f.createURI(providerNamespace,"proxy"));
        setProviderNoCommunication(f.createURI(providerNamespace,"nocommunication"));

        // NOTE: This was deprecated after API version 1 in favour of dc elements title
        setProviderTitle(f.createURI(providerNamespace,"Title"));
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
    
    public static boolean schemaToRdf(Repository myRepository, String keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;

        try
        {
            URI contextKeyUri = f.createURI(keyToUse);
            con.setAutoCommit(false);
            
            con.add(getProviderTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            
            if(modelVersion == 1)
            {
                con.add(getProviderTitle(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contextKeyUri);
                con.add(getProviderTitle(), RDFS.SUBPROPERTYOF, Constants.DC_TITLE, contextKeyUri);
            }

            con.add(getProviderResolutionStrategy(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProviderResolutionStrategy(), RDFS.RANGE, RDFS.RESOURCE, contextKeyUri);
            con.add(getProviderResolutionStrategy(), RDFS.DOMAIN, getProviderTypeUri(), contextKeyUri);
            con.add(getProviderResolutionStrategy(), RDFS.LABEL, f.createLiteral("The provider may use a strategy of either proxying the communications with this provider, or it may redirect to it."), contextKeyUri);

            con.add(getProviderHandledNamespace(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProviderHandledNamespace(), RDFS.RANGE, NamespaceEntryImpl.getNamespaceTypeUri(), contextKeyUri);
            con.add(getProviderHandledNamespace(), RDFS.DOMAIN, getProviderTypeUri(), contextKeyUri);
            con.add(getProviderHandledNamespace(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(getProviderIncludedInQuery(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProviderIncludedInQuery(), RDFS.RANGE, QueryTypeImpl.getQueryTypeUri(), contextKeyUri);
            con.add(getProviderIncludedInQuery(), RDFS.DOMAIN, getProviderTypeUri(), contextKeyUri);
            con.add(getProviderIncludedInQuery(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);

            con.add(getProviderNeedsRdfNormalisation(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProviderNeedsRdfNormalisation(), RDFS.RANGE, NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextKeyUri);
            con.add(getProviderNeedsRdfNormalisation(), RDFS.DOMAIN, getProviderTypeUri(), contextKeyUri);
            con.add(getProviderNeedsRdfNormalisation(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);

            con.add(getProviderResolutionMethod(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getProviderResolutionMethod(), RDFS.RANGE, RDFS.RESOURCE, contextKeyUri);
            con.add(getProviderResolutionMethod(), RDFS.DOMAIN, getProviderTypeUri(), contextKeyUri);
            con.add(getProviderResolutionMethod(), RDFS.LABEL, f.createLiteral("The provider may either use no-communication, or one of the supported resolution methods, for example, HTTP GET or HTTP POST."), contextKeyUri);
            
            con.add(getProviderRequiresSparqlGraphURI(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getProviderRequiresSparqlGraphURI(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getProviderRequiresSparqlGraphURI(), RDFS.DOMAIN, getProviderTypeUri(), contextKeyUri);
            con.add(getProviderRequiresSparqlGraphURI(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);

            con.add(getProviderGraphUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getProviderGraphUri(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getProviderGraphUri(), RDFS.DOMAIN, getProviderTypeUri(), contextKeyUri);
            con.add(getProviderGraphUri(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);

            con.add(getProviderIsDefaultSource(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getProviderIsDefaultSource(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getProviderIsDefaultSource(), RDFS.DOMAIN, getProviderTypeUri(), contextKeyUri);
            con.add(getProviderIsDefaultSource(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);

            con.add(getProviderRedirect(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getProviderRedirect(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getProviderRedirect(), RDFS.DOMAIN, getProviderTypeUri(), contextKeyUri);
            con.add(getProviderRedirect(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);

            con.add(getProviderProxy(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getProviderProxy(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getProviderProxy(), RDFS.DOMAIN, getProviderTypeUri(), contextKeyUri);
            con.add(getProviderProxy(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
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
    public ProviderImpl()
    {
    	super();
    }
    
    public ProviderImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        final ValueFactory f = Constants.valueFactory;
        
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
                
                //resultIsValid = true;
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.getProjectCurationStatusUri()))
            {
                this.setCurationStatus((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProviderTitle()) || nextStatement.getPredicate().equals(f.createURI(Constants.DC_NAMESPACE+"title")))
            {
                this.setTitle(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getProviderResolutionStrategy()))
            {
                this.setRedirectOrProxy((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProviderHandledNamespace()))
            {
                this.addNamespace((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProviderIncludedInQuery()))
            {
                this.addIncludedInQueryType((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProviderIsDefaultSource()))
            {
                this.setIsDefaultSource(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getProviderNeedsRdfNormalisation()))
            {
                this.addNormalisationUri((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileIncludeExcludeOrderUri()))
            {
                this.setProfileIncludeExcludeOrder((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProviderResolutionMethod()))
            {
                this.setEndpointMethod((URI)nextStatement.getObject());
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(_DEBUG)
        {
            log.debug("Provider.fromRdf: would have returned... keyToUse="+keyToUse+ " result="+this.toString());
        }
    }
    
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
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
            
            con.add(providerInstanceUri, ProfileImpl.getProfileIncludeExcludeOrderUri(), profileIncludeExcludeOrderLiteral, providerInstanceUri);
            
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
        return this.getKey().equals(otherProvider.getKey());
    }
    
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((curationStatus == null) ? 0 : curationStatus.hashCode());
        result = prime
                * result
                + ((includedInQueryTypes == null) ? 0 : includedInQueryTypes
                        .hashCode());
        result = prime * result + (isDefaultSourceVar ? 1231 : 1237);
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result
                + ((namespaces == null) ? 0 : namespaces.hashCode());
        result = prime
                * result
                + ((profileIncludeExcludeOrder == null) ? 0
                        : profileIncludeExcludeOrder.hashCode());
        result = prime
                * result
                + ((rdfNormalisationsNeeded == null) ? 0
                        : rdfNormalisationsNeeded.hashCode());
        result = prime * result
                + ((redirectOrProxy == null) ? 0 : redirectOrProxy.hashCode());
        result = prime * result
                + ((sparqlGraphUri == null) ? 0 : sparqlGraphUri.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + (useSparqlGraph ? 1231 : 1237);
        return result;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProviderImpl other = (ProviderImpl) obj;
        if (key == null)
        {
            if (other.key != null)
                return false;
        }
        else if (!key.equals(other.key))
            return false;

        if (curationStatus == null)
        {
            if (other.curationStatus != null)
                return false;
        }
        else if (!curationStatus.equals(other.curationStatus))
            return false;
        if (includedInQueryTypes == null)
        {
            if (other.includedInQueryTypes != null)
                return false;
        }
        else if (!includedInQueryTypes.equals(other.includedInQueryTypes))
            return false;
        if (isDefaultSourceVar != other.isDefaultSourceVar)
            return false;
        if (namespaces == null)
        {
            if (other.namespaces != null)
                return false;
        }
        else if (!namespaces.equals(other.namespaces))
            return false;
        if (profileIncludeExcludeOrder == null)
        {
            if (other.profileIncludeExcludeOrder != null)
                return false;
        }
        else if (!profileIncludeExcludeOrder
                .equals(other.profileIncludeExcludeOrder))
            return false;
        if (rdfNormalisationsNeeded == null)
        {
            if (other.rdfNormalisationsNeeded != null)
                return false;
        }
        else if (!rdfNormalisationsNeeded.equals(other.rdfNormalisationsNeeded))
            return false;
        if (redirectOrProxy == null)
        {
            if (other.redirectOrProxy != null)
                return false;
        }
        else if (!redirectOrProxy.equals(other.redirectOrProxy))
            return false;
        if (sparqlGraphUri == null)
        {
            if (other.sparqlGraphUri != null)
                return false;
        }
        else if (!sparqlGraphUri.equals(other.sparqlGraphUri))
            return false;
        if (title == null)
        {
            if (other.title != null)
                return false;
        }
        else if (!title.equals(other.title))
            return false;
        if (useSparqlGraph != other.useSparqlGraph)
            return false;
        return true;
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
    
    public boolean needsRedirect()
    {
        return getRedirectOrProxy().equals(ProviderImpl.getProviderRedirect());
    }
    
    public boolean needsProxy()
    {
        return getRedirectOrProxy().equals(ProviderImpl.getProviderProxy());
    }
    
    public String toString()
    {
        String result = "\n";
        
        result += "key="+getKey()+"\n";
//        result += "endpointUrls="+getEndpointUrls() + "\n";
        result += "endpointMethod="+getEndpointMethod() + "\n";
        result += "namespaces="+getNamespaces() + "\n";
        result += "includedInCustomQueries="+getIncludedInQueryTypes() + "\n";
        result += "useSparqlGraph="+getUseSparqlGraph() + "\n";
        result += "sparqlGraphUri="+getSparqlGraphUri() + "\n";
        result += "redirectOrProxy="+getRedirectOrProxy() + "\n";
        result += "isDefaultSource="+getIsDefaultSourceVar() + "\n";
        result += "needsUriNormalisation="+getRdfNormalisationsNeeded() + "\n";
        result += "profileIncludeExcludeOrder="+getProfileIncludeExcludeOrder() + "\n";
//        result += "acceptHeaderString="+getAcceptHeaderString() + "\n";
        
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
        
//        if(getEndpointUrls() != null)
//        {
//            result += "<div class=\"endpointurl\">Endpoint URL's: "+StringUtils.xmlEncodeString(getEndpointUrls().toString()) + "</div>\n";
//        }
//        else
//        {
//            result += "<div class=\"endpointurl\">Endpoint URL's: <span class=\"error\">None specified!</span></div>\n";
//        }
//        
//        result += "<div class=\"endpointmethod\">Retrieval Method: "+StringUtils.xmlEncodeString(getEndpointMethod().stringValue()) + "</div>\n";
        
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

    public URI getElementType()
    {
        return getProviderTypeUri();
    }
    
    public boolean containsQueryTypeUri(URI queryKey)
    {
        if(this.getIncludedInQueryTypes() != null)
        {
            return this.getIncludedInQueryTypes().contains(queryKey);
        }

        log.warn("ProviderImpl.containsQueryTypeUri: provider did not have any included query types! this.getKey()="+this.getKey());
        
        return false;
    }
    
    public boolean getIsDefaultSource()
    {
        return getIsDefaultSourceVar();
    }

    public void setIsDefaultSource(boolean isDefaultSourceVar)
    {
        this.setIsDefaultSourceVar(isDefaultSourceVar);
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

    public boolean isUsedWithProfileList(List<Profile> orderedProfileList,
            boolean allowImplicitInclusions, boolean includeNonProfileMatched)
    {
        return ProfileImpl.isUsedWithProfileList(this, orderedProfileList, allowImplicitInclusions, includeNonProfileMatched);
    }

	/**
	 * @return the providerNoCommunication
	 */
	public static URI getProviderNoCommunication() {
		return providerNoCommunication;
	}

	/**
	 * @param providerNoCommunication the providerNoCommunication to set
	 */
	public static void setProviderNoCommunication(
			URI providerNoCommunication) {
		ProviderImpl.providerNoCommunication = providerNoCommunication;
	}    

}
