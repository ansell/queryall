package org.queryall.queryutils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

import org.apache.log4j.Logger;
import org.queryall.api.QueryAllConfiguration;
import org.queryall.blacklist.BlacklistController;
import org.queryall.utils.StringUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfFetcher
{
    private static final Logger log = Logger.getLogger(RdfFetcher.class.getName());
    private static final boolean _TRACE = RdfFetcher.log.isTraceEnabled();
    private static final boolean _DEBUG = RdfFetcher.log.isDebugEnabled();
    private static final boolean _INFO = RdfFetcher.log.isInfoEnabled();
    
    public static String SPARQL_QUERY_OPERATION = "SPARQL_QUERY_OPERATION";
    public static String RDF_XML_FETCH_OPERATION = "RDF_XML_FETCH_OPERATION";
    public static String HTTP_POST_OPERATION = "HTTP_POST_OPERATION";
    
    public String lastReturnedContentType = null;
    public String lastReturnedContentEncoding = null;
    private QueryAllConfiguration localSettings;
    private BlacklistController localBlacklistController;
    
    public RdfFetcher(final QueryAllConfiguration localSettings, final BlacklistController blacklistController)
    {
        this.localSettings = localSettings;
        this.localBlacklistController = blacklistController;
    }
    
    // If postInformation is empty String "" or null then we assume they did not want to post
    public String getDocumentFromUrl(final String endpointUrl, final String postInformation, final String acceptHeader)
        throws java.net.SocketTimeoutException, java.net.ConnectException, java.net.UnknownHostException, Exception
    {
        if(RdfFetcher._DEBUG)
        {
            RdfFetcher.log.debug("RdfFetcher.getDocumentFromUrl: endpointUrl=" + endpointUrl
                    + " Settings.getStringPropertyFromConfig(\"connectTimeout\")="
                    + this.localSettings.getIntProperty("connectTimeout", 0));
        }
        
        final long start = System.currentTimeMillis();
        
        OutputStreamWriter out = null;
        BufferedReader inputStream = null;
        
        HttpURLConnection conn = null;
        
        final URL url = new URL(endpointUrl);
        
        final StringBuilder results = new StringBuilder();
        
        boolean errorOccured = false;
        
        // http://forums.sun.com/thread.jspa?messageID=9552813#9552813
        
        try
        {
            if(RdfFetcher._TRACE)
            {
                // TODO: do the blocking and querying based on the Ips and not the hostname
                final InetAddress[] allIpsForEndpoint = InetAddress.getAllByName(url.getHost());
                
                for(final InetAddress nextAddress : allIpsForEndpoint)
                {
                    RdfFetcher.log.trace("RdfFetcher.getDocumentFromUrl: IP for endpointUrl=" + endpointUrl
                            + " url.getHost()=" + url.getHost() + " endpoint=" + nextAddress.getHostAddress());
                }
            }
            
            this.localBlacklistController.accumulateQueryTotal(url.getProtocol() + "://" + url.getHost());
            
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (compatible; "
                            + this.localSettings.getStringProperty("userAgent", "queryall")
                            + "/"
                            + Settings.VERSION
                            + " +"
                            + this.localSettings.getStringProperty("robotHelpUrl",
                                    "https://sourceforge.net/apps/mediawiki/bio2rdf/index.php?title=RobotHelp") + ")");
            
            if(acceptHeader != null && !acceptHeader.equals(""))
            {
                conn.setRequestProperty("Accept", acceptHeader);
            }
            
            conn.setUseCaches(this.localSettings.getBooleanProperty("useRequestCache", true));
            conn.setConnectTimeout(this.localSettings.getIntProperty("connectTimeout", 2000));
            conn.setReadTimeout(this.localSettings.getIntProperty("readTimeout", 30000));
            
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
                
                inputStream =
                        new BufferedReader(new InputStreamReader(conn.getInputStream(), conn.getContentEncoding()));
            }
            else
            {
                inputStream = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            
            this.lastReturnedContentType = conn.getContentType();
            this.lastReturnedContentEncoding = conn.getContentEncoding();
            
            String line;
            
            while((line = inputStream.readLine()) != null)
            {
                if(RdfFetcher._TRACE)
                {
                    RdfFetcher.log.trace("RdfFetcher.getDocumentFromUrl: endpointUrl=" + endpointUrl
                            + " lastReturnedContentEncoding=" + this.lastReturnedContentEncoding + " line=" + line);
                }
                
                results.append(line + "\n");
            }
        }
        catch(final java.net.UnknownHostException uhe)
        {
            if(RdfFetcher._INFO)
            {
                RdfFetcher.log.info("RdfFetcher.getDocumentFromUrl: Unknown Host Exception occurred endpointUrl="
                        + endpointUrl);
            }
            
            errorOccured = true;
            
            throw uhe;
        }
        catch(final java.net.ConnectException ce)
        {
            if(RdfFetcher._INFO)
            {
                RdfFetcher.log.info("RdfFetcher.getDocumentFromUrl: Connect Exception occurred endpointUrl="
                        + endpointUrl);
            }
            
            errorOccured = true;
            
            throw ce;
        }
        catch(final java.net.SocketTimeoutException ste)
        {
            if(RdfFetcher._INFO)
            {
                RdfFetcher.log.info("RdfFetcher.getDocumentFromUrl: Socket Timeout Exception occurred endpointUrl="
                        + endpointUrl);
            }
            
            errorOccured = true;
            
            throw ste;
        }
        catch(final java.net.SocketException se)
        {
            if(RdfFetcher._INFO)
            {
                RdfFetcher.log.info("RdfFetcher.getDocumentFromUrl: Socket Exception occurred endpointUrl="
                        + endpointUrl);
            }
            
            errorOccured = true;
            
            throw se;
        }
        catch(final Exception ex)
        {
            if(RdfFetcher._INFO)
            {
                RdfFetcher.log.info("RdfFetcher.getDocumentFromUrl: Unknown exception occurred endpointUrl="
                        + endpointUrl + " type=" + ex.getClass().getName() + " message=" + ex.getMessage());
            }
            
            errorOccured = true;
            
            throw ex;
        }
        finally
        {
            if(out != null)
            {
                out.close();
            }
            
            if(inputStream != null)
            {
                inputStream.close();
            }
            
            final long end = System.currentTimeMillis();
            
            if(RdfFetcher._DEBUG)
            {
                RdfFetcher.log.debug(String.format("%s: timing=%10d", "RdfFetcher.getDocumentFromUrl.end",
                        (end - start)));
            }
            
            if(errorOccured)
            {
                // having conn.getResponseCode() slows everything down apparently if there was an
                // error
                // and makes it hard to find out where the error occurred even
                // BlacklistController.accumulateHttpResponseError(url.getProtocol()+"://"+url.getHost(),
                // conn.getResponseCode());
                this.localBlacklistController.accumulateHttpResponseError(url.getProtocol() + "://" + url.getHost(), 1);
                
                if(RdfFetcher._DEBUG)
                {
                    final long errorend = System.currentTimeMillis();
                    
                    RdfFetcher.log.debug(String.format("%s: timing=%10d", "RdfFetcher.getDocumentFromUrl.errorend",
                            (errorend - end)));
                }
            }
        }
        
        if(RdfFetcher._TRACE)
        {
            RdfFetcher.log.trace("RdfFetcher.getDocumentFromUrl: results.toString()=" + results.toString());
        }
        
        return results.toString();
    }
    
    public String submitSparqlQuery(final String endpointUrl, final String format, final String defaultGraphUri,
            final String query, final String debug, final int maxRowsParameter, final String acceptHeader)
        throws java.net.SocketTimeoutException, java.net.ConnectException, java.net.UnknownHostException, Exception
    {
        if(RdfFetcher._DEBUG)
        {
            RdfFetcher.log.debug("RdfFetcher.submitSparqlQuery: endpointUrl=" + endpointUrl + " query=" + query);
        }
        
        final long start = System.currentTimeMillis();
        
        // NOTE: We use POST instead of GET so there is never a chance
        // that the URI will exceed the maximum supported length for a
        // particular HTTP server or intermediate proxy
        String postQuery = "format=" + StringUtils.percentEncode(format) + "&";
        
        if(this.localSettings.getBooleanProperty("useVirtuosoMaxRowsParameter", false))
        {
            postQuery += "maxrows=" + maxRowsParameter + "&";
        }
        
        postQuery += "formatting=Raw&";
        postQuery += "softlimit=50&";
        postQuery += "debug=" + StringUtils.percentEncode(debug) + "&";
        postQuery += "default-graph-uri=" + StringUtils.percentEncode(defaultGraphUri) + "&";
        postQuery += "query=" + StringUtils.percentEncode(query);
        
        if(RdfFetcher._TRACE)
        {
            RdfFetcher.log.trace("RdfFetcher.submitSparqlQuery: postQuery=" + postQuery);
        }
        
        final String results = this.getDocumentFromUrl(endpointUrl, postQuery, acceptHeader);
        
        if(RdfFetcher._DEBUG)
        {
            final long end = System.currentTimeMillis();
            
            RdfFetcher.log.debug(String.format("%s: timing=%10d", "RdfFetcher.submitSparqlQuery", (end - start)));
        }
        
        return results.toString();
    }
    
}
