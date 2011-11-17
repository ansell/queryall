/**
 * 
 */
package org.queryall.impl.provider;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.provider.HttpProvider;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class HttpProviderImpl extends ProviderImpl implements HttpProvider
{
    private static final Logger log = LoggerFactory.getLogger(HttpProviderImpl.class);
    private static final boolean _TRACE = HttpProviderImpl.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = HttpProviderImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = HttpProviderImpl.log.isInfoEnabled();
    
    private Collection<String> endpointUrls = new HashSet<String>();
    
    // Use these to include information based on whether or not the provider was actually used to
    // provide information for particular user queries
    // public Collection<String> providerQueryInclusions = new HashSet<String>();
    // public boolean onlyIncludeProviderQueryIfInformationReturned = true;
    
    private String acceptHeaderString = "";
    
    protected HttpProviderImpl()
    {
        super();
    }
    
    protected HttpProviderImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = new HashSet<Statement>();
        
        currentUnrecognisedStatements.addAll(this.getUnrecognisedStatements());
        
        this.unrecognisedStatements = new HashSet<Statement>();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(HttpProviderImpl._TRACE)
            {
                HttpProviderImpl.log.trace("HttpProvider: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(HttpProviderSchema.getProviderHttpTypeUri()))
            {
                if(HttpProviderImpl._TRACE)
                {
                    HttpProviderImpl.log.trace("HttpProvider: found valid type predicate for URI: " + keyToUse);
                }
                
                // resultIsValid = true;
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(HttpProviderSchema.getProviderHttpAcceptHeader()))
            {
                this.setAcceptHeaderString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(HttpProviderSchema.getProviderHttpEndpointUrl()))
            {
                this.addEndpointUrl(nextStatement.getObject().stringValue());
            }
            else
            {
                if(HttpProviderImpl._TRACE)
                {
                    HttpProviderImpl.log.trace("HttpProvider: unrecognisedStatement nextStatement: "
                            + nextStatement.toString());
                }
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
    }
    
    @Override
    public void addEndpointUrl(final String endpointUrl)
    {
        if(this.endpointUrls == null)
        {
            this.endpointUrls = Collections.synchronizedSet(new HashSet<String>());
        }
        
        this.endpointUrls.add(endpointUrl);
    }
    
    @Override
    public String getAcceptHeaderString(final String defaultAcceptHeader)
    {
        if(this.acceptHeaderString != null && !this.acceptHeaderString.trim().equals(""))
        {
            return this.acceptHeaderString;
        }
        else
        {
            return defaultAcceptHeader;
        }
    }
    
    @Override
    public Collection<String> getEndpointUrls()
    {
        return this.endpointUrls;
    }
    
    @Override
    public boolean hasEndpointUrl()
    {
        return (this.getEndpointUrls() != null && this.getEndpointUrls().size() > 0);
    }
    
    @Override
    public boolean isHttpGetUrl()
    {
        return this.getEndpointMethod().equals(HttpProviderSchema.getProviderHttpGetUrl());
    }
    
    @Override
    public void setAcceptHeaderString(final String acceptHeaderString)
    {
        this.acceptHeaderString = acceptHeaderString;
    }
    
    @Override
    public void setEndpointUrls(final Collection<String> endpointUrls)
    {
        this.endpointUrls = endpointUrls;
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... keyToUse)
        throws OpenRDFException
    {
        super.toRdf(myRepository, modelVersion, keyToUse);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            con.setAutoCommit(false);
            
            if(HttpProviderImpl._TRACE)
            {
                HttpProviderImpl.log.trace("Provider.toRdf: keyToUse=" + keyToUse);
            }
            
            // create some resources and literals to make statements out of
            final URI providerInstanceUri = this.getKey();
            
            Literal acceptHeaderLiteral = null;
            
            if(this.acceptHeaderString != null && this.acceptHeaderString.trim().length() > 0)
            {
                acceptHeaderLiteral = f.createLiteral(this.acceptHeaderString);
            }
            
            con.setAutoCommit(false);
            
            con.add(providerInstanceUri, RDF.TYPE, ProviderSchema.getProviderTypeUri(), keyToUse);
            
            if(acceptHeaderLiteral != null)
            {
                con.add(providerInstanceUri, HttpProviderSchema.getProviderHttpAcceptHeader(), acceptHeaderLiteral,
                        keyToUse);
            }
            
            if(this.getEndpointUrls() != null)
            {
                for(final String nextEndpointUrl : this.getEndpointUrls())
                {
                    if(nextEndpointUrl != null)
                    {
                        con.add(providerInstanceUri, HttpProviderSchema.getProviderHttpEndpointUrl(),
                                f.createLiteral(nextEndpointUrl), keyToUse);
                    }
                }
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            HttpProviderImpl.log.error("RepositoryException: " + re.getMessage());
        }
        catch(final Exception ex)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            HttpProviderImpl.log.error("Exception.. this.getKey()=" + this.getKey().stringValue(), ex);
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
    
}
