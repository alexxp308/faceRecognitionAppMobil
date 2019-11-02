package com.rozvi14.facialrecognition.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rozvi14.facialrecognition.models.GenericResult;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RequestMethods {
    private static final String TAG = "RequestMethods";
    private static HttpClient httpclient = new DefaultHttpClient();
    private static Gson gson = new GsonBuilder().create();

    public static GenericResult getMethod(String url, String token){
        GenericResult result = new GenericResult(false,0,"",null);
        HttpGet request = new HttpGet(url);
        Log.d(TAG, "request(GET): "+url);
        if(token!=null){
            request.addHeader("Authorization", "Token "+token);
            Log.d(TAG, "Authorization: "+ "Token "+token);
        }
        try{
            HttpResponse response = httpclient.execute(request);
            Log.d(TAG, "Response Code: "+response.getStatusLine().getStatusCode());
            String sResponse;
            StringBuffer strBuffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));
            while ((sResponse = reader.readLine()) != null) {
                strBuffer.append(sResponse);
            }
            result = gson.fromJson(strBuffer.toString(),GenericResult.class);
        }catch (Exception e){
            Log.e(TAG, "error>>"+e.getMessage());
        }
        return result;
    }

    public static GenericResult postMethod(String url,Object object){
        GenericResult result = new GenericResult(false,0,"",null);
        HttpPost request = new HttpPost(url);
        Log.d(TAG, "request(POST): "+url);
        try{
            String json = gson.toJson(object);
            StringEntity entity = new StringEntity(json);
            request.setEntity(entity);
            request.addHeader("Accept", "application/json");
            request.addHeader("Content-type", "application/json");

            HttpResponse response = httpclient.execute(request);
            Log.d(TAG, "Response Code: "+response.getStatusLine().getStatusCode());
            String sResponse;
            StringBuffer strBuffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));
            while ((sResponse = reader.readLine()) != null) {
                strBuffer.append(sResponse);
            }
            result = gson.fromJson(strBuffer.toString(),GenericResult.class);
        }catch (Exception e){
            Log.e(TAG, "error>>"+e.getMessage());
        }
        return result;
    }

    public static String makeUrl(Class myclass,Object object){
        String result="";
        Method[] methods = myclass.getDeclaredMethods();
        List<String> fieldValue = new ArrayList<>();
        String nameMethod = "";
        Method method;
        for(int i=0;i<methods.length;i++){
            //solo tomara los metodos que empiezen con get
            if(!methods[i].getReturnType().toString().equals("void") && methods[i].getName().indexOf("get")>-1){
                try{
                    //getReturnType para determinar si es primitivo o String sino hacer otra cosa
                    nameMethod = methods[i].getName().substring(3).toLowerCase();
                    method = myclass.getMethod(methods[i].getName());
                    fieldValue.add(nameMethod+"="+encodeValue(method.invoke(object).toString()));
                }catch (Exception e){
                    Log.e(TAG, "error>>"+e.getMessage());
                }
            }
        }
        result = joinSet(fieldValue,"&");
        return result;
    }

    private static String encodeValue(String value) {
        String result = "";
        try {
            result = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            Log.e(TAG, "error>>"+ex.getMessage());
        }
        return result;
    }

    private static String joinSet(List<String> list, String sep){
        String result = "";
        for(String fv:list){
            result+=fv+sep;
        }
        result = (result.length()>0) ? result.substring(0,result.length()-1) : "";
        return result;
    }
}
