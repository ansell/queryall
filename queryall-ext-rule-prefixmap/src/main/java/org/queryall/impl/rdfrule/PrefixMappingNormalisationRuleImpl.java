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
import org.queryall.api.rdfrule.PrefixMappingNormalisationRule;
import org.queryall.api.rdfrule.PrefixMappingNormalisationRuleSchema;
import org.queryall.api.rdfrule.TransformingRuleSchema;
import org.queryall.api.utils.Constants;
import org.queryall.utils.RdfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class PrefixMappingNormalisationRuleImpl extends BaseTransformingRuleImpl implements
        PrefixMappingNormalisationRule
{
    private static final Logger log = LoggerFactory.getLogger(PrefixMappingNormalisationRuleImpl.class);
    private static final boolean _TRACE = PrefixMappingNormalisationRuleImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = PrefixMappingNormalisationRuleImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = PrefixMappingNormalisationRuleImpl.log.isInfoEnabled();
    
    private static final Set<URI> SIMPLE_PREFIX_MAPPING_NORMALISATION_RULE_IMPL_TYPES = new HashSet<URI>();
    
    static
    {
        PrefixMappingNormalisationRuleImpl.SIMPLE_PREFIX_MAPPING_NORMALISATION_RULE_IMPL_TYPES
                .add(NormalisationRuleSchema.getNormalisationRuleTypeUri());
        PrefixMappingNormalisationRuleImpl.SIMPLE_PREFIX_MAPPING_NORMALISATION_RULE_IMPL_TYPES
                .add(TransformingRuleSchema.getTransformingRuleTypeUri());
        PrefixMappingNormalisationRuleImpl.SIMPLE_PREFIX_MAPPING_NORMALISATION_RULE_IMPL_TYPES
                .add(PrefixMappingNormalisationRuleSchema.getSimplePrefixMappingTypeUri());
    }
    
    public static Set<URI> myTypes()
    {
        return PrefixMappingNormalisationRuleImpl.SIMPLE_PREFIX_MAPPING_NORMALISATION_RULE_IMPL_TYPES;
    }
    
    private String inputPrefix = "";
    
    private String outputPrefix = "";
    
    private Collection<URI> subjectMappingPredicates = new HashSet<URI>();
    private Collection<URI> predicateMappingPredicates = new HashSet<URI>();
    private Collection<URI> objectMappingPredicates = new HashSet<URI>();
    
    public PrefixMappingNormalisationRuleImpl()
    {
        super();
    }
    
    // keyToUse is the URI of the next instance that can be found in
    // myRepository
    public PrefixMappingNormalisationRuleImpl(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = new HashSet<Statement>();
        
        currentUnrecognisedStatements.addAll(this.getUnrecognisedStatements());
        
        this.unrecognisedStatements = new HashSet<Statement>();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(PrefixMappingNormalisationRuleImpl._TRACE)
            {
                PrefixMappingNormalisationRuleImpl.log.trace("PrefixMappingNormalisationRuleImpl: nextStatement: "
                        + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(
                            PrefixMappingNormalisationRuleSchema.getSimplePrefixMappingTypeUri()))
            {
                if(PrefixMappingNormalisationRuleImpl._TRACE)
                {
                    PrefixMappingNormalisationRuleImpl.log
                            .trace("PrefixMappingNormalisationRuleImpl: found valid type predicate for URI: "
                                    + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(PrefixMappingNormalisationRuleSchema.getInputPrefixUri()))
            {
                this.setInputUriPrefix(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(PrefixMappingNormalisationRuleSchema.getOutputPrefixUri()))
            {
                this.setOutputUriPrefix(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(
                    PrefixMappingNormalisationRuleSchema.getSubjectMappingPredicateUri()))
            {
                this.addSubjectMappingPredicate((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(
                    PrefixMappingNormalisationRuleSchema.getObjectMappingPredicateUri()))
            {
                this.addObjectMappingPredicate((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(
                    PrefixMappingNormalisationRuleSchema.getPredicateMappingPredicateUri()))
            {
                this.addPredicateMappingPredicate((URI)nextStatement.getObject());
            }
            else
            {
                if(PrefixMappingNormalisationRuleImpl._TRACE)
                {
                    PrefixMappingNormalisationRuleImpl.log
                            .trace("PrefixMappingNormalisationRuleImpl: unrecognisedStatement nextStatement: "
                                    + nextStatement.toString());
                }
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(PrefixMappingNormalisationRuleImpl._TRACE)
        {
            PrefixMappingNormalisationRuleImpl.log
                    .trace("PrefixMappingNormalisationRuleImpl.fromRdf: would have returned... result="
                            + this.toString());
        }
    }
    
    @Override
    public void addObjectMappingPredicate(final URI sameas)
    {
        this.objectMappingPredicates.add(sameas);
    }
    
    @Override
    public void addPredicateMappingPredicate(final URI equivalentproperty)
    {
        this.predicateMappingPredicates.add(equivalentproperty);
    }
    
    @Override
    public void addSubjectMappingPredicate(final URI mappingPredicateUri)
    {
        this.subjectMappingPredicates.add(mappingPredicateUri);
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Set<URI> getElementTypes()
    {
        return PrefixMappingNormalisationRuleImpl.myTypes();
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
    
    @Override
    public Collection<URI> getObjectMappingPredicates()
    {
        return Collections.unmodifiableCollection(this.objectMappingPredicates);
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
    
    @Override
    public Collection<URI> getPredicateMappingPredicates()
    {
        return Collections.unmodifiableCollection(this.predicateMappingPredicates);
    }
    
    @Override
    public Collection<URI> getSubjectMappingPredicates()
    {
        return Collections.unmodifiableCollection(this.subjectMappingPredicates);
    }
    
    /**
     * @return the validStages
     */
    @Override
    public Set<URI> getValidStages()
    {
        if(this.validStages.size() == 0)
        {
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageQueryVariables());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation());
            // Not sure how this would be implemented after query parsing, or why it would be
            // different to after query creation, so leave it off the list for now
            // this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterQueryParsing());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument());
        }
        
        return Collections.unmodifiableSet(this.validStages);
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
     * The use of this method may modify any endpointSpecific URIs given in the templateVariables
     * section
     */
    @Override
    public Object stageAfterQueryCreation(final Object input)
    {
        // denormalise the query variables
        // WARNING: This may destroy/alter mappings that were created in the query variables stage
        return ((String)input).replace(this.getInputUriPrefix(), this.getOutputUriPrefix());
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
        return RdfUtils.doMappingQueries((Repository)output, this.getInputUriPrefix(), this.getOutputUriPrefix(),
                this.getSubjectMappingPredicates(), this.getPredicateMappingPredicates(),
                this.getObjectMappingPredicates());
    }
    
    /**
     * 
     */
    @Override
    public Object stageAfterResultsToDocument(final Object output)
    {
        return ((String)output).replace(this.getInputUriPrefix(), this.getOutputUriPrefix());
    }
    
    @Override
    public Object stageAfterResultsToPool(final Object output)
    {
        return RdfUtils.doMappingQueries((Repository)output, this.getInputUriPrefix(), this.getOutputUriPrefix(),
                this.getSubjectMappingPredicates(), this.getPredicateMappingPredicates(),
                this.getObjectMappingPredicates());
    }
    
    @Override
    public Object stageBeforeResultsImport(final Object output)
    {
        return ((String)output).replace(this.getInputUriPrefix(), this.getOutputUriPrefix());
    }
    
    @Override
    public Object stageQueryVariables(final Object input)
    {
        // denormalise the query variables
        return ((String)input).replace(this.getOutputUriPrefix(), this.getInputUriPrefix());
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... keyToUse)
        throws OpenRDFException
    {
        super.toRdf(myRepository, modelVersion, keyToUse);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            if(PrefixMappingNormalisationRuleImpl._DEBUG)
            {
                PrefixMappingNormalisationRuleImpl.log.debug("PrefixMappingNormalisationRuleImpl.toRdf: keyToUse="
                        + keyToUse);
            }
            
            final URI keyUri = this.getKey();
            
            final Literal inputUriPrefixLiteral = f.createLiteral(this.getInputUriPrefix());
            final Literal outputUriPrefixLiteral = f.createLiteral(this.getOutputUriPrefix());
            
            con.setAutoCommit(false);
            
            con.add(keyUri, RDF.TYPE, PrefixMappingNormalisationRuleSchema.getSimplePrefixMappingTypeUri(), keyToUse);
            con.add(keyUri, PrefixMappingNormalisationRuleSchema.getInputPrefixUri(), inputUriPrefixLiteral, keyToUse);
            con.add(keyUri, PrefixMappingNormalisationRuleSchema.getOutputPrefixUri(), outputUriPrefixLiteral, keyToUse);
            
            if(this.subjectMappingPredicates != null)
            {
                for(final URI nextMappingPredicate : this.subjectMappingPredicates)
                {
                    con.add(keyUri, PrefixMappingNormalisationRuleSchema.getSubjectMappingPredicateUri(),
                            nextMappingPredicate, keyToUse);
                }
            }
            
            if(this.predicateMappingPredicates != null)
            {
                for(final URI nextMappingPredicate : this.predicateMappingPredicates)
                {
                    con.add(keyUri, PrefixMappingNormalisationRuleSchema.getPredicateMappingPredicateUri(),
                            nextMappingPredicate, keyToUse);
                }
            }
            
            if(this.objectMappingPredicates != null)
            {
                for(final URI nextMappingPredicate : this.objectMappingPredicates)
                {
                    con.add(keyUri, PrefixMappingNormalisationRuleSchema.getObjectMappingPredicateUri(),
                            nextMappingPredicate, keyToUse);
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
            
            PrefixMappingNormalisationRuleImpl.log.error("RepositoryException: " + re.getMessage());
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
}
