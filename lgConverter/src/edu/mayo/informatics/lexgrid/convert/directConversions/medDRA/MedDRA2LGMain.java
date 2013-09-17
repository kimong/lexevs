package edu.mayo.informatics.lexgrid.convert.directConversions.medDRA;import java.net.URI;import java.util.List;import org.LexGrid.LexBIG.Utility.logging.LgMessageDirectorIF;import org.LexGrid.codingSchemes.CodingScheme;import org.lexevs.logging.messaging.impl.CachingMessageDirectorImpl;public class MedDRA2LGMain {    private LgMessageDirectorIF messages;    public CodingScheme map(URI cuiUri, URI sourceDir, LgMessageDirectorIF lg_messages)            throws Exception {        this.messages = new CachingMessageDirectorImpl(lg_messages);        if(sourceDir == null){            messages.fatalAndThrowException("Error! Input file path string is null.");        }                if(!this.validateSourceDir(sourceDir)){            throw new Exception();        }                CodingScheme csclass = null;                try{            csclass = new CodingScheme();            MedDRAMapToLexGrid medDRAMap = new MedDRAMapToLexGrid(sourceDir,cuiUri, messages);            medDRAMap.readMedDRAFiles();            if(medDRAMap.validateMedDRABeans()){                medDRAMap.mapToLexGrid(csclass);                this.messages.info("Processing DONE!!");            }        } catch (Exception e) {            messages.fatalAndThrowException("Failed to load MedDRA Content from: " + sourceDir.getPath() + "", e);        }                    return csclass;    }    private boolean validateSourceDir(URI sourceDir) {        if(!MedDRAFormatValidator.isValidDirectory(sourceDir)){            return false;        }                String msg = "";                List<String> filesInvalid = MedDRAFormatValidator.allMedDRAfilesExist(sourceDir);        if(filesInvalid.size() > 0){            msg = createFileErrorMessage(filesInvalid, "MISSING");            messages.error(msg);            return false;        }                filesInvalid = MedDRAFormatValidator.allMedDRAfilesNotEmpty(sourceDir);        if(filesInvalid.size() > 0){            msg = createFileErrorMessage(filesInvalid, "EMPTY");            messages.error(msg);            return false;        }                return true;    }    private String createFileErrorMessage(List<String> files, String issue){        String msg = "Following files are " + issue + " in directory provided: \n";        for(int i=0; i < files.size(); i++){            msg += files.get(i) + "\n";        }        return msg;    }        public static void main(String args[]) {    }        }