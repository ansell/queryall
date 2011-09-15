package org.queryall.impl.rdfrule;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
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
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.SimplePrefixMappingNormalisationRuleSchema;
import org.queryall.api.rdfrule.SimplePrefixMappingNormalisationRule;
import org.queryall.api.utils.Constants;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SimplePrefixMappingNormalisationRuleImpl extends NormalisationRuleImpl implements SimplePrefixMappingNormalisationRule
{
    private static final Logger log = LoggerFactory.getLogger(SimplePrefixMappingNormalisationRuleImpl.class);
    private static final boolean _TRACE = SimplePrefixMappingNormalisationRuleImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = SimplePrefixMappingNormalisationRuleImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SimplePrefixMappingNormalisationRuleImpl.log.isInfoEnabled();
    
    private static final Set<URI> SIMPLE_PREFIX_MAPPING_NORMALISATION_RULE_IMPL_TYPES = new HashSet<URI>();
    
    static
    {
        SIMPLE_PREFIX_MAPPING_NORMALISATION_RULE_IMPL_TYPES.add(NormalisationRuleSchema.getNormalisationRuleTypeUri());
        SIMPLE_PREFIX_MAPPING_NORMALISATION_RULE_IMPL_TYPES.add(SimplePrefixMappingNormalisationRuleSchema.getSimplePrefixMappingTypeUri());
    }
    
    public static Set<URI> myTypes()
    {
        return SIMPLE_PREFIX_MAPPING_NORMALISATION_RULE_IMPL_TYPES;
    }
    
    /**
     * @return the validStages
     */
    @Override
    public Collection<URI> getValidStages()
    {
        if(this.validStages.size() == 0)
        {
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageQueryVariables());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterQueryParsing());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument());
        }
        
        return Collections.unmodifiableCollection(this.validStages);
    }
    
    
    private String inputPrefix = "";
    
    private String outputPrefix = "";
    private Collection<URI> mappingPredicates = new HashSet<URI>();
    
    public SimplePrefixMappingNormalisationRuleImpl()
    {
        super();
    }
    
    // keyToUse is the URI of the next instance that can be found in
    // myRepository
    public SimplePrefixMappingNormalisationRuleImpl(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = new HashSet<Statement>();
        
        currentUnrecognisedStatements.addAll(this.getUnrecognisedStatements());
        
        this.unrecognisedStatements = new HashSet<Statement>();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(SimplePrefixMappingNormalisationRuleImpl._TRACE)
            {
                SimplePrefixMappingNormalisationRuleImpl.log.trace("SimplePrefixMappingNormalisationRuleImpl: nextStatement: "
                        + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(SimplePrefixMappingNormalisationRuleSchema.getSimplePrefixMappingTypeUri()))
            {
                if(SimplePrefixMappingNormalisationRuleImpl._TRACE)
                {
                    SimplePrefixMappingNormalisationRuleImpl.log
                            .trace("SimplePrefixMappingNormalisationRuleImpl: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(SimplePrefixMappingNormalisationRuleSchema.getInputPrefixUri()))
            {
                this.setInputUriPrefix(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(SimplePrefixMappingNormalisationRuleSchema.getOutputPrefixUri()))
            {
                this.setOutputUriPrefix(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(SimplePrefixMappingNormalisationRuleSchema.getMappingPredicateUri()))
            {
                this.addMappingPredicate((URI)nextStatement.getObject());
            }
            else
            {
                if(SimplePrefixMappingNormalisationRuleImpl._TRACE)
                {
                    SimplePrefixMappingNormalisationRuleImpl.log
                            .trace("SimplePrefixMappingNormalisationRuleImpl: unrecognisedStatement nextStatement: "
                                    + nextStatement.toString());
                }
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(SimplePrefixMappingNormalisationRuleImpl._TRACE)
        {
            SimplePrefixMappingNormalisationRuleImpl.log.trace("SimplePrefixMappingNormalisationRuleImpl.fromRdf: would have returned... result="
                    + this.toString());
        }
    }
    
    private String applyInputRegexToString(final String inputText)
    {
        return this.applyRegex(inputText, Pattern.quote(this.getOutputUriPrefix()), Pattern.quote(this.getInputUriPrefix()));
    }
    
    private String applyOutputRegexToString(final String inputText)
    {
        return this.applyRegex(inputText, Pattern.quote(this.getInputUriPrefix()), Pattern.quote(this.getOutputUriPrefix()));
    }
    
    private String applyRegex(String inputText, final String matchRegex, final String replaceRegex)
    {
        try
        {
            if((matchRegex == null) || (replaceRegex == null))
            {
                if(SimplePrefixMappingNormalisationRuleImpl._TRACE)
                {
                    SimplePrefixMappingNormalisationRuleImpl.log
                            .trace("SimplePrefixMappingNormalisationRuleImpl.applyRegex: something was null matchRegex=" + matchRegex
                                    + ", replaceRegex=" + replaceRegex);
                }
                
                return inputText;
            }
            
            if(SimplePrefixMappingNormalisationRuleImpl._DEBUG)
            {
                SimplePrefixMappingNormalisationRuleImpl.log.debug("SimplePrefixMappingNormalisationRuleImpl.applyRegex: matchRegex=" + matchRegex
                        + ", replaceRegex=" + replaceRegex);
            }
            
            if(matchRegex.trim().equals(""))
            {
                if(SimplePrefixMappingNormalisationRuleImpl._DEBUG)
                {
                    SimplePrefixMappingNormalisationRuleImpl.log
                            .debug("SimplePrefixMappingNormalisationRuleImpl.applyRegex: matchRegex was empty, returning inputText");
                }
                
                return inputText;
            }
            
            String debugInputText = "";
            
            // only take a copy of the string if we need it for debugging
            if(SimplePrefixMappingNormalisationRuleImpl._DEBUG)
            {
                debugInputText = inputText;
            }
            
            inputText = inputText.replaceAll(matchRegex, replaceRegex);
            
            if(SimplePrefixMappingNormalisationRuleImpl._DEBUG)
            {
                SimplePrefixMappingNormalisationRuleImpl.log.debug("SimplePrefixMappingNormalisationRuleImpl.applyRegex: regex start input="
                        + debugInputText);
                SimplePrefixMappingNormalisationRuleImpl.log.debug("SimplePrefixMappingNormalisationRuleImpl.applyRegex: regex complete result="
                        + inputText);
            }
        }
        catch(final PatternSyntaxException pse)
        {
            SimplePrefixMappingNormalisationRuleImpl.log.error("SimplePrefixMappingNormalisationRuleImpl.applyRegex: PatternSyntaxException="
                    + pse.getMessage());
        }
        catch(final IllegalArgumentException iae)
        {
            SimplePrefixMappingNormalisationRuleImpl.log.error("SimplePrefixMappingNormalisationRuleImpl.applyRegex: IllegalArgumentException="
                    + iae.getMessage());
        }
        catch(final IndexOutOfBoundsException ioobe)
        {
            SimplePrefixMappingNormalisationRuleImpl.log.error("SimplePrefixMappingNormalisationRuleImpl.applyRegex: IndexOutOfBoundsException="
                    + ioobe.getMessage());
        }
        
        return inputText;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Set<URI> getElementTypes()
    {
        return SimplePrefixMappingNormalisationRuleImpl.myTypes();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.RegexNormalisationRule#getInputMatchRegex()
     */
    @Override
    public String getInputUriPrefix()
    {
        return this.inputPrefix;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.RegexNormalisationRule#getOutputMatchRegex()
     */
    @Override
    public String getOutputUriPrefix()
    {
        return this.outputPrefix;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.RegexNormalisationRule#setInputMatchRegex(java.lang.String)
     */
    @Override
    public void setInputUriPrefix(final String inputUriPrefix)
    {
        this.inputPrefix = inputUriPrefix;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.impl.RegexNormalisationRule#setOutputMatchRegex(java.lang.String)
     */
    @Override
    public void setOutputUriPrefix(final String outputUriPrefix)
    {
        this.outputPrefix = outputUriPrefix;
    }
    
    /**
     * The use of this method may modify any endpointSpecific URIs given in the templateVariables section
     */
    @Override
    public Object stageAfterQueryCreation(final Object input)
    {
        return applyInputRegexToString((String)input);
    }
    
    /**
     * Not implemented.
     */
    @Override
    public Object stageAfterQueryParsing(final Object input)
    {
        return input;
    }
    
    /**
     * 
     */
    @Override
    public Object stageAfterResultsImport(final Object output)
    {
        return RdfUtils.doMappingQueries((Repository)output, this.getInputUriPrefix(), mappingPredicates, this.getOutputUriPrefix());
    }
    
    /**
     * 
     */
    @Override
    public Object stageAfterResultsToDocument(final Object output)
    {
        return applyOutputRegexToString((String)output);
    }
    
    @Override
    public Object stageAfterResultsToPool(final Object output)
    {
        return RdfUtils.doMappingQueries((Repository)output, this.getInputUriPrefix(), mappingPredicates, this.getOutputUriPrefix());
    }
    
    @Override
    public Object stageBeforeResultsImport(final Object output)
    {
        return applyOutputRegexToString((String)output);
    }
    
    @Override
    public Object stageQueryVariables(final Object input)
    {
        return applyInputRegexToString((String)input);
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
            if(SimplePrefixMappingNormalisationRuleImpl._DEBUG)
            {
                SimplePrefixMappingNormalisationRuleImpl.log.debug("SimplePrefixMappingNormalisationRuleImpl.toRdf: keyToUse=" + keyToUse);
            }
            
            final URI keyUri = this.getKey();
            
            final Literal inputUriPrefixLiteral = f.createLiteral(this.getInputUriPrefix());
            final Literal outputUriPrefixLiteral = f.createLiteral(this.getOutputUriPrefix());
            
            con.setAutoCommit(false);
            
            con.add(keyUri, RDF.TYPE, SimplePrefixMappingNormalisationRuleSchema.getSimplePrefixMappingTypeUri(), keyToUse);
            con.add(keyUri, SimplePrefixMappingNormalisationRuleSchema.getInputPrefixUri(), inputUriPrefixLiteral,
                    keyToUse);
            con.add(keyUri, SimplePrefixMappingNormalisationRuleSchema.getOutputPrefixUri(), outputUriPrefixLiteral,
                    keyToUse);
            
            if(this.mappingPredicates != null)
            {
                for(URI nextMappingPredicate : mappingPredicates)
                {
                    con.add(keyUri, SimplePrefixMappingNormalisationRuleSchema.getMappingPredicateUri(), nextMappingPredicate, keyToUse);
                }
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            SimplePrefixMappingNormalisationRuleImpl.log.error("RepositoryException: " + re.getMessage());
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
        result += "description=" + this.getDescription() + "\n";
        
        return result;
    }

    @Override
    public void addMappingPredicate(String mappingPredicateString)
    {
        this.addMappingPredicate(StringUtils.createURI(mappingPredicateString));
    }

    @Override
    public void addMappingPredicate(URI mappingPredicateUri)
    {
        this.mappingPredicates.add(mappingPredicateUri);
    }

    @Override
    public Collection<URI> getMappingPredicates()
    {
        return this.mappingPredicates;
    }
}
