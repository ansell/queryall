/**
 * 
 */
package org.queryall.api.provider;

import org.queryall.api.services.QueryAllFactory;

/**
 * A factory for creating parsers for Provider objects
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface ProviderFactory extends QueryAllFactory<ProviderEnum, ProviderParser, Provider>
{
    
}
