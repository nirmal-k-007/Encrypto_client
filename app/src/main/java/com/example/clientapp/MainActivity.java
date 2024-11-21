package com.example.clientapp;

import static com.example.clientapp.NetworkUtils.computeSHA256;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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



public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


                String url = "http://192.168.78.53:8080/login";
                ObjectMapper om = new ObjectMapper();
                String jsonInput = null;
                try {
                    jsonInput = om.writeValueAsString(new UserData(email,computeSHA256(pwd),null,null,null));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }


                NetworkUtils.makePostRequest(url, jsonInput, result -> {
                    TextView textView = findViewById(R.id.txt);
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
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
