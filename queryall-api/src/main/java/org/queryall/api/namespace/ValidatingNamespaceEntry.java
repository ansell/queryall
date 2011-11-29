/**
 * 
 */
package org.queryall.api.namespace;

/**
 * A Namespace Entry that can validate identifiers that are thought to be in this namespace.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface ValidatingNamespaceEntry extends NamespaceEntry
{
    /**
     * 
     * @return True if validation is currently possible for this namespace entry.
     */
    boolean getValidationPossible();
    
    /**
     * Defines whether validation is possible for this namespace entry, so that validation is not
     * always on for all ValidatingNamespaceEntry objects
     * 
     * @param validationPossible
     *            True if validation is possible for this namespace entry.
     */
    void setValidationPossible(boolean validationPossible);
    
    /**
     * Validates the given identifier based on the validation method of this object
     * 
     * @param identifier
     *            A string representing an identifier that is thought to be in this namespace
     * @return True if the identifier is valid according to the rules for this namespace, or false
     *         otherwise
     */
    boolean validateIdentifier(String identifier);
}
