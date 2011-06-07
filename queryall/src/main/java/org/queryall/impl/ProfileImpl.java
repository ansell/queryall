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

import java.util.HashSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.queryall.api.NormalisationRule;
import org.queryall.api.ProfilableInterface;
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.api.QueryType;
import org.queryall.helpers.*;

import org.apache.log4j.Logger;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfileImpl extends Profile
{
    private static final Logger log = Logger.getLogger(Profile.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.getSettings().getNamespaceForProfile();
    
    public static final int SPECIFIC_INCLUDE = 1;
    public static final int SPECIFIC_EXCLUDE = 2;
    public static final int IMPLICIT_INCLUDE = 3;
    public static final int NO_MATCH = 4;
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key;
    private String title = "";
    private URI curationStatus = ProjectImpl.getProjectNotCuratedUri();
    
    private int order = 100;
    
    private boolean allowImplicitQueryInclusions = false;
    private boolean allowImplicitProviderInclusions = false;
    private boolean allowImplicitRdfRuleInclusions = false;
    
    private URI defaultProfileIncludeExcludeOrder = ProfileImpl.getProfileIncludeThenExcludeUri();
    
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
    	// TODO: Remove these temporary collections and replace with .add methods instead of .set
        Collection<URI> tempProfileAdministrators = new HashSet<URI>();
        
        Collection<URI> tempIncludeProviders = new HashSet<URI>();
        Collection<URI> tempExcludeProviders = new HashSet<URI>();
        Collection<URI> tempIncludeQueries = new HashSet<URI>();
        Collection<URI> tempExcludeQueries = new HashSet<URI>();
        Collection<URI> tempIncludeRdfRules = new HashSet<URI>();
        Collection<URI> tempExcludeRdfRules = new HashSet<URI>();
        
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
                tempProfileAdministrators.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProfileDefaultIncludeExcludeOrderUri()))
            {
                this.setDefaultProfileIncludeExcludeOrder((URI)nextStatement.getObject());
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
                tempIncludeProviders.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProfileExcludeProviderFromProfile()))
            {
                tempExcludeProviders.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProfileIncludeQueryInProfile()))
            {
                tempIncludeQueries.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProfileExcludeQueryFromProfile()))
            {
                tempExcludeQueries.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProfileIncludeRdfRuleInProfile()))
            {
                tempIncludeRdfRules.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProfileExcludeRdfRuleFromProfile()))
            {
                tempExcludeRdfRules.add((URI)nextStatement.getObject());
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        this.setProfileAdministrators(tempProfileAdministrators);
        
        this.setIncludeProviders(tempIncludeProviders);
        this.setExcludeProviders(tempExcludeProviders);
        this.setIncludeQueryTypes(tempIncludeQueries);
        this.setExcludeQueryTypes(tempExcludeQueries);
        this.setIncludeRdfRules(tempIncludeRdfRules);
        this.setExcludeRdfRules(tempExcludeRdfRules);
        
        if(_TRACE)
        {
            log.trace("Profile.fromRdf: would have returned... result="+this.toString());
        }
    }

	public ProfileImpl()
	{
		// TODO Auto-generated constructor stub
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
            con.add(getProfileIncludeProviderInProfile(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(getProfileIncludeProviderInProfile(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileIncludeProviderInProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(getProfileExcludeProviderFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(getProfileExcludeProviderFromProfile(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(getProfileExcludeProviderFromProfile(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileExcludeProviderFromProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(getProfileIncludeQueryInProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(getProfileIncludeQueryInProfile(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(getProfileIncludeQueryInProfile(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileIncludeQueryInProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(getProfileExcludeQueryFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(getProfileExcludeQueryFromProfile(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(getProfileExcludeQueryFromProfile(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileExcludeQueryFromProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(getProfileIncludeRdfRuleInProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(getProfileIncludeRdfRuleInProfile(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
            con.add(getProfileIncludeRdfRuleInProfile(), RDFS.DOMAIN, getProfileTypeUri(), contextUri);
            con.add(getProfileIncludeRdfRuleInProfile(), RDFS.LABEL, f.createLiteral("."), contextUri);
            
            con.add(getProfileExcludeRdfRuleFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            con.add(getProfileExcludeRdfRuleFromProfile(), RDFS.RANGE, RDFS.RESOURCE, contextUri);
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
        
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            URI profileInstanceUri = keyToUse;
            
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
            
            con.add(profileInstanceUri, RDF.TYPE, getProfileTypeUri(), profileInstanceUri);
            
            con.add(profileInstanceUri, ProjectImpl.getProjectCurationStatusUri(), curationStatusLiteral, profileInstanceUri);
            
            // log.info("About to add to the repository 2");
            if(modelVersion == 1)
            {
                con.add(profileInstanceUri, getProfileTitle(), titleLiteral, profileInstanceUri);
            }
            else
            {
                con.add(profileInstanceUri, Constants.DC_TITLE, titleLiteral, profileInstanceUri);
            }
            
            // log.info("About to add to the repository 3");
            con.add(profileInstanceUri, getProfileOrderUri(), orderLiteral, profileInstanceUri);
            
            // log.info("About to add to the repository 4");
            con.add(profileInstanceUri, getProfileAllowImplicitQueryInclusionsUri(), allowImplicitQueryInclusionsLiteral, profileInstanceUri);
            
            // log.info("About to add to the repository 5");
            con.add(profileInstanceUri, getProfileAllowImplicitProviderInclusionsUri(), allowImplicitProviderInclusionsLiteral, profileInstanceUri);
            
            // log.info("About to add to the repository 6");
            con.add(profileInstanceUri, getProfileAllowImplicitRdfRuleInclusionsUri(), allowImplicitRdfRuleInclusionsLiteral, profileInstanceUri);
            
            // log.info("About to add to the repository 7");
            con.add(profileInstanceUri, getProfileDefaultIncludeExcludeOrderUri(), defaultProfileIncludeExcludeOrderLiteral, profileInstanceUri);
            
            // log.info("About to add array based information");
            
            if(includeProviders != null)
            {
            
                for(URI nextIncludeProviders : includeProviders)
                {
                    con.add(profileInstanceUri, getProfileIncludeProviderInProfile(), nextIncludeProviders, profileInstanceUri);
                }
            }
            
            if(excludeProviders != null)
            {
            
                for(URI nextExcludeProviders : excludeProviders)
                {
                    con.add(profileInstanceUri, getProfileExcludeProviderFromProfile(), nextExcludeProviders, profileInstanceUri);
                }
            }
            
            
            if(profileAdministrators != null)
            {
            
                for(URI nextProfileAdministrator : profileAdministrators)
                {
                    con.add(profileInstanceUri, getProfileAdministratorUri(), nextProfileAdministrator, profileInstanceUri);
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
                    
                    con.add(profileInstanceUri, getProfileIncludeQueryInProfile(), nextIncludeQuery, profileInstanceUri);
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
                    
                    con.add(profileInstanceUri, getProfileExcludeQueryFromProfile(), nextExcludeQuery, profileInstanceUri);
                }
            }
            
            if(includeRdfRules != null)
            {
            
                for(URI nextIncludeRdfRules : includeRdfRules)
                {
                    con.add(profileInstanceUri, getProfileIncludeRdfRuleInProfile(), nextIncludeRdfRules, profileInstanceUri);
                }
            }
            
            if(excludeRdfRules != null)
            {
            
                for(URI nextExcludeRdfRules : excludeRdfRules)
                {
                    con.add(profileInstanceUri, getProfileExcludeRdfRuleFromProfile(), nextExcludeRdfRules, profileInstanceUri);
                }
            }
            
            if(unrecognisedStatements != null)
            {
            
                for(Statement nextUnrecognisedStatement : unrecognisedStatements)
                {
                    con.add(nextUnrecognisedStatement);
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
    
    public boolean equals(Profile otherProfile)
    {
        // For simplicity, we do this based on the key which we presume people set differently for different profiles!
        // TODO: enable an exact distinction by normalising the order of each property set and checking if they exactly match
        return this.getKey().equals(otherProfile.getKey());
    }

    public int usedWithProfilable(ProfilableInterface profilableObject)
    {
        Collection<URI> includeList = null;
        Collection<URI> excludeList = null;
        boolean allowImplicitInclusions = false;
        
        if(profilableObject instanceof Provider)
        {
            includeList = this.getIncludeProviders();
            excludeList = this.getExcludeProviders();
            allowImplicitInclusions = this.getAllowImplicitProviderInclusions();
        }
        else if(profilableObject instanceof QueryType)
        {
            includeList = this.getIncludeQueryTypes();
            excludeList = this.getExcludeQueryTypes();
            allowImplicitInclusions = this.getAllowImplicitQueryTypeInclusions();
        }
        else if(profilableObject instanceof NormalisationRule)
        {
            includeList = this.getIncludeRdfRules();
            excludeList = this.getExcludeRdfRules();
            allowImplicitInclusions = this.getAllowImplicitRdfRuleInclusions();
        }
        else
        {
            throw new RuntimeException("ProfileImpl.usedWithProfilable: Did not recognise the type for object profilableObject="+profilableObject.toString());
        }
        
        
        int trueResult = usedWithIncludeExcludeList(profilableObject.getKey(), profilableObject.getProfileIncludeExcludeOrder(), includeList, excludeList, this.getDefaultProfileIncludeExcludeOrder());
        
        
        if(trueResult == ProfileImpl.IMPLICIT_INCLUDE)
        {
            if(_DEBUG)
            {
                log.debug("ProfileImpl.usedWithProfilable: found implicit match profilableObject.getKey()="+profilableObject.getKey()+" nextIncludeExcludeOrder="+profilableObject.getProfileIncludeExcludeOrder()+" allowImplicitInclusions="+allowImplicitInclusions);
            }
            
            if(allowImplicitInclusions)
            {
                return ProfileImpl.IMPLICIT_INCLUDE;
            }
            else
            {
                return ProfileImpl.NO_MATCH;
            }
        }

        return trueResult;
    }
    
    /**
     * This method implements the main logic with reference to include/exclude decisions
     * based on a given includeExcludeOrder and the default profile include exclude 
     * order which overrides the given includeExcludeOrder if it is the undefined URI
     * 
     * The algorithm starts by checking both the include and exclude lists for the URI and records the existence of the URI in either list
     * 
     * If the nextIncludeExcludeOrder is null or the undefined URI, it is replaced with nextDefaultProfileIncludeExclude, which is not allowed to be undefined if it is required.
     * 
     * Then the main part of the algorithm is checked based on whether nextIncludeExcludeOrder is excludeThenInclude or includeThenExclude
     * 
     * If nextIncludeOrder is excludeThenInclude and an exclude was found then SPECIFIC_EXCLUDE is returned.
     * Otherwise if nextIncludeOrder is excludeThenInclude and an include was found, then SPECIFIC_INCLUDE is returned.
     * Otherwise if nextIncludeOrder is excludeThenInclude, IMPLICIT_INCLUDE is returned.
     * 
     * If next IncludeOrder is includeThenExclude and an include was found then SPECIFIC_INCLUDE is returned.
     * Otherwise if nextIncludeOrder is includeThenExclude and an exclude was found then SPECIFIC_EXCLUDE is returned.
     * Otherwise if nextIncludeORder is includeThenExclude, NO_MATCH is returned
     * 
     * @param nextUri
     * @param nextIncludeExcludeOrder
     * @param includeList
     * @param excludeList
     * @param nextDefaultProfileIncludeExcludeOrder
     * @return One of the following constants, ProfileImpl.SPECIFIC_EXCLUDE, ProfileImpl.SPECIFIC_INCLUDE, ProfileImpl.IMPLICIT_INCLUDE or ProfileImpl.NO_MATCH
     */
    public static final int usedWithIncludeExcludeList(URI nextUri, URI nextIncludeExcludeOrder, Collection<URI> includeList, Collection<URI> excludeList, URI nextDefaultProfileIncludeExcludeOrder)
    {
        if(includeList == null || excludeList == null)
        {
            throw new IllegalArgumentException("Profile.usedWithList: includeList or excludeList was null");
        }
        
        boolean includeFound = includeList.contains(nextUri);
        boolean excludeFound = excludeList.contains(nextUri);
        
        if(nextIncludeExcludeOrder == null || nextIncludeExcludeOrder.equals(ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri()))
        {
            nextIncludeExcludeOrder = nextDefaultProfileIncludeExcludeOrder;
        }
        
        if(nextIncludeExcludeOrder.equals(ProfileImpl.getExcludeThenIncludeUri()))
        {
            if(_DEBUG)
            {
                log.debug("Profile.usedWithList: using exclude then include rules");
            }
            
            if(excludeFound)
            {
                if(_DEBUG)
                {
                    log.debug("Profile.usedWithList: excludeFound=true, returning false");
                }
                
                return ProfileImpl.SPECIFIC_EXCLUDE;
            }
            else if(includeFound)
            {
                if(_DEBUG)
                {
                    log.debug("Profile.usedWithList: includeFound=true, returning true");
                }
                
                return ProfileImpl.SPECIFIC_INCLUDE;
            }
            else
            {
                if(_DEBUG)
                {
                    log.debug("Profile.usedWithList: includeFound=false and excludeFound=false, returning true");
                }
                
                return ProfileImpl.IMPLICIT_INCLUDE;
            }
        }
        else if(nextIncludeExcludeOrder.equals(ProfileImpl.getIncludeThenExcludeUri()))
        {
            if(_DEBUG)
            {
                log.debug("Profile.usedWithList: using include then exclude rules");
            }
            
            if(includeFound)
            {
                if(_DEBUG)
                {
                    log.debug("Profile.usedWithList: includeFound=true, returning true");
                }
                
                return ProfileImpl.SPECIFIC_INCLUDE;
            }
            else if(excludeFound)
            {
                if(_DEBUG)
                {
                    log.debug("Profile.usedWithList: excludeFound=true, returning false");
                }
                
                return ProfileImpl.SPECIFIC_EXCLUDE;
            }
            else
            {
                if(_DEBUG)
                {
                    log.debug("Profile.usedWithList: includeFound=false and excludeFound=false, returning false");
                }
                
                return ProfileImpl.NO_MATCH;
            }
        }
        else
        {
            throw new RuntimeException("Profile.usedWithList: nextIncludeExcludeOrder not recognised ("+nextIncludeExcludeOrder+")");
        }
    }
    
    public static boolean isUsedWithProfileList(ProfilableInterface profilableObject, List<Profile> nextSortedProfileList, boolean recogniseImplicitInclusions, boolean includeNonProfileMatched)
    {
        for(final Profile nextProfile : nextSortedProfileList)
        {
            final int trueResult = nextProfile.usedWithProfilable(profilableObject);
            if(trueResult == ProfileImpl.IMPLICIT_INCLUDE)
            {
                if(_DEBUG)
                {
                    log.debug("isUsedWithProfileList: found implicit include for profilableObject="
                                    + profilableObject.getKey().stringValue()
                                    + " profile="
                                    + nextProfile.getKey().stringValue());
                }
                
                if(recogniseImplicitInclusions)
                {
                    if(_DEBUG)
                    {
                        log.debug("isUsedWithProfileList: returning implicit include true for profilableObject="
                                        + profilableObject.getKey().stringValue()
                                        + " profile="
                                        + nextProfile.getKey().stringValue());
                    }
                    return true;
                }
                else if(_DEBUG)
                {
                    log.debug("isUsedWithProfileList: implicit include not recognised for profilableObject="
                                    + profilableObject.getKey().stringValue()
                                    + " profile="
                                    + nextProfile.getKey().stringValue());
                }
            }
            else if(trueResult == ProfileImpl.SPECIFIC_INCLUDE)
            {
                if(_DEBUG)
                {
                    log.debug("isUsedWithProfileList: returning specific true for profilableObject="
                                    + profilableObject.getKey().stringValue()
                                    + " profile="
                                    + nextProfile.getKey().stringValue());
                }
                return true;
            }
            else if(trueResult == ProfileImpl.SPECIFIC_EXCLUDE)
            {
                if(_DEBUG)
                {
                    log.debug("isUsedWithProfileList: returning specific false for profilableObject="
                                    + profilableObject.getKey().stringValue()
                                    + " profile="
                                    + nextProfile.getKey().stringValue());
                }
                return false;
            }
            
            
        }
        
        boolean returnValue = (profilableObject.getProfileIncludeExcludeOrder().equals(ProfileImpl.getExcludeThenIncludeUri()) 
                            || profilableObject.getProfileIncludeExcludeOrder().equals(ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri())) 
                            && includeNonProfileMatched;
        
        if(_DEBUG)
        {
            log.debug("ProfileImpl.isUsedWithProfileList: returning no matches found returnValue="
                            + returnValue
                            + " for profilableObject=" + profilableObject.getKey().stringValue());
        }
        
        return returnValue;
    }

    
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        
        result.append("key=").append(getKey().stringValue());
        result.append("order=").append(getOrder());
        
        return result.toString();
    }
    
    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        String prefix = "profile_";
        
        return sb.toString();
    }
    
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
    public URI getKey()
    {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }

    public void setKey(URI nextKey)
    {
        this.key = nextKey;
    }    

    /**
     * @return the namespace used to represent objects of this type by default
     */
    public String getDefaultNamespace()
    {
        return defaultNamespace;
    }
    
    /**
     * @return the URI used for the rdf Type of these elements
     */
    public URI getElementType()
    {
        return getProfileTypeUri();
    }
    
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
    
    public int getOrder()
    {
        return order;
    }
    
    public void setOrder(int order)
    {
        this.order = order;
    }

    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }

    public URI getDefaultProfileIncludeExcludeOrder()
    {
        return defaultProfileIncludeExcludeOrder;
    }
    
    public void setDefaultProfileIncludeExcludeOrder(URI defaultProfileIncludeExcludeOrder)
    {
        this.defaultProfileIncludeExcludeOrder = defaultProfileIncludeExcludeOrder;
    }

    public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    public URI getCurationStatus()
    {
        return curationStatus;
    }

    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        unrecognisedStatements.add(unrecognisedStatement);
    }

    public Collection<Statement> getUnrecognisedStatements()
    {
        return unrecognisedStatements;
    }

    public boolean getAllowImplicitQueryTypeInclusions()
    {
        return allowImplicitQueryInclusions;
    }
    
    public void setAllowImplicitQueryTypeInclusions(boolean allowImplicitQueryInclusions)
    {
        this.allowImplicitQueryInclusions = allowImplicitQueryInclusions;
    }
    
    public boolean getAllowImplicitProviderInclusions()
    {
        return allowImplicitProviderInclusions;
    }
    
    public void setAllowImplicitProviderInclusions(boolean allowImplicitProviderInclusions)
    {
        this.allowImplicitProviderInclusions = allowImplicitProviderInclusions;
    }

    public boolean getAllowImplicitRdfRuleInclusions()
    {
        return allowImplicitRdfRuleInclusions;
    }
    
    public void setAllowImplicitRdfRuleInclusions(boolean allowImplicitRdfRuleInclusions)
    {
        this.allowImplicitRdfRuleInclusions = allowImplicitRdfRuleInclusions;
    }
    
    public void setProfileAdministrators(Collection<URI> profileAdministrators)
    {
        this.profileAdministrators = profileAdministrators;
    }
    
    public Collection<URI> getProfileAdministrators()
    {
        return profileAdministrators;
    }
    
    public void setIncludeProviders(Collection<URI> includeProviders)
    {
        this.includeProviders = includeProviders;
    }
    
    public Collection<URI> getIncludeProviders()
    {
        return includeProviders;
    }
    
    public void setExcludeProviders(Collection<URI> excludeProviders)
    {
        this.excludeProviders = excludeProviders;
    }
    
    public Collection<URI> getExcludeProviders()
    {
        return excludeProviders;
    }
    

    public void setIncludeQueryTypes(Collection<URI> includeQueries)
    {
        this.includeQueries = includeQueries;
    }
    
    public Collection<URI> getIncludeQueryTypes()
    {
        return includeQueries;
    }
    
    public void setExcludeQueryTypes(Collection<URI> excludeQueries)
    {
        this.excludeQueries = excludeQueries;
    }
    
    public Collection<URI> getExcludeQueryTypes()
    {
        return excludeQueries;
    }    
    
    public void setIncludeRdfRules(Collection<URI> includeRdfRules)
    {
        this.includeRdfRules = includeRdfRules;
    }
    
    public Collection<URI> getIncludeRdfRules()
    {
        return includeRdfRules;
    }
    
    public void setExcludeRdfRules(Collection<URI> excludeRdfRules)
    {
        this.excludeRdfRules = excludeRdfRules;
    }
    
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

    public void addIncludeProvider(URI includeProvider)
    {
        if(this.includeProviders == null)
        {
            this.includeProviders = new LinkedList<URI>();
        }
        
        this.includeProviders.add(includeProvider);
    }

    public void addExcludeProvider(URI excludeProvider)
    {
        if(this.excludeProviders == null)
        {
            this.excludeProviders = new LinkedList<URI>();
        }
        
        this.excludeProviders.add(excludeProvider);
    }

    public void addIncludeQueryType(URI includeQuery)
    {
        if(this.includeQueries == null)
        {
            this.includeQueries = new LinkedList<URI>();
        }
        
        this.includeQueries.add(includeQuery);
    }

    public void addExcludeQueryType(URI excludeQuery)
    {
        if(this.excludeQueries == null)
        {
            this.excludeQueries = new LinkedList<URI>();
        }
        
        this.excludeQueries.add(excludeQuery);
    }

    public void addIncludeRdfRule(URI includeRdfRule)
    {
        if(this.includeRdfRules == null)
        {
            this.includeRdfRules = new LinkedList<URI>();
        }
        
        this.includeRdfRules.add(includeRdfRule);
    }

    public void addExcludeRdfRule(URI excludeRdfRule)
    {
        if(this.excludeRdfRules == null)
        {
            this.excludeRdfRules = new LinkedList<URI>();
        }
        
        this.excludeRdfRules.add(excludeRdfRule);
    }    
}
