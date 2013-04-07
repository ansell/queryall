/**
 * 
 */
package org.queryall.api.namespace;

/**
 * A ValidatingNamespaceEntry that is implemented using Regular Expressions to determine whether a
 * given identifier is valid for this namespace.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface RegexValidatingNamespaceEntry extends ValidatingNamespaceEntry
{
    /**
     * 
     * @return The Regular Expression that is used to validate identifiers that are thought to be in
     *         this namespace.
     */
    String getIdentifierRegex();
    
    /**
     * 
     * @param identifierRegex
     *            A Regular Expression that will be used to validate identifiers which are thought
     *            to be in this namespace.
     */
    void setIdentifierRegex(String identifierRegex);
    
}
