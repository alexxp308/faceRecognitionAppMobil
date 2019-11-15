package com.rozvi14.facialrecognition.activities;

import android.content.ClipData;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.rozvi14.facialrecognition.R;
import com.rozvi14.facialrecognition.models.Family;
import com.rozvi14.facialrecognition.models.GenericResult;
import com.rozvi14.facialrecognition.utils.GlobalVariables;
import com.rozvi14.facialrecognition.utils.RequestMethods;
import com.rozvi14.facialrecognition.utils.SaveSharedPreference;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

public class ConocidoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "ConocidoActivity";

    //------------------------------------------repeat-------------------------------------------
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navView;
    //------------------------------------------repeat-------------------------------------------

    private LinearLayout miLayout = null;
    private ConstraintLayout constraintConocido = null;
    private ConstraintLayout contraintCreate = null;
    private LinearLayout linearPhotos = null;
    private FloatingActionButton fab;
    List<Family> familyList = null;
    List<String> arrayBitsPhotos = null;
    private final int CODE_MULTIPLE_IMG_GALERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //------------------------------------------repeat-------------------------------------------
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conocido);

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this,drawer,toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        //------------------------------------------repeat-------------------------------------------

        miLayout = findViewById(R.id.LinearLayoutConocido);

        constraintConocido = findViewById(R.id.listConocido);

        linearPhotos = findViewById(R.id.myLinearPhotos);

        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"clic en floating");
                constraintConocido.setVisibility(View.INVISIBLE);
                contraintCreate.setVisibility(View.VISIBLE);

                if(linearPhotos.getChildCount()>0){
                    linearPhotos.removeAllViews();
                }

                arrayBitsPhotos = new ArrayList<>();

                //1 MULTIPLE IMGS
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Seleccionar varias imagenes"),
                        CODE_MULTIPLE_IMG_GALERY);

            }
        });

        familyList = loadFamily();

        if(familyList!=null){
            createCardsFamily();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == CODE_MULTIPLE_IMG_GALERY && resultCode == RESULT_OK){
            ClipData clipData = data.getClipData();
            LinearLayout linearPhotos = findViewById(R.id.myLinearPhotos);
            LinearLayout ln = null;
            ImageView iv = null;
            if(clipData != null){
                for(int i=0; i< clipData.getItemCount(); i++){
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();

                    if(i%2==0){
                        ln = new LinearLayout(this);
                        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                        ln.setLayoutParams(ll);
                        ln.setOrientation(LinearLayout.HORIZONTAL);
                        ln.setPadding(0,0,0,20);
                        linearPhotos.addView(ln);
                    }

                    iv = new ImageView(this);
                    LinearLayout.LayoutParams liv = new LinearLayout.LayoutParams(94, 200,1.0f);
                    iv.setLayoutParams(liv);
                    iv.setImageURI(uri);
                    ln.addView(iv);

                    try {
                        InputStream imageStream = getContentResolver().openInputStream(uri);
                        Bitmap myBitmap = BitmapFactory.decodeStream(imageStream);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        Log.d(TAG, "array as string: "+Arrays.toString(imageBytes));
                        arrayBitsPhotos.add(Arrays.toString(imageBytes));
                    } catch (FileNotFoundException e) {
                        Log.d(TAG,">>ERROR image to bytes");
                    }
                }
            }
        }
    }


    public GenericResult getFamily(){
        GenericResult result = null;
        String idClient = SaveSharedPreference.getIdClient(getApplicationContext());
        String url = GlobalVariables.URLSERVER+"family/list/?idClient="+idClient;
        String token = SaveSharedPreference.getToken(getApplicationContext());
        result = RequestMethods.getMethod(url, token);
        return result;
    }

    public List<Family> loadFamily(){
        GenericResult result = getFamily();
        if(result.isSuccess()){
            Type listType = new TypeToken<ArrayList<Family>>(){}.getType();
            Map<String, Object> resultMapping = result.getResultMapping();
            return new Gson().fromJson(new Gson().toJson(resultMapping.get("familyList")), listType);
        }
        Log.e(TAG, "======= Error load family");
        return null;
    }

    public void createCardsFamily(){
        CardView cv = null;
        TextView tv = null;
        LinearLayout.LayoutParams ll = null;
        for(Family myFamily : familyList){
            cv = new CardView(this);//revisar this
            ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,60 );
            ll.setMargins(40,0,40,40);
            cv.setLayoutParams(ll);

            tv = new TextView(this);
            ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT );
            tv.setLayoutParams(ll);
            tv.setGravity(Gravity.CENTER);
            tv.setText(myFamily.getNombreConocido());

            cv.addView(tv);
            miLayout.addView(cv);

            cv.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //constraintConocido.setVisibility(View.INVISIBLE);

                }
            });
        }
    }

    //------------------------------------------repeat-------------------------------------------
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //aqui no entra revisar el ejemplo de panel para entender mas
        //aqui entra el main y en el main creo q se le agrega las opciones
        switch (item.getItemId()){
            case R.id.nav_item_logout:
                //logout();
                Log.d(TAG, "logout1!!!!!!!!!!");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_item_logout:
                logout();
                //Log.d(TAG, "logout2!!!!!!!!!!");
                return true;
            case R.id.nav_item_conocido:
                goConocidos();
                return true;
            case R.id.nav_item_main:
                goMain();
                return true;
            default:
                return true;
        }
    }

    private GenericResult logoutServer(){
        //usar en pycharm esto: https://stackoverflow.com/questions/30739352/django-rest-framework-token-authentication-logout
        GenericResult result = null;
        String url = GlobalVariables.URLSERVER+"api/logout/";
        String token = SaveSharedPreference.getToken(getApplicationContext());
        result = RequestMethods.getMethod(url, token);
        return result;
    }

    private void logout(){
        GenericResult result = logoutServer();
        if(result.isSuccess()){
            //limpiar session
            SaveSharedPreference.setLoggedIn(getApplicationContext(), false);
            SaveSharedPreference.setToken(getApplicationContext(), "");
            SaveSharedPreference.setUserName(getApplicationContext(), "");
            //limpiar session
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        Log.e("TAG", "======= fail logout");
        return;
    }

    public void goConocidos(){
        if(TAG != "ConocidoActivity"){
            Intent intent = new Intent(getApplicationContext(), ConocidoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
    }

    public void goMain(){
        if(TAG != "MainActivity"){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
    }
    //------------------------------------------repeat-------------------------------------------
}
