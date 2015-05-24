//COMP4521  Kwok Chung Hin   20111831   chkwokad@ust.hk
//COMP4521  Kwok Tsz Ting 20119118  ttkwok@ust.hk
//COMP4521  Li Lok Him  20103470    lhliab@ust.hk

package hk.ust.cse.comp4521.eventmaker.Event;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by User on 5/3/2015.
 */
public class Event {
    public String _id;
    public String _ownerid;
    public String interest;
    public double longitude;
    public double latitude;
    public int numOfPart; //default as 3
    public Timestamp currentTimestamp;
    public String locationName;
    public String starting;
    public String ending;


    public Event(){    }

    public Event(Event evt){
        longitude=evt.longitude;
        latitude=evt.latitude;
        numOfPart=evt.numOfPart;
        _id=evt._id;
        _ownerid=evt._ownerid;
        interest=evt.interest;
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        currentTimestamp = evt.currentTimestamp;
        starting=evt.starting;
        ending=evt.ending;
    }
}
