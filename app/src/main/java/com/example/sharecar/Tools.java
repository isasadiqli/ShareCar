package com.example.sharecar;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Tools {

    private static String password = "", passwordReentered = " ";

    private static boolean signedUp = true;

    public static String name, surname, email, userType;

    private static boolean nameEmpty = true;
    private static boolean surnameEmpty = true;
    private static boolean emailEmpty = true;
    private static boolean passwordEmpty = true;
    private static boolean passwordReenteredEmpty = true;
    private static User user;
    public static String userID;

    private static boolean carNameEmpty = true, carModelEmpty = true, maxPassengerEmpty = true;
    public static boolean startPointEmpty = true, destinationEmpty = true, costEmpty = true, dateEmpty = true;

    private static boolean oldpasswordEmpty = true, passwordUpdateEmpty = true, passwordReenteredUpdateEmpty = true;

    private TextView username, userMail;

    public static boolean isUserDriver = false;

    public static User getUser() {
        return user;
    }

    public static boolean isUserDriver() {
        return isUserDriver;
    }

    public void setUsername(TextView username) {
        this.username = username;
    }

    public void setUserMail(TextView userMail) {
        this.userMail = userMail;
    }

    public static void setSignedUp(boolean signedUp) {
        Tools.signedUp = signedUp;
    }

    public static boolean isSignedUp() {
        return signedUp;
    }

    private static void setPassword(String password) {
        Tools.password = password;
    }

    private static void setPasswordReentered(String passwordReentered) {
        Tools.passwordReentered = passwordReentered;
    }

    private static boolean arePasswordsMatch() {
        return password.equals(passwordReentered);
    }

    public static void editTextListener(Button button, EditText editText, String error, int callingIndex) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                boolean empty = s.toString().trim().length() == 0;
                if (callingIndex == 0) {
                    switch (error) {
                        case "name":
                            nameEmpty = empty;
                            break;
                        case "surname":
                            surnameEmpty = empty;
                            break;
                        case "email":
                            emailEmpty = empty;
                            break;
                        case "password":
                            passwordEmpty = empty;
                            setPassword(String.valueOf(editText.getText()));
                            break;
                        case "passwordReentered":
                            passwordReenteredEmpty = empty;
                            setPasswordReentered(String.valueOf(editText.getText()));
                            break;
                    }

                    if (!signedUp) {
                        button.setEnabled(!(nameEmpty || surnameEmpty || emailEmpty || passwordEmpty || passwordReenteredEmpty)
                                && arePasswordsMatch() && password.trim().length() >= 6);
                    } else button.setEnabled(!(emailEmpty || passwordEmpty));

                } else if (callingIndex == 1) {
                    switch (error) {
                        case "carName":
                            carNameEmpty = empty;
                            break;
                        case "carModel":
                            carModelEmpty = empty;
                            break;
                        case "maxPassenger":
                            maxPassengerEmpty = empty;
                            break;
                    }

                    if (!signedUp) {
                        button.setEnabled(!(nameEmpty || surnameEmpty || emailEmpty || passwordEmpty || passwordReenteredEmpty)
                                && !(carNameEmpty || carModelEmpty || maxPassengerEmpty)
                                && arePasswordsMatch() && password.trim().length() >= 6);
                    } else button.setEnabled(!(emailEmpty || passwordEmpty));

                } else if (callingIndex == 2) {
                    switch (error) {
                        case "start":
                            startPointEmpty = empty;
                            break;
                        case "destination":
                            destinationEmpty = empty;
                            break;
                        case "cost":
                            costEmpty = empty;
                            break;
                    }

                    button.setEnabled(!(startPointEmpty || destinationEmpty || costEmpty));
                } else if (callingIndex == 3) {
                    switch (error) {
                        case "oldpassword":
                            oldpasswordEmpty = empty;
                            setPassword(String.valueOf(editText.getText()));
                            break;
                        case "password":
                            passwordUpdateEmpty = empty;
                            setPassword(String.valueOf(editText.getText()));
                            break;
                        case "passwordReentered":
                            passwordReenteredUpdateEmpty = empty;
                            setPasswordReentered(String.valueOf(editText.getText()));
                            break;
                    }


                    button.setEnabled(!(oldpasswordEmpty || passwordUpdateEmpty || passwordReenteredUpdateEmpty)
                            && arePasswordsMatch() && password.trim().length() >= 6);


                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static void readUserInfo(FirebaseCallback firebaseCallback) {
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                name = snapshot.child(userID).child("name").getValue(String.class);
                surname = snapshot.child(userID).child("surname").getValue(String.class);
                email = snapshot.child(userID).child("email").getValue(String.class);
                userType = snapshot.child(userID).child("userType").getValue(String.class);

                user = new User(name, surname, email, userID);
                firebaseCallback.onCallBack(user);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    public static void addNewTrip(ArrayList<String> cities, int cost, MDate date, Context context) {

        HashMap<String, User> passengers = new HashMap<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Trips").push();

        String key = databaseReference.getKey();
        Trip trip = new Trip(user, passengers, new HashMap<>(), new HashMap<>(), cities, cost, date, key);

        databaseReference.setValue(trip).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                Toast.makeText(context, "The trip from " + cities.get(0) + " to " +
                        cities.get(cities.size() - 1) + "is added successfully", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "There has been an error while adding new trip", Toast.LENGTH_SHORT).show();
        });
    }

}
