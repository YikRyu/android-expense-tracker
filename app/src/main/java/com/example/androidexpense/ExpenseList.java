package com.example.androidexpense;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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


public class ExpenseList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //request codes
    private final static int REQUEST_ADD = 10;
    private final static int REQUEST_EDIT = 20;

    //variables
    String userID;

    //xml bindings
    private DrawerLayout mDrawerLayout;
    private ExpenseAdapter mExpenseAdapter;
    private List<Expenses> mExpensesList;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        //room database
        ExpensesDatabase expenseDB = ExpensesDatabase.getInstance(this);

        //refer user ID
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mUser.getUid(); //grabbing uid of current logged in user
        DatabaseReference mUserInfoDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID); //child node to refer from

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

        //bind xml views with their id
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView mNavigationView = findViewById(R.id.nav_view);
        mRecyclerView = findViewById(R.id.rv_expense_list);

        //navigation options selection listener
        mNavigationView.setNavigationItemSelectedListener(this); //function is somewhere else

        //toolbar related
        mToolbar.setTitle(R.string.toolbar_list); //change title of toolbar
        setSupportActionBar(mToolbar);

        //for the hamburger icon to rotate
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


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
                Toast.makeText(ExpenseList.this, "Something went wrong while trying to fetch user email...", Toast.LENGTH_SHORT).show();
            }
        };
        mUserInfoDatabase.addListenerForSingleValueEvent(valueEventListener); //display

        //display list of expenses with recycler view
        List<Expenses> expensesList = expenseDB.getExpensesDao().getNotTrashed(userID);
        mExpenseAdapter = new ExpenseAdapter(this, expensesList);
        mRecyclerView.setAdapter(mExpenseAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper helper = new ItemTouchHelper(new ExpenseCallback(mExpenseAdapter));
        helper.attachToRecyclerView(mRecyclerView);

    }

    @Override
    public void onResume() {
        super.onResume();

        ExpensesDatabase expenseDB = ExpensesDatabase.getInstance(ExpenseList.this);
        //display list of expenses with recycler view
        List<Expenses> expensesList = expenseDB.getExpensesDao().getNotTrashed(userID);
        mExpenseAdapter = new ExpenseAdapter(this, expensesList);
        mRecyclerView.setAdapter(mExpenseAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    //navigation item selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_home:
                Intent intentHome = new Intent(getApplicationContext(), Home.class);
                startActivity(intentHome);
                break;
            case R.id.menu_expense_list:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.menu_add_new:
                Intent intentAddExpense = new Intent(getApplicationContext(), AddExpense.class);
                startActivityForResult(intentAddExpense, REQUEST_ADD);
                break;
            case R.id.menu_trashcan:
                Intent intentTrashcan = new Intent(getApplicationContext(), Trashcan.class);
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
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){ //will only execute if navbar is opening
            mDrawerLayout.closeDrawer(GravityCompat.START); //close the navigation if pressed back
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_ADD || requestCode == REQUEST_EDIT){
            if(resultCode == RESULT_OK){
                ExpensesDatabase expenseDB = ExpensesDatabase.getInstance(ExpenseList.this);
                List<Expenses> expensesList = expenseDB.getExpensesDao().getNotTrashed(userID);
                mExpenseAdapter = new ExpenseAdapter(this, expensesList);
                mRecyclerView.setAdapter(mExpenseAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }

    }
}