package com.curtisdigital.authoriti.ui.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.model.AccountID;
import com.daimajia.swipe.SwipeLayout;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by mac on 1/24/18.
 */

public class AccountAddItem extends AbstractItem<AccountAddItem, AccountAddItem.ViewHolder> {

    private AccountAddItemListener listener;
    private AccountID accountID;
    private boolean setDefault;

    public AccountAddItem(AccountID accountID, boolean setDefault, AccountAddItemListener listener){
        this.accountID = accountID;
        this.setDefault = setDefault;
        this.listener = listener;
    }

    public AccountID getAccountID(){
        return this.accountID;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_account_add_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_add_account;
    }

    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.tvTitle.setText(accountID.getType());

        if (setDefault){
            holder.markDefault.setVisibility(View.VISIBLE);
        } else {
            holder.markDefault.setVisibility(View.INVISIBLE);
        }

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

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null){
                    listener.itemDelete(holder.getAdapterPosition());
                }

            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        SwipeLayout swipeLayout;
        TextView tvDelete;
        View markDefault;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvDelete = (TextView) itemView.findViewById(R.id.tvDelete);
            markDefault = itemView.findViewById(R.id.markDefault);

        }
    }

    public interface AccountAddItemListener{
        void itemDelete(int position);
    }
}
