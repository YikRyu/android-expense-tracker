package com.example.androidexpense.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExpensesDAO {
    @Query("SELECT * FROM expense_table WHERE  userID=:userID AND isTrashed = 0")
    List<Expenses> getNotTrashed(String userID);

    @Query("SELECT * FROM expense_table WHERE userID=:userID AND isTrashed = 1")
    List<Expenses> getTrashed(String userID);

    @Query("SELECT expenseAmount FROM expense_table WHERE userID=:userID AND expenseType='Income' AND expenseDate=:expenseDate")
    double getIncome(String userID, String expenseDate);

    @Query("SELECT expenseAmount FROM expense_table WHERE userID=:userID AND expenseCategory='Food' AND expenseDate=:expenseDate")
    double getFood(String userID, String expenseDate);

    @Query("SELECT expenseAmount FROM expense_table WHERE userID=:userID AND expenseCategory='Healthcare' AND expenseDate=:expenseDate")
    double getHealthcare(String userID, String expenseDate);

    @Query("SELECT expenseAmount FROM expense_table WHERE userID=:userID AND expenseCategory='Entertainment' AND expenseDate=:expenseDate")
    double getEntertainment(String userID, String expenseDate);

    @Query("SELECT expenseAmount FROM expense_table WHERE userID=:userID AND expenseCategory='Education' AND expenseDate=:expenseDate")
    double getEducation(String userID, String expenseDate);

    @Query("SELECT expenseAmount FROM expense_table WHERE userID=:userID AND expenseCategory='Utilities' AND expenseDate=:expenseDate")
    double getUtilities(String userID, String expenseDate);

    @Query("UPDATE expense_table SET isTrashed = 1 WHERE userID=:userID AND expenseID = :expenseID")
    void updateTrashed(String userID, int expenseID);

    @Query("UPDATE expense_table SET isTrashed = 0 WHERE userID=:userID AND expenseID = :expenseID")
    void updateNotTrashed(String userID, int expenseID);

    @Query("DELETE FROM expense_table WHERE userID=:userID AND isTrashed = 1")
    void deleteAllTrashed(String userID);

    //default queries
    @Insert(onConflict = 1)
    void insert(Expenses expenses);

    @Update(onConflict = 1)
    void update(Expenses expenses);

    @Delete
    void delete(Expenses expenses);
}
