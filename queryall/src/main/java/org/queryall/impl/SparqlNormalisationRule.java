
package org.queryall.impl;

import java.util.HashSet;
import java.util.Collection;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.memory.model.MemValueFactory;

import org.queryall.NormalisationRule;
import org.queryall.RuleTest;
import org.queryall.helpers.*;

public class SparqlNormalisationRule extends NormalisationRuleImpl
{
    private static final Logger log = Logger
            .getLogger(SparqlNormalisationRule.class.getName());
    private static final boolean _TRACE = SparqlNormalisationRule.log
            .isTraceEnabled();
    private static final boolean _DEBUG = SparqlNormalisationRule.log
            .isDebugEnabled();
    private static final boolean _INFO = SparqlNormalisationRule.log
            .isInfoEnabled();
    
    private static final String defaultNamespace = Settings.DEFAULT_RDF_RDFRULE_NAMESPACE;
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    // public String key;
    // public String description = "";
    // public String curationStatus = ProjectImpl.projectNotCuratedUri.stringValue();
    
    public String sparqlConstructQuery = "";
    // public String profileIncludeExcludeOrder = Profile.getProfileIncludeExcludeOrder()UndefinedUri
            // .stringValue();
    // public Collection<String> relatedNamespaces = new HashSet<String>();
    
    private URI mode = sparqlruleModeOnlyDeleteMatches;
    
    public int order = 100;
    
    public static URI sparqlruleTypeUri;
    public static URI sparqlruleSparqlConstructQuery;
    public static URI sparqlruleMode;
    public static URI sparqlruleModeOnlyIncludeMatches;
    public static URI sparqlruleModeOnlyDeleteMatches;
    
    // public static String rdfruleNamespace;
    
    static
    {
        // SparqlNormalisationRule.rdfruleNamespace = Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX
                // + Settings.DEFAULT_RDF_RDFRULE_NAMESPACE
                // + Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX;
        
        try
        {
            final Repository myStaticRepository = new SailRepository(
                    new MemoryStore());
            myStaticRepository.initialize();
            final ValueFactory f = myStaticRepository.getValueFactory();
            
            SparqlNormalisationRule.sparqlruleTypeUri = f
                    .createURI(SparqlNormalisationRule.rdfruleNamespace
                            + "SparqlNormalisationRule");
            SparqlNormalisationRule.sparqlruleSparqlConstructQuery = f
                    .createURI(SparqlNormalisationRule.rdfruleNamespace
                            + "sparqlConstructQuery");
            SparqlNormalisationRule.sparqlruleMode = f
                    .createURI(SparqlNormalisationRule.rdfruleNamespace
                            + "mode");
            SparqlNormalisationRule.sparqlruleModeOnlyDeleteMatches = f
                    .createURI(SparqlNormalisationRule.rdfruleNamespace
                            + "onlyDeleteMatches");
            SparqlNormalisationRule.sparqlruleModeOnlyIncludeMatches = f
                    .createURI(SparqlNormalisationRule.rdfruleNamespace
                            + "onlyIncludeMatches");
        }
        catch (final RepositoryException re)
        {
            SparqlNormalisationRule.log.error(re.getMessage());
        }
    }
    
    // keyToUse is the URI of the next instance that can be found in
    // myRepository
    public SparqlNormalisationRule(Collection<Statement> inputStatements, URI keyToUse, int modelVersion)
            throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        boolean isValid = false;
        
        final Collection<Statement> tempUnrecognisedStatements = new HashSet<Statement>();
        
        final ValueFactory f = new MemValueFactory();
        
        Collection<String> tempRelatedNamespaces = new HashSet<String>();
        
