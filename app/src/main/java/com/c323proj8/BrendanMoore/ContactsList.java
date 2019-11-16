package com.c323proj8.BrendanMoore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactsList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String[] FROM_COLUMNS = {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
    private static final int[] TO_IDS = {R.id.textView_contactsListItem};
    private static final String[] PROJECTION = {
            Contacts._ID,
            Contacts.LOOKUP_KEY,
            Contacts.DISPLAY_NAME_PRIMARY
    };
    private static final int CONTACT_ID_INDEX = 0;
    private static final int CONTACT_KEY_INDEX = 1;
    SimpleCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        ListView listView = findViewById(R.id.listView_contactsList);
        cursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.contacts_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        listView.setAdapter(cursorAdapter);

        LoaderManager.getInstance(this).initLoader(0, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(
                this,
                Contacts.CONTENT_URI,
                PROJECTION,
                null,
                new String[] {},
                Contacts.DISPLAY_NAME_PRIMARY + " ASC"
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
