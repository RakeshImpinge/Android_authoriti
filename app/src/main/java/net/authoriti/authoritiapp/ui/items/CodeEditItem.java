package net.authoriti.authoritiapp.ui.items;

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.mikepenz.fastadapter.items.AbstractItem;

import net.authoriti.authoritiapp.R;
import net.authoriti.authoritiapp.api.model.Picker;

import java.util.List;

/**
 * Created by mac on 12/1/17.
 */

public class CodeEditItem extends AbstractItem<CodeEditItem, CodeEditItem.ViewHolder> {

    public Picker picker;
    String defValue;

    public CodeEditItem(Picker picker, String defValue) {
        this.picker = picker;
        this.defValue = defValue;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_code_edit_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_code_edit;
    }

    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        holder.tiCode.setHint(picker.getLabel());
        holder.etCode.setText(defValue);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatEditText etCode;
        TextInputLayout tiCode;

        public ViewHolder(View itemView) {
            super(itemView);
            etCode = (AppCompatEditText) itemView.findViewById(R.id.etCode);
            tiCode = (TextInputLayout) itemView.findViewById(R.id.tiCode);
        }
    }
}
