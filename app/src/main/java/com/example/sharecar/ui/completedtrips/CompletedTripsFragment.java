package com.example.sharecar.ui.completedtrips;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharecar.NavigationActivity;
import com.example.sharecar.R;
import com.example.sharecar.Trip;
import com.example.sharecar.TripsRecyclerViewAdapter;
import com.example.sharecar.databinding.FragmentCompletedTripsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CompletedTripsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private TripsRecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Trip> trips;

    private CompletedTripsViewModel galleryViewModel;
    private FragmentCompletedTripsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(CompletedTripsViewModel.class);

        binding = FragmentCompletedTripsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        galleryViewModel.getText().observe(getViewLifecycleOwner(), s -> {

            NavigationActivity.binding.appBarNavigation.fab.setVisibility(View.VISIBLE);

            recyclerView = root.findViewById(R.id.completed_trip_list);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            databaseReference = FirebaseDatabase.getInstance().getReference("CompletedTrips");

            trips = new ArrayList<>();
            recyclerViewAdapter = new TripsRecyclerViewAdapter(getContext(), trips, true);
            recyclerView.setAdapter(recyclerViewAdapter);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    trips.clear();
                    for (DataSnapshot ds :
                            snapshot.getChildren()) {
                        Trip trip = ds.getValue(Trip.class);
                        trips.add(trip);
                    }

                    recyclerViewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

            EditText search = root.findViewById(R.id.search_completed_trips);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}