//Code References
//Drawer Navigation Part 1-3 : https://www.youtube.com/watch?v=fGcMLu1GJEc
//                           : https://www.youtube.com/watch?v=zYVEMCiDcmY
//                           : https://www.youtube.com/watch?v=bjYstsO1PgI
//Month and Year Picker: https://avinashbest.medium.com/creating-a-month-and-year-picker-in-android-material-user-interface-40b3461425b5
//Pie Chart: https://www.youtube.com/watch?v=S3zqxVoIUig
package com.example.androidexpense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.example.androidexpense.databinding.ActivityHomeBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //variable declarations
    private ActivityHomeBinding mActivityHomeBinding;

    //request codes
    private final static int REQUEST_ADD = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //bind view with binding
        mActivityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater()); //home binding
        setContentView(mActivityHomeBinding.getRoot());
        //navigation vew
        NavigationView navigationView = mActivityHomeBinding.navView;
        navigationView.setNavigationItemSelectedListener(this); //function is somewhere else

        //toolbar related
        mActivityHomeBinding.toolbar.setTitle(R.string.toolbar_home); //change title of toolbar
        setSupportActionBar(mActivityHomeBinding.toolbar);

        //for the hamburger icon to rotate
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mActivityHomeBinding.drawerLayout, mActivityHomeBinding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mActivityHomeBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //getting logged in user info
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        //if user logged in
        if(mUser == null) {
            //appear dialog if not logged in
            AlertDialog.Builder builder=new AlertDialog.Builder(Home.this);
            builder.setTitle("Not Logged In!");
            builder.setMessage("Please login first!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { //if sure
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(getApplicationContext(),Login.class));
                    dialog.dismiss();
                }
            });
            builder.show();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
        String userID = mUser.getUid(); //grabbing uid of current logged in user
        DatabaseReference mUserInfoDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID); //child node to refer from
        //getting xml id reference of user email
        View headerContainer = mActivityHomeBinding.navView.getHeaderView(0); // returns the container layout from navigation drawer header layout file
        TextView userEmailTV = (TextView) headerContainer.findViewById(R.id.user_email); //fetch reference id of email


        //displaying user email in the nav header
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userEmail = snapshot.child("email").getValue().toString(); //grabbing email from database
                userEmailTV.setText(userEmail); //change the email
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, "Something went wrong while trying to fetch user email...", Toast.LENGTH_SHORT).show();
            }
        };
        mUserInfoDatabase.addListenerForSingleValueEvent(valueEventListener); //display


        //call function when the date picker is pressed
        mActivityHomeBinding.etStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog endDate = new DatePickerDialog(
                        Home.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String formattedMonth = "" + month;
                                String formattedDayOfMonth = "" + dayOfMonth;
                                if(month < 10){

                                    formattedMonth = "0" + month;
                                }
                                if(dayOfMonth < 10){

                                    formattedDayOfMonth = "0" + dayOfMonth;
                                }
                                mActivityHomeBinding.etStart.setText(formattedDayOfMonth + "/" + formattedMonth + "/" + year);
                            }
                        },
                        year,
                        month,
                        day
                );

                endDate.show();
            }
        });

    }

    //navigation item selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_home:
                Intent intentHome = new Intent(this, Home.class);
                startActivity(intentHome);
                break;
            case R.id.menu_expense_list:
                Intent intentExpense = new Intent(this, ExpenseList.class);
                startActivity(intentExpense);
                break;
            case R.id.menu_add_new:
                Intent intent = new Intent(this, AddExpense.class);
                startActivityForResult(intent, REQUEST_ADD);
                break;
            case R.id.menu_trashcan:
                Intent intentTrashcan = new Intent(this, Trashcan.class);
                startActivity(intentTrashcan);
                break;
            case R.id.menu_logout:
                //appear dialog to ask if user sure to logout
                AlertDialog.Builder builder=new AlertDialog.Builder(Home.this);
                builder.setTitle("Logout");
                builder.setMessage("Do you really want to Logout?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() { //if sure
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(),Login.class));
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() { //if cancel
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mActivityHomeBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){ //will only execute if navbar is opening
            mActivityHomeBinding.drawerLayout.closeDrawer(GravityCompat.START); //close the navigation if pressed back
        }
        else {
            super.onBackPressed();
        }
    }

    //date picking
    public void start_onClick(View view) {
        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);

        TimePickerDialog startTime = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mActivityHomeBinding.etStart.setText(hourOfDay + ":" + minute);
                    }
                },
                hours,
                minutes,
                android.text.format.DateFormat.is24HourFormat(this)
        );
        startTime.show();
    }
    public void end_onClick(View view) {
        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);

        TimePickerDialog endTime = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mActivityHomeBinding.etEnd.setText(hourOfDay + ":" + minute);
                    }
                },
                hours,
                minutes,
                android.text.format.DateFormat.is24HourFormat(this)
        );
        endTime.show();
    }
}