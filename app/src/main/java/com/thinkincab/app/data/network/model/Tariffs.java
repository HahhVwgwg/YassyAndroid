package com.thinkincab.app.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Tariffs implements Serializable {

    @SerializedName("type")
    @Expose
    private List<TariffType> type;

    public List<TariffType> getType() {
        return type;
    }

    public void setType(List<TariffType> type) {
        this.type = type;
    }

    public static class TariffType {

        @SerializedName("estimated_fare")
        @Expose
        private int estimatedFare;

        @SerializedName("distance")
        @Expose
        private String distance;

        @SerializedName("time")
        @Expose
        private String time;

        @SerializedName("minute")
        @Expose
        private int minute;

        @SerializedName("service_type")
        @Expose
        private int serviceType;

        public int getEstimatedFare() {
            return estimatedFare;
        }

        public int getMinute() {
            return minute;
        }

        public int getServiceType() {
            return serviceType;
        }

        public String getDistance() {
            return distance;
        }

        public String getTime() {
            return time;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public void setEstimatedFare(int estimatedFare) {
            this.estimatedFare = estimatedFare;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public void setServiceType(int serviceType) {
            this.serviceType = serviceType;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public boolean isStandard() {
            return serviceType == 0;
        }
    }
}
