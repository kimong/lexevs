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
package org.LexGrid.LexBIG42.Impl;

import junit.framework.TestCase;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.Impl.codedNodeSetOperations.Intersect;
import org.LexGrid.LexBIG.Impl.codedNodeSetOperations.RestrictToMatchingDesignations;
import org.LexGrid.LexBIG.Impl.codedNodeSetOperations.RestrictToMatchingProperties;
import org.LexGrid.LexBIG.Impl.codedNodeSetOperations.RestrictToProperties;
import org.LexGrid.LexBIG.Impl.codedNodeSetOperations.Union;
import org.LexGrid.LexBIG.Impl.helpers.TestFilter;
import org.LexGrid.LexBIG.Impl.helpers.TestFilter2;
import org.LexGrid.LexBIG.Impl.testUtility.ServiceHolder;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.concepts.Concept;

/**
 * JUnit Tests for the CodedNodeSetImpl
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 * @version subversion $Revision: $ checked in on $Date: $
 */
public class CodedNodeSetImplTest extends TestCase
{
    public void testUnion() throws LBException
    {
        LexBIGService lbsi = ServiceHolder.instance().getLexBIGService();

        CodedNodeSet cns = lbsi.getCodingSchemeConcepts("Automobiles", null);

        cns.restrictToCodes(Constructors.createConceptReferenceList(new String[]{"005"}, "Automobiles"));

        CodedNodeSet cns2 = lbsi.getCodingSchemeConcepts("Automobiles", null);

        cns2.restrictToCodes(Constructors.createConceptReferenceList(new String[]{"Ford"}, "Automobiles"));

        cns.union(cns2);

        ResolvedConceptReference[] rcr = cns.resolveToList(null, null, null, 50).getResolvedConceptReference();

        assertTrue(rcr.length == 2);
        assertTrue(contains(rcr, "005", "Automobiles"));
        assertTrue(contains(rcr, "Ford", "Automobiles"));
    }

    public void testIntersection() throws LBException
    {
        LexBIGService lbsi = ServiceHolder.instance().getLexBIGService();

        CodedNodeSet cns = lbsi.getCodingSchemeConcepts("Automobiles", null);

        cns.restrictToCodes(Constructors.createConceptReferenceList(new String[]{"005", "Chevy"}, "Automobiles"));

        CodedNodeSet cns2 = lbsi.getCodingSchemeConcepts("Automobiles", null);

        cns2.restrictToCodes(Constructors.createConceptReferenceList(new String[]{"Ford", "005"}, "Automobiles"));

        cns.intersect(cns2);

        ResolvedConceptReference[] rcr = cns.resolveToList(null, null, null, 50).getResolvedConceptReference();

        assertTrue(rcr.length == 1);
        assertTrue(contains(rcr, "005", "Automobiles"));
    }

    public void testDifference() throws LBException
    {
        LexBIGService lbsi = ServiceHolder.instance().getLexBIGService();

        CodedNodeSet cns = lbsi.getCodingSchemeConcepts("Automobiles", null);

        cns.restrictToCodes(Constructors.createConceptReferenceList(new String[]{"005", "Chevy", "Ford", "A0001"},
                                                                    "Automobiles"));

        CodedNodeSet cns2 = lbsi.getCodingSchemeConcepts("Automobiles", null);

        cns2.restrictToCodes(Constructors.createConceptReferenceList(new String[]{"Ford", "005"}, "Automobiles"));

        cns.difference(cns2);

        ResolvedConceptReference[] rcr = cns.resolveToList(null, null, null, 50).getResolvedConceptReference();

        assertTrue(rcr.length == 2);
        assertTrue(contains(rcr, "Chevy", "Automobiles"));
        assertTrue(contains(rcr, "A0001", "Automobiles"));
    }

    public void testUnionToSelf() throws LBException
    {
        LexBIGService lbsi = ServiceHolder.instance().getLexBIGService();

        CodedNodeSet cns = lbsi.getCodingSchemeConcepts("Automobiles", null);

        cns.restrictToCodes(Constructors.createConceptReferenceList(new String[]{"005"}, "Automobiles"));

        cns.union(cns);

        ResolvedConceptReference[] rcr = cns.resolveToList(null, null, null, 50).getResolvedConceptReference();

        assertTrue(rcr.length == 1);
        assertTrue(contains(rcr, "005", "Automobiles"));
    }

