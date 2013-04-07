/**
 * 
 */
package org.queryall.impl.querytype;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.querytype.QueryTypeSchema;
import org.queryall.api.querytype.RdfOutputQueryType;
import org.queryall.api.querytype.RdfOutputQueryTypeSchema;
import org.queryall.api.querytype.RegexInputQueryType;
import org.queryall.api.querytype.RegexInputQueryTypeSchema;
import org.queryall.api.querytype.SparqlProcessorQueryType;
import org.queryall.api.querytype.SparqlProcessorQueryTypeSchema;
import org.queryall.api.utils.Constants;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RegexInputQueryTypeImpl extends SparqlProcessorQueryTypeImpl implements RegexInputQueryType,
        SparqlProcessorQueryType, RdfOutputQueryType
{
    private static final Logger log = LoggerFactory.getLogger(RegexInputQueryTypeImpl.class);
    private static final boolean TRACE = RegexInputQueryTypeImpl.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = RegexInputQueryTypeImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = RegexInputQueryTypeImpl.log.isInfoEnabled();
    
    private static final Set<URI> REGEX_INPUT_QUERY_TYPE_IMPL_TYPES = new HashSet<URI>();
    
    static
    {
        RegexInputQueryTypeImpl.REGEX_INPUT_QUERY_TYPE_IMPL_TYPES.add(QueryTypeSchema.getQueryTypeUri());
        RegexInputQueryTypeImpl.REGEX_INPUT_QUERY_TYPE_IMPL_TYPES.add(RegexInputQueryTypeSchema
                .getRegexInputQueryTypeUri());
        RegexInputQueryTypeImpl.REGEX_INPUT_QUERY_TYPE_IMPL_TYPES.add(RdfOutputQueryTypeSchema
                .getRdfOutputQueryTypeUri());
        RegexInputQueryTypeImpl.REGEX_INPUT_QUERY_TYPE_IMPL_TYPES.add(SparqlProcessorQueryTypeSchema
                .getSparqlProcessorQueryTypeUri());
    }
    
    public static Set<URI> myTypes()
    {
        return RegexInputQueryTypeImpl.REGEX_INPUT_QUERY_TYPE_IMPL_TYPES;
    }
    
    protected String inputRegex = "";
    protected Pattern inputRegexPattern = null;
    
    /**
     * 
     */
    public RegexInputQueryTypeImpl()
    {
        super();
    }
    
    /**
     * @param inputStatements
     * @param keyToUse
     * @param modelVersion
     * @throws OpenRDFException
     */
    public RegexInputQueryTypeImpl(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = this.resetUnrecognisedStatements();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && (nextStatement.getObject().equals(QueryTypeSchema.getQueryTypeUri())
                            || nextStatement.getObject().equals(RegexInputQueryTypeSchema.getRegexInputQueryTypeUri())
                            || nextStatement.getObject().equals(
                                    SparqlProcessorQueryTypeSchema.getSparqlProcessorQueryTypeUri()) || nextStatement
                            .getObject().equals(RdfOutputQueryTypeSchema.getRdfOutputQueryTypeUri())))
            {
                if(RegexInputQueryTypeImpl.TRACE)
                {
                    RegexInputQueryTypeImpl.log.trace("QueryType: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(RegexInputQueryTypeSchema.getQueryInputRegex()))
            {
                this.setInputRegex(nextStatement.getObject().stringValue());
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Set<URI> getElementTypes()
    {
        return RegexInputQueryTypeImpl.myTypes();
    }
    
    @Override
    public String getInputRegex()
    {
        return this.inputRegex;
    }
    
    @Override
    public Pattern getInputRegexPattern()
    {
        if(this.inputRegexPattern == null && this.inputRegex != null)
        {
            this.inputRegexPattern = Pattern.compile(this.inputRegex);
        }
        
        return this.inputRegexPattern;
    }
    
    @Override
    public Map<String, List<String>> matchesForQueryParameters(final Map<String, String> nextQueryParameters)
    {
        if(nextQueryParameters.containsKey(Constants.QUERY))
        {
            return StringUtils.matchesForRegexOnString(this.getInputRegexPattern(), this.inputRegex,
                    nextQueryParameters.get(Constants.QUERY));
        }
        else
        {
            throw new IllegalArgumentException("Query Parameters must include a value for key='" + Constants.QUERY
                    + "'");
        }
    }
    
    @Override
    public boolean matchesQueryParameters(final Map<String, String> nextQueryParameters)
    {
        if(nextQueryParameters.containsKey(Constants.QUERY))
        {
            final boolean result =
                    StringUtils.matchesRegexOnString(this.getInputRegexPattern(), this.inputRegex,
                            nextQueryParameters.get(Constants.QUERY));
            
            if(RegexInputQueryTypeImpl.TRACE && result)
            {
                RegexInputQueryTypeImpl.log.trace("Returning true for matchesQueryParameters key="
                        + this.getKey().stringValue());
            }
            
            return result;
        }
        else
        {
            throw new IllegalArgumentException("Query Parameters must include a value for key='query'");
        }
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.InputQueryType#parseInputs(java.util.Map)
     */
    @Override
    public Map<String, Object> parseInputs(final Map<String, Object> inputParameterMap)
    {
        return inputParameterMap;
    }
    
    @Override
    public void setInputRegex(final String nextInputRegex)
    {
        this.inputRegex = nextInputRegex;
        this.inputRegexPattern = Pattern.compile(nextInputRegex);
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... keyToUse)
        throws OpenRDFException
    {
        super.toRdf(myRepository, modelVersion, keyToUse);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            // create some resources and literals to make statements out of
            final URI queryInstanceUri = this.getKey();
            
            final Literal inputRegexLiteral = f.createLiteral(this.inputRegex);
            
            con.begin();
            
            con.add(queryInstanceUri, RegexInputQueryTypeSchema.getQueryInputRegex(), inputRegexLiteral, keyToUse);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
            {
                con.rollback();
            }
            
            RegexInputQueryTypeImpl.log.error("RepositoryException: " + re.getMessage());
        }
        finally
        {
            if(con != null)
            {
                con.close();
            }
        }
        
        return false;
    }
}
