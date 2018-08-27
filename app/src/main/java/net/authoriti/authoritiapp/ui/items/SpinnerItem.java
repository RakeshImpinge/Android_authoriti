package net.authoriti.authoritiapp.ui.items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.authoriti.authoritiapp.R;
import net.authoriti.authoritiapp.api.model.AccountID;

import java.util.List;

/**
 * Created by mac on 12/13/17.
 */

public class SpinnerItem extends BaseAdapter{

    private Context context;
    private List<AccountID> accountIDs;

    public SpinnerItem(Context context, List<AccountID> accountIDs){
        this.context = context;
        this.accountIDs = accountIDs;

    }

    public void setAccountIDs(List<AccountID> accountIDs){
        this.accountIDs = accountIDs;
    }

    @Override
    public int getCount() {
        return accountIDs.size();
    }

    @Override
    public Object getItem(int position) {
        return accountIDs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        @SuppressLint("ViewHolder")
        View view = LayoutInflater.from(context).inflate(R.layout.item_spinner, parent, false);

        TextView tvOption = (TextView) view.findViewById(R.id.tvOption);
        tvOption.setText(accountIDs.get(position).getType());

        View confirmView = view.findViewById(R.id.llConfirm);
        if (accountIDs.get(position).isConfirmed()){
            confirmView.setVisibility(View.INVISIBLE);
        } else {
            confirmView.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
