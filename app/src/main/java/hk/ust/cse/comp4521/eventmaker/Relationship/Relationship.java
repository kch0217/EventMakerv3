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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Relationship)) return false;

        Relationship that = (Relationship) o;

        if (_id != null ? !_id.equals(that._id) : that._id != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        return !(roomId != null ? !roomId.equals(that.roomId) : that.roomId != null);

    }

    @Override
    public int hashCode() {
        int result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (roomId != null ? roomId.hashCode() : 0);
        return result;
    }
}
