package org.queryall.api.rdfrule;

import java.util.Collection;

public interface SpinNormalisationRule extends NormalisationRule
{

    public abstract void addImport(String nextImport);

    public abstract Collection<String> getImports();
    
}
