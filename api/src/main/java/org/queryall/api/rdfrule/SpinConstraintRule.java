package org.queryall.api.rdfrule;

/**
 * A SpinConstraintRule takes the rules defined in the SpinNormalisationRule interface and uses them
 * to determine whether any of the given constraints fail for data. If any of the constraints fail,
 * the rule fails validation.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface SpinConstraintRule extends SpinNormalisationRule, ValidatingRule
{
    
}