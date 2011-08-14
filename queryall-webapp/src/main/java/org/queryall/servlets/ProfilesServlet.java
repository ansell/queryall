package org.queryall.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.model.URI;
import org.queryall.api.NormalisationRule;
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.api.QueryAllConfiguration;
import org.queryall.api.QueryType;
import org.queryall.enumerations.SortOrder;
import org.queryall.query.Settings;
import org.queryall.utils.ProfileUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfilesServlet extends HttpServlet
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 3461270431775779321L;
    public static final Logger log = LoggerFactory.getLogger(ProfilesServlet.class.getName());
    public static final boolean _TRACE = ProfilesServlet.log.isTraceEnabled();
    public static final boolean _DEBUG = ProfilesServlet.log.isDebugEnabled();
    public static final boolean _INFO = ProfilesServlet.log.isInfoEnabled();
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException
    {
        final QueryAllConfiguration localSettings = Settings.getSettings();
        
        final PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        
        @SuppressWarnings("unused")
        final String realHostName =
                request.getScheme() + "://" + request.getServerName()
                        + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) + "/";
        
        final Map<URI, Provider> allProviders = localSettings.getAllProviders();
        
        final Map<URI, QueryType> allCustomQueries = localSettings.getAllQueryTypes();
        
        final Map<URI, NormalisationRule> allRdfRules = localSettings.getAllNormalisationRules();
        
        final Map<URI, Profile> allProfiles = localSettings.getAllProfiles();
        
        final List<Profile> enabledProfiles =
                ProfileUtils.getAndSortProfileList(localSettings.getURIProperties("activeProfiles"),
                        SortOrder.LOWEST_ORDER_FIRST, localSettings.getAllProfiles());
        
        out.write("<br />Number of queries = " + allCustomQueries.size() + "<br />\n");
        out.write("<br />Number of providers = " + allProviders.size() + "<br />\n");
        out.write("<br />Number of rdf normalisation rules = " + allRdfRules.size() + "<br />\n");
        out.write("<br />Number of profiles = " + allProfiles.size() + "<br />\n");
        
        out.write("<br />Enabled profiles: (" + localSettings.getStringProperties("activeProfiles").size()
                + ")<br />\n");
        
        out.write("<ul>\n");
        
        for(final Profile nextEnabledProfile : enabledProfiles)
        {
            out.write("<li>" + nextEnabledProfile.getKey() + "</li>");
        }
        
        out.write("</ul>\n");
        
        List<URI> includedProviders = new ArrayList<URI>();
        List<URI> excludedProviders = new ArrayList<URI>();
        final List<URI> includedQueries = new ArrayList<URI>();
        final List<URI> excludedQueries = new ArrayList<URI>();
        final List<URI> includedRdfRules = new ArrayList<URI>();
        final List<URI> excludedRdfRules = new ArrayList<URI>();
        
        out.write("The following list is authoritative across all of the currently enabled profiles<br/>\n");
        
        for(final Provider nextProvider : allProviders.values())
        {
            if(nextProvider.isUsedWithProfileList(enabledProfiles,
                    localSettings.getBooleanProperty("recogniseImplicitProviderInclusions", true),
                    localSettings.getBooleanProperty("includeNonProfileMatchedProviders", true)))
            {
                // included for this profile...
                // out.write("Provider included: "+nextProvider.getKey()+"<br />\n");
                includedProviders.add(nextProvider.getKey());
            }
            else
            {
                // not included for this profile...
                excludedProviders.add(nextProvider.getKey());
            }
        }
        
        for(final QueryType nextQuery : allCustomQueries.values())
        {
            if(nextQuery.isUsedWithProfileList(enabledProfiles,
                    localSettings.getBooleanProperty("recogniseImplicitQueryInclusions", true),
                    localSettings.getBooleanProperty("includeNonProfileMatchedQueries", true)))
            {
                // included for this profile...
                // out.write("Query included: "+nextQuery.getKey()+"<br />\n");
                includedQueries.add(nextQuery.getKey());
            }
            else
            {
                // not included for this profile...
                excludedQueries.add(nextQuery.getKey());
            }
        }
        
        for(final NormalisationRule nextRdfRule : allRdfRules.values())
        {
            if(nextRdfRule.isUsedWithProfileList(enabledProfiles,
                    localSettings.getBooleanProperty("recogniseImplicitRdfRuleInclusions", true),
                    localSettings.getBooleanProperty("includeNonProfileMatchedRdfRules", true)))
            {
                // included for this profile...
                // out.write("Rdfrule included: "+nextRdfrule.getKey()+"<br />\n");
                includedRdfRules.add(nextRdfRule.getKey());
            }
            else
            {
                // not included for this profile...
                excludedRdfRules.add(nextRdfRule.getKey());
            }
        }
        
        out.write("Included providers: (" + includedProviders.size() + ")");
        out.write("<ul>\n");
        for(final URI nextInclude : includedProviders)
        {
            out.write("<li>" + nextInclude + "</li>\n");
        }
        out.write("</ul>\n");
        
        out.write("Excluded providers: (" + excludedProviders.size() + ")");
        out.write("<ul>\n");
        for(final URI nextExclude : excludedProviders)
        {
            out.write("<li>" + nextExclude + "</li>\n");
        }
        out.write("</ul>\n");
        
        out.write("Included queries: (" + includedQueries.size() + ")");
        out.write("<ul>\n");
        for(final URI nextInclude : includedQueries)
        {
            out.write("<li>" + nextInclude + "</li>\n");
        }
        out.write("</ul>\n");
        
        out.write("Excluded queries: (" + excludedQueries.size() + ")");
        out.write("<ul>\n");
        for(final URI nextExclude : excludedQueries)
        {
            out.write("<li>" + nextExclude + "</li>\n");
        }
        out.write("</ul>\n");
        
        out.write("Included rdfrules: (" + includedRdfRules.size() + ")");
        out.write("<ul>\n");
        for(final URI nextInclude : includedRdfRules)
        {
            out.write("<li>" + nextInclude + "</li>\n");
        }
        out.write("</ul>\n");
        
        out.write("Excluded rdfrules: (" + excludedRdfRules.size() + ")");
        out.write("<ul>\n");
        for(final URI nextExclude : excludedRdfRules)
        {
            out.write("<li>" + nextExclude + "</li>\n");
        }
        out.write("</ul>\n");
        
        out.write("<div>The next section details the profile by profile details, and does not necessarily match the actual effect if there is more than one profile enabled</div>");
        
        for(final Profile nextProfile : enabledProfiles)
        {
            includedProviders = new ArrayList<URI>();
            excludedProviders = new ArrayList<URI>();
            
            final List<Profile> nextProfileAsList = new ArrayList<Profile>(1);
            nextProfileAsList.add(nextProfile);
            
            if(localSettings.getStringProperties("activeProfiles").contains(nextProfile.getKey()))
            {
                out.write("<div style=\"display:block;\">\n");
                out.write("Profile:" + nextProfile.getKey() + "\n");
                out.write("<span>Profile enabled</span>\n");
            }
            else
            {
                out.write("<div style=\"display:none;\">\n");
                out.write("Profile:" + nextProfile.getKey() + "\n");
                out.write("<span>Profile disabled</span>\n");
            }
            
            out.write("<br />");
            
            for(final Provider nextProvider : allProviders.values())
            {
                if(nextProvider.isUsedWithProfileList(nextProfileAsList,
                        localSettings.getBooleanProperty("recogniseImplicitProviderInclusions", true),
                        localSettings.getBooleanProperty("includeNonProfileMatchedProviders", true)))
                {
                    // included for this profile...
                    // out.write("Provider included: "+nextProvider.getKey()+"<br />\n");
                    includedProviders.add(nextProvider.getKey());
                }
                else
                {
                    // not included for this profile...
                    excludedProviders.add(nextProvider.getKey());
                }
            }
            
            for(final QueryType nextQuery : allCustomQueries.values())
            {
                if(nextQuery.isUsedWithProfileList(nextProfileAsList,
                        localSettings.getBooleanProperty("recogniseImplicitQueryInclusions", true),
                        localSettings.getBooleanProperty("includeNonProfileMatchedQueries", true)))
                {
                    // included for this profile...
                    // out.write("Query included: "+nextQuery.getKey()+"<br />\n");
                    includedQueries.add(nextQuery.getKey());
                }
                else
                {
                    // not included for this profile...
                    excludedQueries.add(nextQuery.getKey());
                }
            }
            
            for(final NormalisationRule nextRdfRule : allRdfRules.values())
            {
                if(nextRdfRule.isUsedWithProfileList(nextProfileAsList,
                        localSettings.getBooleanProperty("recogniseImplicitRdfRuleInclusions", true),
                        localSettings.getBooleanProperty("includeNonProfileMatchedRdfRules", true)))
                {
                    // included for this profile...
                    // out.write("RdfRule included: "+nextRdfRule.getKey()+"<br />\n");
                    includedRdfRules.add(nextRdfRule.getKey());
                }
                else
                {
                    // not included for this profile...
                    excludedRdfRules.add(nextRdfRule.getKey());
                }
            }
            
            out.write("Included providers: (" + includedProviders.size() + ")");
            out.write("<ul>\n");
            for(final URI nextInclude : includedProviders)
            {
                out.write("<li>" + nextInclude + "</li>\n");
            }
            out.write("</ul>\n");
            
            out.write("Excluded providers: (" + excludedProviders.size() + ")");
            out.write("<ul>\n");
            for(final URI nextExclude : excludedProviders)
            {
                out.write("<li>" + nextExclude + "</li>\n");
            }
            out.write("</ul>\n");
            
            out.write("Included queries: (" + includedQueries.size() + ")");
            out.write("<ul>\n");
            for(final URI nextInclude : includedQueries)
            {
                out.write("<li>" + nextInclude + "</li>\n");
            }
            out.write("</ul>\n");
            
            out.write("Excluded queries: (" + excludedQueries.size() + ")");
            out.write("<ul>\n");
            for(final URI nextExclude : excludedQueries)
            {
                out.write("<li>" + nextExclude + "</li>\n");
            }
            out.write("</ul>\n");
            
            out.write("Included rdfrules: (" + includedRdfRules.size() + ")");
            out.write("<ul>\n");
            for(final URI nextInclude : includedRdfRules)
            {
                out.write("<li>" + nextInclude + "</li>\n");
            }
            out.write("</ul>\n");
            
            out.write("Excluded rdfrules: (" + excludedRdfRules.size() + ")");
            out.write("<ul>\n");
            for(final URI nextExclude : excludedRdfRules)
            {
                out.write("<li>" + nextExclude + "</li>\n");
            }
            out.write("</ul>\n");
            out.write("</div>\n");
        }
        
    }
    
}
