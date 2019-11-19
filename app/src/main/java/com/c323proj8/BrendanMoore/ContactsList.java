package com.c323proj8.BrendanMoore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;

import android.os.Bundle;
import android.widget.ListView;

public class ContactsList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        ListView listView = findViewById(R.id.listView_contactsList);
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        ContactsLoader contactsLoader = new ContactsLoader(
                this, listView, loaderManager,
                R.layout.contacts_list_item
        );
        contactsLoader.load();
    }
}
