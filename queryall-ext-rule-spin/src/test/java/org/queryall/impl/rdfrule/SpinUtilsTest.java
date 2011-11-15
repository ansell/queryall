/**
 * 
 */
package org.queryall.impl.rdfrule;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.exception.QueryAllException;

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
public class SpinUtilsTest
{
    
    private OntModel testOntologyModel;
    private ArrayList<Statement> testSesameStatements;
    private Repository testRepository;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        final Model testModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
        
        this.testOntologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, testModel);
        
        final List<com.hp.hpl.jena.rdf.model.Statement> jenaStatements =
                new ArrayList<com.hp.hpl.jena.rdf.model.Statement>(3);
        
        final com.hp.hpl.jena.rdf.model.Statement testJenaStatement1 =
                ResourceFactory.createStatement(ResourceFactory.createResource("http://my.example.org/test/uri/1"),
                        ResourceFactory.createProperty("http://other.example.org/test/property/a1"),
                        ResourceFactory.createTypedLiteral(42));
        final com.hp.hpl.jena.rdf.model.Statement testJenaStatement2 =
                ResourceFactory.createStatement(ResourceFactory.createResource("http://my.example.org/test/uri/1"),
                        RDF.type, ResourceFactory.createResource("http://my.example.org/test/uri/testType"));
        final com.hp.hpl.jena.rdf.model.Statement testJenaStatement3 =
                ResourceFactory.createStatement(
                        ResourceFactory.createResource("http://my.example.org/test/uri/testType"),
                        OWL2.equivalentClass,
                        ResourceFactory.createResource("http://vocab.org/test/equivalentToRuleType1"));
        
        jenaStatements.add(testJenaStatement1);
        jenaStatements.add(testJenaStatement2);
        jenaStatements.add(testJenaStatement3);
        
        this.testOntologyModel.add(jenaStatements);
        
        final ValueFactory vf = new ValueFactoryImpl();
        
        final org.openrdf.model.Statement testSesameStatement1 =
                vf.createStatement(vf.createURI("http://my.example.org/test/uri/1"),
                        vf.createURI("http://other.example.org/test/property/a1"), vf.createLiteral(42));
        final org.openrdf.model.Statement testSesameStatement2 =
                vf.createStatement(vf.createURI("http://my.example.org/test/uri/1"),
                        org.openrdf.model.vocabulary.RDF.TYPE, vf.createURI("http://my.example.org/test/uri/testType"));
        final org.openrdf.model.Statement testSesameStatement3 =
                vf.createStatement(vf.createURI("http://my.example.org/test/uri/testType"), OWL.EQUIVALENTCLASS,
                        vf.createURI("http://vocab.org/test/equivalentToRuleType1"));
        
        this.testSesameStatements = new ArrayList<org.openrdf.model.Statement>(3);
        
        this.testSesameStatements.add(testSesameStatement1);
        this.testSesameStatements.add(testSesameStatement2);
        this.testSesameStatements.add(testSesameStatement3);
        
        this.testRepository = new SailRepository(new MemoryStore());
        this.testRepository.initialize();
        
        final RepositoryConnection connection = this.testRepository.getConnection();
        
        connection.add(this.testSesameStatements);
        connection.commit();
        connection.close();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.testOntologyModel = null;
        this.testSesameStatements = null;
        this.testRepository = null;
    }
    
    /**
     * Test method for
     * {@link org.queryall.impl.rdfrule.SpinUtils#addJenaModelToSesameRepository(com.hp.hpl.jena.rdf.model.Model, org.openrdf.repository.Repository, org.openrdf.model.Resource[])}
     * .
     * 
     * @throws QueryAllException
     */
    @Test
    public void testAddJenaModelToSesameRepositoryNull() throws OpenRDFException, QueryAllException
    {
        final Repository results = SpinUtils.addJenaModelToSesameRepository(this.testOntologyModel, null);
        
        final RepositoryConnection resultConnection = results.getConnection();
        
        Assert.assertEquals(3, resultConnection.size());
        
        RepositoryConnection connection = this.testRepository.getConnection();
        
        // Verify that each of the statements in the result are located in our copy of the test repository
        for(Statement nextStatement : resultConnection.getStatements(null, null, null, false).asList())
        {
            Assert.assertTrue(connection.hasStatement(nextStatement, false));
        }
    }
    
    /**
     * Test method for {@link org.queryall.impl.rdfrule.SpinUtils#addSesameRepositoryToJenaModel(org.openrdf.repository.Repository, com.hp.hpl.jena.rdf.model.Model, java.lang.String, org.openrdf.model.Resource[])}.
     */
    @Test
    public void testAddSesameRepositoryToJenaModel()
    {
        OntModel addSesameRepositoryToJenaModel = SpinUtils.addSesameRepositoryToJenaModel(testRepository, null, "");
        
        Assert.assertEquals(testOntologyModel.size(), addSesameRepositoryToJenaModel.size());
        
        // verify that the given model matches our local test model
        Assert.assertTrue(addSesameRepositoryToJenaModel.isIsomorphicWith(testOntologyModel));
    }
    
    @Test
    public void testGetTurtleSPINQueryFromSPARQL()
    {
        final String query =
                "SELECT ?person\n" + "WHERE {\n" + "    ?person a <ex:Person> .\n" + "    ?person <ex:age> ?age .\n"
                        + "    FILTER (?age > 18) .\n" + "}";
        
        final String turtleString = SpinUtils.getTurtleSPINQueryFromSPARQL(query);
        
        System.out.println(turtleString);
        
        Assert.assertTrue(turtleString.contains("ex:Person"));
        
        Assert.assertTrue(turtleString.contains("ex:age"));
        
        Assert.assertTrue(turtleString.contains("18"));
    }

    /**
     * Test method for {@link org.queryall.impl.rdfrule.SpinUtils#loadModelFromClasspath(java.lang.String)}.
     */
    @Test
    public void testLoadModelFromClasspath()
    {
        final OntModel nextModel = SpinUtils.loadModelFromClasspath("test/owlrl-all");
        
        Assert.assertEquals(3324, nextModel.size());
    }
    
    /**
     * Test method for {@link org.queryall.impl.rdfrule.SpinUtils#loadModelFromUrl(java.lang.String)}.
     */
    @Test
    public void testLoadModelFromUrl()
    {
        final OntModel nextModel = SpinUtils.loadModelFromUrl("http://topbraid.org/spin/owlrl-all");
        
        Assert.assertEquals(3324, nextModel.size());
    }
    
}
