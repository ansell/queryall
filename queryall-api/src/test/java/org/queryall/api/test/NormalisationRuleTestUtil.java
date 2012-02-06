package org.queryall.api.test;

import java.util.Collection;

import org.junit.Assert;
import org.openrdf.model.URI;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.exception.InvalidStageException;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class NormalisationRuleTestUtil
{
    private static final String FAILED_TESTING_STAGE = "Failed testing stage=";
    
    protected static void testIsUsedInStage(final NormalisationRule rule, final URI stage, final boolean expectedResult)
        throws InvalidStageException
    {
        Assert.assertEquals(NormalisationRuleTestUtil.FAILED_TESTING_STAGE + stage, expectedResult,
                rule.usedInStage(stage));
    }
    
    protected static void testIsUsedInStages(final NormalisationRule rule, final Collection<URI> stages,
            final boolean expectedResult) throws InvalidStageException
    {
        for(final URI nextStage : stages)
        {
            NormalisationRuleTestUtil.testIsUsedInStage(rule, nextStage, expectedResult);
        }
    }
    
    protected static void testIsValidInStage(final NormalisationRule rule, final URI stage, final boolean expectedResult)
        throws InvalidStageException
    {
        Assert.assertEquals(NormalisationRuleTestUtil.FAILED_TESTING_STAGE + stage, expectedResult,
                rule.validInStage(stage));
    }
    
    protected static void testIsValidInStages(final NormalisationRule rule, final Collection<URI> stages,
            final boolean expectedResult) throws InvalidStageException
    {
        for(final URI nextStage : stages)
        {
            NormalisationRuleTestUtil.testIsValidInStage(rule, nextStage, expectedResult);
        }
    }
}
