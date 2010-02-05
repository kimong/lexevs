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
package org.LexGrid.LexBIG.Impl.dataAccess;

import java.util.ArrayList;
import java.util.BitSet;

import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Impl.codedNodeSetOperations.RestrictToCodes;
import org.LexGrid.LexBIG.Impl.codedNodeSetOperations.RestrictToEntityTypes;
import org.LexGrid.LexBIG.Impl.codedNodeSetOperations.RestrictToMatchingDesignations;
import org.LexGrid.LexBIG.Impl.codedNodeSetOperations.RestrictToMatchingProperties;
import org.LexGrid.LexBIG.Impl.codedNodeSetOperations.RestrictToProperties;
import org.LexGrid.LexBIG.Impl.codedNodeSetOperations.RestrictToStatus;
import org.LexGrid.LexBIG.Impl.codedNodeSetOperations.interfaces.Restriction;
import org.LexGrid.LexBIG.Impl.helpers.AdditiveCodeHolder;
import org.LexGrid.LexBIG.Impl.helpers.CodeHolder;
import org.LexGrid.LexBIG.Impl.helpers.CodeToReturn;
import org.LexGrid.LexBIG.Impl.helpers.DefaultCodeHolder;
import org.LexGrid.LexBIG.Impl.helpers.ScoredBitSet;
import org.LexGrid.LexBIG.Impl.helpers.ScoredQueryFilter;
import org.LexGrid.LexBIG.Impl.internalExceptions.MissingResourceException;
import org.LexGrid.LexBIG.Impl.internalExceptions.UnexpectedInternalError;
import org.LexGrid.LexBIG.Impl.logging.LgLoggerIF;
import org.LexGrid.LexBIG.Impl.logging.LoggerFactory;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.ActiveOption;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.LexGrid.util.sql.lgTables.SQLTableConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryFilter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;

import edu.mayo.informatics.indexer.lucene.LuceneIndexReader;
import edu.mayo.informatics.lexgrid.convert.indexer.LuceneLoaderCode;

/**
 * Class which implements all of the restriction operations using Lucene
 * searches.
 * 
 * Implementation details - The restrictions work by taking advantage of the
 * fact that lucene searches natively return bitsets - one bit for every
 * document in the index. True for yes, return this document, false for do not
 * return this document. When you do successive searches, you can and the bit
 * sets together to get the merged view of the queries.
 * 
 * Its not quite that simple in our case, however, since our indexes have one
 * document per presentation. So each concept code will have multiple lucene
 * "documents" - and since each document gets its own bit in the bitset - if we
 * match a presentation in search one for Concept A, and then match a different
 * presentation in search 2 for Concept A, and join the bit sets, it won't
 * return any trues for Concept A.
 * 
 * So, after each search, for any concept code which has a bit set to true, we
 * need to set all bits for that concept code to true.
 * 
 * Now, I take advantage of the fact that the documents are added to Lucene in
 * order, and all concept codes are grouped together. The codeBoundyFilter is a
 * special bitset which has a bit set to true for each boundry between concepts.
 * So we can use the codeBoundry filter to rapidly set all of the bits for a
 * concept to true.
 * 
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 * @author <A HREF="mailto:erdmann.jesse@mayo.edu">Jesse Erdmann</A>
 * @author <A HREF="mailto:sharma.deepak2@mayo.edu">Deepak Sharma</A>
 * @author <A HREF="mailto:dwarkanath.sridhar@mayo.edu">Sridhar Dwarkanath</A>
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 * @version subversion $Revision: $ checked in on $Date: $
 */
public class RestrictionImplementations {   
    protected static LgLoggerIF getLogger() {
        return LoggerFactory.getLogger();
    }

