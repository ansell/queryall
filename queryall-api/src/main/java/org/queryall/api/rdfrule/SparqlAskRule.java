package org.queryall.api.rdfrule;

import java.util.List;

/**
 * A SPARQL ASK Rule uses a SPARQL ASK query to validate the results from a provider. It uses the
 * where patterns defined in the SparqlNormalisationRule interface to generate ASK queries.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface SparqlAskRule extends ValidatingRule, SparqlNormalisationRule
{
    /**
     * @return A list of SPARQL Ask queries created using the Where patterns defined in the
     *         SparqlNormalisationRule interface
     */
    List<String> getSparqlAskQueries();
}