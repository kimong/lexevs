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
package org.LexGrid.persistence.dao.hibernate.interceptors;

import org.LexGrid.persistence.constants.PersistenceLayerConstants;
import org.apache.log4j.Logger;
import org.hibernate.EmptyInterceptor;

/**
 * Hibernate Interceptor used to modify the SQL query sent to the database.
 * This interceptor changes the prefix, and also places some extra constraints
 * on the query to ensure that critical queries always use DB table indexes.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class PrefixInterceptor extends EmptyInterceptor {  

	/** The log. */
	private static Logger log = Logger.getLogger(PrefixInterceptor.class.getName());
	
	/** The prefix. */
	private String prefix;
	
	/* (non-Javadoc)
	 * @see org.hibernate.EmptyInterceptor#onPrepareStatement(java.lang.String)
	 */
	public String onPrepareStatement(String sql) { 	
		log.debug("Adjusting table names to prefix: " + prefix);
		sql = sql.replaceAll(PersistenceLayerConstants.PREFIX_PLACEHOLDER, prefix);
	
		return sql;			
	}

	/**
	 * Gets the prefix.
	 * 
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Sets the prefix.
	 * 
	 * @param prefix the new prefix
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}  