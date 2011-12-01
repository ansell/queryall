package org.queryall.api.namespace;

import java.util.Collection;

import org.openrdf.model.URI;
import org.queryall.api.base.BaseQueryAllInterface;

/**
 * Tracks namespaces that can be referenced from queries using prefixes that appear in queries. It
 * provides a many to many relation between query types and providers, as the same prefix can be
 * used on different namespaces and different namespaces can be used on both query types and
 * providers.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface NamespaceEntry extends BaseQueryAllInterface, Comparable<NamespaceEntry>
{
    /**
     * Adds an alternate prefix to this namespace entry that can be used in query types to map a
     * query as relevant to this namespace.
     * 
     * @param alternativePrefix
     *            A prefix that is used in query types to identify this namespace.
     */
    void addAlternativePrefix(String alternativePrefix);
    
    /**
     * 
     * @return True if the alternative prefixes list was reset and false otherwise.
     */
    boolean resetAlternativePrefixes();
    
    /**
     * 
     * @return A list of strings denoting the alternate inputs that map queries to this namespace.
     */
    Collection<String> getAlternativePrefixes();
    
    /**
     * 
     * @return A URI denoting the authority that controls this namespace entry. This may not be the
     *         authority that originally published the data in this namespace.
     */
    URI getAuthority();
    
    /**
     * 
     * @return True if the alternate prefixes should be rewritten using the preferred prefix for
     *         this namespace.
     */
    boolean getConvertQueriesToPreferredPrefix();
    
    /**
     * 
     * @return The string that is the preferred method of indicating that a query maps as relevant
     *         to this namespace.
     */
    String getPreferredPrefix();
    
    /**
     * 
     * @return A string that is used to separate the namespace from identifiers when entries in this
     *         namespace are serialised to URIs.
     */
    String getSeparator();
    
    /**
     * 
     * @return A string containing template items that are replaced in the process of serialising
     *         items in this namespace to URIs.
     */
    String getUriTemplate();
    
    /**
     * 
     * @param authority
     *            A URI denoting the authority that controls this namespace entry. This may not be
     *            the authority that originally published the data in this namespace.
     */
    void setAuthority(URI authority);
    
    /**
     * 
     * @param convertQueriesToPreferredPrefix
     *            True if matches for the alternate prefixes should be converted to the preferred
     *            prefix.
     */
    void setConvertQueriesToPreferredPrefix(boolean convertQueriesToPreferredPrefix);
    
    /**
     * 
     * @param preferredPrefix
     *            The preferred prefix to use to identify this namespace entry in queries.
     */
    void setPreferredPrefix(String preferredPrefix);
    
    /**
     * 
     * @param separator
     *            A string that is used to separate the namespace from identifiers when entries in
     *            this namespace are serialised to URIs.
     */
    void setSeparator(String separator);
    
    /**
     * 
     * @param uriTemplate
     *            A string containing template items that are replaced in the process of serialising
     *            items in this namespace to URIs.
     */
    void setUriTemplate(String uriTemplate);
}
