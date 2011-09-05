package org.queryall.statistics;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.base.BaseQueryAllInterface;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.project.ProjectSchema;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.SparqlProviderSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.blacklist.BlacklistController;
import org.queryall.query.HttpUrlQueryRunnable;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class StatisticsEntry implements BaseQueryAllInterface
{
    private static final Logger log = LoggerFactory.getLogger(StatisticsEntry.class);
    @SuppressWarnings("unused")
    private static final boolean _INFO = StatisticsEntry.log.isInfoEnabled();
    private static final boolean _DEBUG = StatisticsEntry.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _TRACE = StatisticsEntry.log.isTraceEnabled();
    
    public static final int IMPLEMENTED_STATISTICS_VERSION = 1;
    
    public static URI statisticsacceptHeaderUri;
    public static URI statisticsconfigLocationsUri;
    public static URI statisticsconfigVersionUri;
    public static URI statisticsconnecttimeoutUri;
    public static URI statisticscurrentdatetimeUri;
    public static URI statisticserrorproviderUrisUri;
    public static URI statisticskeyUri;
    public static URI statisticslastServerRestartUri;
    public static URI statisticsnamespaceUrisUri;
    public static URI statisticsprofileUrisUri;
    public static URI statisticsqueryStringUri;
    public static URI statisticsquerytypeUrisUri;
    public static URI statisticsreadtimeoutUri;
    public static URI statisticsrealHostNameUri;
    public static URI statisticsrequestedContentTypeUri;
    public static URI statisticsresponseTimeUri;
    public static URI statisticsserverSoftwareVersionUri;
    public static URI statisticsstdeverrorlatencyUri;
    public static URI statisticsstdevlatencyUri;
    public static URI statisticssuccessfulproviderUrisUri;
    public static URI statisticssumerrorlatencyUri;
    public static URI statisticssumerrorsUri;
    public static URI statisticssumLatencyUri;
    public static URI statisticssumQueriesUri;
    public static URI statisticsTypeUri;
    public static URI statisticsuserAgentUri;
    public static URI statisticsuserHostAddressUri;
    
    /***
     * key profileUris successfulproviderUris errorproviderUris configLocations querytypeUris
     * namespaceUris configVersion readtimeout connecttimeout userHostAddress userAgent realHostName
     * queryString responseTime sumLatency sumQueries stdevlatency sumerrors sumerrorlatency
     * stdeverrorlatency
     ***/
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.STATISTICS.getBaseURI();
        
        StatisticsEntry.statisticsTypeUri = f.createURI(baseUri, "Statistics");
        StatisticsEntry.statisticskeyUri = f.createURI(baseUri, "key");
        
        StatisticsEntry.statisticscurrentdatetimeUri = f.createURI(baseUri, "submissionDate");
        StatisticsEntry.statisticsprofileUrisUri = f.createURI(baseUri, "profileUri");
        StatisticsEntry.statisticssuccessfulproviderUrisUri = f.createURI(baseUri, "successfulProviderUri");
        StatisticsEntry.statisticserrorproviderUrisUri = f.createURI(baseUri, "errorProviderUri");
        StatisticsEntry.statisticsconfigLocationsUri = f.createURI(baseUri, "configLocation");
        StatisticsEntry.statisticsquerytypeUrisUri = f.createURI(baseUri, "querytypeUri");
        StatisticsEntry.statisticsnamespaceUrisUri = f.createURI(baseUri, "namespaceUri");
        StatisticsEntry.statisticsconfigVersionUri = f.createURI(baseUri, "configVersion");
        StatisticsEntry.statisticsreadtimeoutUri = f.createURI(baseUri, "readtimeout");
        StatisticsEntry.statisticsconnecttimeoutUri = f.createURI(baseUri, "connecttimeout");
        StatisticsEntry.statisticsuserHostAddressUri = f.createURI(baseUri, "userHostAddress");
        StatisticsEntry.statisticsuserAgentUri = f.createURI(baseUri, "userAgent");
        StatisticsEntry.statisticsrealHostNameUri = f.createURI(baseUri, "realHostName");
        StatisticsEntry.statisticsqueryStringUri = f.createURI(baseUri, "queryString");
        StatisticsEntry.statisticsresponseTimeUri = f.createURI(baseUri, "responseTime");
        StatisticsEntry.statisticssumLatencyUri = f.createURI(baseUri, "sumLatency");
        StatisticsEntry.statisticssumQueriesUri = f.createURI(baseUri, "sumQueries");
        StatisticsEntry.statisticsstdevlatencyUri = f.createURI(baseUri, "stdevlatency");
        StatisticsEntry.statisticssumerrorsUri = f.createURI(baseUri, "sumerrors");
        StatisticsEntry.statisticssumerrorlatencyUri = f.createURI(baseUri, "sumerrorlatency");
        StatisticsEntry.statisticsstdeverrorlatencyUri = f.createURI(baseUri, "stdeverrorlatency");
        
        StatisticsEntry.statisticslastServerRestartUri = f.createURI(baseUri, "lastServerRestart");
        StatisticsEntry.statisticsserverSoftwareVersionUri = f.createURI(baseUri, "serverSoftwareVersion");
        StatisticsEntry.statisticsacceptHeaderUri = f.createURI(baseUri, "acceptHeader");
        StatisticsEntry.statisticsrequestedContentTypeUri = f.createURI(baseUri, "requestedContentType");
    }
    
    /**
     * @return the statisticsconfigLocationsUri
     */
    public static URI getStatisticsconfigLocationsUri()
    {
        return StatisticsEntry.statisticsconfigLocationsUri;
    }
    
    /**
     * @return the statisticsconfigVersionUri
     */
    public static URI getStatisticsconfigVersionUri()
    {
        return StatisticsEntry.statisticsconfigVersionUri;
    }
    
    /**
     * @return the statisticsconnecttimeoutUri
     */
    public static URI getStatisticsconnecttimeoutUri()
    {
        return StatisticsEntry.statisticsconnecttimeoutUri;
    }
    
    /**
     * @return the statisticscurrentdatetimeUri
     */
    public static URI getStatisticscurrentdatetimeUri()
    {
        return StatisticsEntry.statisticscurrentdatetimeUri;
    }
    
    /**
     * @return the statisticserrorproviderUrisUri
     */
    public static URI getStatisticserrorproviderUrisUri()
    {
        return StatisticsEntry.statisticserrorproviderUrisUri;
    }
    
    /**
     * @return the statisticskeyUri
     */
    public static URI getStatisticskeyUri()
    {
        return StatisticsEntry.statisticskeyUri;
    }
    
    /**
     * @return the statisticsNamespace
     */
    public static String getStatisticsNamespace()
    {
        return QueryAllNamespaces.STATISTICS.getBaseURI();
    }
    
    /**
     * @return the statisticsnamespaceUrisUri
     */
    public static URI getStatisticsnamespaceUrisUri()
    {
        return StatisticsEntry.statisticsnamespaceUrisUri;
    }
    
    /**
     * @return the statisticsprofileUrisUri
     */
    public static URI getStatisticsprofileUrisUri()
    {
        return StatisticsEntry.statisticsprofileUrisUri;
    }
    
    /**
     * @return the statisticsqueryStringUri
     */
    public static URI getStatisticsqueryStringUri()
    {
        return StatisticsEntry.statisticsqueryStringUri;
    }
    
    /**
     * @return the statisticsquerytypeUrisUri
     */
    public static URI getStatisticsquerytypeUrisUri()
    {
        return StatisticsEntry.statisticsquerytypeUrisUri;
    }
    
    /**
     * @return the statisticsreadtimeoutUri
     */
    public static URI getStatisticsreadtimeoutUri()
    {
        return StatisticsEntry.statisticsreadtimeoutUri;
    }
    
    /**
     * @return the statisticsrealHostNameUri
     */
    public static URI getStatisticsrealHostNameUri()
    {
        return StatisticsEntry.statisticsrealHostNameUri;
    }
    
    /**
     * @return the statisticsresponseTimeUri
     */
    public static URI getStatisticsresponseTimeUri()
    {
        return StatisticsEntry.statisticsresponseTimeUri;
    }
    
    /**
     * @return the statisticsstdeverrorlatencyUri
     */
    public static URI getStatisticsstdeverrorlatencyUri()
    {
        return StatisticsEntry.statisticsstdeverrorlatencyUri;
    }
    
    /**
     * @return the statisticsstdevlatencyUri
     */
    public static URI getStatisticsstdevlatencyUri()
    {
        return StatisticsEntry.statisticsstdevlatencyUri;
    }
    
    /**
     * @return the statisticssuccessfulproviderUrisUri
     */
    public static URI getStatisticssuccessfulproviderUrisUri()
    {
        return StatisticsEntry.statisticssuccessfulproviderUrisUri;
    }
    
    /**
     * @return the statisticssumerrorlatencyUri
     */
    public static URI getStatisticssumerrorlatencyUri()
    {
        return StatisticsEntry.statisticssumerrorlatencyUri;
    }
    
    /**
     * @return the statisticssumerrorsUri
     */
    public static URI getStatisticssumerrorsUri()
    {
        return StatisticsEntry.statisticssumerrorsUri;
    }
    
    /**
     * @return the statisticssumLatencyUri
     */
    public static URI getStatisticssumLatencyUri()
    {
        return StatisticsEntry.statisticssumLatencyUri;
    }
    
    /**
     * @return the statisticssumQueriesUri
     */
    public static URI getStatisticssumQueriesUri()
    {
        return StatisticsEntry.statisticssumQueriesUri;
    }
    
    /**
     * @return the statisticsTypeUri
     */
    public static URI getStatisticsTypeUri()
    {
        return StatisticsEntry.statisticsTypeUri;
    }
    
    /**
     * @return the statisticsuserAgentUri
     */
    public static URI getStatisticsuserAgentUri()
    {
        return StatisticsEntry.statisticsuserAgentUri;
    }
    
    /**
     * @return the statisticsuserHostAddressUri
     */
    public static URI getStatisticsuserHostAddressUri()
    {
        return StatisticsEntry.statisticsuserHostAddressUri;
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(StatisticsEntry.statisticsTypeUri, RDF.TYPE, OWL.CLASS, contextUri);
            
            con.add(StatisticsEntry.statisticskeyUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            
            con.add(StatisticsEntry.statisticsprofileUrisUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticsquerytypeUrisUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            
            con.add(StatisticsEntry.statisticscurrentdatetimeUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticssuccessfulproviderUrisUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticserrorproviderUrisUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticsconfigLocationsUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticsnamespaceUrisUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticsconfigVersionUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticsreadtimeoutUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticsconnecttimeoutUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticsuserHostAddressUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticsuserAgentUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticsrealHostNameUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticsqueryStringUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticsresponseTimeUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticssumLatencyUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticssumQueriesUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticsstdevlatencyUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticssumerrorsUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticssumerrorlatencyUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticsstdeverrorlatencyUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticslastServerRestartUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticsserverSoftwareVersionUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticsacceptHeaderUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(StatisticsEntry.statisticsrequestedContentTypeUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            
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
            
            StatisticsEntry.log.error("RepositoryException: " + re.getMessage());
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
    
    public Date currentDate = null;
    
    private String acceptHeader = "";
    
    private Collection<String> configLocations = new HashSet<String>();
    
    private String configVersion = "";
    
    private int connecttimeout = -1;
    
    private URI curationStatus = ProjectSchema.getProjectNotCuratedUri();
    
    private Collection<String> errorproviderUris = new HashSet<String>();
    
    /**
     * 
     */
    private URI key;
    
    private String lastServerRestart = "";
    
    private Collection<String> namespaceUris = new HashSet<String>();
    
    private Collection<String> profileUris = new HashSet<String>();
    
    private String queryString = "";
    
    private Collection<String> querytypeUris = new HashSet<String>();
    
    private int readtimeout = -1;
    
    private String realHostName = "";
    
    private String requestedContentType = "";
    
    private long responseTime = -1;
    
    private String serverSoftwareVersion = "";
    
    private double stdeverrorlatency = 0.0;
    
    private double stdevlatency = 0.0;
    private Collection<String> successfulproviderUris = new HashSet<String>();
    private long sumerrorlatency = 0;
    private int sumerrors = 0;
    private long sumLatency = -1;
    private int sumQueries = -1;
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    private String userAgent = "";
    private String userHostAddress = "";
    
    /**
     * 
     */
    public StatisticsEntry()
    {
    }
    
    /**
     * @param nextKey
     * @param nextprofileUris
     * @param nextsuccessfulproviderUris
     * @param nexterrorproviderUris
     * @param nextconfigLocations
     * @param nextquerytypeUris
     * @param nextnamespaceUris
     * @param nextconfigVersion
     * @param nextreadtimeout
     * @param nextconnecttimeout
     * @param nextuserHostAddress
     * @param nextuserAgent
     * @param nextrealHostName
     * @param nextqueryString
     * @param nextresponseTime
     * @param nextsumLatency
     * @param nextsumQueries
     * @param nextstdevlatency
     * @param nextsumerrors
     * @param nextsumerrorlatency
     * @param nextstdeverrorlatency
     * @param nextlastServerRestart
     * @param nextserverSoftwareVersion
     * @param nextrequestedContentType
     *            ;
     * @param nextacceptHeader
     *            ;
     */
    public StatisticsEntry(final String nextKey, final Collection<String> nextprofileUris,
            final Collection<String> nextsuccessfulproviderUris, final Collection<String> nexterrorproviderUris,
            final Collection<String> nextconfigLocations, final Collection<String> nextquerytypeUris,
            final Collection<String> nextnamespaceUris, final String nextconfigVersion, final int nextreadtimeout,
            final int nextconnecttimeout, final String nextuserHostAddress, final String nextuserAgent,
            final String nextrealHostName, final String nextqueryString, final long nextresponseTime,
            final long nextsumLatency, final int nextsumQueries, final double nextstdevlatency,
            final int nextsumerrors, final long nextsumerrorlatency, final double nextstdeverrorlatency,
            final String nextlastServerRestart, final String nextserverSoftwareVersion, final String nextacceptHeader,
            final String nextrequestedContentType)
    {
        this.setKey(nextKey);
        this.profileUris = nextprofileUris;
        this.successfulproviderUris = nextsuccessfulproviderUris;
        this.errorproviderUris = nexterrorproviderUris;
        this.configLocations = nextconfigLocations;
        this.querytypeUris = nextquerytypeUris;
        this.namespaceUris = nextnamespaceUris;
        this.configVersion = nextconfigVersion;
        this.readtimeout = nextreadtimeout;
        this.connecttimeout = nextconnecttimeout;
        this.userHostAddress = nextuserHostAddress;
        this.userAgent = nextuserAgent;
        this.realHostName = nextrealHostName;
        this.queryString = nextqueryString;
        this.responseTime = nextresponseTime;
        this.sumLatency = nextsumLatency;
        this.sumQueries = nextsumQueries;
        this.stdevlatency = nextstdevlatency;
        this.sumerrors = nextsumerrors;
        this.sumerrorlatency = nextsumerrorlatency;
        this.stdeverrorlatency = nextstdeverrorlatency;
        
        this.lastServerRestart = nextlastServerRestart;
        this.serverSoftwareVersion = nextserverSoftwareVersion;
        this.requestedContentType = nextrequestedContentType;
        this.acceptHeader = nextacceptHeader;
    }
    
    @Override
    public void addUnrecognisedStatement(final Statement unrecognisedStatement)
    {
        this.unrecognisedStatements.add(unrecognisedStatement);
    }
    
    @Override
    public boolean equals(final Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(obj == null)
        {
            return false;
        }
        if(!(obj instanceof StatisticsEntry))
        {
            return false;
        }
        final StatisticsEntry other = (StatisticsEntry)obj;
        if(this.configLocations == null)
        {
            if(other.configLocations != null)
            {
                return false;
            }
        }
        else if(!this.configLocations.equals(other.configLocations))
        {
            return false;
        }
        if(this.configVersion == null)
        {
            if(other.configVersion != null)
            {
                return false;
            }
        }
        else if(!this.configVersion.equals(other.configVersion))
        {
            return false;
        }
        if(this.connecttimeout != other.connecttimeout)
        {
            return false;
        }
        if(this.errorproviderUris == null)
        {
            if(other.errorproviderUris != null)
            {
                return false;
            }
        }
        else if(!this.errorproviderUris.equals(other.errorproviderUris))
        {
            return false;
        }
        if(this.key == null)
        {
            if(other.getKey() != null)
            {
                return false;
            }
        }
        else if(!this.key.equals(other.getKey()))
        {
            return false;
        }
        if(this.namespaceUris == null)
        {
            if(other.namespaceUris != null)
            {
                return false;
            }
        }
        else if(!this.namespaceUris.equals(other.namespaceUris))
        {
            return false;
        }
        if(this.profileUris == null)
        {
            if(other.profileUris != null)
            {
                return false;
            }
        }
        else if(!this.profileUris.equals(other.profileUris))
        {
            return false;
        }
        if(this.queryString == null)
        {
            if(other.queryString != null)
            {
                return false;
            }
        }
        else if(!this.queryString.equals(other.queryString))
        {
            return false;
        }
        if(this.querytypeUris == null)
        {
            if(other.querytypeUris != null)
            {
                return false;
            }
        }
        else if(!this.querytypeUris.equals(other.querytypeUris))
        {
            return false;
        }
        if(this.readtimeout != other.readtimeout)
        {
            return false;
        }
        if(this.realHostName == null)
        {
            if(other.realHostName != null)
            {
                return false;
            }
        }
        else if(!this.realHostName.equals(other.realHostName))
        {
            return false;
        }
        if(this.responseTime != other.responseTime)
        {
            return false;
        }
        if(Double.doubleToLongBits(this.stdeverrorlatency) != Double.doubleToLongBits(other.stdeverrorlatency))
        {
            return false;
        }
        if(Double.doubleToLongBits(this.stdevlatency) != Double.doubleToLongBits(other.stdevlatency))
        {
            return false;
        }
        if(this.successfulproviderUris == null)
        {
            if(other.successfulproviderUris != null)
            {
                return false;
            }
        }
        else if(!this.successfulproviderUris.equals(other.successfulproviderUris))
        {
            return false;
        }
        if(this.sumLatency != other.sumLatency)
        {
            return false;
        }
        if(this.sumQueries != other.sumQueries)
        {
            return false;
        }
        if(this.sumerrorlatency != other.sumerrorlatency)
        {
            return false;
        }
        if(this.sumerrors != other.sumerrors)
        {
            return false;
        }
        if(this.userAgent == null)
        {
            if(other.userAgent != null)
            {
                return false;
            }
        }
        else if(!this.userAgent.equals(other.userAgent))
        {
            return false;
        }
        if(this.userHostAddress == null)
        {
            if(other.userHostAddress != null)
            {
                return false;
            }
        }
        else if(!this.userHostAddress.equals(other.userHostAddress))
        {
            return false;
        }
        return true;
    }
    
    /**
     * @return
     * @throws OpenRDFException
     */
    public HttpUrlQueryRunnable generateThread(final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController, final int modelVersion) throws OpenRDFException
    {
        if(localSettings.getURIProperty("statisticsServerMethod", SparqlProviderSchema.getProviderHttpPostSparql())
                .equals(SparqlProviderSchema.getProviderHttpPostSparql()))
        {
            final Repository myRepository = new SailRepository(new MemoryStore());
            myRepository.initialize();
            
            @SuppressWarnings("unused")
            final boolean rdfOkay = this.toRdf(myRepository, this.getKey(), modelVersion);
            
            final RDFFormat writerFormat = Rio.getWriterFormatForMIMEType("text/plain");
            
            final StringWriter insertTriples = new StringWriter();
            
            RdfUtils.toWriter(myRepository, insertTriples, writerFormat);
            
            final String insertTriplesContent = insertTriples.toString();
            
            // log.info("StatisticsEntry: insertTriplesContent="+insertTriplesContent);
            
            String sparqlInsertQuery = "define sql:log-enable 2 INSERT ";
            
            if(localSettings.getBooleanProperty("statisticsServerUseGraphUri", true))
            {
                sparqlInsertQuery +=
                        " INTO GRAPH <" + localSettings.getStringProperty("statisticsServerGraphUri", "") + "> ";
            }
            
            sparqlInsertQuery += " { " + insertTriplesContent + " } ";
            
            if(StatisticsEntry._DEBUG)
            {
                StatisticsEntry.log.debug("StatisticsEntry: sparqlInsertQuery=" + sparqlInsertQuery);
            }
            
            return new HttpUrlQueryRunnable(localSettings.getStringProperty("statisticsServerMethod", ""),
                    localSettings.getStringProperty("statisticsServerUrl", ""), sparqlInsertQuery, "*/*",
                    localSettings, localBlacklistController);
        }
        else if(localSettings.getURIProperty("statisticsServerMethod", HttpProviderSchema.getProviderHttpPostUrlUri())
                .equals(HttpProviderSchema.getProviderHttpPostUrlUri()))
        {
            final String postInformation = this.toPostArray();
            
            if(StatisticsEntry._DEBUG)
            {
                StatisticsEntry.log.debug("StatisticsEntry: postInformation=" + postInformation);
            }
            
            return new HttpUrlQueryRunnable(localSettings.getStringProperty("statisticsServerMethod", ""),
                    localSettings.getStringProperty("statisticsServerUrl", ""), postInformation, "*/*", localSettings,
                    localBlacklistController);
        }
        else
        {
            throw new RuntimeException(
                    "StatisticsEntry.generateThread: Unknown localSettings.getStringPropertyFromConfig(\"statisticsServerMethod\")="
                            + localSettings.getStringProperty("statisticsServerMethod", ""));
        }
    }
    
    /**
     * @return the configLocations
     */
    public Collection<String> getConfigLocations()
    {
        return this.configLocations;
    }
    
    /**
     * @return the configVersion
     */
    public String getConfigVersion()
    {
        return this.configVersion;
    }
    
    /**
     * @return the connecttimeout
     */
    public int getConnecttimeout()
    {
        return this.connecttimeout;
    }
    
    @Override
    public URI getCurationStatus()
    {
        return this.curationStatus;
    }
    
    /**
     * @return the namespace used to represent objects of this type by default
     */
    @Override
    public QueryAllNamespaces getDefaultNamespace()
    {
        return QueryAllNamespaces.STATISTICS;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Set<URI> getElementTypes()
    {
        final Set<URI> results = new HashSet<URI>();
        
        results.add(StatisticsEntry.statisticsTypeUri);
        
        return results;
    }
    
    /**
     * @return the errorproviderUris
     */
    public Collection<String> getErrorproviderUris()
    {
        return this.errorproviderUris;
    }
    
    /**
     * @return the key
     */
    
    @Override
    public URI getKey()
    {
        return this.key;
    }
    
    /**
     * @return the namespaceUris
     */
    public Collection<String> getNamespaceUris()
    {
        return this.namespaceUris;
    }
    
    /**
     * @return the profileUris
     */
    public Collection<String> getProfileUris()
    {
        return this.profileUris;
    }
    
    /**
     * @return the queryString
     */
    public String getQueryString()
    {
        return this.queryString;
    }
    
    /**
     * @return the querytypeUris
     */
    public Collection<String> getQuerytypeUris()
    {
        return this.querytypeUris;
    }
    
    /**
     * @return the readtimeout
     */
    public int getReadtimeout()
    {
        return this.readtimeout;
    }
    
    /**
     * @return the realHostName
     */
    public String getRealHostName()
    {
        return this.realHostName;
    }
    
    /**
     * @return the responseTime
     */
    public long getResponseTime()
    {
        return this.responseTime;
    }
    
    /**
     * @return the stdeverrorlatency
     */
    public double getStdeverrorlatency()
    {
        return this.stdeverrorlatency;
    }
    
    /**
     * @return the stdevlatency
     */
    public double getStdevlatency()
    {
        return this.stdevlatency;
    }
    
    /**
     * @return the successfulproviderUris
     */
    public Collection<String> getSuccessfulproviderUris()
    {
        return this.successfulproviderUris;
    }
    
    /**
     * @return the sumerrorlatency
     */
    public long getSumerrorlatency()
    {
        return this.sumerrorlatency;
    }
    
    /**
     * @return the sumerrors
     */
    public int getSumerrors()
    {
        return this.sumerrors;
    }
    
    /**
     * @return the sumLatency
     */
    public long getSumLatency()
    {
        return this.sumLatency;
    }
    
    /**
     * @return the sumQueries
     */
    public int getSumQueries()
    {
        return this.sumQueries;
    }
    
    @Override
    public String getTitle()
    {
        return null;
    }
    
    @Override
    public Collection<Statement> getUnrecognisedStatements()
    {
        return this.unrecognisedStatements;
    }
    
    /**
     * @return the userAgent
     */
    public String getUserAgent()
    {
        return this.userAgent;
    }
    
    /**
     * @return the userHostAddress
     */
    public String getUserHostAddress()
    {
        return this.userHostAddress;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.configLocations == null) ? 0 : this.configLocations.hashCode());
        result = prime * result + ((this.configVersion == null) ? 0 : this.configVersion.hashCode());
        result = prime * result + this.connecttimeout;
        result = prime * result + ((this.errorproviderUris == null) ? 0 : this.errorproviderUris.hashCode());
        result = prime * result + ((this.key == null) ? 0 : this.key.hashCode());
        result = prime * result + ((this.namespaceUris == null) ? 0 : this.namespaceUris.hashCode());
        result = prime * result + ((this.profileUris == null) ? 0 : this.profileUris.hashCode());
        result = prime * result + ((this.queryString == null) ? 0 : this.queryString.hashCode());
        result = prime * result + ((this.querytypeUris == null) ? 0 : this.querytypeUris.hashCode());
        result = prime * result + this.readtimeout;
        result = prime * result + ((this.realHostName == null) ? 0 : this.realHostName.hashCode());
        result = prime * result + (int)(this.responseTime ^ (this.responseTime >>> 32));
        long temp;
        temp = Double.doubleToLongBits(this.stdeverrorlatency);
        result = prime * result + (int)(temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.stdevlatency);
        result = prime * result + (int)(temp ^ (temp >>> 32));
        result = prime * result + ((this.successfulproviderUris == null) ? 0 : this.successfulproviderUris.hashCode());
        result = prime * result + (int)(this.sumLatency ^ (this.sumLatency >>> 32));
        result = prime * result + this.sumQueries;
        result = prime * result + (int)(this.sumerrorlatency ^ (this.sumerrorlatency >>> 32));
        result = prime * result + this.sumerrors;
        result = prime * result + ((this.userAgent == null) ? 0 : this.userAgent.hashCode());
        result = prime * result + ((this.userHostAddress == null) ? 0 : this.userHostAddress.hashCode());
        return result;
    }
    
    /**
     * @param configLocations
     *            the configLocations to set
     */
    public void setConfigLocations(final Collection<String> configLocations)
    {
        this.configLocations = configLocations;
    }
    
    /**
     * @param configVersion
     *            the configVersion to set
     */
    public void setConfigVersion(final String configVersion)
    {
        this.configVersion = configVersion;
    }
    
    /**
     * @param connecttimeout
     *            the connecttimeout to set
     */
    public void setConnecttimeout(final int connecttimeout)
    {
        this.connecttimeout = connecttimeout;
    }
    
    @Override
    public void setCurationStatus(final URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    /**
     * @param errorproviderUris
     *            the errorproviderUris to set
     */
    public void setErrorproviderUris(final Collection<String> errorproviderUris)
    {
        this.errorproviderUris = errorproviderUris;
    }
    
    /**
     * @param key
     *            the key to set
     */
    
    @Override
    public void setKey(final String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }
    
    @Override
    public void setKey(final URI nextKey)
    {
        this.key = nextKey;
    }
    
    /**
     * @param namespaceUris
     *            the namespaceUris to set
     */
    public void setNamespaceUris(final Collection<String> namespaceUris)
    {
        this.namespaceUris = namespaceUris;
    }
    
    /**
     * @param profileUris
     *            the profileUris to set
     */
    public void setProfileUris(final Collection<String> profileUris)
    {
        this.profileUris = profileUris;
    }
    
    /**
     * @param queryString
     *            the queryString to set
     */
    public void setQueryString(final String queryString)
    {
        this.queryString = queryString;
    }
    
    /**
     * @param querytypeUris
     *            the querytypeUris to set
     */
    public void setQuerytypeUris(final Collection<String> querytypeUris)
    {
        this.querytypeUris = querytypeUris;
    }
    
    /**
     * @param readtimeout
     *            the readtimeout to set
     */
    public void setReadtimeout(final int readtimeout)
    {
        this.readtimeout = readtimeout;
    }
    
    /**
     * @param realHostName
     *            the realHostName to set
     */
    public void setRealHostName(final String realHostName)
    {
        this.realHostName = realHostName;
    }
    
    /**
     * @param responseTime
     *            the responseTime to set
     */
    public void setResponseTime(final long responseTime)
    {
        this.responseTime = responseTime;
    }
    
    /**
     * @param stdeverrorlatency
     *            the stdeverrorlatency to set
     */
    public void setStdeverrorlatency(final double stdeverrorlatency)
    {
        this.stdeverrorlatency = stdeverrorlatency;
    }
    
    /**
     * @param stdevlatency
     *            the stdevlatency to set
     */
    public void setStdevlatency(final double stdevlatency)
    {
        this.stdevlatency = stdevlatency;
    }
    
    /**
     * @param successfulproviderUris
     *            the successfulproviderUris to set
     */
    public void setSuccessfulproviderUris(final Collection<String> successfulproviderUris)
    {
        this.successfulproviderUris = successfulproviderUris;
    }
    
    /**
     * @param sumerrorlatency
     *            the sumerrorlatency to set
     */
    public void setSumerrorlatency(final long sumerrorlatency)
    {
        this.sumerrorlatency = sumerrorlatency;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    
    /**
     * @param sumerrors
     *            the sumerrors to set
     */
    public void setSumerrors(final int sumerrors)
    {
        this.sumerrors = sumerrors;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    
    /**
     * @param sumLatency
     *            the sumLatency to set
     */
    public void setSumLatency(final long sumLatency)
    {
        this.sumLatency = sumLatency;
    }
    
    /**
     * @param sumQueries
     *            the sumQueries to set
     */
    public void setSumQueries(final int sumQueries)
    {
        this.sumQueries = sumQueries;
    }
    
    @Override
    public void setTitle(final String title)
    {
    }
    
    /**
     * @param userAgent
     *            the userAgent to set
     */
    public void setUserAgent(final String userAgent)
    {
        this.userAgent = userAgent;
    }
    
    /**
     * @param userHostAddress
     *            the userHostAddress to set
     */
    public void setUserHostAddress(final String userHostAddress)
    {
        this.userHostAddress = userHostAddress;
    }
    
    @Override
    public String toHtml()
    {
        final StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        final String prefix = "statistics_";
        
        return sb.toString();
    }
    
    @Override
    public String toHtmlFormBody()
    {
        final StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        final String prefix = "statistics_";
        
        return sb.toString();
    }
    
    /**
     * @return
     */
    public String toPostArray()
    {
        String result = "";
        
        result += "key=" + StringUtils.percentEncode(this.getKey().stringValue()) + "&";
        result +=
                "profileUris=" + StringUtils.percentEncode(StringUtils.joinStringCollection(this.profileUris, ","))
                        + "&";
        result +=
                "successfulproviderUris="
                        + StringUtils.percentEncode(StringUtils.joinStringCollection(this.successfulproviderUris, ","))
                        + "&";
        result +=
                "errorproviderUris="
                        + StringUtils.percentEncode(StringUtils.joinStringCollection(this.errorproviderUris, ","))
                        + "&";
        result +=
                "configLocations="
                        + StringUtils.percentEncode(StringUtils.joinStringCollection(this.configLocations, ",")) + "&";
        result +=
                "querytypeUris=" + StringUtils.percentEncode(StringUtils.joinStringCollection(this.querytypeUris, ","))
                        + "&";
        result +=
                "namespaceUris=" + StringUtils.percentEncode(StringUtils.joinStringCollection(this.namespaceUris, ","))
                        + "&";
        result += "configVersion=" + StringUtils.percentEncode(this.configVersion) + "&";
        result += "readtimeout=" + StringUtils.percentEncode(this.readtimeout + "") + "&";
        result += "connecttimeout=" + StringUtils.percentEncode(this.connecttimeout + "") + "&";
        result += "userHostAddress=" + StringUtils.percentEncode(this.userHostAddress) + "&";
        result += "userAgent=" + StringUtils.percentEncode(this.userAgent) + "&";
        result += "realHostName=" + StringUtils.percentEncode(this.realHostName) + "&";
        result += "queryString=" + StringUtils.percentEncode(this.queryString) + "&";
        result += "responseTime=" + StringUtils.percentEncode(this.responseTime + "") + "&";
        result += "sumLatency=" + StringUtils.percentEncode(this.sumLatency + "") + "&";
        result += "sumQueries=" + StringUtils.percentEncode(this.sumQueries + "") + "&";
        result += "stdevlatency=" + StringUtils.percentEncode(this.stdevlatency + "") + "&";
        result += "sumerrors=" + StringUtils.percentEncode(this.sumerrors + "") + "&";
        result += "sumerrorlatency=" + StringUtils.percentEncode(this.sumerrorlatency + "") + "&";
        result += "stdeverrorlatency=" + StringUtils.percentEncode(this.stdeverrorlatency + "") + "&";
        result += "lastServerRestart=" + StringUtils.percentEncode(this.lastServerRestart + "") + "&";
        result += "serverSoftwareVersion=" + StringUtils.percentEncode(this.serverSoftwareVersion + "") + "&";
        result += "acceptHeader=" + StringUtils.percentEncode(this.acceptHeader + "") + "&";
        result += "requestedContentType=" + StringUtils.percentEncode(this.requestedContentType + "") + "&";
        return result;
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        // String nTriplesInsertString = "";
        
        RepositoryConnection connection = null;
        
        try
        {
            connection = myRepository.getConnection();
            
            final ValueFactory f = Constants.valueFactory;
            
            if((keyToUse == null))
            {
                StatisticsEntry.log.error("StatisticsEntry.toRdf: keyToUse was empty");
                
                return false;
            }
            
            final URI keyUri = keyToUse;
            
            if(this.currentDate == null)
            {
                this.currentDate = new Date();
            }
            
            final String currentDateString = Constants.ISO8601UTC().format(this.currentDate);
            
            final Literal currentDateLiteral = f.createLiteral(currentDateString, XMLSchema.DATETIME);
            
            final Literal configVersionLiteral = f.createLiteral(this.configVersion);
            final Literal readtimeoutLiteral = f.createLiteral(this.readtimeout);
            final Literal connecttimeoutLiteral = f.createLiteral(this.connecttimeout);
            final Literal userHostAddressLiteral = f.createLiteral(this.userHostAddress);
            final Literal userAgentLiteral = f.createLiteral(this.userAgent);
            final Literal realHostNameLiteral = f.createLiteral(this.realHostName);
            final Literal queryStringLiteral = f.createLiteral(this.queryString);
            final Literal responseTimeLiteral = f.createLiteral(this.responseTime);
            final Literal sumLatencyLiteral = f.createLiteral(this.sumLatency);
            final Literal sumQueriesLiteral = f.createLiteral(this.sumQueries);
            final Literal stdevlatencyLiteral = f.createLiteral(this.stdevlatency);
            final Literal sumerrorsLiteral = f.createLiteral(this.sumerrors);
            final Literal sumerrorlatencyLiteral = f.createLiteral(this.sumerrorlatency);
            final Literal stdeverrorlatencyLiteral = f.createLiteral(this.stdeverrorlatency);
            
            final Literal lastServerRestartLiteral = f.createLiteral(this.lastServerRestart);
            final Literal serverSoftwareVersionLiteral = f.createLiteral(this.serverSoftwareVersion);
            final Literal requestedContentTypeLiteral = f.createLiteral(this.requestedContentType);
            final Literal acceptHeaderLiteral = f.createLiteral(this.acceptHeader);
            
            connection.setAutoCommit(false);
            
            connection.add(keyUri, RDF.TYPE, StatisticsEntry.statisticsTypeUri, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticscurrentdatetimeUri, currentDateLiteral, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticsconfigVersionUri, configVersionLiteral, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticsreadtimeoutUri, readtimeoutLiteral, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticsconnecttimeoutUri, connecttimeoutLiteral, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticsuserHostAddressUri, userHostAddressLiteral, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticsuserAgentUri, userAgentLiteral, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticsrealHostNameUri, realHostNameLiteral, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticsqueryStringUri, queryStringLiteral, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticsresponseTimeUri, responseTimeLiteral, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticssumLatencyUri, sumLatencyLiteral, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticssumQueriesUri, sumQueriesLiteral, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticsstdevlatencyUri, stdevlatencyLiteral, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticssumerrorsUri, sumerrorsLiteral, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticssumerrorlatencyUri, sumerrorlatencyLiteral, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticsstdeverrorlatencyUri, stdeverrorlatencyLiteral, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticslastServerRestartUri, lastServerRestartLiteral, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticsserverSoftwareVersionUri, serverSoftwareVersionLiteral,
                    keyUri);
            connection.add(keyUri, StatisticsEntry.statisticsacceptHeaderUri, acceptHeaderLiteral, keyUri);
            connection.add(keyUri, StatisticsEntry.statisticsrequestedContentTypeUri, requestedContentTypeLiteral,
                    keyUri);
            
            if(this.profileUris != null)
            {
                for(final String nextProfileUri : this.profileUris)
                {
                    if(!nextProfileUri.trim().equals(""))
                    {
                        connection.add(keyUri, StatisticsEntry.statisticsprofileUrisUri, f.createURI(nextProfileUri),
                                keyUri);
                    }
                }
            }
            
            if(this.successfulproviderUris != null)
            {
                for(final String nextSuccessfulProvidersUri : this.successfulproviderUris)
                {
                    if(!nextSuccessfulProvidersUri.trim().equals(""))
                    {
                        connection.add(keyUri, StatisticsEntry.statisticssuccessfulproviderUrisUri,
                                f.createLiteral(nextSuccessfulProvidersUri), keyUri);
                    }
                }
            }
            
            if(this.errorproviderUris != null)
            {
                for(final String nextErrorProvidersUri : this.errorproviderUris)
                {
                    if(!nextErrorProvidersUri.trim().equals(""))
                    {
                        connection.add(keyUri, StatisticsEntry.statisticserrorproviderUrisUri,
                                f.createLiteral(nextErrorProvidersUri), keyUri);
                    }
                }
            }
            
            if(this.configLocations != null)
            {
                for(final String nextConfigLocation : this.configLocations)
                {
                    if(!nextConfigLocation.trim().equals(""))
                    {
                        connection.add(keyUri, StatisticsEntry.statisticsconfigLocationsUri,
                                f.createLiteral(nextConfigLocation), keyUri);
                    }
                }
            }
            
            if(this.querytypeUris != null)
            {
                for(final String nextQuerytypeUri : this.querytypeUris)
                {
                    if(!nextQuerytypeUri.trim().equals(""))
                    {
                        connection.add(keyUri, StatisticsEntry.statisticsquerytypeUrisUri,
                                f.createURI(nextQuerytypeUri), keyUri);
                    }
                }
            }
            
            if(this.namespaceUris != null)
            {
                for(final String nextNamespaceUri : this.namespaceUris)
                {
                    if(!nextNamespaceUri.trim().equals(""))
                    {
                        connection.add(keyUri, StatisticsEntry.statisticsnamespaceUrisUri,
                                f.createURI(nextNamespaceUri), keyUri);
                    }
                }
            }
            
            // If everything went as planned, we can commit the result
            connection.commit();
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            if(connection != null)
            {
                connection.rollback();
            }
            
            StatisticsEntry.log.error("RepositoryException: " + re.getMessage());
            
            return false;
        }
        finally
        {
            if(connection != null)
            {
                connection.close();
            }
        }
        
        return true;
    }
    
}
