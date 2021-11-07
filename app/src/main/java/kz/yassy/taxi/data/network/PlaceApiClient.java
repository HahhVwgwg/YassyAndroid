package kz.yassy.taxi.data.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaceApiClient {

    private static Retrofit retrofit = null;

    public static PlaceApiInterface getAPIClient() {
        if (retrofit == null) {
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl("https://my.yassy.taxi/ajax/")
                    .client(getHttpClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(PlaceApiInterface.class);
    }

    private static OkHttpClient getHttpClient() {
        try {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            return new OkHttpClient()
                    .newBuilder()
                    //.cache(new Cache(MvpApplication.getInstance().getCacheDir(), 10 * 1024 * 1024)) // 10 MB
                    .connectTimeout(10, TimeUnit.MINUTES)
//                    .addNetworkInterceptor(new StethoInterceptor())
                    .readTimeout(10, TimeUnit.MINUTES)
                    .writeTimeout(10, TimeUnit.MINUTES)
                    .retryOnConnectionFailure(true)
//                    .addInterceptor(interceptor)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
