/**
 * 
 */
package org.queryall.helpers;

import java.io.UnsupportedEncodingException;
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

import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.rio.ntriples.NTriplesUtil;
import org.queryall.Template;

/**
 * @author peter
 *
 */
public class StringUtils 
{
    private static final Logger log = Logger.getLogger(StringUtils.class
            .getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    private static final boolean _INFO = log.isInfoEnabled();
    

    /**
     * @param stringToEncode
     * @return
     */
    public static String percentEncode(String stringToEncode)
    {
        String result = "";
        
        try
        {
            result = URLEncoder.encode(stringToEncode, "utf-8").replace("+",
                    "%20");
        }
        catch (final java.io.UnsupportedEncodingException uee)
        {
            log.fatal("RdfUtils.percentEncode: unable to find utf-8 encoder!");
        }
        
        return result;
    }

    /**
     * @param stringToEncode
     * @return
     */
    public static String plusPercentEncode(String stringToEncode)
    {
        String result = "";
        
        try
        {
            result = URLEncoder.encode(stringToEncode, "utf-8").replace("%2F","/");
        }
        catch (final java.io.UnsupportedEncodingException uee)
        {
            log.fatal("RdfUtils.percentEncode: unable to find utf-8 encoder!");
        }
        
        return result;
    }

    /**
     * @param stringToEncode
     * @return
     */
    public static String plusSpaceEncode(String stringToEncode)
    {
        String result = stringToEncode.replace(" ", "+");
        
        // log.info("RdfUtils.plusSpaceEncode: stringToEncode="+stringToEncode+" result="+result);
        
        return result;
    }

    /**
     * @param stringToEncode
     * @return
     */
    public static String ntriplesEncode(String stringToEncode)
    {
        String resultString = NTriplesUtil.escapeString(stringToEncode);
        
        return resultString;
    }

    /**
     * @param stringsToJoin
     * @param insertedCharacters
     * @param buffer
     * @return
     */
    public static StringBuilder joinStringCollectionHelper(
            Collection<String> stringsToJoin, String insertedCharacters,
            StringBuilder buffer)
    {
        boolean first = true;
        
        for(String nextJoinString : stringsToJoin)
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

    /**
     * @param stringsToJoin
     * @param insertedCharacters
     * @return
     */
    public static String joinStringCollection(Collection<String> stringsToJoin,
            String insertedCharacters)
    {
        final StringBuilder buffer = joinStringCollectionHelper(
                stringsToJoin, insertedCharacters, new StringBuilder());
        
        return buffer.toString();
    }

    /**
     * @param inputString
     * @return
     */
    public static String xmlEncodeString(String inputString)
    {
        final StringBuilder encodedString = new StringBuilder();
        final StringCharacterIterator characters = new StringCharacterIterator(
                inputString);
        
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

    public static List<String> matchesForRegexOnString(Pattern nextRegexPattern, String nextRegex, String nextQueryString)
    {
        List<String> results = new ArrayList<String>();
        
        if(nextRegex == null || nextRegex.trim().equals(""))
        {
            return results;
        }
        
        if(nextRegexPattern == null)
            throw new RuntimeException("RdfUtils.matchesForRegexOnString: nextRegexPattern was null");
            
        Matcher matcher = nextRegexPattern.matcher(nextQueryString);
        
        boolean found = false;
        
        while(matcher.find())
        {
            for(int i = 0; i < matcher.groupCount(); i++)
            {
                if(RdfUtils._TRACE)
                {
                    RdfUtils.log.trace("RdfUtils.matchesForRegexOnString: Found the text \""+matcher.group(i+1)+"\" starting at " +
                              "index="+matcher.start(i+1)+" and ending at index="+matcher.end(i+1)+".\n");
                }
                
                results.add(matcher.group(i+1));
                
                found = true;
            }
        }
        
        if(!found)
        {
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("RdfUtils.matchesForRegexOnString: could not find a match for queryString=" + nextQueryString);
            }
        }
        else if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("RdfUtils.matchesForRegexOnString: found " + results.size() + " matches for queryString=" + nextQueryString);
        }
        
        return results;
    }

    public static StringBuilder applyNativeFunctionTemplate(Template nativeFunction, StringBuilder result)
    {
        if(!nativeFunction.isNativeFunction())
        {
            RdfUtils.log.error("RdfUtils.applyNativeFunctionTemplate: template was not a native function");
        }
        
        if(nativeFunction.getNativeFunctionUri().equals("http://purl.org/queryall/template:xmlencoding"))
        {
            result = new StringBuilder(xmlEncodeString(result.toString()));
        }
        
        return result;
    }

    public static void replaceMatchesForRegexOnString(
        Pattern nextRegexPattern, String nextRegex, 
        StringBuilder nextQueryString, StringBuilder replaceStringBuilder)
    {
        if(nextRegex == null || nextRegex.trim().equals(""))
        {
            // return nextQueryString;
            return;
        }
        
        if(nextRegexPattern == null)
            throw new RuntimeException("RdfUtils.replaceMatchesForRegexOnString: nextRegexPattern was null");
        
        Matcher matcher = nextRegexPattern.matcher(nextQueryString);
        
        StringBuilder buffer = nextQueryString;
        
        while(matcher.find())
        {
            // Only do these if a match was found
            buffer = new StringBuilder(nextQueryString);
            
            for(int i = 0; i < matcher.groupCount(); i++)
            {
                if(RdfUtils._DEBUG)
                {
                    RdfUtils.log.debug("RdfUtils.replaceMatchesForRegexOnString: nextRegex="+nextRegex+" Found the text \""+matcher.group(i+1)+"\" starting at " +
                              "index="+matcher.start(i+1)+" and ending at index="+matcher.end(i+1)+".");
                }
                
                // buffer.replace(matcher.start(i+1), matcher.end(i+1), replaceString);
                buffer.replace(matcher.start(i+1), matcher.end(i+1), matcher.group(i+1));
                
                if(RdfUtils._INFO)
                {
                    RdfUtils.log.info("Buffer after replacement="+buffer.toString());
                }
                //results.add(matcher.group(i+1));
                
                // found = true;
            }
        }
        
        //return buffer;
    }

    public static StringBuilder replaceAll(StringBuilder buffer, String searchString, String replacement)
    {
        int bufferPosition = buffer.length()-1;
        int offset = searchString.length();
        
        while(bufferPosition >= 0)
        { 
            int searchIndex = offset-1;
            
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
                    searchIndex = offset-1;
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
                RdfUtils.log.trace( "RdfUtils.replaceAll: replacing from " + (bufferPosition + 1) + " to " + (bufferPosition + 1 + offset ) + " with (" + replacement + ")" );
            }
            
            buffer.replace(bufferPosition+1, bufferPosition+1+offset, replacement);
        }
        
        return buffer;
    }

