/**
 * 
 */
package org.queryall.impl.rdfrule;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.base.HtmlExport;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.TransformingRuleSchema;
import org.queryall.api.rdfrule.XsltNormalisationRule;
import org.queryall.api.rdfrule.XsltNormalisationRuleSchema;
import org.queryall.exception.InvalidStageException;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class XsltTransformingRuleImpl extends BaseTransformingRuleImpl implements XsltNormalisationRule, HtmlExport
{
    private static final Logger log = LoggerFactory.getLogger(XsltTransformingRuleImpl.class);
    private static final boolean _TRACE = XsltTransformingRuleImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = XsltTransformingRuleImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = XsltTransformingRuleImpl.log.isInfoEnabled();
    
    // public static final NormalisationRuleEnum XSLT_NORMALISATION_RULE_IMPL_ENUM =
    // NormalisationRuleEnum.register(XsltNormalisationRuleImpl.class.getName(),
    // XsltNormalisationRuleImpl.myTypes());
    
    private static final Set<URI> XSLT_TRANSFORMING_RULE_IMPL_TYPES = new HashSet<URI>();
    
    static
    {
        XsltTransformingRuleImpl.XSLT_TRANSFORMING_RULE_IMPL_TYPES.add(NormalisationRuleSchema
                .getNormalisationRuleTypeUri());
        XsltTransformingRuleImpl.XSLT_TRANSFORMING_RULE_IMPL_TYPES.add(TransformingRuleSchema
                .getTransformingRuleTypeUri());
        XsltTransformingRuleImpl.XSLT_TRANSFORMING_RULE_IMPL_TYPES
                .add(XsltNormalisationRuleSchema.getXsltRuleTypeUri());
    }
    
    /**
     * Test method
     * 
     * @param args
     */
    public static void main(final String[] args)
    {
        final String input =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE test><test testAttr=\"test1234\"></test>";
        final String testXsltStyleSheet =
                "<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><xsl:output method=\"text\" indent=\"no\" encoding=\"UTF-8\" omit-xml-declaration=\"yes\"></xsl:output><xsl:template match=\"/test\"><xsl:value-of select=\"@testAttr\"/></xsl:template></xsl:stylesheet>";
        
        final XsltTransformingRuleImpl testRule = new XsltTransformingRuleImpl();
        testRule.setKey(StringUtils.createURI("http://example.org/test/xsltnormalisationrule/42"));
        testRule.setXsltStylesheet(testXsltStyleSheet);
        try
        {
            testRule.addValidStage(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport());
        }
        catch(InvalidStageException e)
        {
            log.error("InvalidStageException found from hardcoded stage URI insertion, bad things may happen now!", e);
            throw new RuntimeException("Found fatal InvalidStageException in hardcoded stage URI insertion", e);
        }
        
        try
        {
            testRule.addStage(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport());
        }
        catch(final InvalidStageException ise)
        {
            System.err.println("Found invalid stage exception");
        }
        
        final String result = (String)testRule.stageBeforeResultsImport(input);
        
        System.out.println("input=\n" + input);
        System.out.println("result=\n" + result);
        
    }
    
    public static Set<URI> myTypes()
    {
        return XsltTransformingRuleImpl.XSLT_TRANSFORMING_RULE_IMPL_TYPES;
    }
    
    private String xsltStylesheet;
    
    public XsltTransformingRuleImpl()
    {
        super();
        
    }
    
    /**
     * @param inputStatements
     * @param keyToUse
     * @param modelVersion
     * @throws OpenRDFException
     */
    public XsltTransformingRuleImpl(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = new HashSet<Statement>();
        
        currentUnrecognisedStatements.addAll(this.getUnrecognisedStatements());
        
        this.unrecognisedStatements = new HashSet<Statement>();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(XsltTransformingRuleImpl._DEBUG)
            {
                XsltTransformingRuleImpl.log.debug("XsltNormalisationRuleImpl: nextStatement: "
                        + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(XsltNormalisationRuleSchema.getXsltRuleTypeUri()))
            {
                if(XsltTransformingRuleImpl._TRACE)
                {
                    XsltTransformingRuleImpl.log
                            .trace("XsltNormalisationRuleImpl: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(XsltNormalisationRuleSchema.getXsltRuleStylesheetUri()))
            {
                this.setXsltStylesheet(nextStatement.getObject().stringValue());
            }
            else
            {
                if(XsltTransformingRuleImpl._TRACE)
                {
                    XsltTransformingRuleImpl.log
                            .trace("XsltNormalisationRuleImpl: unrecognisedStatement nextStatement: "
                                    + nextStatement.toString());
                }
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(XsltTransformingRuleImpl._DEBUG)
        {
            XsltTransformingRuleImpl.log.debug("XsltNormalisationRuleImpl constructor: toString()=" + this.toString());
        }
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Set<URI> getElementTypes()
    {
        return XsltTransformingRuleImpl.myTypes();
    }
    
    /**
     * @return the validStages
     */
    @Override
    public Set<URI> getValidStages()
    {
        if(this.validStages.size() == 0)
        {
            try
            {
                this.addValidStage(NormalisationRuleSchema.getRdfruleStageQueryVariables());
                this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation());
                this.addValidStage(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport());
                this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument());
            }
            catch(InvalidStageException e)
            {
                log.error("InvalidStageException found from hardcoded stage URI insertion, bad things may happen now!", e);
                throw new RuntimeException("Found fatal InvalidStageException in hardcoded stage URI insertion", e);
            }
        }
        
        return Collections.unmodifiableSet(this.validStages);
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
        if(this.getValidStages().contains(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation())
                && this.stages.contains(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation()))
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
        if(this.getValidStages().contains(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument())
                && this.stages.contains(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument()))
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
        XsltTransformingRuleImpl.log.info("stageBeforeResultsImport input=" + (String)input);
        if(this.getValidStages().contains(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport())
                && this.stages.contains(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport()))
        {
            return this.transformString((String)input);
        }
        else
        {
            XsltTransformingRuleImpl.log.info("stageBeforeResultsImport returning input unchanged this.getValidStages="
                    + this.getValidStages().contains(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport())
                    + " this.getStages()="
                    + this.stages.contains(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport()));
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
        if(this.getValidStages().contains(NormalisationRuleSchema.getRdfruleStageQueryVariables())
                && this.stages.contains(NormalisationRuleSchema.getRdfruleStageQueryVariables()))
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
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... keyToUse)
        throws OpenRDFException
    {
        super.toRdf(myRepository, modelVersion, keyToUse);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            if(XsltTransformingRuleImpl._DEBUG)
            {
                XsltTransformingRuleImpl.log.debug("XsltNormalisationRuleImpl.toRdf: keyToUse=" + keyToUse);
            }
            
            final URI keyUri = this.getKey();
            final Literal xsltStylesheetLiteral = f.createLiteral(this.getXsltStylesheet());
            
            con.setAutoCommit(false);
            
            con.add(keyUri, RDF.TYPE, XsltNormalisationRuleSchema.getXsltRuleTypeUri(), keyToUse);
            
            con.add(keyUri, XsltNormalisationRuleSchema.getXsltRuleStylesheetUri(), xsltStylesheetLiteral, keyToUse);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            XsltTransformingRuleImpl.log.error("RepositoryException: " + re.getMessage());
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
    
    private String transformString(final String input)
    {
        XsltTransformingRuleImpl.log.info("input=" + input);
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
            XsltTransformingRuleImpl.log
                    .error("XsltNormalisationRuleImpl.stageBeforeResultsImport: found exception, returning the original input",
                            ex);
            System.out
                    .println("XsltNormalisationRuleImpl.stageBeforeResultsImport: found exception, returning the original input");
            return input;
        }
        
        XsltTransformingRuleImpl.log.info("output=" + outputWriter.toString());
        return outputWriter.toString();
    }
    
}
