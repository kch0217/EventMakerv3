package hk.ust.cse.comp4521.eventmaker.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import hk.ust.cse.comp4521.eventmaker.R;

public class UserDisplay extends Activity {
    ImageView user_pic;
    TextView name;
    TextView age;
    TextView gender;
    TextView interest;
    TextView phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
         user_pic=(ImageView)findViewById(R.id.userInfo_userpic);
         name=(TextView)findViewById(R.id.userInfo_name);
         age=(TextView)findViewById(R.id.userInfo_age);
         gender=(TextView)findViewById(R.id.userInfo_gender);
         interest=(TextView)findViewById(R.id.userInfo_interest);
         phone=(TextView)findViewById(R.id.userInfo_phone);


        Intent intent = getIntent();
        //get the UserInfo object from eventActivity
        UserInfo user = (UserInfo) intent.getExtras().getSerializable("User");

        //initialize all Textview and ImageView

        update_UI(user);


    }
    public void update_UI(UserInfo user){
        if(!user.NamePrivacy.equals("Check"))
        name.setText(user.Name);
        if(!user.AgePrivacy.equals("Check"))
        age.setText("Age: "+user.Age);
        if(!user.GenderPrivacy.equals("Check"))
        gender.setText("Gender: "+user.Gender);

        interest.setText("Interest: "+user.Interest+","+user.Interest2);
        phone.setText("Phone: "+user.Phone);

    }


}
