package org.queryall.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.BooleanLiteralImpl;
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
import org.queryall.api.base.BaseQueryAllInterface;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.namespace.NamespaceEntryEnum;
import org.queryall.api.namespace.NamespaceEntrySchema;
import org.queryall.api.profile.Profile;
import org.queryall.api.profile.ProfileEnum;
import org.queryall.api.profile.ProfileSchema;
import org.queryall.api.project.Project;
import org.queryall.api.project.ProjectEnum;
import org.queryall.api.project.ProjectSchema;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.Provider;
import org.queryall.api.provider.ProviderEnum;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.querytype.QueryTypeEnum;
import org.queryall.api.querytype.QueryTypeSchema;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.NormalisationRuleEnum;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.RegexNormalisationRuleSchema;
import org.queryall.api.rdfrule.SparqlNormalisationRuleSchema;
import org.queryall.api.rdfrule.XsltNormalisationRuleSchema;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.services.ServiceUtils;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.blacklist.BlacklistController;
import org.queryall.impl.provider.HttpProviderImpl;
import org.queryall.impl.querytype.QueryTypeImpl;
import org.queryall.impl.ruletest.RuleTestImpl;
import org.queryall.query.HttpUrlQueryRunnable;
import org.queryall.query.ProvenanceRecord;
import org.queryall.query.QueryBundle;
import org.queryall.query.RdfFetchController;
import org.queryall.query.RdfFetcherQueryRunnable;
import org.queryall.query.RdfFetcherUriQueryRunnable;
import org.queryall.query.Settings;
import org.queryall.statistics.StatisticsEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class to deal with RDF data and resolve RDF queries
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class RdfUtils
{
    public static final Logger log = LoggerFactory.getLogger(RdfUtils.class);
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
                RdfUtils.log.debug("copyAllStatementsToRepository: mySourceConnection.size()="
                        + mySourceConnection.size());
            }
            myDestinationConnection = destination.getConnection();
            myDestinationConnection.add(mySourceConnection.getStatements(null, null, null, true));
            
            myDestinationConnection.commit();
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("copyAllStatementsToRepository: myDestinationConnection.size()="
                        + myDestinationConnection.size());
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("copyAllStatementsToRepository", ex);
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
            endpointUrls.add(hostToUse + new QueryTypeImpl().getDefaultNamespace() + localSettings.getSeparator()
                    + StringUtils.percentEncode(nsAndIdList.get(1)));
            nextQueryBundle.setQueryEndpoint(hostToUse + new QueryTypeImpl().getDefaultNamespace()
                    + localSettings.getSeparator() + StringUtils.percentEncode(nsAndIdList.get(1)));
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
        dummyProvider.setEndpointMethod(HttpProviderSchema.getProviderHttpGetUrl());
        dummyProvider.setKey(hostToUse + QueryAllNamespaces.PROVIDER.getNamespace() + localSettings.getSeparator()
                + StringUtils.percentEncode(namespaceAndIdentifier));
        dummyProvider.setIsDefaultSource(true);
        
        nextQueryBundle.setProvider(dummyProvider);
        
        final QueryType dummyQuery = new QueryTypeImpl();
        
        dummyQuery.setKey(hostToUse + QueryAllNamespaces.QUERY.getNamespace() + localSettings.getSeparator()
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
        
        dummyProvider.setEndpointMethod(HttpProviderSchema.getProviderHttpPostSparql());
        dummyProvider.setKey(localSettings.getDefaultHostAddress() + QueryAllNamespaces.PROVIDER.getNamespace()
                + localSettings.getSeparator() + StringUtils.percentEncode(nextQueryKey.stringValue()));
        dummyProvider.setIsDefaultSource(true);
        
        nextQueryBundle.setOriginalProvider(dummyProvider);
        
        final QueryType dummyQuery = new QueryTypeImpl();
        
        dummyQuery.setKey(localSettings.getDefaultHostAddress() + QueryAllNamespaces.PROVIDER.getNamespace()
                + localSettings.getSeparator() + StringUtils.percentEncode(nextQueryKey.stringValue()));
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
                localSettings, localBlacklistController);
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
            RdfUtils.log.error("getAllStatementsFromRepository: outer caught exception ", ordfe);
            
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
        
        // if(nextValue instanceof CalendarLiteralImpl)
        // {
        // result =
        // ((CalendarLiteralImpl)nextValue).calendarValue().toGregorianCalendar().getTime();
        // }
        // else if(nextValue instanceof CalendarMemLiteral)
        // {
        // result = ((CalendarMemLiteral)nextValue).calendarValue().toGregorianCalendar().getTime();
        // }
        // else
        // {
        try
        {
            result = Constants.ISO8601UTC().parse(nextValue.stringValue());
        }
        catch(final java.text.ParseException pe)
        {
            RdfUtils.log.error("Could not parse date using ISO8601UTC: nextValue.stringValue="
                    + nextValue.stringValue());
            try
            {
                result = DateFormat.getDateInstance().parse(nextValue.stringValue());
            }
            catch(final java.text.ParseException pe2)
            {
                RdfUtils.log.error("Could not parse date using default date format: nextValue.stringValue="
                        + nextValue.stringValue());
                
                throw pe2;
            }
        }
        // }
        
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
            RdfUtils.log.debug("getDistinctObjectsFromRepository: entering method");
            // RdfUtils.log.debug(nextRepository);
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
        // .debug("getDistinctObjectsFromRepository: nextInputPredicate was null or empty");
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
            RdfUtils.log.error("getDistinctObjectsFromRepository: RDF exception", ordfe);
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("getDistinctObjectsFromRepository: general exception", ex);
        }
        // }
        // }
        
        // catch(OpenRDFException ordfe)
        // {
        // log.error("getDistinctObjectsFromRepository: error found");
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
            RdfUtils.log.debug("getDistinctSubjectsFromRepository: entering method");
            // RdfUtils.log.debug(nextRepository);
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
        // .debug("getDistinctSubjectsFromRepository: nextInputPredicate was null or empty");
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
            RdfUtils.log.error("getDistinctSubjectsFromRepository: RDF exception", ordfe);
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("getDistinctSubjectsFromRepository: general exception", ex);
        }
        // }
        // }
        
        // catch(OpenRDFException ordfe)
        // {
        // log.error("getDistinctSubjectsFromRepository: error found");
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
        
        if(nextValue instanceof IntegerLiteralImpl)
        {
            result = ((IntegerLiteralImpl)nextValue).intValue();
        }
        else if(nextValue instanceof IntegerMemLiteral)
        {
            result = ((IntegerMemLiteral)nextValue).intValue();
        }
        else
        {
            result = Integer.parseInt(nextValue.stringValue());
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
        
        if(nextValue instanceof IntegerMemLiteral)
        {
            result = ((IntegerMemLiteral)nextValue).longValue();
        }
        else
        {
            try
            {
                result = Long.parseLong(nextValue.stringValue());
            }
            catch(final NumberFormatException nfe)
            {
                RdfUtils.log.error("getLongFromValue: failed to parse value using Long.parseLong type="
                        + nextValue.getClass().getName() + " nextValue.stringValue=" + nextValue.stringValue());
                
                throw nfe;
            }
        }
        
        return result;
    }
    
    public static Map<URI, NamespaceEntry> getNamespaceEntries(final Repository myRepository)
    {
        final long start = System.currentTimeMillis();
        RepositoryConnection con = null;
        
        try
        {
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("getNamespaceEntrys: started parsing namespace entrys");
            }
            
            // This is the base namespace entry URI, extensions or plugins must include this URI
            // alongside their customised type URIs
            final URI providerTypeUri = NamespaceEntrySchema.getNamespaceTypeUri();
            
            final Map<URI, NamespaceEntry> results = new ConcurrentHashMap<URI, NamespaceEntry>();
            
            con = myRepository.getConnection();
            
            final List<Statement> allDeclaredNamespaceEntrySubjects =
                    con.getStatements(null, RDF.TYPE, providerTypeUri, true).asList();
            
            final Map<URI, Collection<NamespaceEntryEnum>> uriToNamespaceEntryEnums = new HashMap<URI, Collection<NamespaceEntryEnum>>();
            
            // TODO: why is this necessary
            ServiceUtils.getAllEnums();
            
            for(final Statement nextDeclaredNamespaceEntrySubject : allDeclaredNamespaceEntrySubjects)
            {
                if(!(nextDeclaredNamespaceEntrySubject.getSubject() instanceof URI))
                {
                    RdfUtils.log.error("We do not support blank nodes as namespace entry identifiers");
                    continue;
                }
                
                final URI nextSubjectUri = (URI)nextDeclaredNamespaceEntrySubject.getSubject();
                
                final Collection<Value> nextNamespaceEntryValues =
                        RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(myRepository, RDF.TYPE,
                                (URI)nextDeclaredNamespaceEntrySubject.getSubject());
                final List<URI> nextNamespaceEntryUris = new ArrayList<URI>(nextNamespaceEntryValues.size());
                for(final Value nextNamespaceEntryValue : nextNamespaceEntryValues)
                {
                    if(nextNamespaceEntryValue instanceof URI)
                    {
                        nextNamespaceEntryUris.add((URI)nextNamespaceEntryValue);
                    }
                }
                
                final Collection<NamespaceEntryEnum> matchingNamespaceEntryEnums = NamespaceEntryEnum.byTypeUris(nextNamespaceEntryUris);
                
                RdfUtils.log.info("getNamespaceEntrys: matchingNamespaceEntryEnums=" + matchingNamespaceEntryEnums);
                
                if(matchingNamespaceEntryEnums.size() > 0)
                {
                    uriToNamespaceEntryEnums.put(nextSubjectUri, matchingNamespaceEntryEnums);
                }
                else
                {
                    RdfUtils.log.warn("No namespace entry enums found for {}", nextSubjectUri.stringValue());
                }
            }
            
            for(final URI nextSubjectUri : uriToNamespaceEntryEnums.keySet())
            {
                final Collection<NamespaceEntryEnum> nextNamespaceEntryEnums = uriToNamespaceEntryEnums.get(nextSubjectUri);
                
                for(final NamespaceEntryEnum nextNamespaceEntryEnum : nextNamespaceEntryEnums)
                {
                    results.put(
                            nextSubjectUri,
                            ServiceUtils.createNamespaceEntryParser(nextNamespaceEntryEnum).createObject(
                                    con.getStatements(nextSubjectUri, (URI)null, (Value)null, true).asList(),
                                    nextSubjectUri, Settings.CONFIG_API_VERSION));
                }
            }
            
            if(RdfUtils._INFO)
            {
                final long end = System.currentTimeMillis();
                RdfUtils.log.info(String.format("%s: timing=%10d", "getNamespaceEntrys", (end - start)));
            }
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("getNamespaceEntrys: finished parsing namespace entrys");
            }
            
            return results;
        }
        catch(final OpenRDFException e)
        {
            // handle exception
            RdfUtils.log.error("getNamespaceEntrys:", e);
        }
        finally
        {
            if(con != null)
            {
                try
                {
                    con.close();
                }
                catch(final RepositoryException e)
                {
                    RdfUtils.log.error("RepositoryException", e);
                }
            }
        }
        
        return Collections.emptyMap();
    }
    
    public static Map<URI, NormalisationRule> getNormalisationRules(final Repository myRepository)
    {
        final long start = System.currentTimeMillis();
        RepositoryConnection con = null;
        
        try
        {
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("getNormalisationRules: started parsing normalisation rules");
            }
            
            // This is the base normalisation rule URI, extensions or plugins must include this URI
            // alongside their customised type URIs
            final URI normalisationRuleUri = NormalisationRuleSchema.getNormalisationRuleTypeUri();
            
            final Map<URI, NormalisationRule> results = new ConcurrentHashMap<URI, NormalisationRule>();
            
            con = myRepository.getConnection();
            
            final List<Statement> allDeclaredNormalisationRuleSubjects =
                    con.getStatements(null, RDF.TYPE, normalisationRuleUri, true).asList();
            
            final Map<URI, Collection<NormalisationRuleEnum>> uriToNormalisationRuleEnums =
                    new HashMap<URI, Collection<NormalisationRuleEnum>>();
            
            // TODO: why is this necessary
            ServiceUtils.getAllEnums();
            
            for(final Statement nextDeclaredNormalisationRuleSubject : allDeclaredNormalisationRuleSubjects)
            {
                if(!(nextDeclaredNormalisationRuleSubject.getSubject() instanceof URI))
                {
                    RdfUtils.log.error("We do not support blank nodes as normalisation rule identifiers");
                    continue;
                }
                
                final URI nextSubjectUri = (URI)nextDeclaredNormalisationRuleSubject.getSubject();
                
                final Collection<Value> nextNormalisationRuleValues =
                        RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(myRepository, RDF.TYPE,
                                (URI)nextDeclaredNormalisationRuleSubject.getSubject());
                final List<URI> nextNormalisationRuleUris = new ArrayList<URI>(nextNormalisationRuleValues.size());
                for(final Value nextNormalisationRuleValue : nextNormalisationRuleValues)
                {
                    if(nextNormalisationRuleValue instanceof URI)
                    {
                        nextNormalisationRuleUris.add((URI)nextNormalisationRuleValue);
                    }
                }
                
                final Collection<NormalisationRuleEnum> matchingNormalisationRuleEnums =
                        NormalisationRuleEnum.byTypeUris(nextNormalisationRuleUris);
                
                RdfUtils.log.info("getQueryTypes: matchingNormalisationRuleEnums=" + matchingNormalisationRuleEnums);
                
                if(matchingNormalisationRuleEnums.size() > 0)
                {
                    uriToNormalisationRuleEnums.put(nextSubjectUri, matchingNormalisationRuleEnums);
                }
                else
                {
                    RdfUtils.log.warn("No normalisation rule enums found for {}", nextSubjectUri.stringValue());
                }
            }
            
            for(final URI nextSubjectUri : uriToNormalisationRuleEnums.keySet())
            {
                final Collection<NormalisationRuleEnum> nextNormalisationRuleEnums =
                        uriToNormalisationRuleEnums.get(nextSubjectUri);
                
                for(final NormalisationRuleEnum nextNormalisationRuleEnum : nextNormalisationRuleEnums)
                {
                    results.put(
                            nextSubjectUri,
                            ServiceUtils.createNormalisationRuleParser(nextNormalisationRuleEnum).createObject(
                                    con.getStatements(nextSubjectUri, (URI)null, (Value)null, true).asList(),
                                    nextSubjectUri, Settings.CONFIG_API_VERSION));
                }
            }
            
            if(RdfUtils._INFO)
            {
                final long end = System.currentTimeMillis();
                RdfUtils.log.info(String.format("%s: timing=%10d", "getNormalisationRules", (end - start)));
            }
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("getNormalisationRules: finished parsing normalisation rules");
            }
            
            return results;
        }
        catch(final OpenRDFException e)
        {
            // handle exception
            RdfUtils.log.error("getNormalisationRules:", e);
        }
        finally
        {
            if(con != null)
            {
                try
                {
                    con.close();
                }
                catch(final RepositoryException e)
                {
                    RdfUtils.log.error("RepositoryException", e);
                }
            }
        }
        
        return Collections.emptyMap();
    }
    
    // public static Map<URI, NormalisationRule> getNormalisationRules(final Repository
    // myRepository)
    // {
    // if(RdfUtils._DEBUG)
    // {
    // RdfUtils.log.debug("getNormalisationRules: started parsing rdf normalisation rules");
    // }
    //
    // final long start = System.currentTimeMillis();
    //
    // final Map<URI, NormalisationRule> results = new ConcurrentHashMap<URI, NormalisationRule>();
    //
    // try
    // {
    // final RepositoryConnection con = myRepository.getConnection();
    //
    // // Import Regular Expression Normalisation Rules first
    // final URI regexRuleTypeUri = RegexNormalisationRuleSchema.getRegexRuleTypeUri();
    // for(final Statement nextRegexRule : con.getStatements(null, RDF.TYPE, regexRuleTypeUri,
    // true).asList())
    // {
    // final URI nextSubjectUri = (URI)nextRegexRule.getSubject();
    // results.put(nextSubjectUri,
    // new RegexNormalisationRuleImpl(con.getStatements(nextSubjectUri, (URI)null, (Value)null,
    // true)
    // .asList(), nextSubjectUri, Settings.CONFIG_API_VERSION));
    // }
    //
    // // Then do the same thing for SPARQL Normalisation Rules
    // final URI sparqlRuleTypeUri = SparqlNormalisationRuleSchema.getSparqlRuleTypeUri();
    // for(final Statement nextSparqlRule : con.getStatements(null, RDF.TYPE, sparqlRuleTypeUri,
    // true).asList())
    // {
    // final URI nextSubjectUri = (URI)nextSparqlRule.getSubject();
    // results.put(nextSubjectUri,
    // new SparqlNormalisationRuleImpl(con.getStatements(nextSubjectUri, (URI)null, (Value)null,
    // true)
    // .asList(), nextSubjectUri, Settings.CONFIG_API_VERSION));
    // }
    //
    // // Then do the same thing for XSLT Normalisation Rules
    // final URI xsltRuleTypeUri = XsltNormalisationRuleSchema.getXsltRuleTypeUri();
    // for(final Statement nextXsltRule : con.getStatements(null, RDF.TYPE, xsltRuleTypeUri,
    // true).asList())
    // {
    // final URI nextSubjectUri = (URI)nextXsltRule.getSubject();
    // results.put(nextSubjectUri,
    // new XsltNormalisationRuleImpl(con.getStatements(nextSubjectUri, (URI)null, (Value)null, true)
    // .asList(), nextSubjectUri, Settings.CONFIG_API_VERSION));
    // }
    // }
    // catch(final OpenRDFException e)
    // {
    // // handle exception
    // RdfUtils.log.error("getNormalisationRules:", e);
    // }
    // if(RdfUtils._INFO)
    // {
    // final long end = System.currentTimeMillis();
    // RdfUtils.log.info(String.format("%s: timing=%10d", "getNormalisationRules", (end - start)));
    // }
    // if(RdfUtils._DEBUG)
    // {
    // RdfUtils.log.debug("getNormalisationRules: finished parsing normalisation rules");
    // }
    //
    // return results;
    // }
    
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
            RdfUtils.log.debug("getObjectUrisFromRepositoryByPredicateUris: entering method");
            // RdfUtils.log.debug(nextRepository);
            // RdfUtils.log.debug(predicateUris);
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
                                .debug("getObjectUrisFromRepositoryByPredicateUris: nextInputPredicate was null or empty");
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
                            .error("getObjectUrisFromRepositoryByPredicateUris: RDF exception found for nextInputPredicate="
                                    + nextInputPredicate);
                }
                catch(final Exception ex)
                {
                    RdfUtils.log
                            .error("getObjectUrisFromRepositoryByPredicateUris: general exception found for nextInputPredicate="
                                    + nextInputPredicate);
                }
            }
        }
        
        // catch(OpenRDFException ordfe)
        // {
        // log.error("getObjectUrisFromRepositoryByPredicateUris: error found");
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
        final long start = System.currentTimeMillis();
        RepositoryConnection con = null;
        
        try
        {
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("getProfiles: started parsing profiles");
            }
            
            // This is the base profile URI, extensions or plugins must include this URI
            // alongside their customised type URIs
            final URI providerTypeUri = ProfileSchema.getProfileTypeUri();
            
            final Map<URI, Profile> results = new ConcurrentHashMap<URI, Profile>();
            
            con = myRepository.getConnection();
            
            final List<Statement> allDeclaredProfileSubjects =
                    con.getStatements(null, RDF.TYPE, providerTypeUri, true).asList();
            
            final Map<URI, Collection<ProfileEnum>> uriToProfileEnums = new HashMap<URI, Collection<ProfileEnum>>();
            
            // TODO: why is this necessary
            ServiceUtils.getAllEnums();
            
            for(final Statement nextDeclaredProfileSubject : allDeclaredProfileSubjects)
            {
                if(!(nextDeclaredProfileSubject.getSubject() instanceof URI))
                {
                    RdfUtils.log.error("We do not support blank nodes as profile identifiers");
                    continue;
                }
                
                final URI nextSubjectUri = (URI)nextDeclaredProfileSubject.getSubject();
                
                final Collection<Value> nextProfileValues =
                        RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(myRepository, RDF.TYPE,
                                (URI)nextDeclaredProfileSubject.getSubject());
                final List<URI> nextProfileUris = new ArrayList<URI>(nextProfileValues.size());
                for(final Value nextProfileValue : nextProfileValues)
                {
                    if(nextProfileValue instanceof URI)
                    {
                        nextProfileUris.add((URI)nextProfileValue);
                    }
                }
                
                final Collection<ProfileEnum> matchingProfileEnums = ProfileEnum.byTypeUris(nextProfileUris);
                
                RdfUtils.log.info("getProfiles: matchingProfileEnums=" + matchingProfileEnums);
                
                if(matchingProfileEnums.size() > 0)
                {
                    uriToProfileEnums.put(nextSubjectUri, matchingProfileEnums);
                }
                else
                {
                    RdfUtils.log.warn("No profile enums found for {}", nextSubjectUri.stringValue());
                }
            }
            
            for(final URI nextSubjectUri : uriToProfileEnums.keySet())
            {
                final Collection<ProfileEnum> nextProfileEnums = uriToProfileEnums.get(nextSubjectUri);
                
                for(final ProfileEnum nextProfileEnum : nextProfileEnums)
                {
                    results.put(
                            nextSubjectUri,
                            ServiceUtils.createProfileParser(nextProfileEnum).createObject(
                                    con.getStatements(nextSubjectUri, (URI)null, (Value)null, true).asList(),
                                    nextSubjectUri, Settings.CONFIG_API_VERSION));
                }
            }
            
            if(RdfUtils._INFO)
            {
                final long end = System.currentTimeMillis();
                RdfUtils.log.info(String.format("%s: timing=%10d", "getProfiles", (end - start)));
            }
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("getProfiles: finished parsing profiles");
            }
            
            return results;
        }
        catch(final OpenRDFException e)
        {
            // handle exception
            RdfUtils.log.error("getProfiles:", e);
        }
        finally
        {
            if(con != null)
            {
                try
                {
                    con.close();
                }
                catch(final RepositoryException e)
                {
                    RdfUtils.log.error("RepositoryException", e);
                }
            }
        }
        
        return Collections.emptyMap();
    }
    
    public static Map<URI, Project> getProjects(final Repository myRepository)
    {
        final long start = System.currentTimeMillis();
        RepositoryConnection con = null;
        
        try
        {
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("getProjects: started parsing projects");
            }
            
            // This is the base project URI, extensions or plugins must include this URI
            // alongside their customised type URIs
            final URI providerTypeUri = ProjectSchema.getProjectTypeUri();
            
            final Map<URI, Project> results = new ConcurrentHashMap<URI, Project>();
            
            con = myRepository.getConnection();
            
            final List<Statement> allDeclaredProjectSubjects =
                    con.getStatements(null, RDF.TYPE, providerTypeUri, true).asList();
            
            final Map<URI, Collection<ProjectEnum>> uriToProjectEnums = new HashMap<URI, Collection<ProjectEnum>>();
            
            // TODO: why is this necessary
            ServiceUtils.getAllEnums();
            
            for(final Statement nextDeclaredProjectSubject : allDeclaredProjectSubjects)
            {
                if(!(nextDeclaredProjectSubject.getSubject() instanceof URI))
                {
                    RdfUtils.log.error("We do not support blank nodes as project identifiers");
                    continue;
                }
                
                final URI nextSubjectUri = (URI)nextDeclaredProjectSubject.getSubject();
                
                final Collection<Value> nextProjectValues =
                        RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(myRepository, RDF.TYPE,
                                (URI)nextDeclaredProjectSubject.getSubject());
                final List<URI> nextProjectUris = new ArrayList<URI>(nextProjectValues.size());
                for(final Value nextProjectValue : nextProjectValues)
                {
                    if(nextProjectValue instanceof URI)
                    {
                        nextProjectUris.add((URI)nextProjectValue);
                    }
                }
                
                final Collection<ProjectEnum> matchingProjectEnums = ProjectEnum.byTypeUris(nextProjectUris);
                
                RdfUtils.log.info("getProjects: matchingProjectEnums=" + matchingProjectEnums);
                
                if(matchingProjectEnums.size() > 0)
                {
                    uriToProjectEnums.put(nextSubjectUri, matchingProjectEnums);
                }
                else
                {
                    RdfUtils.log.warn("No project enums found for {}", nextSubjectUri.stringValue());
                }
            }
            
            for(final URI nextSubjectUri : uriToProjectEnums.keySet())
            {
                final Collection<ProjectEnum> nextProjectEnums = uriToProjectEnums.get(nextSubjectUri);
                
                for(final ProjectEnum nextProjectEnum : nextProjectEnums)
                {
                    results.put(
                            nextSubjectUri,
                            ServiceUtils.createProjectParser(nextProjectEnum).createObject(
                                    con.getStatements(nextSubjectUri, (URI)null, (Value)null, true).asList(),
                                    nextSubjectUri, Settings.CONFIG_API_VERSION));
                }
            }
            
            if(RdfUtils._INFO)
            {
                final long end = System.currentTimeMillis();
                RdfUtils.log.info(String.format("%s: timing=%10d", "getProjects", (end - start)));
            }
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("getProjects: finished parsing projects");
            }
            
            return results;
        }
        catch(final OpenRDFException e)
        {
            // handle exception
            RdfUtils.log.error("getProjects:", e);
        }
        finally
        {
            if(con != null)
            {
                try
                {
                    con.close();
                }
                catch(final RepositoryException e)
                {
                    RdfUtils.log.error("RepositoryException", e);
                }
            }
        }
        
        return Collections.emptyMap();
    }
    
    public static Map<URI, Provider> getProviders(final Repository myRepository)
    {
        final long start = System.currentTimeMillis();
        RepositoryConnection con = null;
        
        try
        {
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("getProviders: started parsing providers");
            }
            
            // This is the base provider URI, extensions or plugins must include this URI
            // alongside
            // their customised type URIs
            final URI providerUri = ProviderSchema.getProviderTypeUri();
            
            RdfUtils.log.info("getProviders: providerUri=" + providerUri.stringValue());
            
            final Map<URI, Provider> results = new ConcurrentHashMap<URI, Provider>();
            
            con = myRepository.getConnection();
            
            final List<Statement> allDeclaredProviderSubjects =
                    con.getStatements(null, RDF.TYPE, providerUri, true).asList();
            
            RdfUtils.log.info("getProviders: allDeclaredProviderSubjects.size()=" + allDeclaredProviderSubjects.size());
            
            final Map<URI, Collection<ProviderEnum>> uriToProviderEnums = new HashMap<URI, Collection<ProviderEnum>>();
            
            // TODO: why is this necessary
            ServiceUtils.getAllEnums();
            
            for(final Statement nextDeclaredProviderSubject : allDeclaredProviderSubjects)
            {
                RdfUtils.log.info("getProviders: nextDeclaredProviderSubject.getSubject()="
                        + nextDeclaredProviderSubject.getSubject().stringValue());
                
                if(!(nextDeclaredProviderSubject.getSubject() instanceof URI))
                {
                    RdfUtils.log.error("We do not support blank nodes as provider identifiers");
                    continue;
                }
                
                final URI nextSubjectUri = (URI)nextDeclaredProviderSubject.getSubject();
                
                final Collection<Value> nextProviderValues =
                        RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(myRepository, RDF.TYPE,
                                (URI)nextDeclaredProviderSubject.getSubject());
                final List<URI> nextProviderUris = new ArrayList<URI>(nextProviderValues.size());
                for(final Value nextProviderValue : nextProviderValues)
                {
                    if(nextProviderValue instanceof URI)
                    {
                        nextProviderUris.add((URI)nextProviderValue);
                    }
                }
                
                final Collection<ProviderEnum> matchingProviderEnums = ProviderEnum.byTypeUris(nextProviderUris);
                
                RdfUtils.log.info("getProviders: matchingProviderEnums=" + matchingProviderEnums);
                
                uriToProviderEnums.put(nextSubjectUri, matchingProviderEnums);
            }
            
            for(final URI nextSubjectUri : uriToProviderEnums.keySet())
            {
                final Collection<ProviderEnum> nextProviderEnums = uriToProviderEnums.get(nextSubjectUri);
                
                for(final ProviderEnum nextProviderEnum : nextProviderEnums)
                {
                    results.put(
                            nextSubjectUri,
                            ServiceUtils.createProviderParser(nextProviderEnum).createObject(
                                    con.getStatements(nextSubjectUri, (URI)null, (Value)null, true).asList(),
                                    nextSubjectUri, Settings.CONFIG_API_VERSION));
                }
            }
            
            if(RdfUtils._INFO)
            {
                final long end = System.currentTimeMillis();
                RdfUtils.log.info(String.format("%s: timing=%10d", "getProviders", (end - start)));
            }
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("getProviders: finished parsing providers");
            }
            
            return results;
        }
        catch(final OpenRDFException e)
        {
            // handle exception
            RdfUtils.log.error("getProviders:", e);
        }
        finally
        {
            if(con != null)
            {
                try
                {
                    con.close();
                }
                catch(final RepositoryException e)
                {
                    RdfUtils.log.error("RepositoryException", e);
                }
            }
        }
        
        return Collections.emptyMap();
    }
    
    public static Map<URI, QueryType> getQueryTypes(final Repository myRepository)
    {
        final long start = System.currentTimeMillis();
        RepositoryConnection con = null;
        
        try
        {
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("getQueryTypes: started parsing query types");
            }
            
            // This is the base query type URI, extensions or plugins must include this URI
            // alongside
            // their customised type URIs
            final URI queryTypeUri = QueryTypeSchema.getQueryTypeUri();
            
            final Map<URI, QueryType> results = new ConcurrentHashMap<URI, QueryType>();
            
            con = myRepository.getConnection();
            
            final List<Statement> allDeclaredQueryTypeSubjects =
                    con.getStatements(null, RDF.TYPE, queryTypeUri, true).asList();
            
            final Map<URI, Collection<QueryTypeEnum>> uriToQueryTypeEnums =
                    new HashMap<URI, Collection<QueryTypeEnum>>();
            
            // TODO: why is this necessary
            ServiceUtils.getAllEnums();
            
            for(final Statement nextDeclaredQueryTypeSubject : allDeclaredQueryTypeSubjects)
            {
                if(!(nextDeclaredQueryTypeSubject.getSubject() instanceof URI))
                {
                    RdfUtils.log.error("We do not support blank nodes as query type identifiers");
                    continue;
                }
                
                final URI nextSubjectUri = (URI)nextDeclaredQueryTypeSubject.getSubject();
                
                final Collection<Value> nextQueryTypeValues =
                        RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(myRepository, RDF.TYPE,
                                (URI)nextDeclaredQueryTypeSubject.getSubject());
                final List<URI> nextQueryTypeUris = new ArrayList<URI>(nextQueryTypeValues.size());
                for(final Value nextQueryTypeValue : nextQueryTypeValues)
                {
                    if(nextQueryTypeValue instanceof URI)
                    {
                        nextQueryTypeUris.add((URI)nextQueryTypeValue);
                    }
                }
                
                final Collection<QueryTypeEnum> matchingQueryTypeEnums = QueryTypeEnum.byTypeUris(nextQueryTypeUris);
                
                RdfUtils.log.info("getQueryTypes: matchingQueryTypeEnums=" + matchingQueryTypeEnums);
                
                uriToQueryTypeEnums.put(nextSubjectUri, matchingQueryTypeEnums);
            }
            
            for(final URI nextSubjectUri : uriToQueryTypeEnums.keySet())
            {
                final Collection<QueryTypeEnum> nextQueryTypeEnums = uriToQueryTypeEnums.get(nextSubjectUri);
                
                for(final QueryTypeEnum nextQueryTypeEnum : nextQueryTypeEnums)
                {
                    results.put(
                            nextSubjectUri,
                            ServiceUtils.createQueryTypeParser(nextQueryTypeEnum).createObject(
                                    con.getStatements(nextSubjectUri, (URI)null, (Value)null, true).asList(),
                                    nextSubjectUri, Settings.CONFIG_API_VERSION));
                }
            }
            
            if(RdfUtils._INFO)
            {
                final long end = System.currentTimeMillis();
                RdfUtils.log.info(String.format("%s: timing=%10d", "getQueryTypes", (end - start)));
            }
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("getQueryTypes: finished parsing query types");
            }
            
            return results;
        }
        catch(final OpenRDFException e)
        {
            // handle exception
            RdfUtils.log.error("getQueryTypes:", e);
        }
        finally
        {
            if(con != null)
            {
                try
                {
                    con.close();
                }
                catch(final RepositoryException e)
                {
                    RdfUtils.log.error("RepositoryException", e);
                }
            }
        }
        
        return Collections.emptyMap();
    }
    
    public static Collection<QueryType> getQueryTypesForQueryBundles(final Collection<QueryBundle> queryBundles,
            final int modelVersion, final QueryAllConfiguration localSettings)
    {
        // TODO: remove call to BlacklistController.getDefaultController() here
        final RdfFetchController fetchController =
                new RdfFetchController(localSettings, BlacklistController.getDefaultController(), queryBundles);
        
        try
        {
            fetchController.fetchRdfForQueries();
        }
        catch(final InterruptedException ie)
        {
            RdfUtils.log.error("getQueryTypesForQueryBundles: interrupted exception", ie);
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
                        RdfUtils.log.debug("getQueryTypesForQueryBundles: nextReaderFormat for returnedContentType="
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
                                    .error("getQueryTypesForQueryBundles: Not attempting to parse result because Settings.getStringPropertyFromConfig(\"assumedResponseContentType\") isn't supported by Rio and the returned content type wasn't either nextResult.returnedMIMEType="
                                            + nextResult.getReturnedMIMEType()
                                            + " Settings.getStringPropertyFromConfig(\"assumedResponseContentType\")="
                                            + localSettings.getStringProperty("assumedResponseContentType", ""));
                            continue;
                        }
                        else
                        {
                            RdfUtils.log
                                    .warn("getQueryTypesForQueryBundles: readerFormat NOT matched for returnedMIMEType="
                                            + nextResult.getReturnedMIMEType()
                                            + " using configured preferred content type as fallback Settings.getStringPropertyFromConfig(\"assumedResponseContentType\")="
                                            + localSettings.getStringProperty("assumedResponseContentType", ""));
                        }
                    }
                    else if(RdfUtils.log.isDebugEnabled())
                    {
                        RdfUtils.log.debug("getQueryTypesForQueryBundles: readerFormat matched for returnedMIMEType="
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
                    RdfUtils.log.error("getQueryTypesForQueryBundles: RDFParseException", rdfpe);
                }
                catch(final org.openrdf.repository.RepositoryException re)
                {
                    RdfUtils.log.error("getQueryTypesForQueryBundles: RepositoryException inner", re);
                }
                catch(final java.io.IOException ioe)
                {
                    RdfUtils.log.error("getQueryTypesForQueryBundles: IOException", ioe);
                }
            } // end for(RdfFetcherQueryRunnable nextResult : rdfResults)
        }
        catch(final org.openrdf.repository.RepositoryException re)
        {
            RdfUtils.log.error("getQueryTypesForQueryBundles: RepositoryException outer", re);
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
                RdfUtils.log.error("getQueryTypesForQueryBundles: failed to close repository connection", re2);
            }
        }
        
        final Map<URI, QueryType> results = RdfUtils.getQueryTypes(myRepository);
        
        return results.values();
    }
    
    public static Map<URI, RuleTest> getRuleTests(final Repository myRepository)
    {
        final Map<URI, RuleTest> results = new ConcurrentHashMap<URI, RuleTest>();
        
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("getRuleTests: started parsing rule test configurations");
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
            RdfUtils.log.error("getRuleTests:", e);
        }
        
        if(RdfUtils._INFO)
        {
            final long end = System.currentTimeMillis();
            RdfUtils.log.info(String.format("%s: timing=%10d", "getRuleTests", (end - start)));
        }
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("getRuleTests: finished getting rdf rule tests");
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
            RdfUtils.log.error("Could not initialise repository for schemas");
            throw new RuntimeException(e);
        }
        
        try
        {
            if(!ProviderSchema.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("Provider schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("Problem generating Provider schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!HttpProviderSchema.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("HttpProviderImpl schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("Problem generating HttpProviderImpl schema RDF with type=" + ex.getClass().getName(),
                    ex);
        }
        
        try
        {
            if(!ProjectSchema.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("Project schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("Problem generating Project schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!QueryTypeSchema.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("QueryType schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("Problem generating QueryType schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!RegexNormalisationRuleSchema.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("RegexNormalisationRuleImpl schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("Problem generating RegexNormalisationRuleImpl schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!SparqlNormalisationRuleSchema.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("SparqlNormalisationRuleImpl schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("Problem generating SparqlNormalisationRuleImpl schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!XsltNormalisationRuleSchema.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("XsltNormalisationRuleImpl schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("Problem generating SparqlNormalisationRuleImpl schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!RuleTestImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("RuleTest schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("Problem generating RuleTest schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!NamespaceEntrySchema.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("NamespaceEntry schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("Problem generating NamespaceEntry schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!ProfileSchema.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("Profile schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("Problem generating Profile schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!StatisticsEntry.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("Statistics schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("Problem generating Statistics schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!ProvenanceRecord.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("Provenance schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("Problem generating Provenance schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!QueryBundle.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
            {
                RdfUtils.log.error("QueryBundle schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            RdfUtils.log.error("Problem generating QueryBundle schema RDF with type=" + ex.getClass().getName(), ex);
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
                RdfUtils.log.debug("getSparulQueryForObject: could not convert to RDF");
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
            
            RdfUtils.log.debug("getSparulQueryForObject: insertTriples.toString()=" + insertTriples.toString());
        }
        else if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("getSparulQueryForObject: isInsert was false");
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
            RdfUtils.log.debug("getInsertQueryForObject: sparqlInsertQuery=" + sparqlInsertQuery);
        }
        
        return sparqlInsertQuery;
    }
    
    public static Collection<Statement> getStatementsFromRepositoryByPredicateUris(final Repository nextRepository,
            final Collection<URI> predicateUris) throws OpenRDFException
    {
        final Collection<Statement> results = new HashSet<Statement>();
        if(RdfUtils._DEBUG)
        {
            RdfUtils.log.debug("getStatementsFromRepositoryByPredicateUris: entering method");
            // RdfUtils.log.debug(nextRepository);
            // RdfUtils.log.debug(predicateUris);
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
                            .error("getValuesFromRepositoryByPredicateUris: RDF exception found for nextInputPredicate="
                                    + nextInputPredicateUri.stringValue());
                }
                catch(final Exception ex)
                {
                    RdfUtils.log
                            .error("getValuesFromRepositoryByPredicateUris: general exception found for nextInputPredicate="
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
            RdfUtils.log.debug("getStatementsFromRepositoryByPredicateUris: entering method");
            // RdfUtils.log.debug(nextRepository);
            // RdfUtils.log.debug(predicateUris);
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
                            .error("getValuesFromRepositoryByPredicateUris: RDF exception found for nextInputPredicate="
                                    + nextInputPredicateUri.stringValue());
                }
                catch(final Exception ex)
                {
                    RdfUtils.log
                            .error("getValuesFromRepositoryByPredicateUris: general exception found for nextInputPredicate="
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
            RdfUtils.log.error("UTF-8 is not supported by this java vm!!!", uee);
            throw new RuntimeException("UTF-8 is not supported by this java vm!!!", uee);
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
            RdfUtils.log.debug("getValuesFromRepositoryByPredicateUrisAndSubject: entering method");
            // RdfUtils.log.debug(nextRepository);
            // RdfUtils.log.debug(predicateUris);
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
                        RdfUtils.log.debug("queryString=" + queryString);
                    }
                    
                    try
                    {
                        while(queryResult.hasNext())
                        {
                            final Statement nextStatement = queryResult.next();
                            
                            if(RdfUtils._DEBUG)
                            {
                                RdfUtils.log.debug("getValuesFromRepositoryByPredicateUrisAndSubject: nextStatement="
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
                            .error("getValuesFromRepositoryByPredicateUrisAndSubject: RDF exception found for nextInputPredicate="
                                    + nextInputPredicateUri.stringValue() + " ordfe.class" + ordfe.getClass().getName(),
                                    ordfe);
                }
                catch(final Exception ex)
                {
                    RdfUtils.log.error(
                            "getValuesFromRepositoryByPredicateUrisAndSubject: general exception found for nextInputPredicate="
                                    + nextInputPredicateUri.stringValue(), ex);
                }
            }
        }
        
        // catch(OpenRDFException ordfe)
        // {
        // log.error("getValuesFromRepositoryByPredicateUris: error found");
        // throw ordfe;
        // }
        finally
        {
            if(con != null)
            {
                con.close();
            }
        }
        // log.info("getValuesFromRepositoryByPredicateUrisAndSubject: results.size()="+results.size());
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
            RdfUtils.log.debug("insertResultIntoRepository: nextResult.toString()=" + nextResult.toString());
        }
        
        RepositoryConnection myRepositoryConnection = null;
        
        try
        {
            myRepositoryConnection = myRepository.getConnection();
            
            RDFFormat nextReaderFormat = RDFFormat.forMIMEType(nextResult.getReturnedMIMEType());
            
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("insertResultIntoRepository: nextReaderFormat for returnedContentType="
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
                            .error("insertResultIntoRepository: Not attempting to parse result because assumedResponseContentType isn't supported by Rio and the returned content type wasn't either nextResult.returnedMIMEType="
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
                            .warn("insertResultIntoRepository: successful query, but readerFormat NOT matched for returnedMIMEType="
                                    + nextResult.getReturnedMIMEType());
                }
            }
            else if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("insertResultIntoRepository: readerFormat matched for returnedMIMEType="
                        + nextResult.getReturnedMIMEType());
            }
            
            if(RdfUtils._DEBUG)
            {
                RdfUtils.log.debug("insertResultIntoRepository: nextResult.normalisedResult.length()="
                        + nextResult.getNormalisedResult().length());
            }
            
            if(RdfUtils._TRACE)
            {
                RdfUtils.log.trace("insertResultIntoRepository: nextResult.normalisedResult="
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
                RdfUtils.log.debug("insertResultIntoRepository: myRepositoryConnection.size()="
                        + myRepositoryConnection.size());
            }
        }
        catch(final org.openrdf.rio.RDFParseException rdfpe)
        {
            RdfUtils.log.error("insertResultIntoRepository: RDFParseException result: nextResult.endpointUrl="
                    + nextResult.getEndpointUrl() + " message=" + rdfpe.getMessage());
            
            if(RdfUtils._TRACE)
            {
                RdfUtils.log.debug("insertResultIntoRepository: RDFParseException result: normalisedResult="
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
                    RdfUtils.log.error("insertResultIntoRepository: finally section, caught exception", ex);
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
                    new RdfFetcherUriQueryRunnable(nextLocation, "", "", defaultResultFormat, localSettings,
                            localBlacklistController, new QueryBundle());
            
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
                    RdfUtils.log.error("fetchRdfForQuery: caught interrupted exception message=" + ie.getMessage());
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
                    RdfUtils.log.error("fetchRdfForQuery: caught interrupted exception message=" + ie.getMessage());
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
            RdfUtils.log.error("retrieveUrlsToStatements: caught OpenRDFException", e);
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
                RdfUtils.log.error("toWriter: connection didn't close correctly", rex);
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
                RdfUtils.log.error("toWriter: connection didn't close correctly", rex);
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
                RdfUtils.log.error("toWriter: connection didn't close correctly", rex);
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
