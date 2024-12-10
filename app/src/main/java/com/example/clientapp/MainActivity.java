package com.example.clientapp;

import static com.example.clientapp.NetworkUtils.computeSHA256;
import static com.example.clientapp.NetworkUtils.generateKeyPair;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

class UserData implements Parcelable {
    String email,pwd,uname,otp,name,public_key=" ";
    boolean active;

    protected UserData(Parcel in) {
        email = in.readString();
        pwd = in.readString();
        uname = in.readString();
        otp = in.readString();
        name = in.readString();
        public_key = in.readString();
        active = in.readByte() != 0;
    }

    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };

    public UserData() {

    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public UserData(String email, String pwd, String uname, String otp, String name, String public_key, boolean active) {
        this.email = email;
        this.pwd = pwd;
        this.uname = uname;
        this.otp = otp;
        this.name = name;
        this.public_key = public_key;
        this.active = active;
    }

    public UserData(String email, String pwd, String uname, String otp, String name) {
        this.email = email;
        this.pwd = pwd;
        this.uname = uname;
        this.otp = otp;
        this.name = name;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }



    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(pwd);
        parcel.writeString(uname);
        parcel.writeString(otp);
        parcel.writeString(name);
        parcel.writeString(public_key);
        parcel.writeByte((byte) (active ? 1 : 0));
    }
}

class NecessaryData implements Parcelable{

    String uname,name,email,pwd,public_key,private_key,to,to_public_key;

    public NecessaryData() {
    }

