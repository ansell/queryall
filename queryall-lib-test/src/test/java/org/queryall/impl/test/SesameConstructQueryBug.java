package org.queryall.impl.test;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
        final URI subjectUri = this.testValueFactory.createURI("http://bio2rdf.org/po:0000198");
        
        final URI predicateUri = this.testValueFactory.createURI("http://bio2rdf.org/po:is_a");
        
        final URI objectUri = this.testValueFactory.createURI("http://bio2rdf.org/po:0009089");
        
        final URI normalisedObjectUri = this.testValueFactory.createURI("http://oas.example.org/plantontology:0009089");
        
        final Statement testInputStatement = this.testValueFactory.createStatement(subjectUri, predicateUri, objectUri);
        
        final Statement testOutputStatement =
                this.testValueFactory.createStatement(subjectUri, predicateUri, normalisedObjectUri);
        
        final Statement testMappingStatement =
                this.testValueFactory.createStatement(normalisedObjectUri, OWL.SAMEAS, objectUri);
        
        this.testRepositoryConnection.add(testInputStatement);
        
        this.testRepositoryConnection.commit();
        
        Assert.assertEquals("The test statement was not added to the repository", 1,
                this.testRepositoryConnection.size());
        
        final String testQuery =
                "CONSTRUCT { ?subjectUri ?predicateUri ?normalisedObjectUri .  ?normalisedObjectUri <http://www.w3.org/2002/07/owl#sameAs> ?objectUri .  } WHERE {  ?subjectUri ?predicateUri ?objectUri . filter(isIRI(?objectUri) && strStarts(str(?objectUri), \"http://bio2rdf.org/po:\")) . bind(iri(concat(\"http://oas.example.org/plantontology:\", encode_for_uri(substr(str(?objectUri), 23)))) AS ?normalisedObjectUri)  } ";
        
        final GraphQueryResult graphResult =
                this.testRepositoryConnection.prepareGraphQuery(QueryLanguage.SPARQL, testQuery).evaluate();
        
        int selectedStatements = 0;
        
        final Collection<Statement> results = new ArrayList<Statement>(2);
        
        while(graphResult.hasNext())
        {
            final Statement nextStatement = graphResult.next();
            
            results.add(nextStatement);
            selectedStatements++;
        }
        
        Assert.assertEquals("Sesame sparql query bug", 2, selectedStatements);
        
        this.testRepositoryConnection.add(results);
        
        Assert.assertEquals("Repository did not contain the expected number of statements", 3,
                this.testRepositoryConnection.size());
        
        for(final Statement nextResutStatement : this.testRepositoryConnection.getStatements(null, null, null, false)
                .asList())
        {
            System.out.println(nextResutStatement.toString());
        }
        
        Assert.assertTrue("Repository did not include test input statement",
                this.testRepositoryConnection.hasStatement(testInputStatement, false));
        Assert.assertTrue("Repository did not include test output statement",
                this.testRepositoryConnection.hasStatement(testOutputStatement, false));
        Assert.assertTrue("Repository did not include mapping statement",
                this.testRepositoryConnection.hasStatement(testMappingStatement, false));
    }
    
}
