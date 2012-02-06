/**
 * 
 */
package org.queryall.api.utils;

/**
 * Sort constants for use in sorting based on the order attribute on some queryall objects.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public enum SortOrder
{
    /**
     * Specifies that objects with lesser orders, for example, 1, compared to 100, should be sorted
     * first.
     */
    LOWEST_ORDER_FIRST,
    
    /**
     * Specifies that objects with larger orders, for example, 100, compared to 1, should be sorted
     * first.
     */
    HIGHEST_ORDER_FIRST;
}
