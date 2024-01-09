package com.example.testqrscanner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerStatisticAdapter extends FragmentStateAdapter {

    private int tabsNumber;

    public PagerStatisticAdapter(@NonNull FragmentActivity fragmentActivity){
        super(fragmentActivity);

    }

    @Override
    public int getItemCount() {
        return 2;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new StatisticOverAll();
            case 1:
                return new StatisticProducts();
            default:
                return new StatisticOverAll();
        }
    }
}
