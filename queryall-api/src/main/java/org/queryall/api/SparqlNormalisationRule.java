package org.queryall.api;

import java.util.List;

import org.openrdf.model.URI;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface SparqlNormalisationRule extends NormalisationRule
{

	public abstract URI getMode();

	/**
	 * @param mode the mode to set
	 */
	public abstract void setMode(URI mode);

	public abstract String getSparqlConstructQueryTarget();

	public abstract void setSparqlConstructQueryTarget(String sparqlConstructQueryTarget);

	public abstract List<String> getSparqlWherePatterns();

	public abstract void addSparqlWherePattern(String sparqlWherePattern);

	public abstract String getSparqlPrefixes();

	public abstract void setSparqlPrefixes(String sparqlPrefixes);

	public abstract List<String> getSparqlConstructQueries();
}