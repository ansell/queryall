
package org.queryall.helpers;

import java.net.URL;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.BooleanLiteralImpl;
import org.openrdf.model.impl.CalendarLiteralImpl;
import org.openrdf.model.impl.IntegerLiteralImpl;
import org.openrdf.model.impl.NumericLiteralImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.memory.model.BooleanMemLiteral;
import org.openrdf.sail.memory.model.IntegerMemLiteral;
import org.openrdf.sail.memory.model.CalendarMemLiteral;

import org.queryall.api.BaseQueryAllInterface;
import org.queryall.api.NamespaceEntry;
import org.queryall.api.NormalisationRule;
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.api.QueryAllConfiguration;
import org.queryall.api.QueryType;
import org.queryall.api.RuleTest;
import org.queryall.blacklist.BlacklistController;
import org.queryall.impl.HttpProviderImpl;
import org.queryall.impl.NamespaceEntryImpl;
import org.queryall.impl.ProfileImpl;
import org.queryall.impl.ProviderImpl;
import org.queryall.impl.QueryTypeImpl;
import org.queryall.impl.RegexNormalisationRuleImpl;
import org.queryall.impl.RuleTestImpl;
import org.queryall.impl.SparqlNormalisationRuleImpl;
import org.queryall.impl.XsltNormalisationRuleImpl;
import org.queryall.queryutils.HttpUrlQueryRunnable;
import org.queryall.queryutils.QueryBundle;
import org.queryall.queryutils.RdfFetchController;
import org.queryall.queryutils.RdfFetcherQueryRunnable;
import org.queryall.queryutils.RdfFetcherUriQueryRunnable;


