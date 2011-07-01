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
	 * @return the sparqlConstructQuery
	 */
	public abstract String getSparqlConstructQueryTarget();

	/**
	 * @param sparqlConstructQuery the sparqlConstructQuery to set
	 */
	public abstract void setSparqlConstructQueryTarget(String sparqlConstructQueryTarget);

	/**
	 * @return the sparqlConstructQuery
	 */
	public abstract String getSparqlWherePattern();

	/**
	 * @param sparqlConstructQuery the sparqlConstructQuery to set
	 */
	public abstract void setSparqlWherePattern(String sparqlWherePattern);

	/**
	 * @return the sparqlConstructQuery
	 */
	public abstract String getSparqlConstructQuery();

}