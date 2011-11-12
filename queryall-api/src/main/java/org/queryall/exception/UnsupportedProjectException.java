/**
 * 
 */
package org.queryall.exception;

import org.queryall.api.project.Project;

/**
 * An exception that is thrown when an unknown Project is encountered
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class UnsupportedProjectException extends QueryAllException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 9132659393857953163L;
    private Project projectCause;
    
    /**
     * 
     */
    public UnsupportedProjectException()
    {
        super();
    }
    
    /**
     * @param message
     */
    public UnsupportedProjectException(final String message)
    {
        super(message);
    }
    
    /**
     * @param message
     */
    public UnsupportedProjectException(final String message, Project nextProject)
    {
        super(message);
        this.setProjectCause(nextProject);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedProjectException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedProjectException(final String message, Project nextProject, final Throwable cause)
    {
        super(message, cause);
        this.setProjectCause(nextProject);
    }
    
    /**
     * @param cause
     */
    public UnsupportedProjectException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * @return the projectCause
     */
    public Project getProjectCause()
    {
        return projectCause;
    }

    /**
     * @param projectCause the projectCause to set
     */
    public void setProjectCause(Project projectCause)
    {
        this.projectCause = projectCause;
    }
    
}
