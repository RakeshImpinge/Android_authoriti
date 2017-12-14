package com.curtisdigital.authoriti.ui.items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.curtisdigital.authoriti.R;

import java.util.List;

/**
 * Created by mac on 12/14/17.
 */

public class ChaseSpinnerItem extends BaseAdapter {

    private Context context;
    private List<String> options;
    private Boolean[] confirm;

    public ChaseSpinnerItem(Context context, List<String> options, Boolean[] confirm){
        this.context = context;
        this.options = options;
        this.confirm = confirm;
    }

    public void setConfirm(Boolean[] confirm){
        this.confirm = confirm;
    }

    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public Object getItem(int position) {
        return options.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        @SuppressLint("ViewHolder")
        View view = LayoutInflater.from(context).inflate(R.layout.item_chase_spinner, parent, false);

        TextView tvOption = (TextView) view.findViewById(R.id.tvOption);
        tvOption.setText(options.get(position));

        View confirmView = view.findViewById(R.id.llConfirm);
        if (confirm[position]){
            confirmView.setVisibility(View.INVISIBLE);
        } else {
            confirmView.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
