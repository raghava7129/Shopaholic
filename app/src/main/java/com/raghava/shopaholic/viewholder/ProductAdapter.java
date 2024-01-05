package com.raghava.shopaholic.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.raghava.shopaholic.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends BaseAdapter {
    List<Product> products = new ArrayList<>();

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
