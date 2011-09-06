/**
 * 
 */
package org.queryall.api.namespace;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface ValidatingNamespaceEntry extends NamespaceEntry
{
    boolean validateIdentifier(String identifier);

    void setValidationPossible(boolean b);

    boolean getValidationPossible();
}
