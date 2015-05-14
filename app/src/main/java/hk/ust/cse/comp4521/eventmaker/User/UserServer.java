package hk.ust.cse.comp4521.eventmaker.User;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import hk.ust.cse.comp4521.eventmaker.restForUser.RestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Ken on 25/4/2015.
 */
public class UserServer {

    private static String TAG = "UserServer";
    public static List<UserInfo> UserInfoArrayList;
    public static UserInfo returnInfo;
    public static UserInfo searchUser;

    public static List<UserInfo> getAllUsers(){
        updateInternalState();
        return UserInfoArrayList;
    }

    public static void updateInternalState(){

        RestClient.get().getUsers(new Callback<ArrayList<UserInfo>>() {
            @Override
            public void success(ArrayList<UserInfo> userInfos, Response response) {
                UserInfoArrayList = userInfos;
                if (UserInfoArrayList==null)
                    UserInfoArrayList = new ArrayList<UserInfo>();
                Log.w(TAG, "Succeed to fetch all data! The size of Array is " + UserInfoArrayList.size());
                if (!(UserInfoArrayList.size() > 0)) {
                    Log.w(TAG, "Failed to fetch data!");
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Retrofit Error");
                if (UserInfoArrayList==null)
                    UserInfoArrayList = new ArrayList<UserInfo>();
            }
        });




    }

    public static void addAUser(UserInfo2 userInfo){
        RestClient.get().addUser(userInfo, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.i(TAG, "Add Response is " + response.getBody().toString());
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Retrofit Error");
            }
        });


    }

    public static void updateUser(UserInfo2 userInfo ){ //userInfo needs to supply ID
        RestClient.get().updateUser(userInfo, userInfo._id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });

    }

    public static UserInfo getAUser(String phone){
        //updateInternalState();
        if (UserInfoArrayList == null || phone == null)
            return null;
        if (UserInfoArrayList.size() == 0)
            return null;
        int target = calcID(phone);
        if (target == -1)
            return null;
        returnInfo = null;
        RestClient.get().getUser(UserInfoArrayList.get(target)._id, new Callback<UserInfo>() {
            @Override
            public void success(UserInfo userInfo, Response response) {
                Log.i(TAG, "succeed to get a user");
                if (userInfo != null) {

                    returnInfo = userInfo;
                }

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG, "fail to get a user");
            }
        });
        return returnInfo;
    }

    public static void deleteUser(String phone){
        updateInternalState();
        UserInfo temp = null;


        if (UserInfoArrayList == null || phone ==null)
            return;
        if (UserInfoArrayList.size() == 0)
            return;
        int target = calcID(phone);
        String deleteId = null;
        if (target == -1){
            temp = searchAUser(phone);
            if (temp == null)
                return;
            deleteId = temp._id;
        }
        else

           deleteId = UserInfoArrayList.get(target)._id;

        RestClient.get().deleteUser(deleteId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.i(TAG, "Response " + response.getBody().toString());
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
        updateInternalState();
    }

    public static UserInfo searchAUser(String phone){
        searchUser = null;
        RestClient.get().searchUser(phone, new Callback<UserInfo>() {
            @Override
            public void success(UserInfo userInfo, Response response) {
                Log.i(TAG, "succeed to search a user");
                if (userInfo != null) {

                    searchUser = userInfo;
                }

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG, "fail to get a user");
            }
        });
        return searchUser;
    }

    public static int calcID(String phone){
        int target = -1;

        for (int i = 0; i < UserInfoArrayList.size(); i++){
            Log.i(TAG, UserInfoArrayList.get(i).Phone);
            if (UserInfoArrayList.get(i).Phone.equals(phone)){
                target = i;
                break;
            }
        }
        return target;
    }




}
