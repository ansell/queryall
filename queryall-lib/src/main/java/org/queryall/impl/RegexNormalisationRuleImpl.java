
package org.queryall.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.PatternSyntaxException;

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

import org.queryall.api.RegexNormalisationRule;
import org.queryall.helpers.*;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RegexNormalisationRuleImpl extends NormalisationRuleImpl implements RegexNormalisationRule
{
    private static final Logger log = Logger
            .getLogger(RegexNormalisationRuleImpl.class.getName());
    private static final boolean _TRACE = RegexNormalisationRuleImpl.log
            .isTraceEnabled();
    private static final boolean _DEBUG = RegexNormalisationRuleImpl.log
            .isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RegexNormalisationRuleImpl.log
            .isInfoEnabled();
    
    private String inputMatchRegex = "";
    private String inputReplaceRegex = "";
    private String outputMatchRegex = "";
    private String outputReplaceRegex = "";

    private static URI regexruleTypeUri;
    private static URI regexruleInputMatchRegex;
    private static URI regexruleInputReplaceRegex;
    private static URI regexruleOutputMatchRegex;
    private static URI regexruleOutputReplaceRegex;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        RegexNormalisationRuleImpl.setRegexRuleTypeUri(f.createURI(RegexNormalisationRuleImpl.rdfruleNamespace, "RegexNormalisationRule"));
        RegexNormalisationRuleImpl.setRegexRuleInputMatchRegex(f
                .createURI(RegexNormalisationRuleImpl.rdfruleNamespace, "inputMatchRegex"));
        RegexNormalisationRuleImpl.setRegexRuleInputReplaceRegex(f
                .createURI(RegexNormalisationRuleImpl.rdfruleNamespace, "inputReplaceRegex"));
        RegexNormalisationRuleImpl.setRegexRuleOutputMatchRegex(f
                .createURI(RegexNormalisationRuleImpl.rdfruleNamespace, "outputMatchRegex"));
        RegexNormalisationRuleImpl.setRegexRuleOutputReplaceRegex(f
                .createURI(RegexNormalisationRuleImpl.rdfruleNamespace, "outputReplaceRegex"));
    }
    
    public RegexNormalisationRuleImpl()
    {
    	super();
    }
    
    // keyToUse is the URI of the next instance that can be found in
    // myRepository
    public RegexNormalisationRuleImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion)
            throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
    	Collection<Statement> currentUnrecognisedStatements = new HashSet<Statement>();
    	
    	currentUnrecognisedStatements.addAll(this.getUnrecognisedStatements());
    	
    	this.unrecognisedStatements = new HashSet<Statement>();
    	
        for(Statement nextStatement : currentUnrecognisedStatements)
        {
            if(RegexNormalisationRuleImpl._TRACE)
            {
                RegexNormalisationRuleImpl.log
                        .trace("RegexNormalisationRuleImpl: nextStatement: "
                                + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && ( nextStatement.getObject().equals(
                            RegexNormalisationRuleImpl.getRegexRuleTypeUri()) || nextStatement.getObject().equals(
                            RegexNormalisationRuleImpl.getRegexRuleTypeUri()) )
              )
            {
                if(RegexNormalisationRuleImpl._TRACE)
                {
                    RegexNormalisationRuleImpl.log
                            .trace("RegexNormalisationRuleImpl: found valid type predicate for URI: "
                                    + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(
                    RegexNormalisationRuleImpl.getRegexRuleInputMatchRegex()))
            {
                this.setInputMatchRegex(nextStatement.getObject()
                        .stringValue());
            }
            else if(nextStatement.getPredicate().equals(
                    RegexNormalisationRuleImpl.getRegexRuleInputReplaceRegex()))
            {
                this.setInputReplaceRegex(nextStatement.getObject()
                        .stringValue());
            }
            else if(nextStatement.getPredicate().equals(
                    RegexNormalisationRuleImpl.getRegexRuleOutputMatchRegex()))
            {
                this.setOutputMatchRegex(nextStatement.getObject()
                        .stringValue());
            }
            else if(nextStatement.getPredicate().equals(
                    RegexNormalisationRuleImpl.getRegexRuleOutputReplaceRegex()))
            {
                this.setOutputReplaceRegex(nextStatement.getObject()
                        .stringValue());
            }
            else
            {
                if(_TRACE)
                {
                    log.trace("RegexNormalisationRuleImpl: unrecognisedStatement nextStatement: "+nextStatement.toString());
                }
                this.unrecognisedStatements.add(nextStatement);
            }
        }
        
        this.addValidStage(getRdfruleStageQueryVariables());
        this.addValidStage(getRdfruleStageAfterQueryCreation());
        this.addValidStage(getRdfruleStageBeforeResultsImport());
        this.addValidStage(getRdfruleStageAfterResultsToDocument());

        if(RegexNormalisationRuleImpl._TRACE)
        {
            RegexNormalisationRuleImpl.log
                    .trace("RegexNormalisationRuleImpl.fromRdf: would have returned... result="
                            + this.toString());
        }
    }
    
    public static boolean schemaToRdf(Repository myRepository, URI contextUri, int modelVersion) throws OpenRDFException
    {
        NormalisationRuleImpl.schemaToRdf(myRepository, contextUri, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(RegexNormalisationRuleImpl.getRegexRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(RegexNormalisationRuleImpl.getRegexRuleTypeUri(), RDFS.SUBCLASSOF, NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextUri);
            con.add(RegexNormalisationRuleImpl.getRegexRuleTypeUri(), RDFS.LABEL, f.createLiteral("A regular expression based normalisation rule intended to denormalise parts of queries to match endpoints, and renormalise the output of the query to match the normalised form."), contextUri);

            con.add(RegexNormalisationRuleImpl.getRegexRuleInputMatchRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(RegexNormalisationRuleImpl.getRegexRuleInputMatchRegex(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(RegexNormalisationRuleImpl.getRegexRuleInputMatchRegex(), RDFS.DOMAIN, RegexNormalisationRuleImpl.getRegexRuleTypeUri(), contextUri);
            con.add(RegexNormalisationRuleImpl.getRegexRuleInputMatchRegex(), RDFS.LABEL, f.createLiteral("The input regular expression that is used to identify whether the denormalisation part of the rule matches the data, and if so, whether there are any matching groups that need to be substitued into the replacement pattern."), contextUri);

            con.add(RegexNormalisationRuleImpl.getRegexRuleInputReplaceRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(RegexNormalisationRuleImpl.getRegexRuleInputReplaceRegex(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(RegexNormalisationRuleImpl.getRegexRuleInputReplaceRegex(), RDFS.DOMAIN, RegexNormalisationRuleImpl.getRegexRuleTypeUri(), contextUri);
            con.add(RegexNormalisationRuleImpl.getRegexRuleInputReplaceRegex(), RDFS.LABEL, f.createLiteral("The pattern that is used together with the input match regular expression to define what the denormalised data should be."), contextUri);

            con.add(RegexNormalisationRuleImpl.getRegexRuleOutputMatchRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(RegexNormalisationRuleImpl.getRegexRuleOutputMatchRegex(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(RegexNormalisationRuleImpl.getRegexRuleOutputMatchRegex(), RDFS.DOMAIN, RegexNormalisationRuleImpl.getRegexRuleTypeUri(), contextUri);
            con.add(RegexNormalisationRuleImpl.getRegexRuleOutputMatchRegex(), RDFS.LABEL, f.createLiteral("The output regular expression that is used to identify whether the renormalisation part of the rule matches the data, and if so, whether there are any matching groups that need to be substitued into the replacement pattern."), contextUri);

            con.add(RegexNormalisationRuleImpl.getRegexRuleOutputReplaceRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(RegexNormalisationRuleImpl.getRegexRuleOutputReplaceRegex(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(RegexNormalisationRuleImpl.getRegexRuleOutputReplaceRegex(), RDFS.DOMAIN, RegexNormalisationRuleImpl.getRegexRuleTypeUri(), contextUri);
            con.add(RegexNormalisationRuleImpl.getRegexRuleOutputReplaceRegex(), RDFS.LABEL, f.createLiteral("The pattern that is used together with the output match regular expression to define what the normalised data should be."), contextUri);

            // for(String nextValidStage : validStages)
            // {
                // con.add(RegexNormalisationRuleImpl.regexruleTypeUri, NormalisationRule.rdfruleTypeValidForStage,
                    // f.createURI(nextValidStage), contextKeyUri);
            // }

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
            
            RegexNormalisationRuleImpl.log.error("RepositoryException: "
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
    
    public String applyInputRuleToString(String inputText)
    {
        return this.applyRegex(inputText, this.getInputMatchRegex(),
                this.getInputReplaceRegex());
    }
    
    public String applyOutputRuleToString(String inputText)
    {
        return this.applyRegex(inputText, this.getOutputMatchRegex(),
                this.getOutputReplaceRegex());
    }
    
    private String applyRegex(String inputText, String matchRegex,
            String replaceRegex)
    {
        try
        {
            if((matchRegex == null) || (replaceRegex == null))
            {
                if(RegexNormalisationRuleImpl._TRACE)
                {
                    RegexNormalisationRuleImpl.log
                            .trace("RegexNormalisationRuleImpl.applyRegex: something was null matchRegex="
                                    + matchRegex
                                    + ", replaceRegex="
                                    + replaceRegex);
                }
                
                return inputText;
            }
            
            if(RegexNormalisationRuleImpl._DEBUG)
            {
                RegexNormalisationRuleImpl.log
                        .debug("RegexNormalisationRuleImpl.applyRegex: matchRegex="
                                + matchRegex + ", replaceRegex=" + replaceRegex);
            }
            
            if(matchRegex.trim().equals(""))
            {
                if(RegexNormalisationRuleImpl._DEBUG)
                {
                    RegexNormalisationRuleImpl.log
                            .debug("RegexNormalisationRuleImpl.applyRegex: matchRegex was empty, returning inputText");
                }
                
                return inputText;
            }
            
            String debugInputText = "";
            
            // only take a copy of the string if we need it for debugging
            if(RegexNormalisationRuleImpl._DEBUG)
            {
                debugInputText = inputText;
            }
            
            inputText = inputText.replaceAll(matchRegex, replaceRegex);
            
            if(RegexNormalisationRuleImpl._DEBUG)
            {
                RegexNormalisationRuleImpl.log
                        .debug("RegexNormalisationRuleImpl.applyRegex: regex complete input="
                                + debugInputText);
                RegexNormalisationRuleImpl.log
                        .debug("RegexNormalisationRuleImpl.applyRegex: regex complete result="
                                + inputText);
            }
        }
        catch (final PatternSyntaxException pse)
        {
            RegexNormalisationRuleImpl.log
                    .error("RegexNormalisationRuleImpl.applyRegex: PatternSyntaxException="
                            + pse.getMessage());
        }
        catch (final IllegalArgumentException iae)
        {
            RegexNormalisationRuleImpl.log
                    .error("RegexNormalisationRuleImpl.applyRegex: IllegalArgumentException="
                            + iae.getMessage());
        }
        catch (final IndexOutOfBoundsException ioobe)
        {
            RegexNormalisationRuleImpl.log
                    .error("RegexNormalisationRuleImpl.applyRegex: IndexOutOfBoundsException="
                            + ioobe.getMessage());
        }
        
        return inputText;
    }
    
    @Override
    public Object stageQueryVariables(Object input)
    {
        return stages.contains(getRdfruleStageQueryVariables()) ? this.applyInputRuleToString((String)input) : input ;
    }
    
    @Override
    public Object stageAfterQueryCreation(Object input)
    {
        return stages.contains(getRdfruleStageAfterQueryCreation()) ? this.applyInputRuleToString((String)input) : input ;
    }

    @Override
    public Object stageAfterQueryParsing(Object input)
    {
        return input;
    }

    @Override
    public Object stageBeforeResultsImport(Object input)
    {
        return stages.contains(getRdfruleStageBeforeResultsImport()) ? this.applyOutputRuleToString((String)input) : input ;
    }

    @Override
    public Object stageAfterResultsImport(Object input)
    {
        return input;
    }

    @Override
    public Object stageAfterResultsToPool(Object input)
    {
        return input;
    }

    @Override
    public Object stageAfterResultsToDocument(Object input)
    {
        return stages.contains(getRdfruleStageAfterResultsToDocument()) ? this.applyOutputRuleToString((String)input) : input ;
    }

    // NOTE: it is quite okay to have an empty replace regex, but an empty match
    // is not considered useful here
    /* (non-Javadoc)
	 * @see org.queryall.impl.RegexNormalisationRule#hasInputRule()
	 */
    @Override
	public boolean hasInputRule()
    {
        return (this.getInputMatchRegex() != null)
                && (this.getInputReplaceRegex() != null)
                && !this.getInputMatchRegex().trim().equals("");
    }
    
    // NOTE: it is quite okay to have an empty replace regex, but an empty match
    // is not considered useful here
    /* (non-Javadoc)
	 * @see org.queryall.impl.RegexNormalisationRule#hasOutputRule()
	 */
    @Override
	public boolean hasOutputRule()
    {
        return (this.getOutputMatchRegex() != null)
                && (this.getOutputReplaceRegex() != null)
                && !this.getOutputMatchRegex().trim().equals("");
    }
    
    
    @Override
    public String toHtml()
    {
        String result = "";
        
        result += "<div class=\"rulekey\">Rule Key: "
                + StringUtils.xmlEncodeString(this.getKey().stringValue()) + "</div>\n";
        result += "<div class=\"description\">Description: "
                + StringUtils.xmlEncodeString(this.getDescription()) + "</div>\n";
        result += "<div class=\"order\">Order: "
                + StringUtils.xmlEncodeString(this.getOrder() + "") + "</div>\n";
        result += "<div class=\"inputmatchregex\">Input Match Regex: "
                + StringUtils.xmlEncodeString(this.getInputMatchRegex()) + "</div>\n";
        result += "<div class=\"inputreplaceregex\">Input Replace Regex: "
                + StringUtils.xmlEncodeString(this.getInputReplaceRegex())
                + "</div>\n";
        result += "<div class=\"outputmatchregex\">Output Match Regex: "
                + StringUtils.xmlEncodeString(this.getOutputMatchRegex()) + "</div>\n";
        result += "<div class=\"outputreplaceregex\">Output Replace Regex: "
                + StringUtils.xmlEncodeString(this.getOutputReplaceRegex())
                + "</div>\n";
        
        return result;
    }
    
    @Override
    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        String prefix = "rdfrule_";
        
        return sb.toString();
    }
    
    @Override
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        super.toRdf(myRepository, keyToUse, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            if(RegexNormalisationRuleImpl._DEBUG)
            {
                RegexNormalisationRuleImpl.log
                        .debug("RegexNormalisationRuleImpl.toRdf: keyToUse="
                                + keyToUse);
            }
            
            final URI keyUri = this.getKey();
            
            final Literal inputMatchRegexLiteral = f
                    .createLiteral(this.getInputMatchRegex());
            final Literal inputReplaceRegexLiteral = f
                    .createLiteral(this.getInputReplaceRegex());
            final Literal outputMatchRegexLiteral = f
                    .createLiteral(this.getOutputMatchRegex());
            final Literal outputReplaceRegexLiteral = f
                    .createLiteral(this.getOutputReplaceRegex());
            
            con.setAutoCommit(false);
            
            con.add(keyUri, RDF.TYPE, RegexNormalisationRuleImpl.getRegexRuleTypeUri(),
            		keyToUse);
            con.add(keyUri, RegexNormalisationRuleImpl.getRegexRuleInputMatchRegex(),
                    inputMatchRegexLiteral, keyToUse);
            con.add(keyUri, RegexNormalisationRuleImpl.getRegexRuleInputReplaceRegex(),
                    inputReplaceRegexLiteral, keyToUse);
            con.add(keyUri, RegexNormalisationRuleImpl.getRegexRuleOutputMatchRegex(),
                    outputMatchRegexLiteral, keyToUse);
            con.add(keyUri, RegexNormalisationRuleImpl.getRegexRuleOutputReplaceRegex(),
                    outputReplaceRegexLiteral, keyToUse);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch (final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            RegexNormalisationRuleImpl.log.error("RepositoryException: "
                    + re.getMessage());
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
    
    @Override
    public String toString()
    {
        String result = "\n";
        
        result += "key=" + this.getKey() + "\n";
        result += "order=" + this.getOrder()+ "\n";
        result += "inputMatchRegex=" + this.getInputMatchRegex() + "\n";
        result += "inputReplaceRegex=" + this.getInputReplaceRegex() + "\n";
        result += "outputMatchRegex=" + this.getOutputMatchRegex() + "\n";
        result += "outputReplaceRegex=" + this.getOutputReplaceRegex() + "\n";
        result += "description=" + this.getDescription()+ "\n";
        
        return result;
    }
    
    /**
     * @return the URI used for the rdf Type of these elements
     */
    @Override
    public URI getElementType()
    {
        return getRegexRuleTypeUri();
    }

	/* (non-Javadoc)
	 * @see org.queryall.impl.RegexNormalisationRule#setInputMatchRegex(java.lang.String)
	 */
	@Override
	public void setInputMatchRegex(String inputMatchRegex) {
		this.inputMatchRegex = inputMatchRegex;
	}

	/* (non-Javadoc)
	 * @see org.queryall.impl.RegexNormalisationRule#getInputMatchRegex()
	 */
	@Override
	public String getInputMatchRegex() {
		return inputMatchRegex;
	}

	/* (non-Javadoc)
	 * @see org.queryall.impl.RegexNormalisationRule#setInputReplaceRegex(java.lang.String)
	 */
	@Override
	public void setInputReplaceRegex(String inputReplaceRegex) {
		this.inputReplaceRegex = inputReplaceRegex;
	}

	/* (non-Javadoc)
	 * @see org.queryall.impl.RegexNormalisationRule#getInputReplaceRegex()
	 */
	@Override
	public String getInputReplaceRegex() {
		return inputReplaceRegex;
	}

	/* (non-Javadoc)
	 * @see org.queryall.impl.RegexNormalisationRule#setOutputMatchRegex(java.lang.String)
	 */
	@Override
	public void setOutputMatchRegex(String outputMatchRegex) {
		this.outputMatchRegex = outputMatchRegex;
	}

	/* (non-Javadoc)
	 * @see org.queryall.impl.RegexNormalisationRule#getOutputMatchRegex()
	 */
	@Override
	public String getOutputMatchRegex() {
		return outputMatchRegex;
	}

	/* (non-Javadoc)
	 * @see org.queryall.impl.RegexNormalisationRule#setOutputReplaceRegex(java.lang.String)
	 */
	@Override
	public void setOutputReplaceRegex(String outputReplaceRegex) {
		this.outputReplaceRegex = outputReplaceRegex;
	}

	/* (non-Javadoc)
	 * @see org.queryall.impl.RegexNormalisationRule#getOutputReplaceRegex()
	 */
	@Override
	public String getOutputReplaceRegex() {
		return outputReplaceRegex;
	}

	/**
	 * @param regexruleTypeUri the regexruleTypeUri to set
	 */
	public static void setRegexRuleTypeUri(URI regexruleTypeUri) {
		RegexNormalisationRuleImpl.regexruleTypeUri = regexruleTypeUri;
	}

	/**
	 * @return the regexruleTypeUri
	 */
	public static URI getRegexRuleTypeUri() {
		return regexruleTypeUri;
	}

	/**
	 * @param rdfruleInputMatchRegex the rdfruleInputMatchRegex to set
	 */
	public static void setRegexRuleInputMatchRegex(
			URI rdfruleInputMatchRegex) {
		RegexNormalisationRuleImpl.regexruleInputMatchRegex = rdfruleInputMatchRegex;
	}

	/**
	 * @return the rdfruleInputMatchRegex
	 */
	public static URI getRegexRuleInputMatchRegex() {
		return regexruleInputMatchRegex;
	}

	/**
	 * @param rdfruleInputReplaceRegex the rdfruleInputReplaceRegex to set
	 */
	public static void setRegexRuleInputReplaceRegex(
			URI rdfruleInputReplaceRegex) {
		RegexNormalisationRuleImpl.regexruleInputReplaceRegex = rdfruleInputReplaceRegex;
	}

	/**
	 * @return the rdfruleInputReplaceRegex
	 */
	public static URI getRegexRuleInputReplaceRegex() {
		return regexruleInputReplaceRegex;
	}

	/**
	 * @param rdfruleOutputMatchRegex the rdfruleOutputMatchRegex to set
	 */
	public static void setRegexRuleOutputMatchRegex(
			URI rdfruleOutputMatchRegex) {
		RegexNormalisationRuleImpl.regexruleOutputMatchRegex = rdfruleOutputMatchRegex;
	}

	/**
	 * @return the rdfruleOutputMatchRegex
	 */
	public static URI getRegexRuleOutputMatchRegex() {
		return regexruleOutputMatchRegex;
	}

	/**
	 * @param rdfruleOutputReplaceRegex the rdfruleOutputReplaceRegex to set
	 */
	public static void setRegexRuleOutputReplaceRegex(
			URI rdfruleOutputReplaceRegex) {
		RegexNormalisationRuleImpl.regexruleOutputReplaceRegex = rdfruleOutputReplaceRegex;
	}

	/**
	 * @return the rdfruleOutputReplaceRegex
	 */
	public static URI getRegexRuleOutputReplaceRegex() {
		return regexruleOutputReplaceRegex;
	}
}