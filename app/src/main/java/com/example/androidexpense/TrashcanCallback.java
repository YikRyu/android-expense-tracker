package com.example.androidexpense;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class TrashcanCallback extends ItemTouchHelper.SimpleCallback {
    TrashcanAdapter mTrashcanAdapter;

    TrashcanCallback(TrashcanAdapter mTrashcanAdapter){
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mTrashcanAdapter = mTrashcanAdapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        //do nothing
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        switch(direction){
            case ItemTouchHelper.LEFT:
                this.mTrashcanAdapter.restoreItem(position);

            case ItemTouchHelper.RIGHT:
                this.mTrashcanAdapter.deleteItem(position);
        }


    }
}
