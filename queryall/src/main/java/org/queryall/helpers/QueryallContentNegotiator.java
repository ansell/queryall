package org.queryall.helpers;

import de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator;
import de.fuberlin.wiwiss.pubby.negotiation.MediaRangeSpec;

import org.apache.log4j.Logger;

import org.queryall.helpers.*;


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
    
    public static ContentTypeNegotiator getContentNegotiator(Settings localSettings)
    {
        ContentTypeNegotiator newContentNegotiator = new ContentTypeNegotiator();
        
        if(localSettings.getStringPropertyFromConfig("preferredDisplayContentType", Constants.APPLICATION_RDF_XML).equals(Constants.APPLICATION_RDF_XML))
        {
            newContentNegotiator.addVariant("application/rdf+xml;q=0.99");
        }
        else
        {
            newContentNegotiator.addVariant("application/rdf+xml;q=0.95");
        }
        
        if(localSettings.getStringPropertyFromConfig("preferredDisplayContentType", Constants.APPLICATION_RDF_XML).equals(Constants.TEXT_RDF_N3))
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
        
        if(localSettings.getStringPropertyFromConfig("preferredDisplayContentType", Constants.APPLICATION_RDF_XML).equals(Constants.TEXT_TURTLE))
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
        
        if(localSettings.getStringPropertyFromConfig("preferredDisplayContentType", Constants.APPLICATION_RDF_XML).equals(Constants.TEXT_HTML))
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
        
        if(localSettings.getStringPropertyFromConfig("preferredDisplayContentType", Constants.APPLICATION_RDF_XML).equals(Constants.APPLICATION_JSON))
        {
            newContentNegotiator.addVariant("application/json;q=0.99")
            .addAliasMediaType("application/rdf+json;q=0.8");
        }
        else
        {
            newContentNegotiator.addVariant("application/json;q=0.90")
            .addAliasMediaType("application/rdf+json;q=0.8");
        }
        
        
        newContentNegotiator.addVariant("text/plain;q=0.2");

        return newContentNegotiator;
    }
    
    public static String getResponseContentType(String acceptHeader, String userAgent)
    {
        ContentTypeNegotiator negotiator = QueryallContentNegotiator.getContentNegotiator();
    
        return getResponseContentType(acceptHeader, userAgent, negotiator);
    }
    
    
    public static String getResponseContentType(String acceptHeader, String userAgent, ContentTypeNegotiator negotiator)
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
            
            // TODO: change reference to Settings.getSettings() to match whichever instance was used to create the negotiator object
            return Settings.getSettings().getStringPropertyFromConfig("preferredDisplayContentType", Constants.APPLICATION_RDF_XML);
        }
        
        return bestMatch.getMediaType();
    }
}
