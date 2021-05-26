package kz.yassy.taxi.ui.fragment.payment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseBottomSheetDialogFragment;
import kz.yassy.taxi.data.network.model.User;

public class PaymentDetailsBusinessFragment extends BaseBottomSheetDialogFragment implements PaymentIView {
    private final PaymentPresenter<PaymentDetailsBusinessFragment> presenter = new PaymentPresenter<>();
    @BindView(R.id.companyName)
    TextView companyName;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.form_btn)
    Button submit;

    @BindView(R.id.staticLayout)
    LinearLayout staticLayout;
    private User user;

    public PaymentDetailsBusinessFragment(User user) {
        this.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_payment_details_business;
    }

    @Override
    public void initView(View view) {
        if (getDialog() != null) {
            getDialog().setOnShowListener(dialog -> {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetInternal = d.findViewById(R.id.design_bottom_sheet);
                if (bottomSheetInternal != null) {
                    BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            });
        }
        ButterKnife.bind(this, view);
        presenter.attachView(this);
        if (user.getIsBusiness() == 1) {
            companyName.setText(user.getBusinessName());
            status.setText(user.getBusinessPosition());
            submit.setVisibility(View.GONE);
        } else {
            staticLayout.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.form_btn})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.form_btn) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(user.getBusinessUrl()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
    }

    private void sendMail(String Mail) {
        if (Mail == null) {
            String appName = getString(R.string.app_name) + " " + getString(R.string.help);
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto: " + Mail));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, appName);
            startActivity(Intent.createChooser(emailIntent, "Send feedback"));
        }
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        if (isWhatsappInstalled) {
            Uri uri = Uri.parse("smsto:" + "77016410404");
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "");
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } else {
            Toast.makeText(getContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goToMarket);

        }
    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getContext().getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

}
