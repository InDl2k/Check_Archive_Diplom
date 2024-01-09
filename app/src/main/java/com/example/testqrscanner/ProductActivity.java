package com.example.testqrscanner;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.chip.Chip;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ProductActivity extends AppCompatActivity {

    private Product product;
    private LineChart lineChart;
    private ArrayList<MyEntry> checks;
    private List<Category> categories;
    private LocalDateTime now;
    private int minusDays = 0;
    private RadioGroup radioGroup;
    private Chip like;
    private Chip dislike;
    private ImageButton btn_addCategory;
    private ImageButton btn_deleteCategory;
    private Spinner spinner_Categories;
    private float mi = 1e9F;
    private float mx = 0;
    private float count = 0;
    private float totalSpend = 0;
    private Category curCategory;
    private int categoryColor = Color.BLACK;
    private int pos;

    private class MyEntry {
        private Product product;
        private LocalDateTime dateTime;

        MyEntry(Product product, LocalDateTime dateTime){
            this.product = product;
            this.dateTime = dateTime;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public Product getProduct() {
            return product;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        int id = getIntent().getIntExtra("id", 0);
        product = DataBaseController.getProductByID(this, id);
        checks = getChecksThatProduct();
        now = LocalDateTime.now();
        TextView txt_name = (TextView) findViewById(R.id.txt_nameProduct);
        TextView txt_lastPrice = (TextView) findViewById(R.id.txt_lastPriceProduct);
        lineChart = (LineChart) findViewById(R.id.lineChart_product);
        radioGroup = (RadioGroup) findViewById(R.id.rdGroup_prod);
        like = (Chip) findViewById(R.id.chip_like);
        dislike = (Chip) findViewById(R.id.chip_dislike);
        btn_addCategory = (ImageButton) findViewById(R.id.btn_addCategory);
        btn_deleteCategory = (ImageButton) findViewById(R.id.btn_deleteCategory);
        spinner_Categories = (Spinner) findViewById(R.id.spinner_Categories);
        curCategory = DataBaseController.getCategoryByID(ProductActivity.this, product.getCategoryID());
        revalidateSpinner();

        spinner_Categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateCategoryProductData(categories.get(i).getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_deleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCategory(view);
            }
        });

        btn_addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategory(view);
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like();
            }
        });
        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dislike();
            }
        });
        initLike();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                updateData(id);
            }
        });
        radioGroup.check(R.id.rdBtn_alltime);
        txt_name.setText(product.getName());
        txt_lastPrice.setText(String.format("Последняя цена: %.2f руб.", product.getPrice()));
    }

    private void revalidateSpinner(){
        categories = DataBaseController.getCategories(ProductActivity.this);
        spinner_Categories.setAdapter(new SpinnerCategoryAdapter(ProductActivity.this, categories));
        spinner_Categories.setSelection(findCategoryInList(categories, curCategory));
    }

    private int findCategoryInList(List<Category> categories, Category category){
        for(int i = 0; i < categories.size(); ++i){
            if(categories.get(i).getId() == category.getId()){
                return i;
            }
        }
        return -1;
    }

    private void updateCategoryProductData(String name){
        DataBaseController.addCategory(ProductActivity.this, new Category(0, name, categoryColor));
        curCategory = DataBaseController.getCategoryByName(ProductActivity.this, name);
        product.setCategoryID(curCategory.getId());
        DataBaseController.setProductCategory(ProductActivity.this, curCategory, product.getId());
    }

    private void initLike(){
        if(product.getLiked() == 1) like.setChecked(true);
        else if(product.getLiked() == -1) dislike.setChecked(true);
    }

    private void defaultLike(){
        dislike.setBackgroundColor(Color.GRAY);
        like.setBackgroundColor(Color.GRAY);
        product.setLiked(0);
        DataBaseController.setProductLikeStatus(this,0, product.getId());
    }

    private void like(){
        dislike.setBackgroundColor(Color.GRAY);
        if(product.getLiked() == 1){
            defaultLike();
            return;
        }
        like.setBackgroundColor(Color.GREEN);
        product.setLiked(1);
        DataBaseController.setProductLikeStatus(this,1, product.getId());
    }

    private void dislike(){
        like.setBackgroundColor(Color.GRAY);
        if(product.getLiked() == -1){
            defaultLike();
            return;
        }
        dislike.setBackgroundColor(Color.RED);
        product.setLiked(-1);
        DataBaseController.setProductLikeStatus(this,-1, product.getId());
    }

    private ArrayList<MyEntry> getChecksThatProduct(){
        ArrayList<Check> checks = DataBaseController.getChecks(this);
        ArrayList<MyEntry> res = new ArrayList<>();
        for (Check check : checks){
            for(int i = 0; i < check.getProducts().size(); ++i){
                if(check.getProducts().get(i).getId() == this.product.getId()){
                    res.add(new MyEntry(check.getProducts().get(i), check.getDate()));
                }
            }
        }
        return res;
    }

    private void updateData(int id){
        resetValues();
        if(R.id.rdBtn_month == id){
            minusDays = 31;
        }
        else if(R.id.rdBtn_year == id){
            minusDays = 365;
        }
        else{
            minusDays = 0;
        }
        ArrayList<MyEntry> res = new ArrayList<>();
        for(MyEntry check : checks){
            if(check.getDateTime().isAfter(now.minusDays(minusDays)) || minusDays == 0){
                res.add(check);
                mi = min(mi, check.getProduct().getPrice());
                mx = max(mx, check.getProduct().getPrice());
                count += check.getProduct().getCount();
                totalSpend += check.getProduct().getCount() * check.getProduct().getPrice();
            }
        }
        updateValues();
        buildLineChart(res);
    }

    private void buildLineChart(ArrayList<MyEntry> checks){
        lineChart.clear();
        lineChart.invalidate();
        if(checks.isEmpty()) return;
        ArrayList<Entry> data = new ArrayList<>();
        ArrayList<LocalDateTime> dateTimes = new ArrayList<>();
        for (MyEntry check : checks){
            data.add(new Entry(data.size(), check.getProduct().getPrice()));
            dateTimes.add(check.getDateTime());
        }
        if(dateTimes.isEmpty()) return;
        LineDataSet dataSet = new LineDataSet(data, "Цена");
        dataSet.setLineWidth(4);
        dataSet.setCircleRadius(8);
        dataSet.setValueTextSize(10f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        LineData lineData = new LineData(dataSets);
        lineChart.getXAxis().setValueFormatter(new LineCharXAxisValueFormatter(dateTimes));
        lineChart.getXAxis().setAxisMinimum(-1f);
        lineChart.getXAxis().setAxisMaximum((float) dateTimes.size());
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getXAxis().setGranularity(1.0f);
        lineChart.setDrawBorders(true);
        lineChart.setDescription(null);

        lineChart.setData(lineData);
        lineChart.animateY(0, Easing.EaseInExpo);
    }

    private void updateValues(){
        TextView txt_minPrice = (TextView) findViewById(R.id.txt_minPrice); txt_minPrice.setText(String.format("Минимальная цена: %.2f руб.", (mi == 1e9F) ? 0.0f : mi));
        TextView txt_maxPrice = (TextView) findViewById(R.id.txt_maxPrice); txt_maxPrice.setText(String.format("Максимальная цена: %.2f руб.", mx));
        TextView txt_count = (TextView) findViewById(R.id.txt_count); txt_count.setText(String.format("Количество: %.2f", count));
        TextView txt_totalSpend = (TextView) findViewById(R.id.txt_totalSpend); txt_totalSpend.setText(String.format("Затраты за период: %.2f руб.", totalSpend));
    }

    private void resetValues(){
        mi = 1e9F;
        mx = 0;
        count = 0;
        totalSpend = 0;
    }

    private void addCategory(View view){
        Dialog dialog = new Dialog(ProductActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activtiy_dialog_categories);
        EditText txt_name = (EditText) dialog.findViewById(R.id.editText_NameCategory);
        Button btn_chooseColor = (Button) dialog.findViewById(R.id.btn_chooseColorCategory);
        Button btn_addNewCategory = (Button) dialog.findViewById(R.id.btn_addNewCategory);
        ImageView colorPresenter = (ImageView) dialog.findViewById(R.id.img_colorPresenter);
        colorPresenter.setBackgroundColor(categoryColor);

        btn_chooseColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(ProductActivity.this, categoryColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {

                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        categoryColor = color;
                        colorPresenter.setBackgroundColor(categoryColor);
                    }
                });
                colorPicker.show();
            }
        });

        btn_addNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = txt_name.getText().toString();
                if(name.isEmpty()){
                    txt_name.setError("Пустое поле!");
                    return;
                }
                if(!DataBaseController.checkCategory(ProductActivity.this, name)){
                    updateCategoryProductData(name);
                    revalidateSpinner();
                    dialog.dismiss();
                }
                else{
                    ToastMessage.show("Данная категория существует!");
                }
            }
        });

        dialog.show();
    }

    private void deleteCategory(View view){
        if(product.getCategoryID() == 0) return;
        DataBaseController.setDefaultAllProductsThatCategory(ProductActivity.this, curCategory);
        DataBaseController.eraseCategory(ProductActivity.this, curCategory);
        curCategory = DataBaseController.getCategoryByID(ProductActivity.this, 0);
        product.setCategoryID(0);
        revalidateSpinner();
    }

}
