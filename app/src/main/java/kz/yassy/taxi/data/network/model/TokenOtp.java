package kz.yassy.taxi.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenOtp {

    @SerializedName("access_token")
    @Expose
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}


/*{
        "id":57,
        "first_name":"No",
        "last_name":"Name",
        "payment_mode":"CASH",
        "user_type":"NORMAL",
        "email":"79236065655",
        "gender":"MALE",
        "country_code":"+7",
        "mobile":"79236065655",
        "emergency_contact1":null,
        "emergency_contact2":null,
        "password":"$2y$10$mWfDeMLkpWI9n8aofqZqs.AFRFkptnRId9v4D6bbcc2Czn71.Ne5y",
        "picture":null,
        "device_token":"dVRQ1yH56oM:APA91bE1-XJispadoLpEbtu_Wccg5rZ_k7fSSMI0mcUJqBwZ0xtxe-Tkzk4io9QMv5oVXF86OeCM9wmIZfmHzxHDjB6z86f1cznJJuGyNvS-Wx8ndPsdw9Mm69lTsTVahHZkuIiOAJ26",
        "device_id":"d3516417ad9fb510",
        "device_type":"android",
        "login_by":"whatsapp",
        "social_unique_id":null,
        "latitude":null,
        "longitude":null,
        "stripe_cust_id":null,
        "wallet_balance":0,
        "rating":"5.00",
        "otp":112723,
        "language":null,
        "qrcode_url":"uploads\/1615372338234627432_79236065655.png",
        "referral_unique_id":"E1D742",
        "referal_count":0,
        "updated_at":"2021-03-10 16:32:18",
        "access_token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjA0Mzg0MTkyZjA4YmNhYjZhNTExZWFjYTJlNmMyZDEzNDVmNDk1ZGQwNTczYzY3YmVhNWNjMWUxNGQ4NjU0MWFjNDIwNjM3MzQ0NDk0YzY5In0.eyJhdWQiOiIzIiwianRpIjoiMDQzODQxOTJmMDhiY2FiNmE1MTFlYWNhMmU2YzJkMTM0NWY0OTVkZDA1NzNjNjdiZWE1Y2MxZTE0ZDg2NTQxYWM0MjA2MzczNDQ0OTRjNjkiLCJpYXQiOjE2MTUzNzIzMzgsIm5iZiI6MTYxNTM3MjMzOCwiZXhwIjoxNjQ2OTA4MzM4LCJzdWIiOiI1NyIsInNjb3BlcyI6W119.p_vkN5j1Fu2s0-69nj_3zIMnOatAIgsRSJNtJrm562ps8KVX4H4X5GPCeJ69AxH5MB_JZPVd-0GRCYvlatWuldf6f_lBXXBSL8k_w3gTKvyMo0jKqiInYQUtusV5hgJT6cazLWRoHekh33OiOuuHcFefsRiPH3FtUTYxQXYnk6a7cPicRTHk2LQ6Tuc46AjIXjomzKprFJgRI7nOQdQC75Q_8xp9boITi_kqgukKTN0rzV5h93CmPMf9_nOFn4rXyeiL-Nw_CKxUDjpU_bEOxFtFSOVoM2zxMplutDQiKZObROaGpekrPg_MC240RHyz3EIQA4f1ZR4FTBIF5QJ6CMQCrfC32bAn7XeXseHx-f7PxwUaTCC8JlSEq9J2BS00FAbozrkD0P7WkSDVjDjUqjn2ZvDBrAjcqT4IRVRlNOZeXBurSyAUsmWxySqsIyjv9A-EPiDXeiA22rmjoRnkyiAdqz-OnDrXALR1rhBFXLF8r4CuXtylMhuGTPTLYRTc-I3fw-Xd1FWTvK7FVfJImD98DmxdeVux7h-dd9H20q7ahzYVsbaGM6-m64B_9ixCg_qHS99FuZQzpozcPnDlzHh85DpzcuFMCAejsHwIDtNhQMEor9aXEuqb5DY0UgQXmPDXsnJZh3uVnu4SpVzml8ceg7ajMadtnsFoTHy8dLE",
        "currency":"\u20b8",
        "sos":"112",
        "app_contact":"5777",
        "measurement":"Kms"} */
