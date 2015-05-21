package hk.ust.cse.comp4521.eventmaker.Relationship;

/**
 * Created by User on 5/21/2015.
 */
public class Relationship {
    public String userId;
    public String _Id;
    public String roomId;
    public String you;

    public Relationship(Relationship rel){
        _Id=rel._Id;
        roomId=rel.roomId;
        userId=rel.userId;
    }
    public Relationship(){}

}
