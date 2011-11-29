package org.bio2rdf.test;

/**
 * 
 */

import net.sourceforge.jwebunit.junit.WebTester;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstracts the WebTester setup and tear down actions from test implementations so they will always
 * have a new tester and not have to deal with opening and closing issues.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractQueryAllWebTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractQueryAllWebTest.class);
    
    private WebTester tester;
    
    protected abstract String getBaseUrl();
    
    protected abstract String getBeginAtPath();
    
    /**
     * 
     * @return The WebTester object for the current test
     */
    protected WebTester getWebTester()
    {
        return this.tester;
    }
    
    @Before
    public void setUp() throws Exception
    {
        this.tester = new WebTester();
        this.tester.setBaseUrl(this.getBaseUrl());
        // NOTE: Do not remove the following. Apparently it is necessary to initialise some
        // variables that are not initialised earlier, and will cause unsightly
        // NullPointerExceptions if not done before the realistically named gotoPage
        this.tester.beginAt(this.getBeginAtPath());
    }
    
    /**
     * 
     * @param tester
     *            The new WebTester instance for the next test about to be executed
     */
    private void setWebTester(final WebTester tester)
    {
        this.tester = tester;
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        // NOTE: enabling this causes a NullPointerException when trying to close the browser, so
        // just close the browser instead
        // try
        // {
        // this.getWebTester().closeWindow();
        // }
        // catch(final Exception ex)
        // {
        // AbstractOASIntegrationTest.LOGGER.error("Found exception closing window after test", ex);
        // }
        try
        {
            this.getWebTester().closeBrowser();
        }
        catch(final Exception ex)
        {
            AbstractQueryAllWebTest.LOGGER.error("Found exception closing browser after test", ex);
        }
        finally
        {
            this.tester = null;
        }
    }
}
