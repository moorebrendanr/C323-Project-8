package com.c323proj8.BrendanMoore;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public class ContactsLoader implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String[] FROM_COLUMNS = {Contacts.DISPLAY_NAME_PRIMARY};
    private static final int[] TO_IDS = {android.R.id.text1};
    private static final String[] PROJECTION = {
            Contacts._ID,
            Contacts.LOOKUP_KEY,
            Contacts.DISPLAY_NAME_PRIMARY
    };
    private ContactsCursorAdapter cursorAdapter;
    private Context context;
    private LoaderManager loaderManager;

    private class ContactsCursorAdapter extends SimpleCursorAdapter {
        ContactsCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    Cursor cursor = ContactsLoader.this.context.getContentResolver().query(
                            Contacts.CONTENT_URI,
                            PROJECTION,
                            Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?",
                            new String[] { constraint + "%" },
                            Contacts.DISPLAY_NAME_PRIMARY + " ASC"
                    );

                    results.values = cursor;
                    if (cursor != null) {
                        results.count = cursor.getCount();
                    } else {
                        results.count = 0;
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    cursorAdapter.swapCursor((Cursor) results.values);
                }
            };
        }
    }

    public ContactsLoader(Context context, ListView listView,
                          LoaderManager loaderManager, int layout) {
        this.context = context;
        this.loaderManager = loaderManager;
        cursorAdapter = new ContactsCursorAdapter(
                context,
                layout,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        listView.setAdapter(cursorAdapter);
    }

    public ContactsLoader(Context context, final AutoCompleteTextView acTextView,
                          LoaderManager loaderManager, int layout) {
        this.context = context;
        this.loaderManager = loaderManager;
//        this.selection = selection;
        cursorAdapter = new ContactsCursorAdapter(
                context,
                layout,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        acTextView.setAdapter(cursorAdapter);
        acTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        acTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CursorAdapter adapter = (CursorAdapter) parent.getAdapter();
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                acTextView.setText(cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY)));
            }
        });
    }

    public void load() {
        loaderManager.initLoader(0, null, this);
    }

    private void filter(CharSequence query) {
        cursorAdapter.getFilter().filter(query.toString());
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(
                context,
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
