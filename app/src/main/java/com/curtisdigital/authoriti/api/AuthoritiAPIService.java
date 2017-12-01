package com.curtisdigital.authoriti.api;

import com.curtisdigital.authoriti.api.model.Scheme;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by mac on 11/30/17.
 */

public interface AuthoritiAPIService {

    @GET("dev/api/v1/schema")
    Call<Scheme> getScheme();
}
