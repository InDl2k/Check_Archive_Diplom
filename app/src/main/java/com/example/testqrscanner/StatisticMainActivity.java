package com.example.testqrscanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class StatisticMainActivity extends Fragment {

    TabLayout tabLayout;
    TabItem tab_overAll;
    TabItem tab_products;
    ViewPager2 viewPager2;

    StatisticMainActivity(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_statistic_main, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tab_overAll = (TabItem) view.findViewById(R.id.tab_overAll);
        tab_products = (TabItem) view.findViewById(R.id.tab_products);
        viewPager2 = (ViewPager2) view.findViewById(R.id.view_pager2);
        viewPager2.setOffscreenPageLimit(1);
        PagerStatisticAdapter pagerStatisticAdapter = new PagerStatisticAdapter(this.getActivity());
        viewPager2.setAdapter(pagerStatisticAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

        return view;
    }
}
