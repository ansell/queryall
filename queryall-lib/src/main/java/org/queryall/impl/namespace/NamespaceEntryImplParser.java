/**
 * 
 */
package org.queryall.impl.namespace;

import java.util.Collection;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.namespace.NamespaceEntryParser;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class NamespaceEntryImplParser implements NamespaceEntryParser
{
    @Override
    public NamespaceEntry createObject(final Collection<Statement> rdfStatements, final URI subjectKey,
            final int modelVersion) throws IllegalArgumentException
    {
        try
        {
            return new NamespaceEntryImpl(rdfStatements, subjectKey, modelVersion);
        }
        catch(final OpenRDFException ex)
        {
            throw new IllegalArgumentException("Could not parse the given RDF statements into a namespace entry", ex);
        }
    }
}
