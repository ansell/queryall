package org.queryall.impl.rdfrule;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.utils.RdfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.spin.arq.ARQ2SPIN;
import org.topbraid.spin.arq.ARQFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.FileUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SpinUtils
{
    private static final Logger log = LoggerFactory.getLogger(SpinUtils.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = SpinUtils.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = SpinUtils.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SpinUtils.log.isInfoEnabled();
    public static String getTurtleSPINQueryFromSPARQL(String query)
    {
        Model model = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
        model.setNsPrefix("rdf", com.hp.hpl.jena.vocabulary.RDF.getURI());
    
        Query arqQuery = ARQFactory.get().createQuery(model, query);
        // We don't need the results of this operation, but we do need its side-effects on "model"
        new ARQ2SPIN(model).createQuery(arqQuery, null);
        
        
        StringWriter output = new StringWriter();
        
        System.out.println("SPIN query in Turtle:");
        model.write(output, FileUtils.langTurtle);
        
        String turtleString = output.toString();
        
        return turtleString;
    }

    public static OntModel loadModelFromClasspath(String classpathRef) 
    {
        SpinNormalisationRuleImpl.log.info("loading model from classpathRef="+classpathRef);
        
        Model baseModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
        
        if(!classpathRef.startsWith("/"))
        {
            classpathRef = "/"+classpathRef;
        }
        
        InputStream stream = SpinUtils.class.getResourceAsStream(classpathRef);
        
        baseModel.read(stream, "http://temp.base.uri.fake/");
        
        // TODO: make the OntModelSpec here configurable
        return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, baseModel);
    }
    
    public static OntModel loadModelFromUrl(String url) 
    {
        SpinNormalisationRuleImpl.log.info("loading model from url="+url);
        
        Model baseModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
        baseModel.read(url);
        // TODO: make the OntModelSpec here configurable
        return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, baseModel);
    }
    
    public static OntModel addSesameRepositoryToJenaModel(Repository inputRepository, Model outputModel, org.openrdf.model.Resource... contexts) 
        {
    //        Model baseModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
            
            ByteArrayOutputStream internalOutputStream = new ByteArrayOutputStream();
            
            // write out the triples from the model into the output stream
            RdfUtils.toOutputStream(inputRepository, internalOutputStream, RDFFormat.RDFXML, contexts);
            
            // use the resulting byte[] as input to an InputStream
            InputStream bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(internalOutputStream.toByteArray()));
    
            outputModel.read(bufferedInputStream, "http://spin.example.org/");
            
            return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, outputModel);
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
                SpinNormalisationRuleImpl.log.error("Found unexpected exception initialising in memory repository", e);
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
            SpinNormalisationRuleImpl.log.error("Found exception while attempting to add data to OpenRDF repository", e);
    
            try
            {
                if(connection != null)
                {
                    connection.rollback();
                }
            }
            catch(RepositoryException e1)
            {
                SpinNormalisationRuleImpl.log.error("Found exception while attempting to rollback connection due to previous exception", e1);
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
    

}
