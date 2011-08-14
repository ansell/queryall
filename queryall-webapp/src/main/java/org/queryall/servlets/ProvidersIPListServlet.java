package org.queryall.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.queryall.api.HttpProvider;
import org.queryall.api.Provider;
import org.queryall.api.QueryAllConfiguration;
import org.queryall.query.Settings;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProvidersIPListServlet extends HttpServlet
{
    /**
	 * 
	 */
    private static final long serialVersionUID = -7006535158409121292L;
    public static final Logger log = LoggerFactory.getLogger(ProvidersIPListServlet.class.getName());
    public static final boolean _TRACE = ProvidersIPListServlet.log.isTraceEnabled();
    public static final boolean _DEBUG = ProvidersIPListServlet.log.isDebugEnabled();
    public static final boolean _INFO = ProvidersIPListServlet.log.isInfoEnabled();
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException
    {
        final QueryAllConfiguration localSettings = Settings.getSettings();
        
        final PrintWriter out = response.getWriter();
        response.setContentType("text/plain");
        
        final Set<String> resultsSet = new HashSet<String>();
        
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
