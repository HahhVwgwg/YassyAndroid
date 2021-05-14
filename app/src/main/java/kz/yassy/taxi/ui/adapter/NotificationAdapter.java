package kz.yassy.taxi.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import kz.yassy.taxi.R;
import kz.yassy.taxi.data.network.model.NotificationManager;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private List<NotificationManager> notifications;
    private Context mContext;

    public NotificationAdapter(List<NotificationManager> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_notifications, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TextView showMore = holder.tvShowMore;
        Glide.with(mContext)
                .load(notifications.get(position).getImage())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_document_placeholder)
                        .dontAnimate().error(R.drawable.ic_document_placeholder))
                .into(holder.ivNotificationImg);
//        Log.e("notification",notifications.get(position).toString());
        holder.ivNotificationDesc.setText(notifications.get(position).getDescription());
        holder.time.setText(notifications.get(position).getExpiryDate());
        holder.ivNotificationImg.setClipToOutline(true);
        holder.ivNotificationDesc.post(() -> {
            if (holder.ivNotificationDesc.getLineCount() > 3) {
                holder.ivNotificationDesc.setMaxLines(3);
                holder.ivNotificationDesc.setEllipsize(TextUtils.TruncateAt.END);
            } else showMore.setVisibility(View.INVISIBLE);
        });

        showMore.setOnClickListener(v -> {
            if (showMore.getText().toString().equals(mContext.getString(R.string.show_more))) {
                showMore.setText(mContext.getString(R.string.show_less));
                holder.ivNotificationDesc.setMaxLines(Integer.MAX_VALUE);
            } else {
                showMore.setText(mContext.getString(R.string.show_more));
                holder.ivNotificationDesc.setMaxLines(3);
                holder.ivNotificationDesc.setEllipsize(TextUtils.TruncateAt.END);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivNotificationImg;
        private TextView ivNotificationDesc;
        private TextView tvShowMore;
        private TextView time;

        MyViewHolder(View view) {
            super(view);

            ivNotificationDesc = view.findViewById(R.id.ivNotificationDesc);
            ivNotificationImg = view.findViewById(R.id.ivNotificationImg);
            tvShowMore = view.findViewById(R.id.tvShowMore);
            time = view.findViewById(R.id.time);
        }

    }
}
