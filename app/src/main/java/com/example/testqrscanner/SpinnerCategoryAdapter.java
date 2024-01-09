package com.example.testqrscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SpinnerCategoryAdapter extends BaseAdapter {
    private Context context;
    private List<Category> categories;

    public SpinnerCategoryAdapter(Context context, List<Category> categories){
        this.context = context;
        this.categories = categories;
    }


    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_category_element, viewGroup, false);
        ImageView img_color = (ImageView) rootView.findViewById(R.id.img_color);
        TextView txt_name = (TextView) rootView.findViewById(R.id.txt_categoryName);
        txt_name.setText(categories.get(i).getName());
        img_color.setBackgroundColor(categories.get(i).getColor());
        return rootView;
    }
}