    public String getUname() {
        return uname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public NecessaryData(String email, String pwd, String public_key, String private_key) {
        this.email = email;
        this.pwd = pwd;
        this.public_key = public_key;
        this.private_key = private_key;
    }

    public NecessaryData( String email, String pwd, String public_key, String private_key ,String uname, String name) {
        this.uname = uname;
        this.name = name;
        this.email = email;
        this.pwd = pwd;
        this.public_key = public_key;
        this.private_key = private_key;
    }

    public NecessaryData(String uname, String email, String pwd, String public_key, String private_key, String to, String to_public_key) {
        this.uname = uname;
        this.email = email;
        this.pwd = pwd;
        this.public_key = public_key;
        this.private_key = private_key;
        this.to = to;
        this.to_public_key = to_public_key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public String getPrivate_key() {
        return private_key;
    }

    public void setPrivate_key(String private_key) {
        this.private_key = private_key;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTo_public_key() {
        return to_public_key;
    }

    public void setTo_public_key(String to_public_key) {
        this.to_public_key = to_public_key;
    }

    protected NecessaryData(Parcel in) {
        uname = in.readString();
        email = in.readString();
        pwd = in.readString();
        public_key = in.readString();
        private_key = in.readString();
        to = in.readString();
        to_public_key = in.readString();
    }

    public static final Creator<NecessaryData> CREATOR = new Creator<NecessaryData>() {
        @Override
        public NecessaryData createFromParcel(Parcel in) {
            return new NecessaryData(in);
        }

        @Override
        public NecessaryData[] newArray(int size) {
            return new NecessaryData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(uname);
        parcel.writeString(email);
        parcel.writeString(pwd);
        parcel.writeString(public_key);
        parcel.writeString(private_key);
        parcel.writeString(to);
        parcel.writeString(to_public_key);
    }
}

class UpdateData {
    String email,pwd,public_key;

    public UpdateData() {

    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    int active;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public UpdateData(String email, String pwd, String public_key) {
        this.email = email;
        this.pwd = pwd;
        this.public_key = public_key;
    }
}



public class MainActivity extends AppCompatActivity {

    public NecessaryData Available() {
        ChatAppDatabaseHelper dbHelper = new ChatAppDatabaseHelper(getApplicationContext());
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            // Query to get the first row from the Credentials table
            cursor = db.rawQuery("SELECT * FROM " + ChatAppDatabaseHelper.TABLE_CREDENTIALS + " LIMIT 1", null);

            if (cursor.moveToFirst()) {

                String email = cursor.getString(cursor.getColumnIndexOrThrow(ChatAppDatabaseHelper.COLUMN_EMAIL));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(ChatAppDatabaseHelper.COLUMN_PASSWORD));
                String privateKey = cursor.getString(cursor.getColumnIndexOrThrow(ChatAppDatabaseHelper.COLUMN_PRIVATE_KEY));
                String publicKey = cursor.getString(cursor.getColumnIndexOrThrow(ChatAppDatabaseHelper.COLUMN_PUBLIC_KEY));
                String uname = cursor.getString(cursor.getColumnIndexOrThrow(ChatAppDatabaseHelper.COLUMN_USER_NAME));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ChatAppDatabaseHelper.COLUMN_USER_NAME));

                NecessaryData nd = new NecessaryData();
                nd.setName(name);
                nd.setUname(uname);
                nd.setPublic_key(publicKey);
                nd.setPrivate_key(privateKey);
                nd.setPwd(password);
                nd.setEmail(email);

                return nd;
            }
            else {
                return null;  // No data found in the table
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;  // Database or query error
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();  // Close the database
        }
    }


    public boolean replaceCredentials(String email, String password, String privateKey,String publicKey,String uname,String name) {
        // Create or open the database
        ChatAppDatabaseHelper dbHelper = new ChatAppDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Clear all data in the table
            db.delete(ChatAppDatabaseHelper.TABLE_CREDENTIALS, null, null);

            // Prepare the values to insert
            ContentValues values = new ContentValues();
            values.put(ChatAppDatabaseHelper.COLUMN_USER_NAME, uname);
            values.put(ChatAppDatabaseHelper.COLUMN_NAME, name);
            values.put(ChatAppDatabaseHelper.COLUMN_EMAIL, email);
            values.put(ChatAppDatabaseHelper.COLUMN_PASSWORD, password);
            values.put(ChatAppDatabaseHelper.COLUMN_PRIVATE_KEY, privateKey);
            values.put(ChatAppDatabaseHelper.COLUMN_PUBLIC_KEY, publicKey);

            // Insert the new row
            long result = db.insert(ChatAppDatabaseHelper.TABLE_CREDENTIALS, null, values);

            // Check the result to confirm success
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            // Close the database
            db.close();
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NecessaryData nd = Available();
        if(nd!=null)
        {
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            intent.putExtra("necessarydata", nd);
            startActivity(intent);
            finish();
        }
        else {

            setContentView(R.layout.activity_main);

            // Example: Set a click listener on a button
            Button btn = findViewById(R.id.submit);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EditText emailComp = findViewById(R.id.email);
                    String email = emailComp.getText().toString().trim();

                    EditText pwdComp = findViewById(R.id.cpwd);
                    String pwd = pwdComp.getText().toString().trim();


                    String url = "http://[2409:40f4:2058:e6db:e422:542a:7af0:4dcd]:8080/login";
                    ObjectMapper om = new ObjectMapper();
                    String jsonInput = null;
                    try {
                        jsonInput = om.writeValueAsString(new UserData(email, computeSHA256(pwd), null, null, null));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }


                    NetworkUtils.makePostRequest(url, jsonInput, result -> {

                        if (!result.equals("User Not Registered") && !result.equals("Wrong Password Entered")) {
                            String[] keys = generateKeyPair();
                            String[] names = new ObjectMapper().readValue(result,String[].class);
                            if (replaceCredentials(email, computeSHA256(pwd), keys[0], keys[1],names[0],names[1])) {
                                Toast.makeText(MainActivity.this, "Updated Locally", Toast.LENGTH_LONG).show();
                                System.out.println(keys[0]);
                                UpdateData userdata = new UpdateData();
                                userdata.setEmail(email);
                                userdata.setPwd(computeSHA256(pwd));
                                userdata.setPublic_key(keys[1]);

                                NetworkUtils.makePostRequest("http://[2409:40f4:2058:e6db:e422:542a:7af0:4dcd]:8080/updateCredentials", om.writeValueAsString(userdata), res -> {
                                    if (res.equals("User Updated Successfully!!")) {
                                        Toast.makeText(MainActivity.this, "Updated Server DB", Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(MainActivity.this, "Welcome " + result, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, ListActivity.class);
                                        NecessaryData nd = new NecessaryData();
                                        nd.setUname(names[0]);
                                        nd.setName(names[1]);
                                        nd.setEmail(userdata.getEmail());
                                        nd.setPrivate_key(keys[0]);
                                        nd.setPublic_key(userdata.getPublic_key());
                                        nd.setPwd(userdata.getPwd());
                                        intent.putExtra("necessarydata", nd);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to Update Server DB", Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else {
                                Toast.makeText(MainActivity.this, "Failed to Update Locally", Toast.LENGTH_LONG).show();
                            }


                        } else {
                            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });

            Button button = findViewById(R.id.signup);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Intent to redirect to SecondActivity
                    Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                    startActivity(intent);  // Start the second activity
                    finish();
                }
            });
        }

    }
}
