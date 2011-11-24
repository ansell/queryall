/**
 * 
 */
package org.queryall.impl.rdfrule;

import java.util.Collection;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.NormalisationRuleParser;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SpinConstraintRuleImplParser implements NormalisationRuleParser
{
    @Override
    public NormalisationRule createObject(final Collection<Statement> rdfStatements, final URI subjectKey,
            final int modelVersion) throws IllegalArgumentException
    {
        try
        {
            return new SpinConstraintRuleImpl(rdfStatements, subjectKey, modelVersion);
        }
        catch(final OpenRDFException ex)
        {
            throw new IllegalArgumentException("Could not parse the given RDF statements into a normalisation rule", ex);
        }
    }
}
