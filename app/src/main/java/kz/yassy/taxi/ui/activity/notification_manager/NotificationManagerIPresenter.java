package kz.yassy.taxi.ui.activity.notification_manager;

import kz.yassy.taxi.base.MvpPresenter;

public interface NotificationManagerIPresenter<V extends NotificationManagerIView> extends MvpPresenter<V> {
    void getNotificationManager();
}
