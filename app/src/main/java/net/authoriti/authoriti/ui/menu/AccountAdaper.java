package net.authoriti.authoriti.ui.menu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private List<AccountID> customerList;
    private AccountManagerUpdateInterfce updateInterfce;

    public int mDefaultPostion = -1;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView txt_header;
        public RelativeLayout rel_header;
        public View markDefault;
        LinearLayout lin_add_self_id;
        LinearLayout surface;
        TextView txt_delete;
        SwipeLayout swipeLayout;

        MyViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            txt_header = (TextView) view.findViewById(R.id.txt_header);
            txt_delete = (TextView) view.findViewById(R.id.tvDelete);
            rel_header = (RelativeLayout) view.findViewById(R.id.rel_header);
            markDefault = (View) view.findViewById(R.id.markDefault);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            lin_add_self_id = (LinearLayout) itemView.findViewById(R.id.lin_add_self_id);
            surface = (LinearLayout) itemView.findViewById(R.id.surface);
        }
    }

    public AccountAdaper(List<AccountID> customerList, AccountManagerUpdateInterfce accountManagerUpdateInterfce) {
        this.customerList = customerList;
        this.updateInterfce = accountManagerUpdateInterfce;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
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
//                System.out.println("AccountId: " + accountID.getIdentifier());
                holder.swipeLayout.close(true);
                updateInterfce.deleted(accountID.getIdentifier());
            }
        });
        System.out.println("Inside BindViewHolder: " + customerList.size());
        String customer = customerList.get(position).getCustomer();
        System.out.println("Inside BindViewHolder: " + customer);
        if (customer.equals("")) {
            holder.lin_add_self_id.setVisibility(View.VISIBLE);
            customer = "Self Registered ID's";
        } else {
            holder.lin_add_self_id.setVisibility(View.GONE);
            customer = customer + " ID's";
        }

        holder.lin_add_self_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInterfce.addSelfSigned();
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
        return customerList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_account, parent, false);
        return new MyViewHolder(v);
    }
}