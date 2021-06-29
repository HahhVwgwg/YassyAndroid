package kz.yassy.taxi.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PastTrip implements Serializable {
    @SerializedName("assigned_at")
    @Expose
    public String assignedAt;
    @SerializedName("id")
    @Expose
    private int id;

    public String getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(String assignedAt) {
        this.assignedAt = assignedAt;
    }

    @SerializedName("booking_id")
    @Expose
    private String bookingId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("s_latitude")
    @Expose
    private double sLatitude;
    @SerializedName("s_longitude")
    @Expose
    private double sLongitude;
    @SerializedName("d_latitude")
    @Expose
    private double dLatitude;
    @SerializedName("d_longitude")
    @Expose
    private double dLongitude;
    @SerializedName("d_address")
    @Expose
    private String dAddress;
    @SerializedName("s_address")
    @Expose
    private String sAddress;
    @SerializedName("cancelled_by")
    @Expose
    private String cancelledBy;
    @SerializedName("service_type_id")
    @Expose
    private int serviceTypeId;
    @SerializedName("provider_last_name")
    @Expose
    private String providerLastName;
    @SerializedName("provider_first_name")
    @Expose
    private String providerFirstName;
    @SerializedName("provider_number")
    @Expose
    private String providerNumber;
    @SerializedName("provider_service_model")
    @Expose
    private String providerServiceModel;
    @SerializedName("provider_service_number")
    @Expose
    private String providerServiceNumber;
    @SerializedName("provider_service_car")
    @Expose
    private String providerServiceCar;
    @SerializedName("service_type_name")
    @Expose
    private String serviceTypeName;
    @SerializedName("current_provider_id")
    @Expose
    private int currentProviderId;
    @SerializedName("provider_service_color")
    @Expose
    private String providerServiceColor;
    @SerializedName("provider_service_year")
    @Expose
    private String providerServiceYear;
    @SerializedName("waitingAmount")
    @Expose
    private int waitingAmount;
    @SerializedName("waitingStatus")
    @Expose
    private int waitingStatus;
    @SerializedName("waitingTime")
    @Expose
    private int waitingTime;
    @SerializedName("waiting_free_mins")
    @Expose
    private int waitingFreeMins;
    @SerializedName("waiting_min_charge")
    @Expose
    private int waitingMinCharge;
    @SerializedName("total")
    @Expose
    private String total;
    @SerializedName("estimated_fare")
    @Expose
    private String estimatedFare;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;

    @Override
    public String toString() {
        return "PastTrip{" +
                "id=" + id +
                ", assignedAt=" + assignedAt +
                ", bookingId='" + bookingId + '\'' +
                ", status='" + status + '\'' +
                ", sLatitude=" + sLatitude +
                ", sLongitude=" + sLongitude +
                ", dLatitude=" + dLatitude +
                ", dLongitude=" + dLongitude +
                ", dAddress='" + dAddress + '\'' +
                ", sAddress='" + sAddress + '\'' +
                ", cancelledBy='" + cancelledBy + '\'' +
                ", serviceTypeId=" + serviceTypeId +
                ", providerLastName='" + providerLastName + '\'' +
                ", providerFirstName='" + providerFirstName + '\'' +
                ", providerNumber='" + providerNumber + '\'' +
                ", providerServiceModel='" + providerServiceModel + '\'' +
                ", providerServiceNumber='" + providerServiceNumber + '\'' +
                ", providerServiceCar='" + providerServiceCar + '\'' +
                ", serviceTypeName='" + serviceTypeName + '\'' +
                ", currentProviderId=" + currentProviderId +
                ", providerServiceColor='" + providerServiceColor + '\'' +
                ", providerServiceYear='" + providerServiceYear + '\'' +
                ", waitingAmount=" + waitingAmount +
                ", waitingStatus=" + waitingStatus +
                ", waitingTime=" + waitingTime +
                ", waitingFreeMins=" + waitingFreeMins +
                ", waitingMinCharge=" + waitingMinCharge +
                ", total='" + total + '\'' +
                ", estimatedFare='" + estimatedFare + '\'' +
                ", paymentMode='" + paymentMode + '\'' +
                '}';
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getsLatitude() {
        return sLatitude;
    }

    public void setsLatitude(double sLatitude) {
        this.sLatitude = sLatitude;
    }

    public double getsLongitude() {
        return sLongitude;
    }

    public void setsLongitude(double sLongitude) {
        this.sLongitude = sLongitude;
    }

    public double getdLatitude() {
        return dLatitude;
    }

    public void setdLatitude(double dLatitude) {
        this.dLatitude = dLatitude;
    }

    public double getdLongitude() {
        return dLongitude;
    }

    public void setdLongitude(double dLongitude) {
        this.dLongitude = dLongitude;
    }

    public String getdAddress() {
        return dAddress;
    }

    public void setdAddress(String dAddress) {
        this.dAddress = dAddress;
    }

    public String getsAddress() {
        return sAddress;
    }

    public void setsAddress(String sAddress) {
        this.sAddress = sAddress;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getProviderLastName() {
        return providerLastName;
    }

    public void setProviderLastName(String providerLastName) {
        this.providerLastName = providerLastName;
    }

    public String getProviderFirstName() {
        return providerFirstName;
    }

    public void setProviderFirstName(String providerFirstName) {
        this.providerFirstName = providerFirstName;
    }

    public String getProviderNumber() {
        return providerNumber;
    }

    public void setProviderNumber(String providerNumber) {
        this.providerNumber = providerNumber;
    }

    public String getProviderServiceModel() {
        return providerServiceModel;
    }

    public void setProviderServiceModel(String providerServiceModel) {
        this.providerServiceModel = providerServiceModel;
    }

    public String getProviderServiceNumber() {
        return providerServiceNumber;
    }

    public void setProviderServiceNumber(String providerServiceNumber) {
        this.providerServiceNumber = providerServiceNumber;
    }

    public String getProviderServiceCar() {
        return providerServiceCar;
    }

    public void setProviderServiceCar(String providerServiceCar) {
        this.providerServiceCar = providerServiceCar;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public int getCurrentProviderId() {
        return currentProviderId;
    }

    public void setCurrentProviderId(int currentProviderId) {
        this.currentProviderId = currentProviderId;
    }

    public String getProviderServiceColor() {
        return providerServiceColor;
    }

    public void setProviderServiceColor(String providerServiceColor) {
        this.providerServiceColor = providerServiceColor;
    }

    public String getProviderServiceYear() {
        return providerServiceYear;
    }

    public void setProviderServiceYear(String providerServiceYear) {
        this.providerServiceYear = providerServiceYear;
    }

    public int getWaitingAmount() {
        return waitingAmount;
    }

    public void setWaitingAmount(int waitingAmount) {
        this.waitingAmount = waitingAmount;
    }

    public int getWaitingStatus() {
        return waitingStatus;
    }

    public void setWaitingStatus(int waitingStatus) {
        this.waitingStatus = waitingStatus;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getWaitingFreeMins() {
        return waitingFreeMins;
    }

    public void setWaitingFreeMins(int waitingFreeMins) {
        this.waitingFreeMins = waitingFreeMins;
    }

    public int getWaitingMinCharge() {
        return waitingMinCharge;
    }

    public void setWaitingMinCharge(int waitingMinCharge) {
        this.waitingMinCharge = waitingMinCharge;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(String estimatedFare) {
        this.estimatedFare = estimatedFare;
    }
}

