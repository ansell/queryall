package org.queryall.api;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import org.openrdf.model.Statement;
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

	String getStringProperty(String key, String defaultValue);

	long getLongProperty(String key, long defaultValue);

	URI getURIProperty(String key, URI defaultValue);

	int getIntProperty(String key, int defaultValue);

	float getFloatProperty(String key, float defaultValue);

	Map<String, Collection<URI>> getNamespacePrefixesToUris();

	Collection<URI> getURIProperties(String string);

	String getDefaultHostAddress();

	Pattern getPlainNamespacePattern();

	Pattern getPlainNamespaceAndIdentifierPattern();

	Collection<String> getStringProperties(String string);

	Collection<Statement> getStatementProperties(String string);

	Pattern getTagPattern();
	
}