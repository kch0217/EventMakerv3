package hk.ust.cse.comp4521.eventmaker.Relationship;

/**
 * Created by User on 5/21/2015.
 */
public class Relationship {
    public String _id;
    public String userId;
    public String roomId;

    public Relationship(Relationship rel){
        _id=rel._id;
        roomId=rel.roomId;
        userId=rel.userId;
    }
    public Relationship(){}

}
