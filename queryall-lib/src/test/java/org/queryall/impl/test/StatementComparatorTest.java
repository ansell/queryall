/**
 * 
 */
package org.queryall.impl.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.queryall.helpers.StatementComparator;

/**
 * Tests StatementComparator to make sure it complies with its contract, and the equals contract for
 * Statement
 * 
 * It also tests ValueComparator which is a direct dependency of StatementComparator
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class StatementComparatorTest
{
    private StatementComparator testComparator;
    /**
     * Each test requires a new ValueFactory to ensure that the creation of BNode identifiers are
     * segregated between tests as far as possible.
     * 
     * However, sorting between unequal BNodes is not supported given the BNode identifier
     * definition, so BNodes are only used to verify consistent sorting of the same BNode and
     * sorting between BNodes and URIs/Literals
     */
    private ValueFactory valueFactory;
    
    private BNode testSubjectBNode1;
    /**
     * testSubjectUri1 needs to be constructed to sort before testSubjectUri2
     */
    private URI testSubjectUri1;
    private URI testSubjectUri2;
    
    /**
     * testPredicateUri1 needs to be constructed to sort before testPredicateUri2
     */
    private URI testPredicateUri1;
    private URI testPredicateUri2;
    
    private BNode testObjectBNode1;
    /**
     * testObjectUri1 needs to be constructed to sort before testObjectUri2
     */
    private URI testObjectUri1;
    private URI testObjectUri2;
    /**
     * testObjectLiteral1 needs to be constructed to sort before testObjectLiteral2
     */
    private Literal testObjectLiteral1;
    private Literal testObjectLiteral2;
    
    private BNode testContextBNode1;
    /**
     * testContextUri1 needs to be constructed to sort before testContextUri2
     */
    private URI testContextUri1;
    private URI testContextUri2;
    
    private Statement statement1;
    private Statement statement2;
    
    /**
     * Sets up a new StatementComparator before each test
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.testComparator = new StatementComparator();
        this.valueFactory = new ValueFactoryImpl();
        
        this.testSubjectBNode1 = this.valueFactory.createBNode("SubjectBNode1");
        this.testSubjectUri1 = this.valueFactory.createURI("urn:test:statementcomparator:", "subject1");
        this.testSubjectUri2 = this.valueFactory.createURI("urn:test:statementcomparator:", "subject2");
        
        this.testPredicateUri1 = this.valueFactory.createURI("urn:test:statementcomparator:", "predicate1");
        this.testPredicateUri2 = this.valueFactory.createURI("urn:test:statementcomparator:", "predicate2");
        
        this.testObjectBNode1 = this.valueFactory.createBNode("ObjectBNode1");
        this.testObjectUri1 = this.valueFactory.createURI("urn:test:statementcomparator:", "object1");
        this.testObjectUri2 = this.valueFactory.createURI("urn:test:statementcomparator:", "object2");
        this.testObjectLiteral1 = this.valueFactory.createLiteral("test object literal 1");
        this.testObjectLiteral2 = this.valueFactory.createLiteral("test object literal 2");
        
        this.testContextBNode1 = this.valueFactory.createBNode("ContextBNode1");
        this.testContextUri1 = this.valueFactory.createURI("urn:test:statementcomparator:", "context1");
        this.testContextUri2 = this.valueFactory.createURI("urn:test:statementcomparator:", "context2");
    }
    
    /**
     * Cleans up after each test
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.testComparator = null;
        this.valueFactory = null;
        
        this.testSubjectUri1 = null;
        this.testSubjectUri2 = null;
        this.testSubjectBNode1 = null;
        
        this.testPredicateUri1 = null;
        this.testPredicateUri2 = null;
        
        this.testObjectUri1 = null;
        this.testObjectUri2 = null;
        this.testObjectLiteral1 = null;
        this.testObjectLiteral2 = null;
        this.testObjectBNode1 = null;
        
        this.testContextUri1 = null;
        this.testContextUri2 = null;
        this.testContextBNode1 = null;
        
        this.statement1 = null;
        this.statement2 = null;
    }
    
    /**
     * Tests whether a statement with a BNode object is sorted before a statement with a Literal
     * object
     */
    @Test
    public void testCompareBNodeAndLiteralObjects()
    {
        this.statement1 =
                this.valueFactory
                        .createStatement(this.testSubjectBNode1, this.testPredicateUri1, this.testObjectBNode1);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectBNode1, this.testPredicateUri1,
                        this.testObjectLiteral1);
        
        Assert.assertTrue(this.testComparator.compare(this.statement1, this.statement2) < 0);
        Assert.assertTrue(this.testComparator.compare(this.statement2, this.statement1) > 0);
    }
    
    /**
     * Tests whether a statement with a BNode object is sorted before a statement with a URI object
     */
    @Test
    public void testCompareBNodeAndUriObjects()
    {
        this.statement1 =
                this.valueFactory
                        .createStatement(this.testSubjectBNode1, this.testPredicateUri1, this.testObjectBNode1);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectBNode1, this.testPredicateUri1, this.testObjectUri1);
        
        Assert.assertTrue(this.testComparator.compare(this.statement1, this.statement2) < 0);
        Assert.assertTrue(this.testComparator.compare(this.statement2, this.statement1) > 0);
    }
    
    /**
     * Tests whether a Statement with a Blank Node subject is sorted BEFORE a similar statement with
     * a URI subject
     */
    @Test
    public void testCompareBNodeSubject()
    {
        this.statement1 =
                this.valueFactory
                        .createStatement(this.testSubjectBNode1, this.testPredicateUri1, this.testObjectBNode1);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectBNode1);
        
        Assert.assertTrue(this.testComparator.compare(this.statement1, this.statement2) < 0);
        Assert.assertTrue(this.testComparator.compare(this.statement2, this.statement1) > 0);
    }
    
    /**
     * Tests whether two equivalent statements (same subject/predicate/object) with a BNode context
     * and one with a URI defined is sorted as BEFORE
     */
    @Test
    public void testCompareEquivalentBNodeAndUriContext()
    {
        this.statement1 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        this.testContextBNode1);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        this.testContextUri1);
        
        Assert.assertTrue(this.testComparator.compare(this.statement1, this.statement2) < 0);
        Assert.assertTrue(this.testComparator.compare(this.statement2, this.statement1) > 0);
    }
    
    /**
     * Tests whether two equivalent statements (same subject/predicate/object) with null contexts
     * are sorted as EQUALS
     */
    @Test
    public void testCompareEquivalentBothNullContexts()
    {
        this.statement1 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        null);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        null);
        
        Assert.assertEquals(0, this.testComparator.compare(this.statement1, this.statement2));
        Assert.assertEquals(0, this.testComparator.compare(this.statement2, this.statement1));
    }
    
    /**
     * Tests whether two equivalent statements (same subject/predicate/object) with typed null
     * contexts are sorted as EQUALS
     */
    @Test
    public void testCompareEquivalentBothNullContextsTyped1()
    {
        this.statement1 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        (Resource)null);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        (Resource)null);
        
        Assert.assertEquals(0, this.testComparator.compare(this.statement1, this.statement2));
        Assert.assertEquals(0, this.testComparator.compare(this.statement2, this.statement1));
    }
    
    /**
     * Tests whether two equivalent statements (same subject/predicate/object) with typed null
     * contexts are sorted as EQUALS
     */
    @Test
    public void testCompareEquivalentBothNullContextsTyped2()
    {
        this.statement1 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        (Resource)null);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        (BNode)null);
        
        Assert.assertEquals(0, this.testComparator.compare(this.statement1, this.statement2));
        Assert.assertEquals(0, this.testComparator.compare(this.statement2, this.statement1));
    }
    
    /**
     * Tests whether two equivalent statements (same subject/predicate/object) with typed null
     * contexts are sorted as EQUALS
     */
    @Test
    public void testCompareEquivalentBothNullContextsTyped3()
    {
        this.statement1 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        (URI)null);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        (BNode)null);
        
        Assert.assertEquals(0, this.testComparator.compare(this.statement1, this.statement2));
        Assert.assertEquals(0, this.testComparator.compare(this.statement2, this.statement1));
    }
    
    /**
     * Tests whether two equivalent statements (same subject/predicate/object) with typed null
     * contexts are sorted as EQUALS
     */
    @Test
    public void testCompareEquivalentBothNullContextsTyped4()
    {
        this.statement1 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        (URI)null);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        (Resource)null);
        
        Assert.assertEquals(0, this.testComparator.compare(this.statement1, this.statement2));
        Assert.assertEquals(0, this.testComparator.compare(this.statement2, this.statement1));
    }
    
    /**
     * Tests whether two equivalent statements (same subject/predicate/object) with no context
     * defined is sorted as EQUALS
     */
    @Test
    public void testCompareEquivalentNoContexts()
    {
        this.statement1 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1);
        
        Assert.assertEquals(0, this.testComparator.compare(this.statement1, this.statement2));
        Assert.assertEquals(0, this.testComparator.compare(this.statement2, this.statement1));
    }
    
    /**
     * Tests whether two equivalent statements (same subject/predicate/object) with one null context
     * and one with no context defined is sorted as EQUALS
     */
    @Test
    public void testCompareEquivalentOneNullContext()
    {
        this.statement1 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        null);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1);
        
        Assert.assertEquals(0, this.testComparator.compare(this.statement1, this.statement2));
        Assert.assertEquals(0, this.testComparator.compare(this.statement2, this.statement1));
    }
    
    /**
     * Tests whether two equivalent statements (same subject/predicate/object) with one null context
     * and one with a URI defined is sorted as BEFORE
     */
    @Test
    public void testCompareEquivalentOneNullOneBNodeContext()
    {
        this.statement1 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        null);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        this.testContextBNode1);
        
        Assert.assertTrue(this.testComparator.compare(this.statement1, this.statement2) < 0);
        Assert.assertTrue(this.testComparator.compare(this.statement2, this.statement1) > 0);
    }
    
    /**
     * Tests whether two equivalent statements (same subject/predicate/object) with one null context
     * and one with a URI defined is sorted as BEFORE
     */
    @Test
    public void testCompareEquivalentOneNullOneURIContext()
    {
        this.statement1 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        null);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        this.testContextUri1);
        
        Assert.assertTrue(this.testComparator.compare(this.statement1, this.statement2) < 0);
        Assert.assertTrue(this.testComparator.compare(this.statement2, this.statement1) > 0);
    }
    
    /**
     * Tests whether two equivalent statements (same subject/predicate/object) with the same context
     * URI are sorted as EQUALS
     */
    @Test
    public void testCompareEquivalentSameUriContext()
    {
        this.statement1 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        this.testContextUri1);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        this.testContextUri1);
        
        Assert.assertEquals(0, this.testComparator.compare(this.statement1, this.statement2));
        Assert.assertEquals(0, this.testComparator.compare(this.statement2, this.statement1));
    }
    
    /**
     * Tests whether two equivalent statements (same subject/predicate/object) with two different
     * URIs is sorted correctly
     */
    @Test
    public void testCompareEquivalentTwoUrisContext()
    {
        this.statement1 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        this.testContextUri1);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1,
                        this.testContextUri2);
        
        Assert.assertTrue(this.testComparator.compare(this.statement1, this.statement2) < 0);
        Assert.assertTrue(this.testComparator.compare(this.statement2, this.statement1) > 0);
    }
    
    /**
     * Tests consistency of sorting between equivalent statements with different literal objects
     */
    @Test
    public void testCompareLiteralObjects()
    {
        this.statement1 =
                this.valueFactory.createStatement(this.testSubjectBNode1, this.testPredicateUri1,
                        this.testObjectLiteral1);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectBNode1, this.testPredicateUri1,
                        this.testObjectLiteral2);
        
        Assert.assertTrue(this.testComparator.compare(this.statement1, this.statement2) < 0);
        Assert.assertTrue(this.testComparator.compare(this.statement2, this.statement1) > 0);
    }
    
    /**
     * Tests whether a statement with a URI object is sorted before a statement with a Literal
     * object
     */
    @Test
    public void testComparePredicates()
    {
        this.statement1 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri2, this.testObjectUri1);
        
        Assert.assertTrue(this.testComparator.compare(this.statement1, this.statement2) < 0);
        Assert.assertTrue(this.testComparator.compare(this.statement2, this.statement1) > 0);
    }
    
    /**
     * Tests consistency of sorting for two equivalent statements with different subject URIs
     */
    @Test
    public void testCompareSubjects()
    {
        this.statement1 =
                this.valueFactory.createStatement(this.testSubjectUri1, this.testPredicateUri1, this.testObjectUri1);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectUri2, this.testPredicateUri1, this.testObjectUri1);
        
        Assert.assertTrue(this.testComparator.compare(this.statement1, this.statement2) < 0);
        Assert.assertTrue(this.testComparator.compare(this.statement2, this.statement1) > 0);
    }
    
    /**
     * Tests whether a statement with a URI object is sorted before a statement with a Literal
     * object
     */
    @Test
    public void testCompareURIAndLiteralObjects()
    {
        this.statement1 =
                this.valueFactory.createStatement(this.testSubjectBNode1, this.testPredicateUri1, this.testObjectUri1);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectBNode1, this.testPredicateUri1,
                        this.testObjectLiteral1);
        
        Assert.assertTrue(this.testComparator.compare(this.statement1, this.statement2) < 0);
        Assert.assertTrue(this.testComparator.compare(this.statement2, this.statement1) > 0);
    }
    
    /**
     * Tests consistency of sorting between equivalent statements with different URI objects
     */
    @Test
    public void testCompareUriObjects()
    {
        this.statement1 =
                this.valueFactory.createStatement(this.testSubjectBNode1, this.testPredicateUri1, this.testObjectUri1);
        this.statement2 =
                this.valueFactory.createStatement(this.testSubjectBNode1, this.testPredicateUri1, this.testObjectUri2);
        
        Assert.assertTrue(this.testComparator.compare(this.statement1, this.statement2) < 0);
        Assert.assertTrue(this.testComparator.compare(this.statement2, this.statement1) > 0);
    }
    
    /**
     * Tests whether the StatementComparator constants match the general Comparable interface
     * contract
     */
    @Test
    public void testStatementComparatorConstants()
    {
        Assert.assertEquals(0, StatementComparator.EQUALS);
        Assert.assertTrue(StatementComparator.BEFORE < 0);
        Assert.assertTrue(StatementComparator.AFTER > 0);
    }
}
