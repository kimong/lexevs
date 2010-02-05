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
package org.lexevs.dao.database.spring;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

/**
 * Spring Bean Post Processor to set the dynamically determined Dialect.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class DialectSettingPostProcessor implements BeanPostProcessor {
	
	/** The dialect key. */
	private static String dialectKey = "hibernate.dialect";
	
	/** The dialect. */
	private Class dialect;

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
	 */
	public Object postProcessAfterInitialization(Object arg0, String arg1)
			throws BeansException {
		return arg0;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)
	 */
	public Object postProcessBeforeInitialization(Object arg0, String arg1)
			throws BeansException {
		if(arg0 instanceof LocalSessionFactoryBean){
			LocalSessionFactoryBean sessionFactory = (LocalSessionFactoryBean)arg0;
			Properties props = sessionFactory.getHibernateProperties();
			if(dialect != null){
				props.put(dialectKey, dialect.getName());
			}
		}
		return arg0;
	}

	/**
	 * Gets the dialect.
	 * 
	 * @return the dialect
	 */
	public Class getDialect() {
		return dialect;
	}

	/**
	 * Sets the dialect.
	 * 
	 * @param dialect the new dialect
	 */
	public void setDialect(Class dialect) {
		this.dialect = dialect;
	}
}
