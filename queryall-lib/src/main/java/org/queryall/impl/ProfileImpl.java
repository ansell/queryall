package org.queryall.impl;

import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;

import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryConnection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collection;
import java.util.LinkedList;

import org.queryall.api.Profile;
import org.queryall.helpers.*;

import org.apache.log4j.Logger;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfileImpl implements Profile, Comparable<Profile>
{
    private static final Logger log = Logger.getLogger(Profile.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.getSettings().getNamespaceForProfile();
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key;
    private String title = "";
    private URI curationStatus = ProjectImpl.getProjectNotCuratedUri();
    
    private int order = 100;
    
    private boolean allowImplicitQueryInclusions = false;
    private boolean allowImplicitProviderInclusions = false;
    private boolean allowImplicitRdfRuleInclusions = false;
    
    private URI defaultProfileIncludeExcludeOrder = ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri();
    
    private Collection<URI> profileAdministrators = new HashSet<URI>();
    
    private Collection<URI> includeProviders = new HashSet<URI>();
    private Collection<URI> excludeProviders = new HashSet<URI>();
    private Collection<URI> includeQueries = new HashSet<URI>();
    private Collection<URI> excludeQueries = new HashSet<URI>();
    private Collection<URI> includeRdfRules = new HashSet<URI>();
    private Collection<URI> excludeRdfRules = new HashSet<URI>();
    
    
    private static URI profileTypeUri;
    private static URI profileTitle;
    private static URI profileAdministratorUri;
    private static URI profileOrderUri;
    private static URI profileAllowImplicitQueryInclusionsUri;
    private static URI profileAllowImplicitProviderInclusionsUri;
    private static URI profileAllowImplicitRdfRuleInclusionsUri;
    private static URI profileDefaultIncludeExcludeOrderUri;
    private static URI profileIncludeExcludeOrderUri;
    private static URI profileIncludeProviderInProfile;
    private static URI profileExcludeProviderFromProfile;
    private static URI profileIncludeQueryInProfile;
    private static URI profileExcludeQueryFromProfile;
    private static URI profileIncludeRdfRuleInProfile;
    private static URI profileExcludeRdfRuleFromProfile;
    
    private static URI profileExcludeThenIncludeUri;
    private static URI profileIncludeThenExcludeUri;
    private static URI profileIncludeExcludeOrderUndefinedUri;
    
    public static String profileNamespace;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        profileNamespace = Settings.getSettings().getOntologyTermUriPrefix()
                           +Settings.getSettings().getNamespaceForProfile()
                           +Settings.getSettings().getOntologyTermUriSuffix();
                           
        setProfileTypeUri(f.createURI(profileNamespace,"Profile"));
        setProfileTitle(f.createURI(profileNamespace,"Title"));
        setProfileOrderUri(f.createURI(profileNamespace,"order"));
        setProfileAdministratorUri(f.createURI(profileNamespace,"hasProfileAdministrator"));
        
        setProfileAllowImplicitQueryInclusionsUri(f.createURI(profileNamespace,"allowImplicitQueryInclusions"));
        setProfileAllowImplicitProviderInclusionsUri(f.createURI(profileNamespace,"allowImplicitProviderInclusions"));
        setProfileAllowImplicitRdfRuleInclusionsUri(f.createURI(profileNamespace,"allowImplicitRdfRuleInclusions"));
        setProfileDefaultIncludeExcludeOrderUri(f.createURI(profileNamespace,"defaultProfileIncludeExcludeOrder"));
        setProfileIncludeExcludeOrderUri(f.createURI(profileNamespace,"profileIncludeExcludeOrder"));
        setProfileIncludeProviderInProfile(f.createURI(profileNamespace,"includesProvider"));
        setProfileExcludeProviderFromProfile(f.createURI(profileNamespace,"excludesProvider"));
        setProfileIncludeQueryInProfile(f.createURI(profileNamespace,"includesQuery"));
        setProfileExcludeQueryFromProfile(f.createURI(profileNamespace,"excludesQuery"));
        setProfileIncludeRdfRuleInProfile(f.createURI(profileNamespace,"includesRdfRule"));
        setProfileExcludeRdfRuleFromProfile(f.createURI(profileNamespace,"excludesRdfRule"));
        
        setProfileExcludeThenIncludeUri(f.createURI(profileNamespace,"excludeThenInclude"));
        setProfileIncludeThenExcludeUri(f.createURI(profileNamespace,"includeThenExclude"));
        setProfileIncludeExcludeOrderUndefinedUri(f.createURI(profileNamespace,"includeExcludeOrderUndefined"));
    }
    
    public ProfileImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion) throws OpenRDFException
	{
    	boolean defaultProfileIncludeExcludeOrderValidationFailed = true;

        for(Statement nextStatement : inputStatements)
        {
            if(_DEBUG)
            {
                log.debug("Profile.fromRdf: nextStatement: "+nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE) && nextStatement.getObject().equals(getProfileTypeUri()))
            {
                if(_TRACE)
                {
                    log.trace("Profile.fromRdf: found valid type predicate for URI: "+keyToUse);
                }
                
                // resultIsValid = true;
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.getProjectCurationStatusUri()))
            {
                this.setCurationStatus((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProfileTitle()) || nextStatement.getPredicate().equals(Constants.DC_TITLE))
            {
                this.setTitle(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getProfileOrderUri()))
            {
                this.setOrder(RdfUtils.getIntegerFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getProfileAdministratorUri()))
            {
                this.addProfileAdministrator((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProfileDefaultIncludeExcludeOrderUri()))
            {
                this.setDefaultProfileIncludeExcludeOrder((URI)nextStatement.getObject());
                
                if(this.getDefaultProfileIncludeExcludeOrder().equals(getProfileIncludeExcludeOrderUri()) || this.getDefaultProfileIncludeExcludeOrder().equals(getProfileExcludeThenIncludeUri()))
                {
                	defaultProfileIncludeExcludeOrderValidationFailed = false;
                }
            }
            else if(nextStatement.getPredicate().equals(getProfileAllowImplicitQueryInclusionsUri()))
            {
                this.setAllowImplicitQueryTypeInclusions(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getProfileAllowImplicitProviderInclusionsUri()))
            {
                this.setAllowImplicitProviderInclusions(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getProfileAllowImplicitRdfRuleInclusionsUri()))
            {
                this.setAllowImplicitRdfRuleInclusions(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getProfileIncludeProviderInProfile()))
            {
                this.addIncludeProvider((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProfileExcludeProviderFromProfile()))
            {
                this.addExcludeProvider((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProfileIncludeQueryInProfile()))
            {
                this.addIncludeQueryType((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProfileExcludeQueryFromProfile()))
            {
                this.addExcludeQueryType((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProfileIncludeRdfRuleInProfile()))
            {
                this.addIncludeRdfRule((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProfileExcludeRdfRuleFromProfile()))
            {
                this.addExcludeRdfRule((URI)nextStatement.getObject());
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(defaultProfileIncludeExcludeOrderValidationFailed)
        {
        	log.warn("The default profile include exclude order for a profile was not valid. This may cause errors if any profilable objects do not explicitly define their order. profile.getKey()="+this.getKey());
        }
        
        if(_TRACE)
        {
            log.trace("Profile.fromRdf: would have returned... result="+this.toString());
        }
    }

	public ProfileImpl()
	{
	}

	public static boolean schemaToRdf(Repository myRepository, URI contextUri, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(getProfileTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            
            if(modelVersion == 1)
            {
                con.add(getProfileTitle(), RDFS.SUBPROPERTYOF, f.createURI(Constants.DC_NAMESPACE+"title"), contextUri);
            }
            
            // TODO: Add description
            con.add(getProfileOrderUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(getProfileOrderUri(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(getProfileOrderUri(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileOrderUri(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            // TODO: Add description
            con.add(getProfileAdministratorUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(getProfileAdministratorUri(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(getProfileAdministratorUri(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileAdministratorUri(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            // TODO: Add description
            con.add(getProfileDefaultIncludeExcludeOrderUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(getProfileDefaultIncludeExcludeOrderUri(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(getProfileDefaultIncludeExcludeOrderUri(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileDefaultIncludeExcludeOrderUri(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(getProfileAllowImplicitQueryInclusionsUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(getProfileAllowImplicitQueryInclusionsUri(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(getProfileAllowImplicitQueryInclusionsUri(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileAllowImplicitQueryInclusionsUri(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(getProfileAllowImplicitProviderInclusionsUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(getProfileAllowImplicitProviderInclusionsUri(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(getProfileAllowImplicitProviderInclusionsUri(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileAllowImplicitProviderInclusionsUri(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            
            con.add(getProfileAllowImplicitRdfRuleInclusionsUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(getProfileAllowImplicitRdfRuleInclusionsUri(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(getProfileAllowImplicitRdfRuleInclusionsUri(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileAllowImplicitRdfRuleInclusionsUri(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            
            con.add(getProfileIncludeProviderInProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(getProfileIncludeProviderInProfile(), RDFS.RANGE, ProviderImpl.getProviderTypeUri(), contextUri);
            con.add(getProfileIncludeProviderInProfile(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileIncludeProviderInProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(getProfileExcludeProviderFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(getProfileExcludeProviderFromProfile(), RDFS.RANGE, ProviderImpl.getProviderTypeUri(), contextUri);
            con.add(getProfileExcludeProviderFromProfile(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileExcludeProviderFromProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(getProfileIncludeQueryInProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(getProfileIncludeQueryInProfile(), RDFS.RANGE, QueryTypeImpl.getQueryTypeUri(), contextUri);
            con.add(getProfileIncludeQueryInProfile(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileIncludeQueryInProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(getProfileExcludeQueryFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(getProfileExcludeQueryFromProfile(), RDFS.RANGE, QueryTypeImpl.getQueryTypeUri(), contextUri);
            con.add(getProfileExcludeQueryFromProfile(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileExcludeQueryFromProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(getProfileIncludeRdfRuleInProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(getProfileIncludeRdfRuleInProfile(), RDFS.RANGE, NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextUri);
            con.add(getProfileIncludeRdfRuleInProfile(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileIncludeRdfRuleInProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(getProfileExcludeRdfRuleFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(getProfileExcludeRdfRuleFromProfile(), RDFS.RANGE, NormalisationRuleImpl.getNormalisationRuleTypeUri(), contextUri);
            con.add(getProfileExcludeRdfRuleFromProfile(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileExcludeRdfRuleFromProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(getProfileExcludeThenIncludeUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(getProfileExcludeThenIncludeUri(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(getProfileExcludeThenIncludeUri(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileExcludeThenIncludeUri(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(getProfileIncludeThenExcludeUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(getProfileIncludeThenExcludeUri(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(getProfileIncludeThenExcludeUri(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileIncludeThenExcludeUri(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(getProfileIncludeExcludeOrderUndefinedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(getProfileIncludeExcludeOrderUndefinedUri(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(getProfileIncludeExcludeOrderUndefinedUri(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileIncludeExcludeOrderUndefinedUri(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch (RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
                con.rollback();
                
            log.error("RepositoryException: "+re.getMessage());
        }
        finally
        {
            if(con != null)
                con.close();
        }
        
        return false;
    }
        
	@Override
	public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            URI profileInstanceUri = this.getKey();
            
            Literal titleLiteral;
            
            if(title == null)
            {
                titleLiteral = f.createLiteral("");
            }
            else
            {
                titleLiteral = f.createLiteral(title);
            }
            
            Literal orderLiteral = f.createLiteral(order);
            Literal allowImplicitQueryInclusionsLiteral = f.createLiteral(allowImplicitQueryInclusions);
            Literal allowImplicitProviderInclusionsLiteral = f.createLiteral(allowImplicitProviderInclusions);
            Literal allowImplicitRdfRuleInclusionsLiteral = f.createLiteral(allowImplicitRdfRuleInclusions);
            URI defaultProfileIncludeExcludeOrderLiteral = defaultProfileIncludeExcludeOrder;
            
            URI curationStatusLiteral = null;
            
            if(curationStatus == null)
                curationStatusLiteral = ProjectImpl.getProjectNotCuratedUri();
            else
                curationStatusLiteral = curationStatus;
                
            // log.info("About to add to the repository");
            
            con.setAutoCommit(false);
            
            con.add(profileInstanceUri, RDF.TYPE, getProfileTypeUri(), keyToUse);
            
            con.add(profileInstanceUri, ProjectImpl.getProjectCurationStatusUri(), curationStatusLiteral, keyToUse);
            
            // log.info("About to add to the repository 2");
            if(modelVersion == 1)
            {
                con.add(profileInstanceUri, getProfileTitle(), titleLiteral, keyToUse);
            }
            else
            {
                con.add(profileInstanceUri, Constants.DC_TITLE, titleLiteral, keyToUse);
            }
            
            // log.info("About to add to the repository 3");
            con.add(profileInstanceUri, getProfileOrderUri(), orderLiteral, keyToUse);
            
            // log.info("About to add to the repository 4");
            con.add(profileInstanceUri, getProfileAllowImplicitQueryInclusionsUri(), allowImplicitQueryInclusionsLiteral, keyToUse);
            
            // log.info("About to add to the repository 5");
            con.add(profileInstanceUri, getProfileAllowImplicitProviderInclusionsUri(), allowImplicitProviderInclusionsLiteral, keyToUse);
            
            // log.info("About to add to the repository 6");
            con.add(profileInstanceUri, getProfileAllowImplicitRdfRuleInclusionsUri(), allowImplicitRdfRuleInclusionsLiteral, keyToUse);
            
            // log.info("About to add to the repository 7");
            con.add(profileInstanceUri, getProfileDefaultIncludeExcludeOrderUri(), defaultProfileIncludeExcludeOrderLiteral, keyToUse);
            
            // log.info("About to add array based information");
            
            if(includeProviders != null)
            {
            
                for(URI nextIncludeProviders : includeProviders)
                {
                    con.add(profileInstanceUri, getProfileIncludeProviderInProfile(), nextIncludeProviders, keyToUse);
                }
            }
            
            if(excludeProviders != null)
            {
            
                for(URI nextExcludeProviders : excludeProviders)
                {
                    con.add(profileInstanceUri, getProfileExcludeProviderFromProfile(), nextExcludeProviders, keyToUse);
                }
            }
            
            
            if(profileAdministrators != null)
            {
            
                for(URI nextProfileAdministrator : profileAdministrators)
                {
                    con.add(profileInstanceUri, getProfileAdministratorUri(), nextProfileAdministrator, keyToUse);
                }
            }
            
            if(includeQueries != null)
            {
            
                for(URI nextIncludeQuery : includeQueries)
                {
                    if(_TRACE)
                    {
                        log.trace("Profile.toRdf: nextIncludeQuery="+nextIncludeQuery);
                    }
                    
                    con.add(profileInstanceUri, getProfileIncludeQueryInProfile(), nextIncludeQuery, keyToUse);
                }
            }
            
            if(excludeQueries != null)
            {
            
                for(URI nextExcludeQuery : excludeQueries)
                {
                    if(_TRACE)
                    {
                        log.trace("Profile.toRdf: nextExcludeQuery="+nextExcludeQuery);
                    }
                    
                    con.add(profileInstanceUri, getProfileExcludeQueryFromProfile(), nextExcludeQuery, keyToUse);
                }
            }
            
            if(includeRdfRules != null)
            {
            
                for(URI nextIncludeRdfRules : includeRdfRules)
                {
                    con.add(profileInstanceUri, getProfileIncludeRdfRuleInProfile(), nextIncludeRdfRules, keyToUse);
                }
            }
            
            if(excludeRdfRules != null)
            {
            
                for(URI nextExcludeRdfRules : excludeRdfRules)
                {
                    con.add(profileInstanceUri, getProfileExcludeRdfRuleFromProfile(), nextExcludeRdfRules, keyToUse);
                }
            }
            
            if(unrecognisedStatements != null)
            {
            
                for(Statement nextUnrecognisedStatement : unrecognisedStatements)
                {
                    con.add(nextUnrecognisedStatement, keyToUse);
                }
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch (RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            log.error("RepositoryException: "+re.getMessage());
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        
        result.append("key=").append(getKey().stringValue());
        result.append("order=").append(getOrder());
        
        return result.toString();
    }
    
    @Override
    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        String prefix = "profile_";
        
        return sb.toString();
    }
    
    @Override
    public String toHtml()
    {
        StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        String prefix = "profile_";
        
        return sb.toString();
    }
    
    /**
     * @return the key
     */
    @Override
    public URI getKey()
    {
        return key;
    }

    /**
     * @param key the key to set
     */
    @Override
    public void setKey(String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }

    @Override
    public void setKey(URI nextKey)
    {
        this.key = nextKey;
    }    

    /**
     * @return the namespace used to represent objects of this type by default
     */
    @Override
    public String getDefaultNamespace()
    {
        return defaultNamespace;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class, including abstract implementations
     */
    @Override
    public Collection<URI> getElementTypes()
    {
        Collection<URI> results = new ArrayList<URI>(1);
    	
    	results.add(getProfileTypeUri());
    	
    	return results;
    }
    
    @Override
	public int compareTo(Profile otherProfile)
    {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
    
        if ( this == otherProfile ) 
            return EQUAL;
    
        if (this.getOrder() < otherProfile.getOrder()) 
            return BEFORE;
        
        if (this.getOrder() > otherProfile.getOrder()) 
            return AFTER;

        if(this.getKey() == null)
        {
            if(otherProfile.getKey() == null)
            {
                return EQUAL;
            }
            else
            {
                return BEFORE;
            }
        }
        
        return this.getKey().stringValue().compareTo(otherProfile.getKey().stringValue());
    }

    public static URI getExcludeThenIncludeUri()
    {
        return getProfileExcludeThenIncludeUri();
    }

    public static URI getIncludeThenExcludeUri()
    {
        return getProfileIncludeThenExcludeUri();
    }
    
    public static URI getProfileIncludeExcludeOrderUri()
    {
        return profileIncludeExcludeOrderUri;
    }
    
    public static URI getProfileIncludeExcludeOrderUndefinedUri()
    {
        return profileIncludeExcludeOrderUndefinedUri;
    }
    
    @Override
    public int getOrder()
    {
        return order;
    }
    
    @Override
    public void setOrder(int order)
    {
        this.order = order;
    }

    @Override
    public String getTitle()
    {
        return title;
    }
    
    @Override
    public void setTitle(String title)
    {
        this.title = title;
    }

    @Override
    public URI getDefaultProfileIncludeExcludeOrder()
    {
        return defaultProfileIncludeExcludeOrder;
    }
    
    @Override
    public void setDefaultProfileIncludeExcludeOrder(URI defaultProfileIncludeExcludeOrder)
    {
        this.defaultProfileIncludeExcludeOrder = defaultProfileIncludeExcludeOrder;
    }

    @Override
    public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    @Override
    public URI getCurationStatus()
    {
        return curationStatus;
    }

    @Override
    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        unrecognisedStatements.add(unrecognisedStatement);
    }

    @Override
    public Collection<Statement> getUnrecognisedStatements()
    {
        return unrecognisedStatements;
    }

    @Override
    public boolean getAllowImplicitQueryTypeInclusions()
    {
        return allowImplicitQueryInclusions;
    }
    
    @Override
    public void setAllowImplicitQueryTypeInclusions(boolean allowImplicitQueryInclusions)
    {
        this.allowImplicitQueryInclusions = allowImplicitQueryInclusions;
    }
    
    @Override
    public boolean getAllowImplicitProviderInclusions()
    {
        return allowImplicitProviderInclusions;
    }
    
    @Override
    public void setAllowImplicitProviderInclusions(boolean allowImplicitProviderInclusions)
    {
        this.allowImplicitProviderInclusions = allowImplicitProviderInclusions;
    }

    @Override
    public boolean getAllowImplicitRdfRuleInclusions()
    {
        return allowImplicitRdfRuleInclusions;
    }
    
    @Override
    public void setAllowImplicitRdfRuleInclusions(boolean allowImplicitRdfRuleInclusions)
    {
        this.allowImplicitRdfRuleInclusions = allowImplicitRdfRuleInclusions;
    }
    
    @Override
    public void addProfileAdministrator(URI profileAdministrator)
    {
        if(this.profileAdministrators == null)
        {
            this.profileAdministrators = new LinkedList<URI>();
        }
        
        this.profileAdministrators.add(profileAdministrator);
    }
    
    @Override
    public Collection<URI> getProfileAdministrators()
    {
        return profileAdministrators;
    }
    
    @Override
    public Collection<URI> getIncludeProviders()
    {
        return includeProviders;
    }
    
    @Override
    public Collection<URI> getExcludeProviders()
    {
        return excludeProviders;
    }
    
    @Override
    public Collection<URI> getIncludeQueryTypes()
    {
        return includeQueries;
    }
    
    @Override
    public Collection<URI> getExcludeQueryTypes()
    {
        return excludeQueries;
    }    
    
    @Override
    public Collection<URI> getIncludeRdfRules()
    {
        return includeRdfRules;
    }
    
    @Override
    public Collection<URI> getExcludeRdfRules()
    {
        return excludeRdfRules;
    }

	/**
	 * @param profileTypeUri the profileTypeUri to set
	 */
	public static void setProfileTypeUri(URI profileTypeUri) {
		ProfileImpl.profileTypeUri = profileTypeUri;
	}

	/**
	 * @return the profileTypeUri
	 */
	public static URI getProfileTypeUri() {
		return profileTypeUri;
	}

	/**
	 * @param profileTitle the profileTitle to set
	 */
	public static void setProfileTitle(URI profileTitle) {
		ProfileImpl.profileTitle = profileTitle;
	}

	/**
	 * @return the profileTitle
	 */
	public static URI getProfileTitle() {
		return profileTitle;
	}

	/**
	 * @param profileAdministratorUri the profileAdministratorUri to set
	 */
	public static void setProfileAdministratorUri(
			URI profileAdministratorUri) {
		ProfileImpl.profileAdministratorUri = profileAdministratorUri;
	}

	/**
	 * @return the profileAdministratorUri
	 */
	public static URI getProfileAdministratorUri() {
		return profileAdministratorUri;
	}

	/**
	 * @param profileOrderUri the profileOrderUri to set
	 */
	public static void setProfileOrderUri(URI profileOrderUri) {
		ProfileImpl.profileOrderUri = profileOrderUri;
	}

	/**
	 * @return the profileOrderUri
	 */
	public static URI getProfileOrderUri() {
		return profileOrderUri;
	}

	/**
	 * @param profileAllowImplicitQueryInclusionsUri the profileAllowImplicitQueryInclusionsUri to set
	 */
	public static void setProfileAllowImplicitQueryInclusionsUri(
			URI profileAllowImplicitQueryInclusionsUri) {
		ProfileImpl.profileAllowImplicitQueryInclusionsUri = profileAllowImplicitQueryInclusionsUri;
	}

	/**
	 * @return the profileAllowImplicitQueryInclusionsUri
	 */
	public static URI getProfileAllowImplicitQueryInclusionsUri() {
		return profileAllowImplicitQueryInclusionsUri;
	}

	/**
	 * @param profileAllowImplicitProviderInclusionsUri the profileAllowImplicitProviderInclusionsUri to set
	 */
	public static void setProfileAllowImplicitProviderInclusionsUri(
			URI profileAllowImplicitProviderInclusionsUri) {
		ProfileImpl.profileAllowImplicitProviderInclusionsUri = profileAllowImplicitProviderInclusionsUri;
	}

	/**
	 * @return the profileAllowImplicitProviderInclusionsUri
	 */
	public static URI getProfileAllowImplicitProviderInclusionsUri() {
		return profileAllowImplicitProviderInclusionsUri;
	}

	/**
	 * @param profileAllowImplicitRdfRuleInclusionsUri the profileAllowImplicitRdfRuleInclusionsUri to set
	 */
	public static void setProfileAllowImplicitRdfRuleInclusionsUri(
			URI profileAllowImplicitRdfRuleInclusionsUri) {
		ProfileImpl.profileAllowImplicitRdfRuleInclusionsUri = profileAllowImplicitRdfRuleInclusionsUri;
	}

	/**
	 * @return the profileAllowImplicitRdfRuleInclusionsUri
	 */
	public static URI getProfileAllowImplicitRdfRuleInclusionsUri() {
		return profileAllowImplicitRdfRuleInclusionsUri;
	}

	/**
	 * @param profileDefaultIncludeExcludeOrderUri the profileDefaultIncludeExcludeOrderUri to set
	 */
	public static void setProfileDefaultIncludeExcludeOrderUri(
			URI profileDefaultIncludeExcludeOrderUri) {
		ProfileImpl.profileDefaultIncludeExcludeOrderUri = profileDefaultIncludeExcludeOrderUri;
	}

	/**
	 * @return the profileDefaultIncludeExcludeOrderUri
	 */
	public static URI getProfileDefaultIncludeExcludeOrderUri() {
		return profileDefaultIncludeExcludeOrderUri;
	}

	/**
	 * @param profileIncludeExcludeOrderUri the profileIncludeExcludeOrderUri to set
	 */
	public static void setProfileIncludeExcludeOrderUri(
			URI profileIncludeExcludeOrderUri) {
		ProfileImpl.profileIncludeExcludeOrderUri = profileIncludeExcludeOrderUri;
	}

	/**
	 * @param profileIncludeProviderInProfile the profileIncludeProviderInProfile to set
	 */
	public static void setProfileIncludeProviderInProfile(
			URI profileIncludeProviderInProfile) {
		ProfileImpl.profileIncludeProviderInProfile = profileIncludeProviderInProfile;
	}

	/**
	 * @return the profileIncludeProviderInProfile
	 */
	public static URI getProfileIncludeProviderInProfile() {
		return profileIncludeProviderInProfile;
	}

	/**
	 * @param profileExcludeProviderFromProfile the profileExcludeProviderFromProfile to set
	 */
	public static void setProfileExcludeProviderFromProfile(
			URI profileExcludeProviderFromProfile) {
		ProfileImpl.profileExcludeProviderFromProfile = profileExcludeProviderFromProfile;
	}

	/**
	 * @return the profileExcludeProviderFromProfile
	 */
	public static URI getProfileExcludeProviderFromProfile() {
		return profileExcludeProviderFromProfile;
	}

	/**
	 * @param profileIncludeQueryInProfile the profileIncludeQueryInProfile to set
	 */
	public static void setProfileIncludeQueryInProfile(
			URI profileIncludeQueryInProfile) {
		ProfileImpl.profileIncludeQueryInProfile = profileIncludeQueryInProfile;
	}

	/**
	 * @return the profileIncludeQueryInProfile
	 */
	public static URI getProfileIncludeQueryInProfile() {
		return profileIncludeQueryInProfile;
	}

	/**
	 * @param profileExcludeQueryFromProfile the profileExcludeQueryFromProfile to set
	 */
	public static void setProfileExcludeQueryFromProfile(
			URI profileExcludeQueryFromProfile) {
		ProfileImpl.profileExcludeQueryFromProfile = profileExcludeQueryFromProfile;
	}

	/**
	 * @return the profileExcludeQueryFromProfile
	 */
	public static URI getProfileExcludeQueryFromProfile() {
		return profileExcludeQueryFromProfile;
	}

	/**
	 * @param profileIncludeRdfRuleInProfile the profileIncludeRdfRuleInProfile to set
	 */
	public static void setProfileIncludeRdfRuleInProfile(
			URI profileIncludeRdfRuleInProfile) {
		ProfileImpl.profileIncludeRdfRuleInProfile = profileIncludeRdfRuleInProfile;
	}

	/**
	 * @return the profileIncludeRdfRuleInProfile
	 */
	public static URI getProfileIncludeRdfRuleInProfile() {
		return profileIncludeRdfRuleInProfile;
	}

	/**
	 * @param profileExcludeRdfRuleFromProfile the profileExcludeRdfRuleFromProfile to set
	 */
	public static void setProfileExcludeRdfRuleFromProfile(
			URI profileExcludeRdfRuleFromProfile) {
		ProfileImpl.profileExcludeRdfRuleFromProfile = profileExcludeRdfRuleFromProfile;
	}

	/**
	 * @return the profileExcludeRdfRuleFromProfile
	 */
	public static URI getProfileExcludeRdfRuleFromProfile() {
		return profileExcludeRdfRuleFromProfile;
	}

	/**
	 * @param profileExcludeThenIncludeUri the profileExcludeThenIncludeUri to set
	 */
	public static void setProfileExcludeThenIncludeUri(
			URI profileExcludeThenIncludeUri) {
		ProfileImpl.profileExcludeThenIncludeUri = profileExcludeThenIncludeUri;
	}

	/**
	 * @return the profileExcludeThenIncludeUri
	 */
	public static URI getProfileExcludeThenIncludeUri() {
		return profileExcludeThenIncludeUri;
	}

	/**
	 * @param profileIncludeThenExcludeUri the profileIncludeThenExcludeUri to set
	 */
	public static void setProfileIncludeThenExcludeUri(
			URI profileIncludeThenExcludeUri) {
		ProfileImpl.profileIncludeThenExcludeUri = profileIncludeThenExcludeUri;
	}

	/**
	 * @return the profileIncludeThenExcludeUri
	 */
	public static URI getProfileIncludeThenExcludeUri() {
		return profileIncludeThenExcludeUri;
	}

	/**
	 * @param profileIncludeExcludeOrderUndefinedUri the profileIncludeExcludeOrderUndefinedUri to set
	 */
	public static void setProfileIncludeExcludeOrderUndefinedUri(
			URI profileIncludeExcludeOrderUndefinedUri) {
		ProfileImpl.profileIncludeExcludeOrderUndefinedUri = profileIncludeExcludeOrderUndefinedUri;
	}

    @Override
    public void addIncludeProvider(URI includeProvider)
    {
        if(this.includeProviders == null)
        {
            this.includeProviders = new LinkedList<URI>();
        }
        
        this.includeProviders.add(includeProvider);
    }

    @Override
    public void addExcludeProvider(URI excludeProvider)
    {
        if(this.excludeProviders == null)
        {
            this.excludeProviders = new LinkedList<URI>();
        }
        
        this.excludeProviders.add(excludeProvider);
    }

    @Override
    public void addIncludeQueryType(URI includeQuery)
    {
        if(this.includeQueries == null)
        {
            this.includeQueries = new LinkedList<URI>();
        }
        
        this.includeQueries.add(includeQuery);
    }

    @Override
    public void addExcludeQueryType(URI excludeQuery)
    {
        if(this.excludeQueries == null)
        {
            this.excludeQueries = new LinkedList<URI>();
        }
        
        this.excludeQueries.add(excludeQuery);
    }

    @Override
    public void addIncludeRdfRule(URI includeRdfRule)
    {
        if(this.includeRdfRules == null)
        {
            this.includeRdfRules = new LinkedList<URI>();
        }
        
        this.includeRdfRules.add(includeRdfRule);
    }

    @Override
    public void addExcludeRdfRule(URI excludeRdfRule)
    {
        if(this.excludeRdfRules == null)
        {
            this.excludeRdfRules = new LinkedList<URI>();
        }
        
        this.excludeRdfRules.add(excludeRdfRule);
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (allowImplicitProviderInclusions ? 1231 : 1237);
		result = prime * result + (allowImplicitQueryInclusions ? 1231 : 1237);
		result = prime * result
				+ (allowImplicitRdfRuleInclusions ? 1231 : 1237);
		result = prime * result
				+ ((curationStatus == null) ? 0 : curationStatus.hashCode());
		result = prime
				* result
				+ ((defaultProfileIncludeExcludeOrder == null) ? 0
						: defaultProfileIncludeExcludeOrder.hashCode());
		result = prime
				* result
				+ ((excludeProviders == null) ? 0 : excludeProviders.hashCode());
		result = prime * result
				+ ((excludeQueries == null) ? 0 : excludeQueries.hashCode());
		result = prime * result
				+ ((excludeRdfRules == null) ? 0 : excludeRdfRules.hashCode());
		result = prime
				* result
				+ ((includeProviders == null) ? 0 : includeProviders.hashCode());
		result = prime * result
				+ ((includeQueries == null) ? 0 : includeQueries.hashCode());
		result = prime * result
				+ ((includeRdfRules == null) ? 0 : includeRdfRules.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + order;
		result = prime
				* result
				+ ((profileAdministrators == null) ? 0 : profileAdministrators
						.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof ProfileImpl))
			return false;
		ProfileImpl other = (ProfileImpl) obj;
		if(allowImplicitProviderInclusions != other.allowImplicitProviderInclusions)
			return false;
		if(allowImplicitQueryInclusions != other.allowImplicitQueryInclusions)
			return false;
		if(allowImplicitRdfRuleInclusions != other.allowImplicitRdfRuleInclusions)
			return false;
		if(curationStatus == null)
		{
			if(other.curationStatus != null)
				return false;
		}
		else if(!curationStatus.equals(other.curationStatus))
			return false;
		if(defaultProfileIncludeExcludeOrder == null)
		{
			if(other.defaultProfileIncludeExcludeOrder != null)
				return false;
		}
		else if(!defaultProfileIncludeExcludeOrder
				.equals(other.defaultProfileIncludeExcludeOrder))
			return false;
		if(excludeProviders == null)
		{
			if(other.excludeProviders != null)
				return false;
		}
		else if(!excludeProviders.equals(other.excludeProviders))
			return false;
		if(excludeQueries == null)
		{
			if(other.excludeQueries != null)
				return false;
		}
		else if(!excludeQueries.equals(other.excludeQueries))
			return false;
		if(excludeRdfRules == null)
		{
			if(other.excludeRdfRules != null)
				return false;
		}
		else if(!excludeRdfRules.equals(other.excludeRdfRules))
			return false;
		if(includeProviders == null)
		{
			if(other.includeProviders != null)
				return false;
		}
		else if(!includeProviders.equals(other.includeProviders))
			return false;
		if(includeQueries == null)
		{
			if(other.includeQueries != null)
				return false;
		}
		else if(!includeQueries.equals(other.includeQueries))
			return false;
		if(includeRdfRules == null)
		{
			if(other.includeRdfRules != null)
				return false;
		}
		else if(!includeRdfRules.equals(other.includeRdfRules))
			return false;
		if(key == null)
		{
			if(other.key != null)
				return false;
		}
		else if(!key.equals(other.key))
			return false;
		if(order != other.order)
			return false;
		if(profileAdministrators == null)
		{
			if(other.profileAdministrators != null)
				return false;
		}
		else if(!profileAdministrators.equals(other.profileAdministrators))
			return false;
		if(title == null)
		{
			if(other.title != null)
				return false;
		}
		else if(!title.equals(other.title))
			return false;
		return true;
	}    
}
