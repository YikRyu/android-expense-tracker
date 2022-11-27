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


public class EditExpense extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //request codes
    private final static int REQUEST_ADD = 10;
    public final static int REQUEST_EDIT = 20;
    public final static int EDIT_SUCCESS = 200;
    public final static int EDIT_FAIL = 201;

    //variables
    double amount;
    String type;
    String category;
    String selectedDate;
    private int expenseId;


    //xml bindings
    private DrawerLayout mDrawerLayout;
    private EditText mAmount;
    private Spinner mType;
    private Spinner mCategory;
    private TextView mDate;
    private Button mEdit;

    //getting logged in user info
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    String userID = mUser.getUid(); //grabbing uid of current logged in user
    DatabaseReference mUserInfoDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID); //child node to refer from

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        //bind xml views with their id
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView mNavigationView = findViewById(R.id.nav_view);
        mAmount = findViewById(R.id.et_amount_edit);
        mType = findViewById(R.id.sp_typeData_edit);
        mCategory = findViewById(R.id.sp_categoryData_edit);
        mDate = findViewById(R.id.tv_datepicker_edit);
        mEdit = findViewById(R.id.btn_edit);

        //navigation options selection listener
        mNavigationView.setNavigationItemSelectedListener(this); //function is somewhere else

        //toolbar related
        mToolbar.setTitle(R.string.toolbar_edit); //change title of toolbar
        setSupportActionBar(mToolbar);

        //for the hamburger icon to rotate
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        //if user logged in
        if(mUser == null) {
            //appear dialog if not logged in
            AlertDialog.Builder builder=new AlertDialog.Builder(EditExpense.this);
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
                Toast.makeText(EditExpense.this, "Something went wrong while trying to fetch user email...", Toast.LENGTH_SHORT).show();
            }
        };
        mUserInfoDatabase.addListenerForSingleValueEvent(valueEventListener); //display





        //FUNCTIONS FOR EDIT EXPENSE
        //Spinner adapter type
        ArrayAdapter<CharSequence> typeSpinner = ArrayAdapter.createFromResource(
                EditExpense.this,
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
        ArrayAdapter<CharSequence> categorySpinner;
        categorySpinner = ArrayAdapter.createFromResource(
                EditExpense.this,
                R.array.expense_categories,
                android.R.layout.simple_spinner_item
        );
        //setting category spinner dropdown item
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
                        EditExpense.this,
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

        //fetch passed data from intent
        Intent intent = getIntent();
        expenseId = intent.getIntExtra("EXPENSE_ID", 0);
        int spinnerTypePosition = typeSpinner.getPosition(intent.getStringExtra("EXPENSE_TYPE"));
        int spinnerCategoryPosition = categorySpinner.getPosition(intent.getStringExtra("EXPENSE_CATEGORY"));
        String amountText = intent.getStringExtra("EXPENSE_AMOUNT");
        String dateText = intent.getStringExtra("EXPENSE_DATE");

        //setting the existing data
        mType.setSelection(spinnerTypePosition);
        mCategory.setSelection(spinnerCategoryPosition);
        mAmount.setText(amountText);
        mDate.setText(dateText);

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
                Intent intentAddExpense = new Intent(getApplicationContext(), AddExpense.class);
                startActivityForResult(intentAddExpense, REQUEST_ADD);
                break;
            case R.id.menu_trashcan:
                Intent intentTrashcan = new Intent(getApplicationContext(), Trashcan.class);
                startActivity(intentTrashcan);
                break;
            case R.id.menu_logout:
                //appear dialog to ask if user sure to logout
                AlertDialog.Builder builder=new AlertDialog.Builder(EditExpense.this);
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

    public void edit_onClick(View view){
        amount = Double.parseDouble(mAmount.getText().toString()); //change fetched amount from string to double
        ExpensesDatabase expensesDatabase = ExpensesDatabase.getInstance(EditExpense.this);

        Expenses newExpense = new Expenses(
                expenseId,
                userID,
                amount,
                type,
                category,
                selectedDate,
                0
        );

        try{
            expensesDatabase.getExpensesDao().update(newExpense);
            Toast.makeText(EditExpense.this, "Edit success.", Toast.LENGTH_SHORT).show();

            Intent mainIntent = new Intent();

            setResult(RESULT_OK, mainIntent);
            finish();
        }
        catch(Error e){
            Toast.makeText(EditExpense.this, "Edit failed, error: " + e, Toast.LENGTH_SHORT).show();
            finishActivity(EDIT_FAIL);
        }
    }

}