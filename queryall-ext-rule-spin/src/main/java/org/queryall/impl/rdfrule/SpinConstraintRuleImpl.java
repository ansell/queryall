package org.queryall.impl.rdfrule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.base.HtmlExport;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.SpinConstraintRule;
import org.queryall.api.rdfrule.SpinConstraintRuleSchema;
import org.queryall.api.rdfrule.ValidatingRuleSchema;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.exception.QueryAllException;
import org.queryall.exception.ValidationFailedException;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.spin.constraints.ConstraintViolation;
import org.topbraid.spin.constraints.SPINConstraints;
import org.topbraid.spin.statistics.SPINStatistics;
import org.topbraid.spin.system.SPINLabels;
import org.topbraid.spin.system.SPINModuleRegistry;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.compose.MultiUnion;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.ReificationStyle;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SpinConstraintRuleImpl extends BaseValidatingRuleImpl implements SpinConstraintRule, HtmlExport
{
    private static final Logger log = LoggerFactory.getLogger(SpinConstraintRuleImpl.class);
    private static final boolean TRACE = SpinConstraintRuleImpl.log.isTraceEnabled();
    private static final boolean DEBUG = SpinConstraintRuleImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = SpinConstraintRuleImpl.log.isInfoEnabled();
    
    private static final Set<URI> SPIN_CONSTRAINT_RULE_IMPL_TYPES = new HashSet<URI>(8);
    private static final Set<URI> SPIN_CONSTRAINT_RULE_IMPL_VALID_STAGES = new HashSet<URI>(8);
    
    static
    {
        SpinConstraintRuleImpl.SPIN_CONSTRAINT_RULE_IMPL_TYPES.add(NormalisationRuleSchema
                .getNormalisationRuleTypeUri());
        SpinConstraintRuleImpl.SPIN_CONSTRAINT_RULE_IMPL_TYPES.add(ValidatingRuleSchema.getValidatingRuleTypeUri());
        SpinConstraintRuleImpl.SPIN_CONSTRAINT_RULE_IMPL_TYPES.add(SpinConstraintRuleSchema
                .getSpinConstraintRuleTypeUri());
        
        // Need to initialise the SPIN registry at least once
        // SPINModuleRegistry.get().init();
        
        SpinConstraintRuleImpl.SPIN_CONSTRAINT_RULE_IMPL_VALID_STAGES.add(NormalisationRuleSchema
                .getRdfruleStageAfterQueryParsing());
        SpinConstraintRuleImpl.SPIN_CONSTRAINT_RULE_IMPL_VALID_STAGES.add(NormalisationRuleSchema
                .getRdfruleStageAfterResultsImport());
        SpinConstraintRuleImpl.SPIN_CONSTRAINT_RULE_IMPL_VALID_STAGES.add(NormalisationRuleSchema
                .getRdfruleStageAfterResultsToPool());
        
    }
    
    public static Set<URI> myTypes()
    {
        return SpinConstraintRuleImpl.SPIN_CONSTRAINT_RULE_IMPL_TYPES;
    }
    
    private Set<org.openrdf.model.URI> activeEntailments = new HashSet<URI>();
    
    private Set<String> localImports = new HashSet<String>(10);
    
    private Set<URI> urlImports = new HashSet<URI>(10);
    
    private List<OntModel> ontologyModels = new ArrayList<OntModel>(5);
    
    private volatile SPINModuleRegistry registry;
    
    public SpinConstraintRuleImpl()
    {
        super();
    }
    
    // keyToUse is the URI of the next instance that can be found in
    // myRepository
    public SpinConstraintRuleImpl(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = this.resetUnrecognisedStatements();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            // if(SparqlNormalisationRuleImpl.DEBUG)
            // {
            // SparqlNormalisationRuleImpl.log.debug("SparqlNormalisationRuleImpl: nextStatement: "
            // + nextStatement.toString());
            // }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(SpinConstraintRuleSchema.getSpinConstraintRuleTypeUri()))
            {
                if(SpinConstraintRuleImpl.TRACE)
                {
                    SpinConstraintRuleImpl.log.trace("SpinConstraintRuleImpl: found valid type predicate for URI: "
                            + keyToUse);
                }
                
                // isValid = true;
                this.setKey(keyToUse);
            }
            else
            {
                if(SpinConstraintRuleImpl.DEBUG)
                {
                    SpinConstraintRuleImpl.log.debug("SpinConstraintRuleImpl: unrecognisedStatement nextStatement: "
                            + nextStatement.toString());
                }
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        if(SpinConstraintRuleImpl.DEBUG)
        {
            SpinConstraintRuleImpl.log.debug("SpinConstraintRuleImpl constructor: toString()=" + this.toString());
        }
    }
    
    @Override
    public void addEntailmentUri(final URI nextEntailmentURI)
    {
        this.activeEntailments.add(nextEntailmentURI);
    }
    
    @Override
    public void addLocalImport(final String nextImport)
    {
        this.localImports.add(nextImport);
        
        final OntModel nextModel = SpinUtils.loadModelFromClasspath(nextImport);
        
        if(nextModel != null)
        {
            SpinConstraintRuleImpl.log.info("adding model to registry and ontology model list nextImport=" + nextImport
                    + " nextModel.size()=" + nextModel.size());
            this.ontologyModels.add(nextModel);
            this.getSpinModuleRegistry().registerAll(nextModel, nextImport);
        }
        else
        {
            SpinConstraintRuleImpl.log.error("Failed to load import from URL nextImport=" + nextImport);
        }
        
        this.getSpinModuleRegistry().init();
    }
    
    @Override
    public void addUrlImport(final URI nextURLImport)
    {
        this.urlImports.add(nextURLImport);
        
        final OntModel nextModel = SpinUtils.loadModelFromUrl(nextURLImport.stringValue());
        
        if(nextModel != null)
        {
            SpinConstraintRuleImpl.log.info("adding model to registry and ontology model list nextImport="
                    + nextURLImport.stringValue() + " nextModel.size()=" + nextModel.size());
            this.ontologyModels.add(nextModel);
            this.getSpinModuleRegistry().registerAll(nextModel, nextURLImport.stringValue());
        }
        else
        {
            SpinConstraintRuleImpl.log.error("Failed to load import from URL nextURLImport="
                    + nextURLImport.stringValue());
        }
        
        this.getSpinModuleRegistry().init();
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Set<URI> getElementTypes()
    {
        return SpinConstraintRuleImpl.myTypes();
    }
    
    @Override
    public Set<URI> getEntailmentUris()
    {
        return Collections.unmodifiableSet(this.activeEntailments);
    }
    
    @Override
    public Set<String> getLocalImports()
    {
        return Collections.unmodifiableSet(this.localImports);
    }
    
    public SPINModuleRegistry getSpinModuleRegistry()
    {
        return SPINModuleRegistry.get();
        // if(registry == null)
        // {
        // synchronized(this)
        // {
        // if(registry == null)
        // {
        // log.info("registry was not set, setting up a new registry before returning");
        //
        // //SPINThreadFunctionRegistry tempFunctionRegistry1 = new
        // SPINThreadFunctionRegistry(FunctionRegistry.standardRegistry());
        //
        // //SPINModuleRegistry tempSpinModuleRegistry1 = new
        // SPINModuleRegistry()//FunctionRegistry.get());
        //
        // // TODO: is it rational to have a circular dependency like this?
        // // tempFunctionRegistry1.setSpinModuleRegistry(tempSpinModuleRegistry1);
        //
        // // FIXME TODO: how do we get around this step
        // // Jena/ARQ seems to be permanently setup around the use of this global context,
        // // even though FunctionEnv and Context seem to be in quite a few method headers
        // // throughout their code base
        // // Is it necessary for users to setup functions that are not globally named and visible
        // in the same way that they need to be able to setup rules that may not be globally useful
        // // ARQ.getContext().set(ARQConstants.registryFunctions, tempFunctionRegistry1);
        //
        // tempSpinModuleRegistry1.init();
        //
        // registry = tempSpinModuleRegistry1;
        // }
        // }
        // }
        //
        // return registry;
    }
    
    @Override
    public Set<URI> getURLImports()
    {
        return Collections.unmodifiableSet(this.urlImports);
    }
    
    @Override
    public boolean isEntailmentEnabled(final URI entailmentURI)
    {
        return this.activeEntailments.contains(entailmentURI);
    }
    
    @Override
    public boolean resetEntailmentUris()
    {
        try
        {
            this.activeEntailments.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            SpinConstraintRuleImpl.log.debug("Could not clear collection");
        }
        
        this.activeEntailments = new HashSet<URI>();
        
        return true;
    }
    
    @Override
    public boolean resetLocalImports()
    {
        try
        {
            this.localImports.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            SpinConstraintRuleImpl.log.debug("Could not clear collection");
        }
        
        this.localImports = new HashSet<String>();
        
        return true;
    }
    
    @Override
    public boolean resetUrlImports()
    {
        try
        {
            this.urlImports.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            SpinConstraintRuleImpl.log.debug("Could not clear collection");
        }
        
        this.urlImports = new HashSet<URI>();
        
        return true;
    }
    
    public boolean runTests(final Collection<RuleTest> myRules)
    {
        // TODO: implement me or delete me!
        final boolean allPassed = true;
        
        // final Collection<RuleTest> myRules =
        // Settings.getRuleTestsForNormalisationRuleUri(this.getKey());
        
        // for(final RuleTest nextRuleTest : myRules)
        // {
        //
        // }
        
        return allPassed;
    }
    
    public void setSpinModuleRegistry(final SPINModuleRegistry registry)
    {
        this.registry = registry;
        this.registry.init();
    }
    
    @Override
    protected Set<URI> setupValidStages()
    {
        return SpinConstraintRuleImpl.SPIN_CONSTRAINT_RULE_IMPL_VALID_STAGES;
    }
    
    /**
     * @param input
     * @return
     * @throws ValidationFailedException
     */
    private boolean spinConstraintHelper(final Repository input, final Resource... contexts)
        throws ValidationFailedException
    {
        // TODO: how should the varargs context parameter be supported
        List<ConstraintViolation> verifySpinConstraints = null;
        
        try
        {
            verifySpinConstraints = this.verifySpinConstraints(input, contexts);
            
        }
        catch(final QueryAllException e)
        {
            throw new ValidationFailedException("", this, e);
        }
        
        if(verifySpinConstraints.size() > 0)
        {
            // TODO: customise behaviour in cases where spinConstraint failures are found
            return false;
        }
        else
        {
            return true;
        }
    }
    
    @Override
    public boolean stageAfterQueryCreation(final Object input) throws ValidationFailedException
    {
        return true;
    }
    
    @Override
    public boolean stageAfterQueryParsing(final Object input) throws ValidationFailedException
    {
        // TODO: implement me as a way of manipulating parsed SPARQL queries
        return true;
    }
    
    @Override
    public boolean stageAfterResultsImport(final Object input) throws ValidationFailedException
    {
        return this.spinConstraintHelper((Repository)input);
        
    }
    
    @Override
    public boolean stageAfterResultsToDocument(final Object input) throws ValidationFailedException
    {
        return true;
    }
    
    @Override
    public boolean stageAfterResultsToPool(final Object input) throws ValidationFailedException
    {
        return this.spinConstraintHelper((Repository)input);
    }
    
    @Override
    public boolean stageBeforeResultsImport(final Object input) throws ValidationFailedException
    {
        return true;
    }
    
    @Override
    public boolean stageQueryVariables(final Object input) throws ValidationFailedException
    {
        return true;
    }
    
    @Override
    public String toHtml()
    {
        String result = "";
        
        result +=
                "<div class=\"rulekey\">Rule Key: " + StringUtils.xmlEncodeString(this.getKey().stringValue())
                        + "</div>\n";
        result +=
                "<div class=\"description\">Description: " + StringUtils.xmlEncodeString(this.getDescription())
                        + "</div>\n";
        result += "<div class=\"order\">Order: " + StringUtils.xmlEncodeString(this.getOrder() + "") + "</div>\n";
        
        return result;
    }
    
    @Override
    public String toHtmlFormBody()
    {
        final StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        final String prefix = "rdfrule_";
        
        return sb.toString();
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... keyToUse)
        throws OpenRDFException
    {
        super.toRdf(myRepository, modelVersion, keyToUse);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        try
        {
            if(SpinConstraintRuleImpl.DEBUG)
            {
                SpinConstraintRuleImpl.log.debug("SparqlNormalisationRuleImpl.toRdf: keyToUse=" + keyToUse);
            }
            
            final URI keyUri = this.getKey();
            
            con.setAutoCommit(false);
            
            con.add(keyUri, RDF.TYPE, SpinConstraintRuleSchema.getSpinConstraintRuleTypeUri(), keyToUse);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            SpinConstraintRuleImpl.log.error("RepositoryException: " + re.getMessage());
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
    
    @Override
    public String toString()
    {
        String result = "\n";
        
        result += "key=" + this.getKey() + "\n";
        result += "order=" + this.getOrder() + "\n";
        result += "description=" + this.getDescription() + "\n";
        
        return result;
    }
    
    /**
     * See OWLRLExample in spin-examples-1.2.0.jar
     * 
     * Currently limited to adding the resulting triples from the spin reasoning to the repository
     * 
     * TODO: add more modes, such as delete matching, add matching, only return matching triples
     * etc.
     * 
     * @param inputRepository
     *            The OpenRDF repository to use for the input triples
     * @throws QueryAllException
     */
    public List<ConstraintViolation> verifySpinConstraints(final Repository inputRepository,
            final org.openrdf.model.Resource... contexts) throws QueryAllException
    {
        SpinConstraintRuleImpl.log.info("Loading jena model from sesame repository");
        final OntModel queryModel =
                SpinUtils
                        .addSesameRepositoryToJenaModel(inputRepository,
                                ModelFactory.createDefaultModel(ReificationStyle.Minimal), "http://spin.example.org/",
                                contexts);
        
        // Create and add Model for inferred triples
        final Model newTriples = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
        queryModel.addSubModel(newTriples);
        
        SpinConstraintRuleImpl.log.info("Loading ontologies...");
        
        // Register any new functions defined in OWL RL
        // NOTE: The source for these rules is given as "this" so that they can be retrieved in
        // future based on this object
        
        // Build one big union Model of everything
        final Graph[] graphs = new Graph[this.ontologyModels.size() + 1];
        
        graphs[0] = queryModel.getGraph();
        
        int i = 1;
        
        for(final OntModel nextModel : this.ontologyModels)
        {
            SpinConstraintRuleImpl.log.info("i=" + i + " nextModel.size()=" + nextModel.size());
            graphs[i++] = nextModel.getGraph();
        }
        
        final MultiUnion multiUnion = new MultiUnion(graphs);
        
        final Model unionModel = ModelFactory.createModelForGraph(multiUnion);
        
        final Set<Object> allowedRuleSources = new HashSet<Object>();
        
        allowedRuleSources.addAll(this.localImports);
        
        final List<ConstraintViolation> cvs =
                SPINConstraints.check(unionModel, new ArrayList<SPINStatistics>(), null, OntModelSpec.OWL_MEM,
                        "http://topbraid.org/examples/kennedysSPIN", allowedRuleSources);
        
        SpinConstraintRuleImpl.log.info("Constraint violations:");
        
        for(final ConstraintViolation cv : cvs)
        {
            SpinConstraintRuleImpl.log
                    .info(" - at " + SPINLabels.get().getLabel(cv.getRoot()) + ": " + cv.getMessage());
        }
        
        // Note: To optimise the process, we only add the new triples back into the original
        // repository
        return cvs;
    }
    
}
