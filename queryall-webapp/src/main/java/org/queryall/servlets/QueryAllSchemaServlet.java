package org.queryall.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.VelocityEngine;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.PropertyUtils;
import org.queryall.api.utils.Schema;
import org.queryall.negotiation.QueryallContentNegotiator;
import org.queryall.query.Settings;
import org.queryall.servlets.helpers.SettingsContextListener;
import org.queryall.servlets.html.HtmlPageRenderer;
import org.queryall.utils.RdfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryAllSchemaServlet extends HttpServlet
{
    /**
	 * 
	 */
    private static final long serialVersionUID = -4486511923930733168L;
    public static final Logger log = LoggerFactory.getLogger(QueryAllSchemaServlet.class);
    public static final boolean _TRACE = QueryAllSchemaServlet.log.isTraceEnabled();
    public static final boolean _DEBUG = QueryAllSchemaServlet.log.isDebugEnabled();
    public static final boolean _INFO = QueryAllSchemaServlet.log.isInfoEnabled();
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException
    {
        final Date queryStartTime = new Date();
        
        final QueryAllConfiguration localSettings =
                (QueryAllConfiguration)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_CONFIG);
        final ContentTypeNegotiator localContentTypeNegotiator =
                (ContentTypeNegotiator)this.getServletContext().getAttribute(
                        SettingsContextListener.QUERYALL_CONTENTNEGOTIATOR);
        final VelocityEngine localVelocityEngine =
                (VelocityEngine)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_VELOCITY);
        
        final PrintWriter out = response.getWriter();
        
        final String realHostName =
                request.getScheme()
                        + "://"
                        + request.getServerName()
                        + (request.getServerPort() == 80 && request.getScheme().equals("http") ? "" : ":"
                                + request.getServerPort()) + "/";
        
        final String originalRequestedContentType =
                QueryallContentNegotiator.getResponseContentType(request.getHeader("Accept"),
                        request.getHeader("User-Agent"), localContentTypeNegotiator,
                        localSettings.getStringProperty("preferredDisplayContentType", Constants.APPLICATION_RDF_XML));
        
        String requestedContentType = originalRequestedContentType;
        
        final String requesterIpAddress = request.getRemoteAddr();
        
        String queryString = (String)request.getAttribute("org.queryall.RuleTesterServlet.queryString");
        
        if(queryString == null)
        {
            queryString = "";
        }
        
        final String locale = request.getLocale().toString();
        
        final String characterEncoding = request.getCharacterEncoding();
        
        if(QueryAllSchemaServlet._INFO)
        {
            QueryAllSchemaServlet.log.info("QueryAllSchemaServlet: locale=" + locale + " characterEncoding="
                    + characterEncoding);
        }
        
        final String versionParameter = (String)request.getAttribute("org.queryall.RuleTesterServlet.apiVersion");
        
        int apiVersion = Settings.CONFIG_API_VERSION;
        
        if(versionParameter != null && !versionParameter.equals("") && !Constants.CURRENT.equals(versionParameter))
        {
            try
            {
                apiVersion = Integer.parseInt(versionParameter);
            }
            catch(final NumberFormatException nfe)
            {
                QueryAllSchemaServlet.log.error("QueryAllSchemaServlet: apiVersion not recognised versionParameter="
                        + versionParameter);
            }
        }
        
        if(apiVersion > Settings.CONFIG_API_VERSION)
        {
            QueryAllSchemaServlet.log
                    .error("QueryAllSchemaServlet: requested API version not supported by this server. apiVersion="
                            + apiVersion + " Settings.CONFIG_API_VERSION=" + Settings.CONFIG_API_VERSION);
            
            response.setContentType("text/plain");
            response.setStatus(400);
            out.write("Requested API version not supported by this server. Current supported version="
                    + Settings.CONFIG_API_VERSION);
            return;
        }
        
        final Collection<String> debugStrings = new HashSet<String>();
        
        final String explicitUrlContentType =
                (String)request.getAttribute("org.queryall.QueryAllSchemaServlet.chosenContentType");
        
        if(explicitUrlContentType != null && !explicitUrlContentType.equals(""))
        {
            if(QueryAllSchemaServlet.log.isInfoEnabled())
            {
                QueryAllSchemaServlet.log.info("QueryAllSchemaServlet: explicitUrlContentType="
                        + explicitUrlContentType);
            }
            
            // override whatever was requested with the urlrewrite variable
            requestedContentType = explicitUrlContentType;
        }
        
        // even if they request a random format, we need to make sure that Rio
        // has a writer compatible with it, otherwise we revert to one of the
        // defaults as a failsafe mechanism
        // Make sure that their requestedContentType is valid as an RDFFormat, or is text/html using
        // this method
        requestedContentType =
                RdfUtils.findBestContentType(requestedContentType, localSettings.getStringProperty(
                        Constants.PREFERRED_DISPLAY_CONTENT_TYPE, Constants.APPLICATION_RDF_XML),
                        Constants.APPLICATION_RDF_XML);
        
        // this will be null if they chose text/html, but it will be a valid format in other cases
        // due to the above method
        final RDFFormat writerFormat = RdfUtils.getWriterFormat(requestedContentType);
        
        if(QueryAllSchemaServlet.log.isInfoEnabled())
        {
            QueryAllSchemaServlet.log.info("QueryAllSchemaServlet: requestedContentType=" + requestedContentType
                    + " acceptHeader=" + request.getHeader("Accept") + " userAgent=" + request.getHeader("User-Agent"));
        }
        
        if(!originalRequestedContentType.equals(requestedContentType))
        {
            QueryAllSchemaServlet.log
                    .warn("QueryAllSchemaServlet: originalRequestedContentType was overwritten originalRequestedContentType="
                            + originalRequestedContentType + " requestedContentType=" + requestedContentType);
        }
        
        ((Settings)localSettings).configRefreshCheck(false);
        
        response.setContentType(requestedContentType);
        response.setCharacterEncoding("UTF-8");
        
        try
        {
            final Repository myRepository = new SailRepository(new MemoryStore());
            
            Schema.getSchemas(myRepository, Settings.CONFIG_API_VERSION);
            
            final java.io.StringWriter stBuff = new java.io.StringWriter();
            
            if(requestedContentType.equals(Constants.TEXT_HTML))
            {
                if(QueryAllSchemaServlet._DEBUG)
                {
                    QueryAllSchemaServlet.log.debug("QueryAllSchemaServlet: about to call html rendering method");
                }
                
                try
                {
                    HtmlPageRenderer.renderHtml(
                            localVelocityEngine,
                            localSettings,
                            myRepository,
                            stBuff,
                            PropertyUtils.getSystemOrPropertyString("queryall.ontologyPrefix",
                                    "http://purl.org/queryall/") + queryString,
                            PropertyUtils.getSystemOrPropertyString("queryall.ontologyPrefix",
                                    "http://purl.org/queryall/") + queryString, realHostName, request.getContextPath(),
                            -1, debugStrings);
                }
                catch(final OpenRDFException ordfe)
                {
                    QueryAllSchemaServlet.log.error(
                            "QueryAllSchemaServlet: couldn't render HTML because of an RDF exception", ordfe);
                }
                catch(final Exception ex)
                {
                    QueryAllSchemaServlet.log.error(
                            "QueryAllSchemaServlet: couldn't render HTML because of an unknown exception", ex);
                }
            }
            else
            {
                RdfUtils.toWriter(myRepository, stBuff, writerFormat);
            }
            
            final String actualRdfString = stBuff.toString();
            
            if(QueryAllSchemaServlet._TRACE)
            {
                QueryAllSchemaServlet.log.trace("QueryAllSchemaServlet: actualRdfString=" + actualRdfString);
            }
            
            if(requestedContentType.equals(Constants.APPLICATION_RDF_XML))
            {
                out.write(actualRdfString);
            }
            else if(requestedContentType.equals(Constants.TEXT_RDF_N3))
            {
                out.write(actualRdfString);
            }
            else
            {
                out.write(actualRdfString);
            }
            
            final Date queryEndTime = new Date();
            
            final long nextTotalTime = queryEndTime.getTime() - queryStartTime.getTime();
            
            if(QueryAllSchemaServlet._DEBUG)
            {
                QueryAllSchemaServlet.log
                        .debug("QueryAllSchemaServlet: finished returning information to client requesterIpAddress="
                                + requesterIpAddress + " queryString=" + queryString + " totalTime="
                                + Long.toString(nextTotalTime));
            }
        }
        catch(final RuntimeException rex)
        {
            QueryAllSchemaServlet.log.error("QueryAllSchemaServlet.doGet: caught runtime exception", rex);
        }
        finally
        {
            if(out != null)
            {
                out.flush();
            }
        }
    }
}
