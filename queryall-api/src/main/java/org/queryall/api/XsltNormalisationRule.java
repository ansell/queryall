package org.queryall.api;

public interface XsltNormalisationRule
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