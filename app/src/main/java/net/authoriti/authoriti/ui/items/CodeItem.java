package net.authoriti.authoriti.ui.items;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.DefaultValue;
import net.authoriti.authoriti.api.model.Picker;
import net.authoriti.authoriti.ui.code.CodePermissionActivity;
import net.authoriti.authoriti.ui.pick.PasscodePickActivity_;
import net.authoriti.authoriti.utils.AuthoritiData_;
import net.authoriti.authoriti.utils.AuthoritiUtils_;

import com.daimajia.swipe.SwipeLayout;
import com.microblink.util.Log;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.HashMap;
import java.util.List;

import static net.authoriti.authoriti.utils.Constants.PICKER_DATA_TYPE;
import static net.authoriti.authoriti.utils.Constants.PICKER_REQUEST;
import static net.authoriti.authoriti.utils.Constants.TIME_CUSTOM_DATE;
import static net.authoriti.authoriti.utils.Constants.TIME_CUSTOM_TIME;

/**
 * Created by mac on 12/1/17.
 */

public class CodeItem extends AbstractItem<CodeItem, CodeItem.ViewHolder> {
    public Picker picker;
    HashMap<String, DefaultValue> defaultPickerMap;
    int schemaIndex;
    Activity activity;

    public CodeItem(Picker picker) {
        this.picker = picker;
    }

