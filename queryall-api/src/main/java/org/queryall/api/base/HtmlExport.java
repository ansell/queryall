package org.queryall.api.base;

/**
 * Provides conversions between QueryAll objects and HTML representations
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface HtmlExport
{
    
    /**
     * Returns the representation of the object as an XHTML marked up string for display only
     **/
    String toHtml();
    
    /**
     * Returns the input fields in XHTML as a string for use in forms designed to edit this object
     **/
    String toHtmlFormBody();
    
}