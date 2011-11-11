package org.queryall.impl.rdfrule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.rdfrule.SpinNormalisationRule;
import org.topbraid.spin.system.SPINModuleRegistry;

import com.hp.hpl.jena.ontology.OntModel;

public abstract class BaseSpinRuleImpl extends BaseValidatingRuleImpl implements SpinNormalisationRule
{
    
    protected Set<String> localImports = new HashSet<String>(10);
    private Set<URI> urlImports = new HashSet<URI>(10);
    protected List<OntModel> ontologyModels = new ArrayList<OntModel>(5);
    private volatile SPINModuleRegistry registry;
    
    public BaseSpinRuleImpl()
    {
        super();
    }
    
    public BaseSpinRuleImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
    }
    
    @Override
    public void addLocalImport(final String nextImport)
    {
        this.localImports.add(nextImport);
        
        final OntModel nextModel = SpinUtils.loadModelFromClasspath(nextImport);
        
        if(nextModel != null)
        {
            BaseRuleImpl.log.info("adding model to registry and ontology model list nextImport=" + nextImport
                    + " nextModel.size()=" + nextModel.size());
            this.ontologyModels.add(nextModel);
            this.getSpinModuleRegistry().registerAll(nextModel, nextImport);
        }
        else
        {
            BaseRuleImpl.log.error("Failed to load import from URL nextImport=" + nextImport);
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
            BaseRuleImpl.log.info("adding model to registry and ontology model list nextImport="
                    + nextURLImport.stringValue() + " nextModel.size()=" + nextModel.size());
            this.ontologyModels.add(nextModel);
            this.getSpinModuleRegistry().registerAll(nextModel, nextURLImport.stringValue());
        }
        else
        {
            BaseRuleImpl.log.error("Failed to load import from URL nextURLImport=" + nextURLImport.stringValue());
        }
        
        this.getSpinModuleRegistry().init();
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
    
    public void setSpinModuleRegistry(final SPINModuleRegistry registry)
    {
        this.registry = registry;
        this.registry.init();
    }
    
}