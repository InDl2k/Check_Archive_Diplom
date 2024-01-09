package com.example.testqrscanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StatisticProducts extends Fragment implements RecyclerViewInterface {

    private ArrayList<Product> products;

    StatisticProducts(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_statistic_products, container, false);
        products = DataBaseController.getProducts(this.getActivity());
        ProductAdapter productAdapter = new ProductAdapter(this.getContext(), products, this);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_products);
        recyclerView.setAdapter(productAdapter);

        return view;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this.getActivity(), ProductActivity.class);
        intent.putExtra("id", products.get(position).getId());
        startActivity(intent);
    }
}
