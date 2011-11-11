package org.queryall.api.rdfrule;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface XsltNormalisationRule extends TransformingRule
{
    
    /**
     * @return the xsltStylesheet
     */
    String getXsltStylesheet();
    
    /**
     * @param xsltStylesheet
     *            the xsltStylesheet to set
     */
    void setXsltStylesheet(String xsltStylesheet);
    
}