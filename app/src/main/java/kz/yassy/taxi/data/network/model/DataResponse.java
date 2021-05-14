package kz.yassy.taxi.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataResponse {

    @SerializedName("data")
    @Expose
    private Datum data;


    @SerializedName("sos")
    @Expose
    private String sos;
    @SerializedName("cash")
    @Expose
    private int cash;
    @SerializedName("online")
    @Expose
    private int online;
    @SerializedName("rental")
    @Expose
    private int rental;
    @SerializedName("outstation")
    @Expose
    private int outstation;
    @SerializedName("waitingStatus")
    @Expose
    private int waitingStatus;
    @SerializedName("waitingTime")
    @Expose
    private int waitingTime;
    @SerializedName("debit_machine")
    @Expose
    private int debit_machine;
    @SerializedName("voucher")
    @Expose
    private int voucher;


    public DataResponse() {
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getWaitingStatus() {
        return waitingStatus;
    }

    public void setWaitingStatus(int waitingStatus) {
        this.waitingStatus = waitingStatus;
    }

    public int getOutstation() {
        return outstation;
    }

    public void setOutstation(int outstation) {
        this.outstation = outstation;
    }

    public int getDebit_machine() {
        return debit_machine;
    }

    public void setDebit_machine(int debit_machine) {
        this.debit_machine = debit_machine;
    }

    public int getRental() {
        return rental;
    }

    public void setRental(int rental) {
        this.rental = rental;
    }

    //TODO ALLAN - Alterações débito na máquina e voucher
    public int getDebitMachine() {
        return debit_machine;
    }

    public void setDebitMachine(int debit_machine) {
        this.debit_machine = debit_machine;
    }

    public int getVoucher() {
        return voucher;
    }

    public void setVoucher(int voucher) {
        this.voucher = voucher;
    }

    public Datum getData() {
        return data;
    }

    public void setData(Datum data) {
        this.data = data;
    }

    public String getSos() {
        return sos;
    }

    public void setSos(String sos) {
        this.sos = sos;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }
}
