package net.authoriti.authoriti.ui.menu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.AccountID;
import net.authoriti.authoriti.api.model.GroupItem;
import net.authoriti.authoriti.core.AccountManagerUpdateInterfce;
import net.authoriti.authoriti.ui.code.CodePermissionActivity_;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiData_;

import java.util.List;

/**
 * Created by movdev on 3/1/18.
 */

public class AccountAdaper extends RecyclerView.Adapter<AccountAdaper.MyViewHolder> {

    private List<AccountID> countryList;
    private AccountManagerUpdateInterfce updateInterfce;

    public int mDefaultPostion = -1;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView txt_header;
        public LinearLayout lin_header;
        public View markDefault;

        TextView txt_delete;
        SwipeLayout swipeLayout;

        MyViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            txt_header = (TextView) view.findViewById(R.id.txt_header);
            txt_delete = (TextView) view.findViewById(R.id.tvDelete);
            lin_header = (LinearLayout) view.findViewById(R.id.lin_header);
            markDefault = (View) view.findViewById(R.id.markDefault);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
        }
    }

    public AccountAdaper(List<AccountID> countryList, AccountManagerUpdateInterfce accountManagerUpdateInterfce) {
        this.countryList = countryList;
        this.updateInterfce = accountManagerUpdateInterfce;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final AccountID accountID = countryList.get(position);
        holder.tvTitle.setText(accountID.getType());
        if (position == 0) {
            holder.lin_header.setVisibility(View.VISIBLE);
        } else if (!countryList.get(position).getCustomer().equals(countryList.get(position - 1).getCustomer())) {
            holder.lin_header.setVisibility(View.VISIBLE);
        } else {
            holder.lin_header.setVisibility(View.GONE);
        }

        holder.txt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                System.out.println("AccountId: " + accountID.getIdentifier());
                holder.swipeLayout.close(true);
                updateInterfce.deleted(accountID.getIdentifier());
            }
        });

        String customer = countryList.get(position).getCustomer();
        if (customer.equals("")) {
            customer = "Self Registered ID's";
        } else {
            customer = customer + " ID's";
        }
        holder.txt_header.setText(customer);

        if (position == mDefaultPostion) {
            holder.markDefault.setVisibility(View.VISIBLE);
        } else {
            holder.markDefault.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_account, parent, false);
        return new MyViewHolder(v);
    }
}