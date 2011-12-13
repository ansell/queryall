/**
 * 
 */
package org.queryall.utils.test;

import org.junit.After;
import org.junit.Before;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.test.AbstractQueryAllConfigurationTest;
import org.queryall.api.test.DummyNormalisationRule;
import org.queryall.api.test.DummyProvider;
import org.queryall.impl.namespace.NamespaceEntryImpl;
import org.queryall.impl.profile.ProfileImpl;
import org.queryall.impl.querytype.NoInputQueryTypeImpl;
import org.queryall.impl.ruletest.StringRuleTestImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public abstract class BaseQueryAllConfigurationTest extends AbstractQueryAllConfigurationTest
{
    @Override
    protected final NamespaceEntry getNewNamespaceEntry()
    {
        // TODO: replace this with dummy class
        return new NamespaceEntryImpl();
    }
    
    @Override
    protected final NormalisationRule getNewNormalisationRule()
    {
        return new DummyNormalisationRule();
    }
    
    @Override
    protected final Profile getNewProfile()
    {
        // TODO: replace this with dummy class
        return new ProfileImpl();
    }
    
    @Override
    protected final Provider getNewProvider()
    {
        return new DummyProvider();
    }
    
    @Override
    protected final QueryType getNewQueryType()
    {
        // TODO: replace this with dummy class
        return new NoInputQueryTypeImpl();
    }
    
    @Override
    protected final RuleTest getNewRuleTest()
    {
        // TODO: replace this with dummy class
        return new StringRuleTestImpl();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
    }
    
}
