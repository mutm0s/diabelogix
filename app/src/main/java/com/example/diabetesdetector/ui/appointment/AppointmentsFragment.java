package com.example.diabetesdetector.ui.appointment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.diabetesdetector.JavaMailSender;
import com.example.diabetesdetector.R;
import com.example.diabetesdetector.adapters.AppointmentAdapter;
import com.example.diabetesdetector.models.Appointment;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import android.util.Base64;

import java.util.Collections;




public class AppointmentsFragment extends Fragment {

    private static final String TAG = "AppointmentsFragment";
    private static final String APPLICATION_NAME = "YourAppName";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT;
    private static final java.util.Collection<String> SCOPES = Collections.singleton(GmailScopes.GMAIL_SEND);
    private GoogleSignInClient googleSignInClient;
    private static final int REQUEST_CODE_SIGN_IN = 1;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private RecyclerView appointmentsRecyclerView;
    private AppointmentAdapter appointmentAdapter;
    private CalendarView calendarView;
    private FloatingActionButton addAppointmentFab;
    private ImageView buttonBack;
    private DatabaseReference mDatabase;

    private String selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        appointmentsRecyclerView = view.findViewById(R.id.appointmentsRecyclerView);
        calendarView = view.findViewById(R.id.calendarView);
        addAppointmentFab = view.findViewById(R.id.addAppointmentFab);

        // Initialize RecyclerView and Adapter
        appointmentAdapter = new AppointmentAdapter();
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        appointmentsRecyclerView.setAdapter(appointmentAdapter);

        // Set up listeners
        addAppointmentFab.setOnClickListener(v -> showAddAppointmentDialog());

        // Initialize selectedDate with the current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        selectedDate = sdf.format(new Date());

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year);

        // Load today's appointments initially
        getTodayAppointments();

        // Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(GmailScopes.GMAIL_SEND))
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    private void saveAppointment(Appointment appointment) {
        // Save the appointment to the database
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String key = mDatabase.child("users").child(userId).child("appointments").push().getKey();
        if (key != null) {
            mDatabase.child("users").child(userId).child("appointments").child(key).setValue(appointment);
        }
    }

    private void getTodayAppointments() {
        // Get appointments from the database for the selected date
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("users").child(userId).child("appointments").orderByChild("timestamp").equalTo(selectedDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Appointment> appointments = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Appointment appointment = dataSnapshot.getValue(Appointment.class);
                            if (appointment != null) {
                                appointments.add(appointment);
                            }
                        }
                        appointmentAdapter.setAppointmentList(appointments);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });
    }

    private void showAddAppointmentDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.appointment_dialog, null);

        // Set up the dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Add Appointment");
        dialogBuilder.setPositiveButton("Save", null);
        dialogBuilder.setNegativeButton("Cancel", null);

        // Get references to the dialog views
        EditText doctorNameEditText = dialogView.findViewById(R.id.doctorNameEditText);
        EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        CalendarView appointmentCalendarView = dialogView.findViewById(R.id.appointmentCalendarView);
        EditText timeEditText = dialogView.findViewById(R.id.timeEditText);
        EditText emailEditText = dialogView.findViewById(R.id.emailEditText);

        // Set CalendarView date change listener
        appointmentCalendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year);

        // Show the dialog
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        // Set up the Save button click listener
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            // Handle the save action
            String doctorName = doctorNameEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            String time = timeEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();

            if (doctorName.isEmpty() || description.isEmpty() || time.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Create a new appointment and add it to the RecyclerView
                Appointment appointment = new Appointment(doctorName, email, description, selectedDate, time);
                appointmentAdapter.addAppointment(appointment);
                appointmentsRecyclerView.setVisibility(View.VISIBLE);

                saveAppointment(appointment);

                // Send email to the doctor's email address
                sendEmailToDoctor(email, description);

                // Dismiss the dialog
                alertDialog.dismiss();
            }
        });
    }

    private void sendEmailToDoctor(String doctorEmail, String description) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireActivity());
        sendEmail(account, doctorEmail, description);
    }

    private void sendEmail(GoogleSignInAccount account, String doctorEmail, String description) {
        if (account == null) {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
        } else {
            new SendEmailTask(account, doctorEmail, description).execute();
        }
    }

    private class SendEmailTask extends AsyncTask<Void, Void, Void> {
        private final GoogleSignInAccount account;
        private final String doctorEmail;
        private final String description;

        SendEmailTask(GoogleSignInAccount account, String doctorEmail, String description) {
            this.account = account;
            this.doctorEmail = doctorEmail;
            this.description = description;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                MimeMessage mimeMessage = createMessageWithEmail(description, doctorEmail);
                sendMessage(getGmailService(account), mimeMessage);
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Email sent", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                Log.e(TAG, "Failed to send email", e);
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Email sending failed", Toast.LENGTH_SHORT).show());
            }
            return null;
        }
    }

    private MimeMessage createMessageWithEmail(String description, String email) throws MessagingException {
        MimeMessage emailMessage = new MimeMessage(getSession());
        emailMessage.setFrom(new InternetAddress("your-email@gmail.com"));
        emailMessage.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(email));
        emailMessage.setSubject("Appointment Details");
        emailMessage.setText("Appointment Description: " + description);
        return emailMessage;
    }

    private Session getSession() {
        return Session.getInstance(getProperties());
    }

    private Properties getProperties() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        return props;
    }

    private Gmail getGmailService(GoogleSignInAccount account) throws GeneralSecurityException, IOException {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                requireContext(), SCOPES);
        credential.setSelectedAccount(account.getAccount());
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private void sendMessage(Gmail service, MimeMessage email) throws IOException, MessagingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        email.writeTo(baos);
        String encodedEmail = Base64.encodeToString(baos.toByteArray(), Base64.URL_SAFE | Base64.NO_WRAP);

        Message message = new Message().setRaw(encodedEmail);
        service.users().messages().send("me", message).execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Assuming doctorEmail and description are set
                String doctorEmail = "doctor@example.com";
                String description = "Appointment details";
                sendEmail(account, doctorEmail, description);
            } catch (ApiException e) {
                Log.e(TAG, "Google Sign-In failed", e);
            }
        }
    }
}






