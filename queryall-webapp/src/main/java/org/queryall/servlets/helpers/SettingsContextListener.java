/**
 * 
 */
package org.queryall.servlets.helpers;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.velocity.app.VelocityEngine;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.utils.Constants;
import org.queryall.blacklist.BlacklistController;
import org.queryall.negotiation.QueryallContentNegotiator;
import org.queryall.servlets.html.VelocityHelper;
import org.queryall.utils.Settings;
import org.queryall.utils.SettingsFactory;

import de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator;

/**
 * Wraps up a QueryAllConfiguration, a BlacklistController, and a VelocityEngine object in the
 * ServletContext
 * 
 * @author Peter Ansell (p_ansell @ yahoo.com)
 */
public class SettingsContextListener implements ServletContextListener
{
    public static final String QUERYALL_CONTENTNEGOTIATOR = "queryallcontentnegotiator";
    public static final String QUERYALL_VELOCITY = "queryallvelocity";
    public static final String QUERYALL_BLACKLIST = "queryallblacklist";
    public static final String QUERYALL_CONFIG = "queryallconfig";
    
    public SettingsContextListener()
    {
    }
    
    /**
     * Removes the references to the singletons that were on the servlet context when the context is
     * to be destroyed
     */
    @Override
    public void contextDestroyed(final ServletContextEvent sce)
    {
        // remove the attributes that we put into servlet context when it is destroyed
        sce.getServletContext().removeAttribute(SettingsContextListener.QUERYALL_CONFIG);
        sce.getServletContext().removeAttribute(SettingsContextListener.QUERYALL_BLACKLIST);
        sce.getServletContext().removeAttribute(SettingsContextListener.QUERYALL_VELOCITY);
        sce.getServletContext().removeAttribute(SettingsContextListener.QUERYALL_CONTENTNEGOTIATOR);
    }
    
    /**
     * Creates the singleton objects for servlets in the given context to use
     */
    @Override
    public void contextInitialized(final ServletContextEvent sce)
    {
        // create a new settings object
        final QueryAllConfiguration tempSettings = SettingsFactory.generateSettings();
        
        // Use these settings to create a blacklistcontroller object
        final BlacklistController tempBlacklist = new BlacklistController(tempSettings);
        
        // also create a singleton velocity engine, as it is slow to create every time we make a
        // query
        final VelocityEngine currentEngine = VelocityHelper.createVelocityEngine();
        
        // setup the default content type negotiator using the users given preference
        final ContentTypeNegotiator contentTypeNegotiator =
                QueryallContentNegotiator.getContentNegotiator(tempSettings.getStringProperty(
                        "preferredDisplayContentType", Constants.APPLICATION_RDF_XML));
        
        // then put both of them into servlet context so they can be shared between requests in this
        // servlet
        sce.getServletContext().setAttribute(SettingsContextListener.QUERYALL_CONFIG, tempSettings);
        sce.getServletContext().setAttribute(SettingsContextListener.QUERYALL_BLACKLIST, tempBlacklist);
        sce.getServletContext().setAttribute(SettingsContextListener.QUERYALL_VELOCITY, currentEngine);
        sce.getServletContext().setAttribute(SettingsContextListener.QUERYALL_CONTENTNEGOTIATOR, contentTypeNegotiator);
    }
    
}
