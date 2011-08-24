/**
 * 
 */
package org.queryall.api.namespace;

import org.queryall.api.services.QueryAllFactory;

/**
 * A factory for creating parsers for NamespaceEntry objects
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface NamespaceEntryFactory extends QueryAllFactory<NamespaceEntryEnum, NamespaceEntryParser, NamespaceEntry>
{
    
}
