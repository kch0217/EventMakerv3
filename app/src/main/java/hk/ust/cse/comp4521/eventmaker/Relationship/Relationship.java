package hk.ust.cse.comp4521.eventmaker.Relationship;

/**
 * Created by User on 5/21/2015.
 */
public class Relationship {
    public String eventId;
    public String userId;
    public String _Id;
    public String idk;
    public String you;

    public Relationship(Relationship rel){
        _Id=rel._Id;
        eventId=rel.eventId;
        userId=rel.userId;
    }
    public Relationship(){}

}
