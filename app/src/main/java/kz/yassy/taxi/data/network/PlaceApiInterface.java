package kz.yassy.taxi.data.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;

import io.reactivex.Observable;
import kz.yassy.taxi.data.network.model.SearchAddress;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PlaceApiInterface {

    @GET("search_address.php")
    Observable<JsonArray> doPlaces(@Query(value = "q", encoded = true) String search);

    @GET("reverse")
    Observable<SearchAddress> doPoint2(@Query(value = "lat", encoded = true) double lat,
                                       @Query(value = "lon", encoded = true) double lon,
                                       @Query(value = "format", encoded = true) String format,
                                       @Query(value = "addressdetails", encoded = true) int key,
                                       @Query(value = "zoom", encoded = true) int zoom,
                                       @Query(value = "email", encoded = true) String email);

    @FormUrlEncoded
    @POST("search_coord.php")
    Observable<JsonObject> doPoint(@FieldMap HashMap<String, String> params);
}
