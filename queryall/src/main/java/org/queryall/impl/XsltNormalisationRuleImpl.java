/**
 * 
 */
package org.queryall.impl;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.XsltNormalisationRule;
import org.queryall.helpers.Constants;
import org.queryall.helpers.StringUtils;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class XsltNormalisationRuleImpl extends NormalisationRuleImpl implements XsltNormalisationRule
{
    private static final Logger log = Logger
    .getLogger(XsltNormalisationRuleImpl.class.getName());
	private static final boolean _TRACE = XsltNormalisationRuleImpl.log
	    .isTraceEnabled();
	private static final boolean _DEBUG = XsltNormalisationRuleImpl.log
	    .isDebugEnabled();
	@SuppressWarnings("unused")
	private static final boolean _INFO = XsltNormalisationRuleImpl.log
	    .isInfoEnabled();

	private String xsltStylesheet;

	
    private static URI xsltRuleTypeUri;
    private static URI xsltRuleStylesheetUri;
    
    // public static String rdfruleNamespace;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        XsltNormalisationRuleImpl.setXsltRuleTypeUri(f
                .createURI(XsltNormalisationRuleImpl.rdfruleNamespace, "XsltNormalisationRule"));
        XsltNormalisationRuleImpl.setXsltRuleStylesheetUri(f
                .createURI(XsltNormalisationRuleImpl.rdfruleNamespace, "xsltStylesheet"));

    }
    
    public static boolean schemaToRdf(Repository myRepository, String keyToUse, int modelVersion) throws OpenRDFException
    {
        NormalisationRuleImpl.schemaToRdf(myRepository, keyToUse, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            final URI contextKeyUri = f.createURI(keyToUse);
            con.setAutoCommit(false);
            
            con.add(XsltNormalisationRuleImpl.getXsltRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            con.add(XsltNormalisationRuleImpl.getXsltRuleTypeUri(), RDFS.LABEL, f.createLiteral("A XSLT based normalisation rule intended to normalise textual XML documents."), contextKeyUri);
            con.add(XsltNormalisationRuleImpl.getXsltRuleTypeUri(), RDFS.SUBCLASSOF, NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextKeyUri);


            con.add(XsltNormalisationRuleImpl.getXsltRuleStylesheetUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(XsltNormalisationRuleImpl.getXsltRuleStylesheetUri(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(XsltNormalisationRuleImpl.getXsltRuleStylesheetUri(), RDFS.DOMAIN, XsltNormalisationRuleImpl.getXsltRuleTypeUri(), contextKeyUri);
            con.add(XsltNormalisationRuleImpl.getXsltRuleStylesheetUri(), RDFS.LABEL, f.createLiteral("An XSLT stylesheet that will be used to transform textual queries or result documents"), contextKeyUri);

            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch (final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            if(con != null)
            {
                con.rollback();
            }
            
            XsltNormalisationRuleImpl.log.error("RepositoryException: "
                    + re.getMessage());
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

    public XsltNormalisationRuleImpl()
	{
    	super();
	}
	
	/**
	 * @param inputStatements
	 * @param keyToUse
	 * @param modelVersion
	 * @throws OpenRDFException
	 */
	public XsltNormalisationRuleImpl(Collection<Statement> inputStatements,
			URI keyToUse, int modelVersion) throws OpenRDFException
	{
        super(inputStatements, keyToUse, modelVersion);
        
    	Collection<Statement> currentUnrecognisedStatements = new HashSet<Statement>();
    	
    	currentUnrecognisedStatements.addAll(this.getUnrecognisedStatements());
    	
    	this.unrecognisedStatements = new HashSet<Statement>();
    	
        for(Statement nextStatement : currentUnrecognisedStatements)
        {
            if(XsltNormalisationRuleImpl._DEBUG)
            {
                XsltNormalisationRuleImpl.log
                        .debug("XsltNormalisationRuleImpl: nextStatement: "
                                + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE) && nextStatement.getObject().equals(XsltNormalisationRuleImpl.getXsltRuleTypeUri()))
            {
                if(XsltNormalisationRuleImpl._TRACE)
                {
                    XsltNormalisationRuleImpl.log
                            .trace("XsltNormalisationRuleImpl: found valid type predicate for URI: "
                                    + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(XsltNormalisationRuleImpl.getXsltRuleStylesheetUri()))
            {
            	this.setXsltStylesheet(nextStatement.getObject().stringValue());
            }
            else
            {
                if(_TRACE)
                {
                    log.trace("XsltNormalisationRuleImpl: unrecognisedStatement nextStatement: "+nextStatement.toString());
                }
                this.unrecognisedStatements.add(nextStatement);
            }
        }
        
        this.addValidStage(getRdfruleStageQueryVariables());
        this.addValidStage(getRdfruleStageAfterQueryCreation());
        this.addValidStage(getRdfruleStageBeforeResultsImport());
        this.addValidStage(getRdfruleStageAfterResultsToDocument());
        
        if(XsltNormalisationRuleImpl._DEBUG)
        {
            XsltNormalisationRuleImpl.log
                    .debug("XsltNormalisationRuleImpl constructor: toString()="
                            + this.toString());
        }
	}

    @Override
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        super.toRdf(myRepository, keyToUse, modelVersion);

        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            if(XsltNormalisationRuleImpl._DEBUG)
            {
            	XsltNormalisationRuleImpl.log
                        .debug("XsltNormalisationRuleImpl.toRdf: keyToUse="
                                + keyToUse);
            }
            
            final URI keyUri = keyToUse;
            final Literal xsltStylesheetLiteral = f
                    .createLiteral(this.getXsltStylesheet());

            con.setAutoCommit(false);
            
            con.add(keyUri, RDF.TYPE, XsltNormalisationRuleImpl.getXsltRuleTypeUri(),
                    keyUri);
            
            con.add(keyUri, XsltNormalisationRuleImpl.getXsltRuleStylesheetUri(),
            		xsltStylesheetLiteral, keyUri);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch (final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            XsltNormalisationRuleImpl.log.error("RepositoryException: "
                    + re.getMessage());
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
    
	/* (non-Javadoc)
	 * @see org.queryall.impl.XsltNormalisationRule#setXsltStylesheet(java.lang.String)
	 */
	@Override
	public void setXsltStylesheet(String xsltStylesheet)
	{
		this.xsltStylesheet = xsltStylesheet;
	}

	/* (non-Javadoc)
	 * @see org.queryall.impl.XsltNormalisationRule#getXsltStylesheet()
	 */
	@Override
	public String getXsltStylesheet()
	{
		return xsltStylesheet;
	}

	/* (non-Javadoc)
	 * @see org.queryall.api.BaseQueryAllInterface#toHtmlFormBody()
	 */
	@Override
	public String toHtmlFormBody()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.queryall.api.BaseQueryAllInterface#toHtml()
	 */
	@Override
	public String toHtml()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.queryall.api.NormalisationRule#stageQueryVariables(java.lang.Object)
	 */
	@Override
	public Object stageQueryVariables(Object input)
	{
		if(this.getValidStages().contains(NormalisationRuleImpl.getRdfruleStageQueryVariables()) && this.stages.contains(NormalisationRuleImpl.getRdfruleStageQueryVariables()))
			return transformString((String)input);
		else
			return input;
	}

	/* (non-Javadoc)
	 * @see org.queryall.api.NormalisationRule#stageAfterQueryCreation(java.lang.Object)
	 */
	@Override
	public Object stageAfterQueryCreation(Object input)
	{
		if(this.getValidStages().contains(NormalisationRuleImpl.getRdfruleStageAfterQueryCreation()) && this.stages.contains(NormalisationRuleImpl.getRdfruleStageAfterQueryCreation()))
			return transformString((String)input);
		else
			return input;
	}

	/* (non-Javadoc)
	 * @see org.queryall.api.NormalisationRule#stageAfterQueryParsing(java.lang.Object)
	 */
	@Override
	public Object stageAfterQueryParsing(Object input)
	{
		return input;
	}

	/* (non-Javadoc)
	 * @see org.queryall.api.NormalisationRule#stageBeforeResultsImport(java.lang.Object)
	 */
	@Override
	public Object stageBeforeResultsImport(Object input)
	{
		if(this.getValidStages().contains(NormalisationRuleImpl.getRdfruleStageBeforeResultsImport()) && this.stages.contains(NormalisationRuleImpl.getRdfruleStageBeforeResultsImport()))
			return transformString((String)input);
		else
			return input;
	}
	

	/* (non-Javadoc)
	 * @see org.queryall.api.NormalisationRule#stageAfterResultsImport(java.lang.Object)
	 */
	@Override
	public Object stageAfterResultsImport(Object input)
	{
		return input;
	}

	/* (non-Javadoc)
	 * @see org.queryall.api.NormalisationRule#stageAfterResultsToPool(java.lang.Object)
	 */
	@Override
	public Object stageAfterResultsToPool(Object input)
	{
		return input;
	}

	/* (non-Javadoc)
	 * @see org.queryall.api.NormalisationRule#stageAfterResultsToDocument(java.lang.Object)
	 */
	@Override
	public Object stageAfterResultsToDocument(Object input)
	{
		if(this.getValidStages().contains(NormalisationRuleImpl.getRdfruleStageAfterResultsToDocument()) && this.stages.contains(NormalisationRuleImpl.getRdfruleStageAfterResultsToDocument()))
			return transformString((String)input);
		else
			return input;
	}

	private String transformString(String input)
	{
		StringWriter outputWriter = new StringWriter();
		try
		{	
			TransformerFactory tFactory = TransformerFactory.newInstance();
			System.out.println("this.getXsltStyleSheet()="+this.getXsltStylesheet());
			StringReader xsltReader = new StringReader(this.getXsltStylesheet());
			StringReader inputReader = new StringReader(input);

			// Get the XML input document and the stylesheet.
			Source xmlSource = new StreamSource(inputReader);
			Source xslSource = new StreamSource(xsltReader);
			// Generate the transformer.
			Transformer transformer = tFactory.newTransformer(xslSource);
			// Perform the transformation, sending the output to the response.
			transformer.transform(xmlSource, new StreamResult(outputWriter));
		}
		catch(Exception ex)
		{
			log.error("XsltNormalisationRuleImpl.stageBeforeResultsImport: found exception, returning the original input", ex);
			System.out.println("XsltNormalisationRuleImpl.stageBeforeResultsImport: found exception, returning the original input");
			return input;
		}
		
		return outputWriter.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String input = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE test><test testAttr=\"test1234\"></test>";
		String testXsltStyleSheet = "<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><xsl:output method=\"text\" indent=\"no\" encoding=\"UTF-8\" omit-xml-declaration=\"yes\"></xsl:output><xsl:template match=\"/test\"><xsl:value-of select=\"@testAttr\"/></xsl:template></xsl:stylesheet>";

		XsltNormalisationRuleImpl testRule = new XsltNormalisationRuleImpl();
		testRule.setKey(StringUtils.createURI("http://example.org/test/xsltnormalisationrule/42"));
		testRule.setXsltStylesheet(testXsltStyleSheet);
		testRule.addStage(getRdfruleStageBeforeResultsImport());
        testRule.addValidStage(getRdfruleStageBeforeResultsImport());
		
		String result = (String)testRule.stageBeforeResultsImport(input);

		System.out.println("input=\n"+input);
		System.out.println("result=\n"+result);
		
	}

	/**
	 * @param xsltRuleTypeUri the xsltRuleTypeUri to set
	 */
	public static void setXsltRuleTypeUri(URI xsltRuleTypeUri)
	{
		XsltNormalisationRuleImpl.xsltRuleTypeUri = xsltRuleTypeUri;
	}

	/**
	 * @return the xsltRuleTypeUri
	 */
	public static URI getXsltRuleTypeUri()
	{
		return xsltRuleTypeUri;
	}

	/**
	 * @param xsltRuleStylesheetUri the xsltRuleStylesheetUri to set
	 */
	public static void setXsltRuleStylesheetUri(URI xsltRuleStylesheetUri)
	{
		XsltNormalisationRuleImpl.xsltRuleStylesheetUri = xsltRuleStylesheetUri;
	}

	/**
	 * @return the xsltRuleStylesheetUri
	 */
	public static URI getXsltRuleStylesheetUri()
	{
		return xsltRuleStylesheetUri;
	}

}
