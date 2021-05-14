package kz.yassy.taxi.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import kz.yassy.taxi.R;
import kz.yassy.taxi.common.CustomDialog;

public abstract class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment implements MvpView {

    private View view;
    private ProgressDialog progressDialog;
    private BaseActivity mBaseActivity;
    private CustomDialog customDialog;

    protected abstract int getLayoutId();

    protected abstract void initView(View view);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity){
            mBaseActivity = (BaseActivity) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(getLayoutId(), container, false);
            initView(view);
        }
        progressDialog = new ProgressDialog(baseActivity());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        customDialog = new CustomDialog(getContext());
        return view;
    }

    @Override
    public Activity baseActivity() {
        return mBaseActivity;
    }

    @Override
    public void showLoading() {
        if (customDialog != null)
            customDialog.show();
    }

    @Override
    public void hideLoading() {
        if (customDialog != null)
            customDialog.cancel();
    }


    protected void handleError(Throwable e) {
        if (mBaseActivity != null) {
            mBaseActivity.handleError(e);
        }
    }

    @Override
    public void onSuccessLogout(Object object) {
        if (mBaseActivity != null) {
            mBaseActivity.onSuccessLogout(object);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (mBaseActivity != null) {
            mBaseActivity.onError(throwable);
        }
    }

//    protected String getNewNumberFormat(double d) {
//        return BaseActivity.SharedHelper.getKey(this, "currency") + " " +(d);
//    }

    @Override
    public void onDetach() {
        mBaseActivity = null;
        super.onDetach();
    }
}
