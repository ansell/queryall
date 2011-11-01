package org.queryall.impl.rdfrule;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.base.HtmlExport;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.SpinNormalisationRule;
import org.queryall.api.rdfrule.SpinNormalisationRuleSchema;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.spin.inference.DefaultSPINRuleComparator;
import org.topbraid.spin.inference.SPINInferences;
import org.topbraid.spin.inference.SPINRuleComparator;
import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.util.CommandWrapper;
import org.topbraid.spin.util.SPINQueryFinder;
import org.topbraid.spin.vocabulary.SPIN;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.compose.MultiUnion;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.vocabulary.RDFSyntax;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SpinNormalisationRuleImpl extends NormalisationRuleImpl implements SpinNormalisationRule, HtmlExport
{
    private static final Logger log = LoggerFactory.getLogger(SpinNormalisationRuleImpl.class);
    private static final boolean _TRACE = SpinNormalisationRuleImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = SpinNormalisationRuleImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SpinNormalisationRuleImpl.log.isInfoEnabled();
    
    private static final Set<URI> SPIN_NORMALISATION_RULE_IMPL_TYPES = new HashSet<URI>();
    
    static
    {
        SpinNormalisationRuleImpl.SPIN_NORMALISATION_RULE_IMPL_TYPES.add(NormalisationRuleSchema
                .getNormalisationRuleTypeUri());
        SpinNormalisationRuleImpl.SPIN_NORMALISATION_RULE_IMPL_TYPES.add(SpinNormalisationRuleSchema
                .getSpinRuleTypeUri());
        
        // Need to initialise the SPIN registry at least once
        SPINModuleRegistry.get().init();
    }
    
    /**
     * Takes the RDF statements from a Jena Model and adds them to the given contexts in a Sesame repository
     * 
     * @param inputModel
     * @param outputRepository If outputRepository is null, a new in-memory repository is created
     * @param contexts
     * @return
     */
    public static Repository addJenaModelToSesameRepository(Model inputModel, Repository outputRepository, org.openrdf.model.Resource... contexts)
    {
        if(outputRepository == null)
        {
            outputRepository = new SailRepository(new MemoryStore());
            try
            {
                outputRepository.initialize();
            }
            catch(RepositoryException e)
            {
                log.error("Found unexpected exception initialising in memory repository", e);
            }
        }
        
        ByteArrayOutputStream internalOutputStream = new ByteArrayOutputStream();
        
        // write out the triples from the model into the output stream
        inputModel.write(internalOutputStream);
        
        // use the resulting byte[] as input to an InputStream
        InputStream bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(internalOutputStream.toByteArray()));
        
        RepositoryConnection connection = null;
        
        try
        {
            connection = outputRepository.getConnection();
            
            connection.add(bufferedInputStream, "http://spin.example.org/", RDFFormat.RDFXML, contexts);
            
            connection.commit();
        }
        catch(Exception e)
        {
            log.error("Found exception while attempting to add data to OpenRDF repository", e);

            try
            {
                if(connection != null)
                {
                    connection.rollback();
                }
            }
            catch(RepositoryException e1)
            {
                log.error("Found exception while attempting to rollback connection due to previous exception", e1);
            }
        }
        finally
        {
            try
            {
                if(connection != null)
                {
                    connection.close();
                }
            }
            catch(RepositoryException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        return outputRepository;
    }
    
    /**
     * See OWLRLExample in spin-examples-1.2.0.jar
     * 
     * Currently limited to adding the resulting triples from the spin reasoning to the repository
     * 
     * TODO: add more modes, such as delete matching, add matching, only return matching triples etc.
     * 
     * @param inputRepository The OpenRDF repository to use for the input triples
     */
    public static Repository processSpinRules(Repository inputRepository, org.openrdf.model.Resource... contexts)
    {
        // Load domain model with imports
        // System.out.println("Loading domain ontology...");
        // OntModel queryModel = loadModelWithImports("http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl");
        log.info("Loading jena model from sesame repository");
        OntModel queryModel = addSesameRepositoryToJenaModel(inputRepository, contexts);
        
        
        // Create and add Model for inferred triples
        Model newTriples = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
        queryModel.addSubModel(newTriples);
        
        // Load OWL RL library from the web
        log.info("Loading OWL RL ontology...");
        OntModel owlrlModel = loadModelWithImports("http://topbraid.org/spin/owlrl-all");

        // Register any new functions defined in OWL RL
        SPINModuleRegistry.get().registerAll(owlrlModel, null);
        
        // Build one big union Model of everything
        MultiUnion multiUnion = new MultiUnion(new Graph[] {
            queryModel.getGraph(),
            owlrlModel.getGraph()
        });
        Model unionModel = ModelFactory.createModelForGraph(multiUnion);
        
        // Collect rules (and template calls) defined in OWL RL
        Map<CommandWrapper, Map<String,RDFNode>> initialTemplateBindings = new HashMap<CommandWrapper, Map<String,RDFNode>>();
        Map<Resource,List<CommandWrapper>> cls2Query = SPINQueryFinder.getClass2QueryMap(unionModel, queryModel, SPIN.rule, true, initialTemplateBindings, false);
        Map<Resource,List<CommandWrapper>> cls2Constructor = SPINQueryFinder.getClass2QueryMap(queryModel, queryModel, SPIN.constructor, true, initialTemplateBindings, false);
        SPINRuleComparator comparator = new DefaultSPINRuleComparator(queryModel);

        // Run all inferences
        log.info("Running SPIN inferences...");
        SPINInferences.run(queryModel, newTriples, cls2Query, cls2Constructor, initialTemplateBindings, null, null, false, SPIN.rule, comparator, null);
        log.info("Inferred triples: " + newTriples.size());
        
        StmtIterator listStatements = newTriples.listStatements();
        
        while(listStatements.hasNext())
        {
            log.info(listStatements.next().toString());
        }
        return addJenaModelToSesameRepository(newTriples, inputRepository);
    }

    static OntModel loadModelWithImports(String url) 
    {
        Model baseModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
        baseModel.read(url);
        return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, baseModel);
    }
    
    static OntModel addSesameRepositoryToJenaModel(Repository inputRepository, org.openrdf.model.Resource... contexts) 
    {
        Model baseModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
        
        ByteArrayOutputStream internalOutputStream = new ByteArrayOutputStream();
        
        // write out the triples from the model into the output stream
        RdfUtils.toOutputStream(inputRepository, internalOutputStream, RDFFormat.RDFXML, contexts);
        
        // use the resulting byte[] as input to an InputStream
        InputStream bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(internalOutputStream.toByteArray()));

        baseModel.read(bufferedInputStream, "http://spin.example.org/");
        
        return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, baseModel);
    }

    public static Set<URI> myTypes()
    {
        return SpinNormalisationRuleImpl.SPIN_NORMALISATION_RULE_IMPL_TYPES;
    }
    
    public SpinNormalisationRuleImpl()
    {
        super();
    }
    
    // keyToUse is the URI of the next instance that can be found in
    // myRepository
    public SpinNormalisationRuleImpl(final Collection<Statement> inputStatements, final URI keyToUse,
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
                    && nextStatement.getObject().equals(SpinNormalisationRuleSchema.getSpinRuleTypeUri()))
            {
                if(SpinNormalisationRuleImpl._TRACE)
                {
                    SpinNormalisationRuleImpl.log
                            .trace("SparqlNormalisationRuleImpl: found valid type predicate for URI: " + keyToUse);
                }
                
                // isValid = true;
                this.setKey(keyToUse);
            }
            else
            {
                if(SpinNormalisationRuleImpl._DEBUG)
                {
                    SpinNormalisationRuleImpl.log
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
        
        if(SpinNormalisationRuleImpl._DEBUG)
        {
            SpinNormalisationRuleImpl.log.debug("SparqlNormalisationRuleImpl constructor: toString()="
                    + this.toString());
        }
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Set<URI> getElementTypes()
    {
        return SpinNormalisationRuleImpl.myTypes();
    }
    
    /**
     * @return the validStages
     */
    @Override
    public Collection<URI> getValidStages()
    {
        if(this.validStages.size() == 0)
        {
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterQueryParsing());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
            this.addValidStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
        }
        
        return Collections.unmodifiableCollection(this.validStages);
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
    public Object stageAfterQueryCreation(final Object input)
    {
        return input;
    }
    
    @Override
    public Object stageAfterQueryParsing(final Object input)
    {
        return input;
    }
    
    @Override
    public Object stageAfterResultsImport(final Object input)
    {
        return input;
    }
    
    @Override
    public Object stageAfterResultsToDocument(final Object input)
    {
        return input;
    }
    
    @Override
    public Object stageAfterResultsToPool(final Object input)
    {
        return input;
    }
    
    @Override
    public Object stageBeforeResultsImport(final Object input)
    {
        return input;
    }
    
    @Override
    public Object stageQueryVariables(final Object input)
    {
        return input;
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
            if(SpinNormalisationRuleImpl._DEBUG)
            {
                SpinNormalisationRuleImpl.log.debug("SparqlNormalisationRuleImpl.toRdf: keyToUse=" + keyToUse);
            }
            
            final URI keyUri = this.getKey();
            
            con.setAutoCommit(false);
            
            con.add(keyUri, RDF.TYPE, SpinNormalisationRuleSchema.getSpinRuleTypeUri(), keyToUse);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            SpinNormalisationRuleImpl.log.error("RepositoryException: " + re.getMessage());
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
