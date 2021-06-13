package com.example.sharecar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

public class TripsRecyclerViewAdapter extends RecyclerView.Adapter<TripsRecyclerViewAdapter.RecyclerViewHolder> {

    Context context;
    ArrayList<Trip> trips;

    int mExpandedPosition = -1;
    int previousExpandedPosition = -1;
    static int rateClicked = 0;
    boolean completedTrip;

    public TripsRecyclerViewAdapter(Context context, ArrayList<Trip> trips, boolean completedTrip) {
        this.context = context;
        this.trips = trips;
        this.completedTrip = completedTrip;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trip_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull TripsRecyclerViewAdapter.RecyclerViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.start_city.setText(trip.getCities().get(0));
        holder.destination_city.setText(trip.getCities().get(1));
        holder.date.setText(trip.getDate().getDay() + "."
                + trip.getDate().getMonth() + "." + trip.getDate().getYear());
        holder.time.setText(trip.getDate().getHour() + ":" + trip.getDate().getMinute());
        holder.trip_owner_name.setText(trip.getTripSharer().getName() + " " + trip.getTripSharer().getSurname());
        holder.cost.setText(trip.getCost() + "₺");

        holder.accepted_passengers.setText("");
        if (trip.getPassengers() != null) {
            StringBuilder acceptedPassengersList = new StringBuilder();
            for (Map.Entry<String, User> entry :
                    trip.getPassengers().entrySet()) {
                acceptedPassengersList.append(entry.getValue().getName()).append(" ").append(entry.getValue().getSurname()).append("\n");
            }

            holder.accepted_passengers.setText(acceptedPassengersList);
        }

        final boolean isExpanded = position == mExpandedPosition;
        holder.trip_details_layout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        if (!completedTrip) {
            if (!trip.getTripSharer().getUserId().equals(Tools.userID)) {
                holder.sendJoinRequest.setVisibility(View.VISIBLE);
                holder.showRequests.setVisibility(View.GONE);
            } else {
                holder.sendJoinRequest.setVisibility(View.GONE);
                holder.showRequests.setVisibility(View.VISIBLE);
            }
        } else {
            holder.rate_info_layout.setVisibility(View.VISIBLE);

            if (trip.getTripSharer().getUserId().equals(Tools.userID)) {
                holder.rate_layout.setVisibility(View.VISIBLE);
            } else if (trip.getPassengers() != null) {
                if (trip.getPassengers().containsKey(Tools.userID)) {
                    holder.rate_layout.setVisibility(View.VISIBLE);
                } else
                    holder.rate_layout.setVisibility(View.GONE);
            } else
                holder.rate_layout.setVisibility(View.GONE);


            float avarageRate = 0;
            if (trip.getRates() != null) {
                for (Map.Entry<String, Integer> entry :
                        trip.getRates().entrySet()) {
                    avarageRate += entry.getValue();
                }
                avarageRate = avarageRate / trip.getRates().size();
            }

            holder.rate_as_number.setText(new DecimalFormat("##.#").format(avarageRate) + "/5");
        }


        if (isExpanded) {
            previousExpandedPosition = position;

        }

        holder.itemView.setOnClickListener(v -> {
            mExpandedPosition = isExpanded ? -1 : position;
            notifyItemChanged(previousExpandedPosition);
            notifyItemChanged(position);

        });

        if (trip.getPendingUsers() == null) {
            holder.sendJoinRequest.setText("Join");
            if (trip.getPassengers() != null)
                if (trip.getPassengers().containsKey(Tools.userID))
                    holder.sendJoinRequest.setVisibility(View.GONE);
        } else {
            if (trip.getPendingUsers().containsKey(Tools.userID))
                holder.sendJoinRequest.setText("Requested");
            else {
                holder.sendJoinRequest.setText("Join");
                if (trip.getPassengers() != null)
                    if (trip.getPassengers().containsKey(Tools.userID))
                        holder.sendJoinRequest.setVisibility(View.GONE);
            }
        }

        holder.sendJoinRequest.setOnClickListener(v -> {

            String tripOwnerId = trip.getTripSharer().getUserId();
            String userId = Tools.userID;

            DatabaseReference addRequestToTripOwnerDB = FirebaseDatabase.getInstance().getReference("Users")
                    .child(tripOwnerId).child("requests").child(trip.getKey()).child(userId);

            DatabaseReference addRequestToTripDB = FirebaseDatabase.getInstance().getReference("Trips")
                    .child(trip.getKey()).child("pendingUsers").child(userId);

            if (trip.getPendingUsers() != null) {
                if (trip.getPendingUsers().containsKey(userId)) {

                    addRequestToTripOwnerDB.removeValue();
                    addRequestToTripDB.removeValue();

                    holder.sendJoinRequest.setText("Join");

                } else {

                    addRequestToTripOwnerDB.setValue(Tools.getUser());

                    addRequestToTripDB.setValue(Tools.getUser());

                    holder.sendJoinRequest.setText("Requested");
                }
            } else {

                addRequestToTripOwnerDB.setValue(Tools.getUser());

                addRequestToTripDB.setValue(Tools.getUser());

                holder.sendJoinRequest.setText("Requested");
            }

        });

        holder.showRequests.setOnClickListener(v -> {
            if (trip.getPendingUsers() != null) {
                Intent intent = new Intent(context, RequestsActivity.class);
                intent.putExtra("pendingUsers", trip.getPendingUsers());
                intent.putExtra("tripCities", trip.getCities());
                intent.putExtra("tripKey", trip.getKey());
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "There is no request yet", Toast.LENGTH_SHORT).show();
            }
        });

        holder.rate_button.setOnClickListener(v -> {
            rateClicked++;

            if (rateClicked % 2 != 0) {

                holder.rate_button.setText("Confirm");
                holder.ratingBar.setIsIndicator(false);

            } else {
                holder.rate_button.setText("Rate");
                holder.ratingBar.setIsIndicator(true);

                float rating = holder.ratingBar.getRating();

                DatabaseReference addRateDB = FirebaseDatabase.getInstance().getReference("CompletedTrips")
                        .child(trip.getKey()).child("rates").child(Tools.userID);

                addRateDB.setValue(Math.round(rating));

            }
        });

    }


    @Override
    public int getItemCount() {
        return trips.size();
    }

    public void filterList(ArrayList<Trip> filteredList) {
        trips = filteredList;
        notifyDataSetChanged();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView start_city, destination_city, date, time, trip_owner_name, accepted_passengers, cost, rate_as_number;
        LinearLayout trip_details_layout;
        Button sendJoinRequest, showRequests, rate_button;
        RatingBar ratingBar;
        LinearLayout rate_info_layout, rate_layout;

        public RecyclerViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            start_city = itemView.findViewById(R.id.start_city);
            destination_city = itemView.findViewById(R.id.destination_city);
            date = itemView.findViewById(R.id.date_item);
            time = itemView.findViewById(R.id.time_item);
            trip_owner_name = itemView.findViewById(R.id.trip_owner_name);
            accepted_passengers = itemView.findViewById(R.id.accepted_passengers);
            cost = itemView.findViewById(R.id.cost_item);

            trip_details_layout = itemView.findViewById(R.id.trip_details_layout);

            sendJoinRequest = itemView.findViewById(R.id.join_request_button);
            showRequests = itemView.findViewById(R.id.show_requests);

            rate_info_layout = itemView.findViewById(R.id.rate_info_layout);
            rate_layout = itemView.findViewById(R.id.rate_layout);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            rate_button = itemView.findViewById(R.id.rate_button);
            rate_as_number = itemView.findViewById(R.id.rate_as_number);

        }
    }
}
