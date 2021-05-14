package kz.yassy.taxi.ui.fragment.invoice;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseDialogFragment;
import kz.yassy.taxi.data.network.model.Datum;
import kz.yassy.taxi.data.network.model.PastTrip;
import kz.yassy.taxi.data.network.model.SearchRoute;
import kz.yassy.taxi.ui.activity.main.MainActivity;
import kz.yassy.taxi.ui.activity.past_trip_detail.PastTripDetailsIView;
import kz.yassy.taxi.ui.activity.past_trip_detail.PastTripDetailsPresenter;
import kz.yassy.taxi.ui.fragment.dispute.DisputeCallBack;
import kz.yassy.taxi.ui.fragment.dispute.DisputeFragment;

import static kz.yassy.taxi.MvpApplication.DATUM;

public class CheckDialogFragment extends BaseDialogFragment implements PastTripDetailsIView, DisputeCallBack {

    TextView sAddress;
    TextView dAddress;
    TextView paymentMode;
    TextView payable;

    private Datum datum;

    private PastTripDetailsPresenter<CheckDialogFragment> presenter = new PastTripDetailsPresenter();
    private TextView estimatedFare, waitingAmount;

    @Override
    public int getLayoutId() {
        return R.layout.dialog_check;
    }

    @Override
    protected View initView(View view) {
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(CheckDialogFragment.this.getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_check, null);
        presenter.attachView(this);
        if (DATUM != null) {
            showLoading();
            presenter.getPastTripDetails(DATUM.getId());
        }
        view.findViewById(R.id.submit).setOnClickListener(v -> {
            ((MainActivity) Objects.requireNonNull(getActivity())).changeFlow("EMPTY", false);
            dismiss();
        });
        sAddress = view.findViewById(R.id.s_address);
        dAddress = view.findViewById(R.id.d_address);
        paymentMode = view.findViewById(R.id.payment_mode);
        payable = view.findViewById(R.id.payable);
        estimatedFare = view.findViewById(R.id.estimated_fare);
        waitingAmount = view.findViewById(R.id.waiting_amount);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof DisputeFragment) ((DisputeFragment) fragment).setCallBack(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccess(PastTrip pastTrip) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Log.e("PastTrip", pastTrip.toString());
        sAddress.setText(pastTrip.getsAddress());
        dAddress.setText(pastTrip.getdAddress());
        payable.setText((Integer.parseInt(pastTrip.getTotal()) + pastTrip.getWaitingAmount()) + " ₸");
        waitingAmount.setText(pastTrip.getWaitingAmount() + " ₸");
        estimatedFare.setText(pastTrip.getEstimatedFare() + " ₸");
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onSuccessRoute(SearchRoute r) {

    }

    @Override
    public void onRouteError(Throwable throwable) {

    }

    @Override
    public void onDisputeCreated() {
        presenter.getPastTripDetails(DATUM.getId());
    }
}
