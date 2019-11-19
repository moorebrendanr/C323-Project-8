package com.c323proj8.BrendanMoore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

public class SignIn extends AppCompatActivity {
    private AutoCompleteTextView acTextView;
    private Button buttonSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        acTextView = findViewById(R.id.autoCompleteTextView_signIn);
        buttonSignIn = findViewById(R.id.button_signIn);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, 1);
            } else {
                activateInputs();
            }
        }
    }

    public void signIn(View view) {
        startActivity(new Intent(this, ContactsList.class));
    }

    private void activateInputs() {
        acTextView.setEnabled(true);
        buttonSignIn.setEnabled(true);
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        ContactsLoader contactsLoader = new ContactsLoader(
                this,
                acTextView,
                loaderManager,
                android.R.layout.simple_dropdown_item_1line
        );
        contactsLoader.load();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                activateInputs();
            } else {
                Toast.makeText(this, "Please allow read contacts permission.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
