package com.x64technology.linex.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.x64technology.linex.MainActivity;
import com.x64technology.linex.databinding.ActivityConfirmAccountBinding;
import com.x64technology.linex.services.AuthManager;

public class ConfirmAccount extends AppCompatActivity {

    ActivityConfirmAccountBinding binding;
    LinearProgressIndicator progressBar;
    AuthManager authManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authManager = new AuthManager(this);

        progressBar = new LinearProgressIndicator(this);
        progressBar.setIndeterminate(true);
        progressBar.setTrackThickness(2);

        String username = getIntent().getStringExtra("username");


        binding.btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.confConstraint.addView(progressBar, 0);
                binding.btnVerify.setEnabled(false);
                binding.btnResendOtp.setEnabled(false);

                String otp = binding.inpOtp.getEditableText().toString();
                authManager.confirmUser(username, otp, new GenericHandler() {
                    @Override
                    public void onSuccess() {
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        binding.confConstraint.removeView(progressBar);
                        binding.btnVerify.setEnabled(true);
                        binding.btnResendOtp.setEnabled(true);
                        binding.layoutOtp.setError(exception.getLocalizedMessage());
                    }
                });
            }
        });

        binding.btnResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}