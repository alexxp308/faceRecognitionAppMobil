package com.rozvi14.facialrecognition.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rozvi14.facialrecognition.R;
import com.rozvi14.facialrecognition.utils.GlobalVariables;
import com.rozvi14.facialrecognition.utils.RequestMethods;
import com.rozvi14.facialrecognition.models.GenericResult;
import com.rozvi14.facialrecognition.models.Login;
import com.rozvi14.facialrecognition.utils.SaveSharedPreference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    Button btn_login;
    EditText userName,password;
    ConstraintLayout loginForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
            StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        loginForm = findViewById(R.id.loginForm);

        if(SaveSharedPreference.getLoggedStatus(getApplicationContext())) {
            //revisar si tu token no ha expirado
            //para entrar debes estar conectado a internet, si no tienes conexion no puedes hacer nada
            //pero renovar token revisar: https://stackoverflow.com/questions/14567586/token-authentication-for-restful-api-should-the-token-be-periodically-changed
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            loginForm.setVisibility(View.VISIBLE);
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login(){
        Log.d(TAG, "Login");
        btn_login.setEnabled(false);
        if(!validate()){
            onLoginFailed();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        String textUserName = userName.getText().toString();
        String textPassword = password.getText().toString();

        GenericResult result = validateServer(textUserName,textPassword);
        if(!result.isSuccess()){
            onLoginFailed();
            progressDialog.dismiss();
            return;
        }
        String token = (String) result.getResultMapping().get("token");
        String idClient = (String) result.getResultMapping().get("idClient");
        //save values session
        saveValuesSession(token,textUserName, idClient);
        //continue next view
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        progressDialog.dismiss();
    }

    private boolean validate(){
        boolean result = true;
        String textUserName = userName.getText().toString();
        String textPassword = password.getText().toString();

        if(textUserName.isEmpty()){
            userName.setError("Enter your userName");
            result = false;
            return result;
        }

        if(textPassword.isEmpty()){
            password.setError("Enter your password");
            result = false;
            return result;
        }

        return result;
    }

    private GenericResult validateServer(String textUserName, String textPassword){
        GenericResult result = null;
        Login login = new Login(textUserName,textPassword);
        String url = GlobalVariables.URLSERVER+"api/login/";
        result = RequestMethods.postMethod(url,login);
        return result;
    }

    private void onLoginFailed(){
        Toast.makeText(getBaseContext(),"Login failed",Toast.LENGTH_LONG).show();
        btn_login.setEnabled(true);
    }

    private void saveValuesSession(String token,String userName, String idClient){
        SaveSharedPreference.setLoggedIn(getApplicationContext(), true);
        SaveSharedPreference.setToken(getApplicationContext(), token);
        SaveSharedPreference.setUserName(getApplicationContext(), userName);
        SaveSharedPreference.setIdClient(getApplicationContext(), idClient);
    }
}
