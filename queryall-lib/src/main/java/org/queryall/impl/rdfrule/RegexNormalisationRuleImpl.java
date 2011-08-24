package org.queryall.impl.rdfrule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.rdfrule.NormalisationRuleEnum;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.RegexNormalisationRule;
import org.queryall.api.rdfrule.RegexNormalisationRuleSchema;
import org.queryall.api.utils.Constants;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RegexNormalisationRuleImpl extends NormalisationRuleImpl implements RegexNormalisationRule
{
    private static final Logger log = LoggerFactory.getLogger(RegexNormalisationRuleImpl.class);
    private static final boolean _TRACE = RegexNormalisationRuleImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = RegexNormalisationRuleImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RegexNormalisationRuleImpl.log.isInfoEnabled();
    
    static
    {
        // register this normalisation rule implementation with the central register
        NormalisationRuleEnum.register(RegexNormalisationRuleImpl.class.getName(), RegexNormalisationRuleImpl.myTypes());
    }
    
    private static List<URI> myTypes()
    {
        List<URI> results = new ArrayList<URI>(2);
        
        results.add(NormalisationRuleSchema.getNormalisationRuleTypeUri());
        results.add(RegexNormalisationRuleSchema.getRegexRuleTypeUri());
        
        return results;
    }

    private String inputMatchRegex = "";
    
    private String inputReplaceRegex = "";
    
    private String outputMatchRegex = "";
    
    private String outputReplaceRegex = "";
    
    public RegexNormalisationRuleImpl()
    {
        super();
        
        this.addValidStage(NormalisationRuleSchema.getRdfruleStageQueryVariables());
        this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation());
        this.addValidStage(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport());
        this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument());
    }
    
    // keyToUse is the URI of the next instance that can be found in
    // myRepository
    public RegexNormalisationRuleImpl(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        this.addValidStage(NormalisationRuleSchema.getRdfruleStageQueryVariables());
        this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation());
        this.addValidStage(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport());
        this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument());
        
        final Collection<Statement> currentUnrecognisedStatements = new HashSet<Statement>();
        
        currentUnrecognisedStatements.addAll(this.getUnrecognisedStatements());
        
        this.unrecognisedStatements = new HashSet<Statement>();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(RegexNormalisationRuleImpl._TRACE)
            {
                RegexNormalisationRuleImpl.log.trace("RegexNormalisationRuleImpl: nextStatement: "
                        + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && (nextStatement.getObject().equals(RegexNormalisationRuleSchema.getRegexRuleTypeUri()) || nextStatement
                            .getObject().equals(RegexNormalisationRuleSchema.getRegexRuleTypeUri())))
            {
                if(RegexNormalisationRuleImpl._TRACE)
                {
                    RegexNormalisationRuleImpl.log
                            .trace("RegexNormalisationRuleImpl: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(RegexNormalisationRuleSchema.getRegexRuleInputMatchRegex()))
            {
                this.setInputMatchRegex(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(RegexNormalisationRuleSchema.getRegexRuleInputReplaceRegex()))
            {
                this.setInputReplaceRegex(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(RegexNormalisationRuleSchema.getRegexRuleOutputMatchRegex()))
            {
                this.setOutputMatchRegex(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(RegexNormalisationRuleSchema.getRegexRuleOutputReplaceRegex()))
            {
                this.setOutputReplaceRegex(nextStatement.getObject().stringValue());
            }
            else
            {
                if(RegexNormalisationRuleImpl._TRACE)
                {
                    RegexNormalisationRuleImpl.log
                            .trace("RegexNormalisationRuleImpl: unrecognisedStatement nextStatement: "
                                    + nextStatement.toString());
                }
                this.unrecognisedStatements.add(nextStatement);
            }
        }
        
        if(RegexNormalisationRuleImpl._TRACE)
        {
            RegexNormalisationRuleImpl.log.trace("RegexNormalisationRuleImpl.fromRdf: would have returned... result="
                    + this.toString());
        }
    }
    
    public String applyInputRuleToString(final String inputText)
    {
        return this.applyRegex(inputText, this.getInputMatchRegex(), this.getInputReplaceRegex());
    }
    
    public String applyOutputRuleToString(final String inputText)
    {
        return this.applyRegex(inputText, this.getOutputMatchRegex(), this.getOutputReplaceRegex());
    }
    
    private String applyRegex(String inputText, final String matchRegex, final String replaceRegex)
    {
        try
        {
            if((matchRegex == null) || (replaceRegex == null))
            {
                if(RegexNormalisationRuleImpl._TRACE)
                {
                    RegexNormalisationRuleImpl.log
                            .trace("RegexNormalisationRuleImpl.applyRegex: something was null matchRegex=" + matchRegex
                                    + ", replaceRegex=" + replaceRegex);
                }
                
                return inputText;
            }
            
            if(RegexNormalisationRuleImpl._DEBUG)
            {
                RegexNormalisationRuleImpl.log.debug("RegexNormalisationRuleImpl.applyRegex: matchRegex=" + matchRegex
                        + ", replaceRegex=" + replaceRegex);
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
                RegexNormalisationRuleImpl.log.debug("RegexNormalisationRuleImpl.applyRegex: regex complete input="
                        + debugInputText);
                RegexNormalisationRuleImpl.log.debug("RegexNormalisationRuleImpl.applyRegex: regex complete result="
                        + inputText);
            }
        }
        catch(final PatternSyntaxException pse)
        {
            RegexNormalisationRuleImpl.log.error("RegexNormalisationRuleImpl.applyRegex: PatternSyntaxException="
                    + pse.getMessage());
        }
        catch(final IllegalArgumentException iae)
        {
            RegexNormalisationRuleImpl.log.error("RegexNormalisationRuleImpl.applyRegex: IllegalArgumentException="
                    + iae.getMessage());
        }
        catch(final IndexOutOfBoundsException ioobe)
        {
            RegexNormalisationRuleImpl.log.error("RegexNormalisationRuleImpl.applyRegex: IndexOutOfBoundsException="
                    + ioobe.getMessage());
        }
        
        return inputText;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Collection<URI> getElementTypes()
    {
        return myTypes();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.RegexNormalisationRule#getInputMatchRegex()
     */
    @Override
    public String getInputMatchRegex()
    {
        return this.inputMatchRegex;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.RegexNormalisationRule#getInputReplaceRegex()
     */
    @Override
    public String getInputReplaceRegex()
    {
        return this.inputReplaceRegex;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.RegexNormalisationRule#getOutputMatchRegex()
     */
    @Override
    public String getOutputMatchRegex()
    {
        return this.outputMatchRegex;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.RegexNormalisationRule#getOutputReplaceRegex()
     */
    @Override
    public String getOutputReplaceRegex()
    {
        return this.outputReplaceRegex;
    }
    
    // NOTE: it is quite okay to have an empty replace regex, but an empty match
    // is not considered useful here
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.RegexNormalisationRule#hasInputRule()
     */
    @Override
    public boolean hasInputRule()
    {
        return (this.getInputMatchRegex() != null) && (this.getInputReplaceRegex() != null)
                && !this.getInputMatchRegex().trim().equals("");
    }
    
    // NOTE: it is quite okay to have an empty replace regex, but an empty match
    // is not considered useful here
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.RegexNormalisationRule#hasOutputRule()
     */
    @Override
    public boolean hasOutputRule()
    {
        return (this.getOutputMatchRegex() != null) && (this.getOutputReplaceRegex() != null)
                && !this.getOutputMatchRegex().trim().equals("");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.RegexNormalisationRule#setInputMatchRegex(java.lang.String)
     */
    @Override
    public void setInputMatchRegex(final String inputMatchRegex)
    {
        this.inputMatchRegex = inputMatchRegex;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.RegexNormalisationRule#setInputReplaceRegex(java.lang.String)
     */
    @Override
    public void setInputReplaceRegex(final String inputReplaceRegex)
    {
        this.inputReplaceRegex = inputReplaceRegex;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.RegexNormalisationRule#setOutputMatchRegex(java.lang.String)
     */
    @Override
    public void setOutputMatchRegex(final String outputMatchRegex)
    {
        this.outputMatchRegex = outputMatchRegex;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.RegexNormalisationRule#setOutputReplaceRegex(java.lang.String)
     */
    @Override
    public void setOutputReplaceRegex(final String outputReplaceRegex)
    {
        this.outputReplaceRegex = outputReplaceRegex;
    }
    
    @Override
    public Object stageAfterQueryCreation(final Object input)
    {
        return this.stages.contains(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation()) ? this
                .applyInputRuleToString((String)input) : input;
    }
    
    @Override
    public Object stageAfterQueryParsing(final Object input)
    {
        return input;
    }
    
    @Override
    public Object stageAfterResultsImport(final Object input)
    {
        return input;
    }
    
    @Override
    public Object stageAfterResultsToDocument(final Object input)
    {
        return this.stages.contains(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument()) ? this
                .applyOutputRuleToString((String)input) : input;
    }
    
    @Override
    public Object stageAfterResultsToPool(final Object input)
    {
        return input;
    }
    
    @Override
    public Object stageBeforeResultsImport(final Object input)
    {
        return this.stages.contains(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport()) ? this
                .applyOutputRuleToString((String)input) : input;
    }
    
    @Override
    public Object stageQueryVariables(final Object input)
    {
        return this.stages.contains(NormalisationRuleSchema.getRdfruleStageQueryVariables()) ? this
                .applyInputRuleToString((String)input) : input;
    }
    
    @Override
    public String toHtml()
    {
        String result = "";
        
        result +=
                "<div class=\"rulekey\">Rule Key: " + StringUtils.xmlEncodeString(this.getKey().stringValue())
                        + "</div>\n";
        result +=
                "<div class=\"description\">Description: " + StringUtils.xmlEncodeString(this.getDescription())
                        + "</div>\n";
        result += "<div class=\"order\">Order: " + StringUtils.xmlEncodeString(this.getOrder() + "") + "</div>\n";
        result +=
                "<div class=\"inputmatchregex\">Input Match Regex: "
                        + StringUtils.xmlEncodeString(this.getInputMatchRegex()) + "</div>\n";
        result +=
                "<div class=\"inputreplaceregex\">Input Replace Regex: "
                        + StringUtils.xmlEncodeString(this.getInputReplaceRegex()) + "</div>\n";
        result +=
                "<div class=\"outputmatchregex\">Output Match Regex: "
                        + StringUtils.xmlEncodeString(this.getOutputMatchRegex()) + "</div>\n";
        result +=
                "<div class=\"outputreplaceregex\">Output Replace Regex: "
                        + StringUtils.xmlEncodeString(this.getOutputReplaceRegex()) + "</div>\n";
        
        return result;
    }
    
    @Override
    public String toHtmlFormBody()
    {
        final StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        final String prefix = "rdfrule_";
        
        return sb.toString();
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super.toRdf(myRepository, keyToUse, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            if(RegexNormalisationRuleImpl._DEBUG)
            {
                RegexNormalisationRuleImpl.log.debug("RegexNormalisationRuleImpl.toRdf: keyToUse=" + keyToUse);
            }
            
            final URI keyUri = this.getKey();
            
            final Literal inputMatchRegexLiteral = f.createLiteral(this.getInputMatchRegex());
            final Literal inputReplaceRegexLiteral = f.createLiteral(this.getInputReplaceRegex());
            final Literal outputMatchRegexLiteral = f.createLiteral(this.getOutputMatchRegex());
            final Literal outputReplaceRegexLiteral = f.createLiteral(this.getOutputReplaceRegex());
            
            con.setAutoCommit(false);
            
            con.add(keyUri, RDF.TYPE, RegexNormalisationRuleSchema.getRegexRuleTypeUri(), keyToUse);
            con.add(keyUri, RegexNormalisationRuleSchema.getRegexRuleInputMatchRegex(), inputMatchRegexLiteral,
                    keyToUse);
            con.add(keyUri, RegexNormalisationRuleSchema.getRegexRuleInputReplaceRegex(), inputReplaceRegexLiteral,
                    keyToUse);
            con.add(keyUri, RegexNormalisationRuleSchema.getRegexRuleOutputMatchRegex(), outputMatchRegexLiteral,
                    keyToUse);
            con.add(keyUri, RegexNormalisationRuleSchema.getRegexRuleOutputReplaceRegex(), outputReplaceRegexLiteral,
                    keyToUse);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            RegexNormalisationRuleImpl.log.error("RepositoryException: " + re.getMessage());
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
        result += "order=" + this.getOrder() + "\n";
        result += "inputMatchRegex=" + this.getInputMatchRegex() + "\n";
        result += "inputReplaceRegex=" + this.getInputReplaceRegex() + "\n";
        result += "outputMatchRegex=" + this.getOutputMatchRegex() + "\n";
        result += "outputReplaceRegex=" + this.getOutputReplaceRegex() + "\n";
        result += "description=" + this.getDescription() + "\n";
        
        return result;
    }
}
