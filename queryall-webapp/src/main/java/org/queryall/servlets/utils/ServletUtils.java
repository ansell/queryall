/**
 * 
 */
package org.queryall.servlets.utils;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.openrdf.repository.Repository;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.profile.Profile;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.SortOrder;
import org.queryall.exception.QueryAllException;
import org.queryall.exception.UnnormalisableRuleException;
import org.queryall.query.RdfFetchController;
import org.queryall.servlets.GeneralServlet;
import org.queryall.servlets.queryparsers.DefaultQueryOptions;
import org.queryall.utils.RuleUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public class ServletUtils
{

    /**
     * @param redirectString
     *            The StringBuilder that will have the redirect String appended to it
     * @param localSettings
     *            The Settings object
     * @param requestQueryOptions
     *            The query options object
     * @param requestedContentType
     *            The requested content type
     * @param ignoreContextPath
     *            Whether we should ignore the context path or not
     * @param contextPath
     *            The context path from the request
     */
    public static void getRedirectString(final StringBuilder redirectString, final QueryAllConfiguration localSettings,
            final DefaultQueryOptions requestQueryOptions, final String requestedContentType,
            boolean ignoreContextPath, final String contextPath)
    {
        if(localSettings.getBooleanProperty("useHardcodedRequestHostname", false))
        {
            redirectString.append(localSettings.getStringProperty("hardcodedRequestHostname", ""));
        }
        
        if(localSettings.getBooleanProperty("useHardcodedRequestContext", false))
        {
            redirectString.append(localSettings.getStringProperty("hardcodedRequestContext", ""));
            ignoreContextPath = true;
        }
        
        if(!ignoreContextPath)
        {
            if(contextPath.equals(""))
            {
                redirectString.append("/");
            }
            else
            {
                redirectString.append(contextPath);
                redirectString.append("/");
            }
            
        }
        
        if(requestedContentType.equals(Constants.TEXT_HTML))
        {
            redirectString.append(localSettings.getStringProperty("htmlUrlPrefix", "page/"));
            
            if(requestQueryOptions.isQueryPlanRequest())
            {
                redirectString.append(localSettings.getStringProperty("queryplanUrlPrefix", "queryplan/"));
            }
            
            if(requestQueryOptions.containsExplicitPageOffsetValue())
            {
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlOpeningPrefix", "pageoffset"));
                redirectString.append(requestQueryOptions.getPageOffset());
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlClosingPrefix", "/"));
            }
            
            redirectString.append(requestQueryOptions.getParsedRequest());
            
            if(requestQueryOptions.containsExplicitPageOffsetValue())
            {
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlSuffix", ""));
            }
            
            if(requestQueryOptions.isQueryPlanRequest())
            {
                redirectString.append(localSettings.getStringProperty("queryplanUrlSuffix", ""));
            }
            
            redirectString.append(localSettings.getStringProperty("htmlUrlSuffix", ""));
        }
        else if(requestedContentType.equals(Constants.TEXT_RDF_N3))
        {
            redirectString.append(localSettings.getStringProperty("n3UrlPrefix", "n3/"));
            
            if(requestQueryOptions.isQueryPlanRequest())
            {
                redirectString.append(localSettings.getStringProperty("queryplanUrlPrefix", "queryplan/"));
            }
            
            if(requestQueryOptions.containsExplicitPageOffsetValue())
            {
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlOpeningPrefix", "pageoffset"));
                redirectString.append(requestQueryOptions.getPageOffset());
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlClosingPrefix", "/"));
            }
            
            redirectString.append(requestQueryOptions.getParsedRequest());
            
            if(requestQueryOptions.containsExplicitPageOffsetValue())
            {
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlSuffix", ""));
            }
            
            if(requestQueryOptions.isQueryPlanRequest())
            {
                redirectString.append(localSettings.getStringProperty("queryplanUrlSuffix", ""));
            }
            
            redirectString.append(localSettings.getStringProperty("n3UrlSuffix", ""));
        }
        else if(requestedContentType.equals(Constants.APPLICATION_RDF_XML))
        {
            redirectString.append(localSettings.getStringProperty("rdfXmlUrlPrefix", "rdfxml/"));
            
            if(requestQueryOptions.isQueryPlanRequest())
            {
                redirectString.append(localSettings.getStringProperty("queryplanUrlPrefix", "queryplan/"));
            }
            
            if(requestQueryOptions.containsExplicitPageOffsetValue())
            {
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlOpeningPrefix", "pageoffset"));
                redirectString.append(requestQueryOptions.getPageOffset());
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlClosingPrefix", "/"));
            }
            
            redirectString.append(requestQueryOptions.getParsedRequest());
            
            if(requestQueryOptions.containsExplicitPageOffsetValue())
            {
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlSuffix", ""));
            }
            
            if(requestQueryOptions.isQueryPlanRequest())
            {
                redirectString.append(localSettings.getStringProperty("queryplanUrlSuffix", ""));
            }
            
            redirectString.append(localSettings.getStringProperty("rdfXmlUrlSuffix", ""));
        }
        
        else if(requestedContentType.equals(Constants.APPLICATION_JSON))
        {
            redirectString.append(localSettings.getStringProperty("jsonUrlPrefix", "json/"));
            
            if(requestQueryOptions.isQueryPlanRequest())
            {
                redirectString.append(localSettings.getStringProperty("queryplanUrlPrefix", "queryplan/"));
            }
            
            if(requestQueryOptions.containsExplicitPageOffsetValue())
            {
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlOpeningPrefix", "pageoffset"));
                redirectString.append(requestQueryOptions.getPageOffset());
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlClosingPrefix", "/"));
            }
            
            redirectString.append(requestQueryOptions.getParsedRequest());
            
            if(requestQueryOptions.containsExplicitPageOffsetValue())
            {
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlSuffix", ""));
            }
            
            if(requestQueryOptions.isQueryPlanRequest())
            {
                redirectString.append(localSettings.getStringProperty("queryplanUrlSuffix", ""));
            }
            
            redirectString.append(localSettings.getStringProperty("jsonUrlSuffix", ""));
        }
        else
        {
            throw new IllegalArgumentException(
                    "GeneralServlet.getRedirectString: did not recognise requestedContentType=" + requestedContentType);
        }
    }

    /**
     * @param response
     * @param localSettings
     * @param requestQueryOptions
     * @param contextPath
     * @param requestedContentType
     * @return
     */
    public static boolean checkExplicitRedirect(final HttpServletResponse response,
            final QueryAllConfiguration localSettings, final DefaultQueryOptions requestQueryOptions,
            final String contextPath, final String requestedContentType)
    {
        if(!requestQueryOptions.containsExplicitFormat())
        {
            if(localSettings.getBooleanProperty("alwaysRedirectToExplicitFormatUrl", false))
            {
                final int redirectCode = localSettings.getIntProperty("redirectToExplicitFormatHttpCode", 303);
                
                final StringBuilder redirectString = new StringBuilder();
                final boolean ignoreContextPath = false;
                
                getRedirectString(redirectString, localSettings, requestQueryOptions,
                        requestedContentType, ignoreContextPath, contextPath);
                
                if(GeneralServlet._INFO)
                {
                    GeneralServlet.log.info("Sending redirect using redirectCode=" + redirectCode
                            + " to redirectString=" + redirectString.toString());
                }
                if(GeneralServlet._DEBUG)
                {
                    GeneralServlet.log.debug("contextPath=" + contextPath);
                }
                response.setStatus(redirectCode);
                // Cannot use response.sendRedirect as it will change the status to 302, which may
                // not be desired
                response.setHeader("Location", redirectString.toString());
                return true;
            }
        }
        return false;
    }

    /**
     * Encapsulates the call to the pool normalisation method
     * 
     * @param localSettings
     * @param includedProfiles
     * @param fetchController
     * @param myRepository
     *            The repository containing the unnormalised statements
     * @return The repository containing the normalised statements
     * @throws QueryAllException
     */
    public static Repository doPoolNormalisation(final QueryAllConfiguration localSettings,
            final List<Profile> includedProfiles, final RdfFetchController fetchController,
            final Repository myRepository) throws QueryAllException
    {
        try
        {
            return (Repository)RuleUtils.normaliseByStage(
                    NormalisationRuleSchema.getRdfruleStageAfterResultsToPool(),
                    myRepository,
                    RuleUtils.getSortedRulesForProviders(fetchController.getAllUsedProviders(),
                            localSettings.getAllNormalisationRules(), SortOrder.HIGHEST_ORDER_FIRST), includedProfiles,
                    localSettings.getBooleanProperty("recogniseImplicitRdfRuleInclusions", true),
                    localSettings.getBooleanProperty("includeNonProfileMatchedRdfRules", true));
        }
        catch(final UnnormalisableRuleException e)
        {
            GeneralServlet.log.error("Found unnormalisable rule exception while normalising the pool", e);
            throw new QueryAllException("Found unnormalisable rule exception while normalising the pool", e);
        }
        catch(final QueryAllException e)
        {
            GeneralServlet.log.error("Found queryall checked exception while normalising the pool", e);
            throw e;
        }
    }
    
}
