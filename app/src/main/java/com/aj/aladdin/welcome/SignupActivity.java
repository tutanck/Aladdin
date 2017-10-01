package com.aj.aladdin.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.aj.aladdin.R;
import com.aj.aladdin.main.MainActivity;
import com.aj.aladdin.tools.oths.utils.__;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private RelativeLayout progressBarLayout;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBarLayout = (RelativeLayout) findViewById(R.id.progressBarLayout);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!validateForm(email, password)) return;

                progressBarLayout.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBarLayout.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    __.showShortToast(SignupActivity.this, getString(R.string.singup_auth_failed));
                                    Log.d("FirebaseAuth", "" + task.getException());//// TODO: 01/10/2017 check what exc and swow the right msg error
                                } else
                                    MainActivity.start(SignupActivity.this);
                            }
                        });

            }
        });
    }


    private boolean validateForm(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Email obligatoire!");
            return false;
        } else if (!email.contains("@")) {
            inputEmail.setError("Email invalide!");
            return false;
        } else
            inputEmail.setError(null);

        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Mot de passe obligatoire!");
            return false;
        } else if (password.length() < 6) {
            inputPassword.setError(getString(R.string.minimum_password));
            return false;
        } else
            inputPassword.setError(null);

        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBarLayout.setVisibility(View.GONE);
    }
}