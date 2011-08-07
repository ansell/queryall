package org.queryall.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
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
import org.openrdf.sail.memory.model.CalendarMemLiteral;
import org.openrdf.sail.memory.model.IntegerMemLiteral;
import org.queryall.api.BaseQueryAllInterface;
import org.queryall.api.NamespaceEntry;
import org.queryall.api.NormalisationRule;
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.api.QueryAllConfiguration;
import org.queryall.api.QueryType;
import org.queryall.api.RuleTest;
import org.queryall.blacklist.BlacklistController;
import org.queryall.enumerations.Constants;
import org.queryall.impl.HttpProviderImpl;
import org.queryall.impl.NamespaceEntryImpl;
import org.queryall.impl.ProfileImpl;
import org.queryall.impl.ProjectImpl;
import org.queryall.impl.ProviderImpl;
import org.queryall.impl.QueryTypeImpl;
import org.queryall.impl.RegexNormalisationRuleImpl;
import org.queryall.impl.RuleTestImpl;
import org.queryall.impl.SparqlNormalisationRuleImpl;
import org.queryall.impl.XsltNormalisationRuleImpl;
import org.queryall.query.HttpUrlQueryRunnable;
import org.queryall.query.ProvenanceRecord;
import org.queryall.query.QueryBundle;
import org.queryall.query.RdfFetchController;
import org.queryall.query.RdfFetcherQueryRunnable;
import org.queryall.query.RdfFetcherUriQueryRunnable;
import org.queryall.query.Settings;
import org.queryall.statistics.StatisticsEntry;

