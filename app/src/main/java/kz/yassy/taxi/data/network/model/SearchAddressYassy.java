package kz.yassy.taxi.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchAddressYassy {
    @SerializedName("map")
    @Expose
    private String map;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("coords")
    @Expose
    private String coords;

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCoords() {
        return coords;
    }

    public void setCoords(String coords) {
        this.coords = coords;
    }

    public Double getLat() {
        return Double.valueOf(coords.substring(coords.indexOf("(") + 1, coords.indexOf(",")));
    }

    public Double getLong() {
        return Double.valueOf(coords.substring(coords.indexOf(", ") + 1, coords.indexOf(")")));
    }
}