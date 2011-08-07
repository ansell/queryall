package org.queryall.api;

import java.util.Collection;
import java.util.Map;

import org.openrdf.model.URI;

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
	
	void addNamespaceEntry(NamespaceEntry nextNamespaceEntry);
	
	Map<URI, NormalisationRule> getAllNormalisationRules();

	void addNormalisationRule(NormalisationRule nextNormalisationRule);
	
	Map<URI, Profile> getAllProfiles();

	void addProfile(Profile nextProfile);
	
	Map<URI, Provider> getAllProviders();

	void addProvider(Provider nextProvider);
	
	Map<URI, QueryType> getAllQueryTypes();

	void addQueryType(QueryType nextQueryType);
	
	Map<URI, RuleTest> getAllRuleTests();

	void addRuleTest(RuleTest nextRuleTest);

	boolean getBooleanProperty(String propertyKey, boolean defaultValue);

	public abstract String getStringProperty(String key, String defaultValue);

	public abstract long getLongProperty(String key, long defaultValue);

	public abstract URI getURIProperty(String key, URI defaultValue);

	public abstract int getIntProperty(String key, int defaultValue);

	public abstract float getFloatProperty(String key, float defaultValue);

	Map<String, Collection<URI>> getNamespacePrefixesToUris();
	
}