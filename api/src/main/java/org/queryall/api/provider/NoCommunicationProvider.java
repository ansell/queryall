package org.queryall.api.provider;

/**
 * A Provider that does not require network or disk access to resolve queries.
 * 
 * It either performs queries in memory based on its configuration or on the configuration of a
 * query type that is linked to it.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface NoCommunicationProvider extends Provider
{
}
