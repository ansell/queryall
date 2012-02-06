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
import org.openrdf.repository.Repository;
import org.queryall.api.base.BaseQueryAllInterface;
import org.queryall.api.profile.Profile;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.ProfileIncludeExclude;
import org.queryall.api.utils.ProfileMatch;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.exception.InvalidStageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public final class DummyNormalisationRule implements NormalisationRule
{
    private static final Logger LOG = LoggerFactory.getLogger(DummyNormalisationRule.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = DummyNormalisationRule.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = DummyNormalisationRule.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = DummyNormalisationRule.LOG.isInfoEnabled();
    
    private int order = 0;
    private Set<URI> validStages = new HashSet<URI>();
    private Set<URI> stages = new HashSet<URI>();
    private Set<URI> relatedNamespaces = new HashSet<URI>();
    private ProfileIncludeExclude profileIncludeExcludeOrder = ProfileIncludeExclude.UNDEFINED;
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
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.rdfrule.NormalisationRule#addRelatedNamespace(org.openrdf.model.URI)
     */
    @Override
    public void addRelatedNamespace(final URI nextRelatedNamespace)
    {
        this.relatedNamespaces.add(nextRelatedNamespace);
    }
    
    @Override
    public void addStage(final URI stage) throws InvalidStageException
    {
        if(this.validInStage(stage))
        {
            this.stages.add(stage);
        }
        else
        {
            throw new InvalidStageException("Attempted to add a stage that was not in the list of valid stages", this,
                    stage);
        }
    }
    
    @Override
    public void addUnrecognisedStatement(final Statement unrecognisedStatement)
    {
        this.unrecognisedStatements.add(unrecognisedStatement);
    }
    
    @Override
    public int compareTo(final NormalisationRule otherRule)
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
        if(!(obj instanceof BaseQueryAllInterface))
        {
            return false;
        }
        final BaseQueryAllInterface other = (BaseQueryAllInterface)obj;
        if(this.getCurationStatus() == null)
        {
            if(other.getCurationStatus() != null)
            {
                return false;
            }
        }
        else if(!this.getCurationStatus().equals(other.getCurationStatus()))
        {
            return false;
        }
        if(this.getDescription() == null)
        {
            if(other.getDescription() != null)
            {
                return false;
            }
        }
        else if(!this.getDescription().equals(other.getDescription()))
        {
            return false;
        }
        if(this.getKey() == null)
        {
            if(other.getKey() != null)
            {
                return false;
            }
        }
        else if(!this.getKey().equals(other.getKey()))
        {
            return false;
        }
        if(this.getTitle() == null)
        {
            if(other.getTitle() != null)
            {
                return false;
            }
        }
        else if(!this.getTitle().equals(other.getTitle()))
        {
            return false;
        }
        if(this.getUnrecognisedStatements() == null)
        {
            if(other.getUnrecognisedStatements() != null)
            {
                return false;
            }
        }
        else if(!this.getUnrecognisedStatements().equals(other.getUnrecognisedStatements()))
        {
            return false;
        }
        if(!(obj instanceof NormalisationRule))
        {
            return false;
        }
        final NormalisationRule otherRule = (NormalisationRule)obj;
        if(this.getOrder() != otherRule.getOrder())
        {
            return false;
        }
        if(this.getProfileIncludeExcludeOrder() == null)
        {
            if(otherRule.getProfileIncludeExcludeOrder() != null)
            {
                return false;
            }
        }
        else if(!this.getProfileIncludeExcludeOrder().equals(otherRule.getProfileIncludeExcludeOrder()))
        {
            return false;
        }
        if(this.getRelatedNamespaces() == null)
        {
            if(otherRule.getRelatedNamespaces() != null)
            {
                return false;
            }
        }
        else if(!this.getRelatedNamespaces().equals(otherRule.getRelatedNamespaces()))
        {
            return false;
        }
        if(this.getStages() == null)
        {
            if(otherRule.getStages() != null)
            {
                return false;
            }
        }
        else if(!this.getStages().equals(otherRule.getStages()))
        {
            return false;
        }
        if(this.getValidStages() == null)
        {
            if(otherRule.getValidStages() != null)
            {
                return false;
            }
        }
        else if(!this.getValidStages().equals(otherRule.getValidStages()))
        {
            return false;
        }
        return true;
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
        final Set<URI> types = new HashSet<URI>();
        
        types.add(NormalisationRuleSchema.getNormalisationRuleTypeUri());
        
        return types;
    }
    
    @Override
    public URI getKey()
    {
        return this.key;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.rdfrule.NormalisationRule#getOrder()
     */
    @Override
    public int getOrder()
    {
        return this.order;
    }
    
    @Override
    public ProfileIncludeExclude getProfileIncludeExcludeOrder()
    {
        return this.profileIncludeExcludeOrder;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.rdfrule.NormalisationRule#getRelatedNamespaces()
     */
    @Override
    public Collection<URI> getRelatedNamespaces()
    {
        return this.relatedNamespaces;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.rdfrule.NormalisationRule#getStages()
     */
    @Override
    public Set<URI> getStages()
    {
        return this.stages;
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
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.rdfrule.NormalisationRule#getValidStages()
     */
    @Override
    public Set<URI> getValidStages()
    {
        return this.validStages;
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
        result = prime * result + ((this.curationStatus == null) ? 0 : this.curationStatus.hashCode());
        result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
        result = prime * result + ((this.key == null) ? 0 : this.key.hashCode());
        result = prime * result + this.order;
        result =
                prime * result
                        + ((this.profileIncludeExcludeOrder == null) ? 0 : this.profileIncludeExcludeOrder.hashCode());
        result = prime * result + ((this.relatedNamespaces == null) ? 0 : this.relatedNamespaces.hashCode());
        result = prime * result + ((this.stages == null) ? 0 : this.stages.hashCode());
        result = prime * result + ((this.title == null) ? 0 : this.title.hashCode());
        result = prime * result + ((this.unrecognisedStatements == null) ? 0 : this.unrecognisedStatements.hashCode());
        result = prime * result + ((this.validStages == null) ? 0 : this.validStages.hashCode());
        return result;
    }
    
    @Override
    public boolean isUsedWithProfileList(final List<Profile> orderedProfileList, final boolean allowImplicitInclusions,
            final boolean includeNonProfileMatched)
    {
        return ProfileMatch.isUsedWithProfileList(this, orderedProfileList, allowImplicitInclusions,
                includeNonProfileMatched);
    }
    
    @Override
    public boolean resetRelatedNamespaces()
    {
        this.relatedNamespaces = new HashSet<URI>();
        
        return true;
    }
    
    @Override
    public boolean resetStages()
    {
        this.stages = new HashSet<URI>();
        
        return true;
    }
    
    @Override
    public Collection<Statement> resetUnrecognisedStatements()
    {
        final Collection<Statement> unrecognisedStatementsTemp = new ArrayList<Statement>(this.unrecognisedStatements);
        
        this.unrecognisedStatements = new ArrayList<Statement>();
        
        return unrecognisedStatementsTemp;
    }
    
    @Override
    public void setCurationStatus(final URI nextCurationStatus)
    {
        this.curationStatus = nextCurationStatus;
    }
    
    @Override
    public void setDescription(final String nextDescription)
    {
        this.description = nextDescription;
    }
    
    @Override
    public void setKey(final String nextKey) throws IllegalArgumentException
    {
        this.setKey(Constants.VALUE_FACTORY.createURI(nextKey));
    }
    
    @Override
    public void setKey(final URI nextKey)
    {
        this.key = nextKey;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.rdfrule.NormalisationRule#setOrder(int)
     */
    @Override
    public void setOrder(final int nextOrder)
    {
        this.order = nextOrder;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.queryall.api.base.ProfilableInterface#setProfileIncludeExcludeOrder(org.openrdf.model
     * .URI)
     */
    @Override
    public void setProfileIncludeExcludeOrder(final ProfileIncludeExclude nextProfileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = nextProfileIncludeExcludeOrder;
    }
    
    @Override
    public void setTitle(final String nextTitle)
    {
        this.title = nextTitle;
    }
    
    /**
     * NOTE: This is a dummy implementation. Always returns true with no sideeffects.
     * 
     * @param myRepository
     * @param modelVersion
     * @param contextUris
     * @return
     * @throws OpenRDFException
     */
    @Override
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... contextUris)
        throws OpenRDFException
    {
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("DummyNormalisationRule [order=");
        builder.append(this.order);
        builder.append(", title=");
        builder.append(this.title);
        builder.append(", key=");
        builder.append(this.key);
        builder.append("]");
        return builder.toString();
    }
    
    /**
     * NOTE: This is a dummy implementation. Never throws InvalidStageException
     */
    @Override
    public boolean usedInStage(final URI stage) throws InvalidStageException
    {
        return this.stages.contains(stage);
    }
    
    /**
     * NOTE: This is a dummy implementation. Never throws InvalidStageException
     */
    @Override
    public boolean validInStage(final URI stage) throws InvalidStageException
    {
        return this.validStages.contains(stage);
    }
    
}
