package com.curtisdigital.authoriti.api;

import com.curtisdigital.authoriti.api.model.AccountID;
import com.curtisdigital.authoriti.api.model.Scheme;
import com.curtisdigital.authoriti.api.model.request.RequestSignUp;
import com.curtisdigital.authoriti.api.model.response.ResponseInviteCode;
import com.curtisdigital.authoriti.api.model.response.ResponseSignUp;
import com.curtisdigital.authoriti.api.model.response.ResponseSignUpChase;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by mac on 11/30/17.
 */

public interface AuthoritiAPIService {

    @GET("api/v1/schema")
    Call<Scheme> getScheme();

    @FormUrlEncoded
    @POST("api/v1/invite")
    Call<ResponseInviteCode> checkInviteCodeValidate(@Field("code") String code);

    @POST("api/v1/users")
    Call<ResponseSignUp> signUp(@Body RequestSignUp requestSignUp);

    @POST("api/v1/users")
    Call<ResponseSignUpChase> signUpChase(@Body RequestSignUp requestSignUp);

    @FormUrlEncoded
    @POST("api/v1/users/confirm")
    Call<JsonObject> confirmAccountValue(@Header("Authorization") String token,
                                         @Field("accountId") String accountId);
}
