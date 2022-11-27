//REFERENCES
//Recycler View Adapter: https://www.youtube.com/watch?v=M8sKwoVjqU0
package com.example.androidexpense;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>{
    //refer user ID
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    String userID = mUser.getUid(); //grabbing uid of current logged in user
    DatabaseReference mUserInfoDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID); //child node to refer from

    private Context context;
    private LayoutInflater mInflater;
    private List<Expenses> expenseList;

    public class ExpenseViewHolder extends RecyclerView.ViewHolder{
        public final TextView itemType, itemCat, itemAmount;
        final ExpenseAdapter mExpenseAdapter;
        public TextView buttonViewOption;

        ExpenseViewHolder(View view, ExpenseAdapter adapter){
            super(view);
            itemType = view.findViewById(R.id.tv_typeData_item);
            itemCat = view.findViewById(R.id.tv_categoryData_item);
            itemAmount = view.findViewById(R.id.tv_amount_item);
            this.mExpenseAdapter = adapter;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    Expenses expenses = expenseList.get(position);
                    Intent intent = new Intent(v.getContext(),EditExpense.class);
                    intent.putExtra("EXPENSE_ID", expenses.getExpenseID());
                    intent.putExtra("EXPENSE_AMOUNT", expenses.getExpenseAmount());
                    intent.putExtra("EXPENSE_TYPE", expenses.getExpenseType());
                    intent.putExtra("EXPENSE_CATEGORY", expenses.getExpenseCategory());
                    intent.putExtra("EXPENSE_DATE", expenses.getExpenseDate());
                    intent.putExtra("IS_TRASHED", expenses.getTrashed());
                    ((Activity) context).startActivityForResult(intent, 20);
                }
            });
        }
    }

    ExpenseAdapter(Context context, List<Expenses> expenseList) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.list_item, parent, false); //define which xml as the item layout
        return new ExpenseViewHolder(itemView, this); //inflate layout
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        final Expenses expenses = expenseList.get(position); //position of item in RV
        //displaying data fetched
        holder.itemType.setText(expenses.getExpenseType());
        holder.itemCat.setText(expenses.getExpenseCategory());
        holder.itemAmount.setText(Double.toString(expenses.getExpenseAmount())); //fetching from a double value
        if(holder.itemType.getText().equals("Income")){ //change text color according to type
            holder.itemAmount.setTextColor(Color.parseColor("#27B30B")); //green
        }
        else{
            holder.itemAmount.setTextColor(Color.parseColor("#B30B0B")); //red
        }
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public void trashItem(int position){
        ExpensesDatabase expenseDB = ExpensesDatabase.getInstance(context);
        Expenses expenses = expenseList.get(position);
        expenseDB.getExpensesDao().updateTrashed(userID, expenses.getExpenseID());

        expenseList.remove(position);
        notifyItemRemoved(position); //notify remove
        Toast.makeText(context, "Item trashed.", Toast.LENGTH_SHORT).show();
    }
}
