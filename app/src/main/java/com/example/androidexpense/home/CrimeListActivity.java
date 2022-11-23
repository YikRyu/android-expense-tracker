package com.example.androidexpense.home;

import androidx.fragment.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment() {
        return new com.example.criminalintent.CrimeListFragment();
    }
}
