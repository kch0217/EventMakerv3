package hk.ust.cse.comp4521.eventmaker;

/**
 * Created by Ken on 7/4/2015.
 */

enum Interest{FootBall, BasketBall}

public class Constants {

    public static final int NEW_REGISTRATION = 1;
    public static final int MODIFY_REG = 0;
    public static final int DEFAULT_RANGE_DETECTION = 500;

    public static final String SERVER_ID = "147.8.246.197";
    public static final String PORT_NUM = "3000";
    public static final String SERVER_URL = "http://"+SERVER_ID+":"+PORT_NUM;

    public static final String KSERVER_ID="147.8.246.197";
    public static final String KPORT_NUM="3000";
    public static final String KSERVER_URL="http://"+KSERVER_ID+":"+KPORT_NUM;

    public static final String MAP_KEY="AIzaSyD_zzrOpDDnlECR-swGuKpHHo44WZaxI1I";

    public static final String eventId="eventId";
    public static final String eventCode="eventCode";//100 from hin 200 from him

}
