package kz.yassy.taxi.ui.fragment.past_trip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseFragment;
import kz.yassy.taxi.data.network.model.Datum;
import kz.yassy.taxi.ui.activity.past_trip_detail.PastTripDetailForHistoryActivity;

public class PastTripFragment extends BaseFragment implements PastTripIView {

    @BindView(R.id.past_trip_rv)
    RecyclerView pastTripRv;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    @BindView(R.id.tv_error)
    TextView error;
    Unbinder unbinder;

    List<Datum> list = new ArrayList<>();
    TripAdapter adapter;

    private PastTripPresenter<PastTripFragment> presenter = new PastTripPresenter<>();

    public PastTripFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_past_trip;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        error.setText(getString(R.string.no_past_found));
        adapter = new TripAdapter(list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager
                (getActivity(), LinearLayoutManager.VERTICAL, false);
        pastTripRv.setLayoutManager(mLayoutManager);
        pastTripRv.setItemAnimator(new DefaultItemAnimator());
        pastTripRv.setAdapter(adapter);
        presenter.pastTrip();
        showLoading();
        return view;
    }

    @Override
    public void onSuccess(List<Datum> datumList) {
        hideLoading();
        list.clear();
        list.addAll(datumList);
        adapter.notifyDataSetChanged();

        if (list.isEmpty()) errorLayout.setVisibility(View.VISIBLE);
        else errorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onError(Throwable e) {
        hideLoading();
        handleError(e);
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }

    private class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder> {

        private List<Datum> list;
        private Context mContext;

        private TripAdapter(List<Datum> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            return new MyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_past_trip, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Datum datum = list.get(position);
            if (datum.getCancelledBy().equals("NOT_FOUND"))
                return;

            switch (datum.getCancelledBy()) {
                case "NONE":
                    holder.status.setText("Поездка завершена");
                    break;
                case "USER":
                    holder.status.setText("Вы отменили");
                    break;
                case "PROVIDER":
                    holder.status.setText("Водитель отменил");
                    break;
            }
            holder.timestamp.setText(datum.getCreatedAt());
            holder.dAddress.setText(datum.getDAddress());
            holder.sAddress.setText(datum.getSAddress());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private LinearLayout itemView;
            private TextView sAddress, dAddress, timestamp, status;

            MyViewHolder(View view) {
                super(view);
                itemView = view.findViewById(R.id.item_view);
                sAddress = view.findViewById(R.id.s_address);
                dAddress = view.findViewById(R.id.d_address);
                timestamp = view.findViewById(R.id.timestamp);
                status = view.findViewById(R.id.status);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                if (view.getId() == R.id.item_view) {
//                    MvpApplication.DATUM = ;
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation
                            (baseActivity(), itemView, ViewCompat.getTransitionName(itemView));
                    Intent intent = new Intent(getActivity(), PastTripDetailForHistoryActivity.class);
                    intent.putExtra("datumId", list.get(position).getId());
                    startActivity(intent, options.toBundle());
                }
            }
        }
    }
}
