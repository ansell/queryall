package org.queryall.api.rdfrule;

import java.util.List;

public interface SparqlAskRule extends ValidatingRule, SparqlNormalisationRule
{
    /**
     * @return A list of SPARQL Ask queries created using the Where patterns defined in the
     *         SparqlNormalisationRule interface
     */
    List<String> getSparqlAskQueries();
}