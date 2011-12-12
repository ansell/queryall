package org.queryall.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.provider.SparqlProvider;
import org.queryall.api.querytype.OutputQueryType;
import org.queryall.api.querytype.ProcessorQueryType;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.TransformingRule;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.SortOrder;
import org.queryall.api.utils.WebappConfig;
import org.queryall.exception.QueryAllException;
import org.queryall.utils.RuleUtils;
import org.queryall.utils.Settings;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryCreator
{
    private static final Logger log = LoggerFactory.getLogger(QueryCreator.class);
    private static final boolean _TRACE = QueryCreator.log.isTraceEnabled();
    private static final boolean _DEBUG = QueryCreator.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = QueryCreator.log.isInfoEnabled();
    
    private static final List<String> inputUrlEncodeInstructions = new ArrayList<String>(1);
    private static final List<String> inputPlusUrlEncodeInstructions = new ArrayList<String>(1);
    private static final List<String> inputXmlEncodeInstructions = new ArrayList<String>(1);
    private static final List<String> inputNTriplesEncodeInstructions = new ArrayList<String>(1);
    private static final List<String> xmlEncodeInstructions = new ArrayList<String>(1);
    private static final List<String> urlEncodeInstructions = new ArrayList<String>(1);
    private static final List<String> plusUrlEncodeInstructions = new ArrayList<String>(1);
    private static final List<String> ntriplesEncodeInstructions = new ArrayList<String>(1);
    private static final List<String> lowercaseInstructions = new ArrayList<String>(1);
    private static final List<String> inputUrlEncodedlowercaseInstructions = new ArrayList<String>(2);
    private static final List<String> inputXmlEncodedlowercaseInstructions = new ArrayList<String>(2);
    private static final List<String> inputUrlEncodedprivatelowercaseInstructions = new ArrayList<String>(2);
    private static final List<String> inputXmlEncodedprivatelowercaseInstructions = new ArrayList<String>(2);
    private static final List<String> uppercaseInstructions = new ArrayList<String>(1);
    private static final List<String> inputUrlEncodeduppercaseInstructions = new ArrayList<String>(2);
    private static final List<String> inputXmlEncodeduppercaseInstructions = new ArrayList<String>(2);
    private static final List<String> inputUrlEncodedprivateuppercaseInstructions = new ArrayList<String>(2);
    private static final List<String> inputXmlEncodedprivateuppercaseInstructions = new ArrayList<String>(2);
    
    private static final List<String> EMPTY_STRING_LIST = Collections.emptyList();
    
    static
    {
        
        QueryCreator.inputUrlEncodeInstructions.add(Constants.INPUT_URL_ENCODED);
        
        QueryCreator.inputPlusUrlEncodeInstructions.add(Constants.INPUT_PLUS_URL_ENCODED);
        
        QueryCreator.inputXmlEncodeInstructions.add(Constants.INPUT_XML_ENCODED);
        
        QueryCreator.inputNTriplesEncodeInstructions.add(Constants.INPUT_NTRIPLES_ENCODED);
        
        QueryCreator.xmlEncodeInstructions.add(Constants.XML_ENCODED);
        
        QueryCreator.urlEncodeInstructions.add(Constants.URL_ENCODED);
        
        QueryCreator.plusUrlEncodeInstructions.add(Constants.PLUS_URL_ENCODED);
        
        QueryCreator.ntriplesEncodeInstructions.add(Constants.NTRIPLES_ENCODED);
        
        QueryCreator.lowercaseInstructions.add(Constants.LOWERCASE);
        
        QueryCreator.inputUrlEncodedlowercaseInstructions.add(Constants.LOWERCASE);
        QueryCreator.inputUrlEncodedlowercaseInstructions.add(Constants.INPUT_URL_ENCODED);
        
        QueryCreator.inputXmlEncodedlowercaseInstructions.add(Constants.LOWERCASE);
        QueryCreator.inputXmlEncodedlowercaseInstructions.add(Constants.INPUT_XML_ENCODED);
        
        QueryCreator.inputUrlEncodedprivatelowercaseInstructions.add(Constants.PRIVATE_LOWERCASE);
        QueryCreator.inputUrlEncodedprivatelowercaseInstructions.add(Constants.INPUT_URL_ENCODED);
        
        QueryCreator.inputXmlEncodedprivatelowercaseInstructions.add(Constants.PRIVATE_LOWERCASE);
        QueryCreator.inputXmlEncodedprivatelowercaseInstructions.add(Constants.INPUT_XML_ENCODED);
        
        QueryCreator.uppercaseInstructions.add(Constants.UPPERCASE);
        
        QueryCreator.inputUrlEncodeduppercaseInstructions.add(Constants.UPPERCASE);
        QueryCreator.inputUrlEncodeduppercaseInstructions.add(Constants.INPUT_URL_ENCODED);
        
        QueryCreator.inputXmlEncodeduppercaseInstructions.add(Constants.UPPERCASE);
        QueryCreator.inputXmlEncodeduppercaseInstructions.add(Constants.INPUT_XML_ENCODED);
        
        QueryCreator.inputUrlEncodedprivateuppercaseInstructions.add(Constants.PRIVATE_UPPERCASE);
        QueryCreator.inputUrlEncodedprivateuppercaseInstructions.add(Constants.INPUT_URL_ENCODED);
        
        QueryCreator.inputXmlEncodedprivateuppercaseInstructions.add(Constants.PRIVATE_UPPERCASE);
        QueryCreator.inputXmlEncodedprivateuppercaseInstructions.add(Constants.INPUT_XML_ENCODED);
    }
    
    // takes a query and a dictionary of attributes which may or may not be
    // useful for this query
    // typical attributes are:
    // # defaultHostAddress (ie, http://bio2rdf.org/)
    // # graphStart (uses the graphUri from the particular provider if it
    // defined to do so, otherwise substitutes 0 spaces for the placeholder
    // # graphUri (the actual graphUri, incase it is needed separate from the
    // standard graphStart structure
    // # graphEnd
    // # sparqlEndpoint : If the address of the actual endpoint is needed inside
    // of the query, then it is available using this attribute
    // # limit : If present, the global limit will be inserted as LIMIT
    // Settings.getIntPropertyFromConfig("pageoffsetIndividualQueryLimit"), where the limit is an
    // integer number
    // of results to return from each query
    //
    // # Each of the relevant variables also has a urlEncoded version which
    // prefixes the variable name with urlEncoded_
    // # urlEncoded_graphUri
    // # urlEncoded_endpointUrl
    // # urlEncoded_defaultHostAddress
    /**
     * @param queryType
     * @param nextProvider
     * @param attributeList
     * @param includedProfiles
     * @param overallConvertAlternateToPreferredPrefix
     *            TODO
     * @param namespaceInputVariables
     *            TODO
     * @return
     * @throws QueryAllException
     */
    public static String createQuery(final QueryType queryType, final Provider nextProvider,
            final Map<String, String> attributeList, final List<Profile> includedProfiles,
            final boolean recogniseImplicitRdfRuleInclusions, final boolean includeNonProfileMatchedRdfRules,
            final boolean overallConvertAlternateToPreferredPrefix, final QueryAllConfiguration localSettings,
            final Map<String, Collection<NamespaceEntry>> namespaceInputVariables) throws QueryAllException
    {
        final String queryString = attributeList.get(Constants.TEMPLATE_KEY_QUERY_STRING);
        
        if(!(queryType instanceof ProcessorQueryType))
        {
            return queryString;
        }
        
        final ProcessorQueryType processorQueryType = (ProcessorQueryType)queryType;
        
        if(queryString.trim().equals(""))
        {
            QueryCreator.log.error("QueryCreator.createQuery: queryString was empty");
        }
        
        if(processorQueryType.getProcessingTemplateString() == null)
        {
            QueryCreator.log.error("QueryCreator.createQuery: template was null queryType.getKey()="
                    + queryType.getKey().stringValue());
        }
        
        return QueryCreator.doReplacementsOnString(attributeList, processorQueryType.getProcessingTemplateString(),
                queryType, null, nextProvider, attributeList, namespaceInputVariables, includedProfiles,
                recogniseImplicitRdfRuleInclusions, includeNonProfileMatchedRdfRules,
                overallConvertAlternateToPreferredPrefix, localSettings);
    }
    
    /**
     * @param originalQueryType
     * @param includedQueryType
     * @param nextProvider
     * @param attributeList
     * @param namespaceInputVariables
     *            TODO
     * @param includedProfiles
     * @param convertAlternateToPreferredPrefix
     *            TODO
     * @return
     * @throws QueryAllException
     */
    public static String createStaticRdfXmlString(final QueryType originalQueryType,
            final OutputQueryType includedQueryType, final Provider nextProvider,
            final Map<String, String> attributeList,
            final Map<String, Collection<NamespaceEntry>> namespaceInputVariables,
            final List<Profile> includedProfiles, final boolean recogniseImplicitRdfRuleInclusions,
            final boolean includeNonProfileMatchedRdfRules, final boolean convertAlternateToPreferredPrefix,
            final QueryAllConfiguration localSettings) throws QueryAllException
    {
        final String queryString = attributeList.get(Constants.TEMPLATE_KEY_QUERY_STRING);
        
        if(queryString.trim().equals(""))
        {
            QueryCreator.log.error("QueryCreator.createQuery: queryString was empty");
        }
        
        if(includedQueryType.getOutputString() == null)
        {
            QueryCreator.log.error("QueryCreator.createQuery: no outputString defined queryType="
                    + includedQueryType.getKey().stringValue());
            
            return "";
        }
        
        return QueryCreator.doReplacementsOnString(attributeList, includedQueryType.getOutputString(),
                originalQueryType, includedQueryType, nextProvider, attributeList, namespaceInputVariables,
                includedProfiles, recogniseImplicitRdfRuleInclusions, includeNonProfileMatchedRdfRules,
                convertAlternateToPreferredPrefix, localSettings);
    }
    
    public static String doReplacementsOnString(final Map<String, String> queryParameters, final String templateString,
            final QueryType originalQueryType, final QueryType includedQueryType, final Provider nextProvider,
            final Map<String, String> attributeList,
            final Map<String, Collection<NamespaceEntry>> namespaceInputVariables,
            final List<Profile> includedProfiles, final boolean recogniseImplicitRdfRuleInclusions,
            final boolean includeNonProfileMatchedRdfRules, final boolean overallConvertAlternateToPreferredPrefix,
            final QueryAllConfiguration localSettings) throws QueryAllException
    {
        if(QueryCreator._TRACE)
        {
            QueryCreator.log.trace("QueryCreator.doReplacementsOnString: queryString=" + queryParameters
                    + " templateString=" + templateString + " normalisationUrisNeeded=" + nextProvider);
        }
        
        if(!(localSettings.getTagPattern().matcher(templateString).matches()))
        {
            if(QueryCreator._TRACE)
            {
                QueryCreator.log.trace("tag pattern " + ((Settings)localSettings).getTagPattern().toString()
                        + " does not match template string");
                QueryCreator.log.trace("templateString=" + templateString);
                QueryCreator.log.trace("returning templateString unchanged");
            }
            
            return templateString;
        }
        
        final long start = System.currentTimeMillis();
        
        String replacedString = templateString;
        
        String normalisedStandardUri = originalQueryType.getStandardUriTemplateString();
        
        String normalisedQueryUri = "";
        
        // If they want to do the replacements on a different custom query to
        // the one from the custom query for similar includes then we do the
        // switch here
        if(includedQueryType == null)
        {
            normalisedQueryUri = originalQueryType.getQueryUriTemplateString();
        }
        else
        {
            normalisedQueryUri = includedQueryType.getQueryUriTemplateString();
        }
        
        if(QueryCreator._TRACE)
        {
            QueryCreator.log.trace("QueryCreator.createQuery: initial value of replacedString=" + replacedString);
            QueryCreator.log.trace("QueryCreator.createQuery: initial value of normalisedStandardUri="
                    + normalisedStandardUri);
            QueryCreator.log.trace("QueryCreator.createQuery: initial value of normalisedQueryUri="
                    + normalisedQueryUri);
        }
        
        if(includedQueryType != null && includedQueryType.getKey() != null)
        {
            replacedString =
                    replacedString.replace(Constants.TEMPLATE_XML_ENCODED_INCLUDED_QUERY_TYPE,
                            StringUtils.xmlEncodeString(includedQueryType.getKey().stringValue()));
        }
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_QUICK_LIMIT,
                        "LIMIT " + localSettings.getIntProperty(WebappConfig.PAGEOFFSET_QUICK_QUERY_LIMIT));
        
        normalisedQueryUri =
                normalisedQueryUri.replace(Constants.TEMPLATE_QUICK_LIMIT,
                        "limit/" + localSettings.getIntProperty(WebappConfig.PAGEOFFSET_QUICK_QUERY_LIMIT));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_LIMIT,
                        "LIMIT " + localSettings.getIntProperty(WebappConfig.PAGEOFFSET_INDIVIDUAL_QUERY_LIMIT));
        
        normalisedQueryUri =
                normalisedQueryUri.replace(Constants.TEMPLATE_LIMIT,
                        "limit/" + localSettings.getIntProperty(WebappConfig.PAGEOFFSET_INDIVIDUAL_QUERY_LIMIT));
        
        if(attributeList.containsKey(Constants.TEMPLATE_KEY_OFFSET))
        {
            try
            {
                int pageOffset = Integer.parseInt(attributeList.get(Constants.TEMPLATE_KEY_OFFSET));
                
                if(pageOffset < 1)
                {
                    QueryCreator.log.warn("QueryCreator: pageOffset was incorrect fixing it to page 1 bad pageOffset="
                            + pageOffset);
                    
                    pageOffset = 1;
                }
                
                // actual offset for pageOffset 1 is 0, and pageOffset 2 is
                // Settings.getIntPropertyFromConfig("pageoffsetIndividualQueryLimit")
                final int actualPageOffset =
                        (pageOffset - 1) * localSettings.getIntProperty(WebappConfig.PAGEOFFSET_INDIVIDUAL_QUERY_LIMIT);
                
                replacedString = replacedString.replace(Constants.TEMPLATE_SPARQL_OFFSET, "OFFSET " + actualPageOffset);
                replacedString = replacedString.replace(Constants.TEMPLATE_PAGEOFFSET, String.valueOf(pageOffset));
                
                normalisedQueryUri =
                        normalisedQueryUri.replace(Constants.TEMPLATE_SPARQL_OFFSET, "offset/" + actualPageOffset);
                normalisedQueryUri = normalisedQueryUri.replace(Constants.TEMPLATE_OFFSET, "offset/" + pageOffset);
                normalisedQueryUri =
                        normalisedQueryUri.replace(Constants.TEMPLATE_PAGEOFFSET, String.valueOf(pageOffset));
            }
            catch(final NumberFormatException nfe)
            {
                QueryCreator.log.error("QueryCreator: offset was not valid pageOffset="
                        + attributeList.get(Constants.TEMPLATE_KEY_OFFSET));
            }
        }
        
        if(attributeList.containsKey(Constants.TEMPLATE_KEY_USE_SPARQL_GRAPH))
        {
            final String useSparqlGraphString = attributeList.get(Constants.TEMPLATE_KEY_USE_SPARQL_GRAPH);
            
            try
            {
                final boolean useSparqlGraph = Boolean.parseBoolean(useSparqlGraphString);
                
                if(useSparqlGraph)
                {
                    if(attributeList.containsKey(Constants.TEMPLATE_KEY_GRAPH_URI))
                    {
                        final String graphUri = attributeList.get(Constants.TEMPLATE_KEY_GRAPH_URI);
                        
                        if(graphUri.trim().length() == 0)
                        {
                            QueryCreator.log
                                    .error("QueryCreator.createQuery: useSparqlGraph was true but the graphUri was invalid graphUri="
                                            + graphUri
                                            + " . Attempting to ignore graphStart and graphEnd for this query");
                            
                            replacedString = replacedString.replace(Constants.TEMPLATE_GRAPH_START, "");
                            replacedString = replacedString.replace(Constants.TEMPLATE_GRAPH_END, "");
                        }
                        else
                        {
                            replacedString =
                                    replacedString.replace(Constants.TEMPLATE_GRAPH_START, " GRAPH <" + graphUri
                                            + "> { ");
                            replacedString = replacedString.replace(Constants.TEMPLATE_GRAPH_END, " } ");
                        }
                    }
                    else
                    {
                        QueryCreator.log
                                .warn("QueryCreator.createQuery: useSparqlGraph was true but there was no graphUri specified. Attempting to ignore graphStart and graphEnd for this query");
                        
                        replacedString = replacedString.replace(Constants.TEMPLATE_GRAPH_START, "");
                        replacedString = replacedString.replace(Constants.TEMPLATE_GRAPH_END, "");
                    }
                }
                else
                {
                    // replace placeholders with zero spaces
                    replacedString = replacedString.replace(Constants.TEMPLATE_GRAPH_START, "");
                    replacedString = replacedString.replace(Constants.TEMPLATE_GRAPH_END, "");
                }
            }
            catch(final Exception ex)
            {
                QueryCreator.log.error("QueryCreator.createQuery: useSparqlGraph was not a valid boolean value "
                        + ex.getMessage());
            }
        }
        
        for(final String nextAttribute : attributeList.keySet())
        {
            // we have already handled queryString in a special way, the rest
            // are just simple replacements
            if(nextAttribute.equals(Constants.TEMPLATE_KEY_QUERY_STRING)
                    || nextAttribute.equals(Constants.TEMPLATE_KEY_OFFSET))
            {
                continue;
            }
            
            replacedString = replacedString.replace("${" + nextAttribute + "}", attributeList.get(nextAttribute));
            
            normalisedStandardUri =
                    normalisedStandardUri.replace("${" + nextAttribute + "}", attributeList.get(nextAttribute));
            
            if(QueryCreator._TRACE)
            {
                QueryCreator.log.trace("QueryCreator.createQuery: in replace loop ${" + nextAttribute + "}="
                        + attributeList.get(nextAttribute) + " normalisedStandardUri=" + normalisedStandardUri);
            }
            
            normalisedQueryUri =
                    normalisedQueryUri.replace("${" + nextAttribute + "}", attributeList.get(nextAttribute));
            
            if(QueryCreator._TRACE)
            {
                QueryCreator.log.trace("QueryCreator.createQuery: in replace loop ${" + nextAttribute + "}="
                        + attributeList.get(nextAttribute) + " normalisedQueryUri=" + normalisedQueryUri);
            }
        }
        
        String inputUrlEncoded_normalisedStandardUri = normalisedStandardUri;
        
        String inputPlusUrlEncoded_normalisedStandardUri = normalisedStandardUri;
        String inputPlusUrlEncoded_normalisedQueryUri = normalisedQueryUri;
        
        String inputUrlEncoded_normalisedQueryUri = normalisedQueryUri;
        String inputXmlEncoded_normalisedStandardUri = normalisedStandardUri;
        String inputXmlEncoded_normalisedQueryUri = normalisedQueryUri;
        
        String inputUrlEncoded_lowercase_normalisedStandardUri = normalisedStandardUri;
        String inputUrlEncoded_lowercase_normalisedQueryUri = normalisedQueryUri;
        String inputXmlEncoded_lowercase_normalisedStandardUri = normalisedStandardUri;
        String inputXmlEncoded_lowercase_normalisedQueryUri = normalisedQueryUri;
        
        String inputUrlEncoded_uppercase_normalisedStandardUri = normalisedStandardUri;
        String inputUrlEncoded_uppercase_normalisedQueryUri = normalisedQueryUri;
        String inputXmlEncoded_uppercase_normalisedStandardUri = normalisedStandardUri;
        String inputXmlEncoded_uppercase_normalisedQueryUri = normalisedQueryUri;
        
        String inputUrlEncoded_privatelowercase_normalisedStandardUri = normalisedStandardUri;
        String inputUrlEncoded_privatelowercase_normalisedQueryUri = normalisedQueryUri;
        String inputXmlEncoded_privatelowercase_normalisedStandardUri = normalisedStandardUri;
        String inputXmlEncoded_privatelowercase_normalisedQueryUri = normalisedQueryUri;
        
        String inputUrlEncoded_privateuppercase_normalisedStandardUri = normalisedStandardUri;
        String inputUrlEncoded_privateuppercase_normalisedQueryUri = normalisedQueryUri;
        String inputXmlEncoded_privateuppercase_normalisedStandardUri = normalisedStandardUri;
        String inputXmlEncoded_privateuppercase_normalisedQueryUri = normalisedQueryUri;
        
        replacedString =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        replacedString, QueryCreator.EMPTY_STRING_LIST, overallConvertAlternateToPreferredPrefix,
                        namespaceInputVariables, nextProvider);
        
        normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        normalisedStandardUri, QueryCreator.EMPTY_STRING_LIST,
                        overallConvertAlternateToPreferredPrefix, namespaceInputVariables, nextProvider);
        
        inputUrlEncoded_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputUrlEncoded_normalisedStandardUri, QueryCreator.inputUrlEncodeInstructions,
                        overallConvertAlternateToPreferredPrefix, namespaceInputVariables, nextProvider);
        
        inputXmlEncoded_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputXmlEncoded_normalisedStandardUri, QueryCreator.inputXmlEncodeInstructions,
                        overallConvertAlternateToPreferredPrefix, namespaceInputVariables, nextProvider);
        
        normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        normalisedQueryUri, QueryCreator.EMPTY_STRING_LIST, overallConvertAlternateToPreferredPrefix,
                        namespaceInputVariables, nextProvider);
        
        inputUrlEncoded_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputUrlEncoded_normalisedQueryUri, QueryCreator.inputUrlEncodeInstructions,
                        overallConvertAlternateToPreferredPrefix, namespaceInputVariables, nextProvider);
        
        inputPlusUrlEncoded_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputPlusUrlEncoded_normalisedStandardUri, QueryCreator.inputPlusUrlEncodeInstructions,
                        overallConvertAlternateToPreferredPrefix, namespaceInputVariables, nextProvider);
        
        inputPlusUrlEncoded_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputPlusUrlEncoded_normalisedQueryUri, QueryCreator.inputPlusUrlEncodeInstructions,
                        overallConvertAlternateToPreferredPrefix, namespaceInputVariables, nextProvider);
        
        inputXmlEncoded_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputXmlEncoded_normalisedQueryUri, QueryCreator.inputXmlEncodeInstructions,
                        overallConvertAlternateToPreferredPrefix, namespaceInputVariables, nextProvider);
        
        inputUrlEncoded_lowercase_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputUrlEncoded_lowercase_normalisedStandardUri,
                        QueryCreator.inputUrlEncodedlowercaseInstructions, overallConvertAlternateToPreferredPrefix,
                        namespaceInputVariables, nextProvider);
        inputUrlEncoded_lowercase_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputUrlEncoded_lowercase_normalisedQueryUri,
                        QueryCreator.inputUrlEncodedlowercaseInstructions, overallConvertAlternateToPreferredPrefix,
                        namespaceInputVariables, nextProvider);
        
        inputXmlEncoded_lowercase_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputXmlEncoded_lowercase_normalisedStandardUri,
                        QueryCreator.inputXmlEncodedlowercaseInstructions, overallConvertAlternateToPreferredPrefix,
                        namespaceInputVariables, nextProvider);
        inputXmlEncoded_lowercase_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputXmlEncoded_lowercase_normalisedQueryUri,
                        QueryCreator.inputXmlEncodedlowercaseInstructions, overallConvertAlternateToPreferredPrefix,
                        namespaceInputVariables, nextProvider);
        
        inputUrlEncoded_uppercase_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputUrlEncoded_uppercase_normalisedStandardUri,
                        QueryCreator.inputUrlEncodeduppercaseInstructions, overallConvertAlternateToPreferredPrefix,
                        namespaceInputVariables, nextProvider);
        inputUrlEncoded_uppercase_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputUrlEncoded_uppercase_normalisedQueryUri,
                        QueryCreator.inputUrlEncodeduppercaseInstructions, overallConvertAlternateToPreferredPrefix,
                        namespaceInputVariables, nextProvider);
        
        inputXmlEncoded_uppercase_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputXmlEncoded_uppercase_normalisedStandardUri,
                        QueryCreator.inputXmlEncodeduppercaseInstructions, overallConvertAlternateToPreferredPrefix,
                        namespaceInputVariables, nextProvider);
        inputXmlEncoded_uppercase_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputXmlEncoded_uppercase_normalisedQueryUri,
                        QueryCreator.inputXmlEncodeduppercaseInstructions, overallConvertAlternateToPreferredPrefix,
                        namespaceInputVariables, nextProvider);
        
        inputUrlEncoded_privatelowercase_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputUrlEncoded_privatelowercase_normalisedStandardUri,
                        QueryCreator.inputUrlEncodedprivatelowercaseInstructions,
                        overallConvertAlternateToPreferredPrefix, namespaceInputVariables, nextProvider);
        inputUrlEncoded_privatelowercase_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputUrlEncoded_privatelowercase_normalisedQueryUri,
                        QueryCreator.inputUrlEncodedprivatelowercaseInstructions,
                        overallConvertAlternateToPreferredPrefix, namespaceInputVariables, nextProvider);
        
        inputXmlEncoded_privatelowercase_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputXmlEncoded_privatelowercase_normalisedStandardUri,
                        QueryCreator.inputXmlEncodedprivatelowercaseInstructions,
                        overallConvertAlternateToPreferredPrefix, namespaceInputVariables, nextProvider);
        inputXmlEncoded_privatelowercase_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputXmlEncoded_privatelowercase_normalisedQueryUri,
                        QueryCreator.inputXmlEncodedprivatelowercaseInstructions,
                        overallConvertAlternateToPreferredPrefix, namespaceInputVariables, nextProvider);
        
        inputUrlEncoded_privateuppercase_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputUrlEncoded_privateuppercase_normalisedStandardUri,
                        QueryCreator.inputUrlEncodedprivateuppercaseInstructions,
                        overallConvertAlternateToPreferredPrefix, namespaceInputVariables, nextProvider);
        inputUrlEncoded_privateuppercase_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputUrlEncoded_privateuppercase_normalisedQueryUri,
                        QueryCreator.inputUrlEncodedprivateuppercaseInstructions,
                        overallConvertAlternateToPreferredPrefix, namespaceInputVariables, nextProvider);
        
        inputXmlEncoded_privateuppercase_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputXmlEncoded_privateuppercase_normalisedStandardUri,
                        QueryCreator.inputXmlEncodedprivateuppercaseInstructions,
                        overallConvertAlternateToPreferredPrefix, namespaceInputVariables, nextProvider);
        inputXmlEncoded_privateuppercase_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryParameters,
                        inputXmlEncoded_privateuppercase_normalisedQueryUri,
                        QueryCreator.inputXmlEncodedprivateuppercaseInstructions,
                        overallConvertAlternateToPreferredPrefix, namespaceInputVariables, nextProvider);
        
        if(QueryCreator._TRACE)
        {
            QueryCreator.log.trace("QueryCreator.createQuery: after match replacements replacedString="
                    + replacedString);
            QueryCreator.log.trace("QueryCreator.createQuery: after match replacements normalisedStandardUri="
                    + normalisedStandardUri);
            QueryCreator.log.trace("QueryCreator.createQuery: after match replacements normalisedQueryUri="
                    + normalisedQueryUri);
        }
        
        // These three are known to be able to insert the normalised standard
        // (ie, construct), query specific normalised, and endpointspecific
        // standard URI respectively
        replacedString = replacedString.replace(Constants.TEMPLATE_NORMALISED_STANDARD_URI, normalisedStandardUri);
        replacedString = replacedString.replace(Constants.TEMPLATE_NORMALISED_QUERY_URI, normalisedQueryUri);
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_URL_ENCODED_NORMALISED_STANDARD_URI,
                        StringUtils.percentEncode(normalisedStandardUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_PLUS_URL_ENCODED_NORMALISED_STANDARD_URI,
                        StringUtils.plusPercentEncode(normalisedStandardUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_NORMALISED_STANDARD_URI,
                        inputUrlEncoded_normalisedStandardUri);
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_PLUS_URL_ENCODED_NORMALISED_STANDARD_URI,
                        inputPlusUrlEncoded_normalisedStandardUri);
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_NORMALISED_QUERY_URI,
                        inputUrlEncoded_normalisedQueryUri);
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_PLUS_URL_ENCODED_NORMALISED_QUERY_URI,
                        inputPlusUrlEncoded_normalisedQueryUri);
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_NORMALISED_STANDARD_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_normalisedStandardUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_INPUT_PLUS_URL_ENCODED_NORMALISED_STANDARD_URI,
                        StringUtils.xmlEncodeString(inputPlusUrlEncoded_normalisedStandardUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_INPUT_PLUS_URL_ENCODED_NORMALISED_QUERY_URI,
                        StringUtils.xmlEncodeString(inputPlusUrlEncoded_normalisedQueryUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_NORMALISED_STANDARD_URI,
                        StringUtils.xmlEncodeString(normalisedStandardUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_URL_ENCODED_LOWERCASE_NORMALISED_STANDARD_URI,
                        StringUtils.percentEncode(normalisedStandardUri.toLowerCase()));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_URL_ENCODED_UPPERCASE_NORMALISED_STANDARD_URI,
                        StringUtils.percentEncode(normalisedStandardUri.toUpperCase()));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_LOWERCASE_NORMALISED_STANDARD_URI,
                        StringUtils.xmlEncodeString((normalisedStandardUri.toLowerCase())));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_UPPERCASE_NORMALISED_STANDARD_URI,
                        StringUtils.xmlEncodeString((normalisedStandardUri.toUpperCase())));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_LOWERCASE_NORMALISED_STANDARD_URI,
                        inputUrlEncoded_lowercase_normalisedStandardUri);
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_UPPERCASE_NORMALISED_STANDARD_URI,
                        inputUrlEncoded_uppercase_normalisedStandardUri);
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_PRIVATELOWERCASE_NORMALISED_STANDARD_URI,
                        inputUrlEncoded_privatelowercase_normalisedStandardUri);
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_PRIVATEUPPERCASE_NORMALISED_STANDARD_URI,
                        inputUrlEncoded_privateuppercase_normalisedStandardUri);
        
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_LOWERCASE_NORMALISED_STANDARD_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_lowercase_normalisedStandardUri));
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_UPPERCASE_NORMALISED_STANDARD_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_uppercase_normalisedStandardUri));
        
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_PRIVATELOWERCASE_NORMALISED_STANDARD_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_privatelowercase_normalisedStandardUri));
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_PRIVATEUPPERCASE_NORMALISED_STANDARD_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_privateuppercase_normalisedStandardUri));
        
        // replacedString = replacedString.replace(
        // "${xmlEncoded_normalisedOntologyUriPrefix}", Utilities
        // .xmlEncodeString(normalisedOntologyUriPrefix));
        // replacedString = replacedString.replace(
        // "${xmlEncoded_normalisedOntologyUriSuffix}", Utilities
        // .xmlEncodeString(normalisedOntologyUriSuffix));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_URL_ENCODED_NORMALISED_QUERY_URI,
                        StringUtils.percentEncode(normalisedQueryUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_NORMALISED_QUERY_URI,
                        StringUtils.xmlEncodeString(normalisedQueryUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_NORMALISED_QUERY_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_normalisedQueryUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_URL_ENCODED_LOWERCASE_NORMALISED_QUERY_URI,
                        StringUtils.percentEncode(normalisedQueryUri.toLowerCase()));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_URL_ENCODED_UPPERCASE_NORMALISED_QUERY_URI,
                        StringUtils.percentEncode(normalisedQueryUri.toUpperCase()));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_LOWERCASE_NORMALISED_QUERY_URI,
                        StringUtils.xmlEncodeString((normalisedQueryUri.toLowerCase())));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_UPPERCASE_NORMALISED_QUERY_URI,
                        StringUtils.xmlEncodeString((normalisedQueryUri.toUpperCase())));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_LOWERCASE_NORMALISED_QUERY_URI,
                        inputUrlEncoded_lowercase_normalisedQueryUri);
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_UPPERCASE_NORMALISED_QUERY_URI,
                        inputUrlEncoded_uppercase_normalisedQueryUri);
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_PRIVATELOWERCASE_NORMALISED_QUERY_URI,
                        inputUrlEncoded_privatelowercase_normalisedQueryUri);
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_PRIVATEUPPERCASE_NORMALISED_QUERY_URI,
                        inputUrlEncoded_privateuppercase_normalisedQueryUri);
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_LOWERCASE_NORMALISED_QUERY_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_lowercase_normalisedQueryUri));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_UPPERCASE_NORMALISED_QUERY_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_uppercase_normalisedQueryUri));
        
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_PRIVATELOWERCASE_NORMALISED_QUERY_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_privatelowercase_normalisedQueryUri));
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_PRIVATEUPPERCASE_NORMALISED_QUERY_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_privateuppercase_normalisedQueryUri));
        
        if(QueryCreator._TRACE)
        {
            QueryCreator.log.trace("QueryCreator.createQuery: before regex loop started replacedString="
                    + replacedString);
            QueryCreator.log
                    .trace("QueryCreator.createQuery: before regex loop started xmlEncoded_inputUrlEncoded_normalisedQueryUri="
                            + StringUtils.xmlEncodeString(inputUrlEncoded_normalisedQueryUri));
        }
        
        String endpointSpecificUri = normalisedStandardUri;
        String endpointSpecificQueryUri = normalisedQueryUri;
        // String endpointSpecificOntologyUri = normalisedOntologyUriPrefix;
        
        String inputUrlEncoded_endpointSpecificUri = inputUrlEncoded_normalisedStandardUri;
        String inputXmlEncoded_endpointSpecificUri = inputXmlEncoded_normalisedStandardUri;
        String inputUrlEncoded_endpointSpecificQueryUri = inputUrlEncoded_normalisedQueryUri;
        String inputXmlEncoded_endpointSpecificQueryUri = inputXmlEncoded_normalisedQueryUri;
        
        String inputPlusUrlEncoded_endpointSpecificUri = inputPlusUrlEncoded_normalisedStandardUri;
        String inputPlusUrlEncoded_endpointSpecificQueryUri = inputPlusUrlEncoded_normalisedQueryUri;
        
        String inputUrlEncoded_lowercase_endpointSpecificUri = inputUrlEncoded_lowercase_normalisedStandardUri;
        String inputXmlEncoded_lowercase_endpointSpecificUri = inputXmlEncoded_lowercase_normalisedStandardUri;
        String inputUrlEncoded_lowercase_endpointSpecificQueryUri = inputUrlEncoded_lowercase_normalisedQueryUri;
        String inputXmlEncoded_lowercase_endpointSpecificQueryUri = inputXmlEncoded_lowercase_normalisedQueryUri;
        
        String inputUrlEncoded_privatelowercase_endpointSpecificUri =
                inputUrlEncoded_privatelowercase_normalisedStandardUri;
        String inputXmlEncoded_privatelowercase_endpointSpecificUri =
                inputXmlEncoded_privatelowercase_normalisedStandardUri;
        String inputUrlEncoded_privatelowercase_endpointSpecificQueryUri =
                inputUrlEncoded_privatelowercase_normalisedQueryUri;
        String inputXmlEncoded_privatelowercase_endpointSpecificQueryUri =
                inputXmlEncoded_privatelowercase_normalisedQueryUri;
        
        String inputUrlEncoded_uppercase_endpointSpecificUri = inputUrlEncoded_uppercase_normalisedStandardUri;
        String inputXmlEncoded_uppercase_endpointSpecificUri = inputXmlEncoded_uppercase_normalisedStandardUri;
        String inputUrlEncoded_uppercase_endpointSpecificQueryUri = inputUrlEncoded_uppercase_normalisedQueryUri;
        String inputXmlEncoded_uppercase_endpointSpecificQueryUri = inputXmlEncoded_uppercase_normalisedQueryUri;
        
        String inputUrlEncoded_privateuppercase_endpointSpecificUri =
                inputUrlEncoded_privateuppercase_normalisedStandardUri;
        String inputXmlEncoded_privateuppercase_endpointSpecificUri =
                inputXmlEncoded_privateuppercase_normalisedStandardUri;
        String inputUrlEncoded_privateuppercase_endpointSpecificQueryUri =
                inputUrlEncoded_privateuppercase_normalisedQueryUri;
        String inputXmlEncoded_privateuppercase_endpointSpecificQueryUri =
                inputXmlEncoded_privateuppercase_normalisedQueryUri;
        
        if(nextProvider != null)
        {
            final Collection<NormalisationRule> normalisationsNeeded =
                    RuleUtils.getSortedRulesByUris(localSettings.getAllNormalisationRules(),
                            nextProvider.getNormalisationUris(), SortOrder.LOWEST_ORDER_FIRST);
            
            for(final NormalisationRule nextRule : normalisationsNeeded)
            {
                if(nextRule.isUsedWithProfileList(includedProfiles, recogniseImplicitRdfRuleInclusions,
                        includeNonProfileMatchedRdfRules))
                {
                    if(nextRule instanceof TransformingRule)
                    {
                        final TransformingRule nextTransformingRule = (TransformingRule)nextRule;
                        
                        endpointSpecificUri = (String)nextTransformingRule.stageQueryVariables(endpointSpecificUri);
                        endpointSpecificQueryUri =
                                (String)nextTransformingRule.stageQueryVariables(endpointSpecificQueryUri);
                        // endpointSpecificOntologyUri = (String)nextTransformingRule
                        // .applyInputRegex(endpointSpecificOntologyUri);
                        
                        inputUrlEncoded_endpointSpecificUri =
                                (String)nextTransformingRule.stageQueryVariables(inputUrlEncoded_endpointSpecificUri);
                        inputPlusUrlEncoded_endpointSpecificUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputPlusUrlEncoded_endpointSpecificUri);
                        inputXmlEncoded_endpointSpecificUri =
                                (String)nextTransformingRule.stageQueryVariables(inputXmlEncoded_endpointSpecificUri);
                        inputUrlEncoded_endpointSpecificQueryUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputUrlEncoded_endpointSpecificQueryUri);
                        inputPlusUrlEncoded_endpointSpecificQueryUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputPlusUrlEncoded_endpointSpecificQueryUri);
                        inputXmlEncoded_endpointSpecificQueryUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputXmlEncoded_endpointSpecificQueryUri);
                        
                        inputUrlEncoded_lowercase_endpointSpecificUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputUrlEncoded_lowercase_endpointSpecificUri);
                        inputXmlEncoded_lowercase_endpointSpecificUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputXmlEncoded_lowercase_endpointSpecificUri);
                        inputUrlEncoded_uppercase_endpointSpecificUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputUrlEncoded_uppercase_endpointSpecificUri);
                        inputXmlEncoded_uppercase_endpointSpecificUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputXmlEncoded_uppercase_endpointSpecificUri);
                        
                        inputUrlEncoded_lowercase_endpointSpecificQueryUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputUrlEncoded_lowercase_endpointSpecificQueryUri);
                        inputXmlEncoded_lowercase_endpointSpecificQueryUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputXmlEncoded_lowercase_endpointSpecificQueryUri);
                        inputUrlEncoded_uppercase_endpointSpecificQueryUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputUrlEncoded_uppercase_endpointSpecificQueryUri);
                        inputXmlEncoded_uppercase_endpointSpecificQueryUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputXmlEncoded_uppercase_endpointSpecificQueryUri);
                        
                        inputUrlEncoded_privatelowercase_endpointSpecificUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputUrlEncoded_privatelowercase_endpointSpecificUri);
                        inputXmlEncoded_privatelowercase_endpointSpecificUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputXmlEncoded_privatelowercase_endpointSpecificUri);
                        inputUrlEncoded_privateuppercase_endpointSpecificUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputUrlEncoded_privateuppercase_endpointSpecificUri);
                        inputXmlEncoded_privateuppercase_endpointSpecificUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputXmlEncoded_privateuppercase_endpointSpecificUri);
                        
                        inputUrlEncoded_privatelowercase_endpointSpecificQueryUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputUrlEncoded_privatelowercase_endpointSpecificQueryUri);
                        inputXmlEncoded_privatelowercase_endpointSpecificQueryUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputXmlEncoded_privatelowercase_endpointSpecificQueryUri);
                        inputUrlEncoded_privateuppercase_endpointSpecificQueryUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputUrlEncoded_privateuppercase_endpointSpecificQueryUri);
                        inputXmlEncoded_privateuppercase_endpointSpecificQueryUri =
                                (String)nextTransformingRule
                                        .stageQueryVariables(inputXmlEncoded_privateuppercase_endpointSpecificQueryUri);
                        
                        if(QueryCreator._TRACE)
                        {
                            QueryCreator.log.trace("QueryCreator.createQuery: in regex loop endpointSpecificUri="
                                    + endpointSpecificUri);
                            // QueryCreator.log
                            // .trace("QueryCreator.createQuery: in regex loop endpointSpecificOntologyUri="
                            // + endpointSpecificOntologyUri);
                            QueryCreator.log
                                    .trace("QueryCreator.createQuery: in regex loop inputUrlEncoded_endpointSpecificUri="
                                            + inputUrlEncoded_endpointSpecificUri);
                            QueryCreator.log
                                    .trace("QueryCreator.createQuery: in regex loop inputXmlEncoded_endpointSpecificUri="
                                            + inputXmlEncoded_endpointSpecificUri);
                        }
                    }
                }
            }
        }
        
        replacedString = replacedString.replace(Constants.TEMPLATE_ENDPOINT_SPECIFIC_URI, endpointSpecificUri);
        replacedString =
                replacedString.replace(Constants.TEMPLATE_ENDPOINT_SPECIFIC_QUERY_URI, endpointSpecificQueryUri);
        // replacedString = replacedString.replace(
        // "${endpointSpecificOntologyUri}", endpointSpecificOntologyUri);
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_NTRIPLES_ENCODED_ENDPOINT_SPECIFIC_URI,
                        StringUtils.ntriplesEncode(endpointSpecificUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_NTRIPLES_ENCODED_NORMALISED_STANDARD_URI,
                        StringUtils.ntriplesEncode(normalisedStandardUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_NTRIPLES_ENCODED_NORMALISED_QUERY_URI,
                        StringUtils.ntriplesEncode(normalisedQueryUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_NTRIPLES_ENCODED_ENDPOINT_SPECIFIC_URI,
                        StringUtils.xmlEncodeString(StringUtils.ntriplesEncode(endpointSpecificUri)));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_NTRIPLES_ENCODED_NORMALISED_STANDARD_URI,
                        StringUtils.xmlEncodeString(StringUtils.ntriplesEncode(normalisedStandardUri)));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_NTRIPLES_ENCODED_NORMALISED_QUERY_URI,
                        StringUtils.xmlEncodeString(StringUtils.ntriplesEncode(normalisedQueryUri)));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_URL_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_URI,
                        StringUtils.percentEncode(endpointSpecificUri.toLowerCase()));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_URL_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_URI,
                        StringUtils.percentEncode(endpointSpecificUri.toUpperCase()));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_URL_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_QUERY_URI,
                        StringUtils.percentEncode(endpointSpecificQueryUri.toLowerCase()));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_URL_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_QUERY_URI,
                        StringUtils.percentEncode(endpointSpecificQueryUri.toUpperCase()));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_ENDPOINT_SPECIFIC_URI,
                        StringUtils.xmlEncodeString(endpointSpecificUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_ENDPOINT_SPECIFIC_QUERY_URI,
                        StringUtils.xmlEncodeString(endpointSpecificQueryUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_URI,
                        StringUtils.xmlEncodeString(endpointSpecificUri.toLowerCase()));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_URI,
                        StringUtils.xmlEncodeString(endpointSpecificUri.toUpperCase()));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_QUERY_URI,
                        StringUtils.xmlEncodeString(endpointSpecificQueryUri.toLowerCase()));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_QUERY_URI,
                        StringUtils.xmlEncodeString(endpointSpecificQueryUri.toUpperCase()));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_XML_ENCODED_ENDPOINT_SPECIFIC_URI,
                        inputXmlEncoded_endpointSpecificUri);
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_XML_ENCODED_ENDPOINT_SPECIFIC_QUERY_URI,
                        inputXmlEncoded_endpointSpecificQueryUri);
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_URL_ENCODED_ENDPOINT_SPECIFIC_URI,
                        StringUtils.percentEncode(endpointSpecificUri));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_URL_ENCODED_ENDPOINT_SPECIFIC_QUERY_URI,
                        StringUtils.percentEncode(endpointSpecificQueryUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_ENDPOINT_SPECIFIC_URI,
                        inputUrlEncoded_endpointSpecificUri);
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_ENDPOINT_SPECIFIC_QUERY_URI,
                        inputUrlEncoded_endpointSpecificQueryUri);
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_PLUS_URL_ENCODED_ENDPOINT_SPECIFIC_URI,
                        inputPlusUrlEncoded_endpointSpecificUri);
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_PLUS_URL_ENCODED_ENDPOINT_SPECIFIC_QUERY_URI,
                        inputPlusUrlEncoded_endpointSpecificQueryUri);
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_ENDPOINT_SPECIFIC_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_endpointSpecificUri));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_ENDPOINT_SPECIFIC_QUERY_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_endpointSpecificQueryUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_INPUT_PLUS_URL_ENCODED_ENDPOINT_SPECIFIC_URI,
                        StringUtils.xmlEncodeString(inputPlusUrlEncoded_endpointSpecificUri));
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_XML_ENCODED_INPUT_PLUS_URL_ENCODED_ENDPOINT_SPECIFIC_QUERY_URI,
                        StringUtils.xmlEncodeString(inputPlusUrlEncoded_endpointSpecificQueryUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_XML_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_URI,
                        inputXmlEncoded_uppercase_endpointSpecificUri);
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_URI,
                        inputUrlEncoded_uppercase_endpointSpecificUri);
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_XML_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_QUERY_URI,
                        inputXmlEncoded_uppercase_endpointSpecificQueryUri);
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_QUERY_URI,
                        inputUrlEncoded_uppercase_endpointSpecificQueryUri);
        
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_uppercase_endpointSpecificUri));
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_QUERY_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_uppercase_endpointSpecificQueryUri));
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_XML_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_URI,
                        inputXmlEncoded_lowercase_endpointSpecificUri);
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_URI,
                        inputUrlEncoded_lowercase_endpointSpecificUri);
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_XML_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_QUERY_URI,
                        inputXmlEncoded_lowercase_endpointSpecificQueryUri);
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_QUERY_URI,
                        inputUrlEncoded_lowercase_endpointSpecificQueryUri);
        
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_lowercase_endpointSpecificUri));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_XML_ENCODED_PRIVATEUPPERCASE_ENDPOINT_SPECIFIC_URI,
                        inputXmlEncoded_privateuppercase_endpointSpecificUri);
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_PRIVATEUPPERCASE_ENDPOINT_SPECIFIC_URI,
                        inputUrlEncoded_privateuppercase_endpointSpecificUri);
        
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_QUERY_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_lowercase_endpointSpecificQueryUri));
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_INPUT_XML_ENCODED_PRIVATEUPPERCASE_ENDPOINT_SPECIFIC_QUERY_URI,
                        inputXmlEncoded_privateuppercase_endpointSpecificQueryUri);
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_INPUT_URL_ENCODED_PRIVATEUPPERCASE_ENDPOINT_SPECIFIC_QUERY_URI,
                        inputUrlEncoded_privateuppercase_endpointSpecificQueryUri);
        
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_PRIVATEUPPERCASE_ENDPOINT_SPECIFIC_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_privateuppercase_endpointSpecificUri));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_XML_ENCODED_PRIVATELOWERCASE_ENDPOINT_SPECIFIC_URI,
                        inputXmlEncoded_privatelowercase_endpointSpecificUri);
        replacedString =
                replacedString.replace(Constants.TEMPLATE_INPUT_URL_ENCODED_PRIVATELOWERCASE_ENDPOINT_SPECIFIC_URI,
                        inputUrlEncoded_privatelowercase_endpointSpecificUri);
        
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_PRIVATEUPPERCASE_ENDPOINT_SPECIFIC_QUERY_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_privateuppercase_endpointSpecificQueryUri));
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_INPUT_XML_ENCODED_PRIVATELOWERCASE_ENDPOINT_SPECIFIC_QUERY_URI,
                        inputXmlEncoded_privatelowercase_endpointSpecificQueryUri);
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_INPUT_URL_ENCODED_PRIVATELOWERCASE_ENDPOINT_SPECIFIC_QUERY_URI,
                        inputUrlEncoded_privatelowercase_endpointSpecificQueryUri);
        
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_PRIVATELOWERCASE_ENDPOINT_SPECIFIC_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_privatelowercase_endpointSpecificUri));
        replacedString =
                replacedString.replace(
                        Constants.TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_PRIVATELOWERCASE_ENDPOINT_SPECIFIC_QUERY_URI,
                        StringUtils.xmlEncodeString(inputUrlEncoded_privatelowercase_endpointSpecificQueryUri));
        
        if(QueryCreator._DEBUG)
        {
            final long end = System.currentTimeMillis();
            
            QueryCreator.log.debug(String.format("%s: timing=%10d", "QueryCreator.doReplacementsOnString",
                    (end - start)));
            
            QueryCreator.log.debug("QueryCreator.doReplacementsOnString: returning replacedString=" + replacedString);
        }
        
        return replacedString;
    }
    
    /**
     * @param nextQueryType
     * @param nextProvider
     * @param queryParameters
     * @param nextEndpoint
     * @param realHostName
     * @param pageOffset
     * @return
     */
    public static Map<String, String> getAttributeListFor(final QueryType nextIncludedQueryType,
            final Provider nextProvider, final Map<String, String> queryParameters, final String nextEndpoint,
            final String realHostName, final int pageOffset, final QueryAllConfiguration localSettings)
    {
        if(QueryCreator._TRACE)
        {
            QueryCreator.log.trace("QueryCreator.getAttributeListFor: called with nextProvider=" + nextProvider
                    + " queryParameters=" + queryParameters + " nextEndpoint=" + nextEndpoint + " realHostName="
                    + realHostName + " pageOffset=" + pageOffset);
        }
        
        final long start = System.currentTimeMillis();
        
        final Map<String, String> attributeList = new HashMap<String, String>(50);
        
        if(nextIncludedQueryType != null && nextIncludedQueryType.getKey() != null)
        {
            attributeList.put(Constants.TEMPLATE_KEY_INCLUDED_QUERY_TYPE, nextIncludedQueryType.getKey().stringValue());
        }
        
        // TODO: decide on default for hostName
        attributeList.put(Constants.TEMPLATE_KEY_DEFAULT_HOST_NAME, localSettings.getStringProperty(WebappConfig.HOST_NAME));
        
        attributeList.put(Constants.TEMPLATE_KEY_DEFAULT_HOST_ADDRESS, localSettings.getDefaultHostAddress());
        attributeList.put(Constants.TEMPLATE_KEY_DEFAULT_SEPARATOR, localSettings.getSeparator());
        attributeList.put(Constants.TEMPLATE_KEY_REAL_HOST_NAME, realHostName);
        attributeList.put(Constants.TEMPLATE_KEY_QUERY_STRING, queryParameters.get(Constants.QUERY));
        
        // TODO: make this section extensible
        if(nextProvider != null && nextProvider instanceof SparqlProvider)
        {
            final SparqlProvider nextSparqlProvider = (SparqlProvider)nextProvider;
            
            attributeList.put(Constants.TEMPLATE_KEY_GRAPH_URI, nextSparqlProvider.getSparqlGraphUri());
            attributeList.put(Constants.TEMPLATE_KEY_USE_SPARQL_GRAPH,
                    Boolean.toString(nextSparqlProvider.getUseSparqlGraph()));
            attributeList.put(Constants.TEMPLATE_KEY_URL_ENCODED_GRAPH_URI,
                    StringUtils.percentEncode(nextSparqlProvider.getSparqlGraphUri()));
            attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_GRAPH_URI,
                    StringUtils.xmlEncodeString(nextSparqlProvider.getSparqlGraphUri()));
            attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_GRAPH_URI,
                    StringUtils.xmlEncodeString(StringUtils.percentEncode(nextSparqlProvider.getSparqlGraphUri())));
        }
        
        attributeList.put(Constants.TEMPLATE_KEY_OFFSET, Integer.toString(pageOffset));
        
        attributeList.put(Constants.TEMPLATE_KEY_ENDPOINT_URL, nextEndpoint);
        
        attributeList.put(Constants.TEMPLATE_KEY_URL_ENCODED_DEFAULT_HOST_NAME,
                StringUtils.percentEncode(localSettings.getStringProperty(WebappConfig.HOST_NAME)));
        attributeList.put(Constants.TEMPLATE_KEY_URL_ENCODED_DEFAULT_HOST_ADDRESS,
                StringUtils.percentEncode(localSettings.getDefaultHostAddress()));
        attributeList.put(Constants.TEMPLATE_KEY_URL_ENCODED_DEFAULT_SEPARATOR,
                StringUtils.percentEncode(localSettings.getSeparator()));
        attributeList.put(Constants.TEMPLATE_KEY_URL_ENCODED_ENDPOINT_URL, StringUtils.percentEncode(nextEndpoint));
        attributeList.put(Constants.TEMPLATE_KEY_URL_ENCODED_REAL_HOST_NAME, StringUtils.percentEncode(realHostName));
        attributeList.put(Constants.TEMPLATE_KEY_URL_ENCODED_QUERY_STRING,
                StringUtils.percentEncode(queryParameters.get(Constants.QUERY)));
        
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_DEFAULT_HOST_NAME,
                StringUtils.xmlEncodeString(localSettings.getStringProperty(WebappConfig.HOST_NAME)));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_DEFAULT_HOST_ADDRESS,
                StringUtils.xmlEncodeString(localSettings.getDefaultHostAddress()));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_DEFAULT_SEPARATOR,
                StringUtils.xmlEncodeString(localSettings.getSeparator()));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_ENDPOINT_URL, StringUtils.xmlEncodeString(nextEndpoint));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_REAL_HOST_NAME, StringUtils.xmlEncodeString(realHostName));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_QUERY_STRING,
                StringUtils.xmlEncodeString(queryParameters.get(Constants.QUERY)));
        
        attributeList
                .put(Constants.TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_DEFAULT_HOST_NAME, StringUtils
                        .xmlEncodeString(StringUtils.percentEncode(localSettings.getStringProperty(WebappConfig.HOST_NAME))));
        attributeList.put(
                Constants.TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_DEFAULT_HOST_ADDRESS,
                StringUtils.xmlEncodeString(localSettings.getDefaultHostAddress()));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_DEFAULT_SEPARATOR,
                StringUtils.xmlEncodeString(StringUtils.percentEncode(localSettings.getSeparator())));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_ENDPOINT_URL,
                StringUtils.xmlEncodeString(StringUtils.percentEncode(nextEndpoint)));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_REAL_HOST_NAME,
                StringUtils.xmlEncodeString(StringUtils.percentEncode(realHostName)));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_QUERY_STRING,
                StringUtils.xmlEncodeString(StringUtils.percentEncode(queryParameters.get(Constants.QUERY))));
        
        if(QueryCreator._DEBUG)
        {
            final long end = System.currentTimeMillis();
            
            QueryCreator.log.debug(String.format("%s: timing=%10d", "QueryCreator.getAttributeListFor", (end - start)));
            
        }
        
        return attributeList;
    }
    
    /**
     * @param originalQueryType
     * @param templateString
     * @param specialInstructions
     * @param convertAlternateToPreferredPrefix
     *            TODO
     * @param namespaceInputVariables
     *            TODO
     * @param queryString
     * @return
     */
    public static String matchAndReplaceInputVariablesForQueryType(final QueryType originalQueryType,
            final Map<String, String> queryParameters, final String templateString,
            final List<String> specialInstructions, final boolean convertAlternateToPreferredPrefix,
            final Map<String, Collection<NamespaceEntry>> namespaceInputVariables, final Provider nextProvider)
    {
        if(QueryCreator._DEBUG)
        {
            QueryCreator.log.debug("QueryCreator.matchAndReplaceInputVariablesForQueryType: templateString="
                    + templateString + " convertAlternateToPreferredPrefix=" + convertAlternateToPreferredPrefix
                    + " namespaceInputVariables=" + namespaceInputVariables);
        }
        
        final long start = System.currentTimeMillis();
        
        String replacedString = templateString;
        String separatorString = "";
        String authorityString = "";
        
        final Map<String, List<String>> allMatches = originalQueryType.matchesForQueryParameters(queryParameters);
        
        for(final String nextMatchTag : allMatches.keySet())
        {
            final boolean isPublic = originalQueryType.isInputVariablePublic(nextMatchTag);
            final boolean isNamespace = originalQueryType.isInputVariableNamespace(nextMatchTag);
            
            if(isNamespace)
            {
                QueryCreator.log.debug("isNamespace nextMatchTag=" + nextMatchTag);
            }
            
            for(String inputReplaceString : allMatches.get(nextMatchTag))
            {
                
                QueryCreator.log.debug("allMatches.get(nextMatchTag)=" + allMatches.get(nextMatchTag));
                
                // FIXME: determine why namespaceInputVariables isn't being sent here properly from
                // RdfFetchController.generateQueryBundlesForQueryTypeAndProviders
                // FIXME: determine why namespaceInputVariables isn't being sent here properly from
                // GeneralServlet.doQueryUnknown
                if(isNamespace && namespaceInputVariables.containsKey(nextMatchTag))
                {
                    QueryCreator.log.debug("isNamespace and namespaceInputVariables.containsKey(nextMatchTag)");
                    
                    boolean foundANamespace = false;
                    // TODO: What happens if there could be more than one match here, as we aren't
                    // ordering the NamespaceEntries... could have irregular behaviour
                    // Currently, the first namespace to match will set the separatorString and
                    // authorityString to match its definition and then break the loop
                    for(final NamespaceEntry nextNamespaceEntry : namespaceInputVariables.get(nextMatchTag))
                    {
                        if(nextProvider.containsNamespaceOrDefault(nextNamespaceEntry.getKey()))
                        {
                            QueryCreator.log.debug("inputReplaceString=" + inputReplaceString);
                            
                            if(convertAlternateToPreferredPrefix
                                    && nextNamespaceEntry.getConvertQueriesToPreferredPrefix())
                            {
                                inputReplaceString = nextNamespaceEntry.getPreferredPrefix();
                            }
                            
                            separatorString = nextNamespaceEntry.getSeparator();
                            authorityString = nextNamespaceEntry.getAuthority().stringValue();
                            
                            QueryCreator.log.debug("inputReplaceString=" + inputReplaceString);
                            
                            foundANamespace = true;
                            
                            break;
                        }
                    }
                    
                    if(!foundANamespace)
                    {
                        QueryCreator.log.warn("Could not find a namespace for tag=" + nextMatchTag + " nextProvider="
                                + nextProvider.getKey() + " inputReplaceString=" + inputReplaceString);
                    }
                }
                if(QueryCreator._TRACE)
                {
                    QueryCreator.log.trace("QueryCreator.matchAndReplaceInputVariablesForQueryType: nextMatchTag="
                            + nextMatchTag);
                    QueryCreator.log
                            .trace("QueryCreator.matchAndReplaceInputVariablesForQueryType: allMatches.get(nextMatchTag)="
                                    + allMatches.get(nextMatchTag));
                    QueryCreator.log
                            .trace("QueryCreator.matchAndReplaceInputVariablesForQueryType: indexOf matchString="
                                    + replacedString.indexOf("${" + nextMatchTag + "}"));
                }
                
                // in some cases we want to specially encode user input without
                // having said this specifically in the template, this is where it
                // happens, in the order that the list has been constructed in
                for(final String specialInstruction : specialInstructions)
                {
                    if(specialInstruction.equals(Constants.INPUT_URL_ENCODED))
                    {
                        inputReplaceString = StringUtils.percentEncode(inputReplaceString);
                    }
                    else if(specialInstruction.equals(Constants.INPUT_PLUS_URL_ENCODED))
                    {
                        inputReplaceString = StringUtils.plusPercentEncode(inputReplaceString);
                    }
                    else if(specialInstruction.equals(Constants.INPUT_XML_ENCODED))
                    {
                        inputReplaceString = StringUtils.xmlEncodeString(inputReplaceString);
                    }
                    else if(specialInstruction.equals(Constants.INPUT_NTRIPLES_ENCODED))
                    {
                        inputReplaceString = StringUtils.ntriplesEncode(inputReplaceString);
                    }
                    else if(specialInstruction.equals(Constants.LOWERCASE))
                    {
                        inputReplaceString = inputReplaceString.toLowerCase();
                    }
                    else if(specialInstruction.equals(Constants.UPPERCASE))
                    {
                        inputReplaceString = inputReplaceString.toUpperCase();
                    }
                    else if(specialInstruction.equals(Constants.PRIVATE_LOWERCASE))
                    {
                        if(!isPublic)
                        {
                            inputReplaceString = inputReplaceString.toLowerCase();
                        }
                    }
                    else if(specialInstruction.equals(Constants.PRIVATE_UPPERCASE))
                    {
                        if(!isPublic)
                        {
                            inputReplaceString = inputReplaceString.toUpperCase();
                        }
                    }
                }
                
                // TODO: test this
                replacedString = replacedString.replace("${separator}", separatorString);
                
                // TODO: test this
                replacedString = replacedString.replace("${authority}", authorityString);
                
                replacedString = replacedString.replace("${" + nextMatchTag + "}", inputReplaceString);
                
                replacedString =
                        replacedString.replace("${xmlEncoded_" + nextMatchTag + "}",
                                StringUtils.xmlEncodeString(inputReplaceString));
                
                replacedString =
                        replacedString.replace("${urlEncoded_" + nextMatchTag + "}",
                                StringUtils.percentEncode(inputReplaceString));
                
                replacedString =
                        replacedString.replace("${plusUrlEncoded_" + nextMatchTag + "}",
                                StringUtils.plusPercentEncode(inputReplaceString));
                
                // replacedString = replacedString.replace(plusSpaceEncodedMatchString,
                // plusSpaceEncodedReplaceString);
                
                replacedString =
                        replacedString.replace("${ntriplesEncoded_" + nextMatchTag + "}",
                                StringUtils.ntriplesEncode(inputReplaceString));
                
                replacedString =
                        replacedString.replace("${xmlEncoded_urlEncoded_" + nextMatchTag + "}",
                                StringUtils.xmlEncodeString(StringUtils.percentEncode(inputReplaceString)));
                replacedString =
                        replacedString.replace("${xmlEncoded_plusUrlEncoded_" + nextMatchTag + "}",
                                StringUtils.xmlEncodeString(StringUtils.plusPercentEncode(inputReplaceString)));
                
                replacedString =
                        replacedString.replace("${xmlEncoded_ntriplesEncoded_" + nextMatchTag + "}",
                                StringUtils.xmlEncodeString(StringUtils.ntriplesEncode(inputReplaceString)));
                
                /***********************/
                // Start lowercase region
                replacedString =
                        replacedString.replace("${lowercase_" + nextMatchTag + "}", inputReplaceString.toLowerCase());
                replacedString =
                        replacedString.replace("${urlEncoded_lowercase_" + nextMatchTag + "}",
                                StringUtils.percentEncode(inputReplaceString.toLowerCase()));
                replacedString =
                        replacedString.replace("${xmlEncoded_lowercase_" + nextMatchTag + "}",
                                StringUtils.xmlEncodeString(inputReplaceString.toLowerCase()));
                replacedString =
                        replacedString.replace("${xmlEncoded_urlEncoded_lowercase_" + nextMatchTag + "}", StringUtils
                                .xmlEncodeString(StringUtils.percentEncode(inputReplaceString.toLowerCase())));
                replacedString =
                        replacedString
                                .replace("${xmlEncoded_ntriplesEncoded_lowercase_" + nextMatchTag + "}", StringUtils
                                        .xmlEncodeString(StringUtils.ntriplesEncode(inputReplaceString.toLowerCase())));
                // End lowercase region
                /***********************/
                
                /***********************/
                // Start uppercase region
                replacedString =
                        replacedString.replace("${uppercase_" + nextMatchTag + "}", inputReplaceString.toUpperCase());
                replacedString =
                        replacedString.replace("${urlEncoded_uppercase_" + nextMatchTag + "}",
                                StringUtils.percentEncode(inputReplaceString.toUpperCase()));
                replacedString =
                        replacedString.replace("${xmlEncoded_uppercase_" + nextMatchTag + "}",
                                StringUtils.xmlEncodeString(inputReplaceString.toUpperCase()));
                replacedString =
                        replacedString.replace("${xmlEncoded_urlEncoded_uppercase_" + nextMatchTag + "}", StringUtils
                                .xmlEncodeString(StringUtils.percentEncode(inputReplaceString.toUpperCase())));
                replacedString =
                        replacedString
                                .replace("${xmlEncoded_ntriplesEncoded_uppercase_" + nextMatchTag + "}", StringUtils
                                        .xmlEncodeString(StringUtils.ntriplesEncode(inputReplaceString.toUpperCase())));
                // End uppercase region
                /***********************/
                
                if(QueryCreator._TRACE)
                {
                    QueryCreator.log.trace("QueryCreator.matchAndReplaceInputVariablesForQueryType: replacedString="
                            + replacedString);
                }
            } // end for(String nextMatch : allMatches.get(nextMatchTag)
        } // end for(String nextMatchTag : allMatches.keySet())
        
        final String queryString = queryParameters.get(Constants.QUERY);
        
        // lastly put in the actual query string if they want us to do that
        replacedString = replacedString.replace(Constants.TEMPLATE_QUERY_STRING, queryString);
        replacedString = replacedString.replace(Constants.TEMPLATE_LOWERCASE_QUERY_STRING, queryString.toLowerCase());
        replacedString = replacedString.replace(Constants.TEMPLATE_UPPERCASE_QUERY_STRING, queryString.toUpperCase());
        replacedString =
                replacedString.replace(Constants.TEMPLATE_NTRIPLES_ENCODED_QUERY_STRING,
                        StringUtils.ntriplesEncode(queryString));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_URL_ENCODED_QUERY_STRING,
                        StringUtils.percentEncode(queryString));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_QUERY_STRING,
                        StringUtils.xmlEncodeString(queryString));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_URL_ENCODED_QUERY_STRING,
                        StringUtils.xmlEncodeString(StringUtils.percentEncode(queryString)));
        replacedString =
                replacedString.replace(Constants.TEMPLATE_XML_ENCODED_NTRIPLES_ENCODED_QUERY_STRING,
                        StringUtils.xmlEncodeString(StringUtils.ntriplesEncode(queryString)));
        
        if(QueryCreator._DEBUG)
        {
            final long end = System.currentTimeMillis();
            
            QueryCreator.log.debug(String.format("%s: timing=%10d",
                    "QueryCreator.matchAndReplaceInputVariablesForQueryType", (end - start)));
            
            QueryCreator.log.debug("QueryCreator.matchAndReplaceInputVariablesForQueryType: returning replacedString="
                    + replacedString);
        }
        
        return replacedString;
    }
    
    /**
     * @param replacementString
     * @param queryType
     * @param nextNormalisationRules
     *            TODO
     * @param attributeList
     * @param includedProfiles
     * @param convertAlternateToPreferredPrefix
     *            TODO
     * @param namespaceInputVariables
     *            TODO
     * @return
     * @throws QueryAllException
     */
    public static String replaceAttributesOnEndpointUrl(final String replacementString, final QueryType queryType,
            final Provider nextProvider, final Map<String, String> attributeList, final List<Profile> includedProfiles,
            final boolean recogniseImplicitRdfRuleInclusions, final boolean includeNonProfileMatchedRdfRules,
            final boolean convertAlternateToPreferredPrefix, final QueryAllConfiguration localSettings,
            final Map<String, Collection<NamespaceEntry>> namespaceInputVariables) throws QueryAllException
    {
        final String queryString = attributeList.get(Constants.TEMPLATE_KEY_QUERY_STRING);
        
        if(queryString.trim().equals(""))
        {
            QueryCreator.log.error("QueryCreator.replaceAttributesOnEndpointUrl: queryString was empty");
        }
        
        if(replacementString == null)
        {
            QueryCreator.log.error("QueryCreator.replaceAttributesOnEndpointUrl: queryType="
                    + queryType.getKey().stringValue());
        }
        
        return QueryCreator.doReplacementsOnString(attributeList, replacementString, queryType, null, nextProvider,
                attributeList, namespaceInputVariables, includedProfiles, recogniseImplicitRdfRuleInclusions,
                includeNonProfileMatchedRdfRules, convertAlternateToPreferredPrefix, localSettings);
    }
    
    /**
     * @todo Implement me in order to make the replacements method more efficient
     * @param inputString
     * @param tags
     * @return
     */
    public static String replaceTags(String inputString, final Map<String, String> tags, final Pattern nextTagPattern)
    {
        final Matcher m = nextTagPattern.matcher(inputString);
        boolean result = m.find();
        
        if(result)
        {
            final StringBuffer sb = new StringBuffer();
            
            do
            {
                // log.warn("m.group(1)="+m.group(1));
                final String nextGroup = m.group(1);
                // log.warn("nextGroup="+nextGroup);
                
                if(tags.containsKey(nextGroup))
                {
                    // log.warn("tags.get(nextGroup)="+tags.get(nextGroup));
                    m.appendReplacement(sb, tags.get(nextGroup));
                }
                // else
                // {
                // log.warn("nextGroup not found in tags");
                // //m.appendReplacement(sb, nextGroup);
                //
                // }
                //
                result = m.find();
            }
            while(result);
            
            m.appendTail(sb);
            
            inputString = sb.toString();
        }
        
        return inputString;
    }
    
    public static String testReplaceMethod(final String inputString, final QueryAllConfiguration localSettings)
    {
        final Map<String, String> myTestMap = new TreeMap<String, String>();
        
        myTestMap.put("${input_1}", "MyInput1");
        myTestMap.put("${inputUrlEncoded_privatelowercase_input_1}", "myinput1");
        myTestMap.put("${input_2}", "YourInput2");
        myTestMap.put("${inputUrlEncoded_privatelowercase_input_2}", "yourinput2");
        
        final String returnString = QueryCreator.replaceTags(inputString, myTestMap, localSettings.getTagPattern());
        
        // log.warn("QueryCreator.testReplaceMethod returnString="+returnString);
        
        return returnString;
    }
    
}
