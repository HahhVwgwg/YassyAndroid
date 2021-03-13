package com.thinkincab.app.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchAddress {
    @SerializedName("place_id")
    @Expose
    private long placeId;
    @SerializedName("osm_id")
    @Expose
    private long osmId;
    @SerializedName("osm_type")
    @Expose
    private String osmType;
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lon")
    @Expose
    private double lon;
    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("class")
    @Expose
    private String clazz;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("address")
    @Expose
    private Address address;

    public Address getAddress() {
        return address;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public long getOsmId() {
        return osmId;
    }

    public long getPlaceId() {
        return placeId;
    }

    public String getClazz() {
        return clazz;
    }

    public String getIcon() {
        return icon;
    }

    public String getOsmType() {
        return osmType;
    }

    public String getType() {
        return type;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setOsmId(long osmId) {
        this.osmId = osmId;
    }

    public void setOsmType(String osmType) {
        this.osmType = osmType;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class Address {
        @SerializedName("amenity")
        @Expose
        private String amenity;
        @SerializedName("house_number")
        @Expose
        private String house;
        @SerializedName("road")
        @Expose
        private String road;
        @SerializedName("town")
        @Expose
        private String town;
        @SerializedName("county")
        @Expose
        private String county;
        @SerializedName("state")
        @Expose
        private String state;
        @SerializedName("country")
        @Expose
        private String country;

        public String getAmenity() {
            return amenity;
        }

        public String getRoad() {
            return road;
        }

        public String getCountry() {
            return country;
        }

        public String getCounty() {
            return county;
        }

        public String getState() {
            return state;
        }

        public String getTown() {
            return town;
        }

        public void setAmenity(String amenity) {
            this.amenity = amenity;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public void setCounty(String county) {
            this.county = county;
        }

        public void setRoad(String road) {
            this.road = road;
        }

        public void setState(String state) {
            this.state = state;
        }

        public void setTown(String town) {
            this.town = town;
        }

        public String getHouse() {
            return house;
        }

        public void setHouse(String house) {
            this.house = house;
        }
    }
}