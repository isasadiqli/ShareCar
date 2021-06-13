package com.example.sharecar.ui.trips;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharecar.MDate;
import com.example.sharecar.NavigationActivity;
import com.example.sharecar.R;
import com.example.sharecar.Tools;
import com.example.sharecar.Trip;
import com.example.sharecar.TripsRecyclerViewAdapter;
import com.example.sharecar.databinding.FragmentTripsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class TripsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private TripsRecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Trip> trips;

    private TripsViewModel homeViewModel;
    private FragmentTripsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(TripsViewModel.class);

        binding = FragmentTripsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeViewModel.getText().observe(getViewLifecycleOwner(), s -> {
            NavigationActivity.binding.appBarNavigation.fab.setVisibility(View.VISIBLE);

            recyclerView = root.findViewById(R.id.trip_list);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            databaseReference = FirebaseDatabase.getInstance().getReference("Trips");

            trips = new ArrayList<>();
            recyclerViewAdapter = new TripsRecyclerViewAdapter(getContext(), trips, false);
            recyclerView.setAdapter(recyclerViewAdapter);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    trips.clear();
                    for (DataSnapshot ds :
                            snapshot.getChildren()) {
                        Trip trip = ds.getValue(Trip.class);

                        assert trip != null;
                        trip.setDateInt();
                        if (tripOutOfDateChecker(trip))
                            trips.add(trip);
                    }
                    Collections.sort(trips);


                    for (Trip t :
                            trips) {
                        System.out.println("menim 77 " + t);
                    }

                    recyclerViewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

            EditText search = root.findViewById(R.id.search);

            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    filter(s.toString(), 0);
                }
            });

            CheckBox showMyTrips = root.findViewById(R.id.show_my_trips_cb);
            ArrayList<Trip> myTrips = new ArrayList<>();
            showMyTrips.setOnCheckedChangeListener((buttonView, isChecked) -> {
                myTrips.clear();
                if (isChecked) {
                    for (Trip trip :
                            trips) {
                        if (trip.getTripSharer().getUserId().equals(Tools.userID)) {
                            myTrips.add(trip);
                        }
                    }
                    recyclerViewAdapter.filterList(myTrips);
                } else {
                    recyclerViewAdapter.filterList(trips);
                }
            });

        });


        return root;
    }

    private void filter(String text, int point) {
        ArrayList<Trip> filteredList = new ArrayList<>();
        for (Trip trip :
                trips) {

            if (trip.getRoute().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(trip);
            }
        }
        recyclerViewAdapter.filterList(filteredList);
    }

    private boolean tripOutOfDateChecker(Trip trip) {
        Calendar currentCalendar = Calendar.getInstance();

        MDate scheduledDate = trip.getDate();

        Calendar scheduledCalendar = Calendar.getInstance();
        scheduledCalendar.set(scheduledDate.getYear(), scheduledDate.getMonth(), scheduledDate.getDay(),
                scheduledDate.getHour(), scheduledDate.getMinute());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        currentCalendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH) + 1);

        if(scheduledCalendar.before(currentCalendar)){

            databaseReference.child("CompletedTrips").child(trip.getKey()).setValue(trip);
            databaseReference.child("Trips").child(trip.getKey()).removeValue();
            return false;
        }

        return true;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}