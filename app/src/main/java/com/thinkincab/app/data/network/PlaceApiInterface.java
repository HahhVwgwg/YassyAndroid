package com.thinkincab.app.data.network;

import com.thinkincab.app.R;
import com.thinkincab.app.data.network.model.SearchAddress;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceApiInterface {

    @GET("search")
    Observable<List<SearchAddress>> doPlaces(@Query(value = "q", encoded = true) String search,
                                             @Query(value = "format", encoded = true) String format,
                                             @Query(value = "countrycodes", encoded = true) String code,
                                             @Query(value = "addressdetails", encoded = true) int key,
                                             @Query(value = "bounded", encoded = true) int bounded,
                                             @Query(value = "viewbox", encoded = true) String viewbox,
                                             @Query(value = "email", encoded = true) String email);

    @GET("reverse")
    Observable<SearchAddress> doPoint(@Query(value = "lat", encoded = true) double lat,
                                            @Query(value = "lon", encoded = true) double lon,
                                             @Query(value = "format", encoded = true) String format,
                                             @Query(value = "addressdetails", encoded = true) int key,
                                             @Query(value = "zoom", encoded = true) int zoom,
                                             @Query(value = "email", encoded = true) String email);
}
