package org.queryall.api.rdfrule;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface XsltNormalisationRule extends TransformingRule
{
    
    /**
     * @return The stylesheet to be used to transform documents using this rule.
     */
    String getXsltStylesheet();
    
    /**
     * 
     * @param xsltStylesheet
     *            The stylesheet to be used to transform documents using this rule.
     */
    void setXsltStylesheet(String xsltStylesheet);
    
}