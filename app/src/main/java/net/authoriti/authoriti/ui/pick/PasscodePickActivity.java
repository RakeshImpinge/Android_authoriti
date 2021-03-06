package net.authoriti.authoriti.ui.pick;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.AccountID;
import net.authoriti.authoriti.api.model.DefaultValue;
import net.authoriti.authoriti.api.model.Picker;
import net.authoriti.authoriti.api.model.Value;
import net.authoriti.authoriti.core.BaseActivity;
import net.authoriti.authoriti.ui.code.CodePermissionActivity_;
import net.authoriti.authoriti.ui.items.OptionItem;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiUtils;
import net.authoriti.authoriti.utils.ConstantUtils;
import net.authoriti.authoriti.utils.Log;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by mac on 12/1/17.
 */

@EActivity(R.layout.activity_passcode_pick)
public class PasscodePickActivity extends BaseActivity {

    private FastItemAdapter<OptionItem> optionAdapter;

    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;

    @Extra
    Picker picker;

    @Extra
    HashMap<String, DefaultValue> defaultPickerMap = new HashMap<>();
    DefaultValue defaultValue;

    @Extra
    int schemaIndex;

    @Extra
    String pickerType;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.tv_title)
    TextView tv_title;

    @ViewById(R.id.rvOptions)
    RecyclerView rvOptions;

    List<String> title = new ArrayList<>();
    List<String> value = new ArrayList<>();

    @AfterViews
    void callAfterViewInjection() {

        pickerType = picker.getPicker();
        defaultValue = defaultPickerMap.get(pickerType);
        tvTitle.setText(picker.getTitle());

        optionAdapter = new FastItemAdapter<OptionItem>();
        optionAdapter.withSelectable(true);
        rvOptions.setLayoutManager(new LinearLayoutManager(mContext));
        rvOptions.setAdapter(optionAdapter);

        optionAdapter.withOnClickListener(new FastAdapter.OnClickListener<OptionItem>() {
            @Override
            public boolean onClick(View v, IAdapter<OptionItem> adapter, OptionItem item, int
                    position) {
                if (pickerType.equals(PICKER_TIME) && position == picker.getValues().size() - 1) {
                    showDatePicker(item, position);
                } else if (pickerType.equals(PICKER_TIME) && position == picker.getValues().size
                        () - 2) {
                    showTimePicker(item, position);
                } else {
                    boolean isPickerRequestType = false;
                    if (pickerType.equals(PICKER_DATA_TYPE)) {
                        if (value.contains(picker.getValues().get(position).getValue())) {
                            if (value.size() > 1) {
                                int index = value.indexOf(picker.getValues().get(position)
                                        .getValue());
                                value.remove(index);
                                title.remove(index);
                                item.setChecked(false);
                            }
                        } else {
                            value.add(picker.getValues().get(position).getValue());
                            title.add(picker.getValues().get(position).getTitle());
                            item.setChecked(true);
                        }
                        defaultValue.setTitle(title.toString().replace("[", "").replace("]", "")
                                .replace(", ", ","));
                        defaultValue.setDefault(false);
                        defaultValue.setValue(value.toString().replace("[", "").replace("]", "")
                                .replace(", ", ","));
                        defaultPickerMap.put(pickerType, defaultValue);
                        optionAdapter.notifyAdapterDataSetChanged();

                    } else{
                        if (pickerType.equals(PICKER_REQUEST) && defaultPickerMap.containsKey
                                (PICKER_DATA_TYPE)) {
                            defaultValue.setTitle(picker.getValues().get(position).getTitle());
                            defaultValue.setDefault(false);
                            defaultValue.setValue(picker.getValues().get(position).getValue());
                            defaultPickerMap.put(pickerType, defaultValue);

                            List<Value> values = dataManager.getValuesFromDataType(picker
                                    .getValues().get(position).getValue());
                            DefaultValue defaultValueDataType = new DefaultValue(values.get(0)
                                    .getTitle(), values.get(0).getValue(), false);
                            defaultPickerMap.put(PICKER_DATA_TYPE, defaultValueDataType);
                            isPickerRequestType = true;
                        } else if(pickerType.equals(PICKER_ACCOUNT) ) {
                            defaultValue.setTitle(picker.getValues().get(position).getTitle());
                            defaultValue.setTitle(picker.getValues().get(position).getTitle());
                            defaultValue.setDefault(false);
                            defaultValue.setCustomer(item.getCustomerName());
                            defaultValue.setValue(picker.getValues().get(position).getValue());
                            defaultPickerMap.put(pickerType, defaultValue);
                        }else {
                            defaultValue.setTitle(picker.getValues().get(position).getTitle());
                            defaultValue.setDefault(false);

                            defaultValue.setValue(picker.getValues().get(position).getValue());
                            defaultPickerMap.put(pickerType, defaultValue);
                        }
                        Intent intent = new Intent();
                        intent.putExtra("selected_values", defaultPickerMap);
                        intent.putExtra("isPickerRequestType", isPickerRequestType);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                return true;
            }
        });
        showOptions();
        tv_title.setText(getHeaderTitle(picker.getPicker()));
    }

    public String getHeaderTitle(String type) {
        switch (type) {
            case PICKER_ACCOUNT:
                return "Please Select A Wallet ID";
            case PICKER_TIME:
                return "Pick a expiry time";
            case PICKER_INDUSTRY:
                return "Pick an Industry";
            case PICKER_LOCATION_COUNTRY:
                return "Pick a Location";
            case PICKER_LOCATION_STATE:
                return "Pick a Location";
            default:
                return "";
        }
    }


    private void showDatePicker(final OptionItem item, final int position) {
        final Calendar newCalendar = Calendar.getInstance();
        final Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR) + 19, calendar.get(Calendar.MONTH), calendar.get
                (Calendar.DAY_OF_MONTH));
        DatePickerDialog datePickerDialog = new DatePickerDialog(PasscodePickActivity.this, new
                DatePickerDialog.OnDateSetListener() {
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

                        if (newCalendar.getTimeInMillis() > calendar.getTimeInMillis()) {
                            showAlert("", "You can not choose 20 year's later day. Please choose " +
                                    "another.");
                        } else {
                            if (diff < 0) {
                                showAlert("", "You can not choose passed day. Please choose " +
                                        "another.");
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

    private void showTimePicker(final OptionItem item, final int position, final long
            initialMinutes) {

        final Calendar newCalendar = Calendar.getInstance();

        final TimePickerDialog timePickerDialog = new TimePickerDialog(PasscodePickActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        newCalendar.set(Calendar.MINUTE, minute);

                        Date date = newCalendar.getTime();
                        Date now = new Date();

                        long diff = date.getTime() - now.getTime();

                        if (diff < 0 && initialMinutes == 0) {

                            showAlert("", "You can not choose passed hours. Please choose another" +
                                    ".");

                        } else {
                            Log.e("Diff - ", String.valueOf(TimeUnit.MILLISECONDS.toMinutes(diff)));
                            defaultValue.setTitle(String.valueOf(TimeUnit.MILLISECONDS.toMinutes
                                    (diff) + initialMinutes));
                            defaultValue.setDefault(false);
                            defaultValue.setValue(TIME_CUSTOM_DATE);
                            defaultPickerMap.put(pickerType, defaultValue);
                            Intent intent = new Intent();
                            intent.putExtra("selected_values", defaultPickerMap);
                            intent.putExtra("isPickerRequestType", false);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                    }
                }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);

        timePickerDialog.show();

    }

    private void showTimePicker(final OptionItem item, final int position) {
        System.out.println("Showing Time Picker");
        final TimePickerDialog timePickerDialog = new TimePickerDialog(PasscodePickActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay == 0 && minute == 0) {
                            showAlert("", "You should choose at least 1 minute.");
                        } else {
                            defaultValue.setTitle(String.valueOf(hourOfDay * 60 + minute));
                            defaultValue.setDefault(false);
                            defaultValue.setValue(TIME_CUSTOM_TIME);
                            defaultPickerMap.put(pickerType, defaultValue);
                            Intent intent = new Intent();
                            intent.putExtra("selected_values", defaultPickerMap);
                            intent.putExtra("isPickerRequestType", false);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }, 0, 0, true);

        timePickerDialog.show();

    }


    private void showOptions() {
        value = new ArrayList<String>(Arrays.asList(defaultValue.getValue().split("\\s*,\\s*")));
        title = new ArrayList<String>(Arrays.asList(defaultValue.getTitle().split("\\s*,\\s*")));

        if (optionAdapter == null) {
            optionAdapter = new FastItemAdapter<OptionItem>();
        } else {
            optionAdapter.clear();
        }

        // Show heading for account picker
        if (pickerType.equals(PICKER_ACCOUNT)) {
            List<Value> accountNewList = new ArrayList<Value>();
            List<AccountID> accountIDS = dataManager.getUser().getAccountIDs();
            Collections.sort(accountIDS, new Comparator<AccountID>() {
                @Override
                public int compare(AccountID accountID, AccountID t1) {
                    String s1 = accountID.getCustomer();
                    if (s1.trim().equalsIgnoreCase("")) {
                        s1 = "ZZZZZZZZZZ";
                    }

                    String s2 = t1.getCustomer();
                    if (s2.trim().equalsIgnoreCase("")) {
                        s2 = "ZZZZZZZZZZ";
                    }
                    return s1.compareTo(s2);
                }
            });

            for (int i = 0; i < accountIDS.size(); i++) {
                int position = i;
                String headingeName = "";
                if (position == 0 || !accountIDS.get(position).getCustomer().equals(accountIDS.get(position - 1).getCustomer())) {
                    headingeName = accountIDS.get(position).getCustomer();
                    if (headingeName.equals("")) {
                        headingeName = "Self Registered ID's";
                    } else {
                        headingeName = headingeName + " ID's";
                    }
                } else {
                    headingeName = "";
                }

                Value value = new Value(accountIDS.get(i).getIdentifier(), accountIDS.get(i).getType());
                accountNewList.add(value);

                if (defaultValue.getValue().equals(accountIDS.get(i).getIdentifier())
                        && defaultValue.getTitle().equals(accountIDS.get(i).getType()) && defaultValue.getCustomer().equals(accountIDS.get(i).getCustomer())) {
                    optionAdapter.add(new OptionItem(value, true, headingeName,accountIDS.get(position).getCustomer()));
                } else {
                    optionAdapter.add(new OptionItem(value, false, headingeName,accountIDS.get(position).getCustomer()));
                }
            }
            picker.setValues(accountNewList);
        } else {
            for (int i = 0; i < picker.getValues().size(); i++) {
                if (pickerType.equals(PICKER_REQUEST)) {
                    if (value.contains(picker.getValues().get(i).getValue()) && title.contains(picker.getValues().get(i).getTitle())) {
                        optionAdapter.add(new OptionItem(picker.getValues().get(i), true));
                    } else {
                        optionAdapter.add(new OptionItem(picker.getValues().get(i), false));
                    }
                } else if (pickerType.equals(PICKER_DATA_TYPE)) {
                    if (value.contains(picker.getValues().get(i).getValue())) {
                        optionAdapter.add(new OptionItem(picker.getValues().get(i), true));
                    } else {
                        optionAdapter.add(new OptionItem(picker.getValues().get(i), false));
                    }
                } else if (pickerType.equals(PICKER_GEO)) {
                    if (defaultValue.getValue().equals(picker.getValues().get(i).getValue()) &&  defaultValue.getTitle().equals(picker.getValues().get(i).getTitle())) {
                        optionAdapter.add(new OptionItem(picker.getValues().get(i), true));
                    } else {
                        optionAdapter.add(new OptionItem(picker.getValues().get(i), false));
                    }
                }else {
                    if (defaultValue.getValue().equals(picker.getValues().get(i).getValue())) {
                        optionAdapter.add(new OptionItem(picker.getValues().get(i), true));
                    } else {
                        optionAdapter.add(new OptionItem(picker.getValues().get(i), false));
                    }
                }
            }
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        Intent intent = new Intent();
        intent.putExtra("selected_values", defaultPickerMap);
        intent.putExtra("isPickerRequestType", false);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Click(R.id.ivHelp)
    void helpButtonClicked() {
        String endPoint = "";
        if (pickerType.equalsIgnoreCase(PICKER_ACCOUNT)) {
            endPoint = TOPIC_PICKER_ACCOUNT_ID;
        } else if (pickerType.equalsIgnoreCase(PICKER_TIME)) {
            endPoint = TOPIC_PICKER_TIME;
        } else if (pickerType.equalsIgnoreCase(PICKER_INDUSTRY)) {
            endPoint = TOPIC_PICKER_INDUSTRY;
        } else if (pickerType.equalsIgnoreCase(PICKER_LOCATION_COUNTRY)) {
            endPoint = TOPIC_PICKER_LOCATION;
        } else if (pickerType.equalsIgnoreCase(PICKER_GEO)) {
            endPoint = TOPIC_PICKER_GEO;
        } else if (pickerType.equalsIgnoreCase(PICKER_REQUEST)) {
            endPoint = TOPIC_PICKER_REQUESTOR;
        } else if (pickerType.equalsIgnoreCase(PICKER_DATA_TYPE)) {
            endPoint = TOPIC_PICKER_DATA_TYPE;
        }
        if (endPoint.equals("")) return;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ConstantUtils.getHelpUrl
                (endPoint)));
        startActivity(browserIntent);
    }

}
