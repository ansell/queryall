package org.bio2rdf.servlets;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.bio2rdf.servlets.html.*;
import org.queryall.queryutils.*;
import org.queryall.statistics.*;
import org.queryall.helpers.*;
import org.queryall.impl.*;

import org.openrdf.*;
import org.openrdf.rio.*;
import org.openrdf.repository.*;
import org.openrdf.repository.sail.*;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.*;

import org.apache.log4j.Logger;

/** 
 * 
 */

public class QueryAllSchemaServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4486511923930733168L;
	public static final Logger log = Logger
			.getLogger(QueryAllSchemaServlet.class.getName());
	public static final boolean _TRACE = log.isTraceEnabled();
	public static final boolean _DEBUG = log.isDebugEnabled();
	public static final boolean _INFO = log.isInfoEnabled();

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		Date queryStartTime = new Date();

		Settings localSettings = Settings.getSettings();

		PrintWriter out = response.getWriter();

		String realHostName = request.getScheme()
				+ "://"
				+ request.getServerName()
				+ (request.getServerPort() == 80
						&& request.getScheme().equals("http") ? "" : ":"
						+ request.getServerPort()) + "/";

		String originalRequestedContentType = QueryallContentNegotiator
				.getResponseContentType(request.getHeader("Accept"),
						request.getHeader("User-Agent"));

		String requestedContentType = originalRequestedContentType;

        String requesterIpAddress = request.getRemoteAddr();
        
		String queryString = (String) request
				.getAttribute("org.queryall.RuleTesterServlet.queryString");

		if(queryString == null)
		{
			queryString = "";
		}

		String locale = request.getLocale().toString();

		String characterEncoding = request.getCharacterEncoding();

		if(_INFO)
		{
			log.info("QueryAllSchemaServlet: locale=" + locale
					+ " characterEncoding=" + characterEncoding);
		}

		String versionParameter = (String) request
				.getAttribute("org.queryall.RuleTesterServlet.apiVersion");

		int apiVersion = Settings.CONFIG_API_VERSION;

		if(versionParameter != null && !versionParameter.equals("")
				&& !Constants.CURRENT.equals(versionParameter))
		{
			try
			{
				apiVersion = Integer.parseInt(versionParameter);
			}
			catch(NumberFormatException nfe)
			{
				log.error("QueryAllSchemaServlet: apiVersion not recognised versionParameter="
						+ versionParameter);
			}
		}

		if(apiVersion > Settings.CONFIG_API_VERSION)
		{
			log.error("QueryAllSchemaServlet: requested API version not supported by this server. apiVersion="
					+ apiVersion
					+ " Settings.CONFIG_API_VERSION="
					+ Settings.CONFIG_API_VERSION);

			response.setContentType("text/plain");
			response.setStatus(400);
			out.write("Requested API version not supported by this server. Current supported version="
					+ Settings.CONFIG_API_VERSION);
			return;
		}

		Collection<String> debugStrings = new HashSet<String>();

		String explicitUrlContentType = (String) request
				.getAttribute("org.queryall.QueryAllSchemaServlet.chosenContentType");

		if(explicitUrlContentType != null && !explicitUrlContentType.equals(""))
		{
			if(log.isInfoEnabled())
			{
				log.info("QueryAllSchemaServlet: explicitUrlContentType="
						+ explicitUrlContentType);
			}

			// override whatever was requested with the urlrewrite variable
			requestedContentType = explicitUrlContentType;
		}

		// even if they request a random format, we need to make sure that Rio
		// has a writer compatible with it, otherwise we revert to one of the
		// defaults as a failsafe mechanism
		RDFFormat writerFormat = Rio
				.getWriterFormatForMIMEType(requestedContentType);

		if(writerFormat == null)
		{
			writerFormat = Rio.getWriterFormatForMIMEType(localSettings
					.getStringPropertyFromConfig("preferredDisplayContentType",
							Constants.APPLICATION_RDF_XML));

			if(writerFormat == null)
			{
				writerFormat = RDFFormat.RDFXML;

				if(!requestedContentType.equals(Constants.TEXT_HTML))
				{
					requestedContentType = Constants.APPLICATION_RDF_XML;

					log.error("QueryAllSchemaServlet: content negotiation failed to find a suitable content type for results. Defaulting to hard coded RDF/XML writer. Please set localSettings.getStringPropertyFromConfig(\"preferredDisplayContentType\") to a MIME type which is understood by the RDF package being used by the servlet to ensure this message doesn't appear.");
				}
			}
			else if(!requestedContentType.equals(Constants.TEXT_HTML))
			{
				requestedContentType = localSettings
						.getStringPropertyFromConfig(
								"preferredDisplayContentType",
								Constants.APPLICATION_RDF_XML);

				log.error("QueryAllSchemaServlet: content negotiation failed to find a suitable content type for results. Defaulting to localSettings.getStringPropertyFromConfig(\"preferredDisplayContentType\")="
						+ localSettings.getStringPropertyFromConfig(
								"preferredDisplayContentType", ""));
			}
		}

		if(log.isInfoEnabled())
		{
			log.info("QueryAllSchemaServlet: requestedContentType="
					+ requestedContentType + " acceptHeader="
					+ request.getHeader("Accept") + " userAgent="
					+ request.getHeader("User-Agent"));
		}

		if(!originalRequestedContentType.equals(requestedContentType))
		{
			log.warn("QueryAllSchemaServlet: originalRequestedContentType was overwritten originalRequestedContentType="
					+ originalRequestedContentType
					+ " requestedContentType="
					+ requestedContentType);
		}

		localSettings.configRefreshCheck(false);

		response.setContentType(requestedContentType);
		response.setCharacterEncoding("UTF-8");

		try
		{
			Repository myRepository = Settings.getSchemas();

			java.io.StringWriter stBuff = new java.io.StringWriter();

			if(requestedContentType.equals(Constants.TEXT_HTML))
			{
				if(_DEBUG)
				{
					log.debug("QueryAllSchemaServlet: about to call html rendering method");
				}

				try
				{
					HtmlPageRenderer.renderHtml(getServletContext(),
							myRepository, stBuff, debugStrings,
							localSettings.getOntologyTermUriPrefix()
									+ queryString,
							localSettings.getOntologyTermUriPrefix()
									+ queryString, realHostName,
							request.getContextPath(), -1, localSettings);
				}
				catch(OpenRDFException ordfe)
				{
					log.error(
							"QueryAllSchemaServlet: couldn't render HTML because of an RDF exception",
							ordfe);
				}
				catch(Exception ex)
				{
					log.error(
							"QueryAllSchemaServlet: couldn't render HTML because of an unknown exception",
							ex);
				}
			}
			else
			{
				RdfUtils.toWriter(myRepository, stBuff, writerFormat);
			}

			String actualRdfString = stBuff.toString();

			if(_TRACE)
			{
				log.trace("QueryAllSchemaServlet: actualRdfString="
						+ actualRdfString);
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

            Date queryEndTime = new Date();
            
            long nextTotalTime = queryEndTime.getTime()-queryStartTime.getTime();
            
			if(_DEBUG)
			{
				log.debug("QueryAllSchemaServlet: finished returning information to client requesterIpAddress="
						+ requesterIpAddress
						+ " queryString="
						+ queryString
						+ " totalTime=" + Long.toString(nextTotalTime));
			}
		}
		catch(RuntimeException rex)
		{
			log.error("QueryAllSchemaServlet.doGet: caught runtime exception",
					rex);
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
