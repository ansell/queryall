package org.queryall.api;

import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

public abstract class QueryAllConfiguration 
{
	public abstract String getNamespaceForStatistics();

	public abstract void setNamespaceForStatistics(String currentRdfStatisticsNamespace);

	public abstract String getNamespaceForProvenance();

	public abstract void setNamespaceForProvenance(String currentRdfProvenanceNamespace);

	public abstract String getNamespaceForProfile();

	public abstract void setNamespaceForProfile(String currentRdfProfileNamespace);

	public abstract String getNamespaceForNamespaceEntry();

	public abstract void setNamespaceForNamespaceEntry(String currentRdfNamespaceNamespace);

	public abstract String getNamespaceForRuleTest();

	public abstract void setNamespaceForRuleTest(String currentRdfRuleTestNamespace);

	public abstract String getNamespaceForNormalisationRule();

	public abstract void setNamespaceForNormalisationRule(String currentRdfNormalisationRuleNamespace);

	public abstract String getNamespaceForQueryBundle();

	public abstract void setNamespaceForQueryBundle(String currentRdfQueryBundleNamespace);

	public abstract String getNamespaceForQueryType();

	public abstract void setNamespaceForQueryType(String currentRdfQueryNamespace);

	public abstract String getNamespaceForTemplate();

	public abstract void setNamespaceForTemplate(String currentRdfTemplateNamespace);

	public abstract String getNamespaceForProvider();

	public abstract void setNamespaceForProvider(String currentRdfProviderNamespace);

	public abstract String getNamespaceForProject();

	public abstract void setNamespaceForProject(String currentRdfProjectNamespace);

	public abstract String getNamespaceForWebappConfiguration();

	public abstract void setNamespaceForWebappConfiguration(String currentRdfWebappConfigurationNamespace);

	
	
	public abstract String getOntologyTermUriSuffix();

	public abstract void setOntologyTermUriSuffix(String currentOntologyTermUriSuffix);

	public abstract String getOntologyTermUriPrefix();

	public abstract void setOntologyTermUriPrefix(String currentOntologyTermUriPrefix);

	
	
	public abstract Map<URI, NamespaceEntry> getAllNamespaceEntries();

	public abstract Map<URI, NormalisationRule> getAllNormalisationRules();

	public abstract Map<URI, Profile> getAllProfiles();

	public abstract Map<URI, Provider> getAllProviders();

	public abstract Map<URI, QueryType> getAllQueryTypes();

	public abstract Map<URI, RuleTest> getAllRuleTests();


	
	public abstract Map<URI, NamespaceEntry> getNamespaceEntries(Repository myRepository);

	public abstract Map<URI, NormalisationRule> getNormalisationRules(Repository myRepository);

	public abstract Map<URI, Profile> getProfiles(Repository myRepository);

    public abstract Map<URI, Provider> getProviders(Repository myRepository);

	public abstract Map<URI, QueryType> getQueryTypes(Repository myRepository);

	public abstract Map<URI, RuleTest> getRuleTests(Repository myRepository);

}