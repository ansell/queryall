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
import org.queryall.api.QueryAllConfiguration;
import org.queryall.api.RuleTest;
import org.queryall.query.Settings;
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
    public static final boolean _TRACE = RuleTesterServlet.log.isTraceEnabled();
    public static final boolean _DEBUG = RuleTesterServlet.log.isDebugEnabled();
    public static final boolean _INFO = RuleTesterServlet.log.isInfoEnabled();
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException
    {
        final QueryAllConfiguration localSettings = Settings.getSettings();
        // Settings.setServletContext(getServletConfig().getServletContext());
        
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
        
        if(!RuleUtils.runRuleTests(allRuleTests.values(), localSettings.getAllNormalisationRules()))
        {
            allTestsPassed = false;
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
