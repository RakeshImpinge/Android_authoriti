package com.curtisdigital.authoriti.ui.pick;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.model.Picker;
import com.curtisdigital.authoriti.api.model.Value;
import com.curtisdigital.authoriti.core.BaseActivity;
import com.curtisdigital.authoriti.ui.items.OptionItem;
import com.curtisdigital.authoriti.utils.AuthoritiUtils_;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.Objects;

/**
 * Created by mac on 12/1/17.
 */

@EActivity(R.layout.activity_passcode_pick)
public class PasscodePickActivity extends BaseActivity {

    private Picker picker;
    private FastItemAdapter<OptionItem> adapter;

    @Extra
    String pickerType;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.rvOptions)
    RecyclerView rvOptions;

    @AfterViews
    void callAfterViewInjection(){

        picker = AuthoritiUtils_.getInstance_(mContext).getPicker(mContext, pickerType);
        if (picker == null) return;

        if (Objects.equals(pickerType, PICKER_LOCATION_STATE)){
            tvTitle.setText("Pick a Location");

        } else if (Objects.equals(pickerType, PICKER_TIME)){
            tvTitle.setText("Pick a expiry Time");

        } else {
            tvTitle.setText(picker.getTitle());
        }

        adapter = new FastItemAdapter<OptionItem>();
        adapter.withSelectable(true);
        rvOptions.setLayoutManager(new LinearLayoutManager(mContext));
        rvOptions.setAdapter(adapter);

        adapter.withOnClickListener(new FastAdapter.OnClickListener<OptionItem>() {
            @Override
            public boolean onClick(View v, IAdapter<OptionItem> adapter, OptionItem item, int position) {
                return true;
            }
        });

        if (picker.getValues() != null && picker.getValues().size() != 0){
            showOptions();
        }
    }

    private void showOptions(){

        if (adapter == null){
            adapter = new FastItemAdapter<OptionItem>();
        } else {
            adapter.clear();
        }

        for (int i = 0 ; i < picker.getValues().size() ; i ++){
            int selectedIndex = AuthoritiUtils_.getInstance_(mContext).getPickerSelectedIndex(mContext, picker.getPicker());
            adapter.add(new OptionItem(picker.getValues().get(i), selectedIndex == i));
        }
    }

    @Click({R.id.ivBack, R.id.cvBack})
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvSave)
    void saveButtonClicked(){
        finish();
    }

}
