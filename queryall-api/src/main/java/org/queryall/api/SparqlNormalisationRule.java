package org.queryall.api;

import org.openrdf.model.URI;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface SparqlNormalisationRule
{

	public abstract URI getMode();

	/**
	 * @param mode the mode to set
	 */
	public abstract void setMode(URI mode);

	/**
	 * @param sparqlConstructQuery the sparqlConstructQuery to set
	 */
	public abstract void setSparqlConstructQuery(String sparqlConstructQuery);

	/**
	 * @return the sparqlConstructQuery
	 */
	public abstract String getSparqlConstructQuery();

}