        for(final Statement nextStatement : inputStatements)
        {
            if(SparqlNormalisationRule._DEBUG)
            {
                SparqlNormalisationRule.log
                        .debug("SparqlNormalisationRule: nextStatement: "
                                + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(
                            SparqlNormalisationRule.sparqlruleTypeUri))
            {
                if(SparqlNormalisationRule._TRACE)
                {
                    SparqlNormalisationRule.log
                            .trace("SparqlNormalisationRule: found valid type predicate for URI: "
                                    + keyToUse);
                }
                
                isValid = true;
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(
                    SparqlNormalisationRule.sparqlruleSparqlConstructQuery))
            {
                this.sparqlConstructQuery = nextStatement.getObject()
                        .stringValue();
            }
            else if(nextStatement.getPredicate().equals(
                    SparqlNormalisationRule.sparqlruleMode) 
                && nextStatement.getObject().equals(
                    SparqlNormalisationRule.sparqlruleModeOnlyIncludeMatches))
            {
                this.mode = SparqlNormalisationRule.sparqlruleModeOnlyIncludeMatches;
            }
            else if(nextStatement.getPredicate().equals(
                    SparqlNormalisationRule.sparqlruleMode) 
                && nextStatement.getObject().equals(
                    SparqlNormalisationRule.sparqlruleModeOnlyDeleteMatches))
            {
                this.mode = SparqlNormalisationRule.sparqlruleModeOnlyDeleteMatches;
            }
            else
            {
                unrecognisedStatements.add(nextStatement);
            }
        }
        
        // this.relatedNamespaces = tempRelatedNamespaces;
        // this.unrecognisedStatements = tempUnrecognisedStatements;
        
        // stages.add(NormalisationRule.rdfruleStageAfterResultsImport.stringValue());
        
        // mode = sparqlruleModeOnlyIncludeMatches.stringValue();

        Collection<URI> tempValidStages = new HashSet<URI>(2);
        tempValidStages.add(rdfruleStageAfterResultsImport);
        tempValidStages.add(rdfruleStageAfterResultsToPool);

        this.setValidStages(tempValidStages);
        
        if(SparqlNormalisationRule._DEBUG)
        {
            SparqlNormalisationRule.log
                    .debug("SparqlNormalisationRule constructor: toString()="
                            + this.toString());
        }
        
        if(!isValid)
        {
            throw new RuntimeException(
                    "SparqlNormalisationRule.fromRdf: result was not valid");
        }
    }
    
