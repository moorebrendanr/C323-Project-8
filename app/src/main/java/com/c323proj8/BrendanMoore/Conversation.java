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

public class Conversation extends AppCompatActivity {
    private MessagesDatabase db;
    private String contactName;
    private String currentUser;
    private List<Message> messages;
    private ConversationArrayAdapter adapter;

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

    private class ConversationArrayAdapter extends ArrayAdapter<Message> {
        private List<Message> messages;

        public ConversationArrayAdapter(@NonNull List<Message> messages) {
            super(Conversation.this, R.layout.sent_message, messages);
            this.messages = messages;
        }

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
