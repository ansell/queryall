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
import org.openrdf.sail.memory.model.MemValueFactory;

import java.util.HashSet;
import java.util.Collection;
import java.util.LinkedList;

import org.queryall.helpers.*;
import org.queryall.*;

import org.apache.log4j.Logger;

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
        ValueFactory f = new MemValueFactory();
        
        profileNamespace = Settings.getSettings().getOntologyTermUriPrefix()
                           +Settings.getSettings().getNamespaceForProfile()
                           +Settings.getSettings().getOntologyTermUriSuffix();
                           
        setProfileTypeUri(f.createURI(profileNamespace+"Profile"));
        setProfileTitle(f.createURI(profileNamespace+"Title"));
        setProfileOrderUri(f.createURI(profileNamespace+"order"));
        setProfileAdministratorUri(f.createURI(profileNamespace+"hasProfileAdministrator"));
        
        setProfileAllowImplicitQueryInclusionsUri(f.createURI(profileNamespace+"allowImplicitQueryInclusions"));
        setProfileAllowImplicitProviderInclusionsUri(f.createURI(profileNamespace+"allowImplicitProviderInclusions"));
        setProfileAllowImplicitRdfRuleInclusionsUri(f.createURI(profileNamespace+"allowImplicitRdfRuleInclusions"));
        setProfileDefaultIncludeExcludeOrderUri(f.createURI(profileNamespace+"defaultProfileIncludeExcludeOrder"));
        setProfileIncludeExcludeOrderUri(f.createURI(profileNamespace+"profileIncludeExcludeOrder"));
        setProfileIncludeProviderInProfile(f.createURI(profileNamespace+"includesProvider"));
        setProfileExcludeProviderFromProfile(f.createURI(profileNamespace+"excludesProvider"));
        setProfileIncludeQueryInProfile(f.createURI(profileNamespace+"includesQuery"));
        setProfileExcludeQueryFromProfile(f.createURI(profileNamespace+"excludesQuery"));
        setProfileIncludeRdfRuleInProfile(f.createURI(profileNamespace+"includesRdfRule"));
        setProfileExcludeRdfRuleFromProfile(f.createURI(profileNamespace+"excludesRdfRule"));
        
        setProfileExcludeThenIncludeUri(f.createURI(profileNamespace+"excludeThenInclude"));
        setProfileIncludeThenExcludeUri(f.createURI(profileNamespace+"includeThenExclude"));
        setProfileIncludeExcludeOrderUndefinedUri(f.createURI(profileNamespace+"includeExcludeOrderUndefined"));
    }
    
    public static boolean schemaToRdf(Repository myRepository, String keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            URI contextKeyUri = f.createURI(keyToUse);
            con.setAutoCommit(false);
            
            con.add(getProfileTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            
            if(modelVersion == 1)
            {
                con.add(getProfileTitle(), RDFS.SUBPROPERTYOF, f.createURI(Constants.DC_NAMESPACE+"title"), contextKeyUri);
            }
            
            con.add(getProfileOrderUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(getProfileOrderUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(getProfileAdministratorUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(getProfileDefaultIncludeExcludeOrderUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(getProfileAllowImplicitQueryInclusionsUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(getProfileAllowImplicitProviderInclusionsUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(getProfileAllowImplicitRdfRuleInclusionsUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(getProfileIncludeProviderInProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(getProfileExcludeProviderFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(getProfileIncludeQueryInProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(getProfileExcludeQueryFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(getProfileIncludeRdfRuleInProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(getProfileExcludeRdfRuleFromProfile(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(getProfileExcludeThenIncludeUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(getProfileIncludeThenExcludeUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(getProfileIncludeExcludeOrderUndefinedUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
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
    
    // keyToUse is the URI of the next instance that can be found in myRepository
    // returns null if the URI is not in the repository or the information is not enough to create a minimal profile configuration
    public static Profile fromRdf(Collection<Statement> inputStatements, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        Profile result = new ProfileImpl();
        
        // TODO: reenable this when the profiles are being correctly exported and able to be imported with rdf type statements
        // boolean resultIsValid = false;
        boolean resultIsValid = true;
        
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
                result.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.getProjectCurationStatusUri()))
            {
                result.setCurationStatus((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProfileTitle()) || nextStatement.getPredicate().equals(Constants.DC_TITLE))
            {
                result.setTitle(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getProfileOrderUri()))
            {
                result.setOrder(RdfUtils.getIntegerFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getProfileAdministratorUri()))
            {
                tempProfileAdministrators.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProfileDefaultIncludeExcludeOrderUri()))
            {
                result.setDefaultProfileIncludeExcludeOrder((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getProfileAllowImplicitQueryInclusionsUri()))
            {
                result.setAllowImplicitQueryInclusions(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getProfileAllowImplicitProviderInclusionsUri()))
            {
                result.setAllowImplicitProviderInclusions(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getProfileAllowImplicitRdfRuleInclusionsUri()))
            {
                result.setAllowImplicitRdfRuleInclusions(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
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
                result.addUnrecognisedStatement(nextStatement);
            }
        }
        
        result.setProfileAdministrators(tempProfileAdministrators);
        
        result.setIncludeProviders(tempIncludeProviders);
        result.setExcludeProviders(tempExcludeProviders);
        result.setIncludeQueries(tempIncludeQueries);
        result.setExcludeQueries(tempExcludeQueries);
        result.setIncludeRdfRules(tempIncludeRdfRules);
        result.setExcludeRdfRules(tempExcludeRdfRules);
        
        if(_TRACE)
        {
            log.trace("Profile.fromRdf: would have returned... result="+result.toString());
        }
        
        if(resultIsValid)
        {
            return result;
        }
        else
        {
            throw new RuntimeException("Profile.fromRdf: result was not valid");
        }
    }
    
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        //Repository myRepository = new SailRepository(new MemoryStore());
        //myRepository.initialize();
        
        RepositoryConnection con = myRepository.getConnection();
        
        ValueFactory f = myRepository.getValueFactory();
        
        
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
    
    public int usedWithProvider(URI nextProviderUri, URI nextIncludeExcludeOrder)
    {
        if(_DEBUG)
        {
            log.debug("Profile.usedWithProvider: key="+key+" nextProviderUri="+nextProviderUri+" nextIncludeExcludeOrder="+nextIncludeExcludeOrder.stringValue());
        }
        
        int trueResult = usedWithIncludeExcludeList(nextProviderUri, nextIncludeExcludeOrder, includeProviders, excludeProviders, defaultProfileIncludeExcludeOrder);
        
        // in all known cases
        
        if(trueResult == ProfileImpl.IMPLICIT_INCLUDE)
        {
            if(_DEBUG)
            {
                log.debug("Profile.usedWithProvider: found implicit match nextProviderUri="+nextProviderUri+" nextIncludeExcludeOrder="+nextIncludeExcludeOrder+" allowImplicitProviderInclusions="+allowImplicitProviderInclusions);
            }
            
            if(allowImplicitProviderInclusions)
            {
                return ProfileImpl.IMPLICIT_INCLUDE;
            }
            else
            {
                return ProfileImpl.NO_MATCH;
            }
        }
        
        // in all other cases just return the true result
        
        return trueResult;
    }
    
    public int usedWithQuery(URI nextQueryUri, URI nextIncludeExcludeOrder)
    {
        if(_DEBUG)
        {
            log.debug("Profile.usedWithQuery: key="+key+" nextQueryUri="+nextQueryUri+" nextIncludeExcludeOrder="+nextIncludeExcludeOrder);
        }
        
        int trueResult = usedWithIncludeExcludeList(nextQueryUri, nextIncludeExcludeOrder, includeQueries, excludeQueries, defaultProfileIncludeExcludeOrder);
        
        // in all known cases
        
        if(trueResult == ProfileImpl.IMPLICIT_INCLUDE)
        {
            if(_DEBUG)
            {
                log.debug("Profile.usedWithQuery: found implicit match nextQueryUri="+nextQueryUri+" nextIncludeExcludeOrder="+nextIncludeExcludeOrder+" allowImplicitProviderInclusions="+allowImplicitProviderInclusions);
            }
            
            if(allowImplicitProviderInclusions)
            {
                return ProfileImpl.IMPLICIT_INCLUDE;
            }
            else
            {
                return ProfileImpl.NO_MATCH;
            }
        }
        
        // in all other cases just return the true result
        
        return trueResult;
    }
    
    
    public int usedWithRdfRule(URI nextRdfRuleUri, URI nextIncludeExcludeOrder)
    {
        if(_DEBUG)
        {
            log.debug("Profile.usedWithRdfRule: nextRdfRuleUri="+nextRdfRuleUri+" nextIncludeExcludeOrder="+nextIncludeExcludeOrder);
        }
        
        int trueResult = usedWithIncludeExcludeList(nextRdfRuleUri, nextIncludeExcludeOrder, includeRdfRules, excludeRdfRules, defaultProfileIncludeExcludeOrder);
        

        // if the implicit include is not to be recognised in the context of this profile, 
        // filter it out and indicate to the caller that no match occurred so they can continue looking for a match
        if(trueResult == ProfileImpl.IMPLICIT_INCLUDE)
        {
            if(_DEBUG)
            {
                log.debug("Profile.usedWithRdfRule: found implicit match nextRdfRuleUri="+nextRdfRuleUri+" nextIncludeExcludeOrder="+nextIncludeExcludeOrder+" allowImplicitProviderInclusions="+allowImplicitProviderInclusions);
            }
            
            if(allowImplicitProviderInclusions)
            {
                return ProfileImpl.IMPLICIT_INCLUDE;
            }
            else
            {
                return ProfileImpl.NO_MATCH;
            }
        }
        
        // in all other cases just return the true result
        
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

    public boolean getAllowImplicitQueryInclusions()
    {
        return allowImplicitQueryInclusions;
    }
    
    public void setAllowImplicitQueryInclusions(boolean allowImplicitQueryInclusions)
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
    

    public void setIncludeQueries(Collection<URI> includeQueries)
    {
        this.includeQueries = includeQueries;
    }
    
    public Collection<URI> getIncludeQueries()
    {
        return includeQueries;
    }
    
    public void setExcludeQueries(Collection<URI> excludeQueries)
    {
        this.excludeQueries = excludeQueries;
    }
    
    public Collection<URI> getExcludeQueries()
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

    public void addIncludeQuery(URI includeQuery)
    {
        if(this.includeQueries == null)
        {
            this.includeQueries = new LinkedList<URI>();
        }
        
        this.includeQueries.add(includeQuery);
    }

    public void addExcludeQuery(URI excludeQuery)
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
