package com.example.androidexpense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.Calendar;


public class AddExpense extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //request codes
    private static final int NEW_TASK_SUCCESS = 101;
    private static final int NEW_TASK_FAIL = 101;

    //variables
    double amount;
    String type;
    String category;
    String selectedDate;
    private boolean setType = false;
    private boolean setCategory = false;
    private boolean setDate = false;


    //xml bindings
    private DrawerLayout mDrawerLayout;
    private EditText mAmount;
    private Spinner mType;
    private Spinner mCategory;
    private TextView mDate;
    private Button mAdd;

    //getting logged in user info
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    String userID = mUser.getUid(); //grabbing uid of current logged in user
    DatabaseReference mUserInfoDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID); //child node to refer from

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        //bind xml views with their id
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView mNavigationView = findViewById(R.id.nav_view);
        mAmount = findViewById(R.id.et_amount_add);
        mType = findViewById(R.id.sp_typeData_add);
        mCategory = findViewById(R.id.sp_categoryData_add);
        mDate = findViewById(R.id.tv_datepicker_add);
        mAdd = findViewById(R.id.btn_add);
        mAdd.setEnabled(false); //disable button if not all field filled

        //navigation options selection listener
        mNavigationView.setNavigationItemSelectedListener(this); //function is somewhere else

        //toolbar related
        mToolbar.setTitle(R.string.toolbar_add_new); //change title of toolbar
        setSupportActionBar(mToolbar);

        //for the hamburger icon to rotate
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        //if user logged in
        if(mUser == null) {
            //appear dialog if not logged in
            AlertDialog.Builder builder=new AlertDialog.Builder(AddExpense.this);
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
                Toast.makeText(AddExpense.this, "Something went wrong while trying to fetch user email...", Toast.LENGTH_SHORT).show();
            }
        };
        mUserInfoDatabase.addListenerForSingleValueEvent(valueEventListener); //display





        //FUNCTIONS FOR ADD EXPENSE
        //spinner adapter type
        ArrayAdapter<CharSequence> typeSpinner = ArrayAdapter.createFromResource(
                AddExpense.this,
                R.array.expense_type,
                android.R.layout.simple_spinner_item
        );

        typeSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mType.setAdapter(typeSpinner);
        mType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = mType.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        //spinner adapter category
        ArrayAdapter<CharSequence> categorySpinner = ArrayAdapter.createFromResource(
                AddExpense.this,
                R.array.expense_categories,
                android.R.layout.simple_spinner_item
        );

        categorySpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(categorySpinner);
        mCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = mCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        //datepicker
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog date = new DatePickerDialog(
                        AddExpense.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                String formattedMonth = "" + month;
                                String formattedDayOfMonth = "" + dayOfMonth;
                                if (month < 10) {
                                    formattedMonth = "0" + month;
                                }
                                if (dayOfMonth < 10) {
                                    formattedDayOfMonth = "0" + dayOfMonth;
                                }
                                mDate.setText(year + "-" + formattedMonth + "-" + formattedDayOfMonth);
                            }
                        },
                        year,
                        month,
                        day
                );
                date.show();
                selectedDate = mDate.getText().toString();
            }
        });

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
                Intent intentExpense = new Intent(getApplicationContext(), ExpenseList.class);
                startActivity(intentExpense);
                break;
            case R.id.menu_add_new:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.menu_trashcan:
                Intent intentTrashcan = new Intent(getApplicationContext(), Trashcan.class);
                startActivity(intentTrashcan);
                break;
            case R.id.menu_logout:
                //appear dialog to ask if user sure to logout
                AlertDialog.Builder builder=new AlertDialog.Builder(AddExpense.this);
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

    public void add_onClick(View view) {
        //enable button if everything filled
        String fetchAmount = mAmount.getText().toString();
        if(fetchAmount == null) {
            amount = 0.0; //change fetched amount from string to double
        }
        else{
            amount = Double.parseDouble(fetchAmount); //change fetched amount from string to double
        }

        ExpensesDatabase expensesDatabase = ExpensesDatabase.getInstance(AddExpense.this);

        Expenses newExpense = new Expenses(
                userID,
                amount,
                type,
                category,
                selectedDate,
                0
        );

        try{
            expensesDatabase.getExpensesDao().insert(newExpense);
            Toast.makeText(AddExpense.this, "Task created successfully!", Toast.LENGTH_SHORT).show();

            Intent mainIntent = new Intent();

            setResult(RESULT_OK, mainIntent);
            finish();
        }
        catch(Error e){
            Toast.makeText(AddExpense.this, "Task creation error: " + e, Toast.LENGTH_SHORT).show();
            finishActivity(NEW_TASK_FAIL);
        }
    }
}