package org.queryall.api;

public interface RegexNormalisationRule extends NormalisationRule
{

	// NOTE: it is quite okay to have an empty replace regex, but an empty match
	// is not considered useful here
	boolean hasInputRule();

	// NOTE: it is quite okay to have an empty replace regex, but an empty match
	// is not considered useful here
	boolean hasOutputRule();

	/**
	 * @param inputMatchRegex the inputMatchRegex to set
	 */
	void setInputMatchRegex(String inputMatchRegex);

	/**
	 * @return the inputMatchRegex
	 */
	String getInputMatchRegex();

	/**
	 * @param inputReplaceRegex the inputReplaceRegex to set
	 */
	void setInputReplaceRegex(String inputReplaceRegex);

	/**
	 * @return the inputReplaceRegex
	 */
	String getInputReplaceRegex();

	/**
	 * @param outputMatchRegex the outputMatchRegex to set
	 */
	void setOutputMatchRegex(String outputMatchRegex);

	/**
	 * @return the outputMatchRegex
	 */
	String getOutputMatchRegex();

	/**
	 * @param outputReplaceRegex the outputReplaceRegex to set
	 */
	void setOutputReplaceRegex(String outputReplaceRegex);

	/**
	 * @return the outputReplaceRegex
	 */
	String getOutputReplaceRegex();

}