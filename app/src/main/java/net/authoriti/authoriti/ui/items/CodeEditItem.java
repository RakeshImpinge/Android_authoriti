package net.authoriti.authoriti.ui.items;

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.Picker;

import java.util.List;

/**
 * Created by mac on 12/1/17.
 */

public class CodeEditItem extends AbstractItem<CodeEditItem, CodeEditItem.ViewHolder> {

    public Picker picker;
    String defValue;

    int schemaIndex;

    public CodeEditItem(Picker picker, String defValue, int schemaIndex) {
        this.picker = picker;
        this.defValue = defValue;
        this.schemaIndex = schemaIndex;
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

        if (picker.getLabel().toLowerCase().contains("amount")) {
            if (schemaIndex == 5) {
                holder.etCode.setInputType(InputType.TYPE_CLASS_NUMBER);
                holder.etCode.addTextChangedListener(textWatcher);
            } else {
                holder.etCode.setInputType(InputType.TYPE_CLASS_NUMBER | InputType
                        .TYPE_NUMBER_FLAG_DECIMAL); //for decimal numbers
                addDecimalWatcher(holder.etCode);
            }
        } else {
            holder.etCode.setInputType(InputType.TYPE_CLASS_TEXT);
            holder.etCode.addTextChangedListener(textWatcher);
        }

    }

    public void addDecimalWatcher(final AppCompatEditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = s.toString();
                if (value.contains(".") && value.split("\\.").length > 1) {
                    String beforeDecimal = value.split("\\.")[0];
                    String afterDecimal = value.split("\\.")[1];
                    if (afterDecimal.length() > 2) {
                        afterDecimal = afterDecimal.substring(0, 2);
                    }
                    value = beforeDecimal + "." + afterDecimal;
                }
                editText.removeTextChangedListener(this);
                editText.setText(value);
                editText.setSelection(editText.getText().toString().length());
                editText.addTextChangedListener(this);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


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
