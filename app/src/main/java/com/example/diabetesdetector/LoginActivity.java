package com.example.diabetesdetector;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText emailEditText, passwordEditText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth and Database reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);

        // Check if user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToMainActivity();
        }

        // Set onClick listener for the register button
        findViewById(R.id.button_register).setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        // Set onClick listener for the forgot password text view
        TextView forgotPasswordTextView = findViewById(R.id.textViewForgotPassword);
        forgotPasswordTextView.setOnClickListener(v -> showForgotPasswordDialog());
        forgotPasswordTextView.setPaintFlags(forgotPasswordTextView.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
    }

    public void loginUser(View view) {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        // Sign in with email and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        navigateToMainActivity();
                    } else {
                        Log.e(TAG, "Authentication failed: ", task.getException());
                        showToast("Authentication failed.");
                    }
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showForgotPasswordDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_forgot_password);

        EditText editTextEmailDialog = dialog.findViewById(R.id.editTextDialogEmail);
        Button buttonSubmit = dialog.findViewById(R.id.buttonDialogSubmit);

        buttonSubmit.setOnClickListener(v -> {
            String email = editTextEmailDialog.getText().toString().trim();
            Log.d(TAG, "Email from dialog: " + email);

            if (email.isEmpty()) {
                Log.e(TAG, "Email is required.");
                showToast("Email is required.");
                editTextEmailDialog.requestFocus();
            } else {
                checkUsernameExistence(email, editTextEmailDialog, dialog);
            }
        });

        dialog.show();
    }

    private void checkUsernameExistence(String username, EditText editTextEmailDialog, Dialog dialog) {
        String normalizedUsername = username.toLowerCase().trim();
        Log.d(TAG, "Checking username existence: " + normalizedUsername);

        mDatabase.child("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                Log.d(TAG, "DataSnapshot: " + dataSnapshot.toString());

                boolean usernameExists = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userUsername = userSnapshot.child("username").getValue(String.class);
                    if (userUsername != null && userUsername.equalsIgnoreCase(normalizedUsername)) {
                        usernameExists = true;
                        break;
                    }
                }

                if (usernameExists) {
                    sendPasswordResetEmail(username, editTextEmailDialog);
                    dialog.dismiss();
                } else {
                    Log.e(TAG, "Username does not exist.");
                    showToast("Username does not exist.");
                    editTextEmailDialog.requestFocus();
                }
            } else {
                Log.e(TAG, "Error checking username: ", task.getException());
                showToast("Error checking username.");
                editTextEmailDialog.requestFocus();
            }
        });
    }

    private void sendPasswordResetEmail(String username, EditText editTextEmail) {
        String normalizedUsername = username.toLowerCase().trim();
        String emailAddress = normalizedUsername;

        // Validate email format
        if (!isValidEmail(emailAddress)) {
            Log.e(TAG, "The email address is badly formatted: " + emailAddress);
            showToast("The email address is badly formatted.");
            editTextEmail.requestFocus();
            return;
        }

        // Send password reset email
        mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Password reset email sent to: " + emailAddress);
                showToast("Password reset email sent.");
            } else {
                Log.e(TAG, "Failed to send password reset email: ", task.getException());
                showToast("Failed to send password reset email.");
            }
        });
    }

    private boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
