package com.x64technology.linex.screens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.x64technology.linex.MainActivity;
import com.x64technology.linex.databinding.ActivityAuthBinding;
import com.x64technology.linex.services.UserPreference;
import com.x64technology.linex.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class Auth extends AppCompatActivity {
    ActivityAuthBinding activityAuthBinding;
    ProgressDialog progressDialog;
    UserPreference userPreference;
    boolean login = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAuthBinding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(activityAuthBinding.getRoot());

        userPreference = new UserPreference(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait");
        progressDialog.setTitle("authenticating");

        activityAuthBinding.namelayout.setVisibility(login ? View.GONE : View.VISIBLE);

        setCallbacks();
    }


    private void setCallbacks() {
        activityAuthBinding.btnAuthChange.setOnClickListener(view -> {
            login = !login;
            activityAuthBinding.heading.setText(login? "Welcome Back" : "Hello There");
            activityAuthBinding.subheading.setText(login? "nice to see you again" : "thanks for choosing us.");
            activityAuthBinding.btnAuthChange.setText(login? "signup" : "login");

            activityAuthBinding.namelayout.setVisibility(login ? View.GONE : View.VISIBLE);
        });

        activityAuthBinding.btnContinue.setOnClickListener(view -> {
            String email = activityAuthBinding.inpEmail.getEditableText().toString();
            String name = activityAuthBinding.inpName.getEditableText().toString();
            String password = activityAuthBinding.inpPassword.getEditableText().toString();

            makeCall(name, email, password);

        });
    }

    private void makeCall(String name, String email, String password) {
        progressDialog.show();
        String url = login ? Constants.SIGNIN_URL : Constants.SIGNUP_URL;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            if (!login) jsonObject.put("name", name);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try {
                        userPreference.saveUserData(
                                response.getString("email"),
                                response.getString("id"),
                                response.getString("name"),
                                "some dp link");
                        userPreference.saveToken(response.getString("token"));

                        progressDialog.dismiss();
                        startActivity(new Intent(Auth.this, MainActivity.class));
                        finish();
                    } catch (JSONException e) {
                        Toast.makeText(Auth.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    progressDialog.dismiss();
                    activityAuthBinding.inpEmail.setText("");
                    String err = new String(error.networkResponse.data);
                    if (error.networkResponse.statusCode == 404)
                        activityAuthBinding.emailLayout.setError(err);
                    else if (error.networkResponse.statusCode == 403)
                        activityAuthBinding.pwordLayout.setError(err);
                    else Toast.makeText(Auth.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

}