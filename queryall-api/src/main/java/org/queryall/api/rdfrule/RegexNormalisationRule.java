package org.queryall.api.rdfrule;

public interface RegexNormalisationRule extends NormalisationRule
{
    
    /**
     * @return the inputMatchRegex
     */
    String getInputMatchRegex();
    
    /**
     * @return the inputReplaceRegex
     */
    String getInputReplaceRegex();
    
    /**
     * @return the outputMatchRegex
     */
    String getOutputMatchRegex();
    
    /**
     * @return the outputReplaceRegex
     */
    String getOutputReplaceRegex();
    
    // NOTE: it is quite okay to have an empty replace regex, but an empty match
    // is not considered useful here
    boolean hasInputRule();
    
    // NOTE: it is quite okay to have an empty replace regex, but an empty match
    // is not considered useful here
    boolean hasOutputRule();
    
    /**
     * @param inputMatchRegex
     *            the inputMatchRegex to set
     */
    void setInputMatchRegex(String inputMatchRegex);
    
    /**
     * @param inputReplaceRegex
     *            the inputReplaceRegex to set
     */
    void setInputReplaceRegex(String inputReplaceRegex);
    
    /**
     * @param outputMatchRegex
     *            the outputMatchRegex to set
     */
    void setOutputMatchRegex(String outputMatchRegex);
    
    /**
     * @param outputReplaceRegex
     *            the outputReplaceRegex to set
     */
    void setOutputReplaceRegex(String outputReplaceRegex);
    
}