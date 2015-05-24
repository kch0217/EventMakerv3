//COMP4521  Kwok Chung Hin   20111831   chkwokad@ust.hk
//COMP4521  Kwok Tsz Ting 20119118  ttkwok@ust.hk
//COMP4521  Li Lok Him  20103470    lhliab@ust.hk
package hk.ust.cse.comp4521.eventmaker.restForUser;

import java.util.ArrayList;

import hk.ust.cse.comp4521.eventmaker.User.UserInfo;
import hk.ust.cse.comp4521.eventmaker.User.UserInfo2;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.*;

/**
 * Created by Ken on 25/4/2015.
 */
public interface userServerAPI {
    @GET("/users")
    void getUsers(Callback<ArrayList<UserInfo>> callback);




    @GET("/users/{id}")
    void getUser(@Path("id") String id, Callback<UserInfo> callback);

    @DELETE("/users/{id}")
    void deleteUser(@Path("id") String id, Callback<Response> callback);

    @POST("/users")
    void addUser(@Body UserInfo2 userInfo, Callback<Response> callback);

    @PUT("/users/{id}")
    void updateUser(@Body UserInfo2 userInfo, @Path("id") String id, Callback<Response> callback);

    @GET("/users/{Phone}")
    void searchUser(@Path("Phone") String Phone, Callback<UserInfo> callback);
}
