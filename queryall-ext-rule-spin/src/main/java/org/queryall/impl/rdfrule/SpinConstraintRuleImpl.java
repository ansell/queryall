package org.queryall.impl.rdfrule;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
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
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.spin.inference.DefaultSPINRuleComparator;
import org.topbraid.spin.inference.SPINInferences;
import org.topbraid.spin.inference.SPINRuleComparator;
import org.topbraid.spin.util.CommandWrapper;
import org.topbraid.spin.util.SPINQueryFinder;
import org.topbraid.spin.vocabulary.SPIN;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.compose.MultiUnion;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.ReificationStyle;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SpinConstraintRuleImpl extends BaseSpinRuleImpl implements SpinConstraintRule, HtmlExport
{
    static final Logger log = LoggerFactory.getLogger(SpinConstraintRuleImpl.class);
    private static final boolean _TRACE = SpinConstraintRuleImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = SpinConstraintRuleImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SpinConstraintRuleImpl.log.isInfoEnabled();
    
    private static final Set<URI> SPIN_CONSTRAINT_RULE_IMPL_TYPES = new HashSet<URI>();
    
    static
    {
        SpinConstraintRuleImpl.SPIN_CONSTRAINT_RULE_IMPL_TYPES.add(NormalisationRuleSchema
                .getNormalisationRuleTypeUri());
        SpinConstraintRuleImpl.SPIN_CONSTRAINT_RULE_IMPL_TYPES.add(ValidatingRuleSchema.getValidatingRuleTypeUri());
        SpinConstraintRuleImpl.SPIN_CONSTRAINT_RULE_IMPL_TYPES.add(SpinConstraintRuleSchema
                .getSpinConstraintRuleTypeUri());
        
        // Need to initialise the SPIN registry at least once
        // SPINModuleRegistry.get().init();
    }
    
    public static Set<URI> myTypes()
    {
        return SpinConstraintRuleImpl.SPIN_CONSTRAINT_RULE_IMPL_TYPES;
    }
    
    private Set<org.openrdf.model.URI> activeEntailments;
    
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
        
        final Collection<Statement> currentUnrecognisedStatements = new HashSet<Statement>();
        
        currentUnrecognisedStatements.addAll(this.getUnrecognisedStatements());
        
        this.unrecognisedStatements = new HashSet<Statement>();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            // if(SparqlNormalisationRuleImpl._DEBUG)
            // {
            // SparqlNormalisationRuleImpl.log.debug("SparqlNormalisationRuleImpl: nextStatement: "
            // + nextStatement.toString());
            // }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(SpinConstraintRuleSchema.getSpinConstraintRuleTypeUri()))
            {
                if(SpinConstraintRuleImpl._TRACE)
                {
                    SpinConstraintRuleImpl.log
                            .trace("SparqlNormalisationRuleImpl: found valid type predicate for URI: " + keyToUse);
                }
                
                // isValid = true;
                this.setKey(keyToUse);
            }
            else
            {
                if(SpinConstraintRuleImpl._DEBUG)
                {
                    SpinConstraintRuleImpl.log
                            .debug("SparqlNormalisationRuleImpl: unrecognisedStatement nextStatement: "
                                    + nextStatement.toString());
                }
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        // this.relatedNamespaces = tempRelatedNamespaces;
        // this.unrecognisedStatements = tempUnrecognisedStatements;
        
        // stages.add(NormalisationRule.rdfruleStageAfterResultsImport.stringValue());
        
        // mode = sparqlruleModeOnlyIncludeMatches.stringValue();
        
        if(SpinConstraintRuleImpl._DEBUG)
        {
            SpinConstraintRuleImpl.log.debug("SparqlNormalisationRuleImpl constructor: toString()=" + this.toString());
        }
    }
    
    @Override
    public void addEntailmentUri(final URI nextEntailmentURI)
    {
        this.activeEntailments.add(nextEntailmentURI);
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
    
    /**
     * @return the validStages
     */
    @Override
    public Set<URI> getValidStages()
    {
        if(this.validStages.size() == 0)
        {
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterQueryParsing());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
        }
        
        return Collections.unmodifiableSet(this.validStages);
    }
    
    @Override
    public boolean isEntailmentEnabled(final URI entailmentURI)
    {
        return this.activeEntailments.contains(entailmentURI);
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
     */
    public Repository processSpinRules(final Repository inputRepository, final org.openrdf.model.Resource... contexts)
    {
        // Load domain model with imports
        // System.out.println("Loading domain ontology...");
        // OntModel queryModel =
        // loadModelWithImports("http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl");
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
        
        // Collect rules (and template calls) defined in OWL RL
        final Map<CommandWrapper, Map<String, RDFNode>> initialTemplateBindings =
                new HashMap<CommandWrapper, Map<String, RDFNode>>();
        final Map<Resource, List<CommandWrapper>> cls2Query =
                SPINQueryFinder.getClass2QueryMap(unionModel, queryModel, SPIN.rule, true, initialTemplateBindings,
                        false, allowedRuleSources);
        final Map<Resource, List<CommandWrapper>> cls2Constructor =
                SPINQueryFinder.getClass2QueryMap(queryModel, queryModel, SPIN.constructor, true,
                        initialTemplateBindings, false, allowedRuleSources);
        final SPINRuleComparator comparator = new DefaultSPINRuleComparator(queryModel);
        
        // Run all inferences
        SpinConstraintRuleImpl.log.info("Running SPIN inferences...");
        SPINInferences.run(queryModel, newTriples, cls2Query, cls2Constructor, initialTemplateBindings, null, null,
                false, SPIN.rule, comparator, null, allowedRuleSources);
        SpinConstraintRuleImpl.log.info("Inferred triples: " + newTriples.size());
        SpinConstraintRuleImpl.log.info("Query triples: " + queryModel.size());
        
        final StmtIterator listStatements = newTriples.listStatements();
        
        while(listStatements.hasNext())
        {
            SpinConstraintRuleImpl.log.info(listStatements.next().toString());
        }
        
        // Note: To optimise the process, we only add the new triples back into the original
        // repository
        return SpinUtils.addJenaModelToSesameRepository(newTriples, inputRepository);
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
    
    @Override
    public boolean stageAfterQueryCreation(final Object input)
    {
        return true;
    }
    
    @Override
    public boolean stageAfterQueryParsing(final Object input)
    {
        return true;
    }
    
    @Override
    public boolean stageAfterResultsImport(final Object input)
    {
        return true;
    }
    
    @Override
    public boolean stageAfterResultsToDocument(final Object input)
    {
        return true;
    }
    
    @Override
    public boolean stageAfterResultsToPool(final Object input)
    {
        return true;
    }
    
    @Override
    public boolean stageBeforeResultsImport(final Object input)
    {
        return true;
    }
    
    @Override
    public boolean stageQueryVariables(final Object input)
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
    public boolean toRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super.toRdf(myRepository, keyToUse, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            if(SpinConstraintRuleImpl._DEBUG)
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
}
