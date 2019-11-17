package com.rozvi14.facialrecognition.models;

public class Record {
    private Integer id;
    private Integer idClient;
    private String dateRecord;
    private String familyName;
    private String relationship;
    private String percent;
    private String recordPhotoPath;

    public Record(Integer id, Integer idClient, String dateRecord, String familyName, String relationship, String percent, String recordPhotoPath) {
        this.id = id;
        this.idClient = idClient;
        this.dateRecord = dateRecord;
        this.familyName = familyName;
        this.relationship = relationship;
        this.percent = percent;
        this.recordPhotoPath = recordPhotoPath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdClient() {
        return idClient;
    }

    public void setIdClient(Integer idClient) {
        this.idClient = idClient;
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

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getRecordPhotoPath() {
        return recordPhotoPath;
    }

    public void setRecordPhotoPath(String recordPhotoPath) {
        this.recordPhotoPath = recordPhotoPath;
    }

    public String getDateRecord() {
        return dateRecord;
    }

    public void setDateRecord(String dateRecord) {
        this.dateRecord = dateRecord;
    }
}
