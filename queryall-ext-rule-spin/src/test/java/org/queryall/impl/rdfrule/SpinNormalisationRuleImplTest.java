/**
 * 
 */
package org.queryall.impl.rdfrule;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public class SpinNormalisationRuleImplTest
{
    
    private OntModel testOntologyModel;
    private Repository testRepository;
    private List<org.openrdf.model.Statement> testSesameStatements;
//    private SPINModuleRegistry testSpinModuleRegistry1;
//    private SPINModuleRegistry testSpinModuleRegistry2;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        Model testModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
        
        testOntologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, testModel);
        
        List<com.hp.hpl.jena.rdf.model.Statement> jenaStatements = new ArrayList<com.hp.hpl.jena.rdf.model.Statement>(3);
        
        com.hp.hpl.jena.rdf.model.Statement testJenaStatement1 = ResourceFactory.createStatement(ResourceFactory.createResource("http://my.example.org/test/uri/1"), ResourceFactory.createProperty("http://other.example.org/test/property/a1"), ResourceFactory.createTypedLiteral(42));
        com.hp.hpl.jena.rdf.model.Statement testJenaStatement2 = ResourceFactory.createStatement(ResourceFactory.createResource("http://my.example.org/test/uri/1"), RDF.type, ResourceFactory.createResource("http://my.example.org/test/uri/testType"));
        com.hp.hpl.jena.rdf.model.Statement testJenaStatement3 = ResourceFactory.createStatement(ResourceFactory.createResource("http://my.example.org/test/uri/testType"), OWL2.equivalentClass, ResourceFactory.createResource("http://vocab.org/test/equivalentToRuleType1"));

        jenaStatements.add(testJenaStatement1);
        jenaStatements.add(testJenaStatement2);
        jenaStatements.add(testJenaStatement3);
        
        testOntologyModel.add(jenaStatements);

        ValueFactory vf = new ValueFactoryImpl();
        
        org.openrdf.model.Statement testSesameStatement1 = vf.createStatement(vf.createURI("http://my.example.org/test/uri/1"), vf.createURI("http://other.example.org/test/property/a1"), vf.createLiteral(42));
        org.openrdf.model.Statement testSesameStatement2 = vf.createStatement(vf.createURI("http://my.example.org/test/uri/1"), org.openrdf.model.vocabulary.RDF.TYPE, vf.createURI("http://my.example.org/test/uri/testType"));
        org.openrdf.model.Statement testSesameStatement3 = vf.createStatement(vf.createURI("http://my.example.org/test/uri/testType"), OWL.EQUIVALENTCLASS, vf.createURI("http://vocab.org/test/equivalentToRuleType1"));
        
        testSesameStatements = new ArrayList<org.openrdf.model.Statement>(3);
        
        testSesameStatements.add(testSesameStatement1);
        testSesameStatements.add(testSesameStatement2);
        testSesameStatements.add(testSesameStatement3);
        
        testRepository = new SailRepository(new MemoryStore());
        testRepository.initialize();
        
        RepositoryConnection connection = testRepository.getConnection();
        
        connection.add(testSesameStatements);
        connection.commit();
        connection.close();
        
        //SPINThreadFunctionRegistry functionRegistry1 = new SPINThreadFunctionRegistry(FunctionRegistry.standardRegistry());
//        FunctionRegistry functionRegistry1 = FunctionRegistry.standardRegistry();

//        testSpinModuleRegistry1 = new SPINModuleRegistry(functionRegistry1);
        //testSpinModuleRegistry1 = new SPINModuleRegistry();//FunctionRegistry.get());
        
        // TODO: is it rational to have a circular dependency like this?
        //functionRegistry1.setSpinModuleRegistry(testSpinModuleRegistry1);
        
        // TODO: how do we get around this step
        // Jena/ARQ seems to be permanently setup around the use of this global context, 
        // even though FunctionEnv and Context seem to be in quite a few method headers 
        // throughout their code base
//        ARQ.getContext().set(ARQConstants.registryFunctions, functionRegistry1);
        
        //testSpinModuleRegistry1.init();
        
