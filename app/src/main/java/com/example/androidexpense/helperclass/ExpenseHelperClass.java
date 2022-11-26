package com.example.androidexpense.helperclass;

import java.util.Date;
import java.util.UUID;

public class ExpenseHelperClass {
    //variables
    UUID expenseID;
    Double expenseAmount;
    String expenseType;
    String expenseCategory;
    Date expenseDate;
    Integer trashed;

    public ExpenseHelperClass() {

    }

    //constructor
    public ExpenseHelperClass(UUID expenseID, Double expenseAmount, String expenseType, String expenseCategory, Date expenseDate, Integer trashed) {
        this.expenseID = UUID.randomUUID(); //generate random UUID
        this.expenseAmount = expenseAmount;
        this.expenseType = expenseType;
        this.expenseCategory = expenseCategory;
        this.expenseDate = expenseDate;
        this.trashed = 0; //0 as default for not trashed, trashed will be 1
    }

    //getter setters

    public UUID getExpenseID() {
        return expenseID;
    }

    public void setExpenseID(UUID expenseID) {
        this.expenseID = expenseID;
    }

    public Double getExpenseAmount() {
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

    public Date getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }

    public Integer getTrashed() {
        return trashed;
    }

    public void setTrashed(Integer trashed) {
        this.trashed = trashed;
    }
}
