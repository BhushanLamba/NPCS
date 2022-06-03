package wts.com.npcs.retrofit;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface WebServiceInterface {

    @FormUrlEncoded
    @POST("UserAuthentication")
    Call<JsonObject> loginUser(@Field("UserId") String UserId,
                               @Field("Password") String Password,
                               @Field("Latitude") String Latitude,
                               @Field("Longitude") String Longitude,
                               @Field("IMEI") String IMEI,
                               @Field("MacAddress") String MacAddress);

    @FormUrlEncoded
    @POST("FetchUserCurrentBalance")
    Call<JsonObject> getBalance(@Field("UserKey") String UserKey);

    @POST("FetchServiceMaster")
    Call<JsonObject> getService();

    @FormUrlEncoded
    @POST("FetchoperatorMaster")
    Call<JsonObject> getOperators(@Field("ServiceId") String ServiceId);

    @FormUrlEncoded
    @POST("DoRecharge")
    Call<JsonObject> doRecharge(@Field("UserKey") String UserKey,
                                @Field("Amount") String Amount,
                                @Field("ServiceType") String ServiceType,
                                @Field("MobileNo") String MobileNo,
                                @Field("OperatorId") String OperatorId,
                                @Field("PayBy") String PayBy);

    @FormUrlEncoded
    @POST("FetchCommonReport")
    Call<JsonObject> getReport(@Field("UserKey") String UserKey,
                               @Field("FromDate") String FromDate,
                               @Field("ToDate") String ToDate,
                               @Field("TransactionId") String TransactionId,
                               @Field("Status") String Status,
                               @Field("MobileNo") String MobileNo,
                               @Field("AccountNo") String AccountNo,
                               @Field("ParentId") String ParentId,
                               @Field("ServiceName") String ServiceName);

    @FormUrlEncoded
    @POST("FetchCrDrReport")
    Call<JsonObject> crDrReport(@Field("UserKey") String UserKey,
                                @Field("FromDate") String FromDate,
                                @Field("ToDate") String ToDate,
                                @Field("SearchType") String SearchType);
}