//        SPINThreadFunctionRegistry functionRegistry2 = new SPINThreadFunctionRegistry(FunctionRegistry.standardRegistry());
//
//        testSpinModuleRegistry2 = new SPINModuleRegistry(functionRegistry2);
        
        
        // TODO: is it rational to have a circular dependency like this?
//        functionRegistry2.setSpinModuleRegistry(testSpinModuleRegistry2);
        
        // TODO: how do we get around this step
        // Jena/ARQ seems to be permanently setup around the use of this global context, 
        // even though FunctionEnv and Context seem to be in quite a few method headers 
        // throughout their code base
//        ARQ.getContext().set(ARQConstants.registryFunctions, functionRegistry2);
        
//        testSpinModuleRegistry2.init();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        testOntologyModel = null;
        testSesameStatements = null;
        testRepository = null;
//        testSpinModuleRegistry1 = null;
//        testSpinModuleRegistry2 = null;
    }
    
    /**
     * Test method for {@link org.queryall.impl.rdfrule.SpinUtils#addJenaModelToSesameRepository(com.hp.hpl.jena.rdf.model.Model, org.openrdf.repository.Repository, org.openrdf.model.Resource[])}.
     */
    @Test
    public void testAddJenaModelToSesameRepository() throws OpenRDFException
    {
        Repository results = SpinUtils.addJenaModelToSesameRepository(testOntologyModel, null);
        
        RepositoryConnection resultConnection = results.getConnection();
        
        Assert.assertEquals(3, resultConnection.size());
        
    }
    
    @Test
    public void testProcessSpinRulesByURL() throws OpenRDFException
    {
        RepositoryConnection testRepositoryConnection = testRepository.getConnection();
        
        Assert.assertEquals(3, testRepositoryConnection.size());
        
        SpinNormalisationRuleImpl spinNormalisationRuleImpl = new SpinNormalisationRuleImpl();
        spinNormalisationRuleImpl.setKey("http://test.queryall.org/spin/test/urlimport/1");
        
//        spinNormalisationRuleImpl.setSpinModuleRegistry(testSpinModuleRegistry1);
        spinNormalisationRuleImpl.addUrlImport(new URIImpl("http://topbraid.org/spin/owlrl-all"));
        
        Repository results = spinNormalisationRuleImpl.processSpinRules(testRepository);
        
        RepositoryConnection resultConnection = results.getConnection();
        
        Assert.assertEquals(8, resultConnection.size());
        
        for(Statement nextStatement : testSesameStatements)
        {
            Assert.assertTrue(resultConnection.hasStatement(nextStatement, false));
        }
    }
    
    @Test
    public void testProcessSpinRulesByClasspathRef() throws OpenRDFException
    {
        RepositoryConnection testRepositoryConnection = testRepository.getConnection();
        
        Assert.assertEquals(3, testRepositoryConnection.size());
        
        SpinNormalisationRuleImpl spinNormalisationRuleImpl = new SpinNormalisationRuleImpl();
        spinNormalisationRuleImpl.setKey("http://test.queryall.org/spin/test/localimport/1");
        
        // spinNormalisationRuleImpl.setSpinModuleRegistry(testSpinModuleRegistry1);
        spinNormalisationRuleImpl.addLocalImport("/test/owlrl-all.owl");
        
        Repository results = spinNormalisationRuleImpl.processSpinRules(testRepository);
        
        RepositoryConnection resultConnection = results.getConnection();
        
        Assert.assertEquals(8, resultConnection.size());
        
        for(Statement nextStatement : testSesameStatements)
        {
            Assert.assertTrue(resultConnection.hasStatement(nextStatement, false));
        }
    }

    @Test
    public void testGetTurtleSPINQueryFromSPARQL()
    {
        String query =
                "SELECT ?person\n" +
                "WHERE {\n" +
                "    ?person a <ex:Person> .\n" +
                "    ?person <ex:age> ?age .\n" +
                "    FILTER (?age > 18) .\n" +
                "}";
        
        String turtleString = SpinUtils.getTurtleSPINQueryFromSPARQL(query);
        
        System.out.println(turtleString);
        
        Assert.assertTrue(turtleString.contains("ex:Person"));

        Assert.assertTrue(turtleString.contains("ex:age"));

        Assert.assertTrue(turtleString.contains("18"));
    }
    
}
