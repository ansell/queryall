package org.queryall.api;

public abstract class XsltNormalisationRule extends NormalisationRule
{

	/**
	 * @param xsltStylesheet the xsltStylesheet to set
	 */
	public abstract void setXsltStylesheet(String xsltStylesheet);

	/**
	 * @return the xsltStylesheet
	 */
	public abstract String getXsltStylesheet();

}