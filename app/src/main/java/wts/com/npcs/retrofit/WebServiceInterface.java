package wts.com.npcs.retrofit;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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
}
