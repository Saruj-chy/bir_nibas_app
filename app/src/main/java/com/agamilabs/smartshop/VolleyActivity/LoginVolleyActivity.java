package com.agamilabs.smartshop.VolleyActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;

public class LoginVolleyActivity extends AppCompatActivity {


    EditText mUserNameET, mPasswordET;
    String mUsername, mPassword;

    //=========   sharedprefarence
    SharedPreferences sharedPreferences;
    static String SHARED_PREFS = "admin_store";
    String USERNO;
    String usernoState = "userno";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_volley);

        sharedPreferences = getApplicationContext().getSharedPreferences("login_info", Context.MODE_PRIVATE);

    }

    public void onSubmitClick(View view) {


        String login_username = ((EditText) findViewById(R.id.login_username)).getText().toString();
        String login_password = ((EditText) findViewById(R.id.login_password)).getText().toString();

        HashMap<String, String> map = new HashMap<>();
        map.put("username", login_username);
        map.put("password", login_password);

        AppController.getAppController().getAppNetworkController().makeRequest(Config.Verify_App_User, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppController.getAppController().getInAppNotifier().log("login response", response);

                try {
                    JSONObject jo = new JSONObject(response);
                    boolean error = jo.getBoolean("error");

                    if (error) {
                        AppController.getAppController().getInAppNotifier().showToast(jo.getString("message"));
                        return;
                    }


                    jo = jo.getJSONObject("data");

                    SharedPreferences s = getApplicationContext().getSharedPreferences("login_info", Context.MODE_PRIVATE);
                    SharedPreferences.Editor se = s.edit();
                    se.putInt("userno", jo.getInt("userno"));
                    se.putString("username", jo.getString("username"));
                    se.putString("firstname", jo.getString("firstname"));
                    se.putString("lastname", jo.getString("lastname"));
                    se.apply();

                    Intent intent = new Intent(LoginVolleyActivity.this, ProjectListActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    AppController.getAppController().getInAppNotifier().showToast(e.toString());

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppController.getAppController().getInAppNotifier().showToast("Error: " + error.toString());
            }
        }, map);


    }


}