    public static URI createURI(String stringForm)
    {
        return RdfUtils.myValueFactory.createURI(stringForm);
    }

    public static String md5(String inputString) 
    {
        try 
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
    
            byte[] messageDigest = md5.digest(inputString.getBytes("UTF-8"));
            BigInteger bigint = new BigInteger(1, messageDigest);
            
            String hash = bigint.toString(16);
    
            while (hash.length() < 32) 
            {
                hash = "0" + hash;
            }
            
            if(RdfUtils._DEBUG)
            	RdfUtils.log.debug("RdfUtils.md5: inputString="+inputString+ " hash="+hash);
            
            return hash;
        }
        catch (NoSuchAlgorithmException nsae) 
        {
            RdfUtils.log.fatal("RdfUtils.md5: could not find md5 algorithm");
    
            throw new RuntimeException(nsae);
        }
        catch(java.io.UnsupportedEncodingException uee)
        {
            RdfUtils.log.fatal("RdfUtils.md5: invalid JRE, does not support UTF-8");
    
            throw new RuntimeException(uee);
        }
    }

    public static boolean isPlainNamespaceAndIdentifier(String queryString)
    {
        return Settings.getSettings().getPlainNamespaceAndIdentifierPattern().matcher(queryString).find();
    }

    public static boolean isPlainNamespace(String queryString)
    {
        return Settings.getSettings().getPlainNamespacePattern().matcher(queryString).find();
    }

    public static List<String> getNamespaceAndIdentifier(String nsAndId)
    {
        return matchesForRegexOnString(Settings.getSettings().getPlainNamespaceAndIdentifierPattern(), Settings.getSettings().getStringPropertyFromConfig("plainNamespaceAndIdentifierRegex"), nsAndId);
    }

    public static List<String> getNamespaceAndIdentifierFromUri(String nextUri)
    {
        if(nextUri.startsWith(Settings.getSettings().getDefaultHostAddress()))
        {
            return getNamespaceAndIdentifier(nextUri.substring(Settings.getSettings().getDefaultHostAddress().length()));
        }
        
        return null;
    }

}
