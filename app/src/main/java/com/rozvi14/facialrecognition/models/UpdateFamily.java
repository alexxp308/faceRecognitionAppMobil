package com.rozvi14.facialrecognition.models;

public class UpdateFamily {
    private Integer idFamily;
    private String familyName;
    private String relationship;

    public UpdateFamily(Integer idFamily, String familyName, String relationship) {
        this.idFamily = idFamily;
        this.familyName = familyName;
        this.relationship = relationship;
    }

    public Integer getIdFamily() {
        return idFamily;
    }

    public void setIdFamily(Integer idFamily) {
        this.idFamily = idFamily;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}
