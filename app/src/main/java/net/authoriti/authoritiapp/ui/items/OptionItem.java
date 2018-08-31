package net.authoriti.authoritiapp.ui.items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.authoriti.authoritiapp.R;
import net.authoriti.authoritiapp.api.model.Value;
import net.authoriti.authoritiapp.utils.AuthoritiUtils;
import net.authoriti.authoritiapp.utils.AuthoritiUtils_;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;


/**
 * Created by mac on 12/1/17.
 */

public class OptionItem extends AbstractItem<OptionItem, OptionItem.ViewHolder> {

    private Value value;
    private boolean checked;

    public OptionItem(Value value, boolean checked) {
        this.value = value;
        this.checked = checked;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return this.value;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_option_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_option;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        Context context = holder.itemView.getContext();
        AuthoritiUtils utils = AuthoritiUtils_.getInstance_(context);

        holder.tvOption.setText(value.getTitle());

        if (checked) {

            holder.ivCheck.setVisibility(View.VISIBLE);

        } else {

            holder.ivCheck.setVisibility(View.INVISIBLE);

        }


        if (value.isCustomDate() && checked) {

            holder.llDate.setVisibility(View.VISIBLE);

            if (!value.getValue().equals("")) {

                long diff = Long.parseLong(value.getValue());

                holder.tvDate.setText(utils.getDateTime(diff));
            }

        } else {

            holder.llDate.setVisibility(View.INVISIBLE);

        }


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvOption;
        ImageView ivCheck;
        View llDate;
        TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);

            tvOption = (TextView) itemView.findViewById(R.id.tvOption);
            ivCheck = (ImageView) itemView.findViewById(R.id.ivCheck);
            llDate = itemView.findViewById(R.id.llDate);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }

}
