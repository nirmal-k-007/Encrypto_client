package com.example.clientapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//class UserData implements Parcelable {
//    String email,pwd,uname,otp,name,public_key=" ";
//    boolean active;
//
//    protected UserData(Parcel in) {
//        email = in.readString();
//        pwd = in.readString();
//        uname = in.readString();
//        otp = in.readString();
//        name = in.readString();
//        public_key = in.readString();
//        active = in.readByte() != 0;
//    }
//
//    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
//        @Override
//        public UserData createFromParcel(Parcel in) {
//            return new UserData(in);
//        }
//
//        @Override
//        public UserData[] newArray(int size) {
//            return new UserData[size];
//        }
//    };
//
//    public boolean getActive() {
//        return active;
//    }
//
//    public void setActive(boolean active) {
//        this.active = active;
//    }
//
//    public UserData(String email, String pwd, String uname, String otp, String name, String public_key, boolean active) {
//        this.email = email;
//        this.pwd = pwd;
//        this.uname = uname;
//        this.otp = otp;
//        this.name = name;
//        this.public_key = public_key;
//        this.active = active;
//    }
//
//    public UserData(String email, String pwd, String uname, String otp, String name) {
//        this.email = email;
//        this.pwd = pwd;
//        this.uname = uname;
//        this.otp = otp;
//        this.name = name;
//    }
//
//
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPublic_key() {
//        return public_key;
//    }
//
//    public void setPublic_key(String public_key) {
//        this.public_key = public_key;
//    }
//
//
//
//    public String getPwd() {
//        return pwd;
//    }
//
//    public void setPwd(String pwd) {
//        this.pwd = pwd;
//    }
//
//    public String getUname() {
//        return uname;
//    }
//
//    public void setUname(String uname) {
//        this.uname = uname;
//    }
//
//    public String getOtp() {
//        return otp;
//    }
//
//    public void setOtp(String otp) {
//        this.otp = otp;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(@NonNull Parcel parcel, int i) {
//        parcel.writeString(email);
//        parcel.writeString(pwd);
//        parcel.writeString(uname);
//        parcel.writeString(otp);
//        parcel.writeString(name);
//        parcel.writeString(public_key);
//        parcel.writeByte((byte) (active ? 1 : 0));
//    }
//}


public class SignUpActivity extends AppCompatActivity {


    int op=0;
    UserData userdata;
    static ObjectMapper om = new ObjectMapper();


    void verifyOTP(String otp) throws JsonProcessingException {
        if(!otp.isEmpty()) {
            userdata.setOtp(otp);
            String obj = new String(om.writeValueAsString(userdata));
            NetworkUtils.makePostRequest("http://[2409:40f4:205b:c5d0:2fd9:1576:a16:c73a]:8080/verification", obj, result -> {
                Toast.makeText(SignUpActivity.this, result, Toast.LENGTH_LONG).show();
            });
        }
        else{
            Toast.makeText(SignUpActivity.this, "OTP Box cannot be Empty", Toast.LENGTH_LONG).show();
        }
    }


    void sendOTP(String email) throws JsonProcessingException
    {
        Toast.makeText(SignUpActivity.this, "Generating OTP", Toast.LENGTH_LONG).show();
        if(!email.isEmpty()) {
            userdata = new UserData(email,null,null,null,null);
            String obj = new String(om.writeValueAsString(userdata));
            NetworkUtils.makePostRequest("http://[2409:40f4:205b:c5d0:2fd9:1576:a16:c73a]:8080/generation", obj, result -> {
                if(result.equals("Generated"))
                {
                    Toast.makeText(SignUpActivity.this, "Otp Sent to " + email, Toast.LENGTH_LONG).show();
                    EditText emailComp = findViewById(R.id.email);
                    emailComp.setEnabled(false);
                    EditText otp = findViewById(R.id.otp);
                    otp.setVisibility(View.VISIBLE);
                    op=1;
                } else if (result.equals("Error")) {
                    Toast.makeText(SignUpActivity.this, "Some Error Occured", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignUpActivity.this, result, Toast.LENGTH_LONG).show();
                }
            });
        }
        else{
            Toast.makeText(SignUpActivity.this, "Email Box cannot be Empty", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity); // Set the main layout

        Button btn = findViewById(R.id.signup);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = findViewById(R.id.email);
                if(op==0)
                {
                    try {
                        sendOTP(email.getText().toString().trim());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
                else if (op==1) {
                    EditText otp = findViewById(R.id.otp);
                    try {
                        verifyOTP(otp.getText().toString().trim());
                        Intent intent = new Intent(SignUpActivity.this,RegisterActivity.class);
                        UserData userData = new UserData(email.getText().toString().trim(),null,null,null,null);
                        intent.putExtra("userdata",userData);
                        startActivity(intent);
                        finish();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });



    }

}
