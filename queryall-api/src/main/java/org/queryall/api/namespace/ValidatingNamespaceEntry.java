/**
 * 
 */
package org.queryall.api.namespace;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface ValidatingNamespaceEntry extends NamespaceEntry
{
    boolean getValidationPossible();
    
    void setValidationPossible(boolean b);
    
    boolean validateIdentifier(String identifier);
}
