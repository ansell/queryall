/**
 * 
 */
package org.queryall.api.services;

import java.util.Collection;

import org.queryall.api.namespace.NamespaceEntryEnum;
import org.queryall.api.namespace.NamespaceEntryFactory;
import org.queryall.api.namespace.NamespaceEntryParser;
import org.queryall.api.namespace.NamespaceEntryRegistry;
import org.queryall.api.profile.ProfileEnum;
import org.queryall.api.profile.ProfileFactory;
import org.queryall.api.profile.ProfileParser;
import org.queryall.api.profile.ProfileRegistry;
import org.queryall.api.project.ProjectEnum;
import org.queryall.api.project.ProjectFactory;
import org.queryall.api.project.ProjectParser;
import org.queryall.api.project.ProjectRegistry;
import org.queryall.api.provider.ProviderEnum;
import org.queryall.api.provider.ProviderFactory;
import org.queryall.api.provider.ProviderParser;
import org.queryall.api.provider.ProviderRegistry;
import org.queryall.api.querytype.QueryTypeEnum;
import org.queryall.api.querytype.QueryTypeFactory;
import org.queryall.api.querytype.QueryTypeParser;
import org.queryall.api.querytype.QueryTypeRegistry;
import org.queryall.api.rdfrule.NormalisationRuleEnum;
import org.queryall.api.rdfrule.NormalisationRuleFactory;
import org.queryall.api.rdfrule.NormalisationRuleParser;
import org.queryall.api.rdfrule.NormalisationRuleRegistry;
import org.queryall.api.ruletest.RuleTestEnum;
import org.queryall.api.ruletest.RuleTestFactory;
import org.queryall.api.ruletest.RuleTestParser;
import org.queryall.api.ruletest.RuleTestRegistry;
import org.queryall.exception.UnsupportedNamespaceEntryException;
import org.queryall.exception.UnsupportedNormalisationRuleException;
import org.queryall.exception.UnsupportedProfileException;
import org.queryall.exception.UnsupportedProjectException;
import org.queryall.exception.UnsupportedProviderException;
import org.queryall.exception.UnsupportedQueryTypeException;
import org.queryall.exception.UnsupportedRuleTestException;

/**
 * Provides helper methods to interact with the various dynamic services in QueryAll
 * 
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ServiceUtils
{
    public static Collection<QueryAllEnum> getAllEnums()
    {
        return EnumServiceLoader.getInstance().getAll();
    }
    
    /**
     * Creates a namespace entry parser for the given namespace entry enum
     * 
     * @param namespaceEntry
     * @return
     * @throws UnsupportedNamespaceEntryException
     */
    public static NamespaceEntryParser createNamespaceEntryParser(final NamespaceEntryEnum namespaceEntry)
        throws UnsupportedNamespaceEntryException
    {
        final NamespaceEntryFactory factory = NamespaceEntryRegistry.getInstance().get(namespaceEntry);
        
        if(factory != null)
        {
            return factory.getParser();
        }
        
        throw new UnsupportedNamespaceEntryException("No factory available for namespace entry " + namespaceEntry);
    }
    
    /**
     * Creates a normalisation rule parser for the given normalisation rule enum
     * 
     * @param normalisationRule
     * @return
     * @throws UnsupportedNormalisationRuleException
     */
    public static NormalisationRuleParser createNormalisationRuleParser(final NormalisationRuleEnum normalisationRule)
        throws UnsupportedNormalisationRuleException
    {
        final NormalisationRuleFactory factory = NormalisationRuleRegistry.getInstance().get(normalisationRule);
        
        if(factory != null)
        {
            return factory.getParser();
        }
        
        throw new UnsupportedNormalisationRuleException("No factory available for normalisation rule "
                + normalisationRule);
    }
    
    /**
     * Creates a profile parser for the given profile enum
     * 
     * @param profile
     * @return
     * @throws UnsupportedProfileException
     */
    public static ProfileParser createProfileParser(final ProfileEnum profile) throws UnsupportedProfileException
    {
        final ProfileFactory factory = ProfileRegistry.getInstance().get(profile);
        
        if(factory != null)
        {
            return factory.getParser();
        }
        
        throw new UnsupportedProfileException("No factory available for profile " + profile);
    }
    
    /**
     * Creates a project parser for the given project enum
     * 
     * @param project
     * @return
     * @throws UnsupportedProjectException
     */
    public static ProjectParser createProjectParser(final ProjectEnum project) throws UnsupportedProjectException
    {
        final ProjectFactory factory = ProjectRegistry.getInstance().get(project);
        
        if(factory != null)
        {
            return factory.getParser();
        }
        
        throw new UnsupportedProjectException("No factory available for project " + project);
    }
    
    /**
     * Creates a provider parser for the given provider enum
     * 
     * @param provider
     * @return
     * @throws UnsupportedProviderException
     */
    public static ProviderParser createProviderParser(final ProviderEnum provider) throws UnsupportedProviderException
    {
        final ProviderFactory factory = ProviderRegistry.getInstance().get(provider);
        
        if(factory != null)
        {
            return factory.getParser();
        }
        
        throw new UnsupportedProviderException("No factory available for provider " + provider);
    }
    
    /**
     * Creates a query type parser for the given query type enum
     * 
     * @param queryType
     * @return
     * @throws UnsupportedQueryTypeException
     */
    public static QueryTypeParser createQueryTypeParser(final QueryTypeEnum queryType)
        throws UnsupportedQueryTypeException
    {
        final QueryTypeFactory factory = QueryTypeRegistry.getInstance().get(queryType);
        
        if(factory != null)
        {
            return factory.getParser();
        }
        
        throw new UnsupportedQueryTypeException("No factory available for query type " + queryType);
    }
    
    /**
     * Creates a rule test parser for the given rule test enum
     * 
     * @param ruleTest
     * @return
     * @throws UnsupportedRuleTestException
     */
    public static RuleTestParser createRuleTestParser(final RuleTestEnum ruleTest) throws UnsupportedRuleTestException
    {
        final RuleTestFactory factory = RuleTestRegistry.getInstance().get(ruleTest);
        
        if(factory != null)
        {
            return factory.getParser();
        }
        
        throw new UnsupportedRuleTestException("No factory available for rule test " + ruleTest);
    }
    
    /**
	 * 
	 */
    private ServiceUtils()
    {
        
    }
}
