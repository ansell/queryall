/**
 * 
 */
package org.queryall.api.profile;

import org.queryall.api.services.QueryAllFactory;

/**
 * A factory for creating parsers for Profile objects.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface ProfileFactory extends QueryAllFactory<ProfileEnum, ProfileParser, Profile>
{
    
}
