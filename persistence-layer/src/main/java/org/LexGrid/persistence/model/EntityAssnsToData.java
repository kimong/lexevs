package org.LexGrid.persistence.model;
// Generated Jul 30, 2009 7:07:52 AM by Hibernate Tools 3.2.0.CR1



/**
 * EntityAssnsToData generated by hbm2java
 */
public class EntityAssnsToData  implements java.io.Serializable {


     private EntityAssnsToDataId id;
     private String multiAttributesKey;
     private String associationInstanceId;
     private Boolean isDefining;
     private Boolean isInferred;
     private Boolean isActive;
     private Long entryStateId;
     private String dataValue;

    public EntityAssnsToData() {
    }

	
    public EntityAssnsToData(EntityAssnsToDataId id, String dataValue) {
        this.id = id;
        this.dataValue = dataValue;
    }
    public EntityAssnsToData(EntityAssnsToDataId id, String multiAttributesKey, String associationInstanceId, Boolean isDefining, Boolean isInferred, Boolean isActive, Long entryStateId, String dataValue) {
       this.id = id;
       this.multiAttributesKey = multiAttributesKey;
       this.associationInstanceId = associationInstanceId;
       this.isDefining = isDefining;
       this.isInferred = isInferred;
       this.isActive = isActive;
       this.entryStateId = entryStateId;
       this.dataValue = dataValue;
    }
   
    public EntityAssnsToDataId getId() {
        return this.id;
    }
    
    public void setId(EntityAssnsToDataId id) {
        this.id = id;
    }
    public String getMultiAttributesKey() {
        return this.multiAttributesKey;
    }
    
    public void setMultiAttributesKey(String multiAttributesKey) {
        this.multiAttributesKey = multiAttributesKey;
    }
    public String getAssociationInstanceId() {
        return this.associationInstanceId;
    }
    
    public void setAssociationInstanceId(String associationInstanceId) {
        this.associationInstanceId = associationInstanceId;
    }
    public Boolean getIsDefining() {
        return this.isDefining;
    }
    
    public void setIsDefining(Boolean isDefining) {
        this.isDefining = isDefining;
    }
    public Boolean getIsInferred() {
        return this.isInferred;
    }
    
    public void setIsInferred(Boolean isInferred) {
        this.isInferred = isInferred;
    }
    public Boolean getIsActive() {
        return this.isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    public Long getEntryStateId() {
        return this.entryStateId;
    }
    
    public void setEntryStateId(Long entryStateId) {
        this.entryStateId = entryStateId;
    }
    public String getDataValue() {
        return this.dataValue;
    }
    
    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }




}


