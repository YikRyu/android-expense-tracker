package com.example.androidexpense.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

@Entity(tableName = "expense_table")
public class Expenses {
    @NonNull
    @PrimaryKey(autoGenerate = true)
        public int expenseID;

    @ColumnInfo(name = "userID")
        public String userID;

    @ColumnInfo(name = "expenseType")
        public String expenseType;

    @ColumnInfo(name = "expenseCategory")
        public String expenseCategory;

    @ColumnInfo(name = "expenseAmount")
        public double expenseAmount;

    @ColumnInfo(name = "expenseDate")
        public String expenseDate;

    @ColumnInfo(name = "isTrashed")
        public int isTrashed;

    //constructor
    public Expenses(int expenseID, String userID,double expenseAmount, String expenseType, String expenseCategory, String expenseDate, int isTrashed) {
        this.expenseID = expenseID;
        this.userID = userID;
        this.expenseAmount = expenseAmount;
        this.expenseType = expenseType;
        this.expenseCategory = expenseCategory;
        this.expenseDate = expenseDate;
        this.isTrashed = isTrashed; //0 as default for not trashed, trashed will be 1
    }

    //constructor for Edit
    @Ignore
    public Expenses(String userID,double expenseAmount, String expenseType, String expenseCategory, String expenseDate, int isTrashed) {
        this.userID = userID;
        this.expenseAmount = expenseAmount;
        this.expenseType = expenseType;
        this.expenseCategory = expenseCategory;
        this.expenseDate = expenseDate;
        this.isTrashed = isTrashed; //0 as default for not trashed, trashed will be 1
    }

    //getter setters

    public int getExpenseID() {
        return expenseID;
    }
    public void setExpenseID(int expenseID) {
        this.expenseID = expenseID;
    }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public double getExpenseAmount() {
        return expenseAmount;
    }
    public void setExpenseAmount(Double expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public String getExpenseType() {
        return expenseType;
    }
    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public String getExpenseCategory() {
        return expenseCategory;
    }
    public void setExpenseCategory(String expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public String getExpenseDate() {
        return expenseDate;
    }
    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public int getTrashed() {
        return isTrashed;
    }
    public void setTrashed(int isTrashed) {
        this.isTrashed = isTrashed;
    }
}
