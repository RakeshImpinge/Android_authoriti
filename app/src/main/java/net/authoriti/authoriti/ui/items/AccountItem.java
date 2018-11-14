package net.authoriti.authoriti.ui.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.AccountID;
import net.authoriti.authoriti.api.model.Value;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by mac on 12/2/17.
 */

public class AccountItem extends AbstractItem<AccountConfirmItem, AccountItem
        .ViewHolder> {

    private AccountID accountID;
    private boolean setDefault;
    int position = -1;

    public AccountItem(AccountID accountID, boolean setDefault, int position) {
        this.accountID = accountID;
        this.setDefault = setDefault;
        this.position = position;
    }

    public AccountID getAccountID() {
        return this.accountID;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_account_confirm_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_account;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.tvTitle.setText(accountID.getType());

        if (accountID.isConfirmed()) {
            holder.markDefault.setBackgroundResource(R.drawable.bg_green_dot);
            if (setDefault) {
                holder.markDefault.setVisibility(View.VISIBLE);
            } else {
                holder.markDefault.setVisibility(View.INVISIBLE);
            }
            holder.tvSubTitle.setVisibility(View.INVISIBLE);
        } else {
            holder.tvSubTitle.setVisibility(View.VISIBLE);
            holder.markDefault.setVisibility(View.VISIBLE);
            holder.markDefault.setBackgroundResource(R.drawable.bg_yellow_dot);
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvSubTitle;
        TextView tvDelete;
        View markDefault;
        LinearLayout lin_header;


        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            lin_header = (LinearLayout) itemView.findViewById(R.id.lin_header);
            tvSubTitle = (TextView) itemView.findViewById(R.id.tvSubTitle);
            tvDelete = (TextView) itemView.findViewById(R.id.tvDelete);
            markDefault = itemView.findViewById(R.id.markDefault);

        }
    }
}
