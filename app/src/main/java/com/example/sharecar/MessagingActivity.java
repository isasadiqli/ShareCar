package com.example.sharecar;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MessagingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Message> messages;
    private User receiver, sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        receiver = (User) getIntent().getSerializableExtra("receiver");
        sender = Tools.getUser();

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(receiver.getName() + " " + receiver.getSurname());

        TypedValue typedValue = new TypedValue();
        this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        final int color = typedValue.data;
        actionBar.setBackgroundDrawable(new ColorDrawable(color));


        messages = new ArrayList<>();

        recyclerView = findViewById(R.id.message_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MessagingAdapter adapter = new MessagingAdapter(this, messages);
        recyclerView.setAdapter(adapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot ds :
                        snapshot.getChildren()) {
                    Message message = ds.getValue(Message.class);
                    assert message != null;
                    if ((message.getReceiver().getUserId().equals(receiver.getUserId())
                            && message.getSender().getUserId().equals(sender.getUserId()))
                            || (message.getReceiver().getUserId().equals(sender.getUserId())
                            && message.getSender().getUserId().equals(receiver.getUserId()))) {
                        messages.add(message);
                        layoutManager.scrollToPosition(messages.size() - 1);
                    }
                }
                System.out.println("menim" + messages);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    public void sendMessage(View view) {
        EditText input = findViewById(R.id.input);

        Message message = new Message(input.getText().toString(), receiver, sender);
        FirebaseDatabase.getInstance().getReference("Messages").push().setValue(message);

        input.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}