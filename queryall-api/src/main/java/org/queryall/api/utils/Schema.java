/**
 * 
 */
package org.queryall.api.utils;

import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.namespace.NamespaceEntrySchema;
import org.queryall.api.namespace.RegexValidatingNamespaceEntrySchema;
import org.queryall.api.namespace.ValidatingNamespaceEntrySchema;
import org.queryall.api.profile.ProfileSchema;
import org.queryall.api.project.ProjectSchema;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.provider.SparqlProviderSchema;
import org.queryall.api.querytype.QueryTypeSchema;
import org.queryall.api.querytype.RdfXmlOutputQueryTypeSchema;
import org.queryall.api.querytype.RegexInputQueryTypeSchema;
import org.queryall.api.querytype.SparqlProcessorQueryTypeSchema;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.OwlNormalisationRuleSchema;
import org.queryall.api.rdfrule.RdfsNormalisationRuleSchema;
import org.queryall.api.rdfrule.RegexNormalisationRuleSchema;
import org.queryall.api.rdfrule.SparqlNormalisationRuleSchema;
import org.queryall.api.rdfrule.XsltNormalisationRuleSchema;
import org.queryall.api.ruletest.RuleTestSchema;
import org.queryall.api.ruletest.SparqlRuleTestSchema;
import org.queryall.api.ruletest.StringRuleTestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class Schema
{
    private static final Logger log = LoggerFactory.getLogger(Schema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = Schema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = Schema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = Schema.log.isInfoEnabled();
    
    public static Repository getSchemas(final Repository myRepository, final int configVersion)
    {
        return Schema.getSchemas(myRepository, null, configVersion);
    }
    
    public static Repository getSchemas(final Repository myRepository, final URI contextUri, final int configVersion)
    {
        // Repository myRepository = new SailRepository(new ForwardChainingRDFSInferencer(new
        // MemoryStore()));
        // final Repository myRepository = new SailRepository(new MemoryStore());
        
        try
        {
            myRepository.initialize();
        }
        catch(final RepositoryException e)
        {
            Schema.log.error("Could not initialise repository for schemas");
            throw new RuntimeException(e);
        }
        
        try
        {
            if(!ProviderSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("Provider schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating Provider schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!HttpProviderSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("HttpProvider schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating HttpProvider schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!SparqlProviderSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("SparqlProvider schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating SparqlProvider schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!ProjectSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("Project schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating Project schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!QueryTypeSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("QueryType schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating QueryType schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!RegexInputQueryTypeSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("RegexInputQueryType schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating RegexInputQueryType schema RDF with type=" + ex.getClass().getName(),
                    ex);
        }
        
        try
        {
            if(!RdfXmlOutputQueryTypeSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("RdfXmlOutputQueryType schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error(
                    "Problem generating RdfXmlOutputQueryType schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!SparqlProcessorQueryTypeSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("SparqlProcessorQueryType schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating SparqlProcessorQueryType schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!NormalisationRuleSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("NormalisationRule schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log
                    .error("Problem generating NormalisationRule schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!OwlNormalisationRuleSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("OwlNormalisationRule schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating OwlNormalisationRule schema RDF with type=" + ex.getClass().getName(),
                    ex);
        }
        
        try
        {
            if(!RdfsNormalisationRuleSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("RdfsNormalisationRule schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error(
                    "Problem generating RdfsNormalisationRule schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!RegexNormalisationRuleSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("RegexNormalisationRuleImpl schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating RegexNormalisationRuleImpl schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!SparqlNormalisationRuleSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("SparqlNormalisationRuleImpl schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating SparqlNormalisationRuleImpl schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!XsltNormalisationRuleSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("XsltNormalisationRuleImpl schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating SparqlNormalisationRuleImpl schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!RuleTestSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("RuleTest schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating RuleTest schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!SparqlRuleTestSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("SparqlRuleTest schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating SparqlRuleTest schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!StringRuleTestSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("StringRuleTest schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating StringRuleTest schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!NamespaceEntrySchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("NamespaceEntry schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating NamespaceEntry schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!ValidatingNamespaceEntrySchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("ValidatingNamespaceEntry schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating ValidatingNamespaceEntry schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!RegexValidatingNamespaceEntrySchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("RegexValidatingNamespaceEntry schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating RegexValidatingNamespaceEntry schema RDF with type="
                    + ex.getClass().getName(), ex);
        }
        
        try
        {
            if(!ProfileSchema.schemaToRdf(myRepository, contextUri, configVersion))
            {
                Schema.log.error("Profile schema was not placed correctly in the rdf store");
            }
        }
        catch(final Exception ex)
        {
            Schema.log.error("Problem generating Profile schema RDF with type=" + ex.getClass().getName(), ex);
        }
        
        // try
        // {
        // if(!StatisticsEntry.schemaToRdf(myRepository, contextUri, configVersion))
        // {
        // Schema.log.error("Statistics schema was not placed correctly in the rdf store");
        // }
        // }
        // catch(final Exception ex)
        // {
        // Schema.log.error("Problem generating Statistics schema RDF with type=" +
        // ex.getClass().getName(), ex);
        // }
        //
        // try
        // {
        // if(!ProvenanceRecord.schemaToRdf(myRepository, contextUri, configVersion))
        // {
        // Schema.log.error("Provenance schema was not placed correctly in the rdf store");
        // }
        // }
        // catch(final Exception ex)
        // {
        // Schema.log.error("Problem generating Provenance schema RDF with type=" +
        // ex.getClass().getName(), ex);
        // }
        //
        // try
        // {
        // if(!QueryBundle.schemaToRdf(myRepository, contextUri, configVersion))
        // {
        // Schema.log.error("QueryBundle schema was not placed correctly in the rdf store");
        // }
        // }
        // catch(final Exception ex)
        // {
        // Schema.log.error("Problem generating QueryBundle schema RDF with type=" +
        // ex.getClass().getName(), ex);
        // }
        
        return myRepository;
    }
    
}
