package com.example.zenegep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ClientMenuActivity extends AppCompatActivity {
    public final static String serverIP = ClientActivity.serverIp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_menu);

        Button button = findViewById(R.id.button_zene_valaszt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), ClientMusicSelectActivity.class);
                startActivity(intent);
            }
        });

        button = findViewById(R.id.button_stat);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), StatisticsActivity.class);
                startActivity(intent);
            }
        });
        //TODO: layoutra a többi gombot berakni, illetve megcsinálni a clicklistenereket

    }
}
