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
    private ContactsLoader contactsLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        startDBQueryService();

        // I spent ages getting this AutoCompleteTextView to work even though it's not required.
        // I just wanted this cool feature in my app. I hope you like it.
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

    private void startDBQueryService() {
        Intent intent = new Intent(this, DBQueryService.class);
        startService(intent);
    }

    public void signIn(View view) {
        if (contactsLoader != null && contactsLoader.isValidInput) {
            Intent intent = new Intent(this, ContactsList.class);
            intent.putExtra("CURRENT_USER", acTextView.getText().toString());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Not a valid contact name.", Toast.LENGTH_SHORT).show();
        }
    }

    private void activateInputs() {
        acTextView.setEnabled(true);
        buttonSignIn.setEnabled(true);
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        contactsLoader = new ContactsLoader(
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