    public static boolean schemaToRdf(Repository myRepository, String keyToUse, int modelVersion) throws OpenRDFException
    {
        NormalisationRuleImpl.schemaToRdf(myRepository, keyToUse, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            final URI contextKeyUri = f.createURI(keyToUse);
            con.setAutoCommit(false);
            
            con.add(SparqlNormalisationRule.sparqlruleTypeUri, RDF.TYPE, OWL.CLASS,
                    contextKeyUri);
            
            con.add(SparqlNormalisationRule.sparqlruleTypeUri, RDFS.LABEL, 
                f.createLiteral("A SPARQL based normalisation rule intended to normalise in-memory RDF triples."),
                    contextKeyUri);


            con.add(SparqlNormalisationRule.sparqlruleSparqlConstructQuery, RDF.TYPE,
                    OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(SparqlNormalisationRule.sparqlruleSparqlConstructQuery, RDFS.RANGE,
                    RDFS.LITERAL, contextKeyUri);

            con.add(SparqlNormalisationRule.sparqlruleSparqlConstructQuery, RDFS.DOMAIN,
                    SparqlNormalisationRule.sparqlruleTypeUri, contextKeyUri);

            con.add(SparqlNormalisationRule.sparqlruleSparqlConstructQuery, RDFS.LABEL, 
                f.createLiteral("A SPARQL CONSTRUCT pattern that will be used to match against RDF triples in memory at the assigned stages, in the form CONSTRUCT { ... } WHERE { ... }."),
                    contextKeyUri);

            
            con.add(SparqlNormalisationRule.sparqlruleMode, RDF.TYPE,
                    OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(SparqlNormalisationRule.sparqlruleMode, RDFS.RANGE,
                    RDFS.RESOURCE, contextKeyUri);

            con.add(SparqlNormalisationRule.sparqlruleMode, RDFS.DOMAIN,
                    SparqlNormalisationRule.sparqlruleTypeUri, contextKeyUri);

            con.add(SparqlNormalisationRule.sparqlruleMode, RDFS.LABEL, 
                f.createLiteral("The mode that this normalisation rule will be used in. In the absence of SPARQL Update language support, this enables deletions and filtering based on the matched triples."),
                    contextKeyUri);

            
            con.add(SparqlNormalisationRule.sparqlruleModeOnlyIncludeMatches, RDFS.LABEL, 
                f.createLiteral("Specifies that the SPARQL rule will be applied, and only the matches from the rule will remain in the relevant RDF triple store after the application. If the stage is after Results Import, only the results from the current provider and query will be affected."),
                    contextKeyUri);

            con.add(SparqlNormalisationRule.sparqlruleModeOnlyDeleteMatches, RDFS.LABEL, 
                f.createLiteral("Specifies that the SPARQL rule will be applied, and the matches from the rule will be deleted from the relevant RDF triple store after the application. If the stage is after Results Import, only the results from the current provider and query will be affected."),
                    contextKeyUri);

            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch (final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            if(con != null)
            {
                con.rollback();
            }
            
            SparqlNormalisationRule.log.error("RepositoryException: "
                    + re.getMessage());
        }
        catch (final OpenRDFException ordfe)
        {
            SparqlNormalisationRule.log.error(ordfe);
            
            // Something went wrong during the transaction, so we roll it back
            if(con != null)
            {
                con.rollback();
            }
            
            throw ordfe;
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
    
    public Object stageQueryVariables(Object input)
    {
        return input;
    }
        
    public Object stageAfterQueryCreation(Object input)
    {
        return input;
    }

    public Object stageAfterQueryParsing(Object input)
    {
        return input;
    }

    public Object stageBeforeResultsImport(Object input)
    {
        return input;
    }

    public Object stageAfterResultsImport(Object input)
    {
        return doWorkBasedOnMode((Repository) input);
    }

    public Object stageAfterResultsToPool(Object input)
    {
        return doWorkBasedOnMode((Repository) input);
    }
    
    public Object stageAfterResultsToDocument(Object input)
    {
        return input;
    }

    private Repository doWorkBasedOnMode(Repository input)
    {
        if(mode.equals(sparqlruleModeOnlyDeleteMatches))
        {
            return removeStatementsFromRepository((Repository) input);
        }
        else if(mode.equals(sparqlruleModeOnlyIncludeMatches))
        {
            return chooseStatementsFromRepository((Repository) input);
        }
        
        return input;
    }

    private Repository removeStatementsFromRepository(Repository myRepository)
    {
        try
        {
            if(_DEBUG)
            {
                log.debug("SparqlNormalisationRule: removing statements according to sparqlConstructQuery="+sparqlConstructQuery);
            }        
            
            RepositoryConnection removeConnection = myRepository.getConnection();
            
            try 
            {
                GraphQueryResult graphResult = removeConnection.prepareGraphQuery(
                    QueryLanguage.SPARQL, sparqlConstructQuery).evaluate();
                
                int deletedStatements = 0;
                
                while (graphResult.hasNext()) 
                {
                   removeConnection.remove(graphResult.next());
                   deletedStatements++;
                }
                
                removeConnection.commit();
                if(_DEBUG)
                {
                    log.debug("SparqlNormalisationRule: removed "+deletedStatements+ " results");
                }        
                
            }
            catch(Exception ex)
            {
                log.error("SparqlNormalisationRule: exception removing statements",ex);
            }
            finally
            {
                removeConnection.close();
            }
        }
        catch(org.openrdf.repository.RepositoryException rex)
        {
            log.error("SparqlNormalisationRule: RepositoryException exception adding statements", rex);
        }
        catch(OpenRDFException ex)
        {
            log.error("SparqlNormalisationRule: OpenRDFException exception adding statements", ex);
        }
        
        
        return myRepository;
    }

    private Repository chooseStatementsFromRepository(Repository myRepository)
    {
        Repository myResultRepository = null;
        
        try
        {
            myResultRepository = new SailRepository(new MemoryStore());
            myResultRepository.initialize();
            
            if(_DEBUG)
            {
                log.debug("SparqlNormalisationRule: selecting statements according to sparqlConstructQuery="+sparqlConstructQuery);
            }        
            
            RepositoryConnection selectConnection = myRepository.getConnection();
            RepositoryConnection addConnection = myResultRepository.getConnection();
            
            try 
            {
                GraphQueryResult graphResult = selectConnection.prepareGraphQuery(
                    QueryLanguage.SPARQL, sparqlConstructQuery).evaluate();
                
                int selectedStatements = 0;
                
                while (graphResult.hasNext()) 
                {
                   addConnection.add(graphResult.next());
                   selectedStatements++;
                }
                
                if(_DEBUG)
                {
                    log.debug("SparqlNormalisationRule: slected "+selectedStatements+ " statements for results");
                }        
                
                addConnection.commit();
            }
            catch(Exception ex)
            {
                log.error("SparqlNormalisationRule: exception adding statements",ex);
            }
            finally
            {
                selectConnection.close();
                addConnection.close();
            }
        }
        catch(org.openrdf.repository.RepositoryException rex)
        {
            log.error("SparqlNormalisationRule: RepositoryException exception adding statements", rex);
        }
        catch(OpenRDFException ex)
        {
            log.error("SparqlNormalisationRule: OpenRDFException exception adding statements", ex);
        }
        
        return myResultRepository;
    }

    public boolean runTests(Collection<RuleTest> myRules)
    {
        boolean allPassed = true;
        
        //final Collection<RuleTest> myRules = Settings.getRuleTestsForNormalisationRuleUri(this.getKey());
        
        // for(final RuleTest nextRuleTest : myRules)
        // {
        // 
        // }
        
        return allPassed;
    }
    
    @Override
    public String toHtml()
    {
        String result = "";
        
        result += "<div class=\"rulekey\">Rule Key: "
                + Utilities.xmlEncodeString(this.getKey().stringValue()) + "</div>\n";
        result += "<div class=\"description\">Description: "
                + Utilities.xmlEncodeString(this.getDescription()) + "</div>\n";
        result += "<div class=\"order\">Order: "
                + Utilities.xmlEncodeString(this.getOrder() + "") + "</div>\n";
        result += "<div class=\"inputMatchSparqlPattern\">SPARQL Construct query: "
                + Utilities.xmlEncodeString(this.sparqlConstructQuery) + "</div>\n";
        
        return result;
    }
    
    @Override
    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        String prefix = "rdfrule_";
        
        return sb.toString();
    }
    
    @Override
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        super.toRdf(myRepository, keyToUse, modelVersion);

        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            if(SparqlNormalisationRule._DEBUG)
            {
                SparqlNormalisationRule.log
                        .debug("SparqlNormalisationRule.toRdf: keyToUse="
                                + keyToUse);
            }
            
            final URI keyUri = keyToUse;
            final Literal sparqlConstructQueryLiteral = f
                    .createLiteral(this.sparqlConstructQuery);
            final URI modeUri = mode;
            
            con.setAutoCommit(false);
            
            con.add(keyUri, RDF.TYPE, SparqlNormalisationRule.sparqlruleTypeUri,
                    keyUri);
            
            // TODO: do null and empty checks on the following
            
            con.add(keyUri, SparqlNormalisationRule.sparqlruleSparqlConstructQuery,
                    sparqlConstructQueryLiteral, keyUri);
            con.add(keyUri, SparqlNormalisationRule.sparqlruleMode,
                    modeUri, keyUri);

            // if(this.unrecognisedStatements != null)
            // {
                // for(final Statement nextUnrecognisedStatement : this.unrecognisedStatements)
                // {
                    // con.add(nextUnrecognisedStatement);
                // }
            // }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch (final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            SparqlNormalisationRule.log.error("RepositoryException: "
                    + re.getMessage());
        }
        catch (final OpenRDFException ordfe)
        {
            SparqlNormalisationRule.log.error(ordfe);
            
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            throw ordfe;
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
        result += "sparqlConstructQuery=" + this.sparqlConstructQuery + "\n";
        result += "mode=" + this.getMode() + "\n";
        result += "description=" + this.getDescription() + "\n";
        
        return result;
    }
    
    /**
     * @return the URI used for the rdf Type of these elements
     */
    @Override
    public String getElementType()
    {
        return sparqlruleTypeUri.stringValue();
    }
    
    public URI getMode()
    {
        return mode;
    }
}
