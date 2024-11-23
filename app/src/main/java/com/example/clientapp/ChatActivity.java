package com.example.clientapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        NecessaryData necessarydata = getIntent().getParcelableExtra("necessarydata");

        TextView to = findViewById(R.id.to);
        to.setText(necessarydata.getTo());

        LinearLayout msgctr = findViewById(R.id.msgctr);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width: match parent
                LinearLayout.LayoutParams.WRAP_CONTENT  // Height: wrap content
        );

        Button send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView msgtv = new TextView(getApplicationContext());
                params.setMargins(20, 10, 20, 10);
                msgtv.setLayoutParams(params);
                msgtv.setBackgroundColor(Color.parseColor("#6216CF"));


                // Set the message text
                EditText message = findViewById(R.id.msg);
                msgtv.setText(message.getText().toString().trim());



                msgtv.setTextColor(Color.WHITE);
                msgtv.setTextSize(20);
                msgtv.setGravity(Gravity.LEFT);
                msgtv.setPadding(20, 20, 20, 20);

                msgctr.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Remove the listener to avoid multiple calls
                        msgctr.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        int parentWidth = msgctr.getWidth();
                        int maxWidth = parentWidth / 2;
                        msgtv.setMaxWidth(maxWidth);  // Set max width dynamically
                    }
                });


                // Add the TextView to the LinearLayout
                msgctr.addView(msgtv);
            }

        });



    }
}