//
//
//
//public class AppointmentsFragment extends Fragment {
//
//    private RecyclerView appointmentsRecyclerView;
//    private AppointmentAdapter appointmentAdapter;
//    private CalendarView calendarView;
//    private FloatingActionButton addAppointmentFab;
//    private ImageView buttonBack;
//    private DatabaseReference mDatabase;
//
//    private String selectedDate;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_appointments, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//
//        // Initialize views
//        appointmentsRecyclerView = view.findViewById(R.id.appointmentsRecyclerView);
//        calendarView = view.findViewById(R.id.calendarView);
//        addAppointmentFab = view.findViewById(R.id.addAppointmentFab);
//
//        // Initialize RecyclerView and Adapter
//        appointmentAdapter = new AppointmentAdapter();
//        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        appointmentsRecyclerView.setAdapter(appointmentAdapter);
//
//        // Set up listeners
//        addAppointmentFab.setOnClickListener(v -> showAddAppointmentDialog());
//
//        // Initialize selectedDate with the current date
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        selectedDate = sdf.format(new Date());
//
//        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year);
//
//        // Load today's appointments initially
//        getTodayAppointments();
//    }
//
//    private void saveAppointment(Appointment appointment) {
//        // Save the appointment to the database
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        String key = mDatabase.child("users").child(userId).child("appointments").push().getKey();
//        if (key != null) {
//            mDatabase.child("users").child(userId).child("appointments").child(key).setValue(appointment);
//        }
//    }
//
//    private void getTodayAppointments() {
//        // Get appointments from the database for the selected date
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        mDatabase.child("users").child(userId).child("appointments").orderByChild("timestamp").equalTo(selectedDate)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        List<Appointment> appointments = new ArrayList<>();
//                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                            Appointment appointment = dataSnapshot.getValue(Appointment.class);
//                            if (appointment != null) {
//                                appointments.add(appointment);
//                            }
//                        }
//                        appointmentAdapter.setAppointmentList(appointments);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Handle database error
//                    }
//                });
//    }
//
//    private void showAddAppointmentDialog() {
//        // Inflate the dialog layout
//        LayoutInflater inflater = requireActivity().getLayoutInflater();
//        @SuppressLint("InflateParams")
//        View dialogView = inflater.inflate(R.layout.appointment_dialog, null);
//
//        // Set up the dialog
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
//        dialogBuilder.setView(dialogView);
//        dialogBuilder.setTitle("Add Appointment");
//        dialogBuilder.setPositiveButton("Save", null);
//        dialogBuilder.setNegativeButton("Cancel", null);
//
//        // Get references to the dialog views
//        EditText doctorNameEditText = dialogView.findViewById(R.id.doctorNameEditText);
//        EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
//        CalendarView appointmentCalendarView = dialogView.findViewById(R.id.appointmentCalendarView);
//        EditText timeEditText = dialogView.findViewById(R.id.timeEditText);
//        EditText emailEditText = dialogView.findViewById(R.id.emailEditText);
//
//        // Set CalendarView date change listener
//        appointmentCalendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year);
//
//        // Show the dialog
//        AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();
//
//        // Set up the Save button click listener
//        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
//            // Handle the save action
//            String doctorName = doctorNameEditText.getText().toString().trim();
//            String description = descriptionEditText.getText().toString().trim();
//            String time = timeEditText.getText().toString().trim();
//            String email = emailEditText.getText().toString().trim();
//
//            if (doctorName.isEmpty() || description.isEmpty() || time.isEmpty() || email.isEmpty()) {
//                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
//            } else {
//                // Create a new appointment and add it to the RecyclerView
//                Appointment appointment = new Appointment(doctorName, email, description, selectedDate, time);
//                appointmentAdapter.addAppointment(appointment);
//                appointmentsRecyclerView.setVisibility(View.VISIBLE);
//
//                saveAppointment(appointment);
//
//                // Send email to the provided email address (Implement this functionality using Firebase Cloud Functions or a similar service)
//
//                // Dismiss the dialog
//                alertDialog.dismiss();
//            }
//        });
//    }
//}








