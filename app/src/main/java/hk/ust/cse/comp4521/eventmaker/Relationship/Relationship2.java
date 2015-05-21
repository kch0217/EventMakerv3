package hk.ust.cse.comp4521.eventmaker.Relationship;

/**
 * Created by User on 5/21/2015.
 */
public class Relationship2 extends Relationship{
    transient String _Id;

    public Relationship2(){
        _Id="";
        eventId="";
        userId="";
    }

    public Relationship2 (String _eventId, String _userId){
        eventId=_eventId;
        userId=_userId;
    }

    public Relationship2(Relationship rel) {
        super(rel);
    }
}
