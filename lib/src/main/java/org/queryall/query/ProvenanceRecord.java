package org.queryall.query;

import info.aduna.iteration.Iterations;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
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
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.base.HtmlExport;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.provider.HttpProvider;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.blacklist.BlacklistController;
import org.queryall.exception.QueryAllException;
import org.queryall.impl.base.BaseQueryAllImpl;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.SettingsFactory;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProvenanceRecord extends BaseQueryAllImpl implements HtmlExport
{
    private static final Logger log = LoggerFactory.getLogger(ProvenanceRecord.class);
    private static final boolean TRACE = ProvenanceRecord.log.isTraceEnabled();
    private static final boolean DEBUG = ProvenanceRecord.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = ProvenanceRecord.log.isInfoEnabled();
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        try
        {
            con.begin();
            
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
    
    public String recordElementType = "";
    public String recordElementKey = "";
    public String hasAuthorOpenID = "";
    public Date recordDate = null;
    
    public static URI provenanceTypeUri;
    public static URI provenanceElementTypeUri;
    public static URI provenanceElementKeyUri;
    public static URI provenanceHasAuthorOpenIDUri;
    
    public static URI provenanceRecordDateUri;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.PROVENANCE.getBaseURI();
        
        ProvenanceRecord.provenanceTypeUri = f.createURI(baseUri, "ProvenanceRecord");
        ProvenanceRecord.provenanceHasAuthorOpenIDUri = f.createURI(baseUri, "hasAuthorOpenID");
        ProvenanceRecord.provenanceElementTypeUri = f.createURI(baseUri, "elementType");
        ProvenanceRecord.provenanceElementKeyUri = f.createURI(baseUri, "elementKey");
        ProvenanceRecord.provenanceRecordDateUri = f.createURI(baseUri, "recordDate");
    }
    
    public static Map<URI, ProvenanceRecord> fetchProvenanceForElementKey(final String hostToUse,
            final String nextElementKey, final int modelVersion, final HttpProvider dummyProvider,
            final String defaultHostAddress, final String assumedResponseContentType) throws InterruptedException
    {
        final QueryBundle nextQueryBundle = new QueryBundle();
        
        // final HttpProviderImpl dummyProvider = new HttpOnlyProviderImpl();
        
        dummyProvider.addEndpointUrl(hostToUse + "provenancebykey/" + StringUtils.percentEncode(nextElementKey));
        
        nextQueryBundle.addAlternativeEndpointAndQuery(
                hostToUse + "provenancebykey/" + StringUtils.percentEncode(nextElementKey), "");
        
        // TODO: remove calls to SettingsFactory.generateSettings() and
        // new BlacklistController(settings) here
        RdfFetchController fetchController = null;
        Repository myRepository = null;
        RepositoryConnection myRepositoryConnection = null;
        try
        {
            final QueryAllConfiguration settings = SettingsFactory.generateSettings();
            fetchController =
                    new RdfFetchController(settings, new BlacklistController(settings), Arrays.asList(nextQueryBundle));
            
            fetchController.fetchRdfForQueries();
            
            final Collection<RdfFetcherQueryRunnable> rdfResults = fetchController.getSuccessfulResults();
            
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
                        nextReaderFormat = Rio.getParserFormatForMIMEType(assumedResponseContentType);
                        
                        if(nextReaderFormat == null)
                        {
                            ProvenanceRecord.log
                                    .error("ProvenanceRecord.fetchProvenanceForElementKey: Not attempting to parse result because Settings.getStringPropertyFromConfig(\"assumedResponseContentType\") isn't supported by Rio and the returned content type wasn't either nextResult.returnedMIMEType="
                                            + nextResult.getReturnedMIMEType()
                                            + " Settings.getStringPropertyFromConfig(\"assumedResponseContentType\")="
                                            + assumedResponseContentType);
                            continue;
                        }
                        else
                        {
                            ProvenanceRecord.log
                                    .warn("ProvenanceRecord.fetchProvenanceForElementKey: readerFormat NOT matched for returnedMIMEType="
                                            + nextResult.getReturnedMIMEType()
                                            + " using configured preferred content type as fallback Settings.getStringPropertyFromConfig(\"assumedResponseContentType\")="
                                            + assumedResponseContentType);
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
                        myRepositoryConnection.add(new java.io.StringReader(nextResult.getNormalisedResult()),
                                defaultHostAddress + "provenancebykey/" + StringUtils.percentEncode(nextElementKey),
                                nextReaderFormat);
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
        catch(final QueryAllException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch(final InterruptedException ie)
        {
            ProvenanceRecord.log.error("ProvenanceRecord: interrupted exception", ie);
            throw ie;
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
            final int configApiVersion) throws org.openrdf.repository.RepositoryException
    {
        final Map<URI, ProvenanceRecord> results = new ConcurrentHashMap<URI, ProvenanceRecord>();
        
        final URI provenanceTypeUri = ProvenanceRecord.provenanceTypeUri;
        
        RepositoryConnection con = null;
        
        try
        {
            con = myRepository.getConnection();
            
            final RepositoryResult<Statement> statements = con.getStatements(null, RDF.TYPE, provenanceTypeUri, true);
            
            while(statements.hasNext())
            {
                final Statement nextProvider = statements.next();
                
                final URI nextSubjectUri = (URI)nextProvider.getSubject();
                results.put(
                        nextSubjectUri,
                        new ProvenanceRecord(Iterations.asList(con.getStatements(nextSubjectUri, (URI)null,
                                (Value)null, true)), nextSubjectUri, configApiVersion));
            }
        }
        catch(final OpenRDFException e)
        {
            // handle exception
            ProvenanceRecord.log.error("getProvenanceRecordsFromRepository.:", e);
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
    
    // keyToUse is the URI of the next instance that can be found in myRepository
    // returns null if the URI is not in the repository or the information is not enough to create a
    // minimal query configuration
    public ProvenanceRecord(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = this.resetUnrecognisedStatements();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(ProvenanceRecord.DEBUG)
            {
                ProvenanceRecord.log.debug("ProvenanceRecord: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(ProvenanceRecord.provenanceTypeUri))
            {
                if(ProvenanceRecord.TRACE)
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
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(ProvenanceRecord.DEBUG)
        {
            ProvenanceRecord.log.debug("ProvenanceRecord.fromRdf: would have returned... keyToUse=" + keyToUse
                    + " result=" + this.toString());
        }
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
    public Set<URI> getElementTypes()
    {
        return Collections.singleton(ProvenanceRecord.provenanceTypeUri);
    }
    
    public boolean relatedToElementTypes(final Collection<URI> typesToCheck)
    {
        if(typesToCheck == null || this.recordElementType == null || this.recordElementType.trim().equals(""))
        {
            return false;
        }
        
        if(ProvenanceRecord.DEBUG)
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
     * @param nextDate
     *            the date the record was created at
     */
    public void setRecordDate(final Date nextDate)
    {
        this.recordDate = nextDate;
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
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... keyToUse)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            ProvenanceRecord.log.debug("ProvenanceRecord.toRdf: 1");
            // create some resources and literals to make statements out of
            final URI provenanceInstanceUri = this.getKey();
            
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
            
            con.begin();
            
            con.add(provenanceInstanceUri, RDF.TYPE, ProvenanceRecord.provenanceTypeUri, keyToUse);
            con.add(provenanceInstanceUri, ProvenanceRecord.provenanceHasAuthorOpenIDUri, hasAuthorOpenIDLiteral,
                    keyToUse);
            con.add(provenanceInstanceUri, ProvenanceRecord.provenanceElementTypeUri, elementTypeLiteral, keyToUse);
            con.add(provenanceInstanceUri, ProvenanceRecord.provenanceElementKeyUri, elementKeyLiteral, keyToUse);
            con.add(provenanceInstanceUri, ProvenanceRecord.provenanceRecordDateUri, recordDateLiteral, keyToUse);
            
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
        
        sb.append("key=" + this.getKey() + "\n");
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
