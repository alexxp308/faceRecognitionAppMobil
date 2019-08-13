/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rozvi14.facialrecognition;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.Face;
import com.rozvi14.facialrecognition.camera.GraphicOverlay;
import com.rozvi14.facialrecognition.models.EmotionFace;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.MAGENTA,
        Color.RED,
        Color.WHITE,
        Color.YELLOW,
            Color.GRAY
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;

    private Hashtable<Integer, String> emocionesFace = new Hashtable<Integer, String>();


    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);
        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = Color.rgb(85,179,88);//COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face,Hashtable<Integer, String> emocionesFace) {
        mFace = face;
        this.emocionesFace = emocionesFace;
        postInvalidate();
    }

    void getColorCanvas(String information){
        information = information.replaceAll("\\s","");
        String[] values = information.split(":");
        float f = Float.valueOf(values[1].trim()).floatValue();
        if(values[0].equals("unknown")){
            mBoxPaint.setColor(COLOR_CHOICES[7]);
            mIdPaint.setColor(COLOR_CHOICES[7]);
        }else{
            if(f>=60){
                mBoxPaint.setColor(COLOR_CHOICES[2]);
                mIdPaint.setColor(COLOR_CHOICES[2]);
            }else if(f<60 && f>=40){
                mBoxPaint.setColor(COLOR_CHOICES[6]);
                mIdPaint.setColor(COLOR_CHOICES[6]);
            }else if(f<40){
                mBoxPaint.setColor(COLOR_CHOICES[4]);
                mIdPaint.setColor(COLOR_CHOICES[4]);
            }
        }
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */

    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }
        /*
        EnviarDatos mainTest = new EnviarDatos();
        //mainTest.data = bytes;
        mainTest.myBitmap=loadedImage;
        mainTest.execute();
        emotion = mainTest.emotion;*/
        // Draws a circle at the position of the detected face, with the face's track id below.

        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        String information = emocionesFace.get(face.getId());
        Log.w("facegraphic",information);
        //canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);

        //canvas.drawText("Detectando", x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
        //canvas.drawText("EMOCION: " + emocionesFace.get(face.getId()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
        //canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
        //canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET*2, y - ID_Y_OFFSET*2, mIdPaint);

        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        /*
        Log.w("CANVAS","W3:"+ canvas.getWidth());
        Log.w("CANVAS","H3:"+ canvas.getHeight());
        Log.w("CANVAS","left:"+ (int) left );
        Log.w("CANVAS","top:"+ (int) top );
        Log.w("CANVAS","right:"+ (int) right );
        Log.w("CANVAS","bottom:"+ (int) bottom );
        */
        getColorCanvas(information);
        canvas.drawText(information, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
        canvas.drawRect(left, top, right, bottom, mBoxPaint);
        //canvas.drawRect(scaleX(face.getPosition().x), scaleX(face.getPosition().y), scaleX(face.getPosition().x+face.getWidth()), scaleX(face.getPosition().y+face.getHeight()), mBoxPaint);
    }

}
