package com.curtisdigital.authoriti.ui.pick;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.model.Picker;
import com.curtisdigital.authoriti.api.model.Value;
import com.curtisdigital.authoriti.core.BaseActivity;
import com.curtisdigital.authoriti.ui.items.OptionItem;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.curtisdigital.authoriti.utils.AuthoritiUtils;
import com.github.thunder413.datetimeutils.DateTimeUnits;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;


import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * Created by mac on 12/1/17.
 */

@EActivity(R.layout.activity_passcode_pick)
public class PasscodePickActivity extends BaseActivity {

    private Picker picker;
    private FastItemAdapter<OptionItem> adapter;
    private int selectedIndex;

    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;

    @Extra
    String pickerType;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.rvOptions)
    RecyclerView rvOptions;

    @AfterViews
    void callAfterViewInjection(){

        picker = utils.getPicker(mContext, pickerType);
        if (picker == null) return;

        if (pickerType.equals(PICKER_ACCOUNT)){

            tvTitle.setText("PLEASE SELECT A WALLET ID");

        } else if (pickerType.equals(PICKER_LOCATION_STATE)){
            tvTitle.setText("Pick a Location");

        } else if (pickerType.equals(PICKER_TIME)){
            tvTitle.setText("Pick a expiry Time");

        } else {
            tvTitle.setText(picker.getTitle());
        }


        if (utils.presentSelectedIndex(this, picker.getPicker())) {

            selectedIndex = utils.getPickerSelectedIndex(this, picker.getPicker());

        } else {

            if (picker.isEnableDefault() && picker.getDefaultIndex() != -1){

                selectedIndex = utils.getPickerDefaultIndex(this, picker.getPicker());

            } else {

                selectedIndex = utils.getPickerSelectedIndex(this, picker.getPicker());

            }
        }

        adapter = new FastItemAdapter<OptionItem>();
        adapter.withSelectable(true);
        rvOptions.setLayoutManager(new LinearLayoutManager(mContext));
        rvOptions.setAdapter(adapter);

        adapter.withOnClickListener(new FastAdapter.OnClickListener<OptionItem>() {
            @Override
            public boolean onClick(View v, IAdapter<OptionItem> adapter, OptionItem item, int position) {

                if (pickerType.equals(PICKER_TIME) && position == picker.getValues().size() - 1){

                    showDatePicker(item, position);

                 } else if(pickerType.equals(PICKER_TIME) && position == picker.getValues().size() - 2){

                    showTimePicker(item, position);

                } else {

                    utils.setSelectedPickerIndex(mContext, pickerType, position);
                    utils.setIndexSelected(mContext, pickerType, true);
                    finish();

                }

                return true;
            }
        });

        if (picker.getValues() != null && picker.getValues().size() != 0){
            showOptions();
        }
    }

    private void showDatePicker(final OptionItem item, final int position){

        final Calendar newCalendar = Calendar.getInstance();

        final Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR) + 19, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        DatePickerDialog datePickerDialog = new DatePickerDialog(PasscodePickActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                newCalendar.set(year, month, dayOfMonth);

                Date date = newCalendar.getTime();
                Date now = new Date();

                long diff = date.getTime() - now.getTime();
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);

                Value value = item.getValue();
                value.setCustomDate(true);

                Log.e("Diff - ", String.valueOf(diff));

                if (newCalendar.getTimeInMillis() > calendar.getTimeInMillis()){

                    showAlert("", "You can not choose 20 year's later day. Please choose another.");

                } else {

                    if (diff < 0){

                        showAlert("", "You can not choose passed day. Please choose another.");


                    } else {

                        showTimePicker(item, position, minutes);

                    }
                }

            }
        }, newCalendar.get(Calendar.YEAR) - 19, newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void showTimePicker(final OptionItem item, final int position, final long initialMinutes){

        final Calendar newCalendar = Calendar.getInstance();

        final TimePickerDialog timePickerDialog = new TimePickerDialog(PasscodePickActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                newCalendar.set(Calendar.MINUTE, minute);

                Date date = newCalendar.getTime();
                Date now = new Date();

                long diff = date.getTime() - now.getTime();

                if (diff < 0 && initialMinutes == 0 ){

                    showAlert("", "You can not choose passed hours. Please choose another.");

                } else {


                    Log.e("Diff - ", String.valueOf(TimeUnit.MILLISECONDS.toMinutes(diff)));

                    Value value = item.getValue();
                    value.setCustomDate(true);
                    value.setValue(String.valueOf(TimeUnit.MILLISECONDS.toMinutes(diff) + initialMinutes));

                    item.setChecked(true);
                    item.setValue(value);
                    adapter.notifyAdapterItemChanged(position);

                    updateTimePicker(position, value);

                    OptionItem prevItem = adapter.getAdapterItem(selectedIndex);
                    prevItem.setChecked(false);
                    adapter.notifyAdapterItemChanged(selectedIndex);

                    utils.setSelectedPickerIndex(mContext, pickerType, position);
                    utils.setIndexSelected(mContext, pickerType, true);

                    finish();
                }

            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);

        timePickerDialog.show();

    }

    private void showTimePicker(final OptionItem item, final int position){

        final TimePickerDialog timePickerDialog = new TimePickerDialog(PasscodePickActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                if (hourOfDay == 0 && minute == 0){

                    showAlert("", "You should choose at least 1 minute.");


                } else {

                    Value value = item.getValue();
                    value.setCustomDate(true);
                    value.setValue(String.valueOf(hourOfDay * 60 + minute));

                    item.setChecked(true);
                    item.setValue(value);
                    adapter.notifyAdapterItemChanged(position);

                    updateTimePicker(position, value);

                    OptionItem prevItem = adapter.getAdapterItem(selectedIndex);
                    prevItem.setChecked(false);
                    adapter.notifyAdapterItemChanged(selectedIndex);

                    utils.setSelectedPickerIndex(mContext, pickerType, position);
                    utils.setIndexSelected(mContext, pickerType, true);

                    finish();
                }


            }
        }, 0, 0, true);

        timePickerDialog.show();

    }

    private void updateTimePicker(int position, Value value){

        picker.getValues().set(position, value);

        dataManager.setTimePicker(picker);

    }

    private void showOptions(){

        if (adapter == null){
            adapter = new FastItemAdapter<OptionItem>();
        } else {
            adapter.clear();
        }

        for (int i = 0 ; i < picker.getValues().size() ; i ++){

            adapter.add(new OptionItem(picker.getValues().get(i), selectedIndex == i));

        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvSave)
    void saveButtonClicked(){
        finish();
    }

}
