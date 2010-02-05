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
package org.lexgrid.loader.dao.template;

import static org.junit.Assert.assertTrue;

import org.LexGrid.persistence.dao.LexEvsDao;
import org.LexGrid.persistence.model.CodingSchemeSupportedAttrib;
import org.LexGrid.persistence.model.CodingSchemeSupportedAttribId;
import org.LexGrid.persistence.spring.DynamicPropertyApplicationContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lexgrid.loader.properties.ConnectionPropertiesFactory;
import org.lexgrid.loader.properties.impl.DefaultLexEVSPropertiesFactory;
import org.springframework.context.ApplicationContext;

/**
 * The Class SupportedAttributeSupportTest.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class SupportedAttributeSupportTest {
	
	/** The template. */
	private static SupportedAttributeTemplate template;	
	
	/** The lex evs dao. */
	private static LexEvsDao lexEvsDao;	
	
	/**
	 * Before tests setup.
	 * 
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void beforeTestsSetup() throws Exception {
		System.setProperty("LG_CONFIG_FILE", "src/test/resources/config/configSingleDb.props");
		ConnectionPropertiesFactory props = new DefaultLexEVSPropertiesFactory();
		ApplicationContext ctx = new DynamicPropertyApplicationContext("baseLoader.xml", props.getPropertiesForNewLoad(true));
		template = (SupportedAttributeTemplate)ctx.getBean("cachingSupportedAttribuiteTemplate");
		lexEvsDao = (LexEvsDao)ctx.getBean("lexEvsDao");		
	}
	
	/**
	 * Test insert.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testInsert() throws Exception {
		template.addSupportedAssociation("testCsName", "testId", "testUri", "testContent");
		
		CodingSchemeSupportedAttribId id = new CodingSchemeSupportedAttribId();
		id.setCodingSchemeName("testCsName");
		id.setSupportedAttributeTag("Association");
		id.setId("testId");
		id.setIdValue("testContent");
		id.setVal1(" ");

		CodingSchemeSupportedAttrib result = lexEvsDao.findById(CodingSchemeSupportedAttrib.class, id);
		
		assertTrue(result != null);
	}
}
