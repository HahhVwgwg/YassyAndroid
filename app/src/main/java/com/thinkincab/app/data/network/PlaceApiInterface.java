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
                                             @Query(value = "email", encoded = true) String email);
}
