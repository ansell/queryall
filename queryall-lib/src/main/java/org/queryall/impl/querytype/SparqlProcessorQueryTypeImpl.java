/**
 * 
 */
package org.queryall.impl.querytype;

import java.util.Collection;
import java.util.Map;

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
    public SparqlProcessorQueryTypeImpl(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
    }
    
    @Override
    public String getSparqlTemplateString()
    {
        // Wrappers around the getTemplateString function for now
        return this.getProcessingTemplateString();
    }
    
    @Override
    public void setSparqlTemplateString(final String templateString)
    {
        // Wrappers around the setTemplateString function for now
        this.setProcessingTemplateString(templateString);
    }
    
    @Override
    public Map<String, Object> processQueryVariables(Map<String, Object> queryVariables)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String substituteQueryVariables(Map<String, Object> processedQueryVariables)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object parseProcessorQuery(String query)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
