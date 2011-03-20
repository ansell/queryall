package org.queryall.queryutils;

import org.apache.log4j.Logger;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;

import org.queryall.helpers.*;
import org.queryall.blacklist.*;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfFetcher
{
    private static final Logger log = Logger.getLogger(RdfFetcher.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    private static final boolean _INFO = log.isInfoEnabled();
    
    public static String SPARQL_QUERY_OPERATION = "SPARQL_QUERY_OPERATION";
    public static String RDF_XML_FETCH_OPERATION = "RDF_XML_FETCH_OPERATION";
    public static String HTTP_POST_OPERATION = "HTTP_POST_OPERATION";
    
    public String lastReturnedContentType = null;
    public String lastReturnedContentEncoding = null;
    
    public String submitSparqlQuery
    (
        String endpointUrl,
        String format,
        String defaultGraphUri,
        String query,
        String debug,
        int maxRowsParameter,
        String acceptHeader
    )
    throws java.net.SocketTimeoutException, java.net.ConnectException, Exception
    {
        if(_DEBUG)
        {
            log.debug("RdfFetcher.submitSparqlQuery: endpointUrl="+endpointUrl+" query="+query);
        }
        
        final long start = System.currentTimeMillis();
        
        // NOTE: We use POST instead of GET so there is never a chance
        // that the URI will exceed the maximum supported length for a
        // particular HTTP server or intermediate proxy
        String postQuery = "format="+StringUtils.percentEncode(format)+"&";
        
        if(Settings.getSettings().getBooleanPropertyFromConfig("useVirtuosoMaxRowsParameter", true))
            postQuery += "maxrows="+maxRowsParameter+"&";
        
        postQuery += "formatting=Raw&";
        postQuery += "softlimit=50&";
        postQuery += "debug="+StringUtils.percentEncode(debug)+"&";
        postQuery += "default-graph-uri="+StringUtils.percentEncode(defaultGraphUri)+"&";
        postQuery += "query="+StringUtils.percentEncode(query);
        
        if(_TRACE)
        {
            log.trace("RdfFetcher.submitSparqlQuery: postQuery="+postQuery);
        }
        
        String results = getDocumentFromUrl(endpointUrl, postQuery, acceptHeader);
        
        if(_DEBUG)
        {
            final long end = System.currentTimeMillis();
            
            log.debug(String.format("%s: timing=%10d", "RdfFetcher.submitSparqlQuery", (end - start)));
        }
        
        return results.toString();
    }
    
    // If postInformation is empty String "" or null then we assume they did not want to post
    public String getDocumentFromUrl(String endpointUrl, String postInformation, String acceptHeader)
    throws java.net.SocketTimeoutException, java.net.ConnectException, Exception
    {
        if(_DEBUG)
        {
            log.debug("RdfFetcher.getDocumentFromUrl: endpointUrl="+endpointUrl+" Settings.getStringPropertyFromConfig(\"connectTimeout\")="+Settings.getSettings().getIntPropertyFromConfig("connectTimeout", 0));
        }
        
        final long start = System.currentTimeMillis();
        
        
        OutputStreamWriter out = null;
        BufferedReader inputStream = null;
        
        HttpURLConnection conn = null;
        
        URL url = new URL(endpointUrl);
        
        StringBuilder results = new StringBuilder();
        
        boolean errorOccured = false;
        
        // http://forums.sun.com/thread.jspa?messageID=9552813#9552813
        
        try
        {
            if(_TRACE)
            {
                // TODO: do the blocking and querying based on the Ips and not the hostname
                InetAddress[] allIpsForEndpoint = InetAddress.getAllByName(url.getHost());
                
                for(InetAddress nextAddress : allIpsForEndpoint)
                {
                    log.trace("RdfFetcher.getDocumentFromUrl: IP for endpointUrl="+endpointUrl+ " url.getHost()="+url.getHost()+" endpoint=" + nextAddress.getHostAddress());
                }
            }
            
            BlacklistController.accumulateQueryTotal(url.getProtocol()+"://"+url.getHost());
            
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; "+Settings.getSettings().getStringPropertyFromConfig("userAgent", "") + " +http://bio2rdf.wiki.sourceforge.net/RobotHelp)");
            
            if(acceptHeader != null && !acceptHeader.equals(""))
            {
                conn.setRequestProperty("Accept", acceptHeader);
            }
            
            conn.setUseCaches(Settings.getSettings().getBooleanPropertyFromConfig("useRequestCache", true));
            conn.setConnectTimeout(Settings.getSettings().getIntPropertyFromConfig("connectTimeout", 0));
            conn.setReadTimeout(Settings.getSettings().getIntPropertyFromConfig("readTimeout", 0));
            
            if(postInformation != null && !postInformation.trim().equals(""))
            {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                
                out = new OutputStreamWriter(conn.getOutputStream());
                
                out.write(postInformation);
                out.flush();
            }
            else
            {
                conn.setRequestMethod("GET");
            }
            
            // conn.setDoInput(true);
            
            if(conn.getContentEncoding() != null)
            {
                
                inputStream = new BufferedReader(new InputStreamReader(conn.getInputStream(),conn.getContentEncoding()));
            }
            else
            {
                inputStream = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            
            lastReturnedContentType = conn.getContentType();
            lastReturnedContentEncoding = conn.getContentEncoding();
            
            String line;
            
            while((line = inputStream.readLine()) != null)
            {
                if(_TRACE)
                {
                    log.trace("RdfFetcher.getDocumentFromUrl: endpointUrl="+endpointUrl+" lastReturnedContentEncoding="+lastReturnedContentEncoding + " line="+line);
                }
                
                results.append(line + "\n");
            }
        }
        catch(java.net.ConnectException ce)
        {
            if(_INFO)
            {
                log.info("RdfFetcher.getDocumentFromUrl: Connect Exception occurred endpointUrl="+endpointUrl);
            }
            
            errorOccured = true;
            
            throw ce;
        }
        catch(java.net.SocketTimeoutException ste)
        {
            if(_INFO)
            {
                log.info("RdfFetcher.getDocumentFromUrl: Socket Timeout Exception occurred endpointUrl="+endpointUrl);
            }
            
            errorOccured = true;
            
            throw ste;
        }
        catch(java.net.SocketException se)
        {
            if(_INFO)
            {
                log.info("RdfFetcher.getDocumentFromUrl: Socket Exception occurred endpointUrl="+endpointUrl);
            }
            
            errorOccured = true;
            
            throw se;
        }
        catch(Exception ex)
        {
            if(_INFO)
            {
                log.info("RdfFetcher.getDocumentFromUrl: Unknown exception occurred endpointUrl="+endpointUrl+ " type="+ex.getClass().getName()+ " message="+ex.getMessage());
            }
            
            errorOccured = true;
            
            throw ex;
        }
        finally
        {
            if(out != null)
                out.close();
            
            if(inputStream != null)
                inputStream.close();
            
            final long end = System.currentTimeMillis();
            
            if(_DEBUG)
            {
                log.debug(String.format("%s: timing=%10d", "RdfFetcher.getDocumentFromUrl.end", (end - start)));
            }
            
            if(errorOccured)
            {
                // having conn.getResponseCode() slows everything down apparently if there was an error 
                // and makes it hard to find out where the error occurred even
                //BlacklistController.accumulateHttpResponseError(url.getProtocol()+"://"+url.getHost(), conn.getResponseCode());
                BlacklistController.accumulateHttpResponseError(url.getProtocol()+"://"+url.getHost(), 1);
                
                if(_DEBUG)
                {
                    final long errorend = System.currentTimeMillis();
                    
                    log.debug(String.format("%s: timing=%10d", "RdfFetcher.getDocumentFromUrl.errorend", (errorend - end)));
                }
            }
        }
        
        if(_TRACE)
        {
            log.trace("RdfFetcher.getDocumentFromUrl: results.toString()="+results.toString());
        }
        
        
        return results.toString();
    }
    
}
