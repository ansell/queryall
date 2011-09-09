/**
 * 
 */
package org.queryall.impl.querytype;

import java.util.Collection;
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
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
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
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = RdfInputQueryTypeImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RdfInputQueryTypeImpl.log.isInfoEnabled();
    

    private static final Set<URI> RDF_INPUT_QUERY_TYPE_IMPL_TYPES = new HashSet<URI>();
    
    public static Set<URI> myTypes()
    {
        return RDF_INPUT_QUERY_TYPE_IMPL_TYPES;
    }
    
    static
    {
        RDF_INPUT_QUERY_TYPE_IMPL_TYPES.add(QueryTypeSchema.getQueryTypeUri());
        RDF_INPUT_QUERY_TYPE_IMPL_TYPES.add(RdfInputQueryTypeSchema.getRdfInputQueryTypeUri());
        RDF_INPUT_QUERY_TYPE_IMPL_TYPES.add(RdfOutputQueryTypeSchema.getRdfOutputQueryTypeUri());
        RDF_INPUT_QUERY_TYPE_IMPL_TYPES.add(SparqlProcessorQueryTypeSchema.getSparqlProcessorQueryTypeUri());
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
    public RdfInputQueryTypeImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion)
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
                if(_TRACE)
                {
                    log.trace("QueryType: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(RdfInputQueryTypeSchema.getQuerySparqlInputSelect()))
            {
                this.setSparqlInputSelect(nextStatement.getObject().stringValue());
            }
            else
            {
                this.unrecognisedStatements.add(nextStatement);
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
        return RdfInputQueryTypeImpl.myTypes();
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
            
            con.add(queryInstanceUri, RdfInputQueryTypeSchema.getQuerySparqlInputSelect(), sparqlInputSelectLiteral, keyToUse);
            
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

    /**
     * Returns a list of bindings for parameter names based on the input
     * 
     * if a binding is explicitly set to a valid in the map, it will be returned,
     * otherwise, a SPARQL query will be performed in the RDF document obtained from the 
     * queryParameters.get("query") String
     */
    @Override
    public Map<String, List<String>> matchesForQueryParameters(Map<String, String> queryParameters)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean matchesQueryParameters(Map<String, String> queryString)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setSparqlInputSelect(String sparqlInputSelect)
    {
        this.sparqlInputSelect = sparqlInputSelect;
    }

    @Override
    public String getSparqlInputSelect()
    {
        return sparqlInputSelect;
    }

}
