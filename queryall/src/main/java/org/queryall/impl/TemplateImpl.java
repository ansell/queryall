package org.queryall.impl;

import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;

import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryConnection;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.queryall.api.Template;
import org.queryall.helpers.*;

import org.apache.log4j.Logger;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class TemplateImpl extends Template
{
    private static final Logger log = Logger.getLogger(Template.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    @SuppressWarnings("unused")
	private static final boolean _INFO = log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.getSettings().getNamespaceForTemplate();
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key;
    // Many different template types might match for this one... This should match the initial part of the template that is relevant to the required parameter
    // Some examples of this are:
    // key=http://bio2rdf.org/template:xmlEncoded_input_NN matchRegex="xmlEncoded_(input_\d+)" matches xmlEncoded_input_1
    // it is a native function with reference http://bio2rdf.org/nativetemplate:xmlEncoded
    // and then attempts to match its referenced templates using the (.+) group 
    // the last referenced template is applied to the inputs first, before moving back through the chain 
    // to eventually get to templates which are referenced directly by QueryTypes or other elements
    private String matchRegex = "";
    // if this template is marked as a native function, the implementation should recognise the native function URI and treat it accordingly
    // native functions may have referenced templates, which may or may not be native functions
    // An example of native functions are the input_NN set which are recognised and replaced
    // Other native functions also include the xmlEncoded_, privateuppercase_, privatelowercase_, urlEncoded_ ntriplesEncoded_, endpointSpecific_
    private boolean isNativeFunction = false;
    // if isNativeFunction is true, then the following will contain the URI matching the implemented function
    private String nativeFunctionUri = "";
    private URI curationStatus = ProjectImpl.getProjectNotCuratedUri();
    
    // each of the following templates must be applied to this template before returning, additionally, 
    // any parameters that are not declared here which are found in the contextual parameter list must also be included
    private Collection<URI> referencedTemplates = new HashSet<URI>();
    
    // The actual template string for this part is included here
    // It may be empty in native implementations, but otherwise it should contain references to the string which contains refenced template prefixes
    private String templateString = "";
    
    // This is the content type of this template
    // if it is a plain string then use text/plain, 
    // even though this has been erroneously overloaded to mean NTriples in SemWeb circles
    // for SPARQL queries use application/sparql-query
    // for SPARQL results templates use application/sparql-results+xml and application/sparql-results+json, although these will not generate results that can be included in RDF documents
    // Also this to indicate whether the template is a basic application/rdf+xml 
    // or text/rdf+n3 template that may be used for output or insertion into an endpoint as necessary
    private String contentType = "";
    
    private int order = 100;
    private String title;
    
    private static URI templateTypeUri = null;
    
    private static URI templateContentTypeSparqlQuery = null;
    private static URI templateContentTypeSparqlResultsXml = null;
    private static URI templateContentTypeSparqlResultsJson = null;
    private static URI templateContentTypeRdfXml = null;
    private static URI templateContentTypeN3 = null;
    private static URI templateContentTypePlainText = null;
    private static URI templateContentType = null;
    private static URI templateReferencedTemplate = null;
    private static URI templateMatchRegex = null;
    private static URI templateIsNativeFunction = null;
    private static URI templateNativeFunctionUri = null;
    private static URI templateTemplateString = null;
    private static URI templateOrder = null;
    
    public static String templateNamespace;
    
    static
    {
        templateNamespace = Settings.getSettings().getOntologyTermUriPrefix()
                         +Settings.getSettings().getNamespaceForTemplate()
                         +Settings.getSettings().getOntologyTermUriSuffix();
                         
        final ValueFactory f = Constants.valueFactory;

        setTemplateTypeUri(f.createURI(templateNamespace,"Template"));
        setTemplateContentTypeSparqlQuery(f.createURI(templateNamespace,"ContentTypeSparqlQuery"));
        setTemplateContentTypeSparqlResultsXml(f.createURI(templateNamespace,"ContentTypeSparqlResultsXml"));
        setTemplateContentTypeSparqlResultsJson(f.createURI(templateNamespace,"ContentTypeSparqlResultsJson"));
        setTemplateContentTypeRdfXml(f.createURI(templateNamespace,"ContentTypeRdfXml"));
        setTemplateContentTypeN3(f.createURI(templateNamespace,"ContentTypeN3"));
        setTemplateContentTypePlainText(f.createURI(templateNamespace,"ContentTypePlainText"));
        setTemplateContentType(f.createURI(templateNamespace,"contentType"));
        setTemplateReferencedTemplate(f.createURI(templateNamespace,"referencedTemplate"));
        
        setTemplateMatchRegex(f.createURI(templateNamespace,"matchRegex"));
        setTemplateIsNativeFunction(f.createURI(templateNamespace,"isNativeFunction"));
        setTemplateNativeFunctionUri(f.createURI(templateNamespace,"nativeFunctionUri"));
        setTemplateTemplateString(f.createURI(templateNamespace,"templateString"));
        setTemplateContentType(f.createURI(templateNamespace,"contentType"));
        setTemplateOrder(f.createURI(templateNamespace,"order"));
    }
    
    
    public TemplateImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        for(Statement nextStatement : inputStatements)
        {
            if(_DEBUG)
            {
                log.debug("Template: nextStatement: "+nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE) && nextStatement.getObject().equals(getTemplateTypeUri()))
            {
                if(_TRACE)
                {
                    log.trace("Template: found valid type predicate for URI: "+keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.getProjectCurationStatusUri()))
            {
                this.setCurationStatus((URI)nextStatement.getObject());
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(_DEBUG)
        {
            log.debug("Template.fromRdf: would have returned... keyToUse="+keyToUse+" result="+this.toString());
        }
    }
    

    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            // create some resources and literals to make statements out of
            URI templateInstanceUri = keyToUse;
            
            Literal matchRegexLiteral = f.createLiteral(matchRegex);
            Literal isNativeFunctionLiteral = f.createLiteral(isNativeFunction);
            Literal templateStringLiteral = f.createLiteral(templateString);
            URI contentTypeLiteral = f.createURI(contentType);
            Literal orderLiteral = f.createLiteral(order);
            
            URI nativeFunctionUriLiteral = null;
            
            if(nativeFunctionUri!= null && !nativeFunctionUri.trim().equals(""))
            {
                nativeFunctionUriLiteral = f.createURI(nativeFunctionUri);
            }
            
            URI curationStatusLiteral = null;
            
            if(curationStatus == null)
            {
                curationStatusLiteral = ProjectImpl.getProjectNotCuratedUri();
            }
            else
            {
                curationStatusLiteral = curationStatus;
            }
            
            con.setAutoCommit(false);
            
            con.add(templateInstanceUri, RDF.TYPE, getTemplateTypeUri(), templateInstanceUri);
            con.add(templateInstanceUri, ProjectImpl.getProjectCurationStatusUri(), curationStatusLiteral, templateInstanceUri);
            con.add(templateInstanceUri, getTemplateMatchRegex(), matchRegexLiteral, templateInstanceUri);
            con.add(templateInstanceUri, getTemplateIsNativeFunction(), isNativeFunctionLiteral, templateInstanceUri);
            
            if(nativeFunctionUri!= null && !nativeFunctionUri.trim().equals(""))
            {
                con.add(templateInstanceUri, getTemplateNativeFunctionUri(), nativeFunctionUriLiteral, templateInstanceUri);
            }
            
            con.add(templateInstanceUri, getTemplateTemplateString(), templateStringLiteral, templateInstanceUri);
            con.add(templateInstanceUri, getTemplateContentType(), contentTypeLiteral, templateInstanceUri);
            con.add(templateInstanceUri, getTemplateOrder(), orderLiteral, templateInstanceUri);
            
            if(referencedTemplates != null)
            {
                for(URI nextReferencedTemplate : referencedTemplates)
                {
                    con.add(templateInstanceUri, getTemplateReferencedTemplate(), nextReferencedTemplate, templateInstanceUri);
                }
            }
            
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
            URI dcFormatUri = f.createURI(Constants.DC_NAMESPACE,"format");
            
            con.setAutoCommit(false);
            
            con.add(getTemplateTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            con.add(getTemplateContentTypeSparqlQuery(), dcFormatUri, f.createLiteral("application/sparql-query"), contextKeyUri);
            con.add(getTemplateContentTypeSparqlResultsXml(), dcFormatUri, f.createLiteral("application/sparql-results+xml"), contextKeyUri);
            con.add(getTemplateContentTypeSparqlResultsJson(), dcFormatUri, f.createLiteral("application/sparql-results+json"), contextKeyUri);
            con.add(getTemplateContentTypeRdfXml(), dcFormatUri, f.createLiteral("application/rdf+xml"), contextKeyUri);
            con.add(getTemplateContentTypeN3(), dcFormatUri, f.createLiteral("text/rdf+n3"), contextKeyUri);
            con.add(getTemplateContentTypePlainText(), dcFormatUri, f.createLiteral("text/plain"), contextKeyUri);
            
            con.add(getTemplateContentType(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(getTemplateReferencedTemplate(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
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
    

    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        String prefix = "template_";
        
        return sb.toString();
    }
    

    public String toHtml()
    {
        StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        String prefix = "template_";
        
        return sb.toString();
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return this.title;
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
        return getTemplateTypeUri();
    }
    
    public String getTemplateString()
    {
        return templateString;
    }
    
    public void setTemplateString(String templateString)
    {
        this.templateString = templateString;
    }
    
    public String getContentType()
    {
        return contentType;
    }
    
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }
    
    public String getMatchRegex()
    {
        return matchRegex;
    }
    
    public void setMatchRegex(String matchRegex)
    {
        this.matchRegex = matchRegex;
    }
    
    
    public Collection<URI> getReferencedTemplates()
    {
        return referencedTemplates;
    }
    
    public void setReferencedTemplates(Collection<URI> referencedTemplates)
    {
        this.referencedTemplates = referencedTemplates;
    }
    
    public String getNativeFunctionUri()
    {
        return nativeFunctionUri;
    }
    
    public boolean isNativeFunction()
    {
        return isNativeFunction;
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

    public int compareTo(Template otherTemplate)
    {
        @SuppressWarnings("unused")
        final int BEFORE = -1;
        final int EQUAL = 0;
        @SuppressWarnings("unused")
        final int AFTER = 1;
    
        if ( this == otherTemplate ) 
            return EQUAL;

        return this.getKey().stringValue().compareTo(otherTemplate.getKey().stringValue());
    }


	/**
	 * @param templateTypeUri the templateTypeUri to set
	 */
	public static void setTemplateTypeUri(URI templateTypeUri) {
		TemplateImpl.templateTypeUri = templateTypeUri;
	}


	/**
	 * @return the templateTypeUri
	 */
	public static URI getTemplateTypeUri() {
		return templateTypeUri;
	}


	/**
	 * @param templateContentTypeSparqlQuery the templateContentTypeSparqlQuery to set
	 */
	public static void setTemplateContentTypeSparqlQuery(
			URI templateContentTypeSparqlQuery) {
		TemplateImpl.templateContentTypeSparqlQuery = templateContentTypeSparqlQuery;
	}


	/**
	 * @return the templateContentTypeSparqlQuery
	 */
	public static URI getTemplateContentTypeSparqlQuery() {
		return templateContentTypeSparqlQuery;
	}


	/**
	 * @param templateContentTypeSparqlResultsXml the templateContentTypeSparqlResultsXml to set
	 */
	public static void setTemplateContentTypeSparqlResultsXml(
			URI templateContentTypeSparqlResultsXml) {
		TemplateImpl.templateContentTypeSparqlResultsXml = templateContentTypeSparqlResultsXml;
	}


	/**
	 * @return the templateContentTypeSparqlResultsXml
	 */
	public static URI getTemplateContentTypeSparqlResultsXml() {
		return templateContentTypeSparqlResultsXml;
	}


	/**
	 * @param templateContentTypeSparqlResultsJson the templateContentTypeSparqlResultsJson to set
	 */
	public static void setTemplateContentTypeSparqlResultsJson(
			URI templateContentTypeSparqlResultsJson) {
		TemplateImpl.templateContentTypeSparqlResultsJson = templateContentTypeSparqlResultsJson;
	}


	/**
	 * @return the templateContentTypeSparqlResultsJson
	 */
	public static URI getTemplateContentTypeSparqlResultsJson() {
		return templateContentTypeSparqlResultsJson;
	}


	/**
	 * @param templateContentTypeRdfXml the templateContentTypeRdfXml to set
	 */
	public static void setTemplateContentTypeRdfXml(
			URI templateContentTypeRdfXml) {
		TemplateImpl.templateContentTypeRdfXml = templateContentTypeRdfXml;
	}


	/**
	 * @return the templateContentTypeRdfXml
	 */
	public static URI getTemplateContentTypeRdfXml() {
		return templateContentTypeRdfXml;
	}


	/**
	 * @param templateContentTypeN3 the templateContentTypeN3 to set
	 */
	public static void setTemplateContentTypeN3(URI templateContentTypeN3) {
		TemplateImpl.templateContentTypeN3 = templateContentTypeN3;
	}


	/**
	 * @return the templateContentTypeN3
	 */
	public static URI getTemplateContentTypeN3() {
		return templateContentTypeN3;
	}


	/**
	 * @param templateContentTypePlainText the templateContentTypePlainText to set
	 */
	public static void setTemplateContentTypePlainText(
			URI templateContentTypePlainText) {
		TemplateImpl.templateContentTypePlainText = templateContentTypePlainText;
	}


	/**
	 * @return the templateContentTypePlainText
	 */
	public static URI getTemplateContentTypePlainText() {
		return templateContentTypePlainText;
	}


	/**
	 * @param templateContentType the templateContentType to set
	 */
	public static void setTemplateContentType(URI templateContentType) {
		TemplateImpl.templateContentType = templateContentType;
	}


	/**
	 * @return the templateContentType
	 */
	public static URI getTemplateContentType() {
		return templateContentType;
	}


	/**
	 * @param templateReferencedTemplate the templateReferencedTemplate to set
	 */
	public static void setTemplateReferencedTemplate(
			URI templateReferencedTemplate) {
		TemplateImpl.templateReferencedTemplate = templateReferencedTemplate;
	}


	/**
	 * @return the templateReferencedTemplate
	 */
	public static URI getTemplateReferencedTemplate() {
		return templateReferencedTemplate;
	}


	/**
	 * @param templateMatchRegex the templateMatchRegex to set
	 */
	public static void setTemplateMatchRegex(URI templateMatchRegex) {
		TemplateImpl.templateMatchRegex = templateMatchRegex;
	}


	/**
	 * @return the templateMatchRegex
	 */
	public static URI getTemplateMatchRegex() {
		return templateMatchRegex;
	}


	/**
	 * @param templateIsNativeFunction the templateIsNativeFunction to set
	 */
	public static void setTemplateIsNativeFunction(
			URI templateIsNativeFunction) {
		TemplateImpl.templateIsNativeFunction = templateIsNativeFunction;
	}


	/**
	 * @return the templateIsNativeFunction
	 */
	public static URI getTemplateIsNativeFunction() {
		return templateIsNativeFunction;
	}


	/**
	 * @param templateNativeFunctionUri the templateNativeFunctionUri to set
	 */
	public static void setTemplateNativeFunctionUri(
			URI templateNativeFunctionUri) {
		TemplateImpl.templateNativeFunctionUri = templateNativeFunctionUri;
	}


	/**
	 * @return the templateNativeFunctionUri
	 */
	public static URI getTemplateNativeFunctionUri() {
		return templateNativeFunctionUri;
	}


	/**
	 * @param templateTemplateString the templateTemplateString to set
	 */
	public static void setTemplateTemplateString(URI templateTemplateString) {
		TemplateImpl.templateTemplateString = templateTemplateString;
	}


	/**
	 * @return the templateTemplateString
	 */
	public static URI getTemplateTemplateString() {
		return templateTemplateString;
	}


	/**
	 * @param templateOrder the templateOrder to set
	 */
	public static void setTemplateOrder(URI templateOrder) {
		TemplateImpl.templateOrder = templateOrder;
	}


	/**
	 * @return the templateOrder
	 */
	public static URI getTemplateOrder() {
		return templateOrder;
	}


}
