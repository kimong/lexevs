package org.lexgrid.loader.rrf.model;
// Generated Feb 24, 2009 8:40:05 PM by Hibernate Tools 3.2.2.GA



/**
 * MrsatId generated by hbm2java
 */
public class Mrsat  implements java.io.Serializable {


     private String cui;
     private String lui;
     private String sui;
     private String metaui;
     private String stype;
     private String code;
     private String atui;
     private String satui;
     private String atn;
     private String sab;
     private String atv;
     private String suppress;
     private String cvf;

    public Mrsat() {
    }

    public Mrsat(String cui, String lui, String sui, String metaui, String stype, String code, String atui, String satui, String atn, String sab, String atv, String suppress, String cvf) {
       this.cui = cui;
       this.lui = lui;
       this.sui = sui;
       this.metaui = metaui;
       this.stype = stype;
       this.code = code;
       this.atui = atui;
       this.satui = satui;
       this.atn = atn;
       this.sab = sab;
       this.atv = atv;
       this.suppress = suppress;
       this.cvf = cvf;
    }
   
    public String getCui() {
        return this.cui;
    }
    
    public void setCui(String cui) {
        this.cui = cui;
    }
    public String getLui() {
        return this.lui;
    }
    
    public void setLui(String lui) {
        this.lui = lui;
    }
    public String getSui() {
        return this.sui;
    }
    
    public void setSui(String sui) {
        this.sui = sui;
    }
    public String getMetaui() {
        return this.metaui;
    }
    
    public void setMetaui(String metaui) {
        this.metaui = metaui;
    }
    public String getStype() {
        return this.stype;
    }
    
    public void setStype(String stype) {
        this.stype = stype;
    }
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    public String getAtui() {
        return this.atui;
    }
    
    public void setAtui(String atui) {
        this.atui = atui;
    }
    public String getSatui() {
        return this.satui;
    }
    
    public void setSatui(String satui) {
        this.satui = satui;
    }
    public String getAtn() {
        return this.atn;
    }
    
    public void setAtn(String atn) {
        this.atn = atn;
    }
    public String getSab() {
        return this.sab;
    }
    
    public void setSab(String sab) {
        this.sab = sab;
    }
    public String getAtv() {
        return this.atv;
    }
    
    public void setAtv(String atv) {
        this.atv = atv;
    }
    public String getSuppress() {
        return this.suppress;
    }
    
    public void setSuppress(String suppress) {
        this.suppress = suppress;
    }
    public String getCvf() {
        return this.cvf;
    }
    
    public void setCvf(String cvf) {
        this.cvf = cvf;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof Mrsat) ) return false;
		 Mrsat castOther = ( Mrsat ) other; 
         
		 return ( (this.getCui()==castOther.getCui()) || ( this.getCui()!=null && castOther.getCui()!=null && this.getCui().equals(castOther.getCui()) ) )
 && ( (this.getLui()==castOther.getLui()) || ( this.getLui()!=null && castOther.getLui()!=null && this.getLui().equals(castOther.getLui()) ) )
 && ( (this.getSui()==castOther.getSui()) || ( this.getSui()!=null && castOther.getSui()!=null && this.getSui().equals(castOther.getSui()) ) )
 && ( (this.getMetaui()==castOther.getMetaui()) || ( this.getMetaui()!=null && castOther.getMetaui()!=null && this.getMetaui().equals(castOther.getMetaui()) ) )
 && ( (this.getStype()==castOther.getStype()) || ( this.getStype()!=null && castOther.getStype()!=null && this.getStype().equals(castOther.getStype()) ) )
 && ( (this.getCode()==castOther.getCode()) || ( this.getCode()!=null && castOther.getCode()!=null && this.getCode().equals(castOther.getCode()) ) )
 && ( (this.getAtui()==castOther.getAtui()) || ( this.getAtui()!=null && castOther.getAtui()!=null && this.getAtui().equals(castOther.getAtui()) ) )
 && ( (this.getSatui()==castOther.getSatui()) || ( this.getSatui()!=null && castOther.getSatui()!=null && this.getSatui().equals(castOther.getSatui()) ) )
 && ( (this.getAtn()==castOther.getAtn()) || ( this.getAtn()!=null && castOther.getAtn()!=null && this.getAtn().equals(castOther.getAtn()) ) )
 && ( (this.getSab()==castOther.getSab()) || ( this.getSab()!=null && castOther.getSab()!=null && this.getSab().equals(castOther.getSab()) ) )
 && ( (this.getAtv()==castOther.getAtv()) || ( this.getAtv()!=null && castOther.getAtv()!=null && this.getAtv().equals(castOther.getAtv()) ) )
 && ( (this.getSuppress()==castOther.getSuppress()) || ( this.getSuppress()!=null && castOther.getSuppress()!=null && this.getSuppress().equals(castOther.getSuppress()) ) )
 && ( (this.getCvf()==castOther.getCvf()) || ( this.getCvf()!=null && castOther.getCvf()!=null && this.getCvf().equals(castOther.getCvf()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + ( getCui() == null ? 0 : this.getCui().hashCode() );
         result = 37 * result + ( getLui() == null ? 0 : this.getLui().hashCode() );
         result = 37 * result + ( getSui() == null ? 0 : this.getSui().hashCode() );
         result = 37 * result + ( getMetaui() == null ? 0 : this.getMetaui().hashCode() );
         result = 37 * result + ( getStype() == null ? 0 : this.getStype().hashCode() );
         result = 37 * result + ( getCode() == null ? 0 : this.getCode().hashCode() );
         result = 37 * result + ( getAtui() == null ? 0 : this.getAtui().hashCode() );
         result = 37 * result + ( getSatui() == null ? 0 : this.getSatui().hashCode() );
         result = 37 * result + ( getAtn() == null ? 0 : this.getAtn().hashCode() );
         result = 37 * result + ( getSab() == null ? 0 : this.getSab().hashCode() );
         result = 37 * result + ( getAtv() == null ? 0 : this.getAtv().hashCode() );
         result = 37 * result + ( getSuppress() == null ? 0 : this.getSuppress().hashCode() );
         result = 37 * result + ( getCvf() == null ? 0 : this.getCvf().hashCode() );
         return result;
   }   


}


