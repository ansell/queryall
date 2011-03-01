
package org.queryall.helpers;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

import org.openrdf.model.URI;

import org.queryall.*;

import org.apache.log4j.Logger;

public class SparqlQueryCreator
{
    private static final Logger log = Logger.getLogger(SparqlQueryCreator.class
            .getName());
    private static final boolean _TRACE = SparqlQueryCreator.log
            .isTraceEnabled();
    private static final boolean _DEBUG = SparqlQueryCreator.log
            .isDebugEnabled();
    private static final boolean _INFO = SparqlQueryCreator.log.isInfoEnabled();
    
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
    // Settings.getIntPropertyFromConfig("pageoffsetIndividualQueryLimit"), where the limit is an integer number
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
    public static String createQuery(QueryType queryType,
            Provider nextProvider,
            Map<String, String> attributeList,
            Collection<Profile> includedProfiles)
    {
        final String queryString = attributeList.get("queryString");
        
        if(queryString.trim().equals(""))
        {
            SparqlQueryCreator.log
                    .error("SparqlQueryCreator.createQuery: queryString was empty");
        }
        
        if(queryType.getTemplateString() == null)
        {
            SparqlQueryCreator.log
                    .error("SparqlQueryCreator.createQuery: template was null queryType.getKey()="
                            + queryType.getKey().stringValue());
        }
        
        return SparqlQueryCreator.doReplacementsOnString(queryString,
                queryType.getTemplateString(), queryType, null,
                nextProvider.getNormalisationsNeeded(),
                attributeList, includedProfiles);
    }
    
    /**
     * @param originalQueryType
     * @param includedQueryType
     * @param nextProvider
     * @param attributeList
     * @param includedProfiles
     * @return
     */
    public static String createStaticRdfXmlString(
            QueryType originalQueryType, QueryType includedQueryType,
            Provider nextProvider,
            Map<String, String> attributeList,
            Collection<Profile> includedProfiles)
    {
        final String queryString = attributeList.get("queryString");
        
        if(queryString.trim().equals(""))
        {
            SparqlQueryCreator.log
                    .error("SparqlQueryCreator.createQuery: queryString was empty");
        }
        
        if(includedQueryType.getOutputRdfXmlString() == null)
        {
            SparqlQueryCreator.log
                    .error("SparqlQueryCreator.createQuery: no outputRdfXmlString defined queryType="
                            + includedQueryType.getKey().stringValue());
            
            return "";
        }
        
        return SparqlQueryCreator.doReplacementsOnString(queryString,
                includedQueryType.getOutputRdfXmlString(), originalQueryType,
                includedQueryType,
                nextProvider.getNormalisationsNeeded(),
                attributeList, includedProfiles);
    }
    
