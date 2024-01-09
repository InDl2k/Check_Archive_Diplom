package com.example.testqrscanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ArchiveActivity extends Fragment implements RecyclerViewInterface {

    private ArrayList<Check> checks;

    ArchiveActivity(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_archive, container, false);
        checks = DataBaseController.getChecks(this.getActivity());
        CheckAdapter adapter = new CheckAdapter(this.getContext(), checks, this, this.getActivity());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_checks);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this.getActivity(), CheckActivity.class);
        intent.putExtra("id", checks.get(position).getId());
        startActivity(intent);
    }
}
