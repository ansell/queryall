/**
 * 
 */
package org.queryall.api.utils;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class Constants
{
    /**
     * A shared value factory to use for creating URIs when there is no repository available.
     */
    public static final ValueFactory VALUE_FACTORY = new MemValueFactory();
    
    /**
     * The constant for the MIME type "application/rdf+xml".
     */
    public static final String APPLICATION_RDF_XML = "application/rdf+xml";
    
    /**
     * The constant for "current".
     */
    public static final String CURRENT = "current";
    
    /**
     * The constant for the Z time date format, aka, UTC.
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    
    /**
     * The constant for the Dublin Core Elements 1.1 base namespace,
     * "http://purl.org/dc/elements/1.1/".
     */
    public static final String DC_NAMESPACE = "http://purl.org/dc/elements/1.1/";
    
    /**
     * The constant for the SKOS namespace, "http://www.w3.org/2004/02/skos/core#".
     */
    public static final String SKOS_NAMESPACE = "http://www.w3.org/2004/02/skos/core#";
    
    /**
     * The constant for the XSD namespace, "http://www.w3.org/2001/XMLSchema#".
     */
    public static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema#";
    
    /**
     * The constant for the template encoding instruction "inputNTriplesEncoded".
     */
    public static final String INPUT_NTRIPLES_ENCODED = "inputNTriplesEncoded";
    
    /**
     * The constant for the template encoding instruction "inputPlusUrlEncoded".
     */
    public static final String INPUT_PLUS_URL_ENCODED = "inputPlusUrlEncoded";
    
    /**
     * The constant for the template encoding instruction "inputUrlEncoded".
     */
    public static final String INPUT_URL_ENCODED = "inputUrlEncoded";
    
    /**
     * The constant for the template encoding instruction "inputXmlEncoded".
     */
    public static final String INPUT_XML_ENCODED = "inputXmlEncoded";
    
    /**
     * The constant for the template encoding instruction "lowercase".
     */
    public static final String LOWERCASE = "lowercase";
    
    /**
     * The constant for the template encoding instruction "ntriplesEncoded".
     */
    public static final String NTRIPLES_ENCODED = "ntriplesEncoded";
    
    /**
     * The constant for the template encoding instruction "plusUrlEncoded".
     */
    public static final String PLUS_URL_ENCODED = "plusUrlEncoded";
    
    /**
     * The constant for the template encoding instruction "privatelowercase".
     */
    public static final String PRIVATE_LOWERCASE = "privatelowercase";
    
    /**
     * The constant for the template encoding instruction "privateuppercase".
     */
    public static final String PRIVATE_UPPERCASE = "privateuppercase";
    
    /**
     * The constant for the Title property from the Dublin Core Elements namespace.
     */
    public static final URI DC_TITLE = Constants.VALUE_FACTORY.createURI(Constants.DC_NAMESPACE, "title");
    
    /**
     * The constant for the Alternate Label property from the SKOS namespace.
     */
    public static final URI SKOS_ALTLABEL = Constants.VALUE_FACTORY.createURI(Constants.SKOS_NAMESPACE, "altLabel");
    
    /**
     * The constant for the Preferred Label property from the SKOS namespace.
     */
    public static final URI SKOS_PREFLABEL = Constants.VALUE_FACTORY.createURI(Constants.SKOS_NAMESPACE, "prefLabel");
    
    /**
     * The constant for the Boolean property from the XML Schema namespace.
     */
    public static final URI XSD_BOOLEAN = Constants.VALUE_FACTORY.createURI(Constants.XSD_NAMESPACE, "boolean");
    
    /**
     * The constant for the Float property from the XML Schema namespace.
     */
    public static final URI XSD_FLOAT = Constants.VALUE_FACTORY.createURI(Constants.XSD_NAMESPACE, "float");
    
    /**
     * The constant for the Int property from the XML Schema namespace.
     */
    public static final URI XSD_INT = Constants.VALUE_FACTORY.createURI(Constants.XSD_NAMESPACE, "int");
    
    /**
     * The constant for the Integer property from the XML Schema namespace.
     */
    public static final URI XSD_INTEGER = Constants.VALUE_FACTORY.createURI(Constants.XSD_NAMESPACE, "integer");
    
    /**
     * The constant for the Long property from the XML Schema namespace.
     */
    public static final URI XSD_LONG = Constants.VALUE_FACTORY.createURI(Constants.XSD_NAMESPACE, "long");
    
    public static final String STATISTICS_ITEM_CONFIGLOCATIONS = "configlocations";
    public static final String STATISTICS_ITEM_CONFIGVERSION = "configversion";
    public static final String STATISTICS_ITEM_CONNECTTIMEOUT = "connecttimeout";
    public static final String STATISTICS_ITEM_ERRORPROVIDERS = "errorproviders";
    public static final String STATISTICS_ITEM_NAMESPACES = "namespaces";
    public static final String STATISTICS_ITEM_PROFILES = "profiles";
    public static final String STATISTICS_ITEM_QUERYSTRING = "querystring";
    public static final String STATISTICS_ITEM_QUERYTYPES = "querytypes";
    public static final String STATISTICS_ITEM_READTIMEOUT = "readtimeout";
    public static final String STATISTICS_ITEM_REALHOSTNAME = "realhostname";
    public static final String STATISTICS_ITEM_RESPONSETIME = "responsetime";
    public static final String STATISTICS_ITEM_STDEVERRORLATENCY = "stdeverrorlatency";
    public static final String STATISTICS_ITEM_STDEVLATENCY = "stdevlatency";
    public static final String STATISTICS_ITEM_SUCCESSFULPROVIDERS = "successfulproviders";
    public static final String STATISTICS_ITEM_SUMERRORLATENCY = "sumerrorlatency";
    public static final String STATISTICS_ITEM_SUMERRORS = "sumerrors";
    public static final String STATISTICS_ITEM_SUMLATENCY = "sumlatency";
    public static final String STATISTICS_ITEM_SUMQUERIES = "sumqueries";
    public static final String STATISTICS_ITEM_USERAGENT = "useragent";
    public static final String STATISTICS_ITEM_USERHOSTADDRESS = "userhostaddress";;
    
    public static final String TEMPLATE_DEFAULT_SEPARATOR = "${defaultSeparator}";
    public static final String TEMPLATE_SEPARATOR = "${separator}";
    public static final String TEMPLATE_AUTHORITY = "${authority}";
    public static final String TEMPLATE_ENDPOINT_SPECIFIC_QUERY_URI = "${endpointSpecificQueryUri}";
    public static final String TEMPLATE_ENDPOINT_SPECIFIC_URI = "${endpointSpecificUri}";
    public static final String TEMPLATE_GRAPH_END = "${graphEnd}";
    public static final String TEMPLATE_GRAPH_START = "${graphStart}";
    public static final String TEMPLATE_INPUT_PLUS_URL_ENCODED_ENDPOINT_SPECIFIC_QUERY_URI =
            "${inputPlusUrlEncoded_endpointSpecificQueryUri}";
    public static final String TEMPLATE_INPUT_PLUS_URL_ENCODED_ENDPOINT_SPECIFIC_URI =
            "${inputPlusUrlEncoded_endpointSpecificUri}";
    public static final String TEMPLATE_INPUT_PLUS_URL_ENCODED_NORMALISED_QUERY_URI =
            "${inputPlusUrlEncoded_normalisedQueryUri}";
    public static final String TEMPLATE_INPUT_PLUS_URL_ENCODED_NORMALISED_STANDARD_URI =
            "${inputPlusUrlEncoded_normalisedStandardUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_ENDPOINT_SPECIFIC_QUERY_URI =
            "${inputUrlEncoded_endpointSpecificQueryUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_ENDPOINT_SPECIFIC_URI =
            "${inputUrlEncoded_endpointSpecificUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_QUERY_URI =
            "${inputUrlEncoded_lowercase_endpointSpecificQueryUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_URI =
            "${inputUrlEncoded_lowercase_endpointSpecificUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_LOWERCASE_NORMALISED_QUERY_URI =
            "${inputUrlEncoded_lowercase_normalisedQueryUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_LOWERCASE_NORMALISED_STANDARD_URI =
            "${inputUrlEncoded_lowercase_normalisedStandardUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_NORMALISED_QUERY_URI =
            "${inputUrlEncoded_normalisedQueryUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_NORMALISED_STANDARD_URI =
            "${inputUrlEncoded_normalisedStandardUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_PRIVATELOWERCASE_ENDPOINT_SPECIFIC_QUERY_URI =
            "${inputUrlEncoded_privatelowercase_endpointSpecificQueryUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_PRIVATELOWERCASE_ENDPOINT_SPECIFIC_URI =
            "${inputUrlEncoded_privatelowercase_endpointSpecificUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_PRIVATELOWERCASE_NORMALISED_QUERY_URI =
            "${inputUrlEncoded_privatelowercase_normalisedQueryUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_PRIVATELOWERCASE_NORMALISED_STANDARD_URI =
            "${inputUrlEncoded_privatelowercase_normalisedStandardUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_PRIVATEUPPERCASE_ENDPOINT_SPECIFIC_QUERY_URI =
            "${inputUrlEncoded_privateuppercase_endpointSpecificQueryUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_PRIVATEUPPERCASE_ENDPOINT_SPECIFIC_URI =
            "${inputUrlEncoded_privateuppercase_endpointSpecificUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_PRIVATEUPPERCASE_NORMALISED_QUERY_URI =
            "${inputUrlEncoded_privateuppercase_normalisedQueryUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_PRIVATEUPPERCASE_NORMALISED_STANDARD_URI =
            "${inputUrlEncoded_privateuppercase_normalisedStandardUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_QUERY_URI =
            "${inputUrlEncoded_uppercase_endpointSpecificQueryUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_URI =
            "${inputUrlEncoded_uppercase_endpointSpecificUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_UPPERCASE_NORMALISED_QUERY_URI =
            "${inputUrlEncoded_uppercase_normalisedQueryUri}";
    public static final String TEMPLATE_INPUT_URL_ENCODED_UPPERCASE_NORMALISED_STANDARD_URI =
            "${inputUrlEncoded_uppercase_normalisedStandardUri}";
    public static final String TEMPLATE_INPUT_XML_ENCODED_ENDPOINT_SPECIFIC_QUERY_URI =
            "${inputXmlEncoded_endpointSpecificQueryUri}";
    public static final String TEMPLATE_INPUT_XML_ENCODED_ENDPOINT_SPECIFIC_URI =
            "${inputXmlEncoded_endpointSpecificUri}";
    public static final String TEMPLATE_INPUT_XML_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_QUERY_URI =
            "${inputXmlEncoded_lowercase_endpointSpecificQueryUri}";
    public static final String TEMPLATE_INPUT_XML_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_URI =
            "${inputXmlEncoded_lowercase_endpointSpecificUri}";
    public static final String TEMPLATE_INPUT_XML_ENCODED_PRIVATELOWERCASE_ENDPOINT_SPECIFIC_QUERY_URI =
            "${inputXmlEncoded_privatelowercase_endpointSpecificQueryUri}";
    public static final String TEMPLATE_INPUT_XML_ENCODED_PRIVATELOWERCASE_ENDPOINT_SPECIFIC_URI =
            "${inputXmlEncoded_privatelowercase_endpointSpecificUri}";
    public static final String TEMPLATE_INPUT_XML_ENCODED_PRIVATEUPPERCASE_ENDPOINT_SPECIFIC_QUERY_URI =
            "${inputXmlEncoded_privateuppercase_endpointSpecificQueryUri}";
    public static final String TEMPLATE_INPUT_XML_ENCODED_PRIVATEUPPERCASE_ENDPOINT_SPECIFIC_URI =
            "${inputXmlEncoded_privateuppercase_endpointSpecificUri}";
    public static final String TEMPLATE_INPUT_XML_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_QUERY_URI =
            "${inputXmlEncoded_uppercase_endpointSpecificQueryUri}";
    public static final String TEMPLATE_INPUT_XML_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_URI =
            "${inputXmlEncoded_uppercase_endpointSpecificUri}";
    
    public static final String TEMPLATE_KEY_DEFAULT_HOST_ADDRESS = "defaultHostAddress";
    public static final String TEMPLATE_KEY_DEFAULT_HOST_NAME = "defaultHostName";
    public static final String TEMPLATE_KEY_DEFAULT_SEPARATOR = "defaultSeparator";
    public static final String TEMPLATE_KEY_ENDPOINT_URL = "endpointUrl";
    public static final String TEMPLATE_KEY_GRAPH_URI = "graphUri";
    public static final String TEMPLATE_KEY_INCLUDED_QUERY_TYPE = "includedQueryType";
    public static final String TEMPLATE_KEY_OFFSET = "offset";
    public static final String TEMPLATE_KEY_QUERY_STRING = "queryString";
    public static final String TEMPLATE_KEY_REAL_HOST_NAME = "realHostName";
    public static final String TEMPLATE_KEY_URL_ENCODED_DEFAULT_HOST_ADDRESS = "urlEncoded_defaultHostAddress";
    public static final String TEMPLATE_KEY_URL_ENCODED_DEFAULT_HOST_NAME = "urlEncoded_defaultHostName";
    public static final String TEMPLATE_KEY_URL_ENCODED_DEFAULT_SEPARATOR = "urlEncoded_defaultSeparator";
    public static final String TEMPLATE_KEY_URL_ENCODED_ENDPOINT_URL = "urlEncoded_endpointUrl";
    public static final String TEMPLATE_KEY_URL_ENCODED_GRAPH_URI = "urlEncoded_graphUri";
    public static final String TEMPLATE_KEY_URL_ENCODED_QUERY_STRING = "urlEncoded_queryString";
    public static final String TEMPLATE_KEY_URL_ENCODED_REAL_HOST_NAME = "urlEncoded_realHostName";
    public static final String TEMPLATE_KEY_USE_SPARQL_GRAPH = "useSparqlGraph";
    public static final String TEMPLATE_KEY_XML_ENCODED_DEFAULT_HOST_ADDRESS = "xmlEncoded_defaultHostAddress";
    public static final String TEMPLATE_KEY_XML_ENCODED_DEFAULT_HOST_NAME = "xmlEncoded_defaultHostName";
    public static final String TEMPLATE_KEY_XML_ENCODED_DEFAULT_SEPARATOR = "xmlEncoded_defaultSeparator";
    public static final String TEMPLATE_KEY_XML_ENCODED_ENDPOINT_URL = "xmlEncoded_endpointUrl";
    public static final String TEMPLATE_KEY_XML_ENCODED_GRAPH_URI = "xmlEncoded_graphUri";
    public static final String TEMPLATE_KEY_XML_ENCODED_QUERY_STRING = "xmlEncoded_queryString";
    public static final String TEMPLATE_KEY_XML_ENCODED_REAL_HOST_NAME = "xmlEncoded_realHostName";
    public static final String TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_DEFAULT_HOST_ADDRESS =
            "xmlEncoded_urlEncoded_defaultHostAddress";
    public static final String TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_DEFAULT_HOST_NAME =
            "xmlEncoded_urlEncoded_defaultHostName";
    public static final String TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_DEFAULT_SEPARATOR =
            "xmlEncoded_urlEncoded_defaultSeparator";
    public static final String TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_ENDPOINT_URL = "xmlEncoded_urlEncoded_endpointUrl";
    public static final String TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_GRAPH_URI = "xmlEncoded_urlEncoded_graphUri";
    public static final String TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_QUERY_STRING = "xmlEncoded_urlEncoded_queryString";
    public static final String TEMPLATE_KEY_XML_ENCODED_URL_ENCODED_REAL_HOST_NAME =
            "xmlEncoded_urlEncoded_realHostName";
    
    public static final String TEMPLATE_LIMIT = "${limit}";
    public static final String TEMPLATE_LOWERCASE_QUERY_STRING = "${lowercase_queryString}";
    public static final String TEMPLATE_NORMALISED_QUERY_URI = "${normalisedQueryUri}";
    public static final String TEMPLATE_NORMALISED_STANDARD_URI = "${normalisedStandardUri}";
    public static final String TEMPLATE_NTRIPLES_ENCODED_ENDPOINT_SPECIFIC_URI =
            "${ntriplesEncoded_endpointSpecificUri}";
    public static final String TEMPLATE_NTRIPLES_ENCODED_NORMALISED_QUERY_URI = "${ntriplesEncoded_normalisedQueryUri}";
    public static final String TEMPLATE_NTRIPLES_ENCODED_NORMALISED_STANDARD_URI =
            "${ntriplesEncoded_normalisedStandardUri}";
    public static final String TEMPLATE_NTRIPLES_ENCODED_QUERY_STRING = "${ntriplesEncoded_queryString}";
    public static final String TEMPLATE_OFFSET = "${offset}";
    public static final String TEMPLATE_PAGEOFFSET = "${pageoffset}";
    public static final String TEMPLATE_PERCENT_ENCODED_ENDPOINT_QUERY = "${percentEncoded_endpointQuery}";
    public static final String TEMPLATE_PLUS_URL_ENCODED_NORMALISED_STANDARD_URI =
            "${plusUrlEncoded_normalisedStandardUri}";
    public static final String TEMPLATE_QUICK_LIMIT = "${quicklimit}";
    public static final String TEMPLATE_QUERY_STRING = "${queryString}";
    public static final String TEMPLATE_REAL_HOST_NAME = "${realHostName}";
    public static final String TEMPLATE_SPARQL_OFFSET = "${sparqlOffset}";
    public static final String TEMPLATE_UPPERCASE_QUERY_STRING = "${uppercase_queryString}";
    public static final String TEMPLATE_URL_ENCODED_ENDPOINT_SPECIFIC_QUERY_URI =
            "${urlEncoded_endpointSpecificQueryUri}";
    public static final String TEMPLATE_URL_ENCODED_ENDPOINT_SPECIFIC_URI = "${urlEncoded_endpointSpecificUri}";
    public static final String TEMPLATE_URL_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_QUERY_URI =
            "${urlEncoded_lowercase_endpointSpecificQueryUri}";
    public static final String TEMPLATE_URL_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_URI =
            "${urlEncoded_lowercase_endpointSpecificUri}";
    public static final String TEMPLATE_URL_ENCODED_LOWERCASE_NORMALISED_QUERY_URI =
            "${urlEncoded_lowercase_normalisedQueryUri}";
    public static final String TEMPLATE_URL_ENCODED_LOWERCASE_NORMALISED_STANDARD_URI =
            "${urlEncoded_lowercase_normalisedStandardUri}";
    public static final String TEMPLATE_URL_ENCODED_NORMALISED_QUERY_URI = "${urlEncoded_normalisedQueryUri}";
    public static final String TEMPLATE_URL_ENCODED_NORMALISED_STANDARD_URI = "${urlEncoded_normalisedStandardUri}";
    public static final String TEMPLATE_URL_ENCODED_QUERY_STRING = "${urlEncoded_queryString}";
    public static final String TEMPLATE_URL_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_QUERY_URI =
            "${urlEncoded_uppercase_endpointSpecificQueryUri}";
    public static final String TEMPLATE_URL_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_URI =
            "${urlEncoded_uppercase_endpointSpecificUri}";
    public static final String TEMPLATE_URL_ENCODED_UPPERCASE_NORMALISED_QUERY_URI =
            "${urlEncoded_uppercase_normalisedQueryUri}";
    public static final String TEMPLATE_URL_ENCODED_UPPERCASE_NORMALISED_STANDARD_URI =
            "${urlEncoded_uppercase_normalisedStandardUri}";
    public static final String TEMPLATE_XML_ENCODED_ENDPOINT_SPECIFIC_QUERY_URI =
            "${xmlEncoded_endpointSpecificQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_ENDPOINT_SPECIFIC_URI = "${xmlEncoded_endpointSpecificUri}";
    public static final String TEMPLATE_XML_ENCODED_INCLUDED_QUERY_TYPE = "${xmlEncoded_includedQueryType}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_PLUS_URL_ENCODED_ENDPOINT_SPECIFIC_QUERY_URI =
            "${xmlEncoded_inputPlusUrlEncoded_endpointSpecificQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_PLUS_URL_ENCODED_ENDPOINT_SPECIFIC_URI =
            "${xmlEncoded_inputPlusUrlEncoded_endpointSpecificUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_PLUS_URL_ENCODED_NORMALISED_QUERY_URI =
            "${xmlEncoded_inputPlusUrlEncoded_normalisedQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_PLUS_URL_ENCODED_NORMALISED_STANDARD_URI =
            "${xmlEncoded_inputPlusUrlEncoded_normalisedStandardUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_ENDPOINT_SPECIFIC_QUERY_URI =
            "${xmlEncoded_inputUrlEncoded_endpointSpecificQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_ENDPOINT_SPECIFIC_URI =
            "${xmlEncoded_inputUrlEncoded_endpointSpecificUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_QUERY_URI =
            "${xmlEncoded_inputUrlEncoded_lowercase_endpointSpecificQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_URI =
            "${xmlEncoded_inputUrlEncoded_lowercase_endpointSpecificUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_LOWERCASE_NORMALISED_QUERY_URI =
            "${xmlEncoded_inputUrlEncoded_lowercase_normalisedQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_LOWERCASE_NORMALISED_STANDARD_URI =
            "${xmlEncoded_inputUrlEncoded_lowercase_normalisedStandardUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_NORMALISED_QUERY_URI =
            "${xmlEncoded_inputUrlEncoded_normalisedQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_NORMALISED_STANDARD_URI =
            "${xmlEncoded_inputUrlEncoded_normalisedStandardUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_PRIVATELOWERCASE_ENDPOINT_SPECIFIC_QUERY_URI =
            "${xmlEncoded_inputUrlEncoded_privatelowercase_endpointSpecificQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_PRIVATELOWERCASE_ENDPOINT_SPECIFIC_URI =
            "${xmlEncoded_inputUrlEncoded_privatelowercase_endpointSpecificUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_PRIVATELOWERCASE_NORMALISED_QUERY_URI =
            "${xmlEncoded_inputUrlEncoded_privatelowercase_normalisedQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_PRIVATELOWERCASE_NORMALISED_STANDARD_URI =
            "${xmlEncoded_inputUrlEncoded_privatelowercase_normalisedStandardUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_PRIVATEUPPERCASE_ENDPOINT_SPECIFIC_QUERY_URI =
            "${xmlEncoded_inputUrlEncoded_privateuppercase_endpointSpecificQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_PRIVATEUPPERCASE_ENDPOINT_SPECIFIC_URI =
            "${xmlEncoded_inputUrlEncoded_privateuppercase_endpointSpecificUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_PRIVATEUPPERCASE_NORMALISED_QUERY_URI =
            "${xmlEncoded_inputUrlEncoded_privateuppercase_normalisedQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_PRIVATEUPPERCASE_NORMALISED_STANDARD_URI =
            "${xmlEncoded_inputUrlEncoded_privateuppercase_normalisedStandardUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_QUERY_URI =
            "${xmlEncoded_inputUrlEncoded_uppercase_endpointSpecificQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_URI =
            "${xmlEncoded_inputUrlEncoded_uppercase_endpointSpecificUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_UPPERCASE_NORMALISED_QUERY_URI =
            "${xmlEncoded_inputUrlEncoded_uppercase_normalisedQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_INPUT_URL_ENCODED_UPPERCASE_NORMALISED_STANDARD_URI =
            "${xmlEncoded_inputUrlEncoded_uppercase_normalisedStandardUri}";
    public static final String TEMPLATE_XML_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_QUERY_URI =
            "${xmlEncoded_lowercase_endpointSpecificQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_LOWERCASE_ENDPOINT_SPECIFIC_URI =
            "${xmlEncoded_lowercase_endpointSpecificUri}";
    public static final String TEMPLATE_XML_ENCODED_LOWERCASE_NORMALISED_QUERY_URI =
            "${xmlEncoded_lowercase_normalisedQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_LOWERCASE_NORMALISED_STANDARD_URI =
            "${xmlEncoded_lowercase_normalisedStandardUri}";
    public static final String TEMPLATE_XML_ENCODED_NORMALISED_QUERY_URI = "${xmlEncoded_normalisedQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_NORMALISED_STANDARD_URI = "${xmlEncoded_normalisedStandardUri}";
    public static final String TEMPLATE_XML_ENCODED_NTRIPLES_ENCODED_ENDPOINT_SPECIFIC_URI =
            "${xmlEncoded_ntriplesEncoded_endpointSpecificUri}";
    public static final String TEMPLATE_XML_ENCODED_NTRIPLES_ENCODED_NORMALISED_QUERY_URI =
            "${xmlEncoded_ntriplesEncoded_normalisedQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_NTRIPLES_ENCODED_NORMALISED_STANDARD_URI =
            "${xmlEncoded_ntriplesEncoded_normalisedStandardUri}";
    public static final String TEMPLATE_XML_ENCODED_NTRIPLES_ENCODED_QUERY_STRING =
            "${xmlEncoded_ntriplesEncoded_queryString}";
    public static final String TEMPLATE_XML_ENCODED_QUERY_STRING = "${xmlEncoded_queryString}";
    public static final String TEMPLATE_XML_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_QUERY_URI =
            "${xmlEncoded_uppercase_endpointSpecificQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_UPPERCASE_ENDPOINT_SPECIFIC_URI =
            "${xmlEncoded_uppercase_endpointSpecificUri}";
    public static final String TEMPLATE_XML_ENCODED_UPPERCASE_NORMALISED_QUERY_URI =
            "${xmlEncoded_uppercase_normalisedQueryUri}";
    public static final String TEMPLATE_XML_ENCODED_UPPERCASE_NORMALISED_STANDARD_URI =
            "${xmlEncoded_uppercase_normalisedStandardUri}";
    public static final String TEMPLATE_XML_ENCODED_URL_ENCODED_QUERY_STRING = "${xmlEncoded_urlEncoded_queryString}";
    
    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_RDF_N3 = "text/rdf+n3";
    
    public static final String TIME_ZOME = "UTC";
    
    public static final String UPPERCASE = "uppercase";
    public static final String URL_ENCODED = "urlEncoded";
    public static final String XML_ENCODED = "xmlEncoded";
    
    public static final String TEXT_TURTLE = "text/turtle";
    
    public static final String INCLUDE_NON_PROFILE_MATCHED_QUERIES = "includeNonProfileMatchedQueries";
    
    public static final String RECOGNISE_IMPLICIT_QUERY_INCLUSIONS = "recogniseImplicitQueryInclusions";
    
    public static final String APPLICATION_JSON = "application/json";
    
    public static final String APPLICATION_RDF_JSON = "application/rdf+json";
    
    public static final String APPLICATION_X_TRIG = "application/x-trig";
    
    public static final String PREFERRED_DISPLAY_CONTENT_TYPE = "preferredDisplayContentType";
    
    public static final String TEXT_PLAIN = "text/plain";
    
    public static final String TEXT_X_NQUADS = "text/x-nquads";
    
    public static final String QUERY = "queryString";
    
    public static final String APPLICATION_LD_JSON = "application/ld+json";
    
    public static final String APPLICATION_TRIX = "application/trix";
    
    public static final String APPLICATION_XHTML_XML = "application/xhtml+xml";
    
    public static final String CLOSING_BRACE = "}";
    
    public static final List<String> EMPTY_STRING_LIST = Collections.emptyList();
    
    public static final String COIN_BASE_URI = "http://purl.org/court/def/2009/coin#";
    
    /**
     * 
     * @return A new SimpleDataFormat object that formats dates using the Z timezone and the ISO8601
     *         conventions.
     */
    public static SimpleDateFormat ISO8601UTC()
    {
        final SimpleDateFormat result = new SimpleDateFormat(Constants.DATE_FORMAT);
        result.setTimeZone(TimeZone.getTimeZone(Constants.TIME_ZOME));
        return result;
    }
    
    private Constants()
    {
    }
}
