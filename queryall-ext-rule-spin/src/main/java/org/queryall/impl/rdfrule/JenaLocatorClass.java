/**
 * 
 */
package org.queryall.impl.rdfrule;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.util.Locator;
import com.hp.hpl.jena.util.TypedStream;

/**
 * Uses a class to load resources as streams, as opposed to LocatorClassLoader which uses
 * ClassLoaders
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class JenaLocatorClass implements Locator
{
    private static final Logger log = LoggerFactory.getLogger(JenaLocatorClass.class);
    private Class myClass;
    
    /**
     * 
     */
    public JenaLocatorClass(final Class nextClass)
    {
        this.myClass = nextClass;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.hp.hpl.jena.util.Locator#getName()
     */
    @Override
    public String getName()
    {
        return "JenaLocatorClass";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.hp.hpl.jena.util.Locator#open(java.lang.String)
     */
    @Override
    public TypedStream open(final String filenameOrURI)
    {
        if(this.myClass == null)
        {
            return null;
        }
        
        final String fn = FileUtils.toFilename(filenameOrURI);
        if(fn == null)
        {
            if(JenaLocatorClass.log.isTraceEnabled())
            {
                JenaLocatorClass.log.trace("Not found: " + filenameOrURI);
            }
            return null;
        }
        final InputStream in = this.myClass.getResourceAsStream(fn);
        if(in == null)
        {
            if(JenaLocatorClass.log.isTraceEnabled())
            {
                JenaLocatorClass.log.trace("Failed to open: " + filenameOrURI);
            }
            return null;
        }
        
        if(JenaLocatorClass.log.isTraceEnabled())
        {
            JenaLocatorClass.log.trace("Found: " + filenameOrURI);
        }
        
        // base = classLoader.getResource(fn).toExternalForm ;
        return new TypedStream(in);
    }
    
}
