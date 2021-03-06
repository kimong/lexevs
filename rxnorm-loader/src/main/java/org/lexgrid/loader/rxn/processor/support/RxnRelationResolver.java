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
package org.lexgrid.loader.rxn.processor.support;

import org.lexgrid.loader.rrf.constants.RrfLoaderConstants;
import org.lexgrid.loader.rrf.model.Mrrel;
import org.lexgrid.loader.rrf.processor.support.AbstractRrfRelationResolver;
import org.lexgrid.loader.rrf.staging.MrconsoStagingDao;

/**
 * The Class RxnRelationResolver.
 * 
 * @author <a href="mailto:kanjamala.pradip@mayo.edu">Pradip Kanjamala</a>
 */
public class RxnRelationResolver extends AbstractRrfRelationResolver {

	/** The mrconso staging dao. */
	private MrconsoStagingDao mrconsoStagingDao;

	/* (non-Javadoc)
	 * @see org.lexgrid.loader.rrf.processor.support.AbstractRrfRelationResolver#getSource(org.lexgrid.loader.rrf.model.Mrrel)
	 */
	public String getSource(Mrrel item) {
		return item.getCui2();
	}

	/* (non-Javadoc)
	 * @see org.lexgrid.loader.rrf.processor.support.AbstractRrfRelationResolver#getTarget(org.lexgrid.loader.rrf.model.Mrrel)
	 */
	public String getTarget(Mrrel item) {
		return item.getCui1();
	}
	
	public String getRelation(Mrrel item){
		return item.getRela();
	}
	
	/* (non-Javadoc)
	 * @see org.lexgrid.loader.rrf.processor.support.AbstractRrfRelationResolver#getContainerName()
	 */
	@Override
	public String getContainerName() {
		return RrfLoaderConstants.UMLS_RELATIONS_NAME;
	}	
	
	/**
	 * Gets the mrconso staging dao.
	 * 
	 * @return the mrconso staging dao
	 */
	public MrconsoStagingDao getMrconsoStagingDao() {
		return mrconsoStagingDao;
	}

	/**
	 * Sets the mrconso staging dao.
	 * 
	 * @param mrconsoStagingDao the new mrconso staging dao
	 */
	public void setMrconsoStagingDao(MrconsoStagingDao mrconsoStagingDao) {
		this.mrconsoStagingDao = mrconsoStagingDao;
	}	
}