    public void testResultLimit() throws LBException
    {
        LexBIGService lbsi = ServiceHolder.instance().getLexBIGService();

        CodedNodeSet cns = lbsi.getCodingSchemeConcepts("Automobiles", null);

        ResolvedConceptReference[] rcr = cns.resolveToList(null, null, null, 1).getResolvedConceptReference();

        assertTrue(rcr.length == 1);
    }

    public void testFilters() throws LBException
    {
        LexBIGService lbsi = ServiceHolder.instance().getLexBIGService();

        CodedNodeSet cns = lbsi.getCodingSchemeConcepts("Automobiles", null);

        ResolvedConceptReference[] rcr = cns.resolveToList(null, null, null, 0).getResolvedConceptReference();
        // no filters - length of 9
        assertTrue(rcr.length == 9);

        try
        {
            TestFilter.register();
        }
        catch (LBParameterException e1)
        {
            // may have been registered by a previous test - thats ok.
        }

        rcr = cns.resolveToList(null, Constructors.createLocalNameList(TestFilter.name_), null, null, 0)
                .getResolvedConceptReference();
        // filter for second letter of entity description being 'a'- length of 2
        assertTrue(rcr.length == 2);

        assertTrue(contains(rcr, "C0001", "Automobiles"));
        assertTrue(contains(rcr, "Jaguar", "Automobiles"));

        // test with iterator

        ResolvedConceptReferencesIterator rcri = cns.resolve(null, Constructors.createLocalNameList(TestFilter.name_),
                                                             null, null);
        // filter for second letter of entity description being 'a'- length of 2
        assertTrue(rcri.numberRemaining() == -1); // number remaining isn't calculated with filters

        // there should be 2 results.

        assertTrue(rcri.hasNext());
        rcr = new ResolvedConceptReference[]{rcri.next()};
        assertTrue(contains(rcr, "C0001", "Automobiles") || contains(rcr, "Jaguar", "Automobiles"));
        assertTrue(rcri.hasNext());
        rcr = new ResolvedConceptReference[]{rcri.next()};
        assertTrue(contains(rcr, "C0001", "Automobiles") || contains(rcr, "Jaguar", "Automobiles"));

        // another call to next should return null - they all got removed by the filter.
        assertTrue(rcri.next() == null);

        assertFalse(rcri.hasNext());

        // yet another call should throw an exception

        try
        {
            rcri.next();
            fail("Didn't throw LBResourceUnavailableException");
        }
        catch (LBResourceUnavailableException e)
        {
            // expected path
        }

        // test with iterator and get(start, end) method. also sort, so we know what order they should be in

        rcri = cns.resolve(Constructors.createSortOptionList(new String[]{"code"}, new Boolean[]{new Boolean(true)}),
                           Constructors.createLocalNameList(TestFilter.name_), null, null);
        // filter for second letter of entity description being 'a'- length of 2
        assertTrue(rcri.numberRemaining() == -1); // number remaining isn't calculated with filters

        // there should be 2 results.

        rcr = rcri.get(0, 1).getResolvedConceptReference();
        assertTrue(rcr.length == 1);
        assertTrue(rcrEquals(rcr[0], "C0001", "Automobiles"));

        rcr = rcri.get(1, 2).getResolvedConceptReference();
        assertTrue(rcr.length == 1);
        assertTrue(rcrEquals(rcr[0], "Jaguar", "Automobiles"));

        try
        {
            rcr = rcri.get(2, 3).getResolvedConceptReference();
            fail("Didn't throw LBParameterException");
        }
        catch (LBParameterException e)
        {
            // expected path
        }

        // go backwards
        rcr = rcri.get(0, 1).getResolvedConceptReference();
        assertTrue(rcr.length == 1);
        assertTrue(rcrEquals(rcr[0], "C0001", "Automobiles"));

        // get both
        rcr = rcri.get(0, 2).getResolvedConceptReference();
        assertTrue(rcr.length == 2);
        assertTrue(rcrEquals(rcr[0], "C0001", "Automobiles"));
        assertTrue(rcrEquals(rcr[1], "Jaguar", "Automobiles"));

        rcri.release();

        // try an invalid filter...
        try
        {
            rcr = cns.resolveToList(null, Constructors.createLocalNameList("invalid"), null, null, 0)
                    .getResolvedConceptReference();
            fail("Didn't throw LBParameterException");
        }
        catch (LBParameterException e)
        {
            // expected path
        }

        // try two filters.

        try
        {
            TestFilter2.register();
        }
        catch (LBParameterException e)
        {
            // can happen if is already registered by another test.
        }

        rcr = cns.resolveToList(null,
                                Constructors.createLocalNameList(new String[]{TestFilter.name_, TestFilter2.name_}),
                                null, null, 0).getResolvedConceptReference();
        // only 1 should pass both filters
        assertTrue(rcr.length == 1);

        assertTrue(contains(rcr, "C0001", "Automobiles"));
    }

