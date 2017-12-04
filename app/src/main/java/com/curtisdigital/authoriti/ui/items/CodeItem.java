package com.curtisdigital.authoriti.ui.items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.model.Picker;
import com.curtisdigital.authoriti.api.model.Value;
import com.curtisdigital.authoriti.ui.pick.PasscodePickActivity_;
import com.curtisdigital.authoriti.utils.AuthoritiData_;
import com.curtisdigital.authoriti.utils.AuthoritiUtils_;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by mac on 12/1/17.
 */

public class CodeItem extends AbstractItem<CodeItem, CodeItem.ViewHolder>{

    private Picker picker;

    public CodeItem(Picker picker){
        this.picker = picker;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_code_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_code;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        final Context context = holder.itemView.getContext();

        holder.tvTitle.setText(AuthoritiUtils_.getInstance_(context).getPickerTitle(picker.getPicker()));
        int selectedIndex = AuthoritiUtils_.getInstance_(context).getPickerSelectedIndex(context, picker.getPicker());
        if (picker.getValues() != null && picker.getValues().size() != 0){
            Value value = picker.getValues().get(selectedIndex);
            if (value != null){
                holder.tvSubTitle.setText(value.getTitle());
            }
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasscodePickActivity_.intent(context).pickerType(picker.getPicker()).start();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        TextView tvSubTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvSubTitle = (TextView) itemView.findViewById(R.id.tvSubTitle);
        }
    }
}
