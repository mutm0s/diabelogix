package com.example.diabetesdetector.apiService;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiCaller extends AsyncTask<String, Void, String> {
    private ApiCallerCallback callback;

    public void setApiCallerCallback(ApiCallerCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        // params[0] should be the URL of the API
        String apiUrl = params[0];
        try {
            // Create URL object
            URL url = new URL(apiUrl);

            // Create HttpURLConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Set request method
            urlConnection.setRequestMethod("GET");

            urlConnection.setRequestProperty("X-Api-Key", params[1]);

            // Read the response
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }

            // Close resources
            bufferedReader.close();
            inputStream.close();
            urlConnection.disconnect();

            // Return the response as a String

            return response.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            super.onPostExecute(result);
            if (callback != null) {
                callback.onApiCallCompleted(result);
            }
        } else {
            // Handle the case where API call failed
        }
    }

    public interface ApiCallerCallback {
        void onApiCallCompleted(String result);
    }
}
