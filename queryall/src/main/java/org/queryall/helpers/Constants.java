/**
 * 
 */
package org.queryall.helpers;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;

/**
 * @author peter
 *
 */
public final class Constants
{

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

    static
    {
        ValueFactory f = new MemValueFactory();
        
        DC_TITLE = f.createURI(Constants.DC_NAMESPACE+"title");        
    }

    public static URI DC_TITLE;
    

}
