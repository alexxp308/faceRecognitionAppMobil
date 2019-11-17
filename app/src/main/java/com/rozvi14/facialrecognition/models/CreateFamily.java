package com.rozvi14.facialrecognition.models;

import java.util.List;

public class CreateFamily {
    private String familyName;
    private String relationship;
    private List<String> images;

    public CreateFamily(String familyName, String relationship, List<String> images) {
        this.familyName = familyName;
        this.relationship = relationship;
        this.images = images;
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
