package net.authoriti.authoriti.api;

import net.authoriti.authoriti.api.model.Purpose;
import net.authoriti.authoriti.api.model.SchemaGroup;
import net.authoriti.authoriti.api.model.request.RequestComplete;
import net.authoriti.authoriti.api.model.request.RequestSignUp;
import net.authoriti.authoriti.api.model.request.RequestSignUpChase;
import net.authoriti.authoriti.api.model.request.RequestSync;
import net.authoriti.authoriti.api.model.request.RequestUserUpdate;
import net.authoriti.authoriti.api.model.response.ResponseCallAuthentication;
import net.authoriti.authoriti.api.model.response.ResponseInviteCode;
import net.authoriti.authoriti.api.model.response.ResponsePolling;
import net.authoriti.authoriti.api.model.response.ResponseComplete;
import net.authoriti.authoriti.api.model.response.ResponseSignUp;
import net.authoriti.authoriti.api.model.response.ResponseSignUpChase;
import net.authoriti.authoriti.api.model.response.ResponseSync;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by mac on 11/30/17.
 */

public interface AuthoritiAPIService {

    @GET("api/v1/schema")
    Call<SchemaGroup> getSchemeGroup(@Query("client") String client);

    @GET("api/v1/version")
    Call<JsonObject> getMinimuVersion();

    @GET("api/v1/purpose")
    Call<List<Purpose>> getPurposes(@Query("client") String client, @Query("code") String code);

    @FormUrlEncoded
    @POST("api/v1/invite")
    Call<ResponseInviteCode> checkInviteCodeValidate(@Field("code") String code);

    @POST("api/v1/users")
    Call<ResponseSignUp> signUp(@Body RequestSignUp requestSignUp);

    @POST("api/v1/users")
    Call<ResponseSignUpChase> signUpChase(@Body RequestSignUpChase requestSignUpChase);

    @POST("api/v1/users/sync")
    Call<ResponseSync> sync(@Header("Authorization") String token, @Body RequestSync requestSync);

    @FormUrlEncoded
    @POST("api/v1/users/confirm")
    Call<JsonObject> confirmAccountValue(@Header("Authorization") String token,
                                         @Field("accountId") String accountId);

    @PUT("api/v1/users")
    Call<ResponseSignUp> updateUser(@Header("Authorization") String token,
                                    @Body RequestUserUpdate request);

    @DELETE("api/v1/users")
    Call<JsonObject> wipe(@Header("Authorization") String token);

    @Multipart
    @POST("api/v1/log")
    Call<JsonObject> saveDLInfo(@Part("log") RequestBody log,
                                @Part MultipartBody.Part font,
                                @Part MultipartBody.Part back);

    @GET("")
    Call<ResponseBody> getPollingUrl(@Url String url);

    @POST("api/v1/pc-request/complete")
    Call<ResponseComplete> completePollingRequest(@Body RequestComplete requestComplete);

    @POST("api/v1/pc-request/remove")
    Call<ResponseComplete> removePendingPollingRequest(@Body RequestComplete requestComplete);

    @POST("api/v1/call-authorization")
    Call<ResponseCallAuthentication> callAuthorization(@Body Map<String, String> your_variable_name);

    @DELETE("api/v1/call-authorization/{id}")
    Call<ResponseCallAuthentication> callAuthorizationDelete(@Path("id") String id);

}
