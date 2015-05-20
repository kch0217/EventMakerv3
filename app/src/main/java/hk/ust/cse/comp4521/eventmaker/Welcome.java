package hk.ust.cse.comp4521.eventmaker;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
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
import android.os.Build;
import android.widget.Button;

import java.util.Map;

import hk.ust.cse.comp4521.eventmaker.User.UserModel;
import hk.ust.cse.comp4521.eventmaker.User.UserRegistration;
import hk.ust.cse.comp4521.eventmaker.User.UserServer;

public class Welcome extends Activity {

    private GestureDetector mDetector;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        page = 0;

        UserModel usermodel = UserModel.getUserModel();
        usermodel.setContext(Welcome.this);
        //fetch user database
//        UserServer myServer = new UserServer();
//        myServer.updateInternalState();
        Map<String, Object> data = usermodel.getAllInfo();

        if (!((String) data.get("Name")).equals("")) {
            Intent intent = new Intent(this, SearchFrag.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new Fragment1(), "WelcomeFragment1")
                    .commit();
        }


        

        mDetector = new GestureDetector(this, new swipeWelcome());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }








    public class swipeWelcome extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {



            if (velocityX<0){
                if (page == 0 ){
                    page =1 ;
                    Fragment secondFragment = getFragmentManager().findFragmentByTag("WelcomeFragment2");

                    if (secondFragment ==null)
                        secondFragment = new Fragment2();
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right).replace(R.id.container, secondFragment, "WelcomeFragment2").commit();



                    Log.i(null, "p1");
                }
            }
            else{
                if (page ==1){
                    page =0;
                    Fragment firstFragment = getFragmentManager().findFragmentByTag("WelcomeFragment1");
                    if (firstFragment ==null)
                        firstFragment = new Fragment1();
                    getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left).replace(R.id.container, firstFragment, "WelcomeFragment1").commit();


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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class Fragment1 extends Fragment {

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
    public static class Fragment2 extends Fragment {

        public Fragment2() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_welcome2, container, false);
            Button start = (Button) rootView.findViewById(R.id.startButton);
            start.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Log.i(null, "Click is detected");
                    if (view.getId() == R.id.startButton) {
                        UserModel usermodel = UserModel.getUserModel(); //may simplify
                        usermodel.setContext(getActivity());
                        Map<String, Object> data = usermodel.getAllInfo();

                        if (((String) data.get("Name")).equals("")) {
                            Intent intent = new Intent(getActivity(), UserRegistration.class);
                            intent.putExtra("Context", Constants.NEW_REGISTRATION);
                            startActivity(intent);
                        }
                        else{
                            Intent intent = new Intent(getActivity(), SearchFrag.class);
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
