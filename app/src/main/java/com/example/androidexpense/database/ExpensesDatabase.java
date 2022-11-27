package com.example.androidexpense.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Expenses.class}, version = 1)
public abstract class ExpensesDatabase extends RoomDatabase {
    private static final String DB_NAME = "expenseTrackerDB";
    public abstract ExpensesDAO getExpensesDao();
    public static ExpensesDatabase expenseDB;

    public static ExpensesDatabase getInstance(Context context){
        if(null == expenseDB){
            expenseDB = Room.databaseBuilder(context.getApplicationContext(), ExpensesDatabase.class, DB_NAME).allowMainThreadQueries().build();
        }
        return expenseDB;
    }
}
