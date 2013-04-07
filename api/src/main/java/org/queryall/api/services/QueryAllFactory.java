/**
 * 
 */
package org.queryall.api.services;

import org.queryall.api.base.BaseQueryAllInterface;

/**
 * A Factory interface designed to provide access to parsers of BaseQueryAllInterface derived
 * objects using the declared enum, containing the relevant type URIs for the implementation of the
 * given interface.
 * 
 * @param <E>
 *            Required to implement the QueryAllEnum interface
 * @param <P>
 *            Required to implement the QueryAllParser interface
 * @param <Y>
 *            Required to implement the BaseQueryAllInterface, in order to enforce the contract that
 *            the QueryAllParser, P, is a parser of BaseQueryAllInterface objects
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface QueryAllFactory<E extends QueryAllEnum, P extends QueryAllParser<Y>, Y extends BaseQueryAllInterface>
{
    /**
     * 
     * @return The enum which specifies the type URIs that are relevant to this factory
     */
    E getEnum();
    
    /**
     * 
     * @return A parser instance that can create objects of the given type Y, which is an extension
     *         of the BaseQueryAllInterface
     */
    P getParser();
}
