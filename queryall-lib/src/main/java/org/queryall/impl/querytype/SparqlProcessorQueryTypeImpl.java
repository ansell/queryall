/**
 * 
 */
package org.queryall.impl.querytype;

import java.util.Collection;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.querytype.SparqlProcessorQueryType;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class SparqlProcessorQueryTypeImpl extends QueryTypeImpl implements SparqlProcessorQueryType
{
    
    /**
     * 
     */
    public SparqlProcessorQueryTypeImpl()
    {
        super();
    }
    
    /**
     * @param inputStatements
     * @param keyToUse
     * @param modelVersion
     * @throws OpenRDFException
     */
    public SparqlProcessorQueryTypeImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        
    }
    
    @Override
    public String getSparqlTemplateString()
    {
        // Wrappers around the getTemplateString function for now
        return this.getTemplateString();
    }
    
    @Override
    public void setSparqlTemplateString(final String templateString)
    {
        // Wrappers around the setTemplateString function for now
        this.setTemplateString(templateString);
    }
    
}
