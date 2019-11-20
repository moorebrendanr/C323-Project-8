package com.c323proj8.BrendanMoore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

/**
 * This activity shows a list of all contacts.
 */
public class ContactsList extends AppCompatActivity {

    /**
     * Create the activity.
     * @param savedInstanceState the saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        Intent intent = getIntent();
        String currentUser = intent.getStringExtra("CURRENT_USER");
        ListView listView = findViewById(R.id.listView_contactsList);
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        ContactsLoader contactsLoader = new ContactsLoader(
                this, listView, loaderManager,
                R.layout.contacts_list_item,
                currentUser
        );
        contactsLoader.load();
    }
}
