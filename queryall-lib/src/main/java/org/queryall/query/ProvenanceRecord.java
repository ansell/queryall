package org.queryall.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
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
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.blacklist.BlacklistController;
import org.queryall.impl.project.ProjectImpl;
import org.queryall.impl.provider.HttpProviderImpl;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProvenanceRecord implements BaseQueryAllInterface
{
    private static final Logger log = LoggerFactory.getLogger(ProvenanceRecord.class);
    private static final boolean _TRACE = ProvenanceRecord.log.isTraceEnabled();
    private static final boolean _DEBUG = ProvenanceRecord.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = ProvenanceRecord.log.isInfoEnabled();
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(ProvenanceRecord.provenanceTypeUri, RDF.TYPE, OWL.CLASS, contextUri);
            
            con.add(ProvenanceRecord.provenanceRecordDateUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            
            con.add(ProvenanceRecord.provenanceElementKeyUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProvenanceRecord.provenanceElementTypeUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(ProvenanceRecord.provenanceHasAuthorOpenIDUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            
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
            
            ProvenanceRecord.log.error("RepositoryException: " + re.getMessage());
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
    
    public Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    private URI key;
    public String recordElementType = "";
    public String recordElementKey = "";
    public String hasAuthorOpenID = "";
    public Date recordDate = null;
    
    private URI curationStatus = ProjectImpl.getProjectNotCuratedUri();
    public static URI provenanceTypeUri;
    public static URI provenanceElementTypeUri;
    public static URI provenanceElementKeyUri;
    public static URI provenanceHasAuthorOpenIDUri;
    
    public static URI provenanceRecordDateUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.PROVENANCE.getBaseURI();
        
        ProvenanceRecord.provenanceTypeUri = f.createURI(baseUri, "ProvenanceRecord");
        ProvenanceRecord.provenanceHasAuthorOpenIDUri = f.createURI(baseUri, "hasAuthorOpenID");
        ProvenanceRecord.provenanceElementTypeUri = f.createURI(baseUri, "elementType");
        ProvenanceRecord.provenanceElementKeyUri = f.createURI(baseUri, "elementKey");
        ProvenanceRecord.provenanceRecordDateUri = f.createURI(baseUri, "recordDate");
    }
    
    public static Map<URI, ProvenanceRecord> fetchProvenanceForElementKey(final String hostToUse,
            final String nextElementKey, final int modelVersion) throws InterruptedException
    {
        final QueryBundle nextQueryBundle = new QueryBundle();
        
        final HttpProviderImpl dummyProvider = new HttpProviderImpl();
        
        final Collection<String> endpointUrls = new HashSet<String>();
        
        endpointUrls.add(hostToUse + "provenancebykey/" + StringUtils.percentEncode(nextElementKey));
        
        dummyProvider.setEndpointUrls(endpointUrls);
        
        nextQueryBundle.setQueryEndpoint(hostToUse + "provenancebykey/" + StringUtils.percentEncode(nextElementKey));
        
        final Collection<QueryBundle> queryBundles = new HashSet<QueryBundle>();
        
        queryBundles.add(nextQueryBundle);
        
        // TODO: remove calls to Settings.getSettings() and
        // BlacklistController.getDefaultController() here
        final RdfFetchController fetchController =
                new RdfFetchController(Settings.getSettings(), BlacklistController.getDefaultController(), queryBundles);
        
        try
        {
            fetchController.fetchRdfForQueries();
        }
        catch(final InterruptedException ie)
        {
            ProvenanceRecord.log.error("ProvenanceRecord: interrupted exception", ie);
            throw ie;
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
                    
                    if(ProvenanceRecord.log.isDebugEnabled())
                    {
                        ProvenanceRecord.log
                                .debug("ProvenanceRecord.fetchProvenanceForElementKey: nextReaderFormat for returnedContentType="
                                        + nextResult.getReturnedContentType() + " nextReaderFormat=" + nextReaderFormat);
                    }
                    
                    if(nextReaderFormat == null)
                    {
                        nextReaderFormat =
                                Rio.getParserFormatForMIMEType(Settings.getSettings().getStringProperty(
                                        "assumedResponseContentType", Constants.APPLICATION_RDF_XML));
                        
                        if(nextReaderFormat == null)
                        {
                            ProvenanceRecord.log
                                    .error("ProvenanceRecord.fetchProvenanceForElementKey: Not attempting to parse result because Settings.getStringPropertyFromConfig(\"assumedResponseContentType\") isn't supported by Rio and the returned content type wasn't either nextResult.returnedMIMEType="
                                            + nextResult.getReturnedMIMEType()
                                            + " Settings.getStringPropertyFromConfig(\"assumedResponseContentType\")="
                                            + Settings.getSettings()
                                                    .getStringProperty("assumedResponseContentType", ""));
                            continue;
                        }
                        else
                        {
                            ProvenanceRecord.log
                                    .warn("ProvenanceRecord.fetchProvenanceForElementKey: readerFormat NOT matched for returnedMIMEType="
                                            + nextResult.getReturnedMIMEType()
                                            + " using configured preferred content type as fallback Settings.getStringPropertyFromConfig(\"assumedResponseContentType\")="
                                            + Settings.getSettings()
                                                    .getStringProperty("assumedResponseContentType", ""));
                        }
                    }
                    else if(ProvenanceRecord.log.isDebugEnabled())
                    {
                        ProvenanceRecord.log
                                .debug("ProvenanceRecord.fetchProvenanceForElementKey: readerFormat matched for returnedMIMEType="
                                        + nextResult.getReturnedMIMEType());
                    }
                    
                    if(nextResult.getNormalisedResult().length() > 0)
                    {
                        myRepositoryConnection.add(
                                new java.io.StringReader(nextResult.getNormalisedResult()),
                                Settings.getSettings().getDefaultHostAddress() + "provenancebykey/"
                                        + StringUtils.percentEncode(nextElementKey), nextReaderFormat);
                    }
                }
                catch(final org.openrdf.rio.RDFParseException rdfpe)
                {
                    ProvenanceRecord.log.error("ProvenanceRecord.fetchProvenanceForElementKey: RDFParseException",
                            rdfpe);
                }
                catch(final org.openrdf.repository.RepositoryException re)
                {
                    ProvenanceRecord.log.error(
                            "ProvenanceRecord.fetchProvenanceForElementKey: RepositoryException inner", re);
                }
                catch(final java.io.IOException ioe)
                {
                    ProvenanceRecord.log.error("ProvenanceRecord.fetchProvenanceForElementKey: IOException", ioe);
                }
            } // end for(RdfFetcherQUeryRunnable nextResult : rdfResults)
        }
        catch(final org.openrdf.repository.RepositoryException re)
        {
            ProvenanceRecord.log.error("ProvenanceRecord.fetchProvenanceForElementKey: RepositoryException outer", re);
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
                ProvenanceRecord.log.error(
                        "ProvenanceRecord.fetchProvenanceForElementKey: failed to close repository connection", re2);
            }
        }
        
        Map<URI, ProvenanceRecord> results = null;
        
        try
        {
            results = ProvenanceRecord.getProvenanceRecordsFromRepository(myRepository, modelVersion);
        }
        catch(final org.openrdf.repository.RepositoryException re)
        {
            ProvenanceRecord.log
                    .error("ProvenanceRecord.fetchProvenanceForElementKey: failed to get records due to a repository exception",
                            re);
        }
        
        return results;
    }
    
    public static Map<URI, ProvenanceRecord> getProvenanceRecordsFromRepository(final Repository myRepository,
            final int modelVersion) throws org.openrdf.repository.RepositoryException
    {
        final Map<URI, ProvenanceRecord> results = new ConcurrentHashMap<URI, ProvenanceRecord>();
        
        final URI provenanceTypeUri = ProvenanceRecord.provenanceTypeUri;
        
        try
        {
            final RepositoryConnection con = myRepository.getConnection();
            
            for(final Statement nextProvider : con.getStatements(null, RDF.TYPE, provenanceTypeUri, true).asList())
            {
                final URI nextSubjectUri = (URI)nextProvider.getSubject();
                results.put(nextSubjectUri,
                        new ProvenanceRecord(con.getStatements(nextSubjectUri, (URI)null, (Value)null, true).asList(),
                                nextSubjectUri, Settings.CONFIG_API_VERSION));
            }
        }
        catch(final OpenRDFException e)
        {
            // handle exception
            ProvenanceRecord.log.error("getProvenanceRecordsFromRepository.:", e);
        }
        
        return results;
    }
    
    // keyToUse is the URI of the next instance that can be found in myRepository
    // returns null if the URI is not in the repository or the information is not enough to create a
    // minimal query configuration
    public ProvenanceRecord(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final Collection<Statement> tempUnrecognisedStatements = new HashSet<Statement>();
        
        for(final Statement nextStatement : inputStatements)
        {
            if(ProvenanceRecord._DEBUG)
            {
                ProvenanceRecord.log.debug("ProvenanceRecord: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(ProvenanceRecord.provenanceTypeUri))
            {
                if(ProvenanceRecord._TRACE)
                {
                    ProvenanceRecord.log.trace("ProvenanceRecord: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProvenanceRecord.provenanceHasAuthorOpenIDUri))
            {
                this.hasAuthorOpenID = nextStatement.getObject().stringValue();
            }
            else if(nextStatement.getPredicate().equals(ProvenanceRecord.provenanceElementTypeUri))
            {
                this.recordElementType = nextStatement.getObject().stringValue();
            }
            else if(nextStatement.getPredicate().equals(ProvenanceRecord.provenanceElementKeyUri))
            {
                this.recordElementKey = nextStatement.getObject().stringValue();
            }
            else if(nextStatement.getPredicate().equals(ProvenanceRecord.provenanceRecordDateUri))
            {
                try
                {
                    this.recordDate = RdfUtils.getDateTimeFromValue(nextStatement.getObject());
                }
                catch(final java.text.ParseException pe)
                {
                    ProvenanceRecord.log.error("ProvenanceRecord.fromRdf: could not parse date: value="
                            + nextStatement.getObject().stringValue(), pe);
                }
            }
            else
            {
                tempUnrecognisedStatements.add(nextStatement);
            }
        }
        
        this.unrecognisedStatements = tempUnrecognisedStatements;
        
        if(ProvenanceRecord._DEBUG)
        {
            ProvenanceRecord.log.debug("ProvenanceRecord.fromRdf: would have returned... keyToUse=" + keyToUse
                    + " result=" + this.toString());
        }
    }
    
    @Override
    public void addUnrecognisedStatement(final Statement unrecognisedStatement)
    {
        this.unrecognisedStatements.add(unrecognisedStatement);
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
        return QueryAllNamespaces.PROVENANCE;
    }
    
    /**
     * @return the URI used for the Key of the element referred to by this provenance record
     */
    public String getElementKey()
    {
        return this.recordElementKey;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Collection<URI> getElementTypes()
    {
        final Collection<URI> results = new ArrayList<URI>(1);
        
        results.add(ProvenanceRecord.provenanceTypeUri);
        
        return results;
    }
    
    /**
     * @return the key
     */
    @Override
    public URI getKey()
    {
        return this.key;
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
    
    public boolean relatedToElementTypes(final Collection<URI> typesToCheck)
    {
        if(typesToCheck == null || this.recordElementType == null || this.recordElementType.trim().equals(""))
        {
            return false;
        }
        
        if(ProvenanceRecord._DEBUG)
        {
            ProvenanceRecord.log.debug("ProvenanceRecord.relatedToElementTypes: this.getKey()=" + this.getKey());
        }
        
        for(final URI nextTypeToCheck : typesToCheck)
        {
            if(this.recordElementType.equals(nextTypeToCheck))
            {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public void setCurationStatus(final URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    /**
     * @param recordElementKey
     *            the elementKey to set
     */
    public void setElementKey(final String nextElementKey)
    {
        this.recordElementKey = nextElementKey;
    }
    
    /**
     * @param recordElementType
     *            the elementType to set
     */
    public void setElementType(final String nextElementType)
    {
        this.recordElementType = nextElementType;
    }
    
    public void setHasAuthorOpenID(final String nextHasAuthorOpenID)
    {
        this.hasAuthorOpenID = nextHasAuthorOpenID;
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
     * @param nextDate
     *            the date the record was created at
     */
    public void setRecordDate(final Date nextDate)
    {
        this.recordDate = nextDate;
    }
    
    @Override
    public void setTitle(final String title)
    {
    }
    
    @Override
    public String toHtml()
    {
        return "<span>key:</span>" + StringUtils.xmlEncodeString(this.getKey().stringValue());
    }
    
    public String toHtmlDisplayBody()
    {
        final StringBuilder sb = new StringBuilder();
        
        final String prefix = "provenance_";
        
        sb.append("<div class=\"" + prefix + "key_div\"><span class=\"" + prefix + "key_span\">Key:</span>"
                + StringUtils.xmlEncodeString(this.getKey().stringValue()) + "\"</div>\n");
        
        return sb.toString();
    }
    
    @Override
    public String toHtmlFormBody()
    {
        final StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        final String prefix = "provenancerecord_";
        
        // sb.append("<div class=\""+prefix+"preferredPrefix_div\"><span class=\""+prefix+"preferredPrefix_span\">Prefix:</span><input type=\"text\" name=\""+prefix+"preferredPrefix\" value=\""+RdfUtils.xmlEncodeString(preferredPrefix)+"\" /></div>\n");
        // sb.append("<div class=\""+prefix+"description_div\"><span class=\""+prefix+"description_span\">Description:</span><input type=\"text\" name=\""+prefix+"description\" value=\""+RdfUtils.xmlEncodeString(description)+"\" /></div>\n");
        // sb.append("<div class=\""+prefix+"identifierRegex_div\"><span class=\""+prefix+"identifierRegex_span\">Namespace identifier regular expression:</span><input type=\"text\" name=\""+prefix+"identifierRegex\" value=\""+RdfUtils.xmlEncodeString(identifierRegex)+"\" /></div>\n");
        
        return sb.toString();
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            ProvenanceRecord.log.debug("ProvenanceRecord.toRdf: 1");
            // create some resources and literals to make statements out of
            final URI provenanceInstanceUri = keyToUse;
            
            // allow automatic creation of recordDate at this point if it has not been previously
            // specified
            if(this.recordDate == null)
            {
                this.recordDate = new Date();
            }
            
            final String recordDateString = Constants.ISO8601UTC().format(this.recordDate);
            
            final Literal recordDateLiteral = f.createLiteral(recordDateString, XMLSchema.DATETIME);
            
            ProvenanceRecord.log.debug("ProvenanceRecord.toRdf: 2 elementType=" + this.recordElementType);
            final URI elementTypeLiteral = f.createURI(this.recordElementType);
            ProvenanceRecord.log.debug("ProvenanceRecord.toRdf: 3 elementKey=" + this.recordElementKey);
            final URI elementKeyLiteral = f.createURI(this.recordElementKey);
            ProvenanceRecord.log.debug("ProvenanceRecord.toRdf: 4 hasAuthorOpenID=" + this.hasAuthorOpenID);
            final URI hasAuthorOpenIDLiteral = f.createURI(this.hasAuthorOpenID);
            ProvenanceRecord.log.debug("ProvenanceRecord.toRdf: 5");
            
            con.setAutoCommit(false);
            
            con.add(provenanceInstanceUri, RDF.TYPE, ProvenanceRecord.provenanceTypeUri, provenanceInstanceUri);
            con.add(provenanceInstanceUri, ProvenanceRecord.provenanceHasAuthorOpenIDUri, hasAuthorOpenIDLiteral,
                    provenanceInstanceUri);
            con.add(provenanceInstanceUri, ProvenanceRecord.provenanceElementTypeUri, elementTypeLiteral,
                    provenanceInstanceUri);
            con.add(provenanceInstanceUri, ProvenanceRecord.provenanceElementKeyUri, elementKeyLiteral,
                    provenanceInstanceUri);
            con.add(provenanceInstanceUri, ProvenanceRecord.provenanceRecordDateUri, recordDateLiteral,
                    provenanceInstanceUri);
            
            if(this.unrecognisedStatements != null)
            {
                for(final Statement nextUnrecognisedStatement : this.unrecognisedStatements)
                {
                    con.add(nextUnrecognisedStatement);
                }
            }
            
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
            
            ProvenanceRecord.log.error("RepositoryException: " + re.getMessage());
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
    
    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        
        sb.append("key=" + this.key + "\n");
        sb.append("elementType=" + this.recordElementType + "\n");
        sb.append("elementKey=" + this.recordElementKey + "\n");
        sb.append("hasAuthorOpenID=" + this.hasAuthorOpenID + "\n");
        if(this.recordDate != null)
        {
            sb.append("recordDate=" + Constants.ISO8601UTC().format(this.recordDate) + "\n");
        }
        else
        {
            sb.append("recordDate=null");
        }
        
        return sb.toString();
    }
    
}
