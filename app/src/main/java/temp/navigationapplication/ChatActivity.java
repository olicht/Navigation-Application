package temp.navigationapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChatActivity extends AppCompatActivity {

    private FirebaseListAdapter<ChatMessage> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        this.setTitle("Chat");
        // Load chat room contents
        displayChatMessages();

        //set the send button settings:
        FloatingActionButton sendBtn = findViewById(R.id.fab);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);
                // Read the input field and push a new instance of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("messages")
                        .push()
                        .setValue(new ChatMessage(FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getDisplayName(), input.getText().toString())
                        );
                // Clear the input
                input.setText("");
            }
        });

    }

    private void displayChatMessages() {

        ListView listOfMessages = (ListView) findViewById(R.id.list_of_messages);

        Query query = FirebaseDatabase.getInstance().getReference().child("messages")
                .limitToLast(20);

        FirebaseListOptions<ChatMessage> listOptions = new FirebaseListOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .setLayout(R.layout.message)
                .build();

        listAdapter = new FirebaseListAdapter<ChatMessage>(listOptions) {

            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));

            }

        };
        listAdapter.startListening();
        listOfMessages.setAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu mainMenu) {
        getMenuInflater().inflate(R.menu.main_menu, mainMenu);
        return true;
    }


}
