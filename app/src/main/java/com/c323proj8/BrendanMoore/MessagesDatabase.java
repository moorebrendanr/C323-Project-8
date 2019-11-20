package com.c323proj8.BrendanMoore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the Messages database.
 */
public class MessagesDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "messages.db";
    private static final String TABLE_NAME = "Message";
    private static final String COLUMN_ID = "id";
    private static final int COLUMN_ID_INDEX = 0;
    private static final String COLUMN_FROM = "FromWho";
    private static final int COLUMN_FROM_INDEX = 1;
    private static final String COLUMN_TO = "ToWho";
    private static final int COLUMN_TO_INDEX = 2;
    private static final String COLUMN_MESSAGE = "MessageText";
    private static final int COLUMN_MESSAGE_INDEX = 3;

    /**
     * Construct a MessagesDatabase
     * @param context the context
     */
    public MessagesDatabase(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    /**
     * Create the database
     * @param db the database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+" ("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_FROM+" TEXT, "+COLUMN_TO+" TEXT, "+COLUMN_MESSAGE+" TEXT)");
    }

    /**
     * Upgrade the database
     * @param db the database
     * @param oldVersion the old version number
     * @param newVersion the new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    /**
     * Get the total number of items in the database
     * @return the count
     */
    public int getCount() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor allData = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        int count = allData.getCount();
        allData.close();
        return count;
    }

    /**
     * Insert a new row into the table.
     * @param fromWho from who?
     * @param toWho to who?
     * @param messageText message text
     * @return true if inserted
     */
    public boolean insert(String fromWho, String toWho, String messageText) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FROM, fromWho);
        contentValues.put(COLUMN_TO, toWho);
        contentValues.put(COLUMN_MESSAGE, messageText);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    /**
     * Get all the messages between two users.
     * @param currentUser the currently signed in user
     * @param contactName the other user in the conversation
     * @return a List of Messages
     */
    public List<Message> getMessages(String currentUser, String contactName) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor sentMessages = db.rawQuery(
                "SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_FROM+"=? AND "+COLUMN_TO+"=?",
                new String[] {currentUser, contactName});
        Cursor receivedMessages = db.rawQuery(
                "SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_FROM+"=? AND "+COLUMN_TO+"=?",
                new String[] {contactName, currentUser}
        );
        List<Message> messages = new ArrayList<>();
        // Iterate through sent and received messages, adding them to the List in the correct order.
        if (sentMessages.getCount() > 0 && receivedMessages.getCount() > 0) {
            sentMessages.moveToFirst(); receivedMessages.moveToFirst();
            // Iterate until one cursor reaches the end
            while (!(sentMessages.isAfterLast() || receivedMessages.isAfterLast())) {
                int sentID = sentMessages.getInt(COLUMN_ID_INDEX);
                int receivedID = receivedMessages.getInt(COLUMN_ID_INDEX);
                if (sentID < receivedID) {
                    Message sentMessage = new Message(
                            sentMessages.getString(COLUMN_MESSAGE_INDEX),
                            sentMessages.getString(COLUMN_FROM_INDEX),
                            sentMessages.getString(COLUMN_TO_INDEX)
                    );
                    messages.add(sentMessage);
                    sentMessages.moveToNext();
                } else {
                    Message receivedMessage = new Message(
                            receivedMessages.getString(COLUMN_MESSAGE_INDEX),
                            receivedMessages.getString(COLUMN_FROM_INDEX),
                            receivedMessages.getString(COLUMN_TO_INDEX)
                    );
                    messages.add(receivedMessage);
                    receivedMessages.moveToNext();
                }
            }
            Cursor remainder = null;
            if (!sentMessages.isAfterLast()) {
                remainder = sentMessages;
            } else if (!receivedMessages.isAfterLast()) {
                remainder = receivedMessages;
            }

            // Iterate through the remainder of the messages
            if (remainder != null) {
                do {
                    Message message = new Message(
                            remainder.getString(COLUMN_MESSAGE_INDEX),
                            remainder.getString(COLUMN_FROM_INDEX),
                            remainder.getString(COLUMN_TO_INDEX)
                    );
                    messages.add(message);
                } while (remainder.moveToNext());
                remainder.close();
            }
        // Iterate through sent messages if there are no received messages
        } else if (sentMessages.getCount() > 0) {
            while (sentMessages.moveToNext()) {
                Message message = new Message(
                        sentMessages.getString(COLUMN_MESSAGE_INDEX),
                        sentMessages.getString(COLUMN_FROM_INDEX),
                        sentMessages.getString(COLUMN_TO_INDEX)
                );
                messages.add(message);
            }
        // Iterate through received messages if there are no sent messages
        } else if (receivedMessages.getCount() > 0) {
            while (receivedMessages.moveToNext()) {
                Message message = new Message(
                        receivedMessages.getString(COLUMN_MESSAGE_INDEX),
                        receivedMessages.getString(COLUMN_FROM_INDEX),
                        receivedMessages.getString(COLUMN_TO_INDEX)
                );
                messages.add(message);
            }
        }
        sentMessages.close();
        receivedMessages.close();
        return messages;
    }
}
