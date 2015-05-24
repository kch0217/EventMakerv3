//COMP4521  Kwok Chung Hin   20111831   chkwokad@ust.hk
//COMP4521  Kwok Tsz Ting 20119118  ttkwok@ust.hk
//COMP4521  Li Lok Him  20103470    lhliab@ust.hk
package hk.ust.cse.comp4521.eventmaker;

/**
 * Created by Ken on 7/4/2015.
 */



public class Constants {

    public static final int NEW_REGISTRATION = 1;
    public static final int MODIFY_REG = 0;
    public static final int DEFAULT_RANGE_DETECTION = 500;

    public static final String SERVER_ID = "202.189.108.122";
    public static final String PORT_NUM = "3000";
    public static final String SERVER_URL = "http://"+SERVER_ID+":"+PORT_NUM;

    public static final String KSERVER_ID="202.189.108.122";
    public static final String KPORT_NUM="3000";
    public static final String KSERVER_URL="http://"+KSERVER_ID+":"+KPORT_NUM;

    public static final String MAP_KEY="AIzaSyCP9l_atibNsB7jmMxV2bPC_Olrn34ihx0";

//    public static final String PACKAGE_NAME = "hk.ust.cse.comp4521.eventmaker";
//    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
//    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
//    public static final int SUCCESS_RESULT = 0;
//    public static final int FAILURE_RESULT = 1;
//    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

    public static final String eventId="eventId";
    public static final String eventCode="eventCode";//100 from hin 200 from him
    public static final String reconnect="reconnect"; //100=existing 200=new user
    public static final String eventSetting="eventSetting";
    public static final String eventSettingType="eventSettingType";//100=owner,200=non

    //for broadcase receiver
    public static final int ConnectionError = 20;
    public static final int EventDeleted = 21;
    public static final int closeNotification = 40;
    public static final int personnelChanges = 22;
    public static final int allserviceStopped = 30;

    public static final String signaling = "android.intent.action.monitor";
    public static final String closeNot = "android.intent.action.CloNot";


}
