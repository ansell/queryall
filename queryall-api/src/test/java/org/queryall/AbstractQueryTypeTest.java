/**
 * 
 */
package org.queryall;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.queryall.api.QueryType;

/**
 * @author peter
 *
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
     * This method must be overridden to return a new instance of 
     * the implemented QueryType class for each successive invocation
     * @return A new instance of the QueryType implementation
     */
    public abstract QueryType getNewTestQueryType();
    
    /**
     * This method must return the URI used by the implementation 
     * to indicate that all namespaces must match for the 
     * namespace test to be satisfied
     * @return A URI designating that all namespaces need to match
     */
    public abstract URI getAllNamespaceMatchMethodUri();

    /**
     * This method must return the URI used by the implementation 
     * to indicate that only one (1) or more namespaces need 
     * to match for the namespace test to be satisfied
     * @return A URI designating that any namespace matches are sufficient
     */
    public abstract URI getAnyNamespaceMatchMethodUri();

    
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        ValueFactory f = new MemValueFactory();

        testTrueQueryTypeUri = f.createURI("http://example.org/test/includedQueryType");
        testFalseQueryTypeUri = f.createURI("http://example.org/test/excludedQueryType");
        testNamespaceUri1 = f.createURI("http://example.org/test/includedNamespace-1");
        testNamespaceUri2 = f.createURI("http://example.org/test/includedNamespace-2");
        testNamespaceUri3 = f.createURI("http://example.org/test/includedNamespace-3");
        testNamespaceUri4 = f.createURI("http://example.org/test/includedNamespace-4");
        testNamespaceUri5 = f.createURI("http://example.org/test/includedNamespace-5");
        testFalseNamespaceUri = f.createURI("http://example.org/test/excludedNamespace");

        LinkedList<URI> namespaceFalseInner = new LinkedList<URI>();
        namespaceFalseInner.add(testFalseNamespaceUri);

        LinkedList<URI> namespace1Inner = new LinkedList<URI>();
        namespace1Inner.add(testNamespaceUri1);

        LinkedList<URI> namespace2Inner = new LinkedList<URI>();
        namespace2Inner.add(testNamespaceUri2);

        LinkedList<URI> namespace3Inner = new LinkedList<URI>();
        namespace3Inner.add(testNamespaceUri3);

        LinkedList<URI> namespace4Inner = new LinkedList<URI>();
        namespace4Inner.add(testNamespaceUri4);

        LinkedList<URI> namespace5Inner = new LinkedList<URI>();
        namespace5Inner.add(testNamespaceUri5);

        LinkedList<URI> namespace1OrFalseInner = new LinkedList<URI>();
        namespace1OrFalseInner.add(testNamespaceUri1);
        namespace1OrFalseInner.add(testFalseNamespaceUri);

        LinkedList<URI> namespace2OrFalseInner = new LinkedList<URI>();
        namespace2OrFalseInner.add(testNamespaceUri2);
        namespace2OrFalseInner.add(testFalseNamespaceUri);

        namespacesFalse = new LinkedList<Collection<URI>>();
        namespacesFalse.add(namespaceFalseInner);
        
        namespaces12345AndFalse = new LinkedList<Collection<URI>>();
        namespaces12345AndFalse.add(namespace1Inner);
        namespaces12345AndFalse.add(namespace2Inner);
        namespaces12345AndFalse.add(namespace3Inner);
        namespaces12345AndFalse.add(namespace4Inner);
        namespaces12345AndFalse.add(namespace5Inner);
        namespaces12345AndFalse.add(namespaceFalseInner);
        
        namespaces12345 = new LinkedList<Collection<URI>>();
        namespaces12345.add(namespace1Inner);
        namespaces12345.add(namespace2Inner);
        namespaces12345.add(namespace3Inner);
        namespaces12345.add(namespace4Inner);
        namespaces12345.add(namespace5Inner);
        
        namespaces123 = new LinkedList<Collection<URI>>();
        namespaces123.add(namespace1Inner);
        namespaces123.add(namespace2Inner);
        namespaces123.add(namespace3Inner);
        
        namespaces1orFalse = new LinkedList<Collection<URI>>();
        namespaces1orFalse.add(namespace1OrFalseInner);
        
        namespaces1and2orFalse = new LinkedList<Collection<URI>>();
        namespaces1and2orFalse.add(namespace1Inner);
        namespaces1and2orFalse.add(namespace2OrFalseInner);
        
        namespaces12 = new LinkedList<Collection<URI>>();
        namespaces12.add(namespace1Inner);
        namespaces12.add(namespace2Inner);
        
        namespaces34 = new LinkedList<Collection<URI>>();
        namespaces34.add(namespace3Inner);
        namespaces34.add(namespace4Inner);
        
        namespaces45 = new LinkedList<Collection<URI>>();
        namespaces45.add(namespace4Inner);
        namespaces45.add(namespace5Inner);
        
        
        
        int[] testPublicIdentifierIndexes = new int[1];
        testPublicIdentifierIndexes[0] = 2;

        queryTypePublicIdentifiers = getNewTestQueryType();
        queryTypePublicIdentifiers.setPublicIdentifierIndexes(testPublicIdentifierIndexes);
        
        queryType123NamespacesMatchMethodAll = getNewTestQueryType();
        queryType123NamespacesMatchMethodAll.setNamespaceMatchMethod(getAllNamespaceMatchMethodUri());
        queryType123NamespacesMatchMethodAll.setIsNamespaceSpecific(true);
        queryType123NamespacesMatchMethodAll.setHandleAllNamespaces(false);
        queryType123NamespacesMatchMethodAll.addNamespaceToHandle(testNamespaceUri1);
        queryType123NamespacesMatchMethodAll.addNamespaceToHandle(testNamespaceUri2);
        queryType123NamespacesMatchMethodAll.addNamespaceToHandle(testNamespaceUri3);
        
        queryType12345NamespacesMatchMethodAll = getNewTestQueryType();
        queryType12345NamespacesMatchMethodAll.setNamespaceMatchMethod(getAllNamespaceMatchMethodUri());
        queryType12345NamespacesMatchMethodAll.setIsNamespaceSpecific(true);
        queryType12345NamespacesMatchMethodAll.setHandleAllNamespaces(false);
        queryType12345NamespacesMatchMethodAll.addNamespaceToHandle(testNamespaceUri1);
        queryType12345NamespacesMatchMethodAll.addNamespaceToHandle(testNamespaceUri2);
        queryType12345NamespacesMatchMethodAll.addNamespaceToHandle(testNamespaceUri3);
        queryType12345NamespacesMatchMethodAll.addNamespaceToHandle(testNamespaceUri4);
        queryType12345NamespacesMatchMethodAll.addNamespaceToHandle(testNamespaceUri5);
        
        queryTypeNoNamespacesMatchMethodAll = getNewTestQueryType();
        queryTypeNoNamespacesMatchMethodAll.setNamespaceMatchMethod(getAllNamespaceMatchMethodUri());
        queryTypeNoNamespacesMatchMethodAll.setIsNamespaceSpecific(true);
        queryTypeNoNamespacesMatchMethodAll.setHandleAllNamespaces(false);

        queryType123NamespacesMatchMethodAny = getNewTestQueryType();
        queryType123NamespacesMatchMethodAny.setNamespaceMatchMethod(getAnyNamespaceMatchMethodUri());
        queryType123NamespacesMatchMethodAny.setIsNamespaceSpecific(true);
        queryType123NamespacesMatchMethodAny.setHandleAllNamespaces(false);
        queryType123NamespacesMatchMethodAny.addNamespaceToHandle(testNamespaceUri1);
        queryType123NamespacesMatchMethodAny.addNamespaceToHandle(testNamespaceUri2);
        queryType123NamespacesMatchMethodAny.addNamespaceToHandle(testNamespaceUri3);
        
        queryType12345NamespacesMatchMethodAny = getNewTestQueryType();
        queryType12345NamespacesMatchMethodAny.setNamespaceMatchMethod(getAnyNamespaceMatchMethodUri());
        queryType12345NamespacesMatchMethodAny.setIsNamespaceSpecific(true);
        queryType12345NamespacesMatchMethodAny.setHandleAllNamespaces(false);
        queryType12345NamespacesMatchMethodAny.addNamespaceToHandle(testNamespaceUri1);
        queryType12345NamespacesMatchMethodAny.addNamespaceToHandle(testNamespaceUri2);
        queryType12345NamespacesMatchMethodAny.addNamespaceToHandle(testNamespaceUri3);
        queryType12345NamespacesMatchMethodAny.addNamespaceToHandle(testNamespaceUri4);
        queryType12345NamespacesMatchMethodAny.addNamespaceToHandle(testNamespaceUri5);
        
        queryTypeNoNamespacesMatchMethodAny = getNewTestQueryType();
        queryTypeNoNamespacesMatchMethodAny.setNamespaceMatchMethod(getAnyNamespaceMatchMethodUri());
        queryTypeNoNamespacesMatchMethodAny.setIsNamespaceSpecific(true);
        queryTypeNoNamespacesMatchMethodAny.setHandleAllNamespaces(false);

        queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll = getNewTestQueryType();
        queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.setNamespaceMatchMethod(getAllNamespaceMatchMethodUri());
        queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.setIsNamespaceSpecific(false);
        queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.setHandleAllNamespaces(false);

        queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny = getNewTestQueryType();
        queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.setNamespaceMatchMethod(getAnyNamespaceMatchMethodUri());
        queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.setIsNamespaceSpecific(false);
        queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.setHandleAllNamespaces(false);

        
        queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny = getNewTestQueryType();
        queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.setNamespaceMatchMethod(getAnyNamespaceMatchMethodUri());
        queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.setIsNamespaceSpecific(false);
        queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.setHandleAllNamespaces(false);
        queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.addNamespaceToHandle(testNamespaceUri1);
        queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.addNamespaceToHandle(testNamespaceUri2);
        queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.addNamespaceToHandle(testNamespaceUri3);

        queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll = getNewTestQueryType();
        queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.setNamespaceMatchMethod(getAllNamespaceMatchMethodUri());
        queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.setIsNamespaceSpecific(false);
        queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.setHandleAllNamespaces(false);
        queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.addNamespaceToHandle(testNamespaceUri1);
        queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.addNamespaceToHandle(testNamespaceUri2);
        queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.addNamespaceToHandle(testNamespaceUri3);
        
        queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll = getNewTestQueryType();
        queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.setNamespaceMatchMethod(getAllNamespaceMatchMethodUri());
        queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.setIsNamespaceSpecific(false);
        queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.setHandleAllNamespaces(true);

        queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny = getNewTestQueryType();
        queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.setNamespaceMatchMethod(getAnyNamespaceMatchMethodUri());
        queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.setIsNamespaceSpecific(false);
        queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.setHandleAllNamespaces(true);

        
        queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny = getNewTestQueryType();
        queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.setNamespaceMatchMethod(getAnyNamespaceMatchMethodUri());
        queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.setIsNamespaceSpecific(false);
        queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.setHandleAllNamespaces(true);
        queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.addNamespaceToHandle(testNamespaceUri1);
        queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.addNamespaceToHandle(testNamespaceUri2);
        queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.addNamespaceToHandle(testNamespaceUri3);

        queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll = getNewTestQueryType();
        queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.setNamespaceMatchMethod(getAllNamespaceMatchMethodUri());
        queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.setIsNamespaceSpecific(false);
        queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.setHandleAllNamespaces(true);
        queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.addNamespaceToHandle(testNamespaceUri1);
        queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.addNamespaceToHandle(testNamespaceUri2);
        queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.addNamespaceToHandle(testNamespaceUri3);
        
        queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny = getNewTestQueryType();
        queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.setNamespaceMatchMethod(getAnyNamespaceMatchMethodUri());
        queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.setIsNamespaceSpecific(true);
        queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.setHandleAllNamespaces(true);

        queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll = getNewTestQueryType();
        queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.setNamespaceMatchMethod(getAllNamespaceMatchMethodUri());
        queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.setIsNamespaceSpecific(true);
        queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.setHandleAllNamespaces(true);

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        testTrueQueryTypeUri = null;
        testFalseQueryTypeUri = null;
        testNamespaceUri1 = null;
        testNamespaceUri2 = null;
        testNamespaceUri3 = null;
        testNamespaceUri4 = null;
        testNamespaceUri5 = null;
        testFalseNamespaceUri = null;        

        queryTypePublicIdentifiers = null;
    }

    /**
     * Test method for {@link org.queryall.api.QueryType#isInputVariablePublic(int)}.
     */
    @Test
    public void testIsInputVariablePublic()
    {
        assertTrue(queryTypePublicIdentifiers.isInputVariablePublic(2));
        assertFalse(queryTypePublicIdentifiers.isInputVariablePublic(1));
    }

    /**
     * Test method for {@link org.queryall.api.QueryType#handlesNamespaceUris(java.util.Collection)}.
     * 
     * This test does not require the namespaces to be specifically declared for the test to succeed
     */
    @Test
    public void testHandlesNamespaceUris()
    {
        assertTrue(queryType123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces123));
        assertTrue(queryType123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces12));
        assertTrue(queryType123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces1and2orFalse));
        assertTrue(queryType123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces1orFalse));
        assertFalse(queryType123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces12345));
        assertFalse(queryType123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces34));
        assertFalse(queryType123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces45));
        assertFalse(queryType123NamespacesMatchMethodAll.handlesNamespaceUris(namespacesFalse));
        assertFalse(queryType123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces12345AndFalse));

        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces123));
        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces12));
        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces1and2orFalse));
        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces1orFalse));
        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces12345));
        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces34));
        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces45));
        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(namespacesFalse));
        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces12345AndFalse));
    
        assertTrue(queryType12345NamespacesMatchMethodAll.handlesNamespaceUris(namespaces123));
        assertTrue(queryType12345NamespacesMatchMethodAll.handlesNamespaceUris(namespaces12));
        assertTrue(queryType12345NamespacesMatchMethodAll.handlesNamespaceUris(namespaces1and2orFalse));
        assertTrue(queryType12345NamespacesMatchMethodAll.handlesNamespaceUris(namespaces1orFalse));
        assertTrue(queryType12345NamespacesMatchMethodAll.handlesNamespaceUris(namespaces12345));
        assertTrue(queryType12345NamespacesMatchMethodAll.handlesNamespaceUris(namespaces34));
        assertTrue(queryType12345NamespacesMatchMethodAll.handlesNamespaceUris(namespaces45));
        assertFalse(queryType12345NamespacesMatchMethodAll.handlesNamespaceUris(namespacesFalse));
        assertFalse(queryType12345NamespacesMatchMethodAll.handlesNamespaceUris(namespaces12345AndFalse));
    
        
        assertTrue(queryType123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces123));
        assertTrue(queryType123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces12));
        assertTrue(queryType123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces1and2orFalse));
        assertTrue(queryType123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces1orFalse));
        assertTrue(queryType123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces12345));
        assertTrue(queryType123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces34));
        assertFalse(queryType123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces45));
        assertFalse(queryType123NamespacesMatchMethodAny.handlesNamespaceUris(namespacesFalse));
        assertTrue(queryType123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces12345AndFalse));

        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces123));
        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces12));
        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces1and2orFalse));
        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces1orFalse));
        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces12345));
        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces34));
        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces45));
        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(namespacesFalse));
        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces12345AndFalse));
    
        assertTrue(queryType12345NamespacesMatchMethodAny.handlesNamespaceUris(namespaces123));
        assertTrue(queryType12345NamespacesMatchMethodAny.handlesNamespaceUris(namespaces12));
        assertTrue(queryType12345NamespacesMatchMethodAny.handlesNamespaceUris(namespaces1and2orFalse));
        assertTrue(queryType12345NamespacesMatchMethodAny.handlesNamespaceUris(namespaces1orFalse));
        assertTrue(queryType12345NamespacesMatchMethodAny.handlesNamespaceUris(namespaces12345));
        assertTrue(queryType12345NamespacesMatchMethodAny.handlesNamespaceUris(namespaces34));
        assertTrue(queryType12345NamespacesMatchMethodAny.handlesNamespaceUris(namespaces45));
        assertFalse(queryType12345NamespacesMatchMethodAny.handlesNamespaceUris(namespacesFalse));
        assertTrue(queryType12345NamespacesMatchMethodAny.handlesNamespaceUris(namespaces12345AndFalse));
    
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces123));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces12));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces1and2orFalse));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces1orFalse));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces12345));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces34));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces45));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespacesFalse));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces12345AndFalse));
    
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces123));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces12));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces1and2orFalse));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces1orFalse));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces12345));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces34));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces45));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespacesFalse));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces12345AndFalse));
    
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces123));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces12));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces1and2orFalse));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces1orFalse));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces12345));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces34));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces45));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespacesFalse));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces12345AndFalse));
    
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces123));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces12));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces1and2orFalse));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces1orFalse));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces12345));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces34));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces45));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespacesFalse));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces12345AndFalse));
    
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces123));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces12));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces1and2orFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces1orFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces12345));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces34));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces45));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespacesFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces12345AndFalse));
    
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces123));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces12));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces1and2orFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces1orFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces12345));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces34));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces45));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespacesFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces12345AndFalse));
    
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces123));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces12));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces1and2orFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces1orFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces12345));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces34));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces45));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespacesFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespaceUris(namespaces12345AndFalse));
    
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces123));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces12));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces1and2orFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces1orFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces12345));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces34));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces45));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespacesFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespaceUris(namespaces12345AndFalse));
    
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces123));
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces12));
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces1and2orFalse));
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces1orFalse));
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces12345));
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces34));
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces45));
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespaceUris(namespacesFalse));
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespaceUris(namespaces12345AndFalse));
        
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces123));
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces12));
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces1and2orFalse));
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces1orFalse));
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces12345));
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces34));
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces45));
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespaceUris(namespacesFalse));
        assertTrue(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespaceUris(namespaces12345AndFalse));
        
    }

    /**
     * Test method for {@link org.queryall.api.QueryType#handlesNamespacesSpecifically(java.util.Collection)}.
     *
     * This test requires the namespaces to be specifically declared for the test to succeed
     */
    @Test
    public void testHandlesNamespacesSpecifically()
    {
        assertTrue(queryType123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces123));
        assertTrue(queryType123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12));
        assertTrue(queryType123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces1and2orFalse));
        assertTrue(queryType123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces1orFalse));
        assertFalse(queryType123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12345));
        assertFalse(queryType123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces34));
        assertFalse(queryType123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces45));
        assertFalse(queryType123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespacesFalse));
        assertFalse(queryType123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12345AndFalse));

        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces123));
        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12));
        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces1and2orFalse));
        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces1orFalse));
        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12345));
        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces34));
        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces45));
        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespacesFalse));
        assertFalse(queryTypeNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12345AndFalse));
    
        assertTrue(queryType12345NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces123));
        assertTrue(queryType12345NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12));
        assertTrue(queryType12345NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces1and2orFalse));
        assertTrue(queryType12345NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces1orFalse));
        assertTrue(queryType12345NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12345));
        assertTrue(queryType12345NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces34));
        assertTrue(queryType12345NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces45));
        assertFalse(queryType12345NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespacesFalse));
        assertFalse(queryType12345NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12345AndFalse));
    
        
        assertTrue(queryType123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces123));
        assertTrue(queryType123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12));
        assertTrue(queryType123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces1and2orFalse));
        assertTrue(queryType123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces1orFalse));
        assertTrue(queryType123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12345));
        assertTrue(queryType123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces34));
        assertFalse(queryType123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces45));
        assertFalse(queryType123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespacesFalse));
        assertTrue(queryType123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12345AndFalse));

        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces123));
        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12));
        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces1and2orFalse));
        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces1orFalse));
        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12345));
        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces34));
        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces45));
        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespacesFalse));
        assertFalse(queryTypeNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12345AndFalse));
    
        assertTrue(queryType12345NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces123));
        assertTrue(queryType12345NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12));
        assertTrue(queryType12345NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces1and2orFalse));
        assertTrue(queryType12345NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces1orFalse));
        assertTrue(queryType12345NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12345));
        assertTrue(queryType12345NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces34));
        assertTrue(queryType12345NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces45));
        assertFalse(queryType12345NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespacesFalse));
        assertTrue(queryType12345NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12345AndFalse));
    
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces123));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces1and2orFalse));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces1orFalse));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12345));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces34));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces45));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespacesFalse));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12345AndFalse));
    
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces123));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces1and2orFalse));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces1orFalse));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12345));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces34));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces45));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespacesFalse));
        assertFalse(queryTypeNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12345AndFalse));
    
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces123));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces1and2orFalse));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces1orFalse));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12345));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces34));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces45));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespacesFalse));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12345AndFalse));
    
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces123));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces1and2orFalse));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces1orFalse));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12345));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces34));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces45));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespacesFalse));
        assertFalse(queryTypeNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12345AndFalse));
        
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces123));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces1and2orFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces1orFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12345));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces34));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces45));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespacesFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12345AndFalse));
    
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces123));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces1and2orFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces1orFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12345));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces34));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces45));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespacesFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecificNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12345AndFalse));
    
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces123));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces1and2orFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces1orFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12345));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces34));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces45));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespacesFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12345AndFalse));
    
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces123));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces1and2orFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces1orFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12345));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces34));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces45));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespacesFalse));
        assertFalse(queryTypeHandleAllNamespacesNotNamespaceSpecific123NamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12345AndFalse));
    
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces123));
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12));
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces1and2orFalse));
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces1orFalse));
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12345));
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces34));
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces45));
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespacesFalse));
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAny.handlesNamespacesSpecifically(namespaces12345AndFalse));
        
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces123));
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12));
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces1and2orFalse));
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces1orFalse));
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12345));
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces34));
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces45));
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespacesFalse));
        assertFalse(queryTypeHandleAllNamespacesNoNamespacesMatchMethodAll.handlesNamespacesSpecifically(namespaces12345AndFalse));
        
    }

}
