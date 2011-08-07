package org.queryall.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.queryall.api.NormalisationRule;
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.api.QueryAllConfiguration;
import org.queryall.api.QueryType;
import org.queryall.api.SparqlProvider;
import org.queryall.enumerations.Constants;
import org.queryall.enumerations.SortOrder;
import org.queryall.utils.RuleUtils;
import org.queryall.utils.StringUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryCreator
{
    private static final Logger log = Logger.getLogger(QueryCreator.class.getName());
    private static final boolean _TRACE = QueryCreator.log.isTraceEnabled();
    private static final boolean _DEBUG = QueryCreator.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = QueryCreator.log.isInfoEnabled();
    
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
     * @return
     */
    public static String createQuery(final QueryType queryType, final Provider nextProvider,
            final Map<String, String> attributeList, final List<Profile> includedProfiles,
            final boolean recogniseImplicitRdfRuleInclusions, final boolean includeNonProfileMatchedRdfRules,
            final QueryAllConfiguration localSettings)
    {
        final String queryString = attributeList.get(Constants.TEMPLATE_KEY_QUERY_STRING);
        
        if(queryString.trim().equals(""))
        {
            QueryCreator.log.error("QueryCreator.createQuery: queryString was empty");
        }
        
        if(queryType.getTemplateString() == null)
        {
            QueryCreator.log.error("QueryCreator.createQuery: template was null queryType.getKey()="
                    + queryType.getKey().stringValue());
        }
        
        return QueryCreator.doReplacementsOnString(queryString, queryType.getTemplateString(), queryType, null,
                nextProvider.getNormalisationUris(), attributeList, includedProfiles,
                recogniseImplicitRdfRuleInclusions, includeNonProfileMatchedRdfRules, localSettings);
    }
    
    /**
     * @param originalQueryType
     * @param includedQueryType
     * @param nextProvider
     * @param attributeList
     * @param includedProfiles
     * @return
     */
    public static String createStaticRdfXmlString(final QueryType originalQueryType, final QueryType includedQueryType,
            final Provider nextProvider, final Map<String, String> attributeList, final List<Profile> includedProfiles,
            final boolean recogniseImplicitRdfRuleInclusions, final boolean includeNonProfileMatchedRdfRules,
            final QueryAllConfiguration localSettings)
    {
        final String queryString = attributeList.get(Constants.TEMPLATE_KEY_QUERY_STRING);
        
        if(queryString.trim().equals(""))
        {
            QueryCreator.log.error("QueryCreator.createQuery: queryString was empty");
        }
        
        if(includedQueryType.getOutputRdfXmlString() == null)
        {
            QueryCreator.log.error("QueryCreator.createQuery: no outputRdfXmlString defined queryType="
                    + includedQueryType.getKey().stringValue());
            
            return "";
        }
        
        return QueryCreator.doReplacementsOnString(queryString, includedQueryType.getOutputRdfXmlString(),
                originalQueryType, includedQueryType, nextProvider.getNormalisationUris(), attributeList,
                includedProfiles, recogniseImplicitRdfRuleInclusions, includeNonProfileMatchedRdfRules, localSettings);
    }
    
    public static String doReplacementsOnString(final String queryString, final String templateString,
            final QueryType originalQueryType, final QueryType includedQueryType,
            final Collection<URI> normalisationUrisNeeded, final Map<String, String> attributeList,
            final List<Profile> includedProfiles, final boolean recogniseImplicitRdfRuleInclusions,
            final boolean includeNonProfileMatchedRdfRules, final QueryAllConfiguration localSettings)
    {
        if(QueryCreator._DEBUG)
        {
            QueryCreator.log.debug("QueryCreator.doReplacementsOnString: queryString=" + queryString
                    + " templateString=" + templateString + " normalisationUrisNeeded=" + normalisationUrisNeeded);
        }
        
        // FIXME: move tag pattern to another class to avoid the cast here
        if(!((Settings)localSettings).getTagPattern().matcher(templateString).matches())
        {
            if(QueryCreator._DEBUG)
            {
                QueryCreator.log.debug("tag pattern " + ((Settings)localSettings).getTagPattern().toString()
                        + " does not match template string=" + templateString);
                QueryCreator.log.debug("returning templateString unchanged");
            }
            
            return templateString;
        }
        
        final long start = System.currentTimeMillis();
        
        String replacedString = templateString;
        
        String normalisedStandardUri = originalQueryType.getStandardUriTemplateString();
        
        // StringBuilder replacedString = new StringBuilder(templateString);
        //
        // StringBuilder normalisedStandardUri = new
        // StringBuilder(originalQueryType.getStandardUriTemplateString());
        
        // final String normalisedOntologyUriPrefix = Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX;
        //
        // final String normalisedOntologyUriSuffix = Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX;
        
        String normalisedQueryUri = "";
        
        // TODO: include allowance for the input_NN variables which match
        // namespaces here using originalQueryType.publicIdentifierIndexes
        // and/or originalQueryType.namespaceInputIndexes??
        // NOTE: CONVENTION: We assume that both of the template strings will
        // contain the same ${input_N} values as they really represent the
        // identifiers
        // If queries can't keep to this convention they shouldn't be included
        // together and should be performed and created separately
        
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
        
        if(QueryCreator._DEBUG)
        {
            QueryCreator.log.debug("QueryCreator.createQuery: initial value of replacedString=" + replacedString);
            QueryCreator.log.debug("QueryCreator.createQuery: initial value of normalisedStandardUri="
                    + normalisedStandardUri);
            QueryCreator.log.debug("QueryCreator.createQuery: initial value of normalisedQueryUri="
                    + normalisedQueryUri);
        }
        
        if(includedQueryType != null && includedQueryType.getKey() != null)
        {
            replacedString =
                    replacedString.replace(Constants.TEMPLATE_XML_ENCODED_INCLUDED_QUERY_TYPE,
                            StringUtils.xmlEncodeString(includedQueryType.getKey().stringValue()));
        }
        
        replacedString =
                replacedString.replace(Constants.TEMPLATE_LIMIT,
                        "LIMIT " + localSettings.getIntProperty("pageoffsetIndividualQueryLimit", 500));
        
        normalisedQueryUri =
                normalisedQueryUri.replace(Constants.TEMPLATE_LIMIT,
                        "limit/" + localSettings.getIntProperty("pageoffsetIndividualQueryLimit", 500));
        
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
                        (pageOffset - 1) * localSettings.getIntProperty("pageoffsetIndividualQueryLimit", 500);
                
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
        
        final List<String> inputUrlEncodeInstructions = new ArrayList<String>(1);
        inputUrlEncodeInstructions.add(Constants.INPUT_URL_ENCODED);
        
        final List<String> inputPlusUrlEncodeInstructions = new ArrayList<String>(1);
        inputPlusUrlEncodeInstructions.add(Constants.INPUT_PLUS_URL_ENCODED);
        
        final List<String> inputXmlEncodeInstructions = new ArrayList<String>(1);
        inputXmlEncodeInstructions.add(Constants.INPUT_XML_ENCODED);
        
        final List<String> inputNTriplesEncodeInstructions = new ArrayList<String>(1);
        inputNTriplesEncodeInstructions.add(Constants.INPUT_NTRIPLES_ENCODED);
        
        final List<String> xmlEncodeInstructions = new ArrayList<String>(1);
        xmlEncodeInstructions.add(Constants.XML_ENCODED);
        
        final List<String> urlEncodeInstructions = new ArrayList<String>(1);
        urlEncodeInstructions.add(Constants.URL_ENCODED);
        
        final List<String> plusUrlEncodeInstructions = new ArrayList<String>(1);
        plusUrlEncodeInstructions.add(Constants.PLUS_URL_ENCODED);
        
        final List<String> ntriplesEncodeInstructions = new ArrayList<String>(1);
        ntriplesEncodeInstructions.add(Constants.NTRIPLES_ENCODED);
        
        final List<String> lowercaseInstructions = new ArrayList<String>(1);
        lowercaseInstructions.add(Constants.LOWERCASE);
        
        // NOTE: make sure that LOWERCASE is added before INPUT_URL_ENCODED as
        // we always want uppercase URL encoded %FF character patterns to match
        // against the database
        final List<String> inputUrlEncodedlowercaseInstructions = new ArrayList<String>(1);
        inputUrlEncodedlowercaseInstructions.add(Constants.LOWERCASE);
        inputUrlEncodedlowercaseInstructions.add(Constants.INPUT_URL_ENCODED);
        
        final List<String> inputXmlEncodedlowercaseInstructions = new ArrayList<String>(1);
        inputXmlEncodedlowercaseInstructions.add(Constants.LOWERCASE);
        inputXmlEncodedlowercaseInstructions.add(Constants.INPUT_XML_ENCODED);
        
        final List<String> inputUrlEncodedprivatelowercaseInstructions = new ArrayList<String>(1);
        inputUrlEncodedprivatelowercaseInstructions.add(Constants.PRIVATE_LOWERCASE);
        inputUrlEncodedprivatelowercaseInstructions.add(Constants.INPUT_URL_ENCODED);
        
        final List<String> inputXmlEncodedprivatelowercaseInstructions = new ArrayList<String>(1);
        inputXmlEncodedprivatelowercaseInstructions.add(Constants.PRIVATE_LOWERCASE);
        inputXmlEncodedprivatelowercaseInstructions.add(Constants.INPUT_XML_ENCODED);
        
        final List<String> uppercaseInstructions = new ArrayList<String>(1);
        uppercaseInstructions.add(Constants.UPPERCASE);
        
        final List<String> inputUrlEncodeduppercaseInstructions = new ArrayList<String>(2);
        inputUrlEncodeduppercaseInstructions.add(Constants.UPPERCASE);
        inputUrlEncodeduppercaseInstructions.add(Constants.INPUT_URL_ENCODED);
        
        final List<String> inputXmlEncodeduppercaseInstructions = new ArrayList<String>(2);
        inputXmlEncodeduppercaseInstructions.add(Constants.UPPERCASE);
        inputXmlEncodeduppercaseInstructions.add(Constants.INPUT_XML_ENCODED);
        
        final List<String> inputUrlEncodedprivateuppercaseInstructions = new ArrayList<String>(2);
        inputUrlEncodedprivateuppercaseInstructions.add(Constants.PRIVATE_UPPERCASE);
        inputUrlEncodedprivateuppercaseInstructions.add(Constants.INPUT_URL_ENCODED);
        
        final List<String> inputXmlEncodedprivateuppercaseInstructions = new ArrayList<String>(2);
        inputXmlEncodedprivateuppercaseInstructions.add(Constants.PRIVATE_UPPERCASE);
        inputXmlEncodedprivateuppercaseInstructions.add(Constants.INPUT_XML_ENCODED);
        
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
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString, replacedString,
                        new ArrayList<String>());
        
        normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        normalisedStandardUri, new ArrayList<String>());
        
        inputUrlEncoded_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputUrlEncoded_normalisedStandardUri, inputUrlEncodeInstructions);
        
        inputXmlEncoded_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputXmlEncoded_normalisedStandardUri, inputXmlEncodeInstructions);
        
        normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        normalisedQueryUri, new ArrayList<String>());
        
        inputUrlEncoded_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputUrlEncoded_normalisedQueryUri, inputUrlEncodeInstructions);
        
        inputPlusUrlEncoded_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputPlusUrlEncoded_normalisedStandardUri, inputPlusUrlEncodeInstructions);
        
        inputPlusUrlEncoded_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputPlusUrlEncoded_normalisedQueryUri, inputPlusUrlEncodeInstructions);
        
        inputXmlEncoded_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputXmlEncoded_normalisedQueryUri, inputXmlEncodeInstructions);
        
        inputUrlEncoded_lowercase_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputUrlEncoded_lowercase_normalisedStandardUri, inputUrlEncodedlowercaseInstructions);
        inputUrlEncoded_lowercase_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputUrlEncoded_lowercase_normalisedQueryUri, inputUrlEncodedlowercaseInstructions);
        
        inputXmlEncoded_lowercase_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputXmlEncoded_lowercase_normalisedStandardUri, inputXmlEncodedlowercaseInstructions);
        inputXmlEncoded_lowercase_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputXmlEncoded_lowercase_normalisedQueryUri, inputXmlEncodedlowercaseInstructions);
        
        inputUrlEncoded_uppercase_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputUrlEncoded_uppercase_normalisedStandardUri, inputUrlEncodeduppercaseInstructions);
        inputUrlEncoded_uppercase_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputUrlEncoded_uppercase_normalisedQueryUri, inputUrlEncodeduppercaseInstructions);
        
        inputXmlEncoded_uppercase_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputXmlEncoded_uppercase_normalisedStandardUri, inputXmlEncodeduppercaseInstructions);
        inputXmlEncoded_uppercase_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputXmlEncoded_uppercase_normalisedQueryUri, inputXmlEncodeduppercaseInstructions);
        
        inputUrlEncoded_privatelowercase_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputUrlEncoded_privatelowercase_normalisedStandardUri,
                        inputUrlEncodedprivatelowercaseInstructions);
        inputUrlEncoded_privatelowercase_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputUrlEncoded_privatelowercase_normalisedQueryUri,
                        inputUrlEncodedprivatelowercaseInstructions);
        
        inputXmlEncoded_privatelowercase_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputXmlEncoded_privatelowercase_normalisedStandardUri,
                        inputXmlEncodedprivatelowercaseInstructions);
        inputXmlEncoded_privatelowercase_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputXmlEncoded_privatelowercase_normalisedQueryUri,
                        inputXmlEncodedprivatelowercaseInstructions);
        
        inputUrlEncoded_privateuppercase_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputUrlEncoded_privateuppercase_normalisedStandardUri,
                        inputUrlEncodedprivateuppercaseInstructions);
        inputUrlEncoded_privateuppercase_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputUrlEncoded_privateuppercase_normalisedQueryUri,
                        inputUrlEncodedprivateuppercaseInstructions);
        
        inputXmlEncoded_privateuppercase_normalisedStandardUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputXmlEncoded_privateuppercase_normalisedStandardUri,
                        inputXmlEncodedprivateuppercaseInstructions);
        inputXmlEncoded_privateuppercase_normalisedQueryUri =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(originalQueryType, queryString,
                        inputXmlEncoded_privateuppercase_normalisedQueryUri,
                        inputXmlEncodedprivateuppercaseInstructions);
        
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
        
        final Collection<NormalisationRule> normalisationsNeeded =
                RuleUtils.getSortedRulesByUris(localSettings.getAllNormalisationRules(), normalisationUrisNeeded,
                        SortOrder.LOWEST_ORDER_FIRST);
        
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
        
        for(final NormalisationRule nextRule : normalisationsNeeded)
        {
            if(nextRule.isUsedWithProfileList(includedProfiles, recogniseImplicitRdfRuleInclusions,
                    includeNonProfileMatchedRdfRules))
            {
                endpointSpecificUri = (String)nextRule.stageQueryVariables(endpointSpecificUri);
                endpointSpecificQueryUri = (String)nextRule.stageQueryVariables(endpointSpecificQueryUri);
                // endpointSpecificOntologyUri = (String)nextRule
                // .applyInputRegex(endpointSpecificOntologyUri);
                
                inputUrlEncoded_endpointSpecificUri =
                        (String)nextRule.stageQueryVariables(inputUrlEncoded_endpointSpecificUri);
                inputPlusUrlEncoded_endpointSpecificUri =
                        (String)nextRule.stageQueryVariables(inputPlusUrlEncoded_endpointSpecificUri);
                inputXmlEncoded_endpointSpecificUri =
                        (String)nextRule.stageQueryVariables(inputXmlEncoded_endpointSpecificUri);
                inputUrlEncoded_endpointSpecificQueryUri =
                        (String)nextRule.stageQueryVariables(inputUrlEncoded_endpointSpecificQueryUri);
                inputPlusUrlEncoded_endpointSpecificQueryUri =
                        (String)nextRule.stageQueryVariables(inputPlusUrlEncoded_endpointSpecificQueryUri);
                inputXmlEncoded_endpointSpecificQueryUri =
                        (String)nextRule.stageQueryVariables(inputXmlEncoded_endpointSpecificQueryUri);
                
                inputUrlEncoded_lowercase_endpointSpecificUri =
                        (String)nextRule.stageQueryVariables(inputUrlEncoded_lowercase_endpointSpecificUri);
                inputXmlEncoded_lowercase_endpointSpecificUri =
                        (String)nextRule.stageQueryVariables(inputXmlEncoded_lowercase_endpointSpecificUri);
                inputUrlEncoded_uppercase_endpointSpecificUri =
                        (String)nextRule.stageQueryVariables(inputUrlEncoded_uppercase_endpointSpecificUri);
                inputXmlEncoded_uppercase_endpointSpecificUri =
                        (String)nextRule.stageQueryVariables(inputXmlEncoded_uppercase_endpointSpecificUri);
                
                inputUrlEncoded_lowercase_endpointSpecificQueryUri =
                        (String)nextRule.stageQueryVariables(inputUrlEncoded_lowercase_endpointSpecificQueryUri);
                inputXmlEncoded_lowercase_endpointSpecificQueryUri =
                        (String)nextRule.stageQueryVariables(inputXmlEncoded_lowercase_endpointSpecificQueryUri);
                inputUrlEncoded_uppercase_endpointSpecificQueryUri =
                        (String)nextRule.stageQueryVariables(inputUrlEncoded_uppercase_endpointSpecificQueryUri);
                inputXmlEncoded_uppercase_endpointSpecificQueryUri =
                        (String)nextRule.stageQueryVariables(inputXmlEncoded_uppercase_endpointSpecificQueryUri);
                
                inputUrlEncoded_privatelowercase_endpointSpecificUri =
                        (String)nextRule.stageQueryVariables(inputUrlEncoded_privatelowercase_endpointSpecificUri);
                inputXmlEncoded_privatelowercase_endpointSpecificUri =
                        (String)nextRule.stageQueryVariables(inputXmlEncoded_privatelowercase_endpointSpecificUri);
                inputUrlEncoded_privateuppercase_endpointSpecificUri =
                        (String)nextRule.stageQueryVariables(inputUrlEncoded_privateuppercase_endpointSpecificUri);
                inputXmlEncoded_privateuppercase_endpointSpecificUri =
                        (String)nextRule.stageQueryVariables(inputXmlEncoded_privateuppercase_endpointSpecificUri);
                
                inputUrlEncoded_privatelowercase_endpointSpecificQueryUri =
                        (String)nextRule.stageQueryVariables(inputUrlEncoded_privatelowercase_endpointSpecificQueryUri);
                inputXmlEncoded_privatelowercase_endpointSpecificQueryUri =
                        (String)nextRule.stageQueryVariables(inputXmlEncoded_privatelowercase_endpointSpecificQueryUri);
                inputUrlEncoded_privateuppercase_endpointSpecificQueryUri =
                        (String)nextRule.stageQueryVariables(inputUrlEncoded_privateuppercase_endpointSpecificQueryUri);
                inputXmlEncoded_privateuppercase_endpointSpecificQueryUri =
                        (String)nextRule.stageQueryVariables(inputXmlEncoded_privateuppercase_endpointSpecificQueryUri);
                
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
            
            QueryCreator.log.trace("QueryCreator.doReplacementsOnString: returning replacedString=" + replacedString);
        }
        
        return replacedString;
    }
    
    /**
     * @param nextQueryType
     * @param nextProvider
     * @param queryString
     * @param nextEndpoint
     * @param realHostName
     * @param pageOffset
     * @return
     */
    public static Map<String, String> getAttributeListFor(final QueryType nextIncludedQueryType,
            final Provider nextProvider, final String queryString, final String nextEndpoint,
            final String realHostName, final int pageOffset, final QueryAllConfiguration localSettings)
    {
        if(QueryCreator._DEBUG)
        {
            QueryCreator.log.debug("QueryCreator.getAttributeListFor: called with nextProvider="
                    + nextProvider.toString() + " queryString=" + queryString + " nextEndpoint=" + nextEndpoint
                    + " realHostName=" + realHostName + " pageOffset=" + pageOffset);
        }
        
        final Map<String, String> attributeList = new Hashtable<String, String>();
        
        if(nextIncludedQueryType != null && nextIncludedQueryType.getKey() != null)
        {
            attributeList.put(Constants.TEMPLATE_KEY_INCLUDED_QUERY_TYPE, nextIncludedQueryType.getKey().stringValue());
        }
        
        attributeList.put(Constants.TEMPLATE_KEY_DEFAULT_HOST_NAME, localSettings.getStringProperty("hostName", ""));
        // TODO: avoid cast here
        attributeList.put(Constants.TEMPLATE_KEY_DEFAULT_HOST_ADDRESS,
                ((Settings)localSettings).getDefaultHostAddress());
        attributeList.put(Constants.TEMPLATE_KEY_DEFAULT_SEPARATOR, localSettings.getStringProperty("separator", ""));
        attributeList.put(Constants.TEMPLATE_KEY_REAL_HOST_NAME, realHostName);
        attributeList.put(Constants.TEMPLATE_KEY_QUERY_STRING, queryString);
        if(nextProvider instanceof SparqlProvider)
        {
            final SparqlProvider nextSparqlProvider = (SparqlProvider)nextProvider;
            
            attributeList.put(Constants.TEMPLATE_KEY_GRAPH_URI, nextSparqlProvider.getSparqlGraphUri());
            attributeList.put(Constants.TEMPLATE_KEY_USE_SPARQL_GRAPH, nextSparqlProvider.getUseSparqlGraph() + "");
            attributeList.put(Constants.TEMPLATE_KEY_URL_ENCODED_GRAPH_URI,
                    StringUtils.percentEncode(nextSparqlProvider.getSparqlGraphUri()));
            attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_GRAPH_URI,
                    StringUtils.xmlEncodeString(nextSparqlProvider.getSparqlGraphUri()));
            attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_GRAPH_URI,
                    StringUtils.xmlEncodeString(StringUtils.percentEncode(nextSparqlProvider.getSparqlGraphUri())));
        }
        
        attributeList.put(Constants.TEMPLATE_KEY_OFFSET, pageOffset + "");
        
        // if(nextProvider.rdfNormalisationsNeeded != null)
        // {
        // attributeList.put("rdfNormalisationsNeeded",RdfUtils.joinStringCollection(nextProvider.rdfNormalisationsNeeded,","));
        // }
        // else
        // {
        // attributeList.put("rdfNormalisationsNeeded","");
        // }
        
        attributeList.put(Constants.TEMPLATE_KEY_ENDPOINT_URL, nextEndpoint);
        
        attributeList.put(Constants.TEMPLATE_KEY_URL_ENCODED_DEFAULT_HOST_NAME,
                StringUtils.percentEncode(localSettings.getStringProperty("hostName", "")));
        // TODO: avoid cast here
        attributeList.put(Constants.TEMPLATE_KEY_URL_ENCODED_DEFAULT_HOST_ADDRESS,
                StringUtils.percentEncode(((Settings)localSettings).getDefaultHostAddress()));
        attributeList.put(Constants.TEMPLATE_KEY_URL_ENCODED_DEFAULT_SEPARATOR,
                StringUtils.percentEncode(localSettings.getStringProperty("separator", "")));
        attributeList.put(Constants.TEMPLATE_KEY_URL_ENCODED_ENDPOINT_URL, StringUtils.percentEncode(nextEndpoint));
        attributeList.put(Constants.TEMPLATE_KEY_URL_ENCODED_REAL_HOST_NAME, StringUtils.percentEncode(realHostName));
        attributeList.put(Constants.TEMPLATE_KEY_URL_ENCODED_QUERY_STRING, StringUtils.percentEncode(queryString));
        
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_DEFAULT_HOST_NAME,
                StringUtils.xmlEncodeString(localSettings.getStringProperty("hostName", "")));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_DEFAULT_HOST_ADDRESS,
                StringUtils.xmlEncodeString("http://" + localSettings.getStringProperty("hostName", "") + "/"));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_DEFAULT_SEPARATOR,
                StringUtils.xmlEncodeString(localSettings.getStringProperty("separator", ":")));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_ENDPOINT_URL, StringUtils.xmlEncodeString(nextEndpoint));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_REAL_HOST_NAME, StringUtils.xmlEncodeString(realHostName));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_QUERY_STRING, StringUtils.xmlEncodeString(queryString));
        
        attributeList
                .put(Constants.TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_DEFAULT_HOST_NAME, StringUtils
                        .xmlEncodeString(StringUtils.percentEncode(localSettings.getStringProperty("hostName", ""))));
        attributeList.put(
                Constants.TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_DEFAULT_HOST_ADDRESS,
                StringUtils.xmlEncodeString(StringUtils.percentEncode("http://"
                        + localSettings.getStringProperty("hostName", "") + "/")));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_DEFAULT_SEPARATOR, StringUtils
                .xmlEncodeString(StringUtils.percentEncode(localSettings.getStringProperty("separator", ":"))));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_ENDPOINT_URL,
                StringUtils.xmlEncodeString(StringUtils.percentEncode(nextEndpoint)));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_REAL_HOST_NAME,
                StringUtils.xmlEncodeString(StringUtils.percentEncode(realHostName)));
        attributeList.put(Constants.TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_QUERY_STRING,
                StringUtils.xmlEncodeString(StringUtils.percentEncode(queryString)));
        
        return attributeList;
    }
    
    /**
     * @param originalQueryType
     * @param queryString
     * @param templateString
     * @param specialInstructions
     * @return
     */
    public static String matchAndReplaceInputVariablesForQueryType(final QueryType originalQueryType,
            final String queryString, final String templateString, final List<String> specialInstructions)
    {
        if(QueryCreator._DEBUG)
        {
            QueryCreator.log.debug("QueryCreator.matchAndReplaceInputVariablesForQueryType: queryString=" + queryString
                    + " templateString=" + templateString);
        }
        
        final long start = System.currentTimeMillis();
        
        String replacedString = templateString;
        
        final List<String> allMatches = originalQueryType.matchesForQueryString(queryString);
        
        int nextMatch = 0;
        
        for(; nextMatch < allMatches.size(); nextMatch++)
        {
            final int number = nextMatch + 1;
            
            final boolean isPublic = originalQueryType.isInputVariablePublic(number);
            
            String inputReplaceString = allMatches.get(nextMatch);
            
            final String matchString = "${input_" + number + "}";
            
            final String urlEncodedReplaceString = StringUtils.percentEncode(inputReplaceString);
            final String urlEncodedMatchString = "${urlEncoded_input_" + number + "}";
            
            final String plusUrlEncodedReplaceString = StringUtils.plusPercentEncode(inputReplaceString);
            final String plusUrlEncodedMatchString = "${plusUrlEncoded_input_" + number + "}";
            
            // final String plusSpaceEncodedReplaceString =
            // RdfUtils.plusSpaceEncode(inputReplaceString);
            // final String plusSpaceEncodedMatchString = "${plusSpaceEncoded_input_" + number +
            // "}";
            
            final String xmlEncodedReplaceString = StringUtils.xmlEncodeString(inputReplaceString);
            final String xmlEncodedMatchString = "${xmlEncoded_input_" + number + "}";
            
            final String xmlEncodedUrlEncodedReplaceString =
                    StringUtils.xmlEncodeString(StringUtils.percentEncode(inputReplaceString));
            
            final String xmlEncodedUrlEncodedMatchString = "${xmlEncoded_urlEncoded_input_" + number + "}";
            
            final String xmlEncodedPlusUrlEncodedReplaceString =
                    StringUtils.xmlEncodeString(StringUtils.plusPercentEncode(inputReplaceString));
            
            final String xmlEncodedPlusUrlEncodedMatchString = "${xmlEncoded_plusUrlEncoded_input_" + number + "}";
            
            final String xmlEncodedNTriplesReplaceString =
                    StringUtils.xmlEncodeString(StringUtils.ntriplesEncode(inputReplaceString));
            final String xmlEncodedNTriplesMatchString = "${xmlEncoded_ntriplesEncoded_input_" + number + "}";
            
            final String nTriplesReplaceString = StringUtils.ntriplesEncode(inputReplaceString);
            final String nTriplesMatchString = "${ntriplesEncoded_input_" + number + "}";
            
            // Start lowercase region
            /***********************/
            
            final String lowercaseReplaceString = inputReplaceString.toLowerCase();
            final String lowercaseMatchString = "${lowercase_input_" + number + "}";
            
            final String urlEncodedLowercaseReplaceString = StringUtils.percentEncode(inputReplaceString.toLowerCase());
            final String urlEncodedLowercaseMatchString = "${urlEncoded_lowercase_input_" + number + "}";
            
            final String xmlEncodedLowercaseReplaceString =
                    StringUtils.xmlEncodeString(inputReplaceString.toLowerCase());
            final String xmlEncodedLowercaseMatchString = "${xmlEncoded_lowercase_input_" + number + "}";
            
            final String xmlEncodedUrlEncodedLowercaseReplaceString =
                    StringUtils.xmlEncodeString(StringUtils.percentEncode(inputReplaceString.toLowerCase()));
            final String xmlEncodedUrlEncodedLowercaseMatchString =
                    "${xmlEncoded_urlEncoded_lowercase_input_" + number + "}";
            
            final String xmlEncodedNTriplesLowercaseReplaceString =
                    StringUtils.xmlEncodeString(StringUtils.ntriplesEncode(inputReplaceString.toLowerCase()));
            final String xmlEncodedNTriplesLowercaseMatchString =
                    "${xmlEncoded_ntriplesEncoded_lowercase_input_" + number + "}";
            
            // End lowercase region
            /***********************/
            // Start uppercase region
            final String uppercaseReplaceString = inputReplaceString.toUpperCase();
            final String uppercaseMatchString = "${uppercase_input_" + number + "}";
            
            final String urlEncodedUppercaseReplaceString = StringUtils.percentEncode(inputReplaceString.toUpperCase());
            final String urlEncodedUppercaseMatchString = "${urlEncoded_uppercase_input_" + number + "}";
            
            final String xmlEncodedUppercaseReplaceString =
                    StringUtils.xmlEncodeString(inputReplaceString.toUpperCase());
            final String xmlEncodedUppercaseMatchString = "${xmlEncoded_uppercase_input_" + number + "}";
            
            final String xmlEncodedUrlEncodedUppercaseReplaceString =
                    StringUtils.xmlEncodeString(StringUtils.percentEncode(inputReplaceString.toUpperCase()));
            final String xmlEncodedUrlEncodedUppercaseMatchString =
                    "${xmlEncoded_urlEncoded_uppercase_input_" + number + "}";
            
            final String xmlEncodedNTriplesUppercaseReplaceString =
                    StringUtils.xmlEncodeString(StringUtils.ntriplesEncode(inputReplaceString.toUpperCase()));
            final String xmlEncodedNTriplesUppercaseMatchString =
                    "${xmlEncoded_ntriplesEncoded_uppercase_input_" + number + "}";
            
            // End uppercase region
            /***********************/
            
            if(QueryCreator._TRACE)
            {
                QueryCreator.log.trace("QueryCreator.matchAndReplaceInputVariablesForQueryType: number=" + number);
                QueryCreator.log.trace("QueryCreator.matchAndReplaceInputVariablesForQueryType: input_" + number + "="
                        + inputReplaceString);
                QueryCreator.log.trace("QueryCreator.matchAndReplaceInputVariablesForQueryType: matchString="
                        + matchString);
                QueryCreator.log.trace("QueryCreator.matchAndReplaceInputVariablesForQueryType: indexOf matchString="
                        + replacedString.indexOf(matchString));
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
            
            replacedString = replacedString.replace(matchString, inputReplaceString);
            
            replacedString = replacedString.replace(xmlEncodedMatchString, xmlEncodedReplaceString);
            
            replacedString = replacedString.replace(urlEncodedMatchString, urlEncodedReplaceString);
            
            replacedString = replacedString.replace(plusUrlEncodedMatchString, plusUrlEncodedReplaceString);
            
            // replacedString = replacedString.replace(plusSpaceEncodedMatchString,
            // plusSpaceEncodedReplaceString);
            
            replacedString = replacedString.replace(nTriplesMatchString, nTriplesReplaceString);
            
            replacedString = replacedString.replace(xmlEncodedUrlEncodedMatchString, xmlEncodedUrlEncodedReplaceString);
            replacedString =
                    replacedString.replace(xmlEncodedPlusUrlEncodedMatchString, xmlEncodedPlusUrlEncodedReplaceString);
            
            replacedString = replacedString.replace(xmlEncodedNTriplesMatchString, xmlEncodedNTriplesReplaceString);
            
            /***********************/
            // Start lowercase region
            replacedString = replacedString.replace(lowercaseMatchString, lowercaseReplaceString);
            replacedString = replacedString.replace(urlEncodedLowercaseMatchString, urlEncodedLowercaseReplaceString);
            replacedString = replacedString.replace(xmlEncodedLowercaseMatchString, xmlEncodedLowercaseReplaceString);
            replacedString =
                    replacedString.replace(xmlEncodedUrlEncodedLowercaseMatchString,
                            xmlEncodedUrlEncodedLowercaseReplaceString);
            replacedString =
                    replacedString.replace(xmlEncodedNTriplesLowercaseMatchString,
                            xmlEncodedNTriplesLowercaseReplaceString);
            // End lowercase region
            /***********************/
            
            /***********************/
            // Start uppercase region
            replacedString = replacedString.replace(uppercaseMatchString, uppercaseReplaceString);
            replacedString = replacedString.replace(urlEncodedUppercaseMatchString, urlEncodedUppercaseReplaceString);
            replacedString = replacedString.replace(xmlEncodedUppercaseMatchString, xmlEncodedUppercaseReplaceString);
            replacedString =
                    replacedString.replace(xmlEncodedUrlEncodedUppercaseMatchString,
                            xmlEncodedUrlEncodedUppercaseReplaceString);
            replacedString =
                    replacedString.replace(xmlEncodedNTriplesUppercaseMatchString,
                            xmlEncodedNTriplesUppercaseReplaceString);
            // End uppercase region
            /***********************/
            
            if(QueryCreator._DEBUG)
            {
                QueryCreator.log.debug("QueryCreator.matchAndReplaceInputVariablesForQueryType: replacedString="
                        + replacedString);
                // log.debug("QueryCreator.matchAndReplaceInputVariablesForQueryType: normalisedStandardUri="+normalisedStandardUri);
                // log.debug("QueryCreator.matchAndReplaceInputVariablesForQueryType: normalisedQueryUri="+normalisedQueryUri);
            }
        } // end for(;nextMatch < allMatches.size(); nextMatch++)
        
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
     * @param normalisationRules
     *            An ordered list of normalisation rules that need to be applied to the input
     *            document
     * @param includedProfiles
     * @param recogniseImplicitRdfRuleInclusions
     * @param includeNonProfileMatchedRdfRules
     * @param basicRdfXml
     * @return
     */
    public static Object normaliseByStage(final URI stage, Object input,
            final List<NormalisationRule> normalisationRules, final List<Profile> includedProfiles,
            final boolean recogniseImplicitRdfRuleInclusions, final boolean includeNonProfileMatchedRdfRules)
    {
        if(QueryCreator._TRACE)
        {
            QueryCreator.log.trace("QueryCreator.normaliseByStage: before applying normalisation rules");
        }
        
        final long start = System.currentTimeMillis();
        
        // go through the rules
        for(final NormalisationRule nextRule : normalisationRules)
        {
            if(nextRule.isUsedWithProfileList(includedProfiles, recogniseImplicitRdfRuleInclusions,
                    includeNonProfileMatchedRdfRules))
            {
                if(QueryCreator._TRACE)
                {
                    QueryCreator.log.trace("QueryCreator.normaliseByStage: nextRule.order=" + nextRule.getOrder());
                }
                
                input = nextRule.normaliseByStage(stage, input);
            }
        }
        
        if(QueryCreator._DEBUG)
        {
            final long end = System.currentTimeMillis();
            
            QueryCreator.log.debug(String.format("%s: timing=%10d", "QueryCreator.normaliseByStage", (end - start)));
        }
        
        if(QueryCreator._TRACE)
        {
            QueryCreator.log.trace("QueryCreator.normaliseByStage: after applying normalisation rules");
        }
        
        return input;
    }
    
    /**
     * @param replacementString
     * @param queryType
     * @param nextProvider
     * @param attributeList
     * @param includedProfiles
     * @return
     */
    public static String replaceAttributesOnEndpointUrl(final String replacementString, final QueryType queryType,
            final Provider nextProvider, final Map<String, String> attributeList, final List<Profile> includedProfiles,
            final boolean recogniseImplicitRdfRuleInclusions, final boolean includeNonProfileMatchedRdfRules,
            final QueryAllConfiguration localSettings)
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
        
        return QueryCreator.doReplacementsOnString(queryString, replacementString, queryType, null,
                nextProvider.getNormalisationUris(), attributeList, includedProfiles,
                recogniseImplicitRdfRuleInclusions, includeNonProfileMatchedRdfRules, localSettings);
    }
    
    /**
     * @todo Implement me in order to make the replacements method more efficient
     * @param inputString
     * @param tags
     * @return
     */
    public static String replaceTags(String inputString, final Map<String, String> tags,
            final QueryAllConfiguration localSettings)
    {
        final Matcher m = localSettings.getTagPattern().matcher(inputString);
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
    
    public static String testReplaceMethod(final String inputString)
    {
        final Map<String, String> myTestHashtable = new TreeMap<String, String>();
        
        myTestHashtable.put("${input_1}", "MyInput1");
        myTestHashtable.put("${inputUrlEncoded_privatelowercase_input_1}", "myinput1");
        myTestHashtable.put("${input_2}", "YourInput2");
        myTestHashtable.put("${inputUrlEncoded_privatelowercase_input_2}", "yourinput2");
        
        final String returnString = QueryCreator.replaceTags(inputString, myTestHashtable, Settings.getSettings());
        
        // log.warn("QueryCreator.testReplaceMethod returnString="+returnString);
        
        return returnString;
    }
    
}
