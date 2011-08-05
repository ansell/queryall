package org.queryall.api;

import java.util.List;

import org.openrdf.model.URI;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface SparqlNormalisationRule extends NormalisationRule
{

	URI getMode();

	/**
	 * @param mode the mode to set
	 */
	void setMode(URI mode);

	String getSparqlConstructQueryTarget();

	void setSparqlConstructQueryTarget(String sparqlConstructQueryTarget);

	List<String> getSparqlWherePatterns();

	void addSparqlWherePattern(String sparqlWherePattern);

	String getSparqlPrefixes();

	void setSparqlPrefixes(String sparqlPrefixes);

	List<String> getSparqlConstructQueries();
}