/**
 * A utility class to deal with RDF data and resolve RDF queries
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class RdfUtils
{
    public static final Logger log = Logger.getLogger(RdfUtils.class
            .getName());
    public static final boolean _TRACE = RdfUtils.log.isTraceEnabled();
    public static final boolean _DEBUG = RdfUtils.log.isDebugEnabled();
    public static final boolean _INFO = RdfUtils.log.isInfoEnabled();
    
    
    public static void copyAllStatementsToRepository(Repository destination, Repository source)
    {
        RepositoryConnection mySourceConnection = null;
        RepositoryConnection myDestinationConnection = null;
        
        try
        {
            mySourceConnection = source.getConnection();
            if(_DEBUG)
            {
                log.debug("RdfUtils.copyAllStatementsToRepository: mySourceConnection.size()="+mySourceConnection.size());
            }
            myDestinationConnection = destination.getConnection();
            myDestinationConnection.add(mySourceConnection.getStatements(null, null, null, true));
            
            myDestinationConnection.commit();
            if(_DEBUG)
            {
                log.debug("RdfUtils.copyAllStatementsToRepository: myDestinationConnection.size()="+myDestinationConnection.size());
            }
        }
        catch(Exception ex)
        {
            log.error("RdfUtils.copyAllStatementsToRepository", ex);
        }
        finally
        {
            if(mySourceConnection != null)
            {
                try
                {
                    mySourceConnection.close();
                }
                catch(Exception ex)
                {
                    log.error("mySourceConnection",ex);
                }
            }
            if(myDestinationConnection != null)
            {
                try
                {
                    myDestinationConnection.close();
                }
                catch(Exception ex)
                {
                    log.error("myDestinationConnection",ex);
                }
            }
        }

    }
    
    public static Collection<QueryType> fetchQueryTypeByKey(String hostToUse, URI nextQueryKey, int modelVersion, Settings localSettings) throws InterruptedException
    {
        QueryBundle nextQueryBundle = new QueryBundle();
        
        HttpProviderImpl dummyProvider = new HttpProviderImpl();
        
        Collection<String> endpointUrls = new HashSet<String>();
        
        // if(nextQueryKey.startsWith(localSettings.getDefaultHostAddress()))
        // {
            String namespaceAndIdentifier = nextQueryKey.stringValue().substring(localSettings.getDefaultHostAddress().length());
            
            List<String> nsAndIdList = StringUtils.getNamespaceAndIdentifier(namespaceAndIdentifier, localSettings);
            
            if(nsAndIdList.size() == 2)
            {
                endpointUrls.add(hostToUse+new QueryTypeImpl().getDefaultNamespace()+localSettings.getStringProperty("separator", ":")+StringUtils.percentEncode(nsAndIdList.get(1)));
                nextQueryBundle.setQueryEndpoint(hostToUse+new QueryTypeImpl().getDefaultNamespace()+localSettings.getStringProperty("separator", ":")+StringUtils.percentEncode(nsAndIdList.get(1)));
            }
        // }
        // else
        // {
            // dummyProvider.endpointUrls.add(hostToUse+RdfUtils.percentEncode(nextQueryKey.substring(nextQueryKey.indexOf(new QueryTypeImpl().getDefaultNamespace()))));		
            // nextQueryBundle.queryEndpoint = hostToUse+RdfUtils.percentEncode(nextQueryKey.substring(nextQueryKey.indexOf(new QueryTypeImpl().getDefaultNamespace())));
        // }
        
        dummyProvider.setEndpointUrls(endpointUrls);
        dummyProvider.setEndpointMethod(HttpProviderImpl.getProviderHttpGetUrl());
        dummyProvider.setKey(hostToUse+localSettings.getNamespaceForProvider()+localSettings.getStringProperty("separator", ":")+StringUtils.percentEncode(namespaceAndIdentifier));
        dummyProvider.setIsDefaultSource(true);
        
        nextQueryBundle.setProvider(dummyProvider);
        
        QueryType dummyQuery = new QueryTypeImpl();
        
        dummyQuery.setKey(hostToUse+localSettings.getNamespaceForQueryType()+localSettings.getStringProperty("separator", ":")+StringUtils.percentEncode(namespaceAndIdentifier));
        dummyQuery.setTitle("$$__queryfetch__$$");
        dummyQuery.setIncludeDefaults(true);
        
        nextQueryBundle.setQueryType(dummyQuery);
        
        Collection<QueryBundle> queryBundles = new HashSet<QueryBundle>();
        
        queryBundles.add(nextQueryBundle);
        
        return getQueryTypesForQueryBundles(queryBundles, modelVersion, localSettings);
    }
    
    public static Collection<QueryType> fetchQueryTypeByKey(
        URI nextQueryKey, boolean useSparqlGraph, 
        String sparqlGraphUri, String sparqlEndpointUrl, int modelVersion, Settings localSettings)
    {
        String constructQueryString = RdfUtils.getConstructQueryForKey(
            nextQueryKey, useSparqlGraph, sparqlGraphUri);
        
        QueryBundle nextQueryBundle = new QueryBundle();
        
        HttpProviderImpl dummyProvider = new HttpProviderImpl();
        
        Collection<String> endpointUrls = new HashSet<String>();
        
        endpointUrls.add(sparqlEndpointUrl);
        
        dummyProvider.setEndpointUrls(endpointUrls);
        
        nextQueryBundle.setQueryEndpoint(sparqlEndpointUrl);
        
        dummyProvider.setEndpointMethod(HttpProviderImpl.getProviderHttpPostSparql());
        dummyProvider.setKey(localSettings.getDefaultHostAddress()+localSettings.getNamespaceForProvider()+localSettings.getStringProperty("separator", ":")+StringUtils.percentEncode(nextQueryKey.stringValue()));
        dummyProvider.setIsDefaultSource(true);
        
        nextQueryBundle.setOriginalProvider(dummyProvider);
        
        
        QueryType dummyQuery = new QueryTypeImpl();
        
        dummyQuery.setKey(localSettings.getDefaultHostAddress()+localSettings.getNamespaceForQueryType()+localSettings.getStringProperty("separator", ":")+StringUtils.percentEncode(nextQueryKey.stringValue()));
        dummyQuery.setTitle("$$__queryfetch__$$");
        dummyQuery.setIncludeDefaults(true);
        
        nextQueryBundle.setQueryType(dummyQuery);
        
        nextQueryBundle.setQuery(constructQueryString);
        
        Collection<QueryBundle> queryBundles = new HashSet<QueryBundle>();
        
        queryBundles.add(nextQueryBundle);
        
        return getQueryTypesForQueryBundles(queryBundles, modelVersion, localSettings);
    }
    
    public static RDFFormat getWriterFormat(String requestedContentType)
    {
    	if(requestedContentType.equals(Constants.TEXT_HTML))
    		return null;
    	
    	return Rio.getWriterFormatForMIMEType(requestedContentType, RDFFormat.RDFXML);
    }
    
    public static String findBestContentType(String requestedContentType, String preferredDisplayContentType, String fallback)
    {
        if(requestedContentType.equals(Constants.TEXT_HTML))
        {
            return requestedContentType;
        }
        
        // even if they request a random format, we need to make sure that Rio has a writer compatible with it, otherwise we revert to one of the defaults as a failsafe mechanism
        RDFFormat writerFormat = Rio.getWriterFormatForMIMEType(requestedContentType);
        
        if(writerFormat != null)
        {
            return requestedContentType;
        }
        else
        {
            writerFormat = Rio.getWriterFormatForMIMEType(preferredDisplayContentType);
            
            if(writerFormat != null)
            {
                return preferredDisplayContentType;
            }
            else
            {
                return fallback;
            }
        }
    }
    
    public static HttpUrlQueryRunnable generateHttpUrlSparqlDeleteThread(
        BaseQueryAllInterface rdfObject, 
        boolean useSparqlGraph, 
        String sparqlGraphUri,
        String sparqlEndpointMethod,
        String sparqlEndpointUrl,
        String acceptHeader, 
        String expectedReturnFormat,
        QueryAllConfiguration localSettings,
        BlacklistController localBlacklistController) 
    throws OpenRDFException
    {
        String sparqlInsertQuery = getSparulQueryForObject(rdfObject, false, true, useSparqlGraph, sparqlGraphUri);
        
        return generateHttpUrlSparqlThread(
         sparqlInsertQuery,
         sparqlEndpointMethod,
         sparqlEndpointUrl,
         acceptHeader, 
         expectedReturnFormat,
         localSettings,
         localBlacklistController);
    }
    
    public static HttpUrlQueryRunnable generateHttpUrlSparqlInsertThread(
        BaseQueryAllInterface rdfObject, 
        boolean isDelete,
        boolean useSparqlGraph, 
        String sparqlGraphUri,
        String sparqlEndpointMethod,
        String sparqlEndpointUrl,
        String acceptHeader, 
        String expectedReturnFormat,
        QueryAllConfiguration localSettings,
        BlacklistController localBlacklistController) 
    throws OpenRDFException
    {
        String sparqlInsertQuery = getSparulQueryForObject(rdfObject, true, isDelete, useSparqlGraph, sparqlGraphUri);
        
        return generateHttpUrlSparqlThread(
         sparqlInsertQuery,
         sparqlEndpointMethod,
         sparqlEndpointUrl,
         acceptHeader, 
         expectedReturnFormat,
         localSettings,
         localBlacklistController);
    }
    
    public static HttpUrlQueryRunnable generateHttpUrlSparqlThread(
        String sparqlQuery,
        String sparqlEndpointMethod,
        String sparqlEndpointUrl,
        String acceptHeader, 
        String expectedReturnFormat,
        QueryAllConfiguration localSettings,
        BlacklistController localBlacklistController) 
    {
        return new HttpUrlQueryRunnable(
                sparqlEndpointMethod,
                sparqlEndpointUrl, sparqlQuery,
                acceptHeader, expectedReturnFormat, localSettings, localBlacklistController);
    }
    
    /**
     * @param nextRepository
     * @return
     * @throws OpenRDFException
     */
    public static List<Statement> getAllStatementsFromRepository(
            Repository nextRepository) throws OpenRDFException
    {
        List<Statement> results = new ArrayList<Statement>(1);
        
        final RepositoryConnection con = nextRepository.getConnection();
        
        try
        {
        	results = con.getStatements((Resource)null, (URI)null, (Value)null, true).asList();
        	
        	Collections.sort(results, new org.queryall.helpers.StatementComparator());
        }
        catch (final OpenRDFException ordfe)
        {
            RdfUtils.log.error("RdfUtils.getAllStatementsFromRepository: outer caught exception ", ordfe);
            
            throw ordfe;
        }
        finally
        {
            con.close();
        }
        
        return results;
    }
    
    /**
     * @param nextValue
     * @return
     */
    public static boolean getBooleanFromValue(Value nextValue)
    {
        boolean result;
        
        try
        {
            result = ((BooleanLiteralImpl) nextValue).booleanValue();
        }
        catch (final ClassCastException cce)
        {
            try
            {
                result = ((BooleanMemLiteral) nextValue).booleanValue();
            }
            catch (final ClassCastException cce2)
            {
                // HACK for a Virtuoso bug where booleans are transformed into 0 and 1 and typed as integers instead of booleans
                if(nextValue instanceof org.openrdf.sail.memory.model.IntegerMemLiteral)
                {
                    int tempValue = ((IntegerMemLiteral) nextValue).intValue();
                    if(tempValue == 0)
                    {
                        return false;
                    }
                    
                    if(tempValue == 1)
                    {
                        return true;
                    }
                }

                
                if(RdfUtils._DEBUG)
                {
                    RdfUtils.log
                            .debug("RdfUtils.getBooleanFromValue: nextValue was not a typed boolean literal. Trying to parse it as a string... type="
                                    + nextValue.getClass().getName());
                }
                
                result = Boolean.parseBoolean(nextValue.toString());
            }
        }
        
        return result;
    }
    
    public static String getConstructQueryByType(BaseQueryAllInterface nextObject, int offset, int limit, boolean useSparqlGraph, String sparqlGraphUri, Settings localSettings)
    {
        return getConstructQueryByType(nextObject.getElementTypes(), offset, limit, useSparqlGraph, sparqlGraphUri, localSettings);
    }
    
    public static String getConstructQueryByType(Collection<URI> nextTypes, int offset, int limit, boolean useSparqlGraph, String sparqlGraphUri, Settings localSettings)
    {
        StringBuilder result = new StringBuilder();
        
        result.append("CONSTRUCT { ?s a ?type . ");
        
        int counter = 0;

        // TODO: change this to List<String> when titleProperties are ordered in the configuration
        Collection<URI> titleProperties = localSettings.getURIProperties("titleProperties");
        
        for(URI nextTitleUri : titleProperties)
        {
            result.append(" ?s <"+nextTitleUri.stringValue()+"> ?o"+counter+" . ");
            
            counter++;
        }
        
        result.append(" } WHERE { ");
        
        boolean firstType = true;
        
        for(URI nextTypeUri : nextTypes)
        {
        	if(!firstType)
        		result.append(" UNION ");
        	
            // need to open up the union pattern using this if there is more than one type
            if(nextTypes.size() > 1)
            	result.append(" { ");

            if(useSparqlGraph)
	        {
	            result.append(" GRAPH <" + sparqlGraphUri + "> { ");
	        }
	        
	        result.append(" ?s a ?type . ");
	        result.append(" FILTER(?type = ").append(nextTypeUri.toString()).append(" ) . ");
	        
	        counter = 0;
	        
	        for(URI nextTitleUri : titleProperties)
	        {
	            result.append("OPTIONAL{ ?s <"+nextTitleUri.stringValue()+"> ?o"+counter+" . }");
	            
	            counter++;
	        }
	        
	        if(useSparqlGraph)
	        {
	            result.append(" } ");
	        }
	        
	        firstType = false;
        }

        result.append(" } ");
        
        return result.toString();
    }
    
    public static String getConstructQueryForKey(URI nextKey, boolean useSparqlGraph, String sparqlGraphUri)
    {
        StringBuilder result = new StringBuilder();
        
        result.append("CONSTRUCT { <"+nextKey.stringValue()+"> ?p ?o . } WHERE { ");
        
        if(useSparqlGraph)
        {
            result.append(" GRAPH <" + sparqlGraphUri + "> { ");
        }
        
        result.append(" <"+nextKey.stringValue()+"> ?p ?o . ");
        
        if(useSparqlGraph)
        {
            result.append(" } ");
        }
        
        result.append(" } ");
        
        return result.toString();
    }
    
    public static String getConstructQueryForObject(BaseQueryAllInterface nextObject, boolean useSparqlGraph, String sparqlGraphUri)
    {
        return getConstructQueryForKey(nextObject.getKey(), useSparqlGraph, sparqlGraphUri);
    }
    
    /**
     * @param nextValue
     * @return
     */
    public static Date getDateTimeFromValue(Value nextValue) throws java.text.ParseException
    {
        Date result;
        
        try
        {
            result = ((CalendarLiteralImpl)nextValue).calendarValue().toGregorianCalendar().getTime();
        }
        catch (final ClassCastException cce)
        {
            try
            {
                result = ((CalendarMemLiteral) nextValue).calendarValue().toGregorianCalendar().getTime();
            }
            catch (final ClassCastException cce2)
            {
                if(RdfUtils._DEBUG)
                {
                    RdfUtils.log
                            .debug("RdfUtils.getDateTimeFromValue: nextValue was not a typed date time literal. Trying to parse it as a string... type="+ nextValue.getClass().getName());
                }
                try
                {
                    result = Constants.ISO8601UTC().parse(nextValue.toString());
                }
                catch(java.text.ParseException pe)
                {
                    log.error("Could not parse date: nextValue.toString="+nextValue.toString());
                    throw pe;
                }
            }
        }
        
        return result;
    }
    
    /**
     * @param nextRepository
     * @param predicateUris
     * @return
     * @throws OpenRDFException
     */
    public static Collection<String> getDistinctObjectUrisFromRepository(
            Repository nextRepository)//, Collection<String> predicateUris)
            throws OpenRDFException
    {
        final Collection<String> results = new HashSet<String>();
        
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log
                    .debug("RdfUtils.getDistinctObjectsFromRepository: entering method");
            RdfUtils.log.debug(nextRepository);
            // RdfUtils.log.debug(predicateUris);
        }
        
        final RepositoryConnection con = nextRepository.getConnection();
        
        // try
        // {
            // 
            // final ValueFactory f = nextRepository.getValueFactory();
            
            // for(final String nextInputPredicate : predicateUris)
            // {
                // if((nextInputPredicate == null)
                        // || nextInputPredicate.trim().equals(""))
                // {
                    // if(RdfUtils._DEBUG)
                    // {
                        // RdfUtils.log
                                // .debug("RdfUtils.getDistinctObjectsFromRepository: nextInputPredicate was null or empty");
                    // }
                    // 
                    // continue;
                // }
                // 
                try
                {
                    // final URI nextInputPredicateUri = f
                            // .createURI(nextInputPredicate);
                    
                    final String queryString = "SELECT DISTINCT ?object WHERE { ?subject ?predicate ?object . FILTER(isURI(?object)) }";
                    final TupleQuery tupleQuery = con.prepareTupleQuery(
                            QueryLanguage.SPARQL, queryString);
                    final TupleQueryResult queryResult = tupleQuery.evaluate();
                    
                    try
                    {
                        while(queryResult.hasNext())
                        {
                            final BindingSet bindingSet = queryResult.next();
                            final Value valueOfObject = bindingSet
                                    .getValue("object");
                            
                            if(RdfUtils._DEBUG)
                            {
                                RdfUtils.log
                                        .debug("Utilities: found object: valueOfObject="
                                                + valueOfObject);
                            }
                            
                            results.add(getUTF8StringValueFromSesameValue(valueOfObject));
                        }
                    }
                    finally
                    {
                        queryResult.close();
                    }
                }
                catch (final OpenRDFException ordfe)
                {
                    RdfUtils.log
                            .error("RdfUtils.getDistinctObjectsFromRepository: RDF exception",ordfe);
                }
                catch (final Exception ex)
                {
                    RdfUtils.log
                            .error("RdfUtils.getDistinctObjectsFromRepository: general exception",ex);
                }
            // }
        // }
        
        // catch(OpenRDFException ordfe)
        // {
        // log.error("RdfUtils.getDistinctObjectsFromRepository: error found");
        // throw ordfe;
        // }
        // finally
        // {
            // if(con != null)
            // {
                // con.close();
            // }
        // }
        
        return results;
    }

    /**
     * @param nextRepository
     * @param predicateUris
     * @return
     * @throws OpenRDFException
     */
    public static Collection<String> getDistinctSubjectsFromRepository(
            Repository nextRepository)//, Collection<String> predicateUris)
            throws OpenRDFException
    {
        final Collection<String> results = new HashSet<String>();
        
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log
                    .debug("RdfUtils.getDistinctSubjectsFromRepository: entering method");
            RdfUtils.log.debug(nextRepository);
            // RdfUtils.log.debug(predicateUris);
        }
        
        final RepositoryConnection con = nextRepository.getConnection();
        
        // try
        // {
            // 
            // final ValueFactory f = nextRepository.getValueFactory();
            
            // for(final String nextInputPredicate : predicateUris)
            // {
                // if((nextInputPredicate == null)
                        // || nextInputPredicate.trim().equals(""))
                // {
                    // if(RdfUtils._DEBUG)
                    // {
                        // RdfUtils.log
                                // .debug("RdfUtils.getDistinctSubjectsFromRepository: nextInputPredicate was null or empty");
                    // }
                    // 
                    // continue;
                // }
                // 
                try
                {
                    // final URI nextInputPredicateUri = f
                            // .createURI(nextInputPredicate);
                    
                    final String queryString = "SELECT DISTINCT ?subject WHERE { ?subject ?predicate ?object . }";
                    final TupleQuery tupleQuery = con.prepareTupleQuery(
                            QueryLanguage.SPARQL, queryString);
                    final TupleQueryResult queryResult = tupleQuery.evaluate();
                    
                    try
                    {
                        while(queryResult.hasNext())
                        {
                            final BindingSet bindingSet = queryResult.next();
                            final Value valueOfSubject = bindingSet
                                    .getValue("subject");
                            
                            if(RdfUtils._DEBUG)
                            {
                                RdfUtils.log
                                        .debug("Utilities: found subject: valueOfSubject="
                                                + valueOfSubject);
                            }
                            
                            results.add(getUTF8StringValueFromSesameValue(valueOfSubject));
                        }
                    }
                    finally
                    {
                        queryResult.close();
                    }
                }
                catch (final OpenRDFException ordfe)
                {
                    RdfUtils.log
                            .error("RdfUtils.getDistinctSubjectsFromRepository: RDF exception",ordfe);
                }
                catch (final Exception ex)
                {
                    RdfUtils.log
                            .error("RdfUtils.getDistinctSubjectsFromRepository: general exception",ex);
                }
            // }
        // }
        
        // catch(OpenRDFException ordfe)
        // {
        // log.error("RdfUtils.getDistinctSubjectsFromRepository: error found");
        // throw ordfe;
        // }
        // finally
        // {
            // if(con != null)
            // {
                // con.close();
            // }
        // }
        
        return results;
    }
    
    /**
     * @param nextRepository
     * @param predicateUris
     * @return
     * @throws OpenRDFException
     */
    public static Collection<String> getObjectUrisFromRepositoryByPredicateUris(
            Repository nextRepository, Collection<String> predicateUris)
            throws OpenRDFException
    {
        final Collection<String> results = new HashSet<String>();
        
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log
                    .debug("RdfUtils.getObjectUrisFromRepositoryByPredicateUris: entering method");
            RdfUtils.log.debug(nextRepository);
            RdfUtils.log.debug(predicateUris);
        }
        
        final RepositoryConnection con = nextRepository.getConnection();
        
        try
        {
            final ValueFactory f = Constants.valueFactory;
            
            for(final String nextInputPredicate : predicateUris)
            {
                if((nextInputPredicate == null)
                        || nextInputPredicate.trim().equals(""))
                {
                    if(RdfUtils._DEBUG)
                    {
                        RdfUtils.log
                                .debug("RdfUtils.getObjectUrisFromRepositoryByPredicateUris: nextInputPredicate was null or empty");
                    }
                    
                    continue;
                }
                
                try
                {
                    final URI nextInputPredicateUri = f
                            .createURI(nextInputPredicate);
                    
                    final String queryString = "SELECT DISTINCT ?object WHERE { ?subject <"
                            + nextInputPredicateUri.stringValue()
                            + "> ?object . FILTER(isURI(?object)) }";
                    final TupleQuery tupleQuery = con.prepareTupleQuery(
                            QueryLanguage.SPARQL, queryString);
                    final TupleQueryResult queryResult = tupleQuery.evaluate();
                    
                    try
                    {
                        while(queryResult.hasNext())
                        {
                            final BindingSet bindingSet = queryResult.next();
                            final Value valueOfObject = bindingSet
                                    .getValue("object");
                            
                            if(RdfUtils._DEBUG)
                            {
                                RdfUtils.log
                                        .debug("Utilities: found object: valueOfObject="
                                                + valueOfObject);
                            }
                            
                            results.add(getUTF8StringValueFromSesameValue(valueOfObject));
                        }
                    }
                    finally
                    {
                        queryResult.close();
                    }
                }
                catch (final OpenRDFException ordfe)
                {
                    RdfUtils.log
                            .error("RdfUtils.getObjectUrisFromRepositoryByPredicateUris: RDF exception found for nextInputPredicate="
                                    + nextInputPredicate);
                }
                catch (final Exception ex)
                {
                    RdfUtils.log
                            .error("RdfUtils.getObjectUrisFromRepositoryByPredicateUris: general exception found for nextInputPredicate="
                                    + nextInputPredicate);
                }
            }
        }
        
        // catch(OpenRDFException ordfe)
        // {
        // log.error("RdfUtils.getObjectUrisFromRepositoryByPredicateUris: error found");
        // throw ordfe;
        // }
        finally
        {
            if(con != null)
            {
                con.close();
            }
        }
        
        return results;
    }
    
    public static Collection<QueryType> getQueryTypesForQueryBundles(Collection<QueryBundle> queryBundles, int modelVersion, Settings localSettings)
    {
        RdfFetchController fetchController = new RdfFetchController(localSettings, BlacklistController.getDefaultController(), queryBundles);
        
        try
        {
            fetchController.fetchRdfForQueries();
        }
        catch(InterruptedException ie)
        {
            log.fatal("RdfUtils.getQueryTypesForQueryBundles: interrupted exception",ie);
            // throw ie;
        }
        
        Collection<RdfFetcherQueryRunnable> rdfResults = fetchController.getSuccessfulResults();
        
        Repository myRepository = null;
        RepositoryConnection myRepositoryConnection = null;
        try
        {
            myRepository = new SailRepository(new MemoryStore());
            myRepository.initialize();
            myRepositoryConnection = myRepository.getConnection();
            
            for(RdfFetcherQueryRunnable nextResult : rdfResults)
            {
                try
                {
                    RDFFormat nextReaderFormat = RDFFormat.forMIMEType(nextResult.getReturnedMIMEType());
                    
                    if(log.isDebugEnabled())
                    {
                        log.debug("RdfUtils.getQueryTypesForQueryBundles: nextReaderFormat for returnedContentType="+nextResult.getReturnedContentType()+" nextReaderFormat="+nextReaderFormat);
                    }
                    
                    if(nextReaderFormat == null)
                    {
                        nextReaderFormat = Rio.getParserFormatForMIMEType(localSettings.getStringProperty("assumedResponseContentType", Constants.APPLICATION_RDF_XML));
                        
                        if(nextReaderFormat == null)
                        {
                            log.error("RdfUtils.getQueryTypesForQueryBundles: Not attempting to parse result because Settings.getStringPropertyFromConfig(\"assumedResponseContentType\") isn't supported by Rio and the returned content type wasn't either nextResult.returnedMIMEType="+nextResult.getReturnedMIMEType()+" Settings.getStringPropertyFromConfig(\"assumedResponseContentType\")="+localSettings.getStringProperty("assumedResponseContentType", ""));
                            continue;
                        }
                        else
                        {
                            log.warn("RdfUtils.getQueryTypesForQueryBundles: readerFormat NOT matched for returnedMIMEType="+nextResult.getReturnedMIMEType()+" using configured preferred content type as fallback Settings.getStringPropertyFromConfig(\"assumedResponseContentType\")="+localSettings.getStringProperty("assumedResponseContentType", ""));
                        }
                    }
                    else if(log.isDebugEnabled())
                    {
                        log.debug("RdfUtils.getQueryTypesForQueryBundles: readerFormat matched for returnedMIMEType="+nextResult.getReturnedMIMEType());
                    }
                    
                    if(nextResult.getNormalisedResult().length() > 0)
                    {
                        myRepositoryConnection.add(new java.io.StringReader(nextResult.getNormalisedResult()), localSettings.getDefaultHostAddress(), nextReaderFormat);
                    }
                }
                catch(org.openrdf.rio.RDFParseException rdfpe)
                {
                    log.error("RdfUtils.getQueryTypesForQueryBundles: RDFParseException",rdfpe);
                }
                catch(org.openrdf.repository.RepositoryException re)
                {
                    log.error("RdfUtils.getQueryTypesForQueryBundles: RepositoryException inner",re);
                }
                catch(java.io.IOException ioe)
                {
                    log.error("RdfUtils.getQueryTypesForQueryBundles: IOException",ioe);
                }
            } // end for(RdfFetcherQueryRunnable nextResult : rdfResults)
        }
        catch(org.openrdf.repository.RepositoryException re)
        {
            log.error("RdfUtils.getQueryTypesForQueryBundles: RepositoryException outer",re);
        }
        finally
        {
            try
            {
                if(myRepositoryConnection != null)
                    myRepositoryConnection.close();
            }
            catch(org.openrdf.repository.RepositoryException re2)
            {
                log.fatal("RdfUtils.getQueryTypesForQueryBundles: failed to close repository connection", re2);
            }
        }
        
        Map<URI, QueryType> results = null;
        
        results = RdfUtils.getQueryTypes(myRepository);
        
        return results.values();
    }
    
    /**
     * @return a SPARQL Update language query that will either insert or delete triples about rdfObject
     * @throws OpenRDFException
     */
    public static String getSparulQueryForObject(
        BaseQueryAllInterface rdfObject, 
        boolean isInsert,
        boolean isDelete,
        boolean useSparqlGraph, 
        String sparqlGraphUri)
    throws OpenRDFException
    {
        final Repository myRepository = new SailRepository(new MemoryStore());
        myRepository.initialize();
        
        // All queryall objects can be serialised to RDF using this method, along with a given subject URI, which in this case is derived from the object
        final boolean rdfOkay = rdfObject.toRdf(myRepository, rdfObject.getKey(), Settings.CONFIG_API_VERSION);
        
        if(!rdfOkay && isInsert)
        {
            if(_DEBUG)
            {
                log.debug("RdfUtils.getSparulQueryForObject: could not convert to RDF");
            }
            
            return "";
        }
        
        // text/plain is the accepted MIME format for NTriples because they were too lazy to define one... go figure
        final RDFFormat writerFormat = Rio.getWriterFormatForMIMEType("text/plain");
        
        final StringWriter insertTriples = new StringWriter();
        
        if(isInsert)
        {
            RdfUtils.toWriter(myRepository, insertTriples, writerFormat);
            
            log.debug("RdfUtils.getSparulQueryForObject: insertTriples.toString()="+insertTriples.toString());
        }
        else if(_DEBUG)
        {
            log.debug("RdfUtils.getSparulQueryForObject: isInsert was false");
        }
        
        // NOTE: this looks messy because it is. 
        // SPARUL doesn't play nicely if you don't know whether the delete will delete any triples,
        // and empty blocks are mandatory for the MODIFY statement if they are not applicable
        // The define sql:log-enable is a Virtuoso hack to enable SPARUL to work with more than one thread at once
        // HACK: Specific to Virtuoso!
        String sparqlInsertQuery = "define sql:log-enable 2 MODIFY ";
        
        if(useSparqlGraph)
        {
            sparqlInsertQuery += " GRAPH <"
                    + sparqlGraphUri + "> ";
        }
        
        if(isDelete)
        {
            sparqlInsertQuery += " DELETE { <" + rdfObject.getKey() + "> ?p ?o . } ";
        }
        else
        {
            sparqlInsertQuery += " DELETE { } ";
        }
        
        // NOTE: insertTriples will be an empty string if isInsert is false
        sparqlInsertQuery += " INSERT { " + insertTriples.toString() + " } ";
        
        if(isDelete)
        {
            sparqlInsertQuery += " WHERE { <" + rdfObject.getKey() + "> ?p ?o . } ";
        }
        
        if(_DEBUG)
        {
            log.debug("RdfUtils.getInsertQueryForObject: sparqlInsertQuery="
                    + sparqlInsertQuery);
        }
        
        return sparqlInsertQuery;
    }
    
    public static Collection<Statement> getStatementsFromRepositoryByPredicateUris(
            Repository nextRepository, Collection<URI> predicateUris)
            throws OpenRDFException
    {
        final Collection<Statement> results = new HashSet<Statement>();
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log
                    .debug("RdfUtils.getStatementsFromRepositoryByPredicateUris: entering method");
            RdfUtils.log.debug(nextRepository);
            RdfUtils.log.debug(predicateUris);
        }
        
        final RepositoryConnection con = nextRepository.getConnection();
        
        try
        {
            for(final URI nextInputPredicateUri : predicateUris)
            {
                try
                {
                	con.getStatements((URI)null, nextInputPredicateUri, (Value)null, true).addTo(results);
                }
                catch (final OpenRDFException ordfe)
                {
                    RdfUtils.log
                            .error("RdfUtils.getValuesFromRepositoryByPredicateUris: RDF exception found for nextInputPredicate="
                                    + nextInputPredicateUri.stringValue());
                }
                catch (final Exception ex)
                {
                    RdfUtils.log
                            .error("RdfUtils.getValuesFromRepositoryByPredicateUris: general exception found for nextInputPredicate="
                                    + nextInputPredicateUri.stringValue());
                }
            }
        }
        finally
        {
            if(con != null)
            {
                con.close();
            }
        }
        
        return results;
    }
    
    public static Collection<Statement> getStatementsFromRepositoryByPredicateUrisAndSubject(
            Repository nextRepository, Collection<URI> predicateUris, URI subjectUri)
            throws OpenRDFException
    {
        final Collection<Statement> results = new HashSet<Statement>();
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log
                    .debug("RdfUtils.getStatementsFromRepositoryByPredicateUris: entering method");
            RdfUtils.log.debug(nextRepository);
            RdfUtils.log.debug(predicateUris);
        }
        
        final RepositoryConnection con = nextRepository.getConnection();
        
        try
        {
            for(final URI nextInputPredicateUri : predicateUris)
            {
                try
                {
                	con.getStatements(subjectUri, nextInputPredicateUri, (Value)null, true).addTo(results);
                }
                catch (final OpenRDFException ordfe)
                {
                    RdfUtils.log
                            .error("RdfUtils.getValuesFromRepositoryByPredicateUris: RDF exception found for nextInputPredicate="
                                    + nextInputPredicateUri.stringValue());
                }
                catch (final Exception ex)
                {
                    RdfUtils.log
                            .error("RdfUtils.getValuesFromRepositoryByPredicateUris: general exception found for nextInputPredicate="
                                    + nextInputPredicateUri.stringValue());
                }
            }
        }
        finally
        {
            if(con != null)
            {
                con.close();
            }
        }
        
        return results;
    }
    
    
    
    public static Collection<Statement> getStatementsFromRepositoryByPredicateUrisAndSubject(
            Repository nextRepository, URI predicateUri, URI subjectUri)
            throws OpenRDFException
    {
        Collection<URI> predicateUris = new HashSet<URI>();
        predicateUris.add(predicateUri);
        
        return getStatementsFromRepositoryByPredicateUrisAndSubject(nextRepository, predicateUris, subjectUri);
    }
    
    // make sure that we are using UTF-8 to decode to item
    public static String getUTF8StringValueFromSesameValue(Value nextValue)
    {
        try
        {
            return new String(nextValue.stringValue().getBytes("utf-8"), "utf-8");
        }
        catch(java.io.UnsupportedEncodingException uee)
        {
        	log.fatal("RdfUtils: UTF-8 is not supported by this java vm!!!", uee);
            throw new RuntimeException("RdfUtils: UTF-8 is not supported by this java vm!!!", uee);
        }
    }
    
    /**
     * @param nextRepository
     * @param predicateUris
     * @return
     * @throws OpenRDFException
     */
    public static Collection<Value> getValuesFromRepositoryByPredicateUris(
            Repository nextRepository, Collection<URI> predicateUris)
            throws OpenRDFException
    {
        final Collection<Value> results = new HashSet<Value>();
        
        Collection<Statement> relevantStatements = getStatementsFromRepositoryByPredicateUris(nextRepository, predicateUris);

        for(Statement nextStatement : relevantStatements)
        {
            results.add(nextStatement.getObject());
        }
        
        return results;
        
    }
    
    // TODO: make me more efficient
    public static Collection<Value> getValuesFromRepositoryByPredicateUrisAndSubject(
            Repository nextRepository, Collection<URI> predicateUris, URI subjectUri)
            throws OpenRDFException
    {
        final Collection<Value> results = new HashSet<Value>();
        
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log
                    .debug("RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject: entering method");
            RdfUtils.log.debug(nextRepository);
            RdfUtils.log.debug(predicateUris);
        }
        
        final RepositoryConnection con = nextRepository.getConnection();
        
        try
        {
            for(final URI nextInputPredicateUri : predicateUris)
            {
                try
                {
                    final String queryString = "CONSTRUCT { <"+subjectUri.stringValue() +"> <"
                            + nextInputPredicateUri.stringValue()
                            + "> ?object } WHERE { <"+subjectUri.stringValue() +"> <"
                            + nextInputPredicateUri.stringValue()
                            + "> ?object . }";
                    final GraphQuery tupleQuery = con.prepareGraphQuery(
                            QueryLanguage.SPARQL, queryString);
                    final GraphQueryResult queryResult = tupleQuery.evaluate();

                    if(_DEBUG)
                        RdfUtils.log.debug("RdfUtils: queryString="+queryString);                   

                    try
                    {
                        while(queryResult.hasNext())
                        {
                            final Statement nextStatement = queryResult.next();
                            
                            if(_DEBUG)
                                RdfUtils.log.debug("RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject: nextStatement="+nextStatement);                   

                            results.add(nextStatement.getObject());
                            
                            // if(RdfUtils._DEBUG)
                            // {
                                // RdfUtils.log
                                        // .debug("Utilities: found object: valueOfObject="
                                                // + valueOfObject);
                            // }
                            
                            // results.add(new MemStatement(subjectUri, nextInputPredicateUri, valueOfObject, null, false, 0));
                            // results.add(getUTF8StringValueFromSesameValue(valueOfObject));
                        }
                    }
                    finally
                    {
                        queryResult.close();
                    }
                }
                catch (final OpenRDFException ordfe)
                {
                    RdfUtils.log
                            .error("RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject: RDF exception found for nextInputPredicate="
                                    + nextInputPredicateUri.stringValue()+" ordfe.class"+ordfe.getClass().getName(), ordfe);
                }
                catch (final Exception ex)
                {
                    RdfUtils.log
                            .error("RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject: general exception found for nextInputPredicate="
                                    + nextInputPredicateUri.stringValue(), ex);
                }
            }
        }
        
        // catch(OpenRDFException ordfe)
        // {
        // log.error("RdfUtils.getValuesFromRepositoryByPredicateUris: error found");
        // throw ordfe;
        // }
        finally
        {
            if(con != null)
            {
                con.close();
            }
        }
        // log.info("RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject: results.size()="+results.size());
        return results;
        
    }
    
    /**
     * @param nextRepository
     * @param predicateUris
     * @return
     * @throws OpenRDFException
     */
    public static Collection<Value> getValuesFromRepositoryByPredicateUrisAndSubject(
            Repository nextRepository, URI predicateUri, URI subjectUri)
            throws OpenRDFException
    {
        Collection<URI> predicateUris = new HashSet<URI>();
        
        predicateUris.add(predicateUri);
        
        return getValuesFromRepositoryByPredicateUrisAndSubject(nextRepository, predicateUris, subjectUri);
    }
    
    public static void insertResultIntoRepository(RdfFetcherQueryRunnable nextResult, Repository myRepository, Settings localSettings) throws RepositoryException, java.io.IOException
    {
        if(_DEBUG)
        {
            log.debug("RdfUtils.insertResultIntoRepository: nextResult.toString()="+nextResult.toString());
        }
        
        RepositoryConnection myRepositoryConnection = null;
        
        try
        {
            myRepositoryConnection = myRepository.getConnection();
            
            RDFFormat nextReaderFormat = RDFFormat.forMIMEType(nextResult.getReturnedMIMEType());
            
            if(_DEBUG)
            {
                log.debug("RdfUtils.insertResultIntoRepository: nextReaderFormat for returnedContentType="+nextResult.getReturnedContentType()+" nextReaderFormat="+nextReaderFormat);
            }
            
            if(nextReaderFormat == null)
            {
            	String assumedContentType = nextResult.getOriginalQueryBundle().getProvider().getAssumedContentType();

            	if(assumedContentType != null && assumedContentType.trim().length() > 0)
            	{
            		nextReaderFormat = Rio.getParserFormatForMIMEType(assumedContentType);
            	}
            	
            	if(nextReaderFormat == null)
            	{            	
	                nextReaderFormat = Rio.getParserFormatForMIMEType(localSettings.getStringProperty("assumedResponseContentType", Constants.APPLICATION_RDF_XML));
            	}

            	if(nextReaderFormat == null)
                {
                    log.error("RdfUtils.insertResultIntoRepository: Not attempting to parse result because assumedResponseContentType isn't supported by Rio and the returned content type wasn't either nextResult.returnedMIMEType="+nextResult.getReturnedMIMEType()+" nextResult.assumedContentType="+assumedContentType+" Settings.getStringPropertyFromConfig(\"assumedResponseContentType\")="+localSettings.getStringProperty("assumedResponseContentType", ""));
                    //throw new RuntimeException("Utilities: Not attempting to parse because there are no content types to use for interpretation");
                }
                else if(nextResult.getWasSuccessful())
                {
                    log.warn("RdfUtils.insertResultIntoRepository: successful query, but readerFormat NOT matched for returnedMIMEType="+nextResult.getReturnedMIMEType());
                }
            }
            else if(_DEBUG)
            {
                log.debug("RdfUtils.insertResultIntoRepository: readerFormat matched for returnedMIMEType="+nextResult.getReturnedMIMEType());
            }
            
            if(_DEBUG)
            {
                log.debug("RdfUtils.insertResultIntoRepository: nextResult.normalisedResult.length()="+nextResult.getNormalisedResult().length());
            }
            
            if(_TRACE)
            {
                log.trace("RdfUtils.insertResultIntoRepository: nextResult.normalisedResult="+nextResult.getNormalisedResult());
            }
            
            if(nextReaderFormat != null && nextResult.getNormalisedResult().length() > 0)
            {
                myRepositoryConnection.add(new java.io.StringReader(nextResult.getNormalisedResult()), localSettings.getDefaultHostAddress(), nextReaderFormat, nextResult.getOriginalQueryBundle().getProvider().getKey());
                
                myRepositoryConnection.commit();
            }
            
            if(_DEBUG)
            {
                log.debug("RdfUtils.insertResultIntoRepository: myRepositoryConnection.size()="+myRepositoryConnection.size());
            }
        }
        catch(org.openrdf.rio.RDFParseException rdfpe)
        {
            log.error("RdfUtils.insertResultIntoRepository: RDFParseException result: nextResult.endpointUrl="+nextResult.getEndpointUrl()+" message="+rdfpe.getMessage());
            
            if(_TRACE)
            {
                log.debug("RdfUtils.insertResultIntoRepository: RDFParseException result: normalisedResult="+nextResult.getNormalisedResult());
            }
        }
        finally
        {
            if(myRepositoryConnection != null)
            {
                try
                {
                    myRepositoryConnection.close();
                }
                catch(Exception ex)
                {
                    log.error("RdfUtils.insertResultIntoRepository: finally section, caught exception",ex);
                }
            }
        }
    }
    
    public static void insertResultsIntoRepository(Collection<RdfFetcherQueryRunnable> results, Repository myRepository, Settings localSettings) throws RepositoryException, java.io.IOException
    {
        for(RdfFetcherQueryRunnable nextResult : results)
		{
		    insertResultIntoRepository(nextResult, myRepository, localSettings);
		}
	}
    
    public static Collection<Statement> retrieveUrlsToStatements(Collection<String> retrievalUrls, String defaultResultFormat, Settings localSettings, BlacklistController localBlacklistController) throws InterruptedException
    {
        Collection<Statement> results = new HashSet<Statement>();
        
        try
        {
            Repository resultsRepository = new SailRepository(new MemoryStore());
            resultsRepository.initialize();
            
            retrieveUrls(retrievalUrls, defaultResultFormat, resultsRepository, localSettings, localBlacklistController, true);
        
            results = getAllStatementsFromRepository(resultsRepository);
        }
        catch (OpenRDFException e)
        {
            log.error("RdfUtils.retrieveUrlsToStatements: caught OpenRDFException", e);
        }
        
        return results;
    }
    
    public static void retrieveUrls(Collection<String> retrievalUrls, String defaultResultFormat, Repository myRepository, Settings localSettings, BlacklistController localBlacklistController) throws InterruptedException
    {
        retrieveUrls(retrievalUrls, defaultResultFormat, myRepository, localSettings, localBlacklistController, true);
    }
	
    public static void retrieveUrls(Collection<String> retrievalUrls, String defaultResultFormat, Repository myRepository, Settings localSettings, BlacklistController localBlacklistController, boolean inParallel) throws InterruptedException
    {
        Collection<RdfFetcherQueryRunnable> retrievalThreads = new HashSet<RdfFetcherQueryRunnable>();
        
        for(String nextLocation : retrievalUrls)
        {
            RdfFetcherQueryRunnable nextThread = new RdfFetcherUriQueryRunnable( nextLocation,
                         defaultResultFormat,
                         "",
                         "",
                         defaultResultFormat,
                         localSettings,
                         localBlacklistController,
                         new QueryBundle());
            
            retrievalThreads.add(nextThread);
        }
        
        for(RdfFetcherQueryRunnable nextThread : retrievalThreads)
        {
            nextThread.start();
            
            if(!inParallel)
            {
                // TODO: make it possible for users to configure either serial or parallel querying
                try
                {
                    nextThread.join();
                }
                catch( InterruptedException ie )
                {
                    log.error( "RdfFetchController.fetchRdfForQuery: caught interrupted exception message="+ie.getMessage() );
                    throw ie;
                }
            }
        }
        
        if(inParallel)
        {
            for(RdfFetcherQueryRunnable nextThread : retrievalThreads)
            {
                try
                {
                    nextThread.join();
                }
                catch( InterruptedException ie )
                {
                    log.error( "RdfFetchController.fetchRdfForQuery: caught interrupted exception message="+ie.getMessage() );
                    throw ie;
                }
            }
        }
        
        try
        {
            insertResultsIntoRepository(retrievalThreads, myRepository, localSettings);
        }
        catch (RepositoryException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    public static void retrieveUrls(String retrievalUrl, String defaultResultFormat, Repository myRepository, Settings localSettings, BlacklistController localBlacklistController) throws RepositoryException, java.io.IOException , InterruptedException
    {
        Collection<String> retrievalList = new LinkedList<String>();
        retrievalList.add(retrievalUrl);
        
        retrieveUrls(retrievalList, defaultResultFormat, myRepository, localSettings, localBlacklistController, true);
    }
    
    /**
     * @param nextRepository
     * @param outputStream
     */
    public static void toOutputStream(Repository nextRepository,
            java.io.OutputStream outputStream)
    {
        RdfUtils.toOutputStream(nextRepository, outputStream, RDFFormat.RDFXML);
    }
    
    /**
     * @param nextRepository
     * @param outputStream
     * @param format
     */
    public static void toOutputStream(Repository nextRepository,
            java.io.OutputStream outputStream, RDFFormat format)
    {
        RepositoryConnection nextConnection = null;
        
        try
        {
            nextConnection = nextRepository.getConnection();
            
            nextConnection.export(Rio.createWriter(format, outputStream));
        }
        catch (final RepositoryException e)
        {
            RdfUtils.log.error("repository exception", e);
        }
        catch (final RDFHandlerException e)
        {
            RdfUtils.log.error("rdfhandler exception", e);
        }
        finally
        {
            try
            {
                if(nextConnection != null)
                {
                    nextConnection.close();
                }
            }
            catch(RepositoryException rex)
            {
                log.error("RdfUtils.toWriter: connection didn't close correctly",rex);
            }
        }
    }
    
    /**
     * @param nextConnection
     * @return
     */
    public static String toString(Repository nextRepository)
    {
        final java.io.StringWriter stBuff = new java.io.StringWriter();

        RepositoryConnection nextConnection = null;
        
        try
        {
            nextConnection = nextRepository.getConnection();
            
            nextConnection.export(Rio.createWriter(RDFFormat.RDFXML, stBuff));
        }
        catch (final RepositoryException e)
        {
            RdfUtils.log.error("repository exception", e);
        }
        catch (final RDFHandlerException e)
        {
            RdfUtils.log.error("rdfhandler exception", e);
        }
        finally
        {
            try
            {
                if(nextConnection != null)
                {
                    nextConnection.close();
                }
            }
            catch(RepositoryException rex)
            {
                log.error("RdfUtils.toWriter: connection didn't close correctly",rex);
            }
        }
            
        return stBuff.toString();
    }
    
    /**
     * @param nextRepository
     * @param nextWriter
     */
    public static void toWriter(Repository nextRepository,
            java.io.Writer nextWriter)
    {
        RdfUtils.toWriter(nextRepository, nextWriter, RDFFormat.RDFXML);
    }

    /**
     * @param nextRepository
     * @param nextWriter
     * @param format
     */
    public static void toWriter(Repository nextRepository, java.io.Writer nextWriter, RDFFormat format)
    {
        RepositoryConnection nextConnection = null;

        try
        {
            nextConnection = nextRepository.getConnection();
            
            nextConnection.export(Rio.createWriter(format, nextWriter));
        }
        catch (final RepositoryException e)
        {
            RdfUtils.log.error("repository exception", e);
        }
        catch (final RDFHandlerException e)
        {
            RdfUtils.log.error("rdfhandler exception", e);
        }
        finally
        {
            try
            {
                if(nextConnection != null)
                {
                    nextConnection.close();
                }
            }
            catch(RepositoryException rex)
            {
                log.error("RdfUtils.toWriter: connection didn't close correctly",rex);
            }
        }
    }
    
    // from http://java.sun.com/developer/technicalArticles/ThirdParty/WebCrawler/WebCrawler.java
    // License at http://developers.sun.com/license/berkeley_license.html
    @SuppressWarnings("unused")
    public boolean robotSafe(URL url) 
    {
        final String DISALLOW = "Disallow:";
        String strHost = url.getHost();
        
        // TODO: Implement me!!!
        return true;
        /*****
        // form URL of the robots.txt file
        String strRobot = "http://" + strHost + "/robots.txt";
        URL urlRobot;
        try 
        { 
            urlRobot = new URL(strRobot);
        } 
        catch (MalformedURLException e) 
        {
            // something weird is happening, so don't trust it
            return false;
        }
        
        String strCommands;
        
        try 
        {
            InputStream urlRobotStream = urlRobot.openStream();
            
            // read in entire file
            byte b[] = new byte[10000];
            int numRead = urlRobotStream.read(b);
            strCommands = new String(b, 0, numRead);
            while (numRead != -1) 
            {
                if (Thread.currentThread() != searchThread)
                    break;
                numRead = urlRobotStream.read(b);
                if (numRead != -1) 
                {
                    String newCommands = new String(b, 0, numRead);
                    strCommands += newCommands;
                }
            }
            urlRobotStream.close();
        } 
        catch (IOException e) 
        {
            // if there is no robots.txt file, it is OK to search
            return true;
        }
        
        // assume that this robots.txt refers to us and 
        // search for "Disallow:" commands.
        String strURL = url.getFile();
        int index = 0;
        while ((index = strCommands.indexOf(DISALLOW, index)) != -1) 
        {
            index += DISALLOW.length();
            String strPath = strCommands.substring(index);
            StringTokenizer st = new StringTokenizer(strPath);
            
            if (!st.hasMoreTokens())
            break;
            
            String strBadPath = st.nextToken();
            
            // if the URL starts with a disallowed path, it is not safe
            if (strURL.indexOf(strBadPath) == 0)
            return false;
        }
        
        return true;
        *****/
    }

	public static Map<URI, RuleTest> getRuleTests(Repository myRepository)
	{
	    final Map<URI, RuleTest> results = new Hashtable<URI, RuleTest>();
	    
	    if(_DEBUG)
	    {
	        log
	                .debug("Settings.getRuleTests: started parsing rule test configurations");
	    }
	    final long start = System.currentTimeMillis();
	    
	    final URI ruleTestTypeUri = RuleTestImpl.getRuletestTypeUri();
	    try
	    {
	        final RepositoryConnection con = myRepository.getConnection();
	
	        for(Statement nextProvider : con.getStatements(null, RDF.TYPE, ruleTestTypeUri, true).asList())
	        {
	        	URI nextSubjectUri = (URI)nextProvider.getSubject();
	        	results.put(nextSubjectUri, new RuleTestImpl(
	        			con.getStatements(nextSubjectUri, (URI) null, (Value) null, true).asList(), 
	        			nextSubjectUri, 
	        			Settings.CONFIG_API_VERSION));
	        }                
	    }
	    catch (final OpenRDFException e)
	    {
	        // handle exception
	        log.fatal("Settings.getRuleTests:", e);
	    }
	
	
	    if(_INFO)
	    {
	        final long end = System.currentTimeMillis();
	        log.info(String.format("%s: timing=%10d",
	                "Settings.getRuleTests", (end - start)));
	    }
	    if(_DEBUG)
	    {
	        log
	                .debug("Settings.getRuleTests: finished getting rdf rule tests");
	    }
	    
	    return results;
	}

	public static Map<URI, QueryType> getQueryTypes(Repository myRepository)
	{
	    final Map<URI, QueryType> results = new Hashtable<URI, QueryType>();
	    final long start = System.currentTimeMillis();
	    if(_DEBUG)
	    {
	        log
	                .debug("Settings.getQueryTypes: started parsing query types");
	    }
	
	    final URI queryTypeUri = QueryTypeImpl.getQueryTypeUri();
	
	    try
	    {
	        final RepositoryConnection con = myRepository.getConnection();
	
	        for(Statement nextQueryType : con.getStatements(null, RDF.TYPE, queryTypeUri, true).asList())
	        {
	        	URI nextSubjectUri = (URI)nextQueryType.getSubject();
	        	results.put(nextSubjectUri, new QueryTypeImpl(
	        			con.getStatements(nextSubjectUri, (URI) null, (Value) null, true).asList(), 
	        			nextSubjectUri, 
	        			Settings.CONFIG_API_VERSION));
	        }                
	    }
	    catch (final OpenRDFException e)
	    {
	        // handle exception
	        log.fatal("Settings.getQueryTypes:", e);
	    }
	
	    if(_INFO)
	    {
	        final long end = System.currentTimeMillis();
	        log.info(String.format("%s: timing=%10d",
	                "Settings.getQueryTypes", (end - start)));
	    }
	    if(_DEBUG)
	    {
	        log
	                .debug("Settings.getQueryTypes: finished parsing query types");
	    }
	    
	    return results;
	}

	public static Map<URI, Provider> getProviders(Repository myRepository)
	{
	    final Map<URI, Provider> results = new Hashtable<URI, Provider>();
	
	    if(_DEBUG)
	    {
	        log
	                .debug("Settings.getProviders: started parsing provider configurations");
	    }
	    final long start = System.currentTimeMillis();
	
	    // TODO: HACK: treat all providers as HttpProviderImpl for now
	    final URI providerTypeUri = ProviderImpl.getProviderTypeUri();
	    
	    try
	    {
	        final RepositoryConnection con = myRepository.getConnection();
	
	        for(Statement nextProvider : con.getStatements(null, RDF.TYPE, providerTypeUri, true).asList())
	        {
	        	URI nextSubjectUri = (URI)nextProvider.getSubject();
	        	results.put(nextSubjectUri, new HttpProviderImpl(
	        			con.getStatements(nextSubjectUri, (URI) null, (Value) null, true).asList(), 
	        			nextSubjectUri, 
	        			Settings.CONFIG_API_VERSION));
	        }
	    }
	    catch (final OpenRDFException e)
	    {
	        // handle exception
	        log.fatal("Settings.getProviders:", e);
	    }
	
	    if(_INFO)
	    {
	        final long end = System.currentTimeMillis();
	        log.info(String.format("%s: timing=%10d",
	                "Settings.getProviders", (end - start)));
	    }
	    if(_DEBUG)
	    {
	        log
	                .debug("Settings.getProviders: finished parsing provider configurations");
	    }
	    
	    return results;
	}

	public static Map<URI, Profile> getProfiles(Repository myRepository)
	{
		final Map<URI, Profile> results = new Hashtable<URI, Profile>();
	    
		if(_DEBUG)
	    {
	        log
	                .debug("Settings.getProfiles: started parsing profile configurations");
	    }
	    final long start = System.currentTimeMillis();
	
	    final URI profileTypeUri = ProfileImpl.getProfileTypeUri();
	
	    try
	    {
	        final RepositoryConnection con = myRepository.getConnection();
	
	        for(Statement nextProvider : con.getStatements(null, RDF.TYPE, profileTypeUri, true).asList())
	        {
	        	URI nextSubjectUri = (URI)nextProvider.getSubject();
	        	results.put(nextSubjectUri, new ProfileImpl(
	        			con.getStatements(nextSubjectUri, (URI) null, (Value) null, true).asList(), 
	        			nextSubjectUri, 
	        			Settings.CONFIG_API_VERSION));
	        }                
	    }
	    catch (final OpenRDFException e)
	    {
	        // handle exception
	        log.fatal("Settings.getProviders:", e);
	    }
	
	    if(_INFO)
	    {
	        final long end = System.currentTimeMillis();
	        log.info(String.format("%s: timing=%10d",
	                "Settings.getProfiles", (end - start)));
	    }
	    if(_DEBUG)
	    {
	        log
	                .debug("Settings.getProfiles: finished parsing profiles");
	    }
	    
	    return results;
	}

	public static Map<URI, NormalisationRule> getNormalisationRules(Repository myRepository)
	{
	    if(_DEBUG)
	    {
	        log
	                .debug("Settings.getNormalisationRules: started parsing rdf normalisation rules");
	    }
	
	    final long start = System.currentTimeMillis();
	
	    final Map<URI, NormalisationRule> results = new Hashtable<URI, NormalisationRule>();

        try
        {
            final RepositoryConnection con = myRepository.getConnection();

            // Import Regular Expression Normalisation Rules first
            final URI regexRuleTypeUri = RegexNormalisationRuleImpl.getRegexRuleTypeUri();
            for(Statement nextRegexRule : con.getStatements(null, RDF.TYPE, regexRuleTypeUri, true).asList())
            {
            	URI nextSubjectUri = (URI)nextRegexRule.getSubject();
            	results.put(nextSubjectUri, new RegexNormalisationRuleImpl(
            			con.getStatements(nextSubjectUri, (URI) null, (Value) null, true).asList(), 
            			nextSubjectUri, 
            			Settings.CONFIG_API_VERSION));
            }                

    		// Then do the same thing for SPARQL Normalisation Rules
            final URI sparqlRuleTypeUri = SparqlNormalisationRuleImpl.getSparqlRuleTypeUri();
            for(Statement nextSparqlRule : con.getStatements(null, RDF.TYPE, sparqlRuleTypeUri, true).asList())
            {
            	URI nextSubjectUri = (URI)nextSparqlRule.getSubject();
            	results.put(nextSubjectUri, new SparqlNormalisationRuleImpl(
            			con.getStatements(nextSubjectUri, (URI) null, (Value) null, true).asList(), 
            			nextSubjectUri, 
            			Settings.CONFIG_API_VERSION));
            }                

            // Then do the same thing for XSLT Normalisation Rules
            final URI xsltRuleTypeUri = XsltNormalisationRuleImpl.getXsltRuleTypeUri();
            for(Statement nextXsltRule : con.getStatements(null, RDF.TYPE, xsltRuleTypeUri, true).asList())
            {
            	URI nextSubjectUri = (URI)nextXsltRule.getSubject();
            	results.put(nextSubjectUri, new XsltNormalisationRuleImpl(
            			con.getStatements(nextSubjectUri, (URI) null, (Value) null, true).asList(), 
            			nextSubjectUri, 
            			Settings.CONFIG_API_VERSION));
            }
        }
        catch (final OpenRDFException e)
        {
            // handle exception
            log.fatal("Settings.getNormalisationRules:", e);
        }
	    if(_INFO)
	    {
	        final long end = System.currentTimeMillis();
	        log.info(String.format("%s: timing=%10d",
	                "Settings.getNormalisationRules", (end - start)));
	    }
	    if(_DEBUG)
	    {
	        log
	                .debug("Settings.getNormalisationRules: finished parsing normalisation rules");
	    }
	    
	    return results;
	}

	public static Map<URI, NamespaceEntry> getNamespaceEntries(Repository myRepository)
	{
	    final Map<URI, NamespaceEntry> results = new Hashtable<URI, NamespaceEntry>();
	    if(_DEBUG)
	    {
	        log
	                .debug("Settings.getNamespaceEntries: started parsing namespace entry configurations");
	    }
	    final long start = System.currentTimeMillis();
	
	    final URI namespaceEntryTypeUri = NamespaceEntryImpl.getNamespaceTypeUri();
	    try
	    {
	        final RepositoryConnection con = myRepository.getConnection();
	
	        for(Statement nextNamespaceEntry : con.getStatements(null, RDF.TYPE, namespaceEntryTypeUri, true).asList())
	        {
	        	URI nextSubjectUri = (URI)nextNamespaceEntry.getSubject();
	        	results.put(nextSubjectUri, new NamespaceEntryImpl(
	        			con.getStatements(nextSubjectUri, (URI) null, (Value) null, true).asList(), 
	        			nextSubjectUri, 
	        			Settings.CONFIG_API_VERSION));
	        }                
	    }
	    catch (final OpenRDFException e)
	    {
	        // handle exception
	        log.fatal("Settings.getNamespaceEntries:", e);
	    }
	
	    
	    if(_INFO)
	    {
	        final long end = System.currentTimeMillis();
	        log.info(String.format("%s: timing=%10d",
	                "Settings.getNamespaceEntries", (end - start)));
	    }
	    
	    if(_DEBUG)
	    {
	        log
	                .debug("Settings.getNamespaceEntries: finished getting namespace entry information");
	    }
	    
	    return results;
	}

	/**
	 * @param nextValue
	 * @return
	 */
	public static long getLongFromValue(Value nextValue)
	{
	    long result = 0L;
	    
	    try
	    {
	        result = ((IntegerMemLiteral) nextValue).longValue();
	    }
	    catch (final ClassCastException cce)
	    {
	        RdfUtils.log.error("RdfUtils.getLongFromValue: nextValue was not a long numeric literal. Trying to parse it as a string... type="+ nextValue.getClass().getName());
	        try
	        {
	            result = Long.parseLong(nextValue.stringValue());
	        }
	        catch(NumberFormatException nfe)
	        {
	            RdfUtils.log.error("RdfUtils.getLongFromValue: nextValue was not a long numeric literal. Trying to parse it as a string... type="+ nextValue.getClass().getName());
	        }
	    }
	    
	    return result;
	}

	/**
	 * @param nextValue
	 * @return
	 */
	public static int getIntegerFromValue(Value nextValue)
	{
	    int result = 0;
	    
	    try
	    {
	        result = ((IntegerLiteralImpl) nextValue).intValue();
	    }
	    catch (final ClassCastException cce)
	    {
	        try
	        {
	            result = ((IntegerMemLiteral) nextValue).intValue();
	        }
	        catch (final ClassCastException cce2)
	        {
	            if(RdfUtils._DEBUG)
	            {
	                RdfUtils.log
	                        .debug("RdfUtils.getIntegerFromValue: nextValue was not a typed integer literal. Trying to parse it as a string... type="
	                                + nextValue.getClass().getName());
	            }
	            
	            result = Integer.parseInt(nextValue.toString());
	        }
	    }
	    
	    return result;
	}

	/**
	 * @param nextValue
	 * @return
	 */
	public static float getFloatFromValue(Value nextValue)
	{
	    float result = 0.0f;
	    
	    try
	    {
	        result = ((NumericLiteralImpl) nextValue).floatValue();
	    }
	    catch (final ClassCastException cce)
	    {
	        result = Float.parseFloat(nextValue.toString());
	    }
	    
	    return result;
	}
    
}
