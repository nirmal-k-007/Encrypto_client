package com.example.clientapp;

import static com.example.clientapp.NetworkUtils.computeSHA256;
import static com.example.clientapp.NetworkUtils.generateKeyPair;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
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

public class RegisterActivity extends AppCompatActivity {

    public void insertCredentials(String email, String password, String privateKey) {
        ChatAppDatabaseHelper dbHelper = new ChatAppDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ChatAppDatabaseHelper.COLUMN_EMAIL, email);
        values.put(ChatAppDatabaseHelper.COLUMN_PASSWORD, password);
        values.put(ChatAppDatabaseHelper.COLUMN_PRIVATE_KEY, privateKey);

        long result = db.insert(ChatAppDatabaseHelper.TABLE_CREDENTIALS, null, values);
        if (result == -1) {
            System.out.println("Insert failed");
        } else {
            System.out.println("Insert successful");
        }
        db.close();
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText passwordComp = findViewById(R.id.pwd);
                String password = passwordComp.getText().toString().trim();
                EditText confirmpasswordComp = findViewById(R.id.cpwd);
                String confirmpassword = confirmpasswordComp.getText().toString().trim();
                EditText usernameComp = findViewById(R.id.uname);
                String username = usernameComp.getText().toString().trim();
                EditText nameComp = findViewById(R.id.name);
                String name = nameComp.getText().toString().trim();

                UserData userdata = getIntent().getParcelableExtra("userdata");

                if(!username.equals("") && !password.equals("") && !confirmpassword.equals("") && !name.equals(""))
                {
                    if(password.equals(confirmpassword))
                    {

                        userdata.setPwd(computeSHA256(password));
                        userdata.setUname(username);
                        userdata.setName(name);

                        String[] keys = generateKeyPair();


//                        String UserCredentials = null;
//                        try {
//                            UserCredentials = new ObjectMapper().writeValueAsString(new FileObject(userdata.getEmail(),userdata.getPwd(),keys[0]));
//                        } catch (JsonProcessingException ex) {
//                            throw new RuntimeException(ex);
//                        }
//                        Sign.writeToFile("ChatApp/AppData/UserCredentials.json",UserCredentials);

                        userdata.setPublic_key(keys[1]);
                        userdata.setActive(false);
                        String obj = null;
                        try {
                            obj = new ObjectMapper().writeValueAsString(userdata);
                        } catch (JsonProcessingException ex) {
                            throw new RuntimeException(ex);
                        }
                        NetworkUtils.makePostRequest("http://192.168.78.53:8080/registration", obj, result -> {
                            Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_LONG).show();
                            if(result.equals("User Created Successfully!!"))
                            {
                                ChatAppDatabaseHelper dbHelper = new ChatAppDatabaseHelper(getApplicationContext());
                                
                                Toast.makeText(RegisterActivity.this, "You have already been signed in\nClose App and Open again to enjoy!!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_LONG).show();
                            }

                        });

                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Username and Password are Required", Toast.LENGTH_LONG).show();
                }
            }
        });





    }
}
