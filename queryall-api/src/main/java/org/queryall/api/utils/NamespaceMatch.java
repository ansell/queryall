/**
 * 
 */
package org.queryall.api.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.querytype.QueryTypeSchema;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public enum NamespaceMatch
{
    ANY_MATCHED(QueryTypeSchema.getQueryNamespaceMatchAny()),
    
    ALL_MATCHED(QueryTypeSchema.getQueryNamespaceMatchAll());
    
    /**
     * @param namespacesToCheck
     * @param namespacesToHandle
     *            TODO
     * @param namespaceMatchMethod
     *            TODO
     * @return
     * @throws RuntimeException
     */
    public static boolean matchNamespaces(final Map<String, Collection<URI>> namespacesToCheck,
            final Set<URI> namespacesToHandle, final NamespaceMatch namespaceMatchMethod) throws RuntimeException
    {
        // Start off presuming that anyMatched is false so any matches will flip it to true
        boolean anyMatched = false;
        
        // start off presuming that allMatched is true so any non-matches will flip it to false
        // this is safe for empty namespacesToCheck maps as an empty map is by definition all
        // matched
        boolean allMatched = true;
        
        // for each of the namespaces to check (represented by one or more URI's),
        // check that we have a locally handled namespace URI that matches
        // one of the URI's in each of the list of namespaces to check
        
        // if the map is empty, anyMatched needs to be flipped to true to match the expectation of
        // empty maps matching allMatched and anyMatched at least matching allMatched unless there
        // is evidence otherwise
        if(namespacesToCheck.size() == 0)
        {
            anyMatched = true;
        }
        else
        {
            for(final String nextParameter : namespacesToCheck.keySet())
            {
                final Collection<URI> nextNamespaceToCheckList = namespacesToCheck.get(nextParameter);
                
                if(nextNamespaceToCheckList == null)
                {
                    continue;
                }
                
                boolean matchFound = false;
                
                for(final URI nextLocalNamespace : namespacesToHandle)
                {
                    if(nextLocalNamespace == null)
                    {
                        continue;
                    }
                    
                    for(final URI nextNamespaceToCheck : nextNamespaceToCheckList)
                    {
                        if(nextNamespaceToCheck.equals(nextLocalNamespace))
                        {
                            matchFound = true;
                            break;
                        }
                    }
                }
                
                if(matchFound)
                {
                    anyMatched = true;
                    
                    if(namespaceMatchMethod.equals(NamespaceMatch.ANY_MATCHED))
                    {
                        break;
                    }
                }
                else
                {
                    allMatched = false;
                    
                    if(namespaceMatchMethod.equals(NamespaceMatch.ALL_MATCHED))
                    {
                        break;
                    }
                }
            }
        }
        
        if(namespaceMatchMethod.equals(NamespaceMatch.ANY_MATCHED))
        {
            return anyMatched;
        }
        else if(namespaceMatchMethod.equals(NamespaceMatch.ALL_MATCHED))
        {
            return allMatched;
        }
        else
        {
            throw new RuntimeException("Could not recognise the namespaceMatchMethod=" + namespaceMatchMethod);
        }
    }
    
    public static NamespaceMatch valueOf(final URI nextNamespaceMatchUri)
    {
        for(final NamespaceMatch nextEnum : NamespaceMatch.values())
        {
            if(nextEnum.getNamespaceMatchUri().equals(nextNamespaceMatchUri))
            {
                return nextEnum;
            }
        }
        
        return null;
    }
    
    private URI namespaceMatchUri = QueryTypeSchema.getQueryNamespaceMatchAny();
    
    NamespaceMatch(final URI namespaceMatchUri)
    {
        this.namespaceMatchUri = namespaceMatchUri;
    }
    
    /**
     * @return the namespaceMatchUri
     */
    public URI getNamespaceMatchUri()
    {
        return this.namespaceMatchUri;
    }
    
}
