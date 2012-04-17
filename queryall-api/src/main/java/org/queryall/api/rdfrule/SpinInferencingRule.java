package org.queryall.api.rdfrule;

/**
 * A SpinInferencingRule takes the rules defined in the SpinNormalisationRule interface and uses
 * them to infer extra triples for the given RDF statements.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface SpinInferencingRule extends SpinNormalisationRule, TransformingRule
{
    
}