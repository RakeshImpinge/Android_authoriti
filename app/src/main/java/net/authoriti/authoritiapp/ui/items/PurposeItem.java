package net.authoriti.authoritiapp.ui.items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.authoriti.authoritiapp.R;
import net.authoriti.authoritiapp.api.model.Purpose;
import net.authoriti.authoritiapp.ui.code.CodePermissionActivity_;
import net.authoriti.authoritiapp.utils.AuthoritiData;
import net.authoriti.authoritiapp.utils.AuthoritiData_;

import com.daimajia.swipe.SwipeLayout;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by movdev on 3/1/18.
 */

public class PurposeItem extends AbstractItem<PurposeItem, PurposeItem.ViewHolder> {

    private Purpose purpose;
    private int index;
    private boolean isDefault;
    private PurposeItemListener listener;

    public PurposeItem(Purpose purpose, int index, boolean isDefault, PurposeItemListener
            listener) {
        this.purpose = purpose;
        this.index = index;
        this.isDefault = isDefault;
        this.listener = listener;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_purpose_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_purpose;
    }

    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        final Context context = holder.itemView.getContext();
        final AuthoritiData dataManager = AuthoritiData_.getInstance_(context);

        if (isDefault) {
            holder.markDefault.setVisibility(View.VISIBLE);
        } else {
            holder.markDefault.setVisibility(View.INVISIBLE);
        }

        holder.tvTitle.setText(purpose.getLabel());
        holder.tvDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.swipeLayout.close(true);
                if (listener != null) {
                    listener.setPurposeAsDefault(index);
                }
            }
        });

        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.tvDefault);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.tvDefault);
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

        holder.swipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataManager.getScheme() != null) {
                    CodePermissionActivity_.intent(context).purposeIndex(index).start();
                }
            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        SwipeLayout swipeLayout;
        TextView tvDefault;
        View markDefault;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvDefault = (TextView) itemView.findViewById(R.id.tvDefault);
            markDefault = itemView.findViewById(R.id.markDefault);

        }
    }

    public interface PurposeItemListener {

        void setPurposeAsDefault(int index);
    }

}
