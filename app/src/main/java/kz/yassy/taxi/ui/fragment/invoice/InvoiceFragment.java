package kz.yassy.taxi.ui.fragment.invoice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.google.gson.Gson;
import com.razorpay.Checkout;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import kz.yassy.taxi.BuildConfig;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseFragment;
import kz.yassy.taxi.common.Constants;
import kz.yassy.taxi.data.SharedHelper;
import kz.yassy.taxi.data.network.model.BrainTreeResponse;
import kz.yassy.taxi.data.network.model.CheckSumData;
import kz.yassy.taxi.data.network.model.Datum;
import kz.yassy.taxi.data.network.model.Message;
import kz.yassy.taxi.data.network.model.Payment;
import kz.yassy.taxi.data.network.model.User;
import kz.yassy.taxi.ui.activity.main.MainActivity;

import static android.app.Activity.RESULT_OK;
import static kz.yassy.taxi.MvpApplication.DATUM;
import static kz.yassy.taxi.MvpApplication.RIDE_REQUEST;
import static kz.yassy.taxi.base.BaseActivity.online;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.CARD_ID;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.CARD_LAST_FOUR;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.PAYMENT_MODE;
import static kz.yassy.taxi.common.Constants.Status.RATING;
import static kz.yassy.taxi.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

//import com.appoets.braintreepayment.BrainTreePaymentActivity;
//import com.appoets.paytmpayment.PaytmCallback;
//import com.appoets.paytmpayment.PaytmConstants;
//import com.appoets.paytmpayment.PaytmObject;
//import com.appoets.paytmpayment.PaytmPayment;

public class InvoiceFragment extends BaseFragment implements InvoiceIView {

    private static final int BRAINTREE_PAYMENT_REQUEST_CODE = 101;
    private static final String TAG = "InvoiceFragment";
    public static boolean isInvoiceCashToCard = false;
    @BindView(R.id.fragment_invoice)
    NestedScrollView containerScroll;
    @BindView(R.id.payment_mode)
    TextView tvPaymentMode;
    @BindView(R.id.pay_now)
    Button payNow;
    @BindView(R.id.done)
    Button done;
    @BindView(R.id.booking_id)
    TextView bookingId;
    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.travel_time)
    TextView travelTime;
    @BindView(R.id.fixed)
    TextView fixed;
    @BindView(R.id.distance_fare)
    TextView distanceFare;
    @BindView(R.id.tax)
    TextView tax;
    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.payable)
    TextView payable;
    @BindView(R.id.wallet_detection)
    TextView walletDetection;
    @BindView(R.id.time_fare)
    TextView timeFare;
    @BindView(R.id.llDistanceFareContainer)
    LinearLayout llDistanceFareContainer;
    @BindView(R.id.llTimeFareContainer)
    LinearLayout llTimeFareContainer;
    @BindView(R.id.llTipContainer)
    LinearLayout llTipContainer;
    @BindView(R.id.llWalletDeductionContainer)
    LinearLayout llWalletDeductionContainer;
    @BindView(R.id.llDiscountContainer)
    LinearLayout llDiscountContainer;
    @BindView(R.id.llPaymentContainer)
    LinearLayout llPaymentContainer;
    @BindView(R.id.llTravelTime)
    LinearLayout llTravelTime;
    @BindView(R.id.llBaseFare)
    LinearLayout llBaseFare;
    @BindView(R.id.llPayable)
    LinearLayout llPayable;
    @BindView(R.id.llWaitingAmountContainer)
    LinearLayout llWaitingAmountContainer;
    @BindView(R.id.llTolleChargeContainer)
    LinearLayout llTolleChargeContainer;
    @BindView(R.id.llRoundOffContainer)
    LinearLayout llRoundOffContainer;
