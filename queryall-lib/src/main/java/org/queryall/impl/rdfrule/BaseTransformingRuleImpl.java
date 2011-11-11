/**
 * 
 */
package org.queryall.impl.rdfrule;

import java.util.Collection;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.TransformingRule;
import org.queryall.exception.InvalidStageException;
import org.queryall.exception.QueryAllException;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public abstract class BaseTransformingRuleImpl extends BaseRuleImpl implements TransformingRule
{
    
    /**
     * 
     */
    public BaseTransformingRuleImpl()
    {
        super();
    }
    
    /**
     * @param inputStatements
     * @param keyToUse
     * @param modelVersion
     * @throws OpenRDFException
     */
    public BaseTransformingRuleImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
    }
    
    /**
     * This function delegates normalisation to the correct stage normalisation method based on the given URI.
     * 
     * You should not need to modify this method, as it provides generic stage validation that is necessary to ensure that the rule system is consistent.
     */
    @Override
    public final Object normaliseByStage(final URI stage, final Object input) throws QueryAllException
    {
        if(!this.validInStage(stage))
        {
            if(BaseRuleImpl._TRACE)
            {
                BaseRuleImpl.log
                        .trace("NormalisationRuleImpl.normaliseByStage : found an invalid stage for this type of rule (this may not be an error) stage="
                                + stage);
            }
            
            return input;
        }
        
        if(!this.usedInStage(stage))
        {
            if(BaseRuleImpl._DEBUG)
            {
                BaseRuleImpl.log
                        .debug("NormalisationRuleImpl.normaliseByStage : found an inapplicable stage for this type of rule key="
                                + this.getKey().stringValue() + " stage=" + stage);
            }
            
            return input;
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
        
        throw new InvalidStageException("Normalisation rule stage unknown : stage=" + stage);
    }
}
