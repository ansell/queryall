package org.queryall.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.model.URI;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.HttpProvider;
import org.queryall.api.provider.Provider;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.provider.SparqlProvider;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.utils.NamespaceMatch;
import org.queryall.servlets.helpers.SettingsContextListener;
import org.queryall.utils.ProviderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class NamespaceProvidersServlet extends HttpServlet
{
    /**
	 * 
	 */
    private static final long serialVersionUID = -7006535158409121292L;
    public static final Logger LOG = LoggerFactory.getLogger(NamespaceProvidersServlet.class);
    public static final boolean TRACE = NamespaceProvidersServlet.LOG.isTraceEnabled();
    public static final boolean DEBUG = NamespaceProvidersServlet.LOG.isDebugEnabled();
    public static final boolean INFO = NamespaceProvidersServlet.LOG.isInfoEnabled();
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException
    {
        final QueryAllConfiguration localSettings =
                (QueryAllConfiguration)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_CONFIG);
        
        final PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        
        // final Date currentDate = new Date();
        // final String now = Constants.ISO8601UTC().format(currentDate);
        
        @SuppressWarnings("unused")
        final String realHostName =
                request.getScheme() + "://" + request.getServerName()
                        + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) + "/";
        
        final Map<URI, Provider> allProviders = localSettings.getAllProviders();
        
        final Map<URI, NamespaceEntry> allNamespaceEntries = localSettings.getAllNamespaceEntries();
        
        final Map<URI, NormalisationRule> allRdfRules = localSettings.getAllNormalisationRules();
        
        final Map<URI, RuleTest> allRdfRuleTests = localSettings.getAllRuleTests();
        
        final Map<URI, Profile> allProfiles = localSettings.getAllProfiles();
        
        final Map<URI, QueryType> allQueryTypes = localSettings.getAllQueryTypes();
        
        final Map<URI, Collection<Provider>> providersByNamespace = new ConcurrentHashMap<URI, Collection<Provider>>();
        
        final Map<URI, Collection<Provider>> providersByQueryKey = new ConcurrentHashMap<URI, Collection<Provider>>();
        
        final Map<String, Collection<Provider>> allQueryTypesByNamespace =
                new ConcurrentHashMap<String, Collection<Provider>>();
        
        final Collection<String> namespaceUseWithoutDefinitions = new ArrayList<String>();
        
        final AtomicInteger overallQueryTypeProviders = new AtomicInteger(0);
        final AtomicInteger overallNamespaceProviders = new AtomicInteger(0);
        final AtomicInteger overallQueryTypeByNamespaceProviders = new AtomicInteger(0);
        
        for(final URI nextKey : allProviders.keySet())
        {
            final Provider nextProvider = allProviders.get(nextKey);
            
            for(final URI nextQueryKey : nextProvider.getIncludedInQueryTypes())
            {
                if(!providersByQueryKey.containsKey(nextQueryKey))
                {
                    final Map<URI, Provider> queryProviders =
                            ProviderUtils.getProvidersSupportingQueryType(allProviders, nextQueryKey);
                    
                    providersByQueryKey.put(nextQueryKey, queryProviders.values());
                    
                    overallQueryTypeProviders.addAndGet(queryProviders.size());
                }
            }
            
            // HACK: This method is hacky and breaks down when knowledge about the input parameter
            // for each namespace is included
            for(final URI nextNamespace : nextProvider.getNamespaces())
            {
                if(nextNamespace != null && !providersByNamespace.containsKey(nextNamespace))
                {
                    // HACK: FIXME: TODO:
                    final Collection<URI> singleton = Collections.singleton(nextNamespace);
                    final Map<String, Collection<URI>> nextNamespacesList =
                            Collections.singletonMap("input_1", singleton);
                    
                    final Map<URI, Provider> namespaceProviders =
                            ProviderUtils.getProvidersForNamespaceUris(allProviders, nextNamespacesList,
                                    NamespaceMatch.ANY_MATCHED);
                    
                    providersByNamespace.put(nextNamespace, namespaceProviders.values());
                    
                    overallNamespaceProviders.addAndGet(namespaceProviders.size());
                    
                    for(final Provider nextNamespaceProvider : namespaceProviders.values())
                    {
                        for(final URI nextQueryKey : nextNamespaceProvider.getIncludedInQueryTypes())
                        {
                            if(!allQueryTypesByNamespace.containsKey(nextQueryKey.stringValue() + " "
                                    + nextNamespace.stringValue()))
                            {
                                final QueryType queryType = localSettings.getQueryType(nextQueryKey);
                                
                                if(queryType == null)
                                {
                                    NamespaceProvidersServlet.LOG
                                            .error("Could not find query type for URI nextNamespaceProvider="
                                                    + nextNamespaceProvider.getKey().stringValue() + " nextQueryKey="
                                                    + nextQueryKey);
                                    out.write("Could not find query type for URI nextNamespaceProvider="
                                            + nextNamespaceProvider.getKey().stringValue() + " nextQueryKey="
                                            + nextQueryKey + " <br/> \n");
                                    
                                    continue;
                                }
                                
                                final Map<URI, Provider> queryTypesByNamespace =
                                        ProviderUtils.getProvidersForQueryTypeForNamespaceUris(allProviders, queryType,
                                                nextNamespacesList, NamespaceMatch.ANY_MATCHED);
                                
                                allQueryTypesByNamespace.put(
                                        nextQueryKey.stringValue() + " " + nextNamespace.stringValue(),
                                        queryTypesByNamespace.values());
                                
                                overallQueryTypeByNamespaceProviders.addAndGet(queryTypesByNamespace.size());
                            }
                        }
                    }
                }
                
                if(!allNamespaceEntries.containsKey(nextNamespace))
                {
                    NamespaceProvidersServlet.LOG
                            .error("Namespace is defined on a provider but not in the namespace entries list nextProvider="
                                    + nextProvider.getKey().stringValue()
                                    + " nextNamespace="
                                    + nextNamespace.stringValue());
                    namespaceUseWithoutDefinitions.add("nextNamespace=" + nextNamespace.stringValue()
                            + " nextProvider=" + nextProvider.getKey().stringValue());
                }
            }
        }
        
        for(final URI nextNamespaceEntry : allNamespaceEntries.keySet())
        {
            if(!providersByNamespace.containsKey(nextNamespaceEntry))
            {
                NamespaceProvidersServlet.LOG
                        .warn("Namespace entry is defined but it does not have any linked providers nextNamespaceEntry="
                                + nextNamespaceEntry.stringValue());
            }
        }
        
        out.write("<br />Number of namespaces that are known = " + allNamespaceEntries.size() + "<br />\n");
        out.write("<br />Number of namespaces that have providers = " + providersByNamespace.size() + "<br />\n");
        out.write("<br />Number of query titles = " + allQueryTypes.size() + "<br />\n");
        out.write("<br />Number of providers = " + allProviders.size() + "<br />\n");
        out.write("<br />Number of rdf normalisation rules = " + allRdfRules.size() + "<br />\n");
        out.write("<br />Number of rdf normalisation rule tests = " + allRdfRuleTests.size() + "<br />\n");
        out.write("<br />Number of profiles = " + allProfiles.size() + "<br />\n");
        
        out.write("<br />Number of namespace provider options = " + overallNamespaceProviders.toString() + "<br />\n");
        out.write("<br />Number of query title provider options = " + overallQueryTypeProviders.toString() + "<br />\n");
        out.write("<br />Number of query title and namespace combinations = " + allQueryTypesByNamespace.size()
                + "<br />\n");
        out.write("<br />Number of query title and namespace combination provider options = "
                + overallQueryTypeByNamespaceProviders.toString() + "<br /><br />\n");
        
        if(namespaceUseWithoutDefinitions.size() > 0)
        {
            out.write("Namespaces found on providers without definitions:");
            out.write("<ul>");
        }
        
        for(final String nextDebugString : namespaceUseWithoutDefinitions)
        {
            out.write("<li>");
            out.write(nextDebugString);
            out.write("</li>");
        }
        
        if(namespaceUseWithoutDefinitions.size() > 0)
        {
            out.write("</ul>");
        }
        
        out.write("Raw complete namespace Collection<br />\n");
        
        for(final URI nextUniqueNamespace : providersByNamespace.keySet())
        {
            out.write(nextUniqueNamespace.stringValue());
            out.write(",");
        }
        
        for(final URI nextUniqueNamespace : providersByNamespace.keySet())
        {
            if(nextUniqueNamespace == null)
            {
                continue;
            }
            
            out.write("<h3><span class='debug'>Namespace=");
            out.write(nextUniqueNamespace.stringValue());
            out.write("</span></h3>\n");
            
            final Collection<Provider> providersForNextNamespace = providersByNamespace.get(nextUniqueNamespace);
            
            if(providersForNextNamespace.size() == 0)
            {
                out.write("NO Providers known for this namespace");
                NamespaceProvidersServlet.LOG.info("No providers known for namespace="
                        + nextUniqueNamespace.stringValue());
            }
            else
            {
                final Collection<URI> implementedQueriesForNextNamespace = new ArrayList<URI>();
                
                for(final Provider nextProviderForNextNamespace : providersForNextNamespace)
                {
                    for(final URI nextIncludedQuery : nextProviderForNextNamespace.getIncludedInQueryTypes())
                    {
                        if(!implementedQueriesForNextNamespace.contains(nextIncludedQuery))
                        {
                            implementedQueriesForNextNamespace.add(nextIncludedQuery);
                        }
                    }
                }
                
                out.write("Queries for this namespace (");
                out.write(implementedQueriesForNextNamespace.size());
                out.write(")");
                
                if(implementedQueriesForNextNamespace.size() > 0)
                {
                    out.write("<ol>");
                }
                
                for(final URI nextImplementedQuery : implementedQueriesForNextNamespace)
                {
                    out.write("<li>");
                    out.write(nextImplementedQuery.stringValue());
                    out.write("</li>");
                }
                
                if(implementedQueriesForNextNamespace.size() > 0)
                {
                    out.write("</ol>");
                }
            }
        }
        
        for(final URI nextUniqueQuery : providersByQueryKey.keySet())
        {
            if(nextUniqueQuery == null)
            {
                continue;
            }
            
            out.write("<h3><span class='debug'>Query=");
            out.write(nextUniqueQuery.stringValue());
            out.write("</span></h3>\n");
            
            final Collection<Provider> providersForNextQuery = providersByQueryKey.get(nextUniqueQuery);
            
            final List<URI> implementedNamespacesForNextQuery = new ArrayList<URI>();
            
            for(final Provider nextProviderForNextQuery : providersForNextQuery)
            {
                for(final URI nextIncludedNamespace : nextProviderForNextQuery.getNamespaces())
                {
                    if(!implementedNamespacesForNextQuery.contains(nextIncludedNamespace))
                    {
                        implementedNamespacesForNextQuery.add(nextIncludedNamespace);
                    }
                }
            }
            
            out.write("Namespaces for this query (");
            out.write(implementedNamespacesForNextQuery.size());
            out.write(")");
            
            if(implementedNamespacesForNextQuery.size() > 0)
            {
                out.write("<ol>");
            }
            
            for(final URI nextImplementedNamespace : implementedNamespacesForNextQuery)
            {
                out.write("<li>");
                out.write(nextImplementedNamespace.stringValue());
                out.write("</li>");
            }
            
            if(implementedNamespacesForNextQuery.size() > 0)
            {
                out.write("</ol>");
            }
        }
        
        if(NamespaceProvidersServlet.LOG.isTraceEnabled())
        {
            out.write("<h2>Consistency analysis:</h2>");
            for(final URI nextUniqueNamespace : providersByNamespace.keySet())
            {
                for(final URI nextUniqueQueryTitle : providersByQueryKey.keySet())
                {
                    // final QueryType queryForNextTitle =
                    // localSettings.getAllQueryTypes().get(nextUniqueQueryTitle);
                    
                    final Map<URI, Provider> queryTypesForNamespace =
                            ProviderUtils.getProvidersSupportingQueryType(allProviders, nextUniqueQueryTitle);
                    
                    if(queryTypesForNamespace.size() > 0)
                    {
                        if(NamespaceProvidersServlet.LOG.isTraceEnabled())
                        {
                            out.write("<span class='info'>Provider found for namespace and query : nextUniqueQueryTitle=");
                            out.write(nextUniqueQueryTitle.stringValue());
                            out.write(" nextUniqueNamespace=");
                            out.write(nextUniqueNamespace.stringValue());
                            out.write("</span><br />\n");
                            // log.debug("Provider found for namespace and query : nextUniqueQueryTitle="+nextUniqueQueryTitle+" nextUniqueNamespace="+nextUniqueNamespace);
                        }
                        
                        for(final Provider nextQueryNamespaceProvider : queryTypesForNamespace.values())
                        {
                            if(nextQueryNamespaceProvider instanceof HttpProvider)
                            {
                                final HttpProvider nextHttpProvider = (HttpProvider)nextQueryNamespaceProvider;
                                if(nextHttpProvider.getEndpointUrls() != null)
                                {
                                    for(final String nextEndpointUrl : nextHttpProvider.getEndpointUrls())
                                    {
                                        if(NamespaceProvidersServlet.LOG.isTraceEnabled())
                                        {
                                            out.write("<li><span class='debug'><a href='");
                                            out.write(nextEndpointUrl);
                                            out.write("'>");
                                            out.write(nextEndpointUrl);
                                            
                                            if(nextQueryNamespaceProvider instanceof SparqlProvider)
                                            {
                                                final SparqlProvider nextSparqlProvider =
                                                        (SparqlProvider)nextQueryNamespaceProvider;
                                                if(nextSparqlProvider.getUseSparqlGraph())
                                                {
                                                    out.write(" graph=");
                                                    out.write(nextSparqlProvider.getSparqlGraphUri());
                                                }
                                            }
                                            
                                            out.write("</a></span></li>\n");
                                        }
                                    }
                                }
                            }
                            else if(nextQueryNamespaceProvider.getEndpointMethod().equals(
                                    ProviderSchema.getProviderNoCommunication()))
                            {
                                if(NamespaceProvidersServlet.LOG.isTraceEnabled())
                                {
                                    out.write("<li><span class='debug'>No communication required</span></li><br />\n");
                                }
                            }
                            else
                            {
                                if(NamespaceProvidersServlet.LOG.isTraceEnabled())
                                {
                                    out.write("<li><span class='debug'>No endpoint URL's found for a particular provider</span></li><br />\n");
                                }
                            }
                        }
                    }
                    else
                    {
                        // Enable this to get some rarely meaningful notices about queries which
                        // are not designed to handle all namespaces
                        // if(log.isDebugEnabled())
                        // {
                        // log.debug("No provider was found for a namespace for a particular queryTitle nextUniqueQueryTitle="+nextUniqueQueryTitle+" nextUniqueNamespace="+nextUniqueNamespace);
                        // out.write("<span class='debug'>No provider was found for a namespace for a particular queryTitle nextUniqueQueryTitle="+nextUniqueQueryTitle+" nextUniqueNamespace="+nextUniqueNamespace+" </span><br />\n");
                        // }
                    }
                }
            }
        }
    }
    
}
