/**
 * 
 */
package org.queryall.impl;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

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
import org.queryall.exception.InvalidStageException;
import org.queryall.helpers.Constants;
import org.queryall.helpers.StringUtils;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class XsltNormalisationRuleImpl extends NormalisationRuleImpl implements XsltNormalisationRule
{
    private static final Logger log = Logger.getLogger(XsltNormalisationRuleImpl.class.getName());
    private static final boolean _TRACE = XsltNormalisationRuleImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = XsltNormalisationRuleImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = XsltNormalisationRuleImpl.log.isInfoEnabled();
    
    /**
     * @return the xsltRuleStylesheetUri
     */
    public static URI getXsltRuleStylesheetUri()
    {
        return XsltNormalisationRuleImpl.xsltRuleStylesheetUri;
    }
    
    /**
     * @return the xsltRuleTypeUri
     */
    public static URI getXsltRuleTypeUri()
    {
        return XsltNormalisationRuleImpl.xsltRuleTypeUri;
    }
    
    /**
     * @param args
     */
    public static void main(final String[] args)
    {
        final String input =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE test><test testAttr=\"test1234\"></test>";
        final String testXsltStyleSheet =
                "<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><xsl:output method=\"text\" indent=\"no\" encoding=\"UTF-8\" omit-xml-declaration=\"yes\"></xsl:output><xsl:template match=\"/test\"><xsl:value-of select=\"@testAttr\"/></xsl:template></xsl:stylesheet>";
        
        final XsltNormalisationRuleImpl testRule = new XsltNormalisationRuleImpl();
        testRule.setKey(StringUtils.createURI("http://example.org/test/xsltnormalisationrule/42"));
        testRule.setXsltStylesheet(testXsltStyleSheet);
        testRule.addValidStage(NormalisationRuleImpl.getRdfruleStageBeforeResultsImport());
        
        try
        {
            testRule.addStage(NormalisationRuleImpl.getRdfruleStageBeforeResultsImport());
        }
        catch(final InvalidStageException ise)
        {
            System.err.println("Found invalid stage exception");
        }
        
        final String result = (String)testRule.stageBeforeResultsImport(input);
        
        System.out.println("input=\n" + input);
        System.out.println("result=\n" + result);
        
    }
    
    // public static String rdfruleNamespace;
    
    /**
     * @param xsltRuleStylesheetUri
     *            the xsltRuleStylesheetUri to set
     */
    public static void setXsltRuleStylesheetUri(final URI xsltRuleStylesheetUri)
    {
        XsltNormalisationRuleImpl.xsltRuleStylesheetUri = xsltRuleStylesheetUri;
    }
    
    /**
     * @param xsltRuleTypeUri
     *            the xsltRuleTypeUri to set
     */
    public static void setXsltRuleTypeUri(final URI xsltRuleTypeUri)
    {
        XsltNormalisationRuleImpl.xsltRuleTypeUri = xsltRuleTypeUri;
    }
    
    private String xsltStylesheet;
    
    private static URI xsltRuleTypeUri;
    
    private static URI xsltRuleStylesheetUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        XsltNormalisationRuleImpl.setXsltRuleTypeUri(f.createURI(NormalisationRuleImpl.rdfruleNamespace,
                "XsltNormalisationRule"));
        XsltNormalisationRuleImpl.setXsltRuleStylesheetUri(f.createURI(NormalisationRuleImpl.rdfruleNamespace,
                "xsltStylesheet"));
        
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        NormalisationRuleImpl.schemaToRdf(myRepository, contextUri, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(XsltNormalisationRuleImpl.getXsltRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(XsltNormalisationRuleImpl.getXsltRuleTypeUri(), RDFS.LABEL,
                    f.createLiteral("A XSLT based normalisation rule intended to normalise textual XML documents."),
                    contextUri);
            con.add(XsltNormalisationRuleImpl.getXsltRuleTypeUri(), RDFS.SUBCLASSOF,
                    NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextUri);
            
            con.add(XsltNormalisationRuleImpl.getXsltRuleStylesheetUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(XsltNormalisationRuleImpl.getXsltRuleStylesheetUri(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(XsltNormalisationRuleImpl.getXsltRuleStylesheetUri(), RDFS.DOMAIN,
                    XsltNormalisationRuleImpl.getXsltRuleTypeUri(), contextUri);
            con.add(XsltNormalisationRuleImpl.getXsltRuleStylesheetUri(),
                    RDFS.LABEL,
                    f.createLiteral("An XSLT stylesheet that will be used to transform textual queries or result documents"),
                    contextUri);
            
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
            
            XsltNormalisationRuleImpl.log.error("RepositoryException: " + re.getMessage());
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
        
        this.addValidStage(NormalisationRuleImpl.getRdfruleStageQueryVariables());
        this.addValidStage(NormalisationRuleImpl.getRdfruleStageAfterQueryCreation());
        this.addValidStage(NormalisationRuleImpl.getRdfruleStageBeforeResultsImport());
        this.addValidStage(NormalisationRuleImpl.getRdfruleStageAfterResultsToDocument());
    }
    
    /**
     * @param inputStatements
     * @param keyToUse
     * @param modelVersion
     * @throws OpenRDFException
     */
    public XsltNormalisationRuleImpl(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        this.addValidStage(NormalisationRuleImpl.getRdfruleStageQueryVariables());
        this.addValidStage(NormalisationRuleImpl.getRdfruleStageAfterQueryCreation());
        this.addValidStage(NormalisationRuleImpl.getRdfruleStageBeforeResultsImport());
        this.addValidStage(NormalisationRuleImpl.getRdfruleStageAfterResultsToDocument());
        
        final Collection<Statement> currentUnrecognisedStatements = new HashSet<Statement>();
        
        currentUnrecognisedStatements.addAll(this.getUnrecognisedStatements());
        
        this.unrecognisedStatements = new HashSet<Statement>();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(XsltNormalisationRuleImpl._DEBUG)
            {
                XsltNormalisationRuleImpl.log.debug("XsltNormalisationRuleImpl: nextStatement: "
                        + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(XsltNormalisationRuleImpl.getXsltRuleTypeUri()))
            {
                if(XsltNormalisationRuleImpl._TRACE)
                {
                    XsltNormalisationRuleImpl.log
                            .trace("XsltNormalisationRuleImpl: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(XsltNormalisationRuleImpl.getXsltRuleStylesheetUri()))
            {
                this.setXsltStylesheet(nextStatement.getObject().stringValue());
            }
            else
            {
                if(XsltNormalisationRuleImpl._TRACE)
                {
                    XsltNormalisationRuleImpl.log
                            .trace("XsltNormalisationRuleImpl: unrecognisedStatement nextStatement: "
                                    + nextStatement.toString());
                }
                this.unrecognisedStatements.add(nextStatement);
            }
        }
        
        if(XsltNormalisationRuleImpl._DEBUG)
        {
            XsltNormalisationRuleImpl.log.debug("XsltNormalisationRuleImpl constructor: toString()=" + this.toString());
        }
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Collection<URI> getElementTypes()
    {
        final Collection<URI> results = super.getElementTypes();
        
        results.add(XsltNormalisationRuleImpl.getXsltRuleTypeUri());
        return results;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.XsltNormalisationRule#getXsltStylesheet()
     */
    @Override
    public String getXsltStylesheet()
    {
        return this.xsltStylesheet;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.XsltNormalisationRule#setXsltStylesheet(java.lang.String)
     */
    @Override
    public void setXsltStylesheet(final String xsltStylesheet)
    {
        this.xsltStylesheet = xsltStylesheet;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.NormalisationRule#stageAfterQueryCreation(java.lang.Object)
     */
    @Override
    public Object stageAfterQueryCreation(final Object input)
    {
        if(this.getValidStages().contains(NormalisationRuleImpl.getRdfruleStageAfterQueryCreation())
                && this.stages.contains(NormalisationRuleImpl.getRdfruleStageAfterQueryCreation()))
        {
            return this.transformString((String)input);
        }
        else
        {
            return input;
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.NormalisationRule#stageAfterQueryParsing(java.lang.Object)
     */
    @Override
    public Object stageAfterQueryParsing(final Object input)
    {
        return input;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.NormalisationRule#stageAfterResultsImport(java.lang.Object)
     */
    @Override
    public Object stageAfterResultsImport(final Object input)
    {
        return input;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.NormalisationRule#stageAfterResultsToDocument(java.lang.Object)
     */
    @Override
    public Object stageAfterResultsToDocument(final Object input)
    {
        if(this.getValidStages().contains(NormalisationRuleImpl.getRdfruleStageAfterResultsToDocument())
                && this.stages.contains(NormalisationRuleImpl.getRdfruleStageAfterResultsToDocument()))
        {
            return this.transformString((String)input);
        }
        else
        {
            return input;
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.NormalisationRule#stageAfterResultsToPool(java.lang.Object)
     */
    @Override
    public Object stageAfterResultsToPool(final Object input)
    {
        return input;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.NormalisationRule#stageBeforeResultsImport(java.lang.Object)
     */
    @Override
    public Object stageBeforeResultsImport(final Object input)
    {
        XsltNormalisationRuleImpl.log.info("stageBeforeResultsImport input=" + (String)input);
        if(this.getValidStages().contains(NormalisationRuleImpl.getRdfruleStageBeforeResultsImport())
                && this.stages.contains(NormalisationRuleImpl.getRdfruleStageBeforeResultsImport()))
        {
            return this.transformString((String)input);
        }
        else
        {
            XsltNormalisationRuleImpl.log
                    .info("stageBeforeResultsImport returning input unchanged this.getValidStages="
                            + this.getValidStages()
                                    .contains(NormalisationRuleImpl.getRdfruleStageBeforeResultsImport())
                            + " this.getStages()="
                            + this.stages.contains(NormalisationRuleImpl.getRdfruleStageBeforeResultsImport()));
            return input;
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.NormalisationRule#stageQueryVariables(java.lang.Object)
     */
    @Override
    public Object stageQueryVariables(final Object input)
    {
        if(this.getValidStages().contains(NormalisationRuleImpl.getRdfruleStageQueryVariables())
                && this.stages.contains(NormalisationRuleImpl.getRdfruleStageQueryVariables()))
        {
            return this.transformString((String)input);
        }
        else
        {
            return input;
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.BaseQueryAllInterface#toHtml()
     */
    @Override
    public String toHtml()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.BaseQueryAllInterface#toHtmlFormBody()
     */
    @Override
    public String toHtmlFormBody()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super.toRdf(myRepository, keyToUse, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            if(XsltNormalisationRuleImpl._DEBUG)
            {
                XsltNormalisationRuleImpl.log.debug("XsltNormalisationRuleImpl.toRdf: keyToUse=" + keyToUse);
            }
            
            final URI keyUri = this.getKey();
            final Literal xsltStylesheetLiteral = f.createLiteral(this.getXsltStylesheet());
            
            con.setAutoCommit(false);
            
            con.add(keyUri, RDF.TYPE, XsltNormalisationRuleImpl.getXsltRuleTypeUri(), keyToUse);
            
            con.add(keyUri, XsltNormalisationRuleImpl.getXsltRuleStylesheetUri(), xsltStylesheetLiteral, keyToUse);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            XsltNormalisationRuleImpl.log.error("RepositoryException: " + re.getMessage());
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
    
    private String transformString(final String input)
    {
        XsltNormalisationRuleImpl.log.info("input=" + input);
        final StringWriter outputWriter = new StringWriter();
        try
        {
            final TransformerFactory tFactory = TransformerFactory.newInstance();
            // System.out.println("this.getXsltStyleSheet()="+this.getXsltStylesheet());
            final StringReader xsltReader = new StringReader(this.getXsltStylesheet());
            final StringReader inputReader = new StringReader(input);
            
            // Get the XML input document and the stylesheet.
            final Source xmlSource = new StreamSource(inputReader);
            final Source xslSource = new StreamSource(xsltReader);
            // Generate the transformer.
            final Transformer transformer = tFactory.newTransformer(xslSource);
            // Perform the transformation, sending the output to the response.
            transformer.transform(xmlSource, new StreamResult(outputWriter));
        }
        catch(final Exception ex)
        {
            XsltNormalisationRuleImpl.log
                    .error("XsltNormalisationRuleImpl.stageBeforeResultsImport: found exception, returning the original input",
                            ex);
            System.out
                    .println("XsltNormalisationRuleImpl.stageBeforeResultsImport: found exception, returning the original input");
            return input;
        }
        
        XsltNormalisationRuleImpl.log.info("output=" + outputWriter.toString());
        return outputWriter.toString();
    }
    
}
