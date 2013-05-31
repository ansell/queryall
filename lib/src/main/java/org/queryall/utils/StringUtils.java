/**
 * 
 */
package org.queryall.utils;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrdf.model.URI;
import org.openrdf.rio.ntriples.NTriplesUtil;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.WebappConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class StringUtils
{
    private static final Logger log = LoggerFactory.getLogger(StringUtils.class);
    private static final boolean TRACE = StringUtils.log.isTraceEnabled();
    private static final boolean DEBUG = StringUtils.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = StringUtils.log.isInfoEnabled();
    
    public static URI createURI(final String stringForm)
    {
        return Constants.VALUE_FACTORY.createURI(stringForm);
    }
    
    public static Map<String, List<String>> getNamespaceAndIdentifier(final String nsAndId,
            final QueryAllConfiguration localSettings)
    {
        return StringUtils.matchesForRegexOnString(localSettings.getPlainNamespaceAndIdentifierPattern(),
                localSettings.getStringProperty(WebappConfig.PLAIN_NAMESPACE_AND_IDENTIFIER_REGEX), nsAndId);
    }
    
    @SuppressWarnings("unused")
    private static Map<String, List<String>> getNamespaceAndIdentifierFromUri(final String nextUri,
            final QueryAllConfiguration localSettings)
    {
        // FIXME: Should be attempting to match against the template in a namespace entry
        if(nextUri.startsWith(localSettings.getDefaultHostAddress()))
        {
            return StringUtils.getNamespaceAndIdentifier(
                    nextUri.substring(localSettings.getDefaultHostAddress().length()), localSettings);
        }
        
        return null;
    }
    
    public static boolean isPlainNamespace(final String queryString, final QueryAllConfiguration localSettings)
    {
        return localSettings.getPlainNamespacePattern().matcher(queryString).find();
    }
    
    public static boolean isPlainNamespaceAndIdentifier(final String queryString,
            final QueryAllConfiguration localSettings)
    {
        return localSettings.getPlainNamespaceAndIdentifierPattern().matcher(queryString).find();
    }
    
    /**
     * @param stringsToJoin
     * @param insertedCharacters
     * @return
     */
    public static String joinStringCollection(final Collection<String> stringsToJoin, final String insertedCharacters)
    {
        final StringBuilder buffer =
                StringUtils.joinStringCollectionHelper(stringsToJoin, insertedCharacters, new StringBuilder());
        
        return buffer.toString();
    }
    
    /**
     * @param stringsToJoin
     * @param insertedCharacters
     * @param buffer
     * @return
     */
    public static StringBuilder joinStringCollectionHelper(final Collection<String> stringsToJoin,
            final String insertedCharacters, final StringBuilder buffer)
    {
        boolean first = true;
        
        for(final String nextJoinString : stringsToJoin)
        {
            if(!first)
            {
                buffer.append(insertedCharacters);
            }
            
            buffer.append(nextJoinString);
            
            first = false;
        }
        
        return buffer;
    }
    
    public static Map<String, List<String>> matchesForRegexOnString(final Pattern nextRegexPattern,
            final String nextRegex, final String nextQueryString)
    {
        final Map<String, List<String>> results = new ConcurrentHashMap<String, List<String>>();
        
        if(nextRegex == null || nextRegex.trim().equals(""))
        {
            return results;
        }
        
        if(nextRegexPattern == null)
        {
            throw new RuntimeException("matchesForRegexOnString: nextRegexPattern was null");
        }
        
        final Matcher matcher = nextRegexPattern.matcher(nextQueryString);
        
        boolean found = false;
        
        while(matcher.find())
        {
            for(int i = 0; i < matcher.groupCount(); i++)
            {
                if(StringUtils.TRACE)
                {
                    StringUtils.log.trace("matchesForRegexOnString: Found the text \"" + matcher.group(i + 1)
                            + "\" starting at " + "index=" + matcher.start(i + 1) + " and ending at index="
                            + matcher.end(i + 1) + ".\n");
                }
                final List<String> matches = new ArrayList<String>(1);
                matches.add(matcher.group(i + 1));
                
                // TODO: is there any way we can make this cleaner?
                // input_NN here is the convention that was used for the regex matches before the
                // new string based method, so it is used here to provide backwards compatibility
                results.put("input_" + (i + 1), matches);
                
                found = true;
            }
        }
        
        if(!found)
        {
            if(StringUtils.DEBUG)
            {
                StringUtils.log.debug("matchesForRegexOnString: could not find a match for queryString="
                        + nextQueryString);
            }
        }
        else if(StringUtils.DEBUG)
        {
            StringUtils.log.debug("matchesForRegexOnString: found " + results.size() + " matches for queryString="
                    + nextQueryString);
        }
        
        return results;
    }
    
    public static boolean matchesRegexOnString(final Pattern nextRegexPattern, final String nextRegex,
            final String nextQueryString)
    {
        if(nextRegex == null || nextRegex.trim().equals(""))
        {
            return false;
        }
        
        return nextRegexPattern.matcher(nextQueryString).matches();
    }
    
    public static String md5(final String inputString)
    {
        try
        {
            final MessageDigest md5 = MessageDigest.getInstance("MD5");
            
            final byte[] messageDigest = md5.digest(inputString.getBytes("UTF-8"));
            final BigInteger bigint = new BigInteger(1, messageDigest);
            
            String hash = bigint.toString(16);
            
            while(hash.length() < 32)
            {
                hash = "0" + hash;
            }
            
            if(StringUtils.DEBUG)
            {
                StringUtils.log.debug("RdfUtils.md5: inputString=" + inputString + " hash=" + hash);
            }
            
            return hash;
        }
        catch(final NoSuchAlgorithmException nsae)
        {
            StringUtils.log.error("RdfUtils.md5: could not find md5 algorithm");
            
            throw new RuntimeException(nsae);
        }
        catch(final java.io.UnsupportedEncodingException uee)
        {
            StringUtils.log.error("RdfUtils.md5: invalid JRE, does not support UTF-8");
            
            throw new RuntimeException(uee);
        }
    }
    
    /**
     * @param stringToEncode
     * @return
     */
    public static String ntriplesEncode(final String stringToEncode)
    {
        final String resultString = NTriplesUtil.escapeString(stringToEncode);
        
        return resultString;
    }
    
    /**
     * @param stringToEncode
     * @return
     */
    public static String percentEncode(final String stringToEncode)
    {
        String result = "";
        
        try
        {
            result = URLEncoder.encode(stringToEncode, "utf-8").replace("+", "%20");
        }
        catch(final java.io.UnsupportedEncodingException uee)
        {
            StringUtils.log.error("RdfUtils.percentEncode: unable to find utf-8 encoder!");
        }
        
        return result;
    }
    
    /**
     * @param stringToEncode
     * @return
     */
    public static String plusPercentEncode(final String stringToEncode)
    {
        String result = "";
        
        try
        {
            result = URLEncoder.encode(stringToEncode, "utf-8").replace("%2F", "/");
        }
        catch(final java.io.UnsupportedEncodingException uee)
        {
            StringUtils.log.error("RdfUtils.percentEncode: unable to find utf-8 encoder!");
        }
        
        return result;
    }
    
    /**
     * @param stringToEncode
     * @return
     */
    public static String plusSpaceEncode(final String stringToEncode)
    {
        final String result = stringToEncode.replace(" ", "+");
        
        // log.info("RdfUtils.plusSpaceEncode: stringToEncode="+stringToEncode+" result="+result);
        
        return result;
    }
    
    public static StringBuilder replaceAll(final StringBuilder buffer, final String searchString,
            final String replacement)
    {
        int bufferPosition = buffer.length() - 1;
        final int offset = searchString.length();
        
        while(bufferPosition >= 0)
        {
            int searchIndex = offset - 1;
            
            while(searchIndex >= 0)
            {
                if(bufferPosition < 0)
                {
                    return buffer;
                }
                
                if(buffer.charAt(bufferPosition) == searchString.charAt(searchIndex))
                {
                    searchIndex--;
                    bufferPosition--;
                }
                else
                {
                    searchIndex = offset - 1;
                    bufferPosition--;
                    
                    if(bufferPosition < 0)
                    {
                        return buffer;
                    }
                    
                    continue;
                }
            }
            
            if(StringUtils.TRACE)
            {
                StringUtils.log.trace("RdfUtils.replaceAll: replacing from " + (bufferPosition + 1) + " to "
                        + (bufferPosition + 1 + offset) + " with (" + replacement + ")");
            }
            
            buffer.replace(bufferPosition + 1, bufferPosition + 1 + offset, replacement);
        }
        
        return buffer;
    }
    
    public static void replaceMatchesForRegexOnString(final Pattern nextRegexPattern, final String nextRegex,
            final StringBuilder nextQueryString, final StringBuilder replaceStringBuilder)
    {
        if(nextRegex == null || nextRegex.trim().equals(""))
        {
            // return nextQueryString;
            return;
        }
        
        if(nextRegexPattern == null)
        {
            throw new RuntimeException("RdfUtils.replaceMatchesForRegexOnString: nextRegexPattern was null");
        }
        
        final Matcher matcher = nextRegexPattern.matcher(nextQueryString);
        
        StringBuilder buffer = nextQueryString;
        
        while(matcher.find())
        {
            // Only do these if a match was found
            buffer = new StringBuilder(nextQueryString);
            
            for(int i = 0; i < matcher.groupCount(); i++)
            {
                if(StringUtils.TRACE)
                {
                    StringUtils.log.trace("RdfUtils.replaceMatchesForRegexOnString: nextRegex=" + nextRegex
                            + " Found the text \"" + matcher.group(i + 1) + "\" starting at " + "index="
                            + matcher.start(i + 1) + " and ending at index=" + matcher.end(i + 1) + ".");
                }
                
                // buffer.replace(matcher.start(i+1), matcher.end(i+1), replaceString);
                buffer.replace(matcher.start(i + 1), matcher.end(i + 1), matcher.group(i + 1));
                
                if(StringUtils.TRACE)
                {
                    StringUtils.log.trace("Buffer after replacement=" + buffer.toString());
                }
                // results.add(matcher.group(i+1));
                
                // found = true;
            }
        }
        
        // return buffer;
    }
    
    /**
     * @param inputString
     * @return
     */
    public static String xmlEncodeString(final String inputString)
    {
        final StringBuilder encodedString = new StringBuilder();
        final StringCharacterIterator characters = new StringCharacterIterator(inputString);
        
        char nextCharacter = characters.current();
        
        while(nextCharacter != CharacterIterator.DONE)
        {
            if(nextCharacter == '<')
            {
                encodedString.append("&lt;");
            }
            else if(nextCharacter == '>')
            {
                encodedString.append("&gt;");
            }
            else if(nextCharacter == '&')
            {
                encodedString.append("&amp;");
            }
            else if(nextCharacter == '\'')
            {
                encodedString.append("&#039;");
            }
            else if(nextCharacter == '\"')
            {
                encodedString.append("&quot;");
            }
            else
            {
                encodedString.append(nextCharacter);
            }
            
            nextCharacter = characters.next();
        }
        
        return encodedString.toString();
    }
    
}
