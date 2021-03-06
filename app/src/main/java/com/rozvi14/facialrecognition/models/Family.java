package com.rozvi14.facialrecognition.models;

public class Family {
    private Integer id;
    private Integer idClient;
    private String familyName;
    private String relationship;
    private String familyPhotos;

    public Family(Integer id, Integer idClient, String familyName, String relationship, String familyPhotos) {
        this.id = id;
        this.idClient = idClient;
        this.familyName = familyName;
        this.relationship = relationship;
        this.familyPhotos = familyPhotos;
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

    public String getFamilyPhotos() {
        return familyPhotos;
    }

    public void setFamilyPhotos(String familyPhotos) {
        this.familyPhotos = familyPhotos;
    }
}
