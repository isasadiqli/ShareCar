package com.example.sharecar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestRecycleViewAdapter extends RecyclerView.Adapter<RequestRecycleViewAdapter.RequestRecycleViewHolder> {

    private Context context;
    private HashMap<String, User> pendingUsers;
    private ArrayList<String> cities;
    private String tripKey;

    private ArrayList<User> users;
    private ArrayList<String> keys;

    public RequestRecycleViewAdapter(Context context, HashMap<String, User> pendingUsers,
                                     ArrayList<String> cities, String tripKey) {
        this.context = context;
        this.pendingUsers = pendingUsers;
        this.cities = cities;
        this.tripKey = tripKey;
    }


    @NonNull
    @NotNull
    @Override
    public RequestRecycleViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.requests_item, parent, false);
        return new RequestRecycleViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull RequestRecycleViewAdapter.RequestRecycleViewHolder holder, int position) {
        users = new ArrayList<>();
        keys = new ArrayList<>();

        for (Map.Entry<String, User> entry :
                pendingUsers.entrySet()) {
            users.add(entry.getValue());
            keys.add(entry.getKey());
        }

        User user = users.get(position);
        String key = keys.get(position);

        holder.request_sender_name.setText(user.getName() + " " + user.getSurname());
        holder.trip_cities.setText(cities.get(0) + " to " + cities.get(1));

        holder.accept_pending_user.setOnClickListener(v -> {

            DatabaseReference addRequestToTripDB = FirebaseDatabase.getInstance().getReference("Trips")
                    .child(tripKey);

            addRequestToTripDB.child("passengers").child(key).setValue(user);
            addRequestToTripDB.child("pendingUsers").child(key).removeValue();

            removeAt(position);

            if(pendingUsers.size() == 0)
                ((Activity)context).finish();
        });
    }

    public void removeAt(int position) {
        pendingUsers.remove(keys.get(position));
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, pendingUsers.size());
    }

    @Override
    public int getItemCount() {
        return pendingUsers.size();
    }

    public static class RequestRecycleViewHolder extends RecyclerView.ViewHolder {

        TextView request_sender_name, trip_cities;
        Button accept_pending_user;

        public RequestRecycleViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            request_sender_name = itemView.findViewById(R.id.requst_sender_name);
            trip_cities = itemView.findViewById(R.id.trip_cities);

            accept_pending_user = itemView.findViewById(R.id.accept_pending_user);
        }
    }
}
