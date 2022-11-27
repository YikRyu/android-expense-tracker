//Code References
//Drawer Navigation Part 1-3 : https://www.youtube.com/watch?v=fGcMLu1GJEc
//                           : https://www.youtube.com/watch?v=zYVEMCiDcmY
//                           : https://www.youtube.com/watch?v=bjYstsO1PgI
//Pie Chart: https://www.youtube.com/watch?v=S3zqxVoIUig
package com.example.androidexpense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.room.Room;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidexpense.database.ExpensesDatabase;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Calendar;


public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //variables
    private PieChart mPieChart;
    private String selectedDate;

    //request codes
    private final static int REQUEST_ADD = 10;

    //xml bindings
    private DrawerLayout mDrawerLayout;
    private TextView mDate;
    private Button mSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //db building or fetching
        ExpensesDatabase expenseDB = Room.databaseBuilder(getApplicationContext(), ExpensesDatabase.class, "expenseTrackerDB").allowMainThreadQueries().build();
        expenseDB = ExpensesDatabase.getInstance(this);

        //bind xml views with their id
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDate = findViewById(R.id.tv_date);
        mPieChart = findViewById(R.id.piechart);
        mSearch = findViewById(R.id.btn_search);

        //navigation
        NavigationView mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this); //function is somewhere else

        //toolbar related
        mToolbar.setTitle(R.string.toolbar_home); //change title of toolbar
        setSupportActionBar(mToolbar);

        //for the hamburger icon to rotate
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
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
                Toast.makeText(Home.this, "Something went wrong while trying to fetch user email...", Toast.LENGTH_SHORT).show();
            }
        };
        mUserInfoDatabase.addListenerForSingleValueEvent(valueEventListener); //display


        //call function when the date picker is pressed
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog date = new DatePickerDialog(
                        Home.this,
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

        //PIE-CHART FUNCTION AFTER SEARCH BUTTON
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupPieChart();
                loadPieChartData(userID, selectedDate);
            }
        });

    }

    //navigation item selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_home:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.menu_expense_list:
                Intent intentExpense = new Intent(this, ExpenseList.class);
                startActivity(intentExpense);
                break;
            case R.id.menu_add_new:
                Intent intentAddExpense = new Intent(this, AddExpense.class);
                startActivityForResult(intentAddExpense, REQUEST_ADD);
                break;
            case R.id.menu_trashcan:
                Intent intentTrashcan = new Intent(this, Trashcan.class);
                startActivity(intentTrashcan);
                break;
            case R.id.menu_logout:
                //appear dialog to ask if user sure to logout
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
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
        mDrawerLayout.closeDrawer(GravityCompat.START);
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

    //PIE CHART RELATED FUNCTIONS
    //pie chart configuration
    private void setupPieChart() {
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setUsePercentValues(true);
        mPieChart.setEntryLabelTextSize(15);
        mPieChart.setEntryLabelColor(R.color.light_black);
        mPieChart.setCenterText("Expenses \nAnd Incomes");
        mPieChart.setCenterTextSize(20);
        mPieChart.getDescription().setEnabled(false);

        Legend l = mPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setTextSize(13);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    //animation and pie chart data to load
    private void loadPieChartData(String userID,String selectedDate) {
        ExpensesDatabase expenseDB = ExpensesDatabase.getInstance(this);
        //data fetching and assigning, need to change data to float type because piechart requirement
        double i= expenseDB.getExpensesDao().getIncome(userID, selectedDate);
        int income = (int)i;
        double f= expenseDB.getExpensesDao().getIncome(userID, selectedDate);
        int food = (int)f;
        double h= expenseDB.getExpensesDao().getIncome(userID, selectedDate);
        int healthcare = (int)h;
        double et= expenseDB.getExpensesDao().getIncome(userID, selectedDate);
        int entertainment = (int)et;
        double edu= expenseDB.getExpensesDao().getIncome(userID, selectedDate);
        int education = (int)edu;
        double u= expenseDB.getExpensesDao().getIncome(userID, selectedDate);
        int utilities = (int)u;

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(income, "Income"));
        entries.add(new PieEntry(food, "Food"));
        entries.add(new PieEntry(healthcare, "Healthcare"));
        entries.add(new PieEntry(entertainment, "Entertainment"));
        entries.add(new PieEntry(education, "Education"));
        entries.add(new PieEntry(utilities, "Utilities"));

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(mPieChart));
        data.setValueTextSize(15);
        data.setValueTextColor(R.color.light_black);

        mPieChart.setData(data);
        mPieChart.invalidate();

        mPieChart.animateY(1400, Easing.EaseInOutQuad);
    }

}