//
//
//public class AppointmentsFragment extends Fragment {
//
//    private RecyclerView appointmentsRecyclerView;
//    private AppointmentAdapter appointmentAdapter;
//    private CalendarView calendarView;
//    private FloatingActionButton addAppointmentFab;
//    private ImageView buttonBack;
//    private DatabaseReference mDatabase;
//
//    private String selectedDate;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_appointments, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//
//        // Initialize views
//        appointmentsRecyclerView = view.findViewById(R.id.appointmentsRecyclerView);
//        calendarView = view.findViewById(R.id.calendarView);
//        addAppointmentFab = view.findViewById(R.id.addAppointmentFab);
//
//        // Initialize RecyclerView and Adapter
//        appointmentAdapter = new AppointmentAdapter();
//        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        appointmentsRecyclerView.setAdapter(appointmentAdapter);
//
//        // Set up listeners
//        addAppointmentFab.setOnClickListener(v -> showAddAppointmentDialog());
//
//        // Initialize selectedDate with the current date
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        selectedDate = sdf.format(new Date());
//
//        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year);
//
//        // Load today's appointments initially
//        getTodayAppointments();
//    }
//
//    private void saveAppointment(Appointment appointment) {
//        // Save the appointment to the database
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        String key = mDatabase.child("users").child(userId).child("appointments").push().getKey();
//        if (key != null) {
//            mDatabase.child("users").child(userId).child("appointments").child(key).setValue(appointment);
//        }
//    }
//
//    private void getTodayAppointments() {
//        // Get appointments from the database for the selected date
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        mDatabase.child("users").child(userId).child("appointments").orderByChild("timestamp").equalTo(selectedDate)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        List<Appointment> appointments = new ArrayList<>();
//                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                            Appointment appointment = dataSnapshot.getValue(Appointment.class);
//                            if (appointment != null) {
//                                appointments.add(appointment);
//                            }
//                        }
//                        appointmentAdapter.setAppointmentList(appointments);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Handle database error
//                    }
//                });
//    }
//
//    private void showAddAppointmentDialog() {
//        // Inflate the dialog layout
//        LayoutInflater inflater = requireActivity().getLayoutInflater();
//        @SuppressLint("InflateParams")
//        View dialogView = inflater.inflate(R.layout.appointment_dialog, null);
//
//        // Set up the dialog
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
//        dialogBuilder.setView(dialogView);
//        dialogBuilder.setTitle("Add Appointment");
//        dialogBuilder.setPositiveButton("Save", null);
//        dialogBuilder.setNegativeButton("Cancel", null);
//
//        // Get references to the dialog views
//        EditText doctorNameEditText = dialogView.findViewById(R.id.doctorNameEditText);
//        EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
//        CalendarView appointmentCalendarView = dialogView.findViewById(R.id.appointmentCalendarView);
//        EditText timeEditText = dialogView.findViewById(R.id.timeEditText);
//        EditText emailEditText = dialogView.findViewById(R.id.emailEditText);
//
//        // Set CalendarView date change listener
//        appointmentCalendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year);
//
//        // Show the dialog
//        AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();
//
//        // Set up the Save button click listener
//        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
//            // Handle the save action
//            String doctorName = doctorNameEditText.getText().toString().trim();
//            String description = descriptionEditText.getText().toString().trim();
//            String time = timeEditText.getText().toString().trim();
//            String email = emailEditText.getText().toString().trim();
//
//            if (doctorName.isEmpty() || description.isEmpty() || time.isEmpty() || email.isEmpty()) {
//                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
//            } else {
//                // Create a new appointment and add it to the RecyclerView
//                Appointment appointment = new Appointment(doctorName, email, description, selectedDate, time);
//                appointmentAdapter.addAppointment(appointment);
//                appointmentsRecyclerView.setVisibility(View.VISIBLE);
//
//                saveAppointment(appointment);
//
//                // Send email to the doctor's email address
//                sendEmailToDoctor(email, description);
//
//                // Dismiss the dialog
//                alertDialog.dismiss();
//            }
//        });
//    }
//
//    private void sendEmailToDoctor(String doctorEmail, String description) {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            String userId = user.getUid();
//            String subject = "Appointment Details";
//            String message = "Appointment Description: " + description + "\nUser Email: " + user.getEmail();
//
//            JavaMailSender.sendEmail(userId, doctorEmail, subject, message);
//        } else {
//            Toast.makeText(requireContext(), "User not authenticated.", Toast.LENGTH_SHORT).show();
//        }
//    }
//    }































