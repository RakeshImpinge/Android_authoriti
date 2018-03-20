package com.curtisdigital.authoriti.api;

import com.curtisdigital.authoriti.api.model.Purpose;
import com.curtisdigital.authoriti.api.model.SchemaGroup;
import com.curtisdigital.authoriti.api.model.request.RequestDLSave;
import com.curtisdigital.authoriti.api.model.request.RequestSignUp;
import com.curtisdigital.authoriti.api.model.request.RequestUserUpdate;
import com.curtisdigital.authoriti.api.model.response.ResponseInviteCode;
import com.curtisdigital.authoriti.api.model.response.ResponseSignUp;
import com.curtisdigital.authoriti.api.model.response.ResponseSignUpChase;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

/**
 * Created by mac on 11/30/17.
 */

public interface AuthoritiAPIService {

    @GET("api/v1/schema")
    Call<SchemaGroup> getSchemeGroup();

    @GET("api/v1/purpose")
    Call<List<Purpose>> getPurposes();

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

    @PUT("api/v1/users")
    Call<ResponseSignUp> updateUser(@Header("Authorization") String token,
                                @Body RequestUserUpdate request);

    @DELETE("api/v1/users")
    Call<JsonObject> wipe(@Header("Authorization") String token);

    @POST("api/v1/log")
    Call<JsonObject> saveDLInfo(@Body RequestDLSave request);

    @Multipart
    @POST("api/v1/log")
    Call<JsonObject> saveDLInfo(@Part("log") RequestBody log,
                                @Part MultipartBody.Part font,
                                @Part MultipartBody.Part back);
}