    public void testRestrictPropertyTypeReturns() throws LBException
    {
        LexBIGService lbsi = ServiceHolder.instance().getLexBIGService();

        CodedNodeSet cns = lbsi.getCodingSchemeConcepts("Automobiles", null);
        cns.restrictToCodes(Constructors.createConceptReferenceList(new String[] {"A0001"}, "Automobiles"));

        // no type restriction
        ResolvedConceptReference[] rcrs = cns.resolveToList(null, null, null, 0).getResolvedConceptReference();
        assertTrue(rcrs.length == 1);
        Concept ce = rcrs[0].getReferencedEntry();

        assertTrue(ce.getCommentCount() == 0);
        assertTrue(ce.getPropertyCount() == 0);
        assertTrue(ce.getDefinitionCount() == 1);
        //assertTrue(ce.getInstructionCount() == 0);
        assertTrue(ce.getPresentationCount() == 1);

        // restrict to a couple of presentation types
        rcrs = cns.resolveToList(
                                 null,
                                 null,
                                 new PropertyType[]{PropertyType.COMMENT, PropertyType.PRESENTATION,
                                         PropertyType.DEFINITION}, 0).getResolvedConceptReference();
        assertTrue(rcrs.length == 1);
        ce = rcrs[0].getReferencedEntry();

        assertTrue(ce.getCommentCount() == 0);
        assertTrue(ce.getPropertyCount() == 0);
        assertTrue(ce.getDefinitionCount() == 1);
        //assertTrue(ce.getInstructionCount() == 0);
        assertTrue(ce.getPresentationCount() == 1);
        
        assertTrue(ce.getPresentation()[0].getValue().getContent().equals("Automobile"));
        assertTrue(ce.getDefinition()[0].getValue().getContent().equals("An automobile"));
        
       // restrict to one presentation type
        rcrs = cns.resolveToList(
                                 null,
                                 null,
                                 new PropertyType[]{PropertyType.PRESENTATION},
                                 0).getResolvedConceptReference();
        assertTrue(rcrs.length == 1);
        ce = rcrs[0].getReferencedEntry();

        assertTrue(ce.getCommentCount() == 0);
        assertTrue(ce.getPropertyCount() == 0);
        assertTrue(ce.getDefinitionCount() == 0);
        //assertTrue(ce.getInstructionCount() == 0);
        assertTrue(ce.getPresentationCount() == 1);
  
        // restrict to one presentation type and one property name
        rcrs = cns.resolveToList(
                                 null,
                                 Constructors.createLocalNameList("definition"),
                                 new PropertyType[]{PropertyType.DEFINITION},
                                 0).getResolvedConceptReference();
        assertTrue(rcrs.length == 1);
        ce = rcrs[0].getReferencedEntry();

        assertTrue(ce.getCommentCount() == 0);
        assertTrue(ce.getPropertyCount() == 0);
        assertTrue(ce.getDefinitionCount() == 1);
        //assertTrue(ce.getInstructionCount() == 0);
        assertTrue(ce.getPresentationCount() == 0);
        
        // restrict to one presentation type and one property name (which don't line up)
        rcrs = cns.resolveToList(
                                 null,
                                 Constructors.createLocalNameList("textualPresentation"),
                                 new PropertyType[]{PropertyType.DEFINITION},
                                 0).getResolvedConceptReference();
        assertTrue(rcrs.length == 1);
        ce = rcrs[0].getReferencedEntry();

        assertTrue(ce.getCommentCount() == 0);
        assertTrue(ce.getPropertyCount() == 0);
        assertTrue(ce.getDefinitionCount() == 0);
        //assertTrue(ce.getInstructionCount() == 0);
        assertTrue(ce.getPresentationCount() == 0);

    }

    private boolean contains(ResolvedConceptReference[] rcr, String code, String codeSystem)
    {
        boolean contains = false;
        for (int i = 0; i < rcr.length; i++)
        {
            if (rcrEquals(rcr[i], code, codeSystem))
            {
                contains = true;
                break;
            }
        }
        return contains;
    }

    private boolean rcrEquals(ResolvedConceptReference rcr, String code, String codeSystem)
    {
        if (rcr.getConceptCode().equals(code) && rcr.getCodingSchemeName().equals(codeSystem))
        {
            return true;
        }
        return false;
    }
}