//    private void showAddAppointmentDialog() {
//        // Inflate the dialog layout
//        LayoutInflater inflater = requireActivity().getLayoutInflater();
//        @SuppressLint("InflateParams")
//        View dialogView = inflater.inflate(R.layout.appointment_dialog, null);
//
//        // Set up the dialog
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
//        dialogBuilder.setView(dialogView);
//        dialogBuilder.setTitle("Add Appointment");
//        dialogBuilder.setPositiveButton("Save", null);
//        dialogBuilder.setNegativeButton("Cancel", null);
//
//        // Get references to the dialog views
//        EditText doctorNameEditText = dialogView.findViewById(R.id.doctorNameEditText);
//        EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
//        CalendarView appointmentCalendarView = dialogView.findViewById(R.id.appointmentCalendarView);
//        EditText timeEditText = dialogView.findViewById(R.id.timeEditText);
//        EditText emailEditText = dialogView.findViewById(R.id.emailEditText);
//
//        // Set CalendarView date change listener
//        appointmentCalendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year);
//
//        // Show the dialog
//        AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();
//
//        // Set up the Save button click listener
//        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
//            // Handle the save action
//            String doctorName = doctorNameEditText.getText().toString().trim();
//            String description = descriptionEditText.getText().toString().trim();
//            String time = timeEditText.getText().toString().trim();
//            String email = emailEditText.getText().toString().trim();
//
//            if (doctorName.isEmpty() || description.isEmpty() || time.isEmpty() || email.isEmpty()) {
//                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
//            } else {
//                // Create a new appointment and add it to the RecyclerView
//                Appointment appointment = new Appointment(doctorName, email, description, selectedDate, time);
//                appointmentAdapter.addAppointment(appointment);
//                appointmentsRecyclerView.setVisibility(View.VISIBLE);
//
//                saveAppointment(appointment);
//
//                // Send email to the doctor's email address
//                sendEmailToDoctor(email, description);
//
//                // Dismiss the dialog
//                alertDialog.dismiss();
//            }
//        });
//    }
//
//    private void sendEmailToDoctor(String doctorEmail, String description) {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            String userEmail = user.getEmail();
//            String subject = "Appointment Details";
//            String message = "Appointment Description: " + description + "\nUser Email: " + userEmail;
//
//            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", doctorEmail, null));
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
//            emailIntent.putExtra(Intent.EXTRA_TEXT, message);
//
//            try {
//                startActivity(Intent.createChooser(emailIntent, "Send email..."));
//            } catch (android.content.ActivityNotFoundException ex) {
//                Toast.makeText(requireContext(), "No email client installed.", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(requireContext(), "User not authenticated.", Toast.LENGTH_SHORT).show();
//        }
//    }






