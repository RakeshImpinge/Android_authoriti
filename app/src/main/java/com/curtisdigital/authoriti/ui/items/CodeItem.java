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
import com.curtisdigital.authoriti.utils.AuthoritiUtils;
import com.curtisdigital.authoriti.utils.AuthoritiUtils_;
import com.daimajia.swipe.SwipeLayout;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import static com.curtisdigital.authoriti.utils.Constants.PICKER_TIME;

/**
 * Created by mac on 12/1/17.
 */

public class CodeItem extends AbstractItem<CodeItem, CodeItem.ViewHolder>{

    private Picker picker;
    private int selectedIndex;

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
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        final Context context = holder.itemView.getContext();

        final AuthoritiUtils_ utils = AuthoritiUtils_.getInstance_(context);

        holder.tvTitle.setText(picker.getLabel() + " : ");

        if (utils.presentSelectedIndex(context, picker.getPicker())){

            selectedIndex = utils.getPickerSelectedIndex(context, picker.getPicker());

            if (selectedIndex == utils.getPickerDefaultIndex(context, picker.getPicker())){

                holder.markDefault.setVisibility(View.VISIBLE);

            } else {

                holder.markDefault.setVisibility(View.INVISIBLE);

            }

        } else {

            if (picker.isEnableDefault() && picker.getDefaultIndex() != -1 && picker.getDefaultIndex() < picker.getValues().size()){

                holder.markDefault.setVisibility(View.VISIBLE);

                selectedIndex = utils.getPickerDefaultIndex(context, picker.getPicker());

            } else {

                holder.markDefault.setVisibility(View.INVISIBLE);

                selectedIndex = utils.getPickerSelectedIndex(context, picker.getPicker());

            }
        }


        if (picker.getValues() != null && picker.getValues().size() != 0){

            Value value = picker.getValues().get(selectedIndex);

            if (value != null){

                if (picker.getPicker().equals(PICKER_TIME) && value.getValue() != null && value.getValue().length() > 0){

                    if (!value.getValue().equals("")){

                        if (!value.getValue().equals("")){

                            long diff = Long.parseLong(value.getValue());

                            holder.tvSubTitle.setText(value.getTitle() + " - " + utils.getDateTime(diff));
                        }
                    }


                } else {

                    holder.tvSubTitle.setText(value.getTitle());

                }


            }
        }

        holder.tvDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.markDefault.setVisibility(View.VISIBLE);
                holder.swipeLayout.close(true);
                utils.setDefaultPickerItemIndex(context, picker.getPicker(), selectedIndex);

            }
        });

        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.tvDefault);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.tvDefault);
        holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                holder.swipeLayout.setClickable(false);
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                holder.swipeLayout.setClickable(false);
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {
                holder.swipeLayout.setClickable(true);
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });

        holder.swipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasscodePickActivity_.intent(context).pickerType(picker.getPicker()).start();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        TextView tvSubTitle;
        SwipeLayout swipeLayout;
        TextView tvDefault;
        View markDefault;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvSubTitle = (TextView) itemView.findViewById(R.id.tvSubTitle);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvDefault = (TextView) itemView.findViewById(R.id.tvDefault);
            markDefault = itemView.findViewById(R.id.markDefault);

        }
    }
}
