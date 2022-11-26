package com.example.androidexpense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.androidexpense.databinding.ActivityExpenseListBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ExpenseList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //variable declarations
    private ActivityExpenseListBinding mActivityExpenseListBinding;
    private FirebaseAuth mFirebaseAuth;

    //request codes
    private final static int REQUEST_ADD = 10;

    //firebase instances
    FirebaseDatabase rootNode;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //bind view with binding
        mActivityExpenseListBinding = ActivityExpenseListBinding.inflate(getLayoutInflater()); //home binding
        setContentView(mActivityExpenseListBinding .getRoot());

        //toolbar related
        mActivityExpenseListBinding .toolbar.setTitle(R.string.toolbar_home); //change title of toolbar
        setSupportActionBar(mActivityExpenseListBinding .toolbar);

        //for the hamburger icon to rotate
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mActivityExpenseListBinding .drawerLayout, mActivityExpenseListBinding .toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mActivityExpenseListBinding .drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //getting logged in user info
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mFirebaseAuth.getCurrentUser();
        //if user logged in
        if(mUser == null) {
            //appear dialog if not logged in
            AlertDialog.Builder builder=new AlertDialog.Builder(ExpenseList.this);
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
        String UID = mUser.getUid(); //grabbing uid of user
        DatabaseReference mUserInfoDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UID); //child node to refer from
        //getting xml id reference of user email
        View headerContainer = mActivityExpenseListBinding .navView.getHeaderView(0); // returns the container layout from navigation drawer header layout file
        TextView userEmailTV = (TextView) headerContainer.findViewById(R.id.user_email); //fetch reference id of email

        //displaying user email in the nav header
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userEmail = snapshot.child("Email").getValue().toString(); //grabbing email from database
                userEmailTV.setText(userEmail); //change the email
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpenseList.this, "Something went wrong while trying to fetch user email...", Toast.LENGTH_SHORT).show();
            }
        };
        mUserInfoDatabase.addListenerForSingleValueEvent(valueEventListener); //display

        //calling firebase instance root
        rootNode = FirebaseDatabase.getInstance();
        mDatabaseReference = rootNode.getReference("");
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
                AlertDialog.Builder builder=new AlertDialog.Builder(ExpenseList.this);
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
        if(mActivityExpenseListBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){ //will only execute if navbar is opening
            mActivityExpenseListBinding.drawerLayout.closeDrawer(GravityCompat.START); //close the navigation if pressed back
        }
        else {
            super.onBackPressed(); //close the activity instead
        }
    }

    //for options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.clear_all){
            //function
        }
        return true;
    }

}