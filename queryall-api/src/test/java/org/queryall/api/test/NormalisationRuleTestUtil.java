package org.queryall.api.test;

import java.util.Collection;

import org.junit.Assert;
import org.openrdf.model.URI;
import org.queryall.api.rdfrule.NormalisationRule;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class NormalisationRuleTestUtil
{
    protected final static void testIsUsedInStage(final NormalisationRule rule, final URI stage,
            final boolean expectedResult)
    {
        Assert.assertEquals("Failed testing stage="+stage, expectedResult, rule.usedInStage(stage));
    }
    
    protected final static void testIsUsedInStages(final NormalisationRule rule, final Collection<URI> stages,
            final boolean expectedResult)
    {
        for(final URI nextStage : stages)
        {
            NormalisationRuleTestUtil.testIsUsedInStage(rule, nextStage, expectedResult);
        }
    }
    
    protected final static void testIsValidInStage(final NormalisationRule rule, final URI stage,
            final boolean expectedResult)
    {
        Assert.assertEquals("Failed testing stage="+stage, expectedResult, rule.validInStage(stage));
    }
    
    protected final static void testIsValidInStages(final NormalisationRule rule, final Collection<URI> stages,
            final boolean expectedResult)
    {
        for(final URI nextStage : stages)
        {
            NormalisationRuleTestUtil.testIsValidInStage(rule, nextStage, expectedResult);
        }
    }
}
