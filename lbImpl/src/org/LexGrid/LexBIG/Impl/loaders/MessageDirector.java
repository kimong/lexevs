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
package org.LexGrid.LexBIG.Impl.loaders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Core.LogEntry;
import org.LexGrid.LexBIG.DataModel.Core.types.LogLevel;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.ProcessStatus;
import org.LexGrid.LexBIG.Impl.logging.LgLoggerIF;
import org.LexGrid.LexBIG.Impl.logging.LoggerFactory;
import org.LexGrid.messaging.LgMessageDirectorIF;

/**
 * A message director to redirect messages as necessary for LexBig.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 * @author <A HREF="mailto:erdmann.jesse@mayo.edu">Jesse Erdmann</A>
 * @version subversion $Revision: $ checked in on $Date: $
 */
public class MessageDirector implements LgMessageDirectorIF {
    protected List<LogEntry> messages_ = Collections.synchronizedList(new ArrayList<LogEntry>());
    protected int count_ = 1;
    protected String programName_;
    protected ProcessStatus status_;

    protected LgLoggerIF getLogger() {
        return LoggerFactory.getLogger();
    }

    public MessageDirector(String programName, ProcessStatus status) {
        programName_ = programName;
        status_ = status;
    }

    public void busy() {
        // do nothing method
    }

    public void clearMessages() {
        messages_.clear();
    }

    public LogEntry[] getLogEntries(LogLevel level) {
        ArrayList<LogEntry> result = new ArrayList<LogEntry>();
        for (int i = 0; i < messages_.size(); i++) {
            if (level == null || messages_.get(i).getEntryLevel().getType() == level.getType()) {
                result.add(messages_.get(i));
            }
        }
        return result.toArray(new LogEntry[result.size()]);
    }

    public String error(String message) {
        return error(message, null);
    }

    public String error(String message, Throwable sourceException) {
        LogEntry le = new LogEntry();
        le.setAssociatedURL(null);
        le.setEntryLevel(LogLevel.ERROR);
        le.setEntryTime(new Date(System.currentTimeMillis()));
        le.setMessage(message);
        le.setMessageNumber(count_++);
        le.setProcessUID(Thread.currentThread().getName());
        le.setProgramName(programName_);
        messages_.add(le);
        status_.setMessage(message);
        status_.setErrorsLogged(new Boolean(true));
        getLogger().loadLogError(message);
        return le.getMessageNumber() + "";
    }

    public String fatal(String message) {
        return fatal(message, null);
    }

    public String fatal(String message, Throwable sourceException) {
        LogEntry le = new LogEntry();
        le.setAssociatedURL(null);
        le.setEntryLevel(LogLevel.FATAL);
        le.setEntryTime(new Date(System.currentTimeMillis()));
        le.setMessage(message + " Exception - " + sourceException);
        le.setMessageNumber(count_++);
        le.setProcessUID(Thread.currentThread().getName());
        le.setProgramName(programName_);
        messages_.add(le);
        status_.setMessage(message);
        status_.setErrorsLogged(new Boolean(true));
        getLogger().loadLogError(message, sourceException);
        return le.getMessageNumber() + "";
    }

    public void fatalAndThrowException(String message) throws Exception {
        fatalAndThrowException(message, null);
    }

    public void fatalAndThrowException(String message, Throwable sourceException) throws Exception {
        fatal(message, sourceException);
        throw new Exception(message, sourceException);
    }

    public String info(String message) {
        LogEntry le = new LogEntry();
        le.setAssociatedURL(null);
        le.setEntryLevel(LogLevel.INFO);
        le.setEntryTime(new Date(System.currentTimeMillis()));
        le.setMessage(message);
        le.setMessageNumber(count_++);
        le.setProcessUID(Thread.currentThread().getName());
        le.setProgramName(programName_);
        messages_.add(le);
        status_.setMessage(message);
        getLogger().loadLogDebug(message);
        return le.getMessageNumber() + "";
    }

    public String warn(String message) {
        return warn(message, null);
    }

    public String warn(String message, Throwable sourceException) {
        LogEntry le = new LogEntry();
        le.setAssociatedURL(null);
        le.setEntryLevel(LogLevel.WARN);
        le.setEntryTime(new Date(System.currentTimeMillis()));
        le.setMessage(message + " Exception - " + sourceException);
        le.setMessageNumber(count_++);
        le.setProcessUID(Thread.currentThread().getName());
        le.setProgramName(programName_);
        messages_.add(le);
        status_.setMessage(message);
        status_.setWarningsLogged(new Boolean(true));
        getLogger().loadLogWarn(message, sourceException);
        return le.getMessageNumber() + "";
    }

    public String debug(String message) {
        LogEntry le = new LogEntry();
        le.setAssociatedURL(null);
        le.setEntryLevel(LogLevel.DEBUG);
        le.setEntryTime(new Date(System.currentTimeMillis()));
        le.setMessage(message);
        le.setMessageNumber(count_++);
        le.setProcessUID(Thread.currentThread().getName());
        le.setProgramName(programName_);
        messages_.add(le);
        status_.setMessage(message);
        getLogger().loadLogDebug(message);
        return le.getMessageNumber() + "";
    }
}