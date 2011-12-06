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
import org.queryall.api.utils.Constants;

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
    public Object parseProcessorQuery(final String query)
    {
        return query;
    }
    
    @Override
    public Map<String, Object> processQueryVariables(final Map<String, Object> queryVariables)
    {
        return queryVariables;
    }
    
    @Override
    public void setSparqlTemplateString(final String templateString)
    {
        // Wrappers around the setTemplateString function for now
        this.setProcessingTemplateString(templateString);
    }
    
    @Override
    public String substituteQueryVariables(final Map<String, Object> processedQueryVariables)
    {
        if(processedQueryVariables.containsKey(Constants.QUERY))
        {
            return (String)processedQueryVariables.get(Constants.QUERY);
        }
        else
        {
            return "";
        }
    }
    
}
