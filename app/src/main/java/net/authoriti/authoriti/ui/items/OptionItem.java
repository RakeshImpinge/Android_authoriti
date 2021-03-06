package net.authoriti.authoriti.ui.items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.Value;
import net.authoriti.authoriti.utils.AuthoritiUtils;
import net.authoriti.authoriti.utils.AuthoritiUtils_;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;


/**
 * Created by mac on 12/1/17.
 */

public class OptionItem extends AbstractItem<OptionItem, OptionItem.ViewHolder> {

    private Value value;
    private boolean checked;
    private String headingName = "";
    private String customerName = "";

    public OptionItem(Value value, boolean checked) {
        this.value = value;
        this.checked = checked;
    }

    public OptionItem(Value value, boolean checked, String headingName,String customerName) {
        this.value = value;
        this.checked = checked;
        this.headingName = headingName;
        this.customerName=customerName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

        if (!headingName.equals("")) {
            holder.rel_header.setVisibility(View.VISIBLE);
            holder.txt_header.setText(headingName);
        } else {
            holder.txt_header.setText("");
            holder.rel_header.setVisibility(View.GONE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rel_header;
        TextView tvOption, txt_header;
        ImageView ivCheck;
        View llDate;
        TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);

            rel_header = (RelativeLayout) itemView.findViewById(R.id.rel_header);
            tvOption = (TextView) itemView.findViewById(R.id.tvOption);
            txt_header = (TextView) itemView.findViewById(R.id.txt_header);
            ivCheck = (ImageView) itemView.findViewById(R.id.ivCheck);
            llDate = itemView.findViewById(R.id.llDate);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }

}
