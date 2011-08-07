package org.queryall.queryutils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;
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
import org.queryall.api.BaseQueryAllInterface;
import org.queryall.blacklist.BlacklistController;
import org.queryall.helpers.Constants;
import org.queryall.helpers.RdfUtils;
import org.queryall.helpers.Settings;
import org.queryall.helpers.StringUtils;
import org.queryall.impl.HttpProviderImpl;
import org.queryall.impl.ProjectImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProvenanceRecord implements BaseQueryAllInterface
{
    private static final Logger log = Logger.getLogger(ProvenanceRecord.class.getName());
    private static final boolean _TRACE = ProvenanceRecord.log.isTraceEnabled();
    private static final boolean _DEBUG = ProvenanceRecord.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = ProvenanceRecord.log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.getSettings().getNamespaceForProvenance();
    
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
    
    public static String provenanceNamespace;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        ProvenanceRecord.provenanceNamespace =
                Settings.getSettings().getOntologyTermUriPrefix() + Settings.getSettings().getNamespaceForProvenance()
                        + Settings.getSettings().getOntologyTermUriSuffix();
        
        ProvenanceRecord.provenanceTypeUri = f.createURI(ProvenanceRecord.provenanceNamespace, "ProvenanceRecord");
        ProvenanceRecord.provenanceHasAuthorOpenIDUri =
                f.createURI(ProvenanceRecord.provenanceNamespace, "hasAuthorOpenID");
        ProvenanceRecord.provenanceElementTypeUri = f.createURI(ProvenanceRecord.provenanceNamespace, "elementType");
        ProvenanceRecord.provenanceElementKeyUri = f.createURI(ProvenanceRecord.provenanceNamespace, "elementKey");
        ProvenanceRecord.provenanceRecordDateUri = f.createURI(ProvenanceRecord.provenanceNamespace, "recordDate");
    }
    
    // keyToUse is the URI of the next instance that can be found in myRepository
    // returns null if the URI is not in the repository or the information is not enough to create a
    // minimal query configuration
    public ProvenanceRecord(Collection<Statement> inputStatements, URI keyToUse, int modelVersion)
        throws OpenRDFException
    {
        Collection<Statement> tempUnrecognisedStatements = new HashSet<Statement>();
        
        for(Statement nextStatement : inputStatements)
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
                catch(java.text.ParseException pe)
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
    
    public static Map<URI, ProvenanceRecord> getProvenanceRecordsFromRepository(Repository myRepository,
            int modelVersion) throws org.openrdf.repository.RepositoryException
    {
        Map<URI, ProvenanceRecord> results = new Hashtable<URI, ProvenanceRecord>();
        
        URI provenanceTypeUri = ProvenanceRecord.provenanceTypeUri;
        
        try
        {
            final RepositoryConnection con = myRepository.getConnection();
            
            for(Statement nextProvider : con.getStatements(null, RDF.TYPE, provenanceTypeUri, true).asList())
            {
                URI nextSubjectUri = (URI)nextProvider.getSubject();
                results.put(nextSubjectUri,
                        new ProvenanceRecord(con.getStatements(nextSubjectUri, (URI)null, (Value)null, true).asList(),
                                nextSubjectUri, Settings.CONFIG_API_VERSION));
            }
        }
        catch(final OpenRDFException e)
        {
            // handle exception
            ProvenanceRecord.log.fatal("getProvenanceRecordsFromRepository.:", e);
        }
        
        return results;
    }
    
    public static Map<URI, ProvenanceRecord> fetchProvenanceForElementKey(String hostToUse, String nextElementKey,
            int modelVersion) throws InterruptedException
    {
        QueryBundle nextQueryBundle = new QueryBundle();
        
        HttpProviderImpl dummyProvider = new HttpProviderImpl();
        
        Collection<String> endpointUrls = new HashSet<String>();
        
        endpointUrls.add(hostToUse + "provenancebykey/" + StringUtils.percentEncode(nextElementKey));
        
        dummyProvider.setEndpointUrls(endpointUrls);
        
        nextQueryBundle.setQueryEndpoint(hostToUse + "provenancebykey/" + StringUtils.percentEncode(nextElementKey));
        
        Collection<QueryBundle> queryBundles = new HashSet<QueryBundle>();
        
        queryBundles.add(nextQueryBundle);
        
        RdfFetchController fetchController =
                new RdfFetchController(Settings.getSettings(), BlacklistController.getDefaultController(), queryBundles);
        
        try
        {
            fetchController.fetchRdfForQueries();
        }
        catch(InterruptedException ie)
        {
            ProvenanceRecord.log.fatal("ProvenanceRecord: interrupted exception", ie);
            throw ie;
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
                                        "assumedResponseContentType", ""));
                        
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
                catch(org.openrdf.rio.RDFParseException rdfpe)
                {
                    ProvenanceRecord.log.error("ProvenanceRecord.fetchProvenanceForElementKey: RDFParseException",
                            rdfpe);
                }
                catch(org.openrdf.repository.RepositoryException re)
                {
                    ProvenanceRecord.log.error(
                            "ProvenanceRecord.fetchProvenanceForElementKey: RepositoryException inner", re);
                }
                catch(java.io.IOException ioe)
                {
                    ProvenanceRecord.log.error("ProvenanceRecord.fetchProvenanceForElementKey: IOException", ioe);
                }
            } // end for(RdfFetcherQUeryRunnable nextResult : rdfResults)
        }
        catch(org.openrdf.repository.RepositoryException re)
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
            catch(org.openrdf.repository.RepositoryException re2)
            {
                ProvenanceRecord.log.fatal(
                        "ProvenanceRecord.fetchProvenanceForElementKey: failed to close repository connection", re2);
            }
        }
        
        Map<URI, ProvenanceRecord> results = null;
        
        try
        {
            results = ProvenanceRecord.getProvenanceRecordsFromRepository(myRepository, modelVersion);
        }
        catch(org.openrdf.repository.RepositoryException re)
        {
            ProvenanceRecord.log
                    .fatal("ProvenanceRecord.fetchProvenanceForElementKey: failed to get records due to a repository exception",
                            re);
        }
        
        return results;
    }
    
    @Override
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            ProvenanceRecord.log.debug("ProvenanceRecord.toRdf: 1");
            // create some resources and literals to make statements out of
            URI provenanceInstanceUri = keyToUse;
            
            // allow automatic creation of recordDate at this point if it has not been previously
            // specified
            if(recordDate == null)
            {
                recordDate = new Date();
            }
            
            final String recordDateString = Constants.ISO8601UTC().format(recordDate);
            
            Literal recordDateLiteral = f.createLiteral(recordDateString, XMLSchema.DATETIME);
            
            ProvenanceRecord.log.debug("ProvenanceRecord.toRdf: 2 elementType=" + recordElementType);
            URI elementTypeLiteral = f.createURI(recordElementType);
            ProvenanceRecord.log.debug("ProvenanceRecord.toRdf: 3 elementKey=" + recordElementKey);
            URI elementKeyLiteral = f.createURI(recordElementKey);
            ProvenanceRecord.log.debug("ProvenanceRecord.toRdf: 4 hasAuthorOpenID=" + hasAuthorOpenID);
            URI hasAuthorOpenIDLiteral = f.createURI(hasAuthorOpenID);
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
            
            if(unrecognisedStatements != null)
            {
                for(Statement nextUnrecognisedStatement : unrecognisedStatements)
                {
                    con.add(nextUnrecognisedStatement);
                }
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(RepositoryException re)
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
    
    public static boolean schemaToRdf(Repository myRepository, URI contextUri, int modelVersion)
        throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
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
        catch(RepositoryException re)
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
        StringBuilder sb = new StringBuilder();
        
        sb.append("key=" + key + "\n");
        sb.append("elementType=" + recordElementType + "\n");
        sb.append("elementKey=" + recordElementKey + "\n");
        sb.append("hasAuthorOpenID=" + hasAuthorOpenID + "\n");
        if(recordDate != null)
        {
            sb.append("recordDate=" + Constants.ISO8601UTC().format(recordDate) + "\n");
        }
        else
        {
            sb.append("recordDate=null");
        }
        
        return sb.toString();
    }
    
    public String toHtmlDisplayBody()
    {
        StringBuilder sb = new StringBuilder();
        
        String prefix = "provenance_";
        
        sb.append("<div class=\"" + prefix + "key_div\"><span class=\"" + prefix + "key_span\">Key:</span>"
                + StringUtils.xmlEncodeString(getKey().stringValue()) + "\"</div>\n");
        
        return sb.toString();
    }
    
    public boolean relatedToElementTypes(Collection<URI> typesToCheck)
    {
        if(typesToCheck == null || recordElementType == null || recordElementType.trim().equals(""))
        {
            return false;
        }
        
        if(ProvenanceRecord._DEBUG)
        {
            ProvenanceRecord.log.debug("ProvenanceRecord.relatedToElementTypes: this.getKey()=" + this.getKey());
        }
        
        for(URI nextTypeToCheck : typesToCheck)
        {
            if(recordElementType.equals(nextTypeToCheck))
            {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        String prefix = "provenancerecord_";
        
        // sb.append("<div class=\""+prefix+"preferredPrefix_div\"><span class=\""+prefix+"preferredPrefix_span\">Prefix:</span><input type=\"text\" name=\""+prefix+"preferredPrefix\" value=\""+RdfUtils.xmlEncodeString(preferredPrefix)+"\" /></div>\n");
        // sb.append("<div class=\""+prefix+"description_div\"><span class=\""+prefix+"description_span\">Description:</span><input type=\"text\" name=\""+prefix+"description\" value=\""+RdfUtils.xmlEncodeString(description)+"\" /></div>\n");
        // sb.append("<div class=\""+prefix+"identifierRegex_div\"><span class=\""+prefix+"identifierRegex_span\">Namespace identifier regular expression:</span><input type=\"text\" name=\""+prefix+"identifierRegex\" value=\""+RdfUtils.xmlEncodeString(identifierRegex)+"\" /></div>\n");
        
        return sb.toString();
    }
    
    @Override
    public String toHtml()
    {
        return "<span>key:</span>" + StringUtils.xmlEncodeString(getKey().stringValue());
    }
    
    /**
     * @return the key
     */
    @Override
    public URI getKey()
    {
        return key;
    }
    
    /**
     * @param key
     *            the key to set
     */
    @Override
    public void setKey(String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }
    
    @Override
    public void setKey(URI nextKey)
    {
        this.key = nextKey;
    }
    
    /**
     * @return the namespace used to represent objects of this type by default
     */
    @Override
    public String getDefaultNamespace()
    {
        return ProvenanceRecord.defaultNamespace;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Collection<URI> getElementTypes()
    {
        Collection<URI> results = new ArrayList<URI>(1);
        
        results.add(ProvenanceRecord.provenanceTypeUri);
        
        return results;
    }
    
    /**
     * @param recordElementType
     *            the elementType to set
     */
    public void setElementType(String nextElementType)
    {
        this.recordElementType = nextElementType;
    }
    
    /**
     * @return the URI used for the Key of the element referred to by this provenance record
     */
    public String getElementKey()
    {
        return recordElementKey;
    }
    
    /**
     * @param recordElementKey
     *            the elementKey to set
     */
    public void setElementKey(String nextElementKey)
    {
        this.recordElementKey = nextElementKey;
    }
    
    /**
     * @param nextDate
     *            the date the record was created at
     */
    public void setRecordDate(Date nextDate)
    {
        this.recordDate = nextDate;
    }
    
    public void setHasAuthorOpenID(String nextHasAuthorOpenID)
    {
        this.hasAuthorOpenID = nextHasAuthorOpenID;
    }
    
    @Override
    public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    @Override
    public URI getCurationStatus()
    {
        return curationStatus;
    }
    
    @Override
    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        unrecognisedStatements.add(unrecognisedStatement);
    }
    
    @Override
    public Collection<Statement> getUnrecognisedStatements()
    {
        return unrecognisedStatements;
    }
    
    @Override
    public void setTitle(String title)
    {
    }
    
    @Override
    public String getTitle()
    {
        return null;
    }
    
}
