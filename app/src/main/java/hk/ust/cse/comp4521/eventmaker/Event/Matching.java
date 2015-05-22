package hk.ust.cse.comp4521.eventmaker.Event;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import hk.ust.cse.comp4521.eventmaker.Constants;

/**
 * Created by User on 5/14/2015.
 */
public class Matching{
    private String TAG="matching";
    public void Matching(){ }

    //return eventid/null
    public static String checking(String interest, double lat, double longit){
        List<Event> events=Event_T.test;
        List<Event> potential=new ArrayList<Event>();
        Event theone = null;
        if(events!=null){
            if(events.size()>0){
                for(int i=0;i<events.size();i++){
                    if(events.get(i).interest.equals(interest)){
                        float[] result = new float[3];
                        Location.distanceBetween(lat,longit,events.get(i).latitude,events.get(i).longitude,result);
                        if(result[0]< Constants.DEFAULT_RANGE_DETECTION){
                            potential.add(events.get(i));
                        }
                    }
                }
                for(int a=0;a<potential.size();a++){
                    float min= Constants.DEFAULT_RANGE_DETECTION;
                    float[] result=new float[3];
                    Location.distanceBetween(lat,longit,potential.get(a).latitude,potential.get(a).longitude,result);
                    if(result[0]<min){
                        min=result[0];
                        theone=potential.get(a);
                    }
                }
                if (theone == null)
                    return null;
                else
                    return theone._id;
            }
        }
        return null;
    }
}
