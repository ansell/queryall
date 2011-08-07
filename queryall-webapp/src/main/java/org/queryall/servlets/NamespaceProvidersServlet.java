package org.queryall.servlets;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.queryall.impl.*;
import org.queryall.api.HttpProvider;
import org.queryall.api.NamespaceEntry;
import org.queryall.api.NormalisationRule;
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.api.QueryType;
import org.queryall.api.RuleTest;
import org.queryall.api.SparqlProvider;
import org.queryall.helpers.*;

import org.apache.log4j.Logger;

import org.openrdf.model.URI;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class NamespaceProvidersServlet extends HttpServlet 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7006535158409121292L;
	public static final Logger log = Logger.getLogger(NamespaceProvidersServlet.class.getName());
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
		String subversionId = "$Id: $";
        
        Date currentDate = new Date();
        String now = Constants.ISO8601UTC().format(currentDate);
        
        @SuppressWarnings("unused")
        String realHostName = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 ? "" : ":"+ request.getServerPort())+"/";
        
        Map<URI, Provider> allProviders = localSettings.getAllProviders();
        
        Map<URI, NamespaceEntry> allNamespaceEntries = localSettings.getAllNamespaceEntries();
        
        Map<URI, NormalisationRule> allRdfRules = localSettings.getAllNormalisationRules();
        
        Map<URI, RuleTest> allRdfRuleTests = localSettings.getAllRuleTests();
        
        Map<URI, Profile> allProfiles = localSettings.getAllProfiles();
        
        Map<URI, QueryType> allQueryTypes = localSettings.getAllQueryTypes();

        Map<URI, Collection<Provider>> providersByNamespace = new Hashtable<URI, Collection<Provider>>();
        
        Map<URI, Collection<Provider>> providersByQueryKey = new Hashtable<URI, Collection<Provider>>();
        
        Map<String, Collection<Provider>> allQueryTypesByNamespace = new Hashtable<String, Collection<Provider>>();
        
        StringBuilder rdfStrings = new StringBuilder();
        
        int overallQueryTypeProviders = 0;
        int overallNamespaceProviders = 0;
        int overallQueryTypeByNamespaceProviders = 0;
        
        for(URI nextKey : allProviders.keySet())
        {
            Provider nextProvider = allProviders.get(nextKey);
            
            for(URI nextQueryKey : nextProvider.getIncludedInQueryTypes())
            {
                if(!providersByQueryKey.containsKey(nextQueryKey))
                {
                    Map<URI, Provider> queryProviders = ProviderUtils.getProvidersForQueryType(allProviders, nextQueryKey);
                                        
                    providersByQueryKey.put(nextQueryKey, queryProviders.values());
                    
                    overallQueryTypeProviders += queryProviders.size();
                }
            }
            
            for(URI nextNamespace : nextProvider.getNamespaces())
            {
                if(nextNamespace != null && !providersByNamespace.containsKey(nextNamespace))
                {
                    Collection<Collection<URI>> nextNamespacesList = new HashSet<Collection<URI>>();
                    Collection<URI> nextNamespaces = new HashSet<URI>(4);
                    nextNamespaces.add(nextNamespace);
                    nextNamespacesList.add(nextNamespaces);
                    
                    Map<URI, Provider> namespaceProviders = ProviderUtils.getProvidersForNamespaceUris(allProviders, nextNamespacesList, QueryTypeImpl.getQueryNamespaceMatchAny());
                    
                    providersByNamespace.put(nextNamespace, namespaceProviders.values());
                    
                    overallNamespaceProviders += namespaceProviders.size();
                    
                    for(Provider nextNamespaceProvider : namespaceProviders.values())
                    {
                        for(URI nextQueryKey : nextNamespaceProvider.getIncludedInQueryTypes())
                        {
                            if(!allQueryTypesByNamespace.containsKey(nextQueryKey.stringValue() + " " + nextNamespace.stringValue()))
                            {
                                Collection<Collection<URI>> nextQueryTypesByNamespacesList = new HashSet<Collection<URI>>();
                                Collection<URI> nextQueryTypesByNamespaces = new HashSet<URI>(4);
                                nextQueryTypesByNamespaces.add(nextNamespace);
                                nextQueryTypesByNamespacesList.add(nextQueryTypesByNamespaces);
                                
                                Map<URI, Provider> queryTypesByNamespace = Settings.getProvidersForQueryTypeForNamespaceUris(allProviders, nextQueryKey, nextQueryTypesByNamespacesList, QueryTypeImpl.getQueryNamespaceMatchAny());
                                
                                allQueryTypesByNamespace.put(nextQueryKey.stringValue() + " " + nextNamespace.stringValue(), queryTypesByNamespace.values());
                                
                                overallQueryTypeByNamespaceProviders += queryTypesByNamespace.size();
                            }
                        }
                    }
                }
                
                if(!allNamespaceEntries.containsKey(nextNamespace))
                {
                	log.error("Namespace is defined on a provider but not in the namespace entries list nextProvider="+nextProvider.getKey().stringValue()+" nextNamespace="+nextNamespace.stringValue());
                }
            }
        }
        
        for(URI nextNamespaceEntry : allNamespaceEntries.keySet())
        {
        	if(!providersByNamespace.containsKey(nextNamespaceEntry))
        	{
        		log.warn("Namespace entry is defined but it does not have any linked providers nextNamespaceEntry="+nextNamespaceEntry.stringValue());
        	}
        }
        
        
        out.write("<br />Number of namespaces that are known = " + allNamespaceEntries.size()+"<br />\n");
        out.write("<br />Number of namespaces that have providers = " + providersByNamespace.size()+"<br />\n");
        out.write("<br />Number of query titles = " + allQueryTypes.size()+"<br />\n");
        out.write("<br />Number of providers = " + allProviders.size()+"<br />\n");
        out.write("<br />Number of rdf normalisation rules = " + allRdfRules.size()+"<br />\n");
        out.write("<br />Number of rdf normalisation rule tests = " + allRdfRuleTests.size()+"<br />\n");
        out.write("<br />Number of profiles = " + allProfiles.size()+"<br />\n");
        
        out.write("<br />Number of namespace provider options = " + overallNamespaceProviders+"<br />\n");
        out.write("<br />Number of query title provider options = " + overallQueryTypeProviders+"<br />\n");
        out.write("<br />Number of query title and namespace combinations = " + allQueryTypesByNamespace.size()+"<br />\n");
        out.write("<br />Number of query title and namespace combination provider options = " + overallQueryTypeByNamespaceProviders+"<br /><br />\n");
        
        out.write("Raw complete namespace Collection<br />\n");
        
        for(URI nextUniqueNamespace : providersByNamespace.keySet())
        {
            out.write(nextUniqueNamespace.stringValue()+",");
        }
        
        for(URI nextUniqueNamespace : providersByNamespace.keySet())
        {
            if(nextUniqueNamespace == null)
                continue;
            
            out.write("<h3><span class='debug'>Namespace="+nextUniqueNamespace.stringValue()+"</span></h3>\n");
            
            Collection<Provider> providersForNextNamespace = providersByNamespace.get(nextUniqueNamespace);
            
            if(providersForNextNamespace.size() == 0)
            {
            	out.write("NO Providers known for this namespace");
            	log.info("No providers known for namespace="+nextUniqueNamespace.stringValue());
            }
            else
            {
	            Collection<URI> implementedQueriesForNextNamespace = new HashSet<URI>();
	            
	            for(Provider nextProviderForNextNamespace : providersForNextNamespace)
	            {
	                for(URI nextIncludedQuery : nextProviderForNextNamespace.getIncludedInQueryTypes())
	                {
	                    if(!implementedQueriesForNextNamespace.contains(nextIncludedQuery))
	                    {
	                        implementedQueriesForNextNamespace.add(nextIncludedQuery);
	                    }
	                }
	            }
	            
	            out.write("Queries for this namespace ("+implementedQueriesForNextNamespace.size()+")");
	            
	            if(implementedQueriesForNextNamespace.size() > 0)
	            {
	                out.write("<ol>");
	            }
	            
	            for(URI nextImplementedQuery : implementedQueriesForNextNamespace)
	            {
	                out.write("<li>"+nextImplementedQuery.stringValue()+"</li>");
	            }
	            
	            if(implementedQueriesForNextNamespace.size() > 0)
	            {
	                out.write("</ol>");
	            }
            }
        }
        
        for(URI nextUniqueQuery : providersByQueryKey.keySet())
        {
            if(nextUniqueQuery == null)
                continue;
            
            out.write("<h3><span class='debug'>Query="+nextUniqueQuery.stringValue()+"</span></h3>\n");
            
            Collection<Provider> providersForNextQuery = providersByQueryKey.get(nextUniqueQuery);
            
            List<URI> implementedNamespacesForNextQuery = new ArrayList<URI>();
            
            for(Provider nextProviderForNextQuery : providersForNextQuery)
            {
                for(URI nextIncludedNamespace : nextProviderForNextQuery.getNamespaces())
                {
                    if(!implementedNamespacesForNextQuery.contains(nextIncludedNamespace))
                    {
                        implementedNamespacesForNextQuery.add(nextIncludedNamespace);
                    }
                }
            }
            
            out.write("Namespaces for this query ("+implementedNamespacesForNextQuery.size()+")");
            
            if(implementedNamespacesForNextQuery.size() > 0)
            {
                out.write("<ol>");
            }
            
            for(URI nextImplementedNamespace : implementedNamespacesForNextQuery)
            {
                out.write("<li>"+nextImplementedNamespace.stringValue()+"</li>");
            }
            
            if(implementedNamespacesForNextQuery.size() > 0)
            {
                out.write("</ol>");
            }
            
            out.write("RDF N3 compatible list:<br /> \n");
            
            if(implementedNamespacesForNextQuery.size() > 0 && localSettings.getURIProperties("autogenerateIncludeStubList").contains(nextUniqueQuery))
            {
                StringBuilder sb = new StringBuilder();
                
                String shortQueryName = localSettings.getAutogeneratedQueryPrefix()
                    + 
                    StringUtils.md5(
                        StringUtils.percentEncode(nextUniqueQuery.stringValue())
                        + localSettings.getStringProperty("separator", "")
                        + now
                        + localSettings.getStringProperty("separator", "")
                    )
                    + localSettings.getAutogeneratedQuerySuffix();
                
                String queryName = localSettings.getDefaultHostAddress() 
                    + localSettings.getNamespaceForQueryType()
                    + localSettings.getStringProperty("separator", "")
                    + shortQueryName;
                
                String shortProviderName = localSettings.getAutogeneratedProviderPrefix()
                    +
                    StringUtils.md5(
                        StringUtils.percentEncode(nextUniqueQuery.stringValue())
                        +localSettings.getStringProperty("separator", "")
                        +now
                        +localSettings.getStringProperty("separator", "")
                        )
                    +localSettings.getAutogeneratedProviderSuffix();
                    
                String providerName = localSettings.getDefaultHostAddress() 
                    + localSettings.getNamespaceForProvider()
                    + localSettings.getStringProperty("separator", "")
                    + shortProviderName ;
                
                StringBuilder namespacesForThisQuery = new StringBuilder();
                
                for(int nextPosition = 0; nextPosition < implementedNamespacesForNextQuery.size(); nextPosition++) 
                {
                    if(nextPosition != 0)
                        namespacesForThisQuery.append(", ");
                    namespacesForThisQuery.append("<"+implementedNamespacesForNextQuery.get(nextPosition)+"> ");
                }
                
                sb.append(StringUtils.xmlEncodeString("<"+queryName+"> a <http://purl.org/queryall/query:Query> ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/query:isPageable> \"false\"^^<http://www.w3.org/2001/XMLSchema#boolean> ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/dc/elements/1.1/title> \""+StringUtils.ntriplesEncode(shortQueryName)+"\" ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/query:handleAllNamespaces> \"false\"^^<http://www.w3.org/2001/XMLSchema#boolean> ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/query:isNamespaceSpecific> \"true\"^^<http://www.w3.org/2001/XMLSchema#boolean> ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/query:namespaceMatchMethod> <http://purl.org/queryall/query:namespaceMatchAny> ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/query:includeDefaults> \"false\"^^<http://www.w3.org/2001/XMLSchema#boolean> ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/query:inputRegex> \""+StringUtils.ntriplesEncode(localSettings.getStringProperty("plainNamespaceAndIdentifierRegex", ""))+"\" ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/query:templateString> \"\" ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/query:queryUriTemplateString> \"${defaultHostAddress}${input_1}${defaultSeparator}${input_2}\" ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/query:standardUriTemplateString> \"${defaultHostAddress}${input_1}${defaultSeparator}${input_2}\" ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/query:outputRdfXmlString> \"\" ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/query:inRobotsTxt> \"false\"^^<http://www.w3.org/2001/XMLSchema#boolean> ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/profile:profileIncludeExcludeOrder> <http://purl.org/queryall/profile:excludeThenInclude> ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/query:namespaceToHandle> "+namespacesForThisQuery.toString()+" ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/query:hasPublicIdentifierIndex> \"1\"^^<http://www.w3.org/2001/XMLSchema#int> ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/query:hasNamespaceInputIndex> \"1\"^^<http://www.w3.org/2001/XMLSchema#int> ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/query:includeQueryType> <"+nextUniqueQuery+"> .")+"<br />\n<br />\n");
                
                sb.append(StringUtils.xmlEncodeString("<"+providerName+"> a <http://purl.org/queryall/provider:Provider> ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/provider:Title> \""+StringUtils.ntriplesEncode(shortProviderName)+"\" ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/provider:resolutionStrategy> <http://purl.org/queryall/provider:proxy> ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/provider:resolutionMethod> <http://purl.org/queryall/provider:nocommunication> ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/provider:isDefaultSource> \"false\"^^<http://www.w3.org/2001/XMLSchema#boolean> ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/profile:profileIncludeExcludeOrder> <http://purl.org/queryall/profile:excludeThenInclude> ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/provider:includedInQuery> <"+queryName+"> ;")+"<br />\n");
                sb.append(StringUtils.xmlEncodeString("<http://purl.org/queryall/provider:handlesNamespace> "+namespacesForThisQuery.toString()+" . ")+"<br /><br />\n");
                
                out.write(sb.toString()+"<br /> \n");
                
                rdfStrings.append(sb);
            }
        }
        
        if(log.isDebugEnabled())
        {
        out.write("<h2>Consistency analysis:</h2>");
        for(URI nextUniqueNamespace : providersByNamespace.keySet())
        {
            for(URI nextUniqueQueryTitle : providersByQueryKey.keySet())
            {
                Collection<QueryType> queriesForNextTitle = localSettings.getQueryTypesByUri(nextUniqueQueryTitle);
                
                if(queriesForNextTitle.size() == 0)
                {
                    // log.error("No query type definitions were found for the query title "+nextUniqueQueryTitle);
                    out.write("<span class='error'>No query type definitions were found for the query title "+nextUniqueQueryTitle+"</span><br />\n");
                }
                else if(queriesForNextTitle.size() == 1)
                {
                    Map<URI, Provider> queryTypesForNamespace = ProviderUtils.getProvidersForQueryType(allProviders, nextUniqueQueryTitle);
                    
                    // We use QueryType.handleAllNamespaces to detect whether we are conceivably missing query type providers for any of the discovered namespaces
                    // if(queriesForNextTitle.get(0).handleAllNamespaces && queryTypesForNamespace.size() == 0)
                    // {
                        // if(log.isTraceEnabled())
                        // {
                            // // log.warn("No provider was found for a namespace for a querytitle that is defined to be able to handle all namespaces nextUniqueQueryTitle="+nextUniqueQueryTitle+" nextUniqueNamespace="+nextUniqueNamespace);
                            // out.write("<span class='warn'>No provider was found for a namespace for a querytitle that is defined to be able to handle all namespaces nextUniqueQueryTitle="+nextUniqueQueryTitle+" nextUniqueNamespace="+nextUniqueNamespace+" </span><br />\n");
                        // }
                    // }
                    // else 
                    if(queryTypesForNamespace.size() > 0)
                    {
                        if(log.isDebugEnabled())
                        {
                            out.write("<span class='info'>Provider found for namespace and query : nextUniqueQueryTitle="+nextUniqueQueryTitle+" nextUniqueNamespace="+nextUniqueNamespace+"</span><br />\n");
                            // log.debug("Provider found for namespace and query : nextUniqueQueryTitle="+nextUniqueQueryTitle+" nextUniqueNamespace="+nextUniqueNamespace);
                        }
                        
                        for(Provider nextQueryNamespaceProvider : queryTypesForNamespace.values())
                        {
                            if(nextQueryNamespaceProvider instanceof HttpProvider)
                            {
                            	HttpProvider nextHttpProvider = (HttpProvider)nextQueryNamespaceProvider;
                            	if (nextHttpProvider.getEndpointUrls() != null)
                            	{
	                                for(String nextEndpointUrl : nextHttpProvider.getEndpointUrls())
	                                {
	                                    if(log.isDebugEnabled())
	                                    {
	                                        out.write("<li><span class='debug'><a href='"+nextEndpointUrl+"'>"+nextEndpointUrl);
	                                        
	                                        if(nextQueryNamespaceProvider instanceof SparqlProvider)
	                                        {
	                                        	SparqlProvider nextSparqlProvider = (SparqlProvider)nextQueryNamespaceProvider;
		                                        if(nextSparqlProvider.getUseSparqlGraph())
		                                        {
		                                        	out.write(" graph="+nextSparqlProvider.getSparqlGraphUri());
		                                        }
	                                        }
	                                        
	                                        out.write("</a></span></li>\n");
	                                    }
	                                }
                                }
                            }
                            else if(nextQueryNamespaceProvider.getEndpointMethod().equals(ProviderImpl.getProviderNoCommunication().stringValue()))
                            {
                                if(log.isDebugEnabled())
                                {
                                    out.write("<li><span class='debug'>No communication required</span></li><br />\n");
                                }
                            }
                            else
                            {
                                if(log.isDebugEnabled())
                                {
                                    out.write("<li><span class='debug'>No endpoint URL's found for a particular provider</span></li><br />\n");
                                }
                            }
                        }
                    }
                    else
                    {
                        // Enable this to get some rarely meaningful notices about queries which are not designed to handle all namespaces
                        // if(log.isDebugEnabled())
                        // {
                            // log.debug("No provider was found for a namespace for a particular queryTitle nextUniqueQueryTitle="+nextUniqueQueryTitle+" nextUniqueNamespace="+nextUniqueNamespace);
                            // out.write("<span class='debug'>No provider was found for a namespace for a particular queryTitle nextUniqueQueryTitle="+nextUniqueQueryTitle+" nextUniqueNamespace="+nextUniqueNamespace+" </span><br />\n");				
                        // }
                    }
                }
                else
                {
                    log.warn("More than one query type definition was found for the query title "+nextUniqueQueryTitle + " number found="+queriesForNextTitle.size());
                    out.write("<span class='error'>More than one query type definition was found for the query title "+nextUniqueQueryTitle+"</span><br />\n");
                }
            }
        }
        }
        out.write("<a id=\"rdfoutput\">Complete RDF Output</a><br/>\n");
        out.write(rdfStrings.toString());
    
  }
  
}

