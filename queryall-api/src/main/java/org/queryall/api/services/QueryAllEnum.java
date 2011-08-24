package org.queryall.api.services;

import java.util.Collections;
import java.util.List;

import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryAllEnum
{
    
    protected static final Logger log = LoggerFactory.getLogger(QueryAllEnum.class);
    private List<URI> typeURIs;
    private String name;
    
    /**
     * Create a new QueryType enum using the given name, which must be unique
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public QueryAllEnum(final String nextName, final List<URI> nextTypeURIs)
    {
        this.setName(nextName);
        this.setTypeURI(nextTypeURIs);
    }
    
    /**
     * @return the name
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * 
     * @return the typeURIs
     */
    public List<URI> getTypeURIs()
    {
        return Collections.unmodifiableList(this.typeURIs);
    }
    
    /**
     * The name can only be set using the constructor.
     * 
     * @param name
     *            the name to set
     */
    protected void setName(final String name)
    {
        this.name = name;
    }
    
    /**
     * The type can only be set using the constructor.
     * 
     * @param typeURIs
     *            the typeURIs to set
     */
    protected void setTypeURI(final List<URI> typeURI)
    {
        this.typeURIs = typeURI;
    }
    
}