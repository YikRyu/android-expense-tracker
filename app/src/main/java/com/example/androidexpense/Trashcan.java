package com.example.androidexpense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidexpense.database.Expenses;
import com.example.androidexpense.database.ExpensesDatabase;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;


public class Trashcan extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //request codes
    private final static int REQUEST_ADD = 10;

    //variables
    String userID;

    //xml bindings
    private DrawerLayout mDrawerLayout;
    private TrashcanAdapter mTrashcanAdapter;
    private List<Expenses> mExpensesList;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trashcan);

        //bind xml views with their id
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView mNavigationView = findViewById(R.id.nav_view);
        mRecyclerView = findViewById(R.id.rv_trashcan);

        //room database
        ExpensesDatabase expenseDB = ExpensesDatabase.getInstance(this);

        //refer user ID
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mUser.getUid(); //grabbing uid of current logged in user
        DatabaseReference mUserInfoDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID); //child node to refer from

        //navigation options selection listener
        mNavigationView.setNavigationItemSelectedListener(this); //function is somewhere else

        //toolbar related
        mToolbar.setTitle(R.string.toolbar_trashcan); //change title of toolbar
        setSupportActionBar(mToolbar);

        //for the hamburger icon to rotate
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //if user logged in
        if(mUser == null) {
            //appear dialog if not logged in
            AlertDialog.Builder builder=new AlertDialog.Builder(Trashcan.this);
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



        //displaying user email in the nav header
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userEmail = snapshot.child("email").getValue().toString(); //grabbing email from database
                TextView userEmailTV = findViewById(R.id.user_email); //fetch reference id of user email in header xml
                userEmailTV.setText(userEmail); //change the email
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Trashcan.this, "Something went wrong while trying to fetch user email...", Toast.LENGTH_SHORT).show();
            }
        };
        mUserInfoDatabase.addListenerForSingleValueEvent(valueEventListener); //display

        //display list of trashed expenses with recycler view
        List<Expenses> expensesList = expenseDB.getExpensesDao().getTrashed(userID);
        mTrashcanAdapter = new TrashcanAdapter(this, expensesList);
        mRecyclerView.setAdapter(mTrashcanAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper helper = new ItemTouchHelper(new TrashcanCallback(mTrashcanAdapter));
        helper.attachToRecyclerView(mRecyclerView);

    }

    @Override
    public void onResume() {
        super.onResume();

        ExpensesDatabase expenseDB = ExpensesDatabase.getInstance(Trashcan.this);
        //display list of expenses with recycler view
        List<Expenses> expensesList = expenseDB.getExpensesDao().getTrashed(userID);
        mTrashcanAdapter = new TrashcanAdapter(this, expensesList);
        mRecyclerView.setAdapter(mTrashcanAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.getAdapter().notifyDataSetChanged();
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
                Intent intentExpense = new Intent(getApplicationContext(), ExpenseList.class);
                startActivity(intentExpense);
                break;
            case R.id.menu_add_new:
                Intent intentAddExpense = new Intent(getApplicationContext(), AddExpense.class);
                startActivityForResult(intentAddExpense, REQUEST_ADD);
                break;
            case R.id.menu_trashcan:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.menu_logout:
                //appear dialog to ask if user sure to logout
                AlertDialog.Builder builder=new AlertDialog.Builder(Trashcan.this);
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
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){ //will only execute if navbar is opening
            mDrawerLayout.closeDrawer(GravityCompat.START); //close the navigation if pressed back
        }
        else {
            super.onBackPressed();
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
            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(Trashcan.this);
            alert.setTitle("Delete Permanently");
            alert.setMessage("Are you sure you want to delete all the expenses in trashcan permanently?");
            alert.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
            );
            alert.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ExpensesDatabase expenseDB = ExpensesDatabase.getInstance(Trashcan.this);
                            List<Expenses> removeTrashed= expenseDB.getExpensesDao().getTrashed(userID);
                            expenseDB.getExpensesDao().deleteAllTrashed(userID);
                            Toast.makeText(Trashcan.this, "All trashed items yeeted to oblivion.", Toast.LENGTH_SHORT).show();
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    }
            );
            alert.show();
        }
        return true;
    }
}