/**
 * 
 */
package org.queryall.api.utils;

/**
 * Generates the namespace ontology URIs based on calls to PropertyUtils
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public enum QueryAllNamespaces
{
    NAMESPACEENTRY("queryall.namespaceNamespaceEntries", "ns", "Namespace Entries"),
    
    PROFILE("queryall.namespaceProfiles", "profile", "Profiles"),
    
    PROJECT("queryall.namespaceProjects", "project", "Projects"),
    
    PROVENANCE("queryall.namespaceProvenanceRecords", "provenance", "Provenance Records"),
    
    PROVIDER("queryall.namespaceProviders", "provider", "Providers"),
    
    QUERY("queryall.namespaceQueryTypes", "query", "Query Types"),
    
    QUERYBUNDLE("queryall.namespaceQueryBundles", "querybundle", "Query Bundles"),
    
    RDFRULE("queryall.namespaceNormalisationRules", "rdfrule", "RDF Normalisation Rules"),
    
    RULETEST("queryall.namespaceRuleTests", "ruletest", "Rule Tests"),
    
    STATISTICS("queryall.namespaceStatistics", "statistics", "Statistics"),
    
    TEMPLATES("queryall.namespaceTemplates", "template", "Templates"),
    
    WEBAPPCONFIG("queryall.namespaceWebApplicationConfiguration", "webapp_configuration",
            "Web Application Configurations");
    
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
                PropertyUtils.getSystemOrPropertyString("queryall.ontologyPrefix", "http://purl.org/queryall/")
                        + this.namespace + PropertyUtils.getSystemOrPropertyString("queryall.ontologySuffix", ":");
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
