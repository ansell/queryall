package org.queryall.api;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
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