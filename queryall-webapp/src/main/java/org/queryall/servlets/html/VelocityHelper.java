package org.queryall.servlets.html;

import java.io.IOException;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A facade class that simplifies using a custom Velocity engine from a servlet. It encapsulates
 * creation of the VelocityEngine instance, its storage in the servlet context, and the rendering of
 * templates into the servlet response output stream.
 * 
 * @author Richard Cyganiak (richard @ cyganiak.de) Adapted for use by Bio2RDF by...
 * @author Peter Ansell (p_ansell @ yahoo.com)
 */

public class VelocityHelper
{
    private static final Logger log = LoggerFactory.getLogger(VelocityHelper.class);
    private static final boolean _TRACE = VelocityHelper.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = VelocityHelper.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = VelocityHelper.log.isInfoEnabled();
    
    public static VelocityEngine createVelocityEngine()
    {
        if(VelocityHelper._TRACE)
        {
            VelocityHelper.log.trace("VelocityHelper.createVelocityEngine: entering...");
        }
        
        VelocityEngine result = null;
        
        try
        {
            // TODO: Switch to using velocity.properties file instead of hardcoding the properties
            // here
            final java.util.Properties engineProperties = new java.util.Properties();
            
            engineProperties.setProperty("runtime.log.logsystem.class",
                    "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
            engineProperties.setProperty("runtime.log.logsystem.log4j.category",
                    "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
            engineProperties.setProperty("input.encoding", "utf-8");
            engineProperties.setProperty("output.encoding", "utf-8");
            engineProperties.setProperty("resource.loader", "class");
            engineProperties.setProperty("class.resource.loader.class",
                    "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            engineProperties.setProperty("eventhandler.referenceinsertion.class",
                    "org.apache.velocity.app.event.implement.EscapeHtmlReference");
            engineProperties.setProperty("eventhandler.escape.html.match", "/xmlEncoded.*/");
            
            if(VelocityHelper._TRACE)
            {
                VelocityHelper.log.trace("VelocityHelper.createVelocityEngine: about to create velocity engine");
            }
            
            result = new VelocityEngine(engineProperties);
            
            if(VelocityHelper._TRACE)
            {
                VelocityHelper.log.trace("VelocityHelper.createVelocityEngine: created basic engine");
            }
        }
        catch(final Exception ex)
        {
            VelocityHelper.log.error("VelocityHelper.setupVelocityProperties: caught fatal exception", ex);
            
            // throw new RuntimeException(ex);
        }
        
        return result;
    }
    
    /**
     * Renders a template using the template variables put into the velocity context.
     */
    public static void renderXHTML(final VelocityEngine nextVelocityEngine, final Context nextVelocityContext,
            final String templateName, final java.io.Writer nextWriter) throws VelocityException
    {
        try
        {
            if(VelocityHelper._TRACE)
            {
                VelocityHelper.log.trace("VelocityHelper.renderXHTML: about to get velocity engine");
            }
            
            if(VelocityHelper._TRACE)
            {
                VelocityHelper.log.trace("VelocityHelper.renderXHTML: about to mergeTemplate");
            }
            
            nextVelocityEngine.mergeTemplate(templateName, "utf-8", nextVelocityContext, nextWriter);
        }
        catch(final ResourceNotFoundException ex)
        {
            throw new VelocityException(ex);
        }
        catch(final ParseErrorException ex)
        {
            throw new VelocityException(ex);
        }
        catch(final MethodInvocationException ex)
        {
            throw new VelocityException(ex);
        }
        catch(final Exception ex)
        {
            // TODO: remove this after upgrading to a version of Velocity that doesn't contain
            // "throws Exception" on mergeTemplate
            VelocityHelper.log
                    .error("Velocity threw bare Exception, rethrowing as VelocityException so we don't have to have throws Exception everywhere ourselves");
            throw new VelocityException(ex);
        }
        finally
        {
            if(nextWriter != null)
            {
                try
                {
                    nextWriter.flush();
                }
                catch(final IOException ex)
                {
                    VelocityHelper.log.error("Could not flush writer", ex);
                }
            }
        }
    }
    
    /**
     * 
     */
    private VelocityHelper()
    {
    }
}
