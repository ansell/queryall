package org.queryall.helpers;

import de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator;
import de.fuberlin.wiwiss.pubby.negotiation.MediaRangeSpec;

import org.apache.log4j.Logger;
import org.queryall.api.QueryAllConfiguration;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryallContentNegotiator
{
    public static final Logger log = Logger.getLogger(QueryallContentNegotiator.class.getName());
    public static final boolean _TRACE = log.isTraceEnabled();
    public static final boolean _DEBUG = log.isDebugEnabled();
    public static final boolean _INFO = log.isInfoEnabled();
    
    private static ContentTypeNegotiator contentNegotiator = null;
    
    public static ContentTypeNegotiator getContentNegotiator()
    {
        if(QueryallContentNegotiator.contentNegotiator != null)
            return QueryallContentNegotiator.contentNegotiator;
        
        QueryallContentNegotiator.contentNegotiator = getContentNegotiator(Settings.getSettings());
    	
    	return QueryallContentNegotiator.contentNegotiator;
    }
    
    public static ContentTypeNegotiator getContentNegotiator(QueryAllConfiguration localSettings)
    {
        ContentTypeNegotiator newContentNegotiator = new ContentTypeNegotiator();
        
        if(localSettings.getStringProperty("preferredDisplayContentType", Constants.APPLICATION_RDF_XML).equals(Constants.APPLICATION_RDF_XML))
        {
            newContentNegotiator.addVariant("application/rdf+xml;q=0.99");
        }
        else
        {
            newContentNegotiator.addVariant("application/rdf+xml;q=0.95");
        }
        
        if(localSettings.getStringProperty("preferredDisplayContentType", Constants.APPLICATION_RDF_XML).equals(Constants.TEXT_RDF_N3))
        {
            newContentNegotiator.addVariant("text/rdf+n3;q=0.99")
            .addAliasMediaType("text/n3;q=0.5")
            .addAliasMediaType("application/rdf+n3;q=0.5")
            .addAliasMediaType("application/n3;q=0.5");
        }
        else
        {
            newContentNegotiator.addVariant("text/rdf+n3;q=0.90")
            .addAliasMediaType("text/n3;q=0.5")
            .addAliasMediaType("application/rdf+n3;q=0.5")
            .addAliasMediaType("application/n3;q=0.5");
        }
        
        if(localSettings.getStringProperty("preferredDisplayContentType", Constants.APPLICATION_RDF_XML).equals(Constants.TEXT_TURTLE))
        {
            // See http://www.w3.org/TeamSubmission/turtle/ for reasoning here
            newContentNegotiator.addVariant("text/turtle;q=0.99")
            .addAliasMediaType("application/turtle;q=0.8")
            .addAliasMediaType("application/x-turtle;q=0.5");
        }
        else
        {
            // See http://www.w3.org/TeamSubmission/turtle/ for reasoning here
            newContentNegotiator.addVariant("text/turtle;q=0.90")
            .addAliasMediaType("application/turtle;q=0.8")
            .addAliasMediaType("application/x-turtle;q=0.5");
        }
        
        if(localSettings.getStringProperty("preferredDisplayContentType", Constants.APPLICATION_RDF_XML).equals(Constants.TEXT_HTML))
        {
            newContentNegotiator.addVariant("text/html;q=0.99")
            .addAliasMediaType("application/html;q=0.8")
            .addAliasMediaType("application/xhtml+xml;q=0.8");
        }
        else
        {
            newContentNegotiator.addVariant("text/html;q=0.45")
            .addAliasMediaType("application/html;q=0.3")
            .addAliasMediaType("application/xhtml+xml;q=0.3");
        }
        
        if(localSettings.getStringProperty("preferredDisplayContentType", Constants.APPLICATION_RDF_XML).equals(Constants.APPLICATION_JSON))
        {
            newContentNegotiator.addVariant("application/json;q=0.99")
            .addAliasMediaType("application/rdf+json;q=0.8");
        }
        else
        {
            newContentNegotiator.addVariant("application/json;q=0.6")
            .addAliasMediaType("application/rdf+json;q=0.6");
        }
        
        
        if(localSettings.getStringProperty("preferredDisplayContentType", Constants.APPLICATION_RDF_XML).equals(Constants.TEXT_X_NQUADS))
        {
            newContentNegotiator.addVariant("text/x-nquads;q=0.99")
            .addAliasMediaType("text/nquads;q=0.8");
        }
        else
        {
            newContentNegotiator.addVariant("text/x-nquads;q=0.6")
            .addAliasMediaType("text/nquads;q=0.6");
        }
        
        
        // NTriples content type was not intelligently defined, but we try to work with it anyway, basically, if they ask for anything else at the same time as NTriples, they will get it instead
        if(localSettings.getStringProperty("preferredDisplayContentType", Constants.APPLICATION_RDF_XML).equals(Constants.TEXT_PLAIN))
        {
        	newContentNegotiator.addVariant("text/plain;q=0.99");
        }
        else
        {
        	newContentNegotiator.addVariant("text/plain;q=0.2");
        }

        return newContentNegotiator;
    }
    
    public static String getResponseContentType(String acceptHeader, String userAgent, String fallback)
    {
        ContentTypeNegotiator negotiator = QueryallContentNegotiator.getContentNegotiator();
    
        return getResponseContentType(acceptHeader, userAgent, negotiator, fallback);
    }
    
    
    public static String getResponseContentType(String acceptHeader, String userAgent, ContentTypeNegotiator negotiator, String fallback)
    {
        if(_DEBUG)
        {
            log.debug("QueryallContentNegotiator: acceptHeader="+acceptHeader+" userAgent="+userAgent);
        }
        
        MediaRangeSpec bestMatch = negotiator.getBestMatch(acceptHeader, userAgent);
        
        if (bestMatch == null)
        {
            if(_TRACE)
            {
                log.trace("QueryallContentNegotiator: bestMatch not found, returning Settings:preferredDisplayContentType instead");
            }
            
            return fallback;
        }
        
        return bestMatch.getMediaType();
    }
}
