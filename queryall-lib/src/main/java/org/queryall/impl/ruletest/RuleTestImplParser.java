/**
 * 
 */
package org.queryall.impl.ruletest;

import java.util.Collection;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.ruletest.RuleTestParser;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RuleTestImplParser implements RuleTestParser
{
    @Override
    public RuleTest createObject(final Collection<Statement> rdfStatements, final URI subjectKey, final int modelVersion)
        throws IllegalArgumentException
    {
        try
        {
            return new RuleTestImpl(rdfStatements, subjectKey, modelVersion);
        }
        catch(final OpenRDFException ex)
        {
            throw new IllegalArgumentException("Could not parse the given RDF statements into a rule test", ex);
        }
    }
}
