package org.queryall.impl.rdfrule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.profile.Profile;
import org.queryall.api.profile.ProfileSchema;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.TransformingRuleSchema;
import org.queryall.api.rdfrule.ValidatingRuleSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.ProfileIncludeExclude;
import org.queryall.api.utils.ProfileMatch;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.exception.InvalidStageException;
import org.queryall.impl.base.BaseQueryAllImpl;
import org.queryall.utils.RdfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class BaseRuleImpl extends BaseQueryAllImpl implements NormalisationRule
{
    private static final Logger log = LoggerFactory.getLogger(BaseRuleImpl.class);
    private static final boolean TRACE = BaseRuleImpl.log.isTraceEnabled();
    private static final boolean DEBUG = BaseRuleImpl.log.isDebugEnabled();
    private static final boolean INFO = BaseRuleImpl.log.isInfoEnabled();
    
    private ProfileIncludeExclude profileIncludeExcludeOrder = ProfileIncludeExclude.UNDEFINED;
    
    private Collection<URI> relatedNamespaces = new ArrayList<URI>(10);
    
    private Set<URI> stages = new HashSet<URI>(10);
    
    private final Set<URI> validStages = new HashSet<URI>(10);
    
    private int order = 100;
    
    protected BaseRuleImpl()
    {
        super();
        
        this.validStages.addAll(this.setupValidStages());
    }
    
    // keyToUse is the URI of the next instance that can be found in
    // myRepository
    protected BaseRuleImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        this.validStages.addAll(this.setupValidStages());
        
        final Collection<Statement> currentUnrecognisedStatements = this.resetUnrecognisedStatements();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            // if(NormalisationRuleImpl.DEBUG)
            // {
            // NormalisationRuleImpl.log.debug("NormalisationRule: nextStatement: " +
            // nextStatement.toString());
            // }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(NormalisationRuleSchema.getNormalisationRuleTypeUri()))
            {
                if(BaseRuleImpl.TRACE)
                {
                    BaseRuleImpl.log.trace("NormalisationRule: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(NormalisationRuleSchema.getRdfruleOrder()))
            {
                this.setOrder(RdfUtils.getIntegerFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(NormalisationRuleSchema.getRdfruleHasRelatedNamespace()))
            {
                this.addRelatedNamespace((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(NormalisationRuleSchema.getRdfruleStage()))
            {
                try
                {
                    this.addStage((URI)nextStatement.getObject());
                }
                catch(final InvalidStageException ise)
                {
                    BaseRuleImpl.log
                            .error("Stage not applicable for this type of normalisation rule nextStatement.getObject()="
                                    + nextStatement.getObject().stringValue()
                                    + " validStages="
                                    + this.getValidStages().toString()
                                    + " this.getElementTypes()="
                                    + this.getElementTypes() + " keyToUse=" + keyToUse.stringValue());
                }
            }
            else if(nextStatement.getPredicate().equals(ProfileSchema.getProfileIncludeExcludeOrderUri()))
            {
                this.setProfileIncludeExcludeOrder(ProfileIncludeExclude.valueOf((URI)nextStatement.getObject()));
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        // if(NormalisationRuleImpl.DEBUG)
        // {
        // NormalisationRuleImpl.log.debug("NormalisationRuleImpl.fromRdf: would have returned... result="
        // + this.toString());
        // }
    }
    
    /**
     * 
     * @param nextRelatedNamespace
     */
    @Override
    public final void addRelatedNamespace(final URI nextRelatedNamespace)
    {
        this.relatedNamespaces.add(nextRelatedNamespace);
    }
    
    /**
     * @return the Stages
     */
    @Override
    public final void addStage(final URI stage) throws InvalidStageException
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
    
    /**
     * Internal method used by subclasses to add each of their valid stages to the internal list
     * 
     * @return the validStages
     * @throws InvalidStageException
     */
    protected final void addValidStage(final URI validStage) throws InvalidStageException
    {
        if(validStage == null)
        {
            throw new IllegalArgumentException("Valid stage was null");
        }
        
        if(!this.getValidStages().contains(validStage))
        {
            if(NormalisationRuleSchema.getAllStages().contains(validStage))
            {
                this.getValidStages().add(validStage);
            }
            else
            {
                throw new InvalidStageException(
                        "Could not assign a stage as valid as it was not recognised as a stage by this implementation",
                        this, validStage);
            }
        }
    }
    
    @Override
    public int compareTo(final NormalisationRule otherRule)
    {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        
        if(this == otherRule)
        {
            return EQUAL;
        }
        
        if(this.getOrder() < otherRule.getOrder())
        {
            return BEFORE;
        }
        
        if(this.getOrder() > otherRule.getOrder())
        {
            return AFTER;
        }
        
        return this.getKey().stringValue().compareTo(otherRule.getKey().stringValue());
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
        if(!super.equals(obj))
        {
            return false;
        }
        if(!(obj instanceof NormalisationRule))
        {
            return false;
        }
        final NormalisationRule other = (NormalisationRule)obj;
        if(this.getOrder() != other.getOrder())
        {
            return false;
        }
        if(this.getProfileIncludeExcludeOrder() == null)
        {
            if(other.getProfileIncludeExcludeOrder() != null)
            {
                return false;
            }
        }
        else if(!this.getProfileIncludeExcludeOrder().equals(other.getProfileIncludeExcludeOrder()))
        {
            return false;
        }
        if(this.getRelatedNamespaces() == null)
        {
            if(other.getRelatedNamespaces() != null)
            {
                return false;
            }
        }
        else if(!this.getRelatedNamespaces().equals(other.getRelatedNamespaces()))
        {
            return false;
        }
        if(this.getStages() == null)
        {
            if(other.getStages() != null)
            {
                return false;
            }
        }
        else if(!this.getStages().equals(other.getStages()))
        {
            return false;
        }
        if(this.getValidStages() == null)
        {
            if(other.getValidStages() != null)
            {
                return false;
            }
        }
        else if(!this.getValidStages().equals(other.getValidStages()))
        {
            return false;
        }
        return true;
    }
    
    /**
     * @return the namespace used to represent objects of this type by default
     */
    @Override
    public final QueryAllNamespaces getDefaultNamespace()
    {
        return QueryAllNamespaces.RDFRULE;
    }
    
    @Override
    public final int getOrder()
    {
        return this.order;
    }
    
    @Override
    public final ProfileIncludeExclude getProfileIncludeExcludeOrder()
    {
        return this.profileIncludeExcludeOrder;
    }
    
    /**
     * @return the relatedNamespaces
     */
    @Override
    public final Collection<URI> getRelatedNamespaces()
    {
        return this.relatedNamespaces;
    }
    
    /**
     * @return the Stages
     */
    @Override
    public final Set<URI> getStages()
    {
        return Collections.unmodifiableSet(this.stages);
    }
    
    /**
     * @return the validStages
     */
    @Override
    public final Set<URI> getValidStages()
    {
        return Collections.unmodifiableSet(this.validStages);
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
        int result = super.hashCode();
        result = prime * result + this.order;
        result =
                prime * result
                        + ((this.profileIncludeExcludeOrder == null) ? 0 : this.profileIncludeExcludeOrder.hashCode());
        result = prime * result + ((this.relatedNamespaces == null) ? 0 : this.relatedNamespaces.hashCode());
        result = prime * result + ((this.stages == null) ? 0 : this.stages.hashCode());
        result = prime * result + ((this.validStages == null) ? 0 : this.validStages.hashCode());
        return result;
    }
    
    @Override
    public final boolean isUsedWithProfileList(final List<Profile> orderedProfileList,
            final boolean allowImplicitInclusions, final boolean includeNonProfileMatched)
    {
        return ProfileMatch.isUsedWithProfileList(this, orderedProfileList, allowImplicitInclusions,
                includeNonProfileMatched);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.rdfrule.NormalisationRule#resetRelatedNamespaces()
     */
    @Override
    public boolean resetRelatedNamespaces()
    {
        try
        {
            this.relatedNamespaces.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            BaseRuleImpl.log.debug("Could not clear collection");
        }
        
        this.relatedNamespaces = new HashSet<URI>();
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.rdfrule.NormalisationRule#resetStages()
     */
    @Override
    public boolean resetStages()
    {
        try
        {
            this.stages.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            BaseRuleImpl.log.debug("Could not clear collection");
        }
        
        this.stages = new HashSet<URI>();
        
        return true;
    }
    
    @Override
    public final void setOrder(final int order)
    {
        this.order = order;
    }
    
    @Override
    public final void setProfileIncludeExcludeOrder(final ProfileIncludeExclude profileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = profileIncludeExcludeOrder;
    }
    
    /**
     * This method is called internally during object creation before any used stages can be
     * attempted to be added, as this information needs to be setup for each object
     * 
     * @return A set of URIs to initially setup the valid stages for this rule
     */
    protected abstract Set<URI> setupValidStages();
    
    @Override
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... contextKey)
        throws OpenRDFException
    {
        super.toRdf(myRepository, modelVersion, contextKey);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            if(BaseRuleImpl.DEBUG)
            {
                BaseRuleImpl.log.debug("NormalisationRuleImpl.toRdf: keyToUse=" + contextKey);
            }
            
            final URI keyUri = this.getKey();
            final Literal orderLiteral = f.createLiteral(this.getOrder());
            final URI profileIncludeExcludeOrderLiteral = this.getProfileIncludeExcludeOrder().getUri();
            
            con.setAutoCommit(false);
            
            if(modelVersion <= 2)
            {
                con.add(keyUri, RDF.TYPE, NormalisationRuleSchema.getVersion2NormalisationRuleTypeUri(), contextKey);
            }
            else
            {
                for(final URI nextElementType : this.getElementTypes())
                {
                    // hide the TransformationRule and ValidationRule types from clients below
                    // version 5 so that it will not prevent them parsing the rules,
                    // as the types here need to be a subset of the types in the clients
                    // implementation, and these types would break all version 1-4 systems otherwise
                    if(modelVersion < 5)
                    {
                        if(!nextElementType.equals(TransformingRuleSchema.getTransformingRuleTypeUri())
                                && !nextElementType.equals(ValidatingRuleSchema.getValidatingRuleTypeUri()))
                        {
                            con.add(keyUri, RDF.TYPE, nextElementType, contextKey);
                        }
                    }
                    else
                    {
                        con.add(keyUri, RDF.TYPE, nextElementType, contextKey);
                    }
                }
            }
            
            con.add(keyUri, NormalisationRuleSchema.getRdfruleOrder(), orderLiteral, contextKey);
            con.add(keyUri, ProfileSchema.getProfileIncludeExcludeOrderUri(), profileIncludeExcludeOrderLiteral,
                    contextKey);
            
            if(this.getRelatedNamespaces() != null)
            {
                for(final URI nextRelatedNamespace : this.getRelatedNamespaces())
                {
                    con.add(keyUri, NormalisationRuleSchema.getRdfruleHasRelatedNamespace(), nextRelatedNamespace,
                            contextKey);
                }
            }
            
            if(this.getStages() != null)
            {
                for(final URI nextStage : this.getStages())
                {
                    con.add(keyUri, NormalisationRuleSchema.getRdfruleStage(), nextStage, contextKey);
                }
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            BaseRuleImpl.log.error("RepositoryException: " + re.getMessage());
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
    
    @Override
    public final boolean usedInStage(final org.openrdf.model.URI stage) throws InvalidStageException
    {
        if(!NormalisationRuleSchema.getAllStages().contains(stage))
        {
            throw new InvalidStageException(
                    "Cannot check if this rule to be used in this stage as it was not recognised.", this, stage);
        }
        
        return this.validStages.contains(stage) && this.stages.contains(stage);
    }
    
    @Override
    public final boolean validInStage(final org.openrdf.model.URI stage) throws InvalidStageException
    {
        if(!NormalisationRuleSchema.getAllStages().contains(stage))
        {
            throw new InvalidStageException(
                    "Cannot check if this rule is valid in this stage as it was not recognised.", this, stage);
        }
        
        return this.validStages.contains(stage);
    }
}
