package com.example.gamedexter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn1);
        btn.setOnClickListener(v -> Toast.makeText(MainActivity.this, "Button clicked!", Toast.LENGTH_SHORT).show());
    }
}