/**
 * 
 */
package org.queryall.api.services;

import org.queryall.api.BaseQueryAllInterface;

/**
 * 
 * @param <E> Required to implement the QueryAllEnum interface
 * @param <P> Required to implement the QueryAllParser interface
 * @param <Y> Required to implement the BaseQueryAllInterface, in order to enforce the contract that the QueryAllParser, P, is a parser of BaseQueryAllInterface objects
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface QueryAllFactory<E extends QueryAllEnum, P extends QueryAllParser<Y>, Y extends BaseQueryAllInterface>
{
    E getEnum();

    P getParser();
}
