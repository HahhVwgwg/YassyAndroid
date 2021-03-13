package com.thinkincab.app.ui.fragment.service_flow;

import com.thinkincab.app.base.MvpView;
import com.thinkincab.app.data.network.model.DataResponse;

public interface ServiceFlowIView extends MvpView {
    void onSuccess(DataResponse dataResponse);
}
