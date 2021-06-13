package com.example.sharecar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MessagingAdapter extends RecyclerView.Adapter<MessagingAdapter.MessagingHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private ArrayList<Message> messages;

    public MessagingAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @NotNull
    @Override
    public MessagingHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT)
            return new MessagingHolder(LayoutInflater.from(context).inflate(R.layout.message_item_right, parent, false));
        else
            return new MessagingHolder(LayoutInflater.from(context).inflate(R.layout.message_item_left, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessagingAdapter.MessagingHolder holder, int position) {

        Message message = messages.get(position);

        holder.message_time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", message.getMessageTime()));
        holder.message_text.setText(message.getMessageText());
        holder.messege_user.setText(message.getSender().getName());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessagingHolder extends RecyclerView.ViewHolder {

        TextView messege_user, message_text, message_time;

        public MessagingHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            messege_user = itemView.findViewById(R.id.message_user);
            message_text = itemView.findViewById(R.id.message_text);
            message_time = itemView.findViewById(R.id.message_time);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getSender().getUserId().equals(Tools.userID))
            return MSG_TYPE_RIGHT;
        else
            return MSG_TYPE_LEFT;

    }
}