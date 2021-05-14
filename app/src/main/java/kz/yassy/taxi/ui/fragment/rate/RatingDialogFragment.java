package kz.yassy.taxi.ui.fragment.rate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseBottomSheetDialogFragment;
import kz.yassy.taxi.data.network.model.Datum;
import kz.yassy.taxi.ui.activity.main.MainActivity;
import kz.yassy.taxi.ui.fragment.invoice.CheckDialogFragment;
import kz.yassy.taxi.ui.fragment.invoice.InvoiceFragment;

import static kz.yassy.taxi.MvpApplication.DATUM;
import static kz.yassy.taxi.chat.ChatActivity.chatPath;
import static kz.yassy.taxi.common.Constants.BroadcastReceiver.INTENT_FILTER;
import static kz.yassy.taxi.common.Constants.Status.EMPTY;

public class RatingDialogFragment extends BaseBottomSheetDialogFragment implements RatingIView {

    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.comment)
    EditText comment;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.ratings_name)
    TextView ratingsName;
    @BindView(R.id.rcvReason)
    RecyclerView rcvReason;

    private RatingPresenter<RatingDialogFragment> presenter = new RatingPresenter<>();
    private List<String> cancelResponseList = new ArrayList<>(Arrays.asList("Комфортная поездка", "Вежливый водитель", "Хорошая музыка"));
    private RatingAdapter adapter;
    private int last_selected_location = -1;
    private boolean isRatingPositive = true;

    public RatingDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_rating_dialog;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initView(View view) {
        InvoiceFragment.isInvoiceCashToCard = false;
        setCancelable(false);
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        getDialog().setCanceledOnTouchOutside(false);
        ButterKnife.bind(this, view);
        presenter.attachView(this);
        rating.setRating(0);
//        adapter = new RatingAdapter(cancelResponseList);

        ((MainActivity) Objects.requireNonNull(getActivity())).changeFlow("EMPTY", false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager
                (getContext(), LinearLayoutManager.VERTICAL, false);
        rcvReason.setLayoutManager(mLayoutManager);
        rcvReason.setItemAnimator(new DefaultItemAnimator());
//        rcvReason.setAdapter(adapter);

        rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (Math.round(rating) < 4) {
                if (isRatingPositive) {
                    last_selected_location = -1;
                }
                cancelResponseList = new ArrayList<>(Arrays.asList("Грубый водитель", "Неприятный запах в авто", "Испачкан салон"));
                rcvReason.setAdapter(new RatingAdapter(cancelResponseList));
                isRatingPositive = false;
                ratingsName.setText("Что вам не понравилось?");
            } else {
                if (!isRatingPositive) {
                    last_selected_location = -1;
                }
                cancelResponseList = new ArrayList<>(Arrays.asList("Комфортная поездка", "Вежливый водитель", "Хорошая музыка"));
                rcvReason.setAdapter(new RatingAdapter(cancelResponseList));
                isRatingPositive = true;
                ratingsName.setText("Что вам понравилось?");
            }
        });
    }

    @Override
    public void onSuccess(Object object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        dismiss();
        Objects.requireNonNull(getActivity()).sendBroadcast(new Intent(INTENT_FILTER));
        new CheckDialogFragment().show(getFragmentManager(), null);
        ((MainActivity) Objects.requireNonNull(getContext())).changeFlow(EMPTY, false);
        if (!TextUtils.isEmpty(chatPath))
            FirebaseDatabase.getInstance().getReference().child(chatPath).removeValue();
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R.id.submit)
    public void onViewClicked() {
        if (DATUM != null) {
            Datum datum = DATUM;
            HashMap<String, Object> map = new HashMap<>();
            map.put("request_id", datum.getId());
            map.put("rating", Math.round(rating.getRating()));
            map.put("comment", last_selected_location == -1 ? "" : cancelResponseList.get(last_selected_location));
            showLoading();
            presenter.rate(map);
        }
    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }


    private class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.MyViewHolder> {

        private final List<String> list;

        private RatingAdapter(List<String> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public RatingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RatingAdapter.MyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rating_reasons_inflate, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RatingAdapter.MyViewHolder holder, int position) {
            String data = list.get(position);
            holder.tvReason.setText(data);
            holder.done.setVisibility(last_selected_location == position ? View.VISIBLE : View.INVISIBLE);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            RelativeLayout llItemView;
            TextView tvReason;
            ImageView done;

            MyViewHolder(View view) {
                super(view);
                llItemView = view.findViewById(R.id.llItemView);
                tvReason = view.findViewById(R.id.tvReason);
                done = view.findViewById(R.id.cbItem);

                llItemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                submit.setEnabled(true);
                last_selected_location = position;
                notifyDataSetChanged();
            }
        }
    }
}
