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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrdf.model.URI;
import org.openrdf.rio.ntriples.NTriplesUtil;
import org.queryall.api.QueryAllConfiguration;
import org.queryall.api.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class StringUtils
{
    private static final Logger log = LoggerFactory.getLogger(StringUtils.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = StringUtils.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = StringUtils.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = StringUtils.log.isInfoEnabled();
    
    public static URI createURI(final String stringForm)
    {
        return Constants.valueFactory.createURI(stringForm);
    }
    
    public static List<String> getNamespaceAndIdentifier(final String nsAndId, final QueryAllConfiguration localSettings)
    {
        return StringUtils.matchesForRegexOnString(localSettings.getPlainNamespaceAndIdentifierPattern(),
                localSettings.getStringProperty("plainNamespaceAndIdentifierRegex", ""), nsAndId);
    }
    
    public static List<String> getNamespaceAndIdentifierFromUri(final String nextUri,
            final QueryAllConfiguration localSettings)
    {
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
    
    public static List<String> matchesForRegexOnString(final Pattern nextRegexPattern, final String nextRegex,
            final String nextQueryString)
    {
        final List<String> results = new ArrayList<String>();
        
        if(nextRegex == null || nextRegex.trim().equals(""))
        {
            return results;
        }
        
        if(nextRegexPattern == null)
        {
            throw new RuntimeException("RdfUtils.matchesForRegexOnString: nextRegexPattern was null");
        }
        
        final Matcher matcher = nextRegexPattern.matcher(nextQueryString);
        
        boolean found = false;
        
        while(matcher.find())
        {
            for(int i = 0; i < matcher.groupCount(); i++)
            {
                if(RdfUtils._TRACE)
                {
                    RdfUtils.log.trace("RdfUtils.matchesForRegexOnString: Found the text \"" + matcher.group(i + 1)
                            + "\" starting at " + "index=" + matcher.start(i + 1) + " and ending at index="
                            + matcher.end(i + 1) + ".\n");
                }
                
                results.add(matcher.group(i + 1));
                
                found = true;
            }
        }
        
        if(!found)
        {
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("RdfUtils.matchesForRegexOnString: could not find a match for queryString="
                        + nextQueryString);
            }
        }
        else if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("RdfUtils.matchesForRegexOnString: found " + results.size()
                    + " matches for queryString=" + nextQueryString);
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
            
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("RdfUtils.md5: inputString=" + inputString + " hash=" + hash);
            }
            
            return hash;
        }
        catch(final NoSuchAlgorithmException nsae)
        {
            RdfUtils.log.error("RdfUtils.md5: could not find md5 algorithm");
            
            throw new RuntimeException(nsae);
        }
        catch(final java.io.UnsupportedEncodingException uee)
        {
            RdfUtils.log.error("RdfUtils.md5: invalid JRE, does not support UTF-8");
            
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
            
            if(RdfUtils._TRACE)
            {
                RdfUtils.log.trace("RdfUtils.replaceAll: replacing from " + (bufferPosition + 1) + " to "
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
                if(RdfUtils._TRACE)
                {
                    RdfUtils.log.trace("RdfUtils.replaceMatchesForRegexOnString: nextRegex=" + nextRegex
                            + " Found the text \"" + matcher.group(i + 1) + "\" starting at " + "index="
                            + matcher.start(i + 1) + " and ending at index=" + matcher.end(i + 1) + ".");
                }
                
                // buffer.replace(matcher.start(i+1), matcher.end(i+1), replaceString);
                buffer.replace(matcher.start(i + 1), matcher.end(i + 1), matcher.group(i + 1));
                
                if(RdfUtils._TRACE)
                {
                    RdfUtils.log.trace("Buffer after replacement=" + buffer.toString());
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
