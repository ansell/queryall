package org.queryall.api.services;

import java.util.Collections;
import java.util.Set;

import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class QueryAllEnum
{
    private static final Logger log = LoggerFactory.getLogger(QueryAllEnum.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = QueryAllEnum.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = QueryAllEnum.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = QueryAllEnum.log.isInfoEnabled();
    
    private Set<URI> typeURIs;
    private String name;
    
    /**
     * Create a new QueryType enum using the given name, which must be unique, and the given URIs to
     * define the types for objects based on this enum.
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public QueryAllEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        this.setName(nextName);
        this.setTypeURIs(nextTypeURIs);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(obj == null)
        {
            return false;
        }
        if(!(obj instanceof QueryAllEnum))
        {
            return false;
        }
        final QueryAllEnum other = (QueryAllEnum)obj;
        if(this.name == null)
        {
            if(other.name != null)
            {
                return false;
            }
        }
        else if(!this.name.equals(other.name))
        {
            return false;
        }
        if(this.typeURIs == null)
        {
            if(other.typeURIs != null)
            {
                return false;
            }
        }
        else
        {
            if(this.typeURIs.size() != other.typeURIs.size())
            {
                return false;
            }
            
            for(final URI nextUri : this.typeURIs)
            {
                if(!other.typeURIs.contains(nextUri))
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * @return The name for this enum. This should be the name of the implementing class to ensure
     *         that the name will be unique.
     */
    public final String getName()
    {
        return this.name;
    }
    
    /**
     * 
     * @return The set of URIs that define the types for this enum.
     */
    public final Set<URI> getTypeURIs()
    {
        return Collections.unmodifiableSet(this.typeURIs);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + ((this.typeURIs == null) ? 0 : this.typeURIs.hashCode());
        return result;
    }
    
    /**
     * Determines if the given set of URIs match the types that are defined for this enum.
     * 
     * By default, each of the given type URIs must be present in the types defined for this enum.
     * 
     * This method can be overridden to provider custom behaviour for subclasses.
     * 
     * @param nextTypeURIs
     *            A set of URIs to match against the URIs setup for this enum.
     * @return True if the given set of URIs suitably matches the type URIs defined for this enum.
     */
    protected boolean matchForTypeUris(final Set<URI> nextTypeURIs)
    {
        boolean matching = true;
        
        for(final URI nextURI : nextTypeURIs)
        {
            // Default implementation of this method is to check whether the given URIs are all in
            // the type URIs for this Enum
            // This behaviour can be overriden to provide different behaviour in implementations
            if(!this.getTypeURIs().contains(nextURI))
            {
                matching = false;
            }
        }
        
        return matching;
    }
    
    /**
     * The name can only be set using the constructor.
     * 
     * @param name
     *            the name to set
     */
    protected final void setName(final String name)
    {
        this.name = name;
    }
    
    /**
     * The type can only be set using the constructor.
     * 
     * @param typeURIs
     *            the typeURIs to set
     */
    protected final void setTypeURIs(final Set<URI> typeURIs)
    {
        this.typeURIs = typeURIs;
    }
    
    /**
     * Returns a string including the name and the list of type URIs
     */
    @Override
    public String toString()
    {
        return this.name + " {" + this.typeURIs.toString() + "}";
    }
}