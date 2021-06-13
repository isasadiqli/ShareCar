package com.example.sharecar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class UpdateUserActivity extends AppCompatActivity {

    private EditText oldpassword, password, passwordReentered;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Profile");

        TypedValue typedValue = new TypedValue();
        this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        final int color = typedValue.data;
        actionBar.setBackgroundDrawable(new ColorDrawable(color));

        user = FirebaseAuth.getInstance().getCurrentUser();

        TextView username = findViewById(R.id.username_update);
        TextView email = findViewById(R.id.email_update);
        Button confirm = findViewById(R.id.confirm_update);

        oldpassword = findViewById(R.id.old_password_update);
        password = findViewById(R.id.password_update);
        passwordReentered = findViewById(R.id.password_reenter_update);

        username.setText(Tools.getUser().getUserName());
        email.setText(Tools.getUser().getEmail());

        Tools.editTextListener(confirm, oldpassword, "oldpassword", 3);
        Tools.editTextListener(confirm, password, "password", 3);
        Tools.editTextListener(confirm, passwordReentered, "passwordReentered", 3);


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void updateUserClicked(View view) {
        String oldPassword = oldpassword.getText().toString();
        String password = this.password.getText().toString();

        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), oldPassword);

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        assert user != null;
                        user.updatePassword(password)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(this, "Password successfully updated", Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(this, "There has been an error", Toast.LENGTH_SHORT).show();
                                });
                    } else
                        Toast.makeText(UpdateUserActivity.this,
                                "There has been an error while reauthentication", Toast.LENGTH_SHORT).show();
                });


    }
}