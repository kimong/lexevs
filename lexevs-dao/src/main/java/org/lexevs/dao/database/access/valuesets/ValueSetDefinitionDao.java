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
package org.lexevs.dao.database.access.valuesets;

import java.net.URI;
import java.util.List;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.valueSets.DefinitionEntry;
import org.LexGrid.valueSets.ValueSetDefinition;
import org.lexevs.dao.database.access.LexGridSchemaVersionAwareDao;

/**
 * The Interface ValueSetDefinitionDao.
 * 
 * @author <a href="mailto:dwarkanath.sridhar@mayo.edu">Sridhar Dwarkanath</a>
 */
public interface ValueSetDefinitionDao extends LexGridSchemaVersionAwareDao {
	
	/**
	 * Gets the value set definition by URI.
	 * 
	 * @param valueSetDefinitionURI the value set definition URI
	 * 
	 * @return the value set definition by value set definition URI
	 */
	public ValueSetDefinition getValueSetDefinitionByURI(String valueSetDefinitionURI);
	
	/**
	 * Gets the GUID from value set definition URI
	 * 
	 * @param valueSetDefinitionURI the value set definition URI
	 * 
	 * @return the GUID from value set definition URI
	 */
	public String getGuidFromvalueSetDefinitionURI(String valueSetDefinitionURI);
	
	/**
	 * Insert value set definition.
	 * 
	 * @param systemReleaseUri the system release URI
	 * @param definition the definition
	 * 
	 * @return the string
	 */
	public String insertValueSetDefinition(String systemReleaseUri, ValueSetDefinition definition);

	/**
	 * Gets the value set definition URIs.
	 * 
	 * @return List of value set definition URIs
	 */
	public List<String> getValueSetDefinitionURIs();
	
	/**
	 * Return the URI's for the value set definition(s) for the supplied
	 * value set definition name. If the name is null, returns everything. If the name is not
	 * null, returns the value set definition(s) that have the assigned name. 
	 * 
	 * Note: plural because there is no guarantee of valueSetDefinition uniqueness. If the name is the
	 * empty string "", returns all unnamed valueSetDefinitions.
	 * 
	 * @param valueSetDefinitionName
	 * @return value domain URI's
	 * @throws LBException
	 */
	public List<String> getValueSetDefinitionURIsForName(String valueSetDefinitionName) throws LBException;
	
	/**
	 * Return the URI's of all unnamed value set definition(s).
	 * 
	 * @return value set definition URI's
	 * @throws LBException
	 */
	public List<String> getAllValueSetDefinitionsWithNoName()  throws LBException;
	
	/**
	 * Insert value set definition entry.
	 * 
	 * @param valueSetDefinitionGuid the value set definition GUID
	 * @param definitionEntry the Value Set definitionEntry
	 * 
	 * @return the string
	 */
	public String insertDefinitionEntry(String valueSetDefinitionGuid, DefinitionEntry definitionEntry);
	
	/**
	 * Delete value set definition by value set definition URI.
	 * 
	 * @param valuesetdefinitionURI the value set definition URI
	 */
	public void removeValueSetDefinitionByValueSetDefinitionURI(String valueSetDefinitionURI);
}
