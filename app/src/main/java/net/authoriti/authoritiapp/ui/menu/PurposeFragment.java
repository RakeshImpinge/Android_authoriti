package net.authoriti.authoritiapp.ui.menu;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.authoriti.authoritiapp.R;
import net.authoriti.authoritiapp.api.AuthoritiAPI;
import net.authoriti.authoritiapp.api.model.GroupItem;
import net.authoriti.authoritiapp.api.model.Purpose;
import net.authoriti.authoritiapp.core.BaseFragment;
import net.authoriti.authoritiapp.ui.items.PurposeItem;
import net.authoriti.authoritiapp.utils.AuthoritiData;
import net.authoriti.authoritiapp.utils.AuthoritiUtils;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by movdev on 3/1/18.
 */

@EFragment(R.layout.fragment_purpose)
public class PurposeFragment extends BaseFragment implements PurposeItem.PurposeItemListener {

    FastItemAdapter<PurposeItem> adapter;

    PurposeAdaper purposeAdaper;
    List<GroupItem> groupItems = new ArrayList<>();

    @Bean
    AuthoritiData dataManager;

    @Bean
    AuthoritiUtils utils;

    @ViewById(R.id.rvPurpose)
    RecyclerView rvPurpose;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.linRoot)
    LinearLayout linRoot;


    @AfterViews
    void callAfterViewInjection() {
        purposeAdaper = new PurposeAdaper(getContext(), groupItems);
        adapter = new FastItemAdapter<PurposeItem>();
        rvPurpose.setLayoutManager(new LinearLayoutManager(mContext));
        rvPurpose.setAdapter(purposeAdaper);

        rvPurpose.setFocusable(false);
        rvPurpose.setNestedScrollingEnabled(false);
        linRoot.requestFocus();


        if (dataManager.getPurposes() != null) {
            showPurposes();
        } else {
            loadPurposes();
        }
    }

    private void loadPurposes() {
        displayProgressDialog("Loading...");
        AuthoritiAPI.APIService().getPurposes().enqueue(new Callback<List<Purpose>>() {
            @Override
            public void onResponse(Call<List<Purpose>> call, Response<List<Purpose>> response) {
                dismissProgressDialog();
                if (response.code() == 200 && response.body() != null) {
                    dataManager.setPurposes(response.body());
                    showPurposes();
                }
            }

            @Override
            public void onFailure(Call<List<Purpose>> call, Throwable t) {
                dismissProgressDialog();
            }
        });
    }

    private void showPurposes() {
        if (dataManager.getPurposes() != null && dataManager.getPurposes().size() > 0) {
//            if (adapter == null) {
//                adapter = new FastItemAdapter<PurposeItem>();
//            } else {
//                adapter.clear();
//            }

//            int defaultIndex = dataManager.getDefaultPurposeIndex();
//
//            for (int i = 0; i < dataManager.getPurposes().size(); i++) {
//                boolean isDefault = defaultIndex == i;
//                adapter.add(new GroupItem(dataManager.getPurposes().get(i), i, isDefault,
// this));
//            }
            groupItems.clear();
            for (int i = 0; i < dataManager.getPurposes().size(); i++) {
                if (i != 0) {
                    GroupItem heading = new GroupItem();
                    heading.setHeading(1);
                    heading.setLabel(dataManager.getPurposes().get(i).getLabel());
                    groupItems.add(heading);
                }
                for (int j = 0; j < dataManager.getPurposes().get(i).getGroups().size(); j++) {
                    GroupItem item = new GroupItem();
                    item.setHeading(0);
                    item.setIndexGroup(i);
                    item.setIndexItem(j);
                    item.setLabel(dataManager.getPurposes().get(i).getGroups().get(j)
                            .getLabel());
                    groupItems.add(item);
                }
            }
            purposeAdaper.notifyDataSetChanged();
        }
    }


    @Override
    public void setPurposeAsDefault(int index) {

        int defaultIndex = dataManager.getDefaultPurposeIndex();

        if (defaultIndex < 0 || defaultIndex >= dataManager.getPurposes().size()) {

            PurposeItem item = adapter.getAdapterItem(index);
            item.setDefault(true);
            adapter.notifyAdapterItemChanged(index);

            dataManager.setDefaultPurposeIndex(index);

        } else {

            PurposeItem item1 = adapter.getAdapterItem(defaultIndex);
            item1.setDefault(false);
            adapter.notifyAdapterItemChanged(defaultIndex);

            PurposeItem item2 = adapter.getAdapterItem(index);
            item2.setDefault(true);
            adapter.notifyAdapterItemChanged(index);

            dataManager.setDefaultPurposeIndex(index);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

//        if (dataManager.getScheme() != null) {
//            utils.initSelectedIndex(mContext);
//        }
    }
}
