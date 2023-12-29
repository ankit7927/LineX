package com.x64technology.linex.screens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult;
import com.x64technology.linex.MainActivity;
import com.x64technology.linex.databinding.ActivityAuthBinding;
import com.x64technology.linex.services.AuthManager;
import com.x64technology.linex.services.UserPreference;

public class Auth extends AppCompatActivity {
    ActivityAuthBinding activityAuthBinding;
    ProgressDialog progressDialog;
    UserPreference userPreference;
    AuthManager authManager;
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
        activityAuthBinding.emailLayout.setVisibility(login ? View.GONE : View.VISIBLE);

        authManager = new AuthManager(this);

        setCallbacks();
    }


    private void setCallbacks() {
        activityAuthBinding.btnAuthChange.setOnClickListener(view -> {
            login = !login;
            activityAuthBinding.heading.setText(login? "Welcome Back" : "Hello There");
            activityAuthBinding.subheading.setText(login? "nice to see you again" : "thanks for choosing us.");
            activityAuthBinding.btnAuthChange.setText(login? "signup" : "login");

            activityAuthBinding.namelayout.setVisibility(login ? View.GONE : View.VISIBLE);
            activityAuthBinding.emailLayout.setVisibility(login ? View.GONE : View.VISIBLE);
        });

        activityAuthBinding.btnContinue.setOnClickListener(view -> {
            String email = activityAuthBinding.inpEmail.getEditableText().toString();
            String username = activityAuthBinding.inpUsername.getEditableText().toString();
            String name = activityAuthBinding.inpName.getEditableText().toString();
            String password = activityAuthBinding.inpPassword.getEditableText().toString();

            makeCall(name, email, username, password);

        });
    }

    private void makeCall(String name, String email, String username, String password) {
        progressDialog.show();

        if (login)
            authManager.signIn(username, password, new AuthenticationHandler() {
                @Override
                public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                    Intent intent = new Intent(Auth.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                    authenticationContinuation.continueTask();
                }

                @Override
                public void getMFACode(MultiFactorAuthenticationContinuation continuation) {

                }

                @Override
                public void authenticationChallenge(ChallengeContinuation continuation) {

                }

                @Override
                public void onFailure(Exception exception) {

                }
            });
        else {
            authManager.signUp(name, email, "test image", username, password, new SignUpHandler() {
                @Override
                public void onSuccess(CognitoUser user, SignUpResult signUpResult) {
                    Intent intent;
                    if (signUpResult.isUserConfirmed()) {
                        intent = new Intent(Auth.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        intent = new Intent(Auth.this, ConfirmAccount.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Exception exception) {
                    // TODO handle
                }
            });
        }
        progressDialog.dismiss();
    }
}