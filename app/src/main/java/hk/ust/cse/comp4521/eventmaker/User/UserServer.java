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
    public static Boolean connectionState;
    public static Object lock;

    public UserServer() {
        connectionState = null;
    }

    public static List<UserInfo> getAllUsers(){
        updateInternalState();
        return UserInfoArrayList;
    }

    public static void updateInternalState(){ //retrieve all users data from server
        Log.w(TAG, "Trying to update");
        RestClient.get().getUsers(new Callback<ArrayList<UserInfo>>() {
            @Override
            public void success(ArrayList<UserInfo> userInfos, Response response) {
                UserInfoArrayList = userInfos;
                if (UserInfoArrayList == null)
                    UserInfoArrayList = new ArrayList<UserInfo>();
                Log.w(TAG, "Succeed to fetch all data! The size of Array is " + UserInfoArrayList.size());
                if (!(UserInfoArrayList.size() > 0)) {
                    Log.w(TAG, "Failed to fetch data!");
                }
                connectionState = true;
                synchronized (lock) { //sometimes the main thread would wait for the server to update the data, so need to notify it
                    lock.notify();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Retrofit Error");
                if (UserInfoArrayList == null)
                    UserInfoArrayList = new ArrayList<UserInfo>();
                connectionState = false;
                synchronized (lock) {//sometimes the main thread would wait for the server to update the data, so need to notify it
                    lock.notify();
                }
            }
        });




    }


    public static void addAUser(UserInfo2 userInfo){ //add a user to the server
        RestClient.get().addUser(userInfo, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.i(TAG, "Add Response is " + response.getBody().toString());
                connectionState = true;
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Retrofit Error");
                connectionState = false;
            }
        });


    }

    public static void updateUser(UserInfo2 userInfo ){ //userInfo needs to supply ID, modify the user data
        RestClient.get().updateUser(userInfo, userInfo._id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });

    }

    public static UserInfo getAUser(String phone){ // retrieve a user data using its phone number
        //updateInternalState();
        if (UserInfoArrayList == null || phone == null)//check for nullity
            return null;
        if (UserInfoArrayList.size() == 0)
            return null;
        int target = calcID(phone); //check the position of the target user in the arraylist
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
                synchronized (lock) {
                    lock.notify();
                }

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG, "fail to get a user");
                synchronized (lock) {
                    lock.notify();
                }
            }
        });
        return returnInfo;
    }

    public static void deleteUser(String phone){ //delete a user
        updateInternalState(); //update the user data
        UserInfo temp = null;


        if (UserInfoArrayList == null || phone ==null)
            return;
        if (UserInfoArrayList.size() == 0)
            return;
        int target = calcID(phone);
        String deleteId = null;
        if (target == -1){ //not possible to happen, but as the last resort to search it
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
        updateInternalState(); //update the user data from server
    }

    public static UserInfo searchAUser(String phone){ // retrieve a specific user' data from server using its phone number
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

    public static int calcID(String phone){ //find the array position of the user
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
