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
import org.queryall.exception.QueryAllException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.LocationMapper;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class SpinNormalisationRuleImplTest
{
    private static final Logger log = LoggerFactory.getLogger(SpinNormalisationRuleImplTest.class);
    
    private OntModel testOntologyModel;
    private Repository testRepository;
    private List<org.openrdf.model.Statement> testSesameStatements;
    private LocationMapper originalLocationMapper;
    private FileManager originalFileManager;
    
    // private SPINModuleRegistry testSpinModuleRegistry1;
    // private SPINModuleRegistry testSpinModuleRegistry2;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        // store a reference to the original locationMapper here so we can push it back after each test
        this.originalLocationMapper = LocationMapper.get();
        this.originalFileManager = FileManager.get();
        
        OntModel testLocationMapping = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
        
        testLocationMapping.read(SpinNormalisationRuleImplTest.class.getResourceAsStream("/test/test-location-mapping.n3"), "", "N3");
        
        // create a new LocationMapper and set it to initialise from the local mapping file
        LocationMapper lMap = new LocationMapper() ;

        lMap.processConfig(testLocationMapping);
        
        log.info("locationMapper="+lMap.toString());
        
        LocationMapper.setGlobalLocationMapper(lMap);
        
        FileManager testFileManager = new FileManager(lMap);
        
        FileManager.setGlobalFileManager(testFileManager);
        
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
        
        // SPINThreadFunctionRegistry functionRegistry1 = new
        // SPINThreadFunctionRegistry(FunctionRegistry.standardRegistry());
        // FunctionRegistry functionRegistry1 = FunctionRegistry.standardRegistry();
        
        // testSpinModuleRegistry1 = new SPINModuleRegistry(functionRegistry1);
        // testSpinModuleRegistry1 = new SPINModuleRegistry();//FunctionRegistry.get());
        
        // TODO: is it rational to have a circular dependency like this?
        // functionRegistry1.setSpinModuleRegistry(testSpinModuleRegistry1);
        
        // TODO: how do we get around this step
        // Jena/ARQ seems to be permanently setup around the use of this global context,
        // even though FunctionEnv and Context seem to be in quite a few method headers
        // throughout their code base
        // ARQ.getContext().set(ARQConstants.registryFunctions, functionRegistry1);
        
        // testSpinModuleRegistry1.init();
        
        // SPINThreadFunctionRegistry functionRegistry2 = new
        // SPINThreadFunctionRegistry(FunctionRegistry.standardRegistry());
        //
        // testSpinModuleRegistry2 = new SPINModuleRegistry(functionRegistry2);
        
        // TODO: is it rational to have a circular dependency like this?
        // functionRegistry2.setSpinModuleRegistry(testSpinModuleRegistry2);
        
        // TODO: how do we get around this step
        // Jena/ARQ seems to be permanently setup around the use of this global context,
        // even though FunctionEnv and Context seem to be in quite a few method headers
        // throughout their code base
        // ARQ.getContext().set(ARQConstants.registryFunctions, functionRegistry2);
        
        // testSpinModuleRegistry2.init();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        LocationMapper.setGlobalLocationMapper(this.originalLocationMapper);
        FileManager.setGlobalFileManager(this.originalFileManager);
        
        this.testOntologyModel = null;
        this.testSesameStatements = null;
        this.testRepository = null;
        // testSpinModuleRegistry1 = null;
        // testSpinModuleRegistry2 = null;
    }
    
    
    @Test
    public void testProcessSpinRulesByClasspathRef() throws OpenRDFException, QueryAllException
    {
        final RepositoryConnection testRepositoryConnection = this.testRepository.getConnection();
        
        Assert.assertEquals(3, testRepositoryConnection.size());
        
        final SpinConstraintRuleImpl spinNormalisationRuleImpl = new SpinConstraintRuleImpl();
        spinNormalisationRuleImpl.setKey("http://test.queryall.org/spin/test/localimport/1");
        
        
        // spinNormalisationRuleImpl.setSpinModuleRegistry(testSpinModuleRegistry1);
        spinNormalisationRuleImpl.addLocalImport("/test/owlrl-all");
        
        final Repository results = spinNormalisationRuleImpl.processSpinRules(this.testRepository);
        
        final RepositoryConnection resultConnection = results.getConnection();
        
        Assert.assertEquals(8, resultConnection.size());
        
        for(final Statement nextStatement : this.testSesameStatements)
        {
            Assert.assertTrue(resultConnection.hasStatement(nextStatement, false));
        }
    }
    
    @Test
    public void testProcessSpinRulesByURL() throws OpenRDFException, QueryAllException
    {
        final RepositoryConnection testRepositoryConnection = this.testRepository.getConnection();
        
        Assert.assertEquals(3, testRepositoryConnection.size());
        
        final SpinConstraintRuleImpl spinNormalisationRuleImpl = new SpinConstraintRuleImpl();
        spinNormalisationRuleImpl.setKey("http://test.queryall.org/spin/test/urlimport/1");
        
        // spinNormalisationRuleImpl.setSpinModuleRegistry(testSpinModuleRegistry1);
        spinNormalisationRuleImpl.addUrlImport(new URIImpl("http://topbraid.org/spin/owlrl-all"));
        
        final Repository results = spinNormalisationRuleImpl.processSpinRules(this.testRepository);
        
        final RepositoryConnection resultConnection = results.getConnection();
        
        Assert.assertEquals(8, resultConnection.size());
        
        for(final Statement nextStatement : this.testSesameStatements)
        {
            Assert.assertTrue(resultConnection.hasStatement(nextStatement, false));
        }
    }
    
}
