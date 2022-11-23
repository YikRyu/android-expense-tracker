package com.example.androidexpense.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.criminalintent.databinding.FragmentCrimeBinding;

import java.util.UUID;

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";

    private Crime mCrime;
    private FragmentCrimeBinding crimeBinding;

    //attach fragment arguments bundle
    //will get called whenever a crime fragment(this class) needs to be called
    public static CrimeFragment newInstance(UUID crimeId){
        //create new bundle
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId); //put serializable because it is data type UUID, UUID is a class
        //more parameter add here

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID); //call bundle getArguments() function
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        crimeBinding = FragmentCrimeBinding.inflate(getLayoutInflater());
        return crimeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //fetch the title of the exact item called
        crimeBinding.crimeTitle.setText(mCrime.getTitle());
        //what happens when things occurred onto the text view
        crimeBinding.crimeTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //date button, disabled
        crimeBinding.crimeDate.setText(mCrime.getDate().toString());
        crimeBinding.crimeDate.setEnabled(false);

        //fetching solved or not of the exact item pressed
        crimeBinding.crimeSolved.setChecked(mCrime.isSolved());
        //solved or not (solved checkbox)
        crimeBinding.crimeSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });


    }
}
