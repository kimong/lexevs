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
package org.lexgrid.loader.umls.integration.dataload;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.concepts.Entity;
import org.junit.Before;
import org.junit.Test;
import org.lexgrid.loader.rrf.constants.RrfLoaderConstants;

import test.util.DataTestUtils;

/**
 * The Class SameCodeDifferentCuiTest.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class SameCodeDifferentCuiTestIT extends DataLoadTestBase {
	
	/** The test entity. */
	private Entity testEntity;

	/**
	 * Builds the test entity.
	 * 
	 * @throws Exception the exception
	 */
	@Before
	public void buildTestEntity() throws Exception {
		cns.restrictToCodes(Constructors.createConceptReferenceList("ALOPE"));
		ResolvedConceptReference rcr1 = cns.resolveToList(null, null, null, -1).getResolvedConceptReference()[0];
	
		testEntity = rcr1.getEntity();
	}
	
	/**
	 * Test property not null.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testPropertyNotNull() throws Exception {	
		assertNotNull(testEntity.getProperty());
	}
	
	
	/**
	 * Test presentation poperty count.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testPresentationPopertyCount() throws Exception {	
		Property[] preses = testEntity.getPresentation();
		assertTrue(preses.length == 2);
	}
	
	/**
	 * Test pres poperty name cui1.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testPresPopertyNameCui1() throws Exception {	
		Property prop = DataTestUtils.getPropertyWithValue(testEntity.getPresentation(), "Alopecia");
		assertNotNull(prop);
	}
	
	/**
	 * Test pres poperty name cui2.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testPresPopertyNameCui2() throws Exception {	
		Property prop = DataTestUtils.getPropertyWithValue(testEntity.getPresentation(), "Alopecia - Different CUI");
		assertNotNull(prop);
	}
	
	/**
	 * Test cui property count.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testCuiPropertyCount() throws Exception {	
		List<Property> props = DataTestUtils.getPropertiesFromEntity(testEntity, RrfLoaderConstants.UMLS_CUI_PROPERTY);
		assertTrue(props.size() == 2);
	}
	
	/**
	 * Test cui property cui1.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testCuiPropertyCui1() throws Exception {	
		assertTrue(DataTestUtils.isPropertyWithValuePresent(testEntity, 
				RrfLoaderConstants.UMLS_CUI_PROPERTY, 
				"C0002170"));
	}
	
	/**
	 * Test cui property cui2.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testCuiPropertyCui2() throws Exception {	
		assertTrue(DataTestUtils.isPropertyWithValuePresent(testEntity, 
				RrfLoaderConstants.UMLS_CUI_PROPERTY, 
				"C0002171"));
	}
}