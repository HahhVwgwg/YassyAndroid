package kz.yassy.taxi.ui.activity.notification_manager;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.network.model.NotificationManager;

public class NotificationManagerDetailsActivity extends BaseActivity implements NotificationManagerIView {

    @BindView(R.id.ivNotificationImg)
    ImageView ivNotificationImg;
    @BindView(R.id.ivNotificationDesc)
    TextView ivNotificationDesc;
    @BindView(R.id.tvShowMore)
    TextView tvShowMore;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.title)
    TextView title;


    private NotificationManagerPresenter<NotificationManagerDetailsActivity> presenter = new NotificationManagerPresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_notification_manager_details;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        if (getIntent().getExtras() != null) {
            Glide.with(getApplicationContext())
                    .load(getIntent().getExtras().getString("imageUrl"))
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_document_placeholder)
                            .dontAnimate().error(R.drawable.ic_document_placeholder))
                    .into(ivNotificationImg);
            ivNotificationDesc.setText(getIntent().getExtras().getString("description"));
            time.setText(getIntent().getExtras().getString("time"));
            title.setText(getIntent().getExtras().getString("title"));
        }
    }

    @Override
    public void onSuccess(List<NotificationManager> managers) {
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
}