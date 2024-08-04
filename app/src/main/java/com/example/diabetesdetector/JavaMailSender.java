package com.example.diabetesdetector;

import android.util.Log;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JavaMailSender {

    private static final String TAG = "JavaMailSender";

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587"; // TLS port

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void sendEmail(String userId, String recipientEmail, String subject, String messageBody) {
        // Get user email and password from Firebase
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Inside onDataChange");
                String userEmail = dataSnapshot.child("email").getValue(String.class);
                String userPassword = dataSnapshot.child("password").getValue(String.class);

                if (userEmail != null && userPassword != null) {
                    executorService.execute(() -> {
                        try {
                            sendEmailWithCredentials(userEmail, userPassword, recipientEmail, subject, messageBody);
                        } catch (MessagingException e) {
                            Log.e(TAG, "Failed to send email: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                } else {
                    Log.e(TAG, "User email or password is null");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database query cancelled: " + databaseError.getMessage());
            }
        });
    }

    private static void sendEmailWithCredentials(String userEmail, String userPassword, String recipientEmail, String subject, String messageBody) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userEmail, userPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(userEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(messageBody);

            Transport.send(message);
            Log.d(TAG, "Email sent successfully to " + recipientEmail);
        } catch (MessagingException e) {
            Log.e(TAG, "Failed to send email: " + e.getMessage());
            throw e; // Rethrow the exception to propagate it up
        }
    }
}
