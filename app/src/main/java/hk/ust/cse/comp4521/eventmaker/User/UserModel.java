package hk.ust.cse.comp4521.eventmaker.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ken on 7/4/2015.
 */
public class UserModel {

    private static String TAG = "UserModel";
    private Context context;
    private static UserModel usermodel = new UserModel();
    private SharedPreferences prefs;

    public SharedPreferences getSharedPreferences(){
        return prefs;
    }

    private UserModel(){

    }

    public static UserModel getUserModel(){
        return usermodel;
    }



    public void setContext(Context myContext)
    {
        this.context = myContext;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Map<String, Object> getAllInfo(){

        Map<String, Object> allInfo = new HashMap<>();
        allInfo.put("Name", prefs.getString("Name", ""));
        allInfo.put("Age", prefs.getInt("Age", -1));
        allInfo.put("Gender", prefs.getString("Gender", ""));
        allInfo.put("Interest", prefs.getString("Interest", ""));
        allInfo.put("Interest2", prefs.getString("Interest2", ""));
        allInfo.put("Phone", prefs.getString("Phone", ""));
        allInfo.put("NamePrivacy", prefs.getString("NamePrivacy",""));
        allInfo.put("AgePrivacy", prefs.getString("AgePrivacy",""));
        allInfo.put("GenderPrivacy", prefs.getString("GenderPrivacy",""));


        return allInfo;
    }

    public String getPhoneNumber(){
        TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Log.i(null, "TEL Number is " + tel.getLine1Number());
        return tel.getLine1Number();
    }

    public String getPhoneNumberFromSP(){
        return prefs.getString("Phone", null);
    }

    public void saveAllInfo(Map<String, Object> data, boolean modify){

        String _id = null;
        if (modify){
            UserServer myserver = new UserServer();
            UserInfo userInfo = myserver.UserInfoArrayList.get(myserver.calcID(prefs.getString("Phone", null)));

            if (userInfo != null) {
                _id = userInfo._id;

            }
            else
                modify = false;
        }

        SharedPreferences.Editor prefed = prefs.edit();
        prefed.putString("Name", (String)data.get("Name"));
        prefed.putInt("Age", (Integer) data.get("Age"));
        prefed.putString("Gender", (String) data.get("Gender"));
        prefed.putString("Interest", (String) data.get("Interest"));
        prefed.putString("Interest2", (String) data.get("Interest2"));
        prefed.putString("Phone", (String) data.get("Phone"));
        prefed.putString("NamePrivacy", (String) data.get("NamePrivacy"));
        prefed.putString("AgePrivacy", (String) data.get("AgePrivacy"));
        prefed.putString("GenderPrivacy", (String) data.get("GenderPrivacy"));
        prefed.commit();

        UserInfo2 newInfo = new UserInfo2();
        newInfo.Name = (String) data.get("Name");
        newInfo.Age = (Integer) data.get("Age");
        newInfo.Gender =  (String) data.get("Gender");
        newInfo.Interest = (String) data.get("Interest");
        newInfo.Interest2 =  (String) data.get("Interest2");
        newInfo.Phone = (String) data.get("Phone");
        newInfo.NamePrivacy = (String) data.get("NamePrivacy");
        newInfo.AgePrivacy = (String) data.get("AgePrivacy");
        newInfo.GenderPrivacy = (String) data.get("GenderPrivacy");
        UserServer myserver = new UserServer();
        if (modify){
            Log.i(TAG,"Modifying");
            newInfo._id = _id;
            myserver.updateUser(newInfo);
        }
        else {
            Log.i(TAG, "Adding");
            myserver.addAUser(newInfo);
        }


    }

    public String getInterest(){
        String interest = prefs.getString("Interest", "");
        Log.i(null, "Getting Interest: "+interest);
        return interest;
    }

// save the setting of enabling the notification of passive searching
    public void saveSetting(boolean allowed){
        SharedPreferences.Editor prefed = prefs.edit();
        prefed.putBoolean("allowPassiveSearching", allowed);
        prefed.commit();
    }

    // load the setting of enabling the notification of passive searching
    public Map<String, Object> getSetting(){
        Map<String, Object> data= new HashMap<>();
        data.put("allowPassiveSearching", prefs.getBoolean("allowPassiveSearching", false));
        return data;
    }


    public void wipeAlldata(){

        UserServer myServer = new UserServer();
        myServer.deleteUser(prefs.getString("Phone", null));
        SharedPreferences.Editor prefed =prefs.edit();
        prefed.clear();
        prefed.commit();
    }

    public void saveEventId(String id){
        SharedPreferences.Editor pref=prefs.edit();
        pref.putString("Event",id);
        pref.commit();
    }
}
