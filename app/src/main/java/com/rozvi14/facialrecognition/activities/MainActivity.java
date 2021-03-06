package com.rozvi14.facialrecognition.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rozvi14.facialrecognition.FaceTrackerActivity;
import com.rozvi14.facialrecognition.R;
import com.rozvi14.facialrecognition.models.GenericResult;
import com.rozvi14.facialrecognition.utils.GlobalVariables;
import com.rozvi14.facialrecognition.utils.RequestMethods;
import com.rozvi14.facialrecognition.utils.SaveSharedPreference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";

    //------------------------------------------repeat-------------------------------------------
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navView;
    //------------------------------------------repeat-------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //------------------------------------------repeat-------------------------------------------
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =  new NotificationChannel("my-channel","my-channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager =  getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("my-event")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Successful";
                        if (!task.isSuccessful()) {
                            msg = "failed";
                        }
                        Log.d(TAG, msg);

                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void accion_boton(View v){
        Intent intent = new Intent(this, FaceTrackerActivity.class);
        startActivity(intent);
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
