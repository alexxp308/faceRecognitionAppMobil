package com.rozvi14.facialrecognition.models;

public class FaceRecognition {
    private String percent;
    private String name;

    public FaceRecognition() {
    }

    public FaceRecognition(String percent, String name) {
        this.percent = percent;
        this.name = name;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
