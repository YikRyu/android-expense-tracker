//REFERENCES
//Recycler View Adapter: https://www.youtube.com/watch?v=M8sKwoVjqU0
package com.example.androidexpense;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidexpense.database.Expenses;
import com.example.androidexpense.database.ExpensesDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TrashcanAdapter extends RecyclerView.Adapter<TrashcanAdapter.TrashcanViewHolder>{
    //refer user ID
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    String userID = mUser.getUid(); //grabbing uid of current logged in user
    DatabaseReference mUserInfoDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID); //child node to refer from

    Context context;
    List<Expenses> expenseList;

    public TrashcanAdapter(Context context, List<Expenses> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public TrashcanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false); //define which xml as the item layout
        return new TrashcanViewHolder(v); //inflate layout
    }

    @Override
    public void onBindViewHolder(@NonNull TrashcanViewHolder holder, int position) {
        Expenses expenses = expenseList.get(position); //position of item in RV
        //displaying data fetched
        holder.expenseType.setText(expenses.getExpenseType());
        holder.expenseCategory.setText(expenses.getExpenseCategory());
        holder.expenseAmount.setText(Double.toString(expenses.getExpenseAmount())); //fetching from a double value
        if(holder.expenseType.getText().equals("Income")){
            holder.expenseAmount.setTextColor(Color.parseColor("#27B30B")); //green
        }
        else{
            holder.expenseAmount.setTextColor(Color.parseColor("#B30B0B")); //red
        }

    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class TrashcanViewHolder extends RecyclerView.ViewHolder{

        TextView expenseType, expenseCategory, expenseAmount;

        public TrashcanViewHolder(@NonNull View itemView) {
            super(itemView);

            //xml id binding (list item)
            expenseType = itemView.findViewById(R.id.tv_typeData_item);
            expenseCategory = itemView.findViewById(R.id.tv_categoryData_item);
            expenseAmount = itemView.findViewById(R.id.tv_amount_item);

        }
    }

    public void restoreItem(int position){
        ExpensesDatabase expenseDB = ExpensesDatabase.getInstance(context);
        Expenses expenses = expenseList.get(position);
        expenseDB.getExpensesDao().updateNotTrashed(userID, expenses.getExpenseID());

        expenseList.remove(position);
        notifyItemRemoved(position); //notify remove
        Toast.makeText(context, "Item restored.", Toast.LENGTH_SHORT).show();
    }
    public void deleteItem(int position){
        ExpensesDatabase expenseDB = ExpensesDatabase.getInstance(context);
        Expenses expenses = expenseList.get(position);
        expenseDB.getExpensesDao().delete(expenses);

        expenseList.remove(position);
        notifyItemRemoved(position); //notify remove
        Toast.makeText(context, "Item yeeted to oblivion.", Toast.LENGTH_SHORT).show();
    }
}
