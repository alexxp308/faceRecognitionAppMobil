package com.rozvi14.facialrecognition.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.rozvi14.facialrecognition.utils.GlobalVariables;
import com.rozvi14.facialrecognition.utils.RequestMethods;
import com.rozvi14.facialrecognition.utils.SaveSharedPreference;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("StaticFieldLeak")
public class SendFace extends AsyncTask<String, Integer, String> {
    private String faceInBase64;
    private Context myContext;
    private Gson migson;
    private FaceRecognition myface = null;

    @Override
    public String doInBackground(String... strings) {

        String resultBack = "ERROR";

        GenericResult result = null;
        String url = GlobalVariables.URLSERVER+"api/faceRecognition/";
        String token = SaveSharedPreference.getToken(myContext);
        Map<String,String> miMap = new HashMap<>();
        miMap.put("image",faceInBase64);
        result = RequestMethods.postMethod(url,miMap,token);
        System.out.println(result.getMessage());
        if(result.isSuccess()){
            JsonElement jsonElement = migson.toJsonTree(result.getResultMapping());
            this.myface = migson.fromJson(jsonElement, FaceRecognition.class);
            resultBack = "OK";
        }
        return resultBack;
    }

    public SendFace(String faceInBase64, Context myContext) {
        this.faceInBase64 = faceInBase64;
        this.myContext = myContext;
        migson = new Gson();
    }

    public String getFaceInBase64() {
        return faceInBase64;
    }

    public void setFaceInBase64(String faceInBase64) {
        this.faceInBase64 = faceInBase64;
    }

    public FaceRecognition getMyface() {
        return myface;
    }

    public void setMyface(FaceRecognition myface) {
        this.myface = myface;
    }
}
