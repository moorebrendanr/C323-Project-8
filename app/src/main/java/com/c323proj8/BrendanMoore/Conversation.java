package com.c323proj8.BrendanMoore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * This activity shows a conversation between two people.
 */
public class Conversation extends AppCompatActivity {
    private MessagesDatabase db;
    private String contactName;
    private String currentUser;
    private List<Message> messages;
    private ConversationArrayAdapter adapter;

    /**
     * Create the activity
     * @param savedInstanceState the saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Intent startIntent = getIntent();
        contactName = startIntent.getStringExtra("NAME");
        currentUser = startIntent.getStringExtra("CURRENT_USER");
        setTitle(contactName);

        db = new MessagesDatabase(this);

        ListView listView = findViewById(R.id.listView_conversation);
        messages = db.getMessages(currentUser, contactName);
        adapter = new ConversationArrayAdapter(messages);
        listView.setAdapter(adapter);
    }

    /**
     * Callback function for the send button. Add message to database and display it
     * @param view the button
     */
    public void sendMessage(View view) {
        EditText editText = findViewById(R.id.editText_conversation);
        String messageText = editText.getText().toString();
        if (!messageText.equals("")) {
            editText.setText(null);
            db.insert(currentUser, contactName, messageText);
            messages.add(new Message(messageText, currentUser, contactName));
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * An extended ArrayAdapter.
     */
    private class ConversationArrayAdapter extends ArrayAdapter<Message> {
        private List<Message> messages;

        /**
         * Construct a ConversationArrayAdapter
         * @param messages a list of Messages
         */
        public ConversationArrayAdapter(@NonNull List<Message> messages) {
            super(Conversation.this, R.layout.sent_message, messages);
            this.messages = messages;
        }

        /**
         * Get the view for a list item
         * @param position the position of the item
         * @param convertView the cached view
         * @param parent the parent
         * @return the View
         */
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Message message = messages.get(position);
            if (convertView == null) {
                if (message.sentBy.equals(currentUser)) {
                    convertView = getLayoutInflater().inflate(R.layout.sent_message, parent, false);
                } else {
                    convertView = getLayoutInflater().inflate(R.layout.received_message, parent, false);
                }
            }

            TextView textView = convertView.findViewById(R.id.textView_message);
            textView.setText(messages.get(position).text);
            return convertView;
        }
    }
}
