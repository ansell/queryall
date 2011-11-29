package org.queryall.api.rdfrule;

/**
 * A RegexNormalisationRule uses Regular Expressions to transform data from a normalised scheme into
 * a denormalised scheme to match providers, before translating the results from a provider back
 * into the normalised scheme.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface RegexNormalisationRule extends TransformingRule
{
    
    /**
     * 
     * @return The Regular Expression used to match against inputs, in the denormalisation stages.
     */
    String getInputMatchRegex();
    
    /**
     * 
     * @return The Regular Expression used to replace any matches using the input match regex, in
     *         the denormalisation stages.
     */
    String getInputReplaceRegex();
    
    /**
     * 
     * @return The Regular Expression used to match against outputs, in the normalisation stages.
     */
    String getOutputMatchRegex();
    
    /**
     * 
     * @return The Regular Expression used to replace any matches using the output match regex, in
     *         the normalisation stages.
     */
    String getOutputReplaceRegex();
    
    /**
     * 
     * @return True if the input match regex and input replace regex are both well defined, and
     *         false otherwise.
     */
    boolean hasInputRule();
    
    /**
     * 
     * @return True if the output match regex and output replace regex are both well defined, and
     *         false otherwise.
     */
    boolean hasOutputRule();
    
    /**
     * 
     * @param inputMatchRegex
     *            The input match regex to use to match in the denormalisation stages.
     */
    void setInputMatchRegex(String inputMatchRegex);
    
    /**
     * 
     * @param inputReplaceRegex
     *            The input replace regex to use to replace any matches from the input match regex
     *            in the denormalisation stages.
     */
    void setInputReplaceRegex(String inputReplaceRegex);
    
    /**
     * 
     * @param outputMatchRegex
     *            The output match regex to use to match in the denormalisation stages.
     */
    void setOutputMatchRegex(String outputMatchRegex);
    
    /**
     * 
     * @param outputReplaceRegex
     *            The output replace regex to use to replace any matches from the output match regex
     *            in the denormalisation stages.
     */
    void setOutputReplaceRegex(String outputReplaceRegex);
    
}