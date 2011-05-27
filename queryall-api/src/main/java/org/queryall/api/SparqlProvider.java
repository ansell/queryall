package org.queryall.api;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface SparqlProvider
{
    public abstract String getSparqlGraphUri();

    public abstract void setSparqlGraphUri(String sparqlGraphUri);

    public abstract boolean getUseSparqlGraph();

    public abstract void setUseSparqlGraph(boolean useSparqlGraph);


}
