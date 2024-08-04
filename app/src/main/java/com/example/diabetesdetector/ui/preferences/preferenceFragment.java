package com.example.diabetesdetector.ui.preferences;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.example.diabetesdetector.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.UUID;

public class preferenceFragment extends PreferenceFragmentCompat {

    private FirebaseAuth auth;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        auth = FirebaseAuth.getInstance();

        // Change Password Preference
        Preference changePasswordPref = findPreference("change_password");
        if (changePasswordPref != null) {
            changePasswordPref.setOnPreferenceClickListener(preference -> {
                showChangePasswordDialog();
                return true;
            });
        }

        // Update Email Preference
        Preference updateEmailPref = findPreference("update_email");
        if (updateEmailPref != null) {
            updateEmailPref.setOnPreferenceClickListener(preference -> {
                showUpdateEmailDialog();
                return true;
            });
        }

        // Manage Profile Preference
        Preference manageProfilePref = findPreference("manage_profile");
        if (manageProfilePref != null) {
            manageProfilePref.setOnPreferenceClickListener(preference -> {
                showManageProfileDialog();
                return true;
            });
        }

        // Font Size Adjustment Preference
        SeekBarPreference fontSizePref = findPreference("font_size");
        if (fontSizePref != null) {
            fontSizePref.setOnPreferenceChangeListener((preference, newValue) -> {
                int fontSize = (int) newValue;
                // Save the new font size value
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit();
                editor.putInt("font_size", fontSize);
                editor.apply();
                // Apply the new font size to all text within the app
                applyFontSize(requireActivity().getWindow().getDecorView(), fontSize);
                return true;
            });
        }


        // Theme Selection Preference
        ListPreference themePref = findPreference("theme_selection");
        if (themePref != null) {
            themePref.setOnPreferenceChangeListener((preference, newValue) -> {
                String theme = (String) newValue;
                switch (theme) {
                    case "light":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case "dark":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                    case "system":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        break;
                }
                return true;
            });
        }

        // Daily Summary Preference
        SwitchPreferenceCompat dailySummaryPref = findPreference("daily_summary");
        if (dailySummaryPref != null) {
            dailySummaryPref.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean dailySummaryEnabled = (boolean) newValue;
                // Handle daily summary change here
                if (dailySummaryEnabled) {
                    sendDailySummaryNotification(requireContext());
                } else {
                    // Remove any existing daily summary notifications
                    NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    if (notificationManager != null) {
                        notificationManager.cancelAll();
                    }
                }
                // Save the new preference value
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit();
                editor.putBoolean("daily_summary_enabled", dailySummaryEnabled);
                editor.apply();
                return true;
            });
        }
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change Password");

        // Set up the input fields
        final EditText inputCurrentPassword = new EditText(getContext());
        inputCurrentPassword.setHint("Current Password");
        inputCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final EditText inputNewPassword = new EditText(getContext());
        inputNewPassword.setHint("New Password");
        inputNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final EditText inputConfirmPassword = new EditText(getContext());
        inputConfirmPassword.setHint("Confirm New Password");
        inputConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Create a linear layout to hold the inputs
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputCurrentPassword);
        layout.addView(inputNewPassword);
        layout.addView(inputConfirmPassword);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Change", (dialog, which) -> {
            String currentPassword = inputCurrentPassword.getText().toString();
            String newPassword = inputNewPassword.getText().toString();
            String confirmPassword = inputConfirmPassword.getText().toString();

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = auth.getCurrentUser();
            if (user != null && !currentPassword.isEmpty() && !newPassword.isEmpty()) {
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Password update failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showUpdateEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Update Email");

        // Set up the input fields
        final EditText inputCurrentPassword = new EditText(getContext());
        inputCurrentPassword.setHint("Current Password");
        inputCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final EditText inputNewEmail = new EditText(getContext());
        inputNewEmail.setHint("New Email");
        inputNewEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        // Create a linear layout to hold the inputs
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputCurrentPassword);
        layout.addView(inputNewEmail);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Update", (dialog, which) -> {
            String currentPassword = inputCurrentPassword.getText().toString();
            String newEmail = inputNewEmail.getText().toString();

            FirebaseUser user = auth.getCurrentUser();
            if (user != null && !currentPassword.isEmpty() && !newEmail.isEmpty()) {
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updateEmail(newEmail).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(getContext(), "Email updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Email update failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showManageProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Manage Profile");

        // Set up the input field for profile image
        builder.setMessage("Select a new profile image from your device");

        // Set up the buttons
        builder.setPositiveButton("Select Image", (dialog, which) -> selectImage());

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == FragmentActivity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_images/" + UUID.randomUUID().toString());

                storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String profileImageUrl = uri.toString();
                        // Update user profile image logic here...
                        Toast.makeText(getContext(), "Profile image updated", Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> Toast.makeText(getContext(), "Profile image update failed", Toast.LENGTH_SHORT).show());
            }
        }
    }

    // Method to send daily summary notification
    private void sendDailySummaryNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            // Create notification channel for Android O and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("daily_summary_channel", "Daily Summary", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            // Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "daily_summary_channel")
                    .setSmallIcon(R.drawable.notifications)
                    .setContentTitle("Daily Summary")
                    .setContentText("Your daily summary notification message goes here")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            // Show the notification
            notificationManager.notify(0, builder.build());
        }
    }

    // Method to recursively apply the font size to all text within the app
    private void applyFontSize(View view, int fontSize) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = viewGroup.getChildAt(i);
                applyFontSize(childView, fontSize);
            }
        } else if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        }
    }

}


