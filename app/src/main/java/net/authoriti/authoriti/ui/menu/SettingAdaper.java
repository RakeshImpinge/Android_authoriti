package net.authoriti.authoriti.ui.menu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.GroupItem;
import net.authoriti.authoriti.api.model.SettingItem;
import net.authoriti.authoriti.ui.code.CodePermissionActivity_;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiData_;

import java.util.List;

/**
 * Created by movdev on 3/1/18.
 */

public class SettingAdaper extends RecyclerView.Adapter {

    private List<net.authoriti.authoriti.api.model.SettingItem> mData;
    private SettingFragment settingFragment;
    AuthoritiData dataManager;

    public SettingAdaper(SettingFragment settingFragment, List<net.authoriti.authoriti.api.model.SettingItem> data) {
        this.settingFragment = settingFragment;
        this.mData = data;
        dataManager = AuthoritiData_.getInstance_(settingFragment.getContext());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting,
                parent, false);
        viewHolder = new ContentHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ContentHolder mHolder = (ContentHolder) holder;
        mHolder.tvTitle.setText(mData.get(position).getName());

        mHolder.item_code_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingFragment.onClick(mData.get(position).getName());
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ContentHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        LinearLayout item_code_id;

        public ContentHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            item_code_id = (LinearLayout) itemView.findViewById(R.id.item_code_id);
        }
    }
}
