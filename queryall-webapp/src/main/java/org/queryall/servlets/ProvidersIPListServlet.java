package org.queryall.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.provider.HttpProvider;
import org.queryall.api.provider.Provider;
import org.queryall.api.provider.SparqlProvider;
import org.queryall.blacklist.BlacklistController;
import org.queryall.query.RdfFetchController;
import org.queryall.query.RdfFetcherQueryRunnable;
import org.queryall.query.RdfFetcherQueryRunnableImpl;
import org.queryall.query.RdfFetcherSparqlQueryRunnableImpl;
import org.queryall.servlets.helpers.SettingsContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProvidersIPListServlet extends HttpServlet
{
    /**
	 * 
	 */
    private static final long serialVersionUID = -7006535158409121292L;
    public static final Logger log = LoggerFactory.getLogger(ProvidersIPListServlet.class);
    public static final boolean TRACE = ProvidersIPListServlet.log.isTraceEnabled();
    public static final boolean DEBUG = ProvidersIPListServlet.log.isDebugEnabled();
    public static final boolean INFO = ProvidersIPListServlet.log.isInfoEnabled();
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException
    {
        final QueryAllConfiguration localSettings =
                (QueryAllConfiguration)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_CONFIG);
        final BlacklistController localBlacklistController =
                (BlacklistController)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_BLACKLIST);
        
        final PrintWriter out = response.getWriter();
        response.setContentType("text/plain");
        
        final Set<String> resultsSet = new HashSet<String>();
        
        final Collection<RdfFetcherQueryRunnable> sparqlThreads = new ArrayList<RdfFetcherQueryRunnable>();
        
        for(final Provider nextProvider : localSettings.getAllProviders().values())
        {
            if(nextProvider instanceof HttpProvider)
            {
                final HttpProvider nextHttpProvider = (HttpProvider)nextProvider;
                if(nextHttpProvider.hasEndpointUrl())
                {
                    for(final String nextEndpoint : nextHttpProvider.getEndpointUrls())
                    {
                        int lastSlash = -1;
                        if(nextEndpoint.startsWith("http://"))
                        {
                            lastSlash = nextEndpoint.indexOf("/", 7);
                        }
                        else if(nextEndpoint.startsWith("https://"))
                        {
                            lastSlash = nextEndpoint.indexOf("/", 8);
                        }
                        
                        if(lastSlash < 0)
                        {
                            ProvidersIPListServlet.log.info("could not find another slash for nextEndpoint="
                                    + nextEndpoint);
                            continue;
                        }
                        
                        // Test if the endpoint is responsive to a simple SPARQL query
                        
                        // RdfFetcher test = new RdfFetcher(localSettings,
                        // localBlacklistController);
                        
                        // FIXME: need to have a better way of identifying sparql endpoints
                        // should use instanceof SparqlProvider, but it is implemented in the same
                        // class as HttpProvider in queryall-lib, so it fails miserably
                        if(nextEndpoint.endsWith("/sparql") || nextEndpoint.endsWith("/sparql/"))
                        {
                            String sparqlGraphUri = "";
                            
                            if(((SparqlProvider)nextHttpProvider).getUseSparqlGraph())
                            {
                                sparqlGraphUri = ((SparqlProvider)nextHttpProvider).getSparqlGraphUri();
                            }
                            
                            try
                            {
                                final RdfFetcherQueryRunnable testQueryRunnable =
                                        new RdfFetcherSparqlQueryRunnableImpl(nextEndpoint, sparqlGraphUri,
                                                "CONSTRUCT { ?s ?p ?o . } WHERE { ?s ?p ?o . } LIMIT 5", "nextDebug",
                                                "application/rdf+xml", 5, localSettings, localBlacklistController, null);
                                sparqlThreads.add(testQueryRunnable);
                                
                                // test.submitSparqlQuery(nextEndpoint, "application/rdf+xml",
                                // sparqlGraphUri,
                                // "CONSTRUCT { ?s ?p ?o . } WHERE { ?s ?p ?o . } LIMIT 5", "", 5,
                                // "application/rdf+xml");
                            }
                            catch(final Exception e)
                            {
                                ProvidersIPListServlet.log.error("Error accessing SPARQL endpoint: " + nextEndpoint);
                            }
                        }
                        
                        final String endpointUrl = nextEndpoint.substring(0, lastSlash);
                        
                        try
                        {
                            final URL url = new URL(endpointUrl);
                            
                            final InetAddress[] allIpsForEndpoint = InetAddress.getAllByName(url.getHost());
                            
                            for(final InetAddress nextAddress : allIpsForEndpoint)
                            {
                                resultsSet.add(url.getHost() + "\t" + nextAddress.getHostAddress());
                            }
                        }
                        catch(final java.net.UnknownHostException uhe)
                        {
                            ProvidersIPListServlet.log.error("Found java.net.UnknownHostException for nextEndpoint="
                                    + nextEndpoint + " endpointUrl=" + endpointUrl, uhe);
                        }
                    }
                }
                else
                {
                    ProvidersIPListServlet.log.info("HttpProvider did not have any endpointUrls nextProvider.getKey()="
                            + nextProvider.getKey().stringValue());
                }
            }
            else
            {
                ProvidersIPListServlet.log.info("nextProvider not an instance of HttpProvider nextProvider.getKey()="
                        + nextProvider.getKey().stringValue());
            }
        }
        
        try
        {
            new RdfFetchController().fetchRdfForQueriesWithoutNormalisation(sparqlThreads);
        }
        catch(final InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        final String endOfLine = System.getProperty("line.separator");
        
        final List<String> resultsList = new ArrayList<String>(resultsSet.size());
        
        resultsList.addAll(resultsSet);
        
        resultsSet.clear();
        
        Collections.sort(resultsList);
        
        for(final String nextLine : resultsList)
        {
            out.write(nextLine + endOfLine);
        }
    }
}
