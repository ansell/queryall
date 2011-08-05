package org.queryall.api;

public interface XsltNormalisationRule extends NormalisationRule
{

	/**
	 * @param xsltStylesheet the xsltStylesheet to set
	 */
	void setXsltStylesheet(String xsltStylesheet);

	/**
	 * @return the xsltStylesheet
	 */
	String getXsltStylesheet();

}