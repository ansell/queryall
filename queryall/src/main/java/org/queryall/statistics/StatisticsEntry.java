package org.queryall.statistics;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Date;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;

import org.queryall.queryutils.HttpUrlQueryRunnable;
import org.queryall.api.BaseQueryAllInterface;
import org.queryall.blacklist.BlacklistController;
import org.queryall.helpers.Constants;
import org.queryall.helpers.Settings;
import org.queryall.helpers.StringUtils;
import org.queryall.helpers.RdfUtils;
import org.queryall.impl.HttpProviderImpl;
import org.queryall.impl.ProjectImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class StatisticsEntry implements BaseQueryAllInterface
{
	private static final Logger log = Logger.getLogger(StatisticsEntry.class
			.getName());

	@SuppressWarnings("unused")
	private static final boolean _INFO = StatisticsEntry.log.isInfoEnabled();
	private static final boolean _DEBUG = StatisticsEntry.log.isDebugEnabled();
	@SuppressWarnings("unused")
	private static final boolean _TRACE = StatisticsEntry.log.isTraceEnabled();

	private static final String defaultNamespace = Settings.getSettings()
			.getNamespaceForStatistics();

	public static final int IMPLEMENTED_STATISTICS_VERSION = 1;
	public static String statisticsNamespace = "";

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
	 * key profileUris successfulproviderUris errorproviderUris configLocations
	 * querytypeUris namespaceUris configVersion readtimeout connecttimeout
	 * userHostAddress userAgent realHostName queryString responseTime
	 * sumLatency sumQueries stdevlatency sumerrors sumerrorlatency
	 * stdeverrorlatency
	 ***/

	static
	{
		StatisticsEntry.statisticsNamespace = Settings.getSettings()
				.getOntologyTermUriPrefix()
				+ Settings.getSettings().getNamespaceForStatistics()
				+ Settings.getSettings().getOntologyTermUriSuffix();

		final ValueFactory f = Constants.valueFactory;

		StatisticsEntry.statisticsTypeUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "Statistics");
		StatisticsEntry.statisticskeyUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "key");

		StatisticsEntry.statisticscurrentdatetimeUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "submissionDate");
		StatisticsEntry.statisticsprofileUrisUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "profileUri");
		StatisticsEntry.statisticssuccessfulproviderUrisUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "successfulProviderUri");
		StatisticsEntry.statisticserrorproviderUrisUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "errorProviderUri");
		StatisticsEntry.statisticsconfigLocationsUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "configLocation");
		StatisticsEntry.statisticsquerytypeUrisUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "querytypeUri");
		StatisticsEntry.statisticsnamespaceUrisUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "namespaceUri");
		StatisticsEntry.statisticsconfigVersionUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "configVersion");
		StatisticsEntry.statisticsreadtimeoutUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "readtimeout");
		StatisticsEntry.statisticsconnecttimeoutUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "connecttimeout");
		StatisticsEntry.statisticsuserHostAddressUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "userHostAddress");
		StatisticsEntry.statisticsuserAgentUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "userAgent");
		StatisticsEntry.statisticsrealHostNameUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "realHostName");
		StatisticsEntry.statisticsqueryStringUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "queryString");
		StatisticsEntry.statisticsresponseTimeUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "responseTime");
		StatisticsEntry.statisticssumLatencyUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "sumLatency");
		StatisticsEntry.statisticssumQueriesUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "sumQueries");
		StatisticsEntry.statisticsstdevlatencyUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "stdevlatency");
		StatisticsEntry.statisticssumerrorsUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "sumerrors");
		StatisticsEntry.statisticssumerrorlatencyUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "sumerrorlatency");
		StatisticsEntry.statisticsstdeverrorlatencyUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "stdeverrorlatency");

		StatisticsEntry.statisticslastServerRestartUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "lastServerRestart");
		StatisticsEntry.statisticsserverSoftwareVersionUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "serverSoftwareVersion");
		StatisticsEntry.statisticsacceptHeaderUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "acceptHeader");
		StatisticsEntry.statisticsrequestedContentTypeUri = f.createURI(
				StatisticsEntry.statisticsNamespace, "requestedContentType");
	}

	/**
	 * @return the statisticsconfigLocationsUri
	 */
	public static URI getStatisticsconfigLocationsUri()
	{
		return statisticsconfigLocationsUri;
	}

	/**
	 * @return the statisticsconfigVersionUri
	 */
	public static URI getStatisticsconfigVersionUri()
	{
		return statisticsconfigVersionUri;
	}

	/**
	 * @return the statisticsconnecttimeoutUri
	 */
	public static URI getStatisticsconnecttimeoutUri()
	{
		return statisticsconnecttimeoutUri;
	}

	/**
	 * @return the statisticscurrentdatetimeUri
	 */
	public static URI getStatisticscurrentdatetimeUri()
	{
		return statisticscurrentdatetimeUri;
	}

	/**
	 * @return the statisticserrorproviderUrisUri
	 */
	public static URI getStatisticserrorproviderUrisUri()
	{
		return statisticserrorproviderUrisUri;
	}

	/**
	 * @return the statisticskeyUri
	 */
	public static URI getStatisticskeyUri()
	{
		return statisticskeyUri;
	}

	/**
	 * @return the statisticsNamespace
	 */
	public static String getStatisticsNamespace()
	{
		return statisticsNamespace;
	}

	/**
	 * @return the statisticsnamespaceUrisUri
	 */
	public static URI getStatisticsnamespaceUrisUri()
	{
		return statisticsnamespaceUrisUri;
	}

	/**
	 * @return the statisticsprofileUrisUri
	 */
	public static URI getStatisticsprofileUrisUri()
	{
		return statisticsprofileUrisUri;
	}

	/**
	 * @return the statisticsqueryStringUri
	 */
	public static URI getStatisticsqueryStringUri()
	{
		return statisticsqueryStringUri;
	}

	/**
	 * @return the statisticsquerytypeUrisUri
	 */
	public static URI getStatisticsquerytypeUrisUri()
	{
		return statisticsquerytypeUrisUri;
	}

	/**
	 * @return the statisticsreadtimeoutUri
	 */
	public static URI getStatisticsreadtimeoutUri()
	{
		return statisticsreadtimeoutUri;
	}

	/**
	 * @return the statisticsrealHostNameUri
	 */
	public static URI getStatisticsrealHostNameUri()
	{
		return statisticsrealHostNameUri;
	}

	/**
	 * @return the statisticsresponseTimeUri
	 */
	public static URI getStatisticsresponseTimeUri()
	{
		return statisticsresponseTimeUri;
	}

	/**
	 * @return the statisticsstdeverrorlatencyUri
	 */
	public static URI getStatisticsstdeverrorlatencyUri()
	{
		return statisticsstdeverrorlatencyUri;
	}

	/**
	 * @return the statisticsstdevlatencyUri
	 */
	public static URI getStatisticsstdevlatencyUri()
	{
		return statisticsstdevlatencyUri;
	}

	/**
	 * @return the statisticssuccessfulproviderUrisUri
	 */
	public static URI getStatisticssuccessfulproviderUrisUri()
	{
		return statisticssuccessfulproviderUrisUri;
	}

	/**
	 * @return the statisticssumerrorlatencyUri
	 */
	public static URI getStatisticssumerrorlatencyUri()
	{
		return statisticssumerrorlatencyUri;
	}

	/**
	 * @return the statisticssumerrorsUri
	 */
	public static URI getStatisticssumerrorsUri()
	{
		return statisticssumerrorsUri;
	}

	/**
	 * @return the statisticssumLatencyUri
	 */
	public static URI getStatisticssumLatencyUri()
	{
		return statisticssumLatencyUri;
	}

	/**
	 * @return the statisticssumQueriesUri
	 */
	public static URI getStatisticssumQueriesUri()
	{
		return statisticssumQueriesUri;
	}

	/**
	 * @return the statisticsTypeUri
	 */
	public static URI getStatisticsTypeUri()
	{
		return statisticsTypeUri;
	}

	/**
	 * @return the statisticsuserAgentUri
	 */
	public static URI getStatisticsuserAgentUri()
	{
		return statisticsuserAgentUri;
	}

	/**
	 * @return the statisticsuserHostAddressUri
	 */
	public static URI getStatisticsuserHostAddressUri()
	{
		return statisticsuserHostAddressUri;
	}

	public static boolean schemaToRdf(Repository myRepository, URI contextUri,
			int modelVersion) throws OpenRDFException
	{
		RepositoryConnection con = myRepository.getConnection();

		final ValueFactory f = Constants.valueFactory;

		try
		{
            con.setAutoCommit(false);

			con.add(statisticsTypeUri, RDF.TYPE, OWL.CLASS, contextUri);

			con.add(statisticskeyUri, RDF.TYPE, OWL.DATATYPEPROPERTY,
					contextUri);

			con.add(statisticsprofileUrisUri, RDF.TYPE, OWL.OBJECTPROPERTY,
					contextUri);
			con.add(statisticsquerytypeUrisUri, RDF.TYPE, OWL.OBJECTPROPERTY,
					contextUri);

			con.add(statisticscurrentdatetimeUri, RDF.TYPE,
					OWL.DATATYPEPROPERTY, contextUri);
			con.add(statisticssuccessfulproviderUrisUri, RDF.TYPE,
					OWL.DATATYPEPROPERTY, contextUri);
			con.add(statisticserrorproviderUrisUri, RDF.TYPE,
					OWL.DATATYPEPROPERTY, contextUri);
			con.add(statisticsconfigLocationsUri, RDF.TYPE,
					OWL.DATATYPEPROPERTY, contextUri);
			con.add(statisticsnamespaceUrisUri, RDF.TYPE, OWL.DATATYPEPROPERTY,
					contextUri);
			con.add(statisticsconfigVersionUri, RDF.TYPE, OWL.DATATYPEPROPERTY,
					contextUri);
			con.add(statisticsreadtimeoutUri, RDF.TYPE, OWL.DATATYPEPROPERTY,
					contextUri);
			con.add(statisticsconnecttimeoutUri, RDF.TYPE,
					OWL.DATATYPEPROPERTY, contextUri);
			con.add(statisticsuserHostAddressUri, RDF.TYPE,
					OWL.DATATYPEPROPERTY, contextUri);
			con.add(statisticsuserAgentUri, RDF.TYPE, OWL.DATATYPEPROPERTY,
					contextUri);
			con.add(statisticsrealHostNameUri, RDF.TYPE, OWL.DATATYPEPROPERTY,
					contextUri);
			con.add(statisticsqueryStringUri, RDF.TYPE, OWL.DATATYPEPROPERTY,
					contextUri);
			con.add(statisticsresponseTimeUri, RDF.TYPE, OWL.DATATYPEPROPERTY,
					contextUri);
			con.add(statisticssumLatencyUri, RDF.TYPE, OWL.DATATYPEPROPERTY,
					contextUri);
			con.add(statisticssumQueriesUri, RDF.TYPE, OWL.DATATYPEPROPERTY,
					contextUri);
			con.add(statisticsstdevlatencyUri, RDF.TYPE, OWL.DATATYPEPROPERTY,
					contextUri);
			con.add(statisticssumerrorsUri, RDF.TYPE, OWL.DATATYPEPROPERTY,
					contextUri);
			con.add(statisticssumerrorlatencyUri, RDF.TYPE,
					OWL.DATATYPEPROPERTY, contextUri);
			con.add(statisticsstdeverrorlatencyUri, RDF.TYPE,
					OWL.DATATYPEPROPERTY, contextUri);
			con.add(statisticslastServerRestartUri, RDF.TYPE,
					OWL.DATATYPEPROPERTY, contextUri);
			con.add(statisticsserverSoftwareVersionUri, RDF.TYPE,
					OWL.DATATYPEPROPERTY, contextUri);
			con.add(statisticsacceptHeaderUri, RDF.TYPE, OWL.DATATYPEPROPERTY,
					contextUri);
			con.add(statisticsrequestedContentTypeUri, RDF.TYPE,
					OWL.DATATYPEPROPERTY, contextUri);

			// If everything went as planned, we can commit the result
			con.commit();

			return true;
		}
		catch(RepositoryException re)
		{
			// Something went wrong during the transaction, so we roll it back

			if(con != null)
				con.rollback();

			log.error("RepositoryException: " + re.getMessage());
		}
		finally
		{
			if(con != null)
				con.close();
		}

		return false;
	}

	public Date currentDate = null;

	private String acceptHeader = "";

	private Collection<String> configLocations = new HashSet<String>();

	private String configVersion = "";

	private int connecttimeout = -1;

	private URI curationStatus = ProjectImpl.getProjectNotCuratedUri();

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
	public StatisticsEntry(String nextKey, Collection<String> nextprofileUris,
			Collection<String> nextsuccessfulproviderUris,
			Collection<String> nexterrorproviderUris,
			Collection<String> nextconfigLocations,
			Collection<String> nextquerytypeUris,
			Collection<String> nextnamespaceUris, String nextconfigVersion,
			int nextreadtimeout, int nextconnecttimeout,
			String nextuserHostAddress, String nextuserAgent,
			String nextrealHostName, String nextqueryString,
			long nextresponseTime, long nextsumLatency, int nextsumQueries,
			double nextstdevlatency, int nextsumerrors,
			long nextsumerrorlatency, double nextstdeverrorlatency,
			String nextlastServerRestart, String nextserverSoftwareVersion,
			String nextacceptHeader, String nextrequestedContentType)
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

	public void addUnrecognisedStatement(Statement unrecognisedStatement)
	{
		unrecognisedStatements.add(unrecognisedStatement);
	}

	public boolean equals(Object obj)
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
		StatisticsEntry other = (StatisticsEntry) obj;
		if(configLocations == null)
		{
			if(other.configLocations != null)
			{
				return false;
			}
		}
		else if(!configLocations.equals(other.configLocations))
		{
			return false;
		}
		if(configVersion == null)
		{
			if(other.configVersion != null)
			{
				return false;
			}
		}
		else if(!configVersion.equals(other.configVersion))
		{
			return false;
		}
		if(connecttimeout != other.connecttimeout)
		{
			return false;
		}
		if(errorproviderUris == null)
		{
			if(other.errorproviderUris != null)
			{
				return false;
			}
		}
		else if(!errorproviderUris.equals(other.errorproviderUris))
		{
			return false;
		}
		if(key == null)
		{
			if(other.getKey() != null)
			{
				return false;
			}
		}
		else if(!key.equals(other.getKey()))
		{
			return false;
		}
		if(namespaceUris == null)
		{
			if(other.namespaceUris != null)
			{
				return false;
			}
		}
		else if(!namespaceUris.equals(other.namespaceUris))
		{
			return false;
		}
		if(profileUris == null)
		{
			if(other.profileUris != null)
			{
				return false;
			}
		}
		else if(!profileUris.equals(other.profileUris))
		{
			return false;
		}
		if(queryString == null)
		{
			if(other.queryString != null)
			{
				return false;
			}
		}
		else if(!queryString.equals(other.queryString))
		{
			return false;
		}
		if(querytypeUris == null)
		{
			if(other.querytypeUris != null)
			{
				return false;
			}
		}
		else if(!querytypeUris.equals(other.querytypeUris))
		{
			return false;
		}
		if(readtimeout != other.readtimeout)
		{
			return false;
		}
		if(realHostName == null)
		{
			if(other.realHostName != null)
			{
				return false;
			}
		}
		else if(!realHostName.equals(other.realHostName))
		{
			return false;
		}
		if(responseTime != other.responseTime)
		{
			return false;
		}
		if(Double.doubleToLongBits(stdeverrorlatency) != Double
				.doubleToLongBits(other.stdeverrorlatency))
		{
			return false;
		}
		if(Double.doubleToLongBits(stdevlatency) != Double
				.doubleToLongBits(other.stdevlatency))
		{
			return false;
		}
		if(successfulproviderUris == null)
		{
			if(other.successfulproviderUris != null)
			{
				return false;
			}
		}
		else if(!successfulproviderUris.equals(other.successfulproviderUris))
		{
			return false;
		}
		if(sumLatency != other.sumLatency)
		{
			return false;
		}
		if(sumQueries != other.sumQueries)
		{
			return false;
		}
		if(sumerrorlatency != other.sumerrorlatency)
		{
			return false;
		}
		if(sumerrors != other.sumerrors)
		{
			return false;
		}
		if(userAgent == null)
		{
			if(other.userAgent != null)
			{
				return false;
			}
		}
		else if(!userAgent.equals(other.userAgent))
		{
			return false;
		}
		if(userHostAddress == null)
		{
			if(other.userHostAddress != null)
			{
				return false;
			}
		}
		else if(!userHostAddress.equals(other.userHostAddress))
		{
			return false;
		}
		return true;
	}

	/**
	 * @return
	 * @throws OpenRDFException
	 */
	public HttpUrlQueryRunnable generateThread(Settings localSettings,
			BlacklistController localBlacklistController, int modelVersion)
			throws OpenRDFException
	{
		if(localSettings.getURIPropertyFromConfig("statisticsServerMethod",
				HttpProviderImpl.getProviderHttpPostSparqlUri()).equals(
				HttpProviderImpl.getProviderHttpPostSparqlUri()))
		{
			final Repository myRepository = new SailRepository(
					new MemoryStore());
			myRepository.initialize();

			@SuppressWarnings("unused")
			final boolean rdfOkay = this.toRdf(myRepository, this.getKey(),
					modelVersion);

			final RDFFormat writerFormat = Rio
					.getWriterFormatForMIMEType("text/plain");

			final StringWriter insertTriples = new StringWriter();

			RdfUtils.toWriter(myRepository, insertTriples, writerFormat);

			final String insertTriplesContent = insertTriples.toString();

			// log.info("StatisticsEntry: insertTriplesContent="+insertTriplesContent);

			String sparqlInsertQuery = "define sql:log-enable 2 INSERT ";

			if(localSettings.getBooleanPropertyFromConfig(
					"statisticsServerUseGraphUri", true))
			{
				sparqlInsertQuery += " INTO GRAPH <"
						+ localSettings.getStringPropertyFromConfig(
								"statisticsServerGraphUri", "") + "> ";
			}

			sparqlInsertQuery += " { " + insertTriplesContent + " } ";

			if(StatisticsEntry._DEBUG)
			{
				StatisticsEntry.log.debug("StatisticsEntry: sparqlInsertQuery="
						+ sparqlInsertQuery);
			}

			return new HttpUrlQueryRunnable(
					localSettings.getStringPropertyFromConfig(
							"statisticsServerMethod", ""),
					localSettings.getStringPropertyFromConfig(
							"statisticsServerUrl", ""), sparqlInsertQuery,
					"*/*", localSettings.getStringPropertyFromConfig(
							"assumedRequestContentType", ""), localSettings,
					localBlacklistController);
		}
		else if(localSettings.getURIPropertyFromConfig(
				"statisticsServerMethod", HttpProviderImpl.getProviderHttpPostUrlUri()).equals(
				HttpProviderImpl.getProviderHttpPostUrlUri()))
		{
			final String postInformation = this.toPostArray();

			if(StatisticsEntry._DEBUG)
			{
				StatisticsEntry.log.debug("StatisticsEntry: postInformation="
						+ postInformation);
			}

			return new HttpUrlQueryRunnable(
					localSettings.getStringPropertyFromConfig(
							"statisticsServerMethod", ""),
					localSettings.getStringPropertyFromConfig(
							"statisticsServerUrl", ""), postInformation, "*/*",
					localSettings.getStringPropertyFromConfig(
							"assumedRequestContentType", ""), localSettings,
					localBlacklistController);
		}
		else
		{
			throw new RuntimeException(
					"StatisticsEntry.generateThread: Unknown localSettings.getStringPropertyFromConfig(\"statisticsServerMethod\")="
							+ localSettings.getStringPropertyFromConfig(
									"statisticsServerMethod", ""));
		}
	}

	/**
	 * @return the configLocations
	 */
	public Collection<String> getConfigLocations()
	{
		return configLocations;
	}

	/**
	 * @return the configVersion
	 */
	public String getConfigVersion()
	{
		return configVersion;
	}

	/**
	 * @return the connecttimeout
	 */
	public int getConnecttimeout()
	{
		return connecttimeout;
	}

	public URI getCurationStatus()
	{
		return curationStatus;
	}

	/**
	 * @return the namespace used to represent objects of this type by default
	 */

	public String getDefaultNamespace()
	{
		return defaultNamespace;
	}

	/**
	 * @return the URI used for the rdf Type of these elements
	 */

	public URI getElementType()
	{
		return statisticsTypeUri;
	}

	/**
	 * @return the errorproviderUris
	 */
	public Collection<String> getErrorproviderUris()
	{
		return errorproviderUris;
	}

	/**
	 * @return the key
	 */

	public URI getKey()
	{
		return key;
	}

	/**
	 * @return the namespaceUris
	 */
	public Collection<String> getNamespaceUris()
	{
		return namespaceUris;
	}

	/**
	 * @return the profileUris
	 */
	public Collection<String> getProfileUris()
	{
		return profileUris;
	}

	/**
	 * @return the queryString
	 */
	public String getQueryString()
	{
		return queryString;
	}

	/**
	 * @return the querytypeUris
	 */
	public Collection<String> getQuerytypeUris()
	{
		return querytypeUris;
	}

	/**
	 * @return the readtimeout
	 */
	public int getReadtimeout()
	{
		return readtimeout;
	}

	/**
	 * @return the realHostName
	 */
	public String getRealHostName()
	{
		return realHostName;
	}

	/**
	 * @return the responseTime
	 */
	public long getResponseTime()
	{
		return responseTime;
	}

	/**
	 * @return the stdeverrorlatency
	 */
	public double getStdeverrorlatency()
	{
		return stdeverrorlatency;
	}

	/**
	 * @return the stdevlatency
	 */
	public double getStdevlatency()
	{
		return stdevlatency;
	}

	/**
	 * @return the successfulproviderUris
	 */
	public Collection<String> getSuccessfulproviderUris()
	{
		return successfulproviderUris;
	}

	/**
	 * @return the sumerrorlatency
	 */
	public long getSumerrorlatency()
	{
		return sumerrorlatency;
	}

	/**
	 * @return the sumerrors
	 */
	public int getSumerrors()
	{
		return sumerrors;
	}

	/**
	 * @return the sumLatency
	 */
	public long getSumLatency()
	{
		return sumLatency;
	}

	/**
	 * @return the sumQueries
	 */
	public int getSumQueries()
	{
		return sumQueries;
	}

	public String getTitle()
	{
		return null;
	}

	public Collection<Statement> getUnrecognisedStatements()
	{
		return unrecognisedStatements;
	}

	/**
	 * @return the userAgent
	 */
	public String getUserAgent()
	{
		return userAgent;
	}

	/**
	 * @return the userHostAddress
	 */
	public String getUserHostAddress()
	{
		return userHostAddress;
	}

	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((configLocations == null) ? 0 : configLocations.hashCode());
		result = prime * result
				+ ((configVersion == null) ? 0 : configVersion.hashCode());
		result = prime * result + connecttimeout;
		result = prime
				* result
				+ ((errorproviderUris == null) ? 0 : errorproviderUris
						.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result
				+ ((namespaceUris == null) ? 0 : namespaceUris.hashCode());
		result = prime * result
				+ ((profileUris == null) ? 0 : profileUris.hashCode());
		result = prime * result
				+ ((queryString == null) ? 0 : queryString.hashCode());
		result = prime * result
				+ ((querytypeUris == null) ? 0 : querytypeUris.hashCode());
		result = prime * result + readtimeout;
		result = prime * result
				+ ((realHostName == null) ? 0 : realHostName.hashCode());
		result = prime * result + (int) (responseTime ^ (responseTime >>> 32));
		long temp;
		temp = Double.doubleToLongBits(stdeverrorlatency);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(stdevlatency);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime
				* result
				+ ((successfulproviderUris == null) ? 0
						: successfulproviderUris.hashCode());
		result = prime * result + (int) (sumLatency ^ (sumLatency >>> 32));
		result = prime * result + sumQueries;
		result = prime * result
				+ (int) (sumerrorlatency ^ (sumerrorlatency >>> 32));
		result = prime * result + sumerrors;
		result = prime * result
				+ ((userAgent == null) ? 0 : userAgent.hashCode());
		result = prime * result
				+ ((userHostAddress == null) ? 0 : userHostAddress.hashCode());
		return result;
	}

	/**
	 * @param configLocations
	 *            the configLocations to set
	 */
	public void setConfigLocations(Collection<String> configLocations)
	{
		this.configLocations = configLocations;
	}

	/**
	 * @param configVersion
	 *            the configVersion to set
	 */
	public void setConfigVersion(String configVersion)
	{
		this.configVersion = configVersion;
	}

	/**
	 * @param connecttimeout
	 *            the connecttimeout to set
	 */
	public void setConnecttimeout(int connecttimeout)
	{
		this.connecttimeout = connecttimeout;
	}

	public void setCurationStatus(URI curationStatus)
	{
		this.curationStatus = curationStatus;
	}

	/**
	 * @param errorproviderUris
	 *            the errorproviderUris to set
	 */
	public void setErrorproviderUris(Collection<String> errorproviderUris)
	{
		this.errorproviderUris = errorproviderUris;
	}

	/**
	 * @param key
	 *            the key to set
	 */

	public void setKey(String nextKey)
	{
		this.setKey(StringUtils.createURI(nextKey));
	}

	public void setKey(URI nextKey)
	{
		this.key = nextKey;
	}

	/**
	 * @param namespaceUris
	 *            the namespaceUris to set
	 */
	public void setNamespaceUris(Collection<String> namespaceUris)
	{
		this.namespaceUris = namespaceUris;
	}

	/**
	 * @param profileUris
	 *            the profileUris to set
	 */
	public void setProfileUris(Collection<String> profileUris)
	{
		this.profileUris = profileUris;
	}

	/**
	 * @param queryString
	 *            the queryString to set
	 */
	public void setQueryString(String queryString)
	{
		this.queryString = queryString;
	}

	/**
	 * @param querytypeUris
	 *            the querytypeUris to set
	 */
	public void setQuerytypeUris(Collection<String> querytypeUris)
	{
		this.querytypeUris = querytypeUris;
	}

	/**
	 * @param readtimeout
	 *            the readtimeout to set
	 */
	public void setReadtimeout(int readtimeout)
	{
		this.readtimeout = readtimeout;
	}

	/**
	 * @param realHostName
	 *            the realHostName to set
	 */
	public void setRealHostName(String realHostName)
	{
		this.realHostName = realHostName;
	}

	/**
	 * @param responseTime
	 *            the responseTime to set
	 */
	public void setResponseTime(long responseTime)
	{
		this.responseTime = responseTime;
	}

	/**
	 * @param stdeverrorlatency
	 *            the stdeverrorlatency to set
	 */
	public void setStdeverrorlatency(double stdeverrorlatency)
	{
		this.stdeverrorlatency = stdeverrorlatency;
	}

	/**
	 * @param stdevlatency
	 *            the stdevlatency to set
	 */
	public void setStdevlatency(double stdevlatency)
	{
		this.stdevlatency = stdevlatency;
	}

	/**
	 * @param successfulproviderUris
	 *            the successfulproviderUris to set
	 */
	public void setSuccessfulproviderUris(
			Collection<String> successfulproviderUris)
	{
		this.successfulproviderUris = successfulproviderUris;
	}

	/**
	 * @param sumerrorlatency
	 *            the sumerrorlatency to set
	 */
	public void setSumerrorlatency(long sumerrorlatency)
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
	public void setSumerrors(int sumerrors)
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
	public void setSumLatency(long sumLatency)
	{
		this.sumLatency = sumLatency;
	}

	/**
	 * @param sumQueries
	 *            the sumQueries to set
	 */
	public void setSumQueries(int sumQueries)
	{
		this.sumQueries = sumQueries;
	}

	public void setTitle(String title)
	{
	}

	/**
	 * @param userAgent
	 *            the userAgent to set
	 */
	public void setUserAgent(String userAgent)
	{
		this.userAgent = userAgent;
	}

	/**
	 * @param userHostAddress
	 *            the userHostAddress to set
	 */
	public void setUserHostAddress(String userHostAddress)
	{
		this.userHostAddress = userHostAddress;
	}

	public String toHtml()
	{
		StringBuilder sb = new StringBuilder();

		@SuppressWarnings("unused")
		String prefix = "statistics_";

		return sb.toString();
	}

	public String toHtmlFormBody()
	{
		StringBuilder sb = new StringBuilder();

		@SuppressWarnings("unused")
		String prefix = "statistics_";

		return sb.toString();
	}

	/**
	 * @return
	 */
	public String toPostArray()
	{
		String result = "";

		result += "key="
				+ StringUtils.percentEncode(this.getKey().stringValue()) + "&";
		result += "profileUris="
				+ StringUtils.percentEncode(StringUtils.joinStringCollection(
						this.profileUris, ",")) + "&";
		result += "successfulproviderUris="
				+ StringUtils.percentEncode(StringUtils.joinStringCollection(
						this.successfulproviderUris, ",")) + "&";
		result += "errorproviderUris="
				+ StringUtils.percentEncode(StringUtils.joinStringCollection(
						this.errorproviderUris, ",")) + "&";
		result += "configLocations="
				+ StringUtils.percentEncode(StringUtils.joinStringCollection(
						this.configLocations, ",")) + "&";
		result += "querytypeUris="
				+ StringUtils.percentEncode(StringUtils.joinStringCollection(
						this.querytypeUris, ",")) + "&";
		result += "namespaceUris="
				+ StringUtils.percentEncode(StringUtils.joinStringCollection(
						this.namespaceUris, ",")) + "&";
		result += "configVersion="
				+ StringUtils.percentEncode(this.configVersion) + "&";
		result += "readtimeout="
				+ StringUtils.percentEncode(this.readtimeout + "") + "&";
		result += "connecttimeout="
				+ StringUtils.percentEncode(this.connecttimeout + "") + "&";
		result += "userHostAddress="
				+ StringUtils.percentEncode(this.userHostAddress) + "&";
		result += "userAgent=" + StringUtils.percentEncode(this.userAgent)
				+ "&";
		result += "realHostName="
				+ StringUtils.percentEncode(this.realHostName) + "&";
		result += "queryString=" + StringUtils.percentEncode(this.queryString)
				+ "&";
		result += "responseTime="
				+ StringUtils.percentEncode(this.responseTime + "") + "&";
		result += "sumLatency="
				+ StringUtils.percentEncode(this.sumLatency + "") + "&";
		result += "sumQueries="
				+ StringUtils.percentEncode(this.sumQueries + "") + "&";
		result += "stdevlatency="
				+ StringUtils.percentEncode(this.stdevlatency + "") + "&";
		result += "sumerrors=" + StringUtils.percentEncode(this.sumerrors + "")
				+ "&";
		result += "sumerrorlatency="
				+ StringUtils.percentEncode(this.sumerrorlatency + "") + "&";
		result += "stdeverrorlatency="
				+ StringUtils.percentEncode(this.stdeverrorlatency + "") + "&";
		result += "lastServerRestart="
				+ StringUtils.percentEncode(this.lastServerRestart + "") + "&";
		result += "serverSoftwareVersion="
				+ StringUtils.percentEncode(this.serverSoftwareVersion + "")
				+ "&";
		result += "acceptHeader="
				+ StringUtils.percentEncode(this.acceptHeader + "") + "&";
		result += "requestedContentType="
				+ StringUtils.percentEncode(this.requestedContentType + "")
				+ "&";
		return result;
	}

	public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion)
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
				StatisticsEntry.log
						.error("StatisticsEntry.toRdf: keyToUse was empty");

				return false;
			}

			final URI keyUri = keyToUse;

			if(currentDate == null)
			{
				currentDate = new Date();
			}

			final String currentDateString = Constants.ISO8601UTC().format(
					currentDate);

			final Literal currentDateLiteral = f.createLiteral(
					currentDateString, XMLSchema.DATETIME);

			final Literal configVersionLiteral = f
					.createLiteral(this.configVersion);
			final Literal readtimeoutLiteral = f
					.createLiteral(this.readtimeout);
			final Literal connecttimeoutLiteral = f
					.createLiteral(this.connecttimeout);
			final Literal userHostAddressLiteral = f
					.createLiteral(this.userHostAddress);
			final Literal userAgentLiteral = f.createLiteral(this.userAgent);
			final Literal realHostNameLiteral = f
					.createLiteral(this.realHostName);
			final Literal queryStringLiteral = f
					.createLiteral(this.queryString);
			final Literal responseTimeLiteral = f
					.createLiteral(this.responseTime);
			final Literal sumLatencyLiteral = f.createLiteral(this.sumLatency);
			final Literal sumQueriesLiteral = f.createLiteral(this.sumQueries);
			final Literal stdevlatencyLiteral = f
					.createLiteral(this.stdevlatency);
			final Literal sumerrorsLiteral = f.createLiteral(this.sumerrors);
			final Literal sumerrorlatencyLiteral = f
					.createLiteral(this.sumerrorlatency);
			final Literal stdeverrorlatencyLiteral = f
					.createLiteral(this.stdeverrorlatency);

			final Literal lastServerRestartLiteral = f
					.createLiteral(this.lastServerRestart);
			final Literal serverSoftwareVersionLiteral = f
					.createLiteral(this.serverSoftwareVersion);
			final Literal requestedContentTypeLiteral = f
					.createLiteral(this.requestedContentType);
			final Literal acceptHeaderLiteral = f
					.createLiteral(this.acceptHeader);

			connection.setAutoCommit(false);

			connection.add(keyUri, RDF.TYPE, StatisticsEntry.statisticsTypeUri,
					keyUri);
			connection.add(keyUri,
					StatisticsEntry.statisticscurrentdatetimeUri,
					currentDateLiteral, keyUri);
			connection.add(keyUri, StatisticsEntry.statisticsconfigVersionUri,
					configVersionLiteral, keyUri);
			connection.add(keyUri, StatisticsEntry.statisticsreadtimeoutUri,
					readtimeoutLiteral, keyUri);
			connection.add(keyUri, StatisticsEntry.statisticsconnecttimeoutUri,
					connecttimeoutLiteral, keyUri);
			connection.add(keyUri,
					StatisticsEntry.statisticsuserHostAddressUri,
					userHostAddressLiteral, keyUri);
			connection.add(keyUri, StatisticsEntry.statisticsuserAgentUri,
					userAgentLiteral, keyUri);
			connection.add(keyUri, StatisticsEntry.statisticsrealHostNameUri,
					realHostNameLiteral, keyUri);
			connection.add(keyUri, StatisticsEntry.statisticsqueryStringUri,
					queryStringLiteral, keyUri);
			connection.add(keyUri, StatisticsEntry.statisticsresponseTimeUri,
					responseTimeLiteral, keyUri);
			connection.add(keyUri, StatisticsEntry.statisticssumLatencyUri,
					sumLatencyLiteral, keyUri);
			connection.add(keyUri, StatisticsEntry.statisticssumQueriesUri,
					sumQueriesLiteral, keyUri);
			connection.add(keyUri, StatisticsEntry.statisticsstdevlatencyUri,
					stdevlatencyLiteral, keyUri);
			connection.add(keyUri, StatisticsEntry.statisticssumerrorsUri,
					sumerrorsLiteral, keyUri);
			connection.add(keyUri,
					StatisticsEntry.statisticssumerrorlatencyUri,
					sumerrorlatencyLiteral, keyUri);
			connection.add(keyUri,
					StatisticsEntry.statisticsstdeverrorlatencyUri,
					stdeverrorlatencyLiteral, keyUri);
			connection.add(keyUri,
					StatisticsEntry.statisticslastServerRestartUri,
					lastServerRestartLiteral, keyUri);
			connection.add(keyUri,
					StatisticsEntry.statisticsserverSoftwareVersionUri,
					serverSoftwareVersionLiteral, keyUri);
			connection.add(keyUri, StatisticsEntry.statisticsacceptHeaderUri,
					acceptHeaderLiteral, keyUri);
			connection.add(keyUri,
					StatisticsEntry.statisticsrequestedContentTypeUri,
					requestedContentTypeLiteral, keyUri);

			if(this.profileUris != null)
			{
				for (final String nextProfileUri : this.profileUris)
				{
					if(!nextProfileUri.trim().equals(""))
					{
						connection.add(keyUri,
								StatisticsEntry.statisticsprofileUrisUri,
								f.createURI(nextProfileUri), keyUri);
					}
				}
			}

			if(this.successfulproviderUris != null)
			{
				for (final String nextSuccessfulProvidersUri : this.successfulproviderUris)
				{
					if(!nextSuccessfulProvidersUri.trim().equals(""))
					{
						connection
								.add(keyUri,
										StatisticsEntry.statisticssuccessfulproviderUrisUri,
										f.createLiteral(nextSuccessfulProvidersUri),
										keyUri);
					}
				}
			}

			if(this.errorproviderUris != null)
			{
				for (final String nextErrorProvidersUri : this.errorproviderUris)
				{
					if(!nextErrorProvidersUri.trim().equals(""))
					{
						connection.add(keyUri,
								StatisticsEntry.statisticserrorproviderUrisUri,
								f.createLiteral(nextErrorProvidersUri), keyUri);
					}
				}
			}

			if(this.configLocations != null)
			{
				for (final String nextConfigLocation : this.configLocations)
				{
					if(!nextConfigLocation.trim().equals(""))
					{
						connection.add(keyUri,
								StatisticsEntry.statisticsconfigLocationsUri,
								f.createLiteral(nextConfigLocation), keyUri);
					}
				}
			}

			if(this.querytypeUris != null)
			{
				for (final String nextQuerytypeUri : this.querytypeUris)
				{
					if(!nextQuerytypeUri.trim().equals(""))
					{
						connection.add(keyUri,
								StatisticsEntry.statisticsquerytypeUrisUri,
								f.createURI(nextQuerytypeUri), keyUri);
					}
				}
			}

			if(this.namespaceUris != null)
			{
				for (final String nextNamespaceUri : this.namespaceUris)
				{
					if(!nextNamespaceUri.trim().equals(""))
					{
						connection.add(keyUri,
								StatisticsEntry.statisticsnamespaceUrisUri,
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

			StatisticsEntry.log
					.error("RepositoryException: " + re.getMessage());

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
