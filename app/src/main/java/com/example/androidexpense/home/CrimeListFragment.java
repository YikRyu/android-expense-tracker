package com.example.androidexpense.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.criminalintent.databinding.FragmentCrimeListBinding;
import com.example.criminalintent.databinding.ListItemCrimeBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class CrimeListFragment extends Fragment {
    private FragmentCrimeListBinding crimeListBinding;
    private CrimeAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        crimeListBinding = FragmentCrimeListBinding.inflate(getLayoutInflater());
        return crimeListBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        crimeListBinding.crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI(); //calling RV adapter to update the UI
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI(); //call function to update the list after going back to this fragment from crime fragment
    }

    //view holder
    private class CrimeHolder extends RecyclerView.ViewHolder{
        public TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;
        private Crime mCrime;

        public CrimeHolder (ListItemCrimeBinding itemBinding){
            super(itemBinding.getRoot());

            //binding item to xml
            mTitleTextView = itemBinding.listItemCrimeTitleTextView;
            mDateTextView = itemBinding.listItemCrimeDateTextView;
            mSolvedCheckBox = itemBinding.listItemCrimeSolvedCheckBox;

            itemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getId());
                    startActivity(intent);
                }
            });
        }

        //function for fetching recycler view list item info
        public void bindCrime(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedCheckBox.setChecked(mCrime.isSolved());

        }
    }

    //recycler view adapter
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        @NotNull
        @Override
        public CrimeHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            ListItemCrimeBinding itemBinding = ListItemCrimeBinding.inflate(getLayoutInflater());
            return new CrimeHolder(itemBinding);
        }


        @Override
        public void onBindViewHolder(@NotNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime); //call function to get recycler view item info
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }

    //function for calling RV adapter
    private void updateUI(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrime();

        if(mAdapter == null){ //called when this fragment just created so it will return the entire list
            mAdapter = new CrimeAdapter(crimes);
            crimeListBinding.crimeRecyclerView.setAdapter(mAdapter);
        }
        else{ //called when coming back from other activity to update
            mAdapter.notifyDataSetChanged(); //notify the RV some data changed so it can reload
        }

        //item touch helper (card view) function code
        ItemTouchHelper helper = new ItemTouchHelper((new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getBindingAdapterPosition();
                int to = target.getBindingAdapterPosition();
                Collections.swap(crimes, from, to);
                mAdapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                crimes.remove(viewHolder.getBindingAdapterPosition());
                mAdapter.notifyItemRemoved(viewHolder.getBindingAdapterPosition());
            }
        }));

        //attach helper object into Recycler View
        helper.attachToRecyclerView(crimeListBinding.crimeRecyclerView);
    }

}
