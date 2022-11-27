package com.example.androidexpense;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ExpenseCallback extends ItemTouchHelper.SimpleCallback {
    ExpenseAdapter mExpenseAdapter;

    ExpenseCallback(ExpenseAdapter mExpenseAdapter){
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mExpenseAdapter = mExpenseAdapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        //do nothing
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        this.mExpenseAdapter.trashItem(position);
    }
}
