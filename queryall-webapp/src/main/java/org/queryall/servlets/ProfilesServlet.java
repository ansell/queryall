package org.queryall.servlets;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.queryall.api.NormalisationRule;
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.api.QueryType;
import org.queryall.helpers.*;

import org.apache.log4j.Logger;

import org.openrdf.model.URI;

/** 
 * 
 */

public class ProfilesServlet extends HttpServlet 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3461270431775779321L;
	public static final Logger log = Logger.getLogger(ProfilesServlet.class.getName());
    public static final boolean _TRACE = log.isTraceEnabled();
    public static final boolean _DEBUG = log.isDebugEnabled();
    public static final boolean _INFO = log.isInfoEnabled();

    
    @Override
    public void doGet(HttpServletRequest request,
                        HttpServletResponse response)
        throws ServletException, IOException 
    {
    	Settings localSettings = Settings.getSettings();
        
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        
        @SuppressWarnings("unused")
        String realHostName = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 ? "" : ":"+ request.getServerPort())+"/";
        
        Map<URI, Provider> allProviders = localSettings.getAllProviders();
        
        Map<URI, QueryType> allCustomQueries = localSettings.getAllQueryTypes();
        
        Map<URI, NormalisationRule> allRdfRules = localSettings.getAllNormalisationRules();
        
        Map<URI, Profile> allProfiles = localSettings.getAllProfiles();
        
        List<Profile> enabledProfiles = localSettings.getAndSortProfileList(localSettings.getURIProperties("activeProfiles"), Constants.LOWEST_ORDER_FIRST);
        
        out.write("<br />Number of queries = " + allCustomQueries.size()+"<br />\n");
        out.write("<br />Number of providers = " + allProviders.size()+"<br />\n");
        out.write("<br />Number of rdf normalisation rules = " + allRdfRules.size()+"<br />\n");
        out.write("<br />Number of profiles = " + allProfiles.size()+"<br />\n");
        
        out.write("<br />Enabled profiles: ("+localSettings.getStringProperties("activeProfiles").size()+")<br />\n");
        
        out.write("<ul>\n");
        
        for(Profile nextEnabledProfile : enabledProfiles)
        {
            out.write("<li>" + nextEnabledProfile.getKey() + "</li>");
        }
        
        out.write("</ul>\n");
        
        List<URI> includedProviders = new ArrayList<URI>();
        List<URI> excludedProviders = new ArrayList<URI>();
        List<URI> includedQueries = new ArrayList<URI>();
        List<URI> excludedQueries = new ArrayList<URI>();
        List<URI> includedRdfRules = new ArrayList<URI>();
        List<URI> excludedRdfRules = new ArrayList<URI>();
        
        out.write("The following list is authoritative across all of the currently enabled profiles<br/>\n");
        
        for(Provider nextProvider : allProviders.values())
        {
            if(nextProvider.isUsedWithProfileList(enabledProfiles, localSettings.getBooleanProperty("recogniseImplicitProviderInclusions", true), localSettings.getBooleanProperty("includeNonProfileMatchedProviders", true)))
            {
                // included for this profile...
                //out.write("Provider included: "+nextProvider.getKey()+"<br />\n");
                includedProviders.add(nextProvider.getKey());
            }
            else
            {
                // not included for this profile...
                excludedProviders.add(nextProvider.getKey());
            }
        }
        
        for(QueryType nextQuery : allCustomQueries.values())
        {
            if(nextQuery.isUsedWithProfileList(enabledProfiles, localSettings.getBooleanProperty("recogniseImplicitQueryInclusions", true), localSettings.getBooleanProperty("includeNonProfileMatchedQueries", true)))
            {
                // included for this profile...
                //out.write("Query included: "+nextQuery.getKey()+"<br />\n");
                includedQueries.add(nextQuery.getKey());
            }
            else
            {
                // not included for this profile...
                excludedQueries.add(nextQuery.getKey());
            }
        }
        
        for(NormalisationRule nextRdfRule : allRdfRules.values())
        {
            if(nextRdfRule.isUsedWithProfileList(enabledProfiles, localSettings.getBooleanProperty("recogniseImplicitRdfRuleInclusions", true), localSettings.getBooleanProperty("includeNonProfileMatchedRdfRules", true)))
            {
                // included for this profile...
                //out.write("Rdfrule included: "+nextRdfrule.getKey()+"<br />\n");
                includedRdfRules.add(nextRdfRule.getKey());
            }
            else
            {
                // not included for this profile...
                excludedRdfRules.add(nextRdfRule.getKey());
            }
        }
        
        out.write("Included providers: ("+includedProviders.size()+")");
        out.write("<ul>\n");
        for(URI nextInclude : includedProviders)
        {
            out.write("<li>"+nextInclude+"</li>\n");
        }
        out.write("</ul>\n");
        
        out.write("Excluded providers: ("+excludedProviders.size()+")");
        out.write("<ul>\n");
        for(URI nextExclude : excludedProviders)
        {
            out.write("<li>"+nextExclude+"</li>\n");
        }
        out.write("</ul>\n");
        
        out.write("Included queries: ("+includedQueries.size()+")");
        out.write("<ul>\n");
        for(URI nextInclude : includedQueries)
        {
            out.write("<li>"+nextInclude+"</li>\n");
        }
        out.write("</ul>\n");
        
        out.write("Excluded queries: ("+excludedQueries.size()+")");
        out.write("<ul>\n");
        for(URI nextExclude : excludedQueries)
        {
            out.write("<li>"+nextExclude+"</li>\n");
        }
        out.write("</ul>\n");
        
        out.write("Included rdfrules: ("+includedRdfRules.size()+")");
        out.write("<ul>\n");
        for(URI nextInclude : includedRdfRules)
        {
            out.write("<li>"+nextInclude+"</li>\n");
        }
        out.write("</ul>\n");
        
        out.write("Excluded rdfrules: ("+excludedRdfRules.size()+")");
        out.write("<ul>\n");
        for(URI nextExclude : excludedRdfRules)
        {
            out.write("<li>"+nextExclude+"</li>\n");
        }
        out.write("</ul>\n");
        
        out.write("<div>The next section details the profile by profile details, and does not necessarily match the actual effect if there is more than one profile enabled</div>");
        
        for(Profile nextProfile : enabledProfiles)
        {
            includedProviders = new ArrayList<URI>();
            excludedProviders = new ArrayList<URI>();
            
            List<Profile> nextProfileAsList = new ArrayList<Profile>(1);
            nextProfileAsList.add(nextProfile);
            
            if(localSettings.getStringProperties("activeProfiles").contains(nextProfile.getKey()))
            {
                out.write("<div style=\"display:block;\">\n");
                out.write("Profile:"+nextProfile.getKey()+"\n");
                out.write("<span>Profile enabled</span>\n");
            }
            else
            {
                out.write("<div style=\"display:none;\">\n");
                out.write("Profile:"+nextProfile.getKey()+"\n");
                out.write("<span>Profile disabled</span>\n");
            }
            
            out.write("<br />");
            
            
            for(Provider nextProvider : allProviders.values())
            {
                if(nextProvider.isUsedWithProfileList(nextProfileAsList, localSettings.getBooleanProperty("recogniseImplicitProviderInclusions", true), localSettings.getBooleanProperty("includeNonProfileMatchedProviders", true)))
                {
                    // included for this profile...
                    //out.write("Provider included: "+nextProvider.getKey()+"<br />\n");
                    includedProviders.add(nextProvider.getKey());
                }
                else
                {
                    // not included for this profile...
                    excludedProviders.add(nextProvider.getKey());
                }
            }
            
            for(QueryType nextQuery : allCustomQueries.values())
            {
                if(nextQuery.isUsedWithProfileList(nextProfileAsList, localSettings.getBooleanProperty("recogniseImplicitQueryInclusions", true), localSettings.getBooleanProperty("includeNonProfileMatchedQueries", true)))
                {
                    // included for this profile...
                    //out.write("Query included: "+nextQuery.getKey()+"<br />\n");
                    includedQueries.add(nextQuery.getKey());
                }
                else
                {
                    // not included for this profile...
                    excludedQueries.add(nextQuery.getKey());
                }
            }
            
            for(NormalisationRule nextRdfRule : allRdfRules.values())
            {
                if(nextRdfRule.isUsedWithProfileList(nextProfileAsList, localSettings.getBooleanProperty("recogniseImplicitRdfRuleInclusions", true), localSettings.getBooleanProperty("includeNonProfileMatchedRdfRules", true)))
                {
                    // included for this profile...
                    //out.write("RdfRule included: "+nextRdfRule.getKey()+"<br />\n");
                    includedRdfRules.add(nextRdfRule.getKey());
                }
                else
                {
                    // not included for this profile...
                    excludedRdfRules.add(nextRdfRule.getKey());
                }
            }
            
            out.write("Included providers: ("+includedProviders.size()+")");
            out.write("<ul>\n");
            for(URI nextInclude : includedProviders)
            {
                out.write("<li>"+nextInclude+"</li>\n");
            }
            out.write("</ul>\n");
            
            out.write("Excluded providers: ("+excludedProviders.size()+")");
            out.write("<ul>\n");
            for(URI nextExclude : excludedProviders)
            {
                out.write("<li>"+nextExclude+"</li>\n");
            }
            out.write("</ul>\n");
            
            out.write("Included queries: ("+includedQueries.size()+")");
            out.write("<ul>\n");
            for(URI nextInclude : includedQueries)
            {
                out.write("<li>"+nextInclude+"</li>\n");
            }
            out.write("</ul>\n");
            
            out.write("Excluded queries: ("+excludedQueries.size()+")");
            out.write("<ul>\n");
            for(URI nextExclude : excludedQueries)
            {
                out.write("<li>"+nextExclude+"</li>\n");
            }
            out.write("</ul>\n");
            
            out.write("Included rdfrules: ("+includedRdfRules.size()+")");
            out.write("<ul>\n");
            for(URI nextInclude : includedRdfRules)
            {
                out.write("<li>"+nextInclude+"</li>\n");
            }
            out.write("</ul>\n");
            
            out.write("Excluded rdfrules: ("+excludedRdfRules.size()+")");
            out.write("<ul>\n");
            for(URI nextExclude : excludedRdfRules)
            {
                out.write("<li>"+nextExclude+"</li>\n");
            }
            out.write("</ul>\n");
            out.write("</div>\n");
        }
    
  }
  
}

