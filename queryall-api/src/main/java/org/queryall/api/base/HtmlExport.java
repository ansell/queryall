package org.queryall.api.base;

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