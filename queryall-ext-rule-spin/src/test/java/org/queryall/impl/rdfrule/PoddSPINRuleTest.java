/**
 * 
 */
package org.queryall.impl.rdfrule;

import java.io.InputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.exception.QueryAllException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.ReificationStyle;

/**
 * Tests using SPIN for validation and inferencing on PODD documents to see if SPIN/QueryAll can be
 * a viable quick replacement for the current monolithic PODD software
 * 
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class PoddSPINRuleTest
{
    private static final Logger log = LoggerFactory.getLogger(PoddSPINRuleTest.class);
    
    private OntModel testOntologyModel;
    private Repository testRepository;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        final Model testModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
        
        this.testOntologyModel = ModelFactory.createOntologyModel(SpinUtils.getOntModelSpec(), testModel);
        
        final InputStream testInputStream = PoddSPINRuleTest.class.getResourceAsStream("/test/minimal-podd.rdf");
        
        // Use Jena OntModel to pull in all of the Ontology dependencies
        testModel.read(testInputStream, "", "RDF/XML");
        
        Assert.assertEquals(9, this.testOntologyModel.size());
        
        this.testRepository = SpinUtils.addJenaModelToSesameRepository(this.testOntologyModel, null);
        
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown()
    {
        this.testOntologyModel.close();
        this.testOntologyModel = null;
        
        try
        {
            this.testRepository.shutDown();
        }
        catch(final RepositoryException e)
        {
            PoddSPINRuleTest.log.error("Found unexpected RepositoryException", e);
        }
        
        this.testRepository = null;
    }
    
    @Test
    public void test() throws RepositoryException, QueryAllException
    {
        RepositoryConnection testRepositoryConnection = null;
        RepositoryConnection resultConnection = null;
        
        try
        {
            testRepositoryConnection = this.testRepository.getConnection();
            
            Assert.assertEquals(9, testRepositoryConnection.size());
            
            final SpinConstraintRuleImpl spinNormalisationRuleImpl = new SpinConstraintRuleImpl();
            spinNormalisationRuleImpl.setKey("http://test.queryall.org/spin/test/urlimport/1");
            
            // spinNormalisationRuleImpl.setSpinModuleRegistry(testSpinModuleRegistry1);
            spinNormalisationRuleImpl.addUrlImport(new URIImpl("http://topbraid.org/spin/owlrl-all"));
            
            final Repository results = spinNormalisationRuleImpl.processSpinRules(this.testRepository);
            
            resultConnection = results.getConnection();
            
            if(log.isDebugEnabled())
            {
                for(final Statement nextStatement : resultConnection.getStatements(null, null, null, true).asList())
                {
                    PoddSPINRuleTest.log.debug(nextStatement.toString());
                }
            }
            
            Assert.assertEquals(111, resultConnection.size());
        }
        finally
        {
            if(testRepositoryConnection != null)
            {
                testRepositoryConnection.close();
            }
            
            if(resultConnection != null)
            {
                resultConnection.close();
            }
        }
    }
    
}
