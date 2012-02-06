package de.fuberlin.wiwiss.pubby.negotiation;

import java.util.regex.Pattern;

class AcceptHeaderOverride
{
    private Pattern userAgentPattern;
    private String original;
    private String replacement;
    
    AcceptHeaderOverride(final Pattern nextUserAgentPattern, final String nextOriginal, final String nextReplacement)
    {
        this.userAgentPattern = nextUserAgentPattern;
        this.original = nextOriginal;
        this.replacement = nextReplacement;
    }
    
    String getReplacement()
    {
        return this.replacement;
    }
    
    @SuppressWarnings("unused")
    boolean matches(final String acceptHeader)
    {
        return this.matches(acceptHeader, null);
    }
    
    boolean matches(final String acceptHeader, final String userAgentHeader)
    {
        return (this.userAgentPattern == null || (userAgentHeader != null && !userAgentHeader.equals("") && this.userAgentPattern
                .matcher(userAgentHeader).find())) && (this.original == null || this.original.equals(acceptHeader));
    }
}