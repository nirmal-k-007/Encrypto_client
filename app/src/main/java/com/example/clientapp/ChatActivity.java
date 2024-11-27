package com.example.clientapp;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;
import android.util.Base64;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.java_websocket.client.WebSocketClient;

import android.util.Log;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

class RSAEncryptionUtil {

    // Generate RSA Key Pair
    public static String[] generateKeyPair() {
        try {
            // Initialize the KeyPairGenerator for RSA
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // 2048-bit key size
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Encode the private and public keys to Base64
            String encodedPrivateKey = Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.NO_WRAP);
            String encodedPublicKey = Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.NO_WRAP);

            // Return both keys as an array (private key first, then public key)
            return new String[]{encodedPrivateKey, encodedPublicKey};

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Decode private key from Base64
    public static PrivateKey decodePrivateKey(String encodedPrivateKey) throws Exception {
        byte[] decodedKey = Base64.decode(encodedPrivateKey, Base64.NO_WRAP);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    // Decode public key from Base64
    public static PublicKey decodePublicKey(String encodedPublicKey) throws Exception {
        byte[] decodedKey = Base64.decode(encodedPublicKey, Base64.NO_WRAP);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    // Encrypt data using the Public Key
    public static String encrypt(String data, String publicKeyStr) throws Exception {
        PublicKey publicKey = decodePublicKey(publicKeyStr);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());

        // Use appropriate Base64 encoding depending on API level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return java.util.Base64.getEncoder().encodeToString(encryptedBytes); // For API 26+
        } else {
            return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP); // For lower API levels
        }
    }

    // Decrypt data using the Private Key
    public static String decrypt(String encryptedData, String privateKeyStr) throws Exception {
        PrivateKey privateKey = decodePrivateKey(privateKeyStr);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = null;

        // Use appropriate Base64 decoding depending on API level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            decryptedBytes = cipher.doFinal(java.util.Base64.getDecoder().decode(encryptedData)); // For API 26+
        } else {
            decryptedBytes = cipher.doFinal(Base64.decode(encryptedData, Base64.NO_WRAP)); // For lower API levels
        }

        return new String(decryptedBytes);
    }


    public RSAEncryptionUtil() {
    }
}

class MsgFormat{
    String fromID;
    String toID;
    String content;


    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getToID() {
        return toID;
    }

    public MsgFormat() {
    }

    public void setToID(String toID) {
        this.toID = toID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



    public MsgFormat(String fromID, String toID, String content) {
        this.fromID = fromID;
        this.toID = toID;
        this.content = content;
    }
}






public class ChatActivity extends AppCompatActivity {

    ChatAppDatabaseHelper dbhelper = new ChatAppDatabaseHelper(this);

    private WebSocketClient webSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        NecessaryData necessarydata = getIntent().getParcelableExtra("necessarydata");


        TextView to = findViewById(R.id.to);
        assert necessarydata != null;
        to.setText(necessarydata.getTo());



        LinearLayout msgctr = findViewById(R.id.msgctr);
        EditText messageComp = findViewById(R.id.msg);
        Button send = findViewById(R.id.send);

        loadContent(msgctr,necessarydata);

        ObjectMapper om = new ObjectMapper();
        connectWebSocket(necessarydata.getUname(),necessarydata,msgctr);

        send.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String message = messageComp.getText().toString();
                if(!message.equals(""))
                {
                    MsgFormat mess = new MsgFormat();
                    try {
                        String mtos = RSAEncryptionUtil.encrypt(message,necessarydata.getTo_public_key());
//                        Log.d("MESSAGE",mtos);
                        mess.setContent(mtos);
                        mess.setFromID(necessarydata.getUname());
                        mess.setToID(necessarydata.getTo());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        sendMessage(om.writeValueAsString(mess));
                        dbhelper.insertChatLog(necessarydata.getTo(),necessarydata.getUname(),message);
                        addSendMessageToChat(msgctr, message);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(ChatActivity.this,"Sent",Toast.LENGTH_SHORT).show();
                    messageComp.setText("");
                }
                else {
                    Toast.makeText(ChatActivity.this, "Empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void loadContent(LinearLayout msgctr, NecessaryData nd) {
        List<MsgFormat> chatLogs = dbhelper.getChatLogsByUsername(nd.getTo());

        for(MsgFormat obj : chatLogs){
            if(obj.getToID().equals(nd.getUname()))
            {
                addSendMessageToChat(msgctr,obj.getContent());
            }
            else {
                addRecievedMessageToChat(msgctr,obj.getContent());
            }
        }

    }





    private void addSendMessageToChat(LinearLayout msgctr, String msgText) {
        TextView msgtv = new TextView(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(20, 10, 50, 10);
        params.gravity = Gravity.END; // Align the TextView to the right side
        msgtv.setLayoutParams(params);

        // Set the message appearance
        msgtv.setBackgroundColor(Color.parseColor("#6216CF"));
        msgtv.setText(msgText);
        msgtv.setTextColor(Color.WHITE);
        msgtv.setTextSize(20);
        msgtv.setPadding(20, 20, 20, 20);
        msgtv.setGravity(Gravity.LEFT); // Align text inside TextView to the right

        // Ensure max width is half the container width
        msgctr.post(() -> {
            int parentWidth = msgctr.getWidth();
            msgtv.setMaxWidth(parentWidth / 2);
        });

        // Add the TextView to the message container
        msgctr.addView(msgtv);
    }

    private void addRecievedMessageToChat(LinearLayout msgctr, String msgText) {
        TextView msgtv = new TextView(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(20, 10, 50, 10);
        params.gravity = Gravity.START; // Align the TextView to the right side
        msgtv.setLayoutParams(params);

        // Set the message appearance
        msgtv.setBackgroundColor(Color.parseColor("#6216CF"));
        msgtv.setText(msgText);
        msgtv.setTextColor(Color.WHITE);
        msgtv.setTextSize(20);
        msgtv.setPadding(20, 20, 20, 20);
        msgtv.setGravity(Gravity.LEFT); // Align text inside TextView to the right

        // Ensure max width is half the container width
        msgctr.post(() -> {
            int parentWidth = msgctr.getWidth();
            msgtv.setMaxWidth(parentWidth / 2);
        });

        // Add the TextView to the message container
        msgctr.addView(msgtv);
    }

    private void connectWebSocket(String username,NecessaryData nd,LinearLayout msgctr) {
        URI uri = URI.create("ws://[2409:40f4:205b:c5d0:2fd9:1576:a16:c73a]:8080/chat?username="+username);

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                Log.d("WebSocket", "Connection opened");
            }

            @Override
            public void onMessage(String message) {
                Log.d("WebSocket", "Received: " + message);
                try {
                    MsgFormat mess = new ObjectMapper().readValue(message, MsgFormat.class);
                    String msg = RSAEncryptionUtil.decrypt(mess.getContent(),nd.getPrivate_key());
                    System.out.println(msg);
                    dbhelper.insertChatLog(nd.getTo(),nd.getTo(),msg);
                    Log.d("WebSocket", "Received: " + msg);

                    // Run UI update on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addRecievedMessageToChat(msgctr, msg);
                        }
                    });
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d("WebSocket", "Closed: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.d("WebSocket", "Error: " + ex.getMessage());
            }
        };
        webSocketClient.connect();
    }

    private void sendMessage(String message) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(message);
        }
    }

    @Override
    public void onBackPressed() {
        if (webSocketClient != null) {
            webSocketClient.close(1000, "User exited");  // Close the WebSocket with a code and reason
        }
        super.onBackPressed();  // Finish the activity
    }
}

