/**
 * 
 */
package org.queryall.api.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.queryall.api.profile.Profile;
import org.queryall.api.profile.ProfileSchema;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.exception.InvalidStageException;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public final class DummyNormalisationRule implements NormalisationRule
{
    
    private int order = 0;
    private Set<URI> validStages = new HashSet<URI>();
    private Set<URI> stages = new HashSet<URI>();
    private Collection<URI> relatedNamespaces = new HashSet<URI>();
    private URI profileIncludeExcludeOrder = ProfileSchema.getProfileIncludeExcludeOrderUndefinedUri();
    private String title = "";
    private URI key = null;
    private String description = "";
    private URI curationStatus = null;
    private Collection<Statement> unrecognisedStatements = new ArrayList<Statement>();
    
    /**
     * 
     */
    public DummyNormalisationRule()
    {

    }
    
    @Override
    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        this.unrecognisedStatements.add(unrecognisedStatement);
    }
    
    @Override
    public URI getCurationStatus()
    {
        return this.curationStatus;
    }
    
    @Override
    public QueryAllNamespaces getDefaultNamespace()
    {
        return QueryAllNamespaces.RDFRULE;
    }
    
    @Override
    public String getDescription()
    {
        return this.description;
    }
    
    @Override
    public Set<URI> getElementTypes()
    {
        Set<URI> types = new HashSet<URI>();
        
        types.add(NormalisationRuleSchema.getNormalisationRuleTypeUri());
        
        return types;
    }
    
    @Override
    public URI getKey()
    {
        return this.key;
    }
    
    @Override
    public String getTitle()
    {
        return this.title;
    }
    
    @Override
    public Collection<Statement> getUnrecognisedStatements()
    {
        return this.unrecognisedStatements;
    }
    
    @Override
    public Collection<Statement> resetUnrecognisedStatements()
    {
        Collection<Statement> unrecognisedStatementsTemp = new ArrayList<Statement>(this.unrecognisedStatements);
        
        this.unrecognisedStatements = new ArrayList<Statement>();
        
        return unrecognisedStatementsTemp;
    }
    
    @Override
    public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    @Override
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    @Override
    public void setKey(String nextKey) throws IllegalArgumentException
    {
        setKey(new ValueFactoryImpl().createURI(nextKey));
    }
    
    @Override
    public void setKey(URI nextKey)
    {
        this.key = nextKey;
    }
    
    @Override
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    /**
     * NOTE: This is a dummy implementation. Always returns true with no sideeffects.
     * @param myRepository
     * @param modelVersion
     * @param contextUris
     * @return
     * @throws OpenRDFException
     */
    @Override
    public boolean toRdf(Repository myRepository, int modelVersion, URI... contextUris) throws OpenRDFException
    {
        return true;
    }
    
    @Override
    public int compareTo(NormalisationRule otherRule)
    {
        if(this.getOrder() > otherRule.getOrder())
        {
            return 1;
        }
        else if(this.getOrder() == otherRule.getOrder())
        {
            return 0;
        }
        else
        {
            return -1;
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((curationStatus == null) ? 0 : curationStatus.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + order;
        result = prime * result + ((profileIncludeExcludeOrder == null) ? 0 : profileIncludeExcludeOrder.hashCode());
        result = prime * result + ((relatedNamespaces == null) ? 0 : relatedNamespaces.hashCode());
        result = prime * result + ((stages == null) ? 0 : stages.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((unrecognisedStatements == null) ? 0 : unrecognisedStatements.hashCode());
        result = prime * result + ((validStages == null) ? 0 : validStages.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(obj == null)
        {
            return false;
        }
        if(!(obj instanceof DummyNormalisationRule))
        {
            return false;
        }
        DummyNormalisationRule other = (DummyNormalisationRule)obj;
        if(curationStatus == null)
        {
            if(other.curationStatus != null)
            {
                return false;
            }
        }
        else if(!curationStatus.equals(other.curationStatus))
        {
            return false;
        }
        if(description == null)
        {
            if(other.description != null)
            {
                return false;
            }
        }
        else if(!description.equals(other.description))
        {
            return false;
        }
        if(key == null)
        {
            if(other.key != null)
            {
                return false;
            }
        }
        else if(!key.equals(other.key))
        {
            return false;
        }
        if(order != other.order)
        {
            return false;
        }
        if(profileIncludeExcludeOrder == null)
        {
            if(other.profileIncludeExcludeOrder != null)
            {
                return false;
            }
        }
        else if(!profileIncludeExcludeOrder.equals(other.profileIncludeExcludeOrder))
        {
            return false;
        }
        if(relatedNamespaces == null)
        {
            if(other.relatedNamespaces != null)
            {
                return false;
            }
        }
        else if(!relatedNamespaces.equals(other.relatedNamespaces))
        {
            return false;
        }
        if(stages == null)
        {
            if(other.stages != null)
            {
                return false;
            }
        }
        else if(!stages.equals(other.stages))
        {
            return false;
        }
        if(title == null)
        {
            if(other.title != null)
            {
                return false;
            }
        }
        else if(!title.equals(other.title))
        {
            return false;
        }
        if(unrecognisedStatements == null)
        {
            if(other.unrecognisedStatements != null)
            {
                return false;
            }
        }
        else if(!unrecognisedStatements.equals(other.unrecognisedStatements))
        {
            return false;
        }
        if(validStages == null)
        {
            if(other.validStages != null)
            {
                return false;
            }
        }
        else if(!validStages.equals(other.validStages))
        {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DummyNormalisationRule [order=");
        builder.append(order);
        builder.append(", title=");
        builder.append(title);
        builder.append(", key=");
        builder.append(key);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public URI getProfileIncludeExcludeOrder()
    {
        return this.profileIncludeExcludeOrder;
    }
    
    /**
     * NOTE: This is a dummy class, always returns true
     * 
     * @param orderedProfileList
     * @param allowImplicitInclusions
     * @param includeNonProfileMatched
     * @return
     */
    @Override
    public boolean isUsedWithProfileList(List<Profile> orderedProfileList, boolean allowImplicitInclusions,
            boolean includeNonProfileMatched)
    {
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.ProfilableInterface#setProfileIncludeExcludeOrder(org.openrdf.model.URI)
     */
    @Override
    public void setProfileIncludeExcludeOrder(URI profileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = profileIncludeExcludeOrder;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.rdfrule.NormalisationRule#addRelatedNamespace(org.openrdf.model.URI)
     */
    @Override
    public void addRelatedNamespace(URI nextRelatedNamespace)
    {
        this.relatedNamespaces.add(nextRelatedNamespace);
    }
    
    /**
     * NOTE: This is a dummy class, there are no InvalidStageExceptions thrown
     * 
     * @param nextStage
     * @throws InvalidStageException
     */
    @Override
    public void addStage(URI nextStage) throws InvalidStageException
    {
        this.stages.add(nextStage);
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.rdfrule.NormalisationRule#getOrder()
     */
    @Override
    public int getOrder()
    {
        return this.order;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.rdfrule.NormalisationRule#getRelatedNamespaces()
     */
    @Override
    public Collection<URI> getRelatedNamespaces()
    {
        return this.relatedNamespaces;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.rdfrule.NormalisationRule#getStages()
     */
    @Override
    public Set<URI> getStages()
    {
        return this.stages;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.rdfrule.NormalisationRule#getValidStages()
     */
    @Override
    public Set<URI> getValidStages()
    {
        return this.validStages;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.rdfrule.NormalisationRule#setOrder(int)
     */
    @Override
    public void setOrder(int order)
    {
        this.order = order;
    }
    
    /**
     * NOTE: This is a dummy implementation. Never throws InvalidStageException
     */
    @Override
    public boolean usedInStage(URI stage) throws InvalidStageException
    {
        return this.stages.contains(stage);
    }
    
    /**
     * NOTE: This is a dummy implementation. Never throws InvalidStageException
     */
    @Override
    public boolean validInStage(URI stage) throws InvalidStageException
    {
        return this.validStages.contains(stage);
    }
    
}
