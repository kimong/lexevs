/*
 * Copyright: (c) 2004-2010 Mayo Foundation for Medical Education and 
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
package org.LexGrid.LexBIG.Impl.Extensions.Sort;

import java.util.Comparator;
import java.util.Map;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.SortDescription;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.types.SortContext;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Query.Sort;
import org.LexGrid.annotations.LgClientSideSafe;

/**
 * isActive sort which implements the Sort interface.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 * @author <A HREF="mailto:erdmann.jesse@mayo.edu">Jesse Erdmann</A>
 * @version subversion $Revision: $ checked in on $Date: $
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a> 
 */
public class isActiveSort extends AbstractSort {

    private static final long serialVersionUID = 6186186064325148661L;
    final static String description_ = "Sort the results according to the active status flag";
    final static String name_ = "isActive";
    final static String provider_ = "Mayo Clinic";
    final static String version_ = "1.0";
    
    
    public isActiveSort() throws LBParameterException, LBException {
        super(); 
    }

    protected SortDescription buildSortDescription() {
        SortDescription sd = new SortDescription();
        sd.setExtensionBaseClass(Sort.class.getName());
        sd.setExtensionClass(isActiveSort.class.getName());
        sd.setDescription(description_);
        sd.setName(name_);
        sd.setVersion(version_);
        sd.setRestrictToContext(new SortContext[] { SortContext.SET });

       return sd;
    }
    
    @Override
    public void registerComparators(Map<Class, Comparator> classToComparatorsMap) {
        classToComparatorsMap.put(ResolvedConceptReference.class, new isActiveSort.ResolvedConceptReferenceIsActiveSort()); 
    }

    @LgClientSideSafe
    private class ResolvedConceptReferenceIsActiveSort implements Comparator<ResolvedConceptReference>{
       
        @LgClientSideSafe
        public int compare(ResolvedConceptReference o1, ResolvedConceptReference o2) {
            return o1.getEntity().getIsActive().compareTo(o1.getEntity().getIsActive());

        }
    }
}