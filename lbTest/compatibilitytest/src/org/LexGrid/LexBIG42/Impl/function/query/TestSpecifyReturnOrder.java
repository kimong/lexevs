/*
 * Copyright: (c) 2004-2007 Mayo Foundation for Medical Education and 
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
package org.LexGrid.LexBIG42.Impl.function.query;

// LexBIG Test ID: T1_FNC_22	TestSpecifyReturnOrder

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG42.Impl.function.LexBIGServiceTestCase;
import org.LexGrid.LexBIG.Impl.testUtility.ServiceHolder;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.Utility.Constructors;

public class TestSpecifyReturnOrder extends LexBIGServiceTestCase
{
    final static String testID = "T1_FNC_22";

    @Override
    protected String getTestID()
    {
        return testID;
    }

    public void testT1_FNC_22() throws LBException
    {

        CodedNodeSet cns = ServiceHolder.instance().getLexBIGService().getCodingSchemeConcepts(AUTO_SCHEME, null);
        
        ResolvedConceptReference[] rcr = cns.resolveToList(Constructors.createSortOptionList(new String[] {"code"}, new Boolean[] {null}), null, null, 3)
                .getResolvedConceptReference();
        assertTrue(rcr.length == 3);
        assertTrue(rcr[0].getConceptCode().equals("005"));
        assertTrue(rcr[1].getConceptCode().equals("73"));
        assertTrue(rcr[2].getConceptCode().equals("A0001"));

        rcr = cns.resolveToList(Constructors.createSortOptionList(new String[] {"entityDescription"}, new Boolean[] {null}), null, null, 0)
                .getResolvedConceptReference();
        assertTrue(rcr[0].getConceptCode().equals("A0001"));
        assertTrue(rcr[1].getConceptCode().equals("C0001"));
        assertTrue(rcr[2].getConceptCode().equals("Chevy"));
        
        //reverse sort 1.
        rcr = cns.resolveToList(Constructors.createSortOptionList(new String[]{"code"}, new Boolean[]{new Boolean(false)}), null, null, 3)
                .getResolvedConceptReference();
        assertTrue(rcr.length == 3);
        assertTrue(rcr[0].getConceptCode().equals("T0001"));
        assertTrue(rcr[1].getConceptCode().equals("Jaguar"));
        assertTrue(rcr[2].getConceptCode().equals("GM"));

    }

}