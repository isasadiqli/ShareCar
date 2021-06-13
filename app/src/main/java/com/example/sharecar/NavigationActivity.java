package com.example.sharecar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sharecar.databinding.ActivityNavigationBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;

public class NavigationActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public static ActivityNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavigation.toolbar);

        binding.appBarNavigation.fab.setOnClickListener(view -> {

            openAddNewTripDialog();

        });


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_trips, R.id.nav_completed_trips, R.id.nav_people)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void openAddNewTripDialog() {
        Dialog dialog = new Dialog(this, R.style.Dialog);
        dialog.setContentView(R.layout.add_new_trip_dialog);
        dialog.setTitle("ADD NEW TRIP");

        EditText startPointET = dialog.findViewById(R.id.start_point_trip);
        EditText destinationET = dialog.findViewById(R.id.destination_trip);
        EditText costET = dialog.findViewById(R.id.cost_trip);

        TextView dateTextView = dialog.findViewById(R.id.date_trip);
        Button add = dialog.findViewById(R.id.add_trip);

        Calendar calendar = Calendar.getInstance();
        int[] year = {calendar.get(Calendar.YEAR)};
        int[] month = {calendar.get(Calendar.MONTH)};
        int[] day = {calendar.get(Calendar.DAY_OF_MONTH)};
        int[] hour = {calendar.get(Calendar.HOUR_OF_DAY)};
        int[] minute = {calendar.get(Calendar.MINUTE)};

        dateTextView.setOnClickListener(v -> {

            dialog.dismiss();

            @SuppressLint("SetTextI18n")
            TimePickerDialog timePickerDialog = new TimePickerDialog(NavigationActivity.this,
                    (view1, hourOfDay, minute1) -> {

                        hour[0] = hourOfDay;
                        minute[0] = minute1;

                        dateTextView.setText(hour[0] + ":" + minute[0] + " " + day[0]
                                + "." + month[0] + "." + year[0]);
                        dialog.show();

                        boolean startPointEmpty = true, destinationEmpty = true, costEmpty = true, dateEmpty = true;

                        startPointEmpty = startPointET.getText().toString().isEmpty();
                        destinationEmpty = destinationET.getText().toString().isEmpty();
                        costEmpty = costET.getText().toString().isEmpty();
                        dateEmpty = dateTextView.getText().equals("Set time");

                        add.setEnabled(!(startPointEmpty || destinationEmpty || costEmpty || dateEmpty));

                    }
                    , hour[0], minute[0], true);


            DatePickerDialog.OnDateSetListener dateSetListener = (view12, year1, month1, dayOfMonth) -> {

                year[0] = year1;
                month[0] = month1;
                day[0] = dayOfMonth;

                timePickerDialog.show();
            };

            DatePickerDialog datePickerDialog = new DatePickerDialog(NavigationActivity.this,
                    R.style.Dialog, dateSetListener, year[0], month[0], day[0]);

            datePickerDialog.setOnCancelListener(dialog1 -> timePickerDialog.dismiss());

            datePickerDialog.show();

        });


        add.setOnClickListener(v -> {
            ArrayList<String> cities = new ArrayList<>();
            cities.add(startPointET.getText().toString());
            cities.add(destinationET.getText().toString());

            MDate date = new MDate(day[0], month[0] + 1, year[0], hour[0], minute[0]);

            Tools.addNewTrip(cities, Integer.parseInt(costET.getText().toString()), date, this);

            dialog.dismiss();
        });

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        Tools tools = new Tools();

        TextView username = findViewById(R.id.username);
        TextView userMail = findViewById(R.id.user_mail);


        username.setText(Tools.name + " " + Tools.surname);
        userMail.setText(Tools.email);


        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void logOutClicked(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void updateUser(MenuItem item){
        Intent intent = new Intent(this, UpdateUserActivity.class);
        startActivity(intent);
    }
}