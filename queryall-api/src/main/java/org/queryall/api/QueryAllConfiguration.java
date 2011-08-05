package org.queryall.api;

import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface QueryAllConfiguration 
{
	String getNamespaceForStatistics();

	void setNamespaceForStatistics(String currentRdfStatisticsNamespace);

	String getNamespaceForProvenance();

	void setNamespaceForProvenance(String currentRdfProvenanceNamespace);

	String getNamespaceForProfile();

	void setNamespaceForProfile(String currentRdfProfileNamespace);

	String getNamespaceForNamespaceEntry();

	void setNamespaceForNamespaceEntry(String currentRdfNamespaceNamespace);

	String getNamespaceForRuleTest();

	void setNamespaceForRuleTest(String currentRdfRuleTestNamespace);

	String getNamespaceForNormalisationRule();

	void setNamespaceForNormalisationRule(String currentRdfNormalisationRuleNamespace);

	String getNamespaceForQueryBundle();

	void setNamespaceForQueryBundle(String currentRdfQueryBundleNamespace);

	String getNamespaceForQueryType();

	void setNamespaceForQueryType(String currentRdfQueryNamespace);

	String getNamespaceForTemplate();

	void setNamespaceForTemplate(String currentRdfTemplateNamespace);

	String getNamespaceForProvider();

	void setNamespaceForProvider(String currentRdfProviderNamespace);

	String getNamespaceForProject();

	void setNamespaceForProject(String currentRdfProjectNamespace);

	String getNamespaceForWebappConfiguration();

	void setNamespaceForWebappConfiguration(String currentRdfWebappConfigurationNamespace);

	
	
	String getOntologyTermUriSuffix();

	void setOntologyTermUriSuffix(String currentOntologyTermUriSuffix);

	String getOntologyTermUriPrefix();

	void setOntologyTermUriPrefix(String currentOntologyTermUriPrefix);

	
	
	Map<URI, NamespaceEntry> getAllNamespaceEntries();

	Map<URI, NormalisationRule> getAllNormalisationRules();

	Map<URI, Profile> getAllProfiles();

	Map<URI, Provider> getAllProviders();

	Map<URI, QueryType> getAllQueryTypes();

	Map<URI, RuleTest> getAllRuleTests();


	
	Map<URI, NamespaceEntry> getNamespaceEntries(Repository myRepository);

	Map<URI, NormalisationRule> getNormalisationRules(Repository myRepository);

	Map<URI, Profile> getProfiles(Repository myRepository);

    Map<URI, Provider> getProviders(Repository myRepository);

	Map<URI, QueryType> getQueryTypes(Repository myRepository);

	Map<URI, RuleTest> getRuleTests(Repository myRepository);

}