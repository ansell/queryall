/**
 * 
 */
package org.queryall.api.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class PropertyUtils
{
    private static final Logger log = LoggerFactory.getLogger(PropertyUtils.class);
    private static final boolean _TRACE = PropertyUtils.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = PropertyUtils.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = PropertyUtils.log.isInfoEnabled();
    
    /**
     * This matches the queryall.properties file where the generally static API specific section of
     * the configuration settings are stored
     */
    public static final String DEFAULT_PROPERTIES_BUNDLE_NAME = "queryall";
    
    /**
     * Checks for the key first in the system vm properties, then in the localisation properties
     * file, by default, "queryall.properties", then uses the defaultValue if the location is still
     * unknown
     * 
     * @param key
     *            The key to check for first in system vm properties and then in the localisation
     *            properties file
     * @param defaultValue
     *            The value to return if the key does not match any configured value
     * @return the string matching the key
     */
    public static String getSystemOrPropertyString(final String key, final String defaultValue)
    {
        String result = System.getProperty(key);
        
        if(result == null)
        {
            try
            {
                result = ResourceBundle.getBundle(PropertyUtils.DEFAULT_PROPERTIES_BUNDLE_NAME).getString(key);
            }
            catch(final MissingResourceException mre)
            {
                if(PropertyUtils._TRACE)
                {
                    PropertyUtils.log.trace(mre.getMessage(), mre);
                }
            }
        }
        
        if(result == null)
        {
            return defaultValue;
        }
        else
        {
            return result;
        }
    }
    
    /**
     * Private constructor
     */
    private PropertyUtils()
    {
        // Private constructor
    }
    
}
