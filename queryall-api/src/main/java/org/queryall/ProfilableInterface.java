package org.queryall;

import java.util.List;

import org.openrdf.model.URI;

public interface ProfilableInterface extends BaseQueryAllInterface
{

    public abstract URI getProfileIncludeExcludeOrder();

    public abstract void setProfileIncludeExcludeOrder(
            URI profileIncludeExcludeOrder);
    
    public abstract boolean isUsedWithProfileList(List<Profile> orderedProfileList, boolean allowImplicitInclusions, boolean includeNonProfileMatched);

}