/**
 * 
 */
package org.queryall.api.rdfrule;

import org.kohsuke.MetaInfServices;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.base.QueryAllSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllSchema.class)
public class RegexNormalisationRuleSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(RegexNormalisationRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = RegexNormalisationRuleSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = RegexNormalisationRuleSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = RegexNormalisationRuleSchema.LOG.isInfoEnabled();
    
    private static URI regexruleTypeUri;
    
    private static URI regexruleInputMatchRegex;
    
    private static URI regexruleInputReplaceRegex;
    
    private static URI regexruleOutputMatchRegex;
    
    private static URI regexruleOutputReplaceRegex;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        RegexNormalisationRuleSchema.setRegexRuleTypeUri(f.createURI(baseUri, "RegexNormalisationRule"));
        RegexNormalisationRuleSchema.setRegexRuleInputMatchRegex(f.createURI(baseUri, "inputMatchRegex"));
        RegexNormalisationRuleSchema.setRegexRuleInputReplaceRegex(f.createURI(baseUri, "inputReplaceRegex"));
        RegexNormalisationRuleSchema.setRegexRuleOutputMatchRegex(f.createURI(baseUri, "outputMatchRegex"));
        RegexNormalisationRuleSchema.setRegexRuleOutputReplaceRegex(f.createURI(baseUri, "outputReplaceRegex"));
    }
    
    /**
     * The pre-instantiated schema object for RegexNormalisationRuleSchema.
     */
    public static final QueryAllSchema REGEX_NORMALISATION_RULE_SCHEMA = new RegexNormalisationRuleSchema();
    
    /**
     * @return the rdfruleInputMatchRegex
     */
    public static URI getRegexRuleInputMatchRegex()
    {
        return RegexNormalisationRuleSchema.regexruleInputMatchRegex;
    }
    
    /**
     * @return the rdfruleInputReplaceRegex
     */
    public static URI getRegexRuleInputReplaceRegex()
    {
        return RegexNormalisationRuleSchema.regexruleInputReplaceRegex;
    }
    
    /**
     * @return the rdfruleOutputMatchRegex
     */
    public static URI getRegexRuleOutputMatchRegex()
    {
        return RegexNormalisationRuleSchema.regexruleOutputMatchRegex;
    }
    
    /**
     * @return the rdfruleOutputReplaceRegex
     */
    public static URI getRegexRuleOutputReplaceRegex()
    {
        return RegexNormalisationRuleSchema.regexruleOutputReplaceRegex;
    }
    
    /**
     * @return the regexruleTypeUri
     */
    public static URI getRegexRuleTypeUri()
    {
        return RegexNormalisationRuleSchema.regexruleTypeUri;
    }
    
    /**
     * @param rdfruleInputMatchRegex
     *            the rdfruleInputMatchRegex to set
     */
    public static void setRegexRuleInputMatchRegex(final URI rdfruleInputMatchRegex)
    {
        RegexNormalisationRuleSchema.regexruleInputMatchRegex = rdfruleInputMatchRegex;
    }
    
    /**
     * @param rdfruleInputReplaceRegex
     *            the rdfruleInputReplaceRegex to set
     */
    public static void setRegexRuleInputReplaceRegex(final URI rdfruleInputReplaceRegex)
    {
        RegexNormalisationRuleSchema.regexruleInputReplaceRegex = rdfruleInputReplaceRegex;
    }
    
    /**
     * @param rdfruleOutputMatchRegex
     *            the rdfruleOutputMatchRegex to set
     */
    public static void setRegexRuleOutputMatchRegex(final URI rdfruleOutputMatchRegex)
    {
        RegexNormalisationRuleSchema.regexruleOutputMatchRegex = rdfruleOutputMatchRegex;
    }
    
    /**
     * @param rdfruleOutputReplaceRegex
     *            the rdfruleOutputReplaceRegex to set
     */
    public static void setRegexRuleOutputReplaceRegex(final URI rdfruleOutputReplaceRegex)
    {
        RegexNormalisationRuleSchema.regexruleOutputReplaceRegex = rdfruleOutputReplaceRegex;
    }
    
    /**
     * @param nextRegexruleTypeUri
     *            the regexruleTypeUri to set
     */
    public static void setRegexRuleTypeUri(final URI nextRegexruleTypeUri)
    {
        RegexNormalisationRuleSchema.regexruleTypeUri = nextRegexruleTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public RegexNormalisationRuleSchema()
    {
        this(RegexNormalisationRuleSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public RegexNormalisationRuleSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contextUri)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            con.begin();
            
            con.add(RegexNormalisationRuleSchema.getRegexRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(RegexNormalisationRuleSchema.getRegexRuleTypeUri(), RDFS.SUBCLASSOF,
                    TransformingRuleSchema.getTransformingRuleTypeUri(), contextUri);
            con.add(RegexNormalisationRuleSchema.getRegexRuleTypeUri(),
                    RDFS.LABEL,
                    f.createLiteral("A regular expression based normalisation rule intended to denormalise parts of queries to match endpoints, and renormalise the output of the query to match the normalised form."),
                    contextUri);
            
            con.add(RegexNormalisationRuleSchema.getRegexRuleInputMatchRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextUri);
            con.add(RegexNormalisationRuleSchema.getRegexRuleInputMatchRegex(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(RegexNormalisationRuleSchema.getRegexRuleInputMatchRegex(), RDFS.DOMAIN,
                    RegexNormalisationRuleSchema.getRegexRuleTypeUri(), contextUri);
            con.add(RegexNormalisationRuleSchema.getRegexRuleInputMatchRegex(),
                    RDFS.LABEL,
                    f.createLiteral("The input regular expression that is used to identify whether the denormalisation part of the rule matches the data, and if so, whether there are any matching groups that need to be substitued into the replacement pattern."),
                    contextUri);
            
            con.add(RegexNormalisationRuleSchema.getRegexRuleInputReplaceRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextUri);
            con.add(RegexNormalisationRuleSchema.getRegexRuleInputReplaceRegex(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(RegexNormalisationRuleSchema.getRegexRuleInputReplaceRegex(), RDFS.DOMAIN,
                    RegexNormalisationRuleSchema.getRegexRuleTypeUri(), contextUri);
            con.add(RegexNormalisationRuleSchema.getRegexRuleInputReplaceRegex(),
                    RDFS.LABEL,
                    f.createLiteral("The pattern that is used together with the input match regular expression to define what the denormalised data should be."),
                    contextUri);
            
            con.add(RegexNormalisationRuleSchema.getRegexRuleOutputMatchRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextUri);
            con.add(RegexNormalisationRuleSchema.getRegexRuleOutputMatchRegex(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(RegexNormalisationRuleSchema.getRegexRuleOutputMatchRegex(), RDFS.DOMAIN,
                    RegexNormalisationRuleSchema.getRegexRuleTypeUri(), contextUri);
            con.add(RegexNormalisationRuleSchema.getRegexRuleOutputMatchRegex(),
                    RDFS.LABEL,
                    f.createLiteral("The output regular expression that is used to identify whether the renormalisation part of the rule matches the data, and if so, whether there are any matching groups that need to be substitued into the replacement pattern."),
                    contextUri);
            
            con.add(RegexNormalisationRuleSchema.getRegexRuleOutputReplaceRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextUri);
            con.add(RegexNormalisationRuleSchema.getRegexRuleOutputReplaceRegex(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(RegexNormalisationRuleSchema.getRegexRuleOutputReplaceRegex(), RDFS.DOMAIN,
                    RegexNormalisationRuleSchema.getRegexRuleTypeUri(), contextUri);
            con.add(RegexNormalisationRuleSchema.getRegexRuleOutputReplaceRegex(),
                    RDFS.LABEL,
                    f.createLiteral("The pattern that is used together with the output match regular expression to define what the normalised data should be."),
                    contextUri);
            
            // for(String nextValidStage : validStages)
            // {
            // con.add(RegexNormalisationRuleSchema.regexruleTypeUri,
            // NormalisationRule.rdfruleTypeValidForStage,
            // f.createURI(nextValidStage), contextKeyUri);
            // }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            if(con != null)
            {
                con.rollback();
            }
            
            RegexNormalisationRuleSchema.LOG.error("RepositoryException: " + re.getMessage());
        }
        finally
        {
            if(con != null)
            {
                con.close();
            }
        }
        
        return false;
    }
}
