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
 *      http://www.eclipse.org/legal/epl-v10.html
 * 
 */
package edu.mayo.informatics.lexgrid.convert.emfConversions.protegeOwl;

import java.util.Collection;

import org.LexGrid.emf.commonTypes.CommontypesFactory;
import org.LexGrid.emf.commonTypes.Property;
import org.LexGrid.emf.commonTypes.PropertyQualifier;
import org.LexGrid.emf.commonTypes.Source;
import org.LexGrid.emf.commonTypes.Text;
import org.LexGrid.emf.concepts.Comment;
import org.LexGrid.emf.concepts.ConceptsFactory;
import org.LexGrid.emf.concepts.Definition;
import org.LexGrid.emf.concepts.Presentation;
import org.LexGrid.emf.relations.AssociationData;
import org.LexGrid.emf.relations.AssociationQualification;
import org.LexGrid.emf.relations.AssociationSource;
import org.LexGrid.emf.relations.AssociationTarget;
import org.LexGrid.emf.relations.RelationsFactory;
import org.apache.commons.lang.StringUtils;

import edu.mayo.informatics.lexgrid.convert.emfConversions.EMFSupportedMappings;
import edu.stanford.smi.protegex.owl.model.RDFProperty;

public class EMFCreateUtils {
    // /////////////////////////////////////////////
    // // Create and register LexGrid objects //////
    // /////////////////////////////////////////////

    public static AssociationData createAssociationTextData(String data) {
        AssociationData emfData = RelationsFactory.eINSTANCE.createAssociationData();
        emfData.setAssociationDataText(createText(data));
        return emfData;
    }

    public static AssociationQualification createAssociationQualification(RDFProperty rdfProp, EMFSupportedMappings emfSupportedMappings_) {
        String brText = rdfProp.getBrowserText();
        Collection labels = rdfProp.getLabels();
        AssociationQualification emfQual = createAssociationQualification(brText, rdfProp.getURI(),
                labels.isEmpty() ? brText : labels.iterator().next().toString(), emfSupportedMappings_);
        return emfQual;
    }

    public static AssociationQualification createAssociationQualification(String name, String uri, String descriptiveText, EMFSupportedMappings emfSupportedMappings_) {
        AssociationQualification emfQual = RelationsFactory.eINSTANCE.createAssociationQualification();
        emfQual.setAssociationQualifier(name);
        emfSupportedMappings_.registerSupportedAssociationQualifier(name, uri, descriptiveText, false);
        return emfQual;
    }

    public static AssociationSource createAssociationSource(String emfConceptCode, String namespace) {
        AssociationSource emfSrc = RelationsFactory.eINSTANCE.createAssociationSource();
        if (StringUtils.isNotBlank(emfConceptCode))
            emfSrc.setSourceEntityCode(emfConceptCode);
        if (StringUtils.isNotBlank(namespace))
            emfSrc.setSourceEntityCodeNamespace(namespace);
        return emfSrc;
    }

    public static AssociationTarget createAssociationTarget(String emfConceptCode, String namespace) {
        AssociationTarget emfTgt = RelationsFactory.eINSTANCE.createAssociationTarget();
        if (emfConceptCode != null)
            emfTgt.setTargetEntityCode(emfConceptCode);
        if (namespace != null)
            emfTgt.setTargetEntityCodeNamespace(namespace);
        return emfTgt;
    }

    public static Comment createComment(String propID, String propName, String text, EMFSupportedMappings emfSupportedMappings_) {
        Comment emfComment = ConceptsFactory.eINSTANCE.createComment();
        emfComment.setPropertyId(propID);
        emfComment.setPropertyName(propName);
        emfComment.setValue(createText(text));
        emfSupportedMappings_.registerSupportedProperty(propName, null, propName, false);
        return emfComment;
    }

    public static Definition createDefinition(String propID, String propName, String text, Boolean isPreferred, EMFSupportedMappings emfSupportedMappings_) {
        Definition emfDefn = ConceptsFactory.eINSTANCE.createDefinition();
        emfDefn.setPropertyId(propID);
        emfDefn.setPropertyName(propName);
        emfDefn.setValue(createText(text));
        if (isPreferred != null)
            emfDefn.setIsPreferred(isPreferred);
        emfSupportedMappings_.registerSupportedProperty(propName, null, propName, false);
        return emfDefn;
    }

    public static Presentation createPresentation(String propID, String propName, String text, Boolean isPreferred, EMFSupportedMappings emfSupportedMappings_) {
        Presentation emfPres = ConceptsFactory.eINSTANCE.createPresentation();
        emfPres.setPropertyId(propID);
        emfPres.setPropertyName(propName);
        emfPres.setValue(createText(text));
        if (isPreferred != null)
            emfPres.setIsPreferred(isPreferred);
        emfSupportedMappings_.registerSupportedProperty(propName, null, propName, false);
        return emfPres;
    }

    public static Property createProperty(String propID, String propName, String text, EMFSupportedMappings emfSupportedMappings_) {
        Property emfProp = CommontypesFactory.eINSTANCE.createProperty();
        emfProp.setPropertyName(propName);
        emfProp.setPropertyId(propID);
        emfProp.setValue(createText(text));
        emfSupportedMappings_.registerSupportedProperty(propName, null, propName, false);
        return emfProp;
    }

    public static PropertyQualifier createPropertyQualifier(String tag, String text, EMFSupportedMappings emfSupportedMappings_) {
        PropertyQualifier emfPropQual = CommontypesFactory.eINSTANCE.createPropertyQualifier();
        emfPropQual.setPropertyQualifierName(tag);
        emfPropQual.setValue(createText(text));
        emfSupportedMappings_.registerSupportedPropertyQualifier(tag, null, tag, false);
        return emfPropQual;
    }

    public static Source createSource(String value, String role, String subref, EMFSupportedMappings emfSupportedMappings_) {
        Source emfSource = CommontypesFactory.eINSTANCE.createSource();
        emfSource.setValue(value);
        emfSource.setRole(role);
        emfSource.setSubRef(subref);
        if (value != null)
            emfSupportedMappings_.registerSupportedSource(value, null, value, null, false);
        if (role != null)
            emfSupportedMappings_.registerSupportedSourceRole(role, null, role, false);
        return emfSource;
    }

    public static Text createText(String value) {
        Text emfText = CommontypesFactory.eINSTANCE.createText();
        emfText.setValue(value);
        emfText.setDataType(ProtegeOwl2EMFConstants.DATATYPE_STRING);
        return emfText;
    }
}
