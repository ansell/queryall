package org.queryall.queryutils;

import info.aduna.iteration.Iterations;

import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;

import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Date;

import org.queryall.impl.*;
import org.queryall.api.BaseQueryAllInterface;
import org.queryall.api.Provider;
import org.queryall.blacklist.BlacklistController;
import org.queryall.helpers.*;

import org.apache.log4j.Logger;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProvenanceRecord implements BaseQueryAllInterface
{
    private static final Logger log = Logger.getLogger(ProvenanceRecord.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.getSettings().getNamespaceForProvenance();
    
    public Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key;
    public String elementType = "";
    public String elementKey = "";
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
        
        provenanceNamespace = Settings.getSettings().getOntologyTermUriPrefix()
                         +Settings.getSettings().getNamespaceForProvenance()
                         +Settings.getSettings().getOntologyTermUriSuffix();
        
        provenanceTypeUri = f.createURI(provenanceNamespace,"ProvenanceRecord");
        provenanceHasAuthorOpenIDUri = f.createURI(provenanceNamespace,"hasAuthorOpenID");
        provenanceElementTypeUri = f.createURI(provenanceNamespace,"elementType");
        provenanceElementKeyUri = f.createURI(provenanceNamespace,"elementKey");
        provenanceRecordDateUri = f.createURI(provenanceNamespace,"recordDate");
    }
    
    public static Map<String, ProvenanceRecord> getProvenanceRecordsFromRepository(Repository myRepository, int modelVersion) throws org.openrdf.repository.RepositoryException
    {
        Map<String, ProvenanceRecord> results = new Hashtable<String, ProvenanceRecord>();
        
        URI provenanceTypeUri = new ProvenanceRecord().getElementType();
        
        RepositoryConnection myRepositoryConnection = null;
        
        try
        {
            myRepositoryConnection = myRepository.getConnection();
            
            final String queryString = "SELECT ?provenanceRecordUri WHERE { ?provenanceRecordUri a <"
                    + provenanceTypeUri.stringValue() + "> . }";
            final TupleQuery tupleQuery = myRepositoryConnection.prepareTupleQuery(
                    QueryLanguage.SPARQL, queryString);
            final TupleQueryResult queryResult = tupleQuery.evaluate();
            try
            {
                while(queryResult.hasNext())
                {
                    final BindingSet bindingSet = queryResult.next();
                    final Value valueOfProvenanceRecordUri = bindingSet
                            .getValue("provenanceRecordUri");
                    if(_TRACE)
                    {
                        log.trace("ProvenanceRecord.getProvenanceRecordsFromRepository: found provenanceRecord: valueOfProvenanceRecordUri="
                                        + valueOfProvenanceRecordUri);
                    }
                    final RepositoryResult<Statement> statements = 
                            myRepositoryConnection.getStatements((URI) valueOfProvenanceRecordUri,
                                    (URI) null, (Value) null, true);
                    final Collection<Statement> nextStatementList = 
                            Iterations.addAll(statements, new HashSet<Statement>());
                    final ProvenanceRecord nextRecord = ProvenanceRecord.fromRdf(nextStatementList, valueOfProvenanceRecordUri.stringValue(), modelVersion);
                    
                    if(nextRecord != null)
                    {
                        results.put(valueOfProvenanceRecordUri.stringValue(),
                                nextRecord);
                    }
                    else
                    {
                        log.error("ProvenanceRecord.getProvenanceRecordsFromRepository: was not able to create a provenance record with URI valueOfProvenanceRecordUri="
                                        + valueOfProvenanceRecordUri.toString());
                    }
                }
            }
            finally
            {
                queryResult.close();
            }
        }
        catch (OpenRDFException e)
        {
            // handle exception
            log.error("ProvenanceRecord.getProvenanceRecordsFromRepository:", e);
        }
        finally
        {
            if(myRepositoryConnection != null)
                myRepositoryConnection.close();
        }
        
        return results;
    }
    
    public static Map<String, ProvenanceRecord> fetchProvenanceForElementKey(String hostToUse, String nextElementKey, int modelVersion) throws InterruptedException
    {
        QueryBundle nextQueryBundle = new QueryBundle();
        
        HttpProviderImpl dummyProvider = new HttpProviderImpl();
        
        Collection<String> endpointUrls = new HashSet<String>();
        
		endpointUrls.add(hostToUse+"provenancebykey/"+StringUtils.percentEncode(nextElementKey));		
		
		dummyProvider.setEndpointUrls(endpointUrls);
		
		nextQueryBundle.setQueryEndpoint(hostToUse+"provenancebykey/"+StringUtils.percentEncode(nextElementKey));
        
        Collection<QueryBundle> queryBundles = new HashSet<QueryBundle>();
        
        queryBundles.add(nextQueryBundle);
        
        RdfFetchController fetchController = new RdfFetchController(Settings.getSettings(), BlacklistController.getDefaultController(), queryBundles);
        
        try
        {
            fetchController.fetchRdfForQueries();
        }
        catch(InterruptedException ie)
        {
            log.fatal("ProvenanceRecord: interrupted exception",ie);
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
                    
                    if(log.isDebugEnabled())
                    {
                        log.debug("ProvenanceRecord.fetchProvenanceForElementKey: nextReaderFormat for returnedContentType="+nextResult.getReturnedContentType()+" nextReaderFormat="+nextReaderFormat);
                    }
                    
                    if(nextReaderFormat == null)
                    {
                        nextReaderFormat = Rio.getParserFormatForMIMEType(Settings.getSettings().getStringPropertyFromConfig("assumedRequestContentType", ""));
                        
                        if(nextReaderFormat == null)
                        {
                            log.error("ProvenanceRecord.fetchProvenanceForElementKey: Not attempting to parse result because Settings.getStringPropertyFromConfig(\"assumedRequestContentType\") isn't supported by Rio and the returned content type wasn't either nextResult.returnedMIMEType="+nextResult.getReturnedMIMEType()+" Settings.getStringPropertyFromConfig(\"assumedRequestContentType\")="+Settings.getSettings().getStringPropertyFromConfig("assumedRequestContentType", ""));
                            continue;
                        }
                        else
                        {
                            log.warn("ProvenanceRecord.fetchProvenanceForElementKey: readerFormat NOT matched for returnedMIMEType="+nextResult.getReturnedMIMEType()+" using configured preferred content type as fallback Settings.getStringPropertyFromConfig(\"assumedRequestContentType\")="+Settings.getSettings().getStringPropertyFromConfig("assumedRequestContentType", ""));
                        }
                    }
                    else if(log.isDebugEnabled())
                    {
                        log.debug("ProvenanceRecord.fetchProvenanceForElementKey: readerFormat matched for returnedMIMEType="+nextResult.getReturnedMIMEType());
                    }
                    
                    if(nextResult.getNormalisedResult().length() > 0)
                    {
                        myRepositoryConnection.add(new java.io.StringReader(nextResult.getNormalisedResult()), Settings.getSettings().getDefaultHostAddress()+"provenancebykey/"+StringUtils.percentEncode(nextElementKey), nextReaderFormat);
                    }
                }
                catch(org.openrdf.rio.RDFParseException rdfpe)
                {
                    log.error("ProvenanceRecord.fetchProvenanceForElementKey: RDFParseException",rdfpe);
                }
                catch(org.openrdf.repository.RepositoryException re)
                {
                    log.error("ProvenanceRecord.fetchProvenanceForElementKey: RepositoryException inner",re);
                }
                catch(java.io.IOException ioe)
                {
                    log.error("ProvenanceRecord.fetchProvenanceForElementKey: IOException",ioe);
                }
            } // end for(RdfFetcherQUeryRunnable nextResult : rdfResults)
        }
        catch(org.openrdf.repository.RepositoryException re)
        {
            log.error("ProvenanceRecord.fetchProvenanceForElementKey: RepositoryException outer",re);
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
                log.fatal("ProvenanceRecord.fetchProvenanceForElementKey: failed to close repository connection", re2);
            }
        }
        
        Map<String, ProvenanceRecord> results = null;
        
        try
        {
            results = ProvenanceRecord.getProvenanceRecordsFromRepository(myRepository, modelVersion);
        }
        catch(org.openrdf.repository.RepositoryException re)
        {
            log.fatal("ProvenanceRecord.fetchProvenanceForElementKey: failed to get records due to a repository exception", re);
        }
        
        return results;
    }
    
    // keyToUse is the URI of the next instance that can be found in myRepository
    // returns null if the URI is not in the repository or the information is not enough to create a minimal query configuration
    public static ProvenanceRecord fromRdf(Collection<Statement> inputStatements, String keyToUse, int modelVersion) throws OpenRDFException
    {
        ProvenanceRecord result = new ProvenanceRecord();
        
        boolean resultIsValid = false;
        
        Collection<Statement> tempUnrecognisedStatements = new HashSet<Statement>();
        
        for(Statement nextStatement : inputStatements)
        {
            if(_DEBUG)
            {
                log.debug("ProvenanceRecord: nextStatement: "+nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE) && nextStatement.getObject().equals(provenanceTypeUri))
            {
                if(_TRACE)
                {
                    log.trace("ProvenanceRecord: found valid type predicate for URI: "+keyToUse);
                }
                
                resultIsValid = true;
                result.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(provenanceHasAuthorOpenIDUri))
            {
                result.hasAuthorOpenID = nextStatement.getObject().stringValue();
            }
            else if(nextStatement.getPredicate().equals(provenanceElementTypeUri))
            {
                result.elementType = nextStatement.getObject().stringValue();
            }
            else if(nextStatement.getPredicate().equals(provenanceElementKeyUri))
            {
                result.elementKey = nextStatement.getObject().stringValue();
            }
            else if(nextStatement.getPredicate().equals(provenanceRecordDateUri))
            {
                try
                {
                    result.recordDate = RdfUtils.getDateTimeFromValue(nextStatement.getObject());
                }
                catch(java.text.ParseException pe)
                {
                    log.error("ProvenanceRecord.fromRdf: could not parse date: value="+nextStatement.getObject().stringValue(), pe);
                }
            }
            else
            {
                tempUnrecognisedStatements.add(nextStatement);
            }
        }
        
        result.unrecognisedStatements = tempUnrecognisedStatements;
        
        if(_DEBUG)
        {
            log.debug("ProvenanceRecord.fromRdf: would have returned... keyToUse="+keyToUse+" result="+result.toString());
        }
        
        if(resultIsValid)
        {
            return result;
        }
        else
        {
            throw new RuntimeException("ProvenanceRecord.fromRdf: result was not valid keyToUse="+keyToUse);
        }
    }
    

    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            log.debug("ProvenanceRecord.toRdf: 1");
            // create some resources and literals to make statements out of
            URI provenanceInstanceUri = keyToUse;
            
            // allow automatic creation of recordDate at this point if it has not been previously specified
            if(recordDate == null)
            {
                recordDate = new Date();
            }
            
            final String recordDateString = Constants.ISO8601UTC().format(recordDate);
            
            Literal recordDateLiteral = f.createLiteral(recordDateString, XMLSchema.DATETIME);
            
            log.debug("ProvenanceRecord.toRdf: 2 elementType="+elementType);
            URI elementTypeLiteral = f.createURI(elementType);
            log.debug("ProvenanceRecord.toRdf: 3 elementKey="+elementKey);
            URI elementKeyLiteral = f.createURI(elementKey);
            log.debug("ProvenanceRecord.toRdf: 4 hasAuthorOpenID="+hasAuthorOpenID);
            URI hasAuthorOpenIDLiteral = f.createURI(hasAuthorOpenID);
            log.debug("ProvenanceRecord.toRdf: 5");
            
            con.setAutoCommit(false);
            
            con.add(provenanceInstanceUri, RDF.TYPE, provenanceTypeUri, provenanceInstanceUri);
            con.add(provenanceInstanceUri, provenanceHasAuthorOpenIDUri, hasAuthorOpenIDLiteral, provenanceInstanceUri);
            con.add(provenanceInstanceUri, provenanceElementTypeUri, elementTypeLiteral, provenanceInstanceUri);
            con.add(provenanceInstanceUri, provenanceElementKeyUri, elementKeyLiteral, provenanceInstanceUri);
            con.add(provenanceInstanceUri, provenanceRecordDateUri, recordDateLiteral, provenanceInstanceUri);
            
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
        catch (RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
                con.rollback();
                
            log.error("RepositoryException: "+re.getMessage());
        }
        finally
        {
            if(con != null)
                con.close();
        }
        
        return false;
    }
    
    public static boolean schemaToRdf(Repository myRepository, String keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            URI contextKeyUri = f.createURI(keyToUse);
            con.setAutoCommit(false);
            
            con.add(provenanceTypeUri, RDF.TYPE, OWL.CLASS, contextKeyUri);
            
            
            con.add(provenanceRecordDateUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(provenanceElementKeyUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(provenanceElementTypeUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(provenanceHasAuthorOpenIDUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch (RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
                con.rollback();
                
            log.error("RepositoryException: "+re.getMessage());
        }
        finally
        {
            if(con != null)
                con.close();
        }
        
        return false;
    }
    

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append("key="+key+"\n");
        sb.append("elementType="+elementType+"\n");
        sb.append("elementKey="+elementKey+"\n");
        sb.append("hasAuthorOpenID="+hasAuthorOpenID+"\n");
        if(recordDate != null)
        {
            sb.append("recordDate="+Constants.ISO8601UTC().format(recordDate)+"\n");
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
        
        sb.append("<div class=\""+prefix+"key_div\"><span class=\""+prefix+"key_span\">Key:</span>"+StringUtils.xmlEncodeString(getKey().stringValue())+"\"</div>\n");
        
        return sb.toString();
    }
    
    public boolean relatedToElementTypes(Collection<URI> typesToCheck)
    {
        if(typesToCheck == null || elementType == null || elementType.trim().equals(""))
        {
            return false;
        }
        
        if(_DEBUG)
        {
            log.debug("ProvenanceRecord.relatedToElementTypes: this.getKey()="+this.getKey());
        }
        
        for(URI nextTypeToCheck : typesToCheck)
        {
            if(elementType.equals(nextTypeToCheck))
            {
                return true;
            }
        }
        
        return false;
    }
    

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
    

    public String toHtml()
    {
        return "<span>key:</span>"+StringUtils.xmlEncodeString(getKey().stringValue());
    }
    
    /**
     * @return the key
     */

    public URI getKey()
    {
        return key;
    }

    /**
     * @param key the key to set
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
        return provenanceTypeUri;
    }
    
    /**
     * @param elementType the elementType to set
     */
    public void setElementType(String nextElementType)
    {
        this.elementType = nextElementType;
    }
    
    
    /**
     * @return the URI used for the Key of the element referred to by this provenance record
     */
    public String getElementKey()
    {
        return elementKey;
    }
    
    /**
     * @param elementKey the elementKey to set
     */
    public void setElementKey(String nextElementKey)
    {
        this.elementKey = nextElementKey;
    }
    
    /**
     * @param nextDate the date the record was created at
     */
    public void setRecordDate(Date nextDate)
    {
        this.recordDate = nextDate;
    }
    
    public void setHasAuthorOpenID(String nextHasAuthorOpenID)
    {
        this.hasAuthorOpenID = nextHasAuthorOpenID;
    }

    public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    public URI getCurationStatus()
    {
        return curationStatus;
    }
    
    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        unrecognisedStatements.add(unrecognisedStatement);
    }

    public Collection<Statement> getUnrecognisedStatements()
    {
        return unrecognisedStatements;
    }

    public void setTitle(String title)
    {
    }

    public String getTitle()
    {
        return null;
    }


}
