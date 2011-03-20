/**
 * 
 */
package org.queryall.helpers;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;

/**
 * @author peter
 *
 */
public final class Constants
{
    public static final ValueFactory valueFactory = new MemValueFactory();
    
    public static final String CURRENT = "current";
    public static final String URL_ENCODED = "urlEncoded";
    public static final String PLUS_URL_ENCODED = "plusUrlEncoded";
    public static final String INPUT_URL_ENCODED = "inputUrlEncoded";
    public static final String INPUT_PLUS_URL_ENCODED = "inputPlusUrlEncoded";
    public static final String XML_ENCODED = "xmlEncoded";
    public static final String INPUT_XML_ENCODED = "inputXmlEncoded";
    public static final String NTRIPLES_ENCODED = "ntriplesEncoded";
    public static final String INPUT_NTRIPLES_ENCODED = "inputNTriplesEncoded";
    public static final String LOWERCASE = "lowercase";
    public static final String UPPERCASE = "uppercase";
    public static final String PRIVATE_LOWERCASE = "privatelowercase";
    public static final String PRIVATE_UPPERCASE = "privateuppercase";
    // These are used for sorting
    public static final int LOWEST_ORDER_FIRST = 1;
    public static final int HIGHEST_ORDER_FIRST = 2;
    public static final String STATISTICS_ITEM_PROFILES = "profiles";
    public static final String STATISTICS_ITEM_SUCCESSFULPROVIDERS = "successfulproviders";
    public static final String STATISTICS_ITEM_ERRORPROVIDERS = "errorproviders";
    public static final String STATISTICS_ITEM_CONFIGLOCATIONS = "configlocations";
    public static final String STATISTICS_ITEM_QUERYTYPES = "querytypes";
    public static final String STATISTICS_ITEM_NAMESPACES = "namespaces";
    public static final String STATISTICS_ITEM_CONFIGVERSION = "configversion";
    public static final String STATISTICS_ITEM_READTIMEOUT = "readtimeout";
    public static final String STATISTICS_ITEM_CONNECTTIMEOUT = "connecttimeout";
    public static final String STATISTICS_ITEM_USERHOSTADDRESS = "userhostaddress";
    public static final String STATISTICS_ITEM_USERAGENT = "useragent";
    public static final String STATISTICS_ITEM_REALHOSTNAME = "realhostname";
    public static final String STATISTICS_ITEM_QUERYSTRING = "querystring";
    public static final String STATISTICS_ITEM_RESPONSETIME = "responsetime";
    public static final String STATISTICS_ITEM_SUMLATENCY = "sumlatency";
    public static final String STATISTICS_ITEM_SUMQUERIES = "sumqueries";
    public static final String STATISTICS_ITEM_STDEVLATENCY = "stdevlatency";
    public static final String STATISTICS_ITEM_SUMERRORS = "sumerrors";
    public static final String STATISTICS_ITEM_SUMERRORLATENCY = "sumerrorlatency";
    public static final String STATISTICS_ITEM_STDEVERRORLATENCY = "stdeverrorlatency";
    public static final String DC_NAMESPACE = "http://purl.org/dc/elements/1.1/";
    public static final String SKOS_NAMESPACE = "http://www.w3.org/2004/02/skos/core#";

    static
    {
        DC_TITLE = valueFactory.createURI(DC_NAMESPACE, "title");
        SKOS_PREFLABEL = valueFactory.createURI(SKOS_NAMESPACE, "prefLabel");
        SKOS_ALTLABEL = valueFactory.createURI(SKOS_NAMESPACE, "altLabel");
    }

    public static URI DC_TITLE;
    public static URI SKOS_PREFLABEL;
    public static URI SKOS_ALTLABEL;

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static final String TIME_ZOME = "UTC";

    public static final String APPLICATION_RDF_XML = "application/rdf+xml";

    public static final String TEXT_RDF_N3 = "text/rdf+n3";

    public static final String TEXT_HTML = "text/html";

    private Constants()
    {
    }

    // Why can't these objects be thread safe???????
    // TODO: find a thread-safe date formatting library and use it instead
    public static SimpleDateFormat ISO8601UTC()
    {
        final SimpleDateFormat result = new SimpleDateFormat(Constants.DATE_FORMAT);
        result.setTimeZone(TimeZone.getTimeZone(Constants.TIME_ZOME));
        return result;
    }
}
