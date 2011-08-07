package org.queryall.servlets;

import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.queryall.api.HttpProvider;
import org.queryall.api.Provider;
import org.queryall.api.QueryAllConfiguration;
import org.queryall.helpers.*;

import org.apache.log4j.Logger;

/** 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProvidersIPListServlet extends HttpServlet 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7006535158409121292L;
	public static final Logger log = Logger.getLogger(ProvidersIPListServlet.class.getName());
    public static final boolean _TRACE = log.isTraceEnabled();
    public static final boolean _DEBUG = log.isDebugEnabled();
    public static final boolean _INFO = log.isInfoEnabled();

    
    @Override
    public void doGet(HttpServletRequest request,
                        HttpServletResponse response)
        throws ServletException, IOException 
    {
    	QueryAllConfiguration localSettings = Settings.getSettings();
        
        PrintWriter out = response.getWriter();
        response.setContentType("text/plain");
        
        Set<String> resultsSet = new HashSet<String>();
        
        for(Provider nextProvider : localSettings.getAllProviders().values())
        {
        	if(nextProvider instanceof HttpProvider)
        	{
        		HttpProvider nextHttpProvider = (HttpProvider)nextProvider;
        		if(nextHttpProvider.hasEndpointUrl())
        		{
        			for(String nextEndpoint : nextHttpProvider.getEndpointUrls())
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
        					log.info("could not find another slash for nextEndpoint="+nextEndpoint);
        					continue;
        				}
        				
        				String endpointUrl = nextEndpoint.substring(0, lastSlash);
        				
        				try
        				{
	        				URL url = new URL(endpointUrl);
	
	        		        InetAddress[] allIpsForEndpoint = InetAddress.getAllByName(url.getHost());
	        		        
	        		        for(InetAddress nextAddress : allIpsForEndpoint)
	        		        {
	        		        	resultsSet.add(url.getHost()+"\t" + nextAddress.getHostAddress());
	        		        }
        				}
        				catch(java.net.UnknownHostException uhe)
        				{
        					log.error("Found java.net.UnknownHostException for nextEndpoint="+nextEndpoint+" endpointUrl="+endpointUrl ,uhe);
        				}
        			}
        		}
        		else
        		{
            		log.info("HttpProvider did not have any endpointUrls nextProvider.getKey()="+nextProvider.getKey().stringValue());
        		}
        	}
        	else
        	{
        		log.info("nextProvider not an instance of HttpProvider nextProvider.getKey()="+nextProvider.getKey().stringValue());
        	}
        }
        
        String endOfLine = System.getProperty("line.separator");
        
        List<String> resultsList = new ArrayList<String>(resultsSet.size());

        resultsList.addAll(resultsSet);
        
        resultsSet.clear();
        
        Collections.sort(resultsList);

        for(String nextLine : resultsList)
        {
        	out.write(nextLine+endOfLine);
        }
    }
}

