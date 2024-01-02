package com.x64technology.linex.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.x64technology.linex.MainActivity;
import com.x64technology.linex.databinding.ActivityAuthBinding;
import com.x64technology.linex.services.AuthManager;

public class Auth extends AppCompatActivity {
    ActivityAuthBinding activityAuthBinding;
    LinearProgressIndicator progressBar;
    AuthManager authManager;
    TextView errorMsg;
    Intent intent;
    boolean login = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAuthBinding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(activityAuthBinding.getRoot());

        progressBar = new LinearProgressIndicator(this);
        progressBar.setIndeterminate(true);
        progressBar.setTrackThickness(2);

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

            activityAuthBinding.authConstraint.addView(progressBar, 0);
            activityAuthBinding.btnContinue.setEnabled(false);
            makeCall(name, email, username, password);
        });
    }

    private void makeCall(String name, String email, String username, String password) {
        if (login)
            authManager.signIn(username, password, new AuthenticationHandler() {
                @Override
                public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                    activityAuthBinding.authConstraint.removeView(progressBar);
                    activityAuthBinding.btnContinue.setEnabled(true);

                    intent = new Intent(Auth.this, MainActivity.class);
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
                    activityAuthBinding.authConstraint.removeView(progressBar);
                    activityAuthBinding.btnContinue.setEnabled(true);

                    errorMsg = new TextView(Auth.this);
                    errorMsg.setPadding(16, 16, 16, 0);
                    errorMsg.setText(exception.getMessage());
                    activityAuthBinding.authConstraint.addView(errorMsg);
                }
            });
        else {
            authManager.signUp(name, email, "test image", username, password, new SignUpHandler() {
                @Override
                public void onSuccess(CognitoUser user, SignUpResult signUpResult) {
                    activityAuthBinding.authConstraint.removeView(progressBar);
                    activityAuthBinding.btnContinue.setEnabled(true);

                    if (signUpResult.isUserConfirmed()) {
                        intent = new Intent(Auth.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        intent = new Intent(Auth.this, ConfirmAccount.class);
                        intent.putExtra("username", username);

                        launcher.launch(intent);
                    }
                }

                @Override
                public void onFailure(Exception exception) {
                    activityAuthBinding.authConstraint.removeView(progressBar);
                    activityAuthBinding.btnContinue.setEnabled(true);

                    errorMsg = new TextView(Auth.this);
                    errorMsg.setPadding(16, 16, 16, 0);
                    errorMsg.setText(exception.getLocalizedMessage());
                    activityAuthBinding.authConstraint.addView(errorMsg);
                }
            });
        }
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    intent = new Intent(Auth.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
    );
}