    public static String doReplacementsOnString(String queryString,
            String templateString, QueryType originalQueryType,
            QueryType includedQueryType,
            Collection<URI> normalisationUrisNeeded,
            Map<String, String> attributeList,
            Collection<Profile> includedProfiles)
    {
        if(SparqlQueryCreator._DEBUG)
        {
            SparqlQueryCreator.log
                    .debug("SparqlQueryCreator.doReplacementsOnString: queryString="
                            + queryString
                            + " templateString="
                            + templateString
                            + " normalisationUrisNeeded="
                            + normalisationUrisNeeded);
        }
        
        final long start = System.currentTimeMillis();
        
        String replacedString = templateString;
        
        String normalisedStandardUri = originalQueryType.getStandardUriTemplateString();
        
        // StringBuilder replacedString = new StringBuilder(templateString);
        // 
        // StringBuilder normalisedStandardUri = new StringBuilder(originalQueryType.getStandardUriTemplateString());
        
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
        
        if(SparqlQueryCreator._DEBUG)
        {
            SparqlQueryCreator.log
                    .debug("SparqlQueryCreator.createQuery: initial value of replacedString="
                            + replacedString);
            SparqlQueryCreator.log
                    .debug("SparqlQueryCreator.createQuery: initial value of normalisedStandardUri="
                            + normalisedStandardUri);
            SparqlQueryCreator.log
                    .debug("SparqlQueryCreator.createQuery: initial value of normalisedQueryUri="
                            + normalisedQueryUri);
        }
        
        replacedString = replacedString.replace("${limit}", "LIMIT "
                + Settings.getIntPropertyFromConfig("pageoffsetIndividualQueryLimit"));
        
        normalisedQueryUri = normalisedQueryUri.replace("${limit}", "limit/"
                + Settings.getIntPropertyFromConfig("pageoffsetIndividualQueryLimit"));
        
        if(attributeList.containsKey("offset"))
        {
            try
            {
                int pageOffset = Integer.parseInt(attributeList.get("offset"));
                
                if(pageOffset < 1)
                {
                    SparqlQueryCreator.log
                            .warn("SparqlQueryCreator: pageOffset was incorrect fixing it to page 1 bad pageOffset="
                                    + pageOffset);
                    
                    pageOffset = 1;
                }
                
                // actual offset for pageOffset 1 is 0, and pageOffset 2 is
                // Settings.getIntPropertyFromConfig("pageoffsetIndividualQueryLimit")
                final int actualPageOffset = (pageOffset - 1)
                        * Settings.getIntPropertyFromConfig("pageoffsetIndividualQueryLimit");
                
                replacedString = replacedString.replace("${sparqlOffset}",
                        "OFFSET " + actualPageOffset);
                replacedString = replacedString.replace("${pageoffset}",""+pageOffset);
                
                
                normalisedQueryUri = normalisedQueryUri.replace(
                        "${sparqlOffset}", "offset/" + actualPageOffset);
                normalisedQueryUri = normalisedQueryUri.replace("${offset}",
                        "offset/" + pageOffset);
                normalisedQueryUri = normalisedQueryUri.replace("${pageoffset}",""+pageOffset);
            }
            catch (final NumberFormatException nfe)
            {
                SparqlQueryCreator.log
                        .error("SparqlQueryCreator: offset was not valid pageOffset="
                                + attributeList.get("offset"));
            }
        }
        
        if(attributeList.containsKey("useSparqlGraph"))
        {
            final String useSparqlGraphString = attributeList
                    .get("useSparqlGraph");
            
            try
            {
                final boolean useSparqlGraph = Boolean
                        .parseBoolean(useSparqlGraphString);
                
                if(useSparqlGraph)
                {
                    if(attributeList.containsKey("graphUri"))
                    {
                        final String graphUri = attributeList.get("graphUri");
                        
                        if(graphUri.trim().length() == 0)
                        {
                            SparqlQueryCreator.log
                                    .error("SparqlQueryCreator.createQuery: useSparqlGraph was true but the graphUri was invalid graphUri="
                                            + graphUri
                                            + " . Attempting to ignore graphStart and graphEnd for this query");
                            
                            replacedString = replacedString.replace(
                                    "${graphStart}", "");
                            replacedString = replacedString.replace(
                                    "${graphEnd}", "");
                        }
                        else
                        {
                            replacedString = replacedString.replace(
                                    "${graphStart}", " GRAPH <" + graphUri
                                            + "> { ");
                            replacedString = replacedString.replace(
                                    "${graphEnd}", " } ");
                        }
                    }
                    else
                    {
                        SparqlQueryCreator.log
                                .warn("SparqlQueryCreator.createQuery: useSparqlGraph was true but there was no graphUri specified. Attempting to ignore graphStart and graphEnd for this query");
                        
                        replacedString = replacedString.replace(
                                "${graphStart}", "");
                        replacedString = replacedString.replace("${graphEnd}",
                                "");
                    }
                }
                else
                {
                    // replace placeholders with zero spaces
                    replacedString = replacedString
                            .replace("${graphStart}", "");
                    replacedString = replacedString.replace("${graphEnd}", "");
                }
            }
            catch (final Exception ex)
            {
                SparqlQueryCreator.log
                        .error("SparqlQueryCreator.createQuery: useSparqlGraph was not a valid boolean value "
                                + ex.getMessage());
            }
        }
        
        for(final String nextAttribute : attributeList.keySet())
        {
            // we have already handled queryString in a special way, the rest
            // are just simple replacements
            if(nextAttribute.equals("queryString")
                    || nextAttribute.equals("offset"))
            {
                continue;
            }
            
            replacedString = replacedString.replace("${" + nextAttribute + "}",
                    attributeList.get(nextAttribute));
            
            normalisedStandardUri = normalisedStandardUri.replace("${"
                    + nextAttribute + "}", attributeList.get(nextAttribute));
            
            if(SparqlQueryCreator._TRACE)
            {
                SparqlQueryCreator.log
                        .trace("SparqlQueryCreator.createQuery: in replace loop ${"
                                + nextAttribute
                                + "}="
                                + attributeList.get(nextAttribute)
                                + " normalisedStandardUri="
                                + normalisedStandardUri);
            }
            
            normalisedQueryUri = normalisedQueryUri.replace("${"
                    + nextAttribute + "}", attributeList.get(nextAttribute));
            
            if(SparqlQueryCreator._TRACE)
            {
                SparqlQueryCreator.log
                        .trace("SparqlQueryCreator.createQuery: in replace loop ${"
                                + nextAttribute
                                + "}="
                                + attributeList.get(nextAttribute)
                                + " normalisedQueryUri=" + normalisedQueryUri);
            }
        }
        
        final List<String> inputUrlEncodeInstructions = new ArrayList<String>(1);
        inputUrlEncodeInstructions.add(Settings.INPUT_URL_ENCODED);
        
        final List<String> inputPlusUrlEncodeInstructions = new ArrayList<String>(1);
        inputPlusUrlEncodeInstructions.add(Settings.INPUT_PLUS_URL_ENCODED);
        
        final List<String> inputXmlEncodeInstructions = new ArrayList<String>(1);
        inputXmlEncodeInstructions.add(Settings.INPUT_XML_ENCODED);
        
        final List<String> inputNTriplesEncodeInstructions = new ArrayList<String>(
                1);
        inputNTriplesEncodeInstructions.add(Settings.INPUT_NTRIPLES_ENCODED);
        
        final List<String> xmlEncodeInstructions = new ArrayList<String>(1);
        xmlEncodeInstructions.add(Settings.XML_ENCODED);
        
        final List<String> urlEncodeInstructions = new ArrayList<String>(1);
        urlEncodeInstructions.add(Settings.URL_ENCODED);
        
        final List<String> plusUrlEncodeInstructions = new ArrayList<String>(1);
        plusUrlEncodeInstructions.add(Settings.PLUS_URL_ENCODED);
        
        final List<String> ntriplesEncodeInstructions = new ArrayList<String>(1);
        ntriplesEncodeInstructions.add(Settings.NTRIPLES_ENCODED);
        
        final List<String> lowercaseInstructions = new ArrayList<String>(1);
        lowercaseInstructions.add(Settings.LOWERCASE);
        
        // NOTE: make sure that LOWERCASE is added before INPUT_URL_ENCODED as
        // we always want uppercase URL encoded %FF character patterns to match
        // against the database
        final List<String> inputUrlEncodedlowercaseInstructions = new ArrayList<String>(
                1);
        inputUrlEncodedlowercaseInstructions.add(Settings.LOWERCASE);
        inputUrlEncodedlowercaseInstructions.add(Settings.INPUT_URL_ENCODED);
        
        final List<String> inputXmlEncodedlowercaseInstructions = new ArrayList<String>(
                1);
        inputXmlEncodedlowercaseInstructions.add(Settings.LOWERCASE);
        inputXmlEncodedlowercaseInstructions.add(Settings.INPUT_XML_ENCODED);
        
        final List<String> inputUrlEncodedprivatelowercaseInstructions = new ArrayList<String>(
                1);
        inputUrlEncodedprivatelowercaseInstructions
                .add(Settings.PRIVATE_LOWERCASE);
        inputUrlEncodedprivatelowercaseInstructions
                .add(Settings.INPUT_URL_ENCODED);
        
        final List<String> inputXmlEncodedprivatelowercaseInstructions = new ArrayList<String>(1);
        inputXmlEncodedprivatelowercaseInstructions
                .add(Settings.PRIVATE_LOWERCASE);
        inputXmlEncodedprivatelowercaseInstructions
                .add(Settings.INPUT_XML_ENCODED);
        
        final List<String> uppercaseInstructions = new ArrayList<String>(1);
        uppercaseInstructions.add(Settings.UPPERCASE);
        
        final List<String> inputUrlEncodeduppercaseInstructions = new ArrayList<String>(
                2);
        inputUrlEncodeduppercaseInstructions.add(Settings.UPPERCASE);
        inputUrlEncodeduppercaseInstructions.add(Settings.INPUT_URL_ENCODED);
        
        final List<String> inputXmlEncodeduppercaseInstructions = new ArrayList<String>(
                2);
        inputXmlEncodeduppercaseInstructions.add(Settings.UPPERCASE);
        inputXmlEncodeduppercaseInstructions.add(Settings.INPUT_XML_ENCODED);
        
        final List<String> inputUrlEncodedprivateuppercaseInstructions = new ArrayList<String>(
                2);
        inputUrlEncodedprivateuppercaseInstructions
                .add(Settings.PRIVATE_UPPERCASE);
        inputUrlEncodedprivateuppercaseInstructions
                .add(Settings.INPUT_URL_ENCODED);
        
        final List<String> inputXmlEncodedprivateuppercaseInstructions = new ArrayList<String>(2);
        inputXmlEncodedprivateuppercaseInstructions.add(Settings.PRIVATE_UPPERCASE);
        inputXmlEncodedprivateuppercaseInstructions.add(Settings.INPUT_XML_ENCODED);
        
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
        
        replacedString = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString, replacedString, new ArrayList<String>());
        
        normalisedStandardUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString, normalisedStandardUri,
                        new ArrayList<String>());
        
        inputUrlEncoded_normalisedStandardUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString, inputUrlEncoded_normalisedStandardUri,
                        inputUrlEncodeInstructions);
        
        inputXmlEncoded_normalisedStandardUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString, inputXmlEncoded_normalisedStandardUri,
                        inputXmlEncodeInstructions);
        
        normalisedQueryUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString, normalisedQueryUri,
                        new ArrayList<String>());
        
        inputUrlEncoded_normalisedQueryUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString, inputUrlEncoded_normalisedQueryUri,
                        inputUrlEncodeInstructions);
        
        inputPlusUrlEncoded_normalisedStandardUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString, inputPlusUrlEncoded_normalisedStandardUri,
                        inputPlusUrlEncodeInstructions);
        
        
        inputPlusUrlEncoded_normalisedQueryUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString, inputPlusUrlEncoded_normalisedQueryUri,
                        inputPlusUrlEncodeInstructions);
        
        inputXmlEncoded_normalisedQueryUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString, inputXmlEncoded_normalisedQueryUri,
                        inputXmlEncodeInstructions);
        
        inputUrlEncoded_lowercase_normalisedStandardUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString,
                        inputUrlEncoded_lowercase_normalisedStandardUri,
                        inputUrlEncodedlowercaseInstructions);
        inputUrlEncoded_lowercase_normalisedQueryUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString,
                        inputUrlEncoded_lowercase_normalisedQueryUri,
                        inputUrlEncodedlowercaseInstructions);
        
        inputXmlEncoded_lowercase_normalisedStandardUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString,
                        inputXmlEncoded_lowercase_normalisedStandardUri,
                        inputXmlEncodedlowercaseInstructions);
        inputXmlEncoded_lowercase_normalisedQueryUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString,
                        inputXmlEncoded_lowercase_normalisedQueryUri,
                        inputXmlEncodedlowercaseInstructions);
        
        inputUrlEncoded_uppercase_normalisedStandardUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString,
                        inputUrlEncoded_uppercase_normalisedStandardUri,
                        inputUrlEncodeduppercaseInstructions);
        inputUrlEncoded_uppercase_normalisedQueryUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString,
                        inputUrlEncoded_uppercase_normalisedQueryUri,
                        inputUrlEncodeduppercaseInstructions);
        
        inputXmlEncoded_uppercase_normalisedStandardUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString,
                        inputXmlEncoded_uppercase_normalisedStandardUri,
                        inputXmlEncodeduppercaseInstructions);
        inputXmlEncoded_uppercase_normalisedQueryUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString,
                        inputXmlEncoded_uppercase_normalisedQueryUri,
                        inputXmlEncodeduppercaseInstructions);
        
        inputUrlEncoded_privatelowercase_normalisedStandardUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString,
                        inputUrlEncoded_privatelowercase_normalisedStandardUri,
                        inputUrlEncodedprivatelowercaseInstructions);
        inputUrlEncoded_privatelowercase_normalisedQueryUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString,
                        inputUrlEncoded_privatelowercase_normalisedQueryUri,
                        inputUrlEncodedprivatelowercaseInstructions);
        
        inputXmlEncoded_privatelowercase_normalisedStandardUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString,
                        inputXmlEncoded_privatelowercase_normalisedStandardUri,
                        inputXmlEncodedprivatelowercaseInstructions);
        inputXmlEncoded_privatelowercase_normalisedQueryUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString,
                        inputXmlEncoded_privatelowercase_normalisedQueryUri,
                        inputXmlEncodedprivatelowercaseInstructions);
        
        inputUrlEncoded_privateuppercase_normalisedStandardUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString,
                        inputUrlEncoded_privateuppercase_normalisedStandardUri,
                        inputUrlEncodedprivateuppercaseInstructions);
        inputUrlEncoded_privateuppercase_normalisedQueryUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString,
                        inputUrlEncoded_privateuppercase_normalisedQueryUri,
                        inputUrlEncodedprivateuppercaseInstructions);
        
        inputXmlEncoded_privateuppercase_normalisedStandardUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString,
                        inputXmlEncoded_privateuppercase_normalisedStandardUri,
                        inputXmlEncodedprivateuppercaseInstructions);
        inputXmlEncoded_privateuppercase_normalisedQueryUri = SparqlQueryCreator
                .matchAndReplaceInputVariablesForQueryType(originalQueryType,
                        queryString,
                        inputXmlEncoded_privateuppercase_normalisedQueryUri,
                        inputXmlEncodedprivateuppercaseInstructions);
        
        if(SparqlQueryCreator._TRACE)
        {
            SparqlQueryCreator.log
                    .trace("SparqlQueryCreator.createQuery: after match replacements replacedString="
                            + replacedString);
            SparqlQueryCreator.log
                    .trace("SparqlQueryCreator.createQuery: after match replacements normalisedStandardUri="
                            + normalisedStandardUri);
            SparqlQueryCreator.log
                    .trace("SparqlQueryCreator.createQuery: after match replacements normalisedQueryUri="
                            + normalisedQueryUri);
        }
        
        // These three are known to be able to insert the normalised standard
        // (ie, construct), query specific normalised, and endpointspecific
        // standard URI respectively
        replacedString = replacedString.replace("${normalisedStandardUri}",
                normalisedStandardUri);
        replacedString = replacedString.replace("${normalisedQueryUri}",
                normalisedQueryUri);
        
        replacedString = replacedString.replace(
                "${urlEncoded_normalisedStandardUri}", Utilities
                        .percentEncode(normalisedStandardUri));
        
        replacedString = replacedString.replace(
                "${plusUrlEncoded_normalisedStandardUri}", Utilities
                        .plusPercentEncode(normalisedStandardUri));
        
        
        replacedString = replacedString.replace(
                "${inputUrlEncoded_normalisedStandardUri}",
                inputUrlEncoded_normalisedStandardUri);
        
        replacedString = replacedString.replace(
                "${inputPlusUrlEncoded_normalisedStandardUri}", inputPlusUrlEncoded_normalisedStandardUri);
        
        replacedString = replacedString.replace(
                "${inputUrlEncoded_normalisedQueryUri}",
                inputUrlEncoded_normalisedQueryUri);
        
        replacedString = replacedString.replace(
                "${inputPlusUrlEncoded_normalisedQueryUri}", inputPlusUrlEncoded_normalisedQueryUri);
        
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_normalisedStandardUri}",
                        Utilities.xmlEncodeString(inputUrlEncoded_normalisedStandardUri));
        
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputPlusUrlEncoded_normalisedStandardUri}",
                        Utilities.xmlEncodeString(inputPlusUrlEncoded_normalisedStandardUri));
        
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputPlusUrlEncoded_normalisedQueryUri}",
                        Utilities.xmlEncodeString(inputPlusUrlEncoded_normalisedQueryUri));
        
        replacedString = replacedString.replace(
                "${xmlEncoded_normalisedStandardUri}", Utilities
                        .xmlEncodeString(normalisedStandardUri));
        
        replacedString = replacedString.replace(
                "${urlEncoded_lowercase_normalisedStandardUri}", Utilities
                        .percentEncode(normalisedStandardUri.toLowerCase()));
        replacedString = replacedString.replace(
                "${urlEncoded_uppercase_normalisedStandardUri}", Utilities
                        .percentEncode(normalisedStandardUri.toUpperCase()));
        
        replacedString = replacedString
                .replace("${xmlEncoded_lowercase_normalisedStandardUri}",
                        Utilities.xmlEncodeString((normalisedStandardUri
                                .toLowerCase())));
        replacedString = replacedString
                .replace("${xmlEncoded_uppercase_normalisedStandardUri}",
                        Utilities.xmlEncodeString((normalisedStandardUri
                                .toUpperCase())));
        
        replacedString = replacedString.replace(
                "${inputUrlEncoded_lowercase_normalisedStandardUri}",
                inputUrlEncoded_lowercase_normalisedStandardUri);
        replacedString = replacedString.replace(
                "${inputUrlEncoded_uppercase_normalisedStandardUri}",
                inputUrlEncoded_uppercase_normalisedStandardUri);
        
        replacedString = replacedString.replace(
                "${inputUrlEncoded_privatelowercase_normalisedStandardUri}",
                inputUrlEncoded_privatelowercase_normalisedStandardUri);
        replacedString = replacedString.replace(
                "${inputUrlEncoded_privateuppercase_normalisedStandardUri}",
                inputUrlEncoded_privateuppercase_normalisedStandardUri);
        
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_lowercase_normalisedStandardUri}",
                        Utilities
                                .xmlEncodeString(inputUrlEncoded_lowercase_normalisedStandardUri));
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_uppercase_normalisedStandardUri}",
                        Utilities
                                .xmlEncodeString(inputUrlEncoded_uppercase_normalisedStandardUri));
        
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_privatelowercase_normalisedStandardUri}",
                        Utilities
                                .xmlEncodeString(inputUrlEncoded_privatelowercase_normalisedStandardUri));
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_privateuppercase_normalisedStandardUri}",
                        Utilities
                                .xmlEncodeString(inputUrlEncoded_privateuppercase_normalisedStandardUri));
        
        // replacedString = replacedString.replace(
                // "${xmlEncoded_normalisedOntologyUriPrefix}", Utilities
                        // .xmlEncodeString(normalisedOntologyUriPrefix));
        // replacedString = replacedString.replace(
                // "${xmlEncoded_normalisedOntologyUriSuffix}", Utilities
                        // .xmlEncodeString(normalisedOntologyUriSuffix));
        
        replacedString = replacedString.replace(
                "${urlEncoded_normalisedQueryUri}", Utilities
                        .percentEncode(normalisedQueryUri));
        
        replacedString = replacedString.replace(
                "${xmlEncoded_normalisedQueryUri}", Utilities
                        .xmlEncodeString(normalisedQueryUri));
        
        replacedString = replacedString.replace(
                "${xmlEncoded_inputUrlEncoded_normalisedQueryUri}", Utilities
                        .xmlEncodeString(inputUrlEncoded_normalisedQueryUri));
        
        replacedString = replacedString.replace(
                "${urlEncoded_lowercase_normalisedQueryUri}", Utilities
                        .percentEncode(normalisedQueryUri.toLowerCase()));
        replacedString = replacedString.replace(
                "${urlEncoded_uppercase_normalisedQueryUri}", Utilities
                        .percentEncode(normalisedQueryUri.toUpperCase()));
        
        replacedString = replacedString.replace(
                "${xmlEncoded_lowercase_normalisedQueryUri}", Utilities
                        .xmlEncodeString((normalisedQueryUri.toLowerCase())));
        replacedString = replacedString.replace(
                "${xmlEncoded_uppercase_normalisedQueryUri}", Utilities
                        .xmlEncodeString((normalisedQueryUri.toUpperCase())));
        
        replacedString = replacedString.replace(
                "${inputUrlEncoded_lowercase_normalisedQueryUri}",
                inputUrlEncoded_lowercase_normalisedQueryUri);
        replacedString = replacedString.replace(
                "${inputUrlEncoded_uppercase_normalisedQueryUri}",
                inputUrlEncoded_uppercase_normalisedQueryUri);
        
        replacedString = replacedString.replace(
                "${inputUrlEncoded_privatelowercase_normalisedQueryUri}",
                inputUrlEncoded_privatelowercase_normalisedQueryUri);
        replacedString = replacedString.replace(
                "${inputUrlEncoded_privateuppercase_normalisedQueryUri}",
                inputUrlEncoded_privateuppercase_normalisedQueryUri);
        
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_lowercase_normalisedQueryUri}",
                        Utilities
                                .xmlEncodeString(inputUrlEncoded_lowercase_normalisedQueryUri));
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_uppercase_normalisedQueryUri}",
                        Utilities
                                .xmlEncodeString(inputUrlEncoded_uppercase_normalisedQueryUri));
        
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_privatelowercase_normalisedQueryUri}",
                        Utilities
                                .xmlEncodeString(inputUrlEncoded_privatelowercase_normalisedQueryUri));
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_privateuppercase_normalisedQueryUri}",
                        Utilities
                                .xmlEncodeString(inputUrlEncoded_privateuppercase_normalisedQueryUri));
        
        if(SparqlQueryCreator._TRACE)
        {
            SparqlQueryCreator.log
                    .trace("SparqlQueryCreator.createQuery: before regex loop started replacedString="
                            + replacedString);
            SparqlQueryCreator.log
                    .trace("SparqlQueryCreator.createQuery: before regex loop started xmlEncoded_inputUrlEncoded_normalisedQueryUri="
                            + Utilities
                                    .xmlEncodeString(inputUrlEncoded_normalisedQueryUri));
        }
        
        final Collection<NormalisationRule> normalisationsNeeded = Settings
                .getNormalisationRulesForUris(normalisationUrisNeeded,
                        Settings.LOWEST_ORDER_FIRST);
        
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
        
        String inputUrlEncoded_privatelowercase_endpointSpecificUri = inputUrlEncoded_privatelowercase_normalisedStandardUri;
        String inputXmlEncoded_privatelowercase_endpointSpecificUri = inputXmlEncoded_privatelowercase_normalisedStandardUri;
        String inputUrlEncoded_privatelowercase_endpointSpecificQueryUri = inputUrlEncoded_privatelowercase_normalisedQueryUri;
        String inputXmlEncoded_privatelowercase_endpointSpecificQueryUri = inputXmlEncoded_privatelowercase_normalisedQueryUri;
        
        String inputUrlEncoded_uppercase_endpointSpecificUri = inputUrlEncoded_uppercase_normalisedStandardUri;
        String inputXmlEncoded_uppercase_endpointSpecificUri = inputXmlEncoded_uppercase_normalisedStandardUri;
        String inputUrlEncoded_uppercase_endpointSpecificQueryUri = inputUrlEncoded_uppercase_normalisedQueryUri;
        String inputXmlEncoded_uppercase_endpointSpecificQueryUri = inputXmlEncoded_uppercase_normalisedQueryUri;
        
        String inputUrlEncoded_privateuppercase_endpointSpecificUri = inputUrlEncoded_privateuppercase_normalisedStandardUri;
        String inputXmlEncoded_privateuppercase_endpointSpecificUri = inputXmlEncoded_privateuppercase_normalisedStandardUri;
        String inputUrlEncoded_privateuppercase_endpointSpecificQueryUri = inputUrlEncoded_privateuppercase_normalisedQueryUri;
        String inputXmlEncoded_privateuppercase_endpointSpecificQueryUri = inputXmlEncoded_privateuppercase_normalisedQueryUri;
        
        for(final NormalisationRule nextRule : normalisationsNeeded)
        {
            if(Settings.isRdfRuleUsedWithProfileList(nextRule.getKey(),
                    nextRule.getProfileIncludeExcludeOrder(), includedProfiles))
            {
                endpointSpecificUri = (String)nextRule
                        .stageQueryVariables(endpointSpecificUri);
                endpointSpecificQueryUri = (String)nextRule
                        .stageQueryVariables(endpointSpecificQueryUri);
                // endpointSpecificOntologyUri = (String)nextRule
                        // .applyInputRegex(endpointSpecificOntologyUri);
                
                inputUrlEncoded_endpointSpecificUri = (String)nextRule
                        .stageQueryVariables(inputUrlEncoded_endpointSpecificUri);
                inputPlusUrlEncoded_endpointSpecificUri = (String)nextRule
                        .stageQueryVariables(inputPlusUrlEncoded_endpointSpecificUri);
                inputXmlEncoded_endpointSpecificUri = (String)nextRule
                        .stageQueryVariables(inputXmlEncoded_endpointSpecificUri);
                inputUrlEncoded_endpointSpecificQueryUri = (String)nextRule
                        .stageQueryVariables(inputUrlEncoded_endpointSpecificQueryUri);
                inputPlusUrlEncoded_endpointSpecificQueryUri = (String)nextRule
                        .stageQueryVariables(inputPlusUrlEncoded_endpointSpecificQueryUri);
                inputXmlEncoded_endpointSpecificQueryUri = (String)nextRule
                        .stageQueryVariables(inputXmlEncoded_endpointSpecificQueryUri);
                
                inputUrlEncoded_lowercase_endpointSpecificUri = (String)nextRule
                        .stageQueryVariables(inputUrlEncoded_lowercase_endpointSpecificUri);
                inputXmlEncoded_lowercase_endpointSpecificUri = (String)nextRule
                        .stageQueryVariables(inputXmlEncoded_lowercase_endpointSpecificUri);
                inputUrlEncoded_uppercase_endpointSpecificUri = (String)nextRule
                        .stageQueryVariables(inputUrlEncoded_uppercase_endpointSpecificUri);
                inputXmlEncoded_uppercase_endpointSpecificUri = (String)nextRule
                        .stageQueryVariables(inputXmlEncoded_uppercase_endpointSpecificUri);
                
                inputUrlEncoded_lowercase_endpointSpecificQueryUri = (String)nextRule
                        .stageQueryVariables(inputUrlEncoded_lowercase_endpointSpecificQueryUri);
                inputXmlEncoded_lowercase_endpointSpecificQueryUri = (String)nextRule
                        .stageQueryVariables(inputXmlEncoded_lowercase_endpointSpecificQueryUri);
                inputUrlEncoded_uppercase_endpointSpecificQueryUri = (String)nextRule
                        .stageQueryVariables(inputUrlEncoded_uppercase_endpointSpecificQueryUri);
                inputXmlEncoded_uppercase_endpointSpecificQueryUri = (String)nextRule
                        .stageQueryVariables(inputXmlEncoded_uppercase_endpointSpecificQueryUri);
                
                inputUrlEncoded_privatelowercase_endpointSpecificUri = (String)nextRule
                        .stageQueryVariables(inputUrlEncoded_privatelowercase_endpointSpecificUri);
                inputXmlEncoded_privatelowercase_endpointSpecificUri = (String)nextRule
                        .stageQueryVariables(inputXmlEncoded_privatelowercase_endpointSpecificUri);
                inputUrlEncoded_privateuppercase_endpointSpecificUri = (String)nextRule
                        .stageQueryVariables(inputUrlEncoded_privateuppercase_endpointSpecificUri);
                inputXmlEncoded_privateuppercase_endpointSpecificUri = (String)nextRule
                        .stageQueryVariables(inputXmlEncoded_privateuppercase_endpointSpecificUri);
                
                inputUrlEncoded_privatelowercase_endpointSpecificQueryUri = (String)nextRule
                        .stageQueryVariables(inputUrlEncoded_privatelowercase_endpointSpecificQueryUri);
                inputXmlEncoded_privatelowercase_endpointSpecificQueryUri = (String)nextRule
                        .stageQueryVariables(inputXmlEncoded_privatelowercase_endpointSpecificQueryUri);
                inputUrlEncoded_privateuppercase_endpointSpecificQueryUri = (String)nextRule
                        .stageQueryVariables(inputUrlEncoded_privateuppercase_endpointSpecificQueryUri);
                inputXmlEncoded_privateuppercase_endpointSpecificQueryUri = (String)nextRule
                        .stageQueryVariables(inputXmlEncoded_privateuppercase_endpointSpecificQueryUri);
                
                if(SparqlQueryCreator._TRACE)
                {
                    SparqlQueryCreator.log
                            .trace("SparqlQueryCreator.createQuery: in regex loop endpointSpecificUri="
                                    + endpointSpecificUri);
                    // SparqlQueryCreator.log
                            // .trace("SparqlQueryCreator.createQuery: in regex loop endpointSpecificOntologyUri="
                                    // + endpointSpecificOntologyUri);
                    SparqlQueryCreator.log
                            .trace("SparqlQueryCreator.createQuery: in regex loop inputUrlEncoded_endpointSpecificUri="
                                    + inputUrlEncoded_endpointSpecificUri);
                    SparqlQueryCreator.log
                            .trace("SparqlQueryCreator.createQuery: in regex loop inputXmlEncoded_endpointSpecificUri="
                                    + inputXmlEncoded_endpointSpecificUri);
                }
            }
        }
        
        replacedString = replacedString.replace("${endpointSpecificUri}",
                endpointSpecificUri);
        replacedString = replacedString.replace("${endpointSpecificQueryUri}",
                endpointSpecificQueryUri);
        // replacedString = replacedString.replace(
                // "${endpointSpecificOntologyUri}", endpointSpecificOntologyUri);
        
        replacedString = replacedString.replace(
                "${ntriplesEncoded_endpointSpecificUri}", Utilities
                        .ntriplesEncode(endpointSpecificUri));
        
        replacedString = replacedString.replace(
                "${ntriplesEncoded_normalisedStandardUri}", Utilities
                        .ntriplesEncode(normalisedStandardUri));
        
        replacedString = replacedString.replace(
                "${ntriplesEncoded_normalisedQueryUri}", Utilities
                        .ntriplesEncode(normalisedQueryUri));
        
        replacedString = replacedString.replace(
                "${xmlEncoded_ntriplesEncoded_endpointSpecificUri}", Utilities.xmlEncodeString(Utilities.ntriplesEncode(endpointSpecificUri)));
        
        replacedString = replacedString.replace(
                "${xmlEncoded_ntriplesEncoded_normalisedStandardUri}", Utilities.xmlEncodeString(Utilities.ntriplesEncode(normalisedStandardUri)));
        
        replacedString = replacedString.replace(
                "${xmlEncoded_ntriplesEncoded_normalisedQueryUri}", Utilities.xmlEncodeString(Utilities.ntriplesEncode(normalisedQueryUri)));
        
        replacedString = replacedString.replace(
                "${urlEncoded_lowercase_endpointSpecificUri}", Utilities
                        .percentEncode(endpointSpecificUri.toLowerCase()));
        replacedString = replacedString.replace(
                "${urlEncoded_uppercase_endpointSpecificUri}", Utilities
                        .percentEncode(endpointSpecificUri.toUpperCase()));
        
        replacedString = replacedString.replace(
                "${urlEncoded_lowercase_endpointSpecificQueryUri}", Utilities
                        .percentEncode(endpointSpecificQueryUri.toLowerCase()));
        replacedString = replacedString.replace(
                "${urlEncoded_uppercase_endpointSpecificQueryUri}", Utilities
                        .percentEncode(endpointSpecificQueryUri.toUpperCase()));
        
        replacedString = replacedString.replace(
                "${xmlEncoded_endpointSpecificUri}", Utilities
                        .xmlEncodeString(endpointSpecificUri));
        
        replacedString = replacedString.replace(
                "${xmlEncoded_endpointSpecificQueryUri}", Utilities
                        .xmlEncodeString(endpointSpecificQueryUri));
        
        replacedString = replacedString.replace(
                "${xmlEncoded_lowercase_endpointSpecificUri}", Utilities
                        .xmlEncodeString(endpointSpecificUri.toLowerCase()));
        replacedString = replacedString.replace(
                "${xmlEncoded_uppercase_endpointSpecificUri}", Utilities
                        .xmlEncodeString(endpointSpecificUri.toUpperCase()));
        replacedString = replacedString.replace(
                "${xmlEncoded_lowercase_endpointSpecificQueryUri}",
                Utilities.xmlEncodeString(endpointSpecificQueryUri
                        .toLowerCase()));
        replacedString = replacedString.replace(
                "${xmlEncoded_uppercase_endpointSpecificQueryUri}",
                Utilities.xmlEncodeString(endpointSpecificQueryUri
                        .toUpperCase()));
        
        replacedString = replacedString.replace(
                "${inputXmlEncoded_endpointSpecificUri}",
                inputXmlEncoded_endpointSpecificUri);
        replacedString = replacedString.replace(
                "${inputXmlEncoded_endpointSpecificQueryUri}",
                inputXmlEncoded_endpointSpecificQueryUri);
        
        replacedString = replacedString.replace(
                "${urlEncoded_endpointSpecificUri}", Utilities
                        .percentEncode(endpointSpecificUri));
        replacedString = replacedString.replace(
                "${urlEncoded_endpointSpecificQueryUri}", Utilities
                        .percentEncode(endpointSpecificQueryUri));
        
        replacedString = replacedString.replace(
                "${inputUrlEncoded_endpointSpecificUri}",
                inputUrlEncoded_endpointSpecificUri);
        replacedString = replacedString.replace(
                "${inputUrlEncoded_endpointSpecificQueryUri}",
                inputUrlEncoded_endpointSpecificQueryUri);
        
        replacedString = replacedString.replace(
                "${inputPlusUrlEncoded_endpointSpecificUri}",
                inputPlusUrlEncoded_endpointSpecificUri);
        replacedString = replacedString.replace(
                "${inputPlusUrlEncoded_endpointSpecificQueryUri}",
                inputPlusUrlEncoded_endpointSpecificQueryUri);
        
        replacedString = replacedString.replace(
                "${xmlEncoded_inputUrlEncoded_endpointSpecificUri}", Utilities
                        .xmlEncodeString(inputUrlEncoded_endpointSpecificUri));
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_endpointSpecificQueryUri}",
                        Utilities
                                .xmlEncodeString(inputUrlEncoded_endpointSpecificQueryUri));
        
        replacedString = replacedString.replace(
                "${xmlEncoded_inputPlusUrlEncoded_endpointSpecificUri}", Utilities
                        .xmlEncodeString(inputPlusUrlEncoded_endpointSpecificUri));
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputPlusUrlEncoded_endpointSpecificQueryUri}",
                        Utilities.xmlEncodeString(inputPlusUrlEncoded_endpointSpecificQueryUri));
        
        replacedString = replacedString.replace(
                "${inputXmlEncoded_uppercase_endpointSpecificUri}",
                inputXmlEncoded_uppercase_endpointSpecificUri);
        replacedString = replacedString.replace(
                "${inputUrlEncoded_uppercase_endpointSpecificUri}",
                inputUrlEncoded_uppercase_endpointSpecificUri);
        
        replacedString = replacedString.replace(
                "${inputXmlEncoded_uppercase_endpointSpecificQueryUri}",
                inputXmlEncoded_uppercase_endpointSpecificQueryUri);
        replacedString = replacedString.replace(
                "${inputUrlEncoded_uppercase_endpointSpecificQueryUri}",
                inputUrlEncoded_uppercase_endpointSpecificQueryUri);
        
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_uppercase_endpointSpecificUri}",
                        Utilities
                                .xmlEncodeString(inputUrlEncoded_uppercase_endpointSpecificUri));
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_uppercase_endpointSpecificQueryUri}",
                        Utilities
                                .xmlEncodeString(inputUrlEncoded_uppercase_endpointSpecificQueryUri));
        
        replacedString = replacedString.replace(
                "${inputXmlEncoded_lowercase_endpointSpecificUri}",
                inputXmlEncoded_lowercase_endpointSpecificUri);
        replacedString = replacedString.replace(
                "${inputUrlEncoded_lowercase_endpointSpecificUri}",
                inputUrlEncoded_lowercase_endpointSpecificUri);
        replacedString = replacedString.replace(
                "${inputXmlEncoded_lowercase_endpointSpecificQueryUri}",
                inputXmlEncoded_lowercase_endpointSpecificQueryUri);
        replacedString = replacedString.replace(
                "${inputUrlEncoded_lowercase_endpointSpecificQueryUri}",
                inputUrlEncoded_lowercase_endpointSpecificQueryUri);
        
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_lowercase_endpointSpecificUri}",
                        Utilities
                                .xmlEncodeString(inputUrlEncoded_lowercase_endpointSpecificUri));
        replacedString = replacedString.replace(
                "${inputXmlEncoded_privateuppercase_endpointSpecificUri}",
                inputXmlEncoded_privateuppercase_endpointSpecificUri);
        replacedString = replacedString.replace(
                "${inputUrlEncoded_privateuppercase_endpointSpecificUri}",
                inputUrlEncoded_privateuppercase_endpointSpecificUri);
        
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_lowercase_endpointSpecificQueryUri}",
                        Utilities
                                .xmlEncodeString(inputUrlEncoded_lowercase_endpointSpecificQueryUri));
        replacedString = replacedString.replace(
                "${inputXmlEncoded_privateuppercase_endpointSpecificQueryUri}",
                inputXmlEncoded_privateuppercase_endpointSpecificQueryUri);
        replacedString = replacedString.replace(
                "${inputUrlEncoded_privateuppercase_endpointSpecificQueryUri}",
                inputUrlEncoded_privateuppercase_endpointSpecificQueryUri);
        
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_privateuppercase_endpointSpecificUri}",
                        Utilities
                                .xmlEncodeString(inputUrlEncoded_privateuppercase_endpointSpecificUri));
        replacedString = replacedString.replace(
                "${inputXmlEncoded_privatelowercase_endpointSpecificUri}",
                inputXmlEncoded_privatelowercase_endpointSpecificUri);
        replacedString = replacedString.replace(
                "${inputUrlEncoded_privatelowercase_endpointSpecificUri}",
                inputUrlEncoded_privatelowercase_endpointSpecificUri);
        
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_privateuppercase_endpointSpecificQueryUri}",
                        Utilities
                                .xmlEncodeString(inputUrlEncoded_privateuppercase_endpointSpecificQueryUri));
        replacedString = replacedString.replace(
                "${inputXmlEncoded_privatelowercase_endpointSpecificQueryUri}",
                inputXmlEncoded_privatelowercase_endpointSpecificQueryUri);
        replacedString = replacedString.replace(
                "${inputUrlEncoded_privatelowercase_endpointSpecificQueryUri}",
                inputUrlEncoded_privatelowercase_endpointSpecificQueryUri);
        
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_privatelowercase_endpointSpecificUri}",
                        Utilities
                                .xmlEncodeString(inputUrlEncoded_privatelowercase_endpointSpecificUri));
        replacedString = replacedString
                .replace(
                        "${xmlEncoded_inputUrlEncoded_privatelowercase_endpointSpecificQueryUri}",
                        Utilities
                                .xmlEncodeString(inputUrlEncoded_privatelowercase_endpointSpecificQueryUri));
        
        if(_DEBUG)
        {
            final long end = System.currentTimeMillis();
            
            SparqlQueryCreator.log
                    .debug(String.format("%s: timing=%10d",
                            "SparqlQueryCreator.doReplacementsOnString",
                            (end - start)));
            
            SparqlQueryCreator.log
                    .trace("SparqlQueryCreator.doReplacementsOnString: returning replacedString="
                            + replacedString);
        }
        
        return replacedString;
    }
    
    /**
     * @param nextProvider
     * @param queryString
     * @param nextEndpoint
     * @param realHostName
     * @param pageOffset
     * @return
     */
    public static Map<String, String> getAttributeListFor(
            Provider nextProvider, String queryString,
            String nextEndpoint, String realHostName, int pageOffset)
    {
        if(SparqlQueryCreator._DEBUG)
        {
            SparqlQueryCreator.log
                    .debug("SparqlQueryCreator.getAttributeListFor: called with nextProvider="
                            + nextProvider.toString()
                            + " queryString="
                            + queryString
                            + " nextEndpoint="
                            + nextEndpoint
                            + " realHostName="
                            + realHostName
                            + " pageOffset="
                            + pageOffset);
        }
        
        final Map<String, String> attributeList = new Hashtable<String, String>();
        
        attributeList.put("defaultHostName", Settings.getStringPropertyFromConfig("hostName"));
        attributeList.put("defaultHostAddress", Settings.getDefaultHostAddress());
        attributeList.put("defaultSeparator", Settings.getStringPropertyFromConfig("separator"));
        attributeList.put("realHostName", realHostName);
        attributeList.put("queryString", queryString);
        attributeList.put("graphUri", nextProvider.getSparqlGraphUri());
        attributeList.put("useSparqlGraph", nextProvider.getUseSparqlGraph() + "");
        attributeList.put("offset", pageOffset + "");
        
        // if(nextProvider.rdfNormalisationsNeeded != null)
        // {
        // attributeList.put("rdfNormalisationsNeeded",Utilities.joinStringCollection(nextProvider.rdfNormalisationsNeeded,","));
        // }
        // else
        // {
        // attributeList.put("rdfNormalisationsNeeded","");
        // }
        
        attributeList.put("endpointUrl", nextEndpoint);
        
        attributeList.put("urlEncoded_defaultHostName", Utilities
                .percentEncode(Settings.getStringPropertyFromConfig("hostName")));
        attributeList.put("urlEncoded_defaultHostAddress", Utilities
                .percentEncode(Settings.getDefaultHostAddress()));
        attributeList.put("urlEncoded_defaultSeparator", Utilities
                .percentEncode(Settings.getStringPropertyFromConfig("separator")));
        attributeList.put("urlEncoded_graphUri", Utilities
                .percentEncode(nextProvider.getSparqlGraphUri()));
        attributeList.put("urlEncoded_endpointUrl", Utilities
                .percentEncode(nextEndpoint));
        attributeList.put("urlEncoded_realHostName", Utilities
                .percentEncode(realHostName));
        attributeList.put("urlEncoded_queryString", Utilities
                .percentEncode(queryString));
        
        attributeList.put("xmlEncoded_defaultHostName", Utilities
                .xmlEncodeString(Settings.getStringPropertyFromConfig("hostName")));
        attributeList.put("xmlEncoded_defaultHostAddress", Utilities
                .xmlEncodeString("http://" + Settings.getStringPropertyFromConfig("hostName") + "/"));
        attributeList.put("xmlEncoded_defaultSeparator", Utilities
                .xmlEncodeString(Settings.getStringPropertyFromConfig("separator")));
        attributeList.put("xmlEncoded_graphUri", Utilities
                .xmlEncodeString(nextProvider.getSparqlGraphUri()));
        attributeList.put("xmlEncoded_endpointUrl", Utilities
                .xmlEncodeString(nextEndpoint));
        attributeList.put("xmlEncoded_realHostName", Utilities
                .xmlEncodeString(realHostName));
        attributeList.put("xmlEncoded_queryString", Utilities
                .xmlEncodeString(queryString));
        
        attributeList.put("xmlEncoded_urlEncoded_defaultHostName", Utilities
                .xmlEncodeString(Utilities
                        .percentEncode(Settings.getStringPropertyFromConfig("hostName"))));
        attributeList.put("xmlEncoded_urlEncoded_defaultHostAddress", Utilities
                .xmlEncodeString(Utilities.percentEncode("http://"
                        + Settings.getStringPropertyFromConfig("hostName") + "/")));
        attributeList.put("xmlEncoded_urlEncoded_defaultSeparator", Utilities
                .xmlEncodeString(Utilities
                        .percentEncode(Settings.getStringPropertyFromConfig("separator"))));
        attributeList.put("xmlEncoded_urlEncoded_graphUri", Utilities
                .xmlEncodeString(Utilities
                        .percentEncode(nextProvider.getSparqlGraphUri())));
        attributeList.put("xmlEncoded_urlEncoded_endpointUrl", Utilities
                .xmlEncodeString(Utilities.percentEncode(nextEndpoint)));
        attributeList.put("xmlEncoded_urlEncoded_realHostName", Utilities
                .xmlEncodeString(Utilities.percentEncode(realHostName)));
        attributeList.put("xmlEncoded_urlEncoded_queryString", Utilities
                .xmlEncodeString(Utilities.percentEncode(queryString)));
        
        return attributeList;
    }
    
    /**
     * @param originalQueryType
     * @param queryString
     * @param templateString
     * @param specialInstructions
     * @return
     */
    public static String matchAndReplaceInputVariablesForQueryType(
            QueryType originalQueryType, String queryString,
            String templateString, List<String> specialInstructions)
    {
        if(SparqlQueryCreator._DEBUG)
        {
            SparqlQueryCreator.log
                    .debug("SparqlQueryCreator.matchAndReplaceInputVariablesForQueryType: queryString="
                            + queryString + " templateString=" + templateString);
        }
        
        final long start = System.currentTimeMillis();
        
        String replacedString = templateString;
        
        List<String> allMatches = originalQueryType.matchesForQueryString(queryString);
        
        int nextMatch = 0;
        
        for(; nextMatch < allMatches.size(); nextMatch++)
        {
            final int number = nextMatch + 1;
            
            final boolean isPublic = originalQueryType
                    .isInputVariablePublic(number);
            
            String inputReplaceString = allMatches.get(nextMatch);
            
            final String matchString = "${input_" + number + "}";
            
            final String urlEncodedReplaceString = Utilities.percentEncode(inputReplaceString);
            final String urlEncodedMatchString = "${urlEncoded_input_" + number + "}";
            
            final String plusUrlEncodedReplaceString = Utilities.plusPercentEncode(inputReplaceString);
            final String plusUrlEncodedMatchString = "${plusUrlEncoded_input_" + number + "}";
            
            // final String plusSpaceEncodedReplaceString = Utilities.plusSpaceEncode(inputReplaceString);
            // final String plusSpaceEncodedMatchString = "${plusSpaceEncoded_input_" + number + "}";
            
            final String xmlEncodedReplaceString = Utilities.xmlEncodeString(inputReplaceString);
            final String xmlEncodedMatchString = "${xmlEncoded_input_" + number + "}";
            
            final String xmlEncodedUrlEncodedReplaceString = Utilities
                    .xmlEncodeString(Utilities
                            .percentEncode(inputReplaceString));
                    
            final String xmlEncodedUrlEncodedMatchString = "${xmlEncoded_urlEncoded_input_"
                    + number + "}";
            
            final String xmlEncodedPlusUrlEncodedReplaceString = Utilities
                    .xmlEncodeString(Utilities
                            .plusPercentEncode(inputReplaceString));
                    
            final String xmlEncodedPlusUrlEncodedMatchString = "${xmlEncoded_plusUrlEncoded_input_"
                    + number + "}";
            
            final String xmlEncodedNTriplesReplaceString = Utilities
                    .xmlEncodeString(Utilities
                            .ntriplesEncode(inputReplaceString));
            final String xmlEncodedNTriplesMatchString = "${xmlEncoded_ntriplesEncoded_input_"
                    + number + "}";
            
            final String nTriplesReplaceString = Utilities
                    .ntriplesEncode(inputReplaceString);
            final String nTriplesMatchString = "${ntriplesEncoded_input_"
                    + number + "}";
            
            // Start lowercase region
            /***********************/
            
            final String lowercaseReplaceString = inputReplaceString
                    .toLowerCase();
            final String lowercaseMatchString = "${lowercase_input_" + number
                    + "}";
            
            final String urlEncodedLowercaseReplaceString = Utilities
                    .percentEncode(inputReplaceString.toLowerCase());
            final String urlEncodedLowercaseMatchString = "${urlEncoded_lowercase_input_"
                    + number + "}";
            
            final String xmlEncodedLowercaseReplaceString = Utilities
                    .xmlEncodeString(inputReplaceString.toLowerCase());
            final String xmlEncodedLowercaseMatchString = "${xmlEncoded_lowercase_input_"
                    + number + "}";
            
            final String xmlEncodedUrlEncodedLowercaseReplaceString = Utilities
                    .xmlEncodeString(Utilities.percentEncode(inputReplaceString
                            .toLowerCase()));
            final String xmlEncodedUrlEncodedLowercaseMatchString = "${xmlEncoded_urlEncoded_lowercase_input_"
                    + number + "}";
            
            final String xmlEncodedNTriplesLowercaseReplaceString = Utilities
                    .xmlEncodeString(Utilities
                            .ntriplesEncode(inputReplaceString.toLowerCase()));
            final String xmlEncodedNTriplesLowercaseMatchString = "${xmlEncoded_ntriplesEncoded_lowercase_input_"
                    + number + "}";
            
            // End lowercase region
            /***********************/
            // Start uppercase region
            final String uppercaseReplaceString = inputReplaceString
                    .toUpperCase();
            final String uppercaseMatchString = "${uppercase_input_" + number
                    + "}";
            
            final String urlEncodedUppercaseReplaceString = Utilities
                    .percentEncode(inputReplaceString.toUpperCase());
            final String urlEncodedUppercaseMatchString = "${urlEncoded_uppercase_input_"
                    + number + "}";
            
            final String xmlEncodedUppercaseReplaceString = Utilities
                    .xmlEncodeString(inputReplaceString.toUpperCase());
            final String xmlEncodedUppercaseMatchString = "${xmlEncoded_uppercase_input_"
                    + number + "}";
            
            final String xmlEncodedUrlEncodedUppercaseReplaceString = Utilities
                    .xmlEncodeString(Utilities.percentEncode(inputReplaceString
                            .toUpperCase()));
            final String xmlEncodedUrlEncodedUppercaseMatchString = "${xmlEncoded_urlEncoded_uppercase_input_"
                    + number + "}";
            
            final String xmlEncodedNTriplesUppercaseReplaceString = Utilities
                    .xmlEncodeString(Utilities
                            .ntriplesEncode(inputReplaceString.toUpperCase()));
            final String xmlEncodedNTriplesUppercaseMatchString = "${xmlEncoded_ntriplesEncoded_uppercase_input_"
                    + number + "}";
            
            // End uppercase region
            /***********************/
            
            if(SparqlQueryCreator._TRACE)
            {
                SparqlQueryCreator.log
                        .trace("SparqlQueryCreator.matchAndReplaceInputVariablesForQueryType: number="
                                + number);
                SparqlQueryCreator.log
                        .trace("SparqlQueryCreator.matchAndReplaceInputVariablesForQueryType: input_"
                                + number + "=" + inputReplaceString);
                SparqlQueryCreator.log
                        .trace("SparqlQueryCreator.matchAndReplaceInputVariablesForQueryType: matchString="
                                + matchString);
                SparqlQueryCreator.log
                        .trace("SparqlQueryCreator.matchAndReplaceInputVariablesForQueryType: indexOf matchString="
                                + replacedString.indexOf(matchString));
            }
            
            // in some cases we want to specially encode user input without
            // having said this specifically in the template, this is where it
            // happens, in the order that the list has been constructed in
            for(final String specialInstruction : specialInstructions)
            {
                if(specialInstruction.equals(Settings.INPUT_URL_ENCODED))
                {
                    inputReplaceString = Utilities
                            .percentEncode(inputReplaceString);
                }
                else if(specialInstruction.equals(Settings.INPUT_PLUS_URL_ENCODED))
                {
                    inputReplaceString = Utilities
                            .plusPercentEncode(inputReplaceString);
                }
                else if(specialInstruction.equals(Settings.INPUT_XML_ENCODED))
                {
                    inputReplaceString = Utilities
                            .xmlEncodeString(inputReplaceString);
                }
                else if(specialInstruction
                        .equals(Settings.INPUT_NTRIPLES_ENCODED))
                {
                    inputReplaceString = Utilities
                            .ntriplesEncode(inputReplaceString);
                }
                else if(specialInstruction.equals(Settings.LOWERCASE))
                {
                    inputReplaceString = inputReplaceString.toLowerCase();
                }
                else if(specialInstruction.equals(Settings.UPPERCASE))
                {
                    inputReplaceString = inputReplaceString.toUpperCase();
                }
                else if(specialInstruction.equals(Settings.PRIVATE_LOWERCASE))
                {
                    if(!isPublic)
                    {
                        inputReplaceString = inputReplaceString.toLowerCase();
                    }
                }
                else if(specialInstruction.equals(Settings.PRIVATE_UPPERCASE))
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
            
            // replacedString = replacedString.replace(plusSpaceEncodedMatchString, plusSpaceEncodedReplaceString);
            
            replacedString = replacedString.replace(nTriplesMatchString, nTriplesReplaceString);
            
            replacedString = replacedString.replace(xmlEncodedUrlEncodedMatchString, xmlEncodedUrlEncodedReplaceString);
            replacedString = replacedString.replace(xmlEncodedPlusUrlEncodedMatchString, xmlEncodedPlusUrlEncodedReplaceString);
            
            replacedString = replacedString.replace(xmlEncodedNTriplesMatchString, xmlEncodedNTriplesReplaceString);
            
            /***********************/
            // Start lowercase region
            replacedString = replacedString.replace(lowercaseMatchString,
                    lowercaseReplaceString);
            replacedString = replacedString.replace(
                    urlEncodedLowercaseMatchString,
                    urlEncodedLowercaseReplaceString);
            replacedString = replacedString.replace(
                    xmlEncodedLowercaseMatchString,
                    xmlEncodedLowercaseReplaceString);
            replacedString = replacedString.replace(
                    xmlEncodedUrlEncodedLowercaseMatchString,
                    xmlEncodedUrlEncodedLowercaseReplaceString);
            replacedString = replacedString.replace(
                    xmlEncodedNTriplesLowercaseMatchString,
                    xmlEncodedNTriplesLowercaseReplaceString);
            // End lowercase region
            /***********************/
            
            /***********************/
            // Start uppercase region
            replacedString = replacedString.replace(uppercaseMatchString,
                    uppercaseReplaceString);
            replacedString = replacedString.replace(
                    urlEncodedUppercaseMatchString,
                    urlEncodedUppercaseReplaceString);
            replacedString = replacedString.replace(
                    xmlEncodedUppercaseMatchString,
                    xmlEncodedUppercaseReplaceString);
            replacedString = replacedString.replace(
                    xmlEncodedUrlEncodedUppercaseMatchString,
                    xmlEncodedUrlEncodedUppercaseReplaceString);
            replacedString = replacedString.replace(
                    xmlEncodedNTriplesUppercaseMatchString,
                    xmlEncodedNTriplesUppercaseReplaceString);
            // End uppercase region
            /***********************/
            
            if(SparqlQueryCreator._DEBUG)
            {
                SparqlQueryCreator.log
                        .debug("SparqlQueryCreator.matchAndReplaceInputVariablesForQueryType: replacedString="
                                + replacedString);
                // log.debug("SparqlQueryCreator.matchAndReplaceInputVariablesForQueryType: normalisedStandardUri="+normalisedStandardUri);
                // log.debug("SparqlQueryCreator.matchAndReplaceInputVariablesForQueryType: normalisedQueryUri="+normalisedQueryUri);
            }
        } // end for(;nextMatch < allMatches.size(); nextMatch++)
        
        // lastly put in the actual query string if they want us to do that
        replacedString = replacedString.replace("${queryString}", queryString);
        replacedString = replacedString.replace("${lowercase_queryString}",
                queryString.toLowerCase());
        replacedString = replacedString.replace("${uppercase_queryString}",
                queryString.toUpperCase());
        replacedString = replacedString.replace(
                "${ntriplesEncoded_queryString}", Utilities
                        .ntriplesEncode(queryString));
        replacedString = replacedString.replace("${urlEncoded_queryString}",
                Utilities.percentEncode(queryString));
        replacedString = replacedString.replace("${xmlEncoded_queryString}",
                Utilities.xmlEncodeString(queryString));
        replacedString = replacedString.replace(
                "${xmlEncoded_urlEncoded_queryString}", Utilities
                        .xmlEncodeString(Utilities.percentEncode(queryString)));
        replacedString = replacedString
                .replace("${xmlEncoded_ntriplesEncoded_queryString}", Utilities
                        .xmlEncodeString(Utilities.ntriplesEncode(queryString)));
        
        if(SparqlQueryCreator._DEBUG)
        {
            final long end = System.currentTimeMillis();
            
            SparqlQueryCreator.log
                    .debug(String
                            .format(
                                    "%s: timing=%10d",
                                    "SparqlQueryCreator.matchAndReplaceInputVariablesForQueryType",
                                    (end - start)));
            
            SparqlQueryCreator.log
                    .debug("SparqlQueryCreator.matchAndReplaceInputVariablesForQueryType: returning replacedString="
                            + replacedString);
        }
        
        return replacedString;
    }
    
    /**
     * @param basicRdfXml
     * @param normalisationRules An ordered list of normalisation rules that need to be applied to the input document
     * @param includedProfiles
     * @return
     */
    public static Object normaliseByStage(URI stage, Object input,
            List<NormalisationRule> normalisationRules,
            Collection<Profile> includedProfiles)
    {
        if(SparqlQueryCreator._TRACE)
        {
            SparqlQueryCreator.log
                    .trace("SparqlQueryCreator.normaliseByStage: before applying normalisation rules");
        }
        
        final long start = System.currentTimeMillis();
        
        // go through the rules
        for(final NormalisationRule nextRule : normalisationRules)
        {
            // TODO: eliminate the reliance on the Settings class here
            if(Settings.isRdfRuleUsedWithProfileList(nextRule.getKey(),
                    nextRule.getProfileIncludeExcludeOrder(), includedProfiles))
            {
                if(SparqlQueryCreator._TRACE)
                {
                    SparqlQueryCreator.log
                            .trace("SparqlQueryCreator.normaliseByStage: nextRule.order="
                                    + nextRule.getOrder());
                }
                
                input = nextRule.normaliseByStage(stage, input);
            }
        }
        
        if(_DEBUG)
        {
            final long end = System.currentTimeMillis();
            
            SparqlQueryCreator.log.debug(String.format("%s: timing=%10d",
                    "SparqlQueryCreator.normaliseByStage", (end - start)));
        }
        
        if(SparqlQueryCreator._TRACE)
        {
            SparqlQueryCreator.log
                    .trace("SparqlQueryCreator.normaliseByStage: after applying normalisation rules");
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
    public static String replaceAttributesOnEndpointUrl(
            String replacementString, QueryType queryType,
            Provider nextProvider,
            Map<String, String> attributeList,
            Collection<Profile> includedProfiles)
    {
        final String queryString = attributeList.get("queryString");
        
        if(queryString.trim().equals(""))
        {
            SparqlQueryCreator.log
                    .error("SparqlQueryCreator.replaceAttributesOnEndpointUrl: queryString was empty");
        }
        
        if(replacementString == null)
        {
            SparqlQueryCreator.log
                    .error("SparqlQueryCreator.replaceAttributesOnEndpointUrl: queryType="
                            + queryType.getKey().stringValue());
        }
        
        return SparqlQueryCreator.doReplacementsOnString(queryString,
                replacementString, queryType, null,
                nextProvider.getNormalisationsNeeded(),
                attributeList, includedProfiles);
    }
    
    /**
     * @todo Implement me in order to make the replacements method more efficient
     * @param inputString
     * @param tags
     * @return
     */
    public static String replaceTags(String inputString,
            Map<String, String> tags)
    {
        final Matcher m = Settings.getTagPattern().matcher(inputString);
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
            } while(result);
            
            m.appendTail(sb);
            
            inputString = sb.toString();
        }
        
        return inputString;
    }
    
    public static String testReplaceMethod(String inputString)
    {
        final Map<String, String> myTestHashtable = new TreeMap<String, String>();
        
        myTestHashtable.put("${input_1}", "MyInput1");
        myTestHashtable.put("${inputUrlEncoded_privatelowercase_input_1}",
                "myinput1");
        myTestHashtable.put("${input_2}", "YourInput2");
        myTestHashtable.put("${inputUrlEncoded_privatelowercase_input_2}",
                "yourinput2");
        
        final String returnString = SparqlQueryCreator.replaceTags(inputString,
                myTestHashtable);
        
        // log.warn("SparqlQueryCreator.testReplaceMethod returnString="+returnString);
        
        return returnString;
    }
    
}
