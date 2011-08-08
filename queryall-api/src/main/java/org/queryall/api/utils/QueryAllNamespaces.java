/**
 * 
 */
package org.queryall.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public enum QueryAllNamespaces
{
    NAMESPACEENTRY("Settings.DEFAULT_RDF_NAMESPACEENTRY_NAMESPACE", "ns", "Namespace Entries"), PROFILE(
            "Settings.DEFAULT_RDF_PROFILE_NAMESPACE", "profile", "Profiles"), PROJECT(
            "Settings.DEFAULT_RDF_PROJECT_NAMESPACE", "project", "Projects"), PROVENANCE(
            "Settings.DEFAULT_RDF_PROVENANCE_NAMESPACE", "provenance", "Provenance Records"), PROVIDER(
            "Settings.DEFAULT_RDF_PROVIDER_NAMESPACE", "provider", "Providers"), QUERY(
            "Settings.DEFAULT_RDF_QUERY_NAMESPACE", "query", "Query Types"), QUERYBUNDLE(
            "Settings.DEFAULT_RDF_QUERYBUNDLE_NAMESPACE", "querybundle", "Query Bundles"), RDFRULE(
            "Settings.DEFAULT_RDF_RDFRULE_NAMESPACE", "rdfrule", "RDF Normalisation Rules"), RULETEST(
            "Settings.DEFAULT_RDF_RULETEST_NAMESPACE", "ruletest", "Rule Tests"), STATISTICS(
            "Settings.DEFAULT_RDF_STATISTICS_NAMESPACE", "statistics", "Statistics"), TEMPLATES(
            "Settings.DEFAULT_RDF_TEMPLATE_NAMESPACE", "template", "Templates"), WEBAPPCONFIG(
            "Settings.DEFAULT_RDF_WEBAPP_CONFIGURATION_NAMESPACE", "webapp_configuration",
            "Web Application Configurations");
    
    private static final Logger log = LoggerFactory.getLogger(QueryAllNamespaces.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = QueryAllNamespaces.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = QueryAllNamespaces.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = QueryAllNamespaces.log.isInfoEnabled();
    
    private String defaultValue;
    private String description;
    private String namespace;
    private String baseUri;
    
    QueryAllNamespaces(final String nextKey, final String defaultValue, final String nextDescription)
    {
        this.defaultValue = defaultValue;
        this.namespace = PropertyUtils.getSystemOrPropertyString(nextKey, defaultValue);
        this.description = nextDescription;
        this.baseUri =
                PropertyUtils.getSystemOrPropertyString("Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX",
                        "http://purl.org/queryall/")
                        + this.namespace
                        + PropertyUtils.getSystemOrPropertyString("Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX", ":");
    }
    
    public String getBaseURI()
    {
        return this.baseUri;
    }
    
    public String getDefaultValue()
    {
        return this.defaultValue;
    }
    
    public String getDescription()
    {
        return this.description;
    }
    
    public String getNamespace()
    {
        return this.namespace;
    }
}
