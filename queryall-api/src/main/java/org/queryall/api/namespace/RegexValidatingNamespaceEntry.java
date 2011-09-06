/**
 * 
 */
package org.queryall.api.namespace;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface RegexValidatingNamespaceEntry extends ValidatingNamespaceEntry
{
    String getIdentifierRegex();
    
    void setIdentifierRegex(String identifierRegex);
    
}
