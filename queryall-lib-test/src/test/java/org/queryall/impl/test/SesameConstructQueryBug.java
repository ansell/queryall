package org.queryall.impl.test;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

public class SesameConstructQueryBug
{
    
    private Repository testRepository;
    private RepositoryConnection testRepositoryConnection;
    private ValueFactory testValueFactory;

    @Before
    public void setUp() throws Exception
    {
        this.testRepository = new SailRepository(new MemoryStore());
        this.testRepository.initialize();
        this.testRepositoryConnection = this.testRepository.getConnection();
        this.testValueFactory = new ValueFactoryImpl();
    }
    
    @After
    public void tearDown() throws Exception
    {
        if(this.testRepositoryConnection != null)
        {
            try
            {
                this.testRepositoryConnection.close();
            }
            catch(final RepositoryException ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                this.testRepositoryConnection = null;
            }
        }
        
        this.testRepository = null;
        this.testValueFactory = null;
    }
    
    @Test
    public void test() throws RepositoryException, QueryEvaluationException, MalformedQueryException
    {
        URI subjectUri = testValueFactory.createURI("http://bio2rdf.org/po:0000198");
        
        URI predicateUri = testValueFactory.createURI("http://bio2rdf.org/po:is_a");
        
        URI objectUri = testValueFactory.createURI("http://bio2rdf.org/po:0009089");
        
        URI normalisedObjectUri = testValueFactory.createURI("http://oas.example.org/plantontology:0009089");
        
        Statement testInputStatement = testValueFactory.createStatement(subjectUri, predicateUri, objectUri);
        
        Statement testOutputStatement = testValueFactory.createStatement(subjectUri, predicateUri, normalisedObjectUri);

        Statement testMappingStatement = testValueFactory.createStatement(normalisedObjectUri, OWL.SAMEAS, objectUri);

        testRepositoryConnection.add(testInputStatement);
        
        testRepositoryConnection.commit();
        
        Assert.assertEquals("The test statement was not added to the repository", 1, testRepositoryConnection.size());
        
        String testQuery = "CONSTRUCT { ?subjectUri ?predicateUri ?normalisedObjectUri .  ?normalisedObjectUri <http://www.w3.org/2002/07/owl#sameAs> ?objectUri .  } WHERE {  ?subjectUri ?predicateUri ?objectUri . filter(isIRI(?objectUri) && strStarts(str(?objectUri), \"http://bio2rdf.org/po:\")) . bind(iri(concat(\"http://oas.example.org/plantontology:\", encode_for_uri(substr(str(?objectUri), 23)))) AS ?normalisedObjectUri)  } ";

        final GraphQueryResult graphResult = testRepositoryConnection.prepareGraphQuery(QueryLanguage.SPARQL, testQuery).evaluate();
        
        int selectedStatements = 0;
        
        Collection<Statement> results = new ArrayList<Statement>(2);

        while(graphResult.hasNext())
        {
            Statement nextStatement = graphResult.next();
            
            results.add(nextStatement);
            selectedStatements++;
        }
        
        Assert.assertEquals("Sesame sparql query bug", 2, selectedStatements);
        
        testRepositoryConnection.add(results);
        
        Assert.assertEquals("Repository did not contain the expected number of statements", 3, testRepositoryConnection.size());

        for(Statement nextResutStatement : testRepositoryConnection.getStatements(null, null, null, false).asList())
        {
            System.out.println(nextResutStatement.toString());
        }
        

        
        Assert.assertTrue("Repository did not include test input statement", testRepositoryConnection.hasStatement(testInputStatement, false));
        Assert.assertTrue("Repository did not include test output statement", testRepositoryConnection.hasStatement(testOutputStatement, false));
        Assert.assertTrue("Repository did not include mapping statement", testRepositoryConnection.hasStatement(testMappingStatement, false));
    }
    
}
