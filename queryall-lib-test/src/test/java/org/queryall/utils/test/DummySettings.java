/**
 * 
 */
package org.queryall.utils.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

import org.openrdf.model.URI;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.ruletest.RuleTest;

/**
 * This is a dummy class used for testing, it does not perform the actual operations needed
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class DummySettings implements QueryAllConfiguration
{
    
    /**
     * 
     */
    public DummySettings()
    {
        // TODO Auto-generated constructor stub
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#addNamespaceEntry(org.queryall.api.namespace.NamespaceEntry)
     */
    @Override
    public void addNamespaceEntry(NamespaceEntry nextNamespaceEntry)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#addNormalisationRule(org.queryall.api.rdfrule.NormalisationRule)
     */
    @Override
    public void addNormalisationRule(NormalisationRule nextNormalisationRule)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#addProfile(org.queryall.api.profile.Profile)
     */
    @Override
    public void addProfile(Profile nextProfile)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#addProvider(org.queryall.api.provider.Provider)
     */
    @Override
    public void addProvider(Provider nextProvider)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#addQueryType(org.queryall.api.querytype.QueryType)
     */
    @Override
    public void addQueryType(QueryType nextQueryType)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#addRuleTest(org.queryall.api.ruletest.RuleTest)
     */
    @Override
    public void addRuleTest(RuleTest nextRuleTest)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getAllNamespaceEntries()
     */
    @Override
    public Map<URI, NamespaceEntry> getAllNamespaceEntries()
    {
        return Collections.emptyMap();
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getAllNormalisationRules()
     */
    @Override
    public Map<URI, NormalisationRule> getAllNormalisationRules()
    {
        return Collections.emptyMap();
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getAllProfiles()
     */
    @Override
    public Map<URI, Profile> getAllProfiles()
    {
        return Collections.emptyMap();
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getAllProviders()
     */
    @Override
    public Map<URI, Provider> getAllProviders()
    {
        return Collections.emptyMap();
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getAllQueryTypes()
     */
    @Override
    public Map<URI, QueryType> getAllQueryTypes()
    {
        return Collections.emptyMap();
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getAllRuleTests()
     */
    @Override
    public Map<URI, RuleTest> getAllRuleTests()
    {
        return Collections.emptyMap();
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getBooleanProperty(java.lang.String, boolean)
     */
    @Override
    public boolean getBooleanProperty(String propertyKey, boolean defaultValue)
    {
        return defaultValue;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getDefaultHostAddress()
     */
    @Override
    public String getDefaultHostAddress()
    {
        return "";
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getFloatProperty(java.lang.String, float)
     */
    @Override
    public float getFloatProperty(String key, float defaultValue)
    {
        return defaultValue;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getIntProperty(java.lang.String, int)
     */
    @Override
    public int getIntProperty(String key, int defaultValue)
    {
        return defaultValue;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getLongProperty(java.lang.String, long)
     */
    @Override
    public long getLongProperty(String key, long defaultValue)
    {
        return defaultValue;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getNamespacePrefixesToUris()
     */
    @Override
    public Map<String, Collection<URI>> getNamespacePrefixesToUris()
    {
        return Collections.emptyMap();
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getPlainNamespaceAndIdentifierPattern()
     */
    @Override
    public Pattern getPlainNamespaceAndIdentifierPattern()
    {
        return Pattern.compile(".*");
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getPlainNamespacePattern()
     */
    @Override
    public Pattern getPlainNamespacePattern()
    {
        return Pattern.compile(".*");
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getSeparator()
     */
    @Override
    public String getSeparator()
    {
        return "";
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getStringProperties(java.lang.String)
     */
    @Override
    public Collection<String> getStringProperties(String string)
    {
        return new ArrayList<String>(0);
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getStringProperty(java.lang.String, java.lang.String)
     */
    @Override
    public String getStringProperty(String key, String defaultValue)
    {
        return defaultValue;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getTagPattern()
     */
    @Override
    public Pattern getTagPattern()
    {
        return Pattern.compile(".*");
    }
    
    @Override
    public Collection<URI> getURIProperties(String string)
    {
        return new ArrayList<URI>(0);
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#getURIProperty(java.lang.String, org.openrdf.model.URI)
     */
    @Override
    public URI getURIProperty(String key, URI defaultValue)
    {
        // TODO Auto-generated method stub
        return defaultValue;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, boolean)
     */
    @Override
    public void setProperty(String propertyKey, boolean propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, float)
     */
    @Override
    public void setProperty(String propertyKey, float propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, int)
     */
    @Override
    public void setProperty(String propertyKey, int propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, long)
     */
    @Override
    public void setProperty(String propertyKey, long propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, java.lang.String)
     */
    @Override
    public void setProperty(String propertyKey, String propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, org.openrdf.model.URI)
     */
    @Override
    public void setProperty(String propertyKey, URI propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#setStringCollectionProperty(java.lang.String, java.util.Collection)
     */
    @Override
    public void setStringCollectionProperty(String propertyKey, Collection<String> propertyValues)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.QueryAllConfiguration#setURICollectionProperty(java.lang.String, java.util.Collection)
     */
    @Override
    public void setURICollectionProperty(String propertyKey, Collection<URI> propertyValues)
    {
        // TODO Auto-generated method stub
        
    }
    
}
