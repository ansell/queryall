package org.queryall.utils;

import info.aduna.iteration.Iterations;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;
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
import org.queryall.api.provider.HttpProvider;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.Provider;
import org.queryall.api.provider.ProviderEnum;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.provider.SparqlProviderSchema;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.querytype.QueryTypeEnum;
import org.queryall.api.querytype.QueryTypeSchema;
import org.queryall.api.querytype.RegexInputQueryType;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.NormalisationRuleEnum;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.SparqlConstructRuleSchema;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.ruletest.RuleTestEnum;
import org.queryall.api.ruletest.RuleTestSchema;
import org.queryall.api.services.ServiceUtils;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.api.utils.WebappConfig;
import org.queryall.blacklist.BlacklistController;
import org.queryall.comparators.StatementComparator;
import org.queryall.exception.QueryAllException;
import org.queryall.exception.QueryAllRuntimeException;
import org.queryall.exception.UnsupportedNamespaceEntryException;
import org.queryall.exception.UnsupportedNormalisationRuleException;
import org.queryall.exception.UnsupportedProfileException;
import org.queryall.exception.UnsupportedProjectException;
import org.queryall.exception.UnsupportedProviderException;
import org.queryall.exception.UnsupportedQueryTypeException;
import org.queryall.exception.UnsupportedRuleTestException;
import org.queryall.query.HttpUrlQueryRunnableImpl;
import org.queryall.query.QueryBundle;
import org.queryall.query.RdfFetchController;
import org.queryall.query.RdfFetcherQueryRunnable;
import org.queryall.query.RdfFetcherQueryRunnableImpl;
import org.queryall.query.RdfFetcherUriQueryRunnableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class to deal with RDF data and resolve RDF queries
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class RdfUtils
{
    private static final Logger log = LoggerFactory.getLogger(RdfUtils.class);
    private static final boolean TRACE = RdfUtils.log.isTraceEnabled();
    private static final boolean DEBUG = RdfUtils.log.isDebugEnabled();
    private static final boolean INFO = RdfUtils.log.isInfoEnabled();
    
    /**
     * Performs the ASK queries until one returns false, or they are all executed.
     * 
     * If the list is either empty or they all return true, the method will return true, otherwise
     * false.
     * 
     * @param myRepository
     *            The input repository to execute the queries against.
     * @param sparqlAskQueries
     *            The list of SPARQL ASK queries to execute against the given Repository
     * @return True if the list is empty or all of the queries return true, otherwise false.
     */
    public static boolean checkSparqlAskQueries(final Repository myRepository, final List<String> sparqlAskQueries)
        throws QueryAllException
    {
        RepositoryConnection askConnection = null;
        try
        {
            askConnection = myRepository.getConnection();
            
            for(final String nextAskQuery : sparqlAskQueries)
            {
                if(RdfUtils.DEBUG)
                {
                    RdfUtils.log.debug("chooseStatementsFromRepository nextAskQuery=" + nextAskQuery);
                }
                
                boolean evaluate = false;
                
                try
                {
                    evaluate = askConnection.prepareBooleanQuery(QueryLanguage.SPARQL, nextAskQuery).evaluate();
                }
                catch(final QueryEvaluationException e)
                {
                    RdfUtils.log.error("Found QueryEvaluationException", e);
                    throw new QueryAllException("Found QueryEvaluationException", e);
                }
                catch(final RepositoryException e)
                {
                    RdfUtils.log.error("Found RepositoryException", e);
                    throw new QueryAllException("Found RepositoryException", e);
                }
                catch(final MalformedQueryException e)
                {
                    RdfUtils.log.error("Found MalformedQueryException", e);
                    throw new QueryAllException("Found MalformedQueryException", e);
                }
                
                if(!evaluate)
                {
                    return false;
                }
            }
        }
        catch(final RepositoryException e1)
        {
            RdfUtils.log.error("Found RepositoryException", e1);
            throw new QueryAllException("Found RepositoryException", e1);
        }
        finally
        {
            if(askConnection != null)
            {
                try
                {
                    askConnection.close();
                }
                catch(final RepositoryException e)
                {
                    RdfUtils.log.error("Found RepositoryException", e);
                    throw new QueryAllException("Found RepositoryException", e);
                }
            }
        }
        
        return true;
    }
    
    public static Repository chooseStatementsFromRepository(final Repository myRepository,
            final boolean addToMyRepository, final List<String> sparqlConstructQueries)
    {
        Repository resultRepository = null;
        
        try
        {
            if(!addToMyRepository)
            {
                resultRepository = new SailRepository(new MemoryStore());
                resultRepository.initialize();
            }
            
            final RepositoryConnection selectConnection = myRepository.getConnection();
            RepositoryConnection addConnection = null;
            
            if(addToMyRepository)
            {
                addConnection = myRepository.getConnection();
            }
            else
            {
                addConnection = resultRepository.getConnection();
            }
            
            try
            {
                for(final String nextConstructQuery : sparqlConstructQueries)
                {
                    if(RdfUtils.DEBUG)
                    {
                        RdfUtils.log.debug("chooseStatementsFromRepository nextConstructQueries=" + nextConstructQuery);
                    }
                    
                    try
                    {
                        addConnection.begin();
                        
                        final GraphQueryResult graphResult =
                                selectConnection.prepareGraphQuery(QueryLanguage.SPARQL, nextConstructQuery).evaluate();
                        
                        int selectedStatements = 0;
                        
                        while(graphResult.hasNext())
                        {
                            final Statement nextStatement = graphResult.next();
                            
                            if(RdfUtils.TRACE)
                            {
                                RdfUtils.log.trace("adding statement: " + nextStatement);
                            }
                            
                            addConnection.add(nextStatement);
                            selectedStatements++;
                        }
                        
                        if(RdfUtils.DEBUG)
                        {
                            RdfUtils.log.debug("SparqlNormalisationRuleImpl: selected " + selectedStatements
                                    + " statements for results");
                        }
                        
                        addConnection.commit();
                    }
                    catch(final Exception ex)
                    {
                        addConnection.rollback();
                        RdfUtils.log.error("SparqlNormalisationRuleImpl: exception adding statements", ex);
                    }
                }
            }
            finally
            {
                selectConnection.close();
                addConnection.close();
            }
        }
        catch(final org.openrdf.repository.RepositoryException rex)
        {
            RdfUtils.log.error("SparqlNormalisationRuleImpl: RepositoryException exception before adding statements",
                    rex);
        }
        
        if(addToMyRepository)
        {
            return myRepository;
        }
        else
        {
            return resultRepository;
        }
    }
    
    public static void copyAllStatementsToRepository(final Repository destination, final Repository source)
    {
        RepositoryConnection mySourceConnection = null;
        RepositoryConnection myDestinationConnection = null;
        
        try
        {
            mySourceConnection = source.getConnection();
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("copyAllStatementsToRepository: mySourceConnection.size()="
                        + mySourceConnection.size());
            }
            myDestinationConnection = destination.getConnection();
            myDestinationConnection.add(mySourceConnection.getStatements(null, null, null, true));
            
            myDestinationConnection.commit();
            if(RdfUtils.DEBUG)
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
    
    /**
     * Note: The order of the methods inside is important, as we don't want to remove mapping
     * statements in the delete sections
     * 
     * @param output
     * @param inputUriPrefix
     * @param outputUriPrefix
     * @param nextSubjectMappingPredicates
     * @param nextPredicateMappingPredicates
     *            TODO
     * @param nextObjectMappingPredicates
     *            TODO
     * @return
     */
    public static Repository doMappingQueries(Repository output, final String inputUriPrefix,
            final String outputUriPrefix, final Collection<URI> nextSubjectMappingPredicates,
            final Collection<URI> nextPredicateMappingPredicates, final Collection<URI> nextObjectMappingPredicates)
    {
        final StringBuilder addObjectConstructBuilder = new StringBuilder(nextObjectMappingPredicates.size() * 120);
        
        for(final URI nextMappingPredicate : nextObjectMappingPredicates)
        {
            addObjectConstructBuilder.append(" ?normalisedObjectUri <" + nextMappingPredicate.stringValue()
                    + "> ?objectUri . ");
        }
        
        final String addObjectTemplateWhere =
                " ?subjectUri ?predicateUri ?objectUri . filter(isIRI(?objectUri) && strStarts(str(?objectUri), \""
                        + inputUriPrefix + "\")) . bind(iri(concat(\"" + outputUriPrefix
                        + "\", encode_for_uri(substr(str(?objectUri), " + (inputUriPrefix.length() + 1)
                        + ")))) AS ?normalisedObjectUri) ";
        
        final String addObjectTemplate =
                "CONSTRUCT { ?subjectUri ?predicateUri ?normalisedObjectUri . " + addObjectConstructBuilder.toString()
                        + " } WHERE { " + addObjectTemplateWhere + " } ";
        
        if(RdfUtils.DEBUG)
        {
            RdfUtils.log.debug("addObjectTemplate=" + addObjectTemplate);
        }
        
        final List<String> addObjectQueries = new ArrayList<String>(1);
        
        addObjectQueries.add(addObjectTemplate);
        
        output =
                RdfUtils.doSparqlConstructWorkBasedOnMode(output,
                        SparqlConstructRuleSchema.getSparqlRuleModeAddAllMatchingTriples(), addObjectQueries);
        
        if(RdfUtils.TRACE)
        {
            RdfUtils.toOutputStream(output, System.err);
        }
        
        // Need to make sure that we don't nuke the mappings that were generated by the add above
        final StringBuilder deleteObjectConstructBuilder = new StringBuilder(nextObjectMappingPredicates.size() * 120);
        
        for(final URI nextMappingPredicate : nextObjectMappingPredicates)
        {
            deleteObjectConstructBuilder.append("MINUS { ?subjectUri <" + nextMappingPredicate.stringValue()
                    + "> ?objectUri . } ");
        }
        
        final String deleteObjectTemplate =
                "CONSTRUCT {  ?subjectUri ?predicateUri ?objectUri . } WHERE {  ?subjectUri ?predicateUri ?objectUri . "
                        + deleteObjectConstructBuilder.toString()
                        + " filter(isIRI(?objectUri) && strStarts(str(?objectUri), \"" + inputUriPrefix + "\")) . }";
        
        if(RdfUtils.DEBUG)
        {
            RdfUtils.log.debug("deleteObjectTemplate=" + deleteObjectTemplate);
        }
        
        final List<String> deleteObjectQueries = new ArrayList<String>(1);
        
        deleteObjectQueries.add(deleteObjectTemplate);
        
        output =
                RdfUtils.doSparqlConstructWorkBasedOnMode(output,
                        SparqlConstructRuleSchema.getSparqlRuleModeOnlyDeleteMatches(), deleteObjectQueries);
        
        if(RdfUtils.TRACE)
        {
            RdfUtils.toOutputStream(output, System.err);
        }
        
        final StringBuilder addSubjectConstructBuilder = new StringBuilder(nextSubjectMappingPredicates.size() * 120);
        
        for(final URI nextMappingPredicate : nextSubjectMappingPredicates)
        {
            addSubjectConstructBuilder.append(" ?normalisedSubjectUri <" + nextMappingPredicate.stringValue()
                    + "> ?subjectUri . ");
        }
        
        final String addSubjectTemplateWhere =
                " ?subjectUri ?predicateUri ?objectUri . filter(isIRI(?subjectUri) && strStarts(str(?subjectUri), \""
                        + inputUriPrefix + "\")) . bind(iri(concat(\"" + outputUriPrefix
                        + "\", encode_for_uri(substr(str(?subjectUri), " + (inputUriPrefix.length() + 1)
                        + ")))) AS ?normalisedSubjectUri) ";
        
        final String addSubjectTemplate =
                "CONSTRUCT { ?normalisedSubjectUri ?predicateUri ?objectUri . " + addSubjectConstructBuilder.toString()
                        + " } WHERE { " + addSubjectTemplateWhere + " } ";
        
        final List<String> addSubjectQueries = new ArrayList<String>(1);
        
        addSubjectQueries.add(addSubjectTemplate);
        
        output =
                RdfUtils.doSparqlConstructWorkBasedOnMode(output,
                        SparqlConstructRuleSchema.getSparqlRuleModeAddAllMatchingTriples(), addSubjectQueries);
        
        if(RdfUtils.TRACE)
        {
            RdfUtils.toOutputStream(output, System.err);
        }
        
        final String deleteSubjectTemplate =
                "CONSTRUCT {  ?subjectUri ?predicateUri ?objectUri . } WHERE {  ?subjectUri ?predicateUri ?objectUri . filter(isIRI(?subjectUri) && strStarts(str(?subjectUri), \""
                        + inputUriPrefix + "\")) . }";
        
        final List<String> deleteSubjectQueries = new ArrayList<String>(1);
        
        deleteSubjectQueries.add(deleteSubjectTemplate);
        
        output =
                RdfUtils.doSparqlConstructWorkBasedOnMode(output,
                        SparqlConstructRuleSchema.getSparqlRuleModeOnlyDeleteMatches(), deleteSubjectQueries);
        
        if(RdfUtils.TRACE)
        {
            RdfUtils.toOutputStream(output, System.err);
        }
        
        final StringBuilder addPredicateConstructBuilder =
                new StringBuilder(nextPredicateMappingPredicates.size() * 120);
        
        for(final URI nextMappingPredicate : nextPredicateMappingPredicates)
        {
            addPredicateConstructBuilder.append(" ?normalisedPredicateUri <" + nextMappingPredicate.stringValue()
                    + "> ?predicateUri . ");
        }
        
        final String addPredicateTemplateWhere =
                " ?subjectUri ?predicateUri ?objectUri . filter(isIRI(?predicateUri) && strStarts(str(?predicateUri), \""
                        + inputUriPrefix + "\")) . bind(iri(concat(\"" + outputUriPrefix
                        + "\", encode_for_uri(substr(str(?predicateUri), " + (inputUriPrefix.length() + 1)
                        + ")))) AS ?normalisedPredicateUri) ";
        
        final String addPredicateTemplate =
                "CONSTRUCT { ?subjectUri ?normalisedPredicateUri ?objectUri . "
                        + addPredicateConstructBuilder.toString() + " } WHERE { " + addPredicateTemplateWhere + " } ";
        
        final List<String> addPredicateQueries = new ArrayList<String>(1);
        
        addPredicateQueries.add(addPredicateTemplate);
        
        output =
                RdfUtils.doSparqlConstructWorkBasedOnMode(output,
                        SparqlConstructRuleSchema.getSparqlRuleModeAddAllMatchingTriples(), addPredicateQueries);
        
        if(RdfUtils.TRACE)
        {
            RdfUtils.toOutputStream(output, System.err);
        }
        
        final String deletePredicateTemplate =
                "CONSTRUCT {  ?subjectUri ?predicateUri ?objectUri . } WHERE { ?subjectUri ?predicateUri ?objectUri . filter(isIRI(?predicateUri) && strStarts(str(?predicateUri), \""
                        + inputUriPrefix + "\")) . } ";
        
        final List<String> deletePredicateQueries = new ArrayList<String>(1);
        
        deletePredicateQueries.add(deletePredicateTemplate);
        
        output =
                RdfUtils.doSparqlConstructWorkBasedOnMode(output,
                        SparqlConstructRuleSchema.getSparqlRuleModeOnlyDeleteMatches(), deletePredicateQueries);
        
        if(RdfUtils.TRACE)
        {
            RdfUtils.toOutputStream(output, System.err);
        }
        
        return output;
    }
    
    /**
     * Performs changes to the input repository based on the mode of the sparql construct rule
     * 
     * @param input
     *            A repository containing the current set of RDF statements
     * @param nextMode
     *            TODO
     * @param sparqlConstructQueries
     *            TODO
     * @return A repository containing the output set of RDF statements, after normalisation by this
     *         rule
     */
    public static Repository doSparqlConstructWorkBasedOnMode(final Repository input, final URI nextMode,
            final List<String> sparqlConstructQueries)
    {
        if(nextMode.equals(SparqlConstructRuleSchema.getSparqlRuleModeOnlyDeleteMatches()))
        {
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("doWorkBasedOnMode: only delete matches");
            }
            return RdfUtils.removeStatementsFromRepository(input, sparqlConstructQueries);
        }
        else if(nextMode.equals(SparqlConstructRuleSchema.getSparqlRuleModeOnlyIncludeMatches()))
        {
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("doWorkBasedOnMode: only include matches");
            }
            return RdfUtils.chooseStatementsFromRepository(input, false, sparqlConstructQueries);
        }
        else if(nextMode.equals(SparqlConstructRuleSchema.getSparqlRuleModeAddAllMatchingTriples()))
        {
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("doWorkBasedOnMode: add all matches");
            }
            return RdfUtils.chooseStatementsFromRepository(input, true, sparqlConstructQueries);
        }
        
        return input;
    }
    
    public static Collection<QueryType> fetchQueryTypeByKey(final String hostToUse, final URI nextQueryKey,
            final int modelVersion, final QueryAllConfiguration localSettings, final HttpProvider dummyProvider,
            final RegexInputQueryType dummyQuery) throws InterruptedException, QueryAllException
    {
        final QueryBundle nextQueryBundle = new QueryBundle();
        
        // final HttpProviderImpl dummyProvider = new HttpOnlyProviderImpl();
        
        // final Collection<String> endpointUrls = new HashSet<String>();
        
        // if(nextQueryKey.startsWith(localSettings.getDefaultHostAddress()))
        // {
        final String namespaceAndIdentifier =
                nextQueryKey.stringValue().substring(localSettings.getDefaultHostAddress().length());
        
        final Map<String, List<String>> nsAndIdList =
                StringUtils.getNamespaceAndIdentifier(namespaceAndIdentifier, localSettings);
        
        if(nsAndIdList == null)
        {
            throw new IllegalArgumentException("nextQueryKey did not contain a namespace and identifier nextQueryKey="
                    + nextQueryKey);
        }
        
        if(nsAndIdList.size() == 2)
        {
            dummyProvider.addEndpointUrl(hostToUse + QueryAllNamespaces.QUERY.getNamespace()
                    + localSettings.getSeparator() + StringUtils.percentEncode(nsAndIdList.get("input_1").get(0)));
            nextQueryBundle.addAlternativeEndpointAndQuery(hostToUse + QueryAllNamespaces.QUERY.getNamespace()
                    + localSettings.getSeparator() + StringUtils.percentEncode(nsAndIdList.get("input1").get(0)), "");
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
        
        dummyProvider.setEndpointMethod(HttpProviderSchema.getProviderHttpGetUrl());
        dummyProvider.setKey(hostToUse + QueryAllNamespaces.PROVIDER.getNamespace() + localSettings.getSeparator()
                + StringUtils.percentEncode(namespaceAndIdentifier));
        dummyProvider.setIsDefaultSource(true);
        
        nextQueryBundle.setProvider(dummyProvider);
        
        // final QueryType dummyQuery = new RegexInputQueryTypeImpl();
        
        dummyQuery.setKey(hostToUse + QueryAllNamespaces.QUERY.getNamespace() + localSettings.getSeparator()
                + StringUtils.percentEncode(namespaceAndIdentifier));
        dummyQuery.setTitle("$$__queryfetch__$$");
        dummyQuery.setIncludeDefaults(true);
        
        nextQueryBundle.setQueryType(dummyQuery);
        
        return RdfUtils.fetchQueryTypesForQueryBundles(Collections.singleton(nextQueryBundle), modelVersion,
                localSettings, BlacklistController.getDefaultController());
    }
    
    public static Collection<QueryType> fetchQueryTypeByKey(final URI nextQueryKey, final boolean useSparqlGraph,
            final String sparqlGraphUri, final String sparqlEndpointUrl, final int modelVersion,
            final QueryAllConfiguration localSettings, final HttpProvider dummyProvider,
            final RegexInputQueryType dummyQuery) throws QueryAllException
    {
        final String constructQueryString =
                RdfUtils.getConstructQueryForKey(nextQueryKey, useSparqlGraph, sparqlGraphUri);
        
        final QueryBundle nextQueryBundle = new QueryBundle();
        
        // final HttpProviderImpl dummyProvider = new HttpOnlyProviderImpl();
        
        dummyProvider.addEndpointUrl(sparqlEndpointUrl);
        
        dummyProvider.setEndpointMethod(SparqlProviderSchema.getProviderHttpPostSparql());
        dummyProvider.setKey(localSettings.getDefaultHostAddress() + QueryAllNamespaces.PROVIDER.getNamespace()
                + localSettings.getSeparator() + StringUtils.percentEncode(nextQueryKey.stringValue()));
        dummyProvider.setIsDefaultSource(true);
        
        nextQueryBundle.setOriginalProvider(dummyProvider);
        
        // final QueryType dummyQuery = new RegexInputQueryTypeImpl();
        
        dummyQuery.setKey(localSettings.getDefaultHostAddress() + QueryAllNamespaces.PROVIDER.getNamespace()
                + localSettings.getSeparator() + StringUtils.percentEncode(nextQueryKey.stringValue()));
        dummyQuery.setTitle("$$__queryfetch__$$");
        dummyQuery.setIncludeDefaults(true);
        
        nextQueryBundle.setQueryType(dummyQuery);
        nextQueryBundle.addAlternativeEndpointAndQuery(sparqlEndpointUrl, constructQueryString);
        // nextQueryBundle.setQuery(constructQueryString);
        // nextQueryBundle.setQueryEndpoint(sparqlEndpointUrl);
        
        return RdfUtils.fetchQueryTypesForQueryBundles(Arrays.asList(nextQueryBundle), modelVersion, localSettings,
                BlacklistController.getDefaultController());
    }
    
    /**
     * FIXME: Why does this function fetch?
     * 
     * @param queryBundles
     * @param modelVersion
     * @param localSettings
     * @param blacklistController
     * @return
     * @throws QueryAllException
     */
    public static Collection<QueryType> fetchQueryTypesForQueryBundles(final Collection<QueryBundle> queryBundles,
            final int modelVersion, final QueryAllConfiguration localSettings,
            final BlacklistController blacklistController) throws QueryAllException
    {
        final RdfFetchController fetchController =
                new RdfFetchController(localSettings, blacklistController, queryBundles);
        
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
                                Rio.getParserFormatForMIMEType(localSettings
                                        .getStringProperty(WebappConfig.ASSUMED_RESPONSE_CONTENT_TYPE));
                        
                        if(nextReaderFormat == null)
                        {
                            RdfUtils.log
                                    .error("getQueryTypesForQueryBundles: Not attempting to parse result because Settings.getStringPropertyFromConfig(\"assumedResponseContentType\") isn't supported by Rio and the returned content type wasn't either nextResult.returnedMIMEType="
                                            + nextResult.getReturnedMIMEType()
                                            + " localSettings.getStringProperty(WebappConfig.ASSUMED_RESPONSE_CONTENT_TYPE)="
                                            + localSettings
                                                    .getStringProperty(WebappConfig.ASSUMED_RESPONSE_CONTENT_TYPE));
                            continue;
                        }
                        else
                        {
                            RdfUtils.log
                                    .warn("getQueryTypesForQueryBundles: readerFormat NOT matched for returnedMIMEType="
                                            + nextResult.getReturnedMIMEType()
                                            + " using configured assumed response content type as fallback localSettings.getStringProperty(WebappConfig.ASSUMED_RESPONSE_CONTENT_TYPE)="
                                            + localSettings
                                                    .getStringProperty(WebappConfig.ASSUMED_RESPONSE_CONTENT_TYPE));
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
            } // end for(RdfFetcherQueryRunnableImpl nextResult : rdfResults)
        }
        catch(final org.openrdf.repository.RepositoryException re)
        {
            throw new QueryAllException("RepositoryException found", re);
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
    
    public static HttpUrlQueryRunnableImpl generateHttpUrlSparqlDeleteThread(final BaseQueryAllInterface rdfObject,
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
    
    public static HttpUrlQueryRunnableImpl generateHttpUrlSparqlInsertThread(final BaseQueryAllInterface rdfObject,
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
    
    public static HttpUrlQueryRunnableImpl generateHttpUrlSparqlThread(final String sparqlQuery,
            final String sparqlEndpointMethod, final String sparqlEndpointUrl, final String acceptHeader,
            final String expectedReturnFormat, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController)
    {
        return new HttpUrlQueryRunnableImpl(sparqlEndpointMethod, sparqlEndpointUrl, sparqlQuery, acceptHeader,
                localSettings, localBlacklistController);
    }
    
    /**
     * Returns a sorted collection of all the unique Statements from a repository using the default
     * Statement equals and hashCode which do not take context into account then sorted using the
     * given comparator to sort the results.
     * 
     * Note: This method is implemented using two collections, a set for uniqueness and a list for
     * order, so it will take twice the memory.
     * 
     * @param nextRepository
     * @param contexts
     *            An optional varargs array of contexts that should be exported.
     * @return
     * @throws OpenRDFException
     */
    public static Collection<Statement> getAllStatementsFromRepository(final Repository nextRepository,
            final Comparator<Statement> nextComparator, final Resource... contexts) throws OpenRDFException
    {
        RepositoryConnection con = null;
        
        try
        {
            // use HashSet to remove duplicates based on the context parameter
            Set<Statement> resultsSet = new HashSet<Statement>();
            
            con = nextRepository.getConnection();
            
            Iterations.addAll(con.getStatements((Resource)null, (URI)null, (Value)null, true, contexts), resultsSet);
            
            final List<Statement> results = new ArrayList<Statement>(resultsSet);
            
            // clear out the first set and null the reference to regain memory if needed at this
            // point
            resultsSet.clear();
            resultsSet = null;
            
            Collections.sort(results, nextComparator);
            
            return results;
            
        }
        catch(final OpenRDFException ordfe)
        {
            RdfUtils.log.error("getAllStatementsFromRepository: outer caught exception ", ordfe);
            
            throw ordfe;
        }
        finally
        {
            if(con != null)
            {
                try
                {
                    con.close();
                }
                catch(final RepositoryException rex)
                {
                    RdfUtils.log.error("Found repository exception while trying to close connection.");
                }
            }
        }
    }
    
    /**
     * Returns a collection of all of the statements in the repository including duplicates based on
     * Context, including sorting on the context position.
     * 
     * @param nextRepository
     * @param contexts
     *            An optional varargs array of contexts that should be exported.
     * @return
     * @throws OpenRDFException
     */
    public static Collection<Statement> getAllStatementsFromRepository(final Repository nextRepository,
            final Resource... contexts) throws OpenRDFException
    {
        final List<Statement> results = new ArrayList<Statement>();
        
        RepositoryConnection con = null;
        
        try
        {
            con = nextRepository.getConnection();
            
            Iterations.addAll(con.getStatements((Resource)null, (URI)null, (Value)null, true, contexts), results);
            
            Collections.sort(results, new StatementComparator());
        }
        catch(final OpenRDFException ordfe)
        {
            RdfUtils.log.error("getAllStatementsFromRepository: outer caught exception ", ordfe);
            
            throw ordfe;
        }
        finally
        {
            if(con != null)
            {
                try
                {
                    con.close();
                }
                catch(final RepositoryException rex)
                {
                    RdfUtils.log.error("Found repository exception while trying to close connection.", rex);
                }
            }
        }
        
        return results;
    }
    
    /**
     * @param nextValue
     * @return
     */
    public static boolean getBooleanFromValue(final Value nextValue) throws IllegalArgumentException
    {
        if(nextValue == null)
        {
            throw new QueryAllRuntimeException("Could not parse null as a boolean");
        }
        
        if(nextValue instanceof Literal)
        {
            return ((Literal)nextValue).booleanValue();
        }
        else if(nextValue.stringValue().toLowerCase().equals("true"))
        {
            return true;
        }
        else if(nextValue.stringValue().toLowerCase().equals("false"))
        {
            return false;
        }
        
        throw new QueryAllRuntimeException("Could not parse value as boolean");
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
        final Collection<URI> titleProperties = localSettings.getURIProperties(WebappConfig.TITLE_PROPERTIES);
        
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
        
        // TODO: use this method and convert it to a Calendar instance
        if(nextValue instanceof Literal)
        {
            final XMLGregorianCalendar calendarValue = ((Literal)nextValue).calendarValue();
        }
        
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
        final Collection<String> results = new ArrayList<String>();
        
        if(RdfUtils.DEBUG)
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
        // if(RdfUtils.DEBUG)
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
                    
                    if(RdfUtils.DEBUG)
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
        finally
        {
            con.close();
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
        final Collection<String> results = new ArrayList<String>();
        
        if(RdfUtils.DEBUG)
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
        // if(RdfUtils.DEBUG)
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
                    
                    if(RdfUtils.DEBUG)
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
        finally
        {
            con.close();
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
    public static float getFloatFromValue(final Value nextValue) throws NumberFormatException
    {
        float result = 0.0f;
        
        if(nextValue instanceof Literal)
        {
            result = ((Literal)nextValue).floatValue();
        }
        
        return result;
    }
    
    /**
     * @param nextValue
     * @return
     */
    public static int getIntegerFromValue(final Value nextValue) throws NumberFormatException
    {
        int result = 0;
        
        if(nextValue instanceof Literal)
        {
            result = ((Literal)nextValue).intValue();
        }
        
        return result;
    }
    
    /**
     * @param nextValue
     * @return
     */
    public static long getLongFromValue(final Value nextValue) throws NumberFormatException
    {
        long result = 0L;
        
        if(nextValue instanceof Literal)
        {
            result = ((Literal)nextValue).longValue();
        }
        
        return result;
    }
    
    public static Map<URI, NamespaceEntry> getNamespaceEntries(final Repository myRepository)
    {
        return RdfUtils.getNamespaceEntries(myRepository, SettingsFactory.CONFIG_API_VERSION);
    }
    
    public static Map<URI, NamespaceEntry> getNamespaceEntries(final Repository myRepository, final int configApiVersion)
    {
        if(configApiVersion > SettingsFactory.CONFIG_API_VERSION)
        {
            throw new IllegalArgumentException(
                    "This library cannot be used to parse objects using this config API version.");
        }
        
        final long start = System.currentTimeMillis();
        RepositoryConnection con = null;
        
        try
        {
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("getNamespaceEntries: started parsing namespace entrys");
            }
            
            // This is the base namespace entry URI, extensions or plugins must include this URI
            // alongside their customised type URIs
            final URI providerTypeUri = NamespaceEntrySchema.getNamespaceTypeUri();
            
            final Map<URI, NamespaceEntry> results = new ConcurrentHashMap<URI, NamespaceEntry>();
            
            con = myRepository.getConnection();
            
            final RepositoryResult<Statement> allDeclaredNamespaceEntrySubjects =
                    con.getStatements(null, RDF.TYPE, providerTypeUri, true);
            
            final Map<URI, Collection<NamespaceEntryEnum>> uriToNamespaceEntryEnums =
                    new HashMap<URI, Collection<NamespaceEntryEnum>>();
            
            // for(final Statement nextDeclaredNamespaceEntrySubject :
            // allDeclaredNamespaceEntrySubjects)
            while(allDeclaredNamespaceEntrySubjects.hasNext())
            {
                final Statement nextDeclaredNamespaceEntrySubject = allDeclaredNamespaceEntrySubjects.next();
                
                if(!(nextDeclaredNamespaceEntrySubject.getSubject() instanceof URI))
                {
                    RdfUtils.log.error("We do not support blank nodes as namespace entry identifiers");
                    continue;
                }
                
                final URI nextSubjectUri = (URI)nextDeclaredNamespaceEntrySubject.getSubject();
                
                final Collection<Value> nextNamespaceEntryValues =
                        RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(myRepository, RDF.TYPE,
                                (URI)nextDeclaredNamespaceEntrySubject.getSubject());
                final Set<URI> nextNamespaceEntryUris = new HashSet<URI>();
                for(final Value nextValue : nextNamespaceEntryValues)
                {
                    if(nextValue instanceof URI)
                    {
                        nextNamespaceEntryUris.add((URI)nextValue);
                    }
                    else if(RdfUtils.DEBUG)
                    {
                        RdfUtils.log.debug("Found non-URI as rdf:type nextSubjectUri={} rdf:type value={}",
                                nextSubjectUri.stringValue(), nextValue.stringValue());
                    }
                }
                
                final Collection<NamespaceEntryEnum> matchingNamespaceEntryEnums =
                        ServiceUtils.getNamespaceEntryEnumsByTypeUris(nextNamespaceEntryUris);
                
                if(RdfUtils.DEBUG)
                {
                    RdfUtils.log.debug("getNamespaceEntries: matchingNamespaceEntryEnums="
                            + matchingNamespaceEntryEnums);
                }
                
                if(!matchingNamespaceEntryEnums.isEmpty())
                {
                    uriToNamespaceEntryEnums.put(nextSubjectUri, matchingNamespaceEntryEnums);
                }
                else
                {
                    RdfUtils.log.warn("No namespace entry enums found for {} URIs were: {}",
                            nextSubjectUri.stringValue(), nextNamespaceEntryUris);
                }
            }
            
            for(final URI nextSubjectUri : uriToNamespaceEntryEnums.keySet())
            {
                final Collection<NamespaceEntryEnum> nextNamespaceEntryEnums =
                        uriToNamespaceEntryEnums.get(nextSubjectUri);
                
                for(final NamespaceEntryEnum nextNamespaceEntryEnum : nextNamespaceEntryEnums)
                {
                    try
                    {
                        results.put(
                                nextSubjectUri,
                                ServiceUtils.createNamespaceEntryParser(nextNamespaceEntryEnum).createObject(
                                        Iterations.asList(con.getStatements(nextSubjectUri, (URI)null, (Value)null,
                                                true)), nextSubjectUri, configApiVersion));
                    }
                    catch(final UnsupportedNamespaceEntryException e)
                    {
                        RdfUtils.log
                                .error("Could not create a namespace entry parser for the following URI nextSubjectUri="
                                        + nextSubjectUri + " type URI set =" + e.getNamespaceEntryCause().getTypeURIs());
                    }
                }
            }
            
            if(RdfUtils.INFO)
            {
                final long end = System.currentTimeMillis();
                RdfUtils.log.info(String.format("%s: timing=%10d", "getNamespaceEntries", (end - start)));
            }
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("getNamespaceEntries: finished parsing namespace entrys");
            }
            
            return results;
        }
        catch(final OpenRDFException e)
        {
            // handle exception
            RdfUtils.log.error("getNamespaceEntries:", e);
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
        return RdfUtils.getNormalisationRules(myRepository, SettingsFactory.CONFIG_API_VERSION);
    }
    
    public static Map<URI, NormalisationRule> getNormalisationRules(final Repository myRepository,
            final int configApiVersion)
    {
        if(configApiVersion > SettingsFactory.CONFIG_API_VERSION)
        {
            throw new IllegalArgumentException(
                    "This library cannot be used to parse objects using this config API version.");
        }
        
        final long start = System.currentTimeMillis();
        RepositoryConnection con = null;
        
        try
        {
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("getNormalisationRules: started parsing normalisation rules");
            }
            
            // This is the base normalisation rule URI, extensions or plugins must include this URI
            // alongside their customised type URIs
            final URI normalisationRuleUri = NormalisationRuleSchema.getNormalisationRuleTypeUri();
            
            final Map<URI, NormalisationRule> results = new ConcurrentHashMap<URI, NormalisationRule>();
            
            con = myRepository.getConnection();
            
            final RepositoryResult<Statement> allDeclaredNormalisationRuleSubjects =
                    con.getStatements(null, RDF.TYPE, normalisationRuleUri, true);
            
            final Map<URI, Collection<NormalisationRuleEnum>> uriToNormalisationRuleEnums =
                    new HashMap<URI, Collection<NormalisationRuleEnum>>();
            
            // for(final Statement nextDeclaredNormalisationRuleSubject :
            // allDeclaredNormalisationRuleSubjects)
            while(allDeclaredNormalisationRuleSubjects.hasNext())
            {
                final Statement nextDeclaredNormalisationRuleSubject = allDeclaredNormalisationRuleSubjects.next();
                
                if(!(nextDeclaredNormalisationRuleSubject.getSubject() instanceof URI))
                {
                    RdfUtils.log.error("We do not support blank nodes as normalisation rule identifiers");
                    continue;
                }
                
                final URI nextSubjectUri = (URI)nextDeclaredNormalisationRuleSubject.getSubject();
                
                final Collection<Value> nextNormalisationRuleValues =
                        RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(myRepository, RDF.TYPE,
                                (URI)nextDeclaredNormalisationRuleSubject.getSubject());
                final Set<URI> nextNormalisationRuleUris = new HashSet<URI>();
                for(final Value nextValue : nextNormalisationRuleValues)
                {
                    if(nextValue instanceof URI)
                    {
                        nextNormalisationRuleUris.add((URI)nextValue);
                    }
                    else if(RdfUtils.DEBUG)
                    {
                        RdfUtils.log.debug("Found non-URI as rdf:type nextSubjectUri={} rdf:type value={}",
                                nextSubjectUri.stringValue(), nextValue.stringValue());
                    }
                }
                
                final Collection<NormalisationRuleEnum> matchingNormalisationRuleEnums =
                        ServiceUtils.getNormalisationRuleEnumsByTypeUris(nextNormalisationRuleUris);
                
                if(RdfUtils.DEBUG)
                {
                    RdfUtils.log.debug("getNormalisationRules: matchingNormalisationRuleEnums="
                            + matchingNormalisationRuleEnums);
                }
                
                if(!matchingNormalisationRuleEnums.isEmpty())
                {
                    uriToNormalisationRuleEnums.put(nextSubjectUri, matchingNormalisationRuleEnums);
                }
                else
                {
                    RdfUtils.log.warn("No normalisation rule enums found for {} URIs were: {}",
                            nextSubjectUri.stringValue(), nextNormalisationRuleUris);
                }
            }
            
            for(final URI nextSubjectUri : uriToNormalisationRuleEnums.keySet())
            {
                final Collection<NormalisationRuleEnum> nextNormalisationRuleEnums =
                        uriToNormalisationRuleEnums.get(nextSubjectUri);
                
                for(final NormalisationRuleEnum nextNormalisationRuleEnum : nextNormalisationRuleEnums)
                {
                    try
                    {
                        results.put(
                                nextSubjectUri,
                                ServiceUtils.createNormalisationRuleParser(nextNormalisationRuleEnum).createObject(
                                        Iterations.asList(con.getStatements(nextSubjectUri, (URI)null, (Value)null,
                                                true)), nextSubjectUri, configApiVersion));
                    }
                    catch(final UnsupportedNormalisationRuleException e)
                    {
                        RdfUtils.log
                                .error("Could not create a namespace rule parser for the following URI nextSubjectUri="
                                        + nextSubjectUri + " type URI set =" + e.getRuleCause().getTypeURIs());
                    }
                }
            }
            
            if(RdfUtils.INFO)
            {
                final long end = System.currentTimeMillis();
                RdfUtils.log.info(String.format("%s: timing=%10d", "getNormalisationRules", (end - start)));
            }
            if(RdfUtils.DEBUG)
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
    
    /**
     * @param nextRepository
     * @param predicateUris
     * @return
     * @throws OpenRDFException
     */
    public static Collection<String> getObjectUrisFromRepositoryByPredicateUris(final Repository nextRepository,
            final Collection<String> predicateUris) throws OpenRDFException
    {
        final Collection<String> results = new ArrayList<String>();
        
        if(RdfUtils.DEBUG)
        {
            RdfUtils.log.debug("getObjectUrisFromRepositoryByPredicateUris: entering method");
            // RdfUtils.log.debug(nextRepository);
            // RdfUtils.log.debug(predicateUris);
        }
        
        final RepositoryConnection con = nextRepository.getConnection();
        
        try
        {
            final ValueFactory f = Constants.VALUE_FACTORY;
            
            for(final String nextInputPredicate : predicateUris)
            {
                if((nextInputPredicate == null) || nextInputPredicate.trim().equals(""))
                {
                    if(RdfUtils.DEBUG)
                    {
                        RdfUtils.log
                                .debug("getObjectUrisFromRepositoryByPredicateUris: nextInputPredicate was null or empty");
                    }
                }
                else
                {
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
                                
                                if(RdfUtils.DEBUG)
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
        return RdfUtils.getProfiles(myRepository, SettingsFactory.CONFIG_API_VERSION);
    }
    
    public static Map<URI, Profile> getProfiles(final Repository myRepository, final int configApiVersion)
    {
        if(configApiVersion > SettingsFactory.CONFIG_API_VERSION)
        {
            throw new IllegalArgumentException(
                    "This library cannot be used to parse objects using this config API version.");
        }
        
        final long start = System.currentTimeMillis();
        RepositoryConnection con = null;
        
        try
        {
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("getProfiles: started parsing profiles");
            }
            
            // This is the base profile URI, extensions or plugins must include this URI
            // alongside their customised type URIs
            final URI providerTypeUri = ProfileSchema.getProfileTypeUri();
            
            final Map<URI, Profile> results = new ConcurrentHashMap<URI, Profile>();
            
            con = myRepository.getConnection();
            
            final RepositoryResult<Statement> allDeclaredProfileSubjects =
                    con.getStatements(null, RDF.TYPE, providerTypeUri, true);
            
            final Map<URI, Collection<ProfileEnum>> uriToProfileEnums = new HashMap<URI, Collection<ProfileEnum>>();
            
            // for(final Statement nextDeclaredProfileSubject : allDeclaredProfileSubjects)
            while(allDeclaredProfileSubjects.hasNext())
            {
                final Statement nextDeclaredProfileSubject = allDeclaredProfileSubjects.next();
                
                if(!(nextDeclaredProfileSubject.getSubject() instanceof URI))
                {
                    RdfUtils.log.error("We do not support blank nodes as profile identifiers");
                }
                else
                {
                    final URI nextSubjectUri = (URI)nextDeclaredProfileSubject.getSubject();
                    
                    final Collection<Value> nextProfileValues =
                            RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(myRepository, RDF.TYPE,
                                    (URI)nextDeclaredProfileSubject.getSubject());
                    final Set<URI> nextProfileUris = new HashSet<URI>();
                    for(final Value nextValue : nextProfileValues)
                    {
                        if(nextValue instanceof URI)
                        {
                            nextProfileUris.add((URI)nextValue);
                        }
                        else if(RdfUtils.DEBUG)
                        {
                            RdfUtils.log.debug("Found non-URI as rdf:type nextSubjectUri={} rdf:type value={}",
                                    nextSubjectUri.stringValue(), nextValue.stringValue());
                        }
                    }
                    
                    final Collection<ProfileEnum> matchingProfileEnums =
                            ServiceUtils.getProfileEnumsByTypeUris(nextProfileUris);
                    
                    if(RdfUtils.DEBUG)
                    {
                        RdfUtils.log.debug("getProfiles: matchingProfileEnums=" + matchingProfileEnums);
                    }
                    
                    if(!matchingProfileEnums.isEmpty())
                    {
                        uriToProfileEnums.put(nextSubjectUri, matchingProfileEnums);
                    }
                    else
                    {
                        RdfUtils.log.warn("No profile enums found for {} URIs were: {}", nextSubjectUri.stringValue(),
                                nextProfileUris);
                    }
                }
            }
            
            for(final URI nextSubjectUri : uriToProfileEnums.keySet())
            {
                final Collection<ProfileEnum> nextProfileEnums = uriToProfileEnums.get(nextSubjectUri);
                
                for(final ProfileEnum nextProfileEnum : nextProfileEnums)
                {
                    try
                    {
                        results.put(
                                nextSubjectUri,
                                ServiceUtils.createProfileParser(nextProfileEnum).createObject(
                                        Iterations.asList(con.getStatements(nextSubjectUri, (URI)null, (Value)null,
                                                true)), nextSubjectUri, configApiVersion));
                    }
                    catch(final UnsupportedProfileException e)
                    {
                        RdfUtils.log.error("Could not create a profile parser for the following URI nextSubjectUri="
                                + nextSubjectUri + " type URI set =" + e.getProfileCause().getTypeURIs());
                    }
                }
            }
            
            if(RdfUtils.INFO)
            {
                final long end = System.currentTimeMillis();
                RdfUtils.log.info(String.format("%s: timing=%10d", "getProfiles", (end - start)));
            }
            if(RdfUtils.DEBUG)
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
        return RdfUtils.getProjects(myRepository, SettingsFactory.CONFIG_API_VERSION);
    }
    
    public static Map<URI, Project> getProjects(final Repository myRepository, final int configApiVersion)
    {
        if(configApiVersion > SettingsFactory.CONFIG_API_VERSION)
        {
            throw new IllegalArgumentException(
                    "This library cannot be used to parse objects using this config API version.");
        }
        
        final long start = System.currentTimeMillis();
        RepositoryConnection con = null;
        
        try
        {
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("getProjects: started parsing projects");
            }
            
            // This is the base project URI, extensions or plugins must include this URI
            // alongside their customised type URIs
            final URI providerTypeUri = ProjectSchema.getProjectTypeUri();
            
            final Map<URI, Project> results = new ConcurrentHashMap<URI, Project>();
            
            con = myRepository.getConnection();
            
            final RepositoryResult<Statement> allDeclaredProjectSubjects =
                    con.getStatements(null, RDF.TYPE, providerTypeUri, true);
            
            final Map<URI, Collection<ProjectEnum>> uriToProjectEnums = new HashMap<URI, Collection<ProjectEnum>>();
            
            // for(final Statement nextDeclaredProjectSubject : allDeclaredProjectSubjects)
            while(allDeclaredProjectSubjects.hasNext())
            {
                final Statement nextDeclaredProjectSubject = allDeclaredProjectSubjects.next();
                
                if(!(nextDeclaredProjectSubject.getSubject() instanceof URI))
                {
                    RdfUtils.log.error("We do not support blank nodes as project identifiers");
                }
                else
                {
                    final URI nextSubjectUri = (URI)nextDeclaredProjectSubject.getSubject();
                    
                    final Collection<Value> nextProjectValues =
                            RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(myRepository, RDF.TYPE,
                                    (URI)nextDeclaredProjectSubject.getSubject());
                    final Set<URI> nextProjectUris = new HashSet<URI>();
                    
                    // Silently filter out any blank nodes or literals that happen to be used as
                    // rdf:type objects
                    for(final Value nextValue : nextProjectValues)
                    {
                        if(nextValue instanceof URI)
                        {
                            nextProjectUris.add((URI)nextValue);
                        }
                        else if(RdfUtils.DEBUG)
                        {
                            RdfUtils.log.debug("Found non-URI as rdf:type nextSubjectUri={} rdf:type value={}",
                                    nextSubjectUri.stringValue(), nextValue.stringValue());
                        }
                    }
                    
                    final Collection<ProjectEnum> matchingProjectEnums =
                            ServiceUtils.getProjectEnumsByTypeUris(nextProjectUris);
                    
                    if(RdfUtils.DEBUG)
                    {
                        RdfUtils.log.debug("getProjects: matchingProjectEnums=" + matchingProjectEnums);
                    }
                    
                    if(!matchingProjectEnums.isEmpty())
                    {
                        uriToProjectEnums.put(nextSubjectUri, matchingProjectEnums);
                    }
                    else
                    {
                        RdfUtils.log.warn("No project enums found for {} URIs were: {}", nextSubjectUri.stringValue(),
                                nextProjectUris);
                    }
                }
            }
            
            for(final URI nextSubjectUri : uriToProjectEnums.keySet())
            {
                final Collection<ProjectEnum> nextProjectEnums = uriToProjectEnums.get(nextSubjectUri);
                
                for(final ProjectEnum nextProjectEnum : nextProjectEnums)
                {
                    try
                    {
                        results.put(
                                nextSubjectUri,
                                ServiceUtils.createProjectParser(nextProjectEnum).createObject(
                                        Iterations.asList(con.getStatements(nextSubjectUri, (URI)null, (Value)null,
                                                true)), nextSubjectUri, configApiVersion));
                    }
                    catch(final UnsupportedProjectException e)
                    {
                        RdfUtils.log.error("Could not create a project parser for the following URI nextSubjectUri="
                                + nextSubjectUri + " type URI set =" + e.getProjectCause().getTypeURIs());
                    }
                }
            }
            
            if(RdfUtils.INFO)
            {
                final long end = System.currentTimeMillis();
                RdfUtils.log.info(String.format("%s: timing=%10d", "getProjects", (end - start)));
            }
            if(RdfUtils.DEBUG)
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
        return RdfUtils.getProviders(myRepository, SettingsFactory.CONFIG_API_VERSION);
    }
    
    public static Map<URI, Provider> getProviders(final Repository myRepository, final int configApiVersion)
    {
        if(configApiVersion > SettingsFactory.CONFIG_API_VERSION)
        {
            throw new IllegalArgumentException(
                    "This library cannot be used to parse objects using this config API version.");
        }
        
        final long start = System.currentTimeMillis();
        RepositoryConnection con = null;
        
        try
        {
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("getProviders: started parsing providers");
            }
            
            // This is the base provider URI, extensions or plugins must include this URI
            // alongside
            // their customised type URIs
            final URI providerUri = ProviderSchema.getProviderTypeUri();
            
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("getProviders: providerUri=" + providerUri.stringValue());
            }
            
            final Map<URI, Provider> results = new ConcurrentHashMap<URI, Provider>();
            
            con = myRepository.getConnection();
            
            final RepositoryResult<Statement> allDeclaredProviderSubjects =
                    con.getStatements(null, RDF.TYPE, providerUri, true);
            
            final Map<URI, Collection<ProviderEnum>> uriToProviderEnums = new HashMap<URI, Collection<ProviderEnum>>();
            
            // for(final Statement nextDeclaredProviderSubject : allDeclaredProviderSubjects)
            while(allDeclaredProviderSubjects.hasNext())
            {
                final Statement nextDeclaredProviderSubject = allDeclaredProviderSubjects.next();
                
                if(RdfUtils.DEBUG)
                {
                    RdfUtils.log.debug("getProviders: nextDeclaredProviderSubject.getSubject()="
                            + nextDeclaredProviderSubject.getSubject().stringValue());
                }
                
                if(!(nextDeclaredProviderSubject.getSubject() instanceof URI))
                {
                    RdfUtils.log.error("We do not support blank nodes as provider identifiers");
                }
                else
                {
                    final URI nextSubjectUri = (URI)nextDeclaredProviderSubject.getSubject();
                    
                    final Collection<Value> nextProviderValues =
                            RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(myRepository, RDF.TYPE,
                                    (URI)nextDeclaredProviderSubject.getSubject());
                    final Set<URI> nextProviderUris = new HashSet<URI>();
                    for(final Value nextValue : nextProviderValues)
                    {
                        if(nextValue instanceof URI)
                        {
                            nextProviderUris.add((URI)nextValue);
                        }
                        else if(RdfUtils.DEBUG)
                        {
                            RdfUtils.log.debug("Found non-URI as rdf:type nextSubjectUri={} rdf:type value={}",
                                    nextSubjectUri.stringValue(), nextValue.stringValue());
                        }
                    }
                    
                    final Collection<ProviderEnum> matchingProviderEnums =
                            ServiceUtils.getProviderEnumsByTypeUris(nextProviderUris);
                    
                    if(RdfUtils.DEBUG)
                    {
                        RdfUtils.log.debug("getProviders: matchingProviderEnums=" + matchingProviderEnums);
                    }
                    
                    if(!matchingProviderEnums.isEmpty())
                    {
                        uriToProviderEnums.put(nextSubjectUri, matchingProviderEnums);
                    }
                    else
                    {
                        RdfUtils.log.warn("No provider enums found for {} URIs were: {}", nextSubjectUri.stringValue(),
                                nextProviderUris);
                    }
                }
            }
            
            for(final URI nextSubjectUri : uriToProviderEnums.keySet())
            {
                final Collection<ProviderEnum> nextProviderEnums = uriToProviderEnums.get(nextSubjectUri);
                
                for(final ProviderEnum nextProviderEnum : nextProviderEnums)
                {
                    try
                    {
                        results.put(
                                nextSubjectUri,
                                ServiceUtils.createProviderParser(nextProviderEnum).createObject(
                                        Iterations.asList(con.getStatements(nextSubjectUri, (URI)null, (Value)null,
                                                true)), nextSubjectUri, configApiVersion));
                    }
                    catch(final UnsupportedProviderException e)
                    {
                        RdfUtils.log.error("Could not create a provider parser for the following URI nextSubjectUri="
                                + nextSubjectUri + " type URI set =" + e.getProviderCause().getTypeURIs());
                    }
                }
            }
            
            if(RdfUtils.INFO)
            {
                final long end = System.currentTimeMillis();
                RdfUtils.log.info(String.format("%s: timing=%10d", "getProviders", (end - start)));
            }
            if(RdfUtils.DEBUG)
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
        return RdfUtils.getQueryTypes(myRepository, SettingsFactory.CONFIG_API_VERSION);
    }
    
    public static Map<URI, QueryType> getQueryTypes(final Repository myRepository, final int configApiVersion)
    {
        if(configApiVersion > SettingsFactory.CONFIG_API_VERSION)
        {
            throw new IllegalArgumentException(
                    "This library cannot be used to parse objects using this config API version.");
        }
        
        final long start = System.currentTimeMillis();
        RepositoryConnection con = null;
        
        try
        {
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("getQueryTypes: started parsing query types");
            }
            
            // This is the base query type URI, extensions or plugins must include this URI
            // alongside
            // their customised type URIs
            final URI queryTypeUri = QueryTypeSchema.getQueryTypeUri();
            
            final Map<URI, QueryType> results = new ConcurrentHashMap<URI, QueryType>();
            
            con = myRepository.getConnection();
            
            final RepositoryResult<Statement> allDeclaredQueryTypeSubjects =
                    con.getStatements(null, RDF.TYPE, queryTypeUri, true);
            
            final Map<URI, Collection<QueryTypeEnum>> uriToQueryTypeEnums =
                    new HashMap<URI, Collection<QueryTypeEnum>>();
            
            // for(final Statement nextDeclaredQueryTypeSubject : allDeclaredQueryTypeSubjects)
            while(allDeclaredQueryTypeSubjects.hasNext())
            {
                final Statement nextDeclaredQueryTypeSubject = allDeclaredQueryTypeSubjects.next();
                
                if(!(nextDeclaredQueryTypeSubject.getSubject() instanceof URI))
                {
                    RdfUtils.log.error("We do not support blank nodes as query type identifiers");
                }
                else
                {
                    final URI nextSubjectUri = (URI)nextDeclaredQueryTypeSubject.getSubject();
                    
                    final Collection<Value> nextQueryTypeValues =
                            RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(myRepository, RDF.TYPE,
                                    (URI)nextDeclaredQueryTypeSubject.getSubject());
                    final Set<URI> nextQueryTypeUris = new HashSet<URI>();
                    for(final Value nextValue : nextQueryTypeValues)
                    {
                        if(nextValue instanceof URI)
                        {
                            nextQueryTypeUris.add((URI)nextValue);
                        }
                        else if(RdfUtils.DEBUG)
                        {
                            RdfUtils.log.debug("Found non-URI as rdf:type nextSubjectUri={} rdf:type value={}",
                                    nextSubjectUri.stringValue(), nextValue.stringValue());
                        }
                    }
                    
                    final Collection<QueryTypeEnum> matchingQueryTypeEnums =
                            ServiceUtils.getQueryTypeEnumsByTypeUris(nextQueryTypeUris);
                    
                    if(RdfUtils.DEBUG)
                    {
                        RdfUtils.log.debug("getQueryTypes: matchingQueryTypeEnums=" + matchingQueryTypeEnums);
                    }
                    
                    if(!matchingQueryTypeEnums.isEmpty())
                    {
                        uriToQueryTypeEnums.put(nextSubjectUri, matchingQueryTypeEnums);
                    }
                    else
                    {
                        RdfUtils.log.warn("No query type enums found for {} URIs were: {}",
                                nextSubjectUri.stringValue(), nextQueryTypeUris);
                    }
                }
            }
            
            for(final URI nextSubjectUri : uriToQueryTypeEnums.keySet())
            {
                final Collection<QueryTypeEnum> nextQueryTypeEnums = uriToQueryTypeEnums.get(nextSubjectUri);
                
                for(final QueryTypeEnum nextQueryTypeEnum : nextQueryTypeEnums)
                {
                    try
                    {
                        results.put(
                                nextSubjectUri,
                                ServiceUtils.createQueryTypeParser(nextQueryTypeEnum).createObject(
                                        Iterations.asList(con.getStatements(nextSubjectUri, (URI)null, (Value)null,
                                                true)), nextSubjectUri, configApiVersion));
                    }
                    catch(final UnsupportedQueryTypeException e)
                    {
                        RdfUtils.log.error("Could not create a query type parser for the following URI nextSubjectUri="
                                + nextSubjectUri + " type URI set =" + e.getQueryTypeCause().getTypeURIs());
                    }
                }
            }
            
            if(RdfUtils.INFO)
            {
                final long end = System.currentTimeMillis();
                RdfUtils.log.info(String.format("%s: timing=%10d", "getQueryTypes", (end - start)));
            }
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("getQueryTypes: finished parsing query types");
            }
            
            return results;
        }
        catch(final OpenRDFException e)
        {
            // log exception
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
    
    /**
     * Returns rule tests that were parsed based on the current config API version as defined in
     * SettingsFactory.CONFIG_API_VERSION.
     * 
     * @param myRepository
     * @return
     */
    public static Map<URI, RuleTest> getRuleTests(final Repository myRepository)
    {
        return RdfUtils.getRuleTests(myRepository, SettingsFactory.CONFIG_API_VERSION);
    }
    
    /**
     * Returns rule tests that were parsed based on the given config API version.
     * 
     * @param myRepository
     * @param configApiVersion
     * @return
     */
    public static Map<URI, RuleTest> getRuleTests(final Repository myRepository, final int configApiVersion)
    {
        if(configApiVersion > SettingsFactory.CONFIG_API_VERSION)
        {
            throw new IllegalArgumentException(
                    "This library cannot be used to parse objects using this config API version.");
        }
        
        final long start = System.currentTimeMillis();
        RepositoryConnection con = null;
        
        try
        {
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("getRuleTests: started parsing ruleTests");
            }
            
            // This is the base ruleTest URI, extensions or plugins must include this URI
            // alongside their customised type URIs
            final URI providerTypeUri = RuleTestSchema.getRuletestTypeUri();
            
            final Map<URI, RuleTest> results = new ConcurrentHashMap<URI, RuleTest>();
            
            con = myRepository.getConnection();
            
            final RepositoryResult<Statement> allDeclaredRuleTestSubjects =
                    con.getStatements(null, RDF.TYPE, providerTypeUri, true);
            
            final Map<URI, Collection<RuleTestEnum>> uriToRuleTestEnums = new HashMap<URI, Collection<RuleTestEnum>>();
            
            // for(final Statement nextDeclaredRuleTestSubject : allDeclaredRuleTestSubjects)
            while(allDeclaredRuleTestSubjects.hasNext())
            {
                final Statement nextDeclaredRuleTestSubject = allDeclaredRuleTestSubjects.next();
                
                if(!(nextDeclaredRuleTestSubject.getSubject() instanceof URI))
                {
                    RdfUtils.log.error("We do not support blank nodes as rule test identifiers");
                    continue;
                }
                
                final URI nextSubjectUri = (URI)nextDeclaredRuleTestSubject.getSubject();
                
                final Collection<Value> nextRuleTestValues =
                        RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(myRepository, RDF.TYPE,
                                (URI)nextDeclaredRuleTestSubject.getSubject());
                final Set<URI> nextRuleTestUris = new HashSet<URI>();
                for(final Value nextValue : nextRuleTestValues)
                {
                    if(nextValue instanceof URI)
                    {
                        nextRuleTestUris.add((URI)nextValue);
                    }
                    else if(RdfUtils.DEBUG)
                    {
                        RdfUtils.log.debug("Found non-URI as rdf:type nextSubjectUri={} rdf:type value={}",
                                nextSubjectUri.stringValue(), nextValue.stringValue());
                    }
                }
                
                final Collection<RuleTestEnum> matchingRuleTestEnums =
                        ServiceUtils.getRuleTestEnumsByTypeUris(nextRuleTestUris);
                
                if(RdfUtils.DEBUG)
                {
                    RdfUtils.log.debug("getRuleTests: matchingRuleTestEnums=" + matchingRuleTestEnums);
                }
                
                if(!matchingRuleTestEnums.isEmpty())
                {
                    uriToRuleTestEnums.put(nextSubjectUri, matchingRuleTestEnums);
                }
                else
                {
                    RdfUtils.log.warn("No rule test enums found for {} URIs were: {}", nextSubjectUri.stringValue(),
                            nextRuleTestUris);
                }
            }
            
            for(final URI nextSubjectUri : uriToRuleTestEnums.keySet())
            {
                final Collection<RuleTestEnum> nextRuleTestEnums = uriToRuleTestEnums.get(nextSubjectUri);
                
                for(final RuleTestEnum nextRuleTestEnum : nextRuleTestEnums)
                {
                    try
                    {
                        results.put(
                                nextSubjectUri,
                                ServiceUtils.createRuleTestParser(nextRuleTestEnum).createObject(
                                        Iterations.asList(con.getStatements(nextSubjectUri, (URI)null, (Value)null,
                                                true)), nextSubjectUri, configApiVersion));
                    }
                    catch(final UnsupportedRuleTestException e)
                    {
                        RdfUtils.log.error("Could not create a rule test parser for the following URI nextSubjectUri="
                                + nextSubjectUri + " type URI set =" + e.getRuleTestCause().getTypeURIs());
                    }
                }
            }
            
            if(RdfUtils.INFO)
            {
                final long end = System.currentTimeMillis();
                RdfUtils.log.info(String.format("%s: timing=%10d", "getRuleTests", (end - start)));
            }
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("getRuleTests: finished parsing rule tests");
            }
            
            return results;
        }
        catch(final OpenRDFException e)
        {
            // log exception
            RdfUtils.log.error("getRuleTests:", e);
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
        final boolean rdfOkay = rdfObject.toRdf(myRepository, SettingsFactory.CONFIG_API_VERSION, rdfObject.getKey());
        
        if(!rdfOkay && isInsert)
        {
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("getSparulQueryForObject: could not convert to RDF");
            }
            
            return "";
        }
        
        // text/plain is the accepted MIME format for NTriples because they were too lazy to define
        // one... go figure
        final RDFFormat writerFormat = RDFFormat.NTRIPLES;
        
        final StringWriter insertTriples = new StringWriter();
        
        if(isInsert)
        {
            RdfUtils.toWriter(myRepository, insertTriples, writerFormat);
            
            RdfUtils.log.debug("getSparulQueryForObject: insertTriples.toString()=" + insertTriples.toString());
        }
        else if(RdfUtils.DEBUG)
        {
            RdfUtils.log.debug("getSparulQueryForObject: isInsert was false");
        }
        
        // NOTE: this looks messy because it is.
        // SPARUL doesn't play nicely if you don't know whether the delete will delete any triples,
        // and empty blocks are mandatory for the MODIFY statement if they are not applicable
        // The define sql:log-enable is a Virtuoso hack to enable SPARUL to work with more than one
        // thread at once
        // FIXME: HACK: Specific to Virtuoso!
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
        
        if(RdfUtils.DEBUG)
        {
            RdfUtils.log.debug("getInsertQueryForObject: sparqlInsertQuery=" + sparqlInsertQuery);
        }
        
        return sparqlInsertQuery;
    }
    
    public static Collection<Statement> getStatementsFromRepositoryByPredicateUris(final Repository nextRepository,
            final Collection<URI> predicateUris) throws OpenRDFException
    {
        final Collection<Statement> results = new ArrayList<Statement>();
        
        if(RdfUtils.DEBUG)
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
                    Iterations.addAll(con.getStatements((URI)null, nextInputPredicateUri, (Value)null, true), results);
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
        final Collection<Statement> results = new ArrayList<Statement>();
        if(RdfUtils.DEBUG)
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
                    Iterations.addAll(con.getStatements(subjectUri, nextInputPredicateUri, (Value)null, true), results);
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
        return RdfUtils.getStatementsFromRepositoryByPredicateUrisAndSubject(nextRepository,
                Arrays.asList(predicateUri), subjectUri);
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
    
    public static Value getValueFromDateTime(final Date nextDate) throws DatatypeConfigurationException
    {
        final GregorianCalendar gregCal = new GregorianCalendar();
        gregCal.setTime(nextDate);
        XMLGregorianCalendar XMLGregCal;
        XMLGregCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal).normalize();
        final Value valueTyped = Constants.VALUE_FACTORY.createLiteral(XMLGregCal);
        
        return valueTyped;
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
        if(predicateUris.isEmpty())
        {
            return Collections.emptyList();
        }
        
        final Collection<Value> results = new HashSet<Value>();
        
        if(RdfUtils.TRACE)
        {
            RdfUtils.log.trace("getValuesFromRepositoryByPredicateUrisAndSubject: entering method");
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
                    
                    if(RdfUtils.TRACE)
                    {
                        RdfUtils.log.trace("queryString=" + queryString);
                    }
                    
                    try
                    {
                        while(queryResult.hasNext())
                        {
                            final Statement nextStatement = queryResult.next();
                            
                            if(RdfUtils.TRACE)
                            {
                                RdfUtils.log.trace("getValuesFromRepositoryByPredicateUrisAndSubject: nextStatement="
                                        + nextStatement);
                            }
                            
                            results.add(nextStatement.getObject());
                            
                            // if(RdfUtils.DEBUG)
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
        return RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(nextRepository, Arrays.asList(predicateUri),
                subjectUri);
    }
    
    public static RDFFormat getWriterFormat(final String requestedContentType)
    {
        // TODO: Make this extensible
        if(requestedContentType.equals(Constants.TEXT_HTML))
        {
            return null;
        }
        
        return Rio.getWriterFormatForMIMEType(requestedContentType, RDFFormat.RDFXML);
    }
    
    public static void insertResultIntoRepository(final RdfFetcherQueryRunnable nextResult,
            final Repository myRepository, final String defaultAssumedResponseContentType,
            final String defaultHostAddress) throws RepositoryException, java.io.IOException
    {
        if(RdfUtils.TRACE)
        {
            RdfUtils.log.trace("insertResultIntoRepository: nextResult.toString()=" + nextResult.toString());
        }
        
        RepositoryConnection myRepositoryConnection = null;
        
        try
        {
            myRepositoryConnection = myRepository.getConnection();
            
            RDFFormat nextReaderFormat = RDFFormat.forMIMEType(nextResult.getReturnedMIMEType());
            
            if(RdfUtils.TRACE)
            {
                RdfUtils.log.trace("insertResultIntoRepository: nextReaderFormat for returnedContentType="
                        + nextResult.getReturnedContentType() + " nextReaderFormat=" + nextReaderFormat);
            }
            
            // TODO: Integrate the Any23 methods here if a fallback is needed
            if(nextReaderFormat == null)
            {
                String assumedContentType = null;
                
                if(nextResult.getOriginalQueryBundle() != null
                        && nextResult.getOriginalQueryBundle().getProvider() != null)
                {
                    assumedContentType = nextResult.getOriginalQueryBundle().getProvider().getAssumedContentType();
                }
                
                if(assumedContentType != null && assumedContentType.trim().length() > 0)
                {
                    nextReaderFormat = Rio.getParserFormatForMIMEType(assumedContentType);
                }
                
                // HACK: Do not try to parse text/html, as it results in meaningless triples that
                // are confusing
                if(nextReaderFormat == null && !Constants.TEXT_HTML.equals(nextResult.getReturnedMIMEType()))
                {
                    nextReaderFormat = Rio.getParserFormatForMIMEType(defaultAssumedResponseContentType);
                }
                
                if(nextReaderFormat == null)
                {
                    RdfUtils.log
                            .error("insertResultIntoRepository: Not attempting to parse result because assumedResponseContentType isn't supported by Rio and the returned content type wasn't either nextResult.returnedMIMEType="
                                    + nextResult.getReturnedMIMEType()
                                    + " nextResult.assumedContentType="
                                    + assumedContentType
                                    + " defaultAssumedResponseContentType="
                                    + defaultAssumedResponseContentType);
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
            else if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("insertResultIntoRepository: readerFormat matched for returnedMIMEType="
                        + nextResult.getReturnedMIMEType());
            }
            
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("insertResultIntoRepository: nextResult.normalisedResult.length()="
                        + nextResult.getNormalisedResult().length());
                
                if(RdfUtils.TRACE)
                {
                    RdfUtils.log.trace("insertResultIntoRepository: nextResult.normalisedResult="
                            + nextResult.getNormalisedResult());
                }
            }
            
            if(nextReaderFormat != null && nextResult.getNormalisedResult().length() > 0)
            {
                if(nextResult.getOriginalQueryBundle() != null
                        && nextResult.getOriginalQueryBundle().getProvider() != null)
                {
                    myRepositoryConnection.add(new java.io.StringReader(nextResult.getNormalisedResult()),
                            defaultHostAddress, nextReaderFormat, nextResult.getOriginalQueryBundle().getProvider()
                                    .getKey());
                }
                else
                {
                    myRepositoryConnection.add(new java.io.StringReader(nextResult.getNormalisedResult()),
                            defaultHostAddress, nextReaderFormat);
                }
                myRepositoryConnection.commit();
            }
            else if(RdfUtils.DEBUG)
            {
                // Hiding this message in production, in debugging it will show up as WARN in error
                // logs
                RdfUtils.log
                        .warn("Not adding anything for next result as the result was empty or the format was not understood");
            }
            
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log.debug("insertResultIntoRepository: myRepositoryConnection.size()="
                        + myRepositoryConnection.size());
            }
        }
        catch(final org.openrdf.rio.RDFParseException rdfpe)
        {
            RdfUtils.log.error("insertResultIntoRepository: RDFParseException result: nextResult.actualendpointUrl="
                    + nextResult.getActualEndpointUrl() + " message=" + rdfpe.getMessage());
            
            if(RdfUtils.TRACE)
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
    
    public static void insertResultsIntoRepository(final Collection<RdfFetcherQueryRunnableImpl> results,
            final Repository myRepository, final QueryAllConfiguration localSettings) throws RepositoryException,
        java.io.IOException
    {
        for(final RdfFetcherQueryRunnable nextResult : results)
        {
            RdfUtils.insertResultIntoRepository(nextResult, myRepository,
                    localSettings.getStringProperty(WebappConfig.ASSUMED_RESPONSE_CONTENT_TYPE),
                    localSettings.getDefaultHostAddress());
        }
    }
    
    public static Repository removeStatementsFromRepository(final Repository myRepository,
            final List<String> sparqlConstructQueries)
    {
        try
        {
            if(RdfUtils.DEBUG)
            {
                RdfUtils.log
                        .debug("SparqlNormalisationRuleImpl: removing statements according to sparqlConstructQueryTarget="
                                + sparqlConstructQueries);
            }
            
            final RepositoryConnection removeConnection = myRepository.getConnection();
            
            try
            {
                for(final String nextConstructQuery : sparqlConstructQueries)
                {
                    try
                    {
                        final GraphQueryResult graphResult =
                                removeConnection.prepareGraphQuery(QueryLanguage.SPARQL, nextConstructQuery).evaluate();
                        
                        int deletedStatements = 0;
                        
                        while(graphResult.hasNext())
                        {
                            final Statement nextStatement = graphResult.next();
                            
                            if(RdfUtils.TRACE)
                            {
                                RdfUtils.log.trace("removing statement: " + nextStatement);
                            }
                            
                            removeConnection.remove(nextStatement);
                            deletedStatements++;
                        }
                        
                        removeConnection.commit();
                        if(RdfUtils.DEBUG)
                        {
                            RdfUtils.log
                                    .debug("SparqlNormalisationRuleImpl: removed " + deletedStatements + " results");
                        }
                        
                    }
                    catch(final Exception ex)
                    {
                        RdfUtils.log.error("SparqlNormalisationRuleImpl: exception removing statements", ex);
                    }
                }
            }
            finally
            {
                removeConnection.close();
            }
        }
        catch(final org.openrdf.repository.RepositoryException rex)
        {
            RdfUtils.log.error("SparqlNormalisationRuleImpl: RepositoryException exception adding statements", rex);
        }
        
        return myRepository;
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
        final Collection<RdfFetcherQueryRunnableImpl> retrievalThreads = new ArrayList<RdfFetcherQueryRunnableImpl>();
        
        for(final String nextLocation : retrievalUrls)
        {
            final RdfFetcherQueryRunnableImpl nextThread =
                    new RdfFetcherUriQueryRunnableImpl(nextLocation, "", "", defaultResultFormat, localSettings,
                            localBlacklistController, new QueryBundle());
            
            retrievalThreads.add(nextThread);
        }
        
        for(final RdfFetcherQueryRunnableImpl nextThread : retrievalThreads)
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
            for(final RdfFetcherQueryRunnableImpl nextThread : retrievalThreads)
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
            RdfUtils.log.error("Repository exception: ", e);
        }
        catch(final IOException e)
        {
            RdfUtils.log.error("IO exception: ", e);
        }
        
    }
    
    public static void retrieveUrls(final String retrievalUrl, final String defaultResultFormat,
            final Repository myRepository, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController) throws RepositoryException, java.io.IOException,
        InterruptedException
    {
        RdfUtils.retrieveUrls(Collections.singletonList(retrievalUrl), defaultResultFormat, myRepository,
                localSettings, localBlacklistController, true);
    }
    
    public static Collection<Statement> retrieveUrlsToStatements(final Collection<String> retrievalUrls,
            final String defaultResultFormat, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController) throws InterruptedException
    {
        try
        {
            final Repository resultsRepository = new SailRepository(new MemoryStore());
            resultsRepository.initialize();
            
            RdfUtils.retrieveUrls(retrievalUrls, defaultResultFormat, resultsRepository, localSettings,
                    localBlacklistController, true);
            
            return RdfUtils.getAllStatementsFromRepository(resultsRepository);
        }
        catch(final OpenRDFException e)
        {
            RdfUtils.log.error("retrieveUrlsToStatements: caught OpenRDFException", e);
        }
        
        return Collections.emptyList();
    }
    
    /**
     * @param nextRepository
     * @param outputStream
     * @param format
     */
    public static void toOutputStream(final Repository nextRepository, final java.io.OutputStream outputStream,
            final RDFFormat format, final Resource... contexts)
    {
        RdfUtils.toWriter(nextRepository, new OutputStreamWriter(outputStream, Charset.forName("UTF-8")), format,
                contexts);
    }
    
    /**
     * @param nextRepository
     * @param outputStream
     */
    public static void toOutputStream(final Repository nextRepository, final java.io.OutputStream outputStream,
            final Resource... contexts)
    {
        RdfUtils.toOutputStream(nextRepository, outputStream, RDFFormat.RDFXML, contexts);
    }
    
    /**
     * @param nextConnection
     * @return
     */
    public static String toString(final Repository nextRepository, final Resource... contexts)
    {
        final StringWriter stBuff = new StringWriter();
        
        RdfUtils.toWriter(nextRepository, stBuff, RDFFormat.RDFXML, contexts);
        
        return stBuff.toString();
    }
    
    /**
     * Writes the contents of the repository, with optional contexts restrictions, to the given
     * writer.
     * 
     * NOTE: This method logs, but does not through any exceptions.
     * 
     * @param nextRepository
     *            The repository that contains the data to be exported.
     * @param nextWriter
     *            The writer to write the results to.
     * @param format
     *            The format to write the contents of the repository using
     * @param contexts
     *            An optional varargs set of Resources identifying contexts in the repository that
     *            are to be exported
     */
    public static void toWriter(final Repository nextRepository, final java.io.Writer nextWriter,
            final RDFFormat format, final Resource... contexts)
    {
        RepositoryConnection nextConnection = null;
        
        try
        {
            nextConnection = nextRepository.getConnection();
            
            nextConnection.export(Rio.createWriter(format, nextWriter), contexts);
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
     * Writes the contents of the repository, with optional contexts restrictions, to the given
     * writer using the RDF/XML format.
     * 
     * NOTE: This method logs, but does not through any exceptions.
     * 
     * @param nextRepository
     *            The repository that contains the data to be exported.
     * @param nextWriter
     *            The writer to write the results to.
     * @param contexts
     *            An optional varargs set of Resources identifying contexts in the repository that
     *            are to be exported
     */
    public static void toWriter(final Repository nextRepository, final java.io.Writer nextWriter,
            final Resource... contexts)
    {
        RdfUtils.toWriter(nextRepository, nextWriter, RDFFormat.RDFXML, contexts);
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
