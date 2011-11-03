package org.queryall.api.rdfrule;

import java.util.Set;

public interface SpinNormalisationRule extends NormalisationRule
{

    public abstract void addImport(String nextImport);

    public abstract Set<String> getImports();
    
}
