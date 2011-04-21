package org.queryall.api;

public interface RegexNormalisationRule
{

	// NOTE: it is quite okay to have an empty replace regex, but an empty match
	// is not considered useful here
	public abstract boolean hasInputRule();

	// NOTE: it is quite okay to have an empty replace regex, but an empty match
	// is not considered useful here
	public abstract boolean hasOutputRule();

	/**
	 * @param inputMatchRegex the inputMatchRegex to set
	 */
	public abstract void setInputMatchRegex(String inputMatchRegex);

	/**
	 * @return the inputMatchRegex
	 */
	public abstract String getInputMatchRegex();

	/**
	 * @param inputReplaceRegex the inputReplaceRegex to set
	 */
	public abstract void setInputReplaceRegex(String inputReplaceRegex);

	/**
	 * @return the inputReplaceRegex
	 */
	public abstract String getInputReplaceRegex();

	/**
	 * @param outputMatchRegex the outputMatchRegex to set
	 */
	public abstract void setOutputMatchRegex(String outputMatchRegex);

	/**
	 * @return the outputMatchRegex
	 */
	public abstract String getOutputMatchRegex();

	/**
	 * @param outputReplaceRegex the outputReplaceRegex to set
	 */
	public abstract void setOutputReplaceRegex(String outputReplaceRegex);

	/**
	 * @return the outputReplaceRegex
	 */
	public abstract String getOutputReplaceRegex();

}