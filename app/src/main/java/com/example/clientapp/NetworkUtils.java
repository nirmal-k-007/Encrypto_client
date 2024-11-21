package com.example.clientapp;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class NetworkUtils {


    public static String[] generateKeyPair() {
        try {
            // Initialize the KeyPairGenerator for RSA
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // 2048-bit key size
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Encode the private and public keys to Base64
            String encodedPrivateKey = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                encodedPrivateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            }
            String encodedPublicKey = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                encodedPublicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            }

            // Return both keys as an array (private key first, then public key)
            return new String[]{encodedPrivateKey, encodedPublicKey};

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String computeSHA256(String input) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Compute the hash of the input string
            byte[] hashBytes = digest.digest(input.getBytes());

            // Convert the byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                // Convert each byte to a two-digit hexadecimal representation
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            // Return the SHA-256 hash as a hexadecimal string
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void makePostRequest(String urlString, String jsonInput, Callback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                HttpURLConnection connection = null;
                try {
                    // Create URL object
                    URL url = new URL(urlString);

                    // Open connection
                    connection = (HttpURLConnection) url.openConnection();

                    // Set request method and properties
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; utf-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true); // Enable output stream for writing JSON data

                    // Write JSON string to output stream
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonInput.getBytes("utf-8"); // Use the raw string as input
                        os.write(input, 0, input.length);
                    }

                    // Read response code
                    int responseCode = connection.getResponseCode();
                    StringBuilder responseMessage = new StringBuilder();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Process success response
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                responseMessage.append(inputLine);
                            }
                        }
                    } else {
                        // Process error response
                        return "Error: " + responseCode;
                    }

                    // Return the response from the server
                    return responseMessage.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                    return "Exception: " + e.getMessage();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }

            @Override
            protected void onPostExecute(String result) {
                // Invoke callback with the result (server's response)
                callback.onComplete(result);
            }
        }.execute();
    }

    // Callback interface to handle the result
    public interface Callback {
        void onComplete(String result);
    }

}
