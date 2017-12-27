package com.curtisdigital.authoriti.api;

import com.curtisdigital.authoriti.BuildConfig;
import com.curtisdigital.authoriti.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mac on 11/30/17.
 */

public class AuthoritiAPI {

    private static AuthoritiAPI ourInstance = new AuthoritiAPI();
    private static AuthoritiAPIService apiService;

    public static AuthoritiAPI getInstance() {
        return ourInstance;
    }

    private AuthoritiAPI() {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss.SSS'Z'")
                .create();

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .connectionSpecs(Collections.singletonList(new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_1)
                        .allEnabledCipherSuites()
                        .build()))
                .addInterceptor(new Interceptor() {
                    @Override public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("X-Platform", "android")
                                .addHeader("X-App-Version", BuildConfig.VERSION_NAME)
                                .build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(logInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(AuthoritiAPIService.class);
    }

    public static AuthoritiAPIService APIService() { return apiService; }

}
