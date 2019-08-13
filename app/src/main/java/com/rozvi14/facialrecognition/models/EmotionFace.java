package com.rozvi14.facialrecognition.models;

import java.util.Date;

public class EmotionFace {
    private int idFace;
    private int codEmotion;
    private String emotion;
    private String name_img;
    private int model;

    public EmotionFace(int idFace, int codEmotion, String emotion, String name_img, int model) {
        this.idFace = idFace;
        this.codEmotion = codEmotion;
        this.emotion = emotion;
        this.name_img = name_img;
        this.model = model;
    }

    public int getIdFace() {
        return idFace;
    }

    public void setIdFace(int idFace) {
        this.idFace = idFace;
    }

    public int getCodEmotion() {
        return codEmotion;
    }

    public void setCodEmotion(int codEmotion) {
        this.codEmotion = codEmotion;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public String getName_img() {
        return name_img;
    }

    public void setName_img(String name_img) {
        this.name_img = name_img;
    }
}