    public CodeItem(Picker picker, HashMap<String, DefaultValue> defaultPickerMap,
                    int schemaIndex, Activity activity) {
        this.picker = picker;
        this.defaultPickerMap = defaultPickerMap;
        this.schemaIndex = schemaIndex;
        this.activity = activity;
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
        final AuthoritiData_ dataManager = AuthoritiData_.getInstance_(context);
        holder.tvTitle.setText(picker.getLabel() + " : ");

        if (defaultPickerMap != null) {
            if (defaultPickerMap.get(picker.getPicker()).getValue().equals(TIME_CUSTOM_DATE) ||
                    defaultPickerMap.get(picker.getPicker()).getValue().equals(TIME_CUSTOM_TIME)) {
                long diff = Long.parseLong(defaultPickerMap.get(picker.getPicker()).getTitle());
                holder.tvSubTitle.setText(utils.getDateTime(diff));
            } else {
                holder.tvSubTitle.setText(defaultPickerMap.get(picker.getPicker()).getTitle());
            }
            if (defaultPickerMap.get(picker.getPicker()).isDefault()) {
                holder.markDefault.setVisibility(View.VISIBLE);
            } else {
                holder.markDefault.setVisibility(View.GONE);
            }
        } else {
            holder.markDefault.setVisibility(View.GONE);
        }

//        if (utils.presentSelectedIndex(context, picker.getPicker())) {
//            if (picker.getPicker().equals(PICKER_DATA_TYPE)) {
//                List<Value> values = dataManager.getSelectedValuesForDataType(utils
//                        .getPickerSelectedIndex(context, PICKER_REQUEST));
//                List<Value> values1 = dataManager.getDefaultValuesForDataType(context, utils
//                        .getPickerSelectedIndex(context, PICKER_REQUEST));
//                boolean matched = true;
//                if (values != null && values.size() > 0 && values1 != null && values1.size() >
// 0) {
//                    List<Value> temp1;
//                    List<Value> temp2;
//                    if (values.size() > values1.size()) {
//                        temp1 = values;
//                        temp2 = values1;
//                    } else {
//                        temp1 = values1;
//                        temp2 = values;
//                    }
//                    for (Value value : temp1) {
//                        matched = matched && temp2.contains(value);
//                    }
//                } else {
//                    matched = false;
//                }
//                if (picker.isEnableDefault() && matched) {
//                    holder.markDefault.setVisibility(View.VISIBLE);
//                } else {
//                    holder.markDefault.setVisibility(View.INVISIBLE);
//                }
//            } else {
//                selectedIndex = utils.getPickerSelectedIndex(context, picker.getPicker());
//                if (selectedIndex == utils.getPickerDefaultIndex(context, picker.getPicker())) {
//                    holder.markDefault.setVisibility(View.VISIBLE);
//                } else {
//                    holder.markDefault.setVisibility(View.INVISIBLE);
//                }
//            }
//        } else {
//            if (picker.getPicker().equals(PICKER_DATA_TYPE)) {
//                List<Value> defaultValues = dataManager.getDefaultValuesForDataType(context,
//                        utils.getPickerSelectedIndex(context, PICKER_REQUEST));
//                if (picker.isEnableDefault() && defaultValues != null && defaultValues.size() >
// 0) {
//                    holder.markDefault.setVisibility(View.VISIBLE);
//                } else {
//                    holder.markDefault.setVisibility(View.INVISIBLE);
//                }
//            } else {
//                if (picker.isEnableDefault() && picker.getDefaultIndex() != -1 && picker
//                        .getDefaultIndex() < picker.getValues().size()) {
//                    holder.markDefault.setVisibility(View.VISIBLE);
//                    selectedIndex = utils.getPickerDefaultIndex(context, picker.getPicker());
//                } else {
//                    holder.markDefault.setVisibility(View.INVISIBLE);
//                    selectedIndex = utils.getPickerSelectedIndex(context, picker.getPicker());
//                }
//            }
//        }
//        if (picker.getPicker().equals(PICKER_DATA_TYPE)) {
//            List<Value> defaultValues = dataManager.getDefaultValuesForDataType(context, utils
//                    .getPickerSelectedIndex(context, PICKER_REQUEST));
//            if (!utils.presentSelectedIndex(context, picker.getPicker()) && picker
//                    .isEnableDefault() && defaultValues != null && defaultValues.size() > 0) {
//                StringBuilder subTitle = new StringBuilder();
//                for (int i = 0; i < defaultValues.size(); i++) {
//                    Value value = defaultValues.get(i);
//                    if (i != defaultValues.size() - 1) {
//                        subTitle.append(value.getTitle()).append(", ");
//                    } else {
//                        subTitle.append(value.getTitle());
//                    }
//                }
//                holder.tvSubTitle.setText(subTitle.toString());
//                dataManager.setSelectedValuesForDataType(utils.getPickerSelectedIndex(context,
//                        PICKER_REQUEST), defaultValues);
//            } else {
//                List<Value> values = dataManager.getSelectedValuesForDataType(utils
//                        .getPickerSelectedIndex(context, PICKER_REQUEST));
//                if (values != null && values.size() > 0) {
//                    StringBuilder subTitle = new StringBuilder();
//                    for (int i = 0; i < values.size(); i++) {
//                        Value value = values.get(i);
//                        if (i != values.size() - 1) {
//                            subTitle.append(value.getTitle()).append(", ");
//                        } else {
//                            subTitle.append(value.getTitle());
//                        }
//                    }
//                    holder.tvSubTitle.setText(subTitle.toString());
//                } else {
//                    List<Value> values1 = dataManager.getValuesFromDataType(utils
//                            .getPickerSelectedIndex(context, PICKER_REQUEST));
//                    Value value = values1.get(0);
//                    List<Value> initValues = new ArrayList<>();
//                    initValues.ic_add(value);
//                    dataManager.setSelectedValuesForDataType(utils.getPickerSelectedIndex
//                            (context, PICKER_REQUEST), initValues);
//                    holder.tvSubTitle.setText(value.getTitle());
//                }
//            }
//        } else {
//            if (picker.getValues() != null && picker.getValues().size() != 0) {
//                Value value = picker.getValues().get(selectedIndex);
//                if (value != null) {
//                    if (picker.getPicker().equals(PICKER_TIME) && value.getValue() != null &&
//                            value.getValue().length() > 0) {
//                        if (!value.getValue().equals("")) {
//                            if (!value.getValue().equals("")) {
//                                long diff = Long.parseLong(value.getValue());
//                                holder.tvSubTitle.setText(value.getTitle() + " - " + utils
//                                        .getDateTime(diff));
//                            }
//                        }
//                    } else {
//                        holder.tvSubTitle.setText(value.getTitle());
//                    }
//                } else {
//                    holder.tvSubTitle.setText("");
//                }
//            } else {
//                holder.tvSubTitle.setText("");
//            }
//        }

        holder.tvDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DefaultValue defaultValue = defaultPickerMap.get(picker.getPicker());
                holder.markDefault.setVisibility(View.VISIBLE);
                holder.swipeLayout.close(true);
                defaultValue.setDefault(true);
                if (defaultPickerMap != null) {
                    if (defaultPickerMap.containsKey(PICKER_REQUEST) && picker.getPicker().equals
                            (PICKER_REQUEST)) {
                        dataManager.updateDefaultValues(schemaIndex, picker.getPicker(),
                                defaultValue);
                        dataManager.updateDefaultValues(schemaIndex, PICKER_DATA_TYPE,
                                defaultPickerMap.get(PICKER_DATA_TYPE));
                    } else if (defaultPickerMap.containsKey(PICKER_REQUEST) && picker.getPicker()
                            .equals(PICKER_DATA_TYPE)) {
                        dataManager.updateDefaultValues(schemaIndex, picker.getPicker(),
                                defaultValue);
                        dataManager.updateDefaultValues(schemaIndex, PICKER_REQUEST,
                                defaultPickerMap.get(PICKER_REQUEST));
                    } else {
                        dataManager.updateDefaultValues(schemaIndex, picker.getPicker(),
                                defaultValue);
                    }
                }
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
                if (activity instanceof CodePermissionActivity && !((CodePermissionActivity) (activity)).isRequestClickAvailable && picker.getPicker().equals(PICKER_REQUEST)) {

                } else {
                    PasscodePickActivity_.intent(context).picker(picker).defaultPickerMap
                            (defaultPickerMap).schemaIndex(schemaIndex).startForResult
                            (CodePermissionActivity
                                    .INTENT_REQUEST_PICK_VALUE);
                }
            }
        });

        if (activity instanceof CodePermissionActivity && !((CodePermissionActivity) (activity)).isRequestClickAvailable && picker.getPicker().equals(PICKER_REQUEST)) {
            holder.ivArrow.setVisibility(View.INVISIBLE);
        } else {
            holder.ivArrow.setVisibility(View.VISIBLE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvSubTitle;
        SwipeLayout swipeLayout;
        TextView tvDefault;
        View markDefault;
        ImageView ivArrow;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ivArrow = (ImageView) itemView.findViewById(R.id.ivArrow);
            tvSubTitle = (TextView) itemView.findViewById(R.id.tvSubTitle);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvDefault = (TextView) itemView.findViewById(R.id.tvDefault);
            markDefault = itemView.findViewById(R.id.markDefault);
        }
    }
}
