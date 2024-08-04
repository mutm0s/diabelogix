package com.example.diabetesdetector.ui.bot;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diabetesdetector.BuildConfig;
import com.example.diabetesdetector.R;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatBot extends Fragment {

    private static final String TAG = "ChatBot";
    private LinearLayout messageContainer;
    private EditText messageEditText;
    private ScrollView scrollView;

    public static ChatBot newInstance() {
        return new ChatBot();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bot, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViews();
        loadDummyMessages();
    }

    private void initializeViews() {
        scrollView = getView().findViewById(R.id.scrollView);
        messageContainer = getView().findViewById(R.id.messageContainer);
        messageEditText = getView().findViewById(R.id.messageEditText);
        ImageButton sendButton = getView().findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> {
            String userInput = messageEditText.getText().toString().trim();
            if (!userInput.isEmpty()) {
                addOutgoingMessage(userInput);
                messageEditText.setText("");
                generateResponse(userInput, 3);
            }
        });
    }

    private void loadDummyMessages() {
        String[] incomingMessages = {"Hello!", "How are you?"};
        String[] outgoingMessages = {"Hi!", "I'm fine, thank you."};

        for (String message : incomingMessages) {
            addIncomingMessage(message);
        }

        for (String message : outgoingMessages) {
            addOutgoingMessage(message);
        }
    }

    private void generateResponse(String prompt, int retries) {
        GenerativeModel gm = new GenerativeModel("gemini-pro", BuildConfig.GEMINI_API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder().addText(prompt).build();
        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                Log.d(TAG, "Response received: " + resultText);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> addIncomingMessage(resultText));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (t instanceof com.google.ai.client.generativeai.type.GoogleGenerativeAIException) {
                    com.google.ai.client.generativeai.type.GoogleGenerativeAIException aiException = (com.google.ai.client.generativeai.type.GoogleGenerativeAIException) t;
                    String message = aiException.getMessage();
                    Log.e(TAG, "Error generating response: " + message, t);

                    if (message.contains("The model is overloaded. Please try again later.")) {
                        Log.e(TAG, "Model overloaded, retrying...");
                        if (retries > 0) {
                            retryRequest(prompt, retries - 1);
                        } else {
                            Log.e(TAG, "Max retries reached, could not get response");
                        }
                    }
                } else {
                    Log.e(TAG, "Unexpected error", t);
                    if (retries > 0) {
                        retryRequest(prompt, retries - 1);
                    }
                }
            }
        }, executor);
    }

    private void retryRequest(String prompt, int retries) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Thread.sleep(2000); // Wait for 2 seconds before retrying
                generateResponse(prompt, retries);
            } catch (InterruptedException e) {
                Log.e(TAG, "Retry interrupted", e);
            }
        });
    }

    private void addIncomingMessage(String messageText) {
        LinearLayout incomingMessageLayout = (LinearLayout) LayoutInflater.from(getContext())
                .inflate(R.layout.incoming_bot, messageContainer, false);
        TextView messageTextView = incomingMessageLayout.findViewById(R.id.incoming);
        messageTextView.setText(messageText);
        messageContainer.addView(incomingMessageLayout);
        scrollToBottom();
    }

    private void addOutgoingMessage(String messageText) {
        LinearLayout outgoingMessageLayout = (LinearLayout) LayoutInflater.from(getContext())
                .inflate(R.layout.outgoing_bot, messageContainer, false);
        TextView messageTextView = outgoingMessageLayout.findViewById(R.id.outgoing);
        messageTextView.setText(messageText);
        messageContainer.addView(outgoingMessageLayout);
        scrollToBottom();
    }

    private void scrollToBottom() {
        if (getView() != null) {
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        }
    }
}


//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.ScrollView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.example.diabetesdetector.BuildConfig;
//import com.example.diabetesdetector.R;
//import com.google.ai.client.generativeai.GenerativeModel;
//import com.google.ai.client.generativeai.java.GenerativeModelFutures;
//import com.google.ai.client.generativeai.type.Content;
//import com.google.ai.client.generativeai.type.GenerateContentResponse;
//import com.google.common.util.concurrent.FutureCallback;
//import com.google.common.util.concurrent.Futures;
//import com.google.common.util.concurrent.ListenableFuture;
//
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//
//public class ChatBot extends Fragment {
//
//    private LinearLayout messageContainer;
//    private EditText messageEditText;
//    private ScrollView scrollView;
//
//    public static ChatBot newInstance() {
//        return new ChatBot();
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_bot, container, false);
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        initializeViews();
//        loadDummyMessages();
//    }
//
//    private void initializeViews() {
//        scrollView = getView().findViewById(R.id.scrollView);
//        messageContainer = getView().findViewById(R.id.messageContainer);
//        messageEditText = getView().findViewById(R.id.messageEditText);
//        ImageButton sendButton = getView().findViewById(R.id.sendButton);
//
//        sendButton.setOnClickListener(v -> {
//            String userInput = messageEditText.getText().toString().trim();
//            if (!userInput.isEmpty()) {
//                addOutgoingMessage(userInput);
//                messageEditText.setText("");
//                generateResponse(userInput);
//            }
//        });
//    }
//
//    private void loadDummyMessages() {
//        String[] incomingMessages = {"Hello!", "How are you?"};
//        String[] outgoingMessages = {"Hi!", "I'm fine, thank you."};
//
//        for (String message : incomingMessages) {
//            addIncomingMessage(message);
//        }
//
//        for (String message : outgoingMessages) {
//            addOutgoingMessage(message);
//        }
//    }
//
//    private void generateResponse(String prompt) {
//        GenerativeModel gm = new GenerativeModel("gemini-pro", BuildConfig.GEMINI_API_KEY);
//        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
//
//        Content content = new Content.Builder().addText(prompt).build();
//        Executor executor = Executors.newSingleThreadExecutor();
//        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
//
//        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
//            @Override
//            public void onSuccess(GenerateContentResponse result) {
//                String resultText = result.getText();
//                Log.d("ChatBot", "Response received: " + resultText);
//                if (getActivity() != null) {
//                    getActivity().runOnUiThread(() -> addIncomingMessage(resultText));
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Log.e("ChatBot", "Error generating response", t);
//            }
//        }, executor);
//    }
//
//    private void addIncomingMessage(String messageText) {
//        LinearLayout incomingMessageLayout = (LinearLayout) LayoutInflater.from(getContext())
//                .inflate(R.layout.incoming_bot, messageContainer, false);
//        TextView messageTextView = incomingMessageLayout.findViewById(R.id.incoming);
//        messageTextView.setText(messageText);
//        messageContainer.addView(incomingMessageLayout);
//        scrollToBottom();
//    }
//
//    private void addOutgoingMessage(String messageText) {
//        LinearLayout outgoingMessageLayout = (LinearLayout) LayoutInflater.from(getContext())
//                .inflate(R.layout.outgoing_bot, messageContainer, false);
//        TextView messageTextView = outgoingMessageLayout.findViewById(R.id.outgoing);
//        messageTextView.setText(messageText);
//        messageContainer.addView(outgoingMessageLayout);
//        scrollToBottom();
//    }
//
//    private void scrollToBottom() {
//        if (getView() != null) {
//            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
//        }
//    }
//}
//
