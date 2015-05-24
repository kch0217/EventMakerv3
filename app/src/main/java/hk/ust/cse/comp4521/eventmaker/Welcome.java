//COMP4521  Kwok Chung Hin   20111831   chkwokad@ust.hk
//COMP4521  Kwok Tsz Ting 20119118  ttkwok@ust.hk
//COMP4521  Li Lok Him  20103470    lhliab@ust.hk
package hk.ust.cse.comp4521.eventmaker;

import android.app.Activity;
import android.content.Intent;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Map;

import hk.ust.cse.comp4521.eventmaker.User.UserModel;
import hk.ust.cse.comp4521.eventmaker.User.UserRegistration;

public class Welcome extends Activity {

    private GestureDetector mDetector;
    private int page;
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        page = 0;

        UserModel usermodel = UserModel.getUserModel(); //get ready for the user model for handling user data
        usermodel.setContext(Welcome.this);

        Map<String, Object> data = usermodel.getAllInfo(); //retrieve the user data on the device

        if (!((String) data.get("Name")).equals("")) { //check if the user has registered
            Intent intent = new Intent(this, SearchFrag.class); //if the user has registered, direct him/her to the search page
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //clear the back stack
            startActivity(intent);
        }
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new Fragment1(), "WelcomeFragment1") //add the welcome page fragment
                    .commit();
        }




        mDetector = new GestureDetector(this, new swipeWelcome()); //create a gesture for user to swipe between fragments
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) { // execute the touch event
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }








    public class swipeWelcome extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { // when the user has swiped the screen



            if (velocityX<0){ //swipe from right to left and bring in the second welcome fragment
                if (page == 0 ){
                    page =1 ;
                    Fragment secondFragment = getFragmentManager().findFragmentByTag("WelcomeFragment2");

                    if (secondFragment ==null)
                        secondFragment = new Fragment2();
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right).replace(R.id.container, secondFragment, "WelcomeFragment2").commit(); //set the animation for switching the fragment



                    Log.i(null, "p1");
                }
            }
            else{ // swipe from left to right and bring in the first welcome fragment
                if (page ==1){
                    page =0;
                    Fragment firstFragment = getFragmentManager().findFragmentByTag("WelcomeFragment1");
                    if (firstFragment ==null)
                        firstFragment = new Fragment1();
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left).replace(R.id.container, firstFragment, "WelcomeFragment1").commit(); //set the animation for switching the fragment


                    Log.i(null,"p0");
                }
            }

            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    public static class Fragment1 extends Fragment { //first fragment

        public Fragment1() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
            return rootView;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class Fragment2 extends Fragment { //second fragment

        public Fragment2() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_welcome2, container, false);
            Button start = (Button) rootView.findViewById(R.id.buttonStart); //reference to the button
            start.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Log.i(null, "Click is detected");
                    if (view.getId() == R.id.buttonStart) {
                        UserModel usermodel = UserModel.getUserModel(); //get the user data from shared preference
                        usermodel.setContext(getActivity());
                        Map<String, Object> data = usermodel.getAllInfo();

                        if (((String) data.get("Name")).equals("")) { //check if the user has data on the device before
                            Intent intent = new Intent(getActivity(), UserRegistration.class); //if the user has no data before, he would be directed to the registration
                            intent.putExtra("Context", Constants.NEW_REGISTRATION); //add a message to the intent so that the registration class knows that the user is new
                            startActivity(intent);
                        }
                        else{
                            Intent intent = new Intent(getActivity(), SearchFrag.class); //if the user has data before, he would be directed to the search page
                            startActivity(intent);
                        }

                    }
                }
            });

            Log.i(null, "onCreateView of Fragment2");
            return rootView;

        }
    }


}
