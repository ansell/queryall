
package org.queryall.servlets.html;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

/**
 * A facade class that simplifies using a custom Velocity engine from a servlet.
 * It encapsulates creation of the VelocityEngine instance, its storage in the
 * servlet context, and the rendering of templates into the servlet response
 * output stream.
 * 
 * @author Richard Cyganiak (richard @ cyganiak.de) 
 * Adapted for use by Bio2RDF by...
 * @author Peter Ansell (p_ansell @ yahoo.com)
 * @version $Id: VelocityHelper.java 944 2011-02-08 10:23:08Z p_ansell $
 */

public class VelocityHelper
{
    private static final Logger log = Logger.getLogger(VelocityHelper.class
            .getName());
    private static final boolean _TRACE = VelocityHelper.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = VelocityHelper.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = VelocityHelper.log.isInfoEnabled();
    
    private final static String VELOCITY_ENGINE = VelocityHelper.class
            .getName()
            + ".VELOCITY_ENGINE";
    
    private final ServletContext servletContext;
    private final Context velocityContext;
    
    /**
     * 
     * @param servletContext
     */
    public VelocityHelper(ServletContext servletContext)
    {
        this.servletContext = servletContext;
        this.velocityContext = new VelocityContext();
    }
    
    private VelocityEngine createVelocityEngine()
    {
        if(VelocityHelper._TRACE)
        {
            VelocityHelper.log
                    .trace("VelocityHelper.createVelocityEngine: entering...");
        }
        
        VelocityEngine result = null;
        
        try
        {
            final java.util.Properties engineProperties = new java.util.Properties();
            
            engineProperties.setProperty("runtime.log.logsystem.class",
                    "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
            engineProperties.setProperty(
                    "runtime.log.logsystem.log4j.category",
                    "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
            engineProperties.setProperty("input.encoding", "utf-8");
            engineProperties.setProperty("output.encoding", "utf-8");
            // FIXME XXX: When Velocity supports loading from WEB-INF/classes/ as well as WEB-INF/lib/ remove this property
            engineProperties.setProperty("file.resource.loader.path",
                    this.servletContext.getRealPath("/")
                            + "/WEB-INF/classes/templates/");
            engineProperties
                    .setProperty("eventhandler.referenceinsertion.class",
                            "org.apache.velocity.app.event.implement.EscapeHtmlReference");
            engineProperties.setProperty("eventhandler.escape.html.match",
                    "/xmlEncoded.*/");
            
            if(VelocityHelper._TRACE)
            {
                VelocityHelper.log
                        .trace("VelocityHelper.createVelocityEngine: about to create velocity engine");
            }
            
            result = new VelocityEngine(engineProperties);
            
            if(VelocityHelper._TRACE)
            {
                VelocityHelper.log
                        .trace("VelocityHelper.createVelocityEngine: created basic engine");
            }
        }
        catch (final Exception ex)
        {
            VelocityHelper.log
                    .fatal(
                            "VelocityHelper.setupVelocityProperties: caught fatal exception",
                            ex);
            
            // throw new RuntimeException(ex);
        }
        
        return result;
    }
    
    /**
     * @return A receptacle for template variables
     */
    public Context getVelocityContext()
    {
        return this.velocityContext;
    }
    
    private VelocityEngine getVelocityEngine()
    {
        if(VelocityHelper._TRACE)
        {
            VelocityHelper.log
                    .trace("VelocityHelper.getVelocityEngine: about to get current VelocityEngine instance");
        }
        
        VelocityEngine currentEngine = (VelocityEngine) this.servletContext
                .getAttribute(VelocityHelper.VELOCITY_ENGINE);
        
        if(VelocityHelper._TRACE)
        {
            VelocityHelper.log
                    .trace("VelocityHelper.getVelocityEngine: currentEngine="
                            + currentEngine);
        }
        
        if(currentEngine == null)
        // if(engineSetup == null || !engineSetup)
        {
            if(VelocityHelper._TRACE)
            {
                VelocityHelper.log
                        .trace("VelocityHelper.getVelocityEngine: setting up engine...");
            }
            
            currentEngine = this.createVelocityEngine();
            
            // setupVelocityProperties();
            
            if(VelocityHelper._TRACE)
            {
                VelocityHelper.log
                        .trace("VelocityHelper.getVelocityEngine: about to try to synchronize access to servletContext");
            }
            
            // synchronized (servletContext)
            // {
            this.servletContext.setAttribute(VelocityHelper.VELOCITY_ENGINE,
                    currentEngine);
            
            // }
            
            if(VelocityHelper._TRACE)
            {
                VelocityHelper.log
                        .trace("VelocityHelper.getVelocityEngine: finished with synchronized access to servletContext");
            }
        }
        
        if(VelocityHelper._TRACE)
        {
            VelocityHelper.log
                    .trace("VelocityHelper.getVelocityEngine: returning... currentEngine="
                            + currentEngine);
        }
        
        return currentEngine;
    }
    
    /**
     * Renders a template using the template variables put into the velocity
     * context.
     */
    public void renderXHTML(String templateName, java.io.Writer nextWriter)
            throws Exception
    {
        // response.addHeader("Content-Type", "text/html; charset=utf-8");
        // response.addHeader("Cache-Control", "no-cache");
        // response.addHeader("Pragma", "no-cache");
        
        try
        {
            if(VelocityHelper._TRACE)
            {
                VelocityHelper.log
                        .trace("VelocityHelper.renderXHTML: about to setup Velocity properties");
            }
            
            final VelocityEngine currentVelocityEngine = this
                    .getVelocityEngine();
            // setupVelocityEngine();
            
            if(VelocityHelper._TRACE)
            {
                VelocityHelper.log
                        .trace("VelocityHelper.renderXHTML: about to mergeTemplate");
            }
            
            // make sure that the writer is going to be writing in UTF-8
            // OutputStreamWriter utf8Writer = new
            // OutputStreamWriter(nextWriter, "utf-8");
            // currentVelocityEngine.mergeTemplate(templateName, "utf-8",
            // velocityContext, nextWriter);
            currentVelocityEngine.mergeTemplate(templateName, "utf-8",
                    this.velocityContext, nextWriter);
            
            // writer.close();
        }
        catch (final Exception ex)
        {
            VelocityHelper.log.fatal(
                    "VelocityHelper.renderXHTML: caught exception with templateName="
                            + templateName, ex);
            
            throw ex;
            // throw new RuntimeException(ex);
        }
    }
}
