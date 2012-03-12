package org.queryall.impl.rdfrule;

import java.util.ArrayList;
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
    private static final boolean TRACE = PrefixMappingNormalisationRuleImpl.log.isTraceEnabled();
    private static final boolean DEBUG = PrefixMappingNormalisationRuleImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = PrefixMappingNormalisationRuleImpl.log.isInfoEnabled();
    
    private static final Set<URI> PREFIX_MAPPING_NORMALISATION_RULE_IMPL_TYPES = new HashSet<URI>(6);
    private static final Set<URI> PREFIX_MAPPING_NORMALISATION_RULE_IMPL_VALID_STAGES = new HashSet<URI>(10);
    
    static
    {
        PrefixMappingNormalisationRuleImpl.PREFIX_MAPPING_NORMALISATION_RULE_IMPL_TYPES.add(NormalisationRuleSchema
                .getNormalisationRuleTypeUri());
        PrefixMappingNormalisationRuleImpl.PREFIX_MAPPING_NORMALISATION_RULE_IMPL_TYPES.add(TransformingRuleSchema
                .getTransformingRuleTypeUri());
        PrefixMappingNormalisationRuleImpl.PREFIX_MAPPING_NORMALISATION_RULE_IMPL_TYPES
                .add(PrefixMappingNormalisationRuleSchema.getSimplePrefixMappingTypeUri());
        
        PrefixMappingNormalisationRuleImpl.PREFIX_MAPPING_NORMALISATION_RULE_IMPL_VALID_STAGES
                .add(NormalisationRuleSchema.getRdfruleStageQueryVariables());
        PrefixMappingNormalisationRuleImpl.PREFIX_MAPPING_NORMALISATION_RULE_IMPL_VALID_STAGES
                .add(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation());
        // Not sure how this would be implemented after query parsing, or why it would be
        // different to after query creation, so leave it off the list for now
        // PREFIX_MAPPING_NORMALISATION_RULE_IMPL_VALID_STAGES.add(NormalisationRuleSchema.getRdfruleStageAfterQueryParsing());
        PrefixMappingNormalisationRuleImpl.PREFIX_MAPPING_NORMALISATION_RULE_IMPL_VALID_STAGES
                .add(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport());
        PrefixMappingNormalisationRuleImpl.PREFIX_MAPPING_NORMALISATION_RULE_IMPL_VALID_STAGES
                .add(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
        PrefixMappingNormalisationRuleImpl.PREFIX_MAPPING_NORMALISATION_RULE_IMPL_VALID_STAGES
                .add(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
        PrefixMappingNormalisationRuleImpl.PREFIX_MAPPING_NORMALISATION_RULE_IMPL_VALID_STAGES
                .add(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument());
    }
    
    public static Set<URI> myTypes()
    {
        return Collections
                .unmodifiableSet(PrefixMappingNormalisationRuleImpl.PREFIX_MAPPING_NORMALISATION_RULE_IMPL_TYPES);
    }
    
    private String inputPrefix = "";
    
    private String outputPrefix = "";
    
    private Collection<URI> subjectMappingPredicates = new ArrayList<URI>();
    
    private Collection<URI> predicateMappingPredicates = new ArrayList<URI>();
    private Collection<URI> objectMappingPredicates = new ArrayList<URI>();
    
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
        
        final Collection<Statement> currentUnrecognisedStatements = this.resetUnrecognisedStatements();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(PrefixMappingNormalisationRuleImpl.TRACE)
            {
                PrefixMappingNormalisationRuleImpl.log.trace("PrefixMappingNormalisationRuleImpl: nextStatement: "
                        + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(
                            PrefixMappingNormalisationRuleSchema.getSimplePrefixMappingTypeUri()))
            {
                if(PrefixMappingNormalisationRuleImpl.TRACE)
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
                if(PrefixMappingNormalisationRuleImpl.TRACE)
                {
                    PrefixMappingNormalisationRuleImpl.log
                            .trace("PrefixMappingNormalisationRuleImpl: unrecognisedStatement nextStatement: "
                                    + nextStatement.toString());
                }
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(PrefixMappingNormalisationRuleImpl.TRACE)
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
    
    @Override
    public boolean resetObjectMappingPredicates()
    {
        try
        {
            this.objectMappingPredicates.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            PrefixMappingNormalisationRuleImpl.log.debug("Could not clear collection");
        }
        
        this.objectMappingPredicates = new ArrayList<URI>();
        
        return true;
    }
    
    @Override
    public boolean resetPredicateMappingPredicates()
    {
        try
        {
            this.predicateMappingPredicates.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            PrefixMappingNormalisationRuleImpl.log.debug("Could not clear collection");
        }
        
        this.predicateMappingPredicates = new ArrayList<URI>();
        
        return true;
    }
    
    @Override
    public boolean resetSubjectMappingPredicates()
    {
        try
        {
            this.subjectMappingPredicates.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            PrefixMappingNormalisationRuleImpl.log.debug("Could not clear collection");
        }
        
        this.subjectMappingPredicates = new ArrayList<URI>();
        
        return true;
    }
    
    @Override
    public void setInputUriPrefix(final String inputUriPrefix)
    {
        this.inputPrefix = inputUriPrefix;
    }
    
    @Override
    public void setOutputUriPrefix(final String outputUriPrefix)
    {
        this.outputPrefix = outputUriPrefix;
    }
    
    @Override
    protected Set<URI> setupValidStages()
    {
        return PrefixMappingNormalisationRuleImpl.PREFIX_MAPPING_NORMALISATION_RULE_IMPL_VALID_STAGES;
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
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            if(PrefixMappingNormalisationRuleImpl.TRACE)
            {
                PrefixMappingNormalisationRuleImpl.log.trace("PrefixMappingNormalisationRuleImpl.toRdf: keyToUse="
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
