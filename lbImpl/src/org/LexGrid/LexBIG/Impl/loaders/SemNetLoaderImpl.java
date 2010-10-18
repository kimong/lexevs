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
package org.LexGrid.LexBIG.Impl.loaders;

import java.util.Arrays;

import org.LexGrid.LexBIG.DataModel.InterfaceElements.ExtensionDescription;
import org.LexGrid.LexBIG.Extensions.Load.Loader;
import org.LexGrid.LexBIG.Extensions.Load.options.Option;
import org.LexGrid.LexBIG.Extensions.Load.options.OptionHolder;
import org.LexGrid.LexBIG.Utility.logging.LgLoggerIF;
import org.LexGrid.codingSchemes.CodingScheme;
import org.lexevs.dao.database.service.exception.CodingSchemeAlreadyLoadedException;
import org.lexevs.logging.LoggerFactory;

import edu.mayo.informatics.lexgrid.convert.directConversions.semnet.SemNetToLg;
import edu.mayo.informatics.lexgrid.convert.options.IntegerOption;
import edu.mayo.informatics.lexgrid.convert.utility.URNVersionPair;

/**
 * This loader loads a series of coding schemes to a LexBIG service according to
 * a mapping of HL7 RIM database elements to LexBIG/LexGrid.
 * 
 * @author <A HREF="mailto:scott.bauer@mayo.edu">Scott Bauer</A>
 * @author <A HREF="mailto:stancl.craig@mayo.edu">Craig Stancl</A>
 * 
 */
public class SemNetLoaderImpl extends BaseLoader {

    private static final long serialVersionUID = 8781875750618588633L;
    public static final String name = "SemNetLoader";
    private static final String description = "This loader loads UMLS SemNet Content."
            + " into a LexGrid database.";
    
    public static String INHERITANCE_LEVEL = "Inheritance Level";

    public String metaDataFileLocation;

    protected LgLoggerIF getLogger() {
        return LoggerFactory.getLogger();
    }

    public SemNetLoaderImpl() {
        super();
    }

    @Override
    protected OptionHolder declareAllowedOptions(OptionHolder holder) {
        holder.setIsResourceUriFolder(true);
        
        IntegerOption inheretanceOption = new IntegerOption(INHERITANCE_LEVEL);
        inheretanceOption.setPickList(Arrays.asList(0,1,2));
        
        holder.getIntegerOptions().add(inheretanceOption);
        return holder;
    }

    @Override
    protected URNVersionPair[] doLoad() throws CodingSchemeAlreadyLoadedException {
        
        Option<Integer> inheretanceOption = this.getOptions().getIntegerOption(INHERITANCE_LEVEL);
 
        SemNetToLg mainTxfm = new SemNetToLg(this.getResourceUri(), inheretanceOption.getOptionValue(), this.getMessageDirector());
        CodingScheme codingScheme;
        try {
            codingScheme = mainTxfm.readCodingScheme();
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
  
        this.persistCodingSchemeToDatabase(codingScheme);
        
        return new URNVersionPair[] {
                new URNVersionPair(codingScheme.getCodingSchemeURI(), codingScheme.getRepresentsVersion())};
    }

    @Override
    protected ExtensionDescription buildExtensionDescription() {
        ExtensionDescription temp = new ExtensionDescription();
        temp.setExtensionBaseClass(Loader.class.getName());
        temp.setExtensionClass(SemNetLoaderImpl.class.getName());
        temp.setDescription(description);
        temp.setName(name);

        return temp;
    }
}