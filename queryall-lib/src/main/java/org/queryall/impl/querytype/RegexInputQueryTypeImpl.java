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
public class RegexInputQueryTypeImpl extends QueryTypeImpl implements RegexInputQueryType, SparqlProcessorQueryType,
        RdfOutputQueryType
{
    private static final Logger log = LoggerFactory.getLogger(RegexInputQueryTypeImpl.class);
    private static final boolean _TRACE = RegexInputQueryTypeImpl.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = RegexInputQueryTypeImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RegexInputQueryTypeImpl.log.isInfoEnabled();
    
    private static final Set<URI> REGEX_INPUT_QUERY_TYPE_IMPL_TYPES = new HashSet<URI>();
    
    public static Set<URI> myTypes()
    {
        return REGEX_INPUT_QUERY_TYPE_IMPL_TYPES;
    }
    
    static
    {
        REGEX_INPUT_QUERY_TYPE_IMPL_TYPES.add(QueryTypeSchema.getQueryTypeUri());
        REGEX_INPUT_QUERY_TYPE_IMPL_TYPES.add(RegexInputQueryTypeSchema.getRegexInputQueryTypeUri());
        REGEX_INPUT_QUERY_TYPE_IMPL_TYPES.add(RdfOutputQueryTypeSchema.getRdfOutputQueryTypeUri());
        REGEX_INPUT_QUERY_TYPE_IMPL_TYPES.add(SparqlProcessorQueryTypeSchema.getSparqlProcessorQueryTypeUri());
    }

    protected String inputRegex = "";
    protected Pattern inputRegexPattern = null;
    
    
    /**
     * 
     */
    public RegexInputQueryTypeImpl()
    {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param inputStatements
     * @param keyToUse
     * @param modelVersion
     * @throws OpenRDFException
     */
    public RegexInputQueryTypeImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);

        final Collection<Statement> currentUnrecognisedStatements = new HashSet<Statement>();
        
        currentUnrecognisedStatements.addAll(this.getUnrecognisedStatements());
        
        this.unrecognisedStatements = new HashSet<Statement>();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && (nextStatement.getObject().equals(QueryTypeSchema.getQueryTypeUri())
                            || nextStatement.getObject().equals(RegexInputQueryTypeSchema.getRegexInputQueryTypeUri())
                            || nextStatement.getObject().equals(
                                    SparqlProcessorQueryTypeSchema.getSparqlProcessorQueryTypeUri()) || nextStatement
                            .getObject().equals(RdfOutputQueryTypeSchema.getRdfOutputQueryTypeUri())))
            {
                if(_TRACE)
                {
                    log.trace("QueryType: found valid type predicate for URI: " + keyToUse);
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
    public void setInputRegex(final String nextInputRegex)
    {
        this.inputRegex = nextInputRegex;
        this.inputRegexPattern = Pattern.compile(nextInputRegex);
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
            throw new IllegalArgumentException("Query Parameters must include a value for key='"+Constants.QUERY+"'");
        }
    }
    
    @Override
    public boolean matchesQueryParameters(final Map<String, String> nextQueryParameters)
    {
        if(nextQueryParameters.containsKey(Constants.QUERY))
        {
            boolean result = StringUtils.matchesRegexOnString(this.getInputRegexPattern(), this.inputRegex,
                    nextQueryParameters.get(Constants.QUERY));
            
            if(_TRACE && result)
            {
                log.trace("Returning true for matchesQueryParameters key="+this.getKey().stringValue());
            }
            
            return result;
        }
        else
        {
            throw new IllegalArgumentException("Query Parameters must include a value for key='query'");
        }
        
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super.toRdf(myRepository, keyToUse, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            // create some resources and literals to make statements out of
            final URI queryInstanceUri = this.getKey();
            
            final Literal inputRegexLiteral = f.createLiteral(this.inputRegex);
            
            
            con.setAutoCommit(false);
            
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
