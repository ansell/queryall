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
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.openrdf.sail.memory.MemoryStore;

import java.util.HashSet;
import java.util.List;
import java.util.Collection;

import org.queryall.helpers.*;
import org.queryall.*;

import org.apache.log4j.Logger;

public class ProfileImpl extends Profile
{
    private static final Logger log = Logger.getLogger(Profile.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    private static final boolean _INFO = log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.DEFAULT_RDF_PROFILE_NAMESPACE;
    
    public static final int SPECIFIC_INCLUDE = 1;
    public static final int SPECIFIC_EXCLUDE = 2;
    public static final int IMPLICIT_INCLUDE = 3;
    public static final int NO_MATCH = 4;
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key;
    private String title = "";
    private URI curationStatus = ProjectImpl.projectNotCuratedUri;
    
    private int order = 100;
    
    private boolean allowImplicitQueryInclusions = false;
    private boolean allowImplicitProviderInclusions = false;
    private boolean allowImplicitRdfRuleInclusions = false;
    
    private URI defaultProfileIncludeExcludeOrder = ProfileImpl.profileIncludeThenExcludeUri;
    
    private Collection<URI> profileAdministrators = new HashSet<URI>();
    
    private Collection<URI> includeProviders = new HashSet<URI>();
    private Collection<URI> excludeProviders = new HashSet<URI>();
    private Collection<URI> includeQueries = new HashSet<URI>();
    private Collection<URI> excludeQueries = new HashSet<URI>();
    private Collection<URI> includeRdfRules = new HashSet<URI>();
    private Collection<URI> excludeRdfRules = new HashSet<URI>();
    
    
    public static URI profileTypeUri;
    public static URI profileTitle;
    public static URI profileAdministratorUri;
    public static URI profileOrderUri;
    public static URI profileAllowImplicitQueryInclusionsUri;
    public static URI profileAllowImplicitProviderInclusionsUri;
    public static URI profileAllowImplicitRdfRuleInclusionsUri;
    public static URI profileDefaultIncludeExcludeOrderUri;
    public static URI profileIncludeExcludeOrderUri;
    public static URI profileIncludeProviderInProfile;
    public static URI profileExcludeProviderFromProfile;
    public static URI profileIncludeQueryInProfile;
    public static URI profileExcludeQueryFromProfile;
    public static URI profileIncludeRdfRuleInProfile;
    public static URI profileExcludeRdfRuleFromProfile;
    
    public static URI profileExcludeThenIncludeUri;
    public static URI profileIncludeThenExcludeUri;
    public static URI profileIncludeExcludeOrderUndefinedUri;
    
    public static String profileNamespace;
    
    static
    {
        ValueFactory f = new MemValueFactory();
        
        profileNamespace = Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX
                           +Settings.DEFAULT_RDF_PROFILE_NAMESPACE
                           +Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX;
                           
        profileTypeUri = f.createURI(profileNamespace+"Profile");
        profileTitle = f.createURI(profileNamespace+"Title");
        profileOrderUri = f.createURI(profileNamespace+"order");
        profileAdministratorUri = f.createURI(profileNamespace+"hasProfileAdministrator");
        
        profileAllowImplicitQueryInclusionsUri = f.createURI(profileNamespace+"allowImplicitQueryInclusions");
        profileAllowImplicitProviderInclusionsUri = f.createURI(profileNamespace+"allowImplicitProviderInclusions");
        profileAllowImplicitRdfRuleInclusionsUri = f.createURI(profileNamespace+"allowImplicitRdfRuleInclusions");
        profileDefaultIncludeExcludeOrderUri = f.createURI(profileNamespace+"defaultProfileIncludeExcludeOrder");
        profileIncludeExcludeOrderUri = f.createURI(profileNamespace+"profileIncludeExcludeOrder");
        profileIncludeProviderInProfile = f.createURI(profileNamespace+"includesProvider");
        profileExcludeProviderFromProfile = f.createURI(profileNamespace+"excludesProvider");
        profileIncludeQueryInProfile = f.createURI(profileNamespace+"includesQuery");
        profileExcludeQueryFromProfile = f.createURI(profileNamespace+"excludesQuery");
        profileIncludeRdfRuleInProfile = f.createURI(profileNamespace+"includesRdfRule");
        profileExcludeRdfRuleFromProfile = f.createURI(profileNamespace+"excludesRdfRule");
        
        profileExcludeThenIncludeUri = f.createURI(profileNamespace+"excludeThenInclude");
        profileIncludeThenExcludeUri = f.createURI(profileNamespace+"includeThenExclude");
        profileIncludeExcludeOrderUndefinedUri = f.createURI(profileNamespace+"includeExcludeOrderUndefined");
    }
    
    public static boolean schemaToRdf(Repository myRepository, String keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            URI contextKeyUri = f.createURI(keyToUse);
            con.setAutoCommit(false);
            
            con.add(profileTypeUri, RDF.TYPE, OWL.CLASS, contextKeyUri);
            
            if(modelVersion == 1)
            {
                con.add(profileTitle, RDFS.SUBPROPERTYOF, f.createURI(Settings.DC_NAMESPACE+"title"), contextKeyUri);
            }
            
            con.add(profileOrderUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(profileOrderUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(profileAdministratorUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(profileDefaultIncludeExcludeOrderUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(profileAllowImplicitQueryInclusionsUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(profileAllowImplicitProviderInclusionsUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(profileAllowImplicitRdfRuleInclusionsUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(profileIncludeProviderInProfile, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(profileExcludeProviderFromProfile, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(profileIncludeQueryInProfile, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(profileExcludeQueryFromProfile, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(profileIncludeRdfRuleInProfile, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(profileExcludeRdfRuleFromProfile, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(profileExcludeThenIncludeUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(profileIncludeThenExcludeUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(profileIncludeExcludeOrderUndefinedUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
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
        catch (OpenRDFException ordfe)
        {
            log.error(ordfe);
            
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
                con.rollback();
                
            throw ordfe;
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
        
        
        ValueFactory f = new MemValueFactory();
        
        URI profileInstanceUri = keyToUse;
        
        for(Statement nextStatement : inputStatements)
        {
            if(_DEBUG)
            {
                log.debug("Profile.fromRdf: nextStatement: "+nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE) && nextStatement.getObject().equals(profileTypeUri))
            {
                if(_TRACE)
                {
                    log.trace("Profile.fromRdf: found valid type predicate for URI: "+keyToUse);
                }
                
                // resultIsValid = true;
                result.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.projectCurationStatusUri))
            {
                result.setCurationStatus((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(profileTitle) || nextStatement.getPredicate().equals(Settings.DC_TITLE))
            {
                result.setTitle(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(profileOrderUri))
            {
                result.setOrder(Utilities.getIntegerFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(profileAdministratorUri))
            {
                tempProfileAdministrators.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(profileDefaultIncludeExcludeOrderUri))
            {
                result.setDefaultProfileIncludeExcludeOrder((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(profileAllowImplicitQueryInclusionsUri))
            {
                result.setAllowImplicitQueryInclusions(Utilities.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(profileAllowImplicitProviderInclusionsUri))
            {
                result.setAllowImplicitProviderInclusions(Utilities.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(profileAllowImplicitRdfRuleInclusionsUri))
            {
                result.setAllowImplicitRdfRuleInclusions(Utilities.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(profileIncludeProviderInProfile))
            {
                tempIncludeProviders.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(profileExcludeProviderFromProfile))
            {
                tempExcludeProviders.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(profileIncludeQueryInProfile))
            {
                tempIncludeQueries.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(profileExcludeQueryFromProfile))
            {
                tempExcludeQueries.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(profileIncludeRdfRuleInProfile))
            {
                tempIncludeRdfRules.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(profileExcludeRdfRuleFromProfile))
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
    
    @Override
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
                curationStatusLiteral = ProjectImpl.projectNotCuratedUri;
            else
                curationStatusLiteral = curationStatus;
                
            // log.info("About to add to the repository");
            
            con.setAutoCommit(false);
            
            con.add(profileInstanceUri, RDF.TYPE, profileTypeUri, profileInstanceUri);
            
            con.add(profileInstanceUri, ProjectImpl.projectCurationStatusUri, curationStatusLiteral, profileInstanceUri);
            
            // log.info("About to add to the repository 2");
            if(modelVersion == 1)
            {
                con.add(profileInstanceUri, profileTitle, titleLiteral, profileInstanceUri);
            }
            else
            {
                con.add(profileInstanceUri, Settings.DC_TITLE, titleLiteral, profileInstanceUri);
            }
            
            // log.info("About to add to the repository 3");
            con.add(profileInstanceUri, profileOrderUri, orderLiteral, profileInstanceUri);
            
            // log.info("About to add to the repository 4");
            con.add(profileInstanceUri, profileAllowImplicitQueryInclusionsUri, allowImplicitQueryInclusionsLiteral, profileInstanceUri);
            
            // log.info("About to add to the repository 5");
            con.add(profileInstanceUri, profileAllowImplicitProviderInclusionsUri, allowImplicitProviderInclusionsLiteral, profileInstanceUri);
            
            // log.info("About to add to the repository 6");
            con.add(profileInstanceUri, profileAllowImplicitRdfRuleInclusionsUri, allowImplicitRdfRuleInclusionsLiteral, profileInstanceUri);
            
            // log.info("About to add to the repository 7");
            con.add(profileInstanceUri, profileDefaultIncludeExcludeOrderUri, defaultProfileIncludeExcludeOrderLiteral, profileInstanceUri);
            
            // log.info("About to add array based information");
            
            if(includeProviders != null)
            {
            
                for(URI nextIncludeProviders : includeProviders)
                {
                    con.add(profileInstanceUri, profileIncludeProviderInProfile, nextIncludeProviders, profileInstanceUri);
                }
            }
            
            if(excludeProviders != null)
            {
            
                for(URI nextExcludeProviders : excludeProviders)
                {
                    con.add(profileInstanceUri, profileExcludeProviderFromProfile, nextExcludeProviders, profileInstanceUri);
                }
            }
            
            
            if(profileAdministrators != null)
            {
            
                for(URI nextProfileAdministrator : profileAdministrators)
                {
                    con.add(profileInstanceUri, profileAdministratorUri, nextProfileAdministrator, profileInstanceUri);
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
                    
                    con.add(profileInstanceUri, profileIncludeQueryInProfile, nextIncludeQuery, profileInstanceUri);
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
                    
                    con.add(profileInstanceUri, profileExcludeQueryFromProfile, nextExcludeQuery, profileInstanceUri);
                }
            }
            
            if(includeRdfRules != null)
            {
            
                for(URI nextIncludeRdfRules : includeRdfRules)
                {
                    con.add(profileInstanceUri, profileIncludeRdfRuleInProfile, nextIncludeRdfRules, profileInstanceUri);
                }
            }
            
            if(excludeRdfRules != null)
            {
            
                for(URI nextExcludeRdfRules : excludeRdfRules)
                {
                    con.add(profileInstanceUri, profileExcludeRdfRuleFromProfile, nextExcludeRdfRules, profileInstanceUri);
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
        catch (OpenRDFException ordfe)
        {
            log.error(ordfe);
            
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            throw ordfe;
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
        
        int trueResult = usedWithList(nextProviderUri, nextIncludeExcludeOrder, includeProviders, excludeProviders);
        
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
        
        int trueResult = usedWithList(nextQueryUri, nextIncludeExcludeOrder, includeQueries, excludeQueries);
        
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
        
        int trueResult = usedWithList(nextRdfRuleUri, nextIncludeExcludeOrder, includeRdfRules, excludeRdfRules);
        
        // in all known cases
        
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
    
    private int usedWithList(URI nextUri, URI nextIncludeExcludeOrder, Collection<URI> includeList, Collection<URI> excludeList)
    {
        boolean includeFound = false;
        boolean excludeFound = false;
        
        if(includeList == null || excludeList == null)
        {
            throw new IllegalArgumentException("Profile.usedWithList: includeList or excludeList was null");
        }
        
        for(URI nextIncludedUri : includeList)
        {
            if(nextIncludedUri.equals(nextUri))
            {
                includeFound = true;
                break;
            }
        }
        
        for(URI nextExcludedUri : excludeList)
        {
            if(nextExcludedUri.equals(nextUri))
            {
                excludeFound = true;
                break;
            }
        }
        
        if(nextIncludeExcludeOrder == null || nextIncludeExcludeOrder.equals(ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri()))
        {
            nextIncludeExcludeOrder = defaultProfileIncludeExcludeOrder;
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
    
    @Override
    public String toString()
    {
        String result = "\n";
        
        result += "key="+key+"\n";
        
        return result;
    }
    
    @Override
    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        String prefix = "profile_";
        
        return sb.toString();
    }
    
    @Override
    public String toHtml()
    {
        StringBuilder sb = new StringBuilder();
        
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
        this.setKey(Utilities.createURI(nextKey));
    }

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
     * @return the URI used for the rdf Type of these elements
     */
    @Override
    public String getElementType()
    {
        return profileTypeUri.stringValue();
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
        return profileExcludeThenIncludeUri;
    }

    public static URI getIncludeThenExcludeUri()
    {
        return profileIncludeThenExcludeUri;
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
}
