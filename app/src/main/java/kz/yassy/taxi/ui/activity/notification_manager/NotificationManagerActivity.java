package kz.yassy.taxi.ui.activity.notification_manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.network.model.NotificationManager;

public class NotificationManagerActivity extends BaseActivity implements NotificationManagerIView {

    @BindView(R.id.rvNotificationManager)
    RecyclerView rvNotificationManager;

    private NotificationManagerPresenter<NotificationManagerActivity> presenter = new NotificationManagerPresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_notification_manager;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        presenter.getNotificationManager();
        showLoading();
    }

    @Override
    public void onSuccess(List<NotificationManager> managers) {
        hideLoading();
        rvNotificationManager.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvNotificationManager.setAdapter(new NotificationAdapterInside(managers));
    }

    @OnClick({R.id.back_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    public class NotificationAdapterInside extends RecyclerView.Adapter<NotificationAdapterInside.MyViewHolder> {

        private List<NotificationManager> notifications;
        private Context mContext;

        public NotificationAdapterInside(List<NotificationManager> notifications) {
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

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                Log.e("EEEEEEEEEEEEEEE", "EEEEEEEEEEEEEE");
                if (view.getId() == R.id.item_view) {
                    NotificationManager notificationManager = notifications.get(position);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation
                            (baseActivity(), itemView, ViewCompat.getTransitionName(itemView));
                    Intent intent = new Intent(mContext, NotificationManagerDetailsActivity.class);
                    intent.putExtra("time", notificationManager.getExpiryDate());
                    intent.putExtra("imageUrl", notificationManager.getImage());
                    intent.putExtra("description", notificationManager.getDescription());
                    startActivity(intent, options.toBundle());
                }
            }
        }
    }
}
