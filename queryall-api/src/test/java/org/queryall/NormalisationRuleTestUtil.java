package org.queryall;

import java.util.Collection;

import org.openrdf.model.URI;
import org.queryall.api.NormalisationRule;

import static org.junit.Assert.*;

public final class NormalisationRuleTestUtil
{
    public final static void testIsUsedInStage(NormalisationRule rule, URI stage, boolean expectedResult)
    {
    	assertEquals(expectedResult, rule.usedInStage(stage));
    }

    public final static void testIsUsedInStages(NormalisationRule rule, Collection<URI> stages, boolean expectedResult)
    {
    	for(URI nextStage : stages)
    	{
    		testIsUsedInStage(rule, nextStage, expectedResult);
    	}
    }

    public final static void testIsValidInStage(NormalisationRule rule, URI stage, boolean expectedResult)
    {
    	assertEquals(expectedResult, rule.validInStage(stage));
    }

    public final static void testIsValidInStages(NormalisationRule rule, Collection<URI> stages, boolean expectedResult)
    {
    	for(URI nextStage : stages)
    	{
    		testIsValidInStage(rule, nextStage, expectedResult);
    	}
    }
}
