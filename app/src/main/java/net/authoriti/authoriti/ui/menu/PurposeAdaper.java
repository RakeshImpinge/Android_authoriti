package net.authoriti.authoriti.ui.menu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.GroupItem;
import net.authoriti.authoriti.ui.code.CodePermissionActivity_;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiData_;

import java.util.List;

/**
 * Created by movdev on 3/1/18.
 */

public class PurposeAdaper extends RecyclerView.Adapter {

    private final int VIEW_ITEM_HEADER = 1;  // type: name/value
    private final int VIEW_ITEM_CONTENT = 0;

    private List<GroupItem> mData;
    private Context mContext;
    AuthoritiData dataManager;

    public PurposeAdaper(Context context, List<GroupItem> data) {
        this.mContext = context;
        this.mData = data;
        dataManager = AuthoritiData_.getInstance_(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                            .item_purpose_header,
                    parent, false);
            viewHolder = new HeaderHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purpose,
                    parent, false);
            viewHolder = new ContentHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final GroupItem item = mData.get(position);
        if (holder instanceof HeaderHolder) {
            HeaderHolder mHolder = (HeaderHolder) holder;
        } else {
            ContentHolder mHolder = (ContentHolder) holder;
            mHolder.tvTitle.setText(mData.get(position).getLabel());
            mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dataManager.getScheme() != null && dataManager.getDefaultValues() != null) {
                        CodePermissionActivity_.intent(mContext).purposeIndex(item.getIndexGroup())
                                .purposeIndexItem(item.getIndexItem()).start();
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).isHeading() == VIEW_ITEM_HEADER ? VIEW_ITEM_HEADER :
                VIEW_ITEM_CONTENT;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public class ContentHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public ContentHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}