    /**
     * Turn the given bit set into a code holder object.
     * 
     * @param bits
     *            bit set for the codeSystemName - every bit that is set to 1
     *            indicates a result that should be returned.
     * @param internalCodeSystemName
     * @return
     * @throws MissingResourceException
     * @throws UnexpectedInternalError
     */
    @Deprecated
    public static CodeHolder bitsToCodeHolder(BitSet bits, String internalCodeSystemName, String internalVersionString)
            throws MissingResourceException, UnexpectedInternalError {
        try {
            AdditiveCodeHolder results = new DefaultCodeHolder();

            boolean collectScores = false;
            if (bits instanceof ScoredBitSet) {
                collectScores = true;
            }

            IndexInterface ii = ResourceManager.instance().getIndexInterface(internalCodeSystemName,
                    internalVersionString);

            LuceneIndexReader reader = ii.getIndexReader(internalCodeSystemName, internalVersionString);

            BitSet boundryBits = ii.getCodeBoundryFilter().bits(reader.getBaseIndexReader());

            SQLTableConstants stc = ResourceManager.instance().getSQLInterface(internalCodeSystemName,
                    internalVersionString).getSQLTableConstants();

            String codeField = stc.entityCodeOrId;

            for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
                Document doc = reader.document(i);
                int endBoundry = boundryBits.nextSetBit(i);

                if (collectScores) {
                    // need to figure out which score to use - pull the scores
                    // for all bits that are
                    // set for this concept code - keep the highest score.
                    float highScore = 0;

                    for (int j = i; j < endBoundry; j++) {
                        Float temp = ((ScoredBitSet) bits).getScore(j);
                        if (temp != null) {
                            float newFloat = temp.floatValue();
                            if (newFloat > highScore) {
                                highScore = newFloat;
                            }
                        }
                    }

                    results.add(new CodeToReturn(
                            doc.get(codeField),
                            doc.get(SQLTableConstants.TBLCOL_ENTITYDESCRIPTION),
                            doc.get("codingSchemeId"),
                            internalVersionString,
                            highScore,
                            doc.get(SQLTableConstants.TBLCOL_ENTITYCODENAMESPACE),
                            doc.getValues("entityType")
                            ));
                } else {
                    results.add(new CodeToReturn(
                            doc.get(codeField),
                            doc.get(SQLTableConstants.TBLCOL_ENTITYDESCRIPTION),
                            doc.get("codingSchemeId"),
                            internalVersionString,
                            0,
                            doc.get(SQLTableConstants.TBLCOL_ENTITYCODENAMESPACE),
                            doc.getValues("entityType")
                            ));
                }
                i = endBoundry;
                if (i < 0) {
                    // found the end
                    break;
                }
            }
            return results;
        } catch (MissingResourceException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedInternalError("There was an unexpected internal error.", e);
        }
    }
    
    public static Query getQuery(Restriction restriction,
            String internalCodeSystemName, String internalVersionString) throws UnexpectedInternalError,
            MissingResourceException, LBParameterException {
        try {             
            SQLTableConstants stc = ResourceManager.instance().getSQLInterface(internalCodeSystemName,
                    internalVersionString).getSQLTableConstants();

            String codeField = stc.entityCodeOrId;
            String propertyNameField = stc.propertyOrPropertyName;
            String language = null;
            Query textQueryPart = null;
            SearchDesignationOption preferredOnly = null;
            ArrayList<String> propertyName = null;
            ArrayList<String> sources = null;
            ArrayList<String> usageContexts = null;
            NameAndValueList propertyQualifiers = null;
            ConceptReferenceList crl = null;
            PropertyType[] propertyType = null;
            String[] conceptStatus = null;
            ActiveOption activeOption = null;
            String[] entityTypes = null;

            if (restriction instanceof RestrictToMatchingDesignations) {
                RestrictToMatchingDesignations temp = (RestrictToMatchingDesignations) restriction;
                language = temp.getLanguage();
                textQueryPart = temp.getTextQuery();
                propertyType = new PropertyType[] { PropertyType.PRESENTATION };
                preferredOnly = temp.getPreferredOption();
                
            } else if (restriction instanceof RestrictToMatchingProperties) {
                RestrictToMatchingProperties temp = (RestrictToMatchingProperties) restriction;
                language = temp.getLanguage();
                textQueryPart = temp.getTextQuery();
                propertyName = localNameListToArrayList(temp.getPropertyList());
                propertyType = temp.getPropertyTypes();
                sources = localNameListToArrayList(temp.getSourceList());
                usageContexts = localNameListToArrayList(temp.getContextList());
                propertyQualifiers = temp.getQualifierList();

            } else if (restriction instanceof RestrictToProperties) {
                RestrictToProperties temp = (RestrictToProperties) restriction;
                propertyName = localNameListToArrayList(temp.getPropertyList());
                propertyType = temp.getPropertyTypes();
                sources = localNameListToArrayList(temp.getSourceList());
                usageContexts = localNameListToArrayList(temp.getContextList());
                propertyQualifiers = temp.getQualifierList();
                
            } else if (restriction instanceof RestrictToCodes) {
                RestrictToCodes temp = (RestrictToCodes) restriction;
                crl = temp.getConceptReferenceList();
                
            } else if (restriction instanceof RestrictToStatus) {
                RestrictToStatus temp = (RestrictToStatus) restriction;
                activeOption = temp.getActiveOption();
                conceptStatus = temp.getStatus();
                
            } else if (restriction instanceof RestrictToEntityTypes) {
                RestrictToEntityTypes temp = (RestrictToEntityTypes) restriction;
                entityTypes = temp.getTypeList();
                
            } else {
                throw new UnexpectedInternalError("An unsupported restriction type was provided.  The type was "
                        + restriction);
            }
            
            BooleanQuery masterQuery = new BooleanQuery();
            masterQuery.add(new BooleanClause(new TermQuery(new Term(SQLTableConstants.TBLCOL_CODINGSCHEMENAME,
                    internalCodeSystemName)), Occur.MUST));

            if (textQueryPart != null) {
                masterQuery.add(new BooleanClause(textQueryPart, Occur.MUST));
            }

            if (language != null && language.length() > 0) {
                masterQuery.add(new BooleanClause(new TermQuery(new Term(SQLTableConstants.TBLCOL_LANGUAGE, language)),
                        Occur.MUST));
            }

            if (activeOption != null) {
                if (activeOption.equals(ActiveOption.ACTIVE_ONLY)) {
                    // This is a MUST_NOT query - do not set addedSomething to true
                    masterQuery.add(new BooleanClause(new TermQuery(new Term(SQLTableConstants.TBLCOL_ISACTIVE, "F")),
                            Occur.MUST_NOT));
                } else if (activeOption.equals(ActiveOption.INACTIVE_ONLY)) {
                    masterQuery.add(new BooleanClause(new TermQuery(new Term(SQLTableConstants.TBLCOL_ISACTIVE, "F")),
                            Occur.MUST));
                }
            }

            if (conceptStatus != null && conceptStatus.length > 0) {
                BooleanQuery nestedQuery = new BooleanQuery();

                for (int i = 0; i < conceptStatus.length; i++) {
                    nestedQuery.add(new BooleanClause(new TermQuery(new Term(SQLTableConstants.TBLCOL_CONCEPTSTATUS,
                            conceptStatus[i])), Occur.SHOULD));
                }
                masterQuery.add(nestedQuery, Occur.MUST);
            }

            if (preferredOnly != null) {
                if (preferredOnly.name().equals(SearchDesignationOption.PREFERRED_ONLY.name())) {
                    masterQuery.add(new BooleanClause(
                            new TermQuery(new Term(SQLTableConstants.TBLCOL_ISPREFERRED, "T")), Occur.MUST));
                } else if (preferredOnly.name().equals(SearchDesignationOption.NON_PREFERRED_ONLY.name())) {
                    masterQuery.add(new BooleanClause(
                            new TermQuery(new Term(SQLTableConstants.TBLCOL_ISPREFERRED, "T")), Occur.MUST_NOT));
                }
                // else it must be all, so I don't need to add anything to the
                // query for that.
            }

            if (propertyName != null && propertyName.size() > 0) {
                int propertiesSize = propertyName.size();
                BooleanQuery nestedQuery = new BooleanQuery();

                for (int i = 0; i < propertiesSize; i++) {
                    nestedQuery.add(new BooleanClause(new TermQuery(new Term(propertyNameField, propertyName.get(i))),
                            Occur.SHOULD));
                }
                masterQuery.add(nestedQuery, Occur.MUST);
            }

            // propertyType
            if (propertyType != null && propertyType.length > 0) {
                BooleanQuery nestedQuery = new BooleanQuery();
                for (int i = 0; i < propertyType.length; i++) {
                    nestedQuery.add(new BooleanClause(new TermQuery(new Term("propertyType",
                            mapPropertyType(propertyType[i]))), Occur.SHOULD));
                }
                masterQuery.add(nestedQuery, Occur.MUST);
            }

            // sources
            if (sources != null && sources.size() > 0) {
                BooleanQuery nestedQuery = new BooleanQuery();

                for (int i = 0; i < sources.size(); i++) {
                    nestedQuery
                            .add(new BooleanClause(new TermQuery(new Term("sources", sources.get(i))), Occur.SHOULD));
                }
                masterQuery.add(nestedQuery, Occur.MUST);
            }

            // usage contexts
            if (usageContexts != null && usageContexts.size() > 0) {
                BooleanQuery nestedQuery = new BooleanQuery();

                for (int i = 0; i < usageContexts.size(); i++) {
                    nestedQuery.add(new BooleanClause(new TermQuery(new Term("usageContexts", usageContexts.get(i))),
                            Occur.SHOULD));
                }
                masterQuery.add(nestedQuery, Occur.MUST);
            }

            // propertyQualifiers
            if (propertyQualifiers != null && propertyQualifiers.getNameAndValueCount() > 0) {
                BooleanQuery nestedQuery = new BooleanQuery();

                for (int i = 0; i < propertyQualifiers.getNameAndValueCount(); i++) {
                    nestedQuery.add(new BooleanClause(new TermQuery(new Term("qualifiers", propertyQualifiers
                            .getNameAndValue(i).getName()
                            + LuceneLoaderCode.QUALIFIER_NAME_VALUE_SPLIT_TOKEN
                            + propertyQualifiers.getNameAndValue(i).getContent())), Occur.SHOULD));
                }
                masterQuery.add(nestedQuery, Occur.MUST);
            }

            // entityTypes
            if (entityTypes != null && entityTypes.length > 0) {
                BooleanQuery nestedQuery = new BooleanQuery();

                for (int i = 0; i < entityTypes.length; i++) {
                    nestedQuery.add(new BooleanClause(new TermQuery(new Term(SQLTableConstants.TBLCOL_ENTITYTYPE,
                            entityTypes[i])), Occur.SHOULD));
                }
                masterQuery.add(nestedQuery, Occur.MUST);
            }

            if (crl != null && crl.getConceptReferenceCount() > 0) {
                BooleanQuery nestedQuery = new BooleanQuery();
                for (int i = 0; i < crl.getConceptReferenceCount(); i++) {
                    ConceptReference cr = crl.getConceptReference(i);

                    // if no coding scheme in the concept reference, substitute
                    // the existing one.
                    // This seems awkward in the next comparison but it prevents
                    // creating another
                    // String object.
                    if (cr.getCodingSchemeName() == null)
                        cr.setCodingSchemeName(internalCodeSystemName);

                    if (ResourceManager.instance().getInternalCodingSchemeNameForUserCodingSchemeName(
                            internalCodeSystemName, internalVersionString).equals(internalCodeSystemName)
                            && cr.getConceptCode() != null && cr.getConceptCode().length() > 0) {
                        BooleanQuery codeAndNamespaceQuery = new BooleanQuery();
                        
                        codeAndNamespaceQuery.add(new BooleanClause(new TermQuery(new Term(codeField, cr.getConceptCode())),
                                Occur.MUST));
                        if(StringUtils.isNotBlank(cr.getCodeNamespace())){
                            codeAndNamespaceQuery.add(new BooleanClause(new TermQuery(new Term(SQLTableConstants.TBLCOL_ENTITYCODENAMESPACE, cr.getCodeNamespace())),
                                    Occur.MUST));
                        }
                      nestedQuery.add(codeAndNamespaceQuery, Occur.SHOULD);
                    }
                }
                masterQuery.add(nestedQuery, Occur.MUST);
            }

            masterQuery.add(new BooleanClause(new TermQuery(new Term("codeBoundry", "T")), Occur.MUST_NOT));

            getLogger().debug("Generated Query: " + masterQuery.toString());          

            return masterQuery;

        } catch (MissingResourceException e) {
            throw e;
        } catch (UnexpectedInternalError e) {
            throw e;
        } catch (LBParameterException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedInternalError("There was an unexpected internal error.", e);
        }
    }

    /**
     * This method implements all of the Restrictions, returning a bitSet which
     * will have a bit set to 1 for each document in the lucene index that
     * satisfies the restriction.
     * 
     * @param restriction
     * @param collectScores
     * @param internalCodeSystemName
     * @param activeConceptsOnly
     * @return
     * @throws UnexpectedInternalError
     * @throws MissingResourceException
     * @throws LBParameterException
     */
    @Deprecated
    public static BitSet restrictToBitSet(Restriction restriction, boolean collectScores,
            String internalCodeSystemName, String internalVersionString) throws UnexpectedInternalError,
            MissingResourceException, LBParameterException {
        try {
            IndexInterface ii = ResourceManager.instance().getIndexInterface(internalCodeSystemName,
                    internalVersionString);

            SQLTableConstants stc = ResourceManager.instance().getSQLInterface(internalCodeSystemName,
                    internalVersionString).getSQLTableConstants();

            String codeField = stc.entityCodeOrId;
            String propertyNameField = stc.propertyOrPropertyName;
            String language = null;
            Query textQueryPart = null;
            SearchDesignationOption preferredOnly = null;
            ArrayList<String> propertyName = null;
            ArrayList<String> sources = null;
            ArrayList<String> usageContexts = null;
            NameAndValueList propertyQualifiers = null;
            ConceptReferenceList crl = null;
            PropertyType[] propertyType = null;
            String[] conceptStatus = null;
            ActiveOption activeOption = null;
            String[] entityTypes = null;

            if (restriction instanceof RestrictToMatchingDesignations) {
                RestrictToMatchingDesignations temp = (RestrictToMatchingDesignations) restriction;
                language = temp.getLanguage();
                textQueryPart = temp.getTextQuery();
                propertyType = new PropertyType[] { PropertyType.PRESENTATION };
                preferredOnly = temp.getPreferredOption();
                
            } else if (restriction instanceof RestrictToMatchingProperties) {
                RestrictToMatchingProperties temp = (RestrictToMatchingProperties) restriction;
                language = temp.getLanguage();
                textQueryPart = temp.getTextQuery();
                propertyName = localNameListToArrayList(temp.getPropertyList());
                propertyType = temp.getPropertyTypes();
                sources = localNameListToArrayList(temp.getSourceList());
                usageContexts = localNameListToArrayList(temp.getContextList());
                propertyQualifiers = temp.getQualifierList();

            } else if (restriction instanceof RestrictToProperties) {
                RestrictToProperties temp = (RestrictToProperties) restriction;
                propertyName = localNameListToArrayList(temp.getPropertyList());
                propertyType = temp.getPropertyTypes();
                sources = localNameListToArrayList(temp.getSourceList());
                usageContexts = localNameListToArrayList(temp.getContextList());
                propertyQualifiers = temp.getQualifierList();
                
            } else if (restriction instanceof RestrictToCodes) {
                RestrictToCodes temp = (RestrictToCodes) restriction;
                crl = temp.getConceptReferenceList();
                
            } else if (restriction instanceof RestrictToStatus) {
                RestrictToStatus temp = (RestrictToStatus) restriction;
                activeOption = temp.getActiveOption();
                conceptStatus = temp.getStatus();
                
            } else if (restriction instanceof RestrictToEntityTypes) {
                RestrictToEntityTypes temp = (RestrictToEntityTypes) restriction;
                entityTypes = temp.getTypeList();
                
            } else {
                throw new UnexpectedInternalError("An unsupported restriction type was provided.  The type was "
                        + restriction);
            }

            IndexReader reader = ii.getIndexReader(internalCodeSystemName, internalVersionString).getBaseIndexReader();
            BooleanQuery masterQuery = new BooleanQuery();
            masterQuery.add(new BooleanClause(new TermQuery(new Term(SQLTableConstants.TBLCOL_CODINGSCHEMENAME,
                    internalCodeSystemName)), Occur.MUST));
            boolean addedSomething = false;

            if (textQueryPart != null) {
                masterQuery.add(new BooleanClause(textQueryPart, Occur.MUST));
                addedSomething = true;
            }

            if (language != null && language.length() > 0) {
                masterQuery.add(new BooleanClause(new TermQuery(new Term(SQLTableConstants.TBLCOL_LANGUAGE, language)),
                        Occur.MUST));
                addedSomething = true;
            }

            if (activeOption != null) {
                if (activeOption.equals(ActiveOption.ACTIVE_ONLY)) {
                    // This is a MUST_NOT query - do not set addedSomething to true
                    masterQuery.add(new BooleanClause(new TermQuery(new Term(SQLTableConstants.TBLCOL_ISACTIVE, "F")),
                            Occur.MUST_NOT));
                } else if (activeOption.equals(ActiveOption.INACTIVE_ONLY)) {
                    masterQuery.add(new BooleanClause(new TermQuery(new Term(SQLTableConstants.TBLCOL_ISACTIVE, "F")),
                            Occur.MUST));
                    addedSomething = true;
                }
            }

            if (conceptStatus != null && conceptStatus.length > 0) {
                BooleanQuery nestedQuery = new BooleanQuery();

                for (int i = 0; i < conceptStatus.length; i++) {
                    nestedQuery.add(new BooleanClause(new TermQuery(new Term(SQLTableConstants.TBLCOL_CONCEPTSTATUS,
                            conceptStatus[i])), Occur.SHOULD));
                }
                masterQuery.add(nestedQuery, Occur.MUST);
                addedSomething = true;
            }

            if (preferredOnly != null) {
                if (preferredOnly.name().equals(SearchDesignationOption.PREFERRED_ONLY.name())) {
                    masterQuery.add(new BooleanClause(
                            new TermQuery(new Term(SQLTableConstants.TBLCOL_ISPREFERRED, "T")), Occur.MUST));
                    addedSomething = true;
                } else if (preferredOnly.name().equals(SearchDesignationOption.NON_PREFERRED_ONLY.name())) {
                    masterQuery.add(new BooleanClause(
                            new TermQuery(new Term(SQLTableConstants.TBLCOL_ISPREFERRED, "T")), Occur.MUST_NOT));
                    addedSomething = true;
                }
                // else it must be all, so I don't need to add anything to the
                // query for that.
            }

            if (propertyName != null && propertyName.size() > 0) {
                int propertiesSize = propertyName.size();
                BooleanQuery nestedQuery = new BooleanQuery();

                for (int i = 0; i < propertiesSize; i++) {
                    nestedQuery.add(new BooleanClause(new TermQuery(new Term(propertyNameField, propertyName.get(i))),
                            Occur.SHOULD));
                }
                masterQuery.add(nestedQuery, Occur.MUST);
                addedSomething = true;
            }

            // propertyType
            if (propertyType != null && propertyType.length > 0) {
                BooleanQuery nestedQuery = new BooleanQuery();
                for (int i = 0; i < propertyType.length; i++) {
                    nestedQuery.add(new BooleanClause(new TermQuery(new Term("propertyType",
                            mapPropertyType(propertyType[i]))), Occur.SHOULD));
                }
                masterQuery.add(nestedQuery, Occur.MUST);
                addedSomething = true;
            }

            // sources
            if (sources != null && sources.size() > 0) {
                BooleanQuery nestedQuery = new BooleanQuery();

                for (int i = 0; i < sources.size(); i++) {
                    nestedQuery
                            .add(new BooleanClause(new TermQuery(new Term("sources", sources.get(i))), Occur.SHOULD));
                }
                masterQuery.add(nestedQuery, Occur.MUST);
                addedSomething = true;
            }

            // usage contexts
            if (usageContexts != null && usageContexts.size() > 0) {
                BooleanQuery nestedQuery = new BooleanQuery();

                for (int i = 0; i < usageContexts.size(); i++) {
                    nestedQuery.add(new BooleanClause(new TermQuery(new Term("usageContexts", usageContexts.get(i))),
                            Occur.SHOULD));
                }
                masterQuery.add(nestedQuery, Occur.MUST);
                addedSomething = true;
            }

            // propertyQualifiers
            if (propertyQualifiers != null && propertyQualifiers.getNameAndValueCount() > 0) {
                BooleanQuery nestedQuery = new BooleanQuery();

                for (int i = 0; i < propertyQualifiers.getNameAndValueCount(); i++) {
                    nestedQuery.add(new BooleanClause(new TermQuery(new Term("qualifiers", propertyQualifiers
                            .getNameAndValue(i).getName()
                            + LuceneLoaderCode.QUALIFIER_NAME_VALUE_SPLIT_TOKEN
                            + propertyQualifiers.getNameAndValue(i).getContent())), Occur.SHOULD));
                }
                masterQuery.add(nestedQuery, Occur.MUST);
                addedSomething = true;
            }

            // entityTypes
            if (entityTypes != null && entityTypes.length > 0) {
                BooleanQuery nestedQuery = new BooleanQuery();

                for (int i = 0; i < entityTypes.length; i++) {
                    nestedQuery.add(new BooleanClause(new TermQuery(new Term(SQLTableConstants.TBLCOL_ENTITYTYPE,
                            entityTypes[i])), Occur.SHOULD));
                }
                masterQuery.add(nestedQuery, Occur.MUST);
                addedSomething = true;
            }

            if (crl != null && crl.getConceptReferenceCount() > 0) {
                BooleanQuery nestedQuery = new BooleanQuery();
                for (int i = 0; i < crl.getConceptReferenceCount(); i++) {
                    ConceptReference cr = crl.getConceptReference(i);

                    // if no coding scheme in the concept reference, substitute
                    // the existing one.
                    // This seems awkward in the next comparison but it prevents
                    // creating another
                    // String object.
                    if (cr.getCodingSchemeName() == null)
                        cr.setCodingSchemeName(internalCodeSystemName);

                    if (ResourceManager.instance().getInternalCodingSchemeNameForUserCodingSchemeName(
                            internalCodeSystemName, internalVersionString).equals(internalCodeSystemName)
                            && cr.getConceptCode() != null && cr.getConceptCode().length() > 0) {
                        nestedQuery.add(new BooleanClause(new TermQuery(new Term(codeField, cr.getConceptCode())),
                                Occur.SHOULD));
                    }
                }
                masterQuery.add(nestedQuery, Occur.MUST);
                addedSomething = true;
            }

            if (!addedSomething) {
                // nothing in the query other than coding scheme - need to not
                // match on codeBoundry documents.
                masterQuery.add(new BooleanClause(new TermQuery(new Term("codeBoundry", "T")), Occur.MUST_NOT));
            }

            getLogger().debug("Generated Query: " + masterQuery.toString());

            BitSet queryResult;
            if (collectScores) {
                queryResult = new ScoredQueryFilter(masterQuery).bits(reader);
            } else {
                queryResult = new QueryFilter(masterQuery).bits(reader);
            }

            BitSet boundryBits = ii.getCodeBoundryFilter().bits(reader);
            int pos = 0;
            int bitSetLength = queryResult.length();
            for (; pos < bitSetLength; pos++) {
                // jump to the next set bit
                pos = queryResult.nextSetBit(pos);
                if (pos == -1) {
                    // done
                    break;
                }

                int endBoundry = boundryBits.nextSetBit(pos);
                if (endBoundry == -1) {
                    // query matched the last document in the index.
                    endBoundry = bitSetLength;
                }

                int startBoundry = pos - 1;

                // find the real start boundry
                while (true) {
                    if (boundryBits.get(startBoundry)) {
                        // found the start boundry
                        break;
                    } else {
                        startBoundry--;
                    }
                }

                // now set all of the bits for a concept code (the ones
                // inbetween the boundries) to true
                queryResult.set(startBoundry + 1, endBoundry);
                pos = endBoundry;
            }

            return queryResult;

        } catch (MissingResourceException e) {
            throw e;
        } catch (UnexpectedInternalError e) {
            throw e;
        } catch (LBParameterException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedInternalError("There was an unexpected internal error.", e);
        }
    }

    /*
     * Utility method.
     */
    private static ArrayList<String> localNameListToArrayList(LocalNameList lnl) {
        ArrayList<String> properties = new ArrayList<String>();
        if (lnl != null) {
            String[] localNames = lnl.getEntry();
            if (localNames != null) {
                for (int i = 0; i < localNames.length; i++) {
                    String property = localNames[i];
                    if (property != null && property.length() > 0) {
                        properties.add(property);
                    }
                }

            }
        }
        return properties;
    }

    protected static String mapPropertyType(PropertyType propertyType) throws LBInvocationException {
        if (propertyType.equals(PropertyType.COMMENT)) {
            return SQLTableConstants.TBLCOLVAL_COMMENT;
        } else if (propertyType.equals(PropertyType.DEFINITION)) {
            return SQLTableConstants.TBLCOLVAL_DEFINITION;
        } else if (propertyType.equals(PropertyType.GENERIC)) {
            return SQLTableConstants.TBLCOLVAL_PROPERTY;
        } else if (propertyType.equals(PropertyType.PRESENTATION)) {
            return SQLTableConstants.TBLCOLVAL_PRESENTATION;
        } else {
            String id = getLogger().error("UnexpectedPropertyType - " + propertyType);
            throw new LBInvocationException("Unexpected PropertyType", id);
        }
    }
}