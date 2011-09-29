package org.queryall.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.blacklist.BlacklistController;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfFetcher
{
    private static final Logger log = LoggerFactory.getLogger(RdfFetcher.class);
    private static final boolean _TRACE = RdfFetcher.log.isTraceEnabled();
    private static final boolean _DEBUG = RdfFetcher.log.isDebugEnabled();
    private static final boolean _INFO = RdfFetcher.log.isInfoEnabled();
    
    public static String SPARQL_QUERY_OPERATION = "SPARQL_QUERY_OPERATION";
    public static String RDF_XML_FETCH_OPERATION = "RDF_XML_FETCH_OPERATION";
    public static String HTTP_POST_OPERATION = "HTTP_POST_OPERATION";
    
    private String lastReturnedContentType = null;
    private String lastReturnedContentEncoding = null;
    private Exception lastException = null;
    private int lastStatusCode = 200;
    private boolean lastWasError = false;
    private QueryAllConfiguration localSettings;
    private BlacklistController localBlacklistController;
    
    public RdfFetcher(final QueryAllConfiguration localSettings, final BlacklistController blacklistController)
    {
        this.localSettings = localSettings;
        this.localBlacklistController = blacklistController;
    }
    
    // If postInformation is empty String "" or null then we assume they did not want to post
    public String getDocumentFromUrl(final String endpointUrl, final String postInformation, String acceptHeader)
        throws MalformedURLException
    {
        if(RdfFetcher._DEBUG)
        {
            RdfFetcher.log.debug("RdfFetcher.getDocumentFromUrl: endpointUrl=" + endpointUrl
                    + " Settings.getStringPropertyFromConfig(\"connectTimeout\")="
                    + this.localSettings.getIntProperty("connectTimeout", 3000));
        }
        
        final long start = System.currentTimeMillis();
        
        OutputStreamWriter out = null;
        BufferedReader inputStream = null;
        
        HttpURLConnection conn = null;
        
        final URL url = new URL(endpointUrl);
        
        final StringBuilder results = new StringBuilder();
        
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
            
            // logging currently based on the IP address and the protocol, but not on the port
            // number
            // TODO: do we need to log at the port number level?
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
                acceptHeader =
                        this.localSettings.getStringProperty("defaultAcceptHeader", "application/rdf+xml, text/rdf+n3");
            }
            
            conn.setRequestProperty("Accept", acceptHeader);
            
            conn.setUseCaches(this.localSettings.getBooleanProperty("useRequestCache", true));
            conn.setConnectTimeout(this.localSettings.getIntProperty("connectTimeout", 3000));
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
                try
                {
                    inputStream =
                            new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName(conn
                                    .getContentEncoding())));
                }
                catch(final IllegalArgumentException iae)
                {
                    RdfFetcher.log.error(
                            "Content encoding was not known or valid conn.getContentEncoding()="
                                    + conn.getContentEncoding(), iae);
                    
                    inputStream =
                            new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
                }
            }
            else
            {
                inputStream =
                        new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
            }
            
            this.setLastReturnedContentType(conn.getContentType());
            this.setLastReturnedContentEncoding(conn.getContentEncoding());
            
            String line;
            
            while((line = inputStream.readLine()) != null)
            {
                if(RdfFetcher._TRACE)
                {
                    RdfFetcher.log
                            .trace("RdfFetcher.getDocumentFromUrl: endpointUrl=" + endpointUrl
                                    + " lastReturnedContentEncoding=" + this.getLastReturnedContentEncoding()
                                    + " line=" + line);
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
            
            this.setLastWasError(true);
            
            this.setLastException(uhe);
        }
        catch(final java.net.NoRouteToHostException nrthe)
        {
            if(RdfFetcher._INFO)
            {
                RdfFetcher.log.info("RdfFetcher.getDocumentFromUrl: No Route To Host Exception occurred endpointUrl="
                        + endpointUrl);
            }
            
            this.setLastWasError(true);
            
            this.setLastException(nrthe);
        }
        catch(final java.net.PortUnreachableException pue)
        {
            if(RdfFetcher._INFO)
            {
                RdfFetcher.log.info("RdfFetcher.getDocumentFromUrl: Port Unreachable Exception occurred endpointUrl="
                        + endpointUrl);
            }
            
            this.setLastWasError(true);
            
            this.setLastException(pue);
        }
        catch(final java.net.ConnectException ce)
        {
            if(RdfFetcher._INFO)
            {
                RdfFetcher.log.info("RdfFetcher.getDocumentFromUrl: Connect Exception occurred endpointUrl="
                        + endpointUrl);
            }
            
            this.setLastWasError(true);
            
            this.setLastException(ce);
        }
        catch(final java.net.SocketTimeoutException ste)
        {
            if(RdfFetcher._INFO)
            {
                RdfFetcher.log.info("RdfFetcher.getDocumentFromUrl: Socket Timeout Exception occurred endpointUrl="
                        + endpointUrl);
            }
            
            this.setLastWasError(true);
            
            this.setLastException(ste);
        }
        catch(final java.net.SocketException se)
        {
            if(RdfFetcher._INFO)
            {
                RdfFetcher.log.info("RdfFetcher.getDocumentFromUrl: Socket Exception occurred endpointUrl="
                        + endpointUrl);
            }
            
            this.setLastWasError(true);
            
            this.setLastException(se);
        }
        catch(final java.io.IOException ioe)
        {
            if(RdfFetcher._INFO)
            {
                RdfFetcher.log.info("RdfFetcher.getDocumentFromUrl: Input Output Exception occurred endpointUrl="
                        + endpointUrl);
            }
            
            this.setLastWasError(true);
            
            this.setLastException(ioe);
        }
        finally
        {
            if(out != null)
            {
                try
                {
                    out.close();
                }
                catch(final IOException e)
                {
                    RdfFetcher.log.error("Found error trying to close the output stream", e);
                }
            }
            
            if(inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch(final IOException e)
                {
                    RdfFetcher.log.error("Found error trying to close the input stream", e);
                }
            }
            
            final long end = System.currentTimeMillis();
            
            if(RdfFetcher._DEBUG)
            {
                RdfFetcher.log.debug(String.format("%s: timing=%10d", "RdfFetcher.getDocumentFromUrl.end",
                        (end - start)));
            }
            
            // Note: having conn.getResponseCode() slows everything down it seems, if there was an
            // error and makes it hard to find out where the error occurred even
            // If there are issues with this, turn off the conn.getResponseCode() call here
            try
            {
                this.setLastStatusCode(conn.getResponseCode());
            }
            catch(final IOException e)
            {
                this.setLastStatusCode(1);
                RdfFetcher.log.info("Found error trying to get the response status code", e);
            }
            
            if(this.getLastWasError())
            {
                this.localBlacklistController.accumulateHttpResponseError(url.getProtocol() + "://" + url.getHost(),
                        this.getLastStatusCode());
                
                // Try to debug why there are endpoints responding with 406 suddenly
                // may just be a virtuoso bug, but need some evidence
                if(this.getLastStatusCode() == 406)
                {
                    RdfFetcher.log.error("Found an endpoint that responded with 406 to acceptHeader=" + acceptHeader);
                }
                
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
    
    public Exception getLastException()
    {
        return this.lastException;
    }
    
    /**
     * @return the lastReturnedContentEncoding
     */
    public String getLastReturnedContentEncoding()
    {
        return this.lastReturnedContentEncoding;
    }
    
    /**
     * @return the lastReturnedContentType
     */
    public String getLastReturnedContentType()
    {
        return this.lastReturnedContentType;
    }
    
    public int getLastStatusCode()
    {
        return this.lastStatusCode;
    }
    
    public boolean getLastWasError()
    {
        return this.lastWasError;
    }
    
    protected void setLastException(final Exception lastException)
    {
        this.lastException = lastException;
    }
    
    /**
     * @param lastReturnedContentEncoding
     *            the lastReturnedContentEncoding to set
     */
    protected void setLastReturnedContentEncoding(final String lastReturnedContentEncoding)
    {
        this.lastReturnedContentEncoding = lastReturnedContentEncoding;
    }
    
    /**
     * @param lastReturnedContentType
     *            the lastReturnedContentType to set
     */
    protected void setLastReturnedContentType(final String lastReturnedContentType)
    {
        this.lastReturnedContentType = lastReturnedContentType;
    }
    
    protected void setLastStatusCode(final int lastStatusCode)
    {
        this.lastStatusCode = lastStatusCode;
    }
    
    protected void setLastWasError(final boolean lastWasError)
    {
        this.lastWasError = lastWasError;
    }
    
    public String submitSparqlQuery(final String endpointUrl, final String defaultGraphUri, final String query,
            final String debug, final int maxRowsParameter, final String acceptHeader) throws MalformedURLException
    {
        if(RdfFetcher._DEBUG)
        {
            RdfFetcher.log.debug("RdfFetcher.submitSparqlQuery: endpointUrl=" + endpointUrl + " query=" + query);
        }
        
        final long start = System.currentTimeMillis();
        
        // NOTE: We use POST instead of GET to reduce the chance
        // that the URI will exceed the maximum supported length for a
        // particular HTTP server or intermediate proxy
        String postQuery = "";
        
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
        
        return results;
    }
    
}
