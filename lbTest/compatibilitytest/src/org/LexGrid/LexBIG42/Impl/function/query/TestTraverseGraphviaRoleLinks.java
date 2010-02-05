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

// LexBIG Test ID: T1_FNC_11	TestTraverseGraphviaRoleLinks

import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG42.Impl.function.LexBIGServiceTestCase;
import org.LexGrid.LexBIG.Impl.testUtility.ServiceHolder;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.ConvenienceMethods;

public class TestTraverseGraphviaRoleLinks extends LexBIGServiceTestCase
{
    final static String testID = "T1_FNC_11";

    @Override
    protected String getTestID()
    {
        return testID;
    }

    public void testT1_FNC_11() throws LBException
    {
        // in LexBig, a role is just an association, so this is traversing associations.

        CodedNodeGraph cng = ServiceHolder.instance().getLexBIGService().getNodeGraph(THES_SCHEME, null, null);
        cng.restrictToAssociations(Constructors.createNameAndValueList(new String[]{"hasSubtype"}), null);

        // role "Anatomy_Kind"
        ResolvedConceptReference[] rcr = cng.resolveAsList(Constructors.createConceptReference("C43652", THES_SCHEME),
                                                           true, false, 1, 1, null, null, null, 0)
                .getResolvedConceptReference();

        // check for some source (down) codes that I know should be there.
        assertTrue(rcr.length == 1);
        assertTrue(rcr[0].getConceptCode().equals("C43652"));

        Association[] a = rcr[0].getSourceOf().getAssociation();
        assertTrue(contains(a, "hasSubtype", "C12219"));
    }

    private boolean contains(Association[] a, String association, String conceptCode)
    {
        boolean found = false;
        for (int i = 0; i < a.length; i++)
        {
            if (a[i].getAssociationName().equals(association)
                    && contains(a[i].getAssociatedConcepts().getAssociatedConcept(), conceptCode))
            {
                found = true;
                break;
            }
        }

        return found;
    }

    private boolean contains(AssociatedConcept[] ac, String conceptCode)
    {
        boolean found = false;
        for (int i = 0; i < ac.length; i++)
        {
            if (ac[i].getConceptCode().equals(conceptCode))
            {
                found = true;
                break;
            }

        }

        return found;
    }

}