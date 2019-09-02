package com.isep.simov.todo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.isep.simov.todo.R;
import com.isep.simov.todo.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {


    @BindView(R.id.sign_up_email)
    EditText inputEmail;
    @BindView(R.id.sign_up_password)
    EditText inputPassword;
    @BindView(R.id.sign_in_button)
    Button signInButton;
    @BindView(R.id.sign_up_button)
    Button signUpButton;
    @BindView(R.id.reset_password_button)
    Button resetPasswordButton;
    @BindView(R.id.sign_up_progressBar)
    ProgressBar progressBar;


    private FirebaseAuth auth;
    private FirebaseFirestore firebaseDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseFirestore.getInstance();
        resetPasswordButton.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "Not implemented yet", Toast.LENGTH_SHORT).show());

        signInButton.setOnClickListener(v -> {
                    Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show();
                    finish();
                }
        );


        signUpButton.setOnClickListener(v -> {

            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            //create user
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, task -> {
                        Toast.makeText(SignUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String id = auth.getCurrentUser().getUid();
                            User user = new User(id, email);

                            firebaseDB.collection("users").document(id).set(user);
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();
                        }
                    });

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
