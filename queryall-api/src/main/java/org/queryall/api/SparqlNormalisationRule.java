package org.queryall.api;

import java.util.List;

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
	public abstract List<String> getSparqlWherePatterns();

	/**
	 * @param sparqlConstructQuery the sparqlConstructQuery to set
	 */
	public abstract void addSparqlWherePattern(String sparqlWherePattern);

	/**
	 * @return the sparqlConstructQuery
	 */
	public abstract String getSparqlConstructQuery();

	/**
	 * @return the sparqlConstructQuery
	 */
	public abstract String getSparqlPrefixes();

	/**
	 * @param sparqlConstructQuery the sparqlConstructQuery to set
	 */
	public abstract void setSparqlPrefixes(String sparqlPrefixes);

}