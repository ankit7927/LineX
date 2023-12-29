package com.x64technology.linex.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.x64technology.linex.MainActivity;
import com.x64technology.linex.databinding.ActivityConfirmAccountBinding;
import com.x64technology.linex.services.AuthManager;

public class ConfirmAccount extends AppCompatActivity {

    ActivityConfirmAccountBinding binding;
    AuthManager authManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authManager = new AuthManager(this);

        String username = getIntent().getStringExtra("username");


        binding.btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = binding.inpOtp.getEditableText().toString();
                authManager.confirmUser(username, otp, new GenericHandler() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(ConfirmAccount.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Toast.makeText(ConfirmAccount.this, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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