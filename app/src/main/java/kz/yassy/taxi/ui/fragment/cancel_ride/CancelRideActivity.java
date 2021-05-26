package kz.yassy.taxi.ui.fragment.cancel_ride;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.SharedHelper;
import kz.yassy.taxi.data.network.model.CancelResponse;
import kz.yassy.taxi.data.network.model.Datum;

import static kz.yassy.taxi.MvpApplication.DATUM;
import static kz.yassy.taxi.common.Constants.BroadcastReceiver.INTENT_FILTER;


public class CancelRideActivity extends BaseActivity implements CancelRideIView {
    @BindView(R.id.cancel_reason)
    EditText cancelReason;
    @BindView(R.id.dismiss)
    ImageView dismiss;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.rcvReason)
    RecyclerView rcvReason;
    @BindView(R.id.goner)
    RelativeLayout goner;
    @BindView(R.id.animation)
    LottieAnimationView animationView;
    private CancelRidePresenter<CancelRideActivity> presenter = new CancelRidePresenter<>();
    private List<String> cancelResponseList = new ArrayList<String>(Arrays.asList("Передумал", "Водитель не двигался", "Слишком долго ждал", "Водитель не туда уехал", "Помолка транспорта"));
    private ReasonAdapter adapter;
    private int last_selected_location = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cancel;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        adapter = new ReasonAdapter(cancelResponseList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        rcvReason.setLayoutManager(mLayoutManager);
        rcvReason.setItemAnimator(new DefaultItemAnimator());
        rcvReason.setAdapter(adapter);
        submit.setEnabled(false);
        SharedHelper.putKey(getApplicationContext(), "cancelRideActivity", false);
        cancelReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                submit.setEnabled(s.length() > 0);
            }
        });
    }

    @Override
    public void onSuccess(Object object) {
        if (DATUM != null)
            FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(DATUM.getId()));
        SharedHelper.putKey(getApplicationContext(), "cancelRideActivity", true);
        sendBroadcast(new Intent(INTENT_FILTER));
        finish();
//        animationView.cancelAnimation();
//        animationView.setMinFrame(120);
//        animationView.setMaxFrame(386);
//        animationView.playAnimation();
//        animationView.addAnimatorListener(new AnimateTaxi.AnimatorListener() {
//            @Override
//            public void onAnimationStart(AnimateTaxi animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(AnimateTaxi animation) {
//                finish();
//            }
//
//            @Override
//            public void onAnimationCancel(AnimateTaxi animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(AnimateTaxi animation) {
//
//            }
//        });
    }

    @Override
    public void onSuccess(List<CancelResponse> response) {

    }

    @Override
    public void onError(Throwable e) {
//        animationView.cancelAnimation();
//        animationView.setMinFrame(400);
//        animationView.setMaxFrame(841);
//        animationView.playAnimation();
//        animationView.addAnimatorListener(new AnimateTaxi.AnimatorListener() {
//            @Override
//            public void onAnimationStart(AnimateTaxi animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(AnimateTaxi animation) {
//                finish();
//            }
//
//            @Override
//            public void onAnimationCancel(AnimateTaxi animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(AnimateTaxi animation) {
//
//            }
//        });
        handleError(e);
    }

    @Override
    public void onReasonError(Throwable e) {
        handleError(e);
    }

    @OnClick({R.id.dismiss, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dismiss:
                finish();
                break;
            case R.id.submit:
                if (last_selected_location == -1) {
                    Toast.makeText(this, getString(R.string.invalid_selection), Toast.LENGTH_SHORT).show();
                    return;
                }
                cancelRequest();
                break;
        }
    }

    private void cancelRequest() {
        if (cancelReason.getText().toString().isEmpty() && (last_selected_location == -1)) {
            Toast.makeText(this, getString(R.string.please_enter_cancel_reason), Toast.LENGTH_SHORT).show();
            return;
        }

        if (DATUM != null) {
            Datum datum = DATUM;
            HashMap<String, Object> map = new HashMap<>();
            map.put("request_id", datum.getId());
            if ((last_selected_location == -1))
                map.put("cancel_reason", cancelReason.getText().toString());
            else
                map.put("cancel_reason", cancelResponseList.get(last_selected_location));
            goner.setVisibility(View.VISIBLE);
            animationView.cancelAnimation();
            animationView.setMinFrame(120);
            animationView.setMaxFrame(386);
            animationView.playAnimation();
            animationView.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            presenter.cancelRequest(map);
        }
    }

    private class ReasonAdapter extends RecyclerView.Adapter<ReasonAdapter.MyViewHolder> {

        private final List<String> list;

        private ReasonAdapter(List<String> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ReasonAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ReasonAdapter.MyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cancel_reasons_inflate, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ReasonAdapter.MyViewHolder holder, int position) {
            String data = list.get(position);
            holder.tvReason.setText(data);
            holder.cbItem.setChecked(last_selected_location == position);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            LinearLayout llItemView;
            TextView tvReason;
            CheckBox cbItem;

            MyViewHolder(View view) {
                super(view);
                llItemView = view.findViewById(R.id.llItemView);
                tvReason = view.findViewById(R.id.tvReason);
                cbItem = view.findViewById(R.id.cbItem);

                llItemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                if (position == -1) {
                    cancelReason.setVisibility(View.VISIBLE);
                } else {
                    cancelReason.setVisibility(View.GONE);
                }
                submit.setEnabled(true);
                last_selected_location = position;
                notifyDataSetChanged();
            }
        }
    }
}
