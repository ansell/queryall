/**
 * 
 */
package org.queryall.api.test;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.queryall.api.querytype.QueryType;

/**
 * Abstract unit test for QueryType API
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractQueryTypeTest
{
    protected URI testTrueQueryTypeUri;
    protected URI testFalseQueryTypeUri;
    protected URI testNamespaceUri1;
    protected URI testNamespaceUri2;
    protected URI testNamespaceUri3;
    protected URI testNamespaceUri4;
    protected URI testNamespaceUri5;
    protected URI testFalseNamespaceUri;
    
    private Collection<Collection<URI>> namespacesFalse;
    private Collection<Collection<URI>> namespaces12345AndFalse;
    private Collection<Collection<URI>> namespaces12345;
    private Collection<Collection<URI>> namespaces123;
    private Collection<Collection<URI>> namespaces1orFalse;
    private Collection<Collection<URI>> namespaces1and2orFalse;
    private Collection<Collection<URI>> namespaces12;
    private Collection<Collection<URI>> namespaces34;
    private Collection<Collection<URI>> namespaces45;
    
    private QueryType queryTypePublicIdentifiers;
    
    private QueryType queryTypeNamespaceInputIndexes;
    
    private QueryType queryTypeIncludeDefaults;
    private QueryType queryTypeNotIncludeDefaults;
    
    private QueryType queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll;
    private QueryType queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny;
    private QueryType queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll;
    private QueryType queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny;
    
    private QueryType queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll;
    private QueryType queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny;
    private QueryType queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll;
    private QueryType queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny;
    
    private QueryType queryType123NamespacesMatchMethodAll;
    private QueryType queryType12345NamespacesMatchMethodAll;
    private QueryType queryTypeNoNamespacesMatchMethodAll;
    
    private QueryType queryType123NamespacesMatchMethodAny;
    private QueryType queryType12345NamespacesMatchMethodAny;
    private QueryType queryTypeNoNamespacesMatchMethodAny;
    
    private QueryType queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny;
    private QueryType queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll;
    
    /**
     * This method must return the URI used by the implementation to indicate that all namespaces
     * must match for the namespace test to be satisfied
     * 
     * @return A URI designating that all namespaces need to match
     */
    public abstract URI getAllNamespaceMatchMethodUri();
    
    /**
     * This method must return the URI used by the implementation to indicate that only one (1) or
     * more namespaces need to match for the namespace test to be satisfied
     * 
     * @return A URI designating that any namespace matches are sufficient
     */
    public abstract URI getAnyNamespaceMatchMethodUri();
    
    /**
     * This method must be overridden to return a new instance of the implemented QueryType class
     * for each successive invocation
     * 
     * @return A new instance of the QueryType implementation
     */
    public abstract QueryType getNewTestQueryType();
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        final ValueFactory f = new MemValueFactory();
        
        this.testTrueQueryTypeUri = f.createURI("http://example.org/test/includedQueryType");
        this.testFalseQueryTypeUri = f.createURI("http://example.org/test/excludedQueryType");
        this.testNamespaceUri1 = f.createURI("http://example.org/test/includedNamespace-1");
        this.testNamespaceUri2 = f.createURI("http://example.org/test/includedNamespace-2");
        this.testNamespaceUri3 = f.createURI("http://example.org/test/includedNamespace-3");
        this.testNamespaceUri4 = f.createURI("http://example.org/test/includedNamespace-4");
        this.testNamespaceUri5 = f.createURI("http://example.org/test/includedNamespace-5");
        this.testFalseNamespaceUri = f.createURI("http://example.org/test/excludedNamespace");
        
        final LinkedList<URI> namespaceFalseInner = new LinkedList<URI>();
        namespaceFalseInner.add(this.testFalseNamespaceUri);
        
        final LinkedList<URI> namespace1Inner = new LinkedList<URI>();
        namespace1Inner.add(this.testNamespaceUri1);
        
        final LinkedList<URI> namespace2Inner = new LinkedList<URI>();
        namespace2Inner.add(this.testNamespaceUri2);
        
        final LinkedList<URI> namespace3Inner = new LinkedList<URI>();
        namespace3Inner.add(this.testNamespaceUri3);
        
        final LinkedList<URI> namespace4Inner = new LinkedList<URI>();
        namespace4Inner.add(this.testNamespaceUri4);
        
        final LinkedList<URI> namespace5Inner = new LinkedList<URI>();
        namespace5Inner.add(this.testNamespaceUri5);
        
        final LinkedList<URI> namespace1OrFalseInner = new LinkedList<URI>();
        namespace1OrFalseInner.add(this.testNamespaceUri1);
        namespace1OrFalseInner.add(this.testFalseNamespaceUri);
        
        final LinkedList<URI> namespace2OrFalseInner = new LinkedList<URI>();
        namespace2OrFalseInner.add(this.testNamespaceUri2);
        namespace2OrFalseInner.add(this.testFalseNamespaceUri);
        
        this.namespacesFalse = new LinkedList<Collection<URI>>();
        this.namespacesFalse.add(namespaceFalseInner);
        
        this.namespaces12345AndFalse = new LinkedList<Collection<URI>>();
        this.namespaces12345AndFalse.add(namespace1Inner);
        this.namespaces12345AndFalse.add(namespace2Inner);
        this.namespaces12345AndFalse.add(namespace3Inner);
        this.namespaces12345AndFalse.add(namespace4Inner);
        this.namespaces12345AndFalse.add(namespace5Inner);
        this.namespaces12345AndFalse.add(namespaceFalseInner);
        
        this.namespaces12345 = new LinkedList<Collection<URI>>();
        this.namespaces12345.add(namespace1Inner);
        this.namespaces12345.add(namespace2Inner);
        this.namespaces12345.add(namespace3Inner);
        this.namespaces12345.add(namespace4Inner);
        this.namespaces12345.add(namespace5Inner);
        
        this.namespaces123 = new LinkedList<Collection<URI>>();
        this.namespaces123.add(namespace1Inner);
        this.namespaces123.add(namespace2Inner);
        this.namespaces123.add(namespace3Inner);
        
        this.namespaces1orFalse = new LinkedList<Collection<URI>>();
        this.namespaces1orFalse.add(namespace1OrFalseInner);
        
        this.namespaces1and2orFalse = new LinkedList<Collection<URI>>();
        this.namespaces1and2orFalse.add(namespace1Inner);
        this.namespaces1and2orFalse.add(namespace2OrFalseInner);
        
        this.namespaces12 = new LinkedList<Collection<URI>>();
        this.namespaces12.add(namespace1Inner);
        this.namespaces12.add(namespace2Inner);
        
        this.namespaces34 = new LinkedList<Collection<URI>>();
        this.namespaces34.add(namespace3Inner);
        this.namespaces34.add(namespace4Inner);
        
        this.namespaces45 = new LinkedList<Collection<URI>>();
        this.namespaces45.add(namespace4Inner);
        this.namespaces45.add(namespace5Inner);
        
        final int[] testPublicIdentifierIndexes = new int[1];
        testPublicIdentifierIndexes[0] = 2;
        
        this.queryTypePublicIdentifiers = this.getNewTestQueryType();
        this.queryTypePublicIdentifiers.setPublicIdentifierIndexes(testPublicIdentifierIndexes);
        
        final int[] testNamespaceIdentifierIndexes = new int[1];
        testNamespaceIdentifierIndexes[0] = 2;
        
        this.queryTypeNamespaceInputIndexes = this.getNewTestQueryType();
        this.queryTypeNamespaceInputIndexes.setNamespaceInputIndexes(testNamespaceIdentifierIndexes);
        
        this.queryTypeIncludeDefaults = this.getNewTestQueryType();
        this.queryTypeIncludeDefaults.setIncludeDefaults(true);
        
        this.queryTypeNotIncludeDefaults = this.getNewTestQueryType();
        this.queryTypeNotIncludeDefaults.setIncludeDefaults(false);
        
        this.queryType123NamespacesMatchMethodAll = this.getNewTestQueryType();
        this.queryType123NamespacesMatchMethodAll.setNamespaceMatchMethod(this.getAllNamespaceMatchMethodUri());
        this.queryType123NamespacesMatchMethodAll.setIsNamespaceSpecific(true);
        this.queryType123NamespacesMatchMethodAll.setHandleAllNamespaces(false);
        this.queryType123NamespacesMatchMethodAll.addNamespaceToHandle(this.testNamespaceUri1);
        this.queryType123NamespacesMatchMethodAll.addNamespaceToHandle(this.testNamespaceUri2);
        this.queryType123NamespacesMatchMethodAll.addNamespaceToHandle(this.testNamespaceUri3);
        
        this.queryType12345NamespacesMatchMethodAll = this.getNewTestQueryType();
        this.queryType12345NamespacesMatchMethodAll.setNamespaceMatchMethod(this.getAllNamespaceMatchMethodUri());
        this.queryType12345NamespacesMatchMethodAll.setIsNamespaceSpecific(true);
        this.queryType12345NamespacesMatchMethodAll.setHandleAllNamespaces(false);
        this.queryType12345NamespacesMatchMethodAll.addNamespaceToHandle(this.testNamespaceUri1);
        this.queryType12345NamespacesMatchMethodAll.addNamespaceToHandle(this.testNamespaceUri2);
        this.queryType12345NamespacesMatchMethodAll.addNamespaceToHandle(this.testNamespaceUri3);
        this.queryType12345NamespacesMatchMethodAll.addNamespaceToHandle(this.testNamespaceUri4);
        this.queryType12345NamespacesMatchMethodAll.addNamespaceToHandle(this.testNamespaceUri5);
        
        this.queryTypeNoNamespacesMatchMethodAll = this.getNewTestQueryType();
        this.queryTypeNoNamespacesMatchMethodAll.setNamespaceMatchMethod(this.getAllNamespaceMatchMethodUri());
        this.queryTypeNoNamespacesMatchMethodAll.setIsNamespaceSpecific(true);
        this.queryTypeNoNamespacesMatchMethodAll.setHandleAllNamespaces(false);
        
        this.queryType123NamespacesMatchMethodAny = this.getNewTestQueryType();
        this.queryType123NamespacesMatchMethodAny.setNamespaceMatchMethod(this.getAnyNamespaceMatchMethodUri());
        this.queryType123NamespacesMatchMethodAny.setIsNamespaceSpecific(true);
        this.queryType123NamespacesMatchMethodAny.setHandleAllNamespaces(false);
        this.queryType123NamespacesMatchMethodAny.addNamespaceToHandle(this.testNamespaceUri1);
        this.queryType123NamespacesMatchMethodAny.addNamespaceToHandle(this.testNamespaceUri2);
        this.queryType123NamespacesMatchMethodAny.addNamespaceToHandle(this.testNamespaceUri3);
        
        this.queryType12345NamespacesMatchMethodAny = this.getNewTestQueryType();
        this.queryType12345NamespacesMatchMethodAny.setNamespaceMatchMethod(this.getAnyNamespaceMatchMethodUri());
        this.queryType12345NamespacesMatchMethodAny.setIsNamespaceSpecific(true);
        this.queryType12345NamespacesMatchMethodAny.setHandleAllNamespaces(false);
        this.queryType12345NamespacesMatchMethodAny.addNamespaceToHandle(this.testNamespaceUri1);
        this.queryType12345NamespacesMatchMethodAny.addNamespaceToHandle(this.testNamespaceUri2);
        this.queryType12345NamespacesMatchMethodAny.addNamespaceToHandle(this.testNamespaceUri3);
        this.queryType12345NamespacesMatchMethodAny.addNamespaceToHandle(this.testNamespaceUri4);
        this.queryType12345NamespacesMatchMethodAny.addNamespaceToHandle(this.testNamespaceUri5);
        
        this.queryTypeNoNamespacesMatchMethodAny = this.getNewTestQueryType();
        this.queryTypeNoNamespacesMatchMethodAny.setNamespaceMatchMethod(this.getAnyNamespaceMatchMethodUri());
        this.queryTypeNoNamespacesMatchMethodAny.setIsNamespaceSpecific(true);
        this.queryTypeNoNamespacesMatchMethodAny.setHandleAllNamespaces(false);
        
        this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll = this.getNewTestQueryType();
        this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.setNamespaceMatchMethod(this
                .getAllNamespaceMatchMethodUri());
        this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.setIsNamespaceSpecific(false);
        this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.setHandleAllNamespaces(false);
        
        this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny = this.getNewTestQueryType();
        this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.setNamespaceMatchMethod(this
                .getAnyNamespaceMatchMethodUri());
        this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.setIsNamespaceSpecific(false);
        this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.setHandleAllNamespaces(false);
        
        this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny = this.getNewTestQueryType();
        this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.setNamespaceMatchMethod(this
                .getAnyNamespaceMatchMethodUri());
        this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.setIsNamespaceSpecific(false);
        this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.setHandleAllNamespaces(false);
        this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.addNamespaceToHandle(this.testNamespaceUri1);
        this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.addNamespaceToHandle(this.testNamespaceUri2);
        this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.addNamespaceToHandle(this.testNamespaceUri3);
        
        this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll = this.getNewTestQueryType();
        this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.setNamespaceMatchMethod(this
                .getAllNamespaceMatchMethodUri());
        this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.setIsNamespaceSpecific(false);
        this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.setHandleAllNamespaces(false);
        this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.addNamespaceToHandle(this.testNamespaceUri1);
        this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.addNamespaceToHandle(this.testNamespaceUri2);
        this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.addNamespaceToHandle(this.testNamespaceUri3);
        
        this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll = this.getNewTestQueryType();
        this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.setNamespaceMatchMethod(this
                .getAllNamespaceMatchMethodUri());
        this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.setIsNamespaceSpecific(false);
        this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.setHandleAllNamespaces(true);
        
        this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny = this.getNewTestQueryType();
        this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.setNamespaceMatchMethod(this
                .getAnyNamespaceMatchMethodUri());
        this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.setIsNamespaceSpecific(false);
        this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.setHandleAllNamespaces(true);
        
        this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny = this.getNewTestQueryType();
        this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.setNamespaceMatchMethod(this
                .getAnyNamespaceMatchMethodUri());
        this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.setIsNamespaceSpecific(false);
        this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.setHandleAllNamespaces(true);
        this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .addNamespaceToHandle(this.testNamespaceUri1);
        this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .addNamespaceToHandle(this.testNamespaceUri2);
        this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .addNamespaceToHandle(this.testNamespaceUri3);
        
        this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll = this.getNewTestQueryType();
        this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.setNamespaceMatchMethod(this
                .getAllNamespaceMatchMethodUri());
        this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.setIsNamespaceSpecific(false);
        this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.setHandleAllNamespaces(true);
        this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .addNamespaceToHandle(this.testNamespaceUri1);
        this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .addNamespaceToHandle(this.testNamespaceUri2);
        this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .addNamespaceToHandle(this.testNamespaceUri3);
        
        this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny = this.getNewTestQueryType();
        this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.setNamespaceMatchMethod(this
                .getAnyNamespaceMatchMethodUri());
        this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.setIsNamespaceSpecific(true);
        this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.setHandleAllNamespaces(true);
        
        this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll = this.getNewTestQueryType();
        this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.setNamespaceMatchMethod(this
                .getAllNamespaceMatchMethodUri());
        this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.setIsNamespaceSpecific(true);
        this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.setHandleAllNamespaces(true);
        
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.testTrueQueryTypeUri = null;
        this.testFalseQueryTypeUri = null;
        this.testNamespaceUri1 = null;
        this.testNamespaceUri2 = null;
        this.testNamespaceUri3 = null;
        this.testNamespaceUri4 = null;
        this.testNamespaceUri5 = null;
        this.testFalseNamespaceUri = null;
        
        this.namespacesFalse = null;
        this.namespaces12345AndFalse = null;
        this.namespaces12345 = null;
        this.namespaces123 = null;
        this.namespaces1orFalse = null;
        this.namespaces1and2orFalse = null;
        this.namespaces12 = null;
        this.namespaces34 = null;
        this.namespaces45 = null;
        
        this.queryTypePublicIdentifiers = null;
        this.queryTypeNamespaceInputIndexes = null;
        
        this.queryTypeIncludeDefaults = null;
        this.queryTypeNotIncludeDefaults = null;
        
        this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll = null;
        this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny = null;
        this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll = null;
        this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny = null;
        
        this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll = null;
        this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny = null;
        this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll = null;
        this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny = null;
        
        this.queryType123NamespacesMatchMethodAll = null;
        this.queryType12345NamespacesMatchMethodAll = null;
        this.queryTypeNoNamespacesMatchMethodAll = null;
        
        this.queryType123NamespacesMatchMethodAny = null;
        this.queryType12345NamespacesMatchMethodAny = null;
        this.queryTypeNoNamespacesMatchMethodAny = null;
        
        this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny = null;
        this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll = null;
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.querytype.QueryType#handlesNamespacesSpecifically(java.util.Collection)}.
     * 
     * This test requires the namespaces to be specifically declared for the test to succeed
     */
    @Test
    public void testHandlesNamespacesSpecifically()
    {
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAll.handlesNamespacesSpecifically(this.namespaces123));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAll.handlesNamespacesSpecifically(this.namespaces12));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces1and2orFalse));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces1orFalse));
        Assert.assertFalse(this.queryType123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12345));
        Assert.assertFalse(this.queryType123NamespacesMatchMethodAll.handlesNamespacesSpecifically(this.namespaces34));
        Assert.assertFalse(this.queryType123NamespacesMatchMethodAll.handlesNamespacesSpecifically(this.namespaces45));
        Assert.assertFalse(this.queryType123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespacesFalse));
        Assert.assertFalse(this.queryType123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(this.namespaces123));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(this.namespaces12));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(this.namespaces12345));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(this.namespaces34));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(this.namespaces45));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12345AndFalse));
        
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAll.handlesNamespacesSpecifically(this.namespaces123));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAll.handlesNamespacesSpecifically(this.namespaces12));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces1and2orFalse));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces1orFalse));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12345));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAll.handlesNamespacesSpecifically(this.namespaces34));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAll.handlesNamespacesSpecifically(this.namespaces45));
        Assert.assertFalse(this.queryType12345NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespacesFalse));
        Assert.assertFalse(this.queryType12345NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12345AndFalse));
        
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAny.handlesNamespacesSpecifically(this.namespaces123));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAny.handlesNamespacesSpecifically(this.namespaces12));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces1and2orFalse));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces1orFalse));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAny.handlesNamespacesSpecifically(this.namespaces12345));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAny.handlesNamespacesSpecifically(this.namespaces34));
        Assert.assertFalse(this.queryType123NamespacesMatchMethodAny.handlesNamespacesSpecifically(this.namespaces45));
        Assert.assertFalse(this.queryType123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespacesFalse));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(this.namespaces123));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(this.namespaces12));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(this.namespaces12345));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(this.namespaces34));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(this.namespaces45));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12345AndFalse));
        
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAny.handlesNamespacesSpecifically(this.namespaces123));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAny.handlesNamespacesSpecifically(this.namespaces12));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces1and2orFalse));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces1orFalse));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12345));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAny.handlesNamespacesSpecifically(this.namespaces34));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAny.handlesNamespacesSpecifically(this.namespaces45));
        Assert.assertFalse(this.queryType12345NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespacesFalse));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces123));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12345));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces34));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces45));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces123));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12345));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces34));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces45));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces123));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12345));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces34));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces45));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces123));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12345));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces34));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces45));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces123));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12345));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces34));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces45));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces123));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12345));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces34));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces45));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces123));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12345));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces34));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces45));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces123));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12345));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces34));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces45));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces123));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12345));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces34));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces45));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespacesSpecifically(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces123));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12345));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces34));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces45));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespacesSpecifically(this.namespaces12345AndFalse));
        
    }
    
    /**
     * Test method for {@link org.queryall.api.querytype.QueryType#handlesNamespaceUris(java.util.Collection)}
     * .
     * 
     * This test does not require the namespaces to be specifically declared for the test to succeed
     */
    @Test
    public void testHandlesNamespaceUris()
    {
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces123));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces12));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces1and2orFalse));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces1orFalse));
        Assert.assertFalse(this.queryType123NamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces12345));
        Assert.assertFalse(this.queryType123NamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces34));
        Assert.assertFalse(this.queryType123NamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces45));
        Assert.assertFalse(this.queryType123NamespacesMatchMethodAll.handlesNamespaceUris(this.namespacesFalse));
        Assert.assertFalse(this.queryType123NamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces123));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces12));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces12345));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces34));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces45));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces12345AndFalse));
        
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces123));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces12));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces1and2orFalse));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces1orFalse));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces12345));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces34));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAll.handlesNamespaceUris(this.namespaces45));
        Assert.assertFalse(this.queryType12345NamespacesMatchMethodAll.handlesNamespaceUris(this.namespacesFalse));
        Assert.assertFalse(this.queryType12345NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces12345AndFalse));
        
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces123));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces12));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces1and2orFalse));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces1orFalse));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces12345));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces34));
        Assert.assertFalse(this.queryType123NamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces45));
        Assert.assertFalse(this.queryType123NamespacesMatchMethodAny.handlesNamespaceUris(this.namespacesFalse));
        Assert.assertTrue(this.queryType123NamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces123));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces12));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces12345));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces34));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces45));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces12345AndFalse));
        
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces123));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces12));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces1and2orFalse));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces1orFalse));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces12345));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces34));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAny.handlesNamespaceUris(this.namespaces45));
        Assert.assertFalse(this.queryType12345NamespacesMatchMethodAny.handlesNamespaceUris(this.namespacesFalse));
        Assert.assertTrue(this.queryType12345NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces123));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces12));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces12345));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces34));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces45));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces123));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces12));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces12345));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces34));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces45));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces123));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces12));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces12345));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces34));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces45));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces123));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces12));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces12345));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces34));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces45));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces123));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces12));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces12345));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces34));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces45));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces123));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces12));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces12345));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces34));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces45));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces123));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces12));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces12345));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces34));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces45));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces12345AndFalse));
        
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces123));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces12));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces1and2orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces1orFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces12345));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces34));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces45));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespacesFalse));
        Assert.assertFalse(this.queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces12345AndFalse));
        
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces123));
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces12));
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces1and2orFalse));
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces1orFalse));
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces12345));
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces34));
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces45));
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespacesFalse));
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny
                .handlesNamespaceUris(this.namespaces12345AndFalse));
        
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces123));
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces12));
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces1and2orFalse));
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces1orFalse));
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces12345));
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces34));
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces45));
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespacesFalse));
        Assert.assertTrue(this.queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll
                .handlesNamespaceUris(this.namespaces12345AndFalse));
        
    }
    
    /**
     * Test method for {@link org.queryall.api.querytype.QueryType#getIncludeDefaults()}.
     */
    @Test
    public void testIncludeDefaults()
    {
        Assert.assertTrue(this.queryTypeIncludeDefaults.getIncludeDefaults());
        Assert.assertFalse(this.queryTypeNotIncludeDefaults.getIncludeDefaults());
    }
    
    /**
     * Test method for {@link org.queryall.api.querytype.QueryType#isInputVariablePublic(int)}.
     */
    @Test
    public void testIsInputVariablePublic()
    {
        Assert.assertTrue(this.queryTypePublicIdentifiers.isInputVariablePublic(2));
        Assert.assertFalse(this.queryTypePublicIdentifiers.isInputVariablePublic(1));
    }
    
    /**
     * Test method for {@link org.queryall.api.querytype.QueryType#getNamespaceInputIndexes()}.
     */
    @Test
    public void testNamespaceVariables()
    {
        Assert.assertEquals(1, this.queryTypeNamespaceInputIndexes.getNamespaceInputIndexes().length);
    }
    
}
