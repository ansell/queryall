package de.fuberlin.wiwiss.pubby.negotiation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MediaRangeSpec
{
    
    private static final Logger log = LoggerFactory.getLogger(MediaRangeSpec.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = MediaRangeSpec.log.isTraceEnabled();
    private static final boolean DEBUG = MediaRangeSpec.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = MediaRangeSpec.log.isInfoEnabled();
    
    private final static Pattern tokenPattern;
    private final static Pattern parameterPattern;
    private final static Pattern mediaRangePattern;
    private final static Pattern qValuePattern;
    static
    {
        // See RFC 2616, section 2.2
        final String token = "[\\x20-\\x7E&&[^()<>@,;:\\\"/\\[\\]?={} ]]+";
        final String quotedString = "\"((?:[\\x20-\\x7E\\n\\r\\t&&[^\"\\\\]]|\\\\[\\x00-\\x7F])*)\"";
        // See RFC 2616, section 3.6
        final String parameter = ";\\s*(?!q\\s*=)(" + token + ")=(?:(" + token + ")|" + quotedString + ")";
        // See RFC 2616, section 3.9
        final String qualityValue = "(?:0(?:\\.\\d{0,3})?|1(?:\\.0{0,3})?)";
        // See RFC 2616, sections 14.1
        final String quality = ";\\s*q\\s*=\\s*([^;,]*)";
        // See RFC 2616, section 3.7
        final String regex =
                "(" + token + ")/(" + token + ")" + "((?:\\s*" + parameter + ")*)" + "(?:\\s*" + quality + ")?"
                        + "((?:\\s*" + parameter + ")*)";
        tokenPattern = Pattern.compile(token);
        parameterPattern = Pattern.compile(parameter);
        mediaRangePattern = Pattern.compile(regex);
        qValuePattern = Pattern.compile(qualityValue);
    }
    
    private static String escape(final String s)
    {
        return s.replaceAll("[\\\\\"]", "\\\\$0");
    }
    
    /**
     * Parses an HTTP Accept header into a List of MediaRangeSpecs
     * 
     * @return A List of MediaRangeSpecs
     */
    public static List<MediaRangeSpec> parseAccept(final String s)
    {
        if(MediaRangeSpec.DEBUG)
        {
            MediaRangeSpec.log.debug("MediaRangeSpec: about to parse accept string s=" + s);
        }
        
        final List<MediaRangeSpec> result = new ArrayList<MediaRangeSpec>();
        final Matcher m = MediaRangeSpec.mediaRangePattern.matcher(s);
        while(m.find())
        {
            result.add(MediaRangeSpec.parseRange(m.group()));
        }
        return result;
    }
    
    /**
     * Parses a media range from a string such as <tt>text/*;charset=utf-8;q=0.9</tt>. Unlike simple
     * media types, media ranges may include wildcards.
     */
    public static MediaRangeSpec parseRange(final String mediaRange)
    {
        Matcher m = MediaRangeSpec.mediaRangePattern.matcher(mediaRange);
        if(!m.matches())
        {
            return null;
        }
        final String type = m.group(1).toLowerCase();
        final String subtype = m.group(2).toLowerCase();
        final String unparsedParameters = m.group(3);
        final String qValue = m.group(7);
        m = MediaRangeSpec.parameterPattern.matcher(unparsedParameters);
        if("*".equals(type) && !"*".equals(subtype))
        {
            return null;
        }
        final List<String> parameterNames = new ArrayList<String>();
        final List<String> parameterValues = new ArrayList<String>();
        while(m.find())
        {
            final String name = m.group(1).toLowerCase();
            final String value = (m.group(3) == null) ? m.group(2) : MediaRangeSpec.unescape(m.group(3));
            parameterNames.add(name);
            parameterValues.add(value);
        }
        double quality = 1.0;
        if(qValue != null && MediaRangeSpec.qValuePattern.matcher(qValue).matches())
        {
            try
            {
                quality = Double.parseDouble(qValue);
            }
            catch(final NumberFormatException ex)
            {
                // quality stays at default value
            }
        }
        return new MediaRangeSpec(type, subtype, parameterNames, parameterValues, quality);
    }
    
    /**
     * Parses a media type from a string such as <tt>text/html;charset=utf-8;q=0.9</tt>.
     */
    public static MediaRangeSpec parseType(final String mediaType)
    {
        final MediaRangeSpec m = MediaRangeSpec.parseRange(mediaType);
        if(m == null || m.isWildcardType() || m.isWildcardSubtype())
        {
            return null;
        }
        return m;
    }
    
    private static String unescape(final String s)
    {
        return s.replaceAll("\\\\(.)", "$1");
    }
    
    private final String type;
    private final String subtype;
    private final List<String> parameterNames;
    private final List<String> parameterValues;
    private final String mediaType;
    private final double quality;
    
    private MediaRangeSpec(final String type, final String subtype, final List<String> parameterNames,
            final List<String> parameterValues, final double quality)
    {
        this.type = type;
        this.subtype = subtype;
        this.parameterNames = Collections.unmodifiableList(parameterNames);
        this.parameterValues = parameterValues;
        this.mediaType = this.buildMediaType();
        this.quality = quality;
    }
    
    private String buildMediaType()
    {
        final StringBuffer result = new StringBuffer();
        result.append(this.type);
        result.append("/");
        result.append(this.subtype);
        for(int i = 0; i < this.parameterNames.size(); i++)
        {
            result.append(";");
            result.append(this.parameterNames.get(i));
            result.append("=");
            final String value = this.parameterValues.get(i);
            if(MediaRangeSpec.tokenPattern.matcher(value).matches())
            {
                result.append(value);
            }
            else
            {
                result.append("\"");
                result.append(MediaRangeSpec.escape(value));
                result.append("\"");
            }
        }
        return result.toString();
    }
    
    public MediaRangeSpec getBestMatch(final List<MediaRangeSpec> mediaRanges)
    {
        MediaRangeSpec result = null;
        int bestPrecedence = 0;
        final Iterator<MediaRangeSpec> it = mediaRanges.iterator();
        while(it.hasNext())
        {
            final MediaRangeSpec range = it.next();
            if(this.getPrecedence(range) > bestPrecedence)
            {
                bestPrecedence = this.getPrecedence(range);
                result = range;
            }
        }
        return result;
    }
    
    public String getMediaType()
    {
        return this.mediaType;
    }
    
    public String getParameter(final String parameterName)
    {
        for(int i = 0; i < this.parameterNames.size(); i++)
        {
            if(this.parameterNames.get(i).equals(parameterName.toLowerCase()))
            {
                return this.parameterValues.get(i);
            }
        }
        return null;
    }
    
    public List<String> getParameterNames()
    {
        return this.parameterNames;
    }
    
    public int getPrecedence(final MediaRangeSpec range)
    {
        if(range.isWildcardType())
        {
            return 1;
        }
        if(!range.type.equals(this.type))
        {
            return 0;
        }
        if(range.isWildcardSubtype())
        {
            return 2;
        }
        if(!range.subtype.equals(this.subtype))
        {
            return 0;
        }
        if(range.getParameterNames().isEmpty())
        {
            return 3;
        }
        int result = 3;
        for(int i = 0; i < range.getParameterNames().size(); i++)
        {
            final String name = range.getParameterNames().get(i);
            final String value = range.getParameter(name);
            if(!value.equals(this.getParameter(name)))
            {
                return 0;
            }
            result++;
        }
        return result;
    }
    
    public double getQuality()
    {
        return this.quality;
    }
    
    public String getSubtype()
    {
        return this.subtype;
    }
    
    public String getType()
    {
        return this.type;
    }
    
    public boolean isWildcardSubtype()
    {
        return !this.isWildcardType() && "*".equals(this.subtype);
    }
    
    public boolean isWildcardType()
    {
        return "*".equals(this.type);
    }
    
    @Override
    public String toString()
    {
        return this.mediaType + ";q=" + this.quality;
    }
}
