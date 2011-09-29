/**
 * 
 */
package org.queryall.impl.querytype;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.querytype.QueryTypeSchema;
import org.queryall.api.querytype.RdfInputQueryType;
import org.queryall.api.querytype.RdfInputQueryTypeSchema;
import org.queryall.api.querytype.RdfOutputQueryType;
import org.queryall.api.querytype.RdfOutputQueryTypeSchema;
import org.queryall.api.querytype.SparqlProcessorQueryType;
import org.queryall.api.querytype.SparqlProcessorQueryTypeSchema;
import org.queryall.api.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfInputQueryTypeImpl extends QueryTypeImpl implements RdfInputQueryType, SparqlProcessorQueryType,
        RdfOutputQueryType
{
    private static final Logger log = LoggerFactory.getLogger(RdfInputQueryTypeImpl.class);
    private static final boolean _TRACE = RdfInputQueryTypeImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = RdfInputQueryTypeImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RdfInputQueryTypeImpl.log.isInfoEnabled();
    
    private static final Set<URI> RDF_INPUT_QUERY_TYPE_IMPL_TYPES = new HashSet<URI>();
    
    static
    {
        RdfInputQueryTypeImpl.RDF_INPUT_QUERY_TYPE_IMPL_TYPES.add(QueryTypeSchema.getQueryTypeUri());
        RdfInputQueryTypeImpl.RDF_INPUT_QUERY_TYPE_IMPL_TYPES.add(RdfInputQueryTypeSchema.getRdfInputQueryTypeUri());
        RdfInputQueryTypeImpl.RDF_INPUT_QUERY_TYPE_IMPL_TYPES.add(RdfOutputQueryTypeSchema.getRdfOutputQueryTypeUri());
        RdfInputQueryTypeImpl.RDF_INPUT_QUERY_TYPE_IMPL_TYPES.add(SparqlProcessorQueryTypeSchema
                .getSparqlProcessorQueryTypeUri());
    }
    
    public static Set<URI> myTypes()
    {
        return RdfInputQueryTypeImpl.RDF_INPUT_QUERY_TYPE_IMPL_TYPES;
    }
    
    private String sparqlInputSelect;
    
    /**
     * 
     */
    public RdfInputQueryTypeImpl()
    {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param inputStatements
     * @param keyToUse
     * @param modelVersion
     * @throws OpenRDFException
     */
    public RdfInputQueryTypeImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
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
                            || nextStatement.getObject().equals(RdfInputQueryTypeSchema.getRdfInputQueryTypeUri())
                            || nextStatement.getObject().equals(
                                    SparqlProcessorQueryTypeSchema.getSparqlProcessorQueryTypeUri()) || nextStatement
                            .getObject().equals(RdfOutputQueryTypeSchema.getRdfOutputQueryTypeUri())))
            {
                if(RdfInputQueryTypeImpl._TRACE)
                {
                    RdfInputQueryTypeImpl.log.trace("RdfInputQueryTypeImpl: found valid type predicate for URI: "
                            + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(RdfInputQueryTypeSchema.getQuerySparqlInputSelect()))
            {
                this.setSparqlInputSelect(nextStatement.getObject().stringValue());
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
    }
    
    /**
     * Returns a map of bindings as strings mapped to lists of strings.
     * 
     * @param myRepository
     *            The repository to
     * @return
     */
    private Map<String, List<String>> getBindingsForInput(final String input, final RDFFormat inputFormat)
    {
        final Map<String, List<String>> results = new HashMap<String, List<String>>();
        
        final Repository myRepository = new SailRepository(new MemoryStore());
        
        RepositoryConnection addConnection = null;
        try
        {
            myRepository.initialize();
            
            addConnection = myRepository.getConnection();
            
            addConnection.add(new StringReader(input), "http://purl.org/queryall/baseUri#", inputFormat);
            
            addConnection.commit();
        }
        catch(final RDFParseException e1)
        {
            throw new RuntimeException(
                    "Could not initialise in memory repository with the query document due to an RDF parsing exception",
                    e1);
        }
        catch(final RepositoryException e1)
        {
            throw new RuntimeException(
                    "Could not initialise in memory repository with the query document due to a Repository exception",
                    e1);
        }
        catch(final IOException e1)
        {
            throw new RuntimeException(
                    "Could not initialise in memory repository with the query document due to an IO exception", e1);
        }
        finally
        {
            if(addConnection != null)
            {
                try
                {
                    addConnection.close();
                }
                catch(final RepositoryException e)
                {
                    RdfInputQueryTypeImpl.log.error("Found repository exception while trying to close add connection",
                            e);
                }
            }
        }
        
        RepositoryConnection selectConnection = null;
        try
        {
            selectConnection = myRepository.getConnection();
            
            final TupleQueryResult tupleResult =
                    selectConnection.prepareTupleQuery(QueryLanguage.SPARQL, this.getSparqlInputSelect()).evaluate();
            
            int selectBindings = 0;
            int actualBindings = 0;
            
            while(tupleResult.hasNext())
            {
                final BindingSet nextBinding = tupleResult.next();
                
                selectBindings++;
                for(final String nextExpectedBinding : this.getExpectedInputParameters())
                {
                    if(nextBinding.hasBinding(nextExpectedBinding))
                    {
                        final String nextBindingValue =
                                nextBinding.getBinding(nextExpectedBinding).getValue().stringValue();
                        
                        if(results.containsKey(nextExpectedBinding))
                        {
                            results.get(nextExpectedBinding).add(nextBindingValue);
                        }
                        else
                        {
                            final List<String> nextList = new ArrayList<String>(5);
                            nextList.add(nextBindingValue);
                            
                            results.put(nextExpectedBinding, nextList);
                        }
                        actualBindings++;
                    }
                }
            }
            
            if(RdfInputQueryTypeImpl._DEBUG)
            {
                RdfInputQueryTypeImpl.log.debug("RdfInputQueryTypeImpl: found " + selectBindings
                        + " results sets with a total of " + actualBindings + " bound values");
            }
        }
        catch(final org.openrdf.repository.RepositoryException rex)
        {
            RdfInputQueryTypeImpl.log.error("RdfInputQueryTypeImpl: RepositoryException exception adding statements",
                    rex);
        }
        catch(final QueryEvaluationException e)
        {
            if(this.getKey() != null)
            {
                RdfInputQueryTypeImpl.log.error("QueryEvaluationException queryType.getKey()="
                        + this.getKey().stringValue(), e);
            }
            else
            {
                RdfInputQueryTypeImpl.log.error("QueryEvaluationException queryType unknown selectQuery=" + input, e);
            }
        }
        catch(final MalformedQueryException e)
        {
            if(this.getKey() != null)
            {
                RdfInputQueryTypeImpl.log.error("MalformedQueryException queryType.getKey()="
                        + this.getKey().stringValue(), e);
            }
            else
            {
                RdfInputQueryTypeImpl.log.error(
                        "MalformedQueryException queryType unknown selectQuery=" + this.getSparqlInputSelect(), e);
            }
        }
        finally
        {
            try
            {
                if(selectConnection != null)
                {
                    selectConnection.close();
                }
            }
            catch(final RepositoryException e)
            {
                RdfInputQueryTypeImpl.log.error("RepositoryException while trying to close selectConnection", e);
            }
        }
        
        return results;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Set<URI> getElementTypes()
    {
        return RdfInputQueryTypeImpl.myTypes();
    }
    
    @Override
    public String getSparqlInputSelect()
    {
        return this.sparqlInputSelect;
    }
    
    /**
     * Returns a list of bindings for parameter names based on the input
     * 
     * if a binding is explicitly set to a valid in the map, it will be returned, otherwise, a
     * SPARQL query will be performed in the RDF document obtained from the
     * queryParameters.get("query") String
     */
    @Override
    public Map<String, List<String>> matchesForQueryParameters(final Map<String, String> queryParameters)
    {
        return this.getBindingsForInput(queryParameters.get(Constants.QUERY),
                RDFFormat.forMIMEType(queryParameters.get("inputMimeType"), RDFFormat.RDFXML));
    }
    
    @Override
    public boolean matchesQueryParameters(final Map<String, String> queryParameters)
    {
        return (this.getBindingsForInput(queryParameters.get(Constants.QUERY),
                RDFFormat.forMIMEType(queryParameters.get("inputMimeType"), RDFFormat.RDFXML)).size() > 0);
    }
    
    @Override
    public void setSparqlInputSelect(final String sparqlInputSelect)
    {
        this.sparqlInputSelect = sparqlInputSelect;
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
            
            final Literal sparqlInputSelectLiteral = f.createLiteral(this.getSparqlInputSelect());
            
            con.setAutoCommit(false);
            
            con.add(queryInstanceUri, RdfInputQueryTypeSchema.getQuerySparqlInputSelect(), sparqlInputSelectLiteral,
                    keyToUse);
            
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
            
            RdfInputQueryTypeImpl.log.error("RepositoryException: " + re.getMessage());
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
