package kz.yassy.taxi.data.network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import kz.yassy.taxi.BuildConfig;
import kz.yassy.taxi.MvpApplication;
import kz.yassy.taxi.data.SharedHelper;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class APIClient {

    private static Retrofit retrofit = null;

    public static ApiInterface getAPIClient() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(getHttpClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(ApiInterface.class);
    }

    private static OkHttpClient getHttpClient() {
        try {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            return new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(10, TimeUnit.MINUTES)
                    .addNetworkInterceptor(new AddHeaderInterceptor())
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

    private static class AddHeaderInterceptor implements Interceptor {
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            builder.addHeader("X-Requested-With", "XMLHttpRequest");
            builder.addHeader("Authorization",
                    SharedHelper.getKey(MvpApplication.getInstance(), "access_token", ""));
            Log.d("TTT access_token", SharedHelper.getKey(MvpApplication.getInstance(), "access_token", ""));
            return chain.proceed(builder.build());
        }
    }
}
