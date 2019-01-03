package net.authoriti.authoriti.ui.menu;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import net.authoriti.authoriti.MainActivity;
import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.AuthLogIn;
import net.authoriti.authoriti.api.model.SettingItem;
import net.authoriti.authoriti.core.BaseFragment;
import net.authoriti.authoriti.ui.auth.InviteCodeActivity_;
import net.authoriti.authoriti.ui.items.PurposeItem;
import net.authoriti.authoriti.ui.share.ExportActivity_;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_setting)
public class SettingFragment extends BaseFragment {
    @Bean
    AuthoritiData dataManager;

    SettingAdaper adapter;

    @ViewById(R.id.rvOptions)
    RecyclerView rvOptions;

    List<SettingItem> groupItems = new ArrayList<>();

    @AfterViews
    void callAfterViewInjection() {
        initItems();
        adapter = new SettingAdaper(this, groupItems);
        rvOptions.setLayoutManager(new LinearLayoutManager(mContext));
        rvOptions.setAdapter(adapter);
    }

    private void initItems() {
        groupItems.clear();
        groupItems.add(new SettingItem("Wipe"));
        groupItems.add(new SettingItem("Export"));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).updateMenuToolbar(Constants.MENU_SETTING);
    }

    private void deleteAccount() {
        dataManager.wipeSetting(mContext);
    }

    private void wipeLogOut() {

        deleteAccount();

        AuthLogIn logIn = dataManager.loginStatus();
        logIn.setLogin(false);
        logIn.setWipe(true);
        dataManager.setAuthLogin(logIn);

        InviteCodeActivity_.intent(mContext).flags(FLAG_ACTIVITY_CLEAR_TASK |
                FLAG_ACTIVITY_NEW_TASK).showBack(false).start();

    }


    public void onClick(String name) {
        if (name.equals("Wipe")) {
            wipeLogOut();
        } else if (name.equals("Export")) {
            ExportActivity_.intent(this.mContext).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();

        }
    }
}
