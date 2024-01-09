package com.example.testqrscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements RecyclerViewInterface {

    private RecyclerViewInterface recyclerViewInterface;
    private LayoutInflater inflater;
    private List<Product> products;

    ProductAdapter(Context context, List<Product> products, RecyclerViewInterface recyclerViewInterface){
        this.products = products;
        this.inflater = LayoutInflater.from(context);
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.product_list_element, parent, false);
        return new ProductAdapter.ViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.name.setText(product.getName());
        holder.barcode.setText(product.getBarcode());
        holder.lastPrice.setText(String.format("%.2f руб.", product.getPrice()));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public void onItemClick(int position) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, barcode, lastPrice;
        ViewHolder(View view, RecyclerViewInterface recyclerViewInterface){
            super(view);
            name = view.findViewById(R.id.txt_productName);
            barcode = view.findViewById(R.id.txt_barcode);
            lastPrice = view.findViewById(R.id.txt_lastPrice);

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
