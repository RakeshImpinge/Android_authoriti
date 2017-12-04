package com.curtisdigital.authoriti.ui.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.model.Value;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by mac on 12/2/17.
 */

public class AccountItem extends AbstractItem<AccountItem, AccountItem.ViewHolder>{

    private Value value;

    public AccountItem(Value value){
        this.value = value;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_account_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_account;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.tvTitle.setText(value.getTitle());
        holder.tvSubTitle.setText(value.getValue());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvSubTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvSubTitle = (TextView) itemView.findViewById(R.id.tvSubTitle);

        }
    }
}