package net.authoriti.authoriti.ui.menu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.AccountID;
import net.authoriti.authoriti.core.AccountManagerUpdateInterfce;

import java.util.List;

/**
 * Created by movdev on 3/1/18.
 */

public class AccountAdaper extends RecyclerView.Adapter<AccountAdaper.MyViewHolder> {
    private boolean signupInProgress = false;
    private List<AccountID> customerList;
    private AccountManagerUpdateInterfce updateInterfce;

    public int mDefaultPostion = -1;
    private static final String SELF_REGISTERED = "Self Registered ID's";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView txt_header;
        public RelativeLayout rel_header;
        public View markDefault;
        LinearLayout lin_add_self_id;
        LinearLayout surface;
        TextView txt_delete;
        SwipeLayout swipeLayout;
        ImageButton ivSync;

        MyViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            txt_header = (TextView) view.findViewById(R.id.txt_header);
            txt_delete = (TextView) view.findViewById(R.id.tvDelete);
            rel_header = (RelativeLayout) view.findViewById(R.id.rel_header);
            markDefault = (View) view.findViewById(R.id.markDefault);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            lin_add_self_id = (LinearLayout) itemView.findViewById(R.id.lin_add_self_id);
            ivSync = (ImageButton) itemView.findViewById(R.id.ivSync);
            surface = (LinearLayout) itemView.findViewById(R.id.surface);
        }
    }

    public AccountAdaper(List<AccountID> customerList, AccountManagerUpdateInterfce accountManagerUpdateInterfce, boolean signupInProgress) {
        this.customerList = customerList;
        this.updateInterfce = accountManagerUpdateInterfce;
        this.signupInProgress = signupInProgress;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (position == customerList.size()) {
            holder.ivSync.setVisibility(View.GONE);
            holder.lin_add_self_id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateInterfce.addSelfSigned();
                }
            });
            holder.txt_header.setText(SELF_REGISTERED);
            holder.surface.setVisibility(View.GONE);
            return;
        }

        final AccountID accountID = customerList.get(position);
        if (position == 0) {
            holder.rel_header.setVisibility(View.VISIBLE);
        } else if (!customerList.get(position).getCustomer().equals(customerList.get(position - 1).getCustomer())) {
            holder.rel_header.setVisibility(View.VISIBLE);
        } else {
            holder.rel_header.setVisibility(View.GONE);
        }

        holder.txt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.swipeLayout.close(true);
                updateInterfce.deleted(accountID.getIdentifier());
            }
        });

        String customer = customerList.get(position).getCustomer();

        if (customer.equals("")) {
            holder.lin_add_self_id.setVisibility(View.VISIBLE);
            customer = SELF_REGISTERED;
            holder.ivSync.setVisibility(View.GONE);
        } else {
            holder.lin_add_self_id.setVisibility(View.GONE);
            customer = customer + " ID's";

            if (!signupInProgress)
                holder.ivSync.setVisibility(View.VISIBLE);
        }

        holder.lin_add_self_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInterfce.addSelfSigned();
            }
        });

        holder.ivSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInterfce.syncId(customerList.get(position).getCustomer_ID());
            }
        });

        holder.txt_header.setText(customer);
        holder.tvTitle.setText(accountID.getType());
        if (position == mDefaultPostion) {
            holder.markDefault.setVisibility(View.VISIBLE);
        } else {
            holder.markDefault.setVisibility(View.GONE);
        }

        if (accountID.getType().equals("")) {
            holder.surface.setVisibility(View.GONE);
        } else {
            holder.surface.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        final int sz = customerList.size();

        for (int i = 0; i < sz; i++) {
            if (customerList.get(i).getCustomer().equalsIgnoreCase("")) {
                return sz;
            }
        }

        return sz+1;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_account, parent, false);
        return new MyViewHolder(v);
    }
}