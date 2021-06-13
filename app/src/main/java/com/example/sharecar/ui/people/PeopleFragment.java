package com.example.sharecar.ui.people;

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
import com.example.sharecar.Tools;
import com.example.sharecar.User;
import com.example.sharecar.UserListAdapter;
import com.example.sharecar.databinding.FragmentPeopleBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PeopleFragment extends Fragment {

    RecyclerView userRecyclerView;
    ArrayList<User> userList;
    UserListAdapter userListAdapter;

    private PeopleViewModel slideshowViewModel;
    private FragmentPeopleBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(PeopleViewModel.class);

        binding = FragmentPeopleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        slideshowViewModel.getText().observe(getViewLifecycleOwner(), s -> {

            NavigationActivity.binding.appBarNavigation.fab.setVisibility(View.GONE);

            userList = new ArrayList<>();
            userRecyclerView = root.findViewById(R.id.user_list);

            userRecyclerView.setHasFixedSize(true);
            userRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
            userListAdapter = new UserListAdapter(root.getContext(), userList);
            userRecyclerView.setAdapter(userListAdapter);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    userList.clear();
                    for (DataSnapshot ds :
                            snapshot.getChildren()) {
                        User user = ds.getValue(User.class);
                        if (!user.getUserId().equals(Tools.userID))
                            userList.add(user);
                    }
                    userListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

            EditText search = root.findViewById(R.id.search_user);

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
        ArrayList<User> filteredList = new ArrayList<>();
        for (User user :
                userList) {

            if (user.getUserNameForSearch().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(user);
            }
        }
        userListAdapter.filterList(filteredList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}