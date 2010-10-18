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
package org.LexGrid.LexBIG.Impl.helpers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.ActiveOption;
import org.LexGrid.LexBIG.Utility.ServiceUtility;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.annotations.LgClientSideSafe;
import org.LexGrid.annotations.LgProxyClass;
import org.LexGrid.concepts.Entity;
import org.lexevs.locator.LexEvsServiceLocator;

@LgClientSideSafe
public class ToNodeListResolvedConceptReferencesIteratorDecorator implements ResolvedConceptReferencesIterator {

    private static final long serialVersionUID = 765976499602536922L;
    
    private CodeHolder toNodeListCodes;
    private ResolvedConceptReferencesIterator delegate;
    private ActiveOption activeOption;
    private boolean haveInactivesBeenRemoved = false;
    
    private RemoveInactiveRunner removeInactiveRunner = 
        new RemoveInactiveRunner();
    
    private ToResolvedConceptReferenceRunner toResolvedConceptReferenceRunner = 
        new ToResolvedConceptReferenceRunner();
    
    public ToNodeListResolvedConceptReferencesIteratorDecorator(
            ResolvedConceptReferencesIterator delegate, 
            CodeHolder toNodeListCodes, 
            ActiveOption activeOption) {
        try {
            this.toNodeListCodes = toNodeListCodes.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        this.delegate = delegate;
        
        if(activeOption == null) {
            activeOption = ActiveOption.ACTIVE_ONLY;
        }
        
        this.activeOption = activeOption;
    }
    
    @Override
    public ResolvedConceptReferenceList get(int arg0, int arg1) throws LBResourceUnavailableException,
            LBInvocationException, LBParameterException {
        return delegate.get(arg0, arg1);
    }

    @Override
    public ResolvedConceptReferenceList getNext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResolvedConceptReference next() throws LBResourceUnavailableException, LBInvocationException {
        ResolvedConceptReference ref = null;;
        try {
            ref = delegate.next();
        } catch (LBResourceUnavailableException e) {
            //delegate iterator is exhausted
        }
        if(ref != null) {
            toNodeListCodes.remove(new CodeToReturn(ref.getCode(), ref.getCodeNamespace()));
        } else {
            removeInactive();
            
            if(toNodeListCodes.getAllCodes().size() > 0) {
                
                CodeToReturn codeToReturn = toNodeListCodes.getAllCodes().get(0);
                while(toNodeListCodes.getAllCodes().remove(codeToReturn));
               
                ref = this.toResolvedConceptReferenceRunner.toResolvedConceptReference(codeToReturn);
            }
        }
        
        return ref;
    }
    
    @Override
    public ResolvedConceptReferenceList next(int arg0) throws LBResourceUnavailableException, LBInvocationException {
        ResolvedConceptReferenceList list = new ResolvedConceptReferenceList();
        try {
            list = delegate.next(arg0);
        } catch (LBResourceUnavailableException e) {
            //delegate iterator is exhausted
        }
        for(ResolvedConceptReference ref : list.getResolvedConceptReference()) {
            if(ref != null) {
                CodeToReturn codeToReturn = new CodeToReturn(ref.getCode(), ref.getCodeNamespace());
                while(toNodeListCodes.getAllCodes().remove(codeToReturn));
            } 
        }
        
        if(list.getResolvedConceptReferenceCount() < arg0) {
            removeInactive();
            
            int deficit = arg0 - list.getResolvedConceptReferenceCount();
  
            while(deficit > 0 && this.toNodeListCodes.getAllCodes().size() > 0) {
                CodeToReturn codeToReturn = toNodeListCodes.getAllCodes().get(0);
                
                list.addResolvedConceptReference(this.toResolvedConceptReferenceRunner.toResolvedConceptReference(codeToReturn));
                while(toNodeListCodes.getAllCodes().remove(codeToReturn));
            }
        }
        
        return list;
    }

    @Override
    public ResolvedConceptReferencesIterator scroll(int arg0) throws LBResourceUnavailableException,
            LBInvocationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNext() throws LBResourceUnavailableException {
        boolean delegateHasNext;
        try {
            delegateHasNext = delegate.hasNext();
        } catch (LBResourceUnavailableException e) {
            delegateHasNext = false;
        }
        if(! delegateHasNext ) {
           this.removeInactive();
           
           return toNodeListCodes.getAllCodes().size() > 0;
        } else {
            return true;
        }
    }

    @Override
    public int numberRemaining() throws LBResourceUnavailableException {
        return -1;
    }

    @Override
    public void release() throws LBResourceUnavailableException {
        delegate.release();
    }
    
    private void removeInactive() {
        if(! this.haveInactivesBeenRemoved 
                && 
                this.activeOption != null
                &&
                !this.activeOption.equals(ActiveOption.ALL)) {
            
            List<CodeToReturn> activeList = 
                this.removeInactiveRunner .removeInactives(
                        this.toNodeListCodes, 
                        this.activeOption);
            
            this.toNodeListCodes.getAllCodes().clear();
            this.toNodeListCodes.getAllCodes().addAll(activeList);
            
            this.haveInactivesBeenRemoved = true;
        }
    }
    
    @LgProxyClass
    protected static class ToResolvedConceptReferenceRunner implements Serializable {
   
        private static final long serialVersionUID = 1163403069703848281L;

        public ToResolvedConceptReferenceRunner() {
            super();
        }
        
        public ResolvedConceptReference toResolvedConceptReference(CodeToReturn codeToReturn) {
            ResolvedConceptReference returnRef = new ResolvedConceptReference();
            returnRef.setCode(codeToReturn.getCode());
            returnRef.setCodeNamespace(codeToReturn.getNamespace());
            returnRef.setCodingSchemeURI(codeToReturn.getUri());
            returnRef.setCodingSchemeVersion(codeToReturn.getVersion());
            
            String codingSchemeName = null;
            try {
                codingSchemeName = ServiceUtility.getCodingSchemeName(
                        codeToReturn.getUri(), 
                        codeToReturn.getVersion());
            } catch (LBParameterException e) {
                //no-op -- don't assing coding scheme name
                //if we can't find it anywhere.
            }
            
            returnRef.setCodingSchemeName(codingSchemeName);
            
            return returnRef;
        }
        
    }
    
    @LgProxyClass
    protected static class RemoveInactiveRunner implements Serializable {

        private static final long serialVersionUID = 7434331147644851900L;

        public RemoveInactiveRunner(){
            super();
        }
        
        public List<CodeToReturn> removeInactives(CodeHolder codeHolder, ActiveOption activeOption){
            List<CodeToReturn> activeList = new ArrayList<CodeToReturn>();

            for(CodeToReturn codeToReturn : codeHolder.getAllCodes()) {
                String uri = codeToReturn.getUri();
                String version = codeToReturn.getVersion();
                String code = codeToReturn.getCode();
                String namespace = codeToReturn.getNamespace();

                Entity entity = null;
                try {
                    entity = LexEvsServiceLocator.getInstance().
                        getDatabaseServiceManager().
                            getEntityService().getEntity(uri, version, code, namespace, null, null);
                } catch (Exception e) {
                    //
                }

                if(entity != null) {
                    if(entity.getIsActive() == (activeOption.equals(ActiveOption.ACTIVE_ONLY) ? false : true)) {
                        continue;
                    }
                }
                
                activeList.add(codeToReturn);
            }
            
            return activeList;
        } 
    }
}