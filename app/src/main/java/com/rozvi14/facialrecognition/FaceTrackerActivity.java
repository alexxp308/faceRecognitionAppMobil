package com.rozvi14.facialrecognition;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rozvi14.facialrecognition.utils.GlobalVariables;
import com.rozvi14.facialrecognition.camera.CameraSourcePreview;
import com.rozvi14.facialrecognition.camera.GraphicOverlay;
import com.rozvi14.facialrecognition.models.EmotionFace;
import com.rozvi14.facialrecognition.models.FaceRecognition;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class FaceTrackerActivity extends AppCompatActivity {
    private static final String TAG = "FaceTracker";

    //private static final String URL_INI = "http://52.67.0.104";//"https://desolate-eyrie-24315.herokuapp.com";
    //private static final String PORT = "8000";
    //private static final String URL = URL_INI+":"+PORT+"/";
    private CameraSource mCameraSource = null;

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private Button btn_switch_cam;

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private String emotion="";
    private Hashtable<Integer, String> emocionesFace = new Hashtable<Integer, String>();
    private List<EmotionFace> listFaces = new ArrayList<>();
    private String NAME_MODEL = "facial_expression_model_weights";
    private int id_camera = CameraSource.CAMERA_FACING_FRONT;

    private Toast toast;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main_tracker);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        btn_switch_cam = (Button) findViewById(R.id.btn_switch_camera);
        // VERIFICAR SI HAY PERMISO DE ACCESO A CAMARA
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        //SI TIENE PERMISO INICIAR CAMARA

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            id_camera = extras.getInt("id_camera");
        }

        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(id_camera);
        } else {
            requestCameraPermission();
        }

        btn_switch_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
        if(listFaces.size()>0){
            EnviarDatos enviarDatos = new EnviarDatos();
            enviarDatos.setOperacion(2);
            //enviarDatos.execute();
            String result = enviarDatos.doInBackground();
        }
    }



    private void switchCamera(){
        int id_camera_send = -1;
        switch (id_camera){
            case CameraSource.CAMERA_FACING_FRONT:
                id_camera_send = CameraSource.CAMERA_FACING_BACK;
                break;
            case CameraSource.CAMERA_FACING_BACK:
                id_camera_send = CameraSource.CAMERA_FACING_FRONT;
                break;

        }
        Intent intent = new Intent(FaceTrackerActivity.this, FaceTrackerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id_camera", id_camera_send);
        intent.putExtras(bundle);
        FaceTrackerActivity.this.finish();
        startActivity(intent);

    }

    private void createCameraSource(int id_camera) {
        Log.w(TAG, "INICIO DE CAMARA");
        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        //Propio detector
        MyFaceDetector myFaceDetector = new MyFaceDetector(detector);

        myFaceDetector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!myFaceDetector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");
            return;
        }



        mCameraSource = new CameraSource.Builder(context, myFaceDetector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(id_camera)
                .setRequestedFps(10.0f)
                .build();

    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {

        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            this.mOverlay = overlay;
            this.mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face,emocionesFace);
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }

    class MyFaceDetector extends Detector<Face>{
        private Detector<Face> mDelegate;

        MyFaceDetector(Detector<Face> delegate){
            mDelegate=delegate;
        }

        @Override
        public SparseArray<Face> detect(Frame frame) {
            //AGREGAR
            Matrix matrix = new Matrix();
            Bitmap faceBitMap=null;
            Bitmap myBitMap=null;
            int angle=0;
            SparseArray<Face> faces = mDelegate.detect(frame);

            //FALTA LA MODIFICACION CUANDO ESTE HORIZONTAL
            if(faces.size()>0){
                matrix.postRotate(90 * frame.getMetadata().getRotation());
                YuvImage yuvImage = new YuvImage(frame.getGrayscaleImageData().array(), ImageFormat.NV21, frame.getMetadata().getWidth(),frame.getMetadata().getHeight(),null);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(new Rect(0,0,frame.getMetadata().getWidth(),frame.getMetadata().getHeight()),100,byteArrayOutputStream);
                byte[] jpegArray = byteArrayOutputStream.toByteArray();

                Bitmap bitmap_temp = BitmapFactory.decodeByteArray(jpegArray,0,jpegArray.length);
                myBitMap = Bitmap.createBitmap(bitmap_temp,0,0,bitmap_temp.getWidth(),bitmap_temp.getHeight(),matrix,true);

                if(myBitMap!=null){
                    for(int i=0; i<faces.size(); i++) {
                        Face thisFace = faces.valueAt(i);
                        /*float kx = 0.8f;
                        float ky = 1.0f;
                        int xm = (int) (thisFace.getPosition().x + thisFace.getWidth()/2);
                        int ym = (int) (thisFace.getPosition().y + thisFace.getHeight()/2);
                        int x1 = xm - (int) (kx * thisFace.getWidth()/2);
                        int y1 = ym - (int) (ky * thisFace.getHeight()/4);*/

                    /*
                    int x = (int) thisFace.getPosition().x;
                    int y = (int) thisFace.getPosition().y;
                    int width = (int) thisFace.getWidth();
                    int height = (int) thisFace.getHeight();
                    */
                        /*int kwidth = 0;
                        int kheight = 0;
                        if(x1+kx*thisFace.getWidth()>myBitMap.getWidth()){
                            kwidth = myBitMap.getWidth()-x1;
                        }else{
                            kwidth = (int) (kx*thisFace.getWidth());
                        }
                        if(y1+ky*thisFace.getHeight()>myBitMap.getHeight()){
                            kheight = myBitMap.getHeight()-y1;
                        }else{
                            kheight = (int) (ky*thisFace.getHeight()*3/4);
                        }*/

                        //faceBitMap = Bitmap.createBitmap(myBitMap, x1, y1,kwidth,kheight);

                        EnviarDatos enviarDatos = new EnviarDatos();
                        enviarDatos.setMyBitmap(myBitMap);
                        enviarDatos.setIdFace(thisFace.getId());
                        enviarDatos.setOperacion(1);
                        //enviarDatos.execute();
                        String result = enviarDatos.doInBackground();
                        FaceRecognition myface = enviarDatos.getMyface();
                        //String emotion_result = enviarDatos.getEmotion();
                        emocionesFace.put(thisFace.getId(),myface.getName()+": "+myface.getPercent());

                    }
                }else{
                    Log.w("EMOTION","FRAME VACIO");
                }
            }


            return faces;
        }

        public boolean isOperational(){
            return mDelegate.isOperational();
        }

        public boolean setFocus(int id){
            return mDelegate.setFocus(id);
        }
    }


    private FaceRecognition postData(Bitmap myBitmap, int idFace, List<EmotionFace> list) throws IOException {

        FaceRecognition myface = new FaceRecognition("0","");

        String emotion = "";
        String name_file = "";
        int cod_emotion = -1;
        int model = -1;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(GlobalVariables.URLSERVER+"api/faceRecognition/");
        Log.i("ENVIARDATOS",GlobalVariables.URLSERVER+"api/faceRecognition/");
        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            byte[] data = bos.toByteArray();
            ByteArrayEntity bae = new ByteArrayEntity(data);
            httppost.setEntity(bae);
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            String sResponse;
            String respuesta = "";

            StringBuilder s = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));
            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
                respuesta = respuesta + sResponse;
            }
            JSONObject json = new JSONObject(respuesta);

            //emotion = "happy";//json.getString("emotion");
            //cod_emotion = 1;//json.getInt("cod_emotion");
            //name_file = "prueba";//json.getString("name_file");
            //model = 1;//json.getInt("model");
            //list.add(new EmotionFace(idFace, cod_emotion, emotion, name_file, model));

            myface.setName(json.getString("name"));
            DecimalFormat f = new DecimalFormat("##.00");
            myface.setPercent(f.format(json.getDouble("percent")));

            return myface;

        } catch (ClientProtocolException e){ //| JSONException e) {
            Log.i(">>ERROR: ",e.getMessage());
            //if(e.getMessage()!=null) Log.e(e.getClass().getName(), e.getMessage());
        } catch (JSONException e) {
            Log.i(">>ERROR JSON: ",e.getMessage());
        }
        return myface;
    }

    private boolean enviarDataFaces(List<EmotionFace> list){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(GlobalVariables.URLSERVER +"emotionrecognition/save_data_bacth");

        if(list.size()>0){
            JsonObject request = new JsonObject();
            Gson gson = new GsonBuilder().create();
            JsonArray arrayEmotionFaces = gson.toJsonTree(list).getAsJsonArray();
            request.add("data", arrayEmotionFaces);
            Log.w("JSON", arrayEmotionFaces.toString());
            try{
                StringEntity entity = new StringEntity(request.getAsString());
                httppost.setEntity(entity);
                httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/json");

                HttpResponse response = httpclient.execute(httppost);
                String sResponse;
                String respuesta = "";
                StringBuilder s = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent(), "UTF-8"));
                while ((sResponse = reader.readLine()) != null) {
                    s = s.append(sResponse);
                    respuesta = respuesta + sResponse;
                }
                JSONObject json = new JSONObject(respuesta);
                return json.getBoolean("status");
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    public class EnviarDatos extends AsyncTask<String, Integer, String> {
        private Bitmap myBitmap;
        private String emotion="";
        private FaceRecognition myface = new FaceRecognition("0","");
        private int idFace = -1;
        private int operacion = -1;
        private List<EmotionFace> list;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            //mostrarMensaje("msg: aquiii");
            try {
                switch (this.operacion){
                    case 1:
                        this.myface = postData(this.myBitmap, this.idFace,this.list);
                        Log.w("ENVIARDATOS","Emotion: "+this.emotion);
                        //Log.w("ENVIARDATOS",String.valueOf(this.list.size()));
                        break;
                    case 2:
                        enviarDataFaces(this.list);
                        break;
                    default:
                        break;
                }
            }catch (Exception e){
                Log.i(TAG,"ERROR: "+e.getMessage());
                this.emotion="";
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        public Bitmap getMyBitmap() {
            return myBitmap;
        }

        public void setMyBitmap(Bitmap myBitmap) {
            this.myBitmap = myBitmap;
        }

        public String getEmotion() {
            return emotion;
        }

        public void setEmotion(String emotion) {
            this.emotion = emotion;
        }

        public int getIdFace() {
            return idFace;
        }

        public void setIdFace(int idFace) {
            this.idFace = idFace;
        }

        public int getOperacion() {
            return operacion;
        }

        public void setOperacion(int operacion) {
            this.operacion = operacion;
        }

        private void mostrarMensaje(String msg){
            toast = Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }

        public FaceRecognition getMyface() {
            return myface;
        }

        public void setMyface(FaceRecognition myface) {
            this.myface = myface;
        }
    }



}
