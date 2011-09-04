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
import org.queryall.api.provider.Provider;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.ruletest.StringRuleTest;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.utils.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class RuleUtils
{
    public static final Logger log = LoggerFactory.getLogger(RuleUtils.class);
    public static final boolean _TRACE = RuleUtils.log.isTraceEnabled();
    public static final boolean _DEBUG = RuleUtils.log.isDebugEnabled();
    public static final boolean _INFO = RuleUtils.log.isInfoEnabled();
    
    public static List<NormalisationRule> getSortedRulesByUris(final Map<URI, NormalisationRule> allNormalisationRules,
            final Collection<URI> rdfNormalisationsNeeded, final SortOrder sortOrder)
    {
        final List<NormalisationRule> results = new ArrayList<NormalisationRule>();
        // final List<NormalisationRule> intermediateResults = new ArrayList<NormalisationRule>();
        // final Map<URI, NormalisationRule> allNormalisationRules =
        // this.getAllNormalisationRules();
        
        for(final URI nextProviderNormalisationRule : rdfNormalisationsNeeded)
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
     * Runs rule tests over the stages "QueryVariables" and "BeforeResultsImport"
     * 
     * @param myRuleTests
     * @return true if all of the tests passed, otherwise it returns false
     */
    public static boolean runRuleTests(final Collection<RuleTest> myRuleTests,
            final Map<URI, NormalisationRule> allNormalisationRules)
    {
        boolean allPassed = true;
        
        for(final RuleTest nextRuleTest : myRuleTests)
        {
            // TODO: remove cast to RegexRuleTest here
            final String nextTestInputString = ((StringRuleTest)nextRuleTest).getTestInputString();
            final String nextTestOutputString = ((StringRuleTest)nextRuleTest).getTestOutputString();
            
            String nextInputTestResult = nextTestInputString;
            
            // TODO: only testing two out of the stages here
            if(nextRuleTest.getStages().contains(NormalisationRuleSchema.getRdfruleStageQueryVariables()))
            {
                for(final NormalisationRule nextRule : RuleUtils.getSortedRulesByUris(allNormalisationRules,
                        nextRuleTest.getRuleUris(), SortOrder.LOWEST_ORDER_FIRST))
                {
                    nextInputTestResult =
                            (String)nextRule.normaliseByStage(NormalisationRuleSchema.getRdfruleStageQueryVariables(),
                                    nextTestInputString);
                }
                
                if(nextInputTestResult.equals(nextTestOutputString))
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
                                        + nextTestInputString + " actual output :: nextInputTestResult="
                                        + nextInputTestResult + " expected output :: nextTestOutputString="
                                        + nextTestOutputString);
                        RuleUtils.log.info("TEST-FAIL: nextRuleTest.toString()=" + nextRuleTest.toString());
                    }
                }
            }
            
            if(nextRuleTest.getStages().contains(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport()))
            {
                String nextOutputTestResult = nextTestInputString;
                
                for(final NormalisationRule nextRule : RuleUtils.getSortedRulesByUris(allNormalisationRules,
                        nextRuleTest.getRuleUris(), SortOrder.HIGHEST_ORDER_FIRST))
                {
                    nextOutputTestResult =
                            (String)nextRule.normaliseByStage(
                                    NormalisationRuleSchema.getRdfruleStageBeforeResultsImport(), nextTestInputString);
                    
                    if(nextOutputTestResult.equals(nextTestInputString))
                    {
                        if(RuleUtils._DEBUG)
                        {
                            RuleUtils.log.debug("TEST-PASS output test pass: nextTestInputString="
                                    + nextTestInputString + " actual output :: nextOutputTestResult="
                                    + nextOutputTestResult + " expected output :: nextTestOutputString="
                                    + nextTestOutputString);
                        }
                    }
                    else
                    {
                        allPassed = false;
                        
                        if(RuleUtils._INFO)
                        {
                            RuleUtils.log
                                    .info("TEST-FAIL: output test did not result in the input string: nextTestInputString="
                                            + nextTestInputString
                                            + " actual output :: nextOutputTestResult="
                                            + nextOutputTestResult
                                            + " expected output :: nextTestOutputString="
                                            + nextTestOutputString);
                            RuleUtils.log.info("TEST-FAIL: nextRuleTest.toString()=" + nextRuleTest.toString());
                        }
                    }
                }
            } // end if(this.stages.contains(rdfruleStageBeforeResultsImport)
        } // end for(nextRuleTest
        
        return allPassed;
    }
    
    /**
	 * 
	 */
    public RuleUtils()
    {
        // TODO Auto-generated constructor stub
    }
    
}
