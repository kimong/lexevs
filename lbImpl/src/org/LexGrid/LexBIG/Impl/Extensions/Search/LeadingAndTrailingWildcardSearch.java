/*
 * Copyright: (c) 2004-2009 Mayo Foundation for Medical Education and 
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify 
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 * 
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * 		http://www.eclipse.org/legal/epl-v10.html
 * 
 */
package org.LexGrid.LexBIG.Impl.Extensions.Search;

import java.util.StringTokenizer;

import org.LexGrid.LexBIG.DataModel.InterfaceElements.ExtensionDescription;
import org.LexGrid.LexBIG.Extensions.Query.Search;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.springframework.util.StringUtils;

/**
 * The Class LeadingAndTrailingWildcardSearch.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class LeadingAndTrailingWildcardSearch extends AbstractExactMatchBoostingSearch {
 
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7352943717333165742L;
    
    /* (non-Javadoc)
     * @see org.LexGrid.LexBIG.Impl.Extensions.AbstractExtendable#buildExtensionDescription()
     */
    @Override
    protected ExtensionDescription buildExtensionDescription() {
        ExtensionDescription ed = new ExtensionDescription();
        ed.setExtensionBaseClass(Search.class.getName());
        ed.setExtensionClass(LeadingAndTrailingWildcardSearch.class.getName());
        ed
        .setDescription("Equivalent to '*term*'.");
        ed.setName("LeadingAndTrailingWildcard");
        ed.setVersion("1.0");
        return ed;
    }
    
    /* (non-Javadoc)
     * @see org.LexGrid.LexBIG.Extensions.Query.Search#buildQuery(java.lang.String)
     */
    public Query doBuildQuery(String searchText) {
        StringBuffer buffer = new StringBuffer();
        QueryParser queryParser = super.getQueryParser();
        
        StringTokenizer tokenizer = 
            new StringTokenizer
                (searchText, " ");

        while(tokenizer.hasMoreTokens()){
            buffer.append(
                    addLeadingAndTrailingWildcards(tokenizer.nextToken()));
            buffer.append(" ");
        }
        
        try {
            return queryParser.parse(
                    StringUtils.trimTrailingWhitespace(
                            buffer.toString()));
        } catch (ParseException e) {
           throw new RuntimeException(e);
        }
    } 
    
    /**
     * Adds the leading and trailing wildcards.
     * 
     * @param input the input
     * 
     * @return the string
     */
    protected String addLeadingAndTrailingWildcards(String input){
        if (!input.endsWith("*")) {
            input += "*";
        }
        
        if (!input.startsWith("*")) {
            input = "*" + input;
        }
        return input;
    }
}
