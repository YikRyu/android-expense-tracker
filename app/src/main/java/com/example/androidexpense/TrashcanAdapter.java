//REFERENCES
//Recycler View Adapter: https://www.youtube.com/watch?v=M8sKwoVjqU0
package com.example.androidexpense;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidexpense.database.Expenses;

import java.util.List;

public class TrashcanAdapter extends RecyclerView.Adapter<TrashcanAdapter.TrashcanViewHolder>{
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
            holder.expenseAmount.setTextColor(Color.parseColor(String.valueOf(R.color.green)));
        }
        else{
            holder.expenseAmount.setTextColor(Color.parseColor(String.valueOf(R.color.red)));
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
}
