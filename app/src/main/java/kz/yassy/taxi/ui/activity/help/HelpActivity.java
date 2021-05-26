package kz.yassy.taxi.ui.activity.help;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.network.model.Help;
import kz.yassy.taxi.data.network.model.HelpLocal;

public class HelpActivity extends BaseActivity implements HelpIView {

    //    private String ContactNumber = null;
    private String Mail = null;
//    private HelpPresenter<HelpActivity> presenter = new HelpPresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_help;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
//        presenter.attachView(this);
//        presenter.help();
        Mail = "https://wa.me/77016410404";
        RecyclerView recyclerView = findViewById(R.id.help_options);
        List<HelpLocal> helpLocals = new ArrayList<>();
        helpLocals.add(new HelpLocal("В машине остались вещи", 0));
        helpLocals.add(new HelpLocal("Что-то с деньгами", 100));
        helpLocals.add(new HelpLocal("Хочу пожаловаться", 101));
        helpLocals.add(new HelpLocal("Рассказать об ошибке", 102));
        helpLocals.add(new HelpLocal("Экстренная ситуация", 1));
        helpLocals.add(new HelpLocal("Частые вопросы", 103));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new HelpAdapter(helpLocals));
    }

    @Override
    public void onSuccess(Help help) {
//        ContactNumber = help.getContactNumber();
//        Mail = help.getContactEmail();
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

//    private void callContactNumber() {
//        if (ContactNumber != null) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
//                    == PackageManager.PERMISSION_GRANTED)
//                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ContactNumber)));
//            else
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE);
//        }
//    }

    private void sendMail() {
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
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goToMarket);

        }
    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    @OnClick({R.id.back_btn, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.submit:
                sendMail();
                break;
//            case R.id.call:
//                callContactNumber();
//                break;
//            case R.id.mail:
//                sendMail();
//                break;
//            case R.id.web:
//                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://srsride.in/")));
//                break;
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
//        if (requestCode == PERMISSIONS_REQUEST_PHONE)
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                callContactNumber();
//    }

    @Override
    protected void onDestroy() {
//        presenter.onDetach();
        super.onDestroy();
    }


}
