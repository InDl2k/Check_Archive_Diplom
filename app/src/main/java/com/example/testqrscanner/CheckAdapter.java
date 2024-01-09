package com.example.testqrscanner;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.ViewHolder> implements  RecyclerViewInterface {

    private RecyclerViewInterface recyclerViewInterface;
    private LayoutInflater inflater;
    private List<Check> checks;
    private Activity activity;

    CheckAdapter(Context context, List<Check> checks, RecyclerViewInterface recyclerViewInterface, Activity activity){
        this.checks = checks;
        this.inflater = LayoutInflater.from(context);
        this.recyclerViewInterface = recyclerViewInterface;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CheckAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.check_list_element, parent, false);
        return new CheckAdapter.ViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckAdapter.ViewHolder holder, int position) {
        Check check = checks.get(position);
        holder.shopName.setText(DataBaseController.getShopByID(activity, check.getShopID()).getRetailName());
        holder.date.setText(check.getDateStringOfFormat("dd-MM-yyyy HH:mm:ss"));
        holder.price.setText(String.format("Итого: %.2f", check.getPrice()));
    }

    @Override
    public int getItemCount() {
        return checks.size();
    }

    @Override
    public void onItemClick(int position) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView shopName, date, price;
        ViewHolder(View view, RecyclerViewInterface recyclerViewInterface){
            super(view);
            shopName = view.findViewById(R.id.txt_shopName);
            date = view.findViewById(R.id.txt_date);
            price = view.findViewById(R.id.txt_price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });

        }
    }

}
