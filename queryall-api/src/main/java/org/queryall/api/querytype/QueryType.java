package org.queryall.api.querytype;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.base.BaseQueryAllInterface;
import org.queryall.api.base.ProfilableInterface;

/**
 * The base type for queries, containing a variety of links to other query types, namespace
 * matching, and named input tags.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface QueryType extends BaseQueryAllInterface, Comparable<QueryType>, ProfilableInterface
{
    /**
     * Adds a link to another query type that is linked to this one and which uses the same input parameter names as this one, so that it can be used together with this query type to generate useful output.
     * 
     * @param semanticallyLinkedQueryTypes The URI of a query type that is semantically linked to this query type and which uses the same input parameter names
     */
    void addLinkedQueryType(URI semanticallyLinkedQueryTypes);
    
    /**
     * Adds a string indicating that the given input tag will contain namespace prefixes that are relevant to this query type.
     * 
     * @param namespaceInputTag A string used to identify an input tag that is known to represent a namespace for this query type.
     */
    void addNamespaceInputTag(String namespaceInputTag);
    
    /**
     * Adds a URI indicating a namespace that can be handled by this query type.
     * 
     * @param namespaceToHandle A URI indicating a namespace that can be handled by this query type.
     */
    void addNamespaceToHandle(URI namespaceToHandle);
    
    /**
     * Adds a string indicating that the given input tag represents a publicly recognisable identifier.
     * 
     * An example of a publicly recognisable identifier is a namespace prefix.
     * 
     * @param publicIdentifierTag A string used to identify an input tag that is known to represent a publicly recognisable identifier for this query type.
     */
    void addPublicIdentifierTag(String publicIdentifierTag);
    
    /**
     * 
     * @return True if this query type is able to handle all namespaces, and false otherwise.
     */
    boolean getHandleAllNamespaces();
    
    /**
     * 
     * If this variable is false, then this query type will be excluded from use on default providers if it is namespace specific. If this query type is not namespace specific, this property has no effect.
     * 
     * @return True if this query type is applicable to default providers, and false otherwise.
     */
    boolean getIncludeDefaults();
    
    /**
     * If this query type is identified as an expensive or sensitive query type, then this may be indicated using this property. This property generally indicates that the query should be restricted from robot use, according to the generally accepted robots.txt convention. 
     * 
     * @return True if this query type is expensive, and should not be executed by robots.
     */
    boolean getInRobotsTxt();
    
    /**
     * If this query type represents an incomplete, dummy query, that is not useful on its own, than this may be indicated using this property. This property is used to identify whether there are any non-trivial query types matching a query. If all of the matching query types are dummy query types, than the query may return an error. In HTTP, this may correspond to a HTTP 400 error.
     * 
     * @return True if this querytype is not complete on its own, and may not be executed if other non-trivial query types do not also match a given query.
     */
    boolean getIsDummyQueryType();
    
    /**
     * If this query type contains information about namespaces, which can be used to restrict the number of providers to execute the query on, this property should be true.
     * 
     * If this query type may execute queries that contain namespaces as parameters, but this information should not be used to restrict the providers for this query, than this property should be false;
     * 
     * If this query type is true, than one or more namespace input tags must be defined to identify the parameters that contain the namespace information.
     * 
     * @return True if providers for this query type can be restricted using information from the parameters in the query.
     */
    boolean getIsNamespaceSpecific();
    
    /**
     * If this query can be executed using different results ranges to generate new results, than this property should be true.
     * 
     * If this query is invariant across different results ranges, it should be false to avoid the insertion of the same content multiple times.
     * 
     * @return True if this query can be paged using different results ranges, and false otherwise.
     */
    boolean getIsPageable();
    
    /**
     * 
     * @return A set of URIs denoting other query types which are semantically linked to this query type, and which must handle the same parameter names, so they can automatically be evaluated using the named parameters derived for this query type.
     */
    Set<URI> getLinkedQueryTypes();
    
    /**
     * 
     * NOTE: If getIsNamespaceSpecific() returns false, this method will always return an empty Set
     * 
     * If getHandleAllNamespaces() return true, this set WILL still be used to identify the namespaces to filter providers.
     * 
     * @return A set of Strings used to identify required named parameters which represent namespaces in queries performed using this query type.
     */
    Set<String> getNamespaceInputTags();
    
    /**
     * 
     * @return A URI denoting the method that is to be used to determine if a map of namespace parameter names to collections of matching namespace URIs will be determined to match with this query type.
     */
    URI getNamespaceMatchMethod();
    
    /**
     * 
     * If getIsNamespaceSpecific() returns false, this method will always return an empty Set
     * 
     * If getIsNamespaceSpecific() and getHandleAllNamespaces() return true, this set will not be used to identify matching namespaces.
     * 
     * @return Returns a set of URIs indicating which namespace entries are known to match with this query type. 
     */
    Set<URI> getNamespacesToHandle();
    
    Set<String> getPublicIdentifierTags();
    
    String getQueryUriTemplateString();
    
    String getStandardUriTemplateString();
    
    /**
     * This method calculates whether this query type handles the given map of parameter names to namespace entries.
     * 
     * If getIsNamespaceSpecific() returns false, this query must always return false.
     * 
     * The mechanism for deciding whether this query type handles the given map is specified by the URI returned from getNamespaceMatchMethod().
     * 
     * @param namespacesToCheck A Map containing parameter names as keys, and collections of URIs denoting namespace entries that have been matched to each parameter name.
     * @return True if this query type specifically handles the given namespaces, and false otherwise.
     * @throws IllegalArgumentException if the given namespaces map is null
     */
    boolean handlesNamespacesSpecifically(Map<String, Collection<URI>> namespacesToCheck);
    
    /**
     * This method calculates whether this query type is either ambivalent to the given namespaces, or it specifically handles them.
     * 
     * If getHandleAllNamespaces() returns true and getIsNamespaceSpecific() returns true, this query must always return true.
     * 
     * The mechanism for deciding whether this query type handles the given map is specified by the URI returned from getNamespaceMatchMethod().
     * 
     * @param namespacesToCheck A Map containing parameter names as keys, and collections of URIs denoting namespace entries that have been matched to each parameter name.
     * @return True if this query type is not namespace specific, or if it is namespace specific, it handles the given namespaces. Returns false if the query is namespace specific and it does not handle the given namespaces.
     * @throws IllegalArgumentException if the given namespaces map is null
     */
    boolean handlesNamespaceUris(Map<String, Collection<URI>> namespacesToCheck);
    
    /**
     * 
     * If getIsNamespaceSpecific() returns false, this query must always return false.
     * 
     * @param nextMatchTag A string denoting a parameter tag that needs to be identified as a namespace or not a namespace.
     * @return True if the given parameter name is identified as a namespace by this query, and false otherwise.
     */
    boolean isInputVariableNamespace(String nextMatchTag);
    
    /**
     * 
     * @param nextMatchTag A string denoting a parameter tag that needs to be identified as a public variable or not a public variable.
     * @return True if the given parameter name is identified as a public variable by this query, and false otherwise.
     */
    boolean isInputVariablePublic(String inputVariableTag);
    
    boolean resetLinkedQueryTypes();
    
    boolean resetNamespaceInputTags();
    
    boolean resetNamespacesToHandle();
    
    boolean resetPublicIdentifierTags();
    
    void setHandleAllNamespaces(boolean handleAllNamespaces);
    
    void setIncludeDefaults(boolean includeDefaults);
    
    void setInRobotsTxt(boolean inRobotsTxt);
    
    void setIsDummyQueryType(boolean isDummyQueryType);
    
    void setIsNamespaceSpecific(boolean isNamespaceSpecific);
    
    void setIsPageable(boolean isPageable);
    
    void setNamespaceMatchMethod(URI namespaceMatchMethod);
    
    void setQueryUriTemplateString(String queryUriTemplateString);
    
    void setStandardUriTemplateString(String standardUriTemplateString);
    
}
