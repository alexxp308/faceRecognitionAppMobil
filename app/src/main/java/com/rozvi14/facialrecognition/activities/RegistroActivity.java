package com.rozvi14.facialrecognition.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rozvi14.facialrecognition.R;
import com.rozvi14.facialrecognition.models.Family;
import com.rozvi14.facialrecognition.models.GenericResult;
import com.rozvi14.facialrecognition.models.Record;
import com.rozvi14.facialrecognition.utils.GlobalVariables;
import com.rozvi14.facialrecognition.utils.RequestMethods;
import com.rozvi14.facialrecognition.utils.SaveSharedPreference;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import static com.rozvi14.facialrecognition.utils.GlobalVariables.STATICFOLDER;

public class RegistroActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "RegistroActivity";

    //------------------------------------------repeat-------------------------------------------
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navView;
    //------------------------------------------repeat-------------------------------------------

    List<Record> recordList = null;
    Integer idRecord = null;

    private ConstraintLayout constraintList = null;
    private ConstraintLayout constraintDetalleRegistro = null;


    private LinearLayout miLayout = null;
    private Button btnBack = null;
    private TextView txtfecha = null;
    private TextView nombreFamiliar = null;
    private TextView relacionFamiliar = null;
    private TextView porcentaje = null;
    private ImageView imgRegistro = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //------------------------------------------repeat-------------------------------------------
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        //------------------------------------------repeat-------------------------------------------

        constraintList = findViewById(R.id.listConocido);
        constraintDetalleRegistro = findViewById(R.id.showRegistro);

        miLayout = findViewById(R.id.LinearLayoutRegistro);

        btnBack = findViewById(R.id.btnRegresar);
        txtfecha = findViewById(R.id.dateRecord);
        nombreFamiliar = findViewById(R.id.familyName);
        relacionFamiliar = findViewById(R.id.relationShip);
        porcentaje = findViewById(R.id.porcentaje);
        imgRegistro = findViewById(R.id.imgRecord);

        btnBack.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               constraintList.setVisibility(View.VISIBLE);
               constraintDetalleRegistro.setVisibility(View.INVISIBLE);
           }
       });

        recordList = loadRecord();
        Log.d(TAG, "recordlist: "+recordList.size());
        if(recordList!=null){
            createCardsRecord();
        }

    }

    public GenericResult getRecord(){
        GenericResult result = null;
        String url = GlobalVariables.URLSERVER+"record/listRecord/";
        String token = SaveSharedPreference.getToken(getApplicationContext());
        result = RequestMethods.getMethod(url, token);
        return result;
    }

    public List<Record> loadRecord(){
        GenericResult result = getRecord();
        if(result.isSuccess()){
            Type listType = new TypeToken<ArrayList<Record>>(){}.getType();
            Map<String, Object> resultMapping = result.getResultMapping();
            return new Gson().fromJson(new Gson().toJson(resultMapping.get("recordList")), listType);
        }
        Log.e(TAG, "======= Error load record");
        return null;
    }

    public void createCardsRecord(){
        if(miLayout.getChildCount()>0){
            miLayout.removeAllViews();
        }

        CardView cv = null;
        TextView tv = null;
        SpannableString spanString = null;
        LinearLayout.LayoutParams ll = null;
        for(Record myRecord : recordList){
            cv = new CardView(this);//revisar this
            cv.setBackgroundColor(Color.rgb(7,153,139));
            ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,150 );
            ll.setMargins(40,0,40,40);
            cv.setLayoutParams(ll);

            tv = new TextView(this);
            tv.setText(myRecord.getId()+"");
            tv.setVisibility(View.INVISIBLE);
            cv.addView(tv);

            tv = new TextView(this);
            ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,65 );
            tv.setLayoutParams(ll);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,28);
            tv.setTextColor(Color.WHITE);
            tv.setText(myRecord.getDateRecord().replace("T"," ").replace("Z",""));

            cv.addView(tv);

            tv = new TextView(this);
            ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,65 );
            ll.setMargins(0,80,0,0);
            tv.setLayoutParams(ll);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
            tv.setTextColor(Color.WHITE);
            spanString = new SpannableString(myRecord.getFamilyName());
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
            tv.setText(spanString);

            cv.addView(tv);

            miLayout.addView(cv);

            cv.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //constraintConocido.setVisibility(View.INVISIBLE);
                    CardView micv = (CardView) view;
                    TextView mitv = (TextView) micv.getChildAt(0);
                    idRecord = Integer.parseInt(mitv.getText().toString());

                    Record recordSelected = null;
                    for (Record record: recordList){
                        if(record.getId() == idRecord){
                            recordSelected = record;
                        }
                    }

                    txtfecha.setText(recordSelected.getDateRecord().replace("T"," ").replace("Z",""));
                    nombreFamiliar.setText(recordSelected.getFamilyName());
                    relacionFamiliar.setText(recordSelected.getRelationship());
                    porcentaje.setText(recordSelected.getPercent());
                    nombreFamiliar.setText(recordSelected.getFamilyName());

                    String[] proof = recordSelected.getRecordPhotoPath().split("/");
                    int leng = proof.length;
                    String showme = proof[leng-3] +"/"+proof[leng-2]+"/"+proof[leng-1];

                    Picasso.get().load(STATICFOLDER+showme).into(imgRegistro);

                    constraintList.setVisibility(View.INVISIBLE);
                    constraintDetalleRegistro.setVisibility(View.VISIBLE);
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
            case R.id.nav_item_registro:
                goRecords();
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

    public void goRecords(){
        if(TAG != "RegistroActivity"){
            Intent intent = new Intent(getApplicationContext(), RegistroActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
    }
    //------------------------------------------repeat-------------------------------------------
}
