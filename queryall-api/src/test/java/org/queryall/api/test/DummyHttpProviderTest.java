/**
 * 
 */
package org.queryall.api.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.provider.HttpProvider;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public class DummyHttpProviderTest extends AbstractHttpProviderTest
{
    
    /* (non-Javadoc)
     * @see org.queryall.api.test.AbstractHttpProviderTest#getNewTestHttpProvider()
     */
    @Override
    public final HttpProvider getNewTestHttpProvider()
    {
        return new DummyHttpProvider();
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.test.AbstractProfilableTest#getNewTestProfile()
     */
    @Override
    public final Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
    
}
