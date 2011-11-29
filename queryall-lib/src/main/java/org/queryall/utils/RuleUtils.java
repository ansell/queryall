/**
 * 
 */
package org.queryall.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.TransformingRule;
import org.queryall.api.rdfrule.ValidatingRule;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.ruletest.StringRuleTest;
import org.queryall.api.utils.SortOrder;
import org.queryall.exception.InvalidStageException;
import org.queryall.exception.QueryAllException;
import org.queryall.exception.UnnormalisableRuleException;
import org.queryall.exception.UntestableRuleException;
import org.queryall.exception.ValidationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class RuleUtils
{
    private static final Logger log = LoggerFactory.getLogger(RuleUtils.class);
    private static final boolean _TRACE = RuleUtils.log.isTraceEnabled();
    private static final boolean _DEBUG = RuleUtils.log.isDebugEnabled();
    private static final boolean _INFO = RuleUtils.log.isInfoEnabled();
    
    /**
     * 
     * @param allNormalisationRules
     * @param neededRules
     * @param sortOrder
     * @return A sorted list of rules from allNormalisationRules using the URIs from neededRules and
     *         the SortOrder given by sortOrder
     */
    public static List<NormalisationRule> getSortedRulesByUris(final Map<URI, NormalisationRule> allNormalisationRules,
            final Collection<URI> neededRules, final SortOrder sortOrder)
    {
        final List<NormalisationRule> results = new ArrayList<NormalisationRule>();
        // final List<NormalisationRule> intermediateResults = new ArrayList<NormalisationRule>();
        // final Map<URI, NormalisationRule> allNormalisationRules =
        // this.getAllNormalisationRules();
        
        for(final URI nextProviderNormalisationRule : neededRules)
        {
            if(allNormalisationRules.containsKey(nextProviderNormalisationRule))
            {
                results.add(allNormalisationRules.get(nextProviderNormalisationRule));
            }
            else
            {
                if(RuleUtils._DEBUG)
                {
                    RuleUtils.log.debug("Could not find requested Normalisation Rule nextProviderNormalisationRule="
                            + nextProviderNormalisationRule.stringValue());
                }
            }
        }
        
        if(RuleUtils._TRACE)
        {
            RuleUtils.log.trace("Settings: rule sorting started");
        }
        if(sortOrder == SortOrder.LOWEST_ORDER_FIRST)
        {
            Collections.sort(results);
            
            if(RuleUtils._DEBUG)
            {
                int testOrder = -1;
                for(final NormalisationRule nextRule : results)
                {
                    if(testOrder == -1)
                    {
                        if(RuleUtils._TRACE)
                        {
                            RuleUtils.log.trace("Settings: rule sorting verification starting at nextRule.getOrder()="
                                    + nextRule.getOrder());
                        }
                        testOrder = nextRule.getOrder();
                    }
                    else if(testOrder > nextRule.getOrder())
                    {
                        RuleUtils.log.error("Settings: rules were not sorted properly testOrder=" + testOrder
                                + " nextRule.getOrder()=" + nextRule.getOrder());
                    }
                    else if(testOrder < nextRule.getOrder())
                    {
                        if(RuleUtils._TRACE)
                        {
                            RuleUtils.log.trace("Settings: rule verification stepping from testOrder=" + testOrder
                                    + " to nextRule.getOrder()=" + nextRule.getOrder());
                        }
                        testOrder = nextRule.getOrder();
                    }
                }
            } // end if(_TRACE)
        }
        else if(sortOrder == SortOrder.HIGHEST_ORDER_FIRST)
        {
            Collections.sort(results, Collections.reverseOrder());
            
            if(RuleUtils._DEBUG)
            {
                int testOrder = -1;
                
                for(final NormalisationRule nextRule : results)
                {
                    if(testOrder == -1)
                    {
                        if(RuleUtils._TRACE)
                        {
                            RuleUtils.log.trace("Settings: rule sorting verification starting at nextRule.getOrder()="
                                    + nextRule.getOrder());
                        }
                        testOrder = nextRule.getOrder();
                    }
                    else if(testOrder < nextRule.getOrder())
                    {
                        RuleUtils.log.error("Settings: rules were not sorted properly testOrder=" + testOrder
                                + " nextRule.getOrder()=" + nextRule.getOrder());
                    }
                    else if(testOrder > nextRule.getOrder())
                    {
                        if(RuleUtils._TRACE)
                        {
                            RuleUtils.log.trace("Settings: rule verification stepping from testOrder=" + testOrder
                                    + " to nextRule.getOrder()=" + nextRule.getOrder());
                        }
                        testOrder = nextRule.getOrder();
                    }
                }
            } // end if(_TRACE)
        }
        else
        {
            RuleUtils.log.error("Settings: sortOrder was not recognised sortOrder=" + sortOrder);
        }
        
        if(RuleUtils._TRACE)
        {
            RuleUtils.log.trace("Settings: rule sorting finished");
        }
        
        return results;
    }
    
    /**
     * 
     * @param providers
     * @param allNormalisationRules
     * @param sortOrder
     * @return An ordered list of rules based on all of the rules that are defined for the given
     *         providers and the SortOrder given by sortOrder
     */
    public static List<NormalisationRule> getSortedRulesForProviders(final Collection<Provider> providers,
            final Map<URI, NormalisationRule> allNormalisationRules, final SortOrder sortOrder)
    {
        final List<NormalisationRule> results = new LinkedList<NormalisationRule>();
        
        for(final Provider nextProvider : providers)
        {
            results.addAll(RuleUtils.getSortedRulesByUris(allNormalisationRules, nextProvider.getNormalisationUris(),
                    sortOrder));
        }
        
        if(sortOrder == SortOrder.HIGHEST_ORDER_FIRST)
        {
            Collections.sort(results, Collections.reverseOrder());
        }
        else if(sortOrder == SortOrder.LOWEST_ORDER_FIRST)
        {
            Collections.sort(results);
        }
        else
        {
            RuleUtils.log.error("sortOrder unrecognised sortOrder=" + sortOrder);
        }
        
        return results;
    }
    
    /**
     * @param normalisationRules
     *            An ordered list of normalisation rules that need to be applied to the input
     *            document
     * @param includedProfiles
     * @param recogniseImplicitRdfRuleInclusions
     * @param includeNonProfileMatchedRdfRules
     * @param basicRdfXml
     * @return
     * @throws QueryAllException
     *             , UnnormalisableRuleException
     */
    public static Object normaliseByStage(final URI stage, Object input,
            final List<NormalisationRule> normalisationRules, final List<Profile> includedProfiles,
            final boolean recogniseImplicitRdfRuleInclusions, final boolean includeNonProfileMatchedRdfRules)
        throws QueryAllException, UnnormalisableRuleException
    {
        if(RuleUtils._TRACE)
        {
            RuleUtils.log.trace("normaliseByStage: before applying normalisation rules");
        }
        
        final long start = System.currentTimeMillis();
        
        // go through the rules
        for(final NormalisationRule nextRule : normalisationRules)
        {
            if(nextRule.isUsedWithProfileList(includedProfiles, recogniseImplicitRdfRuleInclusions,
                    includeNonProfileMatchedRdfRules) && nextRule.usedInStage(stage))
            {
                if(RuleUtils._TRACE)
                {
                    RuleUtils.log.trace("normaliseByStage: nextRule.order=" + nextRule.getOrder());
                }
                try
                {
                    if(nextRule instanceof TransformingRule)
                    {
                        input = ((TransformingRule)nextRule).normaliseByStage(stage, input);
                    }
                    else if(nextRule instanceof ValidatingRule)
                    {
                        final boolean result = ((ValidatingRule)nextRule).normaliseByStage(stage, input);
                        
                        if(!result)
                        {
                            throw new ValidationFailedException("Validation failed", (ValidatingRule)nextRule);
                        }
                        
                        // if the validation did not fail, we return the input object unchanged.
                    }
                    else
                    {
                        throw new UnnormalisableRuleException("NormalisationRule type not supported", nextRule);
                    }
                }
                catch(final InvalidStageException ise)
                {
                    RuleUtils.log
                            .error("Found invalid stage exception after we checked whether the rule was used in this stage. This should not happen.",
                                    ise);
                }
            }
        }
        
        if(RuleUtils._DEBUG)
        {
            final long end = System.currentTimeMillis();
            
            RuleUtils.log.debug(String.format("%s: timing=%10d", "normaliseByStage", (end - start)));
            
            if(RuleUtils._TRACE)
            {
                RuleUtils.log.trace("normaliseByStage: after applying normalisation rules");
            }
        }
        
        return input;
    }
    
    /**
     * Runs rule tests over the stages "QueryVariables" and "BeforeResultsImport"
     * 
     * @param myRuleTests
     * @return true if all of the tests passed, otherwise it returns false
     * @throws QueryAllException
     */
    public static boolean runRuleTests(final Collection<RuleTest> myRuleTests,
            final Map<URI, NormalisationRule> allNormalisationRules) throws QueryAllException
    {
        boolean allPassed = true;
        
        for(final RuleTest nextRuleTest : myRuleTests)
        {
            if(nextRuleTest instanceof StringRuleTest)
            {
                final StringRuleTest nextStringRuleTest = (StringRuleTest)nextRuleTest;
                
                if(!RuleUtils.runStringRuleTest(allNormalisationRules, nextStringRuleTest))
                {
                    allPassed = false;
                }
            } // end if(nextRuleTest instanceof StringRuleTest
            else
            {
                RuleUtils.log.error("Could not run rule test as we have not implemented it yet class="
                        + nextRuleTest.getClass().getName());
                allPassed = false;
            }
        } // end for(nextRuleTest
        
        return allPassed;
    }
    
    /**
     * Runs StringRuleTests against the Query Variables stage and the Before Results Import stage
     * 
     * @param allNormalisationRules
     * @param allPassed
     * @param nextStringRuleTest
     * @return
     * @throws InvalidStageException
     * @throws QueryAllException
     * @throws RuntimeException
     * @throws ValidationFailedException
     * @throws UntestableRuleException
     */
    public static boolean runStringRuleTest(final Map<URI, NormalisationRule> allNormalisationRules,
            final StringRuleTest nextStringRuleTest) throws InvalidStageException, QueryAllException, RuntimeException,
        ValidationFailedException, UntestableRuleException
    {
        boolean allPassed = true;
        
        final String nextTestInputString = nextStringRuleTest.getTestInputString();
        final String nextTestOutputString = nextStringRuleTest.getTestOutputString();
        
        String nextInputTestResult = nextTestInputString;
        
        if(nextStringRuleTest.getStages().contains(NormalisationRuleSchema.getRdfruleStageQueryVariables()))
        {
            for(final NormalisationRule nextRule : RuleUtils.getSortedRulesByUris(allNormalisationRules,
                    nextStringRuleTest.getRuleUris(), SortOrder.LOWEST_ORDER_FIRST))
            {
                if(nextRule.usedInStage(NormalisationRuleSchema.getRdfruleStageQueryVariables()))
                {
                    if(nextRule instanceof TransformingRule)
                    {
                        final TransformingRule transformingRule = (TransformingRule)nextRule;
                        try
                        {
                            nextInputTestResult =
                                    (String)transformingRule.normaliseByStage(
                                            NormalisationRuleSchema.getRdfruleStageQueryVariables(),
                                            nextTestInputString);
                        }
                        catch(final InvalidStageException e)
                        {
                            RuleUtils.log
                                    .error("InvalidStageException found from hardcoded stage URI insertion after check that stage was used and valid, bad things may happen now!",
                                            e);
                            throw new QueryAllException("Found fatal InvalidStageException in hardcoded stage URI use",
                                    e);
                        }
                    }
                    else if(nextRule instanceof ValidatingRule)
                    {
                        final ValidatingRule validatingRule = (ValidatingRule)nextRule;
                        
                        boolean result = false;
                        
                        try
                        {
                            result =
                                    validatingRule.normaliseByStage(
                                            NormalisationRuleSchema.getRdfruleStageQueryVariables(),
                                            nextTestInputString);
                        }
                        catch(final InvalidStageException e)
                        {
                            RuleUtils.log
                                    .error("InvalidStageException found from hardcoded stage URI insertion after check that stage was used and valid, bad things may happen now!",
                                            e);
                            throw new QueryAllException("Found fatal InvalidStageException in hardcoded stage URI use",
                                    e);
                        }
                        
                        if(!result)
                        {
                            RuleUtils.log.error("ValidatingRule failed nextRule.getKey()="
                                    + nextRule.getKey().stringValue());
                            allPassed = false;
                        }
                    }
                    else
                    {
                        throw new UntestableRuleException(
                                "NormalisationRule type not supported for testing in this implementation", nextRule,
                                nextStringRuleTest);
                    }
                }
            }
            
            if(allPassed && nextInputTestResult.equals(nextTestOutputString))
            {
                if(RuleUtils._DEBUG)
                {
                    RuleUtils.log.debug("TEST-PASS input test pass: nextTestInputString=" + nextTestInputString
                            + " nextInputTestResult=" + nextInputTestResult);
                }
            }
            else
            {
                allPassed = false;
                
                if(RuleUtils._INFO)
                {
                    RuleUtils.log
                            .info("TEST-FAIL: input test did not result in the output string: nextTestInputString="
                                    + nextTestInputString + " actual output was nextInputTestResult="
                                    + nextInputTestResult + " expected output was nextTestOutputString="
                                    + nextTestOutputString);
                    RuleUtils.log.info("TEST-FAIL: nextRuleTest.toString()=" + nextStringRuleTest.toString());
                }
            }
        }
        
        if(nextStringRuleTest.getStages().contains(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport()))
        {
            String nextOutputTestResult = nextTestInputString;
            
            for(final NormalisationRule nextRule : RuleUtils.getSortedRulesByUris(allNormalisationRules,
                    nextStringRuleTest.getRuleUris(), SortOrder.HIGHEST_ORDER_FIRST))
            {
                if(nextRule instanceof TransformingRule)
                {
                    final TransformingRule transformingRule = (TransformingRule)nextRule;
                    nextOutputTestResult =
                            (String)transformingRule.normaliseByStage(
                                    NormalisationRuleSchema.getRdfruleStageBeforeResultsImport(), nextTestInputString);
                }
                else if(nextRule instanceof ValidatingRule)
                {
                    final ValidatingRule validatingRule = (ValidatingRule)nextRule;
                    
                    final boolean result =
                            validatingRule.normaliseByStage(NormalisationRuleSchema.getRdfruleStageQueryVariables(),
                                    nextTestInputString);
                    
                    if(!result)
                    {
                        allPassed = false;
                    }
                }
                else
                {
                    throw new UntestableRuleException(
                            "NormalisationRule type not supported for testing in this implementation", nextRule,
                            nextStringRuleTest);
                }
                
            }
            
            if(allPassed && nextOutputTestResult.equals(nextTestInputString))
            {
                if(RuleUtils._DEBUG)
                {
                    RuleUtils.log.debug("TEST-PASS output test pass: nextTestInputString=" + nextTestInputString
                            + " actual output :: nextOutputTestResult=" + nextOutputTestResult
                            + " expected output :: nextTestOutputString=" + nextTestOutputString);
                }
            }
            else
            {
                allPassed = false;
                
                if(RuleUtils._INFO)
                {
                    RuleUtils.log
                            .info("TEST-FAIL: output test did not result in the input string: nextTestInputString="
                                    + nextTestInputString + " actual output :: nextOutputTestResult="
                                    + nextOutputTestResult + " expected output :: nextTestOutputString="
                                    + nextTestOutputString);
                    RuleUtils.log.info("TEST-FAIL: nextRuleTest.toString()=" + nextStringRuleTest.toString());
                }
            }
        } // end if(this.stages.contains(rdfruleStageBeforeResultsImport)
        return allPassed;
    }
    
    /**
	 * 
	 */
    private RuleUtils()
    {
    }
    
}
