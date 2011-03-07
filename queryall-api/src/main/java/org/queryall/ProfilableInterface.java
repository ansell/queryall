package org.queryall;

import org.openrdf.model.URI;

public interface ProfilableInterface
{

    public abstract URI getProfileIncludeExcludeOrder();

    public abstract void setProfileIncludeExcludeOrder(
            URI profileIncludeExcludeOrder);

}