package com.c323proj8.BrendanMoore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class Conversation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Intent startIntent = getIntent();
        String name = startIntent.getStringExtra("NAME");
        setTitle(name);
    }
}
