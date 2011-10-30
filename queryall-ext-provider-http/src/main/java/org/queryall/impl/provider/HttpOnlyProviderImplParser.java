/**
 * 
 */
package org.queryall.impl.provider;

import java.util.Collection;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.provider.Provider;
import org.queryall.api.provider.ProviderParser;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class HttpOnlyProviderImplParser implements ProviderParser
{
    @Override
    public Provider createObject(final Collection<Statement> rdfStatements, final URI subjectKey, final int modelVersion)
        throws IllegalArgumentException
    {
        try
        {
            return new HttpOnlyProviderImpl(rdfStatements, subjectKey, modelVersion);
        }
        catch(final OpenRDFException ex)
        {
            throw new IllegalArgumentException("Could not parse the given RDF statements into a provider", ex);
        }
    }
}
