package com.example.clientapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ListActivity extends AppCompatActivity {
    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        NecessaryData necessarydata = getIntent().getParcelableExtra("necessarydata");

        TextView txt = findViewById(R.id.myid);
        txt.setText(necessarydata.getUname());

        Button search = findViewById(R.id.go);
        EditText usrname = findViewById(R.id.username);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkUtils.makePostRequest("http://192.168.221.53:8080/searchUser",usrname.getText().toString().trim(),res->{
                    if(!res.equals("No") && !res.equals("Error"))
                    {
                        Toast.makeText(ListActivity.this, "Found!", Toast.LENGTH_SHORT).show();
                        necessarydata.setTo(usrname.getText().toString().trim());
                        necessarydata.setTo_public_key(res);
                        Intent intent = new Intent(ListActivity.this,ChatActivity.class);
                        intent.putExtra("necessarydata",necessarydata);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(ListActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
