/**
 * 
 */
package org.queryall.reasoning;

import java.util.Collection;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.impl.NormalisationRuleImpl;

/**
 * @author karina
 *
 */
public class SadiNormalisationRuleImpl extends NormalisationRuleImpl
{

	/**
	 * 
	 */
	public SadiNormalisationRuleImpl()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param inputStatements
	 * @param keyToUse
	 * @param modelVersion
	 * @throws OpenRDFException
	 */
	public SadiNormalisationRuleImpl(Collection<Statement> inputStatements,
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
		// TODO Auto-generated method stub
		return null;
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
