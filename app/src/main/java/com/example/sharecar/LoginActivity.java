package com.example.sharecar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

public class LoginActivity extends AppCompatActivity {

    private static int haveAccount = 0;

    private EditText name, surname, email, password, passwordReentered;
    private TextInputLayout name_layout, surname_layout, email_layout, password_layout, passwordReentered_layout;
    private Button loginButton;
    private TextView signUpIn, loginTV;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        passwordReentered = findViewById(R.id.passwordReentered);

        name_layout = findViewById(R.id.name_layout);
        surname_layout = findViewById(R.id.surname_layout);
        email_layout = findViewById(R.id.email_layout);
        password_layout = findViewById(R.id.password_layout);
        passwordReentered_layout = findViewById(R.id.passwordReentered_layout);

        loginButton = findViewById(R.id.loginButton);
        signUpIn = findViewById(R.id.signUp);
        loginTV = findViewById(R.id.login_tv);

        progressBar = findViewById(R.id.login_progress_bar);

        Tools.editTextListener(loginButton, name, "name", 0);
        Tools.editTextListener(loginButton, surname, "surname", 0);
        Tools.editTextListener(loginButton, email, "email", 0);
        Tools.editTextListener(loginButton, password, "password", 0);
        Tools.editTextListener(loginButton, passwordReentered, "passwordReentered", 0);

        mAuth = WelcomeActivity.mAuth;

    }

    public void login(View v) {
        String nameStr = name.getText().toString();
        String surnameStr = surname.getText().toString();
        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();

        progressBar.setVisibility(View.VISIBLE);
        LinearLayout layout = findViewById(R.id.login_layout);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(false);
        }

        if (Tools.isSignedUp()) {
            mAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(this, task -> {
                if (!task.isSuccessful())
                    Toast.makeText(LoginActivity.this, "Error happened while signing in", Toast.LENGTH_SHORT).show();
                else {
                    WelcomeActivity.firstCallback = true;
                    Tools.readUserInfo("login", new FirebaseCallback() {
                        @Override
                        public void onCallBack(User user) {
                            if (WelcomeActivity.firstCallback) {
                                Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                                finish();
                                startActivity(intent);
                                WelcomeActivity.firstCallback = false;
                                progressBar.setVisibility(View.GONE);
                                for (int i = 0; i < layout.getChildCount(); i++) {
                                    View child = layout.getChildAt(i);
                                    child.setEnabled(true);
                                }
                            }
                        }
                    });
                }
            });


        } else {

            mAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(this, task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Error happened while signing up", Toast.LENGTH_SHORT).show();
                } else {
                    String userId = mAuth.getCurrentUser().getUid();

                    User user = new User(nameStr, surnameStr, emailStr, userId);

                    DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(userId);
                    currentUserDB.setValue(user).addOnCompleteListener(task1 -> {
                        if (task.isSuccessful())
                            Toast.makeText(this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        for (int i = 0; i < layout.getChildCount(); i++) {
                            View child = layout.getChildAt(i);
                            child.setEnabled(true);
                        }
                    });
                }


            });
        }

    }

    public void signUpClicked(View v) {
        haveAccount++;
        if (haveAccount % 2 != 0) {
            Tools.setSignedUp(false);


            name_layout.setVisibility(View.VISIBLE);
            surname_layout.setVisibility(View.VISIBLE);
            passwordReentered_layout.setVisibility(View.VISIBLE);

            loginButton.setText(R.string.signUp);
            signUpIn.setText(R.string.haveAccount);
            loginTV.setText(R.string.signUp);

        } else {
            Tools.setSignedUp(true);


            name_layout.setVisibility(View.GONE);
            surname_layout.setVisibility(View.GONE);
            passwordReentered_layout.setVisibility(View.GONE);


            loginButton.setText(R.string.login);
            signUpIn.setText(R.string.need_an_account);
            loginTV.setText(R.string.login);
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }


}