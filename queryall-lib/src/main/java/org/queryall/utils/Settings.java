/**
 * 
 */
package org.queryall.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.utils.Constants;
import org.queryall.exception.QueryAllRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a dummy class used for testing, it does not perform the actual operations needed
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class Settings implements QueryAllConfiguration
{
    private static final Logger log = LoggerFactory.getLogger(Settings.class);
    private static final boolean _TRACE = Settings.log.isTraceEnabled();
    private static final boolean _DEBUG = Settings.log.isDebugEnabled();
    private static final boolean _INFO = Settings.log.isInfoEnabled();
    
    private ConcurrentHashMap<URI, NamespaceEntry> namespaceEntries = new ConcurrentHashMap<URI, NamespaceEntry>();
    private ConcurrentHashMap<URI, NormalisationRule> normalisationRules =
            new ConcurrentHashMap<URI, NormalisationRule>();
    private ConcurrentHashMap<URI, Profile> profiles = new ConcurrentHashMap<URI, Profile>();
    private ConcurrentHashMap<URI, Provider> providers = new ConcurrentHashMap<URI, Provider>();
    private ConcurrentHashMap<URI, QueryType> queryTypes = new ConcurrentHashMap<URI, QueryType>();
    private ConcurrentHashMap<URI, RuleTest> ruleTests = new ConcurrentHashMap<URI, RuleTest>();
    private ConcurrentHashMap<String, Collection<URI>> namespacePrefixesToUris =
            new ConcurrentHashMap<String, Collection<URI>>();
    
    private ConcurrentHashMap<String, Collection<Object>> properties = new ConcurrentHashMap<String, Collection<Object>>();
    private Pattern cachedTagPattern;
    private String cachedSeparator;
    
    /**
     * 
     */
    public Settings()
    {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void addNamespaceEntry(final NamespaceEntry nextNamespaceEntry)
    {
        this.namespaceEntries.put(nextNamespaceEntry.getKey(), nextNamespaceEntry);
        
        final Collection<URI> nextPreferredList = new HashSet<URI>();
        nextPreferredList.add(nextNamespaceEntry.getKey());
        
        final Collection<URI> ifPreferredAbsent =
                this.namespacePrefixesToUris.putIfAbsent(nextNamespaceEntry.getPreferredPrefix(), nextPreferredList);
        
        if(ifPreferredAbsent != null)
        {
            nextPreferredList.addAll(ifPreferredAbsent);
            this.namespacePrefixesToUris.put(nextNamespaceEntry.getPreferredPrefix(), nextPreferredList);
        }
        
        for(final String nextAlternate : nextNamespaceEntry.getAlternativePrefixes())
        {
            final Collection<URI> nextAlternateList = new HashSet<URI>();
            nextAlternateList.add(nextNamespaceEntry.getKey());
            
            final Collection<URI> ifAlternateAbsent =
                    this.namespacePrefixesToUris.putIfAbsent(nextAlternate, nextAlternateList);
            
            if(ifAlternateAbsent != null)
            {
                nextAlternateList.addAll(ifAlternateAbsent);
                this.namespacePrefixesToUris.put(nextAlternate, nextAlternateList);
            }
        }
    }
    
    /**
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#addNormalisationRule(org.queryall.api.rdfrule
     *      .NormalisationRule)
     */
    @Override
    public void addNormalisationRule(final NormalisationRule nextNormalisationRule)
    {
        this.normalisationRules.put(nextNormalisationRule.getKey(), nextNormalisationRule);
    }
    
    /**
     * 
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#addProfile(org.queryall.api.profile.Profile)
     */
    @Override
    public void addProfile(final Profile nextProfile)
    {
        this.profiles.put(nextProfile.getKey(), nextProfile);
    }
    
    /**
     * 
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#addProvider(org.queryall.api.provider.Provider)
     */
    @Override
    public void addProvider(final Provider nextProvider)
    {
        this.providers.put(nextProvider.getKey(), nextProvider);
    }
    
    /**
     * 
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#addQueryType(org.queryall.api.querytype.QueryType
     *      )
     */
    @Override
    public void addQueryType(final QueryType nextQueryType)
    {
        this.queryTypes.put(nextQueryType.getKey(), nextQueryType);
    }
    
    /**
     * 
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#addRuleTest(org.queryall.api.ruletest.RuleTest)
     */
    @Override
    public void addRuleTest(final RuleTest nextRuleTest)
    {
        this.ruleTests.put(nextRuleTest.getKey(), nextRuleTest);
    }
    
    @Override
    public Collection<Object> clearProperty(final String propertyKey)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllNamespaceEntries()
     */
    @Override
    public Map<URI, NamespaceEntry> getAllNamespaceEntries()
    {
        return Collections.unmodifiableMap(this.namespaceEntries);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllNormalisationRules()
     */
    @Override
    public Map<URI, NormalisationRule> getAllNormalisationRules()
    {
        return Collections.unmodifiableMap(this.normalisationRules);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllProfiles()
     */
    @Override
    public Map<URI, Profile> getAllProfiles()
    {
        return Collections.unmodifiableMap(this.profiles);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllProviders()
     */
    @Override
    public Map<URI, Provider> getAllProviders()
    {
        return Collections.unmodifiableMap(this.providers);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllQueryTypes()
     */
    @Override
    public Map<URI, QueryType> getAllQueryTypes()
    {
        return Collections.unmodifiableMap(this.queryTypes);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllRuleTests()
     */
    @Override
    public Map<URI, RuleTest> getAllRuleTests()
    {
        return Collections.unmodifiableMap(this.ruleTests);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getBooleanProperty(java.lang.String,
     * boolean)
     */
    @Override
    public boolean getBooleanProperty(final String propertyKey, final boolean defaultValue)
    {
        if(this.properties.containsKey(propertyKey))
        {
            Collection<Object> properties = this.properties.get(propertyKey);
                    
            if(properties.size() == 1)
            {
                Boolean next = (Boolean)properties.iterator().next();
                
                return next;
            }
            else if(properties.size() == 0)
            {
                log.error("No property value found for propertyKey="+propertyKey);
            }
            else
            {
                log.error("More than one property value found for propertyKey="+propertyKey);
            }
        }
        
        return defaultValue;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getDefaultHostAddress()
     */
    @Override
    public String getDefaultHostAddress()
    {
        return "";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getFloatProperty(java.lang.String, float)
     */
    @Override
    public float getFloatProperty(final String propertyKey, final float defaultValue)
    {
        if(this.properties.containsKey(propertyKey))
        {
            Collection<Object> properties = this.properties.get(propertyKey);
                    
            if(properties.size() == 1)
            {
                Float next = (Float)properties.iterator().next();
                
                return next;
            }
            else if(properties.size() == 0)
            {
                log.error("No property value found for propertyKey="+propertyKey);
            }
            else
            {
                log.error("More than one property value found for propertyKey="+propertyKey);
            }
        }
        
        return defaultValue;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getIntProperty(java.lang.String, int)
     */
    @Override
    public int getIntProperty(final String propertyKey, final int defaultValue)
    {
        if(this.properties.containsKey(propertyKey))
        {
            Collection<Object> properties = this.properties.get(propertyKey);
                    
            if(properties.size() == 1)
            {
                Integer next = (Integer)properties.iterator().next();
                
                return next;
            }
            else if(properties.size() == 0)
            {
                log.error("No property value found for propertyKey="+propertyKey);
            }
            else
            {
                log.error("More than one property value found for propertyKey="+propertyKey);
            }
        }
        
        return defaultValue;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getLongProperty(java.lang.String, long)
     */
    @Override
    public long getLongProperty(final String propertyKey, final long defaultValue)
    {
        if(this.properties.containsKey(propertyKey))
        {
            Collection<Object> properties = this.properties.get(propertyKey);
                    
            if(properties.size() == 1)
            {
                Long next = (Long)properties.iterator().next();
                
                return next;
            }
            else if(properties.size() == 0)
            {
                log.error("No property value found for propertyKey="+propertyKey);
            }
            else
            {
                log.error("More than one property value found for propertyKey="+propertyKey);
            }
        }
        
        return defaultValue;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getNamespaceEntry(org.openrdf.model.URI)
     */
    @Override
    public NamespaceEntry getNamespaceEntry(final URI nextNamespaceEntryUri)
    {
        return this.namespaceEntries.get(nextNamespaceEntryUri);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getNamespacePrefixesToUris()
     */
    @Override
    public Map<String, Collection<URI>> getNamespacePrefixesToUris()
    {
        return Collections.unmodifiableMap(this.namespacePrefixesToUris);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getNormalisationRule(org.openrdf.model.URI)
     */
    @Override
    public NormalisationRule getNormalisationRule(final URI nextNormalisationRuleUri)
    {
        return this.normalisationRules.get(nextNormalisationRuleUri);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getPlainNamespaceAndIdentifierPattern()
     */
    @Override
    public Pattern getPlainNamespaceAndIdentifierPattern()
    {
        return Pattern.compile(".*");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getPlainNamespacePattern()
     */
    @Override
    public Pattern getPlainNamespacePattern()
    {
        return Pattern.compile(".*");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getProfile(org.openrdf.model.URI)
     */
    @Override
    public Profile getProfile(final URI nextProfileUri)
    {
        return this.profiles.get(nextProfileUri);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getProvider(org.openrdf.model.URI)
     */
    @Override
    public Provider getProvider(final URI nextProviderUri)
    {
        return this.providers.get(nextProviderUri);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getQueryType(org.openrdf.model.URI)
     */
    @Override
    public QueryType getQueryType(final URI nextQueryTypeUri)
    {
        return this.queryTypes.get(nextQueryTypeUri);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getRuleTest(org.openrdf.model.URI)
     */
    @Override
    public RuleTest getRuleTest(final URI nextRuleTestUri)
    {
        return this.ruleTests.get(nextRuleTestUri);
    }
    
    /**
     * This is a helper method to increase performance for the highly accessed default separator property
     * 
     * It caches the value so it may return a different value to getStringProperty("defaultSeparator", ":") if the property changes after the first call to this method.
     */
    @Override
    public String getSeparator()
    {
        if(this.cachedSeparator != null)
        {
            return this.cachedSeparator;
        }
        
        final String separator =
                this.getStringProperty("defaultSeparator", ":");
        
        if(separator != null)
        {
            this.cachedSeparator = separator;
        }
        
        return separator;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getStringProperties(java.lang.String)
     */
    @Override
    public Collection<String> getStringProperties(final String propertyKey)
    {
        if(this.properties.containsKey(propertyKey))
        {
            Collection<Object> nextProperties = this.properties.get(propertyKey);
                    
            Collection<String> results = new ArrayList<String>(nextProperties.size());
            
            for(Object nextProperty : nextProperties)
            {
                if(nextProperty instanceof String)
                {
                    results.add((String)nextProperty);
                }
                else
                {
                    log.warn("Automatically converting a property that was not a String in getStringProperties nextProperty="+nextProperty);
                    
                    results.add(nextProperty.toString());
                }
            }
            
            return results;
        }
        
        return Collections.emptySet();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getStringProperty(java.lang.String,
     * java.lang.String)
     */
    @Override
    public String getStringProperty(final String propertyKey, final String defaultValue)
    {
        if(this.properties.containsKey(propertyKey))
        {
            Collection<Object> properties = this.properties.get(propertyKey);
                    
            if(properties.size() == 1)
            {
                String next = (String)properties.iterator().next();
                
                return next;
            }
            else if(properties.size() == 0)
            {
                log.error("No property value found for propertyKey="+propertyKey);
            }
            else
            {
                log.error("More than one property value found for propertyKey="+propertyKey);
            }
        }
        
        return defaultValue;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getTagPattern()
     */
    @Override
    public Pattern getTagPattern()
    {
        if(this.cachedTagPattern != null)
        {
            return this.cachedTagPattern;
        }
        
        // TODO: Do we need to split this into two patterns so that we can identify the parameter
        // names as separate matching groups from the entire tag with braces etc.?
        final Pattern tempPattern =
                Pattern.compile(this.getStringProperty("tagPatternRegex", ".*(\\$\\{[\\w_-]+\\}).*"));
        
        if(tempPattern != null)
        {
            this.cachedTagPattern = tempPattern;
        }
        
        return tempPattern;
    }
    
    @Override
    public Collection<URI> getURIProperties(final String propertyKey)
    {
        if(this.properties.containsKey(propertyKey))
        {
            Collection<Object> nextProperties = this.properties.get(propertyKey);
                    
            Collection<URI> results = new ArrayList<URI>(nextProperties.size());
            
            for(Object nextProperty : nextProperties)
            {
                if(nextProperty instanceof URI)
                {
                    results.add((URI)nextProperty);
                }
                else
                {
                    log.warn("Found a property that was not a URI in getURIProperties nextProperty="+nextProperty);
                }
            }
            
            return results;
        }
        
        return Collections.emptySet();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getURIProperty(java.lang.String,
     * org.openrdf.model.URI)
     */
    @Override
    public URI getURIProperty(final String propertyKey, final URI defaultValue)
    {
        Collection<URI> uriProperties = getURIProperties(propertyKey);
        
        if(uriProperties.size() == 1)
        {
            return uriProperties.iterator().next();
        }
        else
        {
            log.warn("Did not find a unique property for propertyKey="+propertyKey+" returning defaultValue="+defaultValue);
            
            return defaultValue;
        }
    }
    
    /**
     * Sets the property into the internal property cache
     * 
     * @param propertyKey The key for the property value
     * @param propertyValue The property value to set
     * @param overwrite If true, the given property value will overwrite the current properties, otherwise they will both be included
     */
    private void setObjectPropertyHelper(final String propertyKey, final Object propertyValue, boolean overwrite)
    {
        log.info("setObjectPropertyHelper(String,Object) propertyKey="+propertyKey+" propertyValue="+propertyValue.toString());
        final Collection<Object> nextList = new ArrayList<Object>(5);
        nextList.add(propertyValue);
        
        final Collection<Object> ifAbsent = this.properties.putIfAbsent(propertyKey, nextList);
        
        log.info("setObjectPropertyHelper(String,Object) this.properties.get(propertyKey)="+this.properties.get(propertyKey));

        // if there were properties previously, than synchronise access to the properties object and add the list, any other additions to this property should wait to synchronise here
        if(ifAbsent != null)
        {
            log.info("setObjectPropertyHelper(String,Object) ifAbsent not equal to null");
            synchronized(this.properties)
            {
                if(!overwrite)
                {
                    nextList.addAll(ifAbsent);
                }
                log.info("setObjectPropertyHelper(String,Object) ifAbsent not equal to null nextList="+nextList);
                this.properties.put(propertyKey, nextList);
            }
        }
    }
    
    /**
     * Sets the property into the internal property cache
     * 
     * @param propertyKey The key for the property value
     * @param propertyValue The property value to set
     * @param overwrite If true, the given property value will overwrite the current properties, otherwise they will both be included
     */
    private void setObjectCollectionPropertyHelper(final String propertyKey, final Collection<Object> propertyValue, boolean overwrite)
    {
        log.info("setObjectPropertyHelper(String,Object) propertyKey="+propertyKey+" propertyValue="+propertyValue.toString());
//        final Collection<Object> nextList = new ArrayList<Object>(5);
//        nextList.add(propertyValue);
        
        final Collection<Object> ifAbsent = this.properties.putIfAbsent(propertyKey, propertyValue);
        
        log.info("setObjectPropertyHelper(String,Object) this.properties.get(propertyKey)="+this.properties.get(propertyKey));

        // if there were properties previously, than synchronise access to the properties object and add the list, any other additions to this property should wait to synchronise here
        if(ifAbsent != null)
        {
            log.info("setObjectPropertyHelper(String,Object) ifAbsent not equal to null");
            synchronized(this.properties)
            {
                // do this to make sure that it is writeable, they could have sent us a collection that was not modifiable
                Collection<Object> nextList = new ArrayList<Object>(propertyValue);
                
                if(!overwrite)
                {
                    nextList.addAll(ifAbsent);
                }
                log.info("setObjectPropertyHelper(String,Object) ifAbsent not equal to null nextList="+nextList);
                this.properties.put(propertyKey, nextList);
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, boolean)
     */
    @Override
    public void setProperty(final String propertyKey, final boolean propertyValue)
    {
        log.info("setProperty(String,boolean) propertyKey="+propertyKey+" propertyValue="+Boolean.valueOf(propertyValue).toString());
        
        setProperty(propertyKey, propertyValue, true);
    }
    
    private void setProperty(final String propertyKey, final boolean propertyValue, boolean overwrite)
    {
        setObjectPropertyHelper(propertyKey, Boolean.valueOf(propertyValue), overwrite);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, float)
     */
    @Override
    public void setProperty(final String propertyKey, final float propertyValue)
    {
        log.info("setProperty(String,float) propertyKey="+propertyKey+" propertyValue="+Float.valueOf(propertyValue).toString());
        
        setProperty(propertyKey, propertyValue, true);
    }
    
    private void setProperty(final String propertyKey, final float propertyValue, boolean overwrite)
    {
        setObjectPropertyHelper(propertyKey, Float.valueOf(propertyValue), overwrite);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, int)
     */
    @Override
    public void setProperty(final String propertyKey, final int propertyValue)
    {
        log.info("setProperty(String,int) propertyKey="+propertyKey+" propertyValue="+Integer.valueOf(propertyValue).toString());
        
        setProperty(propertyKey, propertyValue, true);
    }
    
    private void setProperty(final String propertyKey, final int propertyValue, boolean overwrite)
    {
        setObjectPropertyHelper(propertyKey, Integer.valueOf(propertyValue), overwrite);
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, long)
     */
    @Override
    public void setProperty(final String propertyKey, final long propertyValue)
    {
        log.info("setProperty(String,long) propertyKey="+propertyKey+" propertyValue="+Long.valueOf(propertyValue).toString());
        
        setProperty(propertyKey, propertyValue, true);
    }
    
    private void setProperty(final String propertyKey, final long propertyValue, boolean overwrite)
    {
        setObjectPropertyHelper(propertyKey, Long.valueOf(propertyValue), overwrite);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void setProperty(final String propertyKey, final String propertyValue)
    {
        if(propertyValue == null)
        {
            throw new NullPointerException("property value cannot be null propertyKey="+propertyKey);
        }
        
        log.info("setProperty(String,String) propertyKey="+propertyKey+" propertyValue="+String.valueOf(propertyValue));
        
        setProperty(propertyKey, propertyValue, true);
    }
    
    private void setProperty(final String propertyKey, final String propertyValue, boolean overwrite)
    {
        setObjectPropertyHelper(propertyKey, String.valueOf(propertyValue), overwrite);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String,
     * org.openrdf.model.URI)
     */
    @Override
    public void setProperty(final String propertyKey, final URI propertyValue)
    {
        if(propertyValue == null)
        {
            throw new NullPointerException("property value cannot be null propertyKey="+propertyKey);
        }
        
        log.info("setProperty(String,URI) propertyKey="+propertyKey+" propertyValue="+propertyValue.stringValue());
        setProperty(propertyKey, propertyValue, true);
    }
    
    private void setProperty(final String propertyKey, final URI propertyValue, boolean overwrite)
    {
        if(propertyValue == null)
        {
            throw new NullPointerException("property value cannot be null propertyKey="+propertyKey);
        }
        setObjectPropertyHelper(propertyKey, propertyValue, overwrite);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.queryall.api.base.QueryAllConfiguration#setStringCollectionProperty(java.lang.String,
     * java.util.Collection)
     */
    @Override
    public void setStringCollectionProperty(final String propertyKey, final Collection<String> propertyValues)
    {
        log.info("setProperty(String,Collection<String>) propertyKey="+propertyKey+" propertyValue="+propertyValues.toString());
        
        Collection<Object> newPropertyValues = new ArrayList<Object>(propertyValues);
        
        setObjectCollectionPropertyHelper(propertyKey, newPropertyValues, true);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setURICollectionProperty(java.lang.String,
     * java.util.Collection)
     */
    @Override
    public void setURICollectionProperty(final String propertyKey, final Collection<URI> propertyValues)
    {
        log.info("setProperty(String,Collection<URI>) propertyKey="+propertyKey+" propertyValue="+propertyValues.toString());
        Collection<Object> newPropertyValues = new ArrayList<Object>(propertyValues);
        
        setObjectCollectionPropertyHelper(propertyKey, newPropertyValues, true);
    }

    @Override
    public void setProperty(String propertyKey, Value propertyValue)
    {
        log.info("setProperty(String,Value) propertyKey="+propertyKey+" propertyValue="+propertyValue+" propertyValue.stringValue()="+propertyValue.stringValue()+" propertyValue.getClass().getName()="+propertyValue.getClass().getName());
        setProperty(propertyKey, propertyValue, true);
    }
    
    private void setProperty(String propertyKey, Value propertyValue, boolean overwrite)
    {
        if(propertyValue instanceof URI)
        {
            setProperty(propertyKey, (URI)propertyValue, overwrite);
            return;
        }
        
        if(propertyValue instanceof BNode)
        {
            throw new QueryAllRuntimeException("Cannot handle blank nodes as property values, they have no meaning");
        }
        
        if(propertyValue instanceof Literal)
        {
            Literal literalValue = (Literal)propertyValue;
            URI datatype = literalValue.getDatatype();
            
            log.info("literalValue.getDatatype()="+datatype);
            if(datatype == null)
            {
                // resort to setting it as a String as it didn't have type information attached
                setProperty(propertyKey, propertyValue.stringValue(), overwrite);
            }
            else
            {
                
                if(datatype.equals(Constants.XSD_BOOLEAN))
                {
                    log.info("boolean datatype");
                    try
                    {
                        boolean booleanFromValue = RdfUtils.getBooleanFromValue(literalValue);
                        
                        log.info("booleanFromValue="+booleanFromValue);
                        setProperty(propertyKey, booleanFromValue);
                        
                        return;
                    }
                    catch(QueryAllRuntimeException rex)
                    {
                        log.info("Could not parse boolean value="+literalValue);
                    }
                }
                else if(datatype.equals(Constants.XSD_INT) || datatype.equals(Constants.XSD_INTEGER))
                {
                    log.info("int or integer datatype");
                    try
                    {
                        int intFromValue = RdfUtils.getIntegerFromValue(literalValue);
                        
                        log.info("intFromValue="+intFromValue);
                        setProperty(propertyKey, intFromValue);
                        
                        return;
                    }
                    catch(NumberFormatException nfe)
                    {
                        log.info("Could not parse int value="+literalValue);
                    }
                }
                else if(datatype.equals(Constants.XSD_LONG))
                {
                    log.info("long datatype");
                    try
                    {
                        long longFromValue = RdfUtils.getLongFromValue(literalValue);
                        
                        log.info("longFromValue="+longFromValue);
                        setProperty(propertyKey, longFromValue);
                        
                        return;
                    }
                    catch(NumberFormatException nfe)
                    {
                        log.info("Could not parse long value="+literalValue);
                    }
                }
                else if(datatype.equals(Constants.XSD_FLOAT))
                {
                    log.info("float datatype");
                    try
                    {
                        float floatFromValue = RdfUtils.getFloatFromValue(literalValue);
                        
                        log.info("floatFromValue="+floatFromValue);
                        setProperty(propertyKey, floatFromValue);
                        
                        return;
                    }
                    catch(NumberFormatException nfe)
                    {
                        log.info("Could not parse float value="+literalValue);
                    }
                }
                else
                {
                    log.info("unrecognised datatype");
                    // resort to setting it as a String
                    setProperty(propertyKey, propertyValue.stringValue());
                }
            }
        }
    }

    @Override
    public void setValueCollectionProperty(String propertyKey, Collection<Value> propertyValues)
    {
        clearProperty(propertyKey);
        
        for(Value nextValue : propertyValues)
        {
            setProperty(propertyKey, nextValue, false);
        }
    }
    
    
}
