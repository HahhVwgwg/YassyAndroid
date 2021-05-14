package kz.yassy.taxi.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckVersion {

    @SerializedName("force_update")
    @Expose
    private Boolean forceUpdate;

    @SerializedName("url")
    @Expose
    private String url;


    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "CheckVersion{" +
                "forceUpdate=" + forceUpdate +
                ", url='" + url + '\'' +
                '}';
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(Boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

}
