package org.queryall.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.model.URI;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.exception.QueryAllException;
import org.queryall.exception.QueryAllRuntimeException;
import org.queryall.servlets.helpers.SettingsContextListener;
import org.queryall.servlets.queryparsers.RuleTesterQueryOptions;
import org.queryall.utils.RuleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * 
 */

public class RuleTesterServlet extends HttpServlet
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 7617736644136389429L;
    public static final Logger log = LoggerFactory.getLogger(RuleTesterServlet.class);
    public static final boolean TRACE = RuleTesterServlet.log.isTraceEnabled();
    public static final boolean DEBUG = RuleTesterServlet.log.isDebugEnabled();
    public static final boolean INFO = RuleTesterServlet.log.isInfoEnabled();
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException
    {
        final QueryAllConfiguration localSettings =
                (QueryAllConfiguration)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_CONFIG);
        
        RuleTesterServlet.log.debug("testUri parameter="
                + request.getAttribute("org.queryall.RuleTesterServlet.testUri"));
        
        final RuleTesterQueryOptions requestRuleTesterQueryOptions =
                new RuleTesterQueryOptions((String)request.getAttribute("org.queryall.RuleTesterServlet.testUri"));
        
        final PrintWriter out = response.getWriter();
        
        String methodToTest = "";
        
        if(requestRuleTesterQueryOptions.hasTestUri())
        {
            methodToTest = requestRuleTesterQueryOptions.getTestUri();
        }
        
        RuleTesterServlet.log.debug("test-regexmethods: testuri=" + methodToTest);
        
        final Map<URI, RuleTest> allRuleTests = localSettings.getAllRuleTests();
        
        boolean allTestsPassed = true;
        
        @SuppressWarnings("unused")
        final List<String> automatedTestResults = new ArrayList<String>();
        
        try
        {
            if(!RuleUtils.runRuleTests(allRuleTests.values(), localSettings.getAllNormalisationRules()))
            {
                allTestsPassed = false;
            }
        }
        catch(final QueryAllException e)
        {
            allTestsPassed = false;
            RuleTesterServlet.log.error("Found queryall checked exception while running rule tests", e);
        }
        catch(final QueryAllRuntimeException e)
        {
            allTestsPassed = false;
            RuleTesterServlet.log.error("Found queryall runtime exception while running rule tests", e);
        }
        catch(final RuntimeException e)
        {
            allTestsPassed = false;
            RuleTesterServlet.log.error("Found unknown runtime exception while running rule tests", e);
        }
        
        if(!allTestsPassed)
        {
            out.write("<h1><span class='error'>Test Failure occured</span></h1>");
        }
        else
        {
            out.write("<h1><span class='info'>All Tests passed</span></h1>");
        }
    }
    
}
