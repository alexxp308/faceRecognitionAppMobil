package com.rozvi14.facialrecognition.activities;

import android.content.ClipData;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.rozvi14.facialrecognition.R;
import com.rozvi14.facialrecognition.models.CreateFamily;
import com.rozvi14.facialrecognition.models.Family;
import com.rozvi14.facialrecognition.models.GenericResult;
import com.rozvi14.facialrecognition.models.Login;
import com.rozvi14.facialrecognition.models.UpdateFamily;
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
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.w3c.dom.Text;

import static com.rozvi14.facialrecognition.utils.GlobalVariables.STATICFOLDER;

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

    private Button botonSubirFotos = null;
    private Button botonGuardar = null;
    private EditText textNombre = null;
    private EditText textRelacion = null;
    List<String> arrayBitsPhotos = null;

    private TextView textoCU = null;
    private Integer idFamily = 0;
    private Boolean isCreate = true;

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
        contraintCreate = findViewById(R.id.createEditConocido);

        linearPhotos = findViewById(R.id.myLinearPhotos);

        fab = findViewById(R.id.floatingActionButton);
        botonSubirFotos = findViewById(R.id.button);
        botonGuardar = findViewById(R.id.button4);
        textNombre = findViewById(R.id.editText2);
        textRelacion = findViewById(R.id.editText3);
        textoCU = findViewById(R.id.textView13);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textoCU.setText("Crear Familiar");
                botonSubirFotos.setText("Subir Fotos");
                botonGuardar.setText("Guardar");
                idFamily = 0;

                constraintConocido.setVisibility(View.INVISIBLE);
                contraintCreate.setVisibility(View.VISIBLE);
                if(linearPhotos.getChildCount()>0){
                    linearPhotos.removeAllViews();
                }
                arrayBitsPhotos = new ArrayList<>();
                isCreate = true;
            }
        });

        botonSubirFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCreate){
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
                }else{
                    contraintCreate.setVisibility(View.INVISIBLE);
                    constraintConocido.setVisibility(View.VISIBLE);
                }
            }
        });

        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String familyName = textNombre.getText().toString();
                String relationship = textRelacion.getText().toString();

                if(isCreate){
                    if(arrayBitsPhotos.size()<5){
                        Toast.makeText(getBaseContext(),"Debes subir mas de 5 fotos",Toast.LENGTH_LONG).show();
                        return;
                    }


                    CreateFamily miFamily = new CreateFamily(familyName,relationship,arrayBitsPhotos);
                    GenericResult result = sendInformationServer(miFamily);

                    if(!result.isSuccess()){
                        Toast.makeText(getBaseContext(),"Error en subir información",Toast.LENGTH_LONG).show();
                        return;
                    }
                }else{
                    UpdateFamily udtFamily = new UpdateFamily(idFamily,familyName,relationship);
                    GenericResult result = UpdateFamilyServer(udtFamily);
                    if(!result.isSuccess()){
                        Toast.makeText(getBaseContext(),"Error en actualizar información",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                familyList = loadFamily();
                if(familyList!=null){
                    createCardsFamily();
                }

                contraintCreate.setVisibility(View.INVISIBLE);
                constraintConocido.setVisibility(View.VISIBLE);
            }
        });

        familyList = loadFamily();

        if(familyList!=null){
            createCardsFamily();
        }
    }

    private GenericResult sendInformationServer(CreateFamily miFamily){
        GenericResult result = null;
        String url = GlobalVariables.URLSERVER+"family/createFamily/";
        result = RequestMethods.postMethod(url, miFamily, SaveSharedPreference.getToken(getApplicationContext()));
        return result;
    }

    private GenericResult UpdateFamilyServer(UpdateFamily miFamily){
        GenericResult result = null;
        String url = GlobalVariables.URLSERVER+"family/updateFamily/";
        result = RequestMethods.postMethod(url, miFamily, SaveSharedPreference.getToken(getApplicationContext()));
        return result;
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

                        byte[] bytes;
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        ByteArrayOutputStream output = new ByteArrayOutputStream();
                        try {
                            while ((bytesRead = imageStream.read(buffer)) != -1) {
                                output.write(buffer, 0, bytesRead);
                            }
                        } catch (IOException e) {
                            Log.d(TAG,">>ERROR:" + e.getMessage());
                        }
                        bytes = output.toByteArray();
                        String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
                        arrayBitsPhotos.add(encodedString);
                    } catch (FileNotFoundException e) {
                        Log.d(TAG,">>ERROR image to bytes");
                    }
                }
                Log.d(TAG,">>length array: "+arrayBitsPhotos.size());
            }
        }
    }


    public GenericResult getFamily(){
        GenericResult result = null;
        String url = GlobalVariables.URLSERVER+"family/listFamily/";
        String token = SaveSharedPreference.getToken(getApplicationContext());
        result = RequestMethods.getMethod(url, token);
        return result;
    }

    public List<Family> loadFamily(){
        GenericResult result = getFamily();
        if(result.isSuccess()){
            Type listType = new TypeToken<ArrayList<Family>>(){}.getType();
            Map<String, Object> resultMapping = result.getResultMapping();
            //Log.d(TAG,">>ERROR image to bytes");
            return new Gson().fromJson(new Gson().toJson(resultMapping.get("familyList")), listType);
        }
        Log.e(TAG, "======= Error load family");
        return null;
    }

    public void createCardsFamily(){
        if(miLayout.getChildCount()>0){
            miLayout.removeAllViews();
        }

        CardView cv = null;
        TextView tv = null;
        SpannableString spanString = null;
        LinearLayout.LayoutParams ll = null;
        for(Family myFamily : familyList){
            cv = new CardView(this);//revisar this
            cv.setBackgroundColor(Color.rgb(7,153,139));
            ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,150 );
            ll.setMargins(40,0,40,40);
            cv.setLayoutParams(ll);

            tv = new TextView(this);
            tv.setText(myFamily.getId()+"");
            tv.setVisibility(View.INVISIBLE);
            cv.addView(tv);

            tv = new TextView(this);
            ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,65 );
            tv.setLayoutParams(ll);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,28);
            tv.setTextColor(Color.WHITE);
            tv.setText(myFamily.getFamilyName());

            cv.addView(tv);

            tv = new TextView(this);
            ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,65 );
            ll.setMargins(0,80,0,0);
            tv.setLayoutParams(ll);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
            tv.setTextColor(Color.WHITE);
            spanString = new SpannableString(myFamily.getRelationship());
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
                    idFamily = Integer.parseInt(mitv.getText().toString());
                    isCreate = false;

                    textoCU.setText("Actualizar Familiar");
                    botonSubirFotos.setText("Cancelar");
                    botonGuardar.setText("Actualizar");
                    constraintConocido.setVisibility(View.INVISIBLE);
                    contraintCreate.setVisibility(View.VISIBLE);
                    if(linearPhotos.getChildCount()>0){
                        linearPhotos.removeAllViews();
                    }
                    arrayBitsPhotos = new ArrayList<>();
                    Family familySelected = null;
                    for (Family family: familyList){
                        if(family.getId() == idFamily){
                            familySelected = family;
                        }
                    }
                    textNombre.setText(familySelected.getFamilyName());
                    textRelacion.setText(familySelected.getRelationship());
                    String[] arrayString = familySelected.getFamilyPhotos().split(",");

                    LinearLayout ln = null;
                    ImageView iv = null;
                    for(int i=0; i< arrayString.length; i++) {

                        if (i % 2 == 0) {
                            ln = new LinearLayout(ConocidoActivity.this);
                            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                            ln.setLayoutParams(ll);
                            ln.setOrientation(LinearLayout.HORIZONTAL);
                            ln.setPadding(0, 0, 0, 20);
                            linearPhotos.addView(ln);
                        }

                        iv = new ImageView(ConocidoActivity.this);
                        LinearLayout.LayoutParams liv = new LinearLayout.LayoutParams(94, 200, 1.0f);
                        iv.setLayoutParams(liv);

                        String[] proof = arrayString[i].split("/");
                        int leng = proof.length;
                        String showme = proof[leng-3] +"/"+proof[leng-2]+"/"+proof[leng-1];

                        Picasso.get().load(STATICFOLDER+showme).into(iv);
                        ln.addView(iv);
                    }
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
