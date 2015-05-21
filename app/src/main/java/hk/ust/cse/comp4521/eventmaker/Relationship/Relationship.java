package hk.ust.cse.comp4521.eventmaker.Relationship;

/**
 * Created by User on 5/21/2015.
 */
public class Relationship {
    public String eventId;
    public String userId;
    public String relId;
    public Relationship(Relationship rel){
        eventId=rel.eventId;
        userId=rel.userId;
        relId=rel.relId;
    }
    public Relationship(){}

}
