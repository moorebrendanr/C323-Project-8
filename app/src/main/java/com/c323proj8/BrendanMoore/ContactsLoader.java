package com.c323proj8.BrendanMoore;

import android.content.Context;
import android.content.Intent;
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

/**
 * This class handles interactions with the Contacts Provider, as well as populating lists of
 * contacts.
 */
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
    boolean isValidInput = false;

    /**
     * An extended SimpleCursorAdapter
     */
    private class ContactsCursorAdapter extends SimpleCursorAdapter {
        ContactsCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        /**
         * Get the filter for this adapter.
         * @return a Filter
         */
        @Override
        public Filter getFilter() {
            return new Filter() {
                /**
                 * Perform filtering and return results
                 * @param constraint the filter query
                 * @return the results
                 */
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

                /**
                 * Publish the results
                 * @param constraint the filter query
                 * @param results the results
                 */
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    cursorAdapter.swapCursor((Cursor) results.values);
                }
            };
        }
    }

    /**
     * Construct a ContactsLoader.
     * @param context the context
     * @param listView the ListView to populate
     * @param loaderManager the LoaderManager
     * @param layout the list item layout
     * @param currentUser the currently signed in user
     */
    public ContactsLoader(Context context, ListView listView,
                          LoaderManager loaderManager, int layout, final String currentUser) {
        this.context = context;
        this.loaderManager = loaderManager;
        cursorAdapter = new ContactsCursorAdapter(
                context,
                layout,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Handle clicks on list items. Start Conversation activity.
             * @param parent the parent AdapterView
             * @param view the view that was clicked
             * @param position the position of the clicked item
             * @param id the id of the clicked item
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = ContactsLoader.this.context;
                CursorAdapter adapter = (CursorAdapter) parent.getAdapter();
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                String name = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY));
                Intent intent = new Intent(context, Conversation.class);
                intent.putExtra("NAME", name);
                intent.putExtra("CURRENT_USER", currentUser);
                context.startActivity(intent);
            }
        });
    }

    /**
     * Construct a ContactsLoader
     * @param context the context
     * @param acTextView the AutoCompleteTextView to populate
     * @param loaderManager the LoaderManager
     * @param layout the list item layout
     */
    public ContactsLoader(Context context, final AutoCompleteTextView acTextView,
                          LoaderManager loaderManager, int layout) {
        this.context = context;
        this.loaderManager = loaderManager;
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

            /**
             * Filter the suggestions as text is entered.
             * @param s the input text
             */
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
                isValidInput = verifyInput(s.toString());
            }
        });
        acTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Set the input text to the item which was clicked on.
             * @param parent the parent AdapterView
             * @param view the item which was clicked
             * @param position the position of the clicked item
             * @param id the id of the clicked item
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CursorAdapter adapter = (CursorAdapter) parent.getAdapter();
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                acTextView.setText(cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY)));
            }
        });
    }

    /**
     * Verify that the input name exists as a contact in the phone.
     * @param name the contact name
     * @return true if name is valid
     */
    private boolean verifyInput(String name) {
        Cursor cursor = context.getContentResolver().query(
                Contacts.CONTENT_URI,
                PROJECTION,
                Contacts.DISPLAY_NAME_PRIMARY + "=?",
                new String[] {name},
                null
        );

        if (cursor == null) {
            return false;
        }

        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    /**
     * Load the data.
     */
    public void load() {
        loaderManager.initLoader(0, null, this);
    }

    /**
     * Filter the data.
     * @param query the filter query
     */
    private void filter(CharSequence query) {
        cursorAdapter.getFilter().filter(query.toString());
    }

    /**
     * Create the Loader
     * @param id the id of the Loader
     * @param args optional args
     * @return the Loader
     */
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

    /**
     * Swap cursor when load is finished
     * @param loader the Loader
     * @param data the cursor
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    /**
     * Make cursor null on Loader reset
     * @param loader the Loader
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
