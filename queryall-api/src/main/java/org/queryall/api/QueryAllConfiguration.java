package org.queryall.api;

import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

public abstract class QueryAllConfiguration 
{
	public abstract String getNamespaceForStatistics();

	public abstract void setNamespaceForStatistics(String current_RDF_STATISTICS_NAMESPACE);

	public abstract String getNamespaceForProvenance();

	public abstract void setNamespaceForProvenance(String current_RDF_PROVENANCE_NAMESPACE);

	public abstract String getNamespaceForProfile();

	public abstract void setNamespaceForProfile(String current_RDF_PROFILE_NAMESPACE);

	public abstract String getNamespaceForNamespaceEntry();

	public abstract void setNamespaceForNamespaceEntry(String current_RDF_NAMESPACEENTRY_NAMESPACE);

	public abstract String getNamespaceForRuleTest();

	public abstract void setNamespaceForRuleTest(String current_RDF_RULETEST_NAMESPACE);

	public abstract String getNamespaceForNormalisationRule();

	public abstract void setNamespaceForNormalisationRule(String current_RDF_RDFRULE_NAMESPACE);

	public abstract String getNamespaceForQueryBundle();

	public abstract void setNamespaceForQueryBundle(String current_RDF_QUERYBUNDLE_NAMESPACE);

	public abstract String getNamespaceForQueryType();

	public abstract void setNamespaceForQueryType(String current_RDF_QUERY_NAMESPACE);

	public abstract String getNamespaceForTemplate();

	public abstract void setNamespaceForTemplate(String current_RDF_TEMPLATE_NAMESPACE);

	public abstract String getNamespaceForProvider();

	public abstract void setNamespaceForProvider(String current_RDF_PROVIDER_NAMESPACE);

	public abstract String getNamespaceForProject();

	public abstract void setNamespaceForProject(String rdfProjectNamespace);

	public abstract String getNamespaceForWebappConfiguration();

	public abstract void setNamespaceForWebappConfiguration(String rdfWebappConfigurationNamespace);

	public abstract String getOntologyTermUriSuffix();

	public abstract void setOntologyTermUriSuffix(String current_ONTOLOGYTERMURI_SUFFIX);

	public abstract String getOntologyTermUriPrefix();

	public abstract void setOntologyTermUriPrefix(String ontologyTermUriPrefix);

	
	
	public abstract Map<URI, NamespaceEntry> getAllNamespaceEntries();

	public abstract Map<URI, NormalisationRule> getAllNormalisationRules();

	public abstract Map<URI, Profile> getAllProfiles();

	public abstract Map<URI, Provider> getAllProviders();

	public abstract Map<URI, QueryType> getAllQueryTypes();

	public abstract Map<URI, RuleTest> getAllRuleTests();

	public abstract Map<URI, Template> getAllTemplates();

	

	public abstract Map<URI, NamespaceEntry> getNamespaceEntries(Repository myRepository);

	public abstract Map<URI, NormalisationRule> getNormalisationRules(Repository myRepository);

	public abstract Map<URI, Profile> getProfiles(Repository myRepository);

    public abstract Map<URI, Provider> getProviders(Repository myRepository);

	public abstract Map<URI, QueryType> getQueryTypes(Repository myRepository);

	public abstract Map<URI, RuleTest> getRuleTests(Repository myRepository);

	public abstract Map<URI, Template> getTemplates(Repository myRepository);

}