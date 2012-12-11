/**
 * 
 */
package org.queryall.impl.rdfrule;

import java.util.Collection;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.ValidatingRule;
import org.queryall.api.rdfrule.ValidatingRuleSchema;
import org.queryall.exception.InvalidStageException;
import org.queryall.exception.ValidationFailedException;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public abstract class BaseValidatingRuleImpl extends BaseRuleImpl implements ValidatingRule
{
    /**
     * 
     */
    public BaseValidatingRuleImpl()
    {
        super();
    }
    
    /**
     * @param inputStatements
     * @param keyToUse
     * @param modelVersion
     * @throws OpenRDFException
     */
    public BaseValidatingRuleImpl(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = this.resetUnrecognisedStatements();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(ValidatingRuleSchema.getValidatingRuleTypeUri()))
            {
                if(this.log.isDebugEnabled())
                {
                    this.log.debug("Found validating rule schema type URI");
                }
                
                this.setKey(keyToUse);
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
    }
    
    /**
     * This function delegates normalisation to the correct stage normalisation method based on the
     * given URI.
     * 
     * You should not need to modify this method, as it provides generic stage validation that is
     * necessary to ensure that the rule system is consistent.
     */
    @Override
    public final boolean normaliseByStage(final URI stage, final Object input) throws InvalidStageException,
        ValidationFailedException
    {
        if(!this.validInStage(stage))
        {
            if(this.log.isTraceEnabled())
            {
                this.log.trace("NormalisationRuleImpl.normaliseByStage : found an invalid stage for this type of rule (this may not be an error) stage="
                        + stage);
            }
            
            throw new InvalidStageException("Attempted to use this rule on an invalid stage", this, stage);
        }
        
        if(!this.usedInStage(stage))
        {
            if(this.log.isDebugEnabled())
            {
                this.log.debug("NormalisationRuleImpl.normaliseByStage : found an inapplicable stage for this type of rule key="
                        + this.getKey().stringValue() + " stage=" + stage);
            }
            
            // Don't failover just because they attempted to normalise this rule when it was a valid
            // stage, but not used by this rule
            return true;
        }
        
        if(stage.equals(NormalisationRuleSchema.getRdfruleStageQueryVariables()))
        {
            return this.stageQueryVariables(input);
        }
        else if(stage.equals(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation()))
        {
            return this.stageAfterQueryCreation(input);
        }
        else if(stage.equals(NormalisationRuleSchema.getRdfruleStageAfterQueryParsing()))
        {
            return this.stageAfterQueryParsing(input);
        }
        else if(stage.equals(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport()))
        {
            return this.stageBeforeResultsImport(input);
        }
        else if(stage.equals(NormalisationRuleSchema.getRdfruleStageAfterResultsImport()))
        {
            return this.stageAfterResultsImport(input);
        }
        else if(stage.equals(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool()))
        {
            return this.stageAfterResultsToPool(input);
        }
        else if(stage.equals(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument()))
        {
            return this.stageAfterResultsToDocument(input);
        }
        
        throw new InvalidStageException("Normalisation rule stage unknown", this, stage);
    }
    
}
