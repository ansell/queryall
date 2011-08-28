/**
 * 
 */
package org.queryall.impl.profile;

import java.util.Collection;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.project.Project;
import org.queryall.api.project.ProjectParser;
import org.queryall.impl.project.ProjectImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProjectImplParser implements ProjectParser
{
    @Override
    public Project createObject(final Collection<Statement> rdfStatements, final URI subjectKey, final int modelVersion)
        throws IllegalArgumentException
    {
        try
        {
            return new ProjectImpl(rdfStatements, subjectKey, modelVersion);
        }
        catch(final OpenRDFException ex)
        {
            throw new IllegalArgumentException("Could not parse the given RDF statements into a project", ex);
        }
    }
}
