package com.isep.simov.todo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.isep.simov.todo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.sign_in_email)
    EditText inputEmail;
    @BindView(R.id.sign_in_password)
    EditText inputPassword;
    @BindView(R.id.login_progressBar)
    ProgressBar progressBar;
    @BindView(R.id.sign_up_button)
    Button signUpButton;
    @BindView(R.id.log_in_button)
    Button loginButton;
    @BindView(R.id.reset_password_button)
    Button resetPasswordButton;


    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        signUpButton.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));

        resetPasswordButton.setOnClickListener(v -> {
            //startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            Toast.makeText(LoginActivity.this, "Not implemented yet", Toast.LENGTH_SHORT).show();
        });

        loginButton.setOnClickListener(v -> {
            String email = inputEmail.getText().toString();
            final String password = inputPassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            //authenticate user
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                // there was an error
                                if (password.length() < 6) {
                                    inputPassword.setError(LoginActivity.this.getString(R.string.minimum_password));
                                } else {
                                    Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                LoginActivity.this.startActivity(intent);
                                LoginActivity.this.finish();
                            }
                        }
                    });
        });
    }
}