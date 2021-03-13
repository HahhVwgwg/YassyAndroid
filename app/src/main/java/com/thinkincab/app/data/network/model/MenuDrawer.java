package com.thinkincab.app.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thinkincab.app.R;

public class MenuDrawer {
    @SerializedName("icon")
    @Expose
    private int icon;
    @SerializedName("icon")
    @Expose
    private int name;

    public int getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public void setName(int name) {
        this.name = name;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public static class MenuMain extends MenuDrawer {
        public MenuMain() {
            setIcon(R.drawable.ic_main_drawer);
            setName(R.string.drawer_menu_main);
        }
    }
    public static class MenuFavorites extends MenuDrawer {
        public MenuFavorites() {
            setIcon(R.drawable.ic_bookmark_drawer);
            setName(R.string.drawer_menu_favorites);
        }
    }
    public static class MenuEarlyOrder extends MenuDrawer {
        public MenuEarlyOrder() {
            setIcon(R.drawable.ic_time_drawer);
            setName(R.string.drawer_menu_early_order);
        }
    }
    public static class MenuPayments extends MenuDrawer {
        public MenuPayments() {
            setIcon(R.drawable.ic_money_drawer);
            setName(R.string.drawer_menu_payments);
        }
    }
    public static class MenuHistory extends MenuDrawer {
        public MenuHistory() {
            setIcon(R.drawable.ic_history_drawer);
            setName(R.string.drawer_menu_history);
        }
    }
    public static class MenuHelp extends MenuDrawer {
        public MenuHelp() {
            setIcon(R.drawable.ic_support_drawer);
            setName(R.string.drawer_menu_help);
        }
    }
    public static class MenuSettings extends MenuDrawer {
        public MenuSettings() {
            setIcon(R.drawable.ic_settings_drawer);
            setName(R.string.drawer_menu_settings);
        }
    }
    public static class MenuDriver extends MenuDrawer {
        public MenuDriver() {
            setIcon(R.drawable.ic_settings_drawer);
            setName(R.string.drawer_menu_driver);
        }
    }
    public static class MenuNews extends MenuDrawer {
        public MenuNews() {
            setIcon(R.drawable.ic_settings_drawer);
            setName(R.string.drawer_menu_news);
        }
    }
    public static class MenuInfo extends MenuDrawer {
        public MenuInfo() {
            setIcon(R.drawable.ic_settings_drawer);
            setName(R.string.drawer_menu_info);
        }
    }
    public static class MenuLogout extends MenuDrawer {
        public MenuLogout() {
            setIcon(R.drawable.ic_logout_drawer);
            setName(R.string.drawer_menu_logout);
        }
    }
}