//    @BindView(R.id.tvChange)
//    TextView tvChange;
    @BindView(R.id.tvGiveTip)
    TextView tvGiveTip;
    @BindView(R.id.tvTipAmt)
    TextView tvTipAmt;
    @BindView(R.id.tvDiscount)
    TextView tvDiscount;
    @BindView(R.id.tvWaitingAmount)
    TextView tvWaitingAmount;
    @BindView(R.id.tvTollCharges)
    TextView tvTollCharges;
    @BindView(R.id.tvRoundOff)
    TextView tvRoundOff;
    @BindView(R.id.tvWaitingTimeDesc)
    TextView tvWaitingTimeDesc;
    @BindView(R.id.tvWaitingTime)
    TextView tvWaitingTime;

    TextView driverBeta;
    @BindView(R.id.layout_normal_flow)
    LinearLayout layout_normal_flow;
    @BindView(R.id.layout_rental_flow)
    LinearLayout layout_rental_flow;
    @BindView(R.id.rental_normal_price)
    TextView rentalNormalPrice;

    @BindView(R.id.textView10)
    TextView rentalpackage;
    @BindView(R.id.rental_total_distance_km)
    TextView rentalTotalDistance;
    @BindView(R.id.rental_extra_hr_km_price)
    TextView rentalExtraHrKmPrice;
    @BindView(R.id.rental_travel_time)
    TextView rentalTravelTime;
    @BindView(R.id.rental_hours)
    TextView rentalHours;

    @BindView(R.id.layout_outstation_flow)
    LinearLayout layout_outstation_flow;
    @BindView(R.id.outstation_distance_travelled)
    TextView outstationDistanceTravelled;
    @BindView(R.id.outstation_distance_fare)
    TextView outstationDistanceFare;
    @BindView(R.id.outstation_driver_beta)
    TextView outstationDriverBeta;
    @BindView(R.id.start_date)
    TextView startDate;
    @BindView(R.id.end_date)
    TextView endDate;
    @BindView(R.id.outstation_round_single)
    TextView outstationRoundSingle;
    @BindView(R.id.outstation_no_of_days)
    TextView outstationNoOfDays;

    @BindView(R.id.peek_hour_charges)
    TextView peekHourCharges;
    @BindView(R.id.night_fare)
    TextView nightFare;


    private InvoicePresenter<InvoiceFragment> presenter = new InvoicePresenter<>();
    private BraintreeFragment mBrainTreeFragment;
    private Payment payment;
    private String payingMode;
    private int tips = 0;

    public InvoiceFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_invoice;
    }

    @Override
    public View initView(View view) {
        ButterKnife.bind(this, view);
        presenter.attachView(this);
        try {
            mBrainTreeFragment = BraintreeFragment.newInstance(baseActivity(), BuildConfig.PAYPAL_CLIENT_TOKEN);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
        if (DATUM != null) {
            containerScroll.setVisibility(View.VISIBLE);
            ((MainActivity) Objects.requireNonNull(getContext())).changeFlow(RATING, false);
//            initView(DATUM);
        } else {
            containerScroll.setVisibility(View.GONE);
        }
        if(!online)
        {
            payNow.setVisibility(View.GONE);
        }
        return view;
    }

    String convertDateFormat(String date) {
        String newDateString = "null";
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        try {
            if (date == null) {
                Date newDate = spf.parse(date);
                newDateString = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault()).format(newDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDateString;
    }

    @SuppressLint("StringFormatInvalid")
    private void initView(@NonNull Datum datum) {
        bookingId.setText(datum.getBookingId());
        startDate.setText(convertDateFormat(datum.getStartedAt()));
        endDate.setText(convertDateFormat(datum.getFinishedAt()));
        distance.setText(String.valueOf(datum.getDistance() + " km"));
        if (SharedHelper.getKey(getContext(), "measurementType").equalsIgnoreCase(Constants.MeasurementType.KM)) {
            if (datum.getDistance() > 1 || datum.getDistance() > 1.0)
                distance.setText(String.format("%s %s", datum.getDistance(), Constants.MeasurementType.KM));
            else
                distance.setText(String.format("%s %s", datum.getDistance(), getString(R.string.km)));
        } else {
            if (datum.getDistance() > 1 || datum.getDistance() > 1.0)
                distance.setText(String.format("%s %s", datum.getDistance(), Constants.MeasurementType.MILES));
            else
                distance.setText(String.format("%s %s", datum.getDistance(), getString(R.string.mile)));
        }
        try {
            if (datum.getTravelTime() != null && Double.parseDouble(datum.getTravelTime()) > 0) {
                llTravelTime.setVisibility(View.VISIBLE);
                travelTime.setText(getString(R.string._min, datum.getTravelTime()));
            } else llTravelTime.setVisibility(View.GONE);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            llTravelTime.setVisibility(View.VISIBLE);
            travelTime.setText(getString(R.string._min, datum.getTravelTime()));
        }
        initPaymentView(datum.getPaymentMode(), "", false);

        payment = datum.getPayment();
        try {
            if (payment != null) {
                String crr = SharedHelper.getKey(getContext(), "currency");
                fixed.setText(crr +" "+(payment.getFixed()));
                peekHourCharges.setText(crr +" "+(payment.getPeakPrice()));
                tax.setText(crr +" "+(payment.getTax()));
                total.setText(crr +" "+(payment.getTotal()));
                payable.setText(crr +" "+(payment.getPayable()));
                nightFare.setText(crr +" "+(payment.getNightFare()));
                walletDetection.setText(crr +" "+(payment.getWallet()));

                //      rental
                rentalNormalPrice.setText(crr +" "+(payment.getMinute()));
                if(datum.getRent_plan()!=null)
                {
                    rentalpackage.setText(datum.getRent_plan().getHour()+"");
                    String plan = String.format("%s Hr / %sKm ", datum.getRent_plan().getHour(), datum.getRent_plan().getKm());
                    rentalpackage.setText(plan);
                }
                rentalTravelTime.setText(getString(R.string._min, datum.getTravelTime()));
                rentalTotalDistance.setText(String.valueOf(datum.getDistance() + " km"));
                rentalExtraHrKmPrice.setText(crr +" "+(payment.getRentalExtraHrPrice() + payment.getRentalExtraKmPrice()));

                //      outstation
//            outstationDriverBeta.setText(String.format("(%s Days) %s", payment.getOutstationDays(), crr +" "+(payment.getDriverBeta())));
                outstationDriverBeta.setText(String.format(crr +" "+(payment.getDriverBeta())));

                DecimalFormat df = new DecimalFormat("#.00");
                String summy=crr +" "+df.format(Math.abs(payment.getPayable() - (payment.getDriverBeta() + payment.getTax())));

                outstationDistanceFare.setText(summy);
                if(datum.getDay()!=null)
                {
                       if(datum.getDay().equals("round"))
                    outstationRoundSingle.setText("Round Trip");
                else
                    outstationRoundSingle.setText("Oneway Trip");
                }

                outstationDistanceTravelled.setText(String.valueOf(datum.getDistance() + " km"));
//            outstationNoOfDays.setText(String.format("%s Days", payment.getOutstationDays()));
                String serviceReq = datum.getServicerequired();
                switch (serviceReq) {
                    case "none":
                        layout_normal_flow.setVisibility(View.VISIBLE);
                        break;
                    case "rental":
                        layout_rental_flow.setVisibility(View.VISIBLE);
                        break;
                    case "outstation":
                        layout_outstation_flow.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
                if (payment.getFixed() > 0) {
                    llBaseFare.setVisibility(View.VISIBLE);
                    Double d = payment.getFixed() - payment.getDistance();
                    int i = d.intValue();
                    fixed.setText(String.format("%s  %s", SharedHelper.getKey(getContext(), "currency") ,(i)));
                } else llBaseFare.setVisibility(View.GONE);
                Double dt = payment.getTax();
                int ii = dt.intValue();
                tax.setText(String.format("%s", SharedHelper.getKey(getContext(), "currency") + " " +(ii)));
                Double dtt = payment.getTotal();
                int iii = dtt.intValue();
                total.setText(String.format("%s", SharedHelper.getKey(getContext(), "currency") + " " +(iii)));

                if (payment.getPayable() > 0) {
                    llPayable.setVisibility(View.VISIBLE);
                    payable.setText(String.format("%s", SharedHelper.getKey(getContext(), "currency") + " " +(payment.getPayable())));
                } else llPayable.setVisibility(View.GONE);

                if (payment.getWallet() > 0) {
                    llWalletDeductionContainer.setVisibility(View.VISIBLE);
                    walletDetection.setText(String.format("%s", SharedHelper.getKey(getContext(), "currency") + " " +(payment.getWallet())));
                } else llWalletDeductionContainer.setVisibility(View.GONE);

                if (payment.getDiscount() > 0) {
                    llDiscountContainer.setVisibility(View.VISIBLE);
                    tvDiscount.setText(String.format("-%s", SharedHelper.getKey(getContext(), "currency") + " " +(payment.getDiscount())));
                } else llDiscountContainer.setVisibility(View.GONE);

                if (payment.getWaitingAmount() > 0) {
                    tvWaitingTimeDesc.setVisibility(View.GONE);
                    llWaitingAmountContainer.setVisibility(View.VISIBLE);
                    tvWaitingTime.setText(getString(R.string.waiting_amount));
                    tvWaitingAmount.setText(String.format("%s", SharedHelper.getKey(getContext(), "currency") + " " +(payment.getWaitingAmount())));
                } else if (payment.getWaitingAmount() == 0
                        && datum.getServiceType().getWaitingMinCharge() == 0) {
                    tvWaitingTimeDesc.setVisibility(View.VISIBLE);
                    llWaitingAmountContainer.setVisibility(View.VISIBLE);
                    tvWaitingTime.setText(getString(R.string.waiting_amount_star));
                    tvWaitingAmount.setText(String.format("%s", "0"));
                } else {
                    tvWaitingTimeDesc.setVisibility(View.GONE);
                    tvWaitingTime.setText(getString(R.string.waiting_amount));
                    llWaitingAmountContainer.setVisibility(View.GONE);
                }

                if (payment.getToll_charge() > 0) {
                    llTolleChargeContainer.setVisibility(View.VISIBLE);
                    tvTollCharges.setText(String.format("%s", SharedHelper.getKey(getContext(), "currency") + " " +(payment.getToll_charge())));
                } else llTolleChargeContainer.setVisibility(View.GONE);

                if (payment.getRoundOf() != 0) {
                    //llRoundOffContainer.setVisibility(View.VISIBLE);
                    tvRoundOff.setText(String.format("%s", SharedHelper.getKey(getContext(), "currency") + " " +(payment.getRoundOf())));
                } //else //llRoundOffContainer.setVisibility(View.GONE);

                //      MIN,    HOUR,   DISTANCE,   DISTANCEMIN,    DISTANCEHOUR
                if (datum.getServiceType().getCalculator().equalsIgnoreCase(Constants.InvoiceFare.MINUTE)
                        || datum.getServiceType().getCalculator().equalsIgnoreCase(Constants.InvoiceFare.HOUR)) {
                    llTimeFareContainer.setVisibility(View.VISIBLE);
                    llDistanceFareContainer.setVisibility(View.GONE);
                    distanceFare.setText(R.string.time_fare);
                    if (datum.getServiceType().getCalculator().equalsIgnoreCase(Constants.InvoiceFare.MINUTE)) {
                        if (payment.getMinute() > 0) {
                            llTimeFareContainer.setVisibility(View.VISIBLE);
                            timeFare.setText(String.format("%s", SharedHelper.getKey(getContext(), "currency") + " " +(payment.getMinute())));
                        } else llTimeFareContainer.setVisibility(View.GONE);

                    } else if (datum.getServiceType().getCalculator().equalsIgnoreCase(Constants.InvoiceFare.HOUR)) {
                        if (payment.getHour() > 0) {
                            llTimeFareContainer.setVisibility(View.VISIBLE);
                            timeFare.setText(String.format("%s", SharedHelper.getKey(getContext(), "currency") + " " +(payment.getHour())));
                        } else llTimeFareContainer.setVisibility(View.GONE);
                    }
                } else if (datum.getServiceType().getCalculator().equalsIgnoreCase(Constants.InvoiceFare.DISTANCE)) {
                    llTimeFareContainer.setVisibility(View.GONE);
                    if (payment.getDistance() == 0.0 || payment.getDistance() == 0)
                        llDistanceFareContainer.setVisibility(View.GONE);
                    else {
                        llDistanceFareContainer.setVisibility(View.VISIBLE);
                        distanceFare.setText(String.format("%s", SharedHelper.getKey(getContext(), "currency") + " " +(payment.getDistance())));
                    }
                } else if (datum.getServiceType().getCalculator().equalsIgnoreCase(Constants.InvoiceFare.DISTANCE_MIN)) {
                    if (payment.getDistance() == 0.0 || payment.getDistance() == 0)
                        llDistanceFareContainer.setVisibility(View.GONE);
                    else {
                        llDistanceFareContainer.setVisibility(View.VISIBLE);
                        distanceFare.setText(String.format("%s", SharedHelper.getKey(getContext(), "currency") + " " +(payment.getDistance())));
                    }
                    if (payment.getMinute() > 0) {
                        llTimeFareContainer.setVisibility(View.VISIBLE);
                        timeFare.setText(String.format("%s", SharedHelper.getKey(getContext(), "currency") + " " +(payment.getMinute())));
                    } else llTimeFareContainer.setVisibility(View.GONE);
                } else if (datum.getServiceType().getCalculator().equalsIgnoreCase(Constants.InvoiceFare.DISTANCE_HOUR)) {
                    if (payment.getDistance() == 0.0 || payment.getDistance() == 0) {
                        llDistanceFareContainer.setVisibility(View.GONE);
                    } else {
                        llDistanceFareContainer.setVisibility(View.VISIBLE);
                        distanceFare.setText(String.format("%s", SharedHelper.getKey(getContext(), "currency") + " " +(payment.getDistance())));
                    }
                    if (payment.getHour() > 0) {
                        llTimeFareContainer.setVisibility(View.VISIBLE);
                        timeFare.setText(String.format("%s", SharedHelper.getKey(getContext(), "currency") + " " +(payment.getHour())));
                    } else llTimeFareContainer.setVisibility(View.GONE);
                }containerScroll.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Payment under procesing",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) Objects.requireNonNull(getContext())).changeFlow("RATING", false);
//        Datum datum = DATUM;
//        if (datum.getPaymentMode() != null) payingMode = datum.getPaymentMode();
//        llPaymentContainer.setVisibility(datum.getPaid() == 1 ? View.GONE : View.VISIBLE);
//
//        //TODO ALLAN - Alterações débito na máquina e voucher
//        if (datum.getPaid() == 0 && payingMode.equalsIgnoreCase("CASH") || datum.getPaid() == 0 && payingMode.equalsIgnoreCase("DEBIT_MACHINE")) {
//            done.setVisibility(View.VISIBLE);
//         //   payNow.setVisibility(View.GONE);
//            llTipContainer.setVisibility(View.GONE);
//
//            done.setOnClickListener(v -> Toasty.info(getContext(), getString(R.string.payment_not_confirmed), Toast.LENGTH_SHORT).show());
//        } else if (datum.getPaid() == 0 && !payingMode.equalsIgnoreCase("CASH")) {
//            done.setVisibility(View.GONE);
//            payNow.setVisibility(View.VISIBLE);
//            llTipContainer.setVisibility(View.VISIBLE);
//
//        } else if (datum.getPaid() == 1 && payingMode.equalsIgnoreCase("CASH")) {
//            done.setText("Done");
//            done.setVisibility(View.VISIBLE);
//            payNow.setVisibility(View.GONE);
//            llTipContainer.setVisibility(View.GONE);
//
//        } else if (datum.getPaid() == 1 && !payingMode.equalsIgnoreCase("CASH")) {
//            done.setText("Done");
//            done.setVisibility(View.VISIBLE);
//            payNow.setVisibility(View.GONE);
//            llTipContainer.setVisibility(View.GONE);
//
//        }

        //		By Rajaganapathi(Cross check it)
//        tvChange.setVisibility((!isCard && isCash) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onSuccess(Object obj) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onSuccessPayment(Object o) {
        Toast.makeText(getContext(), R.string.you_have_successfully_paid, Toast.LENGTH_SHORT).show();
        ((MainActivity) Objects.requireNonNull(getContext())).changeFlow("RATING", false);
    }

    @Override
    public void onSuccess(Message message) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if(message.getMessage().equals("Transaction Failed")){
            Toast.makeText(getContext(), "Card failed or insufficient balance! Please pay the driver in cash.", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getContext(), R.string.you_have_successfully_paid, Toast.LENGTH_SHORT).show();
            ((MainActivity) Objects.requireNonNull(getContext())).changeFlow(RATING, false);
        }

    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

//    @OnClick({R.id.payment_mode, R.id.pay_now, R.id.done, R.id.tvChange, R.id.tvGiveTip, R.id.tvTipAmt, R.id.ivInvoice})
    @OnClick({R.id.payment_mode, R.id.pay_now, R.id.done, R.id.tvGiveTip, R.id.tvTipAmt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
//            case R.id.tvChange:
//            case R.id.payment_mode:
//                startActivityForResult(new Intent(getActivity(), PaymentActivity.class), PICK_PAYMENT_METHOD);
//                break;
            case R.id.pay_now:
                if(online)
                startPaymentRaz();

                break;
            case R.id.done:
            case R.id.ivInvoice:
                ((MainActivity) Objects.requireNonNull(getContext())).changeFlow(RATING, false);
                break;
            case R.id.tvGiveTip:
                showTipDialog(payment.getPayable());
                break;
        }
    }

    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            showLoading();
            HashMap<String, Object> cardHashMap = new HashMap<>();
            cardHashMap.put("request_id", DATUM.getId());
            cardHashMap.put("tips", tips);
            cardHashMap.put("payment_type", "ONLINE");
            presenter.payment(cardHashMap);

        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    String name;
    String email;
    String phone;
    public void startPaymentRaz() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this.getActivity();

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();

            User user = new Gson().fromJson(SharedHelper.getKey(this.getContext(), "userInfo"), User.class);
            if (user != null) {
                name=user.getFirstName();
                email=user.getEmail();
                phone=user.getMobile();
            }

            options.put("name", name);
            options.put("description", "Wallet ReCharges");

            options.put("currency", "INR");
            options.put("amount", ((int)payment.getTotal()*100));

            JSONObject preFill = new JSONObject();
            preFill.put("email", email);
            preFill.put("contact",phone);

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(getContext(), "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }

    private void showTipDialog(double totalAmount) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_tip);
        EditText etAmount = dialog.findViewById(R.id.etAmount);
        Button percent10 = dialog.findViewById(R.id.bt10Percent);
        Button percent15 = dialog.findViewById(R.id.bt15Percent);
        Button percent20 = dialog.findViewById(R.id.bt20Percent);
        TextView tvSubmit = dialog.findViewById(R.id.tvSubmit);

        percent10.setOnClickListener(v -> etAmount.setText(String.valueOf((totalAmount * 10) / 100)));

        percent15.setOnClickListener(v -> etAmount.setText(String.valueOf((totalAmount * 15) / 100)));

        percent20.setOnClickListener(v -> etAmount.setText(String.valueOf((totalAmount * 20) / 100)));

        tvSubmit.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etAmount.getText()) && Double.parseDouble(etAmount.getText().toString()) > 0) {
                tvGiveTip.setVisibility(View.GONE);
                tvTipAmt.setVisibility(View.VISIBLE);
                Double d = Double.parseDouble(etAmount.getText().toString());
                int i = d.intValue();
                tips = i;
                double payableCal = payment.getPayable() + tips;
                tvTipAmt.setText(String.format("%s", SharedHelper.getKey(getContext(), "currency") + " " +(tips)));
                if (payableCal > 0) {
                    llPayable.setVisibility(View.VISIBLE);
                    payable.setText(String.format("%s", SharedHelper.getKey(getContext(), "currency") + " " +(payableCal)));
                } else llPayable.setVisibility(View.GONE);
                dialog.dismiss();
            } else {
                tvGiveTip.setVisibility(View.VISIBLE);
                tvTipAmt.setVisibility(View.GONE);
                if (payment.getPayable() > 0) {
                    llPayable.setVisibility(View.VISIBLE);
                    payable.setText(String.format("%s", SharedHelper.getKey(getContext(), "currency") + " " +(payment.getPayable())));
                    tips = 0;
                } else llPayable.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        HashMap<String, Object> map = new HashMap<>();
        switch (requestCode) {
            case PICK_PAYMENT_METHOD:
                if (resultCode == Activity.RESULT_OK) {
                    RIDE_REQUEST.put(PAYMENT_MODE, data.getStringExtra("payment_mode"));
                    if (data.getStringExtra("payment_mode").equals(Constants.PaymentMode.CARD)) {
                        RIDE_REQUEST.put(CARD_ID, data.getStringExtra("card_id"));
                        RIDE_REQUEST.put(CARD_LAST_FOUR, data.getStringExtra("card_last_four"));
                        llTipContainer.setVisibility(View.VISIBLE);
//                        tvChange.setVisibility(View.GONE);
                        isInvoiceCashToCard = true;
                    } else if (data.getStringExtra("payment_mode").equals(Constants.PaymentMode.CASH)) {
                        RIDE_REQUEST.put(CARD_ID, null);
                        RIDE_REQUEST.put(CARD_LAST_FOUR, null);
                        llTipContainer.setVisibility(View.GONE);
//                        tvChange.setVisibility(View.VISIBLE);
                        isInvoiceCashToCard = false;
                    }

                    initPaymentView(data.getStringExtra("payment_mode"),
                            data.getStringExtra("card_last_four"), true);

                    showLoading();

                    map.put("request_id", DATUM.getId());
                    map.put("payment_mode", data.getStringExtra("payment_mode"));
                    if (data.getStringExtra("payment_mode").equals(Constants.PaymentMode.CARD))
                        map.put("card_id", data.getStringExtra("card_id"));

                    presenter.updateRide(map);
                }
                break;

            case BRAINTREE_PAYMENT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                   // String paymentNonce = data.getStringExtra(BrainTreePaymentActivity.PAYMENT_NONCE);
                    HashMap<String, Object> brainHashMap = new HashMap<>();
                    brainHashMap.put("request_id", DATUM.getId());
                    brainHashMap.put("tips", tips);
                   // brainHashMap.put("braintree_nonce", paymentNonce);
                    brainHashMap.put("payment_type", Constants.PaymentMode.BRAINTREE);

                    presenter.payment(brainHashMap);
                } else
                    Toasty.error(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    void initPaymentView(String paymentMode, String value, boolean payment) {

        switch (paymentMode) {
            case Constants.PaymentMode.CASH:
                tvPaymentMode.setText("CASH");
                break;
            case Constants.PaymentMode.DEBIT_MACHINE:
                tvPaymentMode.setText("DEBIT MACHINE");
                break;
            case Constants.PaymentMode.CARD:
                if (payment)
                    if (!value.equals("")) tvPaymentMode.setText(getString(R.string.card_, value));
                    else tvPaymentMode.setText("CARD");
                else tvPaymentMode.setText("CARD");
                break;
            case Constants.PaymentMode.BRAINTREE:
                tvPaymentMode.setText(getString(R.string.braintree));
                break;
            case Constants.PaymentMode.PAYTM:
                tvPaymentMode.setText(getString(R.string.paytm));
                break;
            case Constants.PaymentMode.PAYUMONEY:
                tvPaymentMode.setText(getString(R.string.payumoney));
                break;
            case Constants.PaymentMode.WALLET:
                tvPaymentMode.setText(getString(R.string.wallet));
                break;
            default:
                break;
        }
    }

    @Override
    public void onSuccess(BrainTreeResponse response) {
        if (!response.getToken().isEmpty()) {
           // Intent intent = new Intent(getActivity(), BrainTreePaymentActivity.class);
           // intent.putExtra(BrainTreePaymentActivity.EXTRAS_TOKEN, response.getToken());
           // startActivityForResult(intent, BRAINTREE_PAYMENT_REQUEST_CODE);
        }
    }

    @Override
    public void onPayumoneyCheckSumSucess(CheckSumData checkSumData) {

    }

/*    @Override
    public void onPayTmCheckSumSucess(PaytmObject payTmResponse) {
        *//*new PaytmPayment(Objects.requireNonNull(getActivity()), payTmResponse, new PaytmCallback() {
            @Override
            public void onPaytmSuccess(String status, String message, String paymentmode, String txid) {
                HashMap<String, Object> hashPaytmHashMap = new HashMap<>();
                hashPaytmHashMap.put("id", DATUM.getId());
                hashPaytmHashMap.put("pay", txid);
                hashPaytmHashMap.put("type", "app");
                hashPaytmHashMap.put("wallet", "0");
                presenter.updatePayment(hashPaytmHashMap);
            }

            @Override
            public void onPaytmFailure(String errorMessage) {
                Toasty.error(Objects.requireNonNull(getContext()), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }).setEnvironment(PaytmConstants.ENVIRONMENT_STAGING).startPayment();*//*
    }*/
}
