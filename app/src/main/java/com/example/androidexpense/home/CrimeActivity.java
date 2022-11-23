package com.example.androidexpense.home;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {
    private static final String EXTRA_CRIME_ID = "crime_id";

    //telling fragment which list item details to be displayed by passing ID as intent extra when this activity(fragment holder) started
    public static Intent newIntent(Context packageContext, UUID crimeId){
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    //create the fragment of the detail pageand call the class for display
    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CRIME_ID); //get the UUID of the crime item
        return CrimeFragment.newInstance(crimeId); //call the new instance function in crime fragment class
    }
}