/**
 * A utility class to deal with RDF data and resolve RDF queries
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class RdfUtils
{
    public static final Logger log = Logger.getLogger(RdfUtils.class.getName());
    public static final boolean _TRACE = RdfUtils.log.isTraceEnabled();
    public static final boolean _DEBUG = RdfUtils.log.isDebugEnabled();
    public static final boolean _INFO = RdfUtils.log.isInfoEnabled();
    
    public static void copyAllStatementsToRepository(final Repository destination, final Repository source)
    {
        RepositoryConnection mySourceConnection = null;
        RepositoryConnection myDestinationConnection = null;
        
        try
        {
            mySourceConnection = source.getConnection();
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("RdfUtils.copyAllStatementsToRepository: mySourceConnection.size()="
                        + mySourceConnection.size());
            }
            myDestinationConnection = destination.getConnection();
            myDestinationConnection.add(mySourceConnection.getStatements(null, null, null, true));
            
            myDestinationConnection.commit();
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("RdfUtils.copyAllStatementsToRepository: myDestinationConnection.size()="
                        + myDestinationConnection.size());
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("RdfUtils.copyAllStatementsToRepository", ex);
        }
        finally
        {
            if(mySourceConnection != null)
            {
                try
                {
                    mySourceConnection.close();
                }
                catch(final Exception ex)
                {
                    RdfUtils.log.error("mySourceConnection", ex);
                }
            }
            if(myDestinationConnection != null)
            {
                try
                {
                    myDestinationConnection.close();
                }
                catch(final Exception ex)
                {
                    RdfUtils.log.error("myDestinationConnection", ex);
                }
            }
        }
        
    }
    
    public static Collection<QueryType> fetchQueryTypeByKey(final String hostToUse, final URI nextQueryKey,
            final int modelVersion, final QueryAllConfiguration localSettings) throws InterruptedException
    {
        final QueryBundle nextQueryBundle = new QueryBundle();
        
        final HttpProviderImpl dummyProvider = new HttpProviderImpl();
        
        final Collection<String> endpointUrls = new HashSet<String>();
        
        // if(nextQueryKey.startsWith(localSettings.getDefaultHostAddress()))
        // {
        final String namespaceAndIdentifier =
                nextQueryKey.stringValue().substring(localSettings.getDefaultHostAddress().length());
        
        final List<String> nsAndIdList = StringUtils.getNamespaceAndIdentifier(namespaceAndIdentifier, localSettings);
        
        if(nsAndIdList.size() == 2)
        {
            endpointUrls
                    .add(hostToUse + new QueryTypeImpl().getDefaultNamespace()
                            + localSettings.getStringProperty("separator", ":")
                            + StringUtils.percentEncode(nsAndIdList.get(1)));
            nextQueryBundle
                    .setQueryEndpoint(hostToUse + new QueryTypeImpl().getDefaultNamespace()
                            + localSettings.getStringProperty("separator", ":")
                            + StringUtils.percentEncode(nsAndIdList.get(1)));
        }
        // }
        // else
        // {
        // dummyProvider.endpointUrls.add(hostToUse+RdfUtils.percentEncode(nextQueryKey.substring(nextQueryKey.indexOf(new
        // QueryTypeImpl().getDefaultNamespace()))));
        // nextQueryBundle.queryEndpoint =
        // hostToUse+RdfUtils.percentEncode(nextQueryKey.substring(nextQueryKey.indexOf(new
        // QueryTypeImpl().getDefaultNamespace())));
        // }
        
        dummyProvider.setEndpointUrls(endpointUrls);
        dummyProvider.setEndpointMethod(HttpProviderImpl.getProviderHttpGetUrl());
        dummyProvider
                .setKey(hostToUse + localSettings.getNamespaceForProvider()
                        + localSettings.getStringProperty("separator", ":")
                        + StringUtils.percentEncode(namespaceAndIdentifier));
        dummyProvider.setIsDefaultSource(true);
        
        nextQueryBundle.setProvider(dummyProvider);
        
        final QueryType dummyQuery = new QueryTypeImpl();
        
        dummyQuery
                .setKey(hostToUse + localSettings.getNamespaceForQueryType()
                        + localSettings.getStringProperty("separator", ":")
                        + StringUtils.percentEncode(namespaceAndIdentifier));
        dummyQuery.setTitle("$$__queryfetch__$$");
        dummyQuery.setIncludeDefaults(true);
        
        nextQueryBundle.setQueryType(dummyQuery);
        
        final Collection<QueryBundle> queryBundles = new HashSet<QueryBundle>();
        
        queryBundles.add(nextQueryBundle);
        
        return RdfUtils.getQueryTypesForQueryBundles(queryBundles, modelVersion, localSettings);
    }
    
    public static Collection<QueryType> fetchQueryTypeByKey(final URI nextQueryKey, final boolean useSparqlGraph,
            final String sparqlGraphUri, final String sparqlEndpointUrl, final int modelVersion,
            final QueryAllConfiguration localSettings)
    {
        final String constructQueryString =
                RdfUtils.getConstructQueryForKey(nextQueryKey, useSparqlGraph, sparqlGraphUri);
        
        final QueryBundle nextQueryBundle = new QueryBundle();
        
        final HttpProviderImpl dummyProvider = new HttpProviderImpl();
        
        final Collection<String> endpointUrls = new HashSet<String>();
        
        endpointUrls.add(sparqlEndpointUrl);
        
        dummyProvider.setEndpointUrls(endpointUrls);
        
        nextQueryBundle.setQueryEndpoint(sparqlEndpointUrl);
        
        dummyProvider.setEndpointMethod(HttpProviderImpl.getProviderHttpPostSparql());
        dummyProvider.setKey(localSettings.getDefaultHostAddress() + localSettings.getNamespaceForProvider()
                + localSettings.getStringProperty("separator", ":")
                + StringUtils.percentEncode(nextQueryKey.stringValue()));
        dummyProvider.setIsDefaultSource(true);
        
        nextQueryBundle.setOriginalProvider(dummyProvider);
        
        final QueryType dummyQuery = new QueryTypeImpl();
        
        dummyQuery.setKey(localSettings.getDefaultHostAddress() + localSettings.getNamespaceForQueryType()
                + localSettings.getStringProperty("separator", ":")
                + StringUtils.percentEncode(nextQueryKey.stringValue()));
        dummyQuery.setTitle("$$__queryfetch__$$");
        dummyQuery.setIncludeDefaults(true);
        
        nextQueryBundle.setQueryType(dummyQuery);
        
        nextQueryBundle.setQuery(constructQueryString);
        
        final Collection<QueryBundle> queryBundles = new HashSet<QueryBundle>();
        
        queryBundles.add(nextQueryBundle);
        
        return RdfUtils.getQueryTypesForQueryBundles(queryBundles, modelVersion, localSettings);
    }
    
    public static String findBestContentType(final String requestedContentType,
            final String preferredDisplayContentType, final String fallback)
    {
        if(requestedContentType.equals(Constants.TEXT_HTML))
        {
            return requestedContentType;
        }
        
        // even if they request a random format, we need to make sure that Rio has a writer
        // compatible with it, otherwise we revert to one of the defaults as a failsafe mechanism
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
    
    public static HttpUrlQueryRunnable generateHttpUrlSparqlDeleteThread(final BaseQueryAllInterface rdfObject,
            final boolean useSparqlGraph, final String sparqlGraphUri, final String sparqlEndpointMethod,
            final String sparqlEndpointUrl, final String acceptHeader, final String expectedReturnFormat,
            final QueryAllConfiguration localSettings, final BlacklistController localBlacklistController)
        throws OpenRDFException
    {
        final String sparqlInsertQuery =
                RdfUtils.getSparulQueryForObject(rdfObject, false, true, useSparqlGraph, sparqlGraphUri);
        
        return RdfUtils.generateHttpUrlSparqlThread(sparqlInsertQuery, sparqlEndpointMethod, sparqlEndpointUrl,
                acceptHeader, expectedReturnFormat, localSettings, localBlacklistController);
    }
    
    public static HttpUrlQueryRunnable generateHttpUrlSparqlInsertThread(final BaseQueryAllInterface rdfObject,
            final boolean isDelete, final boolean useSparqlGraph, final String sparqlGraphUri,
            final String sparqlEndpointMethod, final String sparqlEndpointUrl, final String acceptHeader,
            final String expectedReturnFormat, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController) throws OpenRDFException
    {
        final String sparqlInsertQuery =
                RdfUtils.getSparulQueryForObject(rdfObject, true, isDelete, useSparqlGraph, sparqlGraphUri);
        
        return RdfUtils.generateHttpUrlSparqlThread(sparqlInsertQuery, sparqlEndpointMethod, sparqlEndpointUrl,
                acceptHeader, expectedReturnFormat, localSettings, localBlacklistController);
    }
    
    public static HttpUrlQueryRunnable generateHttpUrlSparqlThread(final String sparqlQuery,
            final String sparqlEndpointMethod, final String sparqlEndpointUrl, final String acceptHeader,
            final String expectedReturnFormat, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController)
    {
        return new HttpUrlQueryRunnable(sparqlEndpointMethod, sparqlEndpointUrl, sparqlQuery, acceptHeader,
                expectedReturnFormat, localSettings, localBlacklistController);
    }
    
    /**
     * @param nextRepository
     * @return
     * @throws OpenRDFException
     */
    public static List<Statement> getAllStatementsFromRepository(final Repository nextRepository)
        throws OpenRDFException
    {
        List<Statement> results = new ArrayList<Statement>(1);
        
        final RepositoryConnection con = nextRepository.getConnection();
        
        try
        {
            results = con.getStatements((Resource)null, (URI)null, (Value)null, true).asList();
            
            Collections.sort(results, new org.queryall.comparators.StatementComparator());
        }
        catch(final OpenRDFException ordfe)
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
    public static boolean getBooleanFromValue(final Value nextValue)
    {
        boolean result = false;
        
        if(nextValue instanceof BooleanLiteralImpl)
        {
            result = ((BooleanLiteralImpl)nextValue).booleanValue();
        }
        else if(nextValue instanceof BooleanMemLiteral)
        {
            result = ((BooleanMemLiteral)nextValue).booleanValue();
        }
        else if(nextValue instanceof IntegerMemLiteral)
        {
            final int tempValue = ((IntegerMemLiteral)nextValue).intValue();

            if(tempValue == 0)
            {
                return false;
            }
            
            if(tempValue == 1)
            {
                return true;
            }
        }
        else
        {
            result = Boolean.parseBoolean(nextValue.stringValue());
        }
        
        return result;
    }
    
    public static String getConstructQueryByType(final BaseQueryAllInterface nextObject, final int offset,
            final int limit, final boolean useSparqlGraph, final String sparqlGraphUri,
            final QueryAllConfiguration localSettings)
    {
        return RdfUtils.getConstructQueryByType(nextObject.getElementTypes(), offset, limit, useSparqlGraph,
                sparqlGraphUri, localSettings);
    }
    
    public static String getConstructQueryByType(final Collection<URI> nextTypes, final int offset, final int limit,
            final boolean useSparqlGraph, final String sparqlGraphUri, final QueryAllConfiguration localSettings)
    {
        final StringBuilder result = new StringBuilder();
        
        result.append("CONSTRUCT { ?s a ?type . ");
        
        int counter = 0;
        
        // TODO: change this to List<String> when titleProperties are ordered in the configuration
        final Collection<URI> titleProperties = localSettings.getURIProperties("titleProperties");
        
        for(final URI nextTitleUri : titleProperties)
        {
            result.append(" ?s <" + nextTitleUri.stringValue() + "> ?o" + counter + " . ");
            
            counter++;
        }
        
        result.append(" } WHERE { ");
        
        boolean firstType = true;
        
        for(final URI nextTypeUri : nextTypes)
        {
            if(!firstType)
            {
                result.append(" UNION ");
            }
            
            // need to open up the union pattern using this if there is more than one type
            if(nextTypes.size() > 1)
            {
                result.append(" { ");
            }
            
            if(useSparqlGraph)
            {
                result.append(" GRAPH <" + sparqlGraphUri + "> { ");
            }
            
            result.append(" ?s a ?type . ");
            result.append(" FILTER(?type = ").append(nextTypeUri.toString()).append(" ) . ");
            
            counter = 0;
            
            for(final URI nextTitleUri : titleProperties)
            {
                result.append("OPTIONAL{ ?s <" + nextTitleUri.stringValue() + "> ?o" + counter + " . }");
                
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
    
    public static String getConstructQueryForKey(final URI nextKey, final boolean useSparqlGraph,
            final String sparqlGraphUri)
    {
        final StringBuilder result = new StringBuilder();
        
        result.append("CONSTRUCT { <" + nextKey.stringValue() + "> ?p ?o . } WHERE { ");
        
        if(useSparqlGraph)
        {
            result.append(" GRAPH <" + sparqlGraphUri + "> { ");
        }
        
        result.append(" <" + nextKey.stringValue() + "> ?p ?o . ");
        
        if(useSparqlGraph)
        {
            result.append(" } ");
        }
        
        result.append(" } ");
        
        return result.toString();
    }
    
    public static String getConstructQueryForObject(final BaseQueryAllInterface nextObject,
            final boolean useSparqlGraph, final String sparqlGraphUri)
    {
        return RdfUtils.getConstructQueryForKey(nextObject.getKey(), useSparqlGraph, sparqlGraphUri);
    }
    
    /**
     * @param nextValue
     * @return
     */
    public static Date getDateTimeFromValue(final Value nextValue) throws java.text.ParseException
    {
        Date result;
        
//        if(nextValue instanceof CalendarLiteralImpl)
//        {
//            result = ((CalendarLiteralImpl)nextValue).calendarValue().toGregorianCalendar().getTime();
//        }
//        else if(nextValue instanceof CalendarMemLiteral)
//        {
//            result = ((CalendarMemLiteral)nextValue).calendarValue().toGregorianCalendar().getTime();
//        }
//        else
//        {
            try
            {
                result = Constants.ISO8601UTC().parse(nextValue.stringValue());
            }
            catch(final java.text.ParseException pe)
            {
                RdfUtils.log.error("Could not parse date using ISO8601UTC: nextValue.stringValue=" + nextValue.stringValue());
                try
                {
                    result = DateFormat.getDateInstance().parse(nextValue.stringValue());
                }
                catch(final java.text.ParseException pe2)
                {
                    RdfUtils.log.error("Could not parse date using default date format: nextValue.stringValue=" + nextValue.stringValue());
                    
                    throw pe2;
                }
            }
//        }
        
        return result;
    }
    
    /**
     * @param nextRepository
     * @param predicateUris
     * @return
     * @throws OpenRDFException
     */
    public static Collection<String> getDistinctObjectUrisFromRepository(final Repository nextRepository)// ,
        // Collection<String>
        // predicateUris)
        throws OpenRDFException
    {
        final Collection<String> results = new HashSet<String>();
        
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("RdfUtils.getDistinctObjectsFromRepository: entering method");
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
            
            final String queryString =
                    "SELECT DISTINCT ?object WHERE { ?subject ?predicate ?object . FILTER(isURI(?object)) }";
            final TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            final TupleQueryResult queryResult = tupleQuery.evaluate();
            
            try
            {
                while(queryResult.hasNext())
                {
                    final BindingSet bindingSet = queryResult.next();
                    final Value valueOfObject = bindingSet.getValue("object");
                    
                    if(RdfUtils._DEBUG)
                    {
                        RdfUtils.log.debug("Utilities: found object: valueOfObject=" + valueOfObject);
                    }
                    
                    results.add(RdfUtils.getUTF8StringValueFromSesameValue(valueOfObject));
                }
            }
            finally
            {
                queryResult.close();
            }
        }
        catch(final OpenRDFException ordfe)
        {
            RdfUtils.log.error("RdfUtils.getDistinctObjectsFromRepository: RDF exception", ordfe);
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("RdfUtils.getDistinctObjectsFromRepository: general exception", ex);
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
    public static Collection<String> getDistinctSubjectsFromRepository(final Repository nextRepository)// ,
        // Collection<String>
        // predicateUris)
        throws OpenRDFException
    {
        final Collection<String> results = new HashSet<String>();
        
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("RdfUtils.getDistinctSubjectsFromRepository: entering method");
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
            final TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            final TupleQueryResult queryResult = tupleQuery.evaluate();
            
            try
            {
                while(queryResult.hasNext())
                {
                    final BindingSet bindingSet = queryResult.next();
                    final Value valueOfSubject = bindingSet.getValue("subject");
                    
                    if(RdfUtils._DEBUG)
                    {
                        RdfUtils.log.debug("Utilities: found subject: valueOfSubject=" + valueOfSubject);
                    }
                    
                    results.add(RdfUtils.getUTF8StringValueFromSesameValue(valueOfSubject));
                }
            }
            finally
            {
                queryResult.close();
            }
        }
        catch(final OpenRDFException ordfe)
        {
            RdfUtils.log.error("RdfUtils.getDistinctSubjectsFromRepository: RDF exception", ordfe);
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("RdfUtils.getDistinctSubjectsFromRepository: general exception", ex);
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
     * @param nextValue
     * @return
     */
    public static float getFloatFromValue(final Value nextValue)
    {
        float result = 0.0f;
        
        try
        {
            result = ((NumericLiteralImpl)nextValue).floatValue();
        }
        catch(final ClassCastException cce)
        {
            result = Float.parseFloat(nextValue.stringValue());
        }
        
        return result;
    }
    
    /**
     * @param nextValue
     * @return
     */
    public static int getIntegerFromValue(final Value nextValue)
    {
        int result = 0;
        
        try
        {
            result = ((IntegerLiteralImpl)nextValue).intValue();
        }
        catch(final ClassCastException cce)
        {
            try
            {
                result = ((IntegerMemLiteral)nextValue).intValue();
            }
            catch(final ClassCastException cce2)
            {
                if(RdfUtils._DEBUG)
                {
                    RdfUtils.log
                            .debug("RdfUtils.getIntegerFromValue: nextValue was not a typed integer literal. Trying to parse it as a string... type="
                                    + nextValue.getClass().getName());
                }
                
                result = Integer.parseInt(nextValue.stringValue());
            }
        }
        
        return result;
    }
    
    /**
     * @param nextValue
     * @return
     */
    public static long getLongFromValue(final Value nextValue)
    {
        long result = 0L;
        
        try
        {
            result = ((IntegerMemLiteral)nextValue).longValue();
        }
        catch(final ClassCastException cce)
        {
            RdfUtils.log
                    .error("RdfUtils.getLongFromValue: nextValue was not a long numeric literal. Trying to parse it as a string... type="
                            + nextValue.getClass().getName());
            try
            {
                result = Long.parseLong(nextValue.stringValue());
            }
            catch(final NumberFormatException nfe)
            {
                RdfUtils.log
                        .error("RdfUtils.getLongFromValue: nextValue was not a long numeric literal. Trying to parse it as a string... type="
                                + nextValue.getClass().getName());
            }
        }
        
        return result;
    }
    
    public static Map<URI, NamespaceEntry> getNamespaceEntries(final Repository myRepository)
    {
        final Map<URI, NamespaceEntry> results = new Hashtable<URI, NamespaceEntry>();
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("Settings.getNamespaceEntries: started parsing namespace entry configurations");
        }
        final long start = System.currentTimeMillis();
        
        final URI namespaceEntryTypeUri = NamespaceEntryImpl.getNamespaceTypeUri();
        try
        {
            final RepositoryConnection con = myRepository.getConnection();
            
            for(final Statement nextNamespaceEntry : con.getStatements(null, RDF.TYPE, namespaceEntryTypeUri, true)
                    .asList())
            {
                final URI nextSubjectUri = (URI)nextNamespaceEntry.getSubject();
                results.put(nextSubjectUri,
                        new NamespaceEntryImpl(
                                con.getStatements(nextSubjectUri, (URI)null, (Value)null, true).asList(),
                                nextSubjectUri, Settings.CONFIG_API_VERSION));
            }
        }
        catch(final OpenRDFException e)
        {
            // handle exception
            RdfUtils.log.fatal("Settings.getNamespaceEntries:", e);
        }
        
        if(RdfUtils._INFO)
        {
            final long end = System.currentTimeMillis();
            RdfUtils.log.info(String.format("%s: timing=%10d", "Settings.getNamespaceEntries", (end - start)));
        }
        
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("Settings.getNamespaceEntries: finished getting namespace entry information");
        }
        
        return results;
    }
    
    public static Map<URI, NormalisationRule> getNormalisationRules(final Repository myRepository)
    {
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("Settings.getNormalisationRules: started parsing rdf normalisation rules");
        }
        
        final long start = System.currentTimeMillis();
        
        final Map<URI, NormalisationRule> results = new Hashtable<URI, NormalisationRule>();
        
        try
        {
            final RepositoryConnection con = myRepository.getConnection();
            
            // Import Regular Expression Normalisation Rules first
            final URI regexRuleTypeUri = RegexNormalisationRuleImpl.getRegexRuleTypeUri();
            for(final Statement nextRegexRule : con.getStatements(null, RDF.TYPE, regexRuleTypeUri, true).asList())
            {
                final URI nextSubjectUri = (URI)nextRegexRule.getSubject();
                results.put(nextSubjectUri,
                        new RegexNormalisationRuleImpl(con.getStatements(nextSubjectUri, (URI)null, (Value)null, true)
                                .asList(), nextSubjectUri, Settings.CONFIG_API_VERSION));
            }
            
            // Then do the same thing for SPARQL Normalisation Rules
            final URI sparqlRuleTypeUri = SparqlNormalisationRuleImpl.getSparqlRuleTypeUri();
            for(final Statement nextSparqlRule : con.getStatements(null, RDF.TYPE, sparqlRuleTypeUri, true).asList())
            {
                final URI nextSubjectUri = (URI)nextSparqlRule.getSubject();
                results.put(nextSubjectUri,
                        new SparqlNormalisationRuleImpl(con.getStatements(nextSubjectUri, (URI)null, (Value)null, true)
                                .asList(), nextSubjectUri, Settings.CONFIG_API_VERSION));
            }
            
            // Then do the same thing for XSLT Normalisation Rules
            final URI xsltRuleTypeUri = XsltNormalisationRuleImpl.getXsltRuleTypeUri();
            for(final Statement nextXsltRule : con.getStatements(null, RDF.TYPE, xsltRuleTypeUri, true).asList())
            {
                final URI nextSubjectUri = (URI)nextXsltRule.getSubject();
                results.put(nextSubjectUri,
                        new XsltNormalisationRuleImpl(con.getStatements(nextSubjectUri, (URI)null, (Value)null, true)
                                .asList(), nextSubjectUri, Settings.CONFIG_API_VERSION));
            }
        }
        catch(final OpenRDFException e)
        {
            // handle exception
            RdfUtils.log.fatal("Settings.getNormalisationRules:", e);
        }
        if(RdfUtils._INFO)
        {
            final long end = System.currentTimeMillis();
            RdfUtils.log.info(String.format("%s: timing=%10d", "Settings.getNormalisationRules", (end - start)));
        }
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("Settings.getNormalisationRules: finished parsing normalisation rules");
        }
        
        return results;
    }
    
    /**
     * @param nextRepository
     * @param predicateUris
     * @return
     * @throws OpenRDFException
     */
    public static Collection<String> getObjectUrisFromRepositoryByPredicateUris(final Repository nextRepository,
            final Collection<String> predicateUris) throws OpenRDFException
    {
        final Collection<String> results = new HashSet<String>();
        
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("RdfUtils.getObjectUrisFromRepositoryByPredicateUris: entering method");
            RdfUtils.log.debug(nextRepository);
            RdfUtils.log.debug(predicateUris);
        }
        
        final RepositoryConnection con = nextRepository.getConnection();
        
        try
        {
            final ValueFactory f = Constants.valueFactory;
            
            for(final String nextInputPredicate : predicateUris)
            {
                if((nextInputPredicate == null) || nextInputPredicate.trim().equals(""))
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
                    final URI nextInputPredicateUri = f.createURI(nextInputPredicate);
                    
                    final String queryString =
                            "SELECT DISTINCT ?object WHERE { ?subject <" + nextInputPredicateUri.stringValue()
                                    + "> ?object . FILTER(isURI(?object)) }";
                    final TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
                    final TupleQueryResult queryResult = tupleQuery.evaluate();
                    
                    try
                    {
                        while(queryResult.hasNext())
                        {
                            final BindingSet bindingSet = queryResult.next();
                            final Value valueOfObject = bindingSet.getValue("object");
                            
                            if(RdfUtils._DEBUG)
                            {
                                RdfUtils.log.debug("Utilities: found object: valueOfObject=" + valueOfObject);
                            }
                            
                            results.add(RdfUtils.getUTF8StringValueFromSesameValue(valueOfObject));
                        }
                    }
                    finally
                    {
                        queryResult.close();
                    }
                }
                catch(final OpenRDFException ordfe)
                {
                    RdfUtils.log
                            .error("RdfUtils.getObjectUrisFromRepositoryByPredicateUris: RDF exception found for nextInputPredicate="
                                    + nextInputPredicate);
                }
                catch(final Exception ex)
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
    
    public static Map<URI, Profile> getProfiles(final Repository myRepository)
    {
        final Map<URI, Profile> results = new Hashtable<URI, Profile>();
        
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("Settings.getProfiles: started parsing profile configurations");
        }
        final long start = System.currentTimeMillis();
        
        final URI profileTypeUri = ProfileImpl.getProfileTypeUri();
        
        try
        {
            final RepositoryConnection con = myRepository.getConnection();
            
            for(final Statement nextProvider : con.getStatements(null, RDF.TYPE, profileTypeUri, true).asList())
            {
                final URI nextSubjectUri = (URI)nextProvider.getSubject();
                results.put(nextSubjectUri,
                        new ProfileImpl(con.getStatements(nextSubjectUri, (URI)null, (Value)null, true).asList(),
                                nextSubjectUri, Settings.CONFIG_API_VERSION));
            }
        }
        catch(final OpenRDFException e)
        {
            // handle exception
            RdfUtils.log.fatal("Settings.getProviders:", e);
        }
        
        if(RdfUtils._INFO)
        {
            final long end = System.currentTimeMillis();
            RdfUtils.log.info(String.format("%s: timing=%10d", "Settings.getProfiles", (end - start)));
        }
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("Settings.getProfiles: finished parsing profiles");
        }
        
        return results;
    }
    
    public static Map<URI, Provider> getProviders(final Repository myRepository)
    {
        final Map<URI, Provider> results = new Hashtable<URI, Provider>();
        
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("Settings.getProviders: started parsing provider configurations");
        }
        final long start = System.currentTimeMillis();
        
        // TODO: HACK: treat all providers as HttpProviderImpl for now
        final URI providerTypeUri = ProviderImpl.getProviderTypeUri();
        
        try
        {
            final RepositoryConnection con = myRepository.getConnection();
            
            for(final Statement nextProvider : con.getStatements(null, RDF.TYPE, providerTypeUri, true).asList())
            {
                final URI nextSubjectUri = (URI)nextProvider.getSubject();
                results.put(nextSubjectUri,
                        new HttpProviderImpl(con.getStatements(nextSubjectUri, (URI)null, (Value)null, true).asList(),
                                nextSubjectUri, Settings.CONFIG_API_VERSION));
            }
        }
        catch(final OpenRDFException e)
        {
            // handle exception
            RdfUtils.log.fatal("Settings.getProviders:", e);
        }
        
        if(RdfUtils._INFO)
        {
            final long end = System.currentTimeMillis();
            RdfUtils.log.info(String.format("%s: timing=%10d", "Settings.getProviders", (end - start)));
        }
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("Settings.getProviders: finished parsing provider configurations");
        }
        
        return results;
    }
    
    public static Map<URI, QueryType> getQueryTypes(final Repository myRepository)
    {
        final Map<URI, QueryType> results = new Hashtable<URI, QueryType>();
        final long start = System.currentTimeMillis();
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("Settings.getQueryTypes: started parsing query types");
        }
        
        final URI queryTypeUri = QueryTypeImpl.getQueryTypeUri();
        
        try
        {
            final RepositoryConnection con = myRepository.getConnection();
            
            for(final Statement nextQueryType : con.getStatements(null, RDF.TYPE, queryTypeUri, true).asList())
            {
                final URI nextSubjectUri = (URI)nextQueryType.getSubject();
                results.put(nextSubjectUri,
                        new QueryTypeImpl(con.getStatements(nextSubjectUri, (URI)null, (Value)null, true).asList(),
                                nextSubjectUri, Settings.CONFIG_API_VERSION));
            }
        }
        catch(final OpenRDFException e)
        {
            // handle exception
            RdfUtils.log.fatal("Settings.getQueryTypes:", e);
        }
        
        if(RdfUtils._INFO)
        {
            final long end = System.currentTimeMillis();
            RdfUtils.log.info(String.format("%s: timing=%10d", "Settings.getQueryTypes", (end - start)));
        }
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("Settings.getQueryTypes: finished parsing query types");
        }
        
        return results;
    }
    
    public static Collection<QueryType> getQueryTypesForQueryBundles(final Collection<QueryBundle> queryBundles,
            final int modelVersion, final QueryAllConfiguration localSettings)
    {
        final RdfFetchController fetchController =
                new RdfFetchController(localSettings, BlacklistController.getDefaultController(), queryBundles);
        
        try
        {
            fetchController.fetchRdfForQueries();
        }
        catch(final InterruptedException ie)
        {
            RdfUtils.log.fatal("RdfUtils.getQueryTypesForQueryBundles: interrupted exception", ie);
            // throw ie;
        }
        
        final Collection<RdfFetcherQueryRunnable> rdfResults = fetchController.getSuccessfulResults();
        
        Repository myRepository = null;
        RepositoryConnection myRepositoryConnection = null;
        try
        {
            myRepository = new SailRepository(new MemoryStore());
            myRepository.initialize();
            myRepositoryConnection = myRepository.getConnection();
            
            for(final RdfFetcherQueryRunnable nextResult : rdfResults)
            {
                try
                {
                    RDFFormat nextReaderFormat = RDFFormat.forMIMEType(nextResult.getReturnedMIMEType());
                    
                    if(RdfUtils.log.isDebugEnabled())
                    {
                        RdfUtils.log
                                .debug("RdfUtils.getQueryTypesForQueryBundles: nextReaderFormat for returnedContentType="
                                        + nextResult.getReturnedContentType() + " nextReaderFormat=" + nextReaderFormat);
                    }
                    
                    if(nextReaderFormat == null)
                    {
                        nextReaderFormat =
                                Rio.getParserFormatForMIMEType(localSettings.getStringProperty(
                                        "assumedResponseContentType", Constants.APPLICATION_RDF_XML));
                        
                        if(nextReaderFormat == null)
                        {
                            RdfUtils.log
                                    .error("RdfUtils.getQueryTypesForQueryBundles: Not attempting to parse result because Settings.getStringPropertyFromConfig(\"assumedResponseContentType\") isn't supported by Rio and the returned content type wasn't either nextResult.returnedMIMEType="
                                            + nextResult.getReturnedMIMEType()
                                            + " Settings.getStringPropertyFromConfig(\"assumedResponseContentType\")="
                                            + localSettings.getStringProperty("assumedResponseContentType", ""));
                            continue;
                        }
                        else
                        {
                            RdfUtils.log
                                    .warn("RdfUtils.getQueryTypesForQueryBundles: readerFormat NOT matched for returnedMIMEType="
                                            + nextResult.getReturnedMIMEType()
                                            + " using configured preferred content type as fallback Settings.getStringPropertyFromConfig(\"assumedResponseContentType\")="
                                            + localSettings.getStringProperty("assumedResponseContentType", ""));
                        }
                    }
                    else if(RdfUtils.log.isDebugEnabled())
                    {
                        RdfUtils.log
                                .debug("RdfUtils.getQueryTypesForQueryBundles: readerFormat matched for returnedMIMEType="
                                        + nextResult.getReturnedMIMEType());
                    }
                    
                    if(nextResult.getNormalisedResult().length() > 0)
                    {
                        myRepositoryConnection.add(new java.io.StringReader(nextResult.getNormalisedResult()),
                                localSettings.getDefaultHostAddress(), nextReaderFormat);
                    }
                }
                catch(final org.openrdf.rio.RDFParseException rdfpe)
                {
                    RdfUtils.log.error("RdfUtils.getQueryTypesForQueryBundles: RDFParseException", rdfpe);
                }
                catch(final org.openrdf.repository.RepositoryException re)
                {
                    RdfUtils.log.error("RdfUtils.getQueryTypesForQueryBundles: RepositoryException inner", re);
                }
                catch(final java.io.IOException ioe)
                {
                    RdfUtils.log.error("RdfUtils.getQueryTypesForQueryBundles: IOException", ioe);
                }
            } // end for(RdfFetcherQueryRunnable nextResult : rdfResults)
        }
        catch(final org.openrdf.repository.RepositoryException re)
        {
            RdfUtils.log.error("RdfUtils.getQueryTypesForQueryBundles: RepositoryException outer", re);
        }
        finally
        {
            try
            {
                if(myRepositoryConnection != null)
                {
                    myRepositoryConnection.close();
                }
            }
            catch(final org.openrdf.repository.RepositoryException re2)
            {
                RdfUtils.log.fatal("RdfUtils.getQueryTypesForQueryBundles: failed to close repository connection", re2);
            }
        }
        
        Map<URI, QueryType> results = null;
        
        results = RdfUtils.getQueryTypes(myRepository);
        
        return results.values();
    }
    
    public static Map<URI, RuleTest> getRuleTests(final Repository myRepository)
    {
        final Map<URI, RuleTest> results = new Hashtable<URI, RuleTest>();
        
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("Settings.getRuleTests: started parsing rule test configurations");
        }
        final long start = System.currentTimeMillis();
        
        final URI ruleTestTypeUri = RuleTestImpl.getRuletestTypeUri();
        try
        {
            final RepositoryConnection con = myRepository.getConnection();
            
            for(final Statement nextProvider : con.getStatements(null, RDF.TYPE, ruleTestTypeUri, true).asList())
            {
                final URI nextSubjectUri = (URI)nextProvider.getSubject();
                results.put(nextSubjectUri,
                        new RuleTestImpl(con.getStatements(nextSubjectUri, (URI)null, (Value)null, true).asList(),
                                nextSubjectUri, Settings.CONFIG_API_VERSION));
            }
        }
        catch(final OpenRDFException e)
        {
            // handle exception
            RdfUtils.log.fatal("Settings.getRuleTests:", e);
        }
        
        if(RdfUtils._INFO)
        {
            final long end = System.currentTimeMillis();
            RdfUtils.log.info(String.format("%s: timing=%10d", "Settings.getRuleTests", (end - start)));
        }
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("Settings.getRuleTests: finished getting rdf rule tests");
        }
        
        return results;
    }
    
    public static Repository getSchemas()
    {
        return RdfUtils.getSchemas(null);
    }
    
    public static Repository getSchemas(final URI contextUri)
    {
        // Repository myRepository = new SailRepository(new ForwardChainingRDFSInferencer(new
        // MemoryStore()));
        final Repository myRepository = new SailRepository(new MemoryStore());
        
        try
        {
            myRepository.initialize();
        }
        catch(final RepositoryException e)
        {
            RdfUtils.log.fatal("Could not initialise repository for schemas");
            throw new RuntimeException(e);
        }
        
        try
        {
            if(!ProviderImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("QueryAllSchemaServlet: Provider schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("QueryAllSchemaServlet: Problem generating Provider schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!HttpProviderImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log
                        .error("QueryAllSchemaServlet: HttpProviderImpl schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("QueryAllSchemaServlet: Problem generating HttpProviderImpl schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!ProjectImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("QueryAllSchemaServlet: Project schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("QueryAllSchemaServlet: Problem generating Project schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!QueryTypeImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("QueryAllSchemaServlet: QueryType schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("QueryAllSchemaServlet: Problem generating QueryType schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!RegexNormalisationRuleImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log
                        .error("QueryAllSchemaServlet: RegexNormalisationRuleImpl schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error(
                    "QueryAllSchemaServlet: Problem generating RegexNormalisationRuleImpl schema RDF with type="
                            + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!SparqlNormalisationRuleImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log
                        .error("QueryAllSchemaServlet: SparqlNormalisationRuleImpl schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error(
                    "QueryAllSchemaServlet: Problem generating SparqlNormalisationRuleImpl schema RDF with type="
                            + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!XsltNormalisationRuleImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log
                        .error("QueryAllSchemaServlet: XsltNormalisationRuleImpl schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error(
                    "QueryAllSchemaServlet: Problem generating SparqlNormalisationRuleImpl schema RDF with type="
                            + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!RuleTestImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("QueryAllSchemaServlet: RuleTest schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("QueryAllSchemaServlet: Problem generating RuleTest schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!NamespaceEntryImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log
                        .error("QueryAllSchemaServlet: NamespaceEntry schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("QueryAllSchemaServlet: Problem generating NamespaceEntry schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!ProfileImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("QueryAllSchemaServlet: Profile schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("QueryAllSchemaServlet: Problem generating Profile schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!StatisticsEntry.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log
                        .error("QueryAllSchemaServlet: Statistics schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("QueryAllSchemaServlet: Problem generating Statistics schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!ProvenanceRecord.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log
                        .error("QueryAllSchemaServlet: Provenance schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("QueryAllSchemaServlet: Problem generating Provenance schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!QueryBundle.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log
                        .error("QueryAllSchemaServlet: QueryBundle schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("QueryAllSchemaServlet: Problem generating QueryBundle schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        return myRepository;
    }
    
    /**
     * @return a SPARQL Update language query that will either insert or delete triples about
     *         rdfObject
     * @throws OpenRDFException
     */
    public static String getSparulQueryForObject(final BaseQueryAllInterface rdfObject, final boolean isInsert,
            final boolean isDelete, final boolean useSparqlGraph, final String sparqlGraphUri) throws OpenRDFException
    {
        final Repository myRepository = new SailRepository(new MemoryStore());
        myRepository.initialize();
        
        // All queryall objects can be serialised to RDF using this method, along with a given
        // subject URI, which in this case is derived from the object
        final boolean rdfOkay = rdfObject.toRdf(myRepository, rdfObject.getKey(), Settings.CONFIG_API_VERSION);
        
        if(!rdfOkay && isInsert)
        {
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("RdfUtils.getSparulQueryForObject: could not convert to RDF");
            }
            
            return "";
        }
        
        // text/plain is the accepted MIME format for NTriples because they were too lazy to define
        // one... go figure
        final RDFFormat writerFormat = Rio.getWriterFormatForMIMEType("text/plain");
        
        final StringWriter insertTriples = new StringWriter();
        
        if(isInsert)
        {
            RdfUtils.toWriter(myRepository, insertTriples, writerFormat);
            
            RdfUtils.log
                    .debug("RdfUtils.getSparulQueryForObject: insertTriples.toString()=" + insertTriples.toString());
        }
        else if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("RdfUtils.getSparulQueryForObject: isInsert was false");
        }
        
        // NOTE: this looks messy because it is.
        // SPARUL doesn't play nicely if you don't know whether the delete will delete any triples,
        // and empty blocks are mandatory for the MODIFY statement if they are not applicable
        // The define sql:log-enable is a Virtuoso hack to enable SPARUL to work with more than one
        // thread at once
        // HACK: Specific to Virtuoso!
        String sparqlInsertQuery = "define sql:log-enable 2 MODIFY ";
        
        if(useSparqlGraph)
        {
            sparqlInsertQuery += " GRAPH <" + sparqlGraphUri + "> ";
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
        
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("RdfUtils.getInsertQueryForObject: sparqlInsertQuery=" + sparqlInsertQuery);
        }
        
        return sparqlInsertQuery;
    }
    
    public static Collection<Statement> getStatementsFromRepositoryByPredicateUris(final Repository nextRepository,
            final Collection<URI> predicateUris) throws OpenRDFException
    {
        final Collection<Statement> results = new HashSet<Statement>();
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("RdfUtils.getStatementsFromRepositoryByPredicateUris: entering method");
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
                catch(final OpenRDFException ordfe)
                {
                    RdfUtils.log
                            .error("RdfUtils.getValuesFromRepositoryByPredicateUris: RDF exception found for nextInputPredicate="
                                    + nextInputPredicateUri.stringValue());
                }
                catch(final Exception ex)
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
            final Repository nextRepository, final Collection<URI> predicateUris, final URI subjectUri)
        throws OpenRDFException
    {
        final Collection<Statement> results = new HashSet<Statement>();
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("RdfUtils.getStatementsFromRepositoryByPredicateUris: entering method");
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
                catch(final OpenRDFException ordfe)
                {
                    RdfUtils.log
                            .error("RdfUtils.getValuesFromRepositoryByPredicateUris: RDF exception found for nextInputPredicate="
                                    + nextInputPredicateUri.stringValue());
                }
                catch(final Exception ex)
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
            final Repository nextRepository, final URI predicateUri, final URI subjectUri) throws OpenRDFException
    {
        final Collection<URI> predicateUris = new HashSet<URI>();
        predicateUris.add(predicateUri);
        
        return RdfUtils.getStatementsFromRepositoryByPredicateUrisAndSubject(nextRepository, predicateUris, subjectUri);
    }
    
    // make sure that we are using UTF-8 to decode to item
    public static String getUTF8StringValueFromSesameValue(final Value nextValue)
    {
        try
        {
            return new String(nextValue.stringValue().getBytes("utf-8"), "utf-8");
        }
        catch(final java.io.UnsupportedEncodingException uee)
        {
            RdfUtils.log.fatal("RdfUtils: UTF-8 is not supported by this java vm!!!", uee);
            throw new RuntimeException("RdfUtils: UTF-8 is not supported by this java vm!!!", uee);
        }
    }
    
    /**
     * @param nextRepository
     * @param predicateUris
     * @return
     * @throws OpenRDFException
     */
    public static Collection<Value> getValuesFromRepositoryByPredicateUris(final Repository nextRepository,
            final Collection<URI> predicateUris) throws OpenRDFException
    {
        final Collection<Value> results = new HashSet<Value>();
        
        final Collection<Statement> relevantStatements =
                RdfUtils.getStatementsFromRepositoryByPredicateUris(nextRepository, predicateUris);
        
        for(final Statement nextStatement : relevantStatements)
        {
            results.add(nextStatement.getObject());
        }
        
        return results;
        
    }
    
    // TODO: make me more efficient
    public static Collection<Value> getValuesFromRepositoryByPredicateUrisAndSubject(final Repository nextRepository,
            final Collection<URI> predicateUris, final URI subjectUri) throws OpenRDFException
    {
        final Collection<Value> results = new HashSet<Value>();
        
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject: entering method");
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
                    final String queryString =
                            "CONSTRUCT { <" + subjectUri.stringValue() + "> <" + nextInputPredicateUri.stringValue()
                                    + "> ?object } WHERE { <" + subjectUri.stringValue() + "> <"
                                    + nextInputPredicateUri.stringValue() + "> ?object . }";
                    final GraphQuery tupleQuery = con.prepareGraphQuery(QueryLanguage.SPARQL, queryString);
                    final GraphQueryResult queryResult = tupleQuery.evaluate();
                    
                    if(RdfUtils._DEBUG)
                    {
                        RdfUtils.log.debug("RdfUtils: queryString=" + queryString);
                    }
                    
                    try
                    {
                        while(queryResult.hasNext())
                        {
                            final Statement nextStatement = queryResult.next();
                            
                            if(RdfUtils._DEBUG)
                            {
                                RdfUtils.log
                                        .debug("RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject: nextStatement="
                                                + nextStatement);
                            }
                            
                            results.add(nextStatement.getObject());
                            
                            // if(RdfUtils._DEBUG)
                            // {
                            // RdfUtils.log
                            // .debug("Utilities: found object: valueOfObject="
                            // + valueOfObject);
                            // }
                            
                            // results.add(new MemStatement(subjectUri, nextInputPredicateUri,
                            // valueOfObject, null, false, 0));
                            // results.add(getUTF8StringValueFromSesameValue(valueOfObject));
                        }
                    }
                    finally
                    {
                        queryResult.close();
                    }
                }
                catch(final OpenRDFException ordfe)
                {
                    RdfUtils.log
                            .error("RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject: RDF exception found for nextInputPredicate="
                                    + nextInputPredicateUri.stringValue() + " ordfe.class" + ordfe.getClass().getName(),
                                    ordfe);
                }
                catch(final Exception ex)
                {
                    RdfUtils.log.error(
                            "RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject: general exception found for nextInputPredicate="
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
    public static Collection<Value> getValuesFromRepositoryByPredicateUrisAndSubject(final Repository nextRepository,
            final URI predicateUri, final URI subjectUri) throws OpenRDFException
    {
        final Collection<URI> predicateUris = new HashSet<URI>();
        
        predicateUris.add(predicateUri);
        
        return RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(nextRepository, predicateUris, subjectUri);
    }
    
    public static RDFFormat getWriterFormat(final String requestedContentType)
    {
        if(requestedContentType.equals(Constants.TEXT_HTML))
        {
            return null;
        }
        
        return Rio.getWriterFormatForMIMEType(requestedContentType, RDFFormat.RDFXML);
    }
    
    public static void insertResultIntoRepository(final RdfFetcherQueryRunnable nextResult,
            final Repository myRepository, final QueryAllConfiguration localSettings) throws RepositoryException,
        java.io.IOException
    {
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("RdfUtils.insertResultIntoRepository: nextResult.toString()=" + nextResult.toString());
        }
        
        RepositoryConnection myRepositoryConnection = null;
        
        try
        {
            myRepositoryConnection = myRepository.getConnection();
            
            RDFFormat nextReaderFormat = RDFFormat.forMIMEType(nextResult.getReturnedMIMEType());
            
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("RdfUtils.insertResultIntoRepository: nextReaderFormat for returnedContentType="
                        + nextResult.getReturnedContentType() + " nextReaderFormat=" + nextReaderFormat);
            }
            
            if(nextReaderFormat == null)
            {
                final String assumedContentType =
                        nextResult.getOriginalQueryBundle().getProvider().getAssumedContentType();
                
                if(assumedContentType != null && assumedContentType.trim().length() > 0)
                {
                    nextReaderFormat = Rio.getParserFormatForMIMEType(assumedContentType);
                }
                
                if(nextReaderFormat == null)
                {
                    nextReaderFormat =
                            Rio.getParserFormatForMIMEType(localSettings.getStringProperty(
                                    "assumedResponseContentType", Constants.APPLICATION_RDF_XML));
                }
                
                if(nextReaderFormat == null)
                {
                    RdfUtils.log
                            .error("RdfUtils.insertResultIntoRepository: Not attempting to parse result because assumedResponseContentType isn't supported by Rio and the returned content type wasn't either nextResult.returnedMIMEType="
                                    + nextResult.getReturnedMIMEType()
                                    + " nextResult.assumedContentType="
                                    + assumedContentType
                                    + " Settings.getStringPropertyFromConfig(\"assumedResponseContentType\")="
                                    + localSettings.getStringProperty("assumedResponseContentType", ""));
                    // throw new
                    // RuntimeException("Utilities: Not attempting to parse because there are no content types to use for interpretation");
                }
                else if(nextResult.getWasSuccessful())
                {
                    RdfUtils.log
                            .warn("RdfUtils.insertResultIntoRepository: successful query, but readerFormat NOT matched for returnedMIMEType="
                                    + nextResult.getReturnedMIMEType());
                }
            }
            else if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("RdfUtils.insertResultIntoRepository: readerFormat matched for returnedMIMEType="
                        + nextResult.getReturnedMIMEType());
            }
            
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("RdfUtils.insertResultIntoRepository: nextResult.normalisedResult.length()="
                        + nextResult.getNormalisedResult().length());
            }
            
            if(RdfUtils._TRACE)
            {
                RdfUtils.log.trace("RdfUtils.insertResultIntoRepository: nextResult.normalisedResult="
                        + nextResult.getNormalisedResult());
            }
            
            if(nextReaderFormat != null && nextResult.getNormalisedResult().length() > 0)
            {
                myRepositoryConnection.add(new java.io.StringReader(nextResult.getNormalisedResult()),
                        localSettings.getDefaultHostAddress(), nextReaderFormat, nextResult.getOriginalQueryBundle()
                                .getProvider().getKey());
                
                myRepositoryConnection.commit();
            }
            
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("RdfUtils.insertResultIntoRepository: myRepositoryConnection.size()="
                        + myRepositoryConnection.size());
            }
        }
        catch(final org.openrdf.rio.RDFParseException rdfpe)
        {
            RdfUtils.log.error("RdfUtils.insertResultIntoRepository: RDFParseException result: nextResult.endpointUrl="
                    + nextResult.getEndpointUrl() + " message=" + rdfpe.getMessage());
            
            if(RdfUtils._TRACE)
            {
                RdfUtils.log.debug("RdfUtils.insertResultIntoRepository: RDFParseException result: normalisedResult="
                        + nextResult.getNormalisedResult());
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
                catch(final Exception ex)
                {
                    RdfUtils.log.error("RdfUtils.insertResultIntoRepository: finally section, caught exception", ex);
                }
            }
        }
    }
    
    public static void insertResultsIntoRepository(final Collection<RdfFetcherQueryRunnable> results,
            final Repository myRepository, final QueryAllConfiguration localSettings) throws RepositoryException,
        java.io.IOException
    {
        for(final RdfFetcherQueryRunnable nextResult : results)
        {
            RdfUtils.insertResultIntoRepository(nextResult, myRepository, localSettings);
        }
    }
    
    public static void retrieveUrls(final Collection<String> retrievalUrls, final String defaultResultFormat,
            final Repository myRepository, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController) throws InterruptedException
    {
        RdfUtils.retrieveUrls(retrievalUrls, defaultResultFormat, myRepository, localSettings,
                localBlacklistController, true);
    }
    
    public static void retrieveUrls(final Collection<String> retrievalUrls, final String defaultResultFormat,
            final Repository myRepository, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController, final boolean inParallel) throws InterruptedException
    {
        final Collection<RdfFetcherQueryRunnable> retrievalThreads = new HashSet<RdfFetcherQueryRunnable>();
        
        for(final String nextLocation : retrievalUrls)
        {
            final RdfFetcherQueryRunnable nextThread =
                    new RdfFetcherUriQueryRunnable(nextLocation, defaultResultFormat, "", "", defaultResultFormat,
                            localSettings, localBlacklistController, new QueryBundle());
            
            retrievalThreads.add(nextThread);
        }
        
        for(final RdfFetcherQueryRunnable nextThread : retrievalThreads)
        {
            nextThread.start();
            
            if(!inParallel)
            {
                // TODO: make it possible for users to configure either serial or parallel querying
                try
                {
                    nextThread.join();
                }
                catch(final InterruptedException ie)
                {
                    RdfUtils.log.error("RdfFetchController.fetchRdfForQuery: caught interrupted exception message="
                            + ie.getMessage());
                    throw ie;
                }
            }
        }
        
        if(inParallel)
        {
            for(final RdfFetcherQueryRunnable nextThread : retrievalThreads)
            {
                try
                {
                    nextThread.join();
                }
                catch(final InterruptedException ie)
                {
                    RdfUtils.log.error("RdfFetchController.fetchRdfForQuery: caught interrupted exception message="
                            + ie.getMessage());
                    throw ie;
                }
            }
        }
        
        try
        {
            RdfUtils.insertResultsIntoRepository(retrievalThreads, myRepository, localSettings);
        }
        catch(final RepositoryException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch(final IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    public static void retrieveUrls(final String retrievalUrl, final String defaultResultFormat,
            final Repository myRepository, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController) throws RepositoryException, java.io.IOException,
        InterruptedException
    {
        final Collection<String> retrievalList = new LinkedList<String>();
        retrievalList.add(retrievalUrl);
        
        RdfUtils.retrieveUrls(retrievalList, defaultResultFormat, myRepository, localSettings,
                localBlacklistController, true);
    }
    
    public static Collection<Statement> retrieveUrlsToStatements(final Collection<String> retrievalUrls,
            final String defaultResultFormat, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController) throws InterruptedException
    {
        Collection<Statement> results = new HashSet<Statement>();
        
        try
        {
            final Repository resultsRepository = new SailRepository(new MemoryStore());
            resultsRepository.initialize();
            
            RdfUtils.retrieveUrls(retrievalUrls, defaultResultFormat, resultsRepository, localSettings,
                    localBlacklistController, true);
            
            results = RdfUtils.getAllStatementsFromRepository(resultsRepository);
        }
        catch(final OpenRDFException e)
        {
            RdfUtils.log.error("RdfUtils.retrieveUrlsToStatements: caught OpenRDFException", e);
        }
        
        return results;
    }
    
    /**
     * @param nextRepository
     * @param outputStream
     */
    public static void toOutputStream(final Repository nextRepository, final java.io.OutputStream outputStream)
    {
        RdfUtils.toOutputStream(nextRepository, outputStream, RDFFormat.RDFXML);
    }
    
    /**
     * @param nextRepository
     * @param outputStream
     * @param format
     */
    public static void toOutputStream(final Repository nextRepository, final java.io.OutputStream outputStream,
            final RDFFormat format)
    {
        RepositoryConnection nextConnection = null;
        
        try
        {
            nextConnection = nextRepository.getConnection();
            
            nextConnection.export(Rio.createWriter(format, outputStream));
        }
        catch(final RepositoryException e)
        {
            RdfUtils.log.error("repository exception", e);
        }
        catch(final RDFHandlerException e)
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
            catch(final RepositoryException rex)
            {
                RdfUtils.log.error("RdfUtils.toWriter: connection didn't close correctly", rex);
            }
        }
    }
    
    /**
     * @param nextConnection
     * @return
     */
    public static String toString(final Repository nextRepository)
    {
        final java.io.StringWriter stBuff = new java.io.StringWriter();
        
        RepositoryConnection nextConnection = null;
        
        try
        {
            nextConnection = nextRepository.getConnection();
            
            nextConnection.export(Rio.createWriter(RDFFormat.RDFXML, stBuff));
        }
        catch(final RepositoryException e)
        {
            RdfUtils.log.error("repository exception", e);
        }
        catch(final RDFHandlerException e)
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
            catch(final RepositoryException rex)
            {
                RdfUtils.log.error("RdfUtils.toWriter: connection didn't close correctly", rex);
            }
        }
        
        return stBuff.toString();
    }
    
    /**
     * @param nextRepository
     * @param nextWriter
     */
    public static void toWriter(final Repository nextRepository, final java.io.Writer nextWriter)
    {
        RdfUtils.toWriter(nextRepository, nextWriter, RDFFormat.RDFXML);
    }
    
    /**
     * @param nextRepository
     * @param nextWriter
     * @param format
     */
    public static void toWriter(final Repository nextRepository, final java.io.Writer nextWriter, final RDFFormat format)
    {
        RepositoryConnection nextConnection = null;
        
        try
        {
            nextConnection = nextRepository.getConnection();
            
            nextConnection.export(Rio.createWriter(format, nextWriter));
        }
        catch(final RepositoryException e)
        {
            RdfUtils.log.error("repository exception", e);
        }
        catch(final RDFHandlerException e)
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
            catch(final RepositoryException rex)
            {
                RdfUtils.log.error("RdfUtils.toWriter: connection didn't close correctly", rex);
            }
        }
    }
    
    // from http://java.sun.com/developer/technicalArticles/ThirdParty/WebCrawler/WebCrawler.java
    // License at http://developers.sun.com/license/berkeley_license.html
    @SuppressWarnings("unused")
    public boolean robotSafe(final URL url)
    {
        final String DISALLOW = "Disallow:";
        final String strHost = url.getHost();
        
        // TODO: Implement me!!!
        return true;
        /*****
         * // form URL of the robots.txt file String strRobot = "http://" + strHost + "/robots.txt";
         * URL urlRobot; try { urlRobot = new URL(strRobot); } catch (MalformedURLException e) { //
         * something weird is happening, so don't trust it return false; }
         * 
         * String strCommands;
         * 
         * try { InputStream urlRobotStream = urlRobot.openStream();
         * 
         * // read in entire file byte b[] = new byte[10000]; int numRead = urlRobotStream.read(b);
         * strCommands = new String(b, 0, numRead); while (numRead != -1) { if
         * (Thread.currentThread() != searchThread) break; numRead = urlRobotStream.read(b); if
         * (numRead != -1) { String newCommands = new String(b, 0, numRead); strCommands +=
         * newCommands; } } urlRobotStream.close(); } catch (IOException e) { // if there is no
         * robots.txt file, it is OK to search return true; }
         * 
         * // assume that this robots.txt refers to us and // search for "Disallow:" commands.
         * String strURL = url.getFile(); int index = 0; while ((index =
         * strCommands.indexOf(DISALLOW, index)) != -1) { index += DISALLOW.length(); String strPath
         * = strCommands.substring(index); StringTokenizer st = new StringTokenizer(strPath);
         * 
         * if (!st.hasMoreTokens()) break;
         * 
         * String strBadPath = st.nextToken();
         * 
         * // if the URL starts with a disallowed path, it is not safe if
         * (strURL.indexOf(strBadPath) == 0) return false; }
         * 
         * return true;
         *****/
    }
    
}
