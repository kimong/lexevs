package edu.mayo.informatics.lexgrid.convert.directConversions.medDRA;import java.io.FileNotFoundException;import java.io.FileReader;import java.lang.reflect.Field;import java.net.URI;import java.util.Arrays;import java.util.Hashtable;import java.util.List;import org.LexGrid.LexBIG.Utility.logging.LgMessageDirectorIF;import org.LexGrid.codingSchemes.CodingScheme;import org.LexGrid.commonTypes.EntityDescription;import org.LexGrid.commonTypes.Property;import org.LexGrid.commonTypes.Text;import org.LexGrid.commonTypes.types.EntityTypes;import org.LexGrid.concepts.Comment;import org.LexGrid.concepts.Definition;import org.LexGrid.concepts.Entities;import org.LexGrid.concepts.Entity;import org.LexGrid.concepts.Presentation;import org.LexGrid.custom.relations.RelationsUtil;import org.LexGrid.naming.Mappings;import org.LexGrid.naming.SupportedCodingScheme;import org.LexGrid.naming.SupportedContainerName;import org.LexGrid.naming.SupportedHierarchy;import org.LexGrid.naming.SupportedLanguage;import org.LexGrid.naming.SupportedProperty;import org.LexGrid.relations.AssociationPredicate;import org.LexGrid.relations.AssociationSource;import org.LexGrid.relations.AssociationTarget;import org.LexGrid.relations.Relations;import org.lexevs.logging.messaging.impl.CachingMessageDirectorImpl;import au.com.bytecode.opencsv.CSVReader;import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;import au.com.bytecode.opencsv.bean.CsvToBean;import edu.mayo.informatics.lexgrid.convert.directConversions.medDRA.Data.Database;import edu.mayo.informatics.lexgrid.convert.directConversions.medDRA.Data.DatabaseEntityRecord;import edu.mayo.informatics.lexgrid.convert.directConversions.medDRA.Data.DatabaseMapRecord;import edu.mayo.informatics.lexgrid.convert.directConversions.medDRA.Data.DatabaseRecord;import edu.mayo.informatics.lexgrid.convert.directConversions.medDRA.Data.MedDRARecord_Utils;import edu.mayo.informatics.lexgrid.convert.directConversions.medDRA.Data.MedDRARecord_intl_ord;import edu.mayo.informatics.lexgrid.convert.directConversions.medDRA.Data.MedDRARecord_meddra_release;import edu.mayo.informatics.lexgrid.convert.directConversions.medDRA.Data.MedDRARecord_smq_content;import edu.mayo.informatics.lexgrid.convert.directConversions.medDRA.Data.MedDRARecord_soc;import edu.mayo.informatics.lexgrid.convert.directConversions.medDRA.Data.MedDRA_Metadata;public class MedDRAMapToLexGrid {    private static MedDRA_Metadata [] meddraMetaData = MedDRA_Metadata.values();    private LgMessageDirectorIF messages_;    private URI medDRASourceDir;    private Database meddraDatabase;    private Hashtable<String, Entity> idToEntityHash;            public MedDRAMapToLexGrid(URI inFileName, LgMessageDirectorIF lg_messages) {        this.messages_ = new CachingMessageDirectorImpl(lg_messages);        this.medDRASourceDir = inFileName;        meddraDatabase = new Database();        idToEntityHash = new Hashtable<String, Entity>();    }    public void readMedDRAFiles() {        try{            this.createDatabase();        } catch (Exception e) {            messages_.error("Failed to read MedDRA data files, check connection values");            e.printStackTrace();        }    }        public void mapToLexGrid(CodingScheme csclass){        try {            loadCodingScheme(csclass);            loadConcepts(csclass);            loadRelations(csclass);        } catch (Exception e) {            messages_.error("Failed to read MedDRA data files, check connection values");            e.printStackTrace();        }                     messages_.info("Mapping completed, returning to loader");    }    private void loadConcepts(CodingScheme csclass) {        messages_.info("Loading concepts");        String entityCodeNamespace = csclass.getCodingSchemeName();                Entities concepts = csclass.getEntities();        if (concepts == null) {            concepts = new Entities();            csclass.setEntities(concepts);        }                this.insertRootEntityToHashTable(csclass.getCodingSchemeName());                        List<String> tablenames = MedDRA_Metadata.getEntityTables();        for(String table : tablenames){            messages_.info("Loading: " + table);            this.loadEntityTable(table, entityCodeNamespace, csclass);                }                   }    private void insertRootEntityToHashTable(String codingSchemeName) {        String rootCode = MedDRA2LGConstants.DEFAULT_ROOT_NODE;        String rootName = MedDRA2LGConstants.DEFAULT_ROOT_NODE;        Entity rootEntity = new Entity();        rootEntity.setEntityCode(rootCode);        EntityDescription entityDescription = new EntityDescription();        entityDescription.setContent(rootName);                rootEntity.setEntityDescription(entityDescription );        rootEntity.setEntityType(new String[] { EntityTypes.CONCEPT.toString() });        rootEntity.setEntityCodeNamespace(codingSchemeName);        rootEntity.setIsActive(true);        idToEntityHash.put(rootCode, rootEntity);    }    private void loadRelations(CodingScheme csclass) {        messages_.info("Loading all relations properties");        List<String> tablenames = MedDRA_Metadata.getMapTables();        for(String table : tablenames){            messages_.info("Loading: " + table);            this.loadRelationsTable(table, csclass);                }      }    private void loadEntityTable(String table, String entityCodeNamespace, CodingScheme csclass) {        List<DatabaseRecord> records = meddraDatabase.get(table);        Entities entities = csclass.getEntities();        boolean duplicateID = false;                for(DatabaseRecord record : records){            DatabaseEntityRecord meddraRecord = (DatabaseEntityRecord) record;            String code = meddraRecord.getCode();            duplicateID = false;                        if(table.equals(MedDRA_Metadata.LOW_LEVEL_TERMS.tablename())){                Entity entityLoaded = idToEntityHash.get(code);                                if(entityLoaded != null){                    Presentation presentation = MedDRARecord_Utils.createPresentation("T-2", meddraRecord.getName(), "LT", false);                     entityLoaded.addPresentation(presentation);                    duplicateID = true;                }            }                        if(!duplicateID){                Entity newEntity = new Entity();                newEntity.setEntityCode(meddraRecord.getCode());                EntityDescription entityDescription = new EntityDescription();                entityDescription.setContent(meddraRecord.getName());                                newEntity.setEntityDescription(entityDescription );                newEntity.setEntityType(new String[] { EntityTypes.CONCEPT.toString() });                newEntity.setEntityCodeNamespace(entityCodeNamespace);                newEntity.setIsActive(true);                                if(table.equals(MedDRA_Metadata.SYSTEM_ORGAN_CLASSES.tablename())){                    String intlOrder = "-1";                    List<DatabaseRecord> intlOrders = meddraDatabase.get(MedDRA_Metadata.SOC_INTL_MAP.tablename());                    for(DatabaseRecord order : intlOrders){                        if(((MedDRARecord_intl_ord) order).getSoc_code().equals(code)){                            intlOrder = ((MedDRARecord_intl_ord) order).getIntl_ord_code();                        }                    }                    ((MedDRARecord_soc) meddraRecord).setIntlOrder(intlOrder);                }                                                List<Presentation> presentations = meddraRecord.getPresentations();                List<Definition> definitions = meddraRecord.getDefinitions();                List<Comment> comments = meddraRecord.getComments();                List<Property> properties = meddraRecord.getProperties();                                newEntity.setPresentation(presentations);                newEntity.setDefinition(definitions);                newEntity.setComment(comments);                newEntity.setProperty(properties);                                idToEntityHash.put(meddraRecord.getCode(), newEntity);                entities.addEntity(newEntity);            }        }            }    private void loadRelationsTable(String table, CodingScheme csclass) {        String source, target, association;                  List<DatabaseRecord> records = meddraDatabase.get(table);        boolean loadingSMQ = (table.equals(MedDRA_Metadata.SMQ_CONTENT.tablename()) ? true : false);                for(DatabaseRecord record : records){            DatabaseMapRecord meddraRecord = (DatabaseMapRecord) record;            source = meddraRecord.getSource();            target = meddraRecord.getTarget();            if(!source.equals(target)){                association = MedDRA2LGConstants.ASSOCIATION_HAS_SUBTYPE;                AssociationPredicate parent_association = (AssociationPredicate) RelationsUtil                        .resolveAssociationPredicates(csclass, association).get(0);                Entity sourceEntity = idToEntityHash.get(source);                Entity targetEntity = idToEntityHash.get(target);                if(loadingSMQ){                    loadSMQInfo(record, targetEntity);                }                if (sourceEntity != null && targetEntity != null) {                    AssociationSource ai = new AssociationSource();                    ai.setSourceEntityCode(sourceEntity.getEntityCode());                    ai.setSourceEntityCodeNamespace(csclass.getCodingSchemeName());                    ai = RelationsUtil.subsume(parent_association, ai);                                        AssociationTarget at = new AssociationTarget();                    at.setTargetEntityCode(targetEntity.getEntityCode());                    at.setTargetEntityCodeNamespace(csclass.getCodingSchemeName());                    at = RelationsUtil.subsume(ai, at);                }                  }        }    }    private void loadSMQInfo(DatabaseRecord record, Entity targetEntity) {        MedDRARecord_smq_content smqRecord = (MedDRARecord_smq_content) record;        if(targetEntity != null){            targetEntity.addProperty(MedDRARecord_Utils.createProperty("SMQ:term level", smqRecord.getTerm_level()));            targetEntity.addProperty(MedDRARecord_Utils.createProperty("SMQ:term scope", smqRecord.getTerm_scope()));            targetEntity.addProperty(MedDRARecord_Utils.createProperty("SMQ:term category", smqRecord.getTerm_category()));            targetEntity.addProperty(MedDRARecord_Utils.createProperty("SMQ:term weight", smqRecord.getTerm_weight()));            targetEntity.addProperty(MedDRARecord_Utils.createProperty("SMQ:term status", smqRecord.getTerm_status()));            targetEntity.addProperty(MedDRARecord_Utils.createProperty("SMQ:term addition version", smqRecord.getTerm_addition_version()));            targetEntity.addProperty(MedDRARecord_Utils.createProperty("SMQ:term last modified version", smqRecord.getTerm_last_modified_version()));        }    }    private void loadCodingScheme(CodingScheme csclass) {        try {            messages_.info("Loading coding scheme information");                        // Set Coding Scheme Info            csclass.setCodingSchemeName(MedDRA2LGConstants.DEFAULT_NAME);            csclass.setFormalName(MedDRA2LGConstants.DEFAULT_FORMAL_NAME);            csclass.setCodingSchemeURI(MedDRA2LGConstants.DEFAULT_URN);            csclass.setDefaultLanguage(MedDRA2LGConstants.DEFAULT_LANG);            csclass.setRepresentsVersion(this.getVersion());            csclass.getLocalNameAsReference().add(MedDRA2LGConstants.DEFAULT_NAME);                        EntityDescription enDesc = new EntityDescription();            enDesc.setContent(MedDRA2LGConstants.DEFAULT_ENTITY_DESCRIPTION);            csclass.setEntityDescription(enDesc);                        Text txt = new Text();            txt.setContent(MedDRA2LGConstants.DEFAULT_COPYRIGHT);            csclass.setCopyright(txt);                        // Set Coding Scheme Mappings            csclass.setMappings(new Mappings());            SupportedCodingScheme scs = new SupportedCodingScheme();            scs.setLocalId(csclass.getCodingSchemeName());            scs.setUri(csclass.getCodingSchemeURI());            csclass.getMappings().addSupportedCodingScheme(scs);                        // And Language            SupportedLanguage lang = new SupportedLanguage();            lang.setLocalId(csclass.getDefaultLanguage());            csclass.getMappings().addSupportedLanguage(lang);                        // Add Supported Properties            String [] supportedNames = {"P-1", "P-2", "T-1", "T-2", "PSOC",                     "SMQ:term level", "SMQ:term scope", "SMQ:term category",                     "SMQ:term weight", "SMQ:term status", "SMQ:term addition version",                     "SMQ:term last modified version", "Name", "Code", "Level", "Description",                     "Source", "Note", "MedDRA Dictionary Version", "Status", "Algorithm"};                        for(int i=0; i < supportedNames.length; i++){                SupportedProperty property = new SupportedProperty();                property.setLocalId(supportedNames[i]);                csclass.getMappings().addSupportedProperty(property);            }                                    // Collect some global values            String subtype = MedDRA2LGConstants.ASSOCIATION_HAS_SUBTYPE;            String isa = MedDRA2LGConstants.ASSOCIATION_IS_A;            String root = MedDRA2LGConstants.DEFAULT_ROOT_NODE;            String relationsName = MedDRA2LGConstants.CONTAINER_NAME_RELATIONS;                        // Set Supported Hierarchies            SupportedHierarchy hierarchy = this.createSupportedHierarchy(isa, subtype, root, true);            csclass.getMappings().addSupportedHierarchy(hierarchy);                                   // Setup Container for relations            SupportedContainerName scn= new SupportedContainerName();            scn.setLocalId(relationsName);            csclass.getMappings().addSupportedContainerName(scn);                        // Add relation to codingScheme with an AssociationPredicate            Relations relations = new Relations();            relations.setContainerName(relationsName);            AssociationPredicate predicate = new AssociationPredicate();            predicate.setAssociationName(subtype);            relations.addAssociationPredicate(predicate);                        csclass.addRelations(relations);                                                } catch (Exception e) {            messages_.error("Failed while preparing MedDRA Coding Scheme Class", e);            e.printStackTrace();        }     }        private SupportedHierarchy createSupportedHierarchy(String type, String associationName, String rootNode, boolean forwardNavigable){        SupportedHierarchy hierarchy = new SupportedHierarchy();        hierarchy.setLocalId(type);        hierarchy.setAssociationNames(Arrays.asList(associationName));                hierarchy.setRootCode(rootNode);        hierarchy.setIsForwardNavigable(forwardNavigable);                return hierarchy;    }    private String getVersion() {        String version = "UNKNOWN";        List<DatabaseRecord> records = meddraDatabase.get(MedDRA_Metadata.RELEASE.tablename());        if(records.size() >= 1){            version = ((MedDRARecord_meddra_release) records.get(0)).getVersion();        }        return version;    }    public Database createDatabase(){        String input;                for(int i=0; i < meddraMetaData.length; i++){            input = medDRASourceDir.getPath() + meddraMetaData[i].filename();            try {                              FileReader fileReader = new FileReader(input);                CSVReader reader = new CSVReader(fileReader, '$');                ColumnPositionMappingStrategy<DatabaseRecord> strat = new ColumnPositionMappingStrategy<DatabaseRecord>();                strat.setType(meddraMetaData[i].classname());                String[] columns = getFields(meddraMetaData[i].classname());                             strat.setColumnMapping(columns);                    CsvToBean<DatabaseRecord> csv = new CsvToBean<DatabaseRecord>();                List<DatabaseRecord> list = csv.parse(strat, reader);                meddraDatabase.add(meddraMetaData[i].tablename(), list);            } catch (FileNotFoundException e) {                e.printStackTrace();            }        }                return meddraDatabase;    }    private String[] getFields(Class<?> class1) {        Field [] fields = class1.getDeclaredFields();           String [] fieldnames = new String[fields.length - 1];                for(int i=1; i < fields.length; i++){            fieldnames[i-1] = fields[i].getName();        }        return fieldnames;    }}