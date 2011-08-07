/**
 * 
 */
package org.queryall.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.queryall.api.NormalisationRule;
import org.queryall.api.Provider;
import org.queryall.api.RuleTest;
import org.queryall.impl.NormalisationRuleImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class RuleUtils
{
    public static final Logger log = Logger.getLogger(RuleUtils.class.getName());
    public static final boolean _TRACE = RuleUtils.log.isTraceEnabled();
    public static final boolean _DEBUG = RuleUtils.log.isDebugEnabled();
    public static final boolean _INFO = RuleUtils.log.isInfoEnabled();
    
    /**
	 * 
	 */
    public RuleUtils()
    {
        // TODO Auto-generated constructor stub
    }
    
    public static List<NormalisationRule> getSortedRulesByUris(Map<URI, NormalisationRule> allNormalisationRules,
            Collection<URI> rdfNormalisationsNeeded, SortOrder sortOrder)
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
    
    /**
     * Runs rule tests over the stages "QueryVariables" and "BeforeResultsImport"
     * 
     * @param myRuleTests
     * @return true if all of the tests passed, otherwise it returns false
     */
    public static boolean runRuleTests(Collection<RuleTest> myRuleTests,
            Map<URI, NormalisationRule> allNormalisationRules)
    {
        boolean allPassed = true;
        
        for(final RuleTest nextRuleTest : myRuleTests)
        {
            final String nextTestInputString = nextRuleTest.getTestInputString();
            final String nextTestOutputString = nextRuleTest.getTestOutputString();
            
            String nextInputTestResult = nextTestInputString;
            
            // TODO: only testing two out of the stages here
            if(nextRuleTest.getStages().contains(NormalisationRuleImpl.getRdfruleStageQueryVariables()))
            {
                for(final NormalisationRule nextRule : RuleUtils.getSortedRulesByUris(allNormalisationRules,
                        nextRuleTest.getRuleUris(), SortOrder.LOWEST_ORDER_FIRST))
                {
                    nextInputTestResult =
                            (String)nextRule.normaliseByStage(NormalisationRuleImpl.getRdfruleStageQueryVariables(),
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
            
            if(nextRuleTest.getStages().contains(NormalisationRuleImpl.getRdfruleStageBeforeResultsImport()))
            {
                String nextOutputTestResult = nextTestInputString;
                
                for(final NormalisationRule nextRule : RuleUtils.getSortedRulesByUris(allNormalisationRules,
                        nextRuleTest.getRuleUris(), SortOrder.HIGHEST_ORDER_FIRST))
                {
                    nextOutputTestResult =
                            (String)nextRule.normaliseByStage(
                                    NormalisationRuleImpl.getRdfruleStageBeforeResultsImport(), nextTestInputString);
                    
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
    
    public static List<NormalisationRule> getSortedRulesForProviders(Collection<Provider> providers,
            Map<URI, NormalisationRule> allNormalisationRules, SortOrder sortOrder)
    {
        List<NormalisationRule> results = new LinkedList<NormalisationRule>();
        
        for(Provider nextProvider : providers)
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
    
}
