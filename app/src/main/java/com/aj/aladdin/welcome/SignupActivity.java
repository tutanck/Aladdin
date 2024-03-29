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

import com.aj.aladdin.R;
import com.aj.aladdin.main.A;
import com.aj.aladdin.main.MainActivity;
import com.aj.aladdin.tools.components.fragments.ProgressBarFragment;
import com.aj.aladdin.tools.utils.KeyboardServices;
import com.aj.aladdin.tools.utils.PatternsHolder;
import com.aj.aladdin.tools.utils.__;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputUsername;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBarFragment progressBarFragment;
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
        inputUsername = (EditText) findViewById(R.id.username);

        progressBarFragment = (ProgressBarFragment) getSupportFragmentManager().findFragmentById(R.id.waiter_modal_fragment);

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
                LoginActivity.start(SignupActivity.this);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String username = inputUsername.getText().toString().trim();

                if (!validateForm(email, password, username)) return;

                KeyboardServices.dismiss(SignupActivity.this, inputPassword);
                progressBarFragment.show();
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    progressBarFragment.hide();
                                    __.showShortToast(SignupActivity.this, getString(R.string.singup_auth_failed));
                                    Log.d("FirebaseAuth", "" + task.getException());//// TODO: 01/10/2017 check what exc and swow the right msg error
                                } else MainActivity.start(SignupActivity.this, username);
                            }
                        });
            }
        });
    }


    private boolean validateForm(String email, String password, String username) {
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

        if (TextUtils.isEmpty(username)) {
            inputUsername.setError("Nom d'utilisateur obligatoire!");
            return false;
        } else if (username.length() < 3) {
            inputUsername.setError(getString(R.string.minimum_username));
            return false;
        } else if (!PatternsHolder.isValidUsername(username)) {
            inputUsername.setError(getString(R.string.invalid_username));
            return false;
        } else
            inputUsername.setError(null);

        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBarFragment.hide();
    }


}