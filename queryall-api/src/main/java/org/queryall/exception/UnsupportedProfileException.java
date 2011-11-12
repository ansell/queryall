/**
 * 
 */
package org.queryall.exception;

import org.queryall.api.profile.Profile;

/**
 * An exception that is thrown when an unknown Profile is encountered
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class UnsupportedProfileException extends QueryAllException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 9132659393857953163L;
    private Profile profileCause;
    
    /**
     * 
     */
    public UnsupportedProfileException()
    {
        super();
    }
    
    /**
     * @param message
     */
    public UnsupportedProfileException(final String message)
    {
        super(message);
    }
    
    /**
     * @param message
     */
    public UnsupportedProfileException(final String message, Profile nextProfile)
    {
        super(message);
        this.setProfileCause(nextProfile);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedProfileException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedProfileException(final String message, Profile nextProfile, final Throwable cause)
    {
        super(message, cause);
        this.setProfileCause(nextProfile);
    }
    
    /**
     * @param cause
     */
    public UnsupportedProfileException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * @return the profileCause
     */
    public Profile getProfileCause()
    {
        return profileCause;
    }

    /**
     * @param profileCause the profileCause to set
     */
    public void setProfileCause(Profile profileCause)
    {
        this.profileCause = profileCause;
    }
    
}
