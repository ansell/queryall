package org.queryall.impl.rdfrule;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
            // Not sure how this would be implemented after query parsing, or why it would be different to after query creation, so leave it off the list for now
            //this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterQueryParsing());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument());
        }
        
        return Collections.unmodifiableCollection(this.validStages);
    }
    
    
    private String inputPrefix = "";
    
    private String outputPrefix = "";
    private Collection<URI> subjectMappingPredicates = new HashSet<URI>();
    private Collection<URI> predicateMappingPredicates = new HashSet<URI>();
    private Collection<URI> objectMappingPredicates = new HashSet<URI>();
    
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
            else if(nextStatement.getPredicate().equals(SimplePrefixMappingNormalisationRuleSchema.getSubjectMappingPredicateUri()))
            {
                this.addSubjectMappingPredicate((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(SimplePrefixMappingNormalisationRuleSchema.getObjectMappingPredicateUri()))
            {
                this.addObjectMappingPredicate((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(SimplePrefixMappingNormalisationRuleSchema.getPredicateMappingPredicateUri()))
            {
                this.addPredicateMappingPredicate((URI)nextStatement.getObject());
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
        // denormalise the query variables
        // WARNING: This may destroy/alter mappings that were created in the query variables stage
        return ((String)input).replace(getInputUriPrefix(), getOutputUriPrefix());
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
        return RdfUtils.doMappingQueries((Repository)output, this.getInputUriPrefix(), this.getOutputUriPrefix(), getSubjectMappingPredicates(), getPredicateMappingPredicates(), getObjectMappingPredicates());
    }
    
    /**
     * 
     */
    @Override
    public Object stageAfterResultsToDocument(final Object output)
    {
        return ((String)output).replace(getInputUriPrefix(), getOutputUriPrefix());
    }
    
    @Override
    public Object stageAfterResultsToPool(final Object output)
    {
        return RdfUtils.doMappingQueries((Repository)output, this.getInputUriPrefix(), this.getOutputUriPrefix(), getSubjectMappingPredicates(), getPredicateMappingPredicates(), getObjectMappingPredicates());
    }
    
    @Override
    public Object stageBeforeResultsImport(final Object output)
    {
        return ((String)output).replace(getInputUriPrefix(), getOutputUriPrefix());
    }
    
    @Override
    public Object stageQueryVariables(final Object input)
    {
        // denormalise the query variables
        return ((String)input).replace(getOutputUriPrefix(), getInputUriPrefix());
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
            
            if(this.subjectMappingPredicates != null)
            {
                for(URI nextMappingPredicate : subjectMappingPredicates)
                {
                    con.add(keyUri, SimplePrefixMappingNormalisationRuleSchema.getSubjectMappingPredicateUri(), nextMappingPredicate, keyToUse);
                }
            }
            
            if(this.predicateMappingPredicates != null)
            {
                for(URI nextMappingPredicate : predicateMappingPredicates)
                {
                    con.add(keyUri, SimplePrefixMappingNormalisationRuleSchema.getPredicateMappingPredicateUri(), nextMappingPredicate, keyToUse);
                }
            }
            
            if(this.objectMappingPredicates != null)
            {
                for(URI nextMappingPredicate : objectMappingPredicates)
                {
                    con.add(keyUri, SimplePrefixMappingNormalisationRuleSchema.getObjectMappingPredicateUri(), nextMappingPredicate, keyToUse);
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
    public void addSubjectMappingPredicate(URI mappingPredicateUri)
    {
        this.subjectMappingPredicates.add(mappingPredicateUri);
    }

    @Override
    public Collection<URI> getSubjectMappingPredicates()
    {
        return Collections.unmodifiableCollection(this.subjectMappingPredicates);
    }

    @Override
    public void addPredicateMappingPredicate(URI equivalentproperty)
    {
        this.predicateMappingPredicates.add(equivalentproperty);
    }

    @Override
    public Collection<URI> getPredicateMappingPredicates()
    {
        return Collections.unmodifiableCollection(this.predicateMappingPredicates);
    }

    @Override
    public void addObjectMappingPredicate(URI sameas)
    {
        this.objectMappingPredicates.add(sameas);
    }

    @Override
    public Collection<URI> getObjectMappingPredicates()
    {
        return Collections.unmodifiableCollection(this.objectMappingPredicates);
    }
}
