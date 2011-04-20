/**
 * 
 */
package org.queryall.impl;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

/**
 * @author karina
 *
 */
public class XsltNormalisationRule extends NormalisationRuleImpl
{
	/**
	 * @param inputStatements
	 * @param keyToUse
	 * @param modelVersion
	 * @throws OpenRDFException
	 */
	public XsltNormalisationRule(Collection<Statement> inputStatements,
			URI keyToUse, int modelVersion) throws OpenRDFException
	{
		super(inputStatements, keyToUse, modelVersion);
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.queryall.api.NormalisationRule#stageAfterQueryCreation(java.lang.Object)
	 */
	@Override
	public Object stageAfterQueryCreation(Object input)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.queryall.api.NormalisationRule#stageAfterQueryParsing(java.lang.Object)
	 */
	@Override
	public Object stageAfterQueryParsing(Object input)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.queryall.api.NormalisationRule#stageBeforeResultsImport(java.lang.Object)
	 */
	@Override
	public Object stageBeforeResultsImport(Object input)
	{
		StringWriter outputWriter = new StringWriter();
		try
		{	
			TransformerFactory tFactory = TransformerFactory.newInstance();

			StringReader xsltReader = new StringReader("");
			StringReader inputReader = new StringReader((String)input);

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
			
		}
		return outputWriter.toString();
	}

	/* (non-Javadoc)
	 * @see org.queryall.api.NormalisationRule#stageAfterResultsImport(java.lang.Object)
	 */
	@Override
	public Object stageAfterResultsImport(Object input)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.queryall.api.NormalisationRule#stageAfterResultsToPool(java.lang.Object)
	 */
	@Override
	public Object stageAfterResultsToPool(Object input)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.queryall.api.NormalisationRule#stageAfterResultsToDocument(java.lang.Object)
	 */
	@Override
	public Object stageAfterResultsToDocument